package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.research.Research;
import com.tom.core.CoreInit;

import mezz.jei.api.ingredients.IIngredientRenderer;

public class ResearchRenderer implements IIngredientRenderer<Research> {
	public IIngredientRenderer<ItemStack> renderer;
	private ItemStack STACK = new ItemStack(CoreInit.blueprint);

	@Override
	public void render(Minecraft minecraft, int xPosition, int yPosition, Research ingredient) {
		if(renderer != null){
			renderer.render(minecraft, xPosition, yPosition, STACK);
		}
	}
	@Override
	public List<String> getTooltip(Minecraft minecraft, Research ingredient, ITooltipFlag tooltipFlag) {
		List<String> t = new ArrayList<>();
		t.add(I18n.format(ingredient.getUnlocalizedName()));
		if(tooltipFlag == ITooltipFlag.TooltipFlags.ADVANCED)t.add(TextFormatting.GRAY + ingredient.prefix + ingredient.getName());
		return t;
	}
}
