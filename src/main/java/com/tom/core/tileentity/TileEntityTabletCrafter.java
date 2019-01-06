package com.tom.core.tileentity;

import static com.tom.lib.api.energy.EnergyType.HV;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import com.tom.api.item.ILinkContainer;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.energy.EnergyInit;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.IEnergyReceiver;
import com.tom.util.TomsModUtils;

public class TileEntityTabletCrafter extends TileEntityTomsMod implements IEnergyReceiver, ISidedInventory {
	private EnergyStorage energy = new EnergyStorage(1000000, 10000, 1000);
	private static final int[] slotsTop = new int[]{0, 1, 2, 3, 4, 7, 8, 9, 10, 11};
	private static final int[] slotsBottom = new int[]{6};
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	private static final ItemStack[] items = {new ItemStack(CoreInit.TabletHouse), new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE), ItemStack.EMPTY, new ItemStack(CoreInit.Tablet), new ItemStack(Items.IRON_INGOT), new ItemStack(EnergyInit.battery), new ItemStack(CoreInit.memoryCard), CraftingMaterial.DISPLAY.getStackNormal(), new ItemStack(CoreInit.Tablet)};

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == HV;
	}

	@Override
	public String getName() {
		return "Tablet Crafter";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public int getSizeInventory() {
		return 12;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		if (!is.isEmpty()) {
			if (slot == 5) {
				return (is.getItem() instanceof ILinkContainer && is.getTagCompound() != null && is.getTagCompound().hasKey("x") && is.getTagCompound().hasKey("y") && is.getTagCompound().hasKey("z"));
			} else if (slot == 10) {
				return items[10].isItemEqual(is) || is.getItem() == CoreInit.connectionModem;
			} else if (slot == 8) {
				return items[8].isItemEqual(is) || is.getItem() == CoreInit.trProcessor || is.getItem() == CoreInit.linkedChipset;
			} else if (slot == 6) { return items[6].isItemEqual(is) || is.getItem() == CoreInit.connectionModem; }
			/*if(slot == 0){
				return is.getItem() == CoreInit.TabletHouse;
			}
			if(slot == 1 || slot == 2 || slot == 3 || slot == 4){
				return is.getItem() == Items.redstone;
			}
			if(slot == 7){
				return is.getItem() == Items.iron_ingot;
			}
			if(slot == 8){
				return is.getItem() == CoreInit.Battery;
			}
			if(slot == 9){
				return is.getItem() == CoreInit.memoryCard;
			}
			if(slot == 10){
				return is.getItem() == CoreInit.Display;
			}
			if(slot == 11 || slot == 6){
				return is.getItem() == CoreInit.Tablet;
			}*/
			return items[slot].isItemEqual(is);
		}
		return false;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer arg0) {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		return TomsModUtils.isUseable(xCoord, yCoord, zCoord, arg0, world, this);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing side) {
		return itemstack != null;
	}

	@Override
	public boolean canInsertItem(int par1, ItemStack itemstack, EnumFacing side) {
		return par1 != 6 ? this.isItemValidForSlot(par1, itemstack) : false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.DOWN ? slotsBottom : slotsTop;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return this.canConnectEnergy(from, type) ? this.energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getEnergyStored();
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getMaxEnergyStored();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy.readFromNBT(tag);
		TomsModUtils.loadAllItems(tag.getTagList("Items", 10), inv);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.energy.writeToNBT(tag);
		tag.setTag("Items", TomsModUtils.saveAllItems(inv));
		return tag;
	}

	@Override
	public void updateEntity() {
		if (this.energy.getEnergyStored() > 2000) {
			boolean craft = this.craft(true);
			// System.out.println("craft:"+craft);
			if (craft) {
				this.craft(false);
				this.energy.extractEnergy(2000, false);
			} else if (!this.inv.getStackInSlot(11).isEmpty() && this.inv.getStackInSlot(11).getItem() instanceof ILinkContainer && !this.inv.getStackInSlot(6).isEmpty() && this.inv.getStackInSlot(5).getItem() instanceof ILinkContainer && this.inv.getStackInSlot(5).getTagCompound() != null && this.inv.getStackInSlot(5).getTagCompound().hasKey("x") && this.inv.getStackInSlot(5).getTagCompound().hasKey("y") && this.inv.getStackInSlot(5).getTagCompound().hasKey("z")) {
				NBTTagCompound tag = this.inv.getStackInSlot(5).getTagCompound();
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				if (this.inv.getStackInSlot(11).getItem() == CoreInit.Tablet) {
					TileEntity tile = this.world.getTileEntity(new BlockPos(x, y, z));
					if (tile instanceof TileEntityTabletController) {
						this.inv.setInventorySlotContents(6, this.inv.getStackInSlot(11));
						this.inv.setInventorySlotContents(11, ItemStack.EMPTY);
						this.energy.extractEnergy(100, false);
						if (this.inv.getStackInSlot(6).getTagCompound() == null)
							this.inv.getStackInSlot(6).setTagCompound(new NBTTagCompound());
						this.inv.getStackInSlot(6).getTagCompound().setInteger("x", x);
						this.inv.getStackInSlot(6).getTagCompound().setInteger("y", y);
						this.inv.getStackInSlot(6).getTagCompound().setInteger("z", z);
						//TileEntityTabletController te = (TileEntityTabletController) tile;
						/*int id = te.connectNewTablet();
						this.inv.getStackInSlot(6).getTagCompound().setInteger("id", id);*/
					}
				} else {
					this.inv.setInventorySlotContents(6, this.inv.getStackInSlot(11));
					this.inv.setInventorySlotContents(11, ItemStack.EMPTY);
					this.energy.extractEnergy(100, false);
					if (this.inv.getStackInSlot(6).getTagCompound() == null)
						this.inv.getStackInSlot(6).setTagCompound(new NBTTagCompound());
					this.inv.getStackInSlot(6).getTagCompound().setInteger("x", x);
					this.inv.getStackInSlot(6).getTagCompound().setInteger("y", y);
					this.inv.getStackInSlot(6).getTagCompound().setInteger("z", z);
				}
			} else if (!this.inv.getStackInSlot(10).isEmpty() && this.inv.getStackInSlot(10).getItem() == CoreInit.connectionModem && !this.inv.getStackInSlot(8).isEmpty() && !this.inv.getStackInSlot(2).isEmpty() && this.inv.getStackInSlot(3).isEmpty() && !this.inv.getStackInSlot(4).isEmpty() && !this.inv.getStackInSlot(1).isEmpty() && this.inv.getStackInSlot(8).getTagCompound() != null && this.inv.getStackInSlot(2).getItem() == Items.REDSTONE && this.inv.getStackInSlot(3).getItem() == Items.REDSTONE && this.inv.getStackInSlot(4).getItem() == Items.REDSTONE && this.inv.getStackInSlot(1).getItem() == Items.REDSTONE && this.inv.getStackInSlot(6).isEmpty()) {
				if (this.inv.getStackInSlot(8).getItem() == CoreInit.linkedChipset && this.inv.getStackInSlot(8).getTagCompound().hasKey("x") && this.inv.getStackInSlot(8).getTagCompound().hasKey("y") && this.inv.getStackInSlot(8).getTagCompound().hasKey("z")) {
					NBTTagList linkList = new NBTTagList();
					if (this.inv.getStackInSlot(10).getTagCompound() == null) {
						this.inv.getStackInSlot(10).setTagCompound(new NBTTagCompound());
					}
					if (this.inv.getStackInSlot(10).getTagCompound().hasKey("linkList")) {
						linkList = this.inv.getStackInSlot(10).getTagCompound().getTagList("linkList", 10);
					}
					int x = this.inv.getStackInSlot(8).getTagCompound().getInteger("x");
					int y = this.inv.getStackInSlot(8).getTagCompound().getInteger("y");
					int z = this.inv.getStackInSlot(8).getTagCompound().getInteger("z");
					NBTTagCompound tag = new NBTTagCompound();
					tag.setInteger("x", x);
					tag.setInteger("y", y);
					tag.setInteger("z", z);
					this.inv.getStackInSlot(2).shrink(1);
					if (this.inv.getStackInSlot(2).isEmpty())
						this.inv.setInventorySlotContents(2, ItemStack.EMPTY);
					this.inv.getStackInSlot(3).shrink(1);
					if (this.inv.getStackInSlot(3).isEmpty())
						this.inv.setInventorySlotContents(3, ItemStack.EMPTY);
					this.inv.getStackInSlot(4).shrink(1);
					if (this.inv.getStackInSlot(4).isEmpty())
						this.inv.setInventorySlotContents(4, ItemStack.EMPTY);
					this.inv.getStackInSlot(1).shrink(1);
					if (this.inv.getStackInSlot(1).isEmpty())
						this.inv.setInventorySlotContents(1, ItemStack.EMPTY);
					this.inv.getStackInSlot(8).shrink(1);
					if (this.inv.getStackInSlot(8).isEmpty())
						this.inv.setInventorySlotContents(8, ItemStack.EMPTY);
					this.energy.extractEnergy(100, false);
					linkList.appendTag(tag);
					this.inv.getStackInSlot(10).getTagCompound().setTag("linkList", linkList);
					this.inv.setInventorySlotContents(6, this.inv.getStackInSlot(10));
					this.inv.setInventorySlotContents(10, ItemStack.EMPTY);
				} else if (this.inv.getStackInSlot(8).getItem() == CoreInit.trProcessor && this.inv.getStackInSlot(8).getTagCompound().hasKey("tier")) {
					int tier = 0;
					String d = this.inv.getStackInSlot(8).getTagCompound().hasKey("d") ? this.inv.getStackInSlot(8).getTagCompound().getString("d") : "ap";
					String tagName = d == "an" ? "tierAnt" : (d == "m" ? "tierMagCard" : "tier");
					if (this.inv.getStackInSlot(10).getTagCompound() == null) {
						this.inv.getStackInSlot(10).setTagCompound(new NBTTagCompound());
					}
					if (this.inv.getStackInSlot(10).getTagCompound().hasKey(tagName)) {
						tier = this.inv.getStackInSlot(10).getTagCompound().getInteger(tagName);
					}
					int chipTier = this.inv.getStackInSlot(8).getTagCompound().getInteger("tier");
					if (tier + 1 == chipTier) {
						this.inv.getStackInSlot(2).shrink(1);
						if (this.inv.getStackInSlot(2).isEmpty())
							this.inv.setInventorySlotContents(2, ItemStack.EMPTY);
						this.inv.getStackInSlot(3).shrink(1);
						if (this.inv.getStackInSlot(3).isEmpty())
							this.inv.setInventorySlotContents(3, ItemStack.EMPTY);
						this.inv.getStackInSlot(4).shrink(1);
						if (this.inv.getStackInSlot(4).isEmpty())
							this.inv.setInventorySlotContents(4, ItemStack.EMPTY);
						this.inv.getStackInSlot(1).shrink(1);
						if (this.inv.getStackInSlot(1).isEmpty())
							this.inv.setInventorySlotContents(1, ItemStack.EMPTY);
						this.inv.getStackInSlot(8).shrink(1);
						if (this.inv.getStackInSlot(8).isEmpty())
							this.inv.setInventorySlotContents(8, ItemStack.EMPTY);
						this.energy.extractEnergy(100, false);
						this.inv.getStackInSlot(10).getTagCompound().setInteger(tagName, chipTier);
						this.inv.setInventorySlotContents(6, this.inv.getStackInSlot(10));
						this.inv.setInventorySlotContents(10, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	private boolean craft(boolean simulate) {
		int x = 0;
		int y = 0;
		int z = 0;
		for (int i = 0;i < getSizeInventory() - 1;i++) {
			if (this.inv.getStackInSlot(i).isEmpty() && i != 6) { return false; }
			if ((this.inv.getStackInSlot(i) != null && i != 6) && (items[i].isItemEqual(this.inv.getStackInSlot(i)) || (i == 5 && (this.inv.getStackInSlot(i).getItem() instanceof ILinkContainer && this.inv.getStackInSlot(i).getTagCompound() != null && this.inv.getStackInSlot(i).getTagCompound().hasKey("x") && this.inv.getStackInSlot(i).getTagCompound().hasKey("y") && this.inv.getStackInSlot(i).getTagCompound().hasKey("z"))))) {
				// System.out.println("c:"+i);
				if (i != 5 && i != 6 && !simulate) {
					this.inv.getStackInSlot(i).shrink(1);
					if (this.inv.getStackInSlot(i).isEmpty())
						this.inv.setInventorySlotContents(i, ItemStack.EMPTY);
				}
				if (i == 5 && !simulate) {
					NBTTagCompound tag = this.inv.getStackInSlot(i).getTagCompound();
					x = tag.getInteger("x");
					y = tag.getInteger("y");
					z = tag.getInteger("z");
				}

			} else if (i == 6) {
				if (!this.inv.getStackInSlot(i).isEmpty()) {
					return false;
				} else if (!simulate) {
					// System.out.println("craft");
					this.inv.setInventorySlotContents(i, new ItemStack(CoreInit.Tablet));
					this.inv.getStackInSlot(i).setTagCompound(new NBTTagCompound());
					TileEntity tile = this.world.getTileEntity(new BlockPos(x, y, z));
					if (tile instanceof TileEntityTabletController) {
						this.inv.getStackInSlot(i).getTagCompound().setInteger("x", x);
						this.inv.getStackInSlot(i).getTagCompound().setInteger("y", y);
						this.inv.getStackInSlot(i).getTagCompound().setInteger("z", z);
						//TileEntityTabletController te = (TileEntityTabletController) tile;
						//int id = te.connectNewTablet();
						//this.inv.getStackInSlot(i).getTagCompound().setInteger("id", id);
					}
					this.inv.getStackInSlot(i).getTagCompound().setInteger("Energy", 1000);
				}
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public void closeInventory(EntityPlayer arg0) {

	}

	@Override
	public int getField(int arg0) {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void openInventory(EntityPlayer arg0) {

	}

	@Override
	public void setField(int arg0, int arg1) {

	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public void clear() {
		inv.clear();
	}
}
