package com.tom.api.block;

import com.tom.api.tileentity.TileEntityControllerBase;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;

import net.minecraft.block.Block;
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

public abstract class BlockControllerBase extends BlockContainerTomsMod implements IBlockMultiblockPart, IBlockControler{
	/*@SideOnly(Side.CLIENT)
	protected IIcon side;
	@SideOnly(Side.CLIENT)
	protected IIcon offIIcon;
	@SideOnly(Side.CLIENT)
	protected IIcon dIIcon;*/
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);
	protected BlockControllerBase(Material p_i45386_1_) {
		super(p_i45386_1_);
		/*this.off = off;
		this.on = on;
		this.deformed = deformed;*/
		this.setHardness(2F);
		this.setResistance(2F);
	}
	/*protected String on = "minecraft:redstone_block";
	protected String off = "minecraft:dirt";
	protected String deformed = "minecraft:stone";*/
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState block){
		TileEntity tilee = world.getTileEntity(pos);
		TileEntityControllerBase te = (TileEntityControllerBase)tilee;
		te.blockBreak();
		this.breakBlockI(world, pos.getX(),pos.getY(),pos.getZ(), block);
		super.breakBlock(world, pos, block);
	}
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn) {
		TileEntity tilee = world.getTileEntity(pos);
		TileEntityControllerBase te = (TileEntityControllerBase)tilee;
		te.onNeighborChange();
		this.onNeighborBlockChangeI(world, pos.getX(),pos.getY(),pos.getZ(), state, blockIn);
	}
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState state, EntityLivingBase entity, ItemStack itemstack){
		EnumFacing f = TomsModUtils.getDirectionFacing(entity, false);
		world.setBlockState(pos, state.withProperty(FACING, f).withProperty(STATE, 0), 2);
		TileEntity te = world.getTileEntity(pos);
		//TileEntityControllerBase te2 = (TileEntityControllerBase) te;
		//int d = f.ordinal();
		/*if (d == 5) te2.direction = 4;
		else if(d == 4) te2.direction = 5;
		else if (d == 3) te2.direction = 2;
		else if(d == 2) te2.direction = 3;*/
		//System.out.println(te2.direction);
		//world.setBlockMetadataWithNotify(x, y, z, d, 3);
		this.onBlockPlacedByI(world, pos.getX(),pos.getY(),pos.getZ(), entity, itemstack, te);
	}
	/*public void registerBlockIcons(IIconRegister i){
		this.blockIcon = i.registerIcon(this.on);
		this.side = i.registerIcon("minecraft:mbf");
		this.offIIcon = i.registerIcon(this.off);
		this.dIIcon = i.registerIcon(this.deformed);
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityControllerBase te = (TileEntityControllerBase)world.getTileEntity(x, y, z);
		if(side == te.direction){
			boolean active = te.isActive();
			boolean formed = te.isFormed();
			if(formed && !active){
				return this.offIIcon;
			}else if(formed && active){
				return this.blockIcon;
			}else{
				return this.dIIcon;
			}
		}else{
			return this.side;
		}
	}*/
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		ItemStack itemstack = player.getHeldItemMainhand();
		TileEntityControllerBase te = (TileEntityControllerBase) world.getTileEntity(pos);
		if(itemstack != null && CoreInit.isWrench(itemstack, player)){
			/*int d = te.direction;
			int d2 = d + 1;
			int d3 = d2 > 3 ? 0 : d2;
			te.direction = d3;
			world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);*/
			te.onNeighborChange();
			return true;
		}else{
			return this.onBlockActivatedI(world, pos.getX(),pos.getY(),pos.getZ(), player, side, hitX, hitY, hitZ, te);
		}
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING,STATE});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		/*EnumFacing enumfacing = EnumFacing.getFront(meta % 4+2);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }
        //System.out.println("getState");
        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(STATE, meta / 4);*/
		return TomsModUtils.getBlockStateFromMeta(meta, STATE, FACING, getDefaultState());
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		/*EnumFacing enumfacing = state.getValue(FACING);
    	if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }
        return enumfacing.getIndex() * (state.getValue(STATE)+1);*/
		return TomsModUtils.getMetaFromState(state.getValue(FACING), state.getValue(STATE));
	}
}
