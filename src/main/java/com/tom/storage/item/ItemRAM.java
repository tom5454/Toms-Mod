package com.tom.storage.item;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.item.IMemoryItem;
import com.tom.core.CoreInit;

public class ItemRAM extends Item implements IModelRegisterRequired, IMemoryItem {
	public ItemRAM() {
		setHasSubtypes(true);
	}

	@Override
	public void registerModels() {
		for (RamTypes t : RamTypes.VALUES) {
			CoreInit.registerRender(this, t.ordinal(), "tomsmodstorage:ram_" + t.getName());
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "_" + get(stack).getName();
	}

	public static enum RamTypes implements IStringSerializable {
		BASIC(1, 1024),;
		public final int memory, tier;
		public static final RamTypes[] VALUES = values();

		private RamTypes(int tier, int memory) {
			this.memory = memory;
			this.tier = tier;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}

	private static RamTypes get(ItemStack s) {
		return RamTypes.VALUES[s.getMetadata() % RamTypes.VALUES.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if (GuiScreen.isShiftKeyDown()) {
			tooltip.add(I18n.format("tomsMod.tooltip.memory.tier", get(stack).tier));
			tooltip.add(I18n.format("tomsMod.tooltip.memory.memory", get(stack).memory));
		} else {
			tooltip.add(TextFormatting.ITALIC + I18n.format("tomsMod.tooltip.shiftToShow"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (RamTypes t : RamTypes.VALUES) {
			subItems.add(new ItemStack(this, 1, t.ordinal()));
		}
	}

	@Override
	public int getMemory(ItemStack s) {
		return get(s).memory;
	}

	@Override
	public int getTier(ItemStack s) {
		return get(s).tier;
	}
}
