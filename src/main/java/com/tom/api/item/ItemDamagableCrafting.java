package com.tom.api.item;

import java.util.List;
import java.util.Random;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.core.CoreInit;

public abstract class ItemDamagableCrafting extends Item implements ICustomCraftingHandler, ICustomCraftingHandlerAdv {
	protected boolean refillable = false;

	public ItemDamagableCrafting() {
		setMaxStackSize(1);
		setCreativeTab(CoreInit.tabTomsModItems);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return stack.hasTagCompound() ? refillable ? true : getItemDamage(stack) < getDurability(stack) : true;
	}

	public abstract int getDurability(ItemStack stack);

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		if (hasContainerItem(itemStack)) {
			itemStack = itemStack.copy();
			if (itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("damaged")) {
				itemStack.getTagCompound().removeTag("damaged");
			} else {
				setItemDamage(itemStack, getItemDamage(itemStack) + 1);
			}
			return itemStack;
		}
		return getDefaultContainerItem();
	}

	public ItemStack getDefaultContainerItem() {
		return ItemStack.EMPTY;
	}

	/**
	 * Determines if the durability bar should be rendered for this item.
	 * Defaults to vanilla stack.isDamaged behavior. But modders can use this
	 * for any data they wish.
	 *
	 * @param stack
	 *            The current Item Stack
	 * @return True if it should render the 'durability' bar.
	 */
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getItemDamage(stack) > 0;
	}

	/**
	 * Queries the percentage of the 'Durability' bar that should be drawn.
	 *
	 * @param stack
	 *            The current ItemStack
	 * @return 1.0 for 100% 0 for 0%
	 */
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		int d = getDurability(stack);
		return stack.hasTagCompound() ? 1 - ((d - getItemDamage(stack) * 1D) / d) : 0;
	}

	/**
	 * Returns the packed int RGB value used to render the durability bar in the
	 * GUI. Defaults to a value based on the hue scaled as the damage decreases,
	 * but can be overriden.
	 *
	 * @param stack
	 *            Stack to get durability from
	 * @return A packed RGB value for the durability colour (0x00RRGGBB)
	 */
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1 - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
	}

	@Override
	public ItemDamagableCrafting setUnlocalizedName(String unlocalizedName) {
		super.setUnlocalizedName(unlocalizedName);
		return this;
	}

	@Override
	public final void onUsing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory, ItemStack stack) {
		if (!onUsingInTable(crafter, returnStack, crafingTableInventory, stack)) {
			setItemDamage(stack, getItemDamage(stack) + 1);
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setBoolean("damaged", true);
		}
	}

	@Override
	public void onCrafing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory) {
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int damage = getItemDamage(stack), maxDamage = getDurability(stack);
		tooltip.add(I18n.format("tomsMod.tooltip.durability") + ": " + (maxDamage - damage) + "/" + maxDamage);
	}

	@Override
	public void onCrafingAdv(String player, ItemStack crafting, ItemStackAccess second, IInventory craftMatrix) {
	}

	@Override
	public final void onUsingAdv(String player, ItemStack crafting, ItemStackAccess second, IInventory craftMatrix, ItemStack s) throws CraftingErrorException {
		if (!onUsingInTable(player, crafting, second, craftMatrix, s)) {
			ItemStack stack = second.getStack();
			if (stack != null && stack.getItem() == this) {
				int damage = getItemDamage(stack);
				int dmg = getItemDamage(s);
				int max = getDurability(s);
				if (dmg >= max) {
					if (damage > 1 && dmg > max)
						throw new CraftingErrorException(new TextComponentTranslation("tomsMod.notEnoughDurrability", new TextComponentTranslation(getUnlocalizedName(stack) + ".name")));
					second.setStack(ItemStack.EMPTY);
				} else {
					setItemDamage(stack, dmg);
					if (!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());
					stack.getTagCompound().setBoolean("damaged", true);
				}
			}
		}
	}

	public ItemDamagableCrafting setRefillable(boolean refillable) {
		this.refillable = refillable;
		return this;
	}

	public boolean isRefillable() {
		return refillable;
	}

	public void addToList(List<ItemStack> stack) {
		int d = getDurability(new ItemStack(this));
		for (int i = 0;i < d;i++) {
			ItemStack s = new ItemStack(this);
			setItemDamage(s, i);
			stack.add(s);
		}
	}

	public ItemStack getDamaged() {
		if (!refillable)
			return ItemStack.EMPTY;
		ItemStack s = new ItemStack(this);
		setItemDamage(s, getDurability(s));
		return s;
	}

	/**
	 * Damages the item in the ItemStack
	 */
	public void damageItem(ItemStack stack, int amount, EntityLivingBase entityIn) {
		if (!(entityIn instanceof EntityPlayer) || !((EntityPlayer) entityIn).capabilities.isCreativeMode) {
			if (this.attemptDamageItem(stack, amount, entityIn.getRNG())) {
				entityIn.renderBrokenItemStack(stack);
				stack.shrink(1);

				if (entityIn instanceof EntityPlayer) {
					EntityPlayer entityplayer = (EntityPlayer) entityIn;
					entityplayer.addStat(StatList.getObjectBreakStats(this));
				}

				setDamage(stack, 0);
			}
		}
	}

	/**
	 * Attempts to damage the ItemStack with par1 amount of damage, If the
	 * ItemStack has the Unbreaking enchantment there is a chance for each point
	 * of damage to be negated. Returns true if it takes more damage than
	 * getMaxDamage(). Returns false otherwise or if the ItemStack can't be
	 * damaged or if all points of damage are negated.
	 */
	public boolean attemptDamageItem(ItemStack stack, int amount, Random rand) {
		if (amount > 0) {
			int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
			int j = 0;

			for (int k = 0;i > 0 && k < amount;++k) {
				if (EnchantmentDurability.negateDamage(stack, i, rand)) {
					++j;
				}
			}

			amount -= j;

			if (amount <= 0) { return false; }
		}

		setItemDamage(stack, getItemDamage(stack) + amount); // Redirect through
		// Item's
		// callback if
		// applicable.
		return getItemDamage(stack) > getDurability(stack);
	}

	public abstract int getItemDamage(ItemStack stack);

	public abstract boolean setItemDamage(ItemStack stack, int damage);

	public boolean onUsingInTable(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory, ItemStack stack) {
		return false;
	}

	public boolean onUsingInTable(String crafter, ItemStack returnStack, ItemStackAccess second, IInventory crafingTableInventory, ItemStack stack) throws CraftingErrorException {
		return false;
	}

	public static class ItemDamagableCraftingNormal extends ItemDamagableCrafting {
		protected final int durability;
		protected final ItemStack containerItem;

		public ItemDamagableCraftingNormal(int durability) {
			this(durability, ItemStack.EMPTY);
		}

		public ItemDamagableCraftingNormal(int durability, ItemStack containerItem) {
			this.durability = durability;
			this.containerItem = containerItem;
		}

		@Override
		public int getItemDamage(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getInteger("damage") : 0;
		}

		@Override
		public boolean setItemDamage(ItemStack stack, int damage) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setInteger("damage", damage);
			return hasContainerItem(stack);
		}

		@Override
		public int getDurability(ItemStack stack) {
			return durability;
		}

		@Override
		public ItemStack getDefaultContainerItem() {
			return containerItem.copy();
		}
	}
}
