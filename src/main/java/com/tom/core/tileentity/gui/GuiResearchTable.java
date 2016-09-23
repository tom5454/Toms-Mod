package com.tom.core.tileentity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import mapwriterTm.util.Render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
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

import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.research.IScanningInformation;
import com.tom.api.research.Research;
import com.tom.api.research.ResearchComplexity;
import com.tom.apis.TomsModUtils;
import com.tom.apis.TomsModUtils.GuiRenderRunnable;
import com.tom.core.CoreInit;
import com.tom.core.research.ResearchHandler;
import com.tom.core.research.ResearchHandler.ResearchInformation;

import com.tom.core.tileentity.TileEntityResearchTable;
import com.tom.core.tileentity.gui.GuiResearchTable.GuiResearchSelection.GuiResearchSelectionList.ResearchEntry;
import com.tom.core.tileentity.inventory.ContainerResearchTable;

public class GuiResearchTable extends GuiTomsMod implements INBTPacketReceiver{
	private GuiButtonCopy copyButton;
	private GuiButtonResearchStart researchButton;
	private GuiButtonMenu menuButton;
	private GuiButtonCraft craftButton;
	private final TileEntityResearchTable te;
	private static final Comparator<ResearchEntry> sorter = new ResearchEntrySorter();
	//private static final ResourceLocation LIST_TEXTURE = new ResourceLocation("tomsmod:textures/gui/resSelect.png");
	private NBTTagCompound tag = new NBTTagCompound();
	public GuiResearchTable(InventoryPlayer playerInv, TileEntityResearchTable te) {
		super(new ContainerResearchTable(playerInv, te), "restableGui");
		this.xSize = 221;
		this.ySize = 219;
		this.te = te;
	}
	@Override
	public void drawGuiContainerForegroundLayer(int mX, int mY){
		//String s = I18n.format("tomsmod.gui.resTable"); GuiFurnace
		//fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		//fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
		/*if(te.currentResearch != null){
			ItemStack is = te.currentResearch.getResearchRequirements().get(0);
			//this.drawHoveringText(is.getTooltip(mc.thePlayer,this.mc.gameSettings.advancedItemTooltips), mX, my);
			//this.renderToolTip(is, mX, my);
			this.renderItemInGui(is, guiLeft, guiTop, mX, mY,1);
		}*/
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick,
			int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		//int i = (this.width - this.xSize) / 2;
		//int j = (this.height - this.ySize) / 2;
		int craftingTime = te.getField(1);
		int totalCraftingTime = te.getField(2);
		int researchTime = te.getField(3);
		int totalResearchTime = te.getField(4);
		float totalCraftingTimeF = totalCraftingTime < 1 ? 1 : totalCraftingTime;
		float totalResearchTimeF = totalResearchTime < 1 ? 1 : totalResearchTime;
		float perCraftingTime = craftingTime / totalCraftingTimeF;
		float perResearchTime = researchTime / totalResearchTimeF;
		//System.out.println(perCraftingTime + " " + perResearchTime + " " + totalCraftingTime + " " + totalResearchTime + " " + craftingTime);
		double p1Per = perResearchTime * 100;
		double p3Per = perCraftingTime * 100;
		double p = p1Per / 100D * 55;//max:55
		this.drawTexturedModalRect(guiLeft + 112, guiTop + 45,0,235,p,3);
		int p2Per = te.getField(0);
		if(p2Per > -1){
			double p2 = p2Per / 100D * 16;//max:16
			p2 = p2 == 0 && p2Per != 0 ? 1 : p2;
			this.drawTexturedModalRect(guiLeft + 25, guiTop + 6 + 16-p2, 0,234-p2, 2, p2);
		}else{
			Render.setColourWithAlphaPercent(0xFF0000,70);
			Render.drawRect(guiLeft + 25, guiTop + 6, 2, 16);
			GlStateManager.color(1, 1, 1, 1);
		}
		double p3 = p3Per / 100D * 23;//max:23
		this.drawTexturedModalRect(guiLeft + 167, guiTop + 71,2,219,p3,15);
		if(te.currentResearch != null){
			mc.renderEngine.bindTexture(LIST_TEXTURE);
			this.drawTexturedModalRect(guiLeft + 30, guiTop + 6,0,120,78,100);
			//ResourceLocation l = this.te.currentResearch.getIcon();
			//if(!l.toString().contains(".png"))l = new ResourceLocation(l.toString()+".png");
			//mc.getTextureManager().bindTexture(l);
			//this.drawTexturedModalRect(this.xPosition+4, this.yPosition+4, 0, 0, 16, 16);
			//GL11.glScalef(1.5F, 1.5F, 1.5F);
			//Render.drawTexturedRect(guiLeft+60, guiTop+15, 16, 16);
			ItemStack icon = te.currentResearch.getIcon();
			if(icon == null)icon = new ItemStack(Blocks.BARRIER);
			GL11.glPushMatrix();
			RenderHelper.enableGUIStandardItemLighting();
			renderItemInGui(icon, guiLeft+60, guiTop+15, -50, -50);
			GL11.glPopMatrix();
			String text = I18n.format(te.currentResearch.getUnlocalizedName());
			drawResearchName(guiLeft+68, guiTop+38, text);
			//float textScale = 0.9F;
			//GL11.glPushMatrix();
			//GL11.glScalef(textScale, textScale, textScale);
			//mc.fontRendererObj.drawString(text, guiLeft+78/textScale-textWidth/2/textScale, guiTop+38/textScale, 16777215,true);
			//GL11.glPopMatrix();
			//ItemStack is = te.currentResearch.getResearchRequirements().get(0);
			//GL11.glPushMatrix();
			//this.zLevel = 100.0F;
			//this.itemRender.zLevel = 100.0F;
			//GL11.glTranslated(mouseX, mouseY, this.zLevel);
			//this.itemRender.renderItem(is, this.itemRender.getItemModelMesher().getItemModel(is));
			//this.itemRender.renderItemIntoGUI(is, guiLeft + 50, guiTop + 71);
			//this.renderItemInGui(is, guiLeft + 50, guiTop + 71, mouseX, mouseY,0);

			//GL11.glPopMatrix();
			//this.itemRender.zLevel = 0.0F;
			//this.zLevel = 0.0F;
		}
	}
	private static final int LINE_SPACING = 2;
	private void drawResearchName(int x, int y, String textN){
		List<String> textList = mc.fontRendererObj.listFormattedStringToWidth(textN, 75);
		for(int i = 0;i<textList.size();i++){
			String text = textList.get(i);
			int textWidth = mc.fontRendererObj.getStringWidth(text);
			int tab = textWidth/2;
			mc.fontRendererObj.drawString(text, x-tab, y + (i * (mc.fontRendererObj.FONT_HEIGHT + LINE_SPACING)), 16777215,true);
		}
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		int id = button.id;
		if(id < 4){
			this.sendButtonUpdate(id, te);
		}
	}
	@Override
	public void initGui() {
		labelList.clear();
		super.initGui();
		copyButton = new GuiButtonCopy(0, guiLeft + 8, guiTop + 64);
		buttonList.add(copyButton);
		researchButton = new GuiButtonResearchStart(1, guiLeft + 30, guiTop + 108,I18n.format("tomsMod.research.name"));
		buttonList.add(researchButton);
		menuButton = new GuiButtonMenu(2, guiLeft + 6, guiTop + 104);
		buttonList.add(menuButton);
		craftButton = new GuiButtonCraft(3, guiLeft + 168, guiTop + 60);
		buttonList.add(craftButton);
		TomsModUtils.addRunnableToLabelList(new GuiRenderRunnable() {

			@Override
			public void run(int mouseX, int mouseY) {
				RenderHelper.disableStandardItemLighting();
				if(te.getField(4) < 1){
					boolean flag = te.currentResearch != null;
					if(flag){
						List<ItemStack> stackList = te.currentResearch.getResearchRequirements();
						//this.drawHoveringText(is.getTooltip(mc.thePlayer,this.mc.gameSettings.advancedItemTooltips), mX, my);
						//this.renderToolTip(is, mouseX, mouseY);
						if(stackList != null){
							ItemStack[] stackA = te.getStacks();
							List<ItemStack> inStacks = new ArrayList<ItemStack>();
							for(int i = 3;i<7;i++){
								ItemStack stack = stackA[i];
								if(stack != null){
									inStacks.add(stack);
								}
							}
							List<Runnable> hover = new ArrayList<Runnable>();
							for(int i = 0;i<stackList.size();i++){
								ItemStack stack = stackList.get(i);
								if(stack != null){
									boolean flag1 = true;
									for(ItemStack inStack : inStacks){
										boolean equals = inStack.getItem() == stack.getItem() && inStack.stackSize >= stack.stackSize && inStack.getItemDamage() == stack.getItemDamage();
										if(equals){
											flag1 = false;
											break;
										}
									}
									hover.add(renderItemInGuiWithRunnableHover(stack, guiLeft + 50+(((i+1) % 2)*18), guiTop + 65+(i/2*18), mouseX, mouseY,flag1));
									if(flag1){
										flag = false;
									}
									if(i == 3)break;
								}
							}
							mc.fontRendererObj.drawString(I18n.format("tomsmod.gui.requirements")+":", guiLeft + 33, guiTop + 55, flag ? 0xFFFFFFFF : 0xFFFF0000);
							for(int i = 0;i<hover.size();i++){
								hover.get(i).run();
							}
						}
					}
					boolean hasPaper = te.getStacks()[17] != null && te.getStacks()[17].getItem() == Items.PAPER;
					boolean hasInk = te.getField(0) > 0;
					researchButton.enabled = flag && hasPaper && hasInk;
					if(te.currentResearch != null){
						if(!hasPaper){
							Render.setColourWithAlphaPercent(0xFF0000,30);
							Render.drawRect(guiLeft+6, guiTop+24, 16, 16);
						}
						if(!hasInk){
							Render.setColourWithAlphaPercent(0xFF0000,30);
							Render.drawRect(guiLeft+6, guiTop+6, 16, 16);
						}
					}
				}else{
					researchButton.enabled = false;
					mc.fontRendererObj.drawString(I18n.format("tomsmod.gui.running"), guiLeft + 33, guiTop + 55, 0xFFFFFFFF);
					boolean hasInk = te.getField(0) > 0;
					if(!hasInk){
						Render.setColourWithAlphaPercent(0xFF0000,30);
						Render.drawRect(guiLeft+6, guiTop+6, 16, 16);
					}
				}
				if(te.craftingError > 0){
					mc.fontRendererObj.drawString("!", guiLeft + 200, guiTop + 60, 0xFFFF0000);
					if(isPointInRegion(200, 60, 10, 10, mouseX, mouseY)){
						drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.craftingError_"+te.craftingError)), mouseX, mouseY);
					}
				}
			}
		}, labelList);
	}
	@Override
	public void updateScreen() {
		super.updateScreen();
		this.copyButton.enabled = te.getStacks()[0] != null && te.getStacks()[0].getItem() == CoreInit.bigNoteBook && te.getStacks()[0].getTagCompound() != null && te.getStacks()[0].getTagCompound().hasKey("owner") && te.getStacks()[1] != null && te.getStacks()[1].getItem() == CoreInit.noteBook;
		//this.researchButton.enabled = te.currentResearch != null;
		this.menuButton.enabled = te.getStacks()[0] != null && te.getStacks()[0].getItem() == CoreInit.bigNoteBook && te.getStacks()[0].getTagCompound() != null && te.getStacks()[0].getTagCompound().hasKey("owner") && te.getField(4) < 1;
		this.craftButton.enabled = te.getField(2) < 1 && te.getStacks()[0] != null && te.getStacks()[0].getItem() == CoreInit.bigNoteBook && te.getStacks()[0].getTagCompound() != null && te.getStacks()[0].getTagCompound().hasKey("owner") && te.hasItemsInCrafting();
	}
	public class GuiButtonCopy extends GuiButton{

