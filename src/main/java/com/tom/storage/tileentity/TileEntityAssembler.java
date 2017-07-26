package com.tom.storage.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.inventory.StoredItemStack;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.ReturnData;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.StorageNetworkGrid.ICraftingPatternListener;

public class TileEntityAssembler extends TileEntityTomsMod implements IEnergyReceiver, ICraftingPatternListener {
	private EnergyStorage energy = new EnergyStorage(10000);
	private List<ItemStack> stacksToPush = new ArrayList<>();
	private List<ItemStack> stacksToAdd = new ArrayList<>();
	private int i = 0;

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == HV;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return canConnectEnergy(from, type) ? energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return canConnectEnergy(from, type) ? energy.getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return canConnectEnergy(from, type) ? energy.getMaxEnergyStored() : 0;
	}

	@Override
	public boolean pushRecipe(ICraftable[] recipeIn, boolean doPush) {
		ItemStack[] recipe = new ItemStack[recipeIn.length];
		boolean error = false;
		for (int i = 0;i < recipeIn.length;i++) {
			if (recipeIn[i] != null) {
				if (recipeIn[i] instanceof StoredItemStack) {
					recipe[i] = ((StoredItemStack) recipeIn[i]).getStack();
				} else {
					error = true;
				}
			}
		}
		if (error)
			return false;
		if (i == 0 && stacksToPush.isEmpty() && stacksToAdd.isEmpty() && energy.extractEnergy(5, true) == 5) {
			List<ItemStack> result = TomsModUtils.craft(recipe, null, world);
			if (result != null) {
				if (doPush) {
					energy.extractEnergy(4, false);
					for (int i = 0;i < result.size();i++) {
						ItemStack s = result.get(i);
						if (s != null) {
							/*s = TomsModUtils.pushStackToNeighbours(s, worldObj, pos, null);
							if(s != null){
							stacksToPush.add(s);
							}*/
							stacksToAdd.add(s);
							this.i = 5;
						}
					}
				}
				return true;
			} else {
				ReturnData data = AdvancedCraftingHandler.craft(recipe, null, null, world);
				if (data != null && data.hasAllResearches()) {
					if (doPush) {
						energy.extractEnergy(1 + (data.getTime() / 20), false);
						stacksToAdd.add(data.getReturnStack());
						if (data.getExtraStack() != null)
							stacksToAdd.add(data.getExtraStack());
						this.i = data.getTime() / 10 + 3;
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public double receiveEnergy(double maxReceive, boolean simulate) {
		return energy.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList list = compound.getTagList("itemsStored", 10);
		stacksToPush.clear();
		for (int i = 0;i < list.tagCount();i++) {
			NBTTagCompound t = list.getCompoundTagAt(i);
			ItemStack stack = TomsModUtils.loadItemStackFromNBT(t);
			if (stack != null) {
				stack.setCount(t.getInteger("ItemCount"));
				stacksToPush.add(stack);
			}
		}
		energy.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagList list = new NBTTagList();
		for (int i = 0;i < stacksToPush.size();i++) {
			NBTTagCompound t = new NBTTagCompound();
			ItemStack stack = stacksToPush.get(i);
			t.setInteger("ItemCount", stack.getCount());
			stack.writeToNBT(t);
			list.appendTag(t);
		}
		compound.setTag("itemsStored", list);
		energy.writeToNBT(compound);
		return compound;
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			for (int i = 0;i < stacksToPush.size();i++) {
				ItemStack s = stacksToPush.get(i);
				if (s != null) {
					ItemStack p = TomsModUtils.pushStackToNeighbours(s.copy(), world, pos, EnumFacing.VALUES);
					if (p == null || p.getCount() < 1) {
						stacksToPush.remove(s);
					} else {
						s.setCount(p.getCount());
					}
				}
			}
			int iOld = i;
			if (i > 0 && energy.extractEnergy(.5, true) == .5) {
				i--;
				energy.extractEnergy(.5, false);
			}
			if (iOld == 1) {
				stacksToPush.addAll(stacksToAdd);
				stacksToAdd.clear();
			}
		}
	}
}
