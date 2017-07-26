package com.tom.worldgen;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.tom.config.Config;
import com.tom.core.CoreInit;

public class WorldGen implements IWorldGenerator {
	public static WorldGen instance;
	public static Logger log;
	public static final Predicate<World> OVERWORLD = world -> !Config.notOverworld.contains(world.provider.getDimension());
	public static final Predicate<World> NETHER = world -> !Config.notNether.contains(world.provider.getDimension());
	public static final Predicate<World> END = world -> !Config.notEnd.contains(world.provider.getDimension());
	public static final Predicate<IBlockState> STONE = BlockMatcher.forBlock(Blocks.STONE)::apply;
	public static final Predicate<IBlockState> SAND = BlockMatcher.forBlock(Blocks.SAND)::apply;
	public static final Predicate<IBlockState> NETHERRACK = BlockMatcher.forBlock(Blocks.NETHERRACK)::apply;
	public static final Predicate<IBlockState> END_STONE = BlockMatcher.forBlock(Blocks.END_STONE)::apply;

	public static WorldGen init() {
		CoreInit.log.info("Loading World Generator");
		log = LogManager.getLogger("Tom's Mod World Generator");
		instance = new WorldGen();
		log.info("Loading successful");
		return instance;
	}

	private WorldGen() {
	}

