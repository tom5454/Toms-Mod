package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotOutput;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.factory.tileentity.TileEntitySteamRubberProcessor;
import com.tom.network.messages.MessageFluidStackSync.FluidSynchronizer;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamRubberProcessor extends ContainerTomsMod implements IFluidContainer {
	private FluidSynchronizer sync;
	private TileEntitySteamRubberProcessor te;
	private int lastProgress, lastV;

	public ContainerSteamRubberProcessor(InventoryPlayer playerInv, TileEntitySteamRubberProcessor te) {
		sync = new FluidSynchronizer(() -> te.getTankIn().getFluid());
		this.te = te;
		addSlotToContainer(new SlotVulcanizing(te, 0, 44, 35));
		addSlotToContainer(new SlotOutput(te, 1, 130, 36));
		addPlayerSlots(playerInv, 8, 84);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if (id == 0)
			te.getTankIn().setFluid(stack);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener crafter : listeners) {
			if (lastProgress != te.getField(0))
				crafter.sendWindowProperty(this, 0, te.getField(0));
			if (lastV != te.getField(1))
				crafter.sendWindowProperty(this, 1, te.getField(1));
		}
		lastProgress = te.getField(0);
		lastV = te.getField(1);
		sync.detectAndSendChanges(listeners);
	}

	public static class SlotVulcanizing extends Slot {

		public SlotVulcanizing(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return CraftingMaterial.VULCANIZING_AGENTS.equals(stack);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0)
			te.setField(0, data);
		else if (id == 1)
			te.setField(1, data);
	}
}
