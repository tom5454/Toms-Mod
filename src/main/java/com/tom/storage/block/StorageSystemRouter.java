package com.tom.storage.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.storage.tileentity.TileEntityRouter;

public class StorageSystemRouter extends BlockContainerTomsMod {
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);

	public StorageSystemRouter() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityRouter();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityRouter) {
			try {
				((TileEntityRouter) tile).neighborUpdateGrid(EnumFacing.getFacingFromVector(neighbor.getX() - pos.getX(), neighbor.getY() - pos.getY(), neighbor.getZ() - pos.getZ()));
			} catch (Exception e) {
				// wrong tile
			}
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{STATE});
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STATE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(STATE, meta % 3);
	}
	/*@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote){
			playerIn.openGui(CoreInit.modInstance, GuiIDs.storageNetworkController.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}*/
}
