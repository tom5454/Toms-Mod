package com.tom.factory.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.EmptyEntry;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityMachineBase;
import com.tom.recipes.AdvancedCraftingRecipes;

public abstract class BlockMachineBase extends BlockContainerTomsMod {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public BlockMachineBase(Material material) {
		this(material, CoreInit.MachineFrameSteel, CoreInit.MachineFrameTitanium);
	}
	public BlockMachineBase(Material material, Block casingMv, Block casingHv) {
		super(material);
		AdvancedCraftingRecipes.machines.put(this, new EmptyEntry<Block, Block>(casingMv, casingHv));
	}
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		EnumFacing f = TomsModUtils.getDirectionFacing(placer, false);
		return this.getDefaultState().withProperty(FACING, f.getOpposite()).withProperty(ACTIVE, false);
	}
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		TileEntityMachineBase te = (TileEntityMachineBase) worldIn.getTileEntity(pos);
		te.setType(stack.getMetadata());
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING,ACTIVE});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getFront(meta % 6);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}
		//System.out.println("getState");
		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(ACTIVE, meta > 5);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		return state.getValue(FACING).getIndex() + (state.getValue(ACTIVE) ? 6 : 0);
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityMachineBase te = (TileEntityMachineBase) worldIn.getTileEntity(pos);
		ItemStack s = new ItemStack(this, 1, te.getType());
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
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		TileEntityMachineBase te = (TileEntityMachineBase) world.getTileEntity(pos);
		return new ItemStack(this, 1, te.getType());
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
	{
		list.add(new ItemStack(itemIn, 1, 0));
		list.add(new ItemStack(itemIn, 1, 1));
		list.add(new ItemStack(itemIn, 1, 2));
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		int m = stack.getMetadata();
		tooltip.add(I18n.format("tomsMod.tooltip.accepts", m == 0 ? "HV" : m == 1 ? "MV" : "LV"));
		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("stored")){
			tooltip.add(I18n.format("tomsMod.tooltip.itemsStored"));
		}
	}
	@Override
	protected void dropInventory(World worldIn, BlockPos pos, IInventory te) {
		if(te instanceof TileEntityMachineBase){
			if(((TileEntityMachineBase) te).getUpgradeSlot() > -1)
				te.removeStackFromSlot(((TileEntityMachineBase) te).getUpgradeSlot());
		}
		super.dropInventory(worldIn, pos, te);
	}
	@Override
	public ItemBlock createItemBlock() {
		return new MachineItemBlock(this);
	}
	public static class MachineItemBlock extends ItemBlock{

		public MachineItemBlock(Block block) {
			super(block);
			setHasSubtypes(true);
		}

	}
}
