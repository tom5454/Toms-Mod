package com.tom.config;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IConfigurable.IConfigurationOption;
import com.tom.client.GuiButtonRedstoneMode;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.tileentity.TileEntityForceConverter;
import com.tom.defense.tileentity.inventory.ContainerSecurityStation.SlotPowerLinkCard;
import com.tom.lib.utils.RenderUtil;

public final class ConfigurationForceConverterControl implements IConfigurationOption {
	private IInventory inventory;
	@SideOnly(Side.CLIENT)
	private GuiButtonRedstoneMode rsButton;
	private ForceDeviceControlType controlType = ForceDeviceControlType.IGNORE;
	private int lastButtonID = -1;

	public ConfigurationForceConverterControl(TileEntityForceConverter c) {
		this.inventory = c.inv;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(Minecraft mc, int x, int y) {
		mc.renderEngine.bindTexture(securitySlotLocation);
		RenderUtil.drawTexturedRect(x + 1, y + 1, 18, 18);
		mc.renderEngine.bindTexture(powerlinkSlotLocation);
		RenderUtil.drawTexturedRect(x + 19, y + 1, 18, 18);
	}

	@Override
	public int getWidth() {
		return 50;
	}

	@Override
	public int getHeight() {
		return 40;
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
		rsButton = new GuiButtonRedstoneMode(lastButtonID + 1, x + 40, y, controlType);
		buttonList.add(rsButton);
	}

	@Override
	public void addSlotsToList(IConfigurable tile, List<Slot> slotList, int x, int y) {
		slotList.add(new SlotSecurityCard(inventory, 0, x + 4, y - 20));
		slotList.add(new SlotPowerLinkCard(inventory, 1, x + 22, y - 20));
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void update(Minecraft mc, int x, int y) {
		rsButton.controlType = controlType;
	}
}