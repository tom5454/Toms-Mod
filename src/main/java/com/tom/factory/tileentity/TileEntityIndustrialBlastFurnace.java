package com.tom.factory.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import com.tom.core.CoreInit;
import com.tom.factory.block.BlockBlastFurnace;
import com.tom.factory.block.IndustrialBlastFurnace;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;
import com.tom.util.TomsModUtils;

public class TileEntityIndustrialBlastFurnace extends TileEntityMachineBase {
	/** {{max heat, lava heat, air heat}, {casing heat values...}} */
	private static final int[][][] MAX_HEAT = new int[][][]{{{4000, 260, 0}, {-10000000, 32, 51, 101}}, {{3000, 250, -5}, {-10000000, 30, 48, 90}}, {{1500, 240, -10}, {-10000000, 27, 45, 48}}};
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	public int clientEnergy;
	public int maxProgress = 0;
	private int heat;

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 || index == 1;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 2;
	}

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public String getName() {
		return "industrialBlastFurnace";
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return -1;
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return 2;
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote) {
			clientEnergy = MathHelper.floor(energy.getEnergyStored());
			heat = checkIfMerged(state);
			if (energy.getEnergyStored() > 20 && heat > 0 && canRun()) {
				super.updateEntity(state);
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, IndustrialBlastFurnace.ACTIVE, false);
			}
		}
	}

	public int checkIfMerged(IBlockState state) {
		EnumFacing facing = state.getValue(BlockBlastFurnace.FACING).getOpposite();
		BlockPos center = pos.offset(facing, 2);
		int isValid = check3x3(center.up(3)) + check3x3(center);
		isValid = isValid + checkMid(center.up()) + checkMid(center.up(2));
		return Math.min(isValid, MAX_HEAT[getType()][0][0]);
	}

	private int check3x3(BlockPos center) {
		int ret = isWall(center);
		if (ret > 0) {
			for (EnumFacing f : EnumFacing.HORIZONTALS) {
				int wall1 = isWall(center.offset(f));
				int wall2 = isWall(center.offset(f).offset(f.rotateY()));
				ret = ret + wall1 + wall2;
				if (wall1 < 1 || wall2 < 1)
					return -1000000;
			}
		}
		return ret;
	}

	private int checkMid(BlockPos center) {
		int ret = MAX_HEAT[getType()][0][world.getBlockState(center).getBlock() == Blocks.LAVA ? 1 : 2];
		if (ret > 0) {
			for (EnumFacing f : EnumFacing.HORIZONTALS) {
				int wall1 = isWall(center.offset(f));
				int wall2 = isWall(center.offset(f).offset(f.rotateY()));
				ret = ret + wall1 + wall2;
				if (wall1 < 1 || wall2 < 1)
					return -1000000;
			}
		}
		return ret;
	}

	private int isWall(BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		int id = block == CoreInit.MachineFrameSteel ? 1 : (block == CoreInit.MachineFrameTitanium ? 2 : (block == CoreInit.MachineFrameChrome ? 3 : 0));
		return MAX_HEAT[getType()][1][id];
	}

	public int getClientEnergyStored() {
		return clientEnergy;
	}

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodfactory:textures/blocks/ibfFront.png");
	}

	@Override
	public int[] getOutputSlots() {
		return new int[]{2};
	}

	@Override
	public int[] getInputSlots() {
		return new int[]{0, 1};
	}

	@Override
	public void checkItems() {
		ItemStackChecker s = MachineCraftingHandler.getBlastFurnaceOutput(inv.getStackInSlot(0), inv.getStackInSlot(1), heat);
		if (s != null) {
			checkItems(s, 2, maxProgress = s.getExtra3() * 10, 0, 1);
			setOut(0, s);
		}
	}

	@Override
	public void finish() {
		ItemStackChecker s = getOutput(0);
		addItemsAndSetProgress(s, 2, 0, 1);
	}

	@Override
	public void updateProgress() {
		energy.extractEnergy(5, false);
		progress = Math.max(progress - MathHelper.floor(10 * (getMaxProcessTimeNormal() / TYPE_MULTIPLIER_SPEED[getType()])), 0);
	}
}
