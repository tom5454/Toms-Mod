package com.tom.api.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.storage.multipart.StorageNetworkGrid.CraftableProperties;
import com.tom.storage.multipart.StorageNetworkGrid.CraftingPatternProperties;
import com.tom.storage.multipart.StorageNetworkGrid.ITerminal;

import com.tom.core.tileentity.gui.GuiTomsMod;

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
	CraftableProperties getPropertiesFor(int id);
	int getPropertiesLength();
}
