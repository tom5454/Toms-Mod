package com.tom.storage.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.api.item.ICustomCraftingHandlerAdv;
import com.tom.core.CoreInit;
import com.tom.storage.StorageInit;
import com.tom.storage.tileentity.TileEntityUltimateTank;

public class BlockTankUltimate extends BlockContainerTomsMod implements ICustomCraftingHandlerAdv{

	public BlockTankUltimate() {
		super(Material.GLASS);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityUltimateTank();
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote && playerIn.isSneaking() && CoreInit.isWrench(heldItem, playerIn)){
			worldIn.setBlockToAir(pos);
			return true;
		}
		boolean ret = FluidUtil.getFluidHandler(heldItem) != null;
		FluidUtil.interactWithFluidHandler(heldItem, ((TileEntityUltimateTank) worldIn.getTileEntity(pos)).getTankOnSide(side), playerIn);
		return ret;
	}
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
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
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		ItemStack stack = new ItemStack(this);
		if(tile instanceof TileEntityUltimateTank){
			((TileEntityUltimateTank)tile).writeToStackNBT(stack);
		}
		spawnAsEntity(worldIn, pos, stack);
		super.breakBlock(worldIn, pos, state);
	}
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityUltimateTank){
			((TileEntityUltimateTank)tile).readFromStackNBT(stack);
		}
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		if(stack.hasTagCompound()){
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("BlockEntityTag").getCompoundTag("tank");
			if(!tag.hasKey("Empty")){
				FluidStack s = FluidStack.loadFluidStackFromNBT(tag);
				if(s != null)
					tooltip.add(I18n.format("tomsMod.tooltip.fluidStored", I18n.format(s.getUnlocalizedName()), s.amount, 1024000));
			}
		}
	}
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileEntityUltimateTank){
			return ((TileEntityUltimateTank)tile).getComparatorValue();
		}
		return 0;
	}
	@Override
	public void onCrafingAdv(String player, ItemStack crafting, ItemStack second, IInventory craftMatrix) {
		ItemStack old = craftMatrix.getStackInSlot(4);
		if(old != null && old.getItem() == Item.getItemFromBlock(StorageInit.tankElite))
			crafting.setTagCompound(old.getTagCompound());
	}

	@Override
	public void onUsingAdv(String player, ItemStack crafting, ItemStack second, IInventory craftMatrix, ItemStack s) {

	}
}