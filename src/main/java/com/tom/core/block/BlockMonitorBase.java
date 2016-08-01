package com.tom.core.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.tileentity.TileEntityMonitorBase;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockMonitorBase extends BlockContainerTomsMod {

	protected BlockMonitorBase(Material p_i45386_1_) {
		super(p_i45386_1_);
	}
	public BlockMonitorBase(){
		this(Material.IRON);
	}
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	/*@Override
    @SideOnly(Side.CLIENT)
    public int getRenderType(){
        return CoreInit.proxy.getRenderIdForRenderer(MonitorRenderer.class);
    }*/
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return super
				.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, TomsModUtils.getDirectionFacing(placer, true));
	}
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState bs, EntityLivingBase entity, ItemStack itemstack){
		TileEntity te = world.getTileEntity(pos);
		TileEntityMonitorBase te2 = (TileEntityMonitorBase) te;
		int d = bs.getValue(FACING).ordinal();
		if (d == 5) te2.direction = 4;
		else if(d == 4) te2.direction = 5;
		else if (d == 3) te2.direction = 2;
		else if(d == 2) te2.direction = 3;
		else if(d == 0) te2.direction = 1;
		else if(d == 1) te2.direction = 0;
		//world.setBlockMetadataWithNotify(pos, d, 3);
	}

	@Override
	public boolean isOpaqueCube(IBlockState s){
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
}
