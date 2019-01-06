package com.tom.storage.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.BlockGridDevice;
import com.tom.api.grid.StorageNetworkGrid;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.tileentity.TileEntityInterface;
import com.tom.util.TomsModUtils;

public class BlockInterface extends BlockGridDevice {
	public static final PropertyEnum<InterfaceFacing> FACING = PropertyEnum.<BlockInterface.InterfaceFacing>create("facing", InterfaceFacing.class);

	public BlockInterface() {
		super(Material.IRON);
	}

	@Override
	public TileEntityGridDeviceBase<StorageNetworkGrid> createNewTileEntity(World worldIn, int meta) {
		return new TileEntityInterface();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && CoreInit.isWrench(playerIn, hand)) {
			if (playerIn.isSneaking())
				TomsModUtils.breakBlock(worldIn, pos);
			else {
				state = state.withProperty(FACING, InterfaceFacing.VALUES[(state.getValue(FACING).ordinal() + 1) % InterfaceFacing.VALUES.length]);
				TomsModUtils.setBlockState(worldIn, pos, state);
			}
			return true;
		}
		if (!worldIn.isRemote)
			playerIn.openGui(CoreInit.modInstance, GuiIDs.blockInterface.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	protected void dropInventory(World worldIn, BlockPos pos, IInventory te) {
		for (int i = 0;i < 18;i++)
			te.setInventorySlotContents(i, null);
		if (te instanceof TileEntityInterface) {
			TileEntityInterface t = (TileEntityInterface) te;
			List<ItemStack> items = t.getStacksToPush();
			for (int i = 0;i < items.size();i++) {
				ItemStack s = items.get(i);
				while (s.getCount() > 0) {
					int max = Math.min(s.getCount(), s.getMaxStackSize());
					spawnAsEntity(worldIn, pos, s.splitStack(max));
				}
			}
		}
		super.dropInventory(worldIn, pos, te);
	}

	@Override
	public int quantityDropped(Random random) {
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
		if (tile instanceof TileEntityInterface) {
			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityInterface) tile).writeToStackNBT(tag);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setTag("BlockEntityTag", tag);
			stack.getTagCompound().setBoolean("stored", true);
		}
		spawnAsEntity(worldIn, pos, stack);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("stored")) {
			tooltip.add(I18n.format("tomsMod.tooltip.itemsStored"));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, InterfaceFacing.VALUES[meta % InterfaceFacing.VALUES.length]);
	}

	public static enum InterfaceFacing implements IStringSerializable {
		NONE, DOWN, UP, NORTH, SOUTH, WEST, EAST;
		public static final InterfaceFacing[] VALUES = values();

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		public static InterfaceFacing getFromFacing(EnumFacing f) {
			return VALUES[f == null ? 0 : (f.ordinal() + 1)];
		}
	}
}
