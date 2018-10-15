package com.tom.storage.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockGridDevice;
import com.tom.api.inventory.StoredItemStack;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.core.CoreInit;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.StorageNetworkGrid;
import com.tom.storage.tileentity.TileEntityCraftingController;
import com.tom.util.TomsModUtils;

public class CraftingController extends BlockGridDevice {
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);
	public static final PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);

	public CraftingController() {
		super(Material.IRON);
	}

	@Override
	public TileEntityGridDeviceBase<StorageNetworkGrid> createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCraftingController();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{STATE, FACING});
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return TomsModUtils.getBlockStateFromMeta(meta, STATE, FACING, getDefaultState(), 2);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return TomsModUtils.getMetaFromState(state.getValue(FACING), state.getValue(STATE));
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && playerIn.isSneaking() && CoreInit.isWrench(playerIn, hand)) {
			spawnAsEntity(worldIn, pos, getItem(worldIn, pos, state));
			worldIn.setBlockToAir(pos);
			return true;
		}
		if (!worldIn.isRemote) {
			TileEntityCraftingController te = (TileEntityCraftingController) worldIn.getTileEntity(pos);
			if (playerIn.isSneaking())
				TomsModUtils.sendNoSpam(playerIn, te.cancelCrafting());
			else
				TomsModUtils.sendNoSpam(playerIn, te.serializeMessage());
		}
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityCraftingController te = (TileEntityCraftingController) worldIn.getTileEntity(pos);
		List<ICraftable> s = te.getStoredStacks();
		if (!s.isEmpty()) {
			for (int i = 0;i < s.size();i++) {
				ICraftable stack = s.get(i);
				if (stack != null && stack instanceof StoredItemStack && stack.hasQuantity()) {
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), ((StoredItemStack) stack).getStack());
				}
			}
			s.clear();
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, TomsModUtils.getDirectionFacing(placer, false));
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
}
