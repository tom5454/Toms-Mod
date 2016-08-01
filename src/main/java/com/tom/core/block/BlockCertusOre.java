package com.tom.core.block;

import java.util.Random;

import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.Type;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockCertusOre extends Block {

	public BlockCertusOre() {
		super(Material.ROCK);
		setHardness(6.1F);
		setResistance(21.1F);
	}
	@Override
	public int damageDropped(IBlockState state)
	{
		return TMResource.CERTUS_QUARTZ.getStackNormal(Type.GEM).getMetadata();
	}
	@Override
	public int quantityDroppedWithBonus(int fortune, Random random)
	{
		if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped(this.getBlockState().getValidStates().iterator().next(), random, fortune))
		{
			int i = random.nextInt(fortune + 2) - 1;

			if (i < 0)
			{
				i = 0;
			}

			return this.quantityDropped(random) * (i + 1);
		}
		else
		{
			return this.quantityDropped(random);
		}
	}
	@Override
	public int quantityDropped(Random par1Random){
		return 1;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random par2Random, int par3){
		return TMResource.CERTUS_QUARTZ.getStackNormal(Type.GEM).getItem();
	}
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		return new ItemStack(CoreInit.oreCertusQuartz);
	}
}
