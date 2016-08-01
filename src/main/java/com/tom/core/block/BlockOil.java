package com.tom.core.block;

import com.tom.core.CoreInit;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;

public class BlockOil extends BlockFluidClassic {

	public BlockOil() {
		super(CoreInit.oil, Material.WATER);
		setUnlocalizedName("tm.blockOil");
	}

}
