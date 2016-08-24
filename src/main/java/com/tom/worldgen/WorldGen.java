package com.tom.worldgen;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.google.common.base.Predicate;

import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;

public class WorldGen implements IWorldGenerator {
	public static WorldGen instance;
	public static Logger log;
	public static final Predicate<World> OVERWORLD = new Predicate<World>() {

		@Override
		public boolean apply(World input) {
			return input.provider.isSurfaceWorld();
		}
	};
	public static final Predicate<World> NETHER = new Predicate<World>() {

		@Override
		public boolean apply(World input) {
			return input.provider.getDimensionType() == DimensionType.NETHER;
		}
	};
	public static final Predicate<World> END = new Predicate<World>() {

		@Override
		public boolean apply(World input) {
			return input.provider.getDimensionType() == DimensionType.THE_END;
		}
	};
	public static WorldGen init(){
		CoreInit.log.info("Loading World Generator");
		log = LogManager.getLogger(Configs.Modid + " World Generator");
		instance = new WorldGen();
		log.info("Loading successful");
		return instance;
	}
	private WorldGen(){}
	/**public void generateSurface(World world, java.util.Random rand, int chunkX, int chunkZ){
for(int i = 0; i < 6; i++){
int randPosX = chunkX + rand.nextInt(16);
int randPosY = rand.nextInt(121)+7;
int randPosZ = chunkZ + rand.nextInt(16);
(new WorldGenMinable(mcreator_titaniumOre.block.getDefaultState(), 6)).generate(world, rand, new BlockPos(randPosX, randPosY, randPosZ));
}
}*/
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		chunkX = chunkX * 16;
		chunkZ = chunkZ * 16;
		//int dim = world.provider.getDimension();
		for(Entry<Predicate<World>, List<OreGenEntry>> oreE : CoreInit.oreList.entrySet()){
			if(oreE.getKey().apply(world)){
				for(OreGenEntry ore : oreE.getValue()){
					boolean isPlatinum = ore.ore.getBlock() == CoreInit.orePlatinum;
					boolean isRedDiamond = ore.ore.getBlock() == CoreInit.oreRedDiamond;
					for(int i = 0; i < ore.maxAmount + 5; i++){
						int randPosX = chunkX + random.nextInt(16);
						int randPosY = random.nextInt(ore.yStart)+7;
						int randPosZ = chunkZ + random.nextInt(16);
						boolean generate = isPlatinum ? random.nextInt(10) > 4 : (isRedDiamond ? random.nextInt(10) > 2 : random.nextInt(24) > 15);
						boolean genSuccess = false;
						if(generate)genSuccess = (new WorldGenMinable(ore.ore, ore.maxAmount+2, ore.block)).generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
						//if(generate && (ore == CoreInit.orePlatinum || ore == CoreInit.oreRedDiamond))CoreInit.log.info("oreGen:"+ore.getUnlocalizedName()+" "+generate + " " + genSuccess + " " + randPosX + " " + randPosY + " " + randPosZ);
						if(!genSuccess && generate){
							randPosX = chunkX + random.nextInt(16);
							randPosY = random.nextInt(ore.yStart)+7;
							randPosZ = chunkZ + random.nextInt(16);
							callGenerate(new WorldGenMinable(ore.ore, ore.maxAmount+2, ore.block), world, random, new BlockPos(randPosX, randPosY, randPosZ));
						}
					}
				}
			}
		}
		if(OVERWORLD.apply(world)){
			for(int i = 0;i<20;i++){
				int chance = random.nextInt(100);
				if(chance % 4 == 0){
					int chance2 = random.nextInt(10);
					if(chance > 90 || chance < 5 || chance == 9 || (chance > 50 && chance < 80 && chance2 < 6)){
						if(Config.genRubberTrees){
							if(world.getWorldType() != WorldType.FLAT){
								if(chance2 != 1){
									int randPosX = chunkX + random.nextInt(16);
									int randPosY = random.nextInt(90);
									int randPosZ = chunkZ + random.nextInt(16);
									boolean s = callGenerate(new WorldGenRubberTree(false), world, random, new BlockPos(randPosX, randPosY, randPosZ));
									if(s){
										//CoreInit.log.info("oreGen: Rubber Tree " + randPosX + " " + randPosY + " " + randPosZ);
									}
								}
							}
						}
					}else if((chance == 20 || chance > 90 || chance < 10) && chance2 > 6){
						if(Config.genOilLakes){
							int chance3 = random.nextInt(10);
							if(chance3 == 2 || chance3 > 6){
								int randPosX = chunkX + random.nextInt(16);
								int randPosY = random.nextInt(60)+20;
								int randPosZ = chunkZ + random.nextInt(16);
								boolean s = callGenerate(new WorldGenLakes(CoreInit.oil.getBlock()), world, random, new BlockPos(randPosX, randPosY, randPosZ));
								if(s){
									//CoreInit.log.info("oreGen: Oil Lake " + randPosX + " " + randPosY + " " + randPosZ);
								}
							}
						}
					}
				}
				if(Config.enableBronkenTreeGen){
					if(world.getWorldType() != WorldType.FLAT){
						int randPosX = chunkX + random.nextInt(16);
						int randPosY = random.nextInt(90)+20;
						int randPosZ = chunkZ + random.nextInt(16);
						boolean s = callGenerate(new WorldGenBrokenTree(), world, random, new BlockPos(randPosX, randPosY, randPosZ));
						if(s){
							//CoreInit.log.info("oreGen: Broken Tree " + randPosX + " " + randPosY + " " + randPosZ);
						}
					}
				}
			}
		}
	}
	private static boolean callGenerate(WorldGenerator gen, World world, Random random, BlockPos pos){
		try{
			return gen.generate(world, random, pos);
		}catch(Exception e){
			log.warn("World Generating Failed! Generator Class: " + gen.getClass().getSimpleName() + " Stacktrace:");
			log.catching(e);
		}
		return false;
	}
	public static class OreGenEntry{
		public Predicate<IBlockState> block;
		public IBlockState ore;
		public Callable<IBlockState> oreInit;
		public int yStart, maxAmount;
		public OreGenEntry(Predicate<IBlockState> block, Callable<IBlockState> ore, int yStart,
				int maxAmount) {
			this.block = block;
			this.oreInit = ore;
			this.yStart = yStart;
			this.maxAmount = maxAmount;
		}
		public OreGenEntry(Callable<IBlockState> ore, int yStart,
				int maxAmount) {
			this(BlockMatcher.forBlock(Blocks.STONE), ore, yStart, maxAmount);
		}
	}
}
