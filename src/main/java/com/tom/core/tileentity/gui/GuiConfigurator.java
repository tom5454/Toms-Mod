package com.tom.core.tileentity.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.gui.GuiTomsLib;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.tileentity.IConfigurable;
import com.tom.handler.ConfiguratorHandler.ConfigurableDevice;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.network.messages.MessageNBT.MessageNBTRequest;

import com.tom.core.tileentity.inventory.ContainerConfigurator;
import com.tom.core.tileentity.inventory.ContainerConfigurator.ContainerConfiguratorChoose;

public class GuiConfigurator extends GuiTomsLib implements INBTPacketReceiver {
	private IConfigurable te;
	private int invX, invY;

	/*public GuiConfigurator(InventoryPlayer playerInv, IConfigurable configurable) {
		super(new ContainerConfigurator(playerInv, configurable), "configurator");
		te = configurable;
		NetworkHandler.sendToServer(new MessageNBTRequest(te.getPos2()));
	}*/
	public GuiConfigurator(EntityPlayer player, World world, BlockPos pos, int s) {
		super(new ContainerConfigurator(player, world, pos, s), "configurator");
		te = ((ContainerConfigurator) inventorySlots).te;
		if (te != null)
			NetworkHandler.sendToServer(new MessageNBTRequest(te.getPos2(), ((ContainerConfigurator) inventorySlots).side));
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		te.getOption().readFromNBTPacket(message);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		if (te != null) {
			NBTTagCompound tag = new NBTTagCompound();
			te.getOption().writeModificationNBTPacket(tag);
			NetworkHandler.sendToServer(new MessageNBT(tag, te.getPos2(), ((ContainerConfigurator) inventorySlots).side));
		}
	}

