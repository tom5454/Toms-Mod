package com.tom.apis;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackHelper {
	public static ItemStack newItemStack(Item item, int ammount, int meta){
		ItemStack is = new ItemStack(item);
		is.stackSize = ammount;
		is.setItemDamage(meta);
		return is;
	}
	public static ItemStack newItemStack(Item item, int ammount){
		return newItemStack(item,ammount,0);
	}
	public static ItemStack newItemStack(Item item, int ammount, int meta, NBTTagCompound nbt){
		ItemStack is = newItemStack(item,ammount,meta);
		is.setTagCompound(nbt);
		return is;
	}
	public static ItemStack newItemStack(Item item, int ammount, NBTTagCompound nbt){
		return newItemStack(item,ammount,0,nbt);
	}
	public static ItemStack newItemStack(Block item, int ammount){
		return newItemStack(item,ammount,0);
	}
	public static ItemStack newItemStack(Block item, int ammount, int meta){
		ItemStack is = new ItemStack(item);
		is.stackSize = ammount;
		is.setItemDamage(meta);
		return is;
	}
}
