package com.tom.thirdparty.jei;

import java.awt.Rectangle;
import java.util.List;

import com.tom.storage.tileentity.gui.GuiTerminalBase;

import mezz.jei.api.gui.IAdvancedGuiHandler;

public class GuiTerminalHandler implements IAdvancedGuiHandler<GuiTerminalBase> {

	@Override
	public Class<GuiTerminalBase> getGuiContainerClass() {
		return GuiTerminalBase.class;
	}

	@Override
	public List<Rectangle> getGuiExtraAreas(GuiTerminalBase guiContainer) {
		return null;
	}

	@Override
	public Object getIngredientUnderMouse(GuiTerminalBase gui, int mouseX, int mouseY) {
		return gui.getStackUnderMouse(mouseX, mouseY);
	}

}
