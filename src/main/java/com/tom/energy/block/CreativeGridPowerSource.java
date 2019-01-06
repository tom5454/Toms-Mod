package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;

import com.tom.energy.tileentity.TileEntityCreativeGridPowerSource;

public class CreativeGridPowerSource extends BlockContainerTomsMod {

	public CreativeGridPowerSource() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCreativeGridPowerSource();
	}
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntityCreativeGridPowerSource te = (TileEntityCreativeGridPowerSource) worldIn.getTileEntity(pos);
		te.playerName = placer.getName();
		te.updatePlayerHandler();
	}
}
