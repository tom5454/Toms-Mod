package com.tom.recipes;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import com.tom.api.item.IWrench;

public class WrenchShapelessCraftingRecipe implements IRecipe {
	@Nonnull
	protected ItemStack output = ItemStack.EMPTY;
	protected NonNullList<Object> input = NonNullList.create();

	public WrenchShapelessCraftingRecipe(Block result, Object... recipe) {
		this(new ItemStack(result), recipe);
	}

	public WrenchShapelessCraftingRecipe(Item result, Object... recipe) {
		this(new ItemStack(result), recipe);
	}

	public WrenchShapelessCraftingRecipe(@Nonnull ItemStack result, Object... recipe) {
		output = result.copy();
		for (Object in : recipe) {
			if (in instanceof ItemStack) {
				input.add(((ItemStack) in).copy());
			} else if (in instanceof Item) {
				input.add(new ItemStack((Item) in));
			} else if (in instanceof Block) {
				input.add(new ItemStack((Block) in));
			} else if (in instanceof String) {
				input.add(OreDictionary.getOres((String) in));
			} else {
				String ret = "Invalid shapeless ore recipe: ";
				for (Object tmp : recipe) {
					ret += tmp + ", ";
				}
				ret += output;
				throw new RuntimeException(ret);
			}
		}
		input.add(OreDictionary.getOres("tomsmodwrench"));
	}

	@Override
	@Nonnull
	public ItemStack getRecipeOutput() {
		return output;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
		return output.copy();
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean matches(InventoryCrafting var1, World world) {
		NonNullList<Object> required = NonNullList.create();
		required.addAll(input);

		for (int x = 0;x < var1.getSizeInventory();x++) {
			ItemStack slot = var1.getStackInSlot(x);

			if (!slot.isEmpty()) {
				boolean inRecipe = false;
				Iterator<Object> req = required.iterator();

				while (req.hasNext()) {
					boolean match = false;

					Object next = req.next();

					if (next instanceof ItemStack) {
						match = OreDictionary.itemMatches((ItemStack) next, slot, false);
					} else if (next instanceof List) {
						Iterator<ItemStack> itr = ((List<ItemStack>) next).iterator();
						while (itr.hasNext() && !match) {
							match = OreDictionary.itemMatches(itr.next(), slot, false);
						}
					}

					if (match) {
						inRecipe = true;
						required.remove(next);
						break;
					}
				}

				if (!inRecipe) { return false; }
			}
		}

		return required.isEmpty();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0;i < nonnulllist.size();++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack.getItem() instanceof IWrench) {
				nonnulllist.set(i, itemstack.copy());
			} else {
				nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
			}
		}

		return nonnulllist;
	}

	/**
	 * Returns the input for this recipe, any mod accessing this value should
	 * never manipulate the values in this array as it will effect the recipe
	 * itself.
	 *
	 * @return The recipes input vales.
	 */
	public NonNullList<Object> getInput() {
		return this.input;
	}

	@Override
	public IRecipe setRegistryName(ResourceLocation name) {
		return null;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return null;
	}

	@Override
	public Class<IRecipe> getRegistryType() {
		return null;
	}

	@Override
	public boolean canFit(int width, int height) {
		return (width == 2 && height == 1) || (height == 2 && width == 1);
	}
}
