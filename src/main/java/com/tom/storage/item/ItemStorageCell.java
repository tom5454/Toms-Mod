package com.tom.storage.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.IStorageInventory.IStorageInv;
import com.tom.api.inventory.IStorageInventory.StorageCellInventory;
import com.tom.api.item.IStorageCell;
import com.tom.storage.multipart.StorageNetworkGrid.CellLight;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingHandler;
import com.tom.storage.multipart.StorageNetworkGrid.IStorageData;

public class ItemStorageCell extends Item implements IStorageCell {
	public ItemStorageCell() {
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("tm.itemStorageCell");
	}
	@Override
	public IStorageData getData(final ItemStack stack, World world, BlockPos pos, final int priority) {
		final int meta = stack.getItemDamage();
		return new IStorageData() {
			IStorageInv inv = new StorageCellInventory(stack, priority, 10 * (meta == 0 ? 1 : (meta == 1 ? 4 : (meta == 2 ? 16 : (meta == 3 ? 64 : 1)))));
			@Override
			public IStorageInv getInventory() {
				return inv;
			}

			@Override
			public void update(ItemStack stack, IInventory inv, World world, int priority) {
				this.inv.update(stack, inv, priority);
			}

			@Override
			public ICraftingHandler<?> getCraftingHandler() {
				return null;
			}
		};
	}
	@Override
	public ItemStorageCell setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn,
			List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		IStorageInv inv = this.getData(stack, null, null, 0).getInventory();
		tooltip.add(I18n.format("tomsMod.tooltip.slotsUsed", inv.getItemListSize(),inv.getSizeInventory()));
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
		subItems.add(new ItemStack(itemIn, 1, 2));
		subItems.add(new ItemStack(itemIn, 1, 3));
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getItemDamage();
		return super.getUnlocalizedName(stack) + "_" + (meta == 0 ? "1" : (meta == 1 ? "4" : (meta == 2 ? "16" : (meta == 3 ? "64" : "1"))));
	}
	@Override
	public CellLight getLightState(ItemStack stack, World world, BlockPos pos) {
		IStorageInv inv = this.getData(stack, null, null, 0).getInventory();
		int size = inv.getItemListSize(), max = inv.getSizeInventory();
		return size == max ? CellLight.RED : size >= max / 2 ? CellLight.ORANGE : CellLight.GREEN;
	}
	@Override
	public double getPowerDrain(ItemStack stack, World world, BlockPos pos) {
		return stack.getItemDamage() + 1;
	}
	@Override
	public int getBootTime(ItemStack stack, World world, BlockPos pos) {
		return (stack.getItemDamage() + 1) * 6;
	}
	@Override
	public boolean isValid(ItemStack stack) {
		return true;
	}
}
