package com.tom.factory.tileentity;

import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityMultiblockPartBase;
import com.tom.factory.block.MultiblockHatch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class TileEntityMBHatch extends TileEntityMultiblockPartBase implements IInventory/*, ISecurable, IGUITextFieldSensitive*/{
	private int masterX = 0;
	private int masterY = 0;
	private int masterZ = 0;
	private boolean formed = false;
	private boolean hasMaster = false;
	private ItemStack[] stack = new ItemStack[4];
	//public boolean input = false;
	/*private String owner = "";
	private AccessMode mode = AccessMode.PUBLIC;
	private List<String> list = new ArrayList<String>();
	public String textFieldText = "";*/

	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.masterX = tag.getInteger("mX");
		this.masterY = tag.getInteger("mY");
		this.masterZ = tag.getInteger("mZ");
		this.hasMaster = tag.getBoolean("hM");
		this.formed = tag.getBoolean("formed");
		NBTTagList tagList = tag.getTagList("Items", 10);
		this.stack = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tabCompound1 = tagList.getCompoundTagAt(i);
			byte byte0 = tabCompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.stack.length) {
				this.stack[byte0] = ItemStack.loadItemStackFromNBT(tabCompound1);
			}
		}
		//this.input = tag.getBoolean("mode");
		//this.mode = TomsMathHelper.getAccess(tag.getInteger("mode"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("mX", this.masterX);
		tag.setInteger("mY", this.masterY);
		tag.setInteger("mZ", this.masterZ);
		tag.setBoolean("hM", this.hasMaster);
		tag.setBoolean("formed", this.formed);
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
		//tag.setBoolean("mode", this.input);
		//tag.setInteger("mode", TomsMathHelper.getAccess(this.mode));
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
		return 4;
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
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return true;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		return this.worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		this.stack[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}
	public ItemStack[] getStacks(){
		return this.stack;
	}
	/*@Override
	public boolean setAccess(AccessMode access) {
		boolean ret = this.mode != access;
		if(ret){
			this.mode = access;
		}
		return ret;
	}
	@Override
	public boolean setOwnerName(String name) {
		if(this.owner == ""){
			this.owner = name;
			return true;
		}else{
			return false;
		}
	}
	@Override
	public AccessMode getAccess() {
		return this.mode;
	}
	@Override
	public String getOwnerName() {
		return this.owner;
	}
	@Override
	public boolean canPlayerAccess(String name) {
		if(this.owner == name){
			return true;
		}else if(this.mode == AccessMode.PUBLIC){
			return true;
		}else if(this.mode == AccessMode.RESTRICTED){
			return TomsMathHelper.findString(this.list, name);
		}else{
			return false;
		}
	}
	public void addRESTRICTED(String name){
		if(TomsMathHelper.findString(this.list, name)) return;
		this.list.add(name);
	}
	public void removeRESTRICTED(String name){
		if(!TomsMathHelper.findString(this.list, name)) return;
		int pos = TomsMathHelper.find(this.list, name);
		this.list.remove(pos);
		this.list.sort(new Comparator<String>(){
			@Override
			public int compare(String par1, String par2) {
				return 0;
			}

		});
	}
	public List<String> getRESTRICTED(){
		return this.list;
	}
	@Override
	public void setText(int textFieldID, String text) {
		this.textFieldText = text;
	}
	@Override
	public String getText(int textFieldID) {
		return this.textFieldText;
	}
	@Override
	public void handleGUIButtonPress(int buttonID, EntityPlayer player){
		 if(buttonID == 0) {
			 this.mode = AccessMode.PUBLIC;
		 }else if(buttonID == 1) {
			 this.mode = AccessMode.PRIVATE;
		 }else if(buttonID == 2) {
			 this.mode = AccessMode.RESTRICTED;
		 }
	}//*/
	@Override
	public boolean isPlaceableOnSide() {
		return false;
	}
	@Override
	public MultiblockPartList getPartName() {
		return MultiblockPartList.ItemHatch;
	}
	@Override
	public void formI(int mX, int mY, int mZ) {

	}
	@Override
	public void deFormI(int mX, int mY, int mZ) {

	}
	//	@Override
	//	public void readFromPacket(ByteBuf buf){
	//		/*this.input = buf.readBoolean();
	//		int xCoord = pos.getX();
	//		int yCoord = pos.getY();
	//		int zCoord = pos.getZ();
	//		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);*/
	//	}
	//	@Override
	//	public void writeToPacket(ByteBuf buf){
	//		//buf.writeBoolean(this.input);
	//	}
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}
	@Override
	public void clear() {
		stack = new ItemStack[4];
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
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack is = stack[slot];
		stack[slot] = null;
		return is;
	}
	@Override
	public void setField(int arg0, int arg1) {

	}
	public boolean isInput(){
		return worldObj.getBlockState(pos).getValue(MultiblockHatch.OUTPUT);
	}
}
