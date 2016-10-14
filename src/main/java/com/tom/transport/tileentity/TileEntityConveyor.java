package com.tom.transport.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import com.tom.api.tileentity.IExtendedInventory;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.transport.block.ConveyorBelt;

public class TileEntityConveyor extends TileEntityTomsMod implements ISidedInventory {
	private ItemStack stack = null;
	public double position = 0;
	//private boolean firstStart = true;
	public EnumFacing facing = EnumFacing.DOWN;
	private boolean hasItemLast = false;
	//@SideOnly(Side.CLIENT)
	//public final ModelBelt beltModel = new ModelBelt();
	//@SideOnly(Side.CLIENT)
	//public float partialTicksLast = -1;
	public float posLast = 0;
	@Override
	public String getName() {
		return "conveyor";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation("tile.tm.conveyorBelt.name");
	}
	@Override
	public void clear() {
		this.stack = null;
	}
	@Override
	public void closeInventory(EntityPlayer arg0) {

	}
	@Override
	public ItemStack decrStackSize(int slot, int count) {
		if(slot == 0){
			if (this.stack != null)
			{
				ItemStack var3;

				if (this.stack.stackSize <= count)
				{
					var3 = this.stack;
					this.stack = null;
					return var3;
				}
				else
				{
					var3 = this.stack.splitStack(count);

					if (this.stack.stackSize == 0)
					{
						this.stack = null;
					}

					return var3;
				}
			}
			else
			{
				return null;
			}
		}
		return null;
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
	public int getInventoryStackLimit() {
		return 1;
	}
	@Override
	public int getSizeInventory() {
		return 1;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? this.stack : null;
	}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return true;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUseable(pos, player, worldObj, this);
	}
	@Override
	public void openInventory(EntityPlayer arg0) {

	}
	@Override
	public ItemStack removeStackFromSlot(int slot) {
		if(slot == 0){
			ItemStack is = this.stack;
			stack = null;
			return is;
		}
		return null;
	}
	@Override
	public void setField(int arg0, int arg1) {

	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {
		if(slot == 0){
			this.stack = is;
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("inventory"));
		this.position = compound.getDouble("itemPosition");
		this.facing = EnumFacing.VALUES[compound.getInteger("facing")];
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound t = new NBTTagCompound();
		if(this.stack != null){
			stack.writeToNBT(t);
		}
		compound.setTag("inventory", t);
		compound.setDouble("itemPosition", position);
		compound.setInteger("facing", facing.ordinal());
		return compound;
	}
	@Override
	public void readFromPacket(NBTTagCompound buf) {
		//System.out.println("reading from packet "+pos);
		this.position = buf.getDouble("p");
		this.facing = EnumFacing.getFront(buf.getByte("f"));
		this.stack = ItemStack.loadItemStackFromNBT(buf.getCompoundTag("s"));
		this.posLast = 0;
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}
	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setDouble("p", position);
		buf.setByte("f", (byte) facing.getIndex());
		//ByteBufUtils.writeItemStack(buf, stack);
		NBTTagCompound t = new NBTTagCompound();
		if(stack != null)stack.writeToNBT(t);
		buf.setTag("s", t);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			EnumFacing direction) {
		return index == 0 ? this.position == 0.0D : false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		return index == 0;
	}
	@Override
	public void updateEntity(IBlockState state) {
		if(this.stack != null){
			if(this.position < 16)this.position = this.position + this.getMovementSpeed();
			if(!worldObj.isRemote){
				if(!this.hasItemLast){
					this.markDirty();
					markBlockForUpdate(pos);
				}
				this.hasItemLast = true;
				if(this.position >= 16 && (!worldObj.isRemote)){
					this.position = 16;
					BlockPos p = pos.offset(facing);
					IInventory inv = TileEntityHopper.getInventoryAtPosition(worldObj, p.getX(), p.getY(), p.getZ());
					if(inv != null){
						this.stack = TileEntityHopper.putStackInInventoryAllSlots(inv, stack, facing.getOpposite());
						this.hasItemLast = false;
						this.markDirty();
						markBlockForUpdate(pos);
					}else{
						if(!worldObj.isBlockFullCube(p)){
							EnumFacing position = this.getPosition(state);
							p = p.offset(position.getOpposite());
							inv = TileEntityHopper.getInventoryAtPosition(worldObj, p.getX(), p.getY(), p.getZ());
							if(inv != null && inv instanceof IExtendedInventory && ((IExtendedInventory)inv).canInsertItemFrom(position, pos)){
								this.stack = TileEntityHopper.putStackInInventoryAllSlots(inv, stack, facing.getOpposite());
								this.hasItemLast = false;
								this.markDirty();
								markBlockForUpdate(pos);
							}
						}
					}
				}
			}
		}else{
			this.position = 0;
			if(!worldObj.isRemote){
				if(this.hasItemLast){
					this.markDirty();
					markBlockForUpdate(pos);
				}
				this.hasItemLast = false;
			}
		}
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}
	public EnumFacing getPosition(IBlockState state){
		return state.getValue(ConveyorBelt.POSITION);
	}
	public ItemStack getStack(){
		return stack;
	}
	public float getMovementSpeed(){
		return 0.4F;
	}
}
