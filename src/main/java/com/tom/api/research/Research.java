package com.tom.api.research;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import com.tom.api.research.IScanningInformation.ScanningInformation;
import com.tom.apis.TomsModUtils;

public class Research extends IForgeRegistryEntry.Impl<Research>{
	private final String name;
	private final ItemStack icon;
	private ResearchComplexity comp = ResearchComplexity.BASIC;
	private List<IScanningInformation> requiredScans = null;
	private List<Research> parents = null;
	private int researchTime = 100;
	private List<ItemStack> requiredItems = null;
	private String modname;
	private double energyRequired = 1;
	public String prefix = "";
	public Research(String name, ItemStack icon) {
		this.name = name;
		this.icon = icon;
	}
	public String getUnlocalizedName() {
		return "research."+prefix+name+".name";
	}
	public String getName(){
		return name;
	}
	public String getDescription() {
		return "research."+prefix+name+".desc";
	}

	public ItemStack getIcon() {
		return ItemStack.copyItemStack(icon);
	}
	public ResearchComplexity getComplexity() {
		return comp;
	}

	public List<ItemStack> getResearchRequirements() {
		return TomsModUtils.copyItemStackList(requiredItems);
	}

	public double getEnergyRequired() {
		return energyRequired;
	}

	public List<Research> getParents() {
		return parents;
	}

	public List<IScanningInformation> getRequiredScans() {
		return requiredScans;
	}

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
	public Research setParents(List<Research> parents) {
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
	public Research addParent(Research research){
		if (parents == null) {
			parents = new ArrayList<Research>();
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
	public boolean isValid() {
		return modname != null ? Loader.isModLoaded(modname) : true;
	}
	public Research setMod(String modid){
		this.modname = modid;
		return this;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public Research setEnergyRequired(double energyRequired) {
		this.energyRequired = energyRequired;
		return this;
	}
}
