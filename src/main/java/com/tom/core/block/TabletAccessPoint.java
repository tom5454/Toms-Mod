package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;

import com.tom.core.tileentity.TileEntityTabletAccessPoint;

public class TabletAccessPoint extends BlockContainerTomsMod {
	/*@SideOnly(Side.CLIENT)
	private IIcon front;
	@SideOnly(Side.CLIENT)
	private IIcon side;*/
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	protected TabletAccessPoint(Material arg0) {
		super(arg0);
	}
	public TabletAccessPoint(){
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityTabletAccessPoint();
	}
	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:tm/TAPOn");
		this.front = iconregister.registerIcon("minecraft:tm/TAPOff");
		this.side = iconregister.registerIcon("minecraft:tm/TabletAccessPointSide");
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntity tilee = world.getTileEntity(x, y, z);
		TileEntityTabletAccessPoint te = ((TileEntityTabletAccessPoint)tilee);
		if(side == te.direction){
			return te.connected ? this.blockIcon : this.front;
		}else{
			return this.side;
		}
	}*/
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState bs, EntityLivingBase entity, ItemStack itemstack){
		EnumFacing l = TomsModUtils.getDirectionFacing(entity, true);
		EnumFacing s = l;
		if(l.getAxis() == Axis.Y) s = l.getOpposite();
		world.setBlockState(pos, bs.withProperty(FACING, s).withProperty(ACTIVE, false), 2);
		TileEntityTabletAccessPoint te = (TileEntityTabletAccessPoint) world.getTileEntity(pos);
		int d = l.ordinal();
		te.d = l;
		if (d == 5) te.direction = 4;
		else if(d == 4) te.direction = 5;
		else if (d == 3) te.direction = 2;
		else if(d == 2) te.direction = 3;
		else if(d == 0) te.direction = 1;
		else if(d == 1) te.direction = 0;
	}
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		//EnumFacing dir = EnumFacing.getOrientation(blockAccess.getBlockMetadata(par2, par3, par4));
		TileEntityTabletAccessPoint TE = (TileEntityTabletAccessPoint) source.getTileEntity(pos);
		return setBlockBounds(0.5F - 0.1875F, 0.5F - 0.1875F, 0.0F, 0.5F + 0.1875F, 0.5F + 0.1875F, 0.125F, TE != null ? TE.d.getOpposite() : EnumFacing.NORTH);
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
		setBlockBounds(0.5F - 0.1875F, 0.5F - 0.1875F, 0, 0.5F + 0.1875F, 0.5F + 0.1875F, 0.125F);
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
		if(player.capabilities.isCreativeMode && heldItem != null){
			if(heldItem.getItem() == CoreInit.trProcessor && heldItem.getTagCompound() != null && heldItem.getTagCompound().hasKey("tier")){
				TileEntityTabletAccessPoint te = (TileEntityTabletAccessPoint) world.getTileEntity(pos);
				te.setTier(heldItem.getTagCompound().getInteger("tier"));
				return true;
			}else if(heldItem.getItem() == CoreInit.linkedChipset){
				TileEntityTabletAccessPoint te = (TileEntityTabletAccessPoint) world.getTileEntity(pos);
				te.setLocked(true);
				if(heldItem.getTagCompound() == null)
					heldItem.setTagCompound(new NBTTagCompound());
				heldItem.getTagCompound().setInteger("x", pos.getX());
				heldItem.getTagCompound().setInteger("y", pos.getY());
				heldItem.getTagCompound().setInteger("z", pos.getZ());
				TomsModUtils.sendNoSpamTranslate(player, "tomsMod.chat.posSaved");
				return true;
			}else if(CoreInit.isWrench(heldItem,player) && player.isSneaking()){
				TileEntityTabletAccessPoint te = (TileEntityTabletAccessPoint) world.getTileEntity(pos);
				te.setLocked(false);
				return true;
			}
		}
		return false;
	}
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
