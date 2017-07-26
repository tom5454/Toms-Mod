package com.tom.storage.multipart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.items.IItemHandler;

import com.tom.api.inventory.IStorageInventory;
import com.tom.api.inventory.IStorageInventory.BasicFilter;
import com.tom.api.inventory.IStorageInventory.BasicFilter.IFilteringInformation;
import com.tom.api.inventory.IStorageInventory.BasicFilter.Mode;
import com.tom.api.inventory.IStorageInventory.FilteredStorageInventory;
import com.tom.api.multipart.IGuiMultipart;
import com.tom.apis.TomsModUtils;
import com.tom.storage.handler.StorageNetworkGrid.IChannelLoadListener;
import com.tom.storage.tileentity.gui.GuiStorageBus;
import com.tom.storage.tileentity.inventory.ContainerStorageBus;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.EnumFaceSlot;
import mcmultipart.api.slot.IPartSlot;

public class PartStorageBus extends PartChannelModule implements IGuiMultipart, IChannelLoadListener {
	private boolean checkNBT = true;
	private boolean checkMeta = true;
	private boolean checkMod = false;
	private boolean canViewAll = false;
	private boolean isWhiteList = true;
	private Mode mode = Mode.IO;
	private static final String TAG_NBT_NAME = "config";
	private IStorageInventory data = null;
	public InventoryBasic filterInv = new InventoryBasic("", false, 36);

	@Override
	public void updateEntityI() {
		if (!world.isRemote) {
			EnumFacing f = getFacing();
			BlockPos pos = getPos2().offset(f);
			final IItemHandler inventory = TomsModUtils.getItemHandler(world, pos, f.getOpposite(), false);
			if (inventory != null && isActive().fullyActive()) {
				if (data == null) {
					data = new FilteredStorageInventory(inventory, 0, new BasicFilter(filterInv, new IFilteringInformation() {

						@Override
						public boolean useNBT() {
							return checkNBT;
						}

						@Override
						public boolean useMod() {
							return checkMod;
						}

						@Override
						public boolean useMeta() {
							return checkMeta;
						}

						@Override
						public boolean canViewAll() {
							return canViewAll;
						}

						@Override
						public Mode getMode() {
							return mode;
						}

						@Override
						public boolean isWhiteList() {
							return isWhiteList;
						}
					}), f.getOpposite(), grid);
				}
				((FilteredStorageInventory) data).update(null, inventory, 0);
				grid.getData().addInventory(data);
			} else {
				if (data != null) {
					grid.getData().removeInventory(data);
					data = null;
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("checkMeta", checkMeta);
		tag.setBoolean("checkNBT", checkNBT);
		tag.setBoolean("checkMod", checkMod);
		tag.setTag("itemList", TomsModUtils.saveAllItems(filterInv));
		tag.setBoolean("canView", canViewAll);
		tag.setBoolean("whitelist", isWhiteList);
		tag.setInteger("mode", mode.ordinal());
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
		this.canViewAll = tag.getBoolean("canView");
		this.isWhiteList = tag.getBoolean("whitelist");
		this.mode = Mode.VALUES[tag.getInteger("mode")];
	}

	@Override
	public Object getGui(EntityPlayer player) {
		return new GuiStorageBus(this, player.inventory);
	}

	@Override
	public Object getContainer(EntityPlayer player) {
		return new ContainerStorageBus(this, player.inventory);
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int value) {
		if (id == 0) {
			this.setWhiteList(value == 1);
		} else if (id == 1) {
			this.setCheckMeta(value == 1);
		} else if (id == 2) {
			this.setCheckNBT(value == 1);
		} else if (id == 3) {
			this.setCheckMod(value == 1);
		} else if (id == 4) {
			this.setCanViewAll(value == 1);
		} else if (id == 5) {
			this.setMode(Mode.VALUES[value % Mode.VALUES.length]);
		}
	}

	@Override
	public IPartSlot getPosition() {
		IMultipartContainer c = MultipartHelper.getContainer(world, pos).orElse(null);
		return c != null ? EnumFaceSlot.fromFace(getFacing()) : null;
	}

	@Override
	public double getPowerDrained() {
		return 0.6D;
	}

	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public void invalidateGrid() {
		if (data != null)
			grid.getData().removeInventory(data);
		super.invalidateGrid();
	}

	@Override
	public void onPartsUpdate() {
		updateEntityI();
	}

	public boolean isWhiteList() {
		return isWhiteList;
	}

	public boolean isCheckNBT() {
		return checkNBT;
	}

	public void setCheckNBT(boolean checkNBT) {
		this.checkNBT = checkNBT;
	}

	public boolean isCheckMeta() {
		return checkMeta;
	}

	public void setCheckMeta(boolean checkMeta) {
		this.checkMeta = checkMeta;
	}

	public boolean isCheckMod() {
		return checkMod;
	}

	public void setCheckMod(boolean checkMod) {
		this.checkMod = checkMod;
	}

	public boolean isCanViewAll() {
		return canViewAll;
	}

	public void setCanViewAll(boolean canViewAll) {
		this.canViewAll = canViewAll;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setWhiteList(boolean isWhiteList) {
		this.isWhiteList = isWhiteList;
	}
}
