package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityMixer;
import com.tom.network.messages.MessageFluidStackSync.FluidSynchronizer;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerMixer extends ContainerTomsMod implements IFluidContainer {
	private TileEntityMixer te;
	private FluidSynchronizer sync;
	private int lastEnergy, lastProgress;
	private int lastMaxProgress = -1;

	public ContainerMixer(InventoryPlayer playerInv, TileEntityMixer te) {
		this.te = te;
		int x = 72, y = 43;
		sync = new FluidSynchronizer(() -> te.getTankIn().getFluid(), () -> te.getTankIn2().getFluid(), () -> te.getTankOut().getFluid());
		addSlotToContainer(new Slot(te, 0, x, y));
		addSlotToContainer(new Slot(te, 1, x + 18, y));
		addSlotToContainer(new Slot(te, 2, x, y + 18));
		addSlotToContainer(new Slot(te, 3, x + 18, y + 18));
		addSlotToContainer(new SlotSpeedUpgrade(te, 4, 152, 74, 24));
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
			te.getTankIn2().setFluid(stack);
		else if (id == 2)
			te.getTankOut().setFluid(stack);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener crafter : listeners) {
			MessageProgress msg = new MessageProgress(crafter);
			if (lastEnergy != te.getClientEnergyStored())
				msg.add(0, te.getClientEnergyStored());
			if (lastProgress != te.getField(0))
				crafter.sendWindowProperty(this, 1, te.getField(0));
			if (lastMaxProgress != te.getField(1))
				crafter.sendWindowProperty(this, 2, te.getField(1));
			msg.send();
		}
		sync.detectAndSendChanges(listeners);
		lastEnergy = te.getClientEnergyStored();
		lastProgress = te.getField(0);
		lastMaxProgress = te.getField(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0)
			te.clientEnergy = data;
		else if (id == 1)
			te.setField(0, data);
		else if (id == 2)
			te.setField(1, data);
	}
}