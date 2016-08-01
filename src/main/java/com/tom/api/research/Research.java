package com.tom.api.research;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.Loader;

import com.tom.api.research.IScanningInformation.ScanningInformation;
import com.tom.apis.TomsModUtils;

public class Research implements IResearch{
	private final String name;
	private final ItemStack icon;
	private ResearchComplexity comp = ResearchComplexity.BASIC;
	private List<IScanningInformation> requiredScans = null;
	private List<IResearch> parents = null;
	private int researchTime = 100;
	private List<ItemStack> requiredItems = null;
	private String modname;
	public Research(String name, ItemStack icon) {
		this.name = name;
		this.icon = icon;
	}
	@Override
	public String getName() {
		return "tomsmod.research."+name;
	}

	@Override
	public String getDiscription() {
		return "tomsmod.research."+name+".desc";
	}

	@Override
	public ItemStack getIcon() {
		return ItemStack.copyItemStack(icon);
	}

	@Override
	public ResearchComplexity getComplexity() {
		return comp;
	}

	@Override
	public List<ItemStack> getResearchRequirements() {
		return TomsModUtils.copyItemStackList(requiredItems);
	}

	@Override
	public int getEnergyRequired() {
		return 0;
	}

	@Override
	public List<IResearch> getParents() {
		return parents;
	}

	@Override
	public List<IScanningInformation> getRequiredScans() {
		return requiredScans;
	}

	@Override
	public int getResearchTime() {
		return researchTime;
	}
	public Research setComplexity(ResearchComplexity comp) {
		this.comp = comp;
		return this;
	}
	public Research setRequiredScans(List<IScanningInformation> requiredScans) {
		this.requiredScans = requiredScans;
		return this;
	}
	public Research setParents(List<IResearch> parents) {
		this.parents = parents;
		return this;
	}
	public Research setResearchTime(int researchTime) {
		this.researchTime = researchTime;
		return this;
	}
	public Research setRequiredItems(List<ItemStack> requiredItems) {
		this.requiredItems = requiredItems;
		return this;
	}
	public Research addRequiredScan(Block block, int meta){
		if (requiredScans == null) {
			requiredScans = new ArrayList<IScanningInformation>();
		}
		requiredScans.add(new ScanningInformation(block, meta));
		return this;
	}
	public Research addParent(IResearch research){
		if (parents == null) {
			parents = new ArrayList<IResearch>();
		}
		parents.add(research);
		return this;
	}
	public Research addRequiredItem(ItemStack stack){
		if (requiredItems == null) {
			requiredItems = new ArrayList<ItemStack>();
		}
		requiredItems.add(stack);
		return this;
	}
	@Override
	public boolean isValid() {
		return modname != null ? Loader.isModLoaded(modname) : true;
	}
	public Research setMod(String modid){
		this.modname = modid;
		return this;
	}
}
