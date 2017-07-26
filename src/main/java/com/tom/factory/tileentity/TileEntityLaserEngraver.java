package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.tom.api.energy.EnergyStorage;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntityLaserEngraver extends TileEntityMachineBase {
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	public int clientEnergy = 0;
	private int maxProgress = 0;

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 || index == 4;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}

	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public String getName() {
		return "laserEngraver";
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return 3;
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return 2;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.progress = compound.getInteger("progress");
		this.maxProgress = compound.getInteger("maxProgress");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("progress", progress);
		compound.setInteger("maxProgress", maxProgress);
		return compound;
	}

	public int getClientEnergyStored() {
		return MathHelper.ceil(energy.getEnergyStored());
	}

	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public void updateProgress() {
		int upgradeC = getSpeedUpgradeCount();
		int p = upgradeC + MathHelper.floor(10 * (getMaxProcessTimeNormal() / TYPE_MULTIPLIER_SPEED[getType()])) + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(0.1D * p, false);
	}

	/*private ItemStack getRecipe(){
		int lvl = 2 - getType();
		if(!inv.getStackInSlot(2).isEmpty() && CraftingMaterial.equals(inv.getStackInSlot(2).getItem())){
			if(CraftingMaterial.BLUEPRINT_BASIC_CHIPSET.equals(inv.getStackInSlot(2)) && OreDict.isOre(inv.getStackInSlot(4), "dustRedstone") && CraftingMaterial.SILICON_PLATE.equals(inv.getStackInSlot(0))){
				return CraftingMaterial.BASIC_CHIPSET.getStackNormal();
			}else if(CraftingMaterial.BLUEPRINT_ADVANCED_CHIPSET.equals(inv.getStackInSlot(2)) && OreDict.isOre(inv.getStackInSlot(4), "dustGold") && CraftingMaterial.SILICON_PLATE.equals(inv.getStackInSlot(0)) && inv.getStackInSlot(0).getCount() >= 3){
				return lvl > 0 ? CraftingMaterial.ADVANCED_CHIPSET.getStackNormal(3) : ItemStack.EMPTY;
			}else if(CraftingMaterial.BLUEPRINT_FLUIX_CHIPSET.equals(inv.getStackInSlot(2)) && OreDict.isOre(inv.getStackInSlot(4), "dustFluix") && CraftingMaterial.SILICON_PLATE.equals(inv.getStackInSlot(0)) && inv.getStackInSlot(0).getCount() >= 5){
				return lvl > 0 ? CraftingMaterial.FLUIX_CHIPSET.getStackNormal(5) : ItemStack.EMPTY;
			}else if(CraftingMaterial.BLUEPRINT_QUANTUM_CHIPSET.equals(inv.getStackInSlot(2)) && OreDict.isOre(inv.getStackInSlot(4), "dustPlatinum") && CraftingMaterial.SILICON_PLATE.equals(inv.getStackInSlot(0)) && inv.getStackInSlot(0).getCount() >= 8){
				return lvl > 1 ? CraftingMaterial.QUANTUM_CHIPSET.getStackNormal(8) : ItemStack.EMPTY;
			}else if(CraftingMaterial.BLUEPRINT_LOGIC_PROCESSOR.equals(inv.getStackInSlot(2)) && OreDict.isOre(inv.getStackInSlot(4), "nuggetGold") && CraftingMaterial.SILICON_PLATE.equals(inv.getStackInSlot(0)) && inv.getStackInSlot(0).getCount() >= 1 && inv.getStackInSlot(4).getCount() >= 2){
				return lvl > 1 ? CraftingMaterial.LOGIC_PROCESSOR.getStackNormal(4) : ItemStack.EMPTY;
			}
		}
		return ItemStack.EMPTY;
	}
	private int getTime(){
		if(!inv.getStackInSlot(2).isEmpty() && CraftingMaterial.equals(inv.getStackInSlot(2).getItem())){
			if(CraftingMaterial.BLUEPRINT_BASIC_CHIPSET.equals(inv.getStackInSlot(2)) && OreDict.isOre(inv.getStackInSlot(4), "dustRedstone") && CraftingMaterial.SILICON_PLATE.equals(inv.getStackInSlot(0))){
				return 600;
			}else if(CraftingMaterial.BLUEPRINT_ADVANCED_CHIPSET.equals(inv.getStackInSlot(2)) && OreDict.isOre(inv.getStackInSlot(4), "dustGold") && CraftingMaterial.SILICON_PLATE.equals(inv.getStackInSlot(0)) && inv.getStackInSlot(0).getCount() >= 3){
				return 1200;
			}else if(CraftingMaterial.BLUEPRINT_FLUIX_CHIPSET.equals(inv.getStackInSlot(2)) && OreDict.isOre(inv.getStackInSlot(4), "dustFluix") && CraftingMaterial.SILICON_PLATE.equals(inv.getStackInSlot(0)) && inv.getStackInSlot(0).getCount() >= 5){
				return 1700;
			}else if(CraftingMaterial.BLUEPRINT_QUANTUM_CHIPSET.equals(inv.getStackInSlot(2)) && OreDict.isOre(inv.getStackInSlot(4), "dustPlatinum") && CraftingMaterial.SILICON_PLATE.equals(inv.getStackInSlot(0)) && inv.getStackInSlot(0).getCount() >= 8){
				return 2200;
			}else if(CraftingMaterial.BLUEPRINT_LOGIC_PROCESSOR.equals(inv.getStackInSlot(2)) && OreDict.isOre(inv.getStackInSlot(4), "nuggetGold") && CraftingMaterial.SILICON_PLATE.equals(inv.getStackInSlot(0)) && inv.getStackInSlot(0).getCount() >= 1 && inv.getStackInSlot(4).getCount() >= 2){
				return 600;
			}
		}
		return 0;
	}*/
	@Override
	public int getField(int id) {
		return id == 1 ? maxProgress : super.getField(id);
	}

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodfactory:textures/blocks/laserEngraver.png");
	}

	@Override
	public int[] getOutputSlots() {
		return new int[]{1};
	}

	@Override
	public int[] getInputSlots() {
		return new int[]{0, 4};
	}

	@Override
	public void checkItems() {
		int lvl = 2 - getType();
		ItemStackChecker s = MachineCraftingHandler.getLaserEngraverOutput(inv.getStackInSlot(0), inv.getStackInSlot(4), inv.getStackInSlot(2));
		if (s != null && s.getExtra4() <= lvl) {
			checkItems(s, 1, maxProgress = s.getExtra3(), 0, 4);
			setOut(0, s);
		}
	}

	@Override
	public void finish() {
		addItemsAndSetProgress(getOutput(0), 1, 0, 4);
	}
}
