package com.tom.factory.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockMultiblockCasing;
import com.tom.factory.tileentity.TileEntityAdvMBCasing;

public class AdvancedMultiblockCasing extends BlockMultiblockCasing {
	/*@SideOnly(Side.CLIENT)
	private IIcon NoC;
	@SideOnly(Side.CLIENT)
	private IIcon UD;
	@SideOnly(Side.CLIENT)
	private IIcon RL;
	@SideOnly(Side.CLIENT)
	private IIcon CDR;
	@SideOnly(Side.CLIENT)
	private IIcon CDL;
	@SideOnly(Side.CLIENT)
	private IIcon CUR;
	@SideOnly(Side.CLIENT)
	private IIcon CUL;
	@SideOnly(Side.CLIENT)
	private IIcon f;
	@SideOnly(Side.CLIENT)
	private IIcon dot;*/
	protected AdvancedMultiblockCasing(Material p_i45386_1_) {
		super(p_i45386_1_);
		this.setHardness(3F);
	}
	public AdvancedMultiblockCasing(){
		this(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityAdvMBCasing();
	}
	/*public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntity tilee = world.getTileEntity(x, y, z);
		TileEntityAdvMBCasing te = (TileEntityAdvMBCasing)tilee;
		int[] tTable = te.texture;
		int t = tTable[side];
		if(t == 0){
			return this.NoC;
		}else if(t == 1){
			return this.UD;
		}else if(t == 2){
			return this.dot;
		}else if(t == 3){
			return this.f;
		}else if(t == 4){
			return this.RL;
		}else{
			return this.blockIcon;
		}
	}
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:mbANoC");
		this.NoC = iconregister.registerIcon("minecraft:mbANoC");
		this.f = iconregister.registerIcon("minecraft:mbAf");
		this.UD = iconregister.registerIcon("minecraft:mbAUD");
		this.RL = iconregister.registerIcon("minecraft:mbARL");
		this.CDL = iconregister.registerIcon("minecraft:mbCDL");
		this.CDR = iconregister.registerIcon("minecraft:mbCDR");
		this.CUL = iconregister.registerIcon("minecraft:mbCUL");
		this.CUR = iconregister.registerIcon("minecraft:mbCUR");
		this.dot = iconregister.registerIcon("minecraft:mbADot");
	}*/
	@Override
	public void breakBlockI(World world, int x, int y, int z, IBlockState block) {

	}
	@Override
	public void onNeighborBlockChangeI(IBlockAccess world, int x, int y, int z, IBlockState b, Block block) {

	}

}
