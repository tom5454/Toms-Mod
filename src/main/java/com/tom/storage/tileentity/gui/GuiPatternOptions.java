package com.tom.storage.tileentity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.gui.GuiNumberValueBox;
import com.tom.api.inventory.SlotPhantom;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.tileentity.IPatternTerminal;
import com.tom.apis.TomsModUtils;
import com.tom.lib.Keys;
import com.tom.storage.multipart.StorageNetworkGrid.CraftableProperties;
import com.tom.storage.tileentity.gui.GuiBlockPatternTerminal.GuiButtonUseContainerItems;
import com.tom.storage.tileentity.gui.GuiCraftingAmountSelection.GuiButtonHidden;
import com.tom.storage.tileentity.inventory.ContainerPatternOptions;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiPatternOptions extends GuiTomsMod implements INBTPacketReceiver{
	private ItemStack backButton;
	private GuiButtonHidden buttonBack;
	private IPatternTerminal te;
	private GuiNumberValueBox timeBox;
	private GuiButtonEncode buttonEncode;
	private GuiButtonClear buttonClear;
	private GuiButtonUseContainerItems buttonUseContainerItems;
	private GuiButtonStoredOnly buttonStoredOnly;
	private boolean config = false, editing = false, editingNum = false;
	private int selected = 0, selectedX = 0, selectedY = 0, selectedOption = 0, originalNum = 0;
	private String editingAmount = "0";
	private CraftableProperties propEdit = new CraftableProperties();
	public GuiPatternOptions(InventoryPlayer playerInv, IPatternTerminal te) {
		super(new ContainerPatternOptions(playerInv, te), "patternOptionsGui");
		backButton = te.getButtonStack();
		this.te = te;
	}
	@Override
	public void initGui() {
		xSize = 240;
		ySize = 248;
		labelList.clear();
		super.initGui();
		buttonBack = new GuiButtonHidden(0, guiLeft + 220, guiTop + 2, 18, 18);
		buttonList.add(buttonBack);
		timeBox = new GuiNumberValueBox(1, guiLeft + 100, guiTop + 110, 1024, 1);
		timeBox.addToList(buttonList);
		timeBox.num = te.getProperties().time;
		TomsModUtils.addNumberValueBoxToLabelList(timeBox, labelList);
		buttonEncode = new GuiButtonEncode(2, guiLeft + 216, guiTop + 197);
		buttonList.add(buttonEncode);
		buttonClear = new GuiButtonClear(3, guiLeft + 107, guiTop + 18);
		buttonList.add(buttonClear);
		buttonUseContainerItems = new GuiButtonUseContainerItems(4, guiLeft + 107, guiTop + 32);
		buttonList.add(buttonUseContainerItems);
		buttonStoredOnly = new GuiButtonStoredOnly(5, guiLeft + 119, guiTop + 18);
		buttonList.add(buttonStoredOnly);
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(config){
			Slot s = getSlotUnderMouse();
			if(s != null && s instanceof SlotPhantom && s.getHasStack()){
				selectedOption = 0;
				selected = s.slotNumber;
				selectedX = s.xDisplayPosition;
				selectedY = s.yDisplayPosition;
				config = false;
				editing = true;
				editingAmount = "" + s.getStack().stackSize;
				originalNum = s.getStack().stackSize;
				propEdit.readFromNBT(te.getPropertiesFor(selected).writeToNBT(new NBTTagCompound()));
			}else{
				selected = -1;
				config = false;
			}
		}else if(!editing){
			selected = -1;
			editing = false;
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.enableGUIStandardItemLighting();
		renderItemInGui(backButton, guiLeft + 221, guiTop + 3, -20, -20);
		if(buttonBack.isMouseOver())drawHoveringText(TomsModUtils.getStringList(I18n.format(backButton.getUnlocalizedName()+".name")), mouseX, mouseY);
		if(selected > -1 && editing)drawMenu();
	}
	private void drawMenu() {
		int x = guiLeft + selectedX + 7;
		int y = guiTop + selectedY + 31;
		List<String> list = new ArrayList<String>();
		list.add(getOption(0, "tomsmod.gui." + (propEdit.useMeta ? "useMeta" : "ignoreMeta")));
		list.add(getOption(1, "tomsmod.gui." + (propEdit.useNBT ? "useNBT" : "ignoreNBT")));
		list.add(getOption(2, "tomsmod.gui." + (propEdit.useOreDict ? "useOreDict" : "ignoreOreDict")));
		list.add(getOption(3, "tomsmod.gui.amount", (editingNum ? TextFormatting.GREEN : "") + editingAmount) + (editingNum ? "_" : ""));
		drawHoveringText(list, x, y);
	}
	private String getOption(int id, String key, Object... args){
		boolean s = selectedOption == id;
		return (s ? TextFormatting.GOLD : TextFormatting.GRAY) + I18n.format("tomsmod.gui." + (s ? "hoveringSelected" : "hoveringNormal"), (s ? TextFormatting.BOLD : "") + I18n.format(key, args));
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 0){
			te.sendUpdate(this, -9, 0);
		}else if(button.id == 1){
			te.sendUpdate(this, -10, timeBox.num);
		}else if(button.id == 2){
			te.sendUpdate(this, 2, 0);
		}else if(button.id == 3){
			te.sendUpdate(this, 1, 0);
		}else if(button.id == 4){
			te.sendUpdate(this, 4, buttonUseContainerItems.e ? 0 : 1);
		}else if(button.id == 5){
			te.sendUpdate(this, -11, buttonStoredOnly.storedOnly ? 0 : 1);
		}
	}
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1 && editing){
			editing = false;
			if(editingNum){
				sendStackSizeUpdate();
				editingNum = false;
			}
			sendPropertiesIfChanged();
		}else if(editingNum && editing && (Character.isDigit(typedChar) || keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_RETURN)){
			if(keyCode == Keyboard.KEY_DELETE){
				editingAmount = "";
			}else if(keyCode == Keyboard.KEY_BACK){
				try{
					int len = editingAmount.length();
					if(len > 0)editingAmount = editingAmount.substring(0, len - 1);
				}catch(IndexOutOfBoundsException e){
					editingAmount = "1";
				}
			}else if(keyCode == Keyboard.KEY_RETURN){
				sendStackSizeUpdate();
				editingNum = false;
			}else{
				if(editingAmount.length() < 3)editingAmount += typedChar;
			}
		}else if(editing && keyCode == Keyboard.KEY_RETURN){
			if(selectedOption == 3){
				editingNum = true;
			}else{
				switch(selectedOption){
				case 0:
					propEdit.useMeta = !propEdit.useMeta;
					break;
				case 1:
					propEdit.useNBT = !propEdit.useNBT;
					break;
				case 2:
					propEdit.useOreDict = !propEdit.useOreDict;
					break;
				}
				sendPropertiesIfChanged();
			}
		}else if(editing && !editingNum && keyCode == Keyboard.KEY_UP){
			selectedOption = Math.max(selectedOption - 1, 0);
		}else if(editing && !editingNum && keyCode == Keyboard.KEY_DOWN){
			selectedOption = Math.min(selectedOption + 1, 3);
		}else if ((keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) && !isShiftKeyDown()){
			te.sendUpdate(this, -9, 0);
		}else if(Keys.CONFIG.isActiveAndMatches(keyCode)){
			if(editing){
				editing = false;
				if(editingNum){
					sendStackSizeUpdate();
					editingNum = false;
				}
				sendPropertiesIfChanged();
			}else
				config = true;
		}else{
			super.keyTyped(typedChar, keyCode);
		}
	}
	private void sendPropertiesIfChanged() {
		boolean equals = false;
		{
			NBTTagCompound tag = new NBTTagCompound();
			propEdit.writeToNBT(tag);
			NBTTagCompound tag2 = new NBTTagCompound();
			te.getPropertiesFor(selected).writeToNBT(tag2);
			equals = tag.equals(tag2);
		}
		if(!equals){
			NBTTagCompound tag = new NBTTagCompound();
			propEdit.writeToNBT(tag);
			tag.setByte("cfg", (byte) 2);
			tag.setByte("slot", (byte) selected);
			te.sendUpdate(tag);
		}
	}
	private void sendStackSizeUpdate() {
		int amount = originalNum;
		if(editingAmount.isEmpty())editingAmount = "1";
		try{
			amount = Integer.parseInt(editingAmount);
		}catch(NumberFormatException e){}
		if(amount != originalNum){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setByte("amount", (byte) Math.min(amount, Byte.MAX_VALUE));
			tag.setByte("slot", (byte) selected);
			tag.setByte("cfg", (byte) 1);
			te.sendUpdate(tag);
		}
	}
	@Override
	public void updateScreen() {
		super.updateScreen();
		timeBox.num = te.getProperties().time;
		timeBox.update(true);
		buttonUseContainerItems.e = te.getProperties().useContainerItems;
		buttonEncode.enabled = te.hasPattern();
		buttonStoredOnly.storedOnly = te.getProperties().storedOnly;
	}
	public static class GuiButtonEncode extends GuiButton{
		private static final ResourceLocation GUI = new ResourceLocation(createGuiLocation("guiPatternTerminal"));
		public GuiButtonEncode(int buttonId, int x, int y) {
			super(buttonId, x, y, 11, 11, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				mc.getTextureManager().bindTexture(GUI);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 223 + i * 11, 0, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
	public static class GuiButtonClear extends GuiButton{
		private static final ResourceLocation GUI = new ResourceLocation(createGuiLocation("guiPatternTerminal"));
		public GuiButtonClear(int buttonId, int x, int y) {
			super(buttonId, x, y, 11, 11, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				mc.getTextureManager().bindTexture(GUI);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 223 + i * 11, 11, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
	public static class GuiButtonStoredOnly extends GuiButton{
		public boolean storedOnly;
		public GuiButtonStoredOnly(int buttonId, int x, int y) {
			super(buttonId, x, y, 13, 13, "");
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
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 175 + (storedOnly ? width : 0), 66, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96+4, 4210752);
		String s = I18n.format("tomsmod.gui.patternOptions");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		String s2 = I18n.format("tomsmod.gui.timeInTicks");
		fontRendererObj.drawString(s2, 8, 110, 4210752);
	}
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(editing){
			editing = false;
			if(editingNum){
				sendStackSizeUpdate();
				editingNum = false;
			}
			sendPropertiesIfChanged();
		}else{
			timeBox.onClicked(mouseX, mouseY);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		if(message.getBoolean("isp")){
			NBTTagList list = message.getTagList("l", 10);
			for(int i = 0;i<list.tagCount();i++){
				NBTTagCompound tag = list.getCompoundTagAt(i);
				te.getPropertiesFor(tag.getByte("s")).readFromNBT(tag);
			}
		}
		if(selected > -1)propEdit.readFromNBT(te.getPropertiesFor(selected).writeToNBT(new NBTTagCompound()));
	}
}
