package com.tom.defense.tileentity;

import static com.tom.api.energy.EnergyType.FORCE;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.item.IPowerLinkCard;
import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.item.ISwitch;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.IForceDevice;
import com.tom.api.tileentity.IForcePowerStation;
import com.tom.api.tileentity.IGuiTile;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.defense.DefenseInit;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.block.ForceCapacitor;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.lib.Configs;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityForceCapacitor extends TileEntityTomsMod implements
IPeripheral, ISidedInventory, IEnergyHandler, IForcePowerStation, IGuiTile {
	private static final int DEFAULT_RANGE = 2;
	private static final int RANGE_UPGRADE_INCREASE = 2;
	private EnergyStorage energy = new EnergyStorage(10000000,100000);
	public int clientPer = 0;
	public int linkedDevices = -1;
	public int range = DEFAULT_RANGE;
	private ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	private List<IForceDevice> deviceList = new ArrayList<IForceDevice>();
	public boolean active = false;
	public ForceDeviceControlType rsMode = ForceDeviceControlType.LOW_REDSTONE;
	public int clientEnergy = 0;
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == FORCE && from == this.getFacing(worldObj.getBlockState(pos)).rotateY();
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return FORCE.getList();
	}
	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public String getType() {
		return "force_capacitor";
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public String[] getMethodNames() {
		return new String[]{"getEnergyStored","getMaxEnergyStored"};
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
	InterruptedException {
		if(method == 0){
			return new Object[]{energy .getEnergyStored()};
		}else if(method == 1){
			return new Object[]{energy.getMaxEnergyStored()};
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public void attach(IComputerAccess computer) {

	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public void detach(IComputerAccess computer) {

	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public boolean equals(IPeripheral other) {
		return other == this;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type,
			double maxReceive, boolean simulate) {
		return this.active && this.canConnectEnergy(from, type) ? energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type,
			double maxExtract, boolean simulate) {
		return this.active && this.canConnectEnergy(from, type) ? energy.extractEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getMaxEnergyStored();
	}
	public EnumFacing getFacing(IBlockState state){
		return state.getBlock() == DefenseInit.forceCapacitor ? state.getValue(ForceCapacitor.FACING) : EnumFacing.NORTH;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<stack.length;i++){
			if(stack[i] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[i].writeToNBT(tag);
				tag.setByte("Slot", (byte) i);
				list.appendTag(tag);
			}
		}
		compound.setTag("inventory", list);
		compound.setInteger("redstone_mode", rsMode.ordinal());
		compound.setBoolean("active", active);
		return compound;
	}
	public void writeToStackNBT(NBTTagCompound compound) {
		energy.writeToNBT(compound);
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<stack.length;i++){
			if(stack[i] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[i].writeToNBT(tag);
				tag.setByte("Slot", (byte) i);
				list.appendTag(tag);
			}
		}
		compound.setTag("inventory", list);
		compound.setInteger("redstone_mode", rsMode.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
		stack = new ItemStack[this.getSizeInventory()];
		NBTTagList list = compound.getTagList("inventory", 10);
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < this.stack.length)
			{
				this.stack[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		rsMode = ForceDeviceControlType.get(compound.getInteger("redstone_mode"));
		this.active = compound.getBoolean("active");
	}
	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setInteger("c", clientPer);
		buf.setInteger("d", this.deviceList.size());
		buf.setInteger("r", range);
	}
	@Override
	public void readFromPacket(NBTTagCompound buf) {
		this.clientPer = buf.getInteger("c");
		this.linkedDevices = buf.getInteger("d");
		this.range = buf.getInteger("r");
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}
	@Override
	public void updateEntity(IBlockState currentState) {
		if(!worldObj.isRemote){
			if(rsMode == ForceDeviceControlType.HIGH_REDSTONE){
				this.active = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
			}else if(rsMode == ForceDeviceControlType.LOW_REDSTONE){
				this.active = worldObj.isBlockIndirectlyGettingPowered(pos) == 0;
			}else if(rsMode == ForceDeviceControlType.IGNORE){
				this.active = true;
			}
			this.clientEnergy = MathHelper.floor_double(this.energy.getEnergyStored());
			TomsModUtils.setBlockStateWithCondition(worldObj, pos, currentState, ForceCapacitor.ACTIVE, this.active);
			if(this.active){
				for(int i = 0;i<deviceList.size();i++){
					IForceDevice d = deviceList.get(i);
					if(d != null){
						if(d.isValid(pos)){
							if(MathHelper.sqrt_double(d.getPos2().distanceSq(pos)) <= range){
								double receive = d.receiveEnergy(Math.min(10000,energy.getEnergyStored()), true);
								if(receive > 0){
									d.receiveEnergy(energy.extractEnergy(receive, false), false);
								}
							}
						}else{
							deviceList.remove(d);
						}
					}
				}
			}
			int per = MathHelper.floor_double(energy.getEnergyStored() / energy.getMaxEnergyStored() * 1000);
			boolean update = false;
			if(per != clientPer){
				clientPer = per;
				update = true;
			}
			if(linkedDevices != deviceList.size()){
				linkedDevices = deviceList.size();
				update = true;
			}
			if(stack[1] != null && stack[1].getItem() == DefenseInit.rangeUpgrade){
				int r = stack[1].stackSize * RANGE_UPGRADE_INCREASE + DEFAULT_RANGE;
				if(r != range){
					range = r;
					update = true;
				}
			}else{
				if(range != DEFAULT_RANGE){
					range = DEFAULT_RANGE;
					update = true;
				}
			}
			if(update)markBlockForUpdate(pos);
		}
	}

	@Override
	public String getName() {
		return "forceCapacitor";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return stack[index];
	}

	@Override
	public ItemStack decrStackSize(int slot, int par2) {
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
	public ItemStack removeStackFromSlot(int index) {
		ItemStack is = stack[index];
		stack[index] = null;
		return is;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.stack[index] = stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUseable(pos, player, worldObj, this);
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return id == 0 ? this.linkedDevices : id == 1 ? this.range : id == 2 ? this.clientEnergy : 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)this.linkedDevices = value;
		else if(id == 1)this.range = value;
		else if(id == 2)this.clientEnergy = value;
	}

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public void clear() {
		stack = new ItemStack[this.getSizeInventory()];
	}

	public BlockPos getSecurityStationPos() {
		return stack[0] != null && stack[0].getItem() instanceof ISecurityStationLinkCard ? ((ISecurityStationLinkCard)stack[0].getItem()).getStation(stack[0]) : null;
	}

	@Override
	public double pullEnergy(BlockPos from, double amount, boolean simulate) {
		if(MathHelper.sqrt_double(from.distanceSq(pos)) <= range){
			return energy.extractEnergy(amount, simulate);
		}
		return 0;
	}

	@Override
	public void registerDevice(IForceDevice device) {
		if(MathHelper.sqrt_double(device.getPos2().distanceSq(pos)) <= range && !deviceList.contains(device)){
			deviceList.add(device);
		}
	}
	public boolean onBlockActivated(EntityPlayer player, ItemStack held){
		if(!worldObj.isRemote){
			if(held != null && held.getItem() instanceof ISwitch && ((ISwitch)held.getItem()).isSwitch(held, player)){
				if(rsMode == ForceDeviceControlType.SWITCH){
					boolean canAccess = true;
					BlockPos securityStationPos = this.getSecurityStationPos();
					if(securityStationPos != null){
						TileEntity tileentity = worldObj.getTileEntity(securityStationPos);
						if(tileentity instanceof ISecurityStation){
							ISecurityStation tile = (ISecurityStation) tileentity;
							canAccess = tile.canPlayerAccess(AccessType.SWITCH_DEVICES, player);
						}
					}
					if(canAccess){
						this.active = !this.active;
						return true;
					}else{
						TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
						return false;
					}
				}else{
					TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.mnotSwitchable", new TextComponentTranslation(held.getUnlocalizedName()+".name"));
					return false;
				}
			}else if(held != null && held.getItem() instanceof IPowerLinkCard && ((IPowerLinkCard)held.getItem()).isEmpty(held)){
				boolean canAccess = true;
				BlockPos securityStationPos = this.getSecurityStationPos();
				if(securityStationPos != null){
					TileEntity tileentity = worldObj.getTileEntity(securityStationPos);
					if(tileentity instanceof ISecurityStation){
						ISecurityStation tile = (ISecurityStation) tileentity;
						canAccess = tile.canPlayerAccess(AccessType.CONFIGURATION, player);
					}
				}
				if(canAccess){
					((IPowerLinkCard)held.getItem()).setMaster(held, pos);
					return true;
				}else{
					TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
					return false;
				}
			}else{
				boolean canAccess = true;
				BlockPos securityStationPos = this.getSecurityStationPos();
				if(securityStationPos != null){
					TileEntity tileentity = worldObj.getTileEntity(securityStationPos);
					if(tileentity instanceof ISecurityStation){
						ISecurityStation tile = (ISecurityStation) tileentity;
						canAccess = tile.canPlayerAccess(AccessType.CONFIGURATION, player);
					}
				}
				if(canAccess){
					player.openGui(CoreInit.modInstance, GuiIDs.forceCapacitor.ordinal(), worldObj, pos.getX(),pos.getY(),pos.getZ());
				}else{
					TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
					return false;
				}
				return true;
			}
		}else{
			return true;
		}
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if(id == 0){
			rsMode = ForceDeviceControlType.get(extra);
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}
}
