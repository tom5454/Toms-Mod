package com.tom.storage.tileentity.inventory;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
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

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.tom.api.grid.StorageNetworkGrid.ControllMode;
import com.tom.api.grid.StorageNetworkGrid.SearchType;
import com.tom.api.inventory.StoredItemStack;
import com.tom.handler.TMPlayerHandler;
import com.tom.lib.network.GuiSyncHandler.IPacketReceiver;
import com.tom.lib.utils.RenderUtil;
import com.tom.storage.handler.AutoCraftingHandler;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.ITerminal;
import com.tom.storage.handler.InventoryCache;
import com.tom.storage.handler.StorageData;
import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.TileEntityBasicTerminal.TerminalState;
import com.tom.storage.tileentity.gui.GuiTerminalBase;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerConfigurator.SlotData;
import com.tom.core.tileentity.inventory.ContainerTomsMod;

/** Gui: GuiTerminalBase */
public class ContainerTerminalBase extends ContainerTomsMod implements IPacketReceiver, Function<AutoCraftingHandler.CraftingCalculationResult, Void> {
	public List<StoredItemStack> itemList = Lists.<StoredItemStack>newArrayList();
	public List<StoredItemStack> itemListClient = Lists.<StoredItemStack>newArrayList();
	public List<StoredItemStack> itemListClientSorted = Lists.<StoredItemStack>newArrayList();
	public List<StoredItemStack> craftables = new ArrayList<>();
	public List<SlotData> slotData = new ArrayList<>();
	protected List<SlotStorage> storageSlotList = new ArrayList<>();
	protected int playerSlotsStart;
	protected EntityPlayer player;
	public boolean isCraftingReport = false;
	public AutoCraftingHandler.CalculatedCrafting currentReport;
	private boolean dataSent = false, calculated = false, wideTerm, tallTerm;
	protected ITerminal te;
	public int terminalType, lines;
	private int sortingData, searchType, controllMode;

	public ContainerTerminalBase(ITerminal terminal, EntityPlayer player) {
		te = terminal;
		this.player = player;
		syncHandler.registerShort(0, te::getTerminalMode, i -> terminalType = i);
		syncHandler.registerEnum(1, te::getTerminalState, te::setClientState, TerminalState.VALUES);
		syncHandler.setReceiver(terminal);
	}

	public final void addStorageSlots(int lines, int x, int y) {
		storageSlotList.clear();
		this.lines = lines;
		for (int i = 0;i < lines;++i) {
			for (int j = 0;j < 9;++j) {
				this.addSlotToContainer(new SlotStorage(this.te, i * 9 + j, x + j * 18, y + i * 18));
			}
		}
		scrollTo(0.0F);
	}

	public static enum SlotAction {
		PULL_OR_PUSH_STACK, PULL_ONE, CRAFT, SPACE_CLICK, SHIFT_PULL, GET_HALF, GET_QUARTER;
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
		public final ITerminal inventory;
		public StoredItemStack stack;

		public SlotStorage(ITerminal inventory, int slotIndex, int xPosition, int yPosition) {
			this.xDisplayPosition = xPosition;
			this.yDisplayPosition = yPosition;
			this.slotIndex = slotIndex;
			this.inventory = inventory;
		}

		public ItemStack pullFromSlot(long max) {
			if (stack == null || max < 1)
				return ItemStack.EMPTY;
			StoredItemStack r = inventory.getStorageInventory().pullStack(stack, max);
			if (r != null) {
				return r.getActualStack();
			} else
				return ItemStack.EMPTY;
		}

		public ItemStack pushStack(ItemStack pushStack) {
			StoredItemStack r = inventory.getStorageInventory().pushStack(new StoredItemStack(pushStack, pushStack.getCount()));
			if (r != null) {
				return r.getActualStack();
			} else
				return ItemStack.EMPTY;
		}

		public int getSlotIndex() {
			return slotIndex;
		}

