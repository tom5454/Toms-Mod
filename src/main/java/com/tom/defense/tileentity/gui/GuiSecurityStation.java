package com.tom.defense.tileentity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.tileentity.AccessType;
import com.tom.apis.TomsModUtils;
import com.tom.client.GuiButtonRedstoneMode;
import com.tom.defense.DefenseInit;
import com.tom.defense.item.IdentityCard;
import com.tom.defense.tileentity.TileEntitySecurityStation;
import com.tom.defense.tileentity.inventory.ContainerSecurityStation;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiSecurityStation extends GuiTomsMod {
	private GuiButtonRedstoneMode buttonRedstone;
	private GuiSecurityButton buttonBlockModification, buttonConfiguration, buttonFieldTransport, buttonStayInArea,
			buttonHaveInventory, buttonSwitch, buttonRightsModification;
	private GuiButton buttonCopy;
	private TileEntitySecurityStation te;

	public GuiSecurityStation(InventoryPlayer inv, TileEntitySecurityStation te) {
		super(new ContainerSecurityStation(inv, te), "securityGui");
		this.te = te;
	}

	private static final ItemStack diamondPickaxeStack = new ItemStack(Items.DIAMOND_PICKAXE),
			multitoolConfiguratorStack, multitoolFieldTransportStack, chestStack = new ItemStack(Blocks.CHEST),
			multitoolSwitchStack, multitoolEncodeStack;
	static {
		multitoolConfiguratorStack = new ItemStack(DefenseInit.multiTool, 1, 4);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("isInCreativeTabIcon", true);
		multitoolConfiguratorStack.setTagCompound(tag);
		multitoolFieldTransportStack = new ItemStack(DefenseInit.multiTool, 1, 2);
		tag = new NBTTagCompound();
		tag.setBoolean("isInCreativeTabIcon", true);
		multitoolFieldTransportStack.setTagCompound(tag);
		multitoolSwitchStack = new ItemStack(DefenseInit.multiTool, 1, 1);
		tag = new NBTTagCompound();
		tag.setBoolean("isInCreativeTabIcon", true);
		multitoolSwitchStack.setTagCompound(tag);
		multitoolEncodeStack = new ItemStack(DefenseInit.multiTool, 1, 3);
		tag = new NBTTagCompound();
		tag.setBoolean("isInCreativeTabIcon", true);
		multitoolEncodeStack.setTagCompound(tag);
	}
	private List<RenderButton> renderExtraButtonInformation = new ArrayList<>();
	private List<RenderButton> renderExtraButtonInformation2 = new ArrayList<>();
	private List<GuiSecurityButton> securityButtonList = new ArrayList<>();

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		int id = button.id;
		if (id == 8) {
			this.sendButtonUpdate(id, te, te.rsMode.ordinal() + 1);
		} else if (id < 8)
			this.sendButtonUpdate(button.id, te);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		double per = (te.getField(0) * 1D) / te.getMaxEnergyStored();
		double p = 65D * per;// 65
		// int p = 50;
		this.drawTexturedModalRect(guiLeft + 15, guiTop + 102, 0, 211, p, 12);// 8,74
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.securityStation.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		fontRenderer.drawString(te.getMaxEnergyStored() / 1000 + "kF/" + te.getField(0) + "F", 14, 93, 4210752);
		fontRenderer.drawString(I18n.format("tomsmod.render.master"), 198, 32, 4210752);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.buttonRedstone.controlType = te.rsMode;
		buttonBlockModification.visible = buttonConfiguration.visible = buttonFieldTransport.visible = buttonHaveInventory.visible = buttonRightsModification.visible = buttonStayInArea.visible = buttonSwitch.visible = te.getStackInSlot(2) != null;
		List<AccessType> rights = IdentityCard.decompileRights(te.rightListClient);
		buttonBlockModification.enabled = buttonConfiguration.enabled = buttonFieldTransport.enabled = buttonHaveInventory.enabled = buttonRightsModification.enabled = buttonStayInArea.enabled = buttonSwitch.enabled = false;
		for (AccessType t : rights)
			this.securityButtonList.get(t.ordinal()).enabled = true;
	}

	@Override
	public void initGui() {
		renderExtraButtonInformation.clear();
		renderExtraButtonInformation2.clear();
		securityButtonList.clear();
		this.xSize = 256;
		this.ySize = 211;
		super.initGui();
		this.buttonCopy = new GuiButton(0, guiLeft + 110, guiTop + 95, 30, 20, I18n.format("tomsmod.gui.copy"));
		this.buttonList.add(buttonCopy);
		int buttonY = guiTop + 50;
		int buttonX = guiLeft + 13;
		this.buttonBlockModification = new GuiSecurityButton(1, buttonX, buttonY, new RenderButton() {

			@Override
			public void render(int posX, int posY) {
				renderItemInGui(diamondPickaxeStack, posX, posY, -1, -1);
			}

		}, I18n.format("tomsmod.render.blockModification"));
		this.buttonList.add(buttonBlockModification);
		buttonX += 18;
		this.buttonConfiguration = new GuiSecurityButton(2, buttonX, buttonY, new RenderButton() {

			@Override
			public void render(int posX, int posY) {
				renderItemInGui(multitoolConfiguratorStack, posX, posY, -1, -1);
			}

		}, I18n.format("tomsmod.render.configuration"));
		this.buttonList.add(buttonConfiguration);
		buttonX += 18;
		this.buttonFieldTransport = new GuiSecurityButton(3, buttonX, buttonY, new RenderButton() {

			@Override
			public void render(int posX, int posY) {
				renderItemInGui(multitoolFieldTransportStack, posX, posY, -1, -1);
			}

		}, I18n.format("tomsmod.render.fieldTransport"));
		this.buttonList.add(buttonFieldTransport);
		buttonX += 18;
		this.buttonStayInArea = new GuiSecurityButton(4, buttonX, buttonY, new RenderButton() {

			@Override
			public void render(int posX, int posY) {
				mc.getTextureManager().bindTexture(gui);
				drawTexturedModalRect(posX, posY, 66, 212, 16, 16);
			}

		}, I18n.format("tomsmod.render.stayInArea"));
		this.buttonList.add(buttonStayInArea);
		buttonX += 18;
		this.buttonHaveInventory = new GuiSecurityButton(5, buttonX, buttonY, new RenderButton() {

			@Override
			public void render(int posX, int posY) {
				renderItemInGui(chestStack, posX, posY, -1, -1);
			}

		}, I18n.format("tomsmod.render.haveInventory"));
		this.buttonList.add(buttonHaveInventory);
		buttonX += 18;
		this.buttonSwitch = new GuiSecurityButton(6, buttonX, buttonY, new RenderButton() {

			@Override
			public void render(int posX, int posY) {
				renderItemInGui(multitoolSwitchStack, posX, posY, -1, -1);
			}

		}, I18n.format("tomsmod.render.useSwitch"));
		this.buttonList.add(buttonSwitch);
		buttonX += 18;
		this.buttonRightsModification = new GuiSecurityButton(7, buttonX, buttonY, new RenderButton() {

			@Override
			public void render(int posX, int posY) {
				renderItemInGui(multitoolEncodeStack, posX, posY, -1, -1);
			}

		}, I18n.format("tomsmod.render.rightsModification"));
		this.buttonList.add(buttonRightsModification);
		this.buttonRedstone = new GuiButtonRedstoneMode(8, guiLeft + 143, guiTop + 23, te.rsMode);
		this.buttonList.add(buttonRedstone);
	}

	public class GuiSecurityButton extends GuiButton {
		private RenderButton render;

		public GuiSecurityButton(int buttonId, int x, int y, RenderButton render, String hoveringText) {
			super(buttonId, x, y, 18, 18, hoveringText);
			this.render = render;
			renderExtraButtonInformation.add(new RenderButton() {

				@Override
				public void render(int posX, int posY) {
					if (visible && hovered)
						drawHoveringText(TomsModUtils.getStringList(displayString), posX, posY);
				}
			});
			renderExtraButtonInformation2.add(new RenderButton() {

				@Override
				public void render(int posX, int posY) {
					if (visible)
						GuiSecurityButton.this.render.render(x + 1, y + 1);
				}
			});
			this.enabled = false;
			securityButtonList.add(this);
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
				this.drawTexturedModalRect(this.x, this.y, 83 + (this.enabled ? 18 : 0), 211, this.width, this.height);
				// this.drawTexturedModalRect(this.xPosition + this.width / 2,
				// this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width
				// / 2, this.height);
				// render.render(xPosition+1, yPosition+1);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}

		/**
		 * Returns true if the mouse has been pressed on this control.
		 * Equivalent of MouseListener.mousePressed(MouseEvent e).
		 */
		@Override
		public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
			return this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		}
	}

	public static interface RenderButton {
		void render(int posX, int posY);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (int i = 0;i < this.renderExtraButtonInformation2.size();i++) {
			this.renderExtraButtonInformation2.get(i).render(mouseX, mouseY);
		}
		for (int i = 0;i < this.renderExtraButtonInformation.size();i++) {
			this.renderExtraButtonInformation.get(i).render(mouseX, mouseY);
		}
		buttonRedstone.postDraw(mc, mouseX, mouseY, this);
	}
}
