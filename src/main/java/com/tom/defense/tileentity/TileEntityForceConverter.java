package com.tom.defense.tileentity;

import static com.tom.api.energy.EnergyType.FORCE;
import static com.tom.api.energy.EnergyType.HV;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.item.ISwitch;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IConfigurable.IConfigurationOption.ConfigurationRedstoneControl;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.block.ForceConverter;

public class TileEntityForceConverter extends TileEntityTomsMod implements
IEnergyHandler, IConfigurable {
	public EnergyStorage energy = new EnergyStorage(10000);
	public boolean active = false;
	public ForceDeviceControlType rsMode = ForceDeviceControlType.LOW_REDSTONE;
	private ItemStack securityCardStack = null;
	private IConfigurationOption cfgOption = new ConfigurationRedstoneControl(this, rsMode);
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return (type == FORCE && from == this.getFacing(worldObj.getBlockState(pos)).rotateYCCW()) || (type == HV &&
				from == this.getFacing(worldObj.getBlockState(pos)).rotateY());
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return FORCE.getList(HV);
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type,
			double maxReceive, boolean simulate) {
		if(this.active && type == HV){
			return HV.convertFrom(FORCE, energy.receiveEnergy(FORCE.convertFrom(HV, maxReceive), simulate));
		}
		return 0;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type,
			double maxExtract, boolean simulate) {
		if(type == FORCE){
			return energy.extractEnergy(maxExtract, simulate);
		}
		return 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return type == FORCE ? energy.getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return type == FORCE ? energy.getMaxEnergyStored() : 0;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.energy.writeToNBT(tag);
		tag.setInteger("redstone_mode", rsMode.ordinal());
		tag.setBoolean("active", active);
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy.readFromNBT(tag);
		rsMode = ForceDeviceControlType.get(tag.getInteger("redstone_mode"));
		this.active = tag.getBoolean("active");
	}
	@Override
	public void updateEntity(IBlockState state) {
		if(!worldObj.isRemote){
			if(rsMode == ForceDeviceControlType.HIGH_REDSTONE){
				this.active = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
			}else if(rsMode == ForceDeviceControlType.LOW_REDSTONE){
				this.active = worldObj.isBlockIndirectlyGettingPowered(pos) == 0;
			}else if(rsMode == ForceDeviceControlType.IGNORE){
				this.active = true;
			}
			if(this.rsMode != ForceDeviceControlType.SWITCH)TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, ForceConverter.ACTIVE, (!this.energy.isFull() && this.energy.hasEnergy()) && this.active);
			else TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, ForceConverter.ACTIVE, this.active);
			if(this.active && this.energy.hasEnergy()){
				EnumFacing f = this.getFacing(state).rotateYCCW();
				//for(EnumFacing f : EnumFacing.VALUES){
				//	TileEntity receiver = worldObj.getTileEntity(pos.offset(f));
				//if(receiver instanceof IEnergyReceiver) {
				//System.out.println("send");
				EnumFacing fOut = f.getOpposite();
				//IEnergyReceiver recv = (IEnergyReceiver)receiver;
				FORCE.pushEnergyTo(worldObj, pos, fOut, energy, false);
				//}
				//}
			}
		}
	}
	public EnumFacing getFacing(IBlockState state){
		return state.getValue(ForceConverter.FACING);
	}
	public void onBlockActivated(EntityPlayer player, ItemStack held){
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
					}else{
						TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
					}
				}else{
					TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.mnotSwitchable", new TextComponentTranslation(held.getUnlocalizedName()+".name"));
				}
			}
		}
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		this.rsMode = ForceDeviceControlType.get(message.getInteger("r"));
	}

	@Override
	public IConfigurationOption getOption() {
		return cfgOption;
	}

	@Override
	public boolean canConfigure(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void writeToNBTPacket(NBTTagCompound tag) {
		tag.setInteger("r", this.rsMode.ordinal());
	}

	@Override
	public BlockPos getSecurityStationPos() {
		return this.securityCardStack != null && securityCardStack.getItem() instanceof ISecurityStationLinkCard ? ((ISecurityStationLinkCard)securityCardStack.getItem()).getStation(securityCardStack) : null;
	}

	@Override
	public void setCardStack(ItemStack stack) {
		this.securityCardStack = stack;
	}

	@Override
	public ItemStack getCardStack() {
		return securityCardStack;
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}
}
