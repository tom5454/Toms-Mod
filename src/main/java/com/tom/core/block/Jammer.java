package com.tom.core.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.tileentity.TileEntityJammer;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class Jammer extends BlockContainerTomsMod {
	/*@SideOnly(Side.CLIENT)
	private IIcon online;
	@SideOnly(Side.CLIENT)
	private IIcon off;*/
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public Jammer() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityJammer();
	}
	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:tm/Antenna2");
    	this.online = iconregister.registerIcon("minecraft:tm/jammerOn");
    	this.off = iconregister.registerIcon("minecraft:tm/jammerOff");
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		 if(side > 1){
			 TileEntityJammer te = (TileEntityJammer) world.getTileEntity(x, y, z);
			 if(te.active) return this.online;
			else return this.off;
		 }else return this.blockIcon;
	}*/
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {ACTIVE});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(ACTIVE, meta == 1);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		return state.getValue(ACTIVE) ? 1 : 0;
	}
}
