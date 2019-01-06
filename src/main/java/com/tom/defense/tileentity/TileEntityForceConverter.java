package com.tom.defense.tileentity;

import static com.tom.lib.api.energy.EnergyType.*;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.item.IPowerLinkCard;
import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.item.ISwitch;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IForcePowerStation;
import com.tom.api.tileentity.ISecuredTileEntity;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.config.ConfigurationForceConverterControl;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.block.ForceConverter;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.IEnergyHandler;
import com.tom.lib.api.energy.IEnergyStorage;
import com.tom.util.TomsModUtils;

public class TileEntityForceConverter extends TileEntityTomsMod implements IEnergyHandler, IConfigurable, ISecuredTileEntity {
	public EnergyStorage energy = new EnergyStorage(10000);
	public boolean active = false;
	public ForceDeviceControlType rsMode = ForceDeviceControlType.LOW_REDSTONE;
	public InventoryBasic inv = new InventoryBasic("", false, 2) {
		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return index == 0 ? !stack.isEmpty() && stack.getItem() instanceof ISecurityStationLinkCard && ((ISecurityStationLinkCard) stack.getItem()).getStation(stack) != null : index == 1 ? !stack.isEmpty() && stack.getItem() instanceof IPowerLinkCard && ((IPowerLinkCard) stack.getItem()).getMaster(stack) != null : false;
		}
	};
	private IConfigurationOption cfgOption = new ConfigurationForceConverterControl(this);

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return (type == FORCE && from == this.getFacing(world.getBlockState(pos)).rotateYCCW()) || (type == HV && from == this.getFacing(world.getBlockState(pos)).rotateY());
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return FORCE.getList(HV);
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		if (this.active && type == HV) { return HV.convertFrom(FORCE, energy.receiveEnergy(FORCE.convertFrom(HV, maxReceive), simulate)); }
		return 0;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {
		if (type == FORCE) { return energy.extractEnergy(maxExtract, simulate); }
		return 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return type == FORCE ? energy.getEnergyStored() : 0;
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return type == FORCE ? energy.getMaxEnergyStored() : 0;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.energy.writeToNBT(tag);
		tag.setInteger("redstone_mode", rsMode.ordinal());
		tag.setBoolean("active", active);
		tag.setTag("Items", TomsModUtils.saveAllItems(inv));
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy.readFromNBT(tag);
		rsMode = ForceDeviceControlType.get(tag.getInteger("redstone_mode"));
		this.active = tag.getBoolean("active");
		TomsModUtils.loadAllItems(tag.getTagList("Items", 10), inv);
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote) {
			if (rsMode == ForceDeviceControlType.HIGH_REDSTONE) {
				this.active = world.isBlockIndirectlyGettingPowered(pos) > 0;
			} else if (rsMode == ForceDeviceControlType.LOW_REDSTONE) {
				this.active = world.isBlockIndirectlyGettingPowered(pos) == 0;
			} else if (rsMode == ForceDeviceControlType.IGNORE) {
				this.active = true;
			}
			if (this.rsMode != ForceDeviceControlType.SWITCH)
				TomsModUtils.setBlockStateWithCondition(world, pos, state, ForceConverter.ACTIVE, (!this.energy.isFull() && this.energy.hasEnergy()) && this.active);
			else
				TomsModUtils.setBlockStateWithCondition(world, pos, state, ForceConverter.ACTIVE, this.active);
			BlockPos pos = this.getCapacitorPos();
			if (pos != null) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile instanceof IForcePowerStation) {
					IForcePowerStation te = (IForcePowerStation) tile;
					IEnergyStorage energy = te.getEnergyHandler(this.pos);
					if (!energy.isDummy()) {
						EnergyStorage.transfer(this.energy, energy, 10000, false);
					}
				}
			}
			/*if(this.active && this.energy.hasEnergy()){
				EnumFacing f = this.getFacing(state).rotateYCCW();
				//for(EnumFacing f : EnumFacing.VALUES){
				//	TileEntity receiver = worldObj.getTileEntity(pos.offset(f));
				//if(receiver instanceof IEnergyReceiver) {
				//System.out.println("send");
				EnumFacing fOut = f.getOpposite();
				//IEnergyReceiver recv = (IEnergyReceiver)receiver;
				FORCE.pushEnergyTo(world, pos, fOut, energy, false);
				//}
				//}
			}*/
		}
	}

	public BlockPos getCapacitorPos() {
		return !inv.getStackInSlot(1).isEmpty() && inv.getStackInSlot(1).getItem() instanceof IPowerLinkCard ? ((IPowerLinkCard) inv.getStackInSlot(1).getItem()).getMaster(inv.getStackInSlot(1)) : null;
	}

	public EnumFacing getFacing(IBlockState state) {
		return state.getValue(ForceConverter.FACING);
	}

	public void onBlockActivated(EntityPlayer player, ItemStack held) {
		if (!world.isRemote) {
			if (held != null && held.getItem() instanceof ISwitch && ((ISwitch) held.getItem()).isSwitch(held, player)) {
				if (rsMode == ForceDeviceControlType.SWITCH) {
					boolean canAccess = true;
					BlockPos securityStationPos = this.getSecurityStationPos();
					if (securityStationPos != null) {
						TileEntity tileentity = world.getTileEntity(securityStationPos);
						if (tileentity instanceof ISecurityStation) {
							ISecurityStation tile = (ISecurityStation) tileentity;
							canAccess = tile.canPlayerAccess(AccessType.SWITCH_DEVICES, player);
						}
					}
					if (canAccess) {
						this.active = !this.active;
					} else {
						TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
					}
				} else {
					TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.mnotSwitchable", new TextComponentTranslation(held.getUnlocalizedName() + ".name"));
				}
			}
		}
	}

	@Override
	public void receiveNBTPacket(EntityPlayer pl, NBTTagCompound message) {
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
		return IConfigurable.super.getSecurityStationPos();
	}

	@Override
	public void setCardStack(ItemStack stack) {
		this.inv.setInventorySlotContents(0, stack);
	}

	@Override
	public ItemStack getCardStack() {
		return this.inv.getStackInSlot(0);
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}

	public ItemStack getPowerCardStack() {
		return this.inv.getStackInSlot(1);
	}

	public void setPowerCardStack(ItemStack stack) {
		this.inv.setInventorySlotContents(1, stack);
	}

	@Override
	public String getConfigName() {
		return getBlockType().getUnlocalizedName() + ".name";
	}
}
