package com.tom.storage.tileentity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.tom.api.network.INBTPacketReceiver;
import com.tom.apis.TomsModUtils;
import com.tom.network.messages.MessageCraftingReportSync.MessageType;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.multipart.StorageNetworkGrid.CalculatedClientCrafting;
import com.tom.storage.multipart.StorageNetworkGrid.ClientCraftingStack;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingReportScreen;
import com.tom.storage.multipart.StorageNetworkGrid.IStorageTerminalGui;

import mapwriterTm.util.Render;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCraftingReport extends GuiScreen implements INBTPacketReceiver, ICraftingReportScreen {
	private CalculatedClientCrafting crafting;
	private int xSize = 238;
	private int ySize = 206;
	protected int guiLeft;
	protected int guiTop;
	@SuppressWarnings("rawtypes")
	private List<ClientCraftingStack> itemListClient = new ArrayList<ClientCraftingStack>(15);
	private static final ResourceLocation gui = new ResourceLocation("tomsmod:textures/gui/crafting2.png");
	private IStorageTerminalGui parent;
	private GuiButton buttonCancel, buttonSwitchCPU, buttonStart;
	/** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
	private float currentScroll;
	/** True if the scrollbar is being dragged */
	private boolean isScrolling;
	/** True if the left mouse button was held down last time drawScreen was called. */
	private boolean wasClicking;
	private static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	private NBTTagCompound tag = new NBTTagCompound();
	private NBTTagList stackList = new NBTTagList(), missingList = new NBTTagList(), recipeList = new NBTTagList();
	private boolean hasMain = false;
	private int missingSize;
	private int stackListSize;
	private int recipeListSize;
	public GuiCraftingReport(IStorageTerminalGui parent) {
		this.parent = parent;
	}
	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		if(message.getBoolean("ERROR")){
			sendChatMessage(TextFormatting.RED + I18n.format("tomsMod.chat.craftFail", I18n.format("tomsMod.craftingError_3")));
			close(false);
		}else{
			int id = message.getInteger("id");
			switch(MessageType.values()[id]){
			case MAIN:
				tag.merge(message.getCompoundTag("m"));
				hasMain = true;
				missingSize = message.getInteger("ms");
				stackListSize = message.getInteger("sls");
				recipeListSize = message.getInteger("rls");
				break;
			case MISSING:
				missingList.appendTag(message.getCompoundTag("m"));
				break;
			case NORMAL:
				stackList.appendTag(message.getCompoundTag("m"));
				break;
			case RECIPE:
				recipeList.appendTag(message.getCompoundTag("m"));
				break;
			default:
				break;
			}
			if(hasMain){
				if(stackList.tagCount() == stackListSize && missingList.tagCount() == missingSize && recipeList.tagCount() == recipeListSize){
					tag.setTag("mi", missingList);
					tag.setTag("c", recipeList);
					tag.setTag("l", stackList);
					crafting = StorageNetworkGrid.readCalculatedCraftingFromNBT(tag);
					scrollTo(0);
				}
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
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		{
			boolean flag = Mouse.isButtonDown(0);
			int i = this.guiLeft;
			int j = this.guiTop;
			int k = i + 218;
			int l = j + 19;
			int i1 = k + 14;
			int j1 = l + 116;

			if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
			{
				this.isScrolling = this.needsScrollBars();
			}

			if (!flag)
			{
				this.isScrolling = false;
			}
			this.wasClicking = flag;

			if (this.isScrolling)
			{
				this.currentScroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
				this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
				scrollTo(this.currentScroll);
			}
			super.drawScreen(mouseX, mouseY, partialTicks);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
			i = k;
			j = l;
			k = j1;
			this.drawTexturedModalRect(i, j + (int)((k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
		}
		if(crafting != null){
			GL11.glPushMatrix();
			RenderHelper.enableGUIStandardItemLighting();
			for(int i = 0;i<Math.min(itemListClient.size(), 15);i++){
				int x = guiLeft + 9 + i % 3 * 68;
				int y = guiTop + 19 + i / 3 * 23;
				renderCraftingAt(x, y, itemListClient.get(i), mouseX, mouseY);
			}
			GL11.glPopMatrix();
			int secTime = MathHelper.ceiling_double_int(crafting.time / 20D);
			GL11.glPushMatrix();
			GL11.glTranslated(guiLeft + 5, guiTop + 5, zLevel);
			double scale = 0.71D;
			GL11.glScaled(scale, scale, scale);
			mc.fontRendererObj.drawString(I18n.format("tomsMod.storage.craftingPlan", crafting.memory, crafting.operations, secTime / 60, secTime % 60), 0, 0, 4210752);
			GL11.glPopMatrix();
			for(int i = 0;i<Math.min(itemListClient.size(), 15);i++){
				int x = guiLeft + 9 + i % 3 * 68;
				int y = guiTop + 19 + i / 3 * 23;
				ClientCraftingStack s = itemListClient.get(i);
				if(mouseX >= x && mouseY >= y && mouseX < x + 68 && mouseY < y + 22){
					if(s != null){
						s.draw(x, y, mouseX, mouseY, this, true);
					}
				}
			}
		}else{
			GL11.glPushMatrix();
			GL11.glTranslated(guiLeft + 5, guiTop + 5, zLevel);
			double scale = 0.71D;
			GL11.glScaled(scale, scale, scale);
			mc.fontRendererObj.drawString(I18n.format("tomsMod.storage.craftingPlan", "?", "?", "--", "--"), 0, 0, 4210752);
			GL11.glPopMatrix();
		}
	}
	private boolean needsScrollBars() {
		return crafting != null ? this.crafting.stacks.size() > 15 : false;
	}
	@Override
	public void initGui() {
		labelList.clear();
		super.initGui();
		this.guiLeft = (this.width - xSize) / 2;
		this.guiTop = (this.height - ySize) / 2;
		if(crafting != null)scrollTo(0);
		buttonCancel = new GuiButton(0, guiLeft + 5 ,guiTop + 180, 50, 20, I18n.format("gui.cancel"));
		buttonList.add(buttonCancel);
		buttonStart = new GuiButton(1, guiLeft + 163 ,guiTop + 180, 50, 20, I18n.format("tomsmod.gui.start"));
		buttonList.add(buttonStart);
		buttonSwitchCPU = new GuiButton(2, guiLeft + 10, guiTop + 136, I18n.format("tomsMod.storage.craftingCPU", I18n.format("tomsMod.storage.autoCpu")));
		buttonList.add(buttonSwitchCPU);
	}
	public void scrollTo(float p_148329_1_)
	{
		int i = (this.crafting.stacks.size() + 3 - 1) / 3 - 5;
		int j = (int)(p_148329_1_ * i + 0.5D);

		if (j < 0)
		{
			j = 0;
		}

		for (int k = 0; k < 5; ++k)
		{
			for (int l = 0; l < 3; ++l)
			{
				int i1 = l + (k + j) * 3;

				if (i1 >= 0 && i1 < this.crafting.stacks.size())
				{
					setSlotContents(l + k * 3, this.crafting.stacks.get(i1));
				}
				else
				{
					setSlotContents(l + k * 3, null);
				}
			}
		}
	}
	@SuppressWarnings("rawtypes")
	private void setSlotContents(int i, ClientCraftingStack s) {
		if(itemListClient.size() > i){
			itemListClient.set(i, s);
		}else{
			itemListClient.add(s);
		}
	}
	@SuppressWarnings("rawtypes")
	private void renderCraftingAt(int posX, int posY, ClientCraftingStack s, int mouseX, int mouseY){
		if(s != null){
			s.draw(posX, posY, mouseX, mouseY, this, false);
		}
	}
	@Override
	public void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, int color, String... extraInfo){
		if(stack != null){
			boolean hasBg = mouseX >= x-1 && mouseY >= y-1 && mouseX < x + 17 && mouseY < y + 17;
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
			if(hasBg){
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
	private void close(boolean craft){
		mc.displayGuiScreen(parent.getScreen());
		parent.sendCrafting(crafting == null ? -1 : crafting.cpuId, craft);
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 0){
			close(false);
		}else if(button.id == 1){
			close(true);
		}else if(button.id == 2){
			if(crafting.cpus.size() > 0){
				if(crafting.cpuId == -1){
					int next = crafting.cpus.get(0);
					crafting.cpuId = next;
				}else{
					int index = crafting.cpus.indexOf(crafting.cpuId) + 1;
					if(index < crafting.cpus.size()){
						crafting.cpuId = crafting.cpus.get(index);
					}else{
						crafting.cpuId = -1;
					}
				}
			}
		}
	}
	@Override
	public void updateScreen() {
		if(crafting == null){
			buttonSwitchCPU.displayString = I18n.format("tomsmod.gui.pleaseWait");
			buttonSwitchCPU.enabled = false;
			buttonStart.enabled = false;
		}else{
			boolean startEnabled;
			buttonSwitchCPU.displayString = ((buttonSwitchCPU.enabled = startEnabled = crafting.cpus.size() > 0) ? getButtonText() : I18n.format("tomsMod.stoarge.noCPUsAvailable"));
			buttonStart.enabled = startEnabled && !crafting.hasMissing;
		}
	}
	private String getButtonText(){
		return I18n.format("tomsMod.storage.craftingCPU", crafting.cpuId == -1 ? I18n.format("tomsMod.storage.autoCpu") : I18n.format("tomsMod.storage.cpuId", crafting.cpuId));
	}
	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		int i = Mouse.getEventDWheel();

		if (i != 0 && this.needsScrollBars())
		{
			int j = crafting.stacks.size() / 3 - 5;

			if (i > 0)
			{
				i = 1;
			}

			if (i < 0)
			{
				i = -1;
			}

			this.currentScroll = (float)(this.currentScroll - (double)i / (double)j);
			this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
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
		return mc.fontRendererObj;
	}
}
