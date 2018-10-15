package com.tom.storage.item;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;

public class ItemChipset extends Item implements IModelRegisterRequired {
	public ItemChipset() {
		setHasSubtypes(true);
	}

	@Override
	public void registerModels() {
		for (ChipsetTypes t : ChipsetTypes.VALUES) {
			CoreInit.registerRender(this, t.ordinal(), "tomsmodstorage:chipset_" + t.getName());
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "_" + get(stack).getName();
	}

	public static enum ChipsetTypes implements IStringSerializable {
		BASIC(1),;
		public final int tier;
		public static final ChipsetTypes[] VALUES = values();

		private ChipsetTypes(int tier) {
			this.tier = tier;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}

	private static ChipsetTypes get(ItemStack s) {
		return ChipsetTypes.VALUES[s.getMetadata() % ChipsetTypes.VALUES.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		if (GuiScreen.isShiftKeyDown()) {
			tooltip.add(I18n.format("tomsMod.tooltip.tier") + ": " + get(stack).tier);
		} else {
			tooltip.add(TextFormatting.ITALIC + I18n.format("tomsMod.tooltip.shiftToShow"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab))for (ChipsetTypes t : ChipsetTypes.VALUES) {
			subItems.add(new ItemStack(this, 1, t.ordinal()));
		}
	}
}
