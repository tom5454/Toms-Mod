package com.tom.storage.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import com.tom.api.ITileFluidHandler;
import com.tom.api.block.IItemTile;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.util.TomsModUtils;

public abstract class TMTank extends TileEntityTomsMod implements ITileFluidHandler, IItemTile {
	protected final FluidTank tank;
	protected FluidStack stackLast = null;
	public TMTank(int capacity) {
		tank = new FluidTank(capacity);
	}
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

	public FluidStack getStack() {
		return tank.getFluid();
	}
	@Override
	public void writeToPacket(NBTTagCompound buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tank.writeToNBT(tag);
		buf.setTag("t", tag);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf) {
		NBTTagCompound tag = buf.getCompoundTag("t");
		tank.readFromNBT(tag);
	}

	public void writeToStackNBT(ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound nbt = new NBTTagCompound();
		tank.writeToNBT(tag);
		nbt.setTag("tank", tag);
		stack.getTagCompound().setTag("BlockEntityTag", nbt);
	}

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

	public int getComparatorValue() {
		double v = tank.getFluidAmount() / tank.getCapacity();
		return MathHelper.floor(v * 15);
	}

	public int getCapacity() {
		return tank.getCapacity();
	}
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack stack = new ItemStack(state.getBlock());
		writeToStackNBT(stack);
		drops.add(stack);
	}
}
