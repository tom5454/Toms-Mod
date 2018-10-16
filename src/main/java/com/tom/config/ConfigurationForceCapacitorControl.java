package com.tom.config;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.gui.GuiTomsMod;
import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IConfigurable.IConfigurationOption;
import com.tom.client.GuiButtonRedstoneMode;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.tileentity.TileEntityForceCapacitor;
import com.tom.defense.tileentity.inventory.ContainerSecurityStation.SlotPowerLinkCard;
import com.tom.lib.utils.RenderUtil;

import com.tom.core.tileentity.gui.GuiConfigurator.GuiButtonConfig;

public final class ConfigurationForceCapacitorControl implements IConfigurationOption {
	private IInventory inventory;
	@SideOnly(Side.CLIENT)
	private GuiButtonRedstoneMode rsButton;
	@SideOnly(Side.CLIENT)
	private GuiButtonForcePowerSharing power;
	private ForceDeviceControlType controlType = ForceDeviceControlType.IGNORE;
	private boolean sharing = false;
	private int lastButtonID = -1;

	public ConfigurationForceCapacitorControl(TileEntityForceCapacitor c) {
		this.inventory = c.inv;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(Minecraft mc, int x, int y) {
		mc.renderEngine.bindTexture(securitySlotLocation);
		RenderUtil.drawTexturedRect(x + 1, y + 21, 18, 18);
		mc.renderEngine.bindTexture(powerlinkSlotLocation);
		RenderUtil.drawTexturedRect(x + 19, y + 21, 18, 18);
	}

	@Override
	public int getWidth() {
		return 50;
	}

	@Override
	public int getHeight() {
		return 60;
	}

	@Override
	public void readFromNBTPacket(NBTTagCompound tag) {
		this.controlType = ForceDeviceControlType.get(tag.getInteger("r"));
	}

	@Override
	public void writeModificationNBTPacket(NBTTagCompound tag) {
		tag.setInteger("r", this.controlType.ordinal());
		tag.setBoolean("s", sharing);
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
		} else if (button instanceof GuiButtonForcePowerSharing) {
			if (button.id > lastButtonID) {
				int id = button.id - (lastButtonID + 2);
				if (id == 0) {
					this.sharing = !sharing;
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void init(Minecraft mc, int x, int y, int lastButtonID, List<GuiButton> buttonList, List<GuiLabel> labelList) {
		this.lastButtonID = lastButtonID;
		rsButton = new GuiButtonRedstoneMode(lastButtonID + 1, x + 40, y + 20, controlType);
		power = new GuiButtonForcePowerSharing(lastButtonID + 2, x + 18, y - 1, sharing);
		buttonList.add(rsButton);
		buttonList.add(power);
	}

	@Override
	public void addSlotsToList(IConfigurable tile, List<Slot> slotList, int x, int y) {
		slotList.add(new SlotSecurityCard(inventory, 0, x + 9, y - 20));
		slotList.add(new SlotPowerLinkCard(inventory, 2, x + 27, y - 20));
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void update(Minecraft mc, int x, int y) {
		rsButton.controlType = controlType;
		power.controlType = sharing;
	}

	@SideOnly(Side.CLIENT)
	public static class GuiButtonForcePowerSharing extends GuiButtonConfig {
		public boolean controlType;

		public GuiButtonForcePowerSharing(int buttonId, int x, int y, boolean type) {
			super(buttonId, x, y, 20, 20, I18n.format("tomsmod.gui.forcecap_iconEqual"));
			this.controlType = type;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
			if (controlType) {
				displayString = I18n.format("tomsmod.gui.forcecap_iconDrainAll");
			} else {
				displayString = I18n.format("tomsmod.gui.forcecap_iconEqual");
			}
			super.drawButton(mc, mouseX, mouseY, partTicks);
		}

		@Override
		public void postDraw(Minecraft mc, int mouseX, int mouseY, GuiTomsMod gui) {
			if (this.visible) {
				if (hovered) {
					gui.drawHoveringTextI(I18n.format(controlType ? "tomsmod.gui.forcecap_tooltipDrainAll" : "tomsmod.gui.forcecap_tooltipEqual"), mouseX, mouseY);
				}
			}
		}
	}
}