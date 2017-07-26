package com.tom.storage.block;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.tom.api.item.ICustomCraftingHandlerAdv;
import com.tom.storage.StorageInit;
import com.tom.storage.tileentity.TMTank;
import com.tom.storage.tileentity.TileEntityEliteTank;

public class BlockTankElite extends BlockTankBase implements ICustomCraftingHandlerAdv {

	@Override
	public TMTank createNewTileEntity(World worldIn, int meta) {
		return new TileEntityEliteTank();
	}

	@Override
	public void onCrafingAdv(String player, ItemStack crafting, ItemStackAccess second, IInventory craftMatrix) {
		ItemStack old = craftMatrix.getStackInSlot(4);
		if (old != null && old.getItem() == Item.getItemFromBlock(StorageInit.tankAdv))
			crafting.setTagCompound(old.getTagCompound());
	}

	@Override
	public void onUsingAdv(String player, ItemStack crafting, ItemStackAccess second, IInventory craftMatrix, ItemStack s) {

	}

	@Override
	public int getTankSize() {
		return 256000;
	}
}
