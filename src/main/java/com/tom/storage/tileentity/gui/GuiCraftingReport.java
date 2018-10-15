package com.tom.storage.tileentity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.gui.GuiTomsLib;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.lib.utils.RenderUtil;
import com.tom.network.messages.MessageCraftingReportSync.MessageType;
import com.tom.storage.handler.AutoCraftingHandler;
import com.tom.storage.handler.StorageNetworkGrid.ICraftingReportScreen;
import com.tom.storage.handler.StorageNetworkGrid.IStorageTerminalGui;
import com.tom.storage.tileentity.gui.GuiTerminalBase.GuiButtonTermMode;
import com.tom.util.TomsModUtils;

@SideOnly(Side.CLIENT)
public class GuiCraftingReport extends GuiScreen implements INBTPacketReceiver, ICraftingReportScreen {
	private AutoCraftingHandler.CalculatedClientCrafting crafting;
	private int xSize = 238;
	private int ySize = 206;
	protected int guiLeft;
	protected int guiTop;
	protected boolean tallTerm, wideTerm;
	@SuppressWarnings("rawtypes")
	private List<AutoCraftingHandler.ClientCraftingStack> itemListClient = new ArrayList<>(15);
	private static final ResourceLocation gui = new ResourceLocation("tomsmod:textures/gui/crafting2.png");
	private IStorageTerminalGui parent;
	private GuiButton buttonCancel, buttonSwitchCPU, buttonStart;
	/** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
	private float currentScroll;
	/** True if the scrollbar is being dragged */
	private boolean isScrolling;
	/**
	 * True if the left mouse button was held down last time drawScreen was
	 * called.
	 */
	private boolean wasClicking;
	private static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	private NBTTagCompound tag = new NBTTagCompound();
	private NBTTagList stackList = new NBTTagList(), missingList = new NBTTagList(), recipeList = new NBTTagList();
	private boolean hasMain = false;
	private int missingSize;
	private int stackListSize;
	private int recipeListSize, cols, rows;
	private boolean skip;
	private GuiButtonTermMode buttonModeTall, buttonModeWide;

