package com.tom.storage.multipart.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.tom.api.multipart.MultipartTomsMod;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.multipart.PartChannelModule;
import com.tom.storage.multipart.PartImportBus;

import mcmultipart.api.container.IPartInfo;

public class BlockImportBus extends BlockChannelModule {

	public BlockImportBus() {
		super(0.25, 0.25);
	}

	@Override
	public PartChannelModule createNewTileEntity(World worldIn, int meta) {
		return new PartImportBus();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (tryWrench(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ))
			return true;
		if (!worldIn.isRemote)
			playerIn.openGui(CoreInit.modInstance, GuiIDs.getMultipartGuiId(state.getValue(FACING)).ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public boolean onPartActivated(IPartInfo part, EntityPlayer player, EnumHand hand, RayTraceResult hit) {
		BlockPos pos = part.getPartPos();
		if (tryWrench(part, player, hand, hit))
			return true;
		if (!part.getActualWorld().isRemote)
			player.openGui(CoreInit.modInstance, GuiIDs.getMultipartGuiId(part.getState().getValue(FACING)).ordinal(), part.getActualWorld(), pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void breakBlockI(World worldIn, BlockPos pos, IBlockState state, MultipartTomsMod tile) {
		if (tile instanceof PartImportBus)
			spawnAsEntity(worldIn, pos, ((PartImportBus) tile).stuckStack);
	}
}
