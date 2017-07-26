package com.tom.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import com.tom.apis.MultiBlockPos;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityMultiblockController;
import com.tom.handler.GuiHandler.GuiIDs;

public abstract class BlockMultiblockController2 extends BlockMultiblockController {

	@Override
	public abstract TileEntityMultiblockController createNewTileEntity(World worldIn, int meta);

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (pos instanceof MultiBlockPos) {
			MultiBlockPos p = (MultiBlockPos) pos;
			int id = p.getId();
			TileEntity tile = worldIn.getTileEntity(p.getOther());
			if (!((id == 3 || id == 4 || id == 7 || id == 8) && tile != null && TomsModUtils.interactWithFluidHandler(tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing), playerIn, hand))) { return onPartActivated(worldIn, pos, state, playerIn, hand, facing, p, id); }
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	protected boolean onPartActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, BlockPos partPos, int id) {
		if (id == 10) {
			if (!worldIn.isRemote)
				playerIn.openGui(CoreInit.modInstance, GuiIDs.mbfuelrod.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return false;
	}
}
