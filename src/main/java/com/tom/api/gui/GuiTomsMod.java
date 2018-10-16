package com.tom.api.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotPhantom;
import com.tom.api.multipart.IGuiMultipart;
import com.tom.lib.network.LibNetworkHandler;
import com.tom.lib.utils.RenderUtil;
import com.tom.lib.utils.TomsUtils;
import com.tom.network.messages.MessageGuiButtonPress;

@SideOnly(Side.CLIENT)
public abstract class GuiTomsMod extends GuiContainer {
	protected ResourceLocation gui;
	public List<GuiFluidTank> tanks = new ArrayList<>();
	public static final ResourceLocation LIST_TEXTURE = new ResourceLocation("tomsmod:textures/gui/resSelect.png");

	public GuiTomsMod(Container inv, String guiTexture) {
		super(inv);
		this.gui = new ResourceLocation(createGuiLocation(guiTexture));
	}

	public static String createGuiLocation(String in) {
		return "tomsmod:textures/gui/" + in + ".png";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(gui);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}
	public final void sendButtonUpdate(int id, BlockPos pos) {
		LibNetworkHandler.sendToServer(new MessageGuiButtonPress(id, pos));
	}

	public final void sendButtonUpdate(int id, BlockPos pos, int extraData) {
		LibNetworkHandler.sendToServer(new MessageGuiButtonPress(id, pos, extraData));
	}

	public final void sendButtonUpdate(int id, TileEntity te) {
		LibNetworkHandler.sendToServer(new MessageGuiButtonPress(id, te));
	}

	public final void sendButtonUpdate(int id, TileEntity te, int extraData) {
		LibNetworkHandler.sendToServer(new MessageGuiButtonPress(id, te, extraData));
	}

	public final void sendButtonUpdateP(int id, IGuiMultipart te) {
		LibNetworkHandler.sendToServer(new MessageGuiButtonPress(id, te));
	}

	public final void sendButtonUpdateP(int id, IGuiMultipart te, int extraData) {
		LibNetworkHandler.sendToServer(new MessageGuiButtonPress(id, te, extraData));
	}

	public final void sendButtonUpdate(int id) {
		LibNetworkHandler.sendToServer(new MessageGuiButtonPress(id));
	}

	public final void sendButtonUpdate(int id, int extraData) {
		LibNetworkHandler.sendToServer(new MessageGuiButtonPress(id, extraData));
	}

