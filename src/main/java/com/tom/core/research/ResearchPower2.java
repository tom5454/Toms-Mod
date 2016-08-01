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
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.core.research.handler.ResearchHandler;
import com.tom.recipes.AdvancedCraftingRecipes;

public class ResearchPower2 extends ResearchBaseBasic {

	@Override
	public String getName() {
		return "tomsmod.research.power2";
	}

	@Override
	public ItemStack getIcon() {
		//return new ResourceLocation("textures/items/redstone_dust");
		return new ItemStack(Items.REDSTONE);
	}

	@Override
	public List<ItemStack> getResearchRequirements() {
		return TomsModUtils.getItemStackList(new ItemStack(Items.REDSTONE),TMResource.ZINC.getStackNormal(Type.PLATE, 12), TMResource.COPPER.getStackNormal(Type.PLATE, 12), CraftingMaterial.ACID_PAPER.getStackNormal(12));
	}

	@Override
	public int getEnergyRequired() {
		return 0;
	}

	@Override
	public List<IResearch> getParents() {
		return AdvancedCraftingRecipes.getResearchList(ResearchHandler.getResearchByID(0),ResearchHandler.getResearchByID(1));
	}

	@Override
	public List<IScanningInformation> getRequiredScans() {
		return AdvancedCraftingRecipes.getScanningList(new ScanningInformation(CoreInit.oreCopper,0),new ScanningInformation(CoreInit.oreZinc,0),new ScanningInformation(Blocks.REDSTONE_ORE,0),new ScanningInformation(Blocks.IRON_ORE,0),new ScanningInformation(Blocks.LIT_REDSTONE_ORE,0));
	}

	@Override
	public int getResearchTime() {
		return 1000;
	}
	@Override
	public boolean isValid() {
		return true;
	}
}
