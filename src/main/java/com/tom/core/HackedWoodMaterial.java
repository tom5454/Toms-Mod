package com.tom.core;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class HackedWoodMaterial extends Material {

	public HackedWoodMaterial() {
		super(MapColor.WOOD);
		setRequiresTool();
		setBurning();
	}

}
