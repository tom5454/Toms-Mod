package com.tom.api.tileentity;

import java.util.List;

import mapwriterTm.util.Render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.network.INBTPacketSender;
import com.tom.defense.ForceDeviceControlType;

public interface IConfigurable extends INBTPacketReceiver, INBTPacketSender{
	IConfigurationOption getOption();
	boolean canConfigure(EntityPlayer player, ItemStack stack);
	BlockPos getPos2();
	BlockPos getSecurityStationPos();
	void setCardStack(ItemStack stack);
	ItemStack getCardStack();
	public static interface IConfigurationOption{
		@SideOnly(Side.CLIENT)
		void renderBackground(Minecraft mc, int x, int y);
		int getWidth();
		int getHeight();
		void readFromNBTPacket(NBTTagCompound tag);
		void writeModificationNBTPacket(NBTTagCompound tag);
		@SideOnly(Side.CLIENT)
		void renderForeground(Minecraft mc, int x, int y, int mouseX, int mouseY);
		@SideOnly(Side.CLIENT)
		void actionPreformed(Minecraft mc, GuiButton button);
		@SideOnly(Side.CLIENT)
		void init(Minecraft mc, int x, int y, int lastButtonID, List<GuiButton> buttonList);
		void addSlotsToList(IConfigurable tile, List<Slot> slotList, int x, int y);
		ResourceLocation securitySlotLocation = new ResourceLocation("tomsmod:textures/gui/slotSecurity.png");
		IInventory getInventory();
		void update(Minecraft mc, int x, int y);
		public static class SlotSecurityCard extends Slot{

