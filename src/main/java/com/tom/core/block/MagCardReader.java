package com.tom.core.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.api.block.IRotatable;
import com.tom.api.item.IMagCard;
import com.tom.apis.TomsModUtils;
import com.tom.core.tileentity.TileEntityMagCardReader;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MagCardReader extends BlockContainerTomsMod implements IRotatable {

	public MagCardReader() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityMagCardReader();
	}
	/*@SideOnly(Side.CLIENT)
	private IIcon side;
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:tm/Gray");
		this.side = iconregister.registerIcon("minecraft:tm/transparent");
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		return this.side;

	}
	public IIcon getIcon(int side, int meta){
		return this.blockIcon;
	}*/
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState bs, EntityLivingBase entity, ItemStack itemstack){
		EnumFacing l = TomsModUtils.getDirectionFacing(entity, true);
		TileEntityMagCardReader te = (TileEntityMagCardReader) world.getTileEntity(pos);
		int d = l.ordinal();
		te.d = l;
		if (d == 5) te.direction = 4;
		else if(d == 4) te.direction = 5;
		else if (d == 3) te.direction = 2;
		else if(d == 2) te.direction = 3;
		else if(d == 0) te.direction = 1;
		else if(d == 1) te.direction = 0;
		//System.out.println(d);
		//par1World.setBlockMetadataWithNotify(x, y, z, d, 3);
	}
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		//EnumFacing dir = EnumFacing.getOrientation(blockAccess.getBlockMetadata(par2, par3, par4));
		TileEntityMagCardReader TE = (TileEntityMagCardReader) source.getTileEntity(pos);
		return setBlockBounds(0.5F - (4F/16F), 0.5F - (5F/16F), 0.0F, 0.5F + (4F/16F), 0.5F + (5F/16F), 3F/16F, TE != null ? TE.d.getOpposite() : EnumFacing.NORTH);
		//setBlockBounds(dir.offsetX <= 0 ? 0 : 1F - (1/16), dir.offsetY <= 0 ? 0 : 1F - (1/16), dir.offsetZ <= 0 ? 0 : 1F - (1/16), dir.offsetX >= 0 ? 1 : (1/16), dir.offsetY >= 0 ? 1 : (1/16), dir.offsetZ >= 0 ? 1 : (1/16));
	}
	/*
	@SuppressWarnings("rawtypes")
	@Override
	public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List arraylist, Entity par7Entity){
		setBlockBoundsBasedOnState(world, i, j, k);
		super.addCollisionBoxesToList(world, i, j, k, axisalignedbb, arraylist, par7Entity);
	}
	 */
	/*@Override
	public void setBlockBoundsForItemRender(){
		setBlockBounds(0.5F - (4F/16F), 0.5F - (5F/16F), 0.0F, 0.5F + (4F/16F), 0.5F + (5F/16F), 3F/16F);
	}//*/
	/*@Override
    public boolean renderAsNormalBlock(){
        return false;
    }*/

	@Override
	public boolean isOpaqueCube(IBlockState s){
		return false;
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		TileEntityMagCardReader te = (TileEntityMagCardReader) world.getTileEntity(pos);
		if(heldItem != null && heldItem.getItem() instanceof IMagCard && !world.isRemote) te.activate(player, heldItem);
		return true;
	}

	@Override
	public boolean isRotatable() {
		return true;
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean isFullCube(IBlockState s)
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

}