	public GuiCraftingReport(IStorageTerminalGui parent, boolean skip) {
		this.parent = parent;
		this.skip = skip;
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		if (message.getBoolean("ERROR")) {
			close(false);
		} else {
			try {
				System.out.println(message);
				if (message.getBoolean("s")) {
					byte id = message.getByte("id");
					switch (MessageType.values()[id]) {
					case MAIN:
						tag.merge(message.getCompoundTag("m"));
						hasMain = true;
						missingSize = message.getInteger("ms");
						stackListSize = message.getInteger("sls");
						recipeListSize = message.getInteger("rls");
						NBTTagCompound msg = message.getCompoundTag("e");
						byte termMode = msg.getByte("cm");
						boolean wideTerm = TomsModUtils.getBit(termMode, 4);
						boolean tallTerm = TomsModUtils.getBit(termMode, 5);
						if (tallTerm != this.tallTerm || wideTerm != this.wideTerm) {
							this.tallTerm = tallTerm;
							this.wideTerm = wideTerm;
							parent.setDisplayMode(wideTerm, tallTerm);
							initGui();
						}
						break;
					case MISSING:
						missingList = message.getTagList("m", 10);
						break;
					case NORMAL:
						stackList = message.getTagList("m", 10);
						break;
					case RECIPE:
						recipeList = message.getTagList("m", 10);
						break;
					default:
						break;
					}
					if (hasMain) {
						if (stackList.tagCount() == stackListSize && missingList.tagCount() == missingSize && recipeList.tagCount() == recipeListSize) {
							tag.setTag("mi", missingList);
							tag.setTag("c", recipeList);
							tag.setTag("l", stackList);
							crafting = AutoCraftingHandler.readCalculatedCraftingFromNBT(tag);
							scrollTo(0);
						}
					}
				} else {
					byte termMode = message.getByte("cm");
					boolean wideTerm = TomsModUtils.getBit(termMode, 4);
					boolean tallTerm = TomsModUtils.getBit(termMode, 5);
					if (tallTerm != this.tallTerm || wideTerm != this.wideTerm) {
						this.tallTerm = tallTerm;
						this.wideTerm = wideTerm;
						parent.setDisplayMode(wideTerm, tallTerm);
						initGui();
					}
				}
			} catch (Throwable e) {
				close(false);
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		mc.getTextureManager().bindTexture(gui);
		if (parent.isTall()) {
			if (parent.isWide()) {
				this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 76, 41);
				for (int x = 1;x < cols;x++) {
					this.drawTexturedModalRect(this.guiLeft + x * 68 + 8, this.guiTop, 76, 0, 68, 41);
					this.drawTexturedModalRect(this.guiLeft + x * 68 + 8, this.guiTop + rows * 23 + 18, 76, 133, 68, 73);
				}
				this.drawTexturedModalRect(this.guiLeft, this.guiTop + rows * 23 + 18, 0, 133, 76, 73);
				for (int y = 1;y < rows;y++) {
					this.drawTexturedModalRect(this.guiLeft, this.guiTop + y * 23 + 18, 0, 41, 76, 23);
					for (int x = 1;x < cols;x++) {
						this.drawTexturedModalRect(this.guiLeft + x * 68 + 8, this.guiTop + y * 23 + 18, 76, 41, 68, 23);
					}
					this.drawTexturedModalRect(this.guiLeft + cols * 68 + 8, this.guiTop + y * 23 + 18, 212, 42, 26, 23);
				}
				this.drawTexturedModalRect(this.guiLeft + cols * 68 + 8, this.guiTop, 212, 0, 26, 42);
				this.drawTexturedModalRect(this.guiLeft + cols * 68 + 8, this.guiTop + rows * 23 + 18, 212, 133, 26, 73);
			} else {
				this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 238, 41);
				for (int x = 1;x < rows;x++) {
					this.drawTexturedModalRect(this.guiLeft, this.guiTop + x * 23 + 18, 0, 41, 238, 23);
				}
				this.drawTexturedModalRect(this.guiLeft, this.guiTop + rows * 23 + 18, 0, 133, 238, 73);
			}
		} else if (parent.isWide()) {
			this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 76, this.ySize);
			for (int x = 1;x < cols;x++) {
				this.drawTexturedModalRect(this.guiLeft + x * 68 + 8, this.guiTop, 76, 0, 68, this.ySize);
			}
			this.drawTexturedModalRect(this.guiLeft + cols * 68 + 8, this.guiTop, 212, 0, 26, this.ySize);
		} else {
			this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		}
		{
			boolean flag = Mouse.isButtonDown(0);
			int i = this.guiLeft;
			int j = this.guiTop;
			int k = i + 14 + (cols * 68);
			int l = j + 19;
			int i1 = k + 14;
			int j1 = l + rows * 23 + 1;

			if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1) {
				this.isScrolling = this.needsScrollBars();
			}

			if (!flag) {
				this.isScrolling = false;
			}
			this.wasClicking = flag;

