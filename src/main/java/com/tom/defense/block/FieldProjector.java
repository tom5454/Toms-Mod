package com.tom.defense.block;

import java.util.List;
import java.util.Random;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.defense.tileentity.TileEntityForceFieldProjector;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FieldProjector extends BlockContainerTomsMod {
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public FieldProjector() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityForceFieldProjector();
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		TileEntityForceFieldProjector te = (TileEntityForceFieldProjector) worldIn.getTileEntity(pos);
		return te.onBlockActivated(playerIn, heldItem);
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,ACTIVE);
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ACTIVE) ? 1 : 0;
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(ACTIVE, meta == 1);
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityForceFieldProjector te = (TileEntityForceFieldProjector) worldIn.getTileEntity(pos);
		te.breakBlock();
		ItemStack stack = new ItemStack(this);
		NBTTagCompound tag = new NBTTagCompound();
		te.writeToStackNBT(tag);
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setTag("BlockEntityTag", tag);
		stack.getTagCompound().setBoolean("stored", true);
		spawnAsEntity(worldIn, pos, stack);
		super.breakBlock(worldIn, pos, state);
	}
	@Override
	protected void dropInventory(World worldIn, BlockPos pos, IInventory te) {
		ItemStack s = te.removeStackFromSlot(3);
		if(s != null)spawnAsEntity(worldIn, pos, s);
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
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("stored")){
			tooltip.add(I18n.format("tomsMod.tooltip.itemsStored"));
		}
	}
}
