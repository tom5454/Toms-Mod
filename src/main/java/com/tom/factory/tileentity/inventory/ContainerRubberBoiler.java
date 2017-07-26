package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntityRubberBoiler;
import com.tom.network.messages.MessageFluidStackSync.FluidSynchronizer;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerRubberBoiler extends ContainerTomsMod implements IFluidContainer {
	private int heat, lastProgress, maxHeat;
	private FluidSynchronizer sync;
	private TileEntityRubberBoiler te;

	public ContainerRubberBoiler(InventoryPlayer playerInv, TileEntityRubberBoiler te) {
		this.te = te;
		sync = new FluidSynchronizer(() -> te.getTankIn().getFluid(), () -> te.getTankOut().getFluid());
		addSlotToContainer(new Slot(te, 0, 38, 65));
		addSlotToContainer(new SlotOutput(te, 1, 114, 65));
		addPlayerSlots(playerInv, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int heat = MathHelper.floor(te.getHeat());
		int progress = MathHelper.floor(((double) te.getProgress()) / TileEntityRubberBoiler.MAX_PROGRESS * 100);
		for (IContainerListener crafter : listeners) {
			if (this.heat != heat)
				crafter.sendWindowProperty(this, 0, heat);
			if (this.lastProgress != progress)
				crafter.sendWindowProperty(this, 1, progress);
			if (this.maxHeat != te.maxHeat)
				crafter.sendWindowProperty(this, 2, te.maxHeat);
		}
		sync.detectAndSendChanges(listeners);
		this.heat = heat;
		this.lastProgress = progress;
		this.maxHeat = te.maxHeat;
	}

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if (id == 0)
			te.getTankIn().setFluid(stack);
		else if (id == 1)
			te.getTankOut().setFluid(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0)
			te.clientHeat = data;
		else if (id == 1)
			te.setClientProgress(data);
		else if (id == 2)
			te.maxHeat = data;
	}
}
