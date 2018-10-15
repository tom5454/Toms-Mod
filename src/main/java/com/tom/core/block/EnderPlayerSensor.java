package com.tom.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.CoreInit;

import com.tom.core.tileentity.TileEntityEnderSensor;

public class EnderPlayerSensor extends BlockContainerTomsMod {
	public EnderPlayerSensor() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityEnderSensor();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (!world.isRemote) {
			TileEntityEnderSensor te = (TileEntityEnderSensor) world.getTileEntity(pos);
			if (te.camoStack != null && heldItem != null && CoreInit.isWrench(player, hand)) {
				if (player.isSneaking()) {
					ItemStack camoStack = te.camoStack;
					te.camoStack = null;
					EntityItem itemEntity = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), camoStack);
					if (!player.capabilities.isCreativeMode)
						world.spawnEntity(itemEntity);
					te.transparent = false;
				} else {
					if (te.camoStack.getItem() instanceof ItemBlock) {
						Block b = ((ItemBlock) te.camoStack.getItem()).getBlock();
						if (b == Blocks.GLASS) {
							te.transparent = !te.transparent;
						}
					}
				}
			} else if (te.camoStack == null) {
				if (heldItem != null && heldItem.getItem() instanceof ItemBlock) {
					ItemStack camoStack = null;
					if (player.capabilities.isCreativeMode) {
						camoStack = heldItem.copy();
						camoStack.setCount(1);
					} else {
						camoStack = heldItem.splitStack(1);
					}
					te.camoStack = camoStack;
				}
				te.transparent = false;
			}
			te.markDirty();
			te.markBlockForUpdate(pos);
		}
		return true;
	}
	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public int getRenderType() {
		return 2;
	}
}
