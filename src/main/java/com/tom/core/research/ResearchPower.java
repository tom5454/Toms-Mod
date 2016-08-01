package com.tom.core.research;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.tom.api.research.IResearch;
import com.tom.api.research.IResearch.ResearchBaseBasic;
import com.tom.api.research.IScanningInformation;
import com.tom.api.research.IScanningInformation.ScanningInformation;
import com.tom.apis.TomsModUtils;
import com.tom.recipes.AdvancedCraftingRecipes;

public class ResearchPower extends ResearchBaseBasic {

	@Override
	public String getName() {
		return "tomsmod.research.power";
	}

	@Override
	public ItemStack getIcon() {
		//return new ResourceLocation("textures/items/redstone_dust");
		return new ItemStack(Items.REDSTONE);
	}

	@Override
	public List<ItemStack> getResearchRequirements() {
		return TomsModUtils.getItemStackList(new ItemStack(Items.REDSTONE,4));
	}

	@Override
	public int getEnergyRequired() {
		return 0;
	}

	@Override
	public List<IResearch> getParents() {
		return null;
	}

	@Override
	public List<IScanningInformation> getRequiredScans() {
		return AdvancedCraftingRecipes.getScanningList(new ScanningInformation(Blocks.REDSTONE_WIRE,0),new ScanningInformation(Blocks.REDSTONE_WIRE,15));
	}

	@Override
	public int getResearchTime() {
		return 500;
	}
	@Override
	public boolean isValid() {
		return true;
	}

}
