package com.tom.thirdparty.jei;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import com.tom.core.tileentity.gui.GuiConfigurator;

import mezz.jei.api.gui.IAdvancedGuiHandler;

public class ConfiguratorGuiHandler implements IAdvancedGuiHandler<GuiConfigurator> {

	@Override
	public Class<GuiConfigurator> getGuiContainerClass() {
		return GuiConfigurator.class;
	}

	@Override
	public List<Rectangle> getGuiExtraAreas(GuiConfigurator g) {
		return Collections.singletonList(g.getConfigGuiBB());
	}

	@Override
	public Object getIngredientUnderMouse(GuiConfigurator guiContainer, int mouseX, int mouseY) {
		return null;
	}

}
