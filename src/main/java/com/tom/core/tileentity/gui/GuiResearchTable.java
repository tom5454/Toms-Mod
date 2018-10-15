package com.tom.core.tileentity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.gui.GuiTomsLib;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.research.IScanningInformation;
import com.tom.api.research.Research;
import com.tom.api.research.ResearchComplexity;
import com.tom.core.CoreInit;
import com.tom.core.research.ResearchHandler;
import com.tom.core.research.ResearchHandler.ResearchInformation;
import com.tom.lib.utils.RenderUtil;
import com.tom.lib.utils.TomsUtils.GuiRenderRunnable;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.TileEntityResearchTable;
import com.tom.core.tileentity.gui.GuiResearchTable.GuiResearchSelection.GuiResearchSelectionList.ResearchEntry;
import com.tom.core.tileentity.inventory.ContainerResearchTable;

public class GuiResearchTable extends GuiTomsLib implements INBTPacketReceiver {
	private GuiButtonCopy copyButton;
	private GuiButtonResearchStart researchButton;
	private GuiButtonMenu menuButton;
	private GuiButtonCraft craftButton;
	private final TileEntityResearchTable te;
	private static final Comparator<ResearchEntry> sorter = new ResearchEntrySorter();
	private NBTTagCompound tag = new NBTTagCompound();

	public GuiResearchTable(InventoryPlayer playerInv, TileEntityResearchTable te) {
		super(new ContainerResearchTable(playerInv, te), "restableGui_" + te.getType().getGuiName());
		this.xSize = 221;
		this.ySize = 219;
		this.te = te;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		int craftingTime = te.getField(1);
		int totalCraftingTime = te.getField(2);
		int researchTime = te.getField(3);
		int totalResearchTime = te.getField(4);
		float totalCraftingTimeF = totalCraftingTime < 1 ? 1 : totalCraftingTime;
		float totalResearchTimeF = totalResearchTime < 1 ? 1 : totalResearchTime;
		float perCraftingTime = craftingTime / totalCraftingTimeF;
		float perResearchTime = researchTime / totalResearchTimeF;
		double p1Per = perResearchTime * 100;
		double p3Per = perCraftingTime * 100;
		double p = p1Per / 100D * 55;// max:55
		this.drawTexturedModalRect(guiLeft + 112, guiTop + 45, 0, 235, p, 3);
		int p2Per = te.getField(0);
		if (p2Per > -1) {
			double p2 = p2Per / 100D * 16;// max:16
			p2 = p2 == 0 && p2Per != 0 ? 1 : p2;
			this.drawTexturedModalRect(guiLeft + 25, guiTop + 6 + 16 - p2, 0, 234 - p2, 2, p2);
		} else {
			RenderUtil.setColourWithAlphaPercent(0xFF0000, 70);
			RenderUtil.drawRect(guiLeft + 25, guiTop + 6, 2, 16);
			GlStateManager.color(1, 1, 1, 1);
		}
		double p3 = p3Per / 100D * 23;// max:23
		this.drawTexturedModalRect(guiLeft + 167, guiTop + 71, 2, 219, p3, 15);
		if (te.currentResearch != null) {
			mc.renderEngine.bindTexture(LIST_TEXTURE);
			GL11.glPushMatrix();
			this.drawTexturedModalRect(guiLeft + 30, guiTop + 6, 0, 120, 78, 100);
			ItemStack icon = te.currentResearch.getIcon();
			if (icon == null)
				icon = new ItemStack(Blocks.BARRIER);
			GL11.glPushMatrix();
			renderItemInGui(icon, guiLeft + 60, guiTop + 15, -50, -50);
			GL11.glPopMatrix();
			GL11.glPopMatrix();
			String text = I18n.format(te.currentResearch.getUnlocalizedName());
			drawWrappedText(guiLeft + 68, guiTop + 38, text, 16777215);
		}
	}

	private static final int LINE_SPACING = 2;

