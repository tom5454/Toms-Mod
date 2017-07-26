package com.tom.defense.tileentity.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiNumberValueBox;
import com.tom.api.gui.GuiNumberValueBox.GuiButtonNextNum;
import com.tom.apis.TomsModUtils;
import com.tom.client.GuiButtonRedstoneMode;
import com.tom.defense.tileentity.TileEntityForceFieldProjector;
import com.tom.defense.tileentity.inventory.ContainerForceFieldProjector;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiForceFieldProjector extends GuiTomsMod {
	private TileEntityForceFieldProjector te;
	private GuiButtonRedstoneMode buttonRedstone;
	private GuiNumberValueBox offsetX, offsetY, offsetZ;

	public GuiForceFieldProjector(InventoryPlayer playerInv, TileEntityForceFieldProjector te) {
		super(new ContainerForceFieldProjector(playerInv, te), "guiForceProjector");
		this.te = te;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 5, 4210752);
		String s = I18n.format("tile.tmd.fieldProjector.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		int energy = te.getField(0);
		String energyV = "F";
		if (energy > 1000) {
			energyV = energy / 1000 + "k" + energyV;
		} else {
			energyV = energy + energyV;
		}
		fontRenderer.drawString(te.getMaxEnergyStored() / 1000 + "kF/" + energyV, 27, ySize - 116, 4210752);
		String drainV = "" + te.getField(1) / 100D + " F/t";
		String drain = I18n.format("tomsmod.gui.powerDrain") + ": ";
		fontRenderer.drawString(drain, 8, ySize - 156, 4210752);
		fontRenderer.drawString(drainV, 145 - fontRenderer.getStringWidth(drainV), ySize - 156, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		double per = (te.getField(0) * 1D) / te.getMaxEnergyStored();
		double p = 65D * per;// 65
		// int p = 50;
		this.drawTexturedModalRect(guiLeft + 28, guiTop + 69, 176, 32, p, 12);// 8,74
		if (isActive()) {
			this.drawTexturedModalRect(guiLeft + 123, guiTop + 31, 176, 0, 32, 32);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.sendButtonUpdate(0, te, te.rsMode.ordinal() + 1);
		} else if (button instanceof GuiButtonNextNum) {
			GuiNumberValueBox b = ((GuiButtonNextNum) button).parent;
			this.sendButtonUpdate(b.id, te, b.num);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.buttonRedstone.controlType = te.rsMode;
		offsetX.num = te.getField(2);
		offsetY.num = te.getField(3);
		offsetZ.num = te.getField(4);
		offsetX.update(!isActive());
		offsetY.update(!isActive());
		offsetZ.update(!isActive());
	}

	@Override
	public void initGui() {
		ySize = 176;
		labelList.clear();
		super.initGui();
		this.buttonRedstone = new GuiButtonRedstoneMode(0, guiLeft + 150, guiTop + 5, te.rsMode);
		this.buttonList.add(buttonRedstone);
		offsetX = new GuiNumberValueBox(1, guiLeft + 5, guiTop + 30, 48, -48);
		offsetY = new GuiNumberValueBox(2, guiLeft + 5, guiTop + 40, 48, -48);
		offsetZ = new GuiNumberValueBox(3, guiLeft + 5, guiTop + 50, 48, -48);
		offsetX.addToList(buttonList);
		offsetY.addToList(buttonList);
		offsetZ.addToList(buttonList);
		TomsModUtils.addNumberValueBoxToLabelList(offsetX, labelList);
		TomsModUtils.addNumberValueBoxToLabelList(offsetY, labelList);
		TomsModUtils.addNumberValueBoxToLabelList(offsetZ, labelList);
		offsetX.num = te.getField(2);
		offsetY.num = te.getField(3);
		offsetZ.num = te.getField(4);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		offsetX.onClicked(mouseX, mouseY);
		offsetY.onClicked(mouseX, mouseY);
		offsetZ.onClicked(mouseX, mouseY);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private boolean isActive() {
		return te.getField(1) > 0;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		buttonRedstone.postDraw(mc, mouseX, mouseY, this);
	}
}