			public SlotSecurityCard(IInventory inventoryIn, int index,
					int xPosition, int yPosition) {
				super(inventoryIn, index, xPosition, yPosition);
			}
			@Override
			public int getSlotStackLimit() {
				return 1;
			}
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack != null && stack.getItem() instanceof ISecurityStationLinkCard && ((ISecurityStationLinkCard)stack.getItem()).getStation(stack) != null;
			}
		}
		public static final class ConfigurationOptionSide implements IConfigurationOption{
			public byte sideConfig;
			public final ResourceLocation[] sideTexLoc;
			public final ResourceLocation selectionTexLoc;
			private int lastButtonID = -1;
			private GuiButtonSelection buttonUp, buttonDown, buttonNorth, buttonSouth, buttonEast, buttonWest;
			private IInventory inventory;
			public ConfigurationOptionSide(byte sideConfig,
					ResourceLocation[] sideTexLoc,
					ResourceLocation selectionType, IConfigurable c) {
				this.sideConfig = sideConfig;
				this.sideTexLoc = sideTexLoc;
				this.selectionTexLoc = selectionType;
				this.inventory = new SecurityCardInventory(c);
			}
			public ConfigurationOptionSide(byte sideConfig,
					ResourceLocation sideTexLoc,
					ResourceLocation selectionType, IConfigurable c) {
				this(sideConfig,fillArray(new ResourceLocation[6], sideTexLoc),selectionType,c);
			}
			public static ResourceLocation[] fillArray(ResourceLocation[] putTo, ResourceLocation value){
				for(int i = 0;i<putTo.length;i++)putTo[i] = value;
				return putTo;
			}
			@Override
			@SideOnly(Side.CLIENT)
			public void renderBackground(Minecraft mc, int xP, int yP) {
				mc.renderEngine.bindTexture(sideTexLoc[2]);
				Render.drawTexturedRect(xP+17, yP+17, 16, 16);
				mc.renderEngine.bindTexture(sideTexLoc[1]);
				Render.drawTexturedRect(xP+17, yP, 16, 16);
				mc.renderEngine.bindTexture(sideTexLoc[0]);
				Render.drawTexturedRect(xP+17, yP+34, 16, 16);
				mc.renderEngine.bindTexture(sideTexLoc[4]);
				Render.drawTexturedRect(xP, yP+17, 16, 16);
				mc.renderEngine.bindTexture(sideTexLoc[5]);
				Render.drawTexturedRect(xP+34, yP+17, 16, 16);
				mc.renderEngine.bindTexture(sideTexLoc[3]);
				Render.drawTexturedRect(xP+34, yP+34, 16, 16);
				mc.renderEngine.bindTexture(securitySlotLocation);
				Render.drawTexturedRect(xP-1, yP-1, 18, 18);
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
			public void renderForeground(Minecraft mc, int xP, int yP,
					int mouseX, int mouseY) {
				if(contains(EnumFacing.DOWN)){
					mc.renderEngine.bindTexture(selectionTexLoc);
					Render.drawTexturedRect(xP+17, yP+34, 16, 16);
				}
				if(contains(EnumFacing.UP)){
					mc.renderEngine.bindTexture(selectionTexLoc);
					Render.drawTexturedRect(xP+17, yP, 16, 16);
				}
				if(contains(EnumFacing.NORTH)){
					mc.renderEngine.bindTexture(selectionTexLoc);
					Render.drawTexturedRect(xP+17, yP+17, 16, 16);
				}
				if(contains(EnumFacing.SOUTH)){
					mc.renderEngine.bindTexture(selectionTexLoc);
					Render.drawTexturedRect(xP+34, yP+34, 16, 16);
				}
				if(contains(EnumFacing.WEST)){
					mc.renderEngine.bindTexture(selectionTexLoc);
					Render.drawTexturedRect(xP, yP+17, 16, 16);
				}
				if(contains(EnumFacing.EAST)){
					mc.renderEngine.bindTexture(selectionTexLoc);
					Render.drawTexturedRect(xP+34, yP+17, 16, 16);
				}

			}
			@SideOnly(Side.CLIENT)
			public static class GuiButtonSelection extends GuiButton{

				public GuiButtonSelection(int buttonId, int x, int y) {
					super(buttonId, x, y, 16,16,"");
				}
				@Override
				public void drawButton(Minecraft mc, int mouseX, int mouseY) {
					this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
					int i = this.getHoverState(this.hovered);
					if(i == 2){
						Render.setColourWithAlphaPercent(0xFFFFFF, 20);
						Render.drawRect(xPosition, yPosition, width, height);
					}
				}
			}
			@Override
			public void actionPreformed(Minecraft mc, GuiButton button) {
				if(button instanceof GuiButtonSelection){
					if(button.id > lastButtonID){
						int id = button.id - (lastButtonID + 1);
						EnumFacing side = EnumFacing.VALUES[id % EnumFacing.VALUES.length];
						boolean c = contains(side);
						if(c)sideConfig &= ~(1 << side.ordinal());
						else sideConfig |= 1 << side.ordinal();
					}
				}
			}
			public boolean contains(EnumFacing side) {
				return (sideConfig & (1 << side.ordinal())) != 0;
			}
			@Override
			public void init(Minecraft mc, int x, int y, int lastButtonID, List<GuiButton> buttonList) {
				this.lastButtonID = lastButtonID;
				this.buttonDown = new GuiButtonSelection(lastButtonID+1, x+17, y+34);
				this.buttonUp = new GuiButtonSelection(lastButtonID+2, x+17, y);
				this.buttonNorth = new GuiButtonSelection(lastButtonID+3, x+17, y+17);
				this.buttonSouth = new GuiButtonSelection(lastButtonID+4, x+34, y+34);
				this.buttonWest = new GuiButtonSelection(lastButtonID+5, x, y+17);
				this.buttonEast = new GuiButtonSelection(lastButtonID+6, x+34, y+17);
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
			public void update(Minecraft mc, int x, int y) {

			}
		}
		public static final class ConfigurationRedstoneControl implements IConfigurationOption{
			private IInventory inventory;
			private GuiButtonRedstoneMode rsButton;
			private ForceDeviceControlType controlType;
			private int lastButtonID = -1;
			public ConfigurationRedstoneControl(IConfigurable c, ForceDeviceControlType type) {
				this.inventory = new SecurityCardInventory(c);
				this.controlType = type;
			}
			@Override
			public void renderBackground(Minecraft mc, int x, int y) {
				mc.renderEngine.bindTexture(securitySlotLocation);
				Render.drawTexturedRect(x+1,y+1, 18, 18);
			}

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
			public void renderForeground(Minecraft mc, int x, int y,
					int mouseX, int mouseY) {

			}
			@Override
			public void actionPreformed(Minecraft mc, GuiButton button) {
				if(button instanceof GuiButtonRedstoneMode){
					if(button.id > lastButtonID){
						int id = button.id - (lastButtonID + 1);
						if(id == 0){
							this.controlType = ForceDeviceControlType.get(controlType.ordinal()+1);
						}
					}
				}
			}

			@Override
			public void init(Minecraft mc, int x, int y, int lastButtonID,
					List<GuiButton> buttonList) {
				this.lastButtonID = lastButtonID;
				rsButton = new GuiButtonRedstoneMode(lastButtonID+1, x+20, y, controlType);
				buttonList.add(rsButton);
			}

			@Override
			public void addSlotsToList(IConfigurable tile, List<Slot> slotList, int x, int y) {
				slotList.add(new SlotSecurityCard(inventory, 0, x+2, y+2));
			}

			@Override
			public IInventory getInventory() {
				return inventory;
			}
			@SideOnly(Side.CLIENT)
			public static class GuiButtonRedstoneMode extends GuiButton{
				public ForceDeviceControlType controlType;
				public GuiButtonRedstoneMode(int buttonId, int x, int y, ForceDeviceControlType type) {
					super(buttonId, x, y, 20,20,"");
					this.controlType = type;
				}
				@Override
				public void drawButton(Minecraft mc, int mouseX, int mouseY) {
					if (this.visible)
					{
						mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
						this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
						int i = this.getHoverState(this.hovered);
						GlStateManager.enableBlend();
						GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
						GlStateManager.blendFunc(770, 771);
						this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
						this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
						this.mouseDragged(mc, mouseX, mouseY);
						mc.getTextureManager().bindTexture(controlType.iconLocation);
						Render.drawTexturedRect(xPosition+2, yPosition+2, 16, 16);
					}
				}
			}
			@Override
			public void update(Minecraft mc, int x, int y) {
				rsButton.controlType = controlType;
			}
		}
	}
	public static interface ICustomConfigurationErrorMessage{
		ITextComponent[] getMessage(EntityPlayer player, ItemStack stack);
	}
	public static class SecurityCardInventory implements IInventory{
		private ItemStack stack;
		private IConfigurable te;
		public SecurityCardInventory(IConfigurable tile) {
			this.stack = tile.getCardStack();
			te = tile;
		}

