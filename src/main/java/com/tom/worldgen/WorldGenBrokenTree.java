package com.tom.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenBrokenTree extends WorldGenerator {

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		boolean flag = true;
		EnumFacing f = EnumFacing.VALUES[rand.nextInt(4) + 2];
		int l = rand.nextInt(3) + 2;
		if (isReplaceable(worldIn, position)) {
			for (int i = 0;i < l;i++) {
				if (!isReplaceable(worldIn, position.offset(f, i + 2))) {
					flag = false;
					break;
				}
				IBlockState state = worldIn.getBlockState(position.offset(f, i + 2).down());
				if (!(state.getBlock().canSustainPlant(state, worldIn, position.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling) Blocks.SAPLING) && position.getY() < worldIn.getHeight())) {
					flag = false;
					break;
				}
			}
			if (flag) {
				IBlockState state = worldIn.getBlockState(position.down());

				if (state.getBlock().canSustainPlant(state, worldIn, position.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling) Blocks.SAPLING) && position.getY() < worldIn.getHeight()) {
					state = worldIn.getBlockState(position);
					this.setDirtAt(worldIn, position.down());
					if (state.getBlock().isAir(state, worldIn, position) || state.getBlock().isLeaves(state, worldIn, position) || state.getMaterial() == Material.VINE) {
						this.setBlockAndNotifyAdequately(worldIn, position, Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Y));
						if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(-1, 0, 0))) {
							this.addVine(worldIn, position.add(-1, 0, 0), BlockVine.EAST);
						}

						if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(1, 0, 0))) {
							this.addVine(worldIn, position.add(1, 0, 0), BlockVine.WEST);
						}

						if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, 0, -1))) {
							this.addVine(worldIn, position.add(0, 0, -1), BlockVine.SOUTH);
						}

						if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, 0, 1))) {
							this.addVine(worldIn, position.add(0, 0, 1), BlockVine.NORTH);
						}
						for (int i = 0;i < l;i++) {
							BlockPos pos = position.offset(f, i + 2);
							if (isReplaceable(worldIn, pos)) {
								this.setBlockAndNotifyAdequately(worldIn, pos, Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.fromFacingAxis(f.getAxis())));
								if (rand.nextInt(3) > 0 && worldIn.isAirBlock(pos.add(-1, 0, 0))) {
									this.addVine(worldIn, pos.add(-1, 0, 0), BlockVine.EAST);
								}

								if (rand.nextInt(3) > 0 && worldIn.isAirBlock(pos.add(1, 0, 0))) {
									this.addVine(worldIn, pos.add(1, 0, 0), BlockVine.WEST);
								}

								if (rand.nextInt(3) > 0 && worldIn.isAirBlock(pos.add(0, 0, -1))) {
									this.addVine(worldIn, pos.add(0, 0, -1), BlockVine.SOUTH);
								}

								if (rand.nextInt(3) > 0 && worldIn.isAirBlock(pos.add(0, 0, 1))) {
									this.addVine(worldIn, pos.add(0, 0, 1), BlockVine.NORTH);
								}
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * sets dirt at a specific location if it isn't already dirt
	 */
	protected void setDirtAt(World worldIn, BlockPos pos) {
		if (worldIn.getBlockState(pos).getBlock() != Blocks.DIRT) {
			this.setBlockAndNotifyAdequately(worldIn, pos, Blocks.DIRT.getDefaultState());
		}
	}

	public boolean isReplaceable(World world, BlockPos pos) {
		net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
		return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos) || state.getBlock().isWood(world, pos) || canGrowInto(state.getBlock());
	}

	/**
	 * returns whether or not a tree can grow into a block For example, a tree
	 * will not grow into stone
	 */
	protected boolean canGrowInto(Block blockType) {
		Material material = blockType.getDefaultState().getMaterial();
		return material == Material.AIR || material == Material.LEAVES || blockType == Blocks.GRASS || blockType == Blocks.DIRT || blockType == Blocks.LOG || blockType == Blocks.LOG2 || blockType == Blocks.SAPLING || blockType == Blocks.VINE;
	}

	private void addVine(World worldIn, BlockPos pos, PropertyBool prop) {
		this.setBlockAndNotifyAdequately(worldIn, pos, Blocks.VINE.getDefaultState().withProperty(prop, Boolean.valueOf(true)));
	}
}