	private void drawWrappedText(int x, int y, String textN, int color) {
		List<String> textList = mc.fontRenderer.listFormattedStringToWidth(textN, 75);
		for (int i = 0;i < textList.size();i++) {
			String text = textList.get(i);
			int textWidth = mc.fontRenderer.getStringWidth(text);
			int tab = textWidth / 2;
			mc.fontRenderer.drawString(text, x - tab, y + (i * (mc.fontRenderer.FONT_HEIGHT + LINE_SPACING)), color, true);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		int id = button.id;
		if (id == 3)
			sendButtonUpdate(3, te, te.craftAll ? -1 : isShiftKeyDown() ? 1 : 0);
		else if (id < 4) {
			this.sendButtonUpdate(id, te);
		}
	}

	@Override
	public void initGui() {
		labelList.clear();
		super.initGui();
		copyButton = new GuiButtonCopy(0, guiLeft + 8, guiTop + 64);
		buttonList.add(copyButton);
		researchButton = new GuiButtonResearchStart(1, guiLeft + 30, guiTop + 108);
		buttonList.add(researchButton);
		menuButton = new GuiButtonMenu(2, guiLeft + 6, guiTop + 104);
		buttonList.add(menuButton);
		craftButton = new GuiButtonCraft(3, guiLeft + 168, guiTop + 60);
		buttonList.add(craftButton);
		TomsModUtils.addRunnableToLabelList(new GuiRenderRunnable() {

			@Override
			public void run(int mouseX, int mouseY) {
				RenderHelper.disableStandardItemLighting();
				GlStateManager.enableDepth();
				if (te.getField(4) < 1) {
					boolean researchable = te.currentResearch != null && te.getType().isResearchable(te.currentResearch.getComplexity());
					boolean flag = te.currentResearch != null && !te.completed;
					if (flag && researchable) {
						List<ItemStack> stackList = te.currentResearch.getResearchRequirements();
						if (stackList != null) {
							ItemStack[] stackA = te.getStacks();
							List<ItemStack> inStacks = new ArrayList<>();
							for (int i = 3;i < 7;i++) {
								ItemStack stack = stackA[i];
								if (stack != null) {
									inStacks.add(stack);
								}
							}
							List<Runnable> hover = new ArrayList<>();
							for (int i = 0;i < stackList.size();i++) {
								ItemStack stack = stackList.get(i);
								if (stack != null) {
									boolean flag1 = true;
									for (ItemStack inStack : inStacks) {
										boolean equals = inStack.getItem() == stack.getItem() && inStack.getCount() >= stack.getCount() && inStack.getItemDamage() == stack.getItemDamage();
										if (equals) {
											flag1 = false;
											break;
										}
									}
									hover.add(renderItemInGuiWithRunnableHover(stack, guiLeft + 50 + (((i + 1) % 2) * 18), guiTop + 65 + (i / 2 * 18), mouseX, mouseY, flag1));
									if (flag1) {
										flag = false;
									}
									if (i == 3)
										break;
								}
							}
							mc.fontRenderer.drawString(I18n.format("tomsmod.gui.requirements") + ":", guiLeft + 33, guiTop + 55, flag ? 0xFFFFFFFF : 0xFFFF0000);
							for (int i = 0;i < hover.size();i++) {
								hover.get(i).run();
							}
						}
					} else if (te.currentResearch != null && !researchable && !te.completed) {
						drawWrappedText(guiLeft + 68, guiTop + 55, I18n.format("tomsmod.gui.invalidTable"), 0xFFFF0000);
					}
					boolean hasPaper = te.getStacks()[17] != null && te.getStacks()[17].getItem() == Items.PAPER;
					boolean hasInk = te.getField(0) > 0;
					researchButton.enabled = flag && hasPaper && hasInk;
					if (te.currentResearch != null && !te.completed) {
						if (!hasPaper) {
							RenderUtil.setColourWithAlphaPercent(0xFF0000, 30);
							RenderUtil.drawRect(guiLeft + 6, guiTop + 24, 16, 16);
						}
						if (!hasInk) {
							RenderUtil.setColourWithAlphaPercent(0xFF0000, 30);
							RenderUtil.drawRect(guiLeft + 6, guiTop + 6, 16, 16);
						}
					}
				} else {
					researchButton.enabled = false;
					mc.fontRenderer.drawString(I18n.format("tomsmod.gui.running"), guiLeft + 33, guiTop + 55, 0xFFFFFFFF);
					boolean hasInk = te.getField(0) > 0;
					if (!hasInk) {
						RenderUtil.setColourWithAlphaPercent(0xFF0000, 30);
						RenderUtil.drawRect(guiLeft + 6, guiTop + 6, 16, 16);
					}
				}
				if (te.craftingError > 0) {
					mc.fontRenderer.drawString("!", guiLeft + 200, guiTop + 60, 0xFFFF0000);
					if (isPointInRegion(200, 60, 10, 10, mouseX, mouseY)) {
						drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.craftingError_" + te.craftingError)), mouseX, mouseY);
					}
				}
			}
		}, labelList);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.copyButton.enabled = te.getStacks()[0] != null && te.getStacks()[0].getItem() == CoreInit.bigNoteBook && te.getStacks()[0].getTagCompound() != null && te.getStacks()[0].getTagCompound().hasKey("owner") && te.getStacks()[1] != null && te.getStacks()[1].getItem() == CoreInit.noteBook;
		this.menuButton.enabled = te.getStacks()[0] != null && te.getStacks()[0].getItem() == CoreInit.bigNoteBook && te.getStacks()[0].getTagCompound() != null && te.getStacks()[0].getTagCompound().hasKey("owner") && te.getField(4) < 1;
		this.craftButton.enabled = te.getField(2) < 1 && te.getStacks()[0] != null && te.getStacks()[0].getItem() == CoreInit.bigNoteBook && te.getStacks()[0].getTagCompound() != null && te.getStacks()[0].getTagCompound().hasKey("owner") && te.hasItemsInCrafting();
	}

	public class GuiButtonCopy extends GuiButton {

		public GuiButtonCopy(int buttonId, int x, int y) {
			super(buttonId, x, y, 12, 12, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, /** u */
						25 + i * 12, 219, /** u */
						this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public class GuiButtonResearchStart extends GuiButton {

		public GuiButtonResearchStart(int buttonId, int x, int y) {
			super(buttonId, x, y, 78, 12, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				FontRenderer fontrenderer = mc.fontRenderer;
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, /** u */
						0, 238, /** u */
						this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
				int j = 14737632;

				if (!this.enabled) {
					j = 10526880;
				} else if (this.hovered) {
					j = 0x00FA9A;
				}
				String s = te.completed ? I18n.format("tomsMod.completed") : I18n.format("tomsMod.research.name");
				this.drawCenteredString(fontrenderer, s, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
			}
		}

		/**
		 * Returns true if the mouse has been pressed on this control.
		 * Equivalent of MouseListener.mousePressed(MouseEvent e).
		 */
		@Override
		public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
			return !te.completed && super.mousePressed(mc, mouseX, mouseY);
		}
	}

	public class GuiButtonMenu extends GuiButton {

		public GuiButtonMenu(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, /** u */
						61 + i * 16, 219, /** u */
						this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public class GuiResearchSelection extends GuiScreen {
		private GuiResearchSelectionList list;
		private GuiButton buttonCancel;
		private GuiButton buttonDone;
		private final GuiResearchTable parent;

		public GuiResearchSelection(GuiResearchTable parent) {
			this.parent = parent;
		}

		@Override
		public void initGui() {
			super.initGui();
			this.list = new GuiResearchSelectionList(Minecraft.getMinecraft(), this);
			this.buttonDone = new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("gui.done"));
			this.buttonCancel = new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.cancel"));
			this.buttonList.add(buttonCancel);
			this.buttonList.add(buttonDone);
		}

		@Override
		public void drawScreen(int mouseXIn, int mouseYIn, float ticks) {
			if (list != null)
				this.list.drawScreen(mouseXIn, mouseYIn, ticks);
			super.drawScreen(mouseXIn, mouseYIn, ticks);
		}

		@Override
		protected void actionPerformed(GuiButton button) throws IOException {
			if (button.id == 200) {
				if (list.selected != -1) {
					IGuiListEntry e = list.getListEntry(list.selected);
					Research res = null;
					if (e instanceof ResearchEntry) {
						res = ((ResearchEntry) e).button.research;
					}
					sendButtonUpdate(5, te, ResearchHandler.getId(res));
				}
				this.mc.displayGuiScreen(this.parent);
			} else if (button.id == 201) {
				this.mc.displayGuiScreen(this.parent);
			}
		}

		public class GuiResearchSelectionList extends GuiListExtended {
			private List<IGuiListEntry> researchList;
			private final ResearchHandler handler;
			private List<Runnable> hoverRender = new ArrayList<>();
			private int selected = -1;
			private boolean showCompleted = true;
			private List<ResearchEntry> compRes;

			public GuiResearchSelectionList(Minecraft mcIn, GuiResearchSelection parent) {
				super(mcIn, parent.width, parent.height, 63, parent.height - 32, 24);
				this.researchList = new ArrayList<>();
				this.handler = ResearchHandler.fromNBT(tag, "");
				List<ResearchInformation> list = this.handler.getAvailableResearches();
				this.researchList.clear();
				this.researchList.add(new ResearchHeader(ResearchComplexity.BASIC));
				List<ResearchEntry> defRes = new ArrayList<>();
				List<ResearchEntry> tooCompRes = new ArrayList<>();
				List<ResearchEntry> labRes = new ArrayList<>();
				compRes = new ArrayList<>();
				List<Research> completed = this.handler.getResearchesCompleted();
				for (ResearchInformation r : list) {
					switch (r.getResearch().getComplexity()) {
					case LABORATORY:
						labRes.add(new ResearchEntry(r, false));
						break;
					default:
						defRes.add(new ResearchEntry(r, false));
						break;
					}
				}
				for (Research r : completed) {
					compRes.add(new ResearchEntry(new ResearchInformation(r), true));
				}
				Collections.sort(defRes, sorter);
				Collections.sort(tooCompRes, sorter);
				Collections.sort(labRes, sorter);
				Collections.sort(compRes, sorter);
				researchList.addAll(defRes);
				researchList.addAll(tooCompRes);
				researchList.add(new ResearchHeader(ResearchComplexity.LABORATORY));
				researchList.addAll(labRes);
				researchList.add(new ResearchHeaderCompleted());
				researchList.addAll(compRes);
			}

			@Override
			public IGuiListEntry getListEntry(int index) {
				return this.researchList.get(index);
			}

			@Override
			protected int getSize() {
				return this.researchList.size();
			}

			private boolean showCompletedLast = true;

			@Override
			public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
				if (this.visible) {
					if (showCompletedLast && !showCompleted) {
						List<ResearchEntry> toRemove = new ArrayList<>();
						for (IGuiListEntry r : researchList) {
							if (r instanceof ResearchEntry) {
								ResearchEntry re = (ResearchEntry) r;
								if (re.completed) {
									toRemove.add(re);
								}
							}
						}
						researchList.removeAll(toRemove);
					} else if (!showCompletedLast && showCompleted) {
						researchList.addAll(compRes);
					}
					showCompletedLast = showCompleted;
					this.mouseX = mouseXIn;
					this.mouseY = mouseYIn;
					this.drawBackground();
					int i = this.getScrollBarX();
					int j = i + 6;
					this.bindAmountScrolled();
					GlStateManager.disableLighting();
					GlStateManager.disableFog();
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder worldrenderer = tessellator.getBuffer();
					// Forge: background rendering moved into separate method.
					this.drawContainerBackground(tessellator);
					int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
					int l = this.top + 4 - (int) this.amountScrolled;

					if (this.hasListHeader) {
						this.drawListHeader(k, l, tessellator);
					}

					this.drawSelectionBox(k, l, mouseXIn, mouseYIn, partialTicks);
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.disableDepth();
					int i1 = 4;
					this.overlayBackground(0, this.top, 255, 255);
					this.overlayBackground(this.bottom, this.height, 255, 255);
					GlStateManager.enableBlend();
					GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
					GlStateManager.disableAlpha();
					GlStateManager.shadeModel(7425);
					renderHover();
					GlStateManager.disableTexture2D();
					worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
					worldrenderer.pos(this.left, this.top + i1, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
					worldrenderer.pos(this.right, this.top + i1, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();
					worldrenderer.pos(this.right, this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
					worldrenderer.pos(this.left, this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
					tessellator.draw();
					worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
					worldrenderer.pos(this.left, this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
					worldrenderer.pos(this.right, this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
					worldrenderer.pos(this.right, this.bottom - i1, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
					worldrenderer.pos(this.left, this.bottom - i1, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();
					tessellator.draw();
					int j1 = this.getMaxScroll();
					if (j1 > 0) {
						int h = this.getContentHeight();
						int k1 = (this.bottom - this.top) * (this.bottom - this.top) / (h == 0 ? 1 : h);
						k1 = MathHelper.clamp(k1, 32, this.bottom - this.top - 8);
						int l1 = (int) this.amountScrolled * (this.bottom - this.top - k1) / j1 + this.top;

						if (l1 < this.top) {
							l1 = this.top;
						}

						worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
						worldrenderer.pos(i, this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
						worldrenderer.pos(j, this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
						worldrenderer.pos(j, this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
						worldrenderer.pos(i, this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
						tessellator.draw();
						worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
						worldrenderer.pos(i, l1 + k1, 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
						worldrenderer.pos(j, l1 + k1, 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
						worldrenderer.pos(j, l1, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
						worldrenderer.pos(i, l1, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
						tessellator.draw();
						worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
						worldrenderer.pos(i, l1 + k1 - 1, 0.0D).tex(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
						worldrenderer.pos(j - 1, l1 + k1 - 1, 0.0D).tex(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
						worldrenderer.pos(j - 1, l1, 0.0D).tex(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
						worldrenderer.pos(i, l1, 0.0D).tex(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
						tessellator.draw();
					}

					this.renderDecorations(mouseXIn, mouseYIn);
					GlStateManager.enableTexture2D();
					GlStateManager.shadeModel(7424);
					GlStateManager.enableAlpha();
					GlStateManager.disableBlend();
				}
			}

			private void renderHover() {
				for (int n = 0;n < hoverRender.size();n++) {
					hoverRender.get(n).run();
				}
				hoverRender.clear();
				RenderHelper.disableStandardItemLighting();
			}

			@SideOnly(Side.CLIENT)
			public class ResearchEntry implements IGuiListEntry {
				private GuiResearchSelectButton button;
				private final boolean completed;

				public ResearchEntry(ResearchInformation r, boolean completed) {
					this.button = new GuiResearchSelectButton(0, 0, 0, r);
					this.completed = completed;
				}

				@Override
				public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {

				}

				@Override
				public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
					this.button.x = x + listWidth / 7 + 1;
					this.button.y = y;
					this.button.drawButton(mc, mouseX, mouseY, partialTicks);
				}

				@Override
				public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
					boolean pressed = this.button.mousePressed(mc, p_148278_2_, p_148278_3_);
					if (pressed) {
						this.button.playPressSound(mc.getSoundHandler());
						if (slotIndex == selected) {
							selected = -1;
						} else {
							selected = slotIndex;
						}
						return true;
					}
					return false;
				}

				@Override
				public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
					this.button.mouseReleased(x, y);
				}

				public class GuiResearchSelectButton extends GuiButton {
					private final Research research;
					private final List<IScanningInformation> missing;

					public GuiResearchSelectButton(int buttonId, int x, int y, ResearchInformation r) {
						super(buttonId, x, y, 175, 24, "");
						this.research = r.getResearch();
						this.enabled = r.available;
						this.missing = r.missing;
					}

					/**
					 * Draws this button to the screen.
					 */
					@Override
					public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, float pt) {
						if (this.visible) {
							FontRenderer fontrenderer = mc.fontRenderer;
							mc.getTextureManager().bindTexture(LIST_TEXTURE);
							GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
							this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
							int i = this.getHoverState(this.hovered);
							GlStateManager.enableBlend();
							GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
							GlStateManager.blendFunc(770, 771);
							this.drawTexturedModalRect(this.x, this.y, /** u */
									0, 0 + i * 24 + (selected == researchList.indexOf(ResearchEntry.this) ? 48 : 0), /** u */
									this.width, this.height);
							this.mouseDragged(mc, mouseX, mouseY);
							int j = 14737632;
							ItemStack icon = research.getIcon();
							if (icon == null)
								icon = new ItemStack(Blocks.BARRIER);
							GL11.glPushMatrix();
							RenderHelper.enableGUIStandardItemLighting();
							renderItemInGui(icon, this.x + 4, this.y + 4, -50, -50);
							GL11.glPopMatrix();
							if (!this.enabled) {
								j = 10526880;
							} else if (this.hovered) {
								j = 16777120;
							}

							this.drawCenteredString(fontrenderer, I18n.format(research.getUnlocalizedName()), this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
							if (this.hovered && (!this.enabled)) {
								hoverRender.add(() -> {
									List<String> textLines = new ArrayList<>();
									textLines.add(TextFormatting.RED + I18n.format("tomsmod.gui.missing") + ":");
									for (int k = 0;k < missing.size();k++) {
										IScanningInformation info = missing.get(k);
										String name = I18n.format(info.getUnlocalizedName());
										info.addTooltip(textLines, name, isShiftKeyDown(), mc.gameSettings.advancedItemTooltips);
									}
									drawHoveringText(textLines, mouseX, mouseY);
								});
							}
						}
					}
				}
			}

			@Override
			protected int getScrollBarX() {
				return super.getScrollBarX() + 15;
			}

			/**
			 * Gets the width of the list
			 */
			@Override
			public int getListWidth() {
				return super.getListWidth() + 32;
			}

			@Override
			protected void drawBackground() {

			}

			@SideOnly(Side.CLIENT)
			public class ResearchHeader implements IGuiListEntry {
				private final String name;
				private final int width;

				public ResearchHeader(ResearchComplexity c) {
					this.name = I18n.format(c.toString());
					this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(name);
				}

				@Override
				public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
					return false;
				}

				@Override
				public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

				}

				@Override
				public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {

				}

				@Override
				public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
					mc.fontRenderer.drawString(this.name, mc.currentScreen.width / 2 - this.width / 2, y + slotHeight - mc.fontRenderer.FONT_HEIGHT - 1, 16777215);
				}

			}

			@SideOnly(Side.CLIENT)
			public class ResearchHeaderCompleted implements IGuiListEntry {
				private GuiListButton button = new GuiListButton(0, 0, 0);

				@Override
				public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {

				}

				@Override
				public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
					this.button.x = x + listWidth / 7 + 1;
					this.button.y = y;
					this.button.drawButton(mc, mouseX, mouseY, partialTicks);
				}

				@Override
				public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
					boolean pressed = this.button.mousePressed(mc, p_148278_2_, p_148278_3_);
					if (pressed) {
						this.button.playPressSound(mc.getSoundHandler());
						showCompleted = !showCompleted;
						return true;
					}
					return false;
				}

				@Override
				public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
					this.button.mouseReleased(x, y);
				}

				public class GuiListButton extends GuiButton {
					public GuiListButton(int buttonId, int x, int y) {
						super(buttonId, x, y, 175, 24, "");
					}

					/**
					 * Draws this button to the screen.
					 */
					@Override
					public void drawButton(Minecraft mc, final int mouseX, final int mouseY, float pt) {
						if (this.visible) {
							FontRenderer fontrenderer = mc.fontRenderer;
							mc.getTextureManager().bindTexture(LIST_TEXTURE);
							GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
							this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
							int i = this.getHoverState(this.hovered);
							GlStateManager.enableBlend();
							GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
							GlStateManager.blendFunc(770, 771);
							this.drawTexturedModalRect(this.x, this.y, /** u */
									0, 0 + i * 24, /** u */
									this.width, this.height);
							this.mouseDragged(mc, mouseX, mouseY);
							int j = 14737632;
							if (!this.enabled) {
								j = 10526880;
							} else if (this.hovered) {
								j = 16777120;
							}

							this.drawCenteredString(fontrenderer, I18n.format("tomsMod.completed"), this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
						}
					}
				}
			}
		}

		/**
		 * Called when a mouse button is released. Args : mouseX, mouseY,
		 * releaseButton
		 */
		@Override
		protected void mouseReleased(int mouseX, int mouseY, int state) {
			if (state != 0 || !this.list.mouseReleased(mouseX, mouseY, state)) {
				super.mouseReleased(mouseX, mouseY, state);
			}
		}

		/**
		 * Handles mouse input.
		 */
		@Override
		public void handleMouseInput() throws IOException {
			if (list != null)
				super.handleMouseInput();
			if (list != null)
				this.list.handleMouseInput();
		}

		/**
		 * Called when the mouse is clicked. Args : mouseX, mouseY,
		 * clickedButton
		 */
		@Override
		protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
			if (!this.list.mouseClicked(mouseX, mouseY, mouseButton)) {
				super.mouseClicked(mouseX, mouseY, mouseButton);
			}
		}

		@Override
		public boolean doesGuiPauseGame() {
			return false;
		}

		@Override
		public void updateScreen() {
			buttonDone.enabled = list.selected != -1;
		}
	}

	public class GuiButtonCraft extends GuiButton {

		public GuiButtonCraft(int buttonId, int x, int y) {
			super(buttonId, x, y, 20, 7, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				int i = this.getHoverState(this.hovered);
				int x, y;
				if (te.craftAll) {
					mc.getTextureManager().bindTexture(LIST_TEXTURE);
					x = 78;
					y = 175;
					if (hovered)
						i = 2;
					else
						i = 1;
				} else {
					mc.getTextureManager().bindTexture(gui);
					x = 109;
					y = 219;
				}
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, /** u */
						x, y + i * 7, /** u */
						this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}

		/**
		 * Returns true if the mouse has been pressed on this control.
		 * Equivalent of MouseListener.mousePressed(MouseEvent e).
		 */
		@Override
		public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
			return (this.enabled || te.craftAll) && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		}
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		tag = message;
		this.mc.displayGuiScreen(new GuiResearchSelection(this));
	}

	public static class ResearchEntrySorter implements Comparator<ResearchEntry> {

		@Override
		public int compare(ResearchEntry o1, ResearchEntry o2) {
			return I18n.format(o1.button.research.getUnlocalizedName()).compareTo(I18n.format(o2.button.research.getUnlocalizedName()));
		}

	}
}
