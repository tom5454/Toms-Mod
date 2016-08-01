package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tom.api.inventory.IJEIAutoFillTerminal;
import com.tom.apis.Function;
import com.tom.apis.Function.BiFunction;
import com.tom.storage.tileentity.gui.GuiBlockPatternTerminal;
import com.tom.storage.tileentity.gui.GuiPatternOptions;
import com.tom.storage.tileentity.inventory.ContainerBlockPatternTerminal;
import com.tom.storage.tileentity.inventory.ContainerPatternOptions;

import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.transfer.RecipeTransferErrorInternal;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PatternTerminalJEITransferHandler implements IRecipeTransferHandler{
	private final Class<? extends Container> containerClass;
	private final String recipeUID;
	private final BiFunction<IRecipeLayout, Boolean, ItemStack[]> transferFunc;
	private static final List<Class<? extends Container>> containerClasses = new ArrayList<Class<? extends Container>>();
	private static final Map<Class<? extends GuiContainer>, int[]> guiContainerClasses = new HashMap<Class<? extends GuiContainer>, int[]>();
	private static final List<String> uids = new ArrayList<String>();
	private final boolean useContainerItems;
	static{
		containerClasses.add(ContainerBlockPatternTerminal.class);
		guiContainerClasses.put(GuiBlockPatternTerminal.class, new int[]{71, 127, 24, 17});
		containerClasses.add(ContainerPatternOptions.class);
		guiContainerClasses.put(GuiPatternOptions.class, new int[]{110, 54, 24, 17});
	}
	public PatternTerminalJEITransferHandler(Class<? extends Container> containerClass, String recipeUID, BiFunction<IRecipeLayout, Boolean, ItemStack[]> transferFunc, boolean useContainerItems) {
		this.containerClass = containerClass;
		this.recipeUID = recipeUID;
		this.transferFunc = transferFunc;
		this.useContainerItems = useContainerItems;
	}

	@Override
	public Class<? extends Container> getContainerClass() {
		return containerClass;
	}

	@Override
	public String getRecipeCategoryUid() {
		return recipeUID;
	}

	@Override
	public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player,
			boolean maxTransfer, boolean doTransfer) {
		if(container instanceof IJEIAutoFillTerminal){
			if(doTransfer){
				ItemStack[] stacks = transferFunc.apply(recipeLayout, false);
				ItemStack[] output = transferFunc.apply(recipeLayout, true);
				NBTTagCompound compound = new NBTTagCompound();
				NBTTagList list = new NBTTagList();
				for (int i = 0; i < stacks.length; ++i)
				{
					if (stacks[i] != null)
					{
						NBTTagCompound nbttagcompound = new NBTTagCompound();
						nbttagcompound.setByte("s", (byte)i);
						stacks[i].writeToNBT(nbttagcompound);
						list.appendTag(nbttagcompound);
					}
				}
				compound.setTag("i", list);
				list = new NBTTagList();
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
				compound.setTag("o", list);
				compound.setBoolean("c", useContainerItems);
				((IJEIAutoFillTerminal)container).sendMessage(compound);
				//((IPatternTerminal)container).setRecipe(stacks, output);
			}
		}else{
			return RecipeTransferErrorInternal.instance;
		}
		return null;
	}
	public static void loadPetternTerminalTransferHandler(IRecipeTransferRegistry recipeTransferRegistry, String uid, BiFunction<IRecipeLayout, Boolean, ItemStack[]> transferFunc, boolean useContainerItems){
		uids.add(uid);
		for(int i = 0;i<containerClasses.size();i++)recipeTransferRegistry.addRecipeTransferHandler(new PatternTerminalJEITransferHandler(containerClasses.get(i), uid, transferFunc, useContainerItems));
	}
	public static void loadPetternTerminalTransferHandler(IRecipeTransferRegistry recipeTransferRegistry, String uid, final Function<IRecipeLayout, ItemStack[]> transferFuncInputs, final Function<IRecipeLayout, ItemStack[]> transferFuncOutputs, boolean useContainerItems){
		loadPetternTerminalTransferHandler(recipeTransferRegistry, uid, new BiFunction<IRecipeLayout, Boolean, ItemStack[]>() {

			@Override
			public ItemStack[] apply(IRecipeLayout t, Boolean u) {
				return u ? transferFuncOutputs.apply(t) : transferFuncInputs.apply(t);
			}
		}, useContainerItems);
	}
	public static void loadPetternTerminalTransferHandler(IRecipeTransferRegistry recipeTransferRegistry, String uid, BiFunction<IRecipeLayout, Boolean, ItemStack[]> transferFunc){
		loadPetternTerminalTransferHandler(recipeTransferRegistry, uid, transferFunc, false);
	}
	public static void loadPetternTerminalTransferHandler(IRecipeTransferRegistry recipeTransferRegistry, String uid, final Function<IRecipeLayout, ItemStack[]> transferFuncInputs, final Function<IRecipeLayout, ItemStack[]> transferFuncOutputs){
		loadPetternTerminalTransferHandler(recipeTransferRegistry, uid, transferFuncInputs, transferFuncOutputs, false);
	}
	public static void loadClickAreas(IModRegistry registry){
		String[] uidArray = uids.toArray(new String[]{});
		for(Entry<Class<? extends GuiContainer>, int[]> e : guiContainerClasses.entrySet())registry.addRecipeClickArea(e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2], e.getValue()[3], uidArray);
	}
	public static void clearUids(){
		uids.clear();
	}
}
