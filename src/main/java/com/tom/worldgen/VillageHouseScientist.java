package com.tom.worldgen;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockTrapDoor.DoorHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;

public class VillageHouseScientist extends Village {
	public static ResourceLocation loot = new ResourceLocation(Configs.ModidL, "chests/scientist");
	public static ResourceLocation loot2 = new ResourceLocation(Configs.ModidL, "chests/scientist2");
	public static ResourceLocation lootB = new ResourceLocation("chests/village_blacksmith");

	public VillageHouseScientist() {
	}

	public VillageHouseScientist(Start villagePiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, EnumFacing facing) {
		super(villagePiece, par2);
		this.setCoordBaseMode(facing);
		this.boundingBox = par4StructureBoundingBox;
	}

	public static class VillageManager implements IVillageCreationHandler {

		@Override
		public PieceWeight getVillagePieceWeight(Random random, int i) {
			return new StructureVillagePieces.PieceWeight(VillageHouseScientist.class, Config.scientistHouseWeight, i);
		}

		@Override
		public Class<?> getComponentClass() {
			return VillageHouseScientist.class;
		}

		@Override
		public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
			StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, -32 + 9, 0, 13, 11, 13, facing);
			return !Config.genScientistHouse || !(box != null && box.minY > 16) || (StructureComponent.findIntersecting(pieces, box) != null) ? null : new VillageHouseScientist(startPiece, p5, random, box, facing);
		}

	}

	private boolean placedChests = false;

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox boxIn) {
		try {
			if (placedChests)
				return true;
			if (this.averageGroundLvl < 0) {
				this.averageGroundLvl = this.getAverageGroundLevel(world, boxIn);

				if (this.averageGroundLvl < 0) { return true; }
				averageGroundLvl = Math.max(averageGroundLvl, 32 - 9);
				this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 9 - 1, 0);
			}
			StructureBoundingBox n = new StructureBoundingBox(boundingBox);
			int e = 2;
			n.minX += e;
			n.minZ += e;
			StructureBoundingBox boxO2 = boundingBox;
			boundingBox = n;
			StructureBoundingBox boxE = new StructureBoundingBox(n);
			boxE.minX -= 2;
			boxE.minZ -= 2;
			boxE.maxX += 2;
			boxE.maxZ += 2;
			boxE.minY -= 2;
			for (int x = 0;x < n.getXSize();x++) {
				for (int y = 0;y < n.getYSize();y++) {
					for (int z = 0;z < n.getZSize();z++) {
						Block s = getBlockStateFromPos(world, x, y, z, n).getBlock();
						if (s instanceof BlockChest) {
							return true;
						} else if (s == Blocks.BOOKSHELF) {
							return true;
						} else if (s == Blocks.CRAFTING_TABLE) { return true; }
					}
				}
			}
			IBlockState cobble = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
			IBlockState stairWoodN = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
			IBlockState stairWoodS = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
			IBlockState stairWoodE = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
			IBlockState planks = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
			IBlockState stairStone = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
			IBlockState fence = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
			IBlockState ladderS = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.SOUTH);
			IBlockState deskWR = (random.nextInt(20) > 15 ? Blocks.QUARTZ_STAIRS.getDefaultState() : Blocks.STONE_STAIRS.getDefaultState()).withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
			IBlockState deskW = Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
			IBlockState deskE = Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
			IBlockState stoneSlab = Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM).withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.STONE);
			IBlockState path1 = this.getBiomeSpecificBlockState(Blocks.GRAVEL.getDefaultState());
			IBlockState path = path1 == Blocks.GRAVEL.getDefaultState() ? Blocks.GRASS_PATH.getDefaultState() : path1;
			IBlockState grass = path1 == Blocks.GRAVEL.getDefaultState() ? this.getBiomeSpecificBlockState(Blocks.GRASS.getDefaultState()) : path1;
			this.fillWithBlocks(world, n, 1, 1, 1, 7, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(world, n, 0, 0, 0, 8, 0, 5, cobble, cobble, false);
			this.fillWithBlocks(world, n, 0, 5, 0, 8, 5, 5, cobble, cobble, false);
			this.fillWithBlocks(world, n, 0, 6, 1, 8, 6, 4, cobble, cobble, false);
			this.fillWithBlocks(world, n, 0, 7, 2, 8, 7, 3, cobble, cobble, false);

			for (int i = -1;i <= 2;++i) {
				for (int j = 0;j <= 8;++j) {
					this.setBlockState(world, stairWoodN, j, 6 + i, i, boxE);
					this.setBlockState(world, stairWoodS, j, 6 + i, 5 - i, boxE);
				}
			}

			this.fillWithBlocks(world, n, 0, 1, 0, 0, 1, 5, cobble, cobble, false);
			this.fillWithBlocks(world, n, 1, 1, 5, 8, 1, 5, cobble, cobble, false);
			this.fillWithBlocks(world, n, 8, 1, 0, 8, 1, 4, cobble, cobble, false);
			this.fillWithBlocks(world, n, 2, 1, 0, 7, 1, 0, cobble, cobble, false);
			this.fillWithBlocks(world, n, 0, 2, 0, 0, 4, 0, cobble, cobble, false);
			this.fillWithBlocks(world, n, 0, 2, 5, 0, 4, 5, cobble, cobble, false);
			this.fillWithBlocks(world, n, 8, 2, 5, 8, 4, 5, cobble, cobble, false);
			this.fillWithBlocks(world, n, 8, 2, 0, 8, 4, 0, cobble, cobble, false);
			this.fillWithBlocks(world, n, 0, 2, 1, 0, 4, 4, planks, planks, false);
			this.fillWithBlocks(world, n, 1, 2, 5, 7, 4, 5, planks, planks, false);
			this.fillWithBlocks(world, n, 8, 2, 1, 8, 4, 4, planks, planks, false);
			this.fillWithBlocks(world, n, 1, 2, 0, 7, 4, 0, planks, planks, false);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 2, 0, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 0, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 0, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 3, 0, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 5, 3, 0, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 6, 3, 0, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 3, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 3, 2, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 3, 3, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 2, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 3, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 3, 2, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 3, 3, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 5, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 3, 2, 5, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 5, n);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 5, n);
			this.fillWithBlocks(world, n, 1, 4, 1, 7, 4, 1, planks, planks, false);
			this.fillWithBlocks(world, n, 1, 4, 4, 7, 4, 4, planks, planks, false);
			this.fillWithBlocks(world, n, 1, 3, 4, 7, 3, 4, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
			this.setBlockState(world, planks, 7, 1, 4, n);
			this.setBlockState(world, stairWoodE, 7, 1, 3, n);
			this.setBlockState(world, stairWoodN, 6, 1, 4, n);
			this.setBlockState(world, stairWoodN, 5, 1, 4, n);
			this.setBlockState(world, stairWoodN, 4, 1, 4, n);
			this.setBlockState(world, stairWoodN, 3, 1, 4, n);
			this.setBlockState(world, fence, 6, 1, 3, n);
			this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 6, 2, 3, n);
			this.setBlockState(world, fence, 4, 1, 3, n);
			this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 4, 2, 3, n);
			this.setBlockState(world, Blocks.CRAFTING_TABLE.getDefaultState(), 7, 1, 1, n);
			this.placeTorch(world, EnumFacing.NORTH, 7, 3, 1, n);
			this.placeTorch(world, EnumFacing.NORTH, 1, 3, 1, n);
			placeChest(world, n, random, 1, 1, 4, getCoordBaseMode().getOpposite(), false);
			placeChest(world, n, random, 7, 1, 2, getCoordBaseMode().getAxisDirection() == AxisDirection.POSITIVE ? getCoordBaseMode().rotateY() : getCoordBaseMode().rotateYCCW(), false);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 1, 0, n);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 2, 0, n);
			this.createVillageDoor(world, n, random, 1, 1, 0, EnumFacing.NORTH);
			int lowest = 10;
			for (int l = -e;l < 6 + e;++l) {
				for (int k = -e;k < 9 + e;++k) {
					this.clearCurrentPositionBlocksUpwards(world, k, 9, l, boxE);
					this.replaceAirAndLiquidDownwards(world, cobble, k, -1, l, boxE);
				}
			}
			{
				int xMin = -2, zMin = -2, xMax = 10, zMax = 7, y = -1;
				for (int x = xMin;x <= xMax;++x) {
					for (int z = zMin;z <= zMax;++z) {
						if (x != xMin && x != xMin + 1 && x != xMax && x != xMax - 1 && z != zMin && z != zMin + 1 && z != zMax && z != zMax - 1) {
							this.setBlockState(world, cobble, x, y, z, boxE);
						} else {
							this.setBlockState(world, grass, x, y, z, boxE);
						}
					}
				}
			}
			if (this.getBlockStateFromPos(world, 1, 0, -1, boxE).getMaterial() == Material.AIR && this.getBlockStateFromPos(world, 1, -1, -1, boxE).getMaterial() != Material.AIR) {
				this.setBlockState(world, stairStone, 1, 0, -1, boxE);
				this.setBlockState(world, path, 1, -1, -2, boxE);

				if (this.getBlockStateFromPos(world, 1, -1, -1, boxE).getBlock() == Blocks.GRASS_PATH) {
					this.setBlockState(world, Blocks.GRASS.getDefaultState(), 1, -1, -1, boxE);
				}
			}
			// this.fillWithBlocks(world, boxE, -2, -1, -2, 12, -1, 7, grass,
			// cobble, false);
			// this.fillWithBlocks(world, boxE, -1, -1, 1, 11, -1, 8, grass,
			// cobble, false);
			StructureBoundingBox boxDown = new StructureBoundingBox(n);
			boxDown.offset(0, -n.getYSize() + 1, 0);
			int lowest4 = lowest + 4;
			boxDown.minY = getYWithOffset(-lowest4);

			StructureBoundingBox boxDown2 = new StructureBoundingBox(boxE);
			boxDown2.offset(0, -n.getYSize() - lowest + 4, 0);
			StructureBoundingBox boxO = boundingBox;
			boundingBox = boxDown2;
			this.fillWithBlocks(world, boxDown2, 0, 1, 0, 12, 8, 9, Blocks.COBBLESTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			int floorLvl = 5;
			this.fillWithBlocks(world, boxDown2, 0, floorLvl, 0, 12, floorLvl, 9, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.setBlockState(world, ladderS, 5, floorLvl + 2, 8, boxDown2);
			this.setBlockState(world, ladderS, 5, floorLvl + 1, 8, boxDown2);
			this.setBlockState(world, ladderS, 5, floorLvl, 8, boxDown2);
			this.setBlockState(world, ladderS, 5, floorLvl - 1, 8, boxDown2);
			this.setBlockState(world, ladderS, 5, floorLvl - 2, 8, boxDown2);
			this.setBlockState(world, ladderS, 5, floorLvl - 3, 8, boxDown2);
			if (!this.isZombieInfested) {
				this.generateDoor(world, boxDown2, random, 4, floorLvl + 1, 8, EnumFacing.WEST, this.biomeDoor());
				this.generateDoor(world, boxDown2, random, 6, floorLvl + 1, 8, EnumFacing.EAST, this.biomeDoor());
			}
			this.placeTorch(world, EnumFacing.SOUTH, 2, floorLvl + 2, 8, boxDown2);
			this.placeTorch(world, EnumFacing.SOUTH, 8, floorLvl + 2, 8, boxDown2);
			this.placeTorch(world, EnumFacing.NORTH, 2, floorLvl + 2, 1, boxDown2);
			this.placeTorch(world, EnumFacing.NORTH, 8, floorLvl + 2, 1, boxDown2);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, floorLvl + 1, 7, boxDown2);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, floorLvl + 2, 7, boxDown2);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 6, floorLvl + 1, 7, boxDown2);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 6, floorLvl + 2, 7, boxDown2);
			this.setBlockState(world, deskWR, 1, floorLvl + 1, 4, boxDown2);
			this.setBlockState(world, deskWR, 1, floorLvl + 1, 5, boxDown2);
			placeChest(world, boxDown2, random, 1, floorLvl + 1, 6, getCoordBaseMode().getAxisDirection() == AxisDirection.POSITIVE ? getCoordBaseMode().rotateYCCW() : getCoordBaseMode().rotateY(), false);
			this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 1, floorLvl + 2, 4, boxDown2);
			this.setBlockState(world, Blocks.LEVER.getDefaultState().withProperty(BlockLever.FACING, EnumOrientation.forFacings(EnumFacing.UP, EnumFacing.WEST)).withProperty(BlockLever.POWERED, random.nextBoolean()), 1, floorLvl + 2, 5, boxDown2);
			this.placeTorch(world, EnumFacing.SOUTH, 2, floorLvl - 2, 8, boxDown2);
			this.placeTorch(world, EnumFacing.SOUTH, 8, floorLvl - 2, 8, boxDown2);
			this.placeTorch(world, EnumFacing.NORTH, 2, floorLvl - 2, 1, boxDown2);
			this.placeTorch(world, EnumFacing.NORTH, 8, floorLvl - 2, 1, boxDown2);
			this.setBlockState(world, deskW, 1, floorLvl - 3, 4, boxDown2);
			this.setBlockState(world, deskW, 1, floorLvl - 3, 5, boxDown2);
			this.setBlockState(world, deskW, 1, floorLvl - 3, 6, boxDown2);
			this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 1, floorLvl - 2, 4, boxDown2);
			this.setBlockState(world, Blocks.LEVER.getDefaultState().withProperty(BlockLever.FACING, EnumOrientation.forFacings(EnumFacing.UP, EnumFacing.WEST)).withProperty(BlockLever.POWERED, random.nextBoolean()), 1, floorLvl - 2, 5, boxDown2);
			if (!this.isZombieInfested)
				this.setBlockState(world, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(BlockRedstoneTorch.FACING, EnumFacing.UP), 1, floorLvl - 2, 6, boxDown2);
			placeChest(world, boxDown2, random, 1, floorLvl - 3, 7, getCoordBaseMode().getAxisDirection() == AxisDirection.POSITIVE ? getCoordBaseMode().rotateYCCW() : getCoordBaseMode().rotateY(), true);
			placeChest(world, boxDown2, random, 1, floorLvl - 3, 3, getCoordBaseMode().getAxisDirection() == AxisDirection.POSITIVE ? getCoordBaseMode().rotateYCCW() : getCoordBaseMode().rotateY(), false);
			this.setBlockState(world, deskE, 9, floorLvl - 3, 4, boxDown2);
			this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 9, floorLvl - 2, 4, boxDown2);
			if (!this.isZombieInfested)
				this.setBlockState(world, Blocks.REDSTONE_TORCH.getDefaultState().withProperty(BlockRedstoneTorch.FACING, EnumFacing.EAST), 10, floorLvl - 3, 4, boxDown2);
			this.setBlockState(world, stoneSlab, 10, floorLvl - 3, 5, boxDown2);
			this.setBlockState(world, stoneSlab, 10, floorLvl - 3, 6, boxDown2);
			this.setBlockState(world, stoneSlab, 11, floorLvl - 3, 5, boxDown2);
			this.setBlockState(world, stoneSlab, 11, floorLvl - 3, 6, boxDown2);
			this.fillWithBlocks(world, boxDown2, 10, floorLvl - 2, 5, 11, floorLvl - 1, 6, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
			this.setBlockState(world, CoreInit.hardenedGlassPane.getDefaultState(), 10, floorLvl - 2, 4, boxDown2);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 10, floorLvl - 3, 3, boxDown2);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 10, floorLvl - 2, 3, boxDown2);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 10, floorLvl - 3, 7, boxDown2);
			this.setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH), 10, floorLvl - 2, 7, boxDown2);
			boundingBox = boxO;

			for (int l = 0;l < lowest4;l++) {
				this.setBlockState(world, ladderS, 3, -l, 4, boxDown);
				this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 2, -l, 4, boxDown);
				this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, -l, 4, boxDown);
				this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 3, -l, 5, boxDown);
				if (l % 6 == 3) {
					this.placeTorch(world, EnumFacing.UP, 3, -l, 3, boxDown);
					this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 3, -l, 2, boxDown);
					this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 2, -l, 3, boxDown);
					this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, -l, 3, boxDown);
				} else
					this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 3, -l, 3, boxDown);
			}
			this.placeTorch(world, EnumFacing.NORTH, 3, -lowest4 + 1, 3, boxDown);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, -lowest4 + 1, 3, boxDown);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 2, -lowest4 + 1, 3, boxDown);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, -lowest4 + 2, 3, boxDown);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 2, -lowest4 + 2, 3, boxDown);

			boxO = boundingBox;
			boundingBox = boxDown2;
			this.placeTorch(world, EnumFacing.SOUTH, 4, 7, 4, boxDown2);
			this.placeTorch(world, EnumFacing.SOUTH, 6, 7, 4, boxDown2);
			boundingBox = boxO;

			this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), 7, 8, 1, n);
			this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), 7, 9, 1, n);
			placeChest(world, n, random, 6, 0, 4, getCoordBaseMode().getOpposite(), true);
			placeChest(world, n, random, 4, 7, 3, getCoordBaseMode().getOpposite(), true);
			this.setBlockState(world, Blocks.TRAPDOOR.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.SOUTH).withProperty(BlockTrapDoor.HALF, DoorHalf.TOP).withProperty(BlockTrapDoor.OPEN, false), 3, 0, 4, boxE);
			this.spawnVillagers(world, n, 2, 1, 2, 1);
			boundingBox = boxDown2;
			this.spawnVillagers(world, boxDown2, 2, 2, 2, 2);
			boundingBox = boxO2;
			placedChests = true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	@Override
	protected VillagerProfession chooseForgeProfession(int count, net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession prof) {
		return count == 0 ? VillagerRegistry.instance().getRegistry().getValue(new ResourceLocation("minecraft:librarian")) : CoreInit.professionScientist;
	}

	protected boolean placeChest(World world, StructureBoundingBox box, Random rand, int x, int y, int z, EnumFacing f, boolean a) {
		int i1 = this.getXWithOffset(x, z);
		int j1 = this.getYWithOffset(y);
		int k1 = this.getZWithOffset(x, z);
		BlockPos pos = new BlockPos(i1, j1, k1);
		if (f.getAxis() == Axis.Y)
			f = EnumFacing.NORTH;
		if (box.isVecInside(pos) && (world.getBlockState(pos).getBlock() != Blocks.CHEST)) {
			world.setBlockState(pos, Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, f), 2);
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileEntityChest)
				((TileEntityChest) tile).setLootTable(a ? loot2 : rand.nextInt(10) < 2 ? lootB : loot, rand.nextLong());
			return true;
		} else
			return false;
	}

	@Override
	protected void writeStructureToNBT(NBTTagCompound tagCompound) {
		super.writeStructureToNBT(tagCompound);
		tagCompound.setBoolean("placedChests", placedChests);
	}

	@Override
	protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
		super.readStructureFromNBT(tagCompound, p_143011_2_);
		placedChests = tagCompound.getBoolean("placedChests");
	}
}
