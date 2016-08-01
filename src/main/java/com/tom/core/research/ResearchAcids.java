package com.tom.core.research;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.tom.api.research.IResearch;
import com.tom.api.research.IResearch.ResearchBaseBasic;
import com.tom.api.research.IScanningInformation;
import com.tom.apis.TomsModUtils;
import com.tom.core.TMResource;
import com.tom.core.TMResource.Type;

public class ResearchAcids extends ResearchBaseBasic {

	@Override
	public String getName() {
		return "tomsmod.research.acids";
	}

	@Override
	public ItemStack getIcon() {
		//return new ResourceLocation("textures/items/dustSulfur");
		return TMResource.SULFUR.getStackNormal(Type.DUST);
	}

	@Override
	public List<ItemStack> getResearchRequirements() {
		return TomsModUtils.getItemStackList(TMResource.SULFUR.getStackNormal(Type.DUST,6), new ItemStack(Items.IRON_INGOT,2), TMResource.COPPER.getStackNormal(Type.INGOT,2),TMResource.TIN.getStackNormal(Type.INGOT,2));
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
		return null;
	}

	@Override
	public int getResearchTime() {
		return 200;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
