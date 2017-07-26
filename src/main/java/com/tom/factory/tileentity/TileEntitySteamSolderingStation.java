package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.oredict.OreDictionary;

import com.tom.apis.TomsModUtils;
import com.tom.core.research.ResearchHandler;
import com.tom.factory.block.SteamSolderingStation;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.recipes.handler.AdvancedCraftingHandler.ReturnData;

import com.tom.core.tileentity.TileEntityResearchTable;

public class TileEntitySteamSolderingStation extends TileEntitySteamMachine {
	private static final int[] SLOTS = new int[]{9, 11};
	public int maxProgress = 0;
	private ItemStack output = ItemStack.EMPTY;
	public int craftingError = 0;
	private int craftingErrorShowTimer = 0;
	private int solderingAlloyLevel = 0;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("maxProgress", maxProgress);
		NBTTagCompound tagC = new NBTTagCompound();
		tagC.setTag("out", this.output.writeToNBT(new NBTTagCompound()));
		tag.setTag("crafting", tagC);
		tag.setInteger("solderingAlloy", solderingAlloyLevel);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.maxProgress = tag.getInteger("maxProgress");
		NBTTagCompound tagC = tag.getCompoundTag("crafting");
		this.output = TomsModUtils.loadItemStackFromNBT(tagC.getCompoundTag("out"));
		this.solderingAlloyLevel = tag.getInteger("solderingAlloy");
	}

	@Override
	public int getSizeInventory() {
		return 12;
	}

	@Override
	public int getField(int id) {
		return id == 0 ? progress : id == 1 ? maxProgress : id == 2 ? solderingAlloyLevel : 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0)
			progress = value;
		else if (id == 1)
			maxProgress = value;
		else if (id == 2)
			solderingAlloyLevel = value;
		// else if(id == 1)maxProgress = value;
	}

	@Override
	public int getFieldCount() {
		return 2;
	}

	@Override
	public String getName() {
		return "steamSolderingStation";
	}

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
	public void updateEntity() {
		if (!world.isRemote) {
			if (this.craftingErrorShowTimer > 0)
				if (this.craftingErrorShowTimer-- == 1 && (craftingError == 2 || craftingError == 3))
					craftingError = 0;
			if (tank.getFluidAmount() > 1200) {
				if (solderingAlloyLevel > 0) {
					if (progress > 0) {
						tank.drainInternal(20, true);
						progress--;
						solderingAlloyLevel--;
					} else if (progress == 0) {
						this.craftingError = 0;
						if (!output.isEmpty()) {
							if (inv.getStackInSlot(9).isEmpty()) {
								inv.setInventorySlotContents(9, output.copy());
								output = ItemStack.EMPTY;
							} else {
								if (inv.getStackInSlot(9).isItemEqual(this.output) && ItemStack.areItemStackTagsEqual(output, inv.getStackInSlot(9)) && inv.getStackInSlot(9).getCount() + this.output.getCount() <= Math.min(getInventoryStackLimit(), this.output.getMaxStackSize())) {
									inv.getStackInSlot(9).grow(this.output.getCount());
									output = ItemStack.EMPTY;
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
							ResearchHandler h = TileEntityResearchTable.getResearchHandler(inv.getStackInSlot(10));
							if (h != null) {
								ItemStack[] stack = TomsModUtils.getStackArrayFromInventory(inv);
								ReturnData data = AdvancedCraftingHandler.craft(new ItemStack[]{stack[0], stack[1], stack[2], stack[3], stack[4], stack[5], stack[6], stack[7], stack[8]}, h.getResearchesCompleted(), CraftingLevel.SOLDERING_STATION, world);
								if (data != null) {
									if (data.hasAllResearches()) {
										if (data.isRightLevel()) {
											this.output = data.getReturnStack();
											this.progress = data.getTime();
											this.maxProgress = data.getTime();
											this.craftStart();
										} else {
											craftingError = 3;
											craftingErrorShowTimer = 50;
										}
									} else {
										craftingError = 2;
										craftingErrorShowTimer = 50;
									}
								}
							}
						}
						TomsModUtils.setBlockStateWithCondition(world, pos, SteamSolderingStation.ACTIVE, progress > 0);
					}
				}
				if (solderingAlloyLevel == 0) {
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
							solderingAlloyLevel += 1000;
							tank.drainInternal(50, true);
						}
					}
				}
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, SteamSolderingStation.ACTIVE, false);
			}
		}
	}

	public boolean hasItemsInCrafting() {
		return !inv.getStackInSlot(0).isEmpty() || !inv.getStackInSlot(1).isEmpty() || !inv.getStackInSlot(2).isEmpty() || !inv.getStackInSlot(3).isEmpty() || !inv.getStackInSlot(4).isEmpty() || !inv.getStackInSlot(5).isEmpty() || !inv.getStackInSlot(6).isEmpty() || !inv.getStackInSlot(7).isEmpty() || !inv.getStackInSlot(8).isEmpty();
	}

	private void craftStart() {
		for (int i = 0;i < 9;i++) {
			this.decrStackSize(i, 1);
		}
	}

	@Override
	public int getSteamUsage() {
		return 0;
	}

	@Override
	public void checkItems() {

	}

	@Override
	public void finish() {

	}
}