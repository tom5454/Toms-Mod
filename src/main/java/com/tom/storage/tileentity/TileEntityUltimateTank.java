package com.tom.storage.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import com.tom.apis.TomsModUtils;

public class TileEntityUltimateTank extends TMTank {
	private final FluidTank tank = new FluidTank(1024000);
	private FluidStack stackLast = null;

	// @Override
	// public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
	// if(doFill)markBlockForUpdate(pos);
	// return tank.fill(resource, doFill);
	// }
	//
	// @Override
	// public FluidStack drain(EnumFacing from, FluidStack resource, boolean
	// doDrain) {
	// if(doDrain)markBlockForUpdate(pos);
	// return tank.getFluid() != null && tank.getFluid().isFluidEqual(resource)
	// ? tank.drain(resource.amount, doDrain): null;
	// }
	//
	// @Override
	// public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
	// if(doDrain)markBlockForUpdate(pos);
	// return tank.drain(maxDrain, doDrain);
	// }
	//
	// @Override
	// public boolean canFill(EnumFacing from, Fluid fluid) {
	// return true;
	// }
	//
	// @Override
	// public boolean canDrain(EnumFacing from, Fluid fluid) {
	// return true;
	// }
	//
	// @Override
	// public FluidTankInfo[] getTankInfo(EnumFacing from) {
	// return new FluidTankInfo[]{tank.getInfo()};
	// }
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tank.readFromNBT(compound.getCompoundTag("tank"));
	}

	@Override
	public FluidStack getStack() {
		return tank.getFluid();
	}

	@Override
	public void writeToPacket(NBTTagCompound buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tank.writeToNBT(tag);
		// ByteBufUtils.writeTag(buf, tag);
		buf.setTag("t", tag);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf) {
		NBTTagCompound tag = buf.getCompoundTag("t");
		tank.readFromNBT(tag);
	}

	@Override
	public void writeToStackNBT(ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound nbt = new NBTTagCompound();
		tank.writeToNBT(tag);
		nbt.setTag("tank", tag);
		stack.getTagCompound().setTag("BlockEntityTag", nbt);
	}

	@Override
	public void readFromStackNBT(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("BlockEntityTag");
			tank.readFromNBT(tag.getCompoundTag("tank"));
		}
	}

	@Override
	public net.minecraftforge.fluids.capability.IFluidHandler getTankOnSide(EnumFacing f) {
		return tank;
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
				fluid = fluid.copy();
			if (!TomsModUtils.areFluidStacksEqual(fluid, stackLast)) {
				markBlockForUpdate();
			}
			stackLast = fluid;
		}
	}

	@Override
	public int getComparatorValue() {
		double v = tank.getFluidAmount() / tank.getCapacity();
		return MathHelper.floor(v * 15);
	}

	@Override
	public int getCapacity() {
		return tank.getCapacity();
	}
}