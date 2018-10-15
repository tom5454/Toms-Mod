package com.tom.storage.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import com.tom.api.block.BlockGridDevice;
import com.tom.api.block.IMethod.IClientMethod;
import com.tom.api.block.IRegisterRequired;
import com.tom.client.CustomModelLoader;
import com.tom.core.CoreInit;
import com.tom.storage.client.TerminalBlockModel;
import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.TileEntityBasicTerminal.TerminalColor;
import com.tom.storage.tileentity.TileEntityBasicTerminal.TerminalFacing;
import com.tom.util.TomsModUtils;

public abstract class BlockTerminalBase extends BlockGridDevice implements IRegisterRequired, IClientMethod {
	public static final PropertyEnum<TerminalFacing> FACING = PropertyEnum.create("facing", TerminalFacing.class);
	public static final UnlistedPropertyColor COLOR = new UnlistedPropertyColor();
	public static final UnlistedPropertyState STATE = new UnlistedPropertyState();

	public BlockTerminalBase() {
		super(Material.GLASS);
	}

	@Override
	public abstract TileEntityBasicTerminal createNewTileEntity(World worldIn, int meta);

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && playerIn.isSneaking() && CoreInit.isWrench(playerIn, hand)) {
			spawnAsEntity(worldIn, pos, getItem(worldIn, pos, state));
			worldIn.setBlockToAir(pos);
			return true;
		}
		if (!worldIn.isRemote)
			playerIn.openGui(CoreInit.modInstance, getGuiID(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	public abstract int getGuiID();

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, TerminalFacing.fromFacing(TomsModUtils.getDirectionFacing(placer, true), TomsModUtils.getDirectionFacing(placer, false)));
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, TerminalFacing.getFront(meta % 6));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{FACING}, new IUnlistedProperty[]{COLOR, STATE});
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (block != this) { return block.getLightValue(state, world, pos); }
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityBasicTerminal) {
			TileEntityBasicTerminal te = (TileEntityBasicTerminal) tile;
			return te.getClientPowered() ? 11 : 0;
		}
		return getLightValue(state);
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public static class UnlistedPropertyColor implements IUnlistedProperty<TerminalColor> {

		@Override
		public String getName() {
			return "terminalData";
		}

		@Override
		public boolean isValid(TerminalColor value) {
			return value != null;
		}

		@Override
		public String valueToString(TerminalColor value) {
			return value.getName();
		}

		@Override
		public Class<TerminalColor> getType() {
			return TerminalColor.class;
		}
	}

	public static class UnlistedPropertyState implements IUnlistedProperty<TileEntityBasicTerminal.TerminalState> {

		@Override
		public String getName() {
			return "terminalState";
		}

		@Override
		public boolean isValid(TileEntityBasicTerminal.TerminalState value) {
			return value != null;
		}

		@Override
		public String valueToString(TileEntityBasicTerminal.TerminalState value) {
			return value.getName();
		}

		@Override
		public Class<TileEntityBasicTerminal.TerminalState> getType() {
			return TileEntityBasicTerminal.TerminalState.class;
		}
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntityBasicTerminal te = (TileEntityBasicTerminal) world.getTileEntity(pos);
		return te != null ? getExtendedState(((IExtendedBlockState) state).withProperty(COLOR, te.getColor()).withProperty(STATE, te.getTerminalState()), te) : state;
	}

	public IBlockState getExtendedState(IExtendedBlockState state, TileEntityBasicTerminal te) {
		return state;
	}

	@Override
	public void exec() {
		CustomModelLoader.addOverride(new ResourceLocation("tomsmodstorage", getUnlocalizedName().substring(5)), new TerminalBlockModel(this));
		CoreInit.registerRender(Item.getItemFromBlock(this), 0, "tomsmodstorage:" + getUnlocalizedName().substring(5));
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
	public void register() {
		CoreInit.proxy.runMethod(this);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!worldIn.isRemote && stack.hasTagCompound()) {
			TileEntityBasicTerminal te = (TileEntityBasicTerminal) worldIn.getTileEntity(pos);
			te.setColor(stack.getTagCompound());
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityBasicTerminal te = (TileEntityBasicTerminal) worldIn.getTileEntity(pos);
		NBTTagCompound tag = new NBTTagCompound();
		te.writeToStackNBT(tag);
		ItemStack s = new ItemStack(this);
		s.setTagCompound(tag);
		spawnAsEntity(worldIn, pos, s);
		super.breakBlock(worldIn, pos, state);
	}
}
