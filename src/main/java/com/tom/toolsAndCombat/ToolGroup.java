package com.tom.toolsAndCombat;

import static com.tom.api.recipes.RecipeHelper.addRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.ImmutableMap;

import com.tom.api.block.IMethod;
import com.tom.api.block.IMethod.IClientMethod;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.lib.utils.EmptyEntry;

public class ToolGroup {
	protected Map<String, Item> items;
	protected Map<String, ItemStack> heads;
	public static final Predicate<Entry<String, Item>> notHead = i -> !i.getKey().equals("head");
	public static final Map<Integer, String> defaultHead = ImmutableMap.<Integer, String>of(0, "pickaxe", 1, "axe", 2, "shovel", 3, "hoe", 4, "sword");
	private final String materialName;
	private final int hammerLvl;
	private boolean enabled = true;

	public ToolGroup(ToolMaterial mat, float attackSpeed, Map<Integer, String> meta, String materialName, int hammerLvl) {
		items = new HashMap<>();
		this.hammerLvl = hammerLvl;
		this.materialName = materialName;
		items.put("pickaxe", new Pickaxe(mat));
		items.put("axe", new Axe(mat, attackSpeed));
		items.put("shovel", new Shovel(mat));
		items.put("hoe", new Hoe(mat));
		items.put("sword", new Sword(mat));
		if (meta != null) {
			Head head;
			items.put("head", head = new Head(meta));
			heads = items.entrySet().stream().filter(notHead).map(i -> {
				if (head.isValid(i.getKey()))
					return new EmptyEntry<>(i.getKey(), new ItemStack(head, 1, head.getMetadata(i.getKey())));
				else
					return new EmptyEntry<>(i.getKey(), ItemStack.EMPTY);
			}).filter(i -> !i.getValue().isEmpty()).collect(Collectors.toMap(i -> i.getKey(), i -> i.getValue()));
		}
	}

	public ToolGroup setUnlocalizedName(String name) {
		items.entrySet().stream().forEach(i -> i.getValue().setUnlocalizedName(name + "_" + i.getKey()));
		enabled = Config.enableToolGroup(name);
		return this;
	}

	protected void register() {
		if (enabled)
			items.entrySet().stream().forEach(e -> {
				Item i = e.getValue();
				if (e.getKey().equals("head")) {
					CoreInit.addItemToGameRegistry(i, i.getUnlocalizedName().substring(5));
					CoreInit.proxy.runMethod((IMethod) i);
				} else
					CoreInit.registerItem(i);
			});
	}

	public ToolGroup setCreativeTab(CreativeTabs tab) {
		items.values().stream().forEach(i -> i.setCreativeTab(tab));
		return this;
	}

	protected static class Pickaxe extends ItemPickaxe {
		public Pickaxe(ToolMaterial material) {
			super(material);
		}
	}

	protected static class Axe extends ItemAxe {

		public Axe(ToolMaterial material, float attackSpeed) {
			super(material, material.getAttackDamage() * 1.1F, attackSpeed);
		}
	}

	protected static class Shovel extends ItemSpade {

		public Shovel(ToolMaterial material) {
			super(material);
		}
	}

	protected static class Hoe extends ItemHoe {

		public Hoe(ToolMaterial material) {
			super(material);
		}
	}

	protected static class Sword extends ItemSword {

		public Sword(ToolMaterial material) {
			super(material);
		}
	}

	public static class Head extends Item implements IClientMethod {
		public Map<Integer, String> meta;

		public Head(Map<Integer, String> meta) {
			this.meta = meta;
			setHasSubtypes(true);
		}

		public int getMetadata(String key) {
			return meta.entrySet().stream().filter(e -> e.getValue().equals(key)).map(Entry::getKey).min(Integer::compare).orElse(0);
		}

		public boolean isValid(String key) {
			return meta.containsValue(key);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void exec() {
			String name = CoreInit.getNameForItem(this).replace("|", "");
			meta.entrySet().forEach(e -> CoreInit.registerRender(Head.this, e.getKey(), name + "_" + e.getValue()));
		}

		/**
		 * returns a list of items with the same ID, but different meta (eg: dye
		 * returns 16 items)
		 */
		@Override
		@SideOnly(Side.CLIENT)
		public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
			if (this.isInCreativeTab(tab))meta.keySet().forEach(m -> subItems.add(new ItemStack(this, 1, m)));
		}

		@Override
		public String getUnlocalizedName(ItemStack stack) {
			return getUnlocalizedName() + "_" + meta.getOrDefault(stack.getMetadata(), meta.get(0));
		}
	}

	public Item axe() {
		return items.getOrDefault("axe", Items.AIR);
	}

	public Item shovel() {
		return items.getOrDefault("shovel", Items.AIR);
	}

	public Item sword() {
		return items.getOrDefault("sword", Items.AIR);
	}

	public Item hoe() {
		return items.getOrDefault("hoe", Items.AIR);
	}

	public Item pick() {
		return items.getOrDefault("pickaxe", Items.AIR);
	}

	public ItemStack axeHead() {
		return heads.getOrDefault("axe", ItemStack.EMPTY).copy();
	}

	public ItemStack swordHead() {
		return heads.getOrDefault("sword", ItemStack.EMPTY).copy();
	}

	public ItemStack pickHead() {
		return heads.getOrDefault("pickaxe", ItemStack.EMPTY).copy();
	}

	public ItemStack hoeHead() {
		return heads.getOrDefault("hoe", ItemStack.EMPTY).copy();
	}

	public ItemStack shovelHead() {
		return heads.getOrDefault("shovel", ItemStack.EMPTY).copy();
	}

	public void registerSimpleRecipe() {
		if (enabled)
			items.entrySet().stream().filter(notHead).forEach(i -> addRecipe(new ItemStack(i.getValue()), new Object[]{"FS", " S", 'F', heads.get(i.getKey()), 'S', Items.STICK}));
	}

	public void registerHammerRecipe() {
		if (enabled)
			ToolsInit.addToolRecipes(new Item[]{pick(), axe(), shovel(), hoe(), sword()}, materialName, hammerLvl);
	}
}
