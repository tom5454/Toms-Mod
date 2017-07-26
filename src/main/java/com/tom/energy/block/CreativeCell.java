package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.CoreInit;

import com.tom.energy.tileentity.TileEntityCreativeCell;

public class CreativeCell extends BlockContainerTomsMod {

	public CreativeCell() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCreativeCell();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack is = player.getHeldItem(hand);
		if (is != null && CoreInit.isWrench(player, hand)) {
			if (!world.isRemote) {
				if (player.isSneaking()) {
					world.setBlockToAir(pos);
					return true;
				}
				TileEntityCreativeCell te = (TileEntityCreativeCell) world.getTileEntity(pos);
				boolean c = te.contains(side);
				if (c)
					te.outputSides &= ~(1 << side.ordinal());
				else
					te.outputSides |= 1 << side.ordinal();
				// System.out.println(" "+te.outputSides);
				te.markBlockForUpdate(pos);
				te.markDirty();
			}
			return true;
		}
		return false;
	}
}
