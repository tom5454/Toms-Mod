package com.tom.config;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.IConfigurable.IConfigurationOption;
import com.tom.client.GuiButtonPowerSharing;
import com.tom.client.GuiButtonRedstoneMode;
import com.tom.client.GuiButtonSelection;
import com.tom.defense.ForceDeviceControlType;
import com.tom.lib.utils.RenderUtil;

public final class ConfigurationOptionMachine implements IConfigurationOption {
	public static final ResourceLocation[] MACHINE_LOC = new ResourceLocation[]{new ResourceLocation("tomsmodfactory:textures/blocks/machineSide.png"), new ResourceLocation("tomsmodfactory:textures/blocks/machineSide.png"), new ResourceLocation("tomsmodfactory:textures/blocks/machineSide.png")};
	public byte sideConfig;
	public final ResourceLocation[] sideTexLoc;
	public final ResourceLocation selectionTexLoc;
	private int lastButtonID = -1;
	private GuiButtonSelection buttonUp, buttonDown, buttonNorth, buttonSouth, buttonEast, buttonWest;
	private GuiButtonRedstoneMode rsButton;
	private GuiButtonPowerSharing buttonPowerSharing;
	private ForceDeviceControlType controlType = ForceDeviceControlType.IGNORE;
	private boolean powersharing;

	public ConfigurationOptionMachine(ResourceLocation[] sideTexLoc, ResourceLocation front, ResourceLocation selectionType) {
		this.sideTexLoc = new ResourceLocation[6];
		for (int i = 0;i < 6;i++) {
			if (i == 0) {
				this.sideTexLoc[i] = sideTexLoc[2];
			} else if (i == 1) {
				this.sideTexLoc[i] = sideTexLoc[0];
			} else if (i == 2) {
				this.sideTexLoc[i] = front;
			} else {
				this.sideTexLoc[i] = sideTexLoc[1];
			}
		}
		this.selectionTexLoc = selectionType;
	}

	public ConfigurationOptionMachine(ResourceLocation front, ResourceLocation selectionType) {
		this(MACHINE_LOC, front, selectionType);
	}

	public ConfigurationOptionMachine(ResourceLocation front, ResourceLocation selectionType, ResourceLocation top) {
		this(putLoc(top), front, selectionType);
	}

	private static ResourceLocation[] putLoc(ResourceLocation t) {
		if (t != null) {
			ResourceLocation[] ret = new ResourceLocation[3];
			for (int i = 0;i < 3;i++) {
				if (i == 2)
					ret[i] = t;
				else
					ret[i] = MACHINE_LOC[i];
			}
			return ret;
		} else
			return MACHINE_LOC;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(Minecraft mc, int xP, int yP) {
		mc.renderEngine.bindTexture(sideTexLoc[2]);
		RenderUtil.drawTexturedRect(xP + 17, yP + 17, 16, 16);
		mc.renderEngine.bindTexture(sideTexLoc[1]);
		RenderUtil.drawTexturedRect(xP + 17, yP, 16, 16);
		mc.renderEngine.bindTexture(sideTexLoc[0]);
		RenderUtil.drawTexturedRect(xP + 17, yP + 34, 16, 16);
		mc.renderEngine.bindTexture(sideTexLoc[4]);
		RenderUtil.drawTexturedRect(xP, yP + 17, 16, 16);
		mc.renderEngine.bindTexture(sideTexLoc[5]);
		RenderUtil.drawTexturedRect(xP + 34, yP + 17, 16, 16);
		mc.renderEngine.bindTexture(sideTexLoc[3]);
		RenderUtil.drawTexturedRect(xP + 34, yP + 34, 16, 16);
	}

	@Override
	public int getWidth() {
		return 50;
	}

	@Override
	public int getHeight() {
		return 72;
	}

	@Override
	public void readFromNBTPacket(NBTTagCompound tag) {
		sideConfig = tag.getByte("s");
		this.controlType = ForceDeviceControlType.get(tag.getInteger("r"));
		powersharing = tag.getBoolean("p");
	}

	@Override
	public void writeModificationNBTPacket(NBTTagCompound tag) {
		tag.setByte("s", sideConfig);
		tag.setInteger("r", this.controlType.ordinal());
		tag.setBoolean("p", powersharing);
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
		} else {
			int id = button.id;
			if (id == lastButtonID + 7) {
				this.controlType = ForceDeviceControlType.get(controlType.ordinal() + 1);
			} else if (id == lastButtonID + 8) {
				this.powersharing = !powersharing;
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
		this.rsButton = new GuiButtonRedstoneMode(lastButtonID + 7, x - 4, y - 4, controlType);
		this.buttonPowerSharing = new GuiButtonPowerSharing(lastButtonID + 8, x + 34, y - 4, powersharing);
		buttonList.add(buttonDown);
		buttonList.add(buttonEast);
		buttonList.add(buttonNorth);
		buttonList.add(buttonSouth);
		buttonList.add(buttonUp);
		buttonList.add(buttonWest);
		buttonList.add(rsButton);
		buttonList.add(buttonPowerSharing);
	}

	@Override
	public IInventory getInventory() {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void update(Minecraft mc, int x, int y) {
		rsButton.controlType = controlType;
		buttonPowerSharing.controlType = powersharing;
	}
}