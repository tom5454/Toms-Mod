package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.oredict.OreDictionary;

import com.tom.api.energy.EnergyStorage;
import com.tom.core.research.ResearchHandler;
import com.tom.factory.block.SteamSolderingStation;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.recipes.handler.AdvancedCraftingHandler.ReturnData;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.TileEntityResearchTable;

public class TileEntitySolderingStation extends TileEntityMachineBase {
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11};
	public int maxProgress = 0;
	private ItemStack output = ItemStack.EMPTY;
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
		return index == 11 || (index < 9 && getStackInSlot(index).isItemEqual(itemStackIn));
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 9;
	}

	@Override
	public int getSizeInventory() {
		return 14;
	}

	@Override
	public int getField(int id) {
		return id == 0 ? progress : id == 1 ? maxProgress : id == 2 ? solderingAlloyLevel : id == 3 ? clientEnergy : 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0)
			progress = value;
		else if (id == 1)
			maxProgress = value;
		else if (id == 2)
			solderingAlloyLevel = value;
		else if (id == 3)
			clientEnergy = value;
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
		this.output = TomsModUtils.loadItemStackFromNBT(tagC.getCompoundTag("out"));
		this.solderingAlloyLevel = tag.getInteger("solderingAlloy");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("progress", progress);
		tag.setInteger("maxProgress", maxProgress);
		NBTTagCompound tagC = new NBTTagCompound();
		tagC.setTag("out", this.output.writeToNBT(new NBTTagCompound()));
		tag.setTag("crafting", tagC);
		tag.setInteger("solderingAlloy", solderingAlloyLevel);
		return tag;
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			clientEnergy = MathHelper.floor(energy.getEnergyStored());
			if (this.craftingErrorShowTimer > 0)
				if (this.craftingErrorShowTimer-- == 1 && (craftingError == 2 || craftingError == 3))
					craftingError = 0;
			if (energy.getEnergyStored() > 20 && canRun()) {
				if (solderingAlloyLevel > 0) {
					if (progress > 0) {
						updateProgress();
					} else if (progress == 0) {
						this.craftingError = 0;
						if (!output.isEmpty()) {
							if (inv.getStackInSlot(9).isEmpty()) {
								inv.setInventorySlotContents(9, output.copy());
								output = ItemStack.EMPTY;
								inv.setInventorySlotContents(13, ItemStack.EMPTY);
							} else {
								if (inv.getStackInSlot(9).isItemEqual(this.output) && ItemStack.areItemStackTagsEqual(output, inv.getStackInSlot(9)) && inv.getStackInSlot(9).getCount() + this.output.getCount() <= Math.min(getInventoryStackLimit(), this.output.getMaxStackSize())) {
									inv.getStackInSlot(9).grow(this.output.getCount());
									output = ItemStack.EMPTY;
									inv.setInventorySlotContents(13, ItemStack.EMPTY);
								} else {
									this.craftingError = 1;
								}
							}
						}
						if (output.isEmpty()) {
							maxProgress = 0;
							progress = -1;
						}
					} else {
						if (this.progress < 1 && this.hasItemsInCrafting() && !inv.getStackInSlot(10).isEmpty()) {
							ReturnData data = checkRecipe();
							if (data != null) {
								if (data.hasAllResearches()) {
									if (checkStacks()) {
										tryCraft(data);
									} else
										inv.setInventorySlotContents(13, data.getReturnStack());
								} else {
									craftingError = 2;
									craftingErrorShowTimer = 50;
								}
							}
						}
						TomsModUtils.setBlockStateWithCondition(world, pos, SteamSolderingStation.ACTIVE, progress > 0);
					}
				}
				if (solderingAlloyLevel < 1) {
					if (!inv.getStackInSlot(11).isEmpty()) {
						int[] ids = OreDictionary.getOreIDs(inv.getStackInSlot(11));
						int id = OreDictionary.getOreID("ingotSolderingAlloy");
						boolean f = false;
						for (int i : ids) {
							if (i == id) {
								f = true;
								break;
							}
						}
						if (f) {
							decrStackSize(11, 1);
							solderingAlloyLevel += 4000;
							energy.extractEnergy(10, false);
						}
					}
				}
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, SteamSolderingStation.ACTIVE, false);
			}
		}
	}

	private boolean checkStacks() {
		for (int i = 0;i < 9;i++) {
			ItemStack s = inv.getStackInSlot(i);
			if (!s.isEmpty() && s.getCount() == 1)
				return false;
		}
		return true;
	}

	private void tryCraft(ReturnData data) {
		if (data.isRightLevel()) {
			this.output = data.getReturnStack();
			this.progress = data.getTime() * 10;
			this.maxProgress = data.getTime() * 10;
			this.craftStart();
			this.inv.setInventorySlotContents(13, data.getReturnStack());
		} else {
			craftingError = 3;
			craftingErrorShowTimer = 50;
		}
	}

	private ReturnData checkRecipe() {
		ResearchHandler h = TileEntityResearchTable.getResearchHandler(inv.getStackInSlot(10));
		if (h != null) {
			ItemStack[] inv = TomsModUtils.getStackArrayFromInventory(this.inv);
			ReturnData data = AdvancedCraftingHandler.craft(new ItemStack[]{inv[0], inv[1], inv[2], inv[3], inv[4], inv[5], inv[6], inv[7], inv[8]}, h.getResearchesCompleted(), CraftingLevel.E_SOLDERING_STATION, world);
			return data;
		} else
			return null;
	}

	public boolean hasItemsInCrafting() {
		return !inv.getStackInSlot(0).isEmpty() || !inv.getStackInSlot(1).isEmpty() || !inv.getStackInSlot(2).isEmpty() || !inv.getStackInSlot(3).isEmpty() || !inv.getStackInSlot(4).isEmpty() || !inv.getStackInSlot(5).isEmpty() || !inv.getStackInSlot(6).isEmpty() || !inv.getStackInSlot(7).isEmpty() || !inv.getStackInSlot(8).isEmpty();
	}

	private void craftStart() {
		for (int i = 0;i < 9;i++) {
			this.decrStackSize(i, 1);
		}
	}

	public long getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public void updateProgress() {
		int upgradeC = getSpeedUpgradeCount();
		int p = MathHelper.floor(upgradeC / 2 + 2 + (upgradeC / 4));
		progress = Math.max(0, progress - p * 10);
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

	@Override
	public void checkItems() {
	}

	@Override
	public void finish() {
	}

	public void buttonPressed() {
		if (energy.getEnergyStored() > 20 && canRun()) {
			if (solderingAlloyLevel > 0) {
				if (this.progress < 1 && this.hasItemsInCrafting() && !inv.getStackInSlot(10).isEmpty()) {
					ReturnData data = checkRecipe();
					if (data != null) {
						if (data.hasAllResearches()) {
							tryCraft(data);
						} else {
							craftingError = 2;
							craftingErrorShowTimer = 50;
						}
					}
				}
				TomsModUtils.setBlockStateWithCondition(world, pos, SteamSolderingStation.ACTIVE, progress > 0);
			}
		}
	}
}
