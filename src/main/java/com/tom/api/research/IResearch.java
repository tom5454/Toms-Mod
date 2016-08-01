package com.tom.api.research;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IResearch {
	String getName();
	String getDiscription();
	ItemStack getIcon();
	ResearchComplexity getComplexity();
	List<ItemStack> getResearchRequirements();
	int getEnergyRequired();
	List<IResearch> getParents();
	List<IScanningInformation> getRequiredScans();
	int getResearchTime();
	boolean isValid();
	public static abstract class ResearchBaseBasic implements IResearch{
		@Override
		public ResearchComplexity getComplexity(){
			return ResearchComplexity.BASIC;
		}
		@Override
		public String getDiscription() {
			return getName()+".desc";
		}
	}
}