		@SideOnly(Side.CLIENT)
		public void drawSlot(GuiTerminalBase gui, int mouseX, int mouseY) {
			if (mouseX >= gui.getGuiLeft() + xDisplayPosition - 1 && mouseY >= gui.getGuiTop() + yDisplayPosition - 1 && mouseX < gui.getGuiLeft() + xDisplayPosition + 17 && mouseY < gui.getGuiTop() + yDisplayPosition + 17) {
				RenderUtil.setColourWithAlphaPercent(0xFFFFFF, 60);
				RenderUtil.drawRect(gui.getGuiLeft() + xDisplayPosition, gui.getGuiTop() + yDisplayPosition, 16, 16);
			}
			if (gui.powered == TileEntityBasicTerminal.TerminalState.ACTIVE) {
				if (stack != null) {
					GL11.glPushMatrix();
					gui.renderItemInGui(stack.getStack().copy().splitStack(1), gui.getGuiLeft() + xDisplayPosition, gui.getGuiTop() + yDisplayPosition, 0, 0, false, 0xFFFFFF, false);
					this.drawStackSize(gui.getFontRenderer(stack.getStack()), stack.getQuantity(), gui.getGuiLeft() + xDisplayPosition, gui.getGuiTop() + yDisplayPosition);
					GL11.glPopMatrix();
				}
			} else {
				gui.bindList();
				gui.drawTexturedModalRect(gui.getGuiLeft() + xDisplayPosition - 1, gui.getGuiTop() + yDisplayPosition - 1, 125, 120, 18, 18);
			}
		}

		@SideOnly(Side.CLIENT)
		public boolean drawTooltip(GuiTerminalBase gui, int mouseX, int mouseY) {
			if (stack != null) {
				if (stack.getQuantity() > 9999) {
					gui.renderItemInGui(stack.getStack(), gui.getGuiLeft() + xDisplayPosition, gui.getGuiTop() + yDisplayPosition, mouseX, mouseY, false, 0, true, I18n.format("tomsmod.gui.amount", stack.getQuantity()));
				} else {
					gui.renderItemInGui(stack.getStack(), gui.getGuiLeft() + xDisplayPosition, gui.getGuiTop() + yDisplayPosition, mouseX, mouseY, false, 0, true);
				}
			}
			return mouseX >= (gui.getGuiLeft() + xDisplayPosition) - 1 && mouseY >= (gui.getGuiTop() + yDisplayPosition) - 1 && mouseX < (gui.getGuiLeft() + xDisplayPosition) + 17 && mouseY < (gui.getGuiTop() + yDisplayPosition) + 17;
		}

		@SideOnly(Side.CLIENT)
		private void drawStackSize(FontRenderer fr, long size, int x, int y) {
			float scaleFactor = 0.6f;
			boolean unicodeFlag = fr.getUnicodeFlag();
			fr.setUnicodeFlag(false);
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.disableBlend();
			String stackSize = "?";
			if (size == 0) {
				stackSize = I18n.format("tomsMod.storage.craft");
				scaleFactor = 0.55F;
			} else {
				stackSize = TomsModUtils.formatNumber(size);
			}
			GL11.glPushMatrix();
			GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
			float inverseScaleFactor = 1.0f / scaleFactor;
			int X = (int) (((float) x + 0 + 16.0f - fr.getStringWidth(stackSize) * scaleFactor) * inverseScaleFactor);
			int Y = (int) (((float) y + 0 + 16.0f - 7.0f * scaleFactor) * inverseScaleFactor);
			fr.drawStringWithShadow(stackSize, X, Y, 16777215);
			GL11.glPopMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			fr.setUnicodeFlag(unicodeFlag);
		}
	}

	@Override
	public final void buttonPressed(EntityPlayer player, int id, int extra) {
		TMPlayerHandler playerH = TMPlayerHandler.getPlayerHandlerForName(player.getName());
		if (id == 0) {
			playerH.setItemSorting(extra);
		} else if (id == 1) {
			playerH.setSearchBoxType(extra % SearchType.VALUES.length);
		} else if (id == 2) {
			int controllMode = extra % 0x10;
			playerH.wideTerminal = TomsModUtils.getBit(extra, 4);
			playerH.tallTerminal = TomsModUtils.getBit(extra, 5);
			playerH.controllMode = ControllMode.VALUES[controllMode % ControllMode.VALUES.length];
		} else if (id == 3) {
			TomsModUtils.sendChatTranslate(player, "WIP");
		} else
			onButtonPressed(id, player, extra);
	}

	protected final void addSlotToContainer(SlotStorage slotStorage) {
		storageSlotList.add(slotStorage);
	}

	@Override
	protected Slot addSlotToContainer(Slot slotIn) {
		slotData.add(new SlotData(slotIn));
		return super.addSlotToContainer(slotIn);
	}

	protected Slot addSlotToContainerS(Slot slotIn) {
		return super.addSlotToContainer(slotIn);
	}

	public void setOffset(int x, int y) {
		slotData.forEach(d -> d.setOffset(x, y));
	}

	/**
	 * Use {@link #sendToCrafter(IContainerListener)}, {@link #afterSending()}
	 */
	@Override
	public final void detectAndSendChanges() {
		player.world.profiler.startSection("updateTerminal");
		super.detectAndSendChanges();
		if (!isCraftingReport) {
			List<StoredItemStack> itemListOld = this.itemList;
			List<StoredItemStack> craftablesOld = this.craftables;
			craftables = te.getStorageInventory().getCraftableStacks(InventoryCache.class);
			itemList = te.getStorageInventory().getStacks(InventoryCache.class);
			boolean sendUpdate = false;
			NBTTagList list = new NBTTagList();
			NBTTagList cList = new NBTTagList();
			if (itemList.size() != itemListOld.size())
				sendUpdate = true;
			if (craftables.size() != craftablesOld.size())
				sendUpdate = true;
			for (int i = 0;i < itemList.size();i++) {
				StoredItemStack storedS = itemList.get(i);
				StoredItemStack storedSOld = itemListOld.size() > i ? itemListOld.get(i) : null;
				if (storedSOld == null || (!(storedS.equals(storedSOld) && storedS.getQuantity() == storedSOld.getQuantity()))) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setInteger("slot", i);
					if (storedS != null)
						storedS.writeToNBT(tag);
					list.appendTag(tag);
					sendUpdate = true;
				}
			}
			for (int i = 0;i < craftables.size();i++) {
				StoredItemStack stack = craftables.get(i);
				StoredItemStack stackOld = craftablesOld.size() > i ? craftablesOld.get(i) : null;
				if (stackOld == null || (!stackOld.isEqual(stack))) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setInteger("slot", i);
					if (stack != null)
						stack.writeToNBT(tag);
					tag.setByte("c", (byte) 1);
					cList.appendTag(tag);
					sendUpdate = true;
				}
			}
			NBTTagCompound mainTag = new NBTTagCompound();
			mainTag.setTag("l", list);
			mainTag.setInteger("s", itemList.size());
			mainTag.setInteger("v", craftables.size());
			mainTag.setTag("a", cList);
			TMPlayerHandler playerH = TMPlayerHandler.getPlayerHandlerForName(player.getName());
			int comp = playerH.getItemSortingMode(), search = playerH.getSearchBoxType(),
					controll = playerH.controllMode.ordinal();
			if (comp != sortingData || search != searchType || controll != controllMode || playerH.wideTerminal != wideTerm || playerH.tallTerminal != tallTerm)
				sendUpdate = true;
			sortingData = comp;
			searchType = search;
			controllMode = controll;
			wideTerm = playerH.wideTerminal;
			tallTerm = playerH.tallTerminal;
			mainTag.setInteger("d", comp);
			mainTag.setInteger("t", search);
			int termMode = controll;
			termMode = TomsModUtils.setBit(termMode, 4, playerH.wideTerminal);
			termMode = TomsModUtils.setBit(termMode, 5, playerH.tallTerminal);
			mainTag.setByte("cm", (byte) termMode);
			mainTag.setBoolean("r", false);
			if (sendUpdate)syncHandler.sendNBTToGui(mainTag);
			for (IContainerListener crafter : listeners) {
				sendToCrafter(crafter);
			}
			afterSending();
		} else {
			if (!dataSent) {
				if (calculated) {
					if (currentReport != null) {
						AutoCraftingHandler.CompiledCalculatedCrafting c = te.getData().compileCalculatedCrafting(currentReport);
						NBTTagCompound mainTag = new NBTTagCompound();
						mainTag.setBoolean("r", true);
						TMPlayerHandler playerH = TMPlayerHandler.getPlayerHandlerForName(player.getName());
						int termMode = playerH.controllMode.ordinal();
						termMode = TomsModUtils.setBit(termMode, 4, playerH.wideTerminal);
						termMode = TomsModUtils.setBit(termMode, 5, playerH.tallTerminal);
						mainTag.setByte("cm", (byte) termMode);
						c.sendTo(syncHandler, mainTag);
					} else {
						NBTTagCompound mainTag = new NBTTagCompound();
						mainTag.setBoolean("ERROR", true);
						syncHandler.sendNBTToGui(mainTag);
					}
				} else {
					NBTTagCompound mainTag = new NBTTagCompound();
					mainTag.setBoolean("r", true);
					syncHandler.sendNBTToGui(mainTag);
				}
				dataSent = true;
			} else {
				TMPlayerHandler playerH = TMPlayerHandler.getPlayerHandlerForName(player.getName());
				if (wideTerm != playerH.wideTerminal || playerH.tallTerminal != tallTerm) {
					wideTerm = playerH.wideTerminal;
					tallTerm = playerH.tallTerminal;
					NBTTagCompound mainTag = new NBTTagCompound();
					mainTag.setBoolean("r", true);
					int termMode = playerH.controllMode.ordinal();
					termMode = TomsModUtils.setBit(termMode, 4, playerH.wideTerminal);
					termMode = TomsModUtils.setBit(termMode, 5, playerH.tallTerminal);
					mainTag.setByte("cm", (byte) termMode);
					syncHandler.sendNBTToGui(mainTag);
				}
			}
		}
		player.world.profiler.endSection();
	}

	public final void setSlotContents(int id, StoredItemStack stack) {
		storageSlotList.get(id).stack = stack;
	}

	/**
	 * Updates the gui slots ItemStack's based on scroll position.
	 */
	public final void scrollTo(float p_148329_1_) {
		int i = (this.itemListClientSorted.size() + 9 - 1) / 9 - lines;
		int j = (int) (p_148329_1_ * i + 0.5D);

		if (j < 0) {
			j = 0;
		}

		for (int k = 0;k < lines;++k) {
			for (int l = 0;l < 9;++l) {
				int i1 = l + (k + j) * 9;

				if (i1 >= 0 && i1 < this.itemListClientSorted.size()) {
					setSlotContents(l + k * 9, this.itemListClientSorted.get(i1));
				} else {
					setSlotContents(l + k * 9, null);
				}
			}
		}
	}

	public final void receiveClientNBTPacket(NBTTagCompound message) {
		// StoredItemStack[] stackArray = itemList.toArray(new
		// StoredItemStack[itemList.size()]);
		int size = message.getInteger("s");
		int cSize = message.getInteger("v");
		NBTTagList list = message.getTagList("l", 10);
		NBTTagList cList = message.getTagList("a", 10);
		((ArrayList<StoredItemStack>) itemList).ensureCapacity(size);
		((ArrayList<StoredItemStack>) craftables).ensureCapacity(cSize);
		for (int i = 0;i < list.tagCount();i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("slot");
			/*boolean n = tag.getBoolean("n");
			if(n){
				//stackArray[slot] = null;
				itemList.remove(slot);
			}else{*/
			if (itemList.size() > slot)
				itemList.set(slot, StoredItemStack.readFromNBT(tag));
			else
				itemList.add(slot, StoredItemStack.readFromNBT(tag));
			// }
		}
		for (int i = size;i < itemList.size();i++) {
			itemList.remove(i);
		}
		for (int i = 0;i < cList.tagCount();i++) {
			NBTTagCompound tag = cList.getCompoundTagAt(i);
			int slot = tag.getInteger("slot");
			/*boolean n = tag.getBoolean("n");
			if(n){
				//stackArray[slot] = null;
				itemList.remove(slot);
			}else{*/
			if (craftables.size() > slot)
				craftables.set(slot, StoredItemStack.readFromNBT(tag));
			else
				craftables.add(slot, StoredItemStack.readFromNBT(tag));
			// }
		}
		for (int i = cSize;i < craftables.size();i++) {
			StoredItemStack removed = craftables.remove(i);
			if (removed != null) {
				StoredItemStack storedS = removed.copy();
				removed.setQuantity(-1);
				if (itemList.contains(storedS)) {
					itemList.remove(storedS);
				}
			}
		}
		itemListClient = new ArrayList<>(itemList);
		for (int i = 0;i < craftables.size();i++) {
			StoredItemStack storedS = craftables.get(i);
			if (storedS != null) {
				storedS = storedS.copy();
				storedS.setQuantity(0);
			}
			// if(!itemListClient.contains(storedS)){
			itemListClient.add(storedS);
			// }
		}
	}

	@Override
	public final ItemStack slotClick(int slotId, int clickedButton, ClickType clickTypeIn, EntityPlayer playerIn) {
		SlotAction mode = SlotAction.VALUES[clickTypeIn.ordinal()];
		if (slotId < -1 && slotId != -999 && mode != SlotAction.SPACE_CLICK && !(mode == SlotAction.CRAFT)) {
			if (mode == SlotAction.PULL_OR_PUSH_STACK) {
				SlotStorage slot = storageSlotList.get(0);
				ItemStack stack = playerIn.inventory.getItemStack();
				if (!stack.isEmpty()) {
					ItemStack itemstack = slot.pushStack(stack);
					playerIn.inventory.setItemStack(itemstack);
					return itemstack;
				} else {
					if (itemList.size() > clickedButton) {
						slot.stack = itemList.get(clickedButton);
						if (slot.stack.getQuantity() == 0) {
							// craft(playerIn, slot);
							return ItemStack.EMPTY;
						} else {
							ItemStack itemstack = slot.pullFromSlot(64);
							playerIn.inventory.setItemStack(itemstack);
							return itemstack;
						}
					} else
						return ItemStack.EMPTY;
				}
			} else if (mode == SlotAction.PULL_ONE) {
				SlotStorage slot = storageSlotList.get(0);
				ItemStack stack = playerIn.inventory.getItemStack();
				if (slotId == -3) {
					if (itemList.size() > clickedButton) {
						slot.stack = itemList.get(clickedButton);
						ItemStack itemstack = slot.pullFromSlot(1);
						if (!itemstack.isEmpty()) {
							this.mergeItemStack(itemstack, playerSlotsStart + 1, this.inventorySlots.size(), true);
							if (itemstack.getCount() > 0)
								slot.pushStack(itemstack);
						}
						if (!playerIn.world.isRemote)
							detectAndSendChanges();
						playerIn.inventory.markDirty();
						return stack;
					} else
						return stack;
				} else {
					if (!stack.isEmpty()) {
						slot.stack = itemList.get(clickedButton);
						if (TomsModUtils.areItemStacksEqual(stack, slot.stack.getStack(), true, true, false) && stack.getCount() + 1 <= stack.getMaxStackSize()) {
							ItemStack itemstack = slot.pullFromSlot(1);
							if (!itemstack.isEmpty()) {
								stack.grow(1);
								return stack;
							}
						}
					} else {
						if (itemList.size() > clickedButton) {
							slot.stack = itemList.get(clickedButton);
							ItemStack itemstack = slot.pullFromSlot(1);
							playerIn.inventory.setItemStack(itemstack);
							return itemstack;
						} else
							return ItemStack.EMPTY;
					}
				}
				return ItemStack.EMPTY;
			} else if (mode == SlotAction.GET_HALF) {
				SlotStorage slot = storageSlotList.get(0);
				ItemStack stack = playerIn.inventory.getItemStack();
				if (!stack.isEmpty()) {
					ItemStack stack1 = stack.splitStack(Math.min(stack.getCount(), stack.getMaxStackSize()) / 2);
					ItemStack itemstack = slot.pushStack(stack1);
					stack.grow(!itemstack.isEmpty() ? itemstack.getCount() : 0);
					playerIn.inventory.setItemStack(stack);
					return stack;
				} else {
					if (itemList.size() > clickedButton) {
						slot.stack = itemList.get(clickedButton);
						ItemStack itemstack = slot.pullFromSlot(Math.min(slot.stack.getQuantity(), slot.stack.getStack().getMaxStackSize()) / 2);
						playerIn.inventory.setItemStack(itemstack);
						return itemstack;
					} else
						return ItemStack.EMPTY;
				}
			} else if (mode == SlotAction.GET_QUARTER) {
				SlotStorage slot = storageSlotList.get(0);
				ItemStack stack = playerIn.inventory.getItemStack();
				if (!stack.isEmpty()) {
					ItemStack stack1 = stack.splitStack(Math.min(stack.getCount(), stack.getMaxStackSize()) / 4);
					ItemStack itemstack = slot.pushStack(stack1);
					stack.grow(!itemstack.isEmpty() ? itemstack.getCount() : 0);
					playerIn.inventory.setItemStack(stack);
					return stack;
				} else {
					if (itemList.size() > clickedButton) {
						slot.stack = itemList.get(clickedButton);
						ItemStack itemstack = slot.pullFromSlot(Math.min(slot.stack.getQuantity(), slot.stack.getStack().getMaxStackSize()) / 4);
						playerIn.inventory.setItemStack(itemstack);
						return itemstack;
					} else
						return ItemStack.EMPTY;
				}
			} else if (mode == SlotAction.PULL_ONE) {
				SlotStorage slot = storageSlotList.get(0);
				ItemStack stack = playerIn.inventory.getItemStack();
				if (!stack.isEmpty()) {
					slot.stack = itemList.get(clickedButton);
					// if(TomsModUtils.areItemStacksEqual(stack,
					// slot.stack.stack, true, true, false)){
					ItemStack s = stack.splitStack(1);
					ItemStack s2 = slot.pushStack(s);
					if (!s2.isEmpty()) {
						stack.grow(s2.getCount());
					}
					if (stack.isEmpty()) {
						stack = ItemStack.EMPTY;
					}
					playerIn.inventory.setItemStack(stack);
					return stack;
					// }
				}
				return ItemStack.EMPTY;
			} else {
				SlotStorage slot = storageSlotList.get(0);
				if (itemList.size() > clickedButton && !playerIn.world.isRemote) {
					slot.stack = itemList.get(clickedButton);
					ItemStack itemstack = slot.pullFromSlot(64);
					if (!itemstack.isEmpty()) {
						this.mergeItemStack(itemstack, playerSlotsStart + 1, this.inventorySlots.size(), true);
						if (itemstack.getCount() > 0)
							slot.pushStack(itemstack);
					}
					if (!playerIn.world.isRemote)
						detectAndSendChanges();
					playerIn.inventory.markDirty();
				}
				return ItemStack.EMPTY;
			}
		} else if (slotId == -1 && mode == SlotAction.SPACE_CLICK) {
			for (int i = playerSlotsStart + 1;i < playerSlotsStart + 28;i++) {
				transferStackInSlot(playerIn, i);
			}
			return ItemStack.EMPTY;
		} else if ((slotId < 0 || slotId == 1000) && mode == SlotAction.CRAFT) {
			if (isCraftingReport) {
				isCraftingReport = false;
				craftables.clear();
				itemList.clear();
				detectAndSendChanges();
				if (clickedButton == 1 && !playerIn.world.isRemote) {
					StorageData data = te.getData();
					if (data != null)
						data.queueCrafting(currentReport.mainStack.copy(), playerIn, slotId < 0 ? (-slotId) - 1 : -1);
				}
				return playerIn.inventory.getItemStack();
			} else {
				if (!playerIn.world.isRemote && (-slotId) - 1 >= 0 && (-slotId) - 1 < craftables.size())
					craft(playerIn, craftables.get((-slotId) - 1).copy(clickedButton));
				return playerIn.inventory.getItemStack();
			}
		} else
			return super.slotClick(slotId, clickedButton, clickTypeIn, playerIn);
	}

	@SideOnly(Side.CLIENT)
	public final int drawSlots(GuiTerminalBase gui, int mouseX, int mouseY) {
		for (int i = 0;i < storageSlotList.size();i++) {
			storageSlotList.get(i).drawSlot(gui, mouseX, mouseY);
		}
		for (int i = 0;i < storageSlotList.size();i++) {
			if (storageSlotList.get(i).drawTooltip(gui, mouseX, mouseY)) { return i; }
		}
		return -1;
	}

	public final SlotStorage getSlotByID(int id) {
		return storageSlotList.get(id);
	}

	@Override
	public final ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		if (inventorySlots.size() > index) {
			if (index > playerSlotsStart) {
				if (inventorySlots.get(index) != null && inventorySlots.get(index).getHasStack()) {
					Slot slot = inventorySlots.get(index);
					ItemStack slotStack = slot.getStack();
					ICraftable c = te.getStorageInventory().pushStack(new StoredItemStack(slotStack, slotStack.getCount()));
					ItemStack itemstack = c != null ? ((StoredItemStack) c).getActualStack() : ItemStack.EMPTY;
					slot.putStack(itemstack);
					if (!playerIn.world.isRemote)
						detectAndSendChanges();
				}
			} else {
				return shiftClickItems(playerIn, index);
			}
		}
		return ItemStack.EMPTY;
	}

	protected final void craft(EntityPlayer player, StoredItemStack storedItemStack) {
		// System.out.println("craft: "+(storedItemStack.stack != null ?
		// storedItemStack.stack : "null"));
		if (storedItemStack.getStack() != null && !player.world.isRemote) {
			// te.getGrid().getData().queueCrafting(storedItemStack.stack,
			// storedItemStack.itemCount, player);
			currentReport = null;
			calculated = false;
			te.startCrafting(storedItemStack, this);
			isCraftingReport = true;
			dataSent = false;
		}
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

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public void sendToCrafter(IContainerListener crafter) {
	}

	public void afterSending() {
	}

	public void onButtonPressed(int id, EntityPlayer player, int extra) {
	}

	public ItemStack shiftClickItems(EntityPlayer playerIn, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public Void apply(AutoCraftingHandler.CraftingCalculationResult input) {
		if (input.e != null) {
			String msg = input.e.getMessage();
			currentReport = null;
			calculated = true;
			dataSent = false;
			TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.craftFail", new TextComponentTranslation(msg != null ? msg : "tomsMod.na"));
			input.e.printStackTrace();
		} else {
			currentReport = input.c;
			calculated = true;
			dataSent = false;
		}
		return null;
	}
	@Override
	public void receiveNBTPacket(EntityPlayer from, NBTTagCompound message) {
		te.receiveNBTPacket(from, message);
	}
}
