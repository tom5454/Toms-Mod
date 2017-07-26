package com.tom.core.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.TMResource.CraftingMaterial;

import com.tom.core.block.BlockTreeTap;

public class TileEntityTreeTap extends TileEntityTomsMod implements ITileFluidHandler {
	private ItemStack bottleStack = ItemStack.EMPTY;
	private static final FluidStack RESIN = new FluidStack(CoreInit.resin.get(), 1);
	private FluidTank tank;
	private int progress = 0;

	public TileEntityTreeTap() {
		tank = new FluidTank(1);
		tank.setFluid(RESIN);
	}

	public ItemStack getBottleStack() {
		return bottleStack;
	}

	public void setBottleStack(ItemStack bottleStack) {
		this.bottleStack = bottleStack;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound tag = new NBTTagCompound();
		if (bottleStack != null)
			bottleStack.writeToNBT(tag);
		compound.setTag("item", tag);
		compound.setInteger("progress", progress);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		bottleStack = TomsModUtils.loadItemStackFromNBT(compound.getCompoundTag("item"));
		progress = compound.getInteger("progress");
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote) {
			EnumFacing f = state.getValue(BlockTreeTap.FACING);
			BlockPos trunk = pos.offset(f.getOpposite());
			IBlockState home = world.getBlockState(trunk);
			if (home != null && home.getBlock() == CoreInit.rubberWood) {
				BlockPos loc = trunk.up();
				IBlockState s = world.getBlockState(loc);
				while (s.getBlock() == CoreInit.rubberWood) {
					loc = loc.up();
					s = world.getBlockState(loc);
				}
				if (s.getBlock() == CoreInit.rubberLeaves && world.getBlockState(loc.up()).getBlock() == CoreInit.rubberLeaves) {
					if (!bottleStack.isEmpty()) {
						if (bottleStack.getItem() == Items.GLASS_BOTTLE) {
							progress++;
							if (progress >= 1000) {
								int a = bottleStack.getCount();
								bottleStack = CraftingMaterial.BOTTLE_OF_RESIN.getStackNormal(a);
								progress = 0;
								TomsModUtils.setBlockStateWithCondition(world, pos, state, BlockTreeTap.STATE, 2);
							} else
								TomsModUtils.setBlockStateWithCondition(world, pos, state, BlockTreeTap.STATE, 1);
						} else if (bottleStack.isItemEqual(CraftingMaterial.BOTTLE_OF_RESIN.getStackNormal())) {
							TomsModUtils.setBlockStateWithCondition(world, pos, state, BlockTreeTap.STATE, 2);
							progress = 0;
						} else {
							InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, bottleStack);
							bottleStack = ItemStack.EMPTY;
							progress = 0;
							TomsModUtils.setBlockStateWithCondition(world, pos, state, BlockTreeTap.STATE, 0);
						}
					} else {
						progress = 0;
						TomsModUtils.setBlockStateWithCondition(world, pos, state, BlockTreeTap.STATE, 0);
						TileEntity te = world.getTileEntity(pos.down());
						if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP)) {
							IFluidHandler t = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
							if (t != null) {
								int filled = t.fill(RESIN, false);
								if (filled > 0) {
									t.fill(RESIN, true);
								}
							}
						}
					}
				}
			} else {
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(CoreInit.treeTap));
				world.setBlockToAir(pos);
			}
		}
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return f == EnumFacing.DOWN ? Helper.getFluidHandlerFromTank(tank, false, false, CoreInit.resin) : null;
	}

	@Override
	public boolean canHaveFluidHandler(EnumFacing f) {
		return f == null || f == EnumFacing.DOWN;
	}
}