		@Override
		public String getName() {
			return "security";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public 	ITextComponent getDisplayName() {
			return new TextComponentString(this.getName());
		}

		@Override
		public int getSizeInventory() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			return index == 0 ? this.stack : null;
		}

		@Override
		public ItemStack decrStackSize(int slot, int count) {
			if(slot == 0){
				if (this.stack != null)
				{
					ItemStack var3;

					if (this.stack.stackSize <= count)
					{
						var3 = this.stack;
						this.stack = null;
						return var3;
					}
					else
					{
						var3 = this.stack.splitStack(count);

						if (this.stack.stackSize == 0)
						{
							this.stack = null;
						}

						return var3;
					}
				}
				else
				{
					return null;
				}
			}
			return null;
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			if(index == 0){
				ItemStack s = stack;
				stack = null;
				return s;
			}
			return null;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			if(index == 0)this.stack = stack;
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public void markDirty() {

		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return true;
		}

		@Override
		public void openInventory(EntityPlayer player) {

		}

		@Override
		public void closeInventory(EntityPlayer player) {
			te.setCardStack(stack);
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return index == 0 && stack != null && stack.getItem() instanceof ISecurityStationLinkCard && ((ISecurityStationLinkCard)stack.getItem()).getStation(stack) != null;
		}

		@Override
		public int getField(int id) {
			return 0;
		}

		@Override
		public void setField(int id, int value) {

		}

		@Override
		public int getFieldCount() {
			return 0;
		}

		@Override
		public void clear() {
			this.stack = null;
		}

	}
}
