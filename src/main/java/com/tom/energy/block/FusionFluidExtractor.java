package com.tom.energy.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.energy.tileentity.TileEntityFusionFluidExtractor;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

public class FusionFluidExtractor extends BlockContainerTomsMod {
	public static final PropertyDirection FACING = PropertyDirection.create("facing",Plane.HORIZONTAL);
	/*@SideOnly(Side.CLIENT)
	private IIcon front;*/

	protected FusionFluidExtractor(Material arg0) {
		super(arg0);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	public FusionFluidExtractor() {
		this(Material.IRON);
	}

	/*@SuppressWarnings("unused")
	private int getSide(IBlockAccess world, int x, int y, int z) {
		Block block1 = world.getBlockState(new BlockPos(x+2,y,z)).getBlock();
		Block block2 = world.getBlockState(new BlockPos(x-2,y,z)).getBlock();
		Block block3 = world.getBlockState(new BlockPos(x,y,z+2)).getBlock();
		Block block4 = world.getBlockState(new BlockPos(x+1,y,z)).getBlock();
		Block block5 = world.getBlockState(new BlockPos(x-1,y,z)).getBlock();
		Block block6 = world.getBlockState(new BlockPos(x,y,z+1)).getBlock();
		boolean b1 = block1 == EnergyInit.FusionFluidExtractor && block4 == EnergyInit.FusionCore,
				b2 = block2 == EnergyInit.FusionFluidExtractor && block5 == EnergyInit.FusionCore,
				b3 = block3 == EnergyInit.FusionFluidExtractor && block6 == EnergyInit.FusionCore;
		return b1 ? 4 : (b2 ? 5 : (b3 ? 2 : 3));
	}*/
	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:fusionCase");
		this.front = iconregister.registerIcon("minecraft:fusionFluidP");
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		if(side == getSide(world,x,y,z)){
			return this.front;
		}else{
			return this.blockIcon;
		}
	}*/

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityFusionFluidExtractor();
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING});
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
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		return state.getValue(FACING).getIndex();
	}
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState state, EntityLivingBase entity, ItemStack itemstack){
		EnumFacing f = TomsModUtils.getDirectionFacing(entity, false);
		world.setBlockState(pos, state.withProperty(FACING,f), 2);
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		return FluidUtil.interactWithFluidHandler(heldItem, ((TileEntityFusionFluidExtractor) worldIn.getTileEntity(pos)).getTankOnSide(side), playerIn);
	}
}
