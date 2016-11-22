package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.oredict.OreDictionary;

import com.tom.api.energy.EnergyStorage;
import com.tom.apis.TomsModUtils;
import com.tom.core.research.ResearchHandler;
import com.tom.factory.block.SteamSolderingStation;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.recipes.handler.AdvancedCraftingHandler.ReturnData;

import com.tom.core.tileentity.TileEntityResearchTable;

public class TileEntitySolderingStation extends TileEntityMachineBase {
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	private static final int[] SLOTS = new int[]{9, 11};
	public int maxProgress = 0;
	private ItemStack output = null;
	public int craftingError = 0;
	private int craftingErrorShowTimer = 0;
	private int solderingAlloyLevel = 0;
	public int clientEnergy;
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 11;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 9;
	}

	@Override
	public int getSizeInventory() {
		return 13;
	}
	@Override
	public int getField(int id) {
		return id == 0 ? progress : id == 1 ? maxProgress : id == 2 ? solderingAlloyLevel : id == 3 ? clientEnergy : 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)progress = value;
		else if(id == 1)maxProgress = value;
		else if(id == 2)solderingAlloyLevel = value;
		else if(id == 3)clientEnergy = value;
	}
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public String getName() {
		return "solderingStation";
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return 12;
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return 2;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.progress = tag.getInteger("progress");
		this.maxProgress = tag.getInteger("maxProgress");
		NBTTagCompound tagC = tag.getCompoundTag("crafting");
		this.output = ItemStack.loadItemStackFromNBT(tagC.getCompoundTag("out"));
		this.solderingAlloyLevel = tag.getInteger("solderingAlloy");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("progress", progress);
		tag.setInteger("maxProgress", maxProgress);
		NBTTagCompound tagC = new NBTTagCompound();
		tagC.setTag("out", this.output != null ? this.output.writeToNBT(new NBTTagCompound()) : new NBTTagCompound());
		tag.setTag("crafting", tagC);
		tag.setInteger("solderingAlloy", solderingAlloyLevel);
		return tag;
	}
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			clientEnergy = MathHelper.floor_double(energy.getEnergyStored());
			if(this.craftingErrorShowTimer > 0)if(this.craftingErrorShowTimer-- == 1 && (craftingError == 2 || craftingError == 3))craftingError = 0;
			if(energy.getEnergyStored() > 20 && canRun()){
				if(solderingAlloyLevel > 0){
					if(progress > 0){
						updateProgress();
					}else if(progress == 0){
						this.craftingError = 0;
						if(output != null){
							if(stack[9] == null){
								stack[9] = output.copy();
								output = null;
							}else{
								if(stack[9].isItemEqual(this.output) && ItemStack.areItemStackTagsEqual(output, stack[9]) && stack[9].stackSize + this.output.stackSize <= Math.min(getInventoryStackLimit(), this.output.getMaxStackSize())){
									stack[9].stackSize += this.output.stackSize;
									output = null;
								}else{
									this.craftingError = 1;
								}
							}
						}
						if(output == null){
							maxProgress = 0;
							progress = -1;
						}
					}else{
						if(this.progress < 1 && this.hasItemsInCrafting() && stack[10] != null){
							ResearchHandler h = TileEntityResearchTable.getResearchHandler(stack[10]);
							if(h != null){
								ReturnData data = AdvancedCraftingHandler.craft(new ItemStack[]{stack[0],stack[1],
										stack[2],stack[3],stack[4],stack[5],stack[6],stack[7],stack[8]},
										h.getResearchesCompleted(), CraftingLevel.E_SOLDERING_STATION, worldObj);
								if(data != null){
									if(data.hasAllResearches()){
										if(data.isRightLevel()){
											this.output = data.getReturnStack();
											this.progress = data.getTime() * 10;
											this.maxProgress = data.getTime() * 10;
											this.craftStart();
										}else{
											craftingError = 3;
											craftingErrorShowTimer = 50;
										}
									}else{
										craftingError = 2;
										craftingErrorShowTimer = 50;
									}
								}
							}
						}
						TomsModUtils.setBlockStateWithCondition(worldObj, pos, SteamSolderingStation.ACTIVE, progress > 0);
					}
				}
				if(solderingAlloyLevel == 0){
					if(stack[11] != null){
						int[] ids = OreDictionary.getOreIDs(stack[11]);
						int id = OreDictionary.getOreID("ingotSolderingAlloy");
						boolean f = false;
						for(int i : ids){
							if(i == id){
								f = true;
								break;
							}
						}
						if(f){
							decrStackSize(11, 1);
							solderingAlloyLevel += 1000;
							energy.extractEnergy(10, false);
						}
					}
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, SteamSolderingStation.ACTIVE, false);
			}
		}
	}
	public boolean hasItemsInCrafting(){
		return stack[0] != null || stack[1] != null || stack[2] != null || stack[3] != null ||
				stack[4] != null || stack[5] != null || stack[6] != null || stack[7] != null ||
				stack[8] != null;
	}
	private void craftStart() {
		for(int i = 0;i<9;i++){
			this.decrStackSize(i, 1);
		}
	}

	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}
	private void updateProgress(){
		int upgradeC = getSpeedUpgradeCount();
		int p = upgradeC + MathHelper.floor_double(10 * (getMaxProcessTimeNormal() / TYPE_MULTIPLIER_SPEED[getType()])) + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(1.2D * p, false);
		solderingAlloyLevel -= p;
	}
	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodfactory:textures/blocks/solderingStation.png");
	}

	@Override
	public int[] getOutputSlots() {
		return new int[]{9};
	}

	@Override
	public int[] getInputSlots() {
		return new int[]{11};
	}
}
