package com.tom.defense.tileentity;

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

import com.tom.api.energy.EnergyStorage;
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
import com.tom.defense.ProjectorLensConfigEntry;
import com.tom.defense.ProjectorLensConfigEntry.CompiledProjectorConfig;
import com.tom.defense.block.FieldProjector;
import com.tom.handler.GuiHandler.GuiIDs;

public class TileEntityForceFieldProjector extends TileEntityTomsMod implements
IForceDevice, ISidedInventory, IGuiTile {
	public ForceDeviceControlType rsMode = ForceDeviceControlType.LOW_REDSTONE;
	private ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	private EnergyStorage energy = new EnergyStorage(1000000,100000);
	public boolean active = false;
	private boolean firstStart = true, lastActive = false;
	public int clientEnergy = 0, lastDrained = 0, offsetX = 0, offsetY = 0, offsetZ = 0;
	private CompiledProjectorConfig config;
	@Override
	public double receiveEnergy(double maxReceive, boolean simulate) {
		return energy.receiveEnergy(maxReceive, simulate);
	}
	@Override
	public boolean isValid(BlockPos from) {
		BlockPos c = this.getCapacitorPos();
		return this.hasWorldObj() && c != null && c.equals(from) && pos != null && worldObj.getBlockState(pos) != null && worldObj.getBlockState(pos).getBlock() == DefenseInit.fieldProjector;
	}
	public BlockPos getCapacitorPos(){
		return stack[1] != null && stack[1].getItem() instanceof IPowerLinkCard ? ((IPowerLinkCard)stack[1].getItem()).getMaster(stack[1]) : null;
	}
	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		if(!worldObj.isRemote){
			//player.attackEntityFrom(DamageSourceTomsMod.fieldDamage, 8F);
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
					player.openGui(CoreInit.modInstance, GuiIDs.forceFieldProjector.ordinal(), worldObj, pos.getX(),pos.getY(),pos.getZ());
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
		}else if(id == 1){
			this.offsetX = extra;
		}
		else if(id == 2){
			this.offsetY = extra;
		}
		else if(id == 3){
			this.offsetZ = extra;
		}
	}

	@Override
	public String getName() {
		return "forceFieldProjector";
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
		return 4;
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
		return id == 0 ? this.clientEnergy : id == 1 ? lastDrained : id == 2 ? offsetX : id == 3 ? offsetY : id == 4 ? offsetZ : 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)this.clientEnergy = value;
		else if(id == 1)this.lastDrained = value;
		else if(id == 2)this.offsetX = value;
		else if(id == 3)this.offsetY = value;
		else if(id == 4)this.offsetZ = value;
	}

	@Override
	public int getFieldCount() {
		return 5;
	}

	@Override
	public void clear() {
		this.stack = new ItemStack[this.getSizeInventory()];
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
		offsetX = compound.getInteger("offsetX");
		offsetY = compound.getInteger("offsetY");
		offsetZ = compound.getInteger("offsetZ");
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
		compound.setInteger("offsetX", offsetX);
		compound.setInteger("offsetY", offsetY);
		compound.setInteger("offsetZ", offsetZ);
		return compound;
	}
	public void writeToStackNBT(NBTTagCompound compound) {
		energy.writeToNBT(compound);
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<stack.length;i++){
			if(i != 3 && stack[i] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[i].writeToNBT(tag);
				tag.setByte("Slot", (byte) i);
				list.appendTag(tag);
			}
		}
		compound.setTag("inventory", list);
		compound.setInteger("redstone_mode", rsMode.ordinal());
		compound.setInteger("offsetX", offsetX);
		compound.setInteger("offsetY", offsetY);
		compound.setInteger("offsetZ", offsetZ);
	}
	public BlockPos getSecurityStationPos() {
		return stack[0] != null && stack[0].getItem() instanceof ISecurityStationLinkCard ? ((ISecurityStationLinkCard)stack[0].getItem()).getStation(stack[0]) : null;
	}
	@Override
	public void updateEntity(IBlockState currentState) {
		if(!worldObj.isRemote){
			if(this.firstStart){
				this.updateConfig();
				firstStart = false;
			}
			{
				BlockPos pos = this.getCapacitorPos();
				if(pos != null){
					TileEntity tile = worldObj.getTileEntity(pos);
					if(tile instanceof IForcePowerStation) {
						IForcePowerStation te = (IForcePowerStation) tile;
						te.registerDevice(this);
					}
				}
			}
			if(rsMode == ForceDeviceControlType.HIGH_REDSTONE){
				this.active = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
			}else if(rsMode == ForceDeviceControlType.LOW_REDSTONE){
				this.active = worldObj.isBlockIndirectlyGettingPowered(pos) == 0;
			}else if(rsMode == ForceDeviceControlType.IGNORE){
				this.active = true;
			}
			if(!lastActive && active){
				this.updateConfig();
			}
			lastActive = active;
			this.clientEnergy  = MathHelper.floor_double(this.energy.getEnergyStored());
			TomsModUtils.setBlockStateWithCondition(worldObj, pos, currentState, FieldProjector.ACTIVE, this.active && this.energy.getEnergyStored() > 0.1D);
			this.lastDrained = 0;
			if(config != null){
				if(this.active && this.energy.getEnergyStored() > 100D){
					int energyUsed = config.build(worldObj);
					double realEnergyUsed = 0.01 + (energyUsed / (100D + (stack[3] != null && stack[3].getItem() == DefenseInit.efficiencyUpgrade ? stack[3].stackSize * 50 : 0)));
					energy.extractEnergy(realEnergyUsed, false);
					this.lastDrained = MathHelper.floor_double(realEnergyUsed * 100);
				}else{
					config.destroy(worldObj);
				}
			}
		}
	}
	private List<ProjectorLensConfigEntry> getConfig(){
		List<ProjectorLensConfigEntry> entryList = new ArrayList<ProjectorLensConfigEntry>();
		if(stack[2] != null){
			if(stack[2].getTagCompound() == null)stack[2].setTagCompound(new NBTTagCompound());
			NBTTagList list = stack[2].getTagCompound().getTagList("entries", 10);
			for(int i = 0;i<list.tagCount();i++){
				NBTTagCompound tag = list.getCompoundTagAt(i);
				entryList.add(ProjectorLensConfigEntry.fromNBT(tag));
			}
		}
		return entryList;
	}
	public void breakBlock() {
		config.destroy(worldObj);
	}
	public boolean isValidFieldBlock(BlockPos pos){
		return active && config.contains(pos);
	}
	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}
	private void updateConfig(){
		if(this.config != null && worldObj != null)config.destroy(worldObj);
		this.config = CompiledProjectorConfig.compile(getConfig(), pos.offset(EnumFacing.UP).add(offsetX, offsetY, offsetZ), pos, worldObj.provider.getDimension());
	}
	@Override
	public void markDirty() {
		super.markDirty();
		this.updateConfig();
	}
	@Override
	public BlockPos getPos2() {
		return pos;
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
