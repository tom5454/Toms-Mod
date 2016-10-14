package com.tom.core.tileentity.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.tileentity.IConfigurable;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.network.messages.MessageNBT.MessageNBTRequest;

import com.tom.core.tileentity.inventory.ContainerConfigurator;

public class GuiConfigurator extends GuiTomsMod implements INBTPacketReceiver{
	private IConfigurable te;
	public GuiConfigurator(InventoryPlayer playerInv, IConfigurable configurable) {
		super(new ContainerConfigurator(playerInv, configurable), "configurator");
		te = configurable;
		NetworkHandler.sendToServer(new MessageNBTRequest(te.getPos2()));
	}
	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		te.getOption().readFromNBTPacket(message);
	}
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		NBTTagCompound tag = new NBTTagCompound();
		te.getOption().writeModificationNBTPacket(tag);
		NetworkHandler.sendToServer(new MessageNBT(tag,te.getPos2()));
	}
	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
		te.getOption().init(mc, guiLeft+100-te.getOption().getWidth(), guiTop+70-te.getOption().getHeight(), -1, buttonList);
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		te.getOption().actionPreformed(mc, button);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick,
			int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		te.getOption().renderBackground(mc, guiLeft+100-te.getOption().getWidth(), guiTop+70-te.getOption().getHeight());
		te.getOption().renderForeground(mc, guiLeft+100-te.getOption().getWidth(), guiTop+70-te.getOption().getHeight(), mouseX, mouseY);
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
		String s = I18n.format("item.tm.configurator.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
	}
	@Override
	public void updateScreen() {
		super.updateScreen();
		te.getOption().update(mc, guiLeft+100-te.getOption().getWidth(), guiTop+70-te.getOption().getHeight());
	}
}
