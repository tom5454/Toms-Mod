package com.tom.transport.block;

import java.util.List;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.config.Config;
import com.tom.transport.TransportInit;
import com.tom.transport.tileentity.TileEntityConveyorSlope;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConveyorSlope extends BlockContainerTomsMod {
	//public static final PropertyDirection POSITION = PropertyDirection.create("pos");
	public SlopeItemBlock itemBlock;
	public static final PropertyDirection FACING = PropertyDirection.create("facing",Plane.HORIZONTAL);
	public static final PropertyInteger BELT_POS = PropertyInteger.create("belt", 0, 15);
	public static final PropertyBool IS_DOWN_SLOPE = PropertyBool.create("down");
	public ConveyorSlope() {
		super(Material.IRON);
		itemBlock = new SlopeItemBlock();
		setCreativeTab(TransportInit.tabTomsModTransport);
		setUnlocalizedName("tm.conveyorBeltSlope");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityConveyorSlope();
	}
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		/*if(placer.getHeldItem().getTagCompound() == null){
			placer.getHeldItem().setTagCompound(new NBTTagCompound());
			placer.getHeldItem().getTagCompound().setBoolean("tm_fresh_tag", true);
		}
		placer.getHeldItem().getTagCompound().setFloat("tm_hitY", hitY);*/
		return this.getDefaultState().withProperty(FACING, TomsModUtils.getDirectionFacing(placer, false)).withProperty(IS_DOWN_SLOPE, placer.getHeldItemMainhand().getMetadata() == 1);
	}
	/*@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		EnumFacing facing = state.getValue(POSITION);
		TileEntityConveyorSlope te = (TileEntityConveyorSlope) worldIn.getTileEntity(pos);
		EnumFacing box = EnumFacing.DOWN;
		if(facing.getAxis() == Axis.Y){
			box = TomsModUtils.getDirectionFacing(placer, facing.getAxis() != Axis.Y);
		}else{
			float hitY = stack.getTagCompound() != null ? stack.getTagCompound().getFloat("tm_hitY") : 0.5F;
			if(stack.getTagCompound() != null){
				if(stack.getTagCompound().getBoolean("tm_fresh_tag")) stack.setTagCompound(null);
				else stack.getTagCompound().removeTag("tm_hitY");
			}
			if(hitY > 0.75){
				box = EnumFacing.UP;
			}else if(hitY < 0.25){
				box = EnumFacing.DOWN;
			}else{
				EnumFacing f = TomsModUtils.getDirectionFacing(placer, false);
				if(f.getAxis() != facing.getAxis()){
					box = f;
				}else{
					box = EnumFacing.UP;
				}
			}

		}
		te.facing = box;
		worldIn.markBlockForUpdate(pos);
	}*/
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn,
			BlockPos pos) {
		TileEntityConveyorSlope te = (TileEntityConveyorSlope) worldIn.getTileEntity(pos);
		int belt = 0;
		if(Config.enableConveyorBeltAnimation)belt = MathHelper.floor_double(te.position) % 16;
		return state.withProperty(BELT_POS, belt);
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,IS_DOWN_SLOPE,FACING, BELT_POS);
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() + (state.getValue(IS_DOWN_SLOPE) ? 6 : 0);
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getFront(meta % 6);
		if(facing.getAxis() == Axis.Y)
			facing = EnumFacing.NORTH;
		//System.out.println(meta);
		return this.getDefaultState().withProperty(FACING,  facing).withProperty(IS_DOWN_SLOPE, meta > 5);
	}
	/*private void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, EnumFacing dir)
    {
		AxisAlignedBB box = AxisAlignedBB.fromBounds(minX,minY,minZ,maxX,maxY,maxZ);
		switch (dir) {
		case DOWN:
		default:
			break;
		case UP:
			box = AxisAlignedBB.fromBounds(box.minX, 1 - box.maxY, box.minZ, box.maxX, 1 - box.minY, box.maxZ);
			break;
		case NORTH:
			box =  AxisAlignedBB.fromBounds(box.minX, box.minZ, box.minY, box.maxX, box.maxZ, box.maxY);
			break;
		case SOUTH:
			box =  AxisAlignedBB.fromBounds(box.minX, box.minZ, 1 - box.maxY, box.maxX, box.maxZ, 1 - box.minY);
			break;
		case WEST:
			box =  AxisAlignedBB.fromBounds(box.minY, box.minZ, box.minX, box.maxY, box.maxZ, box.maxX);
			break;
		case EAST:
			box =  AxisAlignedBB.fromBounds(1 - box.maxY, box.minZ, box.minX, 1 - box.minY, box.maxZ, box.maxX);
			break;
		}
		 setBlockBounds(new Float(box.minX),new Float(box.minY),new Float(box.minZ),new Float(box.maxX),new Float(box.maxY),new Float(box.maxZ));
       /* switch (dir) {
            case DOWN:
                setBlockBounds(minX, 1.0F - maxZ, minY, maxX, 1.0F - minZ, maxY);
                break;
            case UP:
                setBlockBounds(minX, minZ, minY, maxX, maxZ, maxY);
                break;
            case NORTH:
                setBlockBounds(1.0F - maxX, minY, 1.0F - maxZ, 1.0F - minX, maxY, 1.0F - minZ);
                break;
            case EAST:
                setBlockBounds(minZ, minY, 1.0F - maxX, maxZ, maxY, 1.0F - minX);
                break;
            case WEST:
                setBlockBounds(1.0F - maxZ, minY, minX, 1.0F - minZ, maxY, maxX);
                break;
            default:
                setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
                break;
        }
    }
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, BlockPos pos){
		//EnumFacing dir = EnumFacing.getOrientation(blockAccess.getBlockMetadata(par2, par3, par4));
		IBlockState state = blockAccess.getBlockState(pos);
		setBlockBounds(0.0F, 10F/16F, 0.0F, 1.0F, 1.0F, 1.0F, state.getValue(FACING));
		//setBlockBounds(dir.offsetX <= 0 ? 0 : 1F - (1/16), dir.offsetY <= 0 ? 0 : 1F - (1/16), dir.offsetZ <= 0 ? 0 : 1F - (1/16), dir.offsetX >= 0 ? 1 : (1/16), dir.offsetY >= 0 ? 1 : (1/16), dir.offsetZ >= 0 ? 1 : (1/16));
	}*/
	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	@Override
	public boolean isOpaqueCube(IBlockState s)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState s)
	{
		return false;
	}
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, 0));
		list.add(new ItemStack(itemIn, 1, 1));
	}
	/*@Override
    public EnumWorldBlockLayer getBlockLayer() {
    	return EnumWorldBlockLayer.CUTOUT;
    }*/
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}
	public class SlopeItemBlock extends ItemBlock{
		public SlopeItemBlock() {
			super(ConveyorSlope.this);
			this.setMaxDamage(0);
			this.setHasSubtypes(true);
		}
		/**
		 * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
		 * placed as a Block (mostly used with ItemBlocks).
		 */
		@Override
		public int getMetadata(int damage)
		{
			return damage;
		}

		/**
		 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
		 * different names based on their damage or NBT.
		 */
		@Override
		public String getUnlocalizedName(ItemStack stack)
		{
			return super.getUnlocalizedName() + "." + (stack.getMetadata() == 1 ? "down" : "up");
		}
	}
}
