package com.tom.recipes.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.FMLLog;

import com.tom.apis.RecipeData;

public class MultiblockCrafterRecipeHandler {
	public static Map<Block, List<RecipeData>> recipeList = new HashMap<Block, List<RecipeData>>();

	public static void add(Block block1, Block block2, Block blockOut) {
		if (block1 != null && block2 != null && blockOut != null) {
			if (!recipeList.containsKey(block1)) {
				addRecipe(block1, block2, blockOut);
			} else {
				List<RecipeData> current = recipeList.get(block1);
				boolean exitV = true;
				for (RecipeData c : current) {
					Block cur = c.block2;
					if (block2 == cur) {
						exitV = false;
						break;
					}
				}
				if (exitV) {
					addRecipe(block1, block2, blockOut);
				} else {
					FMLLog.bigWarning("Recipe Conflict! Ignore the adding!");
				}
			}
		} else {
			FMLLog.bigWarning("Arguments are null! Ignore the adding!");
		}
	}

	public static boolean craft(World world, Block block1, Block block2, EntityPlayer player, int x, int y, int z) {
		List<RecipeData> current = recipeList.get(block1);
		if (current != null) {
			boolean eV = false;
			RecipeData cOut = null;
			for (RecipeData c : current) {
				Block b = c.block2;
				if (b == block2) {
					eV = true;
					cOut = c;
					break;
				}
			}
			if (eV) {
				if (cOut.hasInv) {
					boolean inv = checkInv(player.inventory, cOut);
					if (inv) {
						craft(block1, block2, cOut.output, world, x, y, z);
					}
					return inv;
				} else {
					craft(block1, block2, cOut.output, world, x, y, z);
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private static void addRecipe(Block block1, Block block2, Block blockOut) {
		RecipeData current = new RecipeData(block2, blockOut);
		if (!recipeList.containsKey(block1)) {
			List<RecipeData> c = new ArrayList<RecipeData>();
			c.add(current);
			recipeList.put(block1, c);
		} else {
			List<RecipeData> c = recipeList.get(block1);
			c.add(current);
			recipeList.remove(block1);
			recipeList.put(block1, c);
		}
	}

	private static void addRecipe(Block block1, Block block2, Block blockOut, Item invItem1) {
		RecipeData current = new RecipeData(block2, blockOut, invItem1);
		if (!recipeList.containsKey(block1)) {
			List<RecipeData> c = new ArrayList<RecipeData>();
			c.add(current);
			recipeList.put(block1, c);
		} else {
			List<RecipeData> c = recipeList.get(block1);
			c.add(current);
			recipeList.remove(block1);
			recipeList.put(block1, c);
		}
	}

	public static void add(Block block1, Block block2, Block blockOut, Item invItem1) {
		if (block1 != null && block2 != null && blockOut != null) {
			if (!recipeList.containsKey(block1)) {
				addRecipe(block1, block2, blockOut, invItem1);
			} else {
				List<RecipeData> current = recipeList.get(block1);
				boolean exitV = true;
				for (RecipeData c : current) {
					Block cur = c.block2;
					if (block2 == cur) {
						exitV = false;
						break;
					}
				}
				if (exitV) {
					addRecipe(block1, block2, blockOut, invItem1);
				} else {
					System.err.println("Recipe Conflict! Ignore the adding!");
				}
			}
		} else {
			System.err.println("Arguments are null! Ignore the adding!");
		}
	}

	public static void add(Block block1, RecipeData input) {
		if (block1 != null && input != null && input.block2 != null && input.output != null) {
			if (!recipeList.containsKey(block1)) {
				addRecipe(block1, input);
			} else {
				List<RecipeData> current = recipeList.get(block1);
				boolean exitV = true;
				Block block2 = input.block2;
				for (RecipeData c : current) {
					Block cur = c.block2;
					if (block2 == cur) {
						exitV = false;
						break;
					}
				}
				if (exitV) {
					addRecipe(block1, input);
				} else {
					System.err.println("Recipe Conflict! Ignore the adding!");
				}
			}
		} else {
			System.err.println("Arguments are null! Ignore the adding!");
		}
	}

	private static void addRecipe(Block block1, RecipeData input) {
		RecipeData current = input;
		if (!recipeList.containsKey(block1)) {
			List<RecipeData> c = new ArrayList<RecipeData>();
			c.add(current);
			recipeList.put(block1, c);
		} else {
			List<RecipeData> c = recipeList.get(block1);
			c.add(current);
			recipeList.remove(block1);
			recipeList.put(block1, c);
		}
	}

	public static RecipeData newRecipeList(Block block2, Block blockOut) {
		return new RecipeData(block2, blockOut);
	}

	private static boolean checkInv(InventoryPlayer inv, RecipeData list) {
		return false;
	}

	private static void craft(Block block1, Block block2, Block blockOut, World world, int x, int y, int z) {
		y++;
		world.setBlockState(new BlockPos(x + 1, y, z + 1), Blocks.AIR.getDefaultState());
		world.setBlockState(new BlockPos(x + 1, y, z - 1), Blocks.AIR.getDefaultState());
		world.setBlockState(new BlockPos(x - 1, y, z + 1), Blocks.AIR.getDefaultState());
		world.setBlockState(new BlockPos(x - 1, y, z - 1), Blocks.AIR.getDefaultState());
		y++;
		world.setBlockState(new BlockPos(x + 1, y, z + 1), Blocks.AIR.getDefaultState());
		world.setBlockState(new BlockPos(x + 1, y, z - 1), Blocks.AIR.getDefaultState());
		world.setBlockState(new BlockPos(x - 1, y, z + 1), Blocks.AIR.getDefaultState());
		world.setBlockState(new BlockPos(x - 1, y, z - 1), Blocks.AIR.getDefaultState());
		y++;
		world.setBlockState(new BlockPos(x, y, z), blockOut.getDefaultState());
	}
}
