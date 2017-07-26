package com.tom.factory.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.ITileFluidHandler;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityElectricalRubberProcessor;
import com.tom.factory.tileentity.TileEntityMachineBase;
import com.tom.handler.GuiHandler.GuiIDs;

public class ElectricalRubberProcessor extends BlockMachineBase {

	public ElectricalRubberProcessor() {
		super(Material.IRON);
	}

	@Override
	public TileEntityMachineBase createNewTileEntity(World worldIn, int meta) {
		return new TileEntityElectricalRubberProcessor();
	}

	@Override
	public boolean onBlockActivatedI(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!TomsModUtils.interactWithFluidHandler(((ITileFluidHandler) worldIn.getTileEntity(pos)).getTankOnSide(side), playerIn, hand) && !worldIn.isRemote)
			playerIn.openGui(CoreInit.modInstance, GuiIDs.eRubberProcessor.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}
