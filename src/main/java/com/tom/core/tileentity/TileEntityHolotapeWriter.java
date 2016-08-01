package com.tom.core.tileentity;

import static com.tom.api.energy.EnergyType.LV;

import java.util.ArrayList;
import java.util.List;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.block.HolotapeWriter;
import com.tom.lib.Configs;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Optional;
@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityHolotapeWriter extends TileEntityTomsMod implements
IPeripheral, IEnergyReceiver, ISidedInventory {

	protected EnergyStorage storage = new EnergyStorage(100000,1000);
	private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	public ItemStack holotape = null;
	private double i = 0;
	public boolean isWriting = false;
	private double iMax = 0;
	public boolean isValidH = false;
	public boolean hasH = false;
	public int direction = 0;
	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		storage.readFromNBT(tag);
		this.holotape = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("holotape"));
		this.i = tag.getDouble("i");
		this.iMax = tag.getDouble("iMax");
		this.isWriting = tag.getBoolean("isWriting");
		this.direction = tag.getInteger("direction");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);
		storage.writeToNBT(tag);
		if(this.holotape != null){
			tag.setTag("holotape", this.holotape.writeToNBT(new NBTTagCompound()));
		}else{
			tag.setTag("holotape", new NBTTagCompound());
		}
		tag.setDouble("i", i);
		tag.setDouble("iMax", iMax);
		tag.setBoolean("isWriting",this.isWriting);
		tag.setInteger("direction", this.direction);
		return tag;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {

		return type == LV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {

		return this.canConnectEnergy(from, type) ? storage.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {

		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {

		return storage.getMaxEnergyStored();
	}

	@Override
	public String getType() {
		return "tm_holotape_writer";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"write","isWritable","getWritingProgress"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] a) throws LuaException,
	InterruptedException {
		if(method == 0){
			if(holotape != null){
				if(holotape.getTagCompound() == null) holotape.setTagCompound(new NBTTagCompound());
				if(a.length > 1){
					if((holotape.getTagCompound().hasKey("w") && !holotape.getTagCompound().getBoolean("w")) || !holotape.getTagCompound().hasKey("w")){
						String data = a[1].toString();
						this.holotape.getTagCompound().setString("name", a[0].toString());
						this.holotape.getTagCompound().setString("data", data);
						this.holotape.getTagCompound().setBoolean("w", true);
						int l = data.length();
						this.i = l;
						this.isWriting = true;
						this.iMax = l;
						markBlockForUpdate(pos);
					}else{
						throw new LuaException("This holotape has already written!");
					}
				}else{
					throw new LuaException("Invalid arguments, excepted (String name, String data)");
				}
			}else{
				throw new LuaException("No Holotape in the machine!");
			}
		}else if(method == 1){
			return new Object[]{this.isValidHolotape()};
		}else if(method == 2){
			if(this.isWriting){
				double p = Math.floor(((this.iMax - this.i)/this.iMax) * 1000) / 10D;
				return new Object[]{true,p};
			}else{
				return new Object[]{false,0};
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
	public void updateEntity(){
		if(!worldObj.isRemote){
			if(i > 0 && this.isWriting){
				double energy = Config.holotapeSpeed * 2;
				double extract = this.storage.extractEnergy(energy, true);
				if(extract == energy){
					i = i - Config.holotapeSpeed;
					this.storage.extractEnergy(energy, false);
				}
			}else if(i != 0 && this.isWriting){
				i = 0;
			}else if(i == 0 && this.isWriting){
				for(IComputerAccess c : this.computers){
					c.queueEvent("holotape_done", new Object[]{c.getAttachmentName(),this.holotape.getTagCompound().getString("name")});
				}
				this.isWriting = false;
				markBlockForUpdate(pos);
			}
			this.hasH = this.holotape != null;
			this.isValidH = this.isValidHolotape();
			if(this.hasH){
				if(this.isWriting){
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, HolotapeWriter.STATE, 3);
				}
				else{
					if(this.isValidH){
						TomsModUtils.setBlockStateWithCondition(worldObj, pos, HolotapeWriter.STATE, 2);
					}else{
						TomsModUtils.setBlockStateWithCondition(worldObj, pos, HolotapeWriter.STATE, 1);
					}
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, HolotapeWriter.STATE, 0);
			}
		}
	}
	/*@Override
	public void writeToPacket(NBTTagCompound buf){
		buf.setBoolean(this.holotape != null);
		buf.setBoolean(this.isValidHolotape());
		buf.setBoolean(isWriting);
		buf.writeInt(direction);
	}*/

	public boolean isValidHolotape() {
		if(holotape != null){
			if(holotape.getTagCompound() == null) holotape.setTagCompound(new NBTTagCompound());
			return ((holotape.getTagCompound().hasKey("w") && !holotape.getTagCompound().getBoolean("w")) || !holotape.getTagCompound().hasKey("w"));
		}else{
			return false;
		}
	}

	/*@Override
	public void readFromPacket(ByteBuf buf){
		this.hasH = buf.readBoolean();
		this.isValidH = buf.readBoolean();
		this.isWriting = buf.readBoolean();
		this.direction = buf.readInt();
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}*/

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
	}*/

	@Override
	public boolean isItemValidForSlot(int s, ItemStack is) {
		return s == 0 && is != null && is.getItem() == CoreInit.holotape;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void setInventorySlotContents(int s, ItemStack is) {
		if(s == 0){
			this.holotape = is;
		}
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
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean canExtractItem(int arg0, ItemStack arg1, EnumFacing arg2) {
		return !this.isWriting;
	}

	@Override
	public boolean canInsertItem(int arg0, ItemStack arg1, EnumFacing arg2) {
		return true;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing arg0) {
		return new int[]{0};
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LV.getList();
	}
}
