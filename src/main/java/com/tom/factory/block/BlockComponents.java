package com.tom.factory.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.ICustomItemBlock;
import com.tom.api.block.IIconRegisterRequired;
import com.tom.core.CoreInit;

public class BlockComponents extends Block implements IIconRegisterRequired, ICustomItemBlock{
	public static final PropertyEnum<ComponentVariants> VARIANT = PropertyEnum.<ComponentVariants>create("variant", ComponentVariants.class);
	public BlockComponents() {
		super(Material.IRON);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
	{
		for(int i = 0;i<ComponentVariants.VALUES.length;i++)
			list.add(new ItemStack(itemIn, 1, i));
	}
	@Override
	public void registerIcons() {
		Item item = Item.getItemFromBlock(this);
		String type = CoreInit.getNameForItem(item).replace("|", "");
		for(int i = 0;i<ComponentVariants.VALUES.length;i++)
			CoreInit.registerRender(item, i, type + "." + ComponentVariants.get(i).getName());
	}
	public static enum ComponentVariants implements IStringSerializable{
		REFINERY_HEATER, OUTPUT_HATCH, ENGINEERING_BLOCK,
		;
		public static final ComponentVariants[] VALUES = values();

		@Override
		public String getName() {
			return name().toLowerCase();
		}
		public static ComponentVariants get(int i){
			return VALUES[MathHelper.abs_int(i % VALUES.length)];
		}
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, ComponentVariants.get(meta));
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}
	@Override
	public ItemBlock createItemBlock() {
		return new BlockComponentsItemBlock(this);
	}
	public static class BlockComponentsItemBlock extends ItemBlock{

		public BlockComponentsItemBlock(Block block) {
			super(block);
			setHasSubtypes(true);
		}
		@Override
		public String getUnlocalizedName(ItemStack stack) {
			return super.getUnlocalizedName(stack) + "." + ComponentVariants.get(stack.getMetadata()).getName();
		}
		@Override
		public int getMetadata(int damage) {
			return damage;
		}
	}
	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}
}
