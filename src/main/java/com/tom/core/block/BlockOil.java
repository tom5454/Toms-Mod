package com.tom.core.block;

import net.minecraft.block.material.Material;

import net.minecraftforge.fluids.BlockFluidClassic;

import com.tom.core.CoreInit;

public class BlockOil extends BlockFluidClassic {

	public BlockOil() {
		super(CoreInit.oil.get(), Material.WATER);
		setUnlocalizedName("tm.blockOil");
	}

}
