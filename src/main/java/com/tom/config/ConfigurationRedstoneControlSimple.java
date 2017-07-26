package com.tom.config;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.IConfigurable.IConfigurationOption;
import com.tom.client.GuiButtonRedstoneMode;
import com.tom.defense.ForceDeviceControlType;

public final class ConfigurationRedstoneControlSimple implements IConfigurationOption {
	@SideOnly(Side.CLIENT)
	private GuiButtonRedstoneMode rsButton;
	private ForceDeviceControlType controlType;
	private int lastButtonID = -1;

	@Override
	public int getWidth() {
		return 40;
	}

	@Override
	public int getHeight() {
		return 20;
	}

	@Override
	public void readFromNBTPacket(NBTTagCompound tag) {
		this.controlType = ForceDeviceControlType.get(tag.getInteger("r"));
	}

	@Override
	public void writeModificationNBTPacket(NBTTagCompound tag) {
		tag.setInteger("r", this.controlType.ordinal());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void actionPreformed(Minecraft mc, GuiButton button) {
		if (button instanceof GuiButtonRedstoneMode) {
			if (button.id > lastButtonID) {
				int id = button.id - (lastButtonID + 1);
				if (id == 0) {
					this.controlType = ForceDeviceControlType.get(controlType.ordinal() + 1);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void init(Minecraft mc, int x, int y, int lastButtonID, List<GuiButton> buttonList, List<GuiLabel> labelList) {
		this.lastButtonID = lastButtonID;
		rsButton = new GuiButtonRedstoneMode(lastButtonID + 1, x + 20, y, controlType);
		buttonList.add(rsButton);
	}

	@Override
	public IInventory getInventory() {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void update(Minecraft mc, int x, int y) {
		rsButton.controlType = controlType;
	}
}