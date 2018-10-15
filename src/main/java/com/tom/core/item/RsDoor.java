package com.tom.core.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.core.CoreInit;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.TileEntityRSDoor;

public class RsDoor extends Item {

	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if (side == EnumFacing.UP) {

			++y;

			if (y < 255 && entityPlayer.canPlayerEdit(new BlockPos(x, y, z), side, itemStack) && entityPlayer.canPlayerEdit(new BlockPos(x, y + 1, z), side, itemStack) && world.isAirBlock(new BlockPos(x, y, z)) && world.isAirBlock(new BlockPos(x, y + 1, z)) && world.isSideSolid(new BlockPos(x, y - 1, z), EnumFacing.UP) && placeBlock(world, CoreInit.blockRsDoor, entityPlayer, itemStack, x, y, z) && placeBlock(world, CoreInit.blockRsDoor, entityPlayer, itemStack, x, y + 1, z)) {
				TileEntityRSDoor TE = (TileEntityRSDoor) world.getTileEntity(new BlockPos(x, y, z));
				EnumFacing plD = TomsModUtils.getDirectionFacing(entityPlayer, false);
				TE.place(entityPlayer, true, plD);
				if (!entityPlayer.capabilities.isCreativeMode) {
					itemStack.shrink(1);
					if (itemStack.getCount() < 1)
						entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, (ItemStack) null);
				}

				return true;
			}
		}
		return false;
	}

	protected boolean placeBlock(World world, Block block, EntityPlayer entityPlayer, ItemStack itemStack, int x, int y, int z) {
		if (world.setBlockState(new BlockPos(x, y, z), block.getDefaultState())) {

			block.onBlockPlacedBy(world, new BlockPos(x, y, z), block.getDefaultState(), entityPlayer, itemStack);
			// block.onPostBlockPlaced(world, new BlockPos(x, y, z),
			// block.getDefaultState(), 0);

			return true;

		} else {

			return false;

		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return onItemUse(playerIn.getHeldItem(hand), playerIn, worldIn, pos, facing, hitX, hitY, hitZ) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}
}
