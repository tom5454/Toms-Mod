package com.tom.storage.handler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Function;

import com.tom.api.inventory.IStorageInventory;
import com.tom.lib.network.GuiSyncHandler.IPacketReceiver;
import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.gui.GuiTerminalBase;

public interface ITerminal extends IPacketReceiver {
	int getTerminalMode();

	void setClientState(TileEntityBasicTerminal.TerminalState powered);

	TileEntityBasicTerminal.TerminalState getTerminalState();

	boolean getClientPowered();

	int getProcessingPower();

	int getMemoryUsage();

	@SideOnly(Side.CLIENT)
	void sendUpdate(int id, int extra, GuiTerminalBase gui);

	IStorageInventory getStorageInventory();

	StorageData getData();

	void startCrafting(ICraftable stackToCraft, Function<AutoCraftingHandler.CraftingCalculationResult, Void> apply);

	ItemStack pushStack(ItemStack itemstack);

	ItemStack pullStack(ItemStack p);

	void sendUpdate(NBTTagCompound tag);
}