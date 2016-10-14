package com.tom.worldgen;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import com.tom.core.CoreInit;

import com.tom.core.block.BlockRubberWood;
import com.tom.core.block.BlockRubberWood.WoodType;

public class WorldGenRubberTree extends WorldGenAbstractTree {

	public WorldGenRubberTree(boolean notify) {
		super(notify);
	}
	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		int i = rand.nextInt(3) + 4;
		int rubber = rand.nextInt(i);
		int rubberF = rand.nextInt(4);
		int topLeaves = rand.nextInt(2) + 2;
		boolean flag = true;
		IBlockState metaLeaves = CoreInit.rubberLeaves.getDefaultState(), metaWood = CoreInit.rubberWood.getDefaultState();
		if (position.getY() >= 1 && position.getY() + i + topLeaves + 1 <= worldIn.getHeight())
		{
			for (int j = position.getY(); j <= position.getY() + 1 + i; ++j)
			{
				int k = 1;

				if (j == position.getY())
				{
					k = 0;
				}

				if (j >= position.getY() + 1 + i - 2)
				{
					k = 2;
				}

				BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

				for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l)
				{
					for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1)
					{
						if (j >= 0 && j < worldIn.getHeight())
						{
							if (!this.isReplaceable(worldIn,blockpos$mutableblockpos.setPos(l, j, i1)))
							{
								flag = false;
							}
						}
						else
						{
							flag = false;
						}
					}
				}
			}
			if (!flag)
			{
				return false;
			}
			else
			{

				IBlockState state = worldIn.getBlockState(position.down());

				if (state.getBlock().canSustainPlant(state, worldIn, position.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)Blocks.SAPLING) && position.getY() < worldIn.getHeight() - i - 1)
				{
					this.setDirtAt(worldIn, position.down());
					int k2 = 3;
					int l2 = 0;

					for (int i3 = position.getY() - k2 + i; i3 <= position.getY() + i; ++i3)
					{
						int i4 = i3 - (position.getY() + i);
						int j1 = l2 + 1 - i4 / 2;

						for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1)
						{
							int l1 = k1 - position.getX();

							for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2)
							{
								int j2 = i2 - position.getZ();

								if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0)
								{
									BlockPos blockpos = new BlockPos(k1, i3, i2);
									state = worldIn.getBlockState(blockpos);

									if (state.getBlock().isAir(state, worldIn, blockpos) || state.getBlock().isLeaves(state, worldIn, blockpos) || state.getMaterial() == Material.VINE)
									{
										this.setBlockAndNotifyAdequately(worldIn, blockpos, metaLeaves);
									}
								}
							}
						}
					}

					for (int j3 = 0; j3 < i; ++j3)
					{
						BlockPos upN = position.up(j3);
						state = worldIn.getBlockState(upN);

						if (state.getBlock().isAir(state, worldIn, upN) || state.getBlock().isLeaves(state, worldIn, upN) || state.getMaterial() == Material.VINE)
						{
							genLog(worldIn, rubber, rubberF, j3, metaWood, position);
						}
					}
					BlockPos posTop = position.up(i+1);
					for (int j3 = 0; j3 < topLeaves; ++j3){
						BlockPos blockpos = posTop.up(j3);
						state = worldIn.getBlockState(blockpos);

						if (state.getBlock().isAir(state, worldIn, blockpos) || state.getBlock().isLeaves(state, worldIn, blockpos) || state.getMaterial() == Material.VINE)
						{
							this.setBlockAndNotifyAdequately(worldIn, blockpos, metaLeaves);
						}
					}
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return false;
	}
	private void genLog(World worldIn, int rubber, int rot, int j, IBlockState metaWood, BlockPos pos){
		this.setBlockAndNotifyAdequately(worldIn, pos.up(j), j == rubber ? metaWood.withProperty(BlockRubberWood.TYPE, WoodType.getNorm(rot)) : metaWood);
	}
}