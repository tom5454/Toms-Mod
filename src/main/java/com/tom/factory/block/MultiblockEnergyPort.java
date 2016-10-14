package com.tom.factory.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockMultiblockPart;
import com.tom.factory.tileentity.TileEntityMBEnergyPort;

public class MultiblockEnergyPort extends BlockMultiblockPart{
	/*@SideOnly(Side.CLIENT)
	private IIcon off;
	@SideOnly(Side.CLIENT)
	private IIcon b;*/
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);
	protected MultiblockEnergyPort(Material arg0) {
		super(arg0);
	}
	public MultiblockEnergyPort() {
		this(Material.IRON);
	}
	/*public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityMBEnergyPort te = (TileEntityMBEnergyPort)world.getTileEntity(x, y, z);
		boolean formed = te.isFormed();
		boolean powered = te.powered;
		if(formed && !powered){
			return this.off;
		}else if(formed && powered){
			return this.blockIcon;
		}else{
			return this.b;
		}

	}
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.off = iconregister.registerIcon("minecraft:mbEnergyPOff");
		this.blockIcon = iconregister.registerIcon("minecraft:mbEnergyPOn");
		this.b = iconregister.registerIcon("minecraft:mbEnergyPNF");
	}*/
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityMBEnergyPort();
	}
	@Override
	public void breakBlockI(World world, int x, int y, int z, IBlockState block) {

	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {STATE});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(STATE, meta % 3);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		return state.getValue(STATE);
	}
	@Override
	public void onNeighborBlockChangeI(IBlockAccess world, int x, int y, int z, IBlockState b, Block block) {

	}
}