		public GuiButtonCopy(int buttonId, int x, int y) {
			super(buttonId, x, y,12,12, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				//FontRenderer fontrenderer = mc.fontRendererObj;
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition,/**u*/ 25 + i * 12, 219,/**u*/ this.width, this.height);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, /**u*/200 - this.width / 2, 46 + i * 20,/**u*/ this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
				/*int j = 14737632;

	            if (packedFGColour != 0)
	            {
	                j = packedFGColour;
	            }
	            else
	            if (!this.enabled)
	            {
	                j = 10526880;
	            }
	            else if (this.hovered)
	            {
	                j = 16777120;
	            }*/

				//this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
			}
		}
	}
	public class GuiButtonResearchStart extends GuiButton{

		public GuiButtonResearchStart(int buttonId, int x, int y, String buttonText) {
			super(buttonId, x, y,78,12, buttonText);
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				FontRenderer fontrenderer = mc.fontRendererObj;
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				if(i != 0)this.drawTexturedModalRect(this.xPosition, this.yPosition,/**u*/ 0, 238,/**u*/ this.width, this.height);
				//if(i != 0)this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition,/**u*/ 0, 0,/**u*/ this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
				int j = 14737632;

				/*if (packedFGColour != 0)
	            {
	                j = packedFGColour;
	            }
	            else*/
				if (!this.enabled)
				{
					j = 10526880;
				}
				else if (this.hovered)
				{
					j = 0x00FA9A;
				}

				if(i!=0)this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
			}
		}
	}
	public class GuiButtonMenu extends GuiButton{

		public GuiButtonMenu(int buttonId, int x, int y) {
			super(buttonId, x, y,16,16, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				//FontRenderer fontrenderer = mc.fontRendererObj;
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition,/**u*/ 61 + i * 16, 219,/**u*/ this.width, this.height);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, /**u*/200 - this.width / 2, 46 + i * 20,/**u*/ this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
				/*int j = 14737632;

	            if (packedFGColour != 0)
	            {
	                j = packedFGColour;
	            }
	            else
	            if (!this.enabled)
	            {
	                j = 10526880;
	            }
	            else if (this.hovered)
	            {
	                j = 16777120;
	            }*/

				//this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
			}
		}
	}
	public class GuiResearchSelection extends GuiScreen{
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
			//sendButtonUpdate(6, te);
			this.list = new GuiResearchSelectionList(Minecraft.getMinecraft(), this);
			this.buttonDone = new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("gui.done"));
			this.buttonCancel = new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.cancel"));
			this.buttonList.add(buttonCancel);
			this.buttonList.add(buttonDone);
		}
		@Override
		public void drawScreen(int mouseXIn, int mouseYIn, float ticks) {
			if(list != null)this.list.drawScreen(mouseXIn, mouseYIn, ticks);
			super.drawScreen(mouseXIn, mouseYIn, ticks);
		}
		@Override
		protected void actionPerformed(GuiButton button) throws IOException {
			if(button.id == 200){
				if(list.selected != -1){
					IGuiListEntry e = list.getListEntry(list.selected);
					Research res = null;
					if(e instanceof ResearchEntry){
						res = ((ResearchEntry)e).button.research;
					}
					sendButtonUpdate(5, te, ResearchHandler.getId(res));
				}
				this.mc.displayGuiScreen(this.parent);
			}else if(button.id == 201){
				this.mc.displayGuiScreen(this.parent);
			}
		}
		public class GuiResearchSelectionList extends GuiListExtended{
			private List<IGuiListEntry> researchList;
			private final ResearchHandler handler;
			private List<Runnable> hoverRender = new ArrayList<Runnable>();
			//private final GuiResearchSelection parent;
			private int selected = -1;
			public GuiResearchSelectionList(Minecraft mcIn, GuiResearchSelection parent) {
				super(mcIn, parent.width, parent.height, 63, parent.height-32, 24);
				//this.parent = parent;
				this.researchList = new ArrayList<IGuiListEntry>();
				this.handler = ResearchHandler.fromNBT(tag, "");
				List<ResearchInformation> list = this.handler.getAvailableResearches();
				this.researchList.clear();
				this.researchList.add(new ResearchHeader(ResearchComplexity.BASIC));
				List<ResearchEntry> basRes = new ArrayList<ResearchEntry>();
				List<ResearchEntry> advRes = new ArrayList<ResearchEntry>();
				List<ResearchEntry> labRes = new ArrayList<ResearchEntry>();
				for(ResearchInformation r : list){
					switch(r.getResearch().getComplexity()){
					case ADVANCED:
						advRes.add(new ResearchEntry(r));
						break;
					case BASIC:
						basRes.add(new ResearchEntry(r));
						break;
					case LABORATORY:
						labRes.add(new ResearchEntry(r));
						break;
					default:
						break;
					}
				}
				Collections.sort(basRes, sorter);
				Collections.sort(advRes, sorter);
				Collections.sort(labRes, sorter);
				for(ResearchEntry e : basRes){
					this.researchList.add(e);
				}
				this.researchList.add(new ResearchHeader(ResearchComplexity.ADVANCED));
				for(ResearchEntry e : advRes){
					this.researchList.add(e);
				}
				this.researchList.add(new ResearchHeader(ResearchComplexity.LABORATORY));
				for(ResearchEntry e : labRes){
					this.researchList.add(e);
				}
			}
			@Override
			public IGuiListEntry getListEntry(int index) {
				return this.researchList.get(index);
			}

			@Override
			protected int getSize() {
				return this.researchList.size();
			}
			@Override
			public void drawScreen(int mouseXIn, int mouseYIn, float p_148128_3_)
			{
				if (this.visible)
				{
					this.mouseX = mouseXIn;
					this.mouseY = mouseYIn;
					this.drawBackground();
					int i = this.getScrollBarX();
					int j = i + 6;
					this.bindAmountScrolled();
					GlStateManager.disableLighting();
					GlStateManager.disableFog();
					Tessellator tessellator = Tessellator.getInstance();
					VertexBuffer worldrenderer = tessellator.getBuffer();
					// Forge: background rendering moved into separate method.
					this.drawContainerBackground(tessellator);
					int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
					int l = this.top + 4 - (int)this.amountScrolled;

					if (this.hasListHeader)
					{
						this.drawListHeader(k, l, tessellator);
					}

					this.drawSelectionBox(k, l, mouseXIn, mouseYIn);
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
					if (j1 > 0)
					{
						int h = this.getContentHeight();
						int k1 = (this.bottom - this.top) * (this.bottom - this.top) / (h == 0 ? 1 : h);
						k1 = MathHelper.clamp_int(k1, 32, this.bottom - this.top - 8);
						int l1 = (int)this.amountScrolled * (this.bottom - this.top - k1) / j1 + this.top;

						if (l1 < this.top)
						{
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
			private void renderHover(){
				for(int n = 0;n<hoverRender.size();n++){
					hoverRender.get(n).run();
				}
				hoverRender.clear();
				RenderHelper.disableStandardItemLighting();
			}
			@SideOnly(Side.CLIENT)
			public class ResearchEntry implements IGuiListEntry
			{
				//private final IResearch research;
				private GuiResearchSelectButton button;
				public ResearchEntry(ResearchInformation r) {
					//this.research = research;
					this.button = new GuiResearchSelectButton(0, 0, 0, r);
				}
				@Override
				public void setSelected(int id, int p_178011_2_,
						int p_178011_3_) {}

				@Override
				public void drawEntry(int slotIndex, int x, int y, int listWidth,
						int slotHeight, int mouseX, int mouseY, boolean isSelected) {
					this.button.xPosition = x + listWidth/7 + 1;
					this.button.yPosition = y;
					this.button.drawButton(mc, mouseX, mouseY);
					//new Throwable().printStackTrace();
				}

				@Override
				public boolean mousePressed(int slotIndex, int p_148278_2_,
						int p_148278_3_, int p_148278_4_, int p_148278_5_,
						int p_148278_6_) {
					boolean pressed = this.button.mousePressed(mc, p_148278_2_, p_148278_3_);
					//System.out.println(pressed);
					if(pressed){
						this.button.playPressSound(mc.getSoundHandler());
						if(slotIndex == selected){
							selected = -1;
						}else{
							selected = slotIndex;
						}
						return true;
					}
					return false;
				}

				@Override
				public void mouseReleased(int slotIndex, int x, int y,
						int mouseEvent, int relativeX, int relativeY) {
					this.button.mouseReleased(x, y);
				}
				public class GuiResearchSelectButton extends GuiButton{
					private final Research research;
					private final List<IScanningInformation> missing;
					public GuiResearchSelectButton(int buttonId, int x, int y, ResearchInformation r) {
						super(buttonId, x, y, 175,24,"");
						this.research = r.getResearch();
						this.enabled = r.available;
						this.missing = r.missing;
					}

					/**
					 * Draws this button to the screen.
					 */
					@Override
					public void drawButton(Minecraft mc, final int mouseX, final int mouseY)
					{
						if (this.visible)
						{
							FontRenderer fontrenderer = mc.fontRendererObj;
							mc.getTextureManager().bindTexture(LIST_TEXTURE);
							GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
							this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
							int i = this.getHoverState(this.hovered);
							GlStateManager.enableBlend();
							GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
							GlStateManager.blendFunc(770, 771);
							this.drawTexturedModalRect(this.xPosition, this.yPosition,/**u*/ 0, 0+ i * 24 + (selected == researchList.indexOf(ResearchEntry.this) ? 48 : 0),/**u*/ this.width, this.height);
							//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, /**u*/200 - this.width / 2, 46 + i * 20,/**u*/ this.width / 2, this.height);
							this.mouseDragged(mc, mouseX, mouseY);
							int j = 14737632;
							/*ResourceLocation l = this.research.getIcon();
							if(!l.toString().contains(".png"))l = new ResourceLocation(l.toString()+".png");
							mc.getTextureManager().bindTexture(l);
							//this.drawTexturedModalRect(this.xPosition+4, this.yPosition+4, 0, 0, 16, 16);
							Render.drawTexturedRect(this.xPosition+4, this.yPosition+4, 16, 16);*/
							ItemStack icon = research.getIcon();
							if(icon == null)icon = new ItemStack(Blocks.BARRIER);
							GL11.glPushMatrix();
							RenderHelper.enableGUIStandardItemLighting();
							renderItemInGui(icon, this.xPosition+4, this.yPosition+4, -50, -50);
							GL11.glPopMatrix();
							/*if (packedFGColour != 0)
				            {
				                j = packedFGColour;
				            }
				            else*/
							if (!this.enabled)
							{
								j = 10526880;
							}
							else if (this.hovered)
							{
								j = 16777120;
							}

							this.drawCenteredString(fontrenderer, I18n.format(research.getUnlocalizedName()), this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
							final Minecraft mcf = mc;
							if(this.hovered && (!this.enabled)){
								hoverRender.add(new Runnable(){

									@Override
									public void run() {
										List<String> textLines = new ArrayList<String>();
										textLines.add(TextFormatting.RED+I18n.format("tomsmod.gui.missing")+":");
										for(int k = 0;k<missing.size();k++){
											IScanningInformation info = missing.get(k);
											//IBlockState requiredState = info.getBlock().getStateFromMeta(info.getMeta());
											String name = I18n.format(info.getBlock().getUnlocalizedName()+".name");
											//@SuppressWarnings("rawtypes")
											//ImmutableMap<IProperty, Comparable> stateMap = requiredState.getProperties();
											if(mcf.gameSettings.advancedItemTooltips){
												if(info.getMeta() == -1)textLines.add("|  "+name + " " + TextFormatting.GRAY + info.getBlock().delegate.name().toString());
												else textLines.add("|  "+name+":"+info.getMeta() + " " + TextFormatting.GRAY + info.getBlock().delegate.name().toString());
											}else{
												if(info.getMeta() == -1)textLines.add("|  "+name);
												else textLines.add("|  "+name+":"+info.getMeta());
											}
											/*for(@SuppressWarnings("rawtypes") Entry<IProperty, Comparable> ent : stateMap.entrySet()){
						            			String stateName = ent.getKey().getName();
						            			String value = ent.getValue().toString();
						            			textLines.add("|    "+stateName+":"+value);
						            		}*/
										}
										drawHoveringText(textLines, mouseX, mouseY);
									}

								});
							}
						}
					}
				}
			}
			@Override
			protected int getScrollBarX()
			{
				return super.getScrollBarX() + 15;
			}

			/**
			 * Gets the width of the list
			 */
			@Override
			public int getListWidth()
			{
				return super.getListWidth() + 32;
			}
			@Override
			protected void drawBackground() {

			}
			@SideOnly(Side.CLIENT)
			public class ResearchHeader implements IGuiListEntry
			{
				private final String name;
				private final int width;
				public ResearchHeader(ResearchComplexity c) {
					this.name = I18n.format(c.toString());
					this.width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(name);
				}
				@Override
				public void setSelected(int p_178011_1_, int p_178011_2_,
						int p_178011_3_) {}

				@Override
				public void drawEntry(int slotIndex, int x, int y,
						int listWidth, int slotHeight, int mouseX, int mouseY,
						boolean isSelected) {
					mc.fontRendererObj.drawString(this.name, mc.currentScreen.width / 2 - this.width / 2, y + slotHeight - mc.fontRendererObj.FONT_HEIGHT - 1, 16777215);
				}

				@Override
				public boolean mousePressed(int slotIndex, int p_148278_2_,
						int p_148278_3_, int p_148278_4_, int p_148278_5_,
						int p_148278_6_) {
					return false;
				}

				@Override
				public void mouseReleased(int slotIndex, int x, int y,
						int mouseEvent, int relativeX, int relativeY) {

				}

			}
		}
		/**
		 * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
		 */
		@Override
		protected void mouseReleased(int mouseX, int mouseY, int state)
		{
			if (state != 0 || !this.list.mouseReleased(mouseX, mouseY, state))
			{
				super.mouseReleased(mouseX, mouseY, state);
			}
		}
		/**
		 * Handles mouse input.
		 */
		@Override
		public void handleMouseInput() throws IOException
		{
			if(list != null)super.handleMouseInput();
			if(list != null)this.list.handleMouseInput();
		}
		/**
		 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
		 */
		@Override
		protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
		{
			if (!this.list.mouseClicked(mouseX, mouseY, mouseButton))
			{
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
	public class GuiButtonCraft extends GuiButton{

		public GuiButtonCraft(int buttonId, int x, int y) {
			super(buttonId, x, y,20,7, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				//FontRenderer fontrenderer = mc.fontRendererObj;
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition,/**u*/ 109, 219+ i * 7,/**u*/ this.width, this.height);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, /**u*/200 - this.width / 2, 46 + i * 20,/**u*/ this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
				/*int j = 14737632;

	            if (packedFGColour != 0)
	            {
	                j = packedFGColour;
	            }
	            else
	            if (!this.enabled)
	            {
	                j = 10526880;
	            }
	            else if (this.hovered)
	            {
	                j = 16777120;
	            }*/

				//this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
			}
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
