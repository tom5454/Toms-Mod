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
import com.tom.api.item.IControllerBoard;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;

public class ItemBoard extends Item implements IModelRegisterRequired, IControllerBoard {
	public ItemBoard() {
		setHasSubtypes(true);
	}

	@Override
	public void registerModels() {
		for (BoardTypes t : BoardTypes.VALUES) {
			CoreInit.registerRender(this, t.ordinal(), "tomsmodstorage:board_" + t.getName());
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "_" + get(stack).getName();
	}

	public static enum BoardTypes implements IStringSerializable {
		BASIC(new int[]{1}, new int[]{1}, new int[]{1}, 4, 32, 64, 1, 256, 0, 0),;
		public final int[] memoryTypes, processorTypes, chipsetTypes;
		public final int memorySlots, busSpeed, networkBandwith, towerBandwith, maxAutoCrafting;
		public final double maxPower, basePower;
		public static final BoardTypes[] VALUES = values();

		private BoardTypes(int[] memryTypes, int[] processorTypes, int[] chipsetTypes, int memorySlots, int busSpeed, double maxPower, double basePower, int networkBandwith, int towerBandwith, int maxAutoCrafting) {
			this.memoryTypes = memryTypes;
			this.processorTypes = processorTypes;
			this.chipsetTypes = chipsetTypes;
			this.memorySlots = memorySlots;
			this.busSpeed = busSpeed;
			this.maxPower = maxPower;
			this.basePower = basePower;
			this.networkBandwith = networkBandwith;
			this.towerBandwith = towerBandwith;
			this.maxAutoCrafting = maxAutoCrafting;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}

	private static BoardTypes get(ItemStack s) {
		return BoardTypes.VALUES[s.getMetadata() % BoardTypes.VALUES.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if (GuiScreen.isShiftKeyDown()) {
			tooltip.add(TomsModUtils.formatYesNoMessage("tomsMod.tooltip.board.canBeInTower", canBeInsertedIntoTower(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.board.processor", TomsModUtils.join(getCompatibleProcessorSlotTypes(stack))));
			tooltip.add(I18n.format("tomsMod.tooltip.board.memory", TomsModUtils.join(getCompatibleMemorySlotTypes(stack))));
			tooltip.add(I18n.format("tomsMod.tooltip.board.chipset", TomsModUtils.join(getCompatibleChipsetTypes(stack))));
			tooltip.add(I18n.format("tomsMod.tooltip.board.memorySlots", getMaxMemorySlots(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.board.bus", getBusSpeed(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.board.network", getNetworkBusBandwith(stack)));
			int tower = getTowerBusBandwith(stack);
			tooltip.add(I18n.format("tomsMod.tooltip.board.tower", tower));
			tooltip.add(I18n.format("tomsMod.tooltip.board.power", getBasePowerUsage(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.board.maxPower", getMaxPowerUsage(stack)));
			tooltip.add(I18n.format("tomsMod.tooltip.board.maxAutoCrafting", getMaxAutoCrafting(stack)));
			tooltip.add(TomsModUtils.formatYesNoMessage("tomsMod.tooltip.board.canCommunicateWithTowers", tower > 0));
		} else {
			tooltip.add(TextFormatting.ITALIC + I18n.format("tomsMod.tooltip.shiftToShow"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (BoardTypes t : BoardTypes.VALUES) {
			subItems.add(new ItemStack(this, 1, t.ordinal()));
		}
	}

	@Override
	public boolean canBeInsertedIntoTower(ItemStack s) {
		return false;
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
	public int getNetworkBusBandwith(ItemStack s) {
		return get(s).networkBandwith;
	}

	@Override
	public int getTowerBusBandwith(ItemStack s) {
		return get(s).towerBandwith;
	}

	@Override
	public int getMaxAutoCrafting(ItemStack s) {
		return get(s).maxAutoCrafting;
	}
}
