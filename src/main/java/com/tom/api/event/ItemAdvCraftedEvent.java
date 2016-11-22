package com.tom.api.event;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import com.tom.api.item.ICustomCraftingHandlerAdv.ItemStackAccess;
import com.tom.core.research.ResearchHandler;

@Cancelable
public class ItemAdvCraftedEvent extends Event {

	public final ItemStack crafting;
	public final String player;
	public final IInventory craftMatrix;
	public final ItemStackAccess secondStack;
	public final int timeOld;
	public int timeNew;
	public ITextComponent errorMsg;

	public ItemAdvCraftedEvent(ItemStack crafting, String player, IInventory craftMatrix, ItemStack secondStack,
			int time) {
		this.crafting = crafting;
		this.player = player;
		this.craftMatrix = craftMatrix;
		this.secondStack = new ItemStackAccess(secondStack);
		this.timeNew = this.timeOld = time;
	}
	public ResearchHandler getResearchHandler() {
		return ResearchHandler.getHandlerFromName(player);
	}

	public static class EventResult{
		public final boolean canCraft;
		public final ITextComponent errorMessage;
		public final ItemStack mainStack, secondStack;
		public final int time;

		public EventResult(boolean canCraft, ITextComponent errorMessage, ItemStack mainStack, ItemStack secondStack,
				int time) {
			this.canCraft = canCraft;
			this.errorMessage = errorMessage;
			this.mainStack = mainStack;
			this.secondStack = secondStack;
			this.time = time;
		}
	}

	public static EventResult fire(String player, ItemStack[] mainInv, int from, ItemStack crafting, ItemStack secondary, int time){
		ItemStack[] array = new ItemStack[9];
		System.arraycopy(mainInv, from, array, 0, 9);
		IInventory inv = new InventoryBasic("", false, array.length);
		for(int i = 0;i<array.length;i++){
			inv.setInventorySlotContents(i, array[i]);
		}
		ItemAdvCraftedEvent evt = new ItemAdvCraftedEvent(crafting, player, inv, secondary, time);
		boolean c = MinecraftForge.EVENT_BUS.post(evt);
		return new EventResult(!c, evt.errorMsg, evt.crafting, evt.secondStack.getStack(), evt.timeNew);
	}
}
