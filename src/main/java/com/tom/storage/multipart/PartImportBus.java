package com.tom.storage.multipart;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.items.IItemHandler;

import com.tom.api.multipart.IGuiMultipart;
import com.tom.apis.TomsModUtils;
import com.tom.storage.tileentity.gui.GuiImportBus;
import com.tom.storage.tileentity.inventory.ContainerImportBus;

import mcmultipart.api.slot.EnumFaceSlot;
import mcmultipart.api.slot.IPartSlot;

public class PartImportBus extends PartChannelModule implements IGuiMultipart {
	private boolean checkNBT = true;
	private boolean checkMeta = true;
	private boolean checkMod = false;
	private boolean isWhiteList = true;
	private static final String TAG_NBT_NAME = "config";
	private int transferCooldown = 1;
	public ItemStack stuckStack = ItemStack.EMPTY;
	public InventoryBasic filterInv = new InventoryBasic("", false, 9);
	public InventoryBasic upgradeInv = new InventoryBasic("", false, 1);

	@Override
	public void updateEntityI() {
		if (!world.isRemote) {
			if (isActiveForUpdate()) {
				int pulledStacks = 0;
				int upgradeCount = getSpeedUpgradeCount();
				if (transferCooldown == 0) {
					if (!stuckStack.isEmpty()) {
						stuckStack = grid.pushStack(stuckStack);
						transferCooldown = 4;
					} else {
						BlockPos pos = getPos2().offset(getFacing());
						IItemHandler inv = TomsModUtils.getItemHandler(world, pos, getFacing().getOpposite(), true);
						if (inv != null) {
							boolean allNull = true;
							for (int i = 0;i < filterInv.getSizeInventory();i++) {
								ItemStack valid = filterInv.getStackInSlot(i);
								if (!valid.isEmpty()) {
									allNull = false;
									ItemStack pulledStack = pullStack(inv, this, getMaxPacketSize(), valid, getFacing().getOpposite());
									if (pulledStack != null) {
										pulledStack = grid.pushStack(pulledStack);
										if (pulledStack != null) {
											stuckStack = pulledStack;
										}
										transferCooldown = upgradeCount > 1 ? 2 : 5;
										pulledStacks++;
										if (upgradeCount < 3 || pulledStacks > 1)
											break;
									}
								}
							}
							if (transferCooldown == 0)
								transferCooldown = upgradeCount > 2 ? 16 : 40;
							if (allNull) {
								int extract = getMaxPacketSize();
								for (int i = 0;i < inv.getSlots();i++) {
									ItemStack stack = inv.getStackInSlot(i);
									if (!inv.extractItem(i, Math.min(extract, stack.getCount()), true).isEmpty()) {
										ItemStack pulledStack = inv.extractItem(i, Math.min(extract, stack.getCount()), false);
										if (!pulledStack.isEmpty()) {
											pulledStack = grid.pushStack(pulledStack);
											if (!pulledStack.isEmpty()) {
												stuckStack = pulledStack;
											}
											pulledStacks++;
											transferCooldown = upgradeCount > 1 ? 2 : 5;
											if (upgradeCount < 3 || pulledStacks > 1)
												break;
										}
									}
								}
								if (transferCooldown == 0)
									transferCooldown = upgradeCount > 2 ? 16 : 40;
							}
						}
					}
				} else if (transferCooldown > 0)
					transferCooldown--;
				else
					transferCooldown = 1;
			}
		}
	}

	private static ItemStack pullStack(IItemHandler inv, PartImportBus data, int extract, ItemStack matchTo, EnumFacing side) {
		for (int i = 0;i < inv.getSlots();i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (TomsModUtils.areItemStacksEqual(stack, matchTo, data.checkMeta, data.checkNBT, data.checkMod)) {
					if (data.isWhiteList) {
						if (!inv.extractItem(i, stack.getCount(), true).isEmpty()) { return inv.extractItem(i, stack.getCount(), false); }
					}
				} else if (!data.isWhiteList) {
					if (!inv.extractItem(i, stack.getCount(), true).isEmpty()) { return inv.extractItem(i, stack.getCount(), false); }
				}
			}
		}
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("checkMeta", checkMeta);
		tag.setBoolean("checkNBT", checkNBT);
		tag.setBoolean("checkMod", checkMod);
		tag.setBoolean("whitelist", isWhiteList);
		NBTTagCompound stuckStackTag = new NBTTagCompound();
		stuckStack.writeToNBT(stuckStackTag);
		tag.setTag("stuck", stuckStackTag);
		tag.setTag("itemList", TomsModUtils.saveAllItems(filterInv));
		tag.setTag("upgradeList", TomsModUtils.saveAllItems(upgradeInv));
		tag.setInteger("cooldown", this.transferCooldown);
		nbt.setTag(TAG_NBT_NAME, tag);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagCompound tag = nbt.getCompoundTag(TAG_NBT_NAME);
		this.checkMeta = tag.getBoolean("checkMeta");
		this.checkNBT = tag.getBoolean("checkNBT");
		this.checkMod = tag.getBoolean("checkMod");
		this.isWhiteList = tag.getBoolean("whitelist");
		NBTTagCompound stuckTag = tag.getCompoundTag("stuck");
		stuckStack = TomsModUtils.loadItemStackFromNBT(stuckTag);
		TomsModUtils.loadAllItems(tag.getTagList("itemList", 10), filterInv);
		TomsModUtils.loadAllItems(tag.getTagList("upgradeList", 10), upgradeInv);
		this.transferCooldown = tag.getInteger("cooldown");
	}

	@Override
	public Object getGui(EntityPlayer player) {
		return new GuiImportBus(this, player.inventory);
	}

	@Override
	public Object getContainer(EntityPlayer player) {
		return new ContainerImportBus(this, player.inventory);
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (id == 0) {
			isWhiteList = extra == 1;
		}
	}

	@Override
	public IPartSlot getPosition() {
		return EnumFaceSlot.fromFace(getFacing());
	}

	private int getMaxPacketSize() {
		int stackSize = getSpeedUpgradeCount();
		return stackSize == 0 ? 1 : (stackSize == 1 ? 2 : (stackSize == 2 ? 4 : (stackSize == 3 ? 8 : (stackSize == 4 ? 16 : (stackSize == 5 ? 24 : (stackSize == 6 ? 32 : (stackSize == 7 ? 48 : 64)))))));
	}

	private int getSpeedUpgradeCount() {
		return !upgradeInv.getStackInSlot(0).isEmpty() ? upgradeInv.getStackInSlot(0).getCount() : 0;
	}

	public boolean isWhiteList() {
		return isWhiteList;
	}

	public void setWhiteList(boolean isWhiteList) {
		this.isWhiteList = isWhiteList;
	}

	@Override
	public void addExtraDrops(List<ItemStack> list) {
		if (!stuckStack.isEmpty()) {
			list.add(stuckStack);
			stuckStack = ItemStack.EMPTY;
		}
		for (int i = 0;i < upgradeInv.getSizeInventory();i++) {
			ItemStack stack = upgradeInv.removeStackFromSlot(i);
			if (!stack.isEmpty())
				list.add(stack);
		}
	}

	@Override
	public double getPowerDrained() {
		return 0.5D;
	}

	@Override
	public int getPriority() {
		return 0;
	}
}
