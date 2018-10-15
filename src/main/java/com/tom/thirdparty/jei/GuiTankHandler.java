package com.tom.thirdparty.jei;

import java.awt.Rectangle;
import java.util.List;

import com.tom.api.gui.GuiTomsLib;

import mezz.jei.api.gui.IAdvancedGuiHandler;

public class GuiTankHandler implements IAdvancedGuiHandler<GuiTomsLib> {

	@Override
	public Class<GuiTomsLib> getGuiContainerClass() {
		return GuiTomsLib.class;
	}

	@Override
	public List<Rectangle> getGuiExtraAreas(GuiTomsLib guiContainer) {
		return null;
	}

	@Override
	public Object getIngredientUnderMouse(GuiTomsLib gui, int mouseX, int mouseY) {
		return gui.getFluidUnderMouse(mouseX, mouseY);
	}

}
