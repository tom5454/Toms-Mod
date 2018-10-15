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
import com.tom.factory.tileentity.TileEntityFluidTransposer;
import com.tom.network.messages.MessageFluidStackSync;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;
import com.tom.util.TomsModUtils;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerFluidTransposer extends ContainerTomsMod implements IFluidContainer {
	private TileEntityFluidTransposer te;
	private int lastEnergy, lastProgress;
	private int lastMaxProgress, lastMode;
	private FluidStack tank;

	public ContainerFluidTransposer(InventoryPlayer playerInv, TileEntityFluidTransposer te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 41, 21));
		addSlotToContainer(new Slot(te, 1, 77, 21));
		addSlotToContainer(new SlotOutput(te, 2, 77, 59));
		addSlotToContainer(new SlotSpeedUpgrade(te, 3, 152, 74, 24));
		addPlayerSlots(playerInv, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		FluidStack tank = te.getTank().getFluid();
		if (tank != null)
			tank = tank.copy();
		int mode = te.getMode() ? 1 : 0;
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
			if (lastMode != mode)
				crafter.sendWindowProperty(this, 3, mode);
			msg.send();
			if (!TomsModUtils.areFluidStacksEqual(tank, this.tank))
				MessageFluidStackSync.sendTo(crafter, 0, tank);
		}
		lastEnergy = te.getClientEnergyStored();
		lastProgress = te.getField(0);
		lastMaxProgress = te.getField(1);
		this.tank = tank;
		lastMode = mode;
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
			te.setMode(data);
	}

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if (id == 0)
			te.getTank().setFluid(stack);
	}
}
