package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;

import com.tom.energy.tileentity.TileEntityFusionCharger;

public class FusionCharger extends BlockContainerTomsMod {

	protected FusionCharger(Material arg0) {
		super(arg0);
	}
	public FusionCharger(){
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityFusionCharger();
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState s) {
		return true;
	}
	@Override
	public int getComparatorInputOverride(IBlockState s, World world, BlockPos pos) {
		TileEntityFusionCharger te = ((TileEntityFusionCharger)world.getTileEntity(pos));
		double energy = te.getEnergyStored();
		int rs = MathHelper.floor_double(energy / (te.getMaxEnergyStored() / 15));
		return rs != 0 ? rs : (te.ready() ? 1 : 0);
	}

}
