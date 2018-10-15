package com.tom.factory.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.block.BlockMultiblockController;
import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityMultiblock;
import com.tom.factory.tileentity.TileEntityRefinery;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.util.MultiBlockPos;
import com.tom.util.TomsModUtils;

public class BlockRefinery extends BlockMultiblockController {

	@Override
	public TileEntityMultiblock createNewTileEntity(World worldIn, int meta) {
		return new TileEntityRefinery();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (state.getValue(STATE) != 0) {
			TileEntityRefinery te = (TileEntityRefinery) worldIn.getTileEntity(pos);
			boolean gui = true;
			if (pos instanceof MultiBlockPos) {
				IFluidHandler c = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side, ((MultiBlockPos) pos).getOther(), ((MultiBlockPos) pos).getId());
				if (c != null) {
					gui = !TomsModUtils.interactWithFluidHandler(c, playerIn, hand);
				}
			}
			if (gui) {
				if (!worldIn.isRemote)
					playerIn.openGui(CoreInit.modInstance, GuiIDs.refinery.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}
		return pos instanceof MultiBlockPos;
	}
}
