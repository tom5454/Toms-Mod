package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;

import com.tom.energy.tileentity.TileEntityFusionFluidInjector;

public class FusionFluidInjector extends BlockContainerTomsMod {
	/*@SideOnly(Side.CLIENT)
	private IIcon TopBottom;*/

	protected FusionFluidInjector(Material arg0) {
		super(arg0);
	}

	public FusionFluidInjector() {
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:fusionCase");
		this.TopBottom = iconregister.registerIcon("minecraft:fusionFluidP");
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		if(side == 0 | side == 1){
			return this.TopBottom;
		}else{
			return this.blockIcon;
		}
	}*/
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityFusionFluidInjector();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		return TomsModUtils.interactWithFluidHandler(((TileEntityFusionFluidInjector) worldIn.getTileEntity(pos)).getTankOnSide(side), playerIn, hand);
	}
}
