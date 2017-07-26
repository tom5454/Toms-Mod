package com.tom.storage.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.BlockGridDevice;
import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.energy.EnergyType;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.core.CoreInit;
import com.tom.storage.handler.StorageNetworkGrid;
import com.tom.storage.tileentity.TileEntityEnergyAcceptor;

public class EnergyAcceptor extends BlockGridDevice implements IModelRegisterRequired {
	public static final PropertyEnum<EnergyType> ENERGY_TYPE = PropertyEnum.create("energy", EnergyType.class);

	public EnergyAcceptor() {
		super(Material.IRON);
	}

	@Override
	public TileEntityGridDeviceBase<StorageNetworkGrid> createNewTileEntity(World worldIn, int meta) {
		return new TileEntityEnergyAcceptor();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && playerIn.isSneaking() && CoreInit.isWrench(playerIn, hand)) {
			spawnAsEntity(worldIn, pos, getItem(worldIn, pos, state));
			worldIn.setBlockToAir(pos);
			return true;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
	}

	@Override
	public ItemBlock createItemBlock() {
		return new EnergyAcceptorItemBlock(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ENERGY_TYPE);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(ENERGY_TYPE, EnergyType.get(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ENERGY_TYPE).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ENERGY_TYPE, EnergyType.get(meta));
	}

	public static class EnergyAcceptorItemBlock extends ItemBlock {

		public EnergyAcceptorItemBlock(Block block) {
			super(block);
			setHasSubtypes(true);
		}

		@Override
		public int getMetadata(int damage) {
			return damage;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format("tomsMod.tooltip.accepts", EnergyType.get(stack.getMetadata()).toTooltip()));
	}

	@Override
	public void registerModels() {
		Item item = Item.getItemFromBlock(this);
		String name = CoreInit.getNameForItem(item).replace("|", "");
		CoreInit.registerRender(item, 0, name);
		CoreInit.registerRender(item, 1, name);
		CoreInit.registerRender(item, 2, name);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(ENERGY_TYPE).ordinal();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, 0));
		list.add(new ItemStack(itemIn, 1, 1));
		list.add(new ItemStack(itemIn, 1, 2));
	}
}
