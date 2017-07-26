package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.base.Function;

import com.tom.api.inventory.IJEIAutoFillTerminal;
import com.tom.storage.tileentity.gui.GuiCraftingTerminal;
import com.tom.storage.tileentity.inventory.ContainerCraftingTerminal;

import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.transfer.RecipeTransferErrorInternal;

@SuppressWarnings("rawtypes")
public class CraftingTerminalTransferHandler implements IRecipeTransferHandler {
	private final Class<? extends Container> containerClass;
	private static final List<Class<? extends Container>> containerClasses = new ArrayList<>();
	private static final List<Class<? extends GuiContainer>> guiContainerClasses = new ArrayList<>();
	private static final Function<IRecipeLayout, ItemStack[][]> transferFunc = new Function<IRecipeLayout, ItemStack[][]>() {
		@Override
		public ItemStack[][] apply(IRecipeLayout t) {
			List<ItemStack[]> inputs = new ArrayList<>();
			IGuiItemStackGroup itemStackGroup = t.getItemStacks();
			for (IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
				if (ingredient.isInput()) {
					if (!ingredient.getAllIngredients().isEmpty() && ingredient.getAllIngredients().get(0) != null) {
						inputs.add(ingredient.getAllIngredients().toArray(new ItemStack[]{}));
					} else {
						inputs.add(null);
					}
				}
			}
			return inputs.toArray(new ItemStack[][]{});
		}
	};
	static {
		containerClasses.add(ContainerCraftingTerminal.class);
		guiContainerClasses.add(GuiCraftingTerminal.class);
	}

	public CraftingTerminalTransferHandler(Class<? extends Container> containerClass) {
		this.containerClass = containerClass;
	}

	@Override
	public Class<? extends Container> getContainerClass() {
		return containerClass;
	}

	@Override
	public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		if (container instanceof IJEIAutoFillTerminal) {
			if (doTransfer) {
				ItemStack[][] stacks = transferFunc.apply(recipeLayout);
				// ItemStack[] output = transferFunc.apply(recipeLayout, true);
				NBTTagCompound compound = new NBTTagCompound();
				NBTTagList list = new NBTTagList();
				for (int i = 0;i < stacks.length;++i) {
					if (stacks[i] != null) {
						NBTTagCompound nbttagcompound = new NBTTagCompound();
						nbttagcompound.setByte("s", (byte) i);
						for (int j = 0;j < stacks[i].length && j < 3;j++) {
							if (stacks[i][j] != null && !stacks[i][j].isEmpty()) {
								NBTTagCompound tag = new NBTTagCompound();
								stacks[i][j].writeToNBT(tag);
								nbttagcompound.setTag("i" + j, tag);
							}
						}
						nbttagcompound.setByte("l", (byte) Math.min(3, stacks[i].length));
						list.appendTag(nbttagcompound);
					}
				}
				compound.setTag("i", list);
				/*list = new NBTTagList();
				for (int i = 0; i < output.length; ++i)
				{
					if (output[i] != null)
					{
						NBTTagCompound nbttagcompound = new NBTTagCompound();
						nbttagcompound.setByte("s", (byte)i);
						output[i].writeToNBT(nbttagcompound);
						list.appendTag(nbttagcompound);
					}
				}
				compound.setTag("o", list);*/
				((IJEIAutoFillTerminal) container).sendMessage(compound);
				// ((IPatternTerminal)container).setRecipe(stacks, output);
			}
		} else {
			return RecipeTransferErrorInternal.INSTANCE;
		}
		return null;
	}

	public static void registerTransferHandlers(IRecipeTransferRegistry recipeTransferRegistry) {
		for (int i = 0;i < containerClasses.size();i++)
			recipeTransferRegistry.addRecipeTransferHandler(new CraftingTerminalTransferHandler(containerClasses.get(i)), VanillaRecipeCategoryUid.CRAFTING);
	}

	public static void registerClickAreas(IModRegistry registry) {
		for (int i = 0;i < guiContainerClasses.size();i++)
			registry.addRecipeClickArea(guiContainerClasses.get(i), 90, 127, 24, 17, VanillaRecipeCategoryUid.CRAFTING);
	}
}
