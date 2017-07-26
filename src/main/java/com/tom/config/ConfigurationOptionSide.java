package com.tom.config;

import java.util.List;

import mapwriterTm.util.Render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IConfigurable.SecurityCardInventory;
import com.tom.client.GuiButtonSelection;

public final class ConfigurationOptionSide implements IConfigurable.IConfigurationOption {
	public byte sideConfig;
	public final ResourceLocation[] sideTexLoc;
	public final ResourceLocation selectionTexLoc;
	private int lastButtonID = -1;
	@SideOnly(Side.CLIENT)
	private GuiButtonSelection buttonUp, buttonDown, buttonNorth, buttonSouth, buttonEast, buttonWest;
	private IInventory inventory;

	public ConfigurationOptionSide(ResourceLocation[] sideTexLoc, ResourceLocation selectionType, IConfigurable c) {
		this.sideTexLoc = sideTexLoc;
		this.selectionTexLoc = selectionType;
		this.inventory = new SecurityCardInventory(c);
	}

	public ConfigurationOptionSide(ResourceLocation sideTexLoc, ResourceLocation selectionType, IConfigurable c) {
		this(fillArray(new ResourceLocation[6], sideTexLoc), selectionType, c);
	}

	public static ResourceLocation[] fillArray(ResourceLocation[] putTo, ResourceLocation value) {
		for (int i = 0;i < putTo.length;i++)
			putTo[i] = value;
		return putTo;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(Minecraft mc, int xP, int yP) {
		mc.renderEngine.bindTexture(sideTexLoc[2]);
		Render.drawTexturedRect(xP + 17, yP + 17, 16, 16);
		mc.renderEngine.bindTexture(sideTexLoc[1]);
		Render.drawTexturedRect(xP + 17, yP, 16, 16);
		mc.renderEngine.bindTexture(sideTexLoc[0]);
		Render.drawTexturedRect(xP + 17, yP + 34, 16, 16);
		mc.renderEngine.bindTexture(sideTexLoc[4]);
		Render.drawTexturedRect(xP, yP + 17, 16, 16);
		mc.renderEngine.bindTexture(sideTexLoc[5]);
		Render.drawTexturedRect(xP + 34, yP + 17, 16, 16);
		mc.renderEngine.bindTexture(sideTexLoc[3]);
		Render.drawTexturedRect(xP + 34, yP + 34, 16, 16);
		mc.renderEngine.bindTexture(securitySlotLocation);
		Render.drawTexturedRect(xP - 1, yP - 1, 18, 18);
	}

	@Override
	public int getWidth() {
		return 50;
	}

	@Override
	public int getHeight() {
		return 50;
	}

	@Override
	public void readFromNBTPacket(NBTTagCompound tag) {
		sideConfig = tag.getByte("s");
	}

	@Override
	public void writeModificationNBTPacket(NBTTagCompound tag) {
		tag.setByte("s", sideConfig);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderForeground(Minecraft mc, int xP, int yP, int mouseX, int mouseY) {
		if (contains(EnumFacing.DOWN)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			Render.drawTexturedRect(xP + 17, yP + 34, 16, 16);
		}
		if (contains(EnumFacing.UP)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			Render.drawTexturedRect(xP + 17, yP, 16, 16);
		}
		if (contains(EnumFacing.NORTH)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			Render.drawTexturedRect(xP + 17, yP + 17, 16, 16);
		}
		if (contains(EnumFacing.SOUTH)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			Render.drawTexturedRect(xP + 34, yP + 34, 16, 16);
		}
		if (contains(EnumFacing.WEST)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			Render.drawTexturedRect(xP, yP + 17, 16, 16);
		}
		if (contains(EnumFacing.EAST)) {
			mc.renderEngine.bindTexture(selectionTexLoc);
			Render.drawTexturedRect(xP + 34, yP + 17, 16, 16);
		}

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
	}

	@Override
	public void addSlotsToList(IConfigurable tile, List<Slot> slotList, int x, int y) {
		slotList.add(new SlotSecurityCard(inventory, 0, x, y));
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void update(Minecraft mc, int x, int y) {

	}
}