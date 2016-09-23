package com.tom.core.block;

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

import net.minecraftforge.fml.common.Optional;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.api.item.IMagCard;
import com.tom.apis.TomsModUtils;
import com.tom.lib.Configs;

import com.tom.core.tileentity.TileEntityMagCardDevice;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT)
public class MagCardDevice extends BlockContainerTomsMod implements
IPeripheralProvider {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 3);
	/*@SideOnly(Side.CLIENT)
	protected IIcon side;
	@SideOnly(Side.CLIENT)
	protected IIcon vIIcon;
	@SideOnly(Side.CLIENT)
	protected IIcon ivIIcon;*/
	public MagCardDevice() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	/*public void registerBlockIcons(IIconRegister i){
		this.blockIcon = i.registerIcon("minecraft:tm/magReader");
		this.side = i.registerIcon("minecraft:tm/holoDeviceSide");
		this.vIIcon = i.registerIcon("minecraft:tm/magReaderV");
		this.ivIIcon = i.registerIcon("minecraft:tm/magReaderIv");
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityMagCardDevice te = (TileEntityMagCardDevice)world.getTileEntity(x, y, z);
		if(side == te.direction){
			if(te.ledR) return this.ivIIcon;
			else if(te.ledG){
				return this.vIIcon;
				//return this.vIIcon;
			}else return this.blockIcon;
		}else return this.side;
	}*/
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityMagCardDevice();
	}
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		return te instanceof TileEntityMagCardDevice ? (IPeripheral)te : null;
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		TileEntityMagCardDevice te = (TileEntityMagCardDevice) world.getTileEntity(pos);
		if(heldItem != null && heldItem.getItem() instanceof IMagCard) {
			if(!world.isRemote){
				te.activate(player, heldItem);
			}
			return true;
		}
		return false;
	}
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState bs, EntityLivingBase entity, ItemStack itemstack){
		EnumFacing f = TomsModUtils.getDirectionFacing(entity, false);
		world.setBlockState(pos, bs.withProperty(FACING, f).withProperty(STATE, 0), 2);
		TileEntity te = world.getTileEntity(pos);
		TileEntityMagCardDevice te2 = (TileEntityMagCardDevice) te;
		int d = f.ordinal();
		if (d == 5) te2.direction = 4;
		else if(d == 4) te2.direction = 5;
		else if (d == 3) te2.direction = 2;
		else if(d == 2) te2.direction = 3;
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING,STATE});
	}
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
		return TomsModUtils.getBlockStateFromMeta(meta, STATE, FACING, getDefaultState(), 3);
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
