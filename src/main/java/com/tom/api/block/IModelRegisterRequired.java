package com.tom.api.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModelRegisterRequired {
	@SideOnly(Side.CLIENT)
	void registerModels();
}
