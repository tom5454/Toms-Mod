package com.tom.defense.tileentity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.tom.api.gui.GuiNumberValueBox;
import com.tom.api.gui.GuiNumberValueBox.GuiButtonNextNum;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.apis.TomsModUtils;
import com.tom.defense.ProjectorLensConfigEntry;
import com.tom.defense.tileentity.inventory.ContainerProjectorLensConfigurationMain;
import com.tom.defense.tileentity.inventory.ContainerProjectorLensConfigurationMain.ContainerProjectorLensConfig;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiProjectorLensConfigurationMain extends GuiTomsMod implements INBTPacketReceiver {
	private int selected = -1, showing = 0;
	private List<ProjectorLensConfigEntry> entryList = new ArrayList<>();
	private List<GuiButtonEntry> list = new ArrayList<>();
	private List<Runnable> itemRendererList = new ArrayList<>();
	private GuiButtonScroll buttonScrollUp, buttonScrollDown;
	private GuiButton buttonNew, buttonEdit, buttonDelete;

	public GuiProjectorLensConfigurationMain(EntityPlayer player) {
		super(new ContainerProjectorLensConfigurationMain(player), "projectorLensConfigMain");
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		NBTTagList list = message.getTagList("l", 10);
		entryList.clear();
		for (int i = 0;i < list.tagCount();i++) {
			entryList.add(ProjectorLensConfigEntry.fromNBTClient(list.getCompoundTagAt(i)));
		}
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		int i = scaledresolution.getScaledWidth();
		int j = scaledresolution.getScaledHeight();
		this.setWorldAndResolution(this.mc, i, j);
		if (mc.player.openContainer instanceof INBTPacketReceiver) {
			((INBTPacketReceiver) mc.player.openContainer).receiveNBTPacket(message);
		}
	}

	@Override
	public void initGui() {
		ySize = 176;
		list.clear();
		itemRendererList.clear();
		super.initGui();
		int cfgBX = guiLeft + 124;
		this.buttonNew = new GuiButton(0, cfgBX, guiTop + 24, 40, 20, I18n.format("tomsmod.gui.new"));
		this.buttonEdit = new GuiButton(1, cfgBX, guiTop + 45, 40, 20, I18n.format("tomsmod.gui.edit"));
		this.buttonDelete = new GuiButton(2, cfgBX, guiTop + 66, 40, 20, I18n.format("tomsmod.gui.delete"));
		this.buttonList.add(buttonNew);
		this.buttonList.add(buttonEdit);
		this.buttonList.add(buttonDelete);
		this.buttonScrollUp = new GuiButtonScroll(3, cfgBX, guiTop + 15, false);
		this.buttonScrollDown = new GuiButtonScroll(4, cfgBX + 10, guiTop + 15, true);
		this.buttonList.add(buttonScrollUp);
		this.buttonList.add(buttonScrollDown);
		for (int i = 0;i < entryList.size();i++) {
			GuiButtonEntry b = new GuiButtonEntry(i, guiLeft + 8, 0, entryList.get(i));
			list.add(b);
			buttonList.add(b);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 4, 4210752);
		String s = I18n.format("item.tm.multitool.writer.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (int i = 0;i < itemRendererList.size();i++) {
			itemRendererList.get(i).run();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id < 3)
			this.sendButtonUpdate(button.id, selected);
		else if (button instanceof GuiButtonScroll) {
			GuiButtonScroll b = (GuiButtonScroll) button;
			if (b.isDown)
				this.showing++;
			else
				this.showing--;
		} else if (button instanceof GuiButtonEntry) {
			GuiButtonEntry b = (GuiButtonEntry) button;
			this.selected = b.getID();
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		int listSize = list.size();
		for (int i = 0;i < listSize;i++) {
			list.get(i).visible = false;
		}
		if (listSize > showing) {
			GuiButtonEntry c = list.get(showing);
			c.visible = true;
			c.y = guiTop + 16;
			if (listSize > showing + 1) {
				GuiButtonEntry c2 = list.get(showing + 1);
				c2.visible = true;
				c2.y = guiTop + 38;
				if (listSize > showing + 2) {
					GuiButtonEntry c3 = list.get(showing + 2);
					c3.visible = true;
					c3.y = guiTop + 60;
				}
			}
		}
		this.buttonDelete.enabled = this.buttonEdit.enabled = selected >= 0 && listSize > selected;
		this.buttonScrollUp.enabled = showing > 0 && listSize > 3;
		this.buttonScrollDown.enabled = showing + 2 < listSize && listSize > 3;
	}

	@Override
	protected boolean checkHotbarKeys(int keyCode) {
		return false;
	}

	public class GuiButtonScroll extends GuiButton {
		private boolean isDown;

		public GuiButtonScroll(int buttonId, int x, int y, boolean isDown) {
			super(buttonId, x, y, 9, 9, "");
			this.isDown = isDown;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 176 + i * 9, this.isDown ? 9 : 0, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public class GuiButtonEntry extends GuiButton {
		private ProjectorLensConfigEntry entry;
		private final int id;

		public GuiButtonEntry(int id, int x, int y, ProjectorLensConfigEntry entry) {
			super(id + 5, x, y, 112, 22, entry.getName());
			this.entry = entry;
			this.id = id;
			this.visible = false;
			itemRendererList.add(new Runnable() {

				@Override
				public void run() {
					if (visible)
						renderItemInGui(GuiButtonEntry.this.entry.getStack(), x + 3, y + 3, -1, -1);
				}
			});
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
				this.drawTexturedModalRect(this.x, this.y, 0, 176, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
				int j = 14737632;

				if (packedFGColour != 0) {
					j = packedFGColour;
				} else if (!this.enabled) {
					j = 10526880;
				} else if (this.hovered) {
					j = 16777120;
				} else if (selected == id) {
					j = 0x00cc00;
				}
				this.drawString(fontRenderer, this.displayString, this.x + 21, this.y + 2, j);
			}
		}

		public int getID() {
			return id;
		}
	}

	public static class GuiProjectorLensConfig extends GuiTomsMod implements INBTPacketReceiver {
		private GuiTextField fieldName;
		private ProjectorLensConfigEntry entry;
		private boolean textFieldFoucusedLast;
		private GuiNumberValueBox offsetX, offsetY, offsetZ;

		public GuiProjectorLensConfig(ProjectorLensConfigEntry entry, EntityPlayer player, int id) {
			super(new ContainerProjectorLensConfig(entry, player, id), "projectorLensConfig");
			this.entry = entry;
		}

		@Override
		public void receiveNBTPacket(NBTTagCompound message) {
			entry = ProjectorLensConfigEntry.fromNBTClient(message);
			fieldName.setText(entry.getName());
			offsetX.num = entry.getOffsetX();
			offsetY.num = entry.getOffsetY();
			offsetZ.num = entry.getOffsetZ();
		}

		@Override
		public void initGui() {
			ySize = 176;
			xSize = 210;
			labelList.clear();
			super.initGui();
			fieldName = new GuiTextField(0, fontRenderer, guiLeft + 6, guiTop + 6, 50, 10);
			fieldName.setMaxStringLength(30);
			fieldName.setTextColor(0xffffff);
			TomsModUtils.addTextFieldToLabelList(fieldName, labelList);
			if (entry != null)
				fieldName.setText(entry.getName());
			offsetX = new GuiNumberValueBox(0, guiLeft + 5, guiTop + 20, 32, -32);
			offsetY = new GuiNumberValueBox(1, guiLeft + 5, guiTop + 30, 32, -32);
			offsetZ = new GuiNumberValueBox(2, guiLeft + 5, guiTop + 40, 32, -32);
			offsetX.addToList(buttonList);
			offsetY.addToList(buttonList);
			offsetZ.addToList(buttonList);
			offsetX.num = entry.getOffsetX();
			offsetY.num = entry.getOffsetY();
			offsetZ.num = entry.getOffsetZ();
			TomsModUtils.addNumberValueBoxToLabelList(offsetX, labelList);
			TomsModUtils.addNumberValueBoxToLabelList(offsetY, labelList);
			TomsModUtils.addNumberValueBoxToLabelList(offsetZ, labelList);
		}

		@Override
		protected void actionPerformed(GuiButton button) throws IOException {
			if (button instanceof GuiButtonNextNum) {
				GuiNumberValueBox b = ((GuiButtonNextNum) button).parent;
				this.sendButtonUpdate(b.id, b.num);
			}
		}

		@Override
		protected boolean checkHotbarKeys(int keyCode) {
			return false;
		}

		@Override
		public void updateScreen() {
			super.updateScreen();
			boolean textFieldFoucusedLastO = textFieldFoucusedLast;
			textFieldFoucusedLast = fieldName.isFocused();
			if (textFieldFoucusedLast != textFieldFoucusedLastO) {
				this.sendUpdatePacket();
			}
			offsetX.update(true);
			offsetY.update(true);
			offsetZ.update(true);
		}

		@Override
		protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
			offsetX.onClicked(mouseX, mouseY);
			offsetY.onClicked(mouseX, mouseY);
			offsetZ.onClicked(mouseX, mouseY);
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

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			super.drawScreen(mouseX, mouseY, partialTicks);
			GL11.glPushMatrix();
			// fieldName.drawTextBox();
			offsetX.draw(mouseX, mouseY, false);
			offsetY.draw(mouseX, mouseY, false);
			offsetZ.draw(mouseX, mouseY, false);
			GL11.glPopMatrix();
		}

		private void sendUpdatePacket() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("s", fieldName.getText());
			NetworkHandler.sendToServer(new MessageNBT(tag));
		}
	}
}
