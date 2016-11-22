package com.tom.factory.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidUtil;

import com.tom.api.ITileFluidHandler;
import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityMachineBase;
import com.tom.factory.tileentity.TileEntityMixer;
import com.tom.handler.GuiHandler.GuiIDs;

public class BlockMixer extends BlockMachineBase {

	public BlockMixer() {
		super(Material.IRON);
	}

	@Override
	public TileEntityMachineBase createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMixer();
	}
	@Override
	public boolean onBlockActivatedI(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!FluidUtil.interactWithFluidHandler(heldItem, ((ITileFluidHandler) worldIn.getTileEntity(pos)).getTankOnSide(side), playerIn) && !worldIn.isRemote)playerIn.openGui(CoreInit.modInstance, GuiIDs.mixer.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}