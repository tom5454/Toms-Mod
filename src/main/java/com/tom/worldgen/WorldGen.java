package com.tom.worldgen;

import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenMinable;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.tom.config.Config;
import com.tom.core.CoreInit;

public class WorldGen implements IWorldGenerator {
	public static WorldGen instance;
	public static WorldGen init(){
		CoreInit.log.info("Loading World Generator");
		instance = new WorldGen();
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
		int dim = world.provider.getDimension();
		for(Entry<IBlockState, Entry<Integer, Entry<Integer, Integer>>> oreE : CoreInit.oreList.entrySet()){
			Entry<Integer, Entry<Integer, Integer>> genV = oreE.getValue();
			Entry<Integer, Integer> dimWeight = genV.getValue();
			int oreDim = dimWeight.getKey();
			if(dim == oreDim){
				IBlockState ore = oreE.getKey();
				int y = genV.getKey();
				int a = dimWeight.getValue() + 1;
				boolean isPlatinum = ore.getBlock() == CoreInit.orePlatinum;
				boolean isRedDiamond = ore.getBlock() == CoreInit.oreRedDiamond;
				for(int i = 0; i < a + 5; i++){
					int randPosX = chunkX + random.nextInt(16);
					int randPosY = random.nextInt(y)+7;
					int randPosZ = chunkZ + random.nextInt(16);
					boolean generate = isPlatinum ? random.nextInt(8) > 5 : (isRedDiamond ? random.nextInt(8) > 2 : random.nextInt(24) > 15);
					boolean genSuccess = false;
					if(generate)genSuccess = (new WorldGenMinable(ore, a+2)).generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
					//if(generate && (ore == CoreInit.orePlatinum || ore == CoreInit.oreRedDiamond))CoreInit.log.info("oreGen:"+ore.getUnlocalizedName()+" "+generate + " " + genSuccess + " " + randPosX + " " + randPosY + " " + randPosZ);
					if(!genSuccess && generate){
						randPosX = chunkX + random.nextInt(16);
						randPosY = random.nextInt(y)+7;
						randPosZ = chunkZ + random.nextInt(16);
						(new WorldGenMinable(ore, a)).generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
					}
				}
			}
		}
		if(dim == 0){
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
									boolean s = (new WorldGenRubberTree(false)).generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
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
								boolean s = (new WorldGenLakes(CoreInit.oil.getBlock())).generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
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
						boolean s = (new WorldGenBrokenTree()).generate(world, random, new BlockPos(randPosX, randPosY, randPosZ));
						if(s){
							//CoreInit.log.info("oreGen: Broken Tree " + randPosX + " " + randPosY + " " + randPosZ);
						}
					}
				}
			}
		}
	}
}
