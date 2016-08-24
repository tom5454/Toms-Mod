package com.tom.api.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IIconRegisterRequired {
	@SideOnly(Side.CLIENT)
	void registerIcons();
}
