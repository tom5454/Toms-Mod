package com.tom.api.tileentity;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.network.INBTPacketSender;

public interface IConfigurable extends INBTPacketReceiver, INBTPacketSender {
	IConfigurationOption getOption();

	boolean canConfigure(EntityPlayer player, ItemStack stack);

	BlockPos getPos2();

	String getConfigName();

	default BlockPos getSecurityStationPos() {
		ItemStack securityCardStack = getCardStack();
		return !securityCardStack.isEmpty() && securityCardStack.getItem() instanceof ISecurityStationLinkCard ? ((ISecurityStationLinkCard) securityCardStack.getItem()).getStation(securityCardStack) : null;
	}

	void setCardStack(ItemStack stack);

	ItemStack getCardStack();

	default ItemStack getStack() {
		return new ItemStack(Blocks.BARRIER);
	}

	public static interface IConfigurationOption {
		@SideOnly(Side.CLIENT)
		default void renderBackground(Minecraft mc, int x, int y) {
		}

		int getWidth();

		int getHeight();

		void readFromNBTPacket(NBTTagCompound tag);

		void writeModificationNBTPacket(NBTTagCompound tag);

		@SideOnly(Side.CLIENT)
		default void renderForeground(Minecraft mc, int x, int y, int mouseX, int mouseY) {
		}

		@SideOnly(Side.CLIENT)
		default void actionPreformed(Minecraft mc, GuiButton button) {
		}

		@SideOnly(Side.CLIENT)
		default void init(Minecraft mc, int x, int y, int lastButtonID, List<GuiButton> buttonList, List<GuiLabel> labelList) {
		}

		default void addSlotsToList(IConfigurable tile, List<Slot> slotList, int x, int y) {
		}

		ResourceLocation securitySlotLocation = new ResourceLocation("tomsmod:textures/gui/slotSecurity.png");
		ResourceLocation powerlinkSlotLocation = new ResourceLocation("tomsmod:textures/gui/slotpowerlink.png");

		IInventory getInventory();

		default void update(Minecraft mc, int x, int y) {
		}

		@SideOnly(Side.CLIENT)
		default void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		}

		@SideOnly(Side.CLIENT)
		default void keyTyped(char typedChar, int keyCode) throws IOException {
		}

		default boolean drawBackground() {
			return true;
		}

		public static class SlotSecurityCard extends Slot {

			public SlotSecurityCard(IInventory inventoryIn, int index, int xPosition, int yPosition) {
				super(inventoryIn, index, xPosition, yPosition);
			}

			@Override
			public int getSlotStackLimit() {
				return 1;
			}

			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack != null && stack.getItem() instanceof ISecurityStationLinkCard && ((ISecurityStationLinkCard) stack.getItem()).getStation(stack) != null;
			}
		}
	}

	public static interface ICustomConfigurationErrorMessage {
		ITextComponent[] getMessage(EntityPlayer player, ItemStack stack);
	}

	public static class SecurityCardInventory extends InventoryBasic {
		private IConfigurable te;

		public SecurityCardInventory(IConfigurable tile) {
			super("security", false, 1);
			this.setInventorySlotContents(0, tile.getCardStack());
			te = tile;
		}

		@Override
		public void closeInventory(EntityPlayer player) {
			te.setCardStack(this.getStackInSlot(0));
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return index == 0 && stack != null && stack.getItem() instanceof ISecurityStationLinkCard && ((ISecurityStationLinkCard) stack.getItem()).getStation(stack) != null;
		}
	}
}
