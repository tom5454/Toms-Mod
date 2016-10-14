package com.tom.factory.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityMachineBase;
import com.tom.factory.tileentity.TileEntitySolderingStation;
import com.tom.handler.GuiHandler.GuiIDs;

public class SolderingStation extends BlockMachineBase {

	public SolderingStation() {
		super(Material.IRON);
	}

	@Override
	public TileEntityMachineBase createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySolderingStation();
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote)playerIn.openGui(CoreInit.modInstance, GuiIDs.solderingStation.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}