	@Override
	public void initGui() {
		if (te != null) {
			ySize = 120 + te.getOption().getHeight();
			xSize = Math.max(175, te.getOption().getWidth());
			labelList.clear();
			super.initGui();
			invX = xSize / 2 - 174 / 2;
			invY = te.getOption().getHeight() + 10;
			((ContainerConfigurator) inventorySlots).slotData.forEach(v -> v.setOffset(invX, invY - 82));
			te.getOption().init(mc, guiLeft + xSize / 2 - te.getOption().getWidth() / 2, guiTop + 8, -1, buttonList, labelList);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (te != null)
			te.getOption().actionPreformed(mc, button);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		if (te != null) {
			if (te.getOption().drawBackground()) {
				mc.renderEngine.bindTexture(gui);
				int w = te.getOption().getWidth() + 30;
				int h = te.getOption().getHeight() + 30;
				int y = guiTop - 22;
				drawTexturedModalRect(invX + guiLeft, invY + guiTop, 0, 82, 176, 94);
				drawTexturedModalRect(guiLeft + xSize / 2 - w / 2, y, 0, 0, 10, 10);
				drawTexturedModalRect(guiLeft + xSize / 2 - w / 2, y + h - 10, 0, 72, 10, 10);
				drawTexturedModalRect(guiLeft + xSize / 2 + w / 2, y, 166, 0, 10, 10);
				drawTexturedModalRect(guiLeft + xSize / 2 + w / 2, y + h - 10, 166, 72, 10, 10);
				drawScaledCustomSizeModalRect(guiLeft + xSize / 2 - w / 2 + 10, y + 10, 10, 10, 150, 60, w - 10, h - 20, 256, 256);
				drawScaledCustomSizeModalRect(guiLeft + xSize / 2 - w / 2 + 10, y, 10, 0, 150, 10, w - 10, 10, 256, 256);
				drawScaledCustomSizeModalRect(guiLeft + xSize / 2 - w / 2 + 10, y + h - 10, 10, 72, 150, 10, w - 10, 10, 256, 256);
				drawScaledCustomSizeModalRect(guiLeft + xSize / 2 - w / 2, y + 10, 0, 10, 10, 60, 10, h - 20, 256, 256);
				drawScaledCustomSizeModalRect(guiLeft + xSize / 2 + w / 2, y + 10, 166, 10, 10, 60, 10, h - 20, 256, 256);
			}
			te.getOption().renderBackground(mc, guiLeft + xSize / 2 - te.getOption().getWidth() / 2, guiTop + 8);
			te.getOption().renderForeground(mc, guiLeft + xSize / 2 - te.getOption().getWidth() / 2, guiTop + 8, mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		fontRenderer.drawString(I18n.format("container.inventory"), invX + 8, invY + 2, 4210752);
		String s = I18n.format("item.tm.configurator.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, -16, 4210752);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (te == null)
			mc.player.closeScreen();
		else
			te.getOption().update(mc, guiLeft + 100 - te.getOption().getWidth(), guiTop + 70 - te.getOption().getHeight());
	}

	public static class GuiButtonConfig extends GuiButton {

		public GuiButtonConfig(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
			super(buttonId, x, y, widthIn, heightIn, buttonText);
		}

		public GuiButtonConfig(int buttonId, int x, int y, String buttonText) {
			super(buttonId, x, y, buttonText);
		}

		public void postDraw(Minecraft mc, int mouseX, int mouseY, GuiTomsLib gui) {
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (int i = 0;i < this.buttonList.size();++i) {
			if (this.buttonList.get(i) instanceof GuiButtonConfig)
				((GuiButtonConfig) this.buttonList.get(i)).postDraw(this.mc, mouseX, mouseY, this);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		te.getOption().mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		te.getOption().keyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	public Rectangle getConfigGuiBB() {
		if (te == null)
			return new Rectangle();
		int w = te.getOption().getWidth() + 50;
		int h = te.getOption().getHeight() + 30;
		int y = guiTop - 22;
		return new Rectangle(guiLeft + xSize / 2 - w / 2, y, w, h);
	}

	public static class GuiConfiguratorChoose extends GuiTomsLib implements INBTPacketReceiver {
		private List<ConfigurableDevice> d = new ArrayList<>();

		public GuiConfiguratorChoose(World world, BlockPos pos, EntityPlayer player) {
			super(new ContainerConfiguratorChoose(world, pos, player), "");
		}

		@Override
		public void receiveNBTPacket(NBTTagCompound message) {
			NBTTagList l = message.getTagList("l", 10);
			d.clear();
			for (int i = 0;i < l.tagCount();i++) {
				d.add(new ConfigurableDevice(l.getCompoundTagAt(i)));
			}
			initGui();
		}

		@Override
		public void initGui() {
			xSize = 200;
			super.initGui();
			for (int i = 0;i < d.size();i++) {
				buttonList.add(new GuiButtonConfigurableSelect(i, guiLeft, guiTop + i * 22, 200, d.get(i), this));
			}
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		}

		@Override
		protected void actionPerformed(GuiButton button) throws IOException {
			if (button instanceof GuiButtonConfigurableSelect) {
				EnumFacing f = ((GuiButtonConfigurableSelect) button).d.getSide();
				sendButtonUpdate(f == null ? -1 : f.ordinal());
			}
		}

		public static class GuiButtonConfigurableSelect extends GuiButton {
			private final ConfigurableDevice d;
			private final GuiConfiguratorChoose gui;

			public GuiButtonConfigurableSelect(int buttonId, int x, int y, int w, ConfigurableDevice buttonText, GuiConfiguratorChoose gui) {
				super(buttonId, x, y, w, 20, I18n.format(buttonText.getName() + ".name") + " (" + (buttonText.getSide() == null ? I18n.format("tomsmod.direction.center") : I18n.format("tomsmod.direction." + buttonText.getSide().getName())) + ")");
				d = buttonText;
				this.gui = gui;
			}

			/**
			 * Draws this button to the screen.
			 */
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
				if (this.visible) {
					FontRenderer fontrenderer = mc.fontRenderer;
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
					int j = 14737632;

					if (packedFGColour != 0) {
						j = packedFGColour;
					} else if (!this.enabled) {
						j = 10526880;
					} else if (this.hovered) {
						j = 16777120;
					}

					this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
					GlStateManager.pushMatrix();
					RenderHelper.enableGUIStandardItemLighting();
					gui.renderItemInGui(d.getStack(), x + 1, y + 1, -200, -200);
					RenderHelper.disableStandardItemLighting();
					GlStateManager.popMatrix();
				}
			}
		}
	}
}
