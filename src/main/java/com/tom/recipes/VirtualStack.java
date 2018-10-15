package com.tom.recipes;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public class VirtualStack implements Supplier<ItemStack> {
	private final Item item;
	private final int meta, amount;
	private NBTTagCompound nbt;
	public VirtualStack(Item item, int amount, int meta, String nbt) {
		this.item = item;
		this.meta = meta;
		this.amount = amount;
		if(nbt != null){
			try {
				this.nbt = JsonToNBT.getTagFromJson("{" + nbt + "}");
			} catch (Exception e) {
				throw new RuntimeException("Failed to parse virtual stack NBT", e);
			}
		}
	}
	@Override
	public ItemStack get() {
		ItemStack is = new ItemStack(item, amount, meta);
		if(nbt != null){
			is.setTagCompound(nbt.copy());
		}
		return is;
	}
	public ItemStack getStackNormal(int count) {
		ItemStack is = new ItemStack(item, amount*count, meta);
		if(nbt != null){
			is.setTagCompound(nbt.copy());
		}
		return is;
	}

}
