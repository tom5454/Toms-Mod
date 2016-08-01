package com.tom.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class GpuCable extends Block {

	protected GpuCable(Material arg0) {
		super(arg0);
	}
	public GpuCable(){
		this(Material.ROCK);
	}

}
