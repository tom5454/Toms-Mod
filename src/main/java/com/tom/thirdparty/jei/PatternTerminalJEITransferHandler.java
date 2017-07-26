package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.tom.api.inventory.IJEIAutoFillTerminal;
import com.tom.storage.tileentity.gui.GuiPatternOptions;
import com.tom.storage.tileentity.gui.GuiPatternTerminal;
import com.tom.storage.tileentity.inventory.ContainerPatternOptions;
import com.tom.storage.tileentity.inventory.ContainerPatternTerminal;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.transfer.RecipeTransferErrorInternal;

@SuppressWarnings("rawtypes")
public class PatternTerminalJEITransferHandler implements IRecipeTransferHandler {
	private final Class<? extends Container> containerClass;
	private static final List<Class<? extends Container>> containerClasses = new ArrayList<>();
	private static final Map<Class<? extends GuiContainer>, int[]> guiContainerClasses = new HashMap<>();
	static {
		containerClasses.add(ContainerPatternTerminal.class);
		guiContainerClasses.put(GuiPatternTerminal.class, new int[]{71, 127, 24, 17});
		containerClasses.add(ContainerPatternOptions.class);
		guiContainerClasses.put(GuiPatternOptions.class, new int[]{110, 54, 24, 17});
	}

	public PatternTerminalJEITransferHandler(Class<? extends Container> containerClass) {
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
				ItemStack[] stacks = JEIHandler.directTransfer.apply(recipeLayout, false);
				ItemStack[] output = JEIHandler.directTransfer.apply(recipeLayout, true);
				NBTTagCompound compound = new NBTTagCompound();
				NBTTagList list = new NBTTagList();
				for (int i = 0;i < stacks.length;++i) {
					if (stacks[i] != null) {
						NBTTagCompound nbttagcompound = new NBTTagCompound();
						nbttagcompound.setByte("s", (byte) i);
						stacks[i].writeToNBT(nbttagcompound);
						list.appendTag(nbttagcompound);
					}
				}
				compound.setTag("i", list);
				list = new NBTTagList();
				for (int i = 0;i < output.length;++i) {
					if (output[i] != null) {
						NBTTagCompound nbttagcompound = new NBTTagCompound();
						nbttagcompound.setByte("s", (byte) i);
						output[i].writeToNBT(nbttagcompound);
						list.appendTag(nbttagcompound);
					}
				}
				compound.setTag("o", list);
				((IJEIAutoFillTerminal) container).sendMessage(compound);
			}
		} else {
			return RecipeTransferErrorInternal.INSTANCE;
		}
		return null;
	}

	public static void loadPetternTerminalTransferHandler(IRecipeTransferRegistry recipeTransferRegistry) {
		for (int i = 0;i < containerClasses.size();i++)
			recipeTransferRegistry.addUniversalRecipeTransferHandler(new PatternTerminalJEITransferHandler(containerClasses.get(i)));
	}
}
