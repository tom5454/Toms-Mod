package com.tom.core.item;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;
import com.tom.recipes.ICustomJsonIngerdient;

import com.tom.core.item.ItemCircuit.CircuitType;

public class ItemCircuitRaw extends Item implements IModelRegisterRequired, ICustomJsonIngerdient {
	public ItemCircuitRaw(CreativeTabs tab) {
		setCreativeTab(tab);
		setUnlocalizedName("tm.circuitraw");
		setHasSubtypes(true);
	}
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			ItemCircuit.circuitTypes.values().stream().map(e -> e.createStack(this)).forEach(items::add);
		}
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.tm.circuitraw." + ItemCircuit.getType(stack);
	}
	@Override
	public void registerModels() {
		for (Entry<Integer, String> item : ItemCircuit.models_raw.entrySet()) {
			CoreInit.registerRender(this, item.getKey(), item.getValue());
		}
	}
	@Override
	public Map<String, Object> serialize(ItemStack stack, boolean serializeCount) {
		String id = !stack.hasTagCompound() ? "invalid" : stack.getTagCompound().getString("id");
		return ItemCircuit.serialize(id, "raw", serializeCount ? stack.getCount() : 1);
	}
	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		ItemStack s = updateEntityItem(entityItem.world, new BlockPos(entityItem), entityItem, entityItem.getItem());
		if (s == null || s.isEmpty()) {
			entityItem.setDead();
			return true;
		} else
			entityItem.setItem(s);
		return false;
	}
	public ItemStack updateEntityItem(World world, BlockPos pos, EntityItem item, ItemStack stack) {
		item.setNoDespawn();
		Block block = world.getBlockState(pos).getBlock();
		if (block == CoreInit.ironChloride.getBlock()) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			CircuitType c = ItemCircuit.getCType(stack);
			if (stack.getTagCompound().getInteger("acidTimer") >= c.etchingTime) {
				ItemStack s = c.createStack(CoreInit.circuitUnassembled, stack.getCount());
				return s;
			} else {
				stack.getTagCompound().setInteger("acidTimer", stack.getTagCompound().getInteger("acidTimer") + 1);
			}
		}
		return stack;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		CircuitType c = ItemCircuit.getCType(stack);
		return stack.hasTagCompound() && c != null ? 1 - (stack.getTagCompound().getInteger("acidTimer") / ((double) c.etchingTime)) : 0;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("acidTimer") > 0 : false;
	}
}
