package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.api.item.IMagCard;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.TileEntityMagCardDevice;

public class MagCardDevice extends BlockContainerTomsMod {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 3);
	public MagCardDevice() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityMagCardDevice();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		TileEntityMagCardDevice te = (TileEntityMagCardDevice) world.getTileEntity(pos);
		if (heldItem != null && heldItem.getItem() instanceof IMagCard) {
			if (!world.isRemote) {
				te.activate(player, heldItem);
			}
			return true;
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState bs, EntityLivingBase entity, ItemStack itemstack) {
		EnumFacing f = TomsModUtils.getDirectionFacing(entity, false);
		world.setBlockState(pos, bs.withProperty(FACING, f).withProperty(STATE, 0), 2);
		TileEntity te = world.getTileEntity(pos);
		TileEntityMagCardDevice te2 = (TileEntityMagCardDevice) te;
		int d = f.ordinal();
		if (d == 5)
			te2.direction = 4;
		else if (d == 4)
			te2.direction = 5;
		else if (d == 3)
			te2.direction = 2;
		else if (d == 2)
			te2.direction = 3;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING, STATE});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return TomsModUtils.getBlockStateFromMeta(meta, STATE, FACING, getDefaultState(), 3);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {// System.out.println("getMeta");
		return TomsModUtils.getMetaFromState(state.getValue(FACING), state.getValue(STATE));
	}
}
