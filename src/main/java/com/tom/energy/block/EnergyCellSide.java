package com.tom.energy.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockMultiblockPart;

import com.tom.energy.tileentity.TileEntityEnergyCellSide;

public class EnergyCellSide extends BlockMultiblockPart {
	/*@SideOnly(Side.CLIENT)
	private IIcon out;*/
	protected EnergyCellSide(Material p_i45386_1_) {
		super(p_i45386_1_);
	}
	public EnergyCellSide(){
		this(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityEnergyCellSide();
	}
	/*@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote){
			ItemStack itemstack = player.getHeldItem();
			TileEntity tilee = world.getTileEntity(pos);
			if(tilee instanceof TileEntityEnergyCellSide){
				TileEntityEnergyCellSide te = (TileEntityEnergyCellSide)tilee;
				if(itemstack != null && CoreInit.isWrench(itemstack,player)){
					te.input = !te.input;
					te.markDirty();
					return true;
				}else{
					//player.openGui(CoreInit.modInstance, GuiHandler.GuiIDs.mbFluidHatch.ordinal(), world, x, y, z);
				}
			}/*else{
				System.out.println(tilee);
			}//*/
	/*}
		return true;
	}
	/*public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityEnergyCellSide te = (TileEntityEnergyCellSide)world.getTileEntity(x, y, z);
		boolean input = te.input;
		if(!input){
			return this.out;
		}else{
			return this.blockIcon;
		}
	}
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.out = iconregister.registerIcon("minecraft:mbFluidOut");
		this.blockIcon = iconregister.registerIcon("minecraft:mbFluidIn");
	}*/
	@Override
	public void breakBlockI(World world, int x, int y, int z, IBlockState block) {

	}
	@Override
	public void onNeighborBlockChangeI(IBlockAccess world, int x, int y, int z, IBlockState b, Block block) {

	}

}
