package com.tom.core.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.handler.TMPlayerHandler;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageTabletGui;

import com.tom.core.item.TabletHandler;

import com.tom.core.tileentity.TileEntityTabletController;

public class ContainerTablet extends ContainerTomsMod {
	public TabletHandler tab;
	public ItemStack tabStack;

	public ContainerTablet(ItemStack is, World world, EntityPlayer player) {
		if (is.getTagCompound() != null && is.getTagCompound().hasKey("x") && is.getTagCompound().hasKey("y") && is.getTagCompound().hasKey("z") && is.getTagCompound().hasKey("id")) {
			TileEntity tile = world.getTileEntity(new BlockPos(is.getTagCompound().getInteger("x"), is.getTagCompound().getInteger("y"), is.getTagCompound().getInteger("z")));
			if (tile instanceof TileEntityTabletController) {
				//TileEntityTabletController te = (TileEntityTabletController) tile;
				//int id = is.getTagCompound().getInteger("id");
				this.tab = TMPlayerHandler.getPlayerHandler(player).tabletHandler;
			} else {
				this.tab = null;
			}
		} else {
			this.tab = null;
		}
		this.tabStack = is;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		// return player.getCurrentEquippedItem() != null &&
		// player.getCurrentEquippedItem().getItem() == CoreInit.Tablet &&
		// ((Tablet)player.getCurrentEquippedItem().getItem()).getEnergyStored(player.getCurrentEquippedItem())
		// > 10;
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener crafter : listeners) {
			if (crafter instanceof EntityPlayerMP) {
				NetworkHandler.sendTo(new MessageTabletGui(tabStack), (EntityPlayerMP) crafter);
			}
		}
	}

}
