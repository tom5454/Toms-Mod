package com.tom.storage.tileentity.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.gui.GuiTomsMod;
import com.tom.client.EventHandlerClient;
import com.tom.storage.tileentity.TileEntityStorageNetworkController;
import com.tom.storage.tileentity.TileEntityStorageNetworkController.ControllerState;
import com.tom.storage.tileentity.inventory.ContainerController;

public class GuiController extends GuiTomsMod {
	private GuiTextField text;
	private GuiButtonOnOff onOff;
	private TileEntityStorageNetworkController te;

	public GuiController(InventoryPlayer playerInv, TileEntityStorageNetworkController te) {
		super(new ContainerController(playerInv, te), "guiController");
		this.te = te;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			te.setActiveByPlayer(!te.isActiveByPlayer());
			sendPacket(false);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 28)
			sendPacket(true);
		else {
			if (!text.textboxKeyTyped(typedChar, keyCode))
				super.keyTyped(typedChar, keyCode);
			else {
				te.setCmd(text.getText());
				sendPacket(false);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 30, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.storageController.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	private void sendPacket(boolean enter) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("c", text.getText());
		tag.setBoolean("run", enter);
		tag.setBoolean("a", te.isActiveByPlayer());
		sendNBTToTile(tag);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (te.getState() != ControllerState.OFF) {
			GL11.glPushMatrix();
			RenderHelper.disableStandardItemLighting();
			mc.renderEngine.bindTexture(gui);
			GlStateManager.color(1, 1, 1, 1);
			drawTexturedModalRect(guiLeft + 24, guiTop + 18, 0, 176, 120, 23);
			GL11.glTranslated(guiLeft + 24.5, guiTop + 19.1, 0);
			float f = 1.15F;
			GL11.glScalef(f, f, f);
			drawText(0, 0);
			GL11.glPopMatrix();
		}
	}

	private void drawText(int x, int y) {
		EventHandlerClient.lcdFont.drawString(te.getState().getText1(te.getWorld().getTotalWorldTime(), 13, te.getCmd()).replace(' ', '\u0000'), x, y, 0x00D8DE);
		EventHandlerClient.lcdFont.drawString(te.getState().getText2(te.getWorld().getTotalWorldTime(), 13, te.getCmd()).replace(' ', '\u0000'), x, y + 10, 0x00D8DE);
	}

	@Override
	public void initGui() {
		ySize = 176;
		labelList.clear();
		super.initGui();
		text = new GuiTextFieldC(0, guiLeft + 26, guiTop + 32, 0, 0);
		text.setFocused(true);
		text.setText(te.getCmd()[0]);
		text.setVisible(te.canType());
		text.setTextColor(0x00D8DE);
		onOff = new GuiButtonOnOff(0, guiLeft + 7, guiTop + 70);
		buttonList.add(onOff);
		// TomsModUtils.addTextFieldToLabelList(text, labelList);

	}

	public static class GuiTextFieldC extends GuiTextField {

		public GuiTextFieldC(int componentId, int x, int y, int par5Width, int par6Height) {
			super(componentId, EventHandlerClient.lcdFont, x, y, par5Width, par6Height);
			setEnableBackgroundDrawing(false);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		te = (TileEntityStorageNetworkController) te.getWorld().getTileEntity(te.getPos());
		text.setText(te.getCmd()[0]);
		text.setVisible(te.canType());
	}

	public class GuiButtonOnOff extends GuiButton {

		public GuiButtonOnOff(int buttonId, int x, int y) {
			super(buttonId, x, y, 20, 20, "");
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
				this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				mc.getTextureManager().bindTexture(gui);
				GuiController.this.drawTexturedModalRect(this.x + 2.5, this.y + 1.5, 176.001, te.isActiveByPlayer() ? 19 : 0, 15, 17);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
}
