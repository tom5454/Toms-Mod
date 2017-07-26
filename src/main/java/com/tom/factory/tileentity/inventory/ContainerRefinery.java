package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.factory.tileentity.TileEntityRefinery;
import com.tom.network.messages.MessageFluidStackSync.FluidSynchronizer;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerRefinery extends ContainerTomsMod implements IFluidContainer {
	private TileEntityRefinery te;
	private FluidSynchronizer sync;
	private int heat, burnTime;

	public ContainerRefinery(InventoryPlayer playerInv, TileEntityRefinery te) {
		this.te = te;
		sync = new FluidSynchronizer(() -> te.getTankIn().getFluid(), () -> te.getTankOut1().getFluid(), () -> te.getTankOut2().getFluid(), () -> te.getTankOut3().getFluid());
		addSlotToContainer(new SlotFurnaceFuel(te, 0, 49, 63));
		addSlotToContainer(new SlotFurnaceFuel(te, 1, 30, 27));
		addSlotToContainer(new SlotFurnaceFuel(te, 2, 30, 45));
		addSlotToContainer(new SlotFurnaceFuel(te, 3, 30, 63));
		addPlayerSlots(playerInv, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if (id == 0)
			te.getTankIn().setFluid(stack);
		else if (id == 1)
			te.getTankOut1().setFluid(stack);
		else if (id == 2)
			te.getTankOut2().setFluid(stack);
		else if (id == 3)
			te.getTankOut3().setFluid(stack);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int heat = MathHelper.floor(te.getHeat());
		int burnTime = MathHelper.floor(((double) te.getBurnTime()) / te.getMaxBurnTime() * 100);
		for (IContainerListener crafter : listeners) {
			if (this.heat != heat)
				crafter.sendWindowProperty(this, 0, heat);
			if (this.burnTime != burnTime)
				crafter.sendWindowProperty(this, 1, burnTime);
		}
		sync.detectAndSendChanges(listeners);
		this.heat = heat;
		this.burnTime = burnTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0) {
			te.clientHeat = data;
		} else if (id == 1)
			te.setBurnTime(data);
	}
}
