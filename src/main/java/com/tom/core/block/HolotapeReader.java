package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.CoreInit;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.TileEntityHolotapeReader;

public class HolotapeReader extends BlockContainerTomsMod {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);
	public HolotapeReader() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityHolotapeReader();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		TileEntityHolotapeReader te = (TileEntityHolotapeReader) world.getTileEntity(pos);
		if (!heldItem.isEmpty() && heldItem.getItem() == CoreInit.holotape) {
			if (!te.hasH) {
				if (!world.isRemote) {
					ItemStack t = heldItem.splitStack(1);
					te.holotape.setInventorySlotContents(0, t);
					te.markBlockForUpdate(pos);
				}
				return true;
			}
		} else {
			if (te.hasH) {
				if (!world.isRemote) {
					ItemStack holotape = te.holotape.removeStackFromSlot(0);
					EnumFacing facing = state.getValue(FACING).getOpposite();
					EntityItem itemEntity = new EntityItem(world, pos.getX()+.5+facing.getFrontOffsetX()*.5, pos.getY()+.5, pos.getZ()+.5+facing.getFrontOffsetZ()*.5, holotape);
					itemEntity.motionX = facing.getFrontOffsetX() * 0.3;
					itemEntity.motionZ = facing.getFrontOffsetZ() * 0.3;
					world.spawnEntity(itemEntity);
					te.markBlockForUpdate(pos);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState bs, EntityLivingBase entity, ItemStack itemstack) {
		EnumFacing f = TomsModUtils.getDirectionFacing(entity, false);
		world.setBlockState(pos, bs.withProperty(FACING, f).withProperty(STATE, 0), 2);
		TileEntity te = world.getTileEntity(pos);
		TileEntityHolotapeReader te2 = (TileEntityHolotapeReader) te;
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

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean formed = (meta & 8) > 0;
		boolean isRight = (meta & 4) > 0;
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(STATE, formed ? isRight ? 2 : 1 : 0);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		boolean formed = state.getValue(STATE) > 0;
		boolean isRight = state.getValue(STATE) == 2;
		int i = 0;
		i = i | state.getValue(FACING).getHorizontalIndex();

		if (formed) {
			i |= 8;
		}

		if (isRight) {
			i |= 4;
		}

		return i;
	}
}