	/**
	 * public void generateSurface(World world, java.util.Random rand, int
	 * chunkX, int chunkZ){ for(int i = 0; i < 6; i++){ int randPosX = chunkX +
	 * rand.nextInt(16); int randPosY = rand.nextInt(121)+7; int randPosZ =
	 * chunkZ + rand.nextInt(16); (new
	 * WorldGenMinable(mcreator_titaniumOre.block.getDefaultState(),
	 * 6)).generate(world, rand, new BlockPos(randPosX, randPosY, randPosZ)); }
	 * }
	 */
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		chunkX = chunkX * 16;
		chunkZ = chunkZ * 16;
		Biome biome = world.getBiome(new BlockPos(chunkX, 0, chunkZ));
		// int dim = world.provider.getDimension();
		for (Entry<Predicate<World>, List<OreGenEntry>> oreE : CoreInit.oreList.entrySet()) {
			if (oreE.getKey().test(world)) {
				for (OreGenEntry ore : oreE.getValue()) {
					boolean isPlatinum = ore.ore.getBlock() == CoreInit.orePlatinum;
					boolean isRedDiamond = ore.ore.getBlock() == CoreInit.oreRedDiamond;
					for (int i = 0;i < ore.maxAmount + 5;i++) {
						int randPosX = chunkX + random.nextInt(16);
						int randPosY = random.nextInt(ore.ySize) + ore.yStart;
						int randPosZ = chunkZ + random.nextInt(16);
						boolean generate = isPlatinum ? random.nextInt(10) > 4 : (isRedDiamond ? random.nextInt(10) > 2 : random.nextInt(24) > 15);
						boolean genSuccess = false;
						if (generate)
							genSuccess = (new WorldGenMinable(ore.ore, ore.maxAmount + 2, in -> ore.block.test(in))).generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
						// if(generate && (ore == CoreInit.orePlatinum || ore ==
						// CoreInit.oreRedDiamond))CoreInit.log.info("oreGen:"+ore.getUnlocalizedName()+"
						// "+generate + " " + genSuccess + " " + randPosX + " "
						// + randPosY + " " + randPosZ);
						if (!genSuccess && generate) {
							randPosX = chunkX + random.nextInt(16);
							randPosY = random.nextInt(ore.ySize) + ore.yStart;
							randPosZ = chunkZ + random.nextInt(16);
							callGenerate(new WorldGenMinable(ore.ore, ore.veinSize, in -> ore.block.test(in)), world, random, new BlockPos(randPosX, randPosY, randPosZ));
						}
					}
				}
			}
		}
		if (OVERWORLD.test(world)) {
			for (int i = 0;i < 20;i++) {
				int chance = random.nextInt(100);
				if (chance % 4 == 0) {
					int chance2 = random.nextInt(10);
					if (chance > 90 || chance < 5 || chance == 9 || (chance > 50 && chance < 80 && chance2 < 6)) {
						if (Config.enableFeature(Config.RUBBER_TREES_FEATURE, world)) {
							if (world.getWorldType() != WorldType.FLAT) {
								if (chance2 != 1) {
									int randPosX = chunkX + random.nextInt(16);
									int randPosY = random.nextInt(90);
									int randPosZ = chunkZ + random.nextInt(16);
									boolean s = callGenerate(new WorldGenRubberTree(false), world, random, new BlockPos(randPosX, randPosY, randPosZ));
									if (s) {
										// CoreInit.log.info("oreGen: Rubber
										// Tree " + randPosX + " " + randPosY +
										// " " + randPosZ);
									}
								}
							}
						}
					} else if (((chance == 20 || chance > 90 || chance < 10) && chance2 > 6) || (SAND.test(biome.topBlock) && ((chance > 80 || chance < 22) && chance2 > 4))) {
						if (Config.enableFeature(Config.OIL_LAKE, world)) {
							int chance3 = random.nextInt(10);
							if (chance3 == 2 || chance3 > 6) {
								int randPosX = chunkX + random.nextInt(16);
								int randPosY = SAND.test(biome.topBlock) ? random.nextInt(55) + 20 : random.nextInt(60) + 20;
								int randPosZ = chunkZ + random.nextInt(16);
								boolean s = callGenerate(new WorldGenOilLake(), world, random, new BlockPos(randPosX, randPosY, randPosZ));
								if (s) {
									// CoreInit.log.info("oreGen: Oil Lake " +
									// randPosX + " " + randPosY + " " +
									// randPosZ);
								}
							}
						}
					}
				}
				if (Config.enableFeature(Config.BROKEN_TREE, world)) {
					if (world.getWorldType() != WorldType.FLAT) {
						int randPosX = chunkX + random.nextInt(16);
						int randPosY = random.nextInt(90) + 20;
						int randPosZ = chunkZ + random.nextInt(16);
						boolean s = callGenerate(new WorldGenBrokenTree(), world, random, new BlockPos(randPosX, randPosY, randPosZ));
						if (s) {
							// CoreInit.log.info("oreGen: Broken Tree " +
							// randPosX + " " + randPosY + " " + randPosZ);
						}
					}
				}
			}
		}
	}

	private static boolean callGenerate(WorldGenerator gen, World world, Random random, BlockPos pos) {
		try {
			return gen.generate(world, random, pos);
		} catch (Exception e) {
			log.warn("World Generating Failed! Generator Class: " + gen.getClass().getSimpleName() + " Stacktrace:");
			log.catching(e);
		}
		return false;
	}

	public static class OreGenEntry {
		public Predicate<IBlockState> block;
		public Predicate<World> world;
		public IBlockState ore;
		public Supplier<IBlockState> oreInit;
		public String name = "unknown";
		public int yStart, ySize, maxAmount, veinSize;

		public OreGenEntry(Predicate<IBlockState> block, Predicate<World> world, Supplier<IBlockState> ore, int yStart, int maxAmount, int yEnd, int veinSize) {
			this.block = block;
			this.oreInit = ore;
			this.yStart = yStart;
			this.maxAmount = maxAmount;
			this.ySize = yEnd - yStart;
			this.veinSize = veinSize;
			this.world = world;
		}

		public OreGenEntry(Predicate<World> world, Supplier<IBlockState> ore, int yStart, int maxAmount, int yEnd, int veinSize) {
			this(BlockMatcher.forBlock(Blocks.STONE)::apply, world, ore, yStart, maxAmount, yEnd, veinSize);
		}
	}
}
