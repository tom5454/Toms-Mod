package com.tom.storage.tileentity.inventory;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import mapwriterTm.util.Render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;

import com.tom.api.inventory.IStorageInventory;
import com.tom.api.inventory.StoredItemStack;
import com.tom.api.tileentity.IGuiTile;
import com.tom.apis.TomsModUtils;
import com.tom.handler.PlayerHandler;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.storage.multipart.StorageNetworkGrid.CalculatedCrafting;
import com.tom.storage.multipart.StorageNetworkGrid.CompiledCalculatedCrafting;
import com.tom.storage.multipart.StorageNetworkGrid.ITerminal;
import com.tom.storage.tileentity.gui.GuiTerminalBase;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerTerminalBase extends ContainerTomsMod implements IGuiTile{
	public List<StoredItemStack> itemList = Lists.<StoredItemStack>newArrayList();
	public List<StoredItemStack> itemListClient = Lists.<StoredItemStack>newArrayList();
	public List<StoredItemStack> itemListClientSorted = Lists.<StoredItemStack>newArrayList();
	public List<ItemStack> craftables = new ArrayList<ItemStack>();
	protected List<SlotStorage> storageSlotList = new ArrayList<SlotStorage>();
	protected int playerSlotsStart;
	protected EntityPlayer player;
	public boolean isCraftingReport = false;
	public CalculatedCrafting currentReport;
	private boolean dataSent = false, lastPower;
	protected ITerminal te;
	public int terminalType;
	private int sortingData, searchType;
	public ContainerTerminalBase(ITerminal terminal, EntityPlayer player) {
		te = terminal;
		this.player = player;
	}
	protected final void addStorageSlots(int lines, int x, int y){
		for (int i = 0; i < lines; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new SlotStorage(this.te.getGrid().getInventory(), i * 9 + j, x + j * 18, y + i * 18));
			}
		}
		scrollTo(0.0F);
	}
	public static enum SlotAction{
		PULL_OR_PUSH_STACK, PULL_ONE, CRAFT, SPACE_CLICK, SHIFT_PULL, GET_HALF, GET_QUARTER
		;
		public static final SlotAction[] VALUES = values();
	}
	public static class SlotStorage {
		/** display position of the inventory slot on the screen x axis */
		public int xDisplayPosition;
		/** display position of the inventory slot on the screen y axis */
		public int yDisplayPosition;
		/** The index of the slot in the inventory. */
		private final int slotIndex;
		/** The inventory we want to extract a slot from. */
		public final IStorageInventory inventory;
		public StoredItemStack stack;
		public SlotStorage(IStorageInventory inventory, int slotIndex, int xPosition, int yPosition) {
			this.xDisplayPosition = xPosition;
			this.yDisplayPosition = yPosition;
			this.slotIndex = slotIndex;
			this.inventory = inventory;
		}
		public ItemStack pullFromSlot(int max){
			return inventory.pullStack(stack, max);
		}
		public ItemStack pushStack(ItemStack pushStack){
			return inventory.pushStack(pushStack);
		}
		public int getSlotIndex() {
			return slotIndex;
		}
		@SideOnly(Side.CLIENT)
		public void drawSlot(GuiTerminalBase gui, int mouseX, int mouseY){
			if(mouseX >= gui.getGuiLeft()+xDisplayPosition-1 && mouseY >= gui.getGuiTop()+yDisplayPosition-1 && mouseX < gui.getGuiLeft()+xDisplayPosition + 17 && mouseY < gui.getGuiTop()+yDisplayPosition + 17){
				Render.setColourWithAlphaPercent(0xFFFFFF,60);
				Render.drawRect(gui.getGuiLeft()+xDisplayPosition, gui.getGuiTop()+yDisplayPosition, 16, 16);
			}
			if(gui.powered){
				if(stack != null){
					GL11.glPushMatrix();
					gui.renderItemInGui(stack.stack.copy().splitStack(1), gui.getGuiLeft()+xDisplayPosition, gui.getGuiTop()+yDisplayPosition, 0, 0, false, 0xFFFFFF, false);
					this.drawStackSize(gui.getFontRenderer(stack.stack), stack.itemCount, gui.getGuiLeft()+xDisplayPosition, gui.getGuiTop()+yDisplayPosition);
					GL11.glPopMatrix();
				}
			}else{
				gui.bindList();
				gui.drawTexturedModalRect(gui.getGuiLeft()+xDisplayPosition-1, gui.getGuiTop()+yDisplayPosition-1, 125, 120, 18, 18);
			}
		}
		@SideOnly(Side.CLIENT)
		public boolean drawTooltip(GuiTerminalBase gui, int mouseX, int mouseY){
			boolean ret = false;
			if(stack != null){
				if(stack.itemCount > 9999){
					gui.renderItemInGui(stack.stack, gui.getGuiLeft()+xDisplayPosition, gui.getGuiTop()+yDisplayPosition, mouseX, mouseY, false, 0, true, I18n.format("tomsmod.gui.amount", stack.itemCount));
				}else{
					gui.renderItemInGui(stack.stack, gui.getGuiLeft()+xDisplayPosition, gui.getGuiTop()+yDisplayPosition, mouseX, mouseY, false, 0, true);
				}
			}
			return ret;
		}
		@SideOnly(Side.CLIENT)
		private void drawStackSize(FontRenderer fr, int size, int x, int y){
			float scaleFactor = 0.6f;
			boolean unicodeFlag = fr.getUnicodeFlag();
			fr.setUnicodeFlag(false);
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.disableBlend();
			String stackSize = "?";
			if(size == 0){
				stackSize = I18n.format("tomsMod.storage.craft");
				scaleFactor = 0.55F;
			}else{
				stackSize = TomsModUtils.formatNumber(size);
			}
			GL11.glPushMatrix();
			GL11.glScaled( scaleFactor, scaleFactor, scaleFactor );
			float inverseScaleFactor = 1.0f / scaleFactor;
			int X = (int) ( ( (float) x + 0 + 16.0f - fr.getStringWidth( stackSize ) * scaleFactor ) * inverseScaleFactor );
			int Y = (int) ( ( (float) y + 0 + 16.0f - 7.0f * scaleFactor ) * inverseScaleFactor );
			fr.drawStringWithShadow( stackSize, X, Y, 16777215 );
			GL11.glPopMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			fr.setUnicodeFlag(unicodeFlag);
		}
	}
	@Override
	public final void buttonPressed(EntityPlayer player, int id, int extra) {
		PlayerHandler playerH = PlayerHandler.getPlayerHandlerForName(player.getName());
		if(id == 0){
			playerH.setItemSorting(extra);
		}else if(id == 1){
			playerH.setSearchBoxType(extra % 4);
		}else onButtonPressed(id, player, extra);
	}
	protected final void addSlotToContainer(SlotStorage slotStorage) {
		storageSlotList.add(slotStorage);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	/**Use {@link #sendToCrafter(IContainerListener)}, {@link #afterSending()}*/
	@Override
	public final void detectAndSendChanges() {
		super.detectAndSendChanges();
		if(!isCraftingReport){
			List<StoredItemStack> itemListOld = this.itemList;
			List<ItemStack> craftablesOld = this.craftables;
			craftables = te.getGrid().getInventory().getCraftableStacks();
			itemList = te.getGrid().getInventory().getStacks();
			boolean sendUpdate = false;
			NBTTagList list = new NBTTagList();
			NBTTagList cList = new NBTTagList();
			if(itemList.size() != itemListOld.size())sendUpdate = true;
			if(craftables.size() != craftablesOld.size())sendUpdate = true;
			for(int i = 0;i<itemList.size();i++){
				StoredItemStack storedS = itemList.get(i);
				StoredItemStack storedSOld = itemListOld.size() > i ? itemListOld.get(i) : null;
				if(storedSOld == null || (!(storedS.equals(storedSOld) && storedS.itemCount == storedSOld.itemCount))){
					NBTTagCompound tag = new NBTTagCompound();
					tag.setInteger("slot", i);
					//tag.setBoolean("n", false);
					if(storedS != null)storedS.writeToNBT(tag);
					list.appendTag(tag);
					sendUpdate = true;
				}
			}
			for(int i = 0;i<craftables.size();i++){
				ItemStack stack = craftables.get(i);
				ItemStack stackOld = craftablesOld.size() > i ? craftablesOld.get(i) : null;
				if(stackOld == null || (!TomsModUtils.areItemStacksEqual(stack, stackOld, true, true, false))){
					NBTTagCompound tag = new NBTTagCompound();
					tag.setInteger("slot", i);
					//tag.setBoolean("n", false);
					if(stack != null)stack.writeToNBT(tag);
					tag.setByte("Count", (byte) 1);
					cList.appendTag(tag);
					sendUpdate = true;
				}
			}
			/*for(int i = itemList.size();i<itemListOld.size();i++){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("slot", i);
			tag.setBoolean("n", true);
			list.appendTag(tag);
		}*/
			NBTTagCompound mainTag = new NBTTagCompound();
			mainTag.setTag("l", list);
			mainTag.setInteger("s", itemList.size());
			mainTag.setInteger("v", craftables.size());
			mainTag.setTag("a", cList);
			PlayerHandler playerH = PlayerHandler.getPlayerHandlerForName(player.getName());
			int comp = playerH.getItemSortingMode(), search = playerH.getSearchBoxType();
			if(comp != sortingData || search != searchType)sendUpdate = true;
			sortingData = comp;
			searchType = search;
			mainTag.setInteger("d", comp);
			mainTag.setInteger("t", search);
			//mainTag.setInteger("c", te.getTerminalMode());
			mainTag.setBoolean("r", false);
			boolean isPowered = te.getGrid().isPowered();
			for(IContainerListener crafter : listeners) {
				if(sendUpdate)NetworkHandler.sendTo(new MessageNBT(mainTag), (EntityPlayerMP) crafter);
				if(terminalType != te.getTerminalMode()){
					crafter.sendProgressBarUpdate(this, 0, te.getTerminalMode());
				}
				if(lastPower != isPowered){
					crafter.sendProgressBarUpdate(this, 1, isPowered ? 1 : 0);
				}
				sendToCrafter(crafter);
			}
			terminalType = te.getTerminalMode();
			lastPower = isPowered;
			afterSending();
		}else{
			if(!dataSent){
				//currentReport.writeToClientNBTPacket(tag);
				CompiledCalculatedCrafting c = te.getGrid().getData().compileCalculatedCrafting(currentReport);
				for(IContainerListener crafter : listeners) {
					c.sendTo((EntityPlayerMP) crafter);
				}
				dataSent = true;
			}
		}
	}
	public final void setSlotContents(int id, StoredItemStack stack){
		storageSlotList.get(id).stack = stack;
	}
	/**
	 * Updates the gui slots ItemStack's based on scroll position.
	 */
	public final void scrollTo(float p_148329_1_)
	{
		int i = (this.itemListClientSorted.size() + 9 - 1) / 9 - 5;
		int j = (int)(p_148329_1_ * i + 0.5D);

		if (j < 0)
		{
			j = 0;
		}

		for (int k = 0; k < 5; ++k)
		{
			for (int l = 0; l < 9; ++l)
			{
				int i1 = l + (k + j) * 9;

				if (i1 >= 0 && i1 < this.itemListClientSorted.size())
				{
					setSlotContents(l + k * 9, this.itemListClientSorted.get(i1));
				}
				else
				{
					setSlotContents(l + k * 9, null);
				}
			}
		}
	}

	public final void receiveClientNBTPacket(NBTTagCompound message) {
		//StoredItemStack[] stackArray = itemList.toArray(new StoredItemStack[itemList.size()]);
		int size = message.getInteger("s");
		int cSize = message.getInteger("v");
		NBTTagList list = message.getTagList("l", 10);
		NBTTagList cList = message.getTagList("a", 10);
		((ArrayList<StoredItemStack>)itemList).ensureCapacity(size);
		((ArrayList<ItemStack>)craftables).ensureCapacity(cSize);
		for(int i = 0;i<list.tagCount();i++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("slot");
			/*boolean n = tag.getBoolean("n");
			if(n){
				//stackArray[slot] = null;
				itemList.remove(slot);
			}else{*/
			if(itemList.size() > slot)
				itemList.set(slot, StoredItemStack.readFromNBT(tag));
			else
				itemList.add(slot, StoredItemStack.readFromNBT(tag));
			//}
		}
		for(int i = size;i<itemList.size();i++){
			itemList.remove(i);
		}
		for(int i = 0;i<cList.tagCount();i++){
			NBTTagCompound tag = cList.getCompoundTagAt(i);
			int slot = tag.getInteger("slot");
			/*boolean n = tag.getBoolean("n");
			if(n){
				//stackArray[slot] = null;
				itemList.remove(slot);
			}else{*/
			if(craftables.size() > slot)
				craftables.set(slot, ItemStack.loadItemStackFromNBT(tag));
			else
				craftables.add(slot, ItemStack.loadItemStackFromNBT(tag));
			//}
		}
		for(int i = cSize;i<craftables.size();i++){
			ItemStack removed = craftables.remove(i);
			if(removed != null){
				StoredItemStack storedS = new StoredItemStack(removed, -1);
				if(itemList.contains(storedS)){
					itemList.remove(storedS);
				}
			}
		}
		itemListClient = new ArrayList<StoredItemStack>(itemList);
		for(int i = 0;i<craftables.size();i++){
			StoredItemStack storedS = new StoredItemStack(craftables.get(i), 0);
			//if(!itemListClient.contains(storedS)){
			itemListClient.add(storedS);
			//}
		}
	}
	@Override
	public final ItemStack slotClick(int slotId, int clickedButton,
			ClickType clickTypeIn, EntityPlayer playerIn) {
		SlotAction mode = SlotAction.VALUES[clickTypeIn.ordinal()];
		if(slotId < -1 && ((-(slotId-1))-1) < storageSlotList.size() && mode != SlotAction.SPACE_CLICK && !(mode == SlotAction.CRAFT)){
			slotId = slotId - 1;
			if(mode == SlotAction.PULL_OR_PUSH_STACK){
				SlotStorage slot = storageSlotList.get((-slotId)-1);
				ItemStack stack = playerIn.inventory.getItemStack();
				if(stack != null){
					ItemStack itemstack = slot.pushStack(stack);
					playerIn.inventory.setItemStack(itemstack);
					return itemstack;
				}else{
					if(itemList.size() > clickedButton){
						slot.stack = itemList.get(clickedButton);
						if(slot.stack.itemCount == 0){
							//craft(playerIn, slot);
							return null;
						}else{
							ItemStack itemstack = slot.pullFromSlot(64);
							playerIn.inventory.setItemStack(itemstack);
							return itemstack;
						}
					}else return null;
				}
			}else if(mode == SlotAction.PULL_ONE){
				SlotStorage slot = storageSlotList.get((-slotId)-1);
				ItemStack stack = playerIn.inventory.getItemStack();
				if(stack != null){
					slot.stack = itemList.get(clickedButton);
					if(TomsModUtils.areItemStacksEqual(stack, slot.stack.stack, true, true, false) && stack.stackSize + 1 <= stack.getMaxStackSize()){
						ItemStack itemstack = slot.pullFromSlot(1);
						if(itemstack != null){
							stack.stackSize++;
							return stack;
						}
					}
				}else{
					if(itemList.size() > clickedButton){
						slot.stack = itemList.get(clickedButton);
						ItemStack itemstack = slot.pullFromSlot(1);
						playerIn.inventory.setItemStack(itemstack);
						return itemstack;
					}else return null;
				}
				return null;
			}else if(mode == SlotAction.GET_HALF){
				SlotStorage slot = storageSlotList.get((-slotId)-1);
				ItemStack stack = playerIn.inventory.getItemStack();
				if(stack != null){
					ItemStack stack1 = stack.splitStack(Math.min(stack.stackSize, stack.getMaxStackSize()) / 2);
					ItemStack itemstack = slot.pushStack(stack1);
					stack.stackSize += itemstack != null ? itemstack.stackSize : 0;
					playerIn.inventory.setItemStack(stack);
					return stack;
				}else{
					if(itemList.size() > clickedButton){
						slot.stack = itemList.get(clickedButton);
						ItemStack itemstack = slot.pullFromSlot(Math.min(slot.stack.itemCount, slot.stack.stack.getMaxStackSize()) / 2);
						playerIn.inventory.setItemStack(itemstack);
						return itemstack;
					}else return null;
				}
			}else if(mode == SlotAction.GET_QUARTER){
				SlotStorage slot = storageSlotList.get((-slotId)-1);
				ItemStack stack = playerIn.inventory.getItemStack();
				if(stack != null){
					ItemStack stack1 = stack.splitStack(Math.min(stack.stackSize, stack.getMaxStackSize()) / 4);
					ItemStack itemstack = slot.pushStack(stack1);
					stack.stackSize += itemstack != null ? itemstack.stackSize : 0;
					playerIn.inventory.setItemStack(stack);
					return stack;
				}else{
					if(itemList.size() > clickedButton){
						slot.stack = itemList.get(clickedButton);
						ItemStack itemstack = slot.pullFromSlot(Math.min(slot.stack.itemCount, slot.stack.stack.getMaxStackSize()) / 4);
						playerIn.inventory.setItemStack(itemstack);
						return itemstack;
					}else return null;
				}
			}else if(mode == SlotAction.PULL_ONE){
				SlotStorage slot = storageSlotList.get((-slotId)-1);
				ItemStack stack = playerIn.inventory.getItemStack();
				if(stack != null){
					slot.stack = itemList.get(clickedButton);
					//if(TomsModUtils.areItemStacksEqual(stack, slot.stack.stack, true, true, false)){
					ItemStack s = stack.splitStack(1);
					ItemStack s2 = slot.pushStack(s);
					if(s2 != null){
						stack.stackSize += s2.stackSize;
					}
					if(stack != null && stack.stackSize < 1){
						stack = null;
					}
					playerIn.inventory.setItemStack(stack);
					return stack;
					//}
				}
				return null;
			}else{
				SlotStorage slot = storageSlotList.get((-slotId)-1);
				if(itemList.size() > clickedButton && !playerIn.worldObj.isRemote){
					slot.stack = itemList.get(clickedButton);
					ItemStack itemstack = slot.pullFromSlot(64);
					this.mergeItemStack(itemstack, playerSlotsStart+1, this.inventorySlots.size(), true);
					if(itemstack.stackSize > 0)slot.pushStack(itemstack);
					if(!playerIn.worldObj.isRemote)detectAndSendChanges();
					playerIn.inventory.markDirty();
				}
				return null;
			}
		}else if(slotId == -1 && mode == SlotAction.SPACE_CLICK){
			for(int i = playerSlotsStart+1;i<playerSlotsStart+28;i++){
				transferStackInSlot(playerIn, i);
			}
			return null;
		}else if((slotId < 0 || slotId == 1000) && mode == SlotAction.CRAFT){
			if(isCraftingReport){
				isCraftingReport = false;
				craftables.clear();
				itemList.clear();
				detectAndSendChanges();
				if(clickedButton == 1 && !playerIn.worldObj.isRemote){
					te.getGrid().getData().queueCrafting(currentReport.mainStack.copy(), currentReport.mainStack.getQuantity(), playerIn, slotId < 0 ? (-slotId)-1 : -1);
				}
				return playerIn.inventory.getItemStack();
			}else{
				if(!playerIn.worldObj.isRemote)craft(playerIn, new StoredItemStack(craftables.get((-slotId)-1), clickedButton));
				return playerIn.inventory.getItemStack();
			}
		}else
			return super.slotClick(slotId, clickedButton, clickTypeIn, playerIn);
	}
	@SideOnly(Side.CLIENT)
	public final int drawSlots(GuiTerminalBase gui, int mouseX, int mouseY){
		for(int i = 0;i<storageSlotList.size();i++){
			storageSlotList.get(i).drawSlot(gui, mouseX, mouseY);
		}
		for(int i = 0;i<storageSlotList.size();i++){
			if(storageSlotList.get(i).drawTooltip(gui, mouseX, mouseY)){
				return i;
			}
		}
		return -1;
	}
	public final SlotStorage getSlotByID(int id){
		return storageSlotList.get(id);
	}
	@Override
	public final ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		if(inventorySlots.size() > index){
			if(index > playerSlotsStart){
				if(inventorySlots.get(index) != null && inventorySlots.get(index).getHasStack()){
					Slot slot = inventorySlots.get(index);
					ItemStack itemstack = te.getGrid().getInventory().pushStack(slot.getStack());
					slot.putStack(itemstack);
					if(!playerIn.worldObj.isRemote)detectAndSendChanges();
				}
			}else{
				return shiftClickItems(playerIn, index);
			}
		}
		return null;
	}
	protected final void craft(EntityPlayer player, StoredItemStack storedItemStack){
		//System.out.println("craft: "+(storedItemStack.stack != null ? storedItemStack.stack : "null"));
		if(storedItemStack.stack != null && !player.worldObj.isRemote){
			//te.getGrid().getData().queueCrafting(storedItemStack.stack, storedItemStack.itemCount, player);
			try{
				currentReport = te.getGrid().getData().calculateCrafting(storedItemStack, storedItemStack.itemCount);
				isCraftingReport = true;
				dataSent = false;
			}catch(Throwable e){
				String msg = e.getMessage();
				TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.craftFail", new TextComponentTranslation(msg != null ? msg : "tomsMod.na"));
			}
		}
	}
	@Override
	@SideOnly(Side.CLIENT)
	/**Use {@link #sendToCrafter(IContainerListener)}, {@link #afterSending()}*/
	public final void updateProgressBar(int id, int data) {
		if(id == 0){
			terminalType = data;
		}else if(id == 1){
			te.setClientPowered(data == 1);
		}else onProgressBarUpdate(id, data);
	}
	@Override
	protected final void addPlayerSlots(InventoryPlayer playerInventory, int x, int y) {
		this.playerSlotsStart = inventorySlots.size() - 1;
		super.addPlayerSlots(playerInventory, x, y);
	}
	@Override
	protected final void addPlayerSlotsExceptHeldItem(InventoryPlayer playerInventory, int x, int y) {
		this.playerSlotsStart = inventorySlots.size() - 1;
		super.addPlayerSlotsExceptHeldItem(playerInventory, x, y);
	}
	public void sendToCrafter(IContainerListener crafter){}
	public void afterSending(){}
	public void onProgressBarUpdate(int id, int data){}
	public void onButtonPressed(int id, EntityPlayer player, int extra){}
	public ItemStack shiftClickItems(EntityPlayer playerIn, int index){return null;}
}
