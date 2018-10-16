package com.tom.factory.tileentity.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.tom.api.gui.GuiFluidTank;
import com.tom.api.gui.GuiTomsMod;
import com.tom.factory.tileentity.TileEntityFluidTransposer;
import com.tom.factory.tileentity.inventory.ContainerFluidTransposer;
import com.tom.util.TomsModUtils;

public class GuiFluidTransposer extends GuiTomsMod {
	private TileEntityFluidTransposer te;
	private GuiButtonMode buttonMode;

	public GuiFluidTransposer(InventoryPlayer playerInv, TileEntityFluidTransposer te) {
		super(new ContainerFluidTransposer(playerInv, te), "fluidTransposerGui");
		this.te = te;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.disableStandardItemLighting();
		renderFluidTooltips(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.fluidTransposer.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		if (te.getMode()) {
			drawTexturedModalRect(guiLeft + 101, guiTop + 20, 176, 57, 28, 18);
		}
		float p1Per = (te.clientEnergy * 1F) / te.getMaxEnergyStored(null, null);
		int bar = te.getField(0);
		float p2Per = (te.getMaxProgress() - bar * 1F) / te.getMaxProgress();
		double p1 = p1Per * 65;
		double p2 = p2Per * 22;
		drawTexturedModalRect(guiLeft + 8, guiTop + 76 - p1, 176, 154 - p1, 12, p1);
		if (bar > 0) {
			double x = guiLeft + 105;
			double pX = 207;
			int y = guiTop + 22;
			int pY = 58;
			if (!te.getMode()) {
				x = x + 19 - p2;
				pX = pX + 22 - p2;
				pY = 33;
			}
			drawTexturedModalRect(x, y, pX, pY, p2, 16);
		}
	}

	@Override
	public void initGui() {
		ySize = 176;
		labelList.clear();
		super.initGui();
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 150, guiTop + 15, te.getTank()).setUV(176, 0).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
		buttonMode = new GuiButtonMode(0, guiLeft + 114, guiTop + 54);
		buttonList.add(buttonMode);
	}

	private static final ItemStack BUCKET = new ItemStack(Items.BUCKET);
	private static final ItemStack WATER = new ItemStack(Items.WATER_BUCKET);

	public class GuiButtonMode extends GuiButton {
		public boolean isExtract;

		public GuiButtonMode(int buttonId, int x, int y) {
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
				this.mouseDragged(mc, mouseX, mouseY);
				renderItemInGui(isExtract ? BUCKET : WATER, x + 2, y + 2, -20, -20);
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		buttonMode.isExtract = te.getMode();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			sendButtonUpdate(0, te, te.getMode() ? 0 : 1);
		}
	}
}
