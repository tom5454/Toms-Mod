package com.tom.core.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.IGuiTile;
import com.tom.api.tileentity.TileEntityTomsMod;

public class TileEntityItemProxy extends TileEntityTomsMod implements
IEnergyReceiver, ISidedInventory, IGuiTile {
	private EnergyStorage energy = new EnergyStorage(10000000, 10000, 10000);
	private ItemStack[] stack = new ItemStack[31];
	public String owner = "";
	public boolean redstone = true;
	/**White List ?*/
	public boolean mode = false;
	public boolean isItemMode = true;
	public boolean isLocked = false;
	public boolean useNBT = true;
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		NBTTagList tagList = tag.getTagList("Items", 10);
		this.stack = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tabCompound1 = tagList.getCompoundTagAt(i);
			byte byte0 = tabCompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.stack.length) {
				this.stack[byte0] = ItemStack.loadItemStackFromNBT(tabCompound1);
			}
		}
		/*NBTTagList tagList2 = tag.getTagList("Inventory", 10);
		this.outStack = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < tagList2.tagCount(); i++) {
			NBTTagCompound tabCompound1 = tagList2.getCompoundTagAt(i);
			byte byte0 = tabCompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.outStack.length) {
				this.outStack[byte0] = ItemStack.loadItemStackFromNBT(tabCompound1);
			}
		}*/
		this.energy.readFromNBT(tag);
		this.owner = tag.getString("owner");
		this.mode = tag.getBoolean("mode");
		this.isItemMode = tag.getBoolean("isItemMode");
		this.isLocked = tag.getBoolean("isLocked");
		this.useNBT = tag.getBoolean("useNBT");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		NBTTagList tagList = new NBTTagList();
		for (int i = 0; i < this.stack.length; i++) {
			if (this.stack[i] != null) {
				NBTTagCompound tagCompound1 = new NBTTagCompound();
				tagCompound1.setByte("Slot", (byte) i);
				this.stack[i].writeToNBT(tagCompound1);
				tagList.appendTag(tagCompound1);
			}
		}
		tag.setTag("Items", tagList);
		/*NBTTagList tagList2 = new NBTTagList();
		for (int i = 0; i < this.outStack.length; i++) {
			if (this.outStack[i] != null) {
				NBTTagCompound tagCompound1 = new NBTTagCompound();
				tagCompound1.setByte("Slot", (byte) i);
				this.outStack[i].writeToNBT(tagCompound1);
				tagList2.appendTag(tagCompound1);
			}
		}
		tag.setTag("Inventory", tagList2);*/
		this.energy.writeToNBT(tag);
		tag.setString("owner", this.owner);
		//tag.setInteger("mode", TomsMathHelper.getAccess(this.mode));
		tag.setBoolean("mode", this.mode);
		tag.setBoolean("isItemMode", this.isItemMode);
		tag.setBoolean("isLocked", this.isLocked);
		tag.setBoolean("useNBT", this.useNBT);
		return tag;
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2) {
		if (this.stack[par1] != null) {
			ItemStack itemstack;
			if (this.stack[par1].stackSize <= par2) {
				itemstack = this.stack[par1];
				this.stack[par1] = null;
				return itemstack;
			} else {
				itemstack = this.stack[par1].splitStack(par2);

				if (this.stack[par1].stackSize == 0) {
					this.stack[par1] = null;
				}
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getName() {
		return "Mutiblock Hatch";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return 27+4;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.stack[slot];
	}

	/*@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (this.stack[slot] != null) {
			ItemStack itemstack = this.stack[slot];
			this.stack[slot] = null;
			return itemstack;
		} else {
			return null;
		}
	}*/

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return true;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.isPlayerAccess(player) && this.worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		this.stack[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == HV;
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
	public boolean isPlayerAccess(EntityPlayer player){
		return this.isLocked ? player.getName().equals(owner) : true;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack is, EnumFacing arg2) {
		return slot > 26 && is.isItemEqual(this.stack[slot]);
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack is, EnumFacing arg2) {
		return false;
	}
	@Override
	public void updateEntity(){
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.redstone = !(worldObj.isBlockIndirectlyGettingPowered(pos) > 0);
		if(this.redstone && this.energy.getEnergyStored() > 100 && !worldObj.isRemote){
			this.energy.extractEnergy(100, false);
			List<EntityPlayer> entities = worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(xCoord - 16, yCoord - 16, zCoord - 16, xCoord + 17, yCoord + 17, zCoord + 17));
			List<EntityItem> entitiyItems = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(xCoord - 16, yCoord - 16, zCoord - 16, xCoord + 17, yCoord + 17, zCoord + 17));
			for(Entity e : entities){
				if(e instanceof EntityPlayer){
					EntityPlayer player = (EntityPlayer) e;
					if(this.isLocked && player.getName().equals(owner)) continue;
					InventoryPlayer inv = player.inventory;
					for(int i = 0;i<inv.getSizeInventory();i++){
						ItemStack c = inv.getStackInSlot(i);
						if(c != null){
							boolean found = false;
							//ItemStack item = null;
							int invSlot = this.mode ? i : -1;
							for(int j = 0;j<27;j++){
								if(this.stack[j] != null && c != null && this.stack[j].isItemEqual(c) && ((!this.useNBT) || (this.stack[j].getTagCompound() == null && c.getTagCompound() == null) || (this.stack[j].getTagCompound() != null && c.getTagCompound() != null && this.stack[j].getTagCompound().equals(c.getTagCompound())))){
									found = true;
									//item = this.stack[j];
									invSlot = i;
									break;
								}
							}
							if(invSlot != -1 && ((found && !this.mode) || (!found && this.mode))){
								for(int j = 27;j<31;j++){
									ItemStack slotStack = this.stack[j];
									if(c != null){
										if(slotStack == null){
											this.stack[j] = c;
											int ex = c.stackSize + 10;
											double energyEx = this.energy.extractEnergy(ex, true);
											if(energyEx == ex){
												inv.setInventorySlotContents(invSlot, null);
												c = null;
											}
											this.energy.extractEnergy(energyEx, false);
										}else{
											if(slotStack.isItemEqual(c)){
												int space = Math.min(this.getInventoryStackLimit(),c.getMaxStackSize()) - slotStack.stackSize;
												double energyEx = this.energy.extractEnergy(10, true);
												if(energyEx == 10){
													if(space < c.stackSize){
														double ex = space+10;
														energyEx = this.energy.extractEnergy(ex, true);
														//int ext = energyEx-10;
														space = Math.min(Math.min(this.getInventoryStackLimit(), c.stackSize),Math.min(c.getMaxStackSize(),MathHelper.floor_double(this.energy.getEnergyStored()-10))) - slotStack.stackSize;
														slotStack.stackSize = Math.min(this.getInventoryStackLimit(),Math.min(c.getMaxStackSize(),MathHelper.floor_double(this.energy.getEnergyStored()-10)));
														c.stackSize = c.stackSize - MathHelper.floor_double(space);
														energyEx = space+10;
														if(c.stackSize < 1){
															inv.setInventorySlotContents(invSlot, null);
															c = null;
														}
													}else{
														int ex = c.stackSize + 10;
														energyEx = this.energy.extractEnergy(ex, true);
														//int ext = energyEx-10;
														space = Math.min(Math.min(this.getInventoryStackLimit(), c.stackSize),Math.min(c.getMaxStackSize(),MathHelper.floor_double(this.energy.getEnergyStored()-10)));
														slotStack.stackSize = slotStack.stackSize + space;
														c.stackSize = c.stackSize - space;
														energyEx = space + 10;
														if(c.stackSize < 1){ inv.setInventorySlotContents(invSlot, null); c = null;}
													}
													this.energy.extractEnergy(energyEx, false);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if(this.isItemMode){
				for(Entity e : entitiyItems){
					if(e instanceof EntityItem){
						ItemStack c = ((EntityItem)e).getEntityItem();
						if(c != null){
							boolean found = false;
							//ItemStack item = null;
							for(int j = 0;j<27;j++){
								if(this.stack[j] != null && c != null && this.stack[j].isItemEqual(c) && ((!this.useNBT) || ((this.stack[j].getTagCompound() == null && c.getTagCompound() == null) || (this.stack[j].getTagCompound() != null && c.getTagCompound() != null && this.stack[j].getTagCompound().equals(c.getTagCompound()))))){
									found = true;
									//item = this.stack[j];
									break;
								}
							}
							if((found && !this.mode) || (!found && this.mode)){
								for(int j = 27;j<31;j++){
									ItemStack slotStack = this.stack[j];
									if(c != null){
										if(slotStack == null){
											this.stack[j] = c;
											int ex = c.stackSize + 10;
											double energyEx = this.energy.extractEnergy(ex, true);
											if(energyEx == ex){
												e.setDead();
												c = null;
											}
											this.energy.extractEnergy(energyEx, false);
										}else{
											if(slotStack.isItemEqual(c)){
												int space = Math.min(this.getInventoryStackLimit(),c.getMaxStackSize()) - slotStack.stackSize;
												double energyEx = this.energy.extractEnergy(10, true);
												if(energyEx == 10){
													if(space < c.stackSize){
														double ex = space+10;
														energyEx = this.energy.extractEnergy(ex, true);
														//int ext = energyEx-10;
														space = Math.min(Math.min(this.getInventoryStackLimit(), c.stackSize),Math.min(c.getMaxStackSize(),MathHelper.floor_double(this.energy.getEnergyStored()-10))) - slotStack.stackSize;
														slotStack.stackSize = Math.min(this.getInventoryStackLimit(),Math.min(c.getMaxStackSize(),MathHelper.floor_double(this.energy.getEnergyStored()-10)));
														c.stackSize = c.stackSize - space;
														energyEx = space+10;
														if(c.stackSize < 1){
															e.setDead();
															c = null;
														}
													}else{
														int ex = c.stackSize + 10;
														energyEx = this.energy.extractEnergy(ex, true);
														//int ext = energyEx-10;
														space = Math.min(Math.min(this.getInventoryStackLimit(), c.stackSize),Math.min(c.getMaxStackSize(),MathHelper.floor_double(this.energy.getEnergyStored()-10)));
														slotStack.stackSize = slotStack.stackSize + space;
														c.stackSize = c.stackSize - space;
														energyEx = space + 10;
														if(c.stackSize < 1){ e.setDead(); c = null;}
													}
													this.energy.extractEnergy(energyEx, false);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	@Override
	public void writeToPacket(NBTTagCompound buf){
		//ByteBufUtils.writeUTF8String(buf, owner);
		buf.setString("n", owner);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf){
		this.owner = buf.getString("n");
	}
	@Override
	public void buttonPressed(EntityPlayer player, int id,int extra) {
		if(this.isLocked && !player.getName().equals(this.owner)) return;
		if(id == 0) this.mode = !this.mode;
		else if(id == 1) this.isItemMode = !this.isItemMode;
		else if(id == 2) this.isLocked = !this.isLocked;
		else if(id == 3) this.useNBT = !this.useNBT;
		markBlockForUpdate(pos);
	}
	@Override
	public void clear() {
		stack = new ItemStack[31];
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
		ItemStack is = stack[arg0];
		stack[arg0] = null;
		return is;
	}
	@Override
	public void setField(int arg0, int arg1) {

	}
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}
	@Override
	public boolean hasCustomName() {
		return false;
	}
	@Override
	public int[] getSlotsForFace(EnumFacing arg0) {
		return new int[]{27,28,29,30};
	}
	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}
}
