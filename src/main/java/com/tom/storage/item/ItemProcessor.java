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
import com.tom.api.item.IProcessor;
import com.tom.core.CoreInit;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.util.TomsModUtils;

public class ItemProcessor extends Item implements IModelRegisterRequired, IProcessor {
	public ItemProcessor() {
		setHasSubtypes(true);
	}

	@Override
	public void registerModels() {
		for (ProcessorTypes t : ProcessorTypes.VALUES) {
			CoreInit.registerRender(this, t.ordinal(), "tomsmodstorage:processor_" + t.getName());
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "_" + get(stack).getName();
	}

	public static enum ProcessorTypes implements IStringSerializable {
		BASIC(1, 10, 2048, 0.1, 0.04, 1, 0, 128, 0, 0, 0, 262144, 0.1),;
		public final int tier, maxMemory, cores, maxTowers, maxChannels, maxProcessintPower, maxAutoCrafting,
		maxOperations, maxStorage, maxAutoCraftingStorage;
		public final double power, powerPerOp, heat;
		public static final ProcessorTypes[] VALUES = values();

		private ProcessorTypes(int tier, int maxProcessintPower, int maxMemory, double power, double powerPerOp, int cores, int maxTowers, int maxChannels, int maxAutoCrafting, int maxOperations, int maxAutoCraftingStorage, int maxStorage, double heat) {
			this.tier = tier;
			this.maxMemory = maxMemory;
			this.cores = cores;
			this.maxTowers = maxTowers;
			this.maxChannels = maxChannels;
			this.power = power;
			this.powerPerOp = powerPerOp;
			this.maxProcessintPower = maxProcessintPower;
			this.maxAutoCrafting = maxAutoCrafting;
			this.maxOperations = maxOperations;
			this.maxAutoCraftingStorage = maxAutoCraftingStorage;
			this.maxStorage = maxStorage;
			this.heat = heat;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}

	@Override
	public int getMaxProcessingPower(ItemStack s) {
		return get(s).maxProcessintPower;
	}

	@Override
	public int getMaxMemory(ItemStack s) {
		return get(s).maxMemory;
	}

	@Override
	public double getPowerDrained(ItemStack s) {
		return get(s).power;
	}

	@Override
	public double getPowerDrainedPerOperation(ItemStack s) {
		return get(s).powerPerOp;
	}

	@Override
	public int getProcessorTier(ItemStack s) {
		return get(s).tier;
	}

	@Override
	public int getCoreCount(ItemStack s) {
		return get(s).cores;
	}

	@Override
	public int getMaxCompatibleTowers(ItemStack s) {
		return get(s).maxTowers;
	}

	@Override
	public int getMaxChannels(ItemStack s) {
		return get(s).maxChannels;
	}

	private static ProcessorTypes get(ItemStack s) {
		return ProcessorTypes.VALUES[s.getMetadata() % ProcessorTypes.VALUES.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		if (GuiScreen.isShiftKeyDown()) {
			tooltip.add(I18n.format("tomsMod.tooltip.processor.tier", getProcessorTier(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.processor.cores", getCoreCount(stack)));
			int pp = getMaxProcessingPower(stack), towers = getMaxCompatibleTowers(stack);
			tooltip.add(I18n.format("tomsMod.tooltip.processor.speed", (pp * 20), pp > 49 ? I18n.format("tomsMod.tooltip.processor.ghz") : I18n.format("tomsMod.tooltip.processor.mhz")));
			tooltip.add(I18n.format("tomsMod.tooltip.processor.memory", getMaxMemory(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.processor.maxChannels", getMaxChannels(stack)));
			double energy = getPowerDrained(stack) + getPowerDrainedPerOperation(stack) * (pp / 2);
			tooltip.add(I18n.format("tomsMod.tooltip.processor.energy", EnergyStorage.regulateValue(energy)));
			tooltip.add(I18n.format("tomsMod.tooltip.processor.maxTowers", towers));
			tooltip.add(TomsModUtils.formatYesNoMessage("tomsMod.tooltip.processor.canBeInTower", towers > 1));
			tooltip.add(I18n.format("tomsMod.tooltip.board.maxAutoCrafting", getMaxAutoCrafting(stack)));
		} else {
			tooltip.add(TextFormatting.ITALIC + I18n.format("tomsMod.tooltip.shiftToShow"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab))
			for (ProcessorTypes t : ProcessorTypes.VALUES) {
				subItems.add(new ItemStack(this, 1, t.ordinal()));
			}
	}

	@Override
	public int getMaxAutoCrafting(ItemStack s) {
		return get(s).maxAutoCrafting;
	}

	@Override
	public int getMaxAutoCraftingOperations(ItemStack s) {
		return get(s).maxOperations;
	}

	@Override
	public int getMaxAutoCraftingStorage(ItemStack s) {
		return get(s).maxAutoCraftingStorage;
	}

	@Override
	public int getMaxStorage(ItemStack s) {
		return get(s).maxStorage;
	}

	@Override
	public double getHeatProduction(ItemStack s) {
		return get(s).heat;
	}
}
