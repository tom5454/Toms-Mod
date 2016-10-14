package com.tom.factory.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockMultiblockPart;
import com.tom.factory.tileentity.TileEntityMBFuelRod;

public class MultiblockFuelRod extends BlockMultiblockPart {
	/*@SideOnly(Side.CLIENT)
	private IIcon top;*/
	protected MultiblockFuelRod(Material p_i45386_1_) {
		super(p_i45386_1_);
	}
	public MultiblockFuelRod(){
		this(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityMBFuelRod();
	}
	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.top = iconregister.registerIcon("minecraft:mbFuelRod");
		this.blockIcon = iconregister.registerIcon("minecraft:mbAf");
	}
	public IIcon getIcon(int side,int meta){
		if(side == 1) return top;
		else return this.blockIcon;
	}*/
	@Override
	public void breakBlockI(World world, int x, int y, int z, IBlockState block) {

	}
	@Override
	public void onNeighborBlockChangeI(IBlockAccess world, int x, int y, int z, IBlockState b, Block block) {
		// TODO Auto-generated method stub

	}
}