	public final void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, boolean redBg) {
		renderItemInGui(stack, x, y, mouseX, mouseY, redBg, 1);
	}

	public final void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, boolean redBg, float size) {
		if (stack != null) {
			GL11.glPushMatrix();
			if (redBg) {
				RenderUtil.setColourWithAlphaPercent(0xFF0000, 50);
				RenderUtil.drawRect(x, y, 16, 16);
			}
			RenderHelper.enableGUIStandardItemLighting();
			this.zLevel = 200.0F;
			this.itemRender.zLevel = 200.0F;
			FontRenderer font = null;
			if (stack != null)
				font = stack.getItem().getFontRenderer(stack);
			if (font == null)
				font = fontRenderer;
			GlStateManager.translate(x, y, 0);
			if (size != 1)
				GlStateManager.scale(size, size, size);
			this.itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
			this.itemRender.renderItemOverlayIntoGUI(font, stack, 0, 0, null);
			this.zLevel = 0.0F;
			this.itemRender.zLevel = 0.0F;
			if (mouseX >= x && mouseY >= y && mouseX < x + 16 && mouseY < y + 16) {
				List<String> list = stack.getTooltip(mc.player, getTooltipFlag());
				list.add(I18n.format("tomsmod.gui.amount", stack.getCount()));
				for (int i = 0;i < list.size();++i) {
					if (i == 0) {
						list.set(i, stack.getRarity().rarityColor + list.get(i));
					} else {
						list.set(i, TextFormatting.GRAY + list.get(i));
					}
				}
				this.drawHoveringText(list, mouseX, mouseY);
			}
			RenderHelper.disableStandardItemLighting();
			GL11.glPopMatrix();
		}
	}

	public final void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, boolean hasBg, int color, boolean tooltip, String... extraInfo) {
		if (stack != null) {
			if (!tooltip) {
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
			} else if (mouseX >= x - 1 && mouseY >= y - 1 && mouseX < x + 17 && mouseY < y + 17) {
				List<String> list = stack.getTooltip(mc.player, getTooltipFlag());
				// list.add(I18n.format("tomsmod.gui.amount", stack.stackSize));
				if (extraInfo != null && extraInfo.length > 0) {
					list.addAll(TomsUtils.getStringList(extraInfo));
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

	public final void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY) {
		this.renderItemInGui(stack, x, y, mouseX, mouseY, false, 1);
	}

	public final void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, float size) {
		this.renderItemInGui(stack, x, y, mouseX, mouseY, false, size);
	}

	public void onTextfieldUpdate(int id) {

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		Slot slot = this.getSlotUnderMouse();
		if (slot instanceof SlotPhantom) {
			SlotPhantom s = (SlotPhantom) slot;
			this.handleMouseClick(s, s.slotNumber, mouseButton, isCtrlKeyDown() ? isShiftKeyDown() ? ClickType.QUICK_CRAFT : ClickType.CLONE : isShiftKeyDown() ? ClickType.PICKUP_ALL : ClickType.PICKUP);
		} else
			super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public final FontRenderer getFontRenderer(ItemStack stack) {
		FontRenderer font = null;
		if (stack != null)
			font = stack.getItem().getFontRenderer(stack);
		if (font == null)
			font = fontRenderer;
		return font;
	}

	protected final void drawInventoryText(int y) {
		fontRenderer.drawString(I18n.format("container.inventory"), 6, y, 4210752);
	}

	/**
	 * Draws a textured rectangle at the current z-value.
	 */
	public final void drawTexturedModalRect(double x, double y, double textureX, double textureY, double width, double height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(x + 0, y + height, this.zLevel).tex((textureX + 0) * f, (float) (textureY + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + height, this.zLevel).tex((float) (textureX + width) * f, (float) (textureY + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + 0, this.zLevel).tex((float) (textureX + width) * f, (textureY + 0) * f1).endVertex();
		vertexbuffer.pos(x + 0, y + 0, this.zLevel).tex((textureX + 0) * f, (textureY + 0) * f1).endVertex();
		tessellator.draw();
	}

	public final Runnable renderItemInGuiWithRunnableHover(final ItemStack stack, final int x, final int y, final int mouseX, final int mouseY, boolean redBg) {
		if (stack != null) {
			if (redBg) {
				RenderUtil.setColourWithAlphaPercent(0xFF0000, 50);
				RenderUtil.drawRect(x, y, 16, 16);
			}
			GlStateManager.translate(0.0F, 0.0F, 32.0F);
			RenderHelper.enableGUIStandardItemLighting();
			this.zLevel = 200.0F;
			this.itemRender.zLevel = 200.0F;
			FontRenderer font = null;
			if (stack != null)
				font = stack.getItem().getFontRenderer(stack);
			if (font == null)
				font = fontRenderer;
			this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
			this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, null);
			this.zLevel = 0.0F;
			this.itemRender.zLevel = 0.0F;
			RenderHelper.disableStandardItemLighting();
			return new Runnable() {

				@Override
				public void run() {
					if (mouseX >= x && mouseY >= y && mouseX < x + 16 && mouseY < y + 16) {
						List<String> list = stack.getTooltip(mc.player, getTooltipFlag());
						list.add(I18n.format("tomsmod.gui.amount", stack.getCount()));
						for (int i = 0;i < list.size();++i) {
							if (i == 0) {
								list.set(i, stack.getRarity().rarityColor + list.get(i));
							} else {
								list.set(i, TextFormatting.GRAY + list.get(i));
							}
						}
						drawHoveringText(list, mouseX, mouseY);
						RenderHelper.disableStandardItemLighting();
					}
				}
			};
		} else {
			return () -> {};
		}
	}

	public final void drawHoveringTextI(List<String> textLines, int x, int y) {
		drawHoveringText(textLines, x, y);
		RenderHelper.disableStandardItemLighting();
	}

	public final void drawHoveringTextI(String text, int x, int y) {
		drawHoveringTextI(Collections.<String>singletonList(text), x, y);
	}

	@Override
	public void initGui() {
		tanks.clear();
		super.initGui();
	}

	@Override
	public boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
		return super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
	}

	@Override
	public void drawHoveringText(List<String> textLines, int x, int y) {
		super.drawHoveringText(textLines, x, y);
	}

	public ResourceLocation getBackgroundTexture() {
		return gui;
	}

	public final void renderFluidTooltips(int mouseX, int mouseY) {
		tanks.forEach(v -> v.drawTooltip(mouseX, mouseY));
	}

	public FluidStack getFluidUnderMouse(int mouseX, int mouseY) {
		return tanks.stream().filter(v -> v.isUnderMouse(mouseX, mouseY)).findFirst().map(GuiFluidTank::getFluid).orElse(null);
	}
	/*@Override
	public void updateScreen() {
		super.updateScreen();
		if(inventorySlots.inventorySlots.stream().filter(p -> p.inventory instanceof TileEntity).map(s -> (TileEntity) s.inventory).anyMatch(TileEntity::isInvalid)){
			mc.player.closeScreen();
		}
	}*/
	public static ITooltipFlag getTooltipFlag(){
		return Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GL11.glColor4f(1, 1, 1, 1);
		this.drawDefaultBackground();
		GL11.glColor4f(1, 1, 1, 1);
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
