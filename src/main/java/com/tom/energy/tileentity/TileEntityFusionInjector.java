package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.Configs;
import com.tom.util.TomsModUtils;

import com.tom.energy.block.FusionInjector;

public class TileEntityFusionInjector extends TileEntityTomsMod implements IEnergyReceiver, ICustomMultimeterInformation {

	private EnergyStorage energy = new EnergyStorage(Configs.InjectorMaxEnergy, Configs.InjectorMaxEnergyInput);

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return from == Configs.InjectorPort && type == HV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return this.canConnectEnergy(from, type) ? energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {

		return this.energy.getEnergyStored();
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getMaxEnergyStored();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energy.readFromNBT(tag.getCompoundTag("Energy"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("Energy", energy.writeToNBT(new NBTTagCompound()));
		return tag;
	}

	public boolean ready(boolean force) {
		return force ? this.energy.getEnergyStored() >= this.energy.getMaxEnergyStored() - Configs.InjectorUsage - 1 : (this.energy.getEnergyStored() - Configs.InjectorUsage) >= 0;
	}

	public void disCharge(boolean force) {
		if (force) {
			this.energy.setEnergyStored(1000);
		} else {
			this.energy.extractEnergy(Configs.InjectorUsage, false);
		}
		// this.energy = force ? 0 : this.energy - Configs.InjectorUsage;
	}

	@Override
	public void updateEntity() {
		/*this.i = this.i + 1;
		if(this.i > Configs.textureUpdateRate){
			this.i = 0;
			int xCoord = pos.getX();
			int yCoord = pos.getY();
			int zCoord = pos.getZ();
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}*/
		if (!this.world.isRemote) {
			IBlockState state = this.world.getBlockState(pos);
			if (this.ready(true)) {
				if (!state.getValue(FusionInjector.READY)) {
					TomsModUtils.setBlockState(world, pos, state.withProperty(FusionInjector.READY, true));
				}
			} else {
				if (state.getValue(FusionInjector.READY)) {
					TomsModUtils.setBlockState(world, pos, state.withProperty(FusionInjector.READY, false));
				}
			}
		}
	}

	public double getEnergyStored() {
		return this.energy.getEnergyStored();
	}

	public double getMaxEnergyStored() {
		return this.energy.getEnergyStored();
	}

	public void disCharge(int a) {
		this.energy.extractEnergy(a, false);
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}

	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		list.add(new TextComponentTranslation("tomsMod.chat.ready", new TextComponentTranslation("tomsMod.chat." + (this.ready(true) ? "yes" : "no")).setStyle(new Style().setColor(this.ready(true) ? TextFormatting.GREEN : TextFormatting.RED))));
		return list;
	}

}