			if (this.isScrolling) {
				this.currentScroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
				this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
				scrollTo(this.currentScroll);
			}
			super.drawScreen(mouseX, mouseY, partialTicks);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
			i = k;
			j = l;
			k = j1;
			this.drawTexturedModalRect(i, j + (int) ((k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
		}
		if (crafting != null) {
			GL11.glPushMatrix();
			RenderHelper.enableGUIStandardItemLighting();
			for (int i = 0;i < Math.min(itemListClient.size(), cols * rows);i++) {
				int x = guiLeft + 9 + i % cols * 68;
				int y = guiTop + 19 + i / cols * 23;
				renderCraftingAt(x, y, itemListClient.get(i), mouseX, mouseY);
			}
			GL11.glPopMatrix();
			int secTime = MathHelper.ceil(crafting.time / 20D);
			GL11.glPushMatrix();
			GL11.glTranslated(guiLeft + 5, guiTop + 5, zLevel);
			double scale = 0.71D;
			GL11.glScaled(scale, scale, scale);
			mc.fontRenderer.drawString(I18n.format("tomsMod.storage.craftingPlan", crafting.memory, crafting.operations, secTime / 60, secTime % 60), 0, 0, 4210752);
			GL11.glPopMatrix();
			for (int i = 0;i < Math.min(itemListClient.size(), cols * rows);i++) {
				int x = guiLeft + 9 + i % cols * 68;
				int y = guiTop + 19 + i / cols * 23;
				AutoCraftingHandler.ClientCraftingStack s = itemListClient.get(i);
				if (mouseX >= x && mouseY >= y && mouseX < x + 68 && mouseY < y + 22) {
					if (s != null) {
						s.draw(x, y, mouseX, mouseY, this, true);
					}
				}
			}
		} else {
			GL11.glPushMatrix();
			GL11.glTranslated(guiLeft + 5, guiTop + 5, zLevel);
			double scale = 0.71D;
			GL11.glScaled(scale, scale, scale);
			mc.fontRenderer.drawString(I18n.format("tomsMod.storage.craftingPlan", "?", "?", "--", "--"), 0, 0, 4210752);
			GL11.glPopMatrix();
		}
		if (buttonModeTall.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.displayMode_" + buttonModeTall.type)), mouseX, mouseY);
		}
		if (buttonModeWide.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storage.displayMode_" + buttonModeWide.type)), mouseX, mouseY);
		}
	}

	private boolean needsScrollBars() {
		return crafting != null ? this.crafting.stacks.size() > rows * cols : false;
	}

	@Override
	public void initGui() {
		labelList.clear();
		buttonList.clear();
		if (parent.isTall()) {
			rows = (height - 73 - 19 - 20) / 22;
			if (parent.isWide()) {// y:73+19
				cols = (width - 9 - 26 - 38) / 67;
			} else {
				cols = 3;
			}
		} else if (parent.isWide()) {
			cols = (width - 9 - 26 - 38) / 67;
			rows = 5;
		} else {
			rows = 5;
			cols = 3;
		}
		ySize = rows * 22 + 73 + 23;
		xSize = cols * 67 + 9 + 28;
		super.initGui();
		this.guiLeft = (this.width - xSize) / 2;
		this.guiTop = (this.height - ySize) / 2;
		if (crafting != null)
			scrollTo(0);
		buttonCancel = new GuiButton(0, guiLeft + 5, guiTop + (rows - 5) * 23 + 180, 50, 20, I18n.format("gui.cancel"));
		buttonList.add(buttonCancel);
		buttonStart = new GuiButton(1, guiLeft + (cols - 3) * 68 + 163, guiTop + (rows - 5) * 23 + 180, 50, 20, I18n.format("tomsmod.gui.start"));
		buttonList.add(buttonStart);
		buttonSwitchCPU = new GuiButtonExt(2, guiLeft + 10, guiTop + (rows - 5) * 23 + 136, cols * 68 - 2, 20, I18n.format("tomsMod.storage.craftingCPU", I18n.format("tomsMod.storage.autoCpu")));
		buttonList.add(buttonSwitchCPU);
		buttonModeTall = new GuiButtonTermMode(3, guiLeft - 18, guiTop + 5);
		buttonList.add(buttonModeTall);
		buttonModeWide = new GuiButtonTermMode(4, guiLeft - 18, guiTop + 23);
		buttonList.add(buttonModeWide);
	}

	public void scrollTo(float p_148329_1_) {
		int i = (this.crafting.stacks.size() + cols - 1) / cols - rows;
		int j = (int) (p_148329_1_ * i + 0.5D);

		if (j < 0) {
			j = 0;
		}

		for (int k = 0;k < rows;++k) {
			for (int l = 0;l < cols;++l) {
				int i1 = l + (k + j) * cols;

				if (i1 >= 0 && i1 < this.crafting.stacks.size()) {
					setSlotContents(l + k * cols, this.crafting.stacks.get(i1));
				} else {
					setSlotContents(l + k * cols, null);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void setSlotContents(int i, AutoCraftingHandler.ClientCraftingStack s) {
		if (itemListClient.size() > i) {
			itemListClient.set(i, s);
		} else {
			itemListClient.add(s);
		}
	}

	@SuppressWarnings("rawtypes")
	private void renderCraftingAt(int posX, int posY, AutoCraftingHandler.ClientCraftingStack s, int mouseX, int mouseY) {
		if (s != null) {
			s.draw(posX, posY, mouseX, mouseY, this, false);
		}
	}

	@Override
	public void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, int color, String... extraInfo) {
		if (stack != null) {
			boolean hasBg = mouseX >= x - 1 && mouseY >= y - 1 && mouseX < x + 17 && mouseY < y + 17;
			if (hasBg) {
				RenderUtil.setColourWithAlphaPercent(color, 50);
				RenderUtil.drawRect(x, y, 16, 16);
			}
			GlStateManager.translate(0.0F, 0.0F, 32.0F);
			this.zLevel = 100.0F;
			this.itemRender.zLevel = 100.0F;
			FontRenderer font = null;
			if (stack != null)
				font = stack.getItem().getFontRenderer(stack);
			if (font == null)
				font = fontRenderer;
			GlStateManager.enableDepth();
			this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
			this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, null);
			this.zLevel = 0.0F;
			this.itemRender.zLevel = 0.0F;
			if (hasBg) {
				List<String> list = stack.getTooltip(mc.player, GuiTomsLib.getTooltipFlag());
				// list.add(I18n.format("tomsmod.gui.amount", stack.stackSize));
				if (extraInfo != null && extraInfo.length > 0) {
					list.addAll(TomsModUtils.getStringList(extraInfo));
				}
				for (int i = 0;i < list.size();++i) {
					if (i == 0) {
						list.set(i, stack.getRarity().rarityColor + list.get(i));
					} else {
						list.set(i, TextFormatting.GRAY + list.get(i));
					}
				}
				this.drawHoveringText(list, mouseX, mouseY);
			}
		}
	}

	private void close(boolean craft) {
		mc.displayGuiScreen(parent.getScreen());
		parent.sendCrafting(crafting == null ? -1 : crafting.cpuId, craft);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			close(false);
		} else if (button.id == 1) {
			close(true);
		} else if (button.id == 2) {
			if (crafting.cpus.size() > 0) {
				if (crafting.cpuId == -1) {
					int next = crafting.cpus.get(0);
					crafting.cpuId = next;
				} else {
					int index = crafting.cpus.indexOf(crafting.cpuId) + 1;
					if (index < crafting.cpus.size()) {
						crafting.cpuId = crafting.cpus.get(index);
					} else {
						crafting.cpuId = -1;
					}
				}
			}
		} else if (button.id == 3) {
			parent.sendDisplayMode(parent.isWide(), !parent.isTall());
		} else if (button.id == 4) {
			parent.sendDisplayMode(!parent.isWide(), parent.isTall());
		}
	}

	@Override
	public void updateScreen() {
		if (crafting == null) {
			buttonSwitchCPU.displayString = I18n.format("tomsmod.gui.pleaseWait");
			buttonSwitchCPU.enabled = false;
			buttonStart.enabled = false;
		} else {
			boolean startEnabled;
			buttonSwitchCPU.displayString = ((buttonSwitchCPU.enabled = startEnabled = crafting.cpus.size() > 0) ? getButtonText() : I18n.format("tomsMod.stoarge.noCPUsAvailable"));
			buttonStart.enabled = startEnabled && !crafting.hasMissing;
		}
		buttonModeTall.type = parent.isTall() ? 0 : 1;
		buttonModeWide.type = parent.isWide() ? 2 : 1;
		if (skip) {
			if (buttonStart.enabled)
				close(true);
		}
	}

	private String getButtonText() {
		return I18n.format("tomsMod.storage.craftingCPU", crafting.cpuId == -1 ? I18n.format("tomsMod.storage.autoCpu") : I18n.format("tomsMod.storage.cpuId", crafting.cpuId));
	}

	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = Mouse.getEventDWheel();

		if (i != 0 && this.needsScrollBars()) {
			int j = crafting.stacks.size() / 3 - 5;

			if (i > 0) {
				i = 1;
			}

			if (i < 0) {
				i = -1;
			}

			this.currentScroll = (float) (this.currentScroll - (double) i / (double) j);
			this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
			scrollTo(this.currentScroll);
		}
	}

	@Override
	public void drawHoveringText(List<String> textLines, int x, int y) {
		super.drawHoveringText(textLines, x, y);
	}

	@Override
	public float getZLevel() {
		return zLevel;
	}

	@Override
	public FontRenderer getFontRenderer() {
		return mc.fontRenderer;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
			mc.displayGuiScreen((GuiScreen) parent);
		}
	}
}
