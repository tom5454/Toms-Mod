package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;

import com.tom.core.block.HolotapeReader;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityHolotapeReader extends TileEntityTomsMod implements
IPeripheral, IInventory {
	private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	public ItemStack holotape = null;
	//public boolean isValidH = false;
	public boolean hasH = false;
	public int direction = 0;
	@Override
	public String getType() {
		return "tm_holotape_reader";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"isValid","read"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
	InterruptedException {
		if(method == 0){
			return new Object[]{this.isValidHolotape()};
		}else if(method == 1){
			if(holotape != null){
				if(holotape.getTagCompound() == null) holotape.setTagCompound(new NBTTagCompound());
				if(holotape.getTagCompound().hasKey("w") && holotape.getTagCompound().getBoolean("w") && holotape.getTagCompound().hasKey("data") && holotape.getTagCompound().hasKey("name")){
					String data = holotape.getTagCompound().getString("data");
					String name = holotape.getTagCompound().getString("name");
					return new Object[]{name,data};
				}else{
					throw new LuaException("This holotape hasn't written yet!");
				}
			}else{
				throw new LuaException("No Holotape in the machine!");
			}
		}
		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {
		computers.add(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		computers.remove(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other == this;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.holotape = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("holotape"));
		this.direction = tag.getInteger("direction");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(this.holotape != null){
			tag.setTag("holotape", this.holotape.writeToNBT(new NBTTagCompound()));
		}else{
			tag.setTag("holotape", new NBTTagCompound());
		}
		tag.setInteger("direction", this.direction);
		return tag;
	}
	/*@Override
	public void writeToPacket(ByteBuf buf){
		//buf.writeBoolean(this.holotape != null);
		//buf.writeBoolean(this.isValidHolotape());
		//buf.writeInt(direction);
	}

	@Override
	public void readFromPacket(ByteBuf buf){
		//this.hasH = buf.readBoolean();
		//this.isValidH = buf.readBoolean();
		//this.direction = buf.readInt();
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}*/
	public boolean isValidHolotape(){
		if(holotape != null){
			if(holotape.getTagCompound() == null) holotape.setTagCompound(new NBTTagCompound());
			return (holotape.getTagCompound().hasKey("w") && holotape.getTagCompound().getBoolean("w"));
		}else{
			return false;
		}
	}
	@Override
	public void updateEntity(){
		if(!worldObj.isRemote){
			this.hasH = this.holotape != null;
			IBlockState state = worldObj.getBlockState(pos);
			if(hasH){
				if(this.isValidHolotape()){
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, HolotapeReader.STATE, 2);
				}else{
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, HolotapeReader.STATE, 1);
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, HolotapeReader.STATE, 0);
			}
		}
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2) {
		if (par1 == 0 && this.holotape != null) {
			ItemStack itemstack;
			if (this.holotape.stackSize <= par2) {
				itemstack = this.holotape;
				this.holotape = null;
				return itemstack;
			} else {
				itemstack = this.holotape.splitStack(par2);

				if (this.holotape.stackSize == 0) {
					this.holotape = null;
				}
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getName() {
		return "Holotape Writer";
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
	public ItemStack getStackInSlot(int s) {
		return s == 0 ? this.holotape : null;
	}

	/*@Override
	public ItemStack getStackInSlotOnClosing(int s) {
		if(s == 0 && this.holotape != null){
			ItemStack is = this.holotape;
			this.holotape = null;
			return is;
		}
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}*/

	@Override
	public boolean isItemValidForSlot(int s, ItemStack is) {
		return s == 0 && is != null && is.getItem() == CoreInit.holotape;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
	}

	/*@Override
	public void openInventory() {

	}*/

	@Override
	public void setInventorySlotContents(int s, ItemStack is) {
		if(s == 0){
			this.holotape = is;
		}
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
	public void clear() {
		this.holotape = null;
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
		if(arg0 == 0){
			ItemStack ret = this.holotape;
			this.holotape = null;
			return ret;
		}
		return null;
	}

	@Override
	public void setField(int arg0, int arg1) {

	}
}
