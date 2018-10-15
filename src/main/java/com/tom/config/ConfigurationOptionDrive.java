package com.tom.config;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IConfigurable.IConfigurationOption;
import com.tom.client.GuiButtonSelection;
import com.tom.lib.utils.RenderUtil;
import com.tom.storage.tileentity.TileEntityDrive;
import com.tom.storage.tileentity.gui.GuiCraftingAmountSelection.GuiButtonNum;
import com.tom.util.TMLogger;
import com.tom.util.TomsModUtils;

public final class ConfigurationOptionDrive implements IConfigurationOption {
	private static final ResourceLocation SIDE = new ResourceLocation("tomsmodstorage:textures/blocks/device_side.png");
	public static final ResourceLocation[] SIDES = new ResourceLocation[]{new ResourceLocation("tomsmodstorage:textures/blocks/device_bottom.png"), new ResourceLocation("tomsmodstorage:textures/blocks/device_top.png"), new ResourceLocation("tomsmodstorage:textures/blocks/drive.png"), SIDE, SIDE, SIDE};
	public byte sideConfig;
	private IInventory inventory;
	private int priority;
	@SideOnly(Side.CLIENT)
	private GuiButtonNum up1, up10, up100, up1000, down1, down10, down100, down1000;
	@SideOnly(Side.CLIENT)
	private GuiTextField numberField;
	private static final ResourceLocation selectionTexLoc = new ResourceLocation("tomsmodstorage:textures/blocks/disabled.png");
	private int lastButtonID = -1;
	@SideOnly(Side.CLIENT)
	private GuiButtonSelection buttonUp, buttonDown, buttonNorth, buttonSouth, buttonEast, buttonWest;

