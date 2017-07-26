package com.tom.defense.tileentity.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.network.INBTPacketReceiver;
import com.tom.apis.TomsModUtils;
import com.tom.client.GuiButtonRedstoneMode;
import com.tom.defense.tileentity.TileEntityDefenseStation;
import com.tom.defense.tileentity.TileEntityDefenseStation.DefenseStationConfig;
import com.tom.defense.tileentity.inventory.ContainerDefenseStation;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiDefenseStation extends GuiTomsMod implements INBTPacketReceiver {
	private GuiButtonDefenseStationSelection buttonSelection;
	private TileEntityDefenseStation te;
	private GuiButton whiteListButton;
	private GuiButton metaButton;
	private GuiButton modButton;
	private GuiButton nbtButton;
	private GuiButton playerButton;
	private GuiButtonRedstoneMode buttonRedstone;
	private GuiTextField fieldName;
	private boolean textFieldFoucusedLast;

	public GuiDefenseStation(InventoryPlayer inv, TileEntityDefenseStation te) {
		super(new ContainerDefenseStation(inv, te), "guiDefenseStation");
		this.te = te;
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		fieldName.setText(message.getString("n"));
	}

	@Override
	public void initGui() {
		this.xSize = 256;
		this.ySize = 216;
		labelList.clear();
		super.initGui();
		whiteListButton = new GuiButton(0, guiLeft + 10, guiTop + 72, 15, 20, "B");
		buttonList.add(whiteListButton);
		metaButton = new GuiButton(1, guiLeft + 25, guiTop + 72, 30, 20, TextFormatting.GREEN + "Meta");
		buttonList.add(metaButton);
		modButton = new GuiButton(2, guiLeft + 56, guiTop + 72, 30, 20, TextFormatting.RED + "Mod");
		buttonList.add(modButton);
		nbtButton = new GuiButton(3, guiLeft + 87, guiTop + 72, 30, 20, TextFormatting.GREEN + "NBT");
		buttonList.add(nbtButton);
		buttonSelection = new GuiButtonDefenseStationSelection(4, guiLeft + 5, guiTop + 25, te.config);
		buttonList.add(buttonSelection);
		buttonRedstone = new GuiButtonRedstoneMode(5, guiLeft + 202, guiTop + 7, te.rsMode);
		buttonList.add(buttonRedstone);
		playerButton = new GuiButton(6, guiLeft + 10, guiTop + 51, 50, 20, I18n.format("tomsmod.gui.generic"));
		buttonList.add(playerButton);
		fieldName = new GuiTextField(0, fontRenderer, guiLeft + 6, guiTop + 6, 126, 15);
		fieldName.setMaxStringLength(30);
		fieldName.setTextColor(0xffffff);
		TomsModUtils.addTextFieldToLabelList(fieldName, labelList);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		whiteListButton.displayString = te.isWhiteList() ? "W" : "B";
		metaButton.displayString = te.useMeta() ? TextFormatting.GREEN + "Meta" : TextFormatting.RED + "Meta";
		modButton.displayString = te.useMod() ? TextFormatting.GREEN + "Mod" : TextFormatting.RED + "Mod";
		nbtButton.displayString = te.useNBT() ? TextFormatting.GREEN + "NBT" : TextFormatting.RED + "NBT";
		playerButton.displayString = te.isPlayerKill() ? I18n.format("tomsmod.gui.player") : I18n.format("tomsmod.gui.generic");
		buttonRedstone.controlType = te.rsMode;
		buttonSelection.config = te.config;
		boolean textFieldFoucusedLastO = textFieldFoucusedLast;
		textFieldFoucusedLast = fieldName.isFocused();
		if (textFieldFoucusedLast != textFieldFoucusedLastO) {
			this.sendUpdatePacket();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		int id = button.id;
		if ((id >= 0 && id < 4) || id == 6) {
			this.sendButtonUpdate(id, te);
		} else if (id == 4) {
			this.sendButtonUpdate(id, te, te.config.ordinal() + 1);
		} else if (id == 5) {
			this.sendButtonUpdate(id, te, te.rsMode.ordinal() + 1);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		if (te.getMaxEnergyStored() > 0) {
			double per = (te.getField(0) * 1D) / te.getMaxEnergyStored();
			double p = 65D * per;// 65
			// int p = 50;
			this.drawTexturedModalRect(guiLeft + 27, guiTop + 104, 0, 216, p, 12);// 8,74
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
		fontRenderer.drawString(I18n.format("tomsmod.gui.actionRange"), 138, 30, 4210752);
		fontRenderer.drawString(I18n.format("tomsmod.gui.informRange"), 138, 60, 4210752);
		if (te.getMaxEnergyStored() <= 0)
			drawCenteredString(fontRenderer, I18n.format("tomsMod.invalid"), 59, 106, 0xFF0000);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (this.buttonSelection.isMouseOver()) {
			this.drawHoveringText(TomsModUtils.getStringList(I18n.format(this.buttonSelection.config.getName())), mouseX, mouseY);
		} else if (this.isPointInRegion(27, 104, 65, 12, mouseX, mouseY)) {
			if (te.getMaxEnergyStored() > 0)
				this.drawHoveringText(TomsModUtils.getStringList(te.getMaxEnergyStored() + "F/" + te.getField(0) + "F"), mouseX, mouseY);
			else
				this.drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.tooltip.invalidPowerlink")), mouseX, mouseY);
		}
		buttonRedstone.postDraw(mc, mouseX, mouseY, this);
	}

	public class GuiButtonDefenseStationSelection extends GuiButton {
		private DefenseStationConfig config;

		public GuiButtonDefenseStationSelection(int buttonId, int x, int y, DefenseStationConfig config) {
			super(buttonId, x, y, 16, 16, "");
			this.config = config;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 65 + config.ordinal() * 16, 216, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		fieldName.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!fieldName.isFocused())
			super.keyTyped(typedChar, keyCode);
		fieldName.textboxKeyTyped(typedChar, keyCode);
		if (keyCode == 28 && fieldName.isFocused()) {
			fieldName.setFocused(false);
		}
	}

	private void sendUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("n", fieldName.getText());
		NetworkHandler.sendToServer(new MessageNBT(tag, te));
	}
}
