package com.tom.thirdparty.jei;

import java.awt.Rectangle;
import java.util.List;

import com.tom.core.tileentity.gui.GuiTomsMod;

import mezz.jei.api.gui.IAdvancedGuiHandler;

public class GuiTankHandler implements IAdvancedGuiHandler<GuiTomsMod> {

	@Override
	public Class<GuiTomsMod> getGuiContainerClass() {
		return GuiTomsMod.class;
	}

	@Override
	public List<Rectangle> getGuiExtraAreas(GuiTomsMod guiContainer) {
		return null;
	}

	@Override
	public Object getIngredientUnderMouse(GuiTomsMod gui, int mouseX, int mouseY) {
		return gui.getFluidUnderMouse(mouseX, mouseY);
	}

}
