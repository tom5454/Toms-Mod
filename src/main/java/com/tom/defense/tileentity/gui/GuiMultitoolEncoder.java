package com.tom.defense.tileentity.gui;

import java.io.IOException;

import com.tom.core.tileentity.gui.GuiTomsMod;
import com.tom.defense.tileentity.inventory.ContainerMultitoolEncoder;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GuiMultitoolEncoder extends GuiTomsMod {
	private GuiButton buttonWrite, buttonConfigure;
	public GuiMultitoolEncoder(EntityPlayer player, ItemStack stack) {
		super(new ContainerMultitoolEncoder(player, stack), "writerGui");
	}
	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
		this.buttonConfigure = new GuiButton(0, guiLeft+70, guiTop+48, 100, 20, I18n.format("tomsmod.gui.configL"));
		this.buttonWrite = new GuiButton(1, guiLeft+70, guiTop+16, 100, 20, I18n.format("tomsmod.gui.createCard"));
		this.buttonList.add(buttonConfigure);
		this.buttonList.add(buttonWrite);
	}
	@Override
	public void updateScreen() {
		super.updateScreen();
		buttonWrite.enabled = mc.thePlayer.openContainer.getSlot(0).getStack() != null && mc.thePlayer.openContainer.getSlot(1).getStack() == null;
		buttonConfigure.enabled = mc.thePlayer.openContainer.getSlot(2).getStack() != null;
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
		String s = I18n.format("item.tm.multitool.writer.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id < 2)this.sendButtonUpdate(button.id);
	}
	@Override
	protected boolean checkHotbarKeys(int keyCode) {
		return false;
	}
}
