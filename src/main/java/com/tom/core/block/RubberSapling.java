package com.tom.core.block;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.tom.worldgen.WorldGenRubberTree;

public class RubberSapling extends BlockBush implements IGrowable
{
	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
	protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

	public RubberSapling()
	{
		this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, Integer.valueOf(0)));
		setSoundType(SoundType.PLANT);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return SAPLING_AABB;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (!worldIn.isRemote)
		{
			super.updateTick(worldIn, pos, state, rand);

			if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0)
			{
				this.grow(worldIn, pos, state, rand);
			}
		}
	}

	public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (state.getValue(STAGE).intValue() == 0)
		{
			worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
		}
		else
		{
			this.generateTree(worldIn, pos, state, rand);
		}
	}

	public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(worldIn, rand, pos)) return;
		WorldGenerator worldgenerator = new WorldGenRubberTree(true);
		int i = 0;
		int j = 0;
		boolean flag = false;

		//switch ((BlockPlanks.EnumType)state.getValue(TYPE))
		//{
		//    case SPRUCE:
		//        label114:
		//
		//        for (i = 0; i >= -1; --i)
		//        {
		//            for (j = 0; j >= -1; --j)
		//            {
		//                if (this.isTwoByTwoOfType(worldIn, pos, i, j, BlockPlanks.EnumType.SPRUCE))
		//                {
		//                    worldgenerator = new WorldGenMegaPineTree(false, rand.nextBoolean());
		//                    flag = true;
		//                    break label114;
		//                }
		//            }
		//        }
		//
		//        if (!flag)
		//        {
		//            j = 0;
		//            i = 0;
		//            worldgenerator = new WorldGenTaiga2(true);
		//        }
		//
		//        break;
		//    case BIRCH:
		//        worldgenerator = new WorldGenBirchTree(true, false);
		//        break;
		//    case JUNGLE:
		//        IBlockState iblockstate = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
		//        IBlockState iblockstate1 = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
		//        label269:
		//
		//        for (i = 0; i >= -1; --i)
		//        {
		//            for (j = 0; j >= -1; --j)
		//            {
		//                if (this.isTwoByTwoOfType(worldIn, pos, i, j, BlockPlanks.EnumType.JUNGLE))
		//                {
		//                    worldgenerator = new WorldGenMegaJungle(true, 10, 20, iblockstate, iblockstate1);
		//                    flag = true;
		//                    break label269;
		//                }
		//            }
		//        }
		//
		//        if (!flag)
		//        {
		//            j = 0;
		//            i = 0;
		//            worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
		//        }
		//
		//        break;
		//    case ACACIA:
		//        worldgenerator = new WorldGenSavannaTree(true);
		//        break;
		//    case DARK_OAK:
		//        label390:
		//
		//        for (i = 0; i >= -1; --i)
		//        {
		//            for (j = 0; j >= -1; --j)
		//            {
		//                if (this.isTwoByTwoOfType(worldIn, pos, i, j, BlockPlanks.EnumType.DARK_OAK))
		//                {
		//                    worldgenerator = new WorldGenCanopyTree(true);
		//                    flag = true;
		//                    break label390;
		//                }
		//            }
		//        }
		//
		//        if (!flag)
		//        {
		//            return;
		//        }
		//
		//    case OAK:
		//}

		IBlockState iblockstate2 = Blocks.AIR.getDefaultState();

		if (flag)
		{
			worldIn.setBlockState(pos.add(i, 0, j), iblockstate2, 4);
			worldIn.setBlockState(pos.add(i + 1, 0, j), iblockstate2, 4);
			worldIn.setBlockState(pos.add(i, 0, j + 1), iblockstate2, 4);
			worldIn.setBlockState(pos.add(i + 1, 0, j + 1), iblockstate2, 4);
		}
		else
		{
			worldIn.setBlockState(pos, iblockstate2, 4);
		}

		if (!worldgenerator.generate(worldIn, rand, pos.add(i, 0, j)))
		{
			if (flag)
			{
				worldIn.setBlockState(pos.add(i, 0, j), state, 4);
				worldIn.setBlockState(pos.add(i + 1, 0, j), state, 4);
				worldIn.setBlockState(pos.add(i, 0, j + 1), state, 4);
				worldIn.setBlockState(pos.add(i + 1, 0, j + 1), state, 4);
			}
			else
			{
				worldIn.setBlockState(pos, state, 4);
			}
		}
	}

	/**
	 * Whether this IGrowable can grow
	 */
	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
	{
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
	{
		return worldIn.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
	{
		this.grow(worldIn, pos, state, rand);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(STAGE, Integer.valueOf(meta & 8));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{
		int i = 0;;
		i = i | state.getValue(STAGE).intValue();
		return i;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {STAGE});
	}
}