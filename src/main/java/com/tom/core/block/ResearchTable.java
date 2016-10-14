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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;

import com.tom.core.tileentity.TileEntityResearchTable;

public class ResearchTable extends BlockContainerTomsMod {
	/**0:Base,1:Left,2:Right*/
	public static final PropertyInteger STATE = PropertyInteger.create("state",0,2);
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public ResearchTable() {
		super(Material.WOOD);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityResearchTable();
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING,STATE});
	}
	/*@Override
	public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getHorizontal(meta % 4).rotateY();

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }
        //System.out.println(enumfacing);
        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(STATE, (meta-1) / 4);
    }

    @Override
	public int getMetaFromState(IBlockState state)
    {//System.out.println("getMeta");
    	EnumFacing enumfacing = state.getValue(FACING);
    	if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }
        return (enumfacing.getHorizontalIndex()+1) * (state.getValue(STATE)+1);
    }*/
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		boolean formed = (meta & 8) > 0;
		boolean isRight = (meta & 4) > 0;
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(STATE, formed ? isRight ? 2 : 1 : 0);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{
		boolean formed = state.getValue(STATE) > 0;
		boolean isRight = state.getValue(STATE) == 2;
		int i = 0;
		i = i | state.getValue(FACING).getHorizontalIndex();

		if (formed)
		{
			i |= 8;
		}

		if (isRight)
		{
			i |= 4;
		}

		return i;
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(worldIn.isRemote) return true;
		//TileEntityResearchTable te = (TileEntityResearchTable) worldIn.getTileEntity(pos);
		int blockState = state.getValue(STATE);
		EnumFacing facing = state.getValue(FACING);
		if(blockState == 0){
			if(heldItem != null && heldItem.getItem() == Item.getItemFromBlock(this)){
				BlockPos offsetPos = pos.offset(facing.rotateY());
				IBlockState offsetBlockState = worldIn.getBlockState(offsetPos);
				if(offsetBlockState == null || offsetBlockState.getBlock() == null || offsetBlockState.getBlock().getMaterial(offsetBlockState) == Material.AIR){
					heldItem.splitStack(1);
					TomsModUtils.setBlockState(worldIn, pos, state.withProperty(STATE, 2));
					worldIn.setBlockState(offsetPos, this.getDefaultState().withProperty(FACING, facing).withProperty(STATE, 1));
					//te.isMaster = true;
					/*for(int i = 0;i<13;i++){
						System.out.println(i+" "+this.getStateFromMeta(i));
					}*/
				}else{
					TomsModUtils.sendNoSpamTranslate(player, "tomsMod.chat.destObs");
				}
			}
		}else if(blockState == 1){
			BlockPos parentPos = pos.offset(facing.rotateYCCW());
			IBlockState parentState = worldIn.getBlockState(parentPos);
			if(parentState != null && parentState.getBlock() == this){
				parentState.getBlock().onBlockActivated(worldIn, parentPos, parentState, player, hand, heldItem, side, hitX, hitY, hitZ);
			}
		}else if(blockState == 2){
			//worldIn.setBlockState(pos.offset(EnumFacing.UP, 2), Blocks.stone.getDefaultState());
			player.openGui(CoreInit.modInstance, GuiIDs.researchTable.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, TomsModUtils.getDirectionFacing(placer, false).getOpposite()).withProperty(STATE, 0);
	}
	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}
	@Override
	public boolean isFullCube(IBlockState s) {
		return false;
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		this.breakBlock(worldIn, pos, state, true);
	}
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state, boolean first){
		if(first){
			EnumFacing facing = state.getValue(FACING);
			int blockState = state.getValue(STATE);
			if(blockState != 0){
				/*for(EnumFacing f : EnumFacing.VALUES){
					if(f.getAxis() == Axis.Y) continue;
					IBlockState testState = worldIn.getBlockState(pos.offset(f));
					if(testState != null && testState.getBlock() == this){
						if(facing == testState.getValue(FACING) && blockState != testState.getValue(STATE)){
							((ResearchTable)testState.getBlock()).breakBlock(worldIn, pos, testState,false);
							worldIn.setBlockToAir(pos.offset(f));
							break;
						}
					}
				}*/
				EnumFacing f;
				if(blockState == 1){
					f = facing.rotateYCCW();
				}else{
					f = facing.rotateY();
				}
				IBlockState testState = worldIn.getBlockState(pos.offset(f));
				if(testState != null && testState.getBlock() == this){
					if(facing == testState.getValue(FACING) && blockState != testState.getValue(STATE)){
						((ResearchTable)testState.getBlock()).breakBlock(worldIn, pos, testState,false);
						worldIn.setBlockToAir(pos.offset(f));
						EntityItem drop2 = new EntityItem(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5,new ItemStack(CoreInit.researchTable));
						worldIn.spawnEntityInWorld(drop2);
					}
				}
			}
		}
		super.breakBlock(worldIn, pos, state);
	}
}
