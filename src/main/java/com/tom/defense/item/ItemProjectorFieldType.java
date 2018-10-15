package com.tom.defense.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.item.ICustomCraftingHandler;
import com.tom.core.CoreInit;
import com.tom.util.TomsModUtils;

public class ItemProjectorFieldType extends Item implements ICustomCraftingHandler, IModelRegisterRequired {
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab))
			for (int i = 0;i < FieldType.VALUES.length;i++)
				subItems.add(new ItemStack(this, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + FieldType.get(stack.getItemDamage()).getName();
	}

	public static enum FieldType {
		CUBE("cube", 6), CONTAINMENT("containment", 6), WALL("wall", 1), TUBE("tube", 4),;
		private final String name;
		private final int walls;
		public static final FieldType[] VALUES = values();

		private FieldType(String name, int walls) {
			this.name = name;
			this.walls = walls;
		}

		public String getName() {
			return name;
		}

		public static FieldType get(int index) {
			return VALUES[index % VALUES.length];
		}

		public int getWalls() {
			return walls;
		}

		public boolean is2DOnly() {
			return this == WALL;
		}

		/** {wallID; wallMode:[0: invalid, 1: onlyEffect, 2: valid]} */
		public int[] getWall(BlockPos pos, BlockPos posMin, BlockPos posMax, int extraNBTData) {
			if (this == WALL)
				return new int[]{1, 2};
			int xStart = posMin.getX(), yStart = posMin.getY(), zStart = posMin.getZ();
			int xStop = posMax.getX(), yStop = posMax.getY(), zStop = posMax.getZ();
			int xC = pos.getX(), yC = pos.getY(), zC = pos.getZ();
			if (this == TUBE) {
				int f = TomsModUtils.getFirstTrue(xC == xStart, xC == xStop, yC == yStart, yC == yStop, zC == zStart, zC == zStop);
				if (f > -1 && ((extraNBTData == 0 && !(xC == xStart || xC == xStop)) || (extraNBTData == 1 && !(yC == yStart || yC == yStop)) || (extraNBTData == 2 && !(zC == zStart || zC == zStop)))) {
					int u = f + 1;
					if (extraNBTData == 0) {
						u += 2;
					} else if (extraNBTData == 1) {
						if (u > 2)
							u -= 2;
					}
					return new int[]{u, 2};
				} else
					return new int[]{0, 0};
			}
			int f = TomsModUtils.getFirstTrue(xC == xStart, xC == xStop, yC == yStart, yC == yStop, zC == zStart, zC == zStop);
			if (f > -1)
				return new int[]{f + 1, 2};
			else if (this == CONTAINMENT)
				return new int[]{0, 1};
			return new int[]{0, 0};
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
		if (FieldType.get(stack.getItemDamage()) == FieldType.TUBE) {
			int currentS = (stack.getTagCompound() != null ? stack.getTagCompound().getInteger("extra") : 0) % 3;
			if (currentS == 0)
				tooltip.add(I18n.format("tomsMod.tooltip.rotation", "X"));
			else if (currentS == 1)
				tooltip.add(I18n.format("tomsMod.tooltip.rotation", "Y"));
			else if (currentS == 2)
				tooltip.add(I18n.format("tomsMod.tooltip.rotation", "Z"));
			tooltip.add(I18n.format("tomsMod.tooltip.rotationTip"));
		}
	}

	@Override
	public void onCrafing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory) {
		for (int i = 0;i < crafingTableInventory.getSizeInventory();i++) {
			ItemStack stack = crafingTableInventory.getStackInSlot(i);
			if (stack != null && stack.getItem() == this && FieldType.get(stack.getItemDamage()) == FieldType.TUBE) {
				int currentS = stack.getTagCompound() != null ? stack.getTagCompound().getInteger("extra") : 0;
				if (returnStack.getTagCompound() == null)
					returnStack.setTagCompound(new NBTTagCompound());
				returnStack.getTagCompound().setInteger("extra", (currentS + 1) % 3);
				break;
			}
		}
	}

	@Override
	public void onUsing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory, ItemStack stack) {

	}

	@Override
	public void registerModels() {
		CoreInit.registerRender(this, 0, "tomsmoddefense:projectorModule_" + FieldType.get(0).getName());
		CoreInit.registerRender(this, 1, "tomsmoddefense:projectorModule_" + FieldType.get(1).getName());
		CoreInit.registerRender(this, 2, "tomsmoddefense:projectorModule_" + FieldType.get(2).getName());
		CoreInit.registerRender(this, 3, "tomsmoddefense:projectorModule_" + FieldType.get(3).getName());
	}
}
