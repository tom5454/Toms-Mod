package com.tom.factory.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class MultiblockEnergyPort extends Block {
	public MultiblockEnergyPort() {
		super(Material.IRON);
		this.setHardness(5);
		this.setResistance(10);
	}
}
