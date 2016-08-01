package com.tom.factory.tileentity;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityMultiblockPartBase;
import com.tom.apis.TomsModUtils;
import com.tom.factory.block.MultiblockFluidHatch;
import com.tom.lib.Configs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class TileEntityMBFluidPort extends TileEntityMultiblockPartBase implements ITileFluidHandler, IInventory{
	private final FluidTank tank = new FluidTank(Configs.BASIC_TANK_SIZE);
	private int masterX = 0;
	private int masterY = 0;
	private int masterZ = 0;
	private boolean formed = false;
	private boolean hasMaster = false;
	private ItemStack[] stack = new ItemStack[2];
	//private boolean input = false;
	//	@Override
	//	public boolean canDrain(EnumFacing arg0, Fluid fluid) {
	//		return !this.isInput() && this.tank.getFluid() != null && this.tank.getFluid().getFluid() == fluid;
	//	}
	//
	//	@Override
	//	public boolean canFill(EnumFacing from, Fluid fluid) {
	//		return this.isInput() && !(this.tank.getCapacity() == this.tank.getFluidAmount()) && (tank.getFluid() == null || (tank.getFluid().getFluid() == fluid));
	//	}
	//
	//	@Override
	//	public FluidStack drain(EnumFacing from, FluidStack fluidStack, boolean doDrain) {
	//		if(this.canDrain(from, fluidStack != null ? fluidStack.getFluid() : null)){
	//			return this.tank.drain(fluidStack.amount, doDrain);
	//		}else{
	//			return null;
	//		}
	//	}
	//
	//	@Override
	//	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
	//		if(!this.isInput() && this.tank.getFluid() != null){
	//			return this.tank.drain(maxDrain, doDrain);
	//		}else{
	//			return null;
	//		}
	//	}
	//
	//	@Override
	//	public int fill(EnumFacing from, FluidStack fluid, boolean doFill) {
	//		return this.canFill(from, fluid != null ? fluid.getFluid() : null) ? this.tank.fill(fluid, doFill) : 0;
	//	}
	//
	//	@Override
	//	public FluidTankInfo[] getTankInfo(EnumFacing arg0) {
	//		return new FluidTankInfo[]{new FluidTankInfo(this.tank)};
	//	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.masterX = tag.getInteger("mX");
		this.masterY = tag.getInteger("mY");
		this.masterZ = tag.getInteger("mZ");
		this.hasMaster = tag.getBoolean("hM");
		this.formed = tag.getBoolean("formed");
		//this.input = tag.getBoolean("mode");
		this.tank.readFromNBT(tag.getCompoundTag("tank"));
		this.stack = new ItemStack[2];
		NBTTagList list = tag.getTagList("inventory", 10);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound t = list.getCompoundTagAt(i);
			int index = t.getByte("index");
			if(index >= 0 && index < this.stack.length) {
				this.stack[index] = ItemStack.loadItemStackFromNBT(t);
			}
		}
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
		//tag.setBoolean("mode", this.input);
		NBTTagCompound tankTag = new NBTTagCompound();
		this.tank.writeToNBT(tankTag);
		tag.setTag("tank",tankTag);
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<this.stack.length;i++){
			ItemStack stack = this.stack[i];
			if(stack != null) {
				NBTTagCompound t = new NBTTagCompound();
				stack.writeToNBT(t);
				t.setByte("index", (byte)i);
				list.appendTag(t);
			}
		}
		tag.setTag("inventory", list);
		//tag.setInteger("mode", TomsMathHelper.getAccess(this.mode));
		return tag;
	}

	@Override
	public ItemStack decrStackSize(int slot, int par2) {
		//if(slot == 5) return null;
		if (this.stack[slot] != null) {
			ItemStack itemstack;
			if (this.stack[slot].stackSize <= par2) {
				itemstack = this.stack[slot];
				this.stack[slot] = null;
				return itemstack;
			} else {
				itemstack = this.stack[slot].splitStack(par2);

				if (this.stack[slot].stackSize == 0) {
					this.stack[slot] = null;
				}
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getName() {
		return "Multiblock Fluid Tank";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public int getSizeInventory() {
		return 2;
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
		//		return slot == 0 && item != null && item.getItem() instanceof IFluidContainerItem;
		return false;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		return TomsModUtils.isUseable(xCoord, yCoord, zCoord, player, worldObj, this);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		this.stack[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}
	public int getFluidAmmount(){
		return this.tank.getFluidAmount();
	}
	public boolean isInput(){
		return worldObj != null ? !worldObj.getBlockState(pos).getValue(MultiblockFluidHatch.OUTPUT) : false;
	}
	/*public void setMode(boolean mode){
		this.input = mode;
		this.markDirty();
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}*/
	@Override
	public void updateEntity(){
		//		if(this.stack[0] != null && this.stack[0].getItem() instanceof ItemFluidContainer){
		//			ItemFluidContainer item = (ItemFluidContainer) this.stack[0].getItem();
		//			FluidStack fluid = item.getFluid(this.stack[0]);
		//			ItemStack inStack = this.stack[0];
		//			if(fluid != null){
		//				if(this.tank.getFluid() != null && fluid.isFluidEqual(this.tank.getFluid())){
		//					this.tank.fill(item.drain(this.stack[0], Math.min(item.getFluid(this.stack[0]).amount,this.tank.getCapacity() - this.tank.getFluidAmount()), true), true);
		//				}else{
		//					this.tank.fill(item.drain(this.stack[0], Math.min(item.getFluid(this.stack[0]).amount,this.tank.getCapacity()), true), true);
		//				}
		//			}else if(fluid == null && (this.stack[1] == null || this.stack[1].isItemEqual(this.stack[0]))){
		//				int inStackA = inStack.stackSize;
		//				if(this.stack[1] == null){
		//					this.stack[1] = new ItemStack(inStack.getItem(), inStackA);
		//				}else{
		//					this.stack[1].stackSize = this.stack[1].stackSize + inStackA;
		//				}
		//				this.stack[0].stackSize = this.stack[0].stackSize - inStackA;
		//				if(this.stack[0].stackSize == 0) this.stack[0] = null;
		//			}
		//		}
	}

	@Override
	public boolean isPlaceableOnSide() {
		return false;
	}

	@Override
	public MultiblockPartList getPartName() {
		return MultiblockPartList.FluidPort;
	}

	@Override
	public void formI(int mX, int mY, int mZ) {

	}

	@Override
	public void deFormI(int mX, int mY, int mZ) {

	}
	public void drain(int amount){
		this.tank.drain(amount, true);
	}
	public void fill(int amount){
		this.tank.fill(new FluidStack(this.tank.getFluid().getFluid(), amount), true);
	}
	public void fill(int ammount, Fluid fluid){
		this.tank.fill(new FluidStack(fluid,ammount), true);
	}
	public Fluid getFluid(){
		return this.tank.getFluid() != null ? this.tank.getFluid().getFluid() : null;
	}
	public FluidStack getFluidStack(){
		return this.tank.getFluid();
	}
	public void setFluidStack(FluidStack stack){
		this.tank.setFluid(stack);
	}
	//	@Override
	//	public void writeToPacket(ByteBuf buf){
	//		//buf.writeBoolean(this.input);
	//	}
	//	@Override
	//	public void readFromPacket(ByteBuf buf){
	//		/*this.input = buf.readBoolean();
	//		int xCoord = pos.getX();
	//		int yCoord = pos.getY();
	//		int zCoord = pos.getZ();
	//		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);*/
	//	}
	public int getMaxFluidAmount(){
		return this.tank.getCapacity();
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public void clear() {
		this.stack = new ItemStack[2];
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
	public net.minecraftforge.fluids.capability.IFluidHandler getTankOnSide(EnumFacing f) {
		return tank;
	}
}
