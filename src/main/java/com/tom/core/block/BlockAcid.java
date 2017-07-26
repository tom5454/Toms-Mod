package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.BlockFluidClassic;

import com.tom.core.CoreInit;
import com.tom.core.DamageSourceTomsMod;

public class BlockAcid extends BlockFluidClassic {

	public BlockAcid() {
		super(CoreInit.ironChloride.get(), Material.WATER);
		setUnlocalizedName("tm.blockAcid");
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
		if (!(entityIn instanceof EntityItem))
			entityIn.attackEntityFrom(DamageSourceTomsMod.acid, 4F);
	}
}
