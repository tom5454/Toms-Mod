package com.tom.api.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.gui.GuiTomsMod;
import com.tom.storage.handler.AutoCraftingHandler.CraftingPatternProperties;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.ITerminal;

public interface IPatternTerminal extends ITerminal {
	IInventory getRecipeInv();

	IInventory getResultInv();

	IInventory getPatternInv();

	ItemStack getButtonStack();

	CraftingPatternProperties getProperties();

	boolean hasPattern();

	@SideOnly(Side.CLIENT)
	void sendUpdate(GuiTomsMod gui, int id, int extra);

	void sendUpdate(NBTTagCompound message);

	ICraftable.CraftableProperties getPropertiesFor(int id);

	int getPropertiesLength();

	IInventory getUpgradeInv();

	int getCraftingBehaviour();

	void setCraftingBehaviour(int data);
}
