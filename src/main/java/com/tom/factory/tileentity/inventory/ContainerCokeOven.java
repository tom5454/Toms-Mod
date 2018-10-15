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
import com.tom.factory.tileentity.TileEntityCokeOven;
import com.tom.network.messages.MessageFluidStackSync;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerCokeOven extends ContainerTomsMod implements IFluidContainer {
	private FluidStack tank;
	private int progress;
	private TileEntityCokeOven te;

	public ContainerCokeOven(InventoryPlayer playerInv, TileEntityCokeOven te) {
		addSlotToContainer(new Slot(te, 0, 28, 41));
		addSlotToContainer(new SlotOutput(te, 1, 87, 41));
		addPlayerSlots(playerInv, 8, 94);
		this.te = te;
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
		int progress = te.getField(1) > 0 ? MathHelper.floor(((double) te.getField(0)) / te.getField(1) * 100) : 0;
		for (IContainerListener crafter : listeners) {
			if (!TomsModUtils.areFluidStacksEqual(tank, this.tank))
				MessageFluidStackSync.sendTo(crafter, 0, tank);
			if (this.progress != progress)
				crafter.sendWindowProperty(this, 0, progress);
		}
		this.progress = progress;
		this.tank = tank;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0)
			te.setField(0, data);
	}

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if (id == 0)
			te.getTank().setFluid(stack);
	}
}
