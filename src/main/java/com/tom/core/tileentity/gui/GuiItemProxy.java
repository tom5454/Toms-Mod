package com.tom.core.tileentity.gui;

import java.io.IOException;

import com.tom.core.tileentity.TileEntityItemProxy;
import com.tom.core.tileentity.inventory.ContainerItemProxy;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class GuiItemProxy extends GuiTomsMod {
	private GuiButton whiteListButton;
	private GuiButton itemButton;
	private GuiButton lockButton;
	private GuiButton nbtButton;
	private final TileEntityItemProxy te;
	public GuiItemProxy(InventoryPlayer playerInv, TileEntityItemProxy tile) {
		super(new ContainerItemProxy(playerInv, tile), "GuiItemProxy");
		this.ySize = MathHelper.floor_double(this.ySize * 1.1D);
		this.te = tile;
	}
	@Override
	public void drawGuiContainerForegroundLayer(int mX, int my){
		String s = I18n.format("tomsmod.gui.itemProxy");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory"), 6, ySize - 102 + 2, 4210752);

	}
	@Override
	protected void mouseClicked(int x, int y, int b) throws IOException{
		super.mouseClicked(x, y, b);
	}
	@Override
	public void initGui(){
		super.initGui();
		whiteListButton = new GuiButton(0, guiLeft + 56, guiTop + 72, 10, 20, "B");
		buttonList.add(whiteListButton);
		itemButton = new GuiButton(1, guiLeft + 66, guiTop + 72, 10, 20, TextFormatting.GREEN+"I");
		buttonList.add(itemButton);
		lockButton = new GuiButton(2, guiLeft + 76, guiTop + 72, 10, 20, TextFormatting.RED+"L");
		buttonList.add(lockButton);
		nbtButton = new GuiButton(3, guiLeft + 86, guiTop + 72, 10, 20, TextFormatting.GREEN+"N");
		buttonList.add(nbtButton);
	}
	@Override
	public void updateScreen(){
		super.updateScreen();
		whiteListButton.displayString = te.mode ? "W" : "B";
		itemButton.displayString = te.isItemMode ? TextFormatting.GREEN+"I" : TextFormatting.RED+"I";
		lockButton.displayString = te.isLocked ? TextFormatting.GREEN+"L" : TextFormatting.RED+"L";
		nbtButton.displayString = te.useNBT ? TextFormatting.GREEN+"N" : TextFormatting.RED+"N";
	}
	@Override
	protected void actionPerformed(GuiButton button){
		//super.actionPerformed(button);
		int id = button.id;
		if(id >= 0 && id < 4){
			this.sendButtonUpdate(id, te);
		}
	}
}
