package com.tom.energy.tileentity;

import static com.tom.lib.api.energy.EnergyType.LV;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.block.IItemTile;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.IEnergyProvider;
import com.tom.util.TomsModUtils;

import com.tom.energy.block.GeoGenerator;

public class TileEntityGeoGenerator extends TileEntityTomsMod implements ITileFluidHandler, IEnergyProvider, IItemTile {
	private EnergyStorage energy = new EnergyStorage(20000, 200);
	private FluidTank tank = new FluidTank(10000);

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == LV;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LV.getList();
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {
		return canConnectEnergy(from, type) ? energy.extractEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return canConnectEnergy(from, type) ? energy.getEnergyStored() : 0;
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return canConnectEnergy(from, type) ? energy.getMaxEnergyStored() : 0;
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return Helper.getFluidHandlerFromTank(tank, FluidRegistry.LAVA, true, false);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
		if (compound.hasKey("tankS"))
			tank.readFromNBT(compound.getCompoundTag("tankS"));
		else
			tank.readFromNBT(compound.getCompoundTag("tank"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		return compound;
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote) {
			if (tank.getFluidAmount() > 0 && !energy.isFull()) {
				tank.drainInternal(1, true);
				energy.receiveEnergy(20, false);
				TomsModUtils.setBlockStateWithCondition(world, pos, state, GeoGenerator.ACTIVE, true);
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, state, GeoGenerator.ACTIVE, false);
			}
			if (this.energy.getEnergyStored() > 0) {
				for (EnumFacing f : EnumFacing.VALUES) {
					// TileEntity receiver =
					// worldObj.getTileEntity(pos.offset(f));
					// if(receiver instanceof IEnergyReceiver) {
					// System.out.println("send");
					EnumFacing fOut = f.getOpposite();
					// IEnergyReceiver recv = (IEnergyReceiver)receiver;
					LV.pushEnergyTo(world, pos, fOut, energy, false);
					// }
				}
			}
		}
	}

	public void writeToStackNBT(NBTTagCompound tag) {
		energy.writeToNBT(tag);
		tag.setTag("tankS", tank.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack s = new ItemStack(state.getBlock());
		s.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = new NBTTagCompound();
		writeToStackNBT(tag);
		s.getTagCompound().setTag("BlockEntityTag", tag);
		s.getTagCompound().setBoolean("stored", true);
		drops.add(s);
	}
}