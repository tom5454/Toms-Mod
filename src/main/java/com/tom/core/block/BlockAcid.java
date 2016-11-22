package com.tom.core.block;

import net.minecraft.block.material.Material;

import net.minecraftforge.fluids.BlockFluidClassic;

import com.tom.core.CoreInit;

public class BlockAcid extends BlockFluidClassic {

	public BlockAcid() {
		super(CoreInit.sulfuricAcid, Material.WATER);
		setUnlocalizedName("tm.blockAcid");
	}

}
