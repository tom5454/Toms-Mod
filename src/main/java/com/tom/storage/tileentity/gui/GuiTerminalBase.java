package com.tom.storage.tileentity.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.tom.api.inventory.StoredItemStack;
import com.tom.api.multipart.IGuiMultipart;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.apis.TomsModUtils;
import com.tom.handler.PlayerHandler;
import com.tom.storage.StorageInit;
import com.tom.storage.multipart.StorageNetworkGrid.IStorageTerminalGui;
import com.tom.storage.multipart.StorageNetworkGrid.ITerminal;
import com.tom.storage.multipart.StorageNetworkGrid.StorageItemStackComparatorAmount;
import com.tom.storage.multipart.StorageNetworkGrid.StorageItemStackSorting;
import com.tom.storage.tileentity.gui.GuiCraftingAmountSelection.GuiButtonHidden;
import com.tom.storage.tileentity.inventory.ContainerTerminalBase;
import com.tom.storage.tileentity.inventory.ContainerTerminalBase.SlotAction;
import com.tom.thirdparty.jei.JEIHandler;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiTerminalBase extends GuiTomsMod implements
INBTPacketReceiver, IStorageTerminalGui{
	/** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
	protected float currentScroll;
	/** True if the scrollbar is being dragged */
	protected boolean isScrolling;
	/** True if the left mouse button was held down last time drawScreen was called. */
	protected boolean wasClicking;
	protected GuiTextField searchField;
	protected int slotIDUnderMouse = -1, sortData, searchType;
	protected String searchLast = "";
	protected boolean jeiSync = false;
	protected static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	public Comparator<StoredItemStack> comparator = new StorageItemStackComparatorAmount(false);
	//protected static final ResourceLocation LIST_TEXTURE = new ResourceLocation("tomsmod:textures/gui/resSelect.png");
	protected GuiButtonSortingType buttonSortingType;
	protected ITerminal te;
	protected GuiButtonSortingDir buttonDirection;
	protected GuiButtonSearchType buttonSearchType;
	protected GuiButtonViewType buttonViewType;
	protected GuiButtonHidden buttonCraftings;
	public final int slotHeight;
	public boolean powered;
	public GuiTerminalBase(ContainerTerminalBase inv, String guiTexture, ITerminal terminal, int slotHeight) {
		super(inv, guiTexture);
		te = terminal;
		this.slotHeight = slotHeight;
	}
	public static class GuiButtonSortingType extends GuiButton{
		public int type = 0;
		public GuiButtonSortingType(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				FontRenderer fontrenderer = mc.fontRendererObj;
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				//int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 191 + 16 * type, 50, this.width, this.height);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
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

				if(type == 2)fontrenderer.drawString("@", this.xPosition + this.width / 2 - 3, this.yPosition + (this.height - 6) / 2, 0, false);
			}
		}
	}
	public static class GuiButtonSortingDir extends GuiButton{
		public boolean dir = false;
		public GuiButtonSortingDir(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				//int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 175, 34 + (dir ? 16 : 0), this.width, this.height);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
	public static class GuiButtonSearchType extends GuiButton{
		public int type = 0;
		public GuiButtonSearchType(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				//int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 191 + type * 16, 34, this.width, this.height);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
	public static class GuiButtonViewType extends GuiButton{
		public int type = 0;
		public GuiButtonViewType(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				//int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 175 + type * 16, 97, this.width, this.height);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
	protected void windowClick(int windowId, int i, int j, SlotAction pullOne,
			EntityPlayerSP thePlayer) {
		mc.playerController.windowClick(windowId, i, j, ClickType.values()[pullOne.ordinal()], thePlayer);
	}
	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		boolean isReport = message.getBoolean("r");
		if(!isReport){
			this.getContainer().receiveClientNBTPacket(message);
			int data = message.getInteger("d");
			int searchBoxType = message.getInteger("t");
			boolean canLoseFocus = searchBoxType % 2 == 1;
			//terminalType = message.getInteger("c");
			if(searchField != null){
				searchField.setCanLoseFocus(canLoseFocus);
				if(!canLoseFocus && !searchField.isFocused()){
					searchField.setFocused(true);
				}
			}
			jeiSync = searchBoxType > 1;
			StorageItemStackSorting type = PlayerHandler.getSortingType(data);
			comparator = type.getComparator(PlayerHandler.getSortingDir(data));
			this.updateSearch();
			sortData = data;
			searchType = searchBoxType;
		}else{
			GuiCraftingReport r = new GuiCraftingReport(this);
			mc.displayGuiScreen(r);
			r.receiveNBTPacket(message);
		}
	}
	@Override
	public void initGui() {
		labelList.clear();
		super.initGui();
		this.searchField = new GuiTextField(0, this.fontRendererObj, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRendererObj.FONT_HEIGHT);
		this.searchField.setMaxStringLength(100);
		this.searchField.setEnableBackgroundDrawing(false);
		this.searchField.setVisible(true);
		this.searchField.setTextColor(16777215);
		this.searchField.setText(searchLast);
		updateSearch();
		TomsModUtils.addTextFieldToLabelList(searchField, labelList);
		buttonSortingType = new GuiButtonSortingType(0, guiLeft - 17, guiTop);
		buttonList.add(buttonSortingType);
		buttonDirection = new GuiButtonSortingDir(1, guiLeft - 17, guiTop + 17);
		buttonList.add(buttonDirection);
		/*TomsModUtils.addRunnableToLabelList(new GuiRenderRunnable() {

			@Override
			public void run(int mouseX, int mouseY) {
				if(buttonSortingType.isMouseOver()){
					drawHoveringText(TomsModUtils.getListFromArray(I18n.format("tomsMod.storage.sorting_" + buttonSortingType.type)), mouseX, mouseY);
				}
			}
		}, labelList);*/
		buttonSearchType = new GuiButtonSearchType(2, guiLeft - 17, guiTop + 17 + 17);
		buttonList.add(buttonSearchType);
		/*TomsModUtils.addRunnableToLabelList(new GuiRenderRunnable() {

			@Override
			public void run(int mouseX, int mouseY) {
				if(buttonSearchType.isMouseOver()){
					drawHoveringText(TomsModUtils.getListFromArray(I18n.format("tomsMod.storage.search_" + buttonSearchType.type)), mouseX, mouseY);
				}
			}
		}, labelList);*/
		buttonViewType = new GuiButtonViewType(3, guiLeft - 17, guiTop + 17 + 17 + 17);
		buttonList.add(buttonViewType);
		/*TomsModUtils.addRunnableToLabelList(new GuiRenderRunnable() {

			@Override
			public void run(int mouseX, int mouseY) {
				if(buttonViewType.isMouseOver()){
					drawHoveringText(TomsModUtils.getListFromArray(I18n.format("tomsMod.storage.view_" + buttonViewType.type)), mouseX, mouseY);
				}
			}
		}, labelList);*/
		buttonCraftings = new GuiButtonHidden(4, guiLeft + 172, guiTop - 2, 18, 18);
		buttonList.add(buttonCraftings);
	}
	@Override
	public void updateScreen() {
		super.updateScreen();
		buttonDirection.dir = PlayerHandler.getSortingDir(sortData);
		buttonSortingType.type = PlayerHandler.getSortingType(sortData).ordinal();
		buttonSearchType.type = searchType;
		buttonViewType.type = getContainer().terminalType;
		powered = te.getClientPowered();
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
		int j1 = l + slotHeight * 18;

		if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
		{
			this.isScrolling = this.needsScrollBars();
		}

		if (!flag)
		{
			this.isScrolling = false;
		}
		this.wasClicking = flag;

		if (this.isScrolling)
		{
			this.currentScroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
			this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
			getContainer().scrollTo(this.currentScroll);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
		this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
		i = k;
		j = l;
		k = j1;
		this.drawTexturedModalRect(i, j + (int)((k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
		GL11.glPushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		slotIDUnderMouse = getContainer().drawSlots(this, mouseX, mouseY);
		GL11.glPopMatrix();
		if(buttonSortingType.isMouseOver()){
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.sorting_" + buttonSortingType.type)), mouseX, mouseY);
		}
		if(buttonSearchType.isMouseOver()){
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.search_" + buttonSearchType.type)), mouseX, mouseY);
		}
		if(buttonViewType.isMouseOver()){
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.view_" + buttonViewType.type)), mouseX, mouseY);
		}
		if(buttonCraftings.isMouseOver()){
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.craftingStatus")), mouseX, mouseY);
		}
	}
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
			throws IOException {
		//System.out.println("click");
		if(slotIDUnderMouse > -1){
			if(isCtrlKeyDown()){
				if(getContainer().getSlotByID(slotIDUnderMouse).stack != null && getContainer().getSlotByID(slotIDUnderMouse).stack.itemCount > 0){
					//getContainer().getSlotByID(slotIDUnderMouse).stack.writeToNBT(tag);
					//NetworkHandler.sendToServer(new MessageNBT(tag));
					for(int i = 0;i<getContainer().itemList.size();i++){
						if(getContainer().getSlotByID(slotIDUnderMouse).stack.equals(getContainer().itemList.get(i))){
							windowClick(this.inventorySlots.windowId, -(slotIDUnderMouse+2), i, SlotAction.PULL_ONE, this.mc.thePlayer);
							return;
						}
					}
				}
				return;
			}else if(mouseButton == 2){
				if(getContainer().getSlotByID(slotIDUnderMouse).stack != null){
					//getContainer().getSlotByID(slotIDUnderMouse).stack.writeToNBT(tag);
					//NetworkHandler.sendToServer(new MessageNBT(tag));
					/*for(int i = 0;i<getContainer().itemList.size();i++){
						if(getContainer().getSlotByID(slotIDUnderMouse).stack.equals(getContainer().itemList.get(i))){
							this.mc.playerController.windowClick(this.inventorySlots.windowId, -(slotIDUnderMouse+1), i, 6, this.mc.thePlayer);
							return;
						}
					}*/
					for(int i = 0;i<getContainer().craftables.size();i++){
						if(TomsModUtils.areItemStacksEqual(getContainer().getSlotByID(slotIDUnderMouse).stack.stack, getContainer().craftables.get(i), true, true, false)){
							mc.displayGuiScreen(new GuiCraftingAmountSelection(this, getContainer().getSlotByID(slotIDUnderMouse).stack.stack, new ItemStack(StorageInit.basicTerminal)));
							return;
						}
					}
				}
				//this.mc.playerController.windowClick(this.inventorySlots.windowId, -(slotIDUnderMouse+1), 0, 6, this.mc.thePlayer);
			}else if(mouseButton == 1){
				if(mc.thePlayer.inventory.getItemStack() != null){
					windowClick(this.inventorySlots.windowId, -(slotIDUnderMouse+2), 0, SlotAction.GET_HALF, this.mc.thePlayer);
				}else{
					//NBTTagCompound tag = new NBTTagCompound();
					//tag.setInteger("slot", slotIDUnderMouse);
					if(getContainer().getSlotByID(slotIDUnderMouse).stack != null && getContainer().getSlotByID(slotIDUnderMouse).stack.itemCount > 0){
						//getContainer().getSlotByID(slotIDUnderMouse).stack.writeToNBT(tag);
						//NetworkHandler.sendToServer(new MessageNBT(tag));
						for(int i = 0;i<getContainer().itemList.size();i++){
							if(getContainer().getSlotByID(slotIDUnderMouse).stack.equals(getContainer().itemList.get(i))){
								windowClick(this.inventorySlots.windowId, -(slotIDUnderMouse+2), i, isShiftKeyDown() ? SlotAction.GET_QUARTER : SlotAction.GET_HALF, this.mc.thePlayer);
								return;
							}
						}
					}
				}
			}else if(mouseButton == 0){
				if(mc.thePlayer.inventory.getItemStack() != null){
					windowClick(this.inventorySlots.windowId, -(slotIDUnderMouse+2), 0, SlotAction.PULL_OR_PUSH_STACK, this.mc.thePlayer);
				}else{
					//NBTTagCompound tag = new NBTTagCompound();
					//tag.setInteger("slot", slotIDUnderMouse);
					if(getContainer().getSlotByID(slotIDUnderMouse).stack != null){
						//getContainer().getSlotByID(slotIDUnderMouse).stack.writeToNBT(tag);
						//NetworkHandler.sendToServer(new MessageNBT(tag));
						if(getContainer().getSlotByID(slotIDUnderMouse).stack.itemCount > 0){
							for(int i = 0;i<getContainer().itemList.size();i++){
								if(getContainer().getSlotByID(slotIDUnderMouse).stack.equals(getContainer().itemList.get(i))){
									windowClick(this.inventorySlots.windowId, -(slotIDUnderMouse+2), i, isShiftKeyDown() ? SlotAction.SHIFT_PULL : SlotAction.PULL_OR_PUSH_STACK, this.mc.thePlayer);
									return;
								}
							}
						}else{
							/*for(int i = 0;i<getContainer().craftables.size();i++){
								if(getContainer().getSlotByID(slotIDUnderMouse).stack.equals(getContainer().craftables.get(i))){
									this.mc.playerController.windowClick(this.inventorySlots.windowId, -(slotIDUnderMouse+1), i, 7, this.mc.thePlayer);
									return;
								}
							}*/
							for(int i = 0;i<getContainer().craftables.size();i++){
								if(TomsModUtils.areItemStacksEqual(getContainer().getSlotByID(slotIDUnderMouse).stack.stack, getContainer().craftables.get(i), true, true, false)){
									mc.displayGuiScreen(new GuiCraftingAmountSelection(this, getContainer().getSlotByID(slotIDUnderMouse).stack.stack, new ItemStack(StorageInit.basicTerminal)));
									break;
								}
							}
						}
					}
				}
			}
		}else if(Keyboard.isKeyDown(57)){
			windowClick(this.inventorySlots.windowId, -1, 0, SlotAction.SPACE_CLICK, this.mc.thePlayer);
		}else{
			if(mouseButton == 1 && isPointInRegion(searchField.xPosition - guiLeft, searchField.yPosition - guiTop, searchField.width, searchField.height, mouseX, mouseY))
				searchField.setText("");
			searchField.mouseClicked(mouseX, mouseY, mouseButton);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	public ContainerTerminalBase getContainer(){
		return (ContainerTerminalBase)inventorySlots;
	}
	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		int i = Mouse.getEventDWheel();

		if (i != 0 && this.needsScrollBars())
		{
			int j = getContainer().itemListClientSorted.size() / 9 - 5;

			if (i > 0)
			{
				i = 1;
			}

			if (i < 0)
			{
				i = -1;
			}

			this.currentScroll = (float)(this.currentScroll - (double)i / (double)j);
			this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
			getContainer().scrollTo(this.currentScroll);
		}
	}

	protected boolean needsScrollBars() {
		return this.getContainer().itemListClientSorted.size() > 45;
	}
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(!this.searchField.textboxKeyTyped(typedChar, keyCode))super.keyTyped(typedChar, keyCode);
	}
	protected void updateSearch(){
		String searchString = searchField.getText();
		getContainer().itemListClientSorted.clear();
		boolean searchMod = false;
		if(searchString.startsWith("@")){
			searchMod = true;
			searchString = searchString.substring(1);
		}
		Pattern m = null;
		try
		{
			m = Pattern.compile(searchString.toLowerCase(), Pattern.CASE_INSENSITIVE);
		}
		catch( final Throwable ignore )
		{
			try
			{
				m = Pattern.compile(Pattern.quote(searchString.toLowerCase()), Pattern.CASE_INSENSITIVE);
			}
			catch( final Throwable __ )
			{
				return;
			}
		}
		boolean notDone = false;
		for(int i = 0;i<getContainer().itemListClient.size();i++){
			StoredItemStack is = getContainer().itemListClient.get(i);
			if(is != null && is.stack != null){
				String dspName = searchMod ? is.stack.getItem().delegate.name().getResourceDomain() : is.stack.getDisplayName();
				notDone = true;
				if(m.matcher(dspName.toLowerCase()).find())
				{
					addStackToClientList(is);
					notDone = false;
				}
				if(true && notDone)
				{
					for(String lp : is.stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips))
					{
						if(m.matcher(lp).find())
						{
							addStackToClientList(is);
							notDone = false;
							break;
						}
					}
				}
			}
		}
		Collections.sort(getContainer().itemListClientSorted, comparator);
		if(!searchLast.equals(searchString)){
			getContainer().scrollTo(0);
			this.currentScroll = 0;
			if(jeiSync)JEIHandler.setJeiSearchText(searchString);
		}else{
			getContainer().scrollTo(this.currentScroll);
		}
		this.searchLast = searchString;

		//mc.displayGuiScreen(null);
	}
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if(slotIDUnderMouse == -1)super.mouseReleased(mouseX, mouseY, state);
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 0){
			StorageItemStackSorting type = StorageItemStackSorting.get(PlayerHandler.getSortingType(sortData).ordinal() + 1);
			boolean dir = PlayerHandler.getSortingDir(sortData);
			sendButtonUpdate(0, PlayerHandler.getItemSortingMode(type, dir));
		}else if(button.id == 1){
			StorageItemStackSorting type = PlayerHandler.getSortingType(sortData);
			boolean dir = !PlayerHandler.getSortingDir(sortData);
			sendButtonUpdate(0, PlayerHandler.getItemSortingMode(type, dir));
		}else if(button.id == 2){
			sendButtonUpdate(1, searchType + 1);
		}else if(button.id == 3){
			sendButtonUpdate(0, te, getContainer().terminalType + 1);
		}
	}
	private void addStackToClientList(StoredItemStack is){
		if(getContainer().terminalType == 0)
			getContainer().itemListClientSorted.add(is);
		else if(getContainer().terminalType == 1){
			if(is.itemCount > 0)
				getContainer().itemListClientSorted.add(is);
		}else if(getContainer().terminalType == 2){
			if(is.itemCount == 0)
				getContainer().itemListClientSorted.add(is);
		}
	}
	@Override
	public void sendCrafting(int cpuId, boolean doCraft) {
		windowClick(this.inventorySlots.windowId, cpuId == -1 ? 1000 : -(cpuId+1), doCraft ? 1 : 0, SlotAction.CRAFT, this.mc.thePlayer);
	}

	@Override
	public GuiScreen getScreen() {
		return this;
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick,
			int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		mc.getTextureManager().bindTexture(LIST_TEXTURE);
		drawTexturedModalRect(guiLeft + 169, guiTop - 4, 0, 220, 25, 7);
		drawTexturedModalRect(guiLeft + 175, guiTop + 1, 25, 220, 12, 12);
	}

	@Override
	public void openCraftingReport(ItemStack stack, int amount) {
		for(int i = 0;i<getContainer().craftables.size();i++){
			if(TomsModUtils.areItemStacksEqual(stack, getContainer().craftables.get(i), true, true, false)){
				windowClick(this.inventorySlots.windowId, -(i+1), amount, SlotAction.CRAFT, this.mc.thePlayer);
				return;
			}
		}
	}
	public void sendButtonUpdate(int id, ITerminal term){
		if(term instanceof IGuiMultipart)sendButtonUpdate(id, (IGuiMultipart) term);
		else sendButtonUpdate(id, term.getPos2());
	}
	public void sendButtonUpdate(int id, ITerminal term, int extraData){
		if(term instanceof IGuiMultipart)sendButtonUpdate(id, (IGuiMultipart) term, extraData);
		else sendButtonUpdate(id, term.getPos2(), extraData);
	}
	public void bindList() {
		mc.renderEngine.bindTexture(LIST_TEXTURE);
	}
}
