package com.tom.storage.block;

import com.tom.api.block.BlockGridDevice;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.tileentity.TileEntityBasicTerminal;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BasicTerminal extends BlockGridDevice {
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public BasicTerminal() {
		super(Material.IRON);
	}

	@Override
	public TileEntityGridDeviceBase<StorageNetworkGrid> createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBasicTerminal();
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(!worldIn.isRemote)playerIn.openGui(CoreInit.modInstance, GuiIDs.basicTerminalBlock.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return super
				.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, TomsModUtils.getDirectionFacing(placer, true));
	}
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta % 6));
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,FACING);
	}
	@SuppressWarnings("deprecation")
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (block != this)
		{
			return block.getLightValue(state, world, pos);
		}
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityBasicTerminal){
			TileEntityBasicTerminal te = (TileEntityBasicTerminal) tile;
			return te.poweredClient ? 11 : 0;
		}
		return getLightValue(state);
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