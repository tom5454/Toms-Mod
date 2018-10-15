package com.tom.storage.multipart;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.tom.api.multipart.IGuiMultipart;
import com.tom.storage.tileentity.gui.GuiExportBus;
import com.tom.storage.tileentity.inventory.ContainerExportBus;
import com.tom.util.TomsModUtils;

import mcmultipart.api.slot.EnumFaceSlot;
import mcmultipart.api.slot.IPartSlot;

public class PartExportBus extends PartChannelModule implements IGuiMultipart {
	private boolean checkNBT = true;
	private boolean checkMeta = true;
	private boolean checkMod = false;
	private static final String TAG_NBT_NAME = "config";
	private int transferCooldown = 1;
	public InventoryBasic filterInv = new InventoryBasic("", false, 9);
	public InventoryBasic upgradeInv = new InventoryBasic("", false, 1);
	public ItemStack stuckStack = ItemStack.EMPTY;

	@Override
	public void updateEntityI() {
		// world.profiler.startSection("partexportbus");
		if (!world.isRemote) {
			if (isActiveForUpdate()) {
				int upgradeCount = getSpeedUpgradeCount();
				if (transferCooldown == 0) {
					if (!stuckStack.isEmpty()) {
						BlockPos pos = getPos2().offset(getFacing());
						IInventory inv = TileEntityHopper.getInventoryAtPosition(world, pos.getX(), pos.getY(), pos.getZ());
						if (inv != null) {
							stuckStack = TileEntityHopper.putStackInInventoryAllSlots(inv, inv, stuckStack, getFacing().getOpposite());
						}
						stuckStack = grid.pushStack(stuckStack);
						transferCooldown = 10;
					} else {
						BlockPos pos = getPos2().offset(getFacing());
						IItemHandler inv = TomsModUtils.getItemHandler(world, pos, getFacing().getOpposite(), true);
						if (inv != null) {
							for (int i = 0;i < filterInv.getSizeInventory();i++) {
								ItemStack valid = filterInv.getStackInSlot(i);
								if (!valid.isEmpty()) {
									valid = valid.copy();
									valid.setCount(Math.min(getMaxPacketSize(), valid.getMaxStackSize()));
									ItemStack pulledStack = grid.pullStack(valid, checkMeta, checkNBT, checkMod);
									if (!pulledStack.isEmpty()) {
										pulledStack = ItemHandlerHelper.insertItemStacked(inv, pulledStack, false);
										if (!pulledStack.isEmpty()) {
											pulledStack = grid.pushStack(pulledStack);
											if (!pulledStack.isEmpty()) {
												stuckStack = pulledStack;
											}
										}
										transferCooldown = upgradeCount > 3 ? (upgradeCount > 5 ? 2 : 5) : 16;
										break;
									}
								}
							}
							if (transferCooldown == 0)
								transferCooldown = upgradeCount > 4 ? 24 : 60;
						}
					}
				} else if (transferCooldown > 0)
					transferCooldown--;
				else
					transferCooldown = 1;
			}
		}
		// world.profiler.endSection();
	}

	@Override
	public Object getGui(EntityPlayer player) {
		return new GuiExportBus(this, player.inventory);
	}

	@Override
	public Object getContainer(EntityPlayer player) {
		return new ContainerExportBus(this, player.inventory);
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {

	}

	private int getMaxPacketSize() {
		int stackSize = getSpeedUpgradeCount();
		return stackSize == 0 ? 1 : (stackSize == 1 ? 2 : (stackSize == 2 ? 4 : (stackSize == 3 ? 8 : (stackSize == 4 ? 16 : (stackSize == 5 ? 24 : (stackSize == 6 ? 32 : (stackSize == 7 ? 48 : 64)))))));
	}

	private int getSpeedUpgradeCount() {
		return !upgradeInv.getStackInSlot(0).isEmpty() ? upgradeInv.getStackInSlot(0).getCount() : 0;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("checkMeta", checkMeta);
		tag.setBoolean("checkNBT", checkNBT);
		tag.setBoolean("checkMod", checkMod);
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
		TomsModUtils.loadAllItems(tag.getTagList("itemList", 10), filterInv);
		TomsModUtils.loadAllItems(tag.getTagList("upgradeList", 10), upgradeInv);
		this.transferCooldown = tag.getInteger("cooldown");
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
		return 0.5;
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public IPartSlot getPosition() {
		return EnumFaceSlot.fromFace(getFacing());
	}
}
