package com.tom.factory.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockMultiblockPart;
import com.tom.factory.tileentity.TileEntityMBPressurePort;

public class MultiblockPressurePort extends BlockMultiblockPart {
	/*@SideOnly(Side.CLIENT)
	private IIcon off;
	@SideOnly(Side.CLIENT)
	private IIcon b;*/

	protected MultiblockPressurePort(Material arg0) {
		super(arg0);
	}
	public MultiblockPressurePort(){
		this(Material.IRON);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityMBPressurePort();
	}
	/*public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityMBPressurePort te = (TileEntityMBPressurePort)world.getTileEntity(x, y, z);
		boolean formed = te.isFormed();
		boolean powered = te.pressurized();
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
		this.off = iconregister.registerIcon("minecraft:mbPPN");
		this.blockIcon = iconregister.registerIcon("minecraft:mbPPP");
		this.b = iconregister.registerIcon("minecraft:mbPPNF");
	}*/
	@Override
	public void breakBlockI(World world, int x, int y, int z, IBlockState block) {

	}
	//	@Override
	//	public void onNeighborBlockChangeI(World world, int x, int y, int z,
	//			IBlockState b, Block block) {
	//		/*TileEntityMBPressurePort te = (TileEntityMBPressurePort)world.getTileEntity(new BlockPos(x, y, z));
	//		te.onNeighborChange();*/
	//
	//	}
	@Override
	public void onNeighborBlockChangeI(IBlockAccess world, int x, int y, int z, IBlockState b, Block block) {

	}

}
