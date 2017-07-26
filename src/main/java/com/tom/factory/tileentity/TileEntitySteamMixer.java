package com.tom.factory.tileentity;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.inventory.InventorySection;
import com.tom.apis.TomsModUtils;
import com.tom.apis.TomsModUtils.FillRunnable;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.Type;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntitySteamMixer extends TileEntitySteamMachine {
	private FluidTank tankIn = new FluidTank(10000);
	private FluidTank tankOut = new FluidTank(10000);
	public int clientProgress = 0;
	private static final int[] SLOTS = new int[]{0, 1, 2, 3};
	public static final int MAX_PROCESS_TIME = 150;
	private FluidStack crafting;
	protected static final Object[][] RECIPES = new Object[][]{{TomsModUtils.createRecipe(new Object[]{new ItemStack(Items.GLOWSTONE_DUST, 2), "dyeWhite", Items.REDSTONE, Items.SUGAR}), new Object[][]{{new FluidStack(FluidRegistry.WATER, 1000), true, 0}, {new FluidStack(CoreInit.photoactiveLiquid.get(), 200), false, 1}}, false,}, {TomsModUtils.createRecipe(new Object[]{new ItemStack(Items.GUNPOWDER, 2), new ItemStack(Items.ROTTEN_FLESH, 8), new Object[]{"dyeWhite", 2}, TMResource.SULFUR.getStackName(Type.DUST)}), new Object[][]{{new FluidStack(FluidRegistry.WATER, 1000), true, 0}, {new FluidStack(CoreInit.sulfuricAcid.get(), 500), false, 1}}, false,}, {TomsModUtils.createRecipe(new Object[]{new ItemStack(Items.GUNPOWDER, 2), new ItemStack(Items.ROTTEN_FLESH, 6), new Object[]{"dyeWhite", 2}, TMResource.IRON.getStackName(Type.DUST)}), new Object[][]{{new FluidStack(FluidRegistry.WATER, 1000), true, 0}, {new FluidStack(CoreInit.ironChloride.get(), 500), false, 1}}, false,}};

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return Helper.getFluidHandlerFromTanksWithPredicate(new FluidTank[]{tank, tankIn, tankOut}, new Object[]{CoreInit.steam.get(), FluidRegistry.WATER, null}, new boolean[]{true, true, false}, new boolean[]{false, false, true});
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("tankIn", tankIn.writeToNBT(new NBTTagCompound()));
		tag.setTag("tankOut", tankOut.writeToNBT(new NBTTagCompound()));
		if (crafting != null)
			tag.setTag("crafting", crafting.writeToNBT(new NBTTagCompound()));
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tankIn.readFromNBT(tag.getCompoundTag("tankIn"));
		tankOut.readFromNBT(tag.getCompoundTag("tankOut"));
		crafting = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("crafting"));
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public String getName() {
		return "steamMixer";
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return true;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}

	@SuppressWarnings("unchecked")
	private Object[] findRecipe(boolean apply) {
		Object[] obj = TomsModUtils.checkAndConsumeMatch(RECIPES, new InventorySection(this, 0, 4), new Object[]{tankIn, tankOut});
		if ((Integer) obj[0] > -1) {
			if (apply)
				TomsModUtils.runAll(((List<Runnable>) obj[1]).stream().filter(r -> !(r instanceof FillRunnable)).collect(Collectors.toList()));
			return new Object[]{obj[0], obj};
		}
		return new Object[]{-1};
	}

	public FluidTank getTankIn() {
		return tankIn;
	}

	public FluidTank getTankOut() {
		return tankOut;
	}

	@Override
	public int getSteamUsage() {
		return 8;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void checkItems() {
		if (((Integer) findRecipe(false)[0]) > -1) {
			Object[] r = findRecipe(true);
			progress = MAX_PROCESS_TIME;
			crafting = ((List<ItemStackChecker>) ((Object[]) r[1])[2]).get(0).getExtraF();
		}
	}

	@Override
	public void finish() {
		progress = -1;
		if (crafting != null) {
			int f = tankOut.fillInternal(crafting, false);
			if (f == crafting.amount) {
				tankOut.fillInternal(crafting, true);
			}
		}
	}
}
