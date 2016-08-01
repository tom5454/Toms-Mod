package com.tom.energy.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class FusionCore extends Block {

	protected FusionCore(Material arg0) {
		super(arg0);
	}
	public FusionCore(){
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

}
