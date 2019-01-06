package com.tom.storage.handler;

import java.util.Comparator;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.grid.StorageNetworkGrid;
import com.tom.api.grid.StorageNetworkGrid.ICraftingReportScreen;

public interface ICraftable {
	public static class CraftableComparatorAmount implements IReversableCraftableComparator {
		public boolean reversed;

		public CraftableComparatorAmount(boolean reversed) {
			this.reversed = reversed;
		}

		@Override
		public int compare(ICraftable in1, ICraftable in2) {
			int c = in2.getQuantity() > in1.getQuantity() ? 1 : (in1.getQuantity() == in2.getQuantity() ? in1.getUnlocalizedName().compareTo(in2.getUnlocalizedName()) : -1);
			return this.reversed ? -c : c;
		}

		@Override
		public void setReverse(boolean reverse) {
			this.reversed = reverse;
		}

		@Override
		public Comparator<ICraftable> getReversed() {
			return new CraftableComparatorAmount(!reversed);
		}
	}

	public static class CraftableComparatorName implements IReversableCraftableComparator {
		public boolean reversed;

		public CraftableComparatorName(boolean reversed) {
			this.reversed = reversed;
		}

		@Override
		public int compare(ICraftable in1, ICraftable in2) {
			int c = in1.getDisplayName().compareTo(in2.getDisplayName());
			return this.reversed ? -c : c;
		}

		@Override
		public void setReverse(boolean reverse) {
			this.reversed = reverse;
		}

		@Override
		public Comparator<ICraftable> getReversed() {
			return new CraftableComparatorName(!reversed);
		}
	}

	public static class CraftableComparatorMod implements IReversableCraftableComparator {
		public boolean reversed;

		public CraftableComparatorMod(boolean reversed) {
			this.reversed = reversed;
		}

		@Override
		public int compare(ICraftable in1, ICraftable in2) {
			String modname1 = in1.getDelegateName().getResourceDomain();
			String modname2 = in2.getDelegateName().getResourceDomain();
			int c2 = modname1.compareTo(modname2);
			int c = c2 == 0 ? in2.getQuantity() > in1.getQuantity() ? 1 : (in1.getQuantity() == in2.getQuantity() ? in1.getUnlocalizedName().compareTo(in2.getUnlocalizedName()) : -1) : c2;
			return this.reversed ? -c : c;
		}

		@Override
		public void setReverse(boolean reverse) {
			this.reversed = reverse;
		}

		@Override
		public Comparator<ICraftable> getReversed() {
			return new CraftableComparatorMod(!reversed);
		}
	}

	public static enum CraftableSorting {
		AMOUNT(new CraftableComparatorAmount(false)), NAME(new CraftableComparatorName(false)), MOD(new CraftableComparatorMod(false)),;
		public static final CraftableSorting[] VALUES = values();
		private final Comparator<ICraftable> comparator, comparatorRev;

		private CraftableSorting(IReversableCraftableComparator comparator) {
			this.comparator = comparator;
			this.comparatorRev = comparator.getReversed();
		}

		public Comparator<ICraftable> getComparator(boolean reversed) {
			return reversed ? comparatorRev : comparator;
		}

		public static CraftableSorting get(int index) {
			return VALUES[MathHelper.abs(index % VALUES.length)];
		}
	}

	public static interface IReversableCraftableComparator extends Comparator<ICraftable> {
		void setReverse(boolean reverse);

		Comparator<ICraftable> getReversed();
	}

	class CraftableProperties {
		public boolean useOreDict = true, useMeta = true, useNBT = false;

		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			tag.setBoolean("nbt", useNBT);
			tag.setBoolean("meta", useMeta);
			tag.setBoolean("oreDict", useOreDict);
			return tag;
		}

		public void readFromNBT(NBTTagCompound tag) {
			useMeta = tag.getBoolean("meta");
			useNBT = tag.getBoolean("nbt");
			useOreDict = tag.getBoolean("oreDict");
		}
	}

	ICraftable copy();

	String getDisplayName();

	ResourceLocation getDelegateName();

	void removeQuantity(long value);

	void writeObjToNBT(NBTTagCompound t);

	void addValidRecipes(List<AutoCraftingHandler.ICraftingRecipe<?>> recipes, List<AutoCraftingHandler.ICraftingRecipe<?>> validRecipes);

	void pull(NetworkCache cache, List<ICraftable> requiredStacksToPull);

	boolean hasQuantity();

	boolean isEqual(ICraftable s);

	long handleSecondaryPull(ICraftable secondary);

	void setNoQuantity();

	void add(ICraftable other);

	AutoCraftingHandler.RecipeReturnInformation useRecipe(AutoCraftingHandler.ICraftingRecipe<?> r, NetworkCache cache, AutoCraftingHandler.SecondaryOutList secondaryOutList, List<ICraftable> requiredStacksToPull, List<AutoCraftingHandler.RecipeToCraft> recipesToCraft, List<ICraftable> toCraft);

	long getQuantity();

	ITextComponent serializeTextComponent(TextFormatting color);

	void checkIfIngredientsAreAvailable(NetworkCache cache, List<ICraftable> missingStacks, AutoCraftingHandler.CalculatedCrafting crafting);

	@SuppressWarnings("rawtypes")
	@SideOnly(Side.CLIENT)
	void drawEntry(AutoCraftingHandler.ClientCraftingStack ccStack, int posX, int posY, int mouseX, int mouseY, ICraftingReportScreen screen, boolean isTooltip);

	ICraftable pushToGrid(StorageNetworkGrid grid);

	void setQuantity(long value);

	Class<? extends ICache<?>> getCacheClass();

	@SideOnly(Side.CLIENT)
	String serializeStringTooltip();

	long getLevel();

	void setLevel(long level);

	ICraftable.CraftableProperties getProperties();

	long getMaxStackSize();

	String getUnlocalizedName();

	int getBaseBytes();

	int getQuantityBytes();

	long getMaxQuantityForBytes(int bytes);
}