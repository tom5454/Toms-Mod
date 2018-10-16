package com.tom.storage.tileentity.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import com.tom.api.gui.GuiTomsMod;
import com.tom.api.inventory.StoredItemStack;
import com.tom.api.multipart.IGuiMultipart;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.client.GuiButtonTransparent;
import com.tom.handler.TMPlayerHandler;
import com.tom.storage.StorageInit;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.ITerminal;
import com.tom.storage.handler.StorageNetworkGrid.ControllMode;
import com.tom.storage.handler.StorageNetworkGrid.IStorageTerminalGui;
import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.inventory.ContainerTerminalBase;
import com.tom.storage.tileentity.inventory.ContainerTerminalBase.SlotAction;
import com.tom.thirdparty.jei.JEIHandler;
import com.tom.util.TomsModUtils;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.IPartSlot;

public class GuiTerminalBase extends GuiTomsMod implements INBTPacketReceiver, IStorageTerminalGui {
	/** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
	protected float currentScroll;
	/** True if the scrollbar is being dragged */
	protected boolean isScrolling;
	/**
	 * True if the left mouse button was held down last time drawScreen was
	 * called.
	 */
	protected boolean wasClicking;
	protected GuiTextField searchField;
	protected int slotIDUnderMouse = -1, sortData, searchType, controllMode, rowCount;
	protected String searchLast = "";
	protected boolean jeiSync = false, tallTerm, wideTerm;
	protected static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	public Comparator<ICraftable> comparator = new ICraftable.CraftableComparatorAmount(false);
	protected GuiButtonSortingType buttonSortingType;
	protected ITerminal te;
	protected GuiButtonSortingDir buttonDirection;
	protected GuiButtonSearchType buttonSearchType;
	protected GuiButtonViewType buttonViewType;
	protected GuiButtonTransparent buttonCraftings;
	protected GuiButtonControllMode buttonCtrlMode;
	protected GuiButtonTermMode buttonMode;
	public final int textureSlotCount, guiHeight, slotStartX, slotStartY;
	public TileEntityBasicTerminal.TerminalState powered;
	private boolean skipResult;

	public GuiTerminalBase(ContainerTerminalBase inv, String guiTexture, ITerminal terminal, int textureSlotCount, int guiHeight, int slotStartX, int slotStartY) {
		super(inv, guiTexture);
		te = terminal;
		this.textureSlotCount = textureSlotCount;
		this.guiHeight = guiHeight;
		this.slotStartX = slotStartX;
		this.slotStartY = slotStartY;
	}

	public static class GuiButtonSortingType extends GuiButton {
		public int type = 0;

		public GuiButtonSortingType(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				FontRenderer fontrenderer = mc.fontRenderer;
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 191 + 16 * type, 50, this.width, this.height);
				// this.drawTexturedModalRect(this.xPosition + this.width / 2,
				// this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width
				// / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
				/*int j = 14737632;

				if (packedFGColour != 0)
				{
				    j = packedFGColour;
				}
				else
				if (!this.enabled)
				{
				    j = 10526880;
				}
				else if (this.hovered)
				{
				    j = 16777120;
				}*/

				if (type == 2)
					fontrenderer.drawString("@", this.x + this.width / 2 - 3, this.y + (this.height - 6) / 2, 0x404040, false);
			}
		}
	}

	public static class GuiButtonSortingDir extends GuiButton {
		public boolean dir = false;

		public GuiButtonSortingDir(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 175, 34 + (dir ? 16 : 0), this.width, this.height);
				// this.drawTexturedModalRect(this.xPosition + this.width / 2,
				// this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width
				// / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public static class GuiButtonSearchType extends GuiButton {
		public int type = 0;

		public GuiButtonSearchType(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 191 + type * 16, 34, this.width, this.height);
				// this.drawTexturedModalRect(this.xPosition + this.width / 2,
				// this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width
				// / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public static class GuiButtonViewType extends GuiButton {
		public int type = 0;

		public GuiButtonViewType(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 175 + type * 16, 97, this.width, this.height);
				// this.drawTexturedModalRect(this.xPosition + this.width / 2,
				// this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width
				// / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public static class GuiButtonControllMode extends GuiButton {
		public int type = 0;

		public GuiButtonControllMode(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 175 + type * 16, 129, this.width, this.height);
				// this.drawTexturedModalRect(this.xPosition + this.width / 2,
				// this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width
				// / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public static class GuiButtonTermMode extends GuiButton {
		public int type = 0;

		public GuiButtonTermMode(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 175 + type * 16, 145, this.width, this.height);
				// this.drawTexturedModalRect(this.xPosition + this.width / 2,
				// this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width
				// / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	protected void windowClick(int i, int j, SlotAction pullOne) {
		mc.playerController.windowClick(this.inventorySlots.windowId, i, j, ClickType.values()[pullOne.ordinal()], this.mc.player);
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		boolean isReport = message.getBoolean("r");
		if (!isReport) {
			this.getContainer().receiveClientNBTPacket(message);
			int data = message.getInteger("d");
			int searchBoxType = message.getInteger("t");
			boolean canLoseFocus = searchBoxType % 2 == 1;
			// terminalType = message.getInteger("c");
			if (searchField != null) {
				searchField.setCanLoseFocus(canLoseFocus);
				if (!canLoseFocus && !searchField.isFocused()) {
					searchField.setFocused(true);
				}
			}
			jeiSync = searchBoxType > 1;
			ICraftable.CraftableSorting type = TMPlayerHandler.getSortingType(data);
			comparator = type.getComparator(TMPlayerHandler.getSortingDir(data));
			sortData = data;
			searchType = searchBoxType;
			byte termMode = message.getByte("cm");
			controllMode = termMode % 0x10;
			wideTerm = TomsModUtils.getBit(termMode, 4);
			boolean tallTerm = TomsModUtils.getBit(termMode, 5);
			if (tallTerm != this.tallTerm) {
				this.tallTerm = tallTerm;
				initGui();
			}
			this.updateSearch();
		} else {
			GuiCraftingReport r = new GuiCraftingReport(this, skipResult);
			mc.displayGuiScreen(r);
			if (message.getBoolean("c"))
				r.receiveNBTPacket(message);
			skipResult = false;
		}
	}

	@Override
	public void initGui() {
		buttonList.clear();
		labelList.clear();
		if (tallTerm) {
			int guiSize = guiHeight - textureSlotCount * 18;
			rowCount = (height - 20 - guiSize) / 18;
			ySize = guiSize + rowCount * 18;
			((ContainerTerminalBase) inventorySlots).setOffset(0, (rowCount - textureSlotCount) * 18);
			((ContainerTerminalBase) inventorySlots).addStorageSlots(rowCount, slotStartX + 1, slotStartY + 1);
		} else {
			ySize = guiHeight;
			rowCount = textureSlotCount;
			((ContainerTerminalBase) inventorySlots).setOffset(0, 0);
			((ContainerTerminalBase) inventorySlots).addStorageSlots(rowCount, slotStartX + 1, slotStartY + 1);
		}
		super.initGui();
		this.searchField = new GuiTextField(0, this.fontRenderer, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRenderer.FONT_HEIGHT);
		this.searchField.setMaxStringLength(100);
		this.searchField.setEnableBackgroundDrawing(false);
		this.searchField.setVisible(true);
		this.searchField.setTextColor(16777215);
		this.searchField.setText(searchLast);
		updateSearch();
		TomsModUtils.addTextFieldToLabelList(searchField, labelList);
		buttonSortingType = new GuiButtonSortingType(0, guiLeft - 18, guiTop + 5);
		buttonList.add(buttonSortingType);
		buttonDirection = new GuiButtonSortingDir(1, guiLeft - 18, guiTop + 18 + 5);
		buttonList.add(buttonDirection);
		buttonSearchType = new GuiButtonSearchType(2, guiLeft - 18, guiTop + 2 * 18 + 5);
		buttonList.add(buttonSearchType);
		buttonViewType = new GuiButtonViewType(3, guiLeft - 18, guiTop + 3 * 18 + 5);
		buttonList.add(buttonViewType);
		buttonCraftings = new GuiButtonTransparent(4, guiLeft + 172, guiTop - 2, 18, 18);
		buttonList.add(buttonCraftings);
		buttonCtrlMode = new GuiButtonControllMode(5, guiLeft - 18, guiTop + 4 * 18 + 5);
		buttonList.add(buttonCtrlMode);
		buttonMode = new GuiButtonTermMode(6, guiLeft - 18, guiTop + 5 * 18 + 5);
		buttonList.add(buttonMode);
	}

	public int convertToTall(int x) {
		return x + (rowCount - textureSlotCount) * 18;
	}

	@Override
	public void updateScreen() {
		if (te instanceof IGuiMultipart) {
			IGuiMultipart part = ((IGuiMultipart) te);
			IPartSlot p = part.getPosition();
			IMultipartContainer c = MultipartHelper.getContainer(part.getWorld2(), part.getPos2()).orElse(null);
			if (c == null) {
				TileEntity t = part.getWorld2().getTileEntity(part.getPos2());
				if (t instanceof ITerminal)
					te = (ITerminal) t;
				else {
					mc.player.closeScreen();
					return;
				}
			}
			IPartInfo i = c.get(p).orElse(null);
			if (i != null && i.getTile() != null && i.getTile() instanceof ITerminal)
				te = (ITerminal) i.getTile();
			else {
				mc.player.closeScreen();
				return;
			}
		}
		super.updateScreen();
		buttonDirection.dir = TMPlayerHandler.getSortingDir(sortData);
		buttonSortingType.type = TMPlayerHandler.getSortingType(sortData).ordinal();
		buttonSearchType.type = searchType;
		buttonViewType.type = getContainer().terminalType;
		powered = te.getTerminalState();
		buttonCtrlMode.type = controllMode;
		buttonMode.type = tallTerm ? 0 : 1;
		updateSearch();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		boolean flag = Mouse.isButtonDown(0);
		int i = this.guiLeft;
		int j = this.guiTop;
		int k = i + 174;
		int l = j + 18;
		int i1 = k + 14;
		int j1 = l + rowCount * 18;

		if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1) {
			this.isScrolling = this.needsScrollBars();
		}

		if (!flag) {
			this.isScrolling = false;
		}
		this.wasClicking = flag;

		if (this.isScrolling) {
			this.currentScroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
			this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
			getContainer().scrollTo(this.currentScroll);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
		this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
		i = k;
		j = l;
		k = j1;
		this.drawTexturedModalRect(i, j + (int) ((k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
		GL11.glPushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		slotIDUnderMouse = getContainer().drawSlots(this, mouseX, mouseY);
		GL11.glPopMatrix();
		if (buttonSortingType.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.sorting_" + buttonSortingType.type)), mouseX, mouseY);
		}
		if (buttonSearchType.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.search_" + buttonSearchType.type)), mouseX, mouseY);
		}
		if (buttonViewType.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.view_" + buttonViewType.type)), mouseX, mouseY);
		}
		if (buttonCraftings.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.craftingStatus")), mouseX, mouseY);
		}
		if (buttonCtrlMode.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.ctrlMode_" + buttonCtrlMode.type)), mouseX, mouseY);
		}
		if (buttonMode.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.displayMode_" + buttonMode.type)), mouseX, mouseY);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (slotIDUnderMouse > -1) {
			if (isPullOne(mouseButton)) {
				if (getContainer().getSlotByID(slotIDUnderMouse).stack != null && getContainer().getSlotByID(slotIDUnderMouse).stack.getQuantity() > 0) {
					for (int i = 0;i < getContainer().itemList.size();i++) {
						if (getContainer().getSlotByID(slotIDUnderMouse).stack.equals(getContainer().itemList.get(i))) {
							windowClick(isTransferOne(mouseButton) ? -3 : -2, i, SlotAction.PULL_ONE);
							return;
						}
					}
				}
				return;
			} else if (isCraft(mouseButton)) {
				if (getContainer().getSlotByID(slotIDUnderMouse).stack != null) {
					for (int i = 0;i < getContainer().craftables.size();i++) {
						if (TomsModUtils.areItemStacksEqual(getContainer().getSlotByID(slotIDUnderMouse).stack.getStack(), getContainer().craftables.get(i).getStack(), true, true, false)) {
							mc.displayGuiScreen(new GuiCraftingAmountSelection(this, getContainer().getSlotByID(slotIDUnderMouse).stack.getStack(), new ItemStack(StorageInit.basicTerminal)));
							return;
						}
					}
				}
			} else if (pullHalf(mouseButton)) {
				if (!mc.player.inventory.getItemStack().isEmpty()) {
					windowClick(-2, 0, SlotAction.GET_HALF);
				} else {
					if (getContainer().getSlotByID(slotIDUnderMouse).stack != null && getContainer().getSlotByID(slotIDUnderMouse).stack.getQuantity() > 0) {
						for (int i = 0;i < getContainer().itemList.size();i++) {
							if (getContainer().getSlotByID(slotIDUnderMouse).stack.equals(getContainer().itemList.get(i))) {
								windowClick(-2, i, isShiftKeyDown() ? SlotAction.GET_QUARTER : SlotAction.GET_HALF);
								return;
							}
						}
					}
				}
			} else if (pullNormal(mouseButton)) {
				if (!mc.player.inventory.getItemStack().isEmpty()) {
					windowClick(-(slotIDUnderMouse + 2), 0, SlotAction.PULL_OR_PUSH_STACK);
				} else {
					if (getContainer().getSlotByID(slotIDUnderMouse).stack != null) {
						if (getContainer().getSlotByID(slotIDUnderMouse).stack.getQuantity() > 0) {
							for (int i = 0;i < getContainer().itemList.size();i++) {
								if (getContainer().getSlotByID(slotIDUnderMouse).stack.equals(getContainer().itemList.get(i))) {
									windowClick(-2, i, isShiftKeyDown() ? SlotAction.SHIFT_PULL : SlotAction.PULL_OR_PUSH_STACK);
									return;
								}
							}
						} else {
							for (int i = 0;i < getContainer().craftables.size();i++) {
								if (TomsModUtils.areItemStacksEqual(getContainer().getSlotByID(slotIDUnderMouse).stack.getStack(), getContainer().craftables.get(i).getStack(), true, true, false)) {
									mc.displayGuiScreen(new GuiCraftingAmountSelection(this, getContainer().getSlotByID(slotIDUnderMouse).stack.getStack(), new ItemStack(StorageInit.basicTerminal)));
									break;
								}
							}
						}
					}
				}
			}
		} else if (Keyboard.isKeyDown(57)) {
			windowClick(-1, 0, SlotAction.SPACE_CLICK);
		} else {
			if (mouseButton == 1 && isPointInRegion(searchField.x - guiLeft, searchField.y - guiTop, searchField.width, searchField.height, mouseX, mouseY))
				searchField.setText("");
			searchField.mouseClicked(mouseX, mouseY, mouseButton);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	public ContainerTerminalBase getContainer() {
		return (ContainerTerminalBase) inventorySlots;
	}

	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = Mouse.getEventDWheel();

		if (i != 0 && this.needsScrollBars()) {
			int j = getContainer().itemListClientSorted.size() / 9 - 5;

			if (i > 0) {
				i = 1;
			}

			if (i < 0) {
				i = -1;
			}

			this.currentScroll = (float) (this.currentScroll - (double) i / (double) j);
			this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
			getContainer().scrollTo(this.currentScroll);
		}
	}

	protected boolean needsScrollBars() {
		return this.getContainer().itemListClientSorted.size() > rowCount * 9;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!this.searchField.textboxKeyTyped(typedChar, keyCode))
			super.keyTyped(typedChar, keyCode);
	}

	protected void updateSearch() {
		String searchString = searchField.getText();
		getContainer().itemListClientSorted.clear();
		boolean searchMod = false;
		if (searchString.startsWith("@")) {
			searchMod = true;
			searchString = searchString.substring(1);
		}
		Pattern m = null;
		try {
			m = Pattern.compile(searchString.toLowerCase(), Pattern.CASE_INSENSITIVE);
		} catch (Throwable ignore) {
			try {
				m = Pattern.compile(Pattern.quote(searchString.toLowerCase()), Pattern.CASE_INSENSITIVE);
			} catch (Throwable __) {
				return;
			}
		}
		boolean notDone = false;
		for (int i = 0;i < getContainer().itemListClient.size();i++) {
			StoredItemStack is = getContainer().itemListClient.get(i);
			if (is != null && is.getStack() != null) {
				String dspName = searchMod ? is.getStack().getItem().delegate.name().getResourceDomain() : is.getStack().getDisplayName();
				notDone = true;
				if (m.matcher(dspName.toLowerCase()).find()) {
					addStackToClientList(is);
					notDone = false;
				}
				if (notDone) {
					for (String lp : is.getStack().getTooltip(mc.player, getTooltipFlag())) {
						if (m.matcher(lp).find()) {
							addStackToClientList(is);
							notDone = false;
							break;
						}
					}
				}
			}
		}
		Collections.sort(getContainer().itemListClientSorted, comparator);
		if (!searchLast.equals(searchString)) {
			getContainer().scrollTo(0);
			this.currentScroll = 0;
			if (jeiSync)
				JEIHandler.setJeiSearchText(searchString);
		} else {
			getContainer().scrollTo(this.currentScroll);
		}
		this.searchLast = searchString;
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (slotIDUnderMouse == -1)
			super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			ICraftable.CraftableSorting type = ICraftable.CraftableSorting.get(TMPlayerHandler.getSortingType(sortData).ordinal() + 1);
			boolean dir = TMPlayerHandler.getSortingDir(sortData);
			sendButtonUpdate(0, TMPlayerHandler.getItemSortingMode(type, dir));
		} else if (button.id == 1) {
			ICraftable.CraftableSorting type = TMPlayerHandler.getSortingType(sortData);
			boolean dir = !TMPlayerHandler.getSortingDir(sortData);
			sendButtonUpdate(0, TMPlayerHandler.getItemSortingMode(type, dir));
		} else if (button.id == 2) {
			sendButtonUpdate(1, searchType + 1);
		} else if (button.id == 3) {
			te.sendUpdate(0, getContainer().terminalType + 1, this);
		} else if (button.id == 4) {
			sendButtonUpdate(3);
		} else if (button.id == 5) {
			int termMode = controllMode + 1;
			termMode = TomsModUtils.setBit(termMode, 4, wideTerm);
			termMode = TomsModUtils.setBit(termMode, 5, tallTerm);
			sendButtonUpdate(2, termMode);
		} else if (button.id == 6) {
			int termMode = controllMode;
			termMode = TomsModUtils.setBit(termMode, 4, wideTerm);
			termMode = TomsModUtils.setBit(termMode, 5, !tallTerm);
			sendButtonUpdate(2, termMode);
		}
	}

	private void addStackToClientList(StoredItemStack is) {
		if (getContainer().terminalType == 0)
			getContainer().itemListClientSorted.add(is);
		else if (getContainer().terminalType == 1) {
			if (is.getQuantity() > 0)
				getContainer().itemListClientSorted.add(is);
		} else if (getContainer().terminalType == 2) {
			if (is.getQuantity() == 0)
				getContainer().itemListClientSorted.add(is);
		}
	}

	@Override
	public void sendCrafting(int cpuId, boolean doCraft) {
		windowClick(cpuId == -1 ? 1000 : -(cpuId + 1), doCraft ? 1 : 0, SlotAction.CRAFT);
	}

	@Override
	public GuiScreen getScreen() {
		return this;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(gui);
		if (tallTerm) {
			this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, slotStartY);
			int guiStart = textureSlotCount * 18 + slotStartY;
			int guiRStart = rowCount * 18 + slotStartY;
			int guiSize = guiHeight - textureSlotCount * 18 - slotStartY;
			this.drawTexturedModalRect(this.guiLeft, this.guiTop + guiRStart, 0, guiStart, this.xSize, guiSize);
			int scrollbarW = 25;
			this.drawTexturedModalRect(this.guiLeft, this.guiTop + slotStartY, 0, slotStartY, slotStartX + 9 * 18 + scrollbarW, 18);
			for (int i = 1;i < rowCount - 1;i++) {
				this.drawTexturedModalRect(this.guiLeft, this.guiTop + slotStartY + i * 18, 0, slotStartY + 18, slotStartX + 9 * 18 + scrollbarW, 18);
			}
			this.drawTexturedModalRect(this.guiLeft, this.guiTop + slotStartY + (rowCount - 1) * 18, 0, slotStartY + (textureSlotCount - 1) * 18, slotStartX + 9 * 18 + scrollbarW, 18);
			this.drawTexturedModalRect(this.guiLeft + slotStartX + 9 * 18 + 25, this.guiTop, slotStartX + 9 * 18 + 25, 0, xSize - (slotStartX + 9 * 18 + 24), guiHeight);
		} else
			this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		mc.getTextureManager().bindTexture(LIST_TEXTURE);
		drawTexturedModalRect(guiLeft + 169, guiTop - 4, 0, 220, 25, 7);
		drawTexturedModalRect(guiLeft + 175, guiTop + 1, 25, 220, 12, 12);
	}

	@Override
	public void openCraftingReport(ItemStack stack, int amount, boolean show) {
		for (int i = 0;i < getContainer().craftables.size();i++) {
			if (TomsModUtils.areItemStacksEqual(stack, getContainer().craftables.get(i).getStack(), true, true, false)) {
				windowClick(-(i + 1), amount, SlotAction.CRAFT);
				if (!show)
					skipResult = true;
				return;
			}
		}
	}

	public void sendButtonUpdateT(int id, ITerminal term, BlockPos pos) {
		if (term instanceof IGuiMultipart)
			sendButtonUpdateP(id, (IGuiMultipart) term);
		else
			sendButtonUpdate(id, pos);
	}

	public void sendButtonUpdateT(int id, ITerminal term, int extraData, BlockPos pos) {
		if (term instanceof IGuiMultipart)
			sendButtonUpdateP(id, (IGuiMultipart) term, extraData);
		else
			sendButtonUpdate(id, pos, extraData);
	}

	public void bindList() {
		mc.renderEngine.bindTexture(LIST_TEXTURE);
	}

	@Override
	public void drawAsBackground(float partialTicks) {
		drawScreen(-256, -256, partialTicks);
	}

	public ItemStack getStackUnderMouse(int mouseX, int mouseY) {
		if (slotIDUnderMouse == -1)
			return null;
		StoredItemStack s = getContainer().getSlotByID(slotIDUnderMouse).stack;
		return s != null ? s.getStack().copy() : null;
	}

	public boolean isPullOne(int mouseButton) {
		switch (ctrlm()) {
		case AE:
			return isCtrlKeyDown();
		case RS:
			return mouseButton == 2;
		default:
			return false;
		}
	}

	public boolean isTransferOne(int mouseButton) {
		switch (ctrlm()) {
		case AE:
			return isShiftKeyDown() && isCtrlKeyDown();
		case RS:
			return isShiftKeyDown() && mouseButton == 2;
		default:
			return false;
		}
	}

	public boolean isCraft(int mouseButton) {
		switch (ctrlm()) {
		case AE:
			return mouseButton == 2;
		case RS:
			return isShiftKeyDown() && isCtrlKeyDown() && mouseButton == 0;
		default:
			return false;
		}
	}

	public boolean pullHalf(int mouseButton) {
		switch (ctrlm()) {
		case AE:
			return mouseButton == 1;
		case RS:
			return mouseButton == 1;
		default:
			return false;
		}
	}

	public boolean pullNormal(int mouseButton) {
		switch (ctrlm()) {
		case AE:
			return mouseButton == 0;
		case RS:
			return mouseButton == 0;
		default:
			return false;
		}
	}

	private ControllMode ctrlm() {
		return ControllMode.VALUES[controllMode];
	}

	@Override
	public boolean isTall() {
		return tallTerm;
	}

	@Override
	public boolean isWide() {
		return wideTerm;
	}

	@Override
	public void sendDisplayMode(boolean wide, boolean tall) {
		int termMode = controllMode;
		termMode = TomsModUtils.setBit(termMode, 4, wide);
		termMode = TomsModUtils.setBit(termMode, 5, tall);
		sendButtonUpdate(2, termMode);
	}

	@Override
	public void setDisplayMode(boolean wide, boolean tall) {
		this.wideTerm = wide;
		this.tallTerm = tall;
	}
}
