package com.tom.energy.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidUtil;

import com.tom.api.ITileFluidHandler;
import com.tom.api.block.BlockContainerTomsMod;

import com.tom.energy.tileentity.TileEntityLiquidFueledGenerator;

public class LiquidFueledGenerator extends BlockContainerTomsMod {

	public LiquidFueledGenerator() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityLiquidFueledGenerator();
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean ret = FluidUtil.getFluidHandler(heldItem) != null;
		FluidUtil.interactWithFluidHandler(heldItem, ((ITileFluidHandler) worldIn.getTileEntity(pos)).getTankOnSide(side), playerIn);
		return ret;
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityLiquidFueledGenerator te = (TileEntityLiquidFueledGenerator) worldIn.getTileEntity(pos);
		ItemStack s = new ItemStack(this);
		s.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = new NBTTagCompound();
		te.writeToStackNBT(tag);
		s.getTagCompound().setTag("BlockEntityTag", tag);
		s.getTagCompound().setBoolean("stored", true);
		spawnAsEntity(worldIn, pos, s);
		super.breakBlock(worldIn, pos, state);
	}
	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("stored")){
			tooltip.add(I18n.format("tomsMod.tooltip.itemsStored"));
		}
	}
}
