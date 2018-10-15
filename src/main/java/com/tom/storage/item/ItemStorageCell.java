package com.tom.storage.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.inventory.IStorageInventory;
import com.tom.api.inventory.StorageCellInventory;
import com.tom.api.item.IStorageCell;
import com.tom.core.CoreInit;
import com.tom.storage.handler.StorageNetworkGrid;
import com.tom.util.TomsModUtils;

public class ItemStorageCell extends Item implements IStorageCell, IModelRegisterRequired {
	public static enum CellLight {
		GREEN, ORANGE, RED
	}

	public ItemStorageCell() {
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("tm.itemStorageCell");
	}

	@Override
	public IStorageInventory getData(final ItemStack stack, World world, BlockPos pos, final int priority, final StorageNetworkGrid grid) {
		final int meta = stack.getItemDamage();
		return new StorageCellInventory(stack, priority, getSize(meta), grid);
	}

	@Override
	public ItemStorageCell setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		StorageCellInventory inv = new StorageCellInventory(stack, getSize(stack.getMetadata()));
		tooltip.add(I18n.format("tomsMod.tooltip.bytesUsed", inv.getClientBytes(), inv.getMaxBytes()));
		tooltip.add(I18n.format("tomsMod.tooltip.cellFormatting", I18n.format("tomsMod.storage.formatnormal")));
	}

	private static int getSize(int meta) {
		return (meta == 0 ? 1 : (meta == 1 ? 4 : (meta == 2 ? 16 : (meta == 3 ? 64 : 1)))) * 1024;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)){
			subItems.add(new ItemStack(this, 1, 0));
			subItems.add(new ItemStack(this, 1, 1));
			subItems.add(new ItemStack(this, 1, 2));
			subItems.add(new ItemStack(this, 1, 3));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getItemDamage();
		return super.getUnlocalizedName(stack) + "_" + (meta == 0 ? "1" : (meta == 1 ? "4" : (meta == 2 ? "16" : (meta == 3 ? "64" : "1"))));
	}

	@Override
	public ItemStorageCell.CellLight getLightState(IStorageInventory data) {
		StorageCellInventory inv = (StorageCellInventory) data;
		int size = inv.getBytes(), max = inv.getMaxBytes();
		return size >= max ? ItemStorageCell.CellLight.RED : size >= max / 2 ? ItemStorageCell.CellLight.ORANGE : ItemStorageCell.CellLight.GREEN;
	}

	@Override
	public double getPowerDrain(ItemStack stack, World world, BlockPos pos, StorageNetworkGrid grid) {
		return stack.getItemDamage() + 1;
	}

	@Override
	public int getBootTime(ItemStack stack, World world, BlockPos pos, StorageNetworkGrid grid) {
		return (stack.getItemDamage() + 1) * 6;
	}

	@Override
	public boolean isValid(ItemStack stack, StorageNetworkGrid grid) {
		return true;
	}

	@Override
	public void registerModels() {
		CoreInit.registerRender(this, 0, "tomsmodstorage:1kStorageCell");
		CoreInit.registerRender(this, 1, "tomsmodstorage:4kStorageCell");
		CoreInit.registerRender(this, 2, "tomsmodstorage:16kStorageCell");
		CoreInit.registerRender(this, 3, "tomsmodstorage:64kStorageCell");
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (!entityItem.world.isRemote) {
			entityItem.setNoDespawn();
			entityItem.setEntityInvulnerable(true);
			entityItem.extinguish();
		}
		return super.onEntityItemUpdate(entityItem);
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		if (!player.isSneaking()) {
			if (player.isSneaking())
				TomsModUtils.sendNoSpamTranslate(player, "tomsMod.chat.sneakToDrop");
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("cellName")) {
			return super.getItemStackDisplayName(stack) + " - " + stack.getTagCompound().getString("cellName");
		} else
			return super.getItemStackDisplayName(stack);
	}
}