	public ConfigurationOptionDrive(TileEntityDrive c) {
		inventory = c.inv;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(Minecraft mc, int xP, int yP) {
		mc.renderEngine.bindTexture(securitySlotLocation);
		RenderUtil.drawTexturedRect(xP - 1, yP - 1, 18, 18);
		mc.renderEngine.bindTexture(SIDES[2]);
		RenderUtil.drawTexturedRect(xP + 17, yP + 17, 16, 16);
		mc.renderEngine.bindTexture(SIDES[1]);
		RenderUtil.drawTexturedRect(xP + 17, yP, 16, 16);
		mc.renderEngine.bindTexture(SIDES[0]);
		RenderUtil.drawTexturedRect(xP + 17, yP + 34, 16, 16);
		mc.renderEngine.bindTexture(SIDES[4]);
		RenderUtil.drawTexturedRect(xP, yP + 17, 16, 16);
		mc.renderEngine.bindTexture(SIDES[5]);
		RenderUtil.drawTexturedRect(xP + 34, yP + 17, 16, 16);
		mc.renderEngine.bindTexture(SIDES[3]);
		RenderUtil.drawTexturedRect(xP + 34, yP + 34, 16, 16);
	}

	@Override
	public int getWidth() {
		return 200;
	}

	@Override
	public int getHeight() {
		return 100;
	}

	@Override
	public void readFromNBTPacket(NBTTagCompound tag) {
		sideConfig = tag.getByte("s");
		numberField.setText("" + (priority = tag.getInteger("p")));
	}

	@Override
	public void writeModificationNBTPacket(NBTTagCompound tag) {
		tag.setByte("s", sideConfig);
		int amount = priority;
		try {
			String s = numberField.getText();
			amount = s.isEmpty() ? 0 : Integer.parseInt(s);
		} catch (NumberFormatException e) {
			TMLogger.catching(e, "Exception occurred while reading a number from a number field! THIS SHOULDN'T BE POSSIBLE!");
		}
		tag.setInteger("p", amount);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderForeground(Minecraft mc, int xP, int yP, int mouseX, int mouseY) {
		if (contains(EnumFacing.DOWN)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			RenderUtil.drawTexturedRect(xP + 17, yP + 34, 16, 16);
		}
		if (contains(EnumFacing.UP)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			RenderUtil.drawTexturedRect(xP + 17, yP, 16, 16);
		}
		if (contains(EnumFacing.NORTH)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			RenderUtil.drawTexturedRect(xP + 17, yP + 17, 16, 16);
		}
		if (contains(EnumFacing.SOUTH)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			RenderUtil.drawTexturedRect(xP + 34, yP + 34, 16, 16);
		}
		if (contains(EnumFacing.WEST)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			RenderUtil.drawTexturedRect(xP, yP + 17, 16, 16);
		}
		if (contains(EnumFacing.EAST)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			RenderUtil.drawTexturedRect(xP + 34, yP + 17, 16, 16);
		}
		mc.fontRenderer.drawString(I18n.format("tomsmod.gui.priority"), xP + 55, yP + 37, 0xFFFFFF);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void actionPreformed(Minecraft mc, GuiButton button) {
		if (button instanceof GuiButtonSelection) {
			if (button.id > lastButtonID) {
				int id = button.id - (lastButtonID + 1);
				EnumFacing side = EnumFacing.VALUES[id % EnumFacing.VALUES.length];
				boolean c = contains(side);
				if (c)
					sideConfig &= ~(1 << side.ordinal());
				else
					sideConfig |= 1 << side.ordinal();
			}
		} else if (button instanceof GuiButtonNum) {
			GuiButtonNum n = (GuiButtonNum) button;
			int amount = 1;
			try {
				String s = numberField.getText();
				amount = s.isEmpty() ? 0 : Integer.parseInt(s);
			} catch (NumberFormatException e) {
				TMLogger.catching(e, "Exception occurred while reading a number from a number field! THIS SHOULDN'T BE POSSIBLE!");
			}
			amount = Math.max(Math.min((amount == 1 && MathHelper.abs(n.getNum()) != 1 ? n.getNum() : amount + n.getNum()), 1000000), -1000000);
			numberField.setText("" + amount);
		} else {
			// int id = button.id;
		}
	}

	public boolean contains(EnumFacing side) {
		return (sideConfig & (1 << side.ordinal())) != 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void init(Minecraft mc, int x, int y, int lastButtonID, List<GuiButton> buttonList, List<GuiLabel> labelList) {
		this.lastButtonID = lastButtonID;
		this.buttonDown = new GuiButtonSelection(lastButtonID + 1, x + 17, y + 34);
		this.buttonUp = new GuiButtonSelection(lastButtonID + 2, x + 17, y);
		this.buttonNorth = new GuiButtonSelection(lastButtonID + 3, x + 17, y + 17);
		this.buttonSouth = new GuiButtonSelection(lastButtonID + 4, x + 34, y + 34);
		this.buttonWest = new GuiButtonSelection(lastButtonID + 5, x, y + 17);
		this.buttonEast = new GuiButtonSelection(lastButtonID + 6, x + 34, y + 17);
		buttonList.add(buttonDown);
		buttonList.add(buttonEast);
		buttonList.add(buttonNorth);
		buttonList.add(buttonSouth);
		buttonList.add(buttonUp);
		buttonList.add(buttonWest);
		int numId = lastButtonID + 7;
		int guiLeft = x + 65, guiTop = y - 20;
		up1 = new GuiButtonNum(numId, guiLeft + 20, guiTop + 26, 1, 1, 20);
		up10 = new GuiButtonNum(numId, guiLeft + 45, guiTop + 26, 10, 16, 25);
		up100 = new GuiButtonNum(numId, guiLeft + 75, guiTop + 26, 100, 32, 30);
		up1000 = new GuiButtonNum(numId, guiLeft + 110, guiTop + 26, 1000, 64, 35);
		down1 = new GuiButtonNum(numId, guiLeft + 20, guiTop + 76, -1, -1, 20);
		down10 = new GuiButtonNum(numId, guiLeft + 45, guiTop + 76, -10, -16, 25);
		down100 = new GuiButtonNum(numId, guiLeft + 75, guiTop + 76, -100, -32, 30);
		down1000 = new GuiButtonNum(numId, guiLeft + 110, guiTop + 76, -1000, -64, 35);
		buttonList.add(down1);
		buttonList.add(down10);
		buttonList.add(down100);
		buttonList.add(down1000);
		buttonList.add(up1);
		buttonList.add(up10);
		buttonList.add(up100);
		buttonList.add(up1000);
		numberField = new GuiTextField(1, mc.fontRenderer, guiLeft + 61, guiTop + 57, 59, 10);
		numberField.setTextColor(0xFFFFFF);
		numberField.setEnableBackgroundDrawing(true);
		numberField.setText("1");
		numberField.setCanLoseFocus(false);
		numberField.setFocused(true);
		numberField.setMaxStringLength(6);
		TomsModUtils.addTextFieldToLabelList(numberField, labelList);
	}

	@Override
	public void addSlotsToList(IConfigurable tile, List<Slot> slotList, int x, int y) {
		slotList.add(new SlotSecurityCard(inventory, 10, x - 73, y - 82));
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		if (Character.isDigit(typedChar) || keyCode == 14) {
			numberField.textboxKeyTyped(typedChar, keyCode);
		}
	}
}