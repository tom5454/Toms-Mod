package com.tom.defense.tileentity.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.tileentity.IConfigurable.IConfigurationOption.GuiButtonRedstoneMode;
import com.tom.defense.tileentity.TileEntityForceCapacitor;
import com.tom.defense.tileentity.inventory.ContainerForceCapacitor;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiForceCapacitor extends GuiTomsMod {
	private TileEntityForceCapacitor te;
	private GuiButtonRedstoneMode buttonRedstone;
	public GuiForceCapacitor(InventoryPlayer inv, TileEntityForceCapacitor te) {
		super(new ContainerForceCapacitor(inv,te), "GuiCapacitor");
		this.te = te;
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.forceCapacitor.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		String linkedD = I18n.format("tomsmod.render.linkedDevices")+": ";
		String range = I18n.format("tomsmod.render.range")+": ";
		fontRendererObj.drawString(linkedD, 8, ySize - 140, 4210752);
		fontRendererObj.drawString(range, 8, ySize - 130, 4210752);
		String linkedV = ""+te.getField(0);
		String rangeV = ""+te.getField(1);
		fontRendererObj.drawString(linkedV, 120-fontRendererObj.getStringWidth(linkedV), ySize - 140, 4210752);
		fontRendererObj.drawString(rangeV, 120-fontRendererObj.getStringWidth(rangeV), ySize - 130, 4210752);
		fontRendererObj.drawString(te.getMaxEnergyStored(null, null) + "F/" + te.getField(2)+"F", 8, ySize - 112, 4210752);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick,
			int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		double per = (te.getField(2)*1D) / te.getMaxEnergyStored(null, null);
		double p = 70D * per;//70
		//int p = 50;
		this.drawTexturedModalRect(guiLeft+8, guiTop+74,176,0,p,5);//8,74
	}
	@Override
	public void updateScreen() {
		super.updateScreen();
		this.buttonRedstone.controlType = te.rsMode;
	}
	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
		this.buttonRedstone = new GuiButtonRedstoneMode(0, guiLeft+150, guiTop+5, te.rsMode);
		this.buttonList.add(buttonRedstone);
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 0){
			this.sendButtonUpdate(0, te, te.rsMode.ordinal()+1);
		}
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		buttonRedstone.postDraw(mc, mouseX, mouseY, this);
	}
}
