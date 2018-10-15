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
import com.tom.api.item.IControllerBoard;
import com.tom.core.CoreInit;
import com.tom.util.TomsModUtils;

public class ItemTowerBoard extends Item implements IModelRegisterRequired, IControllerBoard {
	public ItemTowerBoard() {
		setHasSubtypes(true);
	}

	@Override
	public void registerModels() {
		for (TowerBoardTypes t : TowerBoardTypes.VALUES) {
			CoreInit.registerRender(this, t.ordinal(), "tomsmodstorage:towerboard_" + t.getName());
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "_" + get(stack).getName();
	}

	public static enum TowerBoardTypes implements IStringSerializable {
		BASIC(new int[]{3}, new int[]{3}, new int[]{3}, 4, 128, 256, 2, 0),;
		public final int[] memoryTypes, processorTypes, chipsetTypes;
		public final int memorySlots, busSpeed, maxAutoCrafting;
		public final double maxPower, basePower;

		private TowerBoardTypes(int[] memryTypes, int[] processorTypes, int[] chipsetTypes, int memorySlots, int busSpeed, double maxPower, double basePower, int maxAutoCrafting) {
			this.memoryTypes = memryTypes;
			this.processorTypes = processorTypes;
			this.chipsetTypes = chipsetTypes;
			this.memorySlots = memorySlots;
			this.busSpeed = busSpeed;
			this.maxPower = maxPower;
			this.basePower = basePower;
			this.maxAutoCrafting = maxAutoCrafting;
		}

		public static final TowerBoardTypes[] VALUES = values();

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}

	private static TowerBoardTypes get(ItemStack s) {
		return TowerBoardTypes.VALUES[s.getMetadata() % TowerBoardTypes.VALUES.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		if (GuiScreen.isShiftKeyDown()) {
			tooltip.add(TomsModUtils.formatYesNoMessage("tomsMod.tooltip.board.canBeInTower", canBeInsertedIntoTower(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.board.processor", TomsModUtils.join(getCompatibleProcessorSlotTypes(stack))));
			tooltip.add(I18n.format("tomsMod.tooltip.board.memory", TomsModUtils.join(getCompatibleMemorySlotTypes(stack))));
			tooltip.add(I18n.format("tomsMod.tooltip.board.chipset", TomsModUtils.join(getCompatibleChipsetTypes(stack))));
			tooltip.add(I18n.format("tomsMod.tooltip.board.memorySlots", getMaxMemorySlots(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.board.bus", getBusSpeed(stack)));
			// tooltip.add(I18n.format("tomsMod.tooltip.board.network",
			// getNetworkBusBandwith(stack)));
			// tooltip.add(I18n.format("tomsMod.tooltip.board.tower",
			// getTowerBusBandwith(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.board.power", getBasePowerUsage(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.board.maxPower", getMaxPowerUsage(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.board.maxAutoCrafting", getMaxAutoCrafting(stack)));
		} else {
			tooltip.add(TextFormatting.ITALIC + I18n.format("tomsMod.tooltip.shiftToShow"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab))
			for (TowerBoardTypes t : TowerBoardTypes.VALUES) {
				subItems.add(new ItemStack(this, 1, t.ordinal()));
			}
	}

	@Override
	public int[] getCompatibleProcessorSlotTypes(ItemStack s) {
		return get(s).processorTypes;
	}

	@Override
	public int[] getCompatibleMemorySlotTypes(ItemStack s) {
		return get(s).memoryTypes;
	}

	@Override
	public int[] getCompatibleChipsetTypes(ItemStack s) {
		return get(s).chipsetTypes;
	}

	@Override
	public int getMaxMemorySlots(ItemStack s) {
		return get(s).memorySlots;
	}

	@Override
	public double getMaxPowerUsage(ItemStack s) {
		return get(s).maxPower;
	}

	@Override
	public double getBasePowerUsage(ItemStack s) {
		return get(s).basePower;
	}

	@Override
	public int getBusSpeed(ItemStack s) {
		return get(s).busSpeed;
	}

	@Override
	public boolean canBeInsertedIntoTower(ItemStack s) {
		return true;
	}

	@Override
	public int getNetworkBusBandwith(ItemStack s) {
		return 0;
	}

	@Override
	public int getTowerBusBandwith(ItemStack s) {
		return 0;
	}

	@Override
	public int getMaxAutoCrafting(ItemStack s) {
		return get(s).maxAutoCrafting;
	}
}