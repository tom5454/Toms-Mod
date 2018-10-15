package com.tom.energy.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.energy.EnergyInit;
import com.tom.util.TomsModUtils;

import com.tom.energy.tileentity.TileEntityFusionController;

public class FusionController extends BlockContainerTomsMod {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);
	/*@SideOnly(Side.CLIENT)
	private IIcon front;
	@SideOnly(Side.CLIENT)
	private IIcon side;
	@SideOnly(Side.CLIENT)
	private IIcon top;
	@SideOnly(Side.CLIENT)
	private IIcon topPowered;
	@SideOnly(Side.CLIENT)
	private IIcon topDeformed;*/

	protected FusionController(Material arg0) {
		super(arg0);
	}

	public FusionController() {
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityFusionController();
	}
	/*@SideOnly(Side.CLIENT)
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		this.direction(world, x, y, z);
	}*/

	/*private void direction(World world, int x, int y, int z) {
		if (!world.isRemote) {
			Block direction = world.getBlock(x, y, z - 1);
			Block direction1 = world.getBlock(x, y, z + 1);
			Block direction2 = world.getBlock(x - 1, y, z);
			Block direction3 = world.getBlock(x + 1, y, z);
			byte byte0 = 3;
	
			if (direction.func_149730_j() && !direction.func_149730_j()) {
				byte0 = 3;
			}
	
			if (direction1.func_149730_j() && !direction1.func_149730_j()) {
				byte0 = 2;
			}
	
			if (direction2.func_149730_j() && !direction2.func_149730_j()) {
				byte0 = 5;
			}
	
			if (direction3.func_149730_j() && !direction3.func_149730_j()) {
				byte0 = 4;
			}
	
			world.setBlockMetadataWithNotify(x, y, z, byte0, 2);
		}
	}
	public void onBlockPlacedBy(World world, int x, int y, int z,EntityLivingBase entity, ItemStack itemstack){
		int direction = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if(direction == 0){
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}
	
		if(direction == 1){
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);
		}
	
		if(direction == 2){
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}
	
		if(direction == 3){
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		}
	}
	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:fusionCase");
		this.side = iconregister.registerIcon("minecraft:fusionCase");
		this.top = iconregister.registerIcon("minecraft:fusionGlassInActive");
		this.front = iconregister.registerIcon("minecraft:fusionController");
		this.topPowered = iconregister.registerIcon("minecraft:fusionGlassActive");
		this.topDeformed = iconregister.registerIcon("minecraft:fusionGlassEmpty");
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		if(side == 1){
			TileEntityFusionController te = (TileEntityFusionController) world.getTileEntity(x, y, z);
			return te.getMultiblock() ? (te.active() ? topPowered : top) : topDeformed;
		}else if(side == getSide(world,x,y,z)){
			return this.front;
		}else{
			return this.blockIcon;
		}
	}*/

	@SuppressWarnings("unused")
	private int getSide(IBlockAccess world, int x, int y, int z) {
		Block block1 = world.getBlockState(new BlockPos(x + 1, y, z)).getBlock();
		Block block2 = world.getBlockState(new BlockPos(x - 1, y, z)).getBlock();
		Block block3 = world.getBlockState(new BlockPos(x, y, z + 1)).getBlock();
		boolean b1 = block1 == EnergyInit.FusionCore, b2 = block2 == EnergyInit.FusionCore,
				b3 = block3 == EnergyInit.FusionCore;
		return b1 ? 4 : (b2 ? 5 : (b3 ? 2 : 3));
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState s) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState s, World world, BlockPos pos) {
		return ((TileEntityFusionController) world.getTileEntity(pos)).getComparatorOutput();
	}

	@Override
	public boolean canConnectRedstone(IBlockState s, IBlockAccess world, BlockPos pos, EnumFacing f) {
		return true;
	}

	/*public void onNeighborBlockChange(World world, int x, int y, int z, Block l){
		if (Block.getIdFromBlock(l) > 0 && l.canProvidePower() && world.isBlockIndirectlyGettingPowered(x, y, z)){
			((TileEntityFusionController)world.getTileEntity(x, y, z)).redstone();
		}else if(Block.getIdFromBlock(l) > 0 && l.canProvidePower()){
			((TileEntityFusionController)world.getTileEntity(x, y, z)).redstoneOff();
		}
	}*/
	@Override
	public boolean canDropFromExplosion(Explosion e) {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemstack) {
		EnumFacing f = TomsModUtils.getDirectionFacing(entity, false);
		world.setBlockState(pos, state.withProperty(FACING, f).withProperty(STATE, 0), 2);
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
