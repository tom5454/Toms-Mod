package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityElectricalRubberProcessor;
import com.tom.network.messages.MessageFluidStackSync.FluidSynchronizer;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerElectricalRubberProcessor extends ContainerTomsMod implements IFluidContainer {
	private TileEntityElectricalRubberProcessor te;
	private int lastEnergy = -1, lastProgress = -1;
	private int lastMaxProgress = -1, lastVulcanizing = -1;
	private FluidSynchronizer sync;

	public ContainerElectricalRubberProcessor(InventoryPlayer playerInv, TileEntityElectricalRubberProcessor te) {
		this.te = te;
		sync = new FluidSynchronizer(() -> te.getTankIn().getFluid(), () -> te.getTankCresin().getFluid());
		addSlotToContainer(new Slot(te, 0, 65, 25));
		addSlotToContainer(new Slot(te, 1, 65, 43));
		addSlotToContainer(new SlotOutput(te, 2, 148, 36));
		addSlotToContainer(new SlotSpeedUpgrade(te, 3, 152, 63, 24));
		addPlayerSlots(playerInv, 8, 84);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener crafter : listeners) {
			MessageProgress msg = new MessageProgress(crafter);
			if (lastEnergy != te.getClientEnergyStored())
				msg.add(0, te.getClientEnergyStored());
			// crafter.sendProgressBarUpdate(this, 0,
			// te.getClientEnergyStored());
			if (lastProgress != te.getField(0))
				crafter.sendWindowProperty(this, 1, te.getField(0));
			if (lastMaxProgress != te.getField(1))
				crafter.sendWindowProperty(this, 2, te.getField(1));
			if (lastVulcanizing != te.getField(2))
				crafter.sendWindowProperty(this, 3, te.getField(2));
			msg.send();
		}
		sync.detectAndSendChanges(listeners);
		lastEnergy = te.getClientEnergyStored();
		lastProgress = te.getField(0);
		lastMaxProgress = te.getField(1);
		lastVulcanizing = te.getField(2);
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
		else if (id == 3)
			te.setField(2, data);
	}

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if (id == 0)
			te.getTankIn().setFluid(stack);
		else if (id == 1)
			te.getTankCresin().setFluid(stack);
	}
}
