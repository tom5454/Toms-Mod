package com.tom.energy.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.energy.tileentity.TileEntityLavaGenerator;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
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
import net.minecraftforge.fluids.FluidUtil;

public class LavaGenerator extends BlockContainerTomsMod {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public LavaGenerator() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityLavaGenerator();
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(heldItem != null){
			TileEntityLavaGenerator te = (TileEntityLavaGenerator) worldIn.getTileEntity(pos);
			/*if(heldItem.getItem() instanceof IFluidContainerItem){
				IFluidContainerItem fluidItem = (IFluidContainerItem) heldItem.getItem();
				if(!worldIn.isRemote && fluidItem.getFluid(heldItem) != null && fluidItem.getFluid(heldItem).getFluid() == FluidRegistry.LAVA){
					TileEntityLavaGenerator te = (TileEntityLavaGenerator) worldIn.getTileEntity(pos);
					FluidStack drained = fluidItem.drain(heldItem, fluidItem.getCapacity(heldItem), false);
					int fill = te.fill(side, drained, false);
					if(fill == fluidItem.getCapacity(heldItem)){
						te.fill(side, fluidItem.drain(heldItem, fill, !playerIn.capabilities.isCreativeMode), true);
					}
				}
				return true;
			}else if(heldItem.getItem() == Items.lava_bucket){
				if(!worldIn.isRemote){
					TileEntityLavaGenerator te = (TileEntityLavaGenerator) worldIn.getTileEntity(pos);
					int fill = te.fill(side, new FluidStack(FluidRegistry.LAVA,1000), false);
					if(fill == 1000){
						te.fill(side, new FluidStack(FluidRegistry.LAVA,1000), true);
						if(!playerIn.capabilities.isCreativeMode)playerIn.setHeldItem(hand, new ItemStack(Items.bucket));
					}
				}
				return true;
			}*/
			return FluidUtil.interactWithFluidHandler(heldItem, te.getTankOnSide(side), playerIn);
		}
		return false;
	}
	@Override
	public boolean isOpaqueCube(IBlockState s){
		return false;
	}
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing s){
		return s == EnumFacing.DOWN;
	}
	/*@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {

            /*Block block = worldIn.getBlockState(pos.north()).getBlock();
            Block block1 = worldIn.getBlockState(pos.south()).getBlock();
            Block block2 = worldIn.getBlockState(pos.west()).getBlock();
            Block block3 = worldIn.getBlockState(pos.east()).getBlock();

            if (enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            }
            else if (enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
            worldIn.setBlockState(pos, state.withProperty(ACTIVE, false), 2);
            System.out.println("on block Added "+enumfacing.toString());
        }
    }*/
	/* @Override
	public void onBlockPlaced(World world, BlockPos pos,IBlockState state, EntityLivingBase entity, ItemStack itemstack){
    	EnumFacing f = TomsModUtils.getDirectionFacing(entity, false);
    	world.setBlockState(pos, state.withProperty(FACING, f).withProperty(ACTIVE, false), 2);
    }*/
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		EnumFacing f = TomsModUtils.getDirectionFacing(placer, false);
		return this.getDefaultState().withProperty(FACING, f).withProperty(ACTIVE, false);
	}
	/* @Override
	@SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH).withProperty(ACTIVE, false);
    }*/
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING,ACTIVE});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getFront(meta % 6);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}
		//System.out.println("getState");
		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(ACTIVE, meta > 5);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		return state.getValue(FACING).getIndex() + (state.getValue(ACTIVE) ? 6 : 0);
	}
	/*@Override
	@SideOnly(Side.CLIENT)
    @SuppressWarnings("incomplete-switch")
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (state.getValue(ACTIVE))
        {
            EnumFacing enumfacing = state.getValue(FACING);
            double d0 = pos.getX() + 0.5D;
            double d1 = pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double d2 = pos.getZ() + 0.5D;
            double d3 = 0.39D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;
            int[] array = new int[0];

            switch (enumfacing.getOpposite())
            {
                case WEST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 12/16D, pos.getY()+1, pos.getZ() + 4/16D, 0.0D, 0.0D, 0.0D, array);
                    break;
                case EAST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 4/16D, pos.getY()+1, pos.getZ() + 12/16D, 0.0D, 0.0D, 0.0D, array);
                    break;
                case NORTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 12/16D, pos.getY()+1, pos.getZ() + 12/16D, 0.0D, 0.0D, 0.0D, array);
                    break;
                case SOUTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 4/16D, pos.getY()+1, pos.getZ() + 4/16D, 0.0D, 0.0D, 0.0D, array);
            }
        }
    }*/
}
