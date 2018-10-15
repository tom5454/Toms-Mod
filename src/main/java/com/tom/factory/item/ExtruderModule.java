package com.tom.factory.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.item.IExtruderModule;
import com.tom.core.CoreInit;

public class ExtruderModule extends Item implements IExtruderModule, IModelRegisterRequired {
	public ExtruderModule() {
		setHasSubtypes(true);
		ExtruderLevel.item = this;
	}

	@Override
	public int getLevel(ItemStack stack, World world, BlockPos pos) {
		return ExtruderLevel.get(stack.getMetadata()).level;
	}

	@Override
	public int getSpeed(ItemStack stack, World world, BlockPos pos) {
		return ExtruderLevel.get(stack.getMetadata()).speed;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(I18n.format("tomsMod.tooltip.tier") + ": " + ExtruderLevel.get(stack.getMetadata()).level);
		tooltip.add(I18n.format("tomsMod.tooltip.speed") + ": " + ExtruderLevel.get(stack.getMetadata()).speed);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + ExtruderLevel.get(stack.getMetadata()).name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab))
			for (ExtruderLevel e : ExtruderLevel.VALUES) {
				subItems.add(e.getStack(1));
			}
	}

	public static enum ExtruderLevel {
		BASIC("basic", 2, 1), ADVANCED("advanced", 4, 1), ELITE("elite", 5, 2), ULTIMATE("ultimate", 10, 5);
		public static final ExtruderLevel[] VALUES = values();
		private final String name;
		private final int level, speed;
		private static Item item;

		private ExtruderLevel(String name, int level, int speedModifier) {
			this.name = name;
			this.level = level;
			this.speed = speedModifier;
		}

		public String getName() {
			return name;
		}

		public int getLevel() {
			return level;
		}

		public ItemStack getStack(int a) {
			return new ItemStack(item, a, ordinal());
		}

		public static ExtruderLevel get(int index) {
			return VALUES[MathHelper.abs(index % VALUES.length)];
		}

		public int getSpeed() {
			return speed;
		}
	}

	@Override
	public void registerModels() {
		CoreInit.registerRender(this, 0, "tomsmodfactory:extruder1");
		CoreInit.registerRender(this, 1, "tomsmodfactory:extruder2");
		CoreInit.registerRender(this, 2, "tomsmodfactory:extruder3");
		CoreInit.registerRender(this, 3, "tomsmodfactory:extruder4");
	}
}
