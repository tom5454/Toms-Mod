package com.tom.storage.item;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import com.tom.api.block.IMethod.IClientMethod;
import com.tom.api.block.IRegisterRequired;
import com.tom.api.multipart.PartModule;
import com.tom.apis.TomsModUtils;
import com.tom.client.CustomModelLoader;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.block.BlockTerminalBase;
import com.tom.storage.client.TerminalPartModel;
import com.tom.storage.multipart.PartTerminal;
import com.tom.storage.multipart.block.BlockChannelModule;
import com.tom.storage.tileentity.TileEntityBasicTerminal.TerminalFacing;

import mcmultipart.api.container.IPartInfo;

public abstract class ItemPartTerminal extends BlockChannelModule implements IRegisterRequired, IClientMethod {

	public ItemPartTerminal(double size, double deep) {
		super(size, deep);
	}

	@Override
	public abstract PartTerminal createNewTileEntity(World worldIn, int meta);

	@Override
	public ItemPartTerminal setUnlocalizedName(String name) {
		super.setUnlocalizedName(name);
		return this;
	}

	@Override
	public ItemPartTerminal setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}

	@Override
	public void exec() {
		CustomModelLoader.addOverride(new ResourceLocation("tomsmodstorage", getUnlocalizedName().substring(5)), new TerminalPartModel(this));
		CoreInit.registerRender(Item.getItemFromBlock(this), 0, "tomsmodstorage:" + getUnlocalizedName().substring(5));
	}

	@Override
	public void register() {
		CoreInit.proxy.runMethod(this);
	}

	public abstract boolean hasCustomFront();

	public abstract String getName();

	public abstract String getCategory();

	public abstract int[][][] getImageIDs();

	public boolean mirrorModel(int state) {
		return false;
	}

	public int getStates() {
		return 1;
	}

	public ResourceLocation getFrontModelMapper(int state, ResourceLocation loc) {
		return loc;
	}

	public int getState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(BlockTerminalBase.FACING, TerminalFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockTerminalBase.FACING).ordinal();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		STATE = PropertyInteger.create("state", 0, 2);
		return new ExtendedBlockState(this, new IProperty[]{BlockTerminalBase.FACING, STATE}, new IUnlistedProperty[]{BlockTerminalBase.COLOR, BlockTerminalBase.STATE});
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(BlockTerminalBase.FACING, getFacing2(world, pos, facing, hitX, hitY, hitZ, placer));
	}

	public TerminalFacing getFacing2(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityLivingBase placer) {
		return TerminalFacing.fromFacing(getFacing(world, pos, facing, hitX, hitY, hitZ, placer), TomsModUtils.getDirectionFacing(placer, false));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ) && !worldIn.isRemote) {
			playerIn.openGui(CoreInit.modInstance, GuiIDs.getMultipartGuiId(facing).ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public boolean onPartActivated(IPartInfo part, EntityPlayer player, EnumHand hand, RayTraceResult hit) {
		if (!super.onPartActivated(part, player, hand, hit) && !part.getActualWorld().isRemote) {
			BlockPos pos = part.getPartPos();
			player.openGui(CoreInit.modInstance, GuiIDs.getMultipartGuiId(getFacing(part.getState())).ordinal(), part.getActualWorld(), pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public EnumFacing getFacing(IBlockState state) {
		return state.getValue(BlockTerminalBase.FACING).getFace();
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos, PartModule<?> duct) {
		PartTerminal te = (PartTerminal) duct;
		return te != null ? ((IExtendedBlockState) state).withProperty(BlockTerminalBase.COLOR, te.getColor()).withProperty(BlockTerminalBase.STATE, te.getTerminalState()) : state;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		PartTerminal duct = (PartTerminal) worldIn.getTileEntity(pos);
		if (!player.capabilities.isCreativeMode)
			spawnAsEntity(worldIn, pos, duct.createStack(this));
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
	}

	@Override
	public void onPartHarvested(IPartInfo part, EntityPlayer player) {
		PartTerminal duct = (PartTerminal) part.getTile();
		if (!player.capabilities.isCreativeMode)
			spawnAsEntity(part.getActualWorld(), part.getPartPos(), duct.createStack(this));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		PartTerminal duct = (PartTerminal) worldIn.getTileEntity(pos);
		duct.setColor(stack.getTagCompound());
	}

	@Override
	public void onPartPlacedBy(IPartInfo part, EntityLivingBase placer, ItemStack stack) {
		PartTerminal duct = (PartTerminal) part.getTile();
		duct.setColor(stack.getTagCompound());
	}
}
