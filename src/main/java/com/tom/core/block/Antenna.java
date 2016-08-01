package com.tom.core.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.tileentity.TileEntityAntenna;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class Antenna extends BlockContainerTomsMod{
	/*@SideOnly(Side.CLIENT)
	private IIcon online;
	@SideOnly(Side.CLIENT)
	private IIcon missing;
	@SideOnly(Side.CLIENT)
	private IIcon off;
	@SideOnly(Side.CLIENT)
	private IIcon noCont;*/
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);
	protected Antenna(Material p_i45386_1_) {
		super(p_i45386_1_);
	}
	public Antenna() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
		//this.setBlockTextureName("minecraft:tm/Antenna2");
	}
	/*@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z){
		this.setBlockBounds(1/16, 0, 1/16, 1-1/16, 1, 1-1/16);
	}*/

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityAntenna();
	}
	/*@Override
    public boolean renderAsNormalBlock(){
        return false;
    }

    @Override
    public boolean isOpaqueCube(){
        return false;
    }*/
	/*public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
    	if(side > 1){
    		TileEntityAntenna te = (TileEntityAntenna) world.getTileEntity(x, y, z);
    		if(te.powered && te.redstone){
    			//if(){
    				if(te.online) return this.online;
    				else return this.noCont;
    			//}else return this.missing;
    		}else return this.off;
    	}else return this.blockIcon;
    }
    @SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
    	this.blockIcon = iconregister.registerIcon("minecraft:tm/Antenna2");
    	this.online = iconregister.registerIcon("minecraft:tm/AntennaSideOnline");
    	this.missing = iconregister.registerIcon("minecraft:tm/AntennaSideMissing");
    	this.off = iconregister.registerIcon("minecraft:tm/AntennaSideOff");
    	this.noCont = iconregister.registerIcon("minecraft:tm/AntennaSideCContNF");
    }*/
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
}
