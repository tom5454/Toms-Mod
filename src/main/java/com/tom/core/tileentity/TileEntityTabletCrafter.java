package com.tom.core.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.item.ILinkContainer;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;

public class TileEntityTabletCrafter extends TileEntityTomsMod implements
IEnergyReceiver, ISidedInventory {
	private EnergyStorage energy = new EnergyStorage(1000000,10000,1000);
	private static final int[] slotsTop = new int[]{0,1,2,3,4,7,8,9,10,11};
	private static final int[] slotsBottom = new int[]{6};
	private ItemStack[] machineItemStacks = new ItemStack[12];
	private static final Item[] items = {CoreInit.TabletHouse,Items.REDSTONE,Items.REDSTONE,Items.REDSTONE
			,Items.REDSTONE,null,CoreInit.Tablet,Items.IRON_INGOT,CoreInit.Battery,CoreInit.memoryCard,CoreInit.Display,CoreInit.Tablet};
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == HV;
	}

	@Override
	public ItemStack decrStackSize(int slot, int par2) {
		//if(slot == 5) return null;
		if (this.machineItemStacks[slot] != null) {
			ItemStack itemstack;
			if (this.machineItemStacks[slot].stackSize <= par2) {
				itemstack = this.machineItemStacks[slot];
				this.machineItemStacks[slot] = null;
				return itemstack;
			} else {
				itemstack = this.machineItemStacks[slot].splitStack(par2);

				if (this.machineItemStacks[slot].stackSize == 0) {
					this.machineItemStacks[slot] = null;
				}
				return itemstack;
			}
		} else {
			return null;
		}
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
	public ItemStack getStackInSlot(int slot) {
		return this.machineItemStacks[slot];
	}

	/*@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (this.machineItemStacks[slot] != null) {
			ItemStack itemstack = this.machineItemStacks[slot];
			this.machineItemStacks[slot] = null;
			return itemstack;
		} else {
			return null;
		}
	}*/

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		if(is != null){
			if(slot == 5){
				return (is.getItem() instanceof ILinkContainer && is.getTagCompound() != null && is.getTagCompound().hasKey("x")
						&& is.getTagCompound().hasKey("y") && is.getTagCompound().hasKey("z"));
			}else if(slot == 10){
				return items[10] == is.getItem() || is.getItem() == CoreInit.connectionModem;
			}else if(slot == 8){
				return items[8] == is.getItem() || is.getItem() == CoreInit.trProcessor || is.getItem() == CoreInit.linkedChipset;
			}else if(slot == 6){
				return items[6] == is.getItem() || is.getItem() == CoreInit.connectionModem;
			}
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
			return items[slot] == is.getItem();
		}
		return false;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer arg0) {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		return TomsModUtils.isUseable(xCoord, yCoord, zCoord, arg0, worldObj, this);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		this.machineItemStacks[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}
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
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive,
			boolean simulate) {
		return this.canConnectEnergy(from, type) ? this.energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getMaxEnergyStored();
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.energy.readFromNBT(tag);
		NBTTagList tagList = tag.getTagList("Items", 10);
		this.machineItemStacks = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tabCompound1 = tagList.getCompoundTagAt(i);
			byte byte0 = tabCompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.machineItemStacks.length) {
				this.machineItemStacks[byte0] = ItemStack.loadItemStackFromNBT(tabCompound1);
			}
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		this.energy.writeToNBT(tag);
		NBTTagList tagList = new NBTTagList();
		for (int i = 0; i < this.machineItemStacks.length; i++) {
			if (this.machineItemStacks[i] != null) {
				NBTTagCompound tagCompound1 = new NBTTagCompound();
				tagCompound1.setByte("Slot", (byte) i);
				this.machineItemStacks[i].writeToNBT(tagCompound1);
				tagList.appendTag(tagCompound1);
			}
		}
		tag.setTag("Items", tagList);
		return tag;
	}
	@Override
	public void updateEntity(){
		if(this.energy.getEnergyStored() > 2000){
			boolean craft = this.craft(true);
			//System.out.println("craft:"+craft);
			if(craft){
				this.craft(false);
				this.energy.extractEnergy(2000, false);
			}else if(this.machineItemStacks[11] != null && this.machineItemStacks[11].getItem() instanceof ILinkContainer && this.machineItemStacks[6] == null &&
					this.machineItemStacks[5].getItem() instanceof ILinkContainer && this.machineItemStacks[5].getTagCompound() != null &&
					this.machineItemStacks[5].getTagCompound().hasKey("x") && this.machineItemStacks[5].getTagCompound().hasKey("y") &&
					this.machineItemStacks[5].getTagCompound().hasKey("z")){
				NBTTagCompound tag = this.machineItemStacks[5].getTagCompound();
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				if(this.machineItemStacks[11].getItem() == CoreInit.Tablet){
					TileEntity tile = this.worldObj.getTileEntity(new BlockPos(x, y, z));
					if(tile instanceof TileEntityTabletController){
						this.machineItemStacks[6] = this.machineItemStacks[11];
						this.machineItemStacks[11] = null;
						this.energy.extractEnergy(100, false);
						if(this.machineItemStacks[6].getTagCompound() == null)
							this.machineItemStacks[6].setTagCompound(new NBTTagCompound());
						this.machineItemStacks[6].getTagCompound().setInteger("x", x);
						this.machineItemStacks[6].getTagCompound().setInteger("y", y);
						this.machineItemStacks[6].getTagCompound().setInteger("z", z);
						TileEntityTabletController te = (TileEntityTabletController) tile;
						int id = te.connectNewTablet();
						this.machineItemStacks[6].getTagCompound().setInteger("id", id);
					}
				}else{
					this.machineItemStacks[6] = this.machineItemStacks[11];
					this.machineItemStacks[11] = null;
					this.energy.extractEnergy(100, false);
					if(this.machineItemStacks[6].getTagCompound() == null)
						this.machineItemStacks[6].setTagCompound(new NBTTagCompound());
					this.machineItemStacks[6].getTagCompound().setInteger("x", x);
					this.machineItemStacks[6].getTagCompound().setInteger("y", y);
					this.machineItemStacks[6].getTagCompound().setInteger("z", z);
				}
			}else if(this.machineItemStacks[10] != null && this.machineItemStacks[10].getItem() == CoreInit.connectionModem &&
					this.machineItemStacks[8] != null && this.machineItemStacks[2] != null && this.machineItemStacks[3] != null &&
					this.machineItemStacks[4] != null && this.machineItemStacks[1] != null && this.machineItemStacks[8].getTagCompound() != null &&
					this.machineItemStacks[2].getItem() == Items.REDSTONE && this.machineItemStacks[3].getItem() == Items.REDSTONE &&
					this.machineItemStacks[4].getItem() == Items.REDSTONE && this.machineItemStacks[1].getItem() == Items.REDSTONE &&
					this.machineItemStacks[6] == null){
				if(this.machineItemStacks[8].getItem() == CoreInit.linkedChipset && this.machineItemStacks[8].getTagCompound().hasKey("x") &&
						this.machineItemStacks[8].getTagCompound().hasKey("y") && this.machineItemStacks[8].getTagCompound().hasKey("z")){
					NBTTagList linkList = new NBTTagList();
					if(this.machineItemStacks[10].getTagCompound() == null){
						this.machineItemStacks[10].setTagCompound(new NBTTagCompound());
					}
					if(this.machineItemStacks[10].getTagCompound().hasKey("linkList")){
						linkList = this.machineItemStacks[10].getTagCompound().getTagList("linkList", 10);
					}
					int x = this.machineItemStacks[8].getTagCompound().getInteger("x");
					int y = this.machineItemStacks[8].getTagCompound().getInteger("y");
					int z = this.machineItemStacks[8].getTagCompound().getInteger("z");
					NBTTagCompound tag = new NBTTagCompound();
					tag.setInteger("x",x);
					tag.setInteger("y",y);
					tag.setInteger("z",z);
					this.machineItemStacks[2].stackSize = this.machineItemStacks[2].stackSize-1;
					if(this.machineItemStacks[2].stackSize == 0)this.machineItemStacks[2] = null;
					this.machineItemStacks[3].stackSize = this.machineItemStacks[3].stackSize-1;
					if(this.machineItemStacks[3].stackSize == 0)this.machineItemStacks[3] = null;
					this.machineItemStacks[4].stackSize = this.machineItemStacks[4].stackSize-1;
					if(this.machineItemStacks[4].stackSize == 0)this.machineItemStacks[4] = null;
					this.machineItemStacks[1].stackSize = this.machineItemStacks[1].stackSize-1;
					if(this.machineItemStacks[1].stackSize == 0)this.machineItemStacks[1] = null;
					this.machineItemStacks[8].stackSize = this.machineItemStacks[8].stackSize-1;
					if(this.machineItemStacks[8].stackSize == 0)this.machineItemStacks[8] = null;
					this.energy.extractEnergy(100, false);
					linkList.appendTag(tag);
					this.machineItemStacks[10].getTagCompound().setTag("linkList",linkList);
					this.machineItemStacks[6] = this.machineItemStacks[10];
					this.machineItemStacks[10] = null;
				}else if(this.machineItemStacks[8].getItem() == CoreInit.trProcessor && this.machineItemStacks[8].getTagCompound().hasKey("tier")){
					int tier = 0;
					String d = this.machineItemStacks[8].getTagCompound().hasKey("d") ? this.machineItemStacks[8].getTagCompound().getString("d") : "ap";
					String tagName = d == "an" ? "tierAnt" : (d == "m" ? "tierMagCard" : "tier");
					if(this.machineItemStacks[10].getTagCompound() == null){
						this.machineItemStacks[10].setTagCompound(new NBTTagCompound());
					}
					if(this.machineItemStacks[10].getTagCompound().hasKey(tagName)){
						tier = this.machineItemStacks[10].getTagCompound().getInteger(tagName);
					}
					int chipTier = this.machineItemStacks[8].getTagCompound().getInteger("tier");
					if(tier + 1 == chipTier){
						this.machineItemStacks[2].stackSize = this.machineItemStacks[2].stackSize-1;
						if(this.machineItemStacks[2].stackSize == 0)this.machineItemStacks[2] = null;
						this.machineItemStacks[3].stackSize = this.machineItemStacks[3].stackSize-1;
						if(this.machineItemStacks[3].stackSize == 0)this.machineItemStacks[3] = null;
						this.machineItemStacks[4].stackSize = this.machineItemStacks[4].stackSize-1;
						if(this.machineItemStacks[4].stackSize == 0)this.machineItemStacks[4] = null;
						this.machineItemStacks[1].stackSize = this.machineItemStacks[1].stackSize-1;
						if(this.machineItemStacks[1].stackSize == 0)this.machineItemStacks[1] = null;
						this.machineItemStacks[8].stackSize = this.machineItemStacks[8].stackSize-1;
						if(this.machineItemStacks[8].stackSize == 0)this.machineItemStacks[8] = null;
						this.energy.extractEnergy(100, false);
						this.machineItemStacks[10].getTagCompound().setInteger(tagName, chipTier);
						this.machineItemStacks[6] = this.machineItemStacks[10];
						this.machineItemStacks[10] = null;
					}
				}
			}
		}
	}
	private boolean craft(boolean simulate){
		int x = 0;
		int y = 0;
		int z = 0;
		for(int i = 0;i<this.machineItemStacks.length-1;i++){
			if(this.machineItemStacks[i] == null && i != 6){
				return false;
			}
			if((this.machineItemStacks[i] != null && i != 6) &&
					(this.machineItemStacks[i].getItem() == items[i] || (i == 5 && (
							this.machineItemStacks[i].getItem() instanceof ILinkContainer && this.machineItemStacks[i].getTagCompound() != null &&
							this.machineItemStacks[i].getTagCompound().hasKey("x") && this.machineItemStacks[i].getTagCompound().hasKey("y") &&
							this.machineItemStacks[i].getTagCompound().hasKey("z"))))){
				//System.out.println("c:"+i);
				if(i != 5 && i != 6 && !simulate){
					this.machineItemStacks[i].stackSize = this.machineItemStacks[i].stackSize-1;
					if(this.machineItemStacks[i].stackSize == 0)this.machineItemStacks[i] = null;
				}
				if(i == 5 && !simulate){
					NBTTagCompound tag = this.machineItemStacks[i].getTagCompound();
					x = tag.getInteger("x");
					y = tag.getInteger("y");
					z = tag.getInteger("z");
				}

			}else if(i == 6){
				if(this.machineItemStacks[i] != null){
					return false;
				}else if(!simulate){
					//System.out.println("craft");
					this.machineItemStacks[i] = new ItemStack(CoreInit.Tablet);
					this.machineItemStacks[i].setTagCompound(new NBTTagCompound());
					TileEntity tile = this.worldObj.getTileEntity(new BlockPos(x, y, z));
					if(tile instanceof TileEntityTabletController){
						this.machineItemStacks[i].getTagCompound().setInteger("x", x);
						this.machineItemStacks[i].getTagCompound().setInteger("y", y);
						this.machineItemStacks[i].getTagCompound().setInteger("z", z);
						TileEntityTabletController te = (TileEntityTabletController) tile;
						int id = te.connectNewTablet();
						this.machineItemStacks[i].getTagCompound().setInteger("id", id);
					}
					this.machineItemStacks[i].getTagCompound().setInteger("Energy", 1000);
				}
			}else{
				return false;
			}
		}
		return true;
	}

	@Override
	public void clear() {

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
	public ItemStack removeStackFromSlot(int arg0) {
		return null;
	}

	@Override
	public void setField(int arg0, int arg1) {

	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}
}
