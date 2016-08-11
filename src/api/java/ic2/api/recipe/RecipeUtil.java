package ic2.api.recipe;

import net.minecraft.item.ItemStack;

class RecipeUtil {
	public static ItemStack setImmutableSize(ItemStack stack, int size) {
		if (stack.stackSize != size) {
			stack = stack.copy();
			stack.stackSize = size;
		}

		return stack;
	}
}
