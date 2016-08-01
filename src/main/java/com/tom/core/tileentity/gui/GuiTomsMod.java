package com.tom.core.tileentity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mapwriterTm.util.Render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotPhantom;
import com.tom.api.multipart.IGuiMultipart;
import com.tom.apis.TomsModUtils;
import com.tom.core.client.IGuiWidget;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageGuiButtonPress;

@SideOnly(Side.CLIENT)
//@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = Configs.NEI)
public abstract class GuiTomsMod extends GuiContainer{
	protected final List<IGuiWidget> widgets = new ArrayList<IGuiWidget>();
	protected ResourceLocation gui;
	private static final int TEX_WIDTH = 16;
	private static final int TEX_HEIGHT = 16;
	private static final int MIN_FLUID_HEIGHT = 1;
	private static final int TANK_WIDTH = 20;
	private static final int TANK_HEIGHT = 55;
	protected static final ResourceLocation LIST_TEXTURE = new ResourceLocation("tomsmod:textures/gui/resSelect.png");
	public GuiTomsMod(Container inv, String guiTexture) {
		super(inv);
		this.gui = new ResourceLocation(createGuiLocation(guiTexture));
	}
	public static String createGuiLocation(String in){
		return "tomsmod:textures/gui/" + in + ".png";
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX,
			int mouseY) {
		mc.getTextureManager().bindTexture(gui);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}
	/*@Override
    @Optional.Method(modid = Configs.NEI)
    public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility){
        for(IGuiWidget w : widgets) {
            if(w instanceof IGuiAnimatedStat) {
                IGuiAnimatedStat stat = (IGuiAnimatedStat)w;
                if(stat.isLeftSided()) {
                    if(stat.getWidth() > 20) {
                        currentVisibility.showUtilityButtons = false;
                        currentVisibility.showStateButtons = false;
                    }
                } else {
                    if(stat.getAffectedY() < 10) {
                        currentVisibility.showWidgets = false;
                    }
                }
            }
        }
        return currentVisibility;
    }
	/**
	 * NEI will give the specified item to the InventoryRange returned if the player's inventory is full.
	 * return null for no range
	 */
	/*@Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item){
        return null;
    }

    /**
	 * @return A list of TaggedInventoryAreas that will be used with the savestates.
	 */
	/*@Override
    @Optional.Method(modid = Configs.NEI)
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui){
        return null;
    }

    /**
	 * Handles clicks while an itemstack has been dragged from the item panel. Use this to set configurable slots and the like.
	 * Changes made to the stackSize of the dragged stack will be kept
	 * @param gui The current gui instance
	 * @param mousex The x position of the mouse
	 * @param mousey The y position of the mouse
	 * @param draggedStack The stack being dragged from the item panel
	 * @param button The button presed
	 * @return True if the drag n drop was handled. False to resume processing through other routes. The held stack will be deleted if draggedStack.stackSize == 0
	 */
	/*@Override
    public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button){
        return false;
    }

    /**
	 * Used to prevent the item panel from drawing on top of other gui elements.
	 * @param x The x coordinate of the rectangle bounding the slot
	 * @param y The y coordinate of the rectangle bounding the slot
	 * @param w The w coordinate of the rectangle bounding the slot
	 * @param h The h coordinate of the rectangle bounding the slot
	 * @return true if the item panel slot within the specified rectangle should not be rendered.
	 */
	/*@Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h){
        for(IGuiWidget widget : widgets) {
            if(widget instanceof IGuiAnimatedStat) {
                IGuiAnimatedStat stat = (IGuiAnimatedStat)widget;
                if(stat.getBounds().intersects(new Rectangle(x, y, w, h))) return true;
            }
        }
        return false;
    }*/
	public final void sendButtonUpdate(int id, BlockPos pos){
		NetworkHandler.sendToServer(new MessageGuiButtonPress(id, pos));
	}
	public final void sendButtonUpdate(int id, BlockPos pos, int extraData){
		NetworkHandler.sendToServer(new MessageGuiButtonPress(id, pos, extraData));
	}
	public final void sendButtonUpdate(int id, TileEntity te){
		NetworkHandler.sendToServer(new MessageGuiButtonPress(id, te));
	}
	public final void sendButtonUpdate(int id, TileEntity te, int extraData){
		NetworkHandler.sendToServer(new MessageGuiButtonPress(id, te, extraData));
	}
	public final void sendButtonUpdate(int id, IGuiMultipart te){
		NetworkHandler.sendToServer(new MessageGuiButtonPress(id, te));
	}
	public final void sendButtonUpdate(int id, IGuiMultipart te, int extraData){
		NetworkHandler.sendToServer(new MessageGuiButtonPress(id, te, extraData));
	}
	public final void sendButtonUpdate(int id){
		NetworkHandler.sendToServer(new MessageGuiButtonPress(id));
	}
	public final void sendButtonUpdate(int id, int extraData){
		NetworkHandler.sendToServer(new MessageGuiButtonPress(id, extraData));
	}
	public final void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, boolean redBg){
		if(stack != null){
			if(redBg){
				Render.setColourWithAlphaPercent(0xFF0000,50);
				Render.drawRect(x, y, 16, 16);
			}
			GlStateManager.translate(0.0F, 0.0F, 32.0F);
			RenderHelper.enableGUIStandardItemLighting();
			this.zLevel = 200.0F;
			this.itemRender.zLevel = 200.0F;
			FontRenderer font = null;
			if (stack != null) font = stack.getItem().getFontRenderer(stack);
			if (font == null) font = fontRendererObj;
			this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
			this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, null);
			this.zLevel = 0.0F;
			this.itemRender.zLevel = 0.0F;
			if(mouseX >= x && mouseY >= y && mouseX < x + 16 && mouseY < y + 16){
				List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
				list.add(I18n.format("tomsmod.gui.amount", stack.stackSize));
				for (int i = 0; i < list.size(); ++i)
				{
					if (i == 0)
					{
						list.set(i, stack.getRarity().rarityColor + list.get(i));
					}
					else
					{
						list.set(i, TextFormatting.GRAY + list.get(i));
					}
				}
				this.drawHoveringText(list, mouseX, mouseY);
			}
			RenderHelper.disableStandardItemLighting();
		}
	}
	public final void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, boolean hasBg, int color, boolean tooltip, String... extraInfo){
		if(stack != null){
			if(!tooltip){
				if(hasBg){
					Render.setColourWithAlphaPercent(color,50);
					Render.drawRect(x, y, 16, 16);
				}
				GlStateManager.translate(0.0F, 0.0F, 32.0F);
				this.zLevel = 100.0F;
				this.itemRender.zLevel = 100.0F;
				FontRenderer font = null;
				if (stack != null) font = stack.getItem().getFontRenderer(stack);
				if (font == null) font = fontRendererObj;
				GlStateManager.enableDepth();
				this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
				this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, null);
				this.zLevel = 0.0F;
				this.itemRender.zLevel = 0.0F;
			}else
				if(mouseX >= x-1 && mouseY >= y-1 && mouseX < x + 17 && mouseY < y + 17){
					List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
					//list.add(I18n.format("tomsmod.gui.amount", stack.stackSize));
					if(extraInfo != null && extraInfo.length > 0){
						list.addAll(TomsModUtils.getStringList(extraInfo));
					}
					for (int i = 0; i < list.size(); ++i)
					{
						if (i == 0)
						{
							list.set(i, stack.getRarity().rarityColor + list.get(i));
						}
						else
						{
							list.set(i, TextFormatting.GRAY + list.get(i));
						}
					}
					this.drawHoveringText(list, mouseX, mouseY);
				}
		}
	}
	public final void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY){
		this.renderItemInGui(stack, x, y, mouseX, mouseY, false);
	}
	public void onTextfieldUpdate(int id) {

	}
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
			throws IOException {
		Slot slot = this.getSlotUnderMouse();
		if(slot instanceof SlotPhantom){
			SlotPhantom s = (SlotPhantom) slot;
			/*ItemStack draggedStack = this.mc.thePlayer.inventory.getItemStack();
			if(draggedStack != null){*/
			this.handleMouseClick(s, s.slotNumber, mouseButton, ClickType.PICKUP);
			//}
		}else
			super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	public final FontRenderer getFontRenderer(ItemStack stack){
		FontRenderer font = null;
		if (stack != null) font = stack.getItem().getFontRenderer(stack);
		if (font == null) font = fontRendererObj;
		return font;
	}
	public final int getGuiLeft(){
		return guiLeft;
	}
	public final int getGuiTop(){
		return guiTop;
	}
	private final void drawFluid(int xPosition, int yPosition, FluidStack fluidStack, int capacityMb) {
		if (fluidStack == null) {
			return;
		}
		Fluid fluid = fluidStack.getFluid();
		if (fluid == null) {
			return;
		}

		TextureMap textureMapBlocks = mc.getTextureMapBlocks();
		ResourceLocation fluidStill = fluid.getStill();
		TextureAtlasSprite fluidStillSprite = null;
		if (fluidStill != null) {
			fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
		}
		if (fluidStillSprite == null) {
			fluidStillSprite = textureMapBlocks.getMissingSprite();
		}

		int fluidColor = fluid.getColor(fluidStack);

		int scaledAmount = (fluidStack.amount * (TANK_HEIGHT - 8)) / capacityMb;
		if (fluidStack.amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
			scaledAmount = MIN_FLUID_HEIGHT;
		}
		if (scaledAmount > (TANK_HEIGHT - 8)) {
			scaledAmount = (TANK_HEIGHT - 8);
		}

		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		setGLColorFromInt(fluidColor);

		final int xTileCount = (TANK_WIDTH - 8) / TEX_WIDTH;
		final int xRemainder = (TANK_WIDTH - 8) - (xTileCount * TEX_WIDTH);
		final int yTileCount = scaledAmount / TEX_HEIGHT;
		final int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);

		final int yStart = yPosition + (TANK_HEIGHT - 8);

		for (int xTile = 0; xTile <= xTileCount; xTile++) {
			for (int yTile = 0; yTile <= yTileCount; yTile++) {
				int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
				int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
				int x = xPosition + (xTile * TEX_WIDTH);
				int y = yStart - ((yTile + 1) * TEX_HEIGHT);
				if (width > 0 && height > 0) {
					int maskTop = TEX_HEIGHT - height;
					int maskRight = TEX_WIDTH - width;

					drawFluidTexture(x, y, fluidStillSprite, maskTop, maskRight, 100);
				}
			}
		}
	}

	private static final void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;

		GlStateManager.color(red, green, blue, 1.0F);
	}

	private static final void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
		double uMin = textureSprite.getMinU();
		double uMax = textureSprite.getMaxU();
		double vMin = textureSprite.getMinV();
		double vMax = textureSprite.getMaxV();
		uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
		vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexBuffer.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
		vertexBuffer.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
		vertexBuffer.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
		vertexBuffer.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
		tessellator.draw();
	}

	public final void drawFluidTankTooltip(int posX, int posY, FluidTank tank, int mouseX, int mouseY) {
		if(isPointInRegion(posX, posY, TANK_WIDTH, TANK_HEIGHT, mouseX, mouseY)){
			List<String> tooltip = new ArrayList<String>();
			if (tank.getFluid() == null || tank.getFluid().getFluid() == null) {
				tooltip.add(I18n.format("tomsMod.chat.empty"));
			}else{
				tooltip.add(tank.getFluid().getLocalizedName());
				tooltip.add(TextFormatting.GRAY + I18n.format("tomsmod.gui.fluid", tank.getFluidAmount(), tank.getCapacity()));
			}
			drawHoveringText(tooltip, mouseX, mouseY);
		}
	}
	public final void drawFluidTank(int xPosition, int yPosition, FluidTank tank) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableBlend();
		mc.getTextureManager().bindTexture(LIST_TEXTURE);
		drawTexturedModalRect(xPosition, yPosition, 78, 120, TANK_WIDTH, TANK_HEIGHT);
		drawFluid(xPosition + 4, yPosition + 4, tank.getFluid(), tank.getCapacity());
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 200);
		mc.getTextureManager().bindTexture(LIST_TEXTURE);
		drawTexturedModalRect(xPosition, yPosition, 98, 120, TANK_WIDTH, TANK_HEIGHT);
		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.popMatrix();
	}
	public final void drawFluidTank(int xPosition, int yPosition, FluidTank tank, int texX, int texY) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		if(texX > -1){
			mc.getTextureManager().bindTexture(gui);
			drawTexturedModalRect(xPosition, yPosition, texX, texY, TANK_WIDTH, TANK_HEIGHT);
		}
		GlStateManager.disableBlend();
		drawFluid(xPosition + 4, yPosition + 4, tank.getFluid(), tank.getCapacity());
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 200);
		mc.getTextureManager().bindTexture(LIST_TEXTURE);
		drawTexturedModalRect(xPosition, yPosition, 98, 120, TANK_WIDTH, TANK_HEIGHT);
		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.popMatrix();
	}
	protected final void drawInventoryText(int y){
		fontRendererObj.drawString(I18n.format("container.inventory"), 6, y, 4210752);
	}
	/**
	 * Draws a textured rectangle at the current z-value.
	 */
	public final void drawTexturedModalRect(double x, double y, double textureX, double textureY, double width, double height)
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(x + 0, y + height, this.zLevel).tex((textureX + 0) * f, (float)(textureY + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + height, this.zLevel).tex((float)(textureX + width) * f, (float)(textureY + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + 0, this.zLevel).tex((float)(textureX + width) * f, (textureY + 0) * f1).endVertex();
		vertexbuffer.pos(x + 0, y + 0, this.zLevel).tex((textureX + 0) * f, (textureY + 0) * f1).endVertex();
		tessellator.draw();
	}
	public final Runnable renderItemInGuiWithRunnableHover(final ItemStack stack, final int x, final int y, final int mouseX, final int mouseY, boolean redBg){
		if(stack != null){
			if(redBg){
				Render.setColourWithAlphaPercent(0xFF0000,50);
				Render.drawRect(x, y, 16, 16);
			}
			GlStateManager.translate(0.0F, 0.0F, 32.0F);
			RenderHelper.enableGUIStandardItemLighting();
			this.zLevel = 200.0F;
			this.itemRender.zLevel = 200.0F;
			FontRenderer font = null;
			if (stack != null) font = stack.getItem().getFontRenderer(stack);
			if (font == null) font = fontRendererObj;
			this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
			this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, null);
			this.zLevel = 0.0F;
			this.itemRender.zLevel = 0.0F;
			RenderHelper.disableStandardItemLighting();
			return new Runnable() {

				@Override
				public void run() {
					if(mouseX >= x && mouseY >= y && mouseX < x + 16 && mouseY < y + 16){
						List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
						list.add(I18n.format("tomsmod.gui.amount", stack.stackSize));
						for (int i = 0; i < list.size(); ++i)
						{
							if (i == 0)
							{
								list.set(i, stack.getRarity().rarityColor + list.get(i));
							}
							else
							{
								list.set(i, TextFormatting.GRAY + list.get(i));
							}
						}
						drawHoveringText(list, mouseX, mouseY);
						RenderHelper.disableStandardItemLighting();
					}
				}
			};
		}else{
			return new Runnable() {@Override public void run() {}};
		}
	}
}
