package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntityFluidBoiler;
import com.tom.network.messages.MessageFluidStackSync.FluidSynchronizer;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerFluidBoiler extends ContainerTomsMod implements IFluidContainer {
	private TileEntityFluidBoiler te;
	private int heat;
	private FluidSynchronizer sync;

	public ContainerFluidBoiler(InventoryPlayer playerInv, TileEntityFluidBoiler te) {
		this.te = te;
		sync = new FluidSynchronizer(() -> te.getTankWater().getFluid(), () -> te.getTankSteam().getFluid(), () -> te.getTankFuel().getFluid());
		addPlayerSlots(playerInv, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUsable(te.getPos(), playerIn, te.getWorld(), te);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int heat = MathHelper.floor(te.getHeat());
		for (IContainerListener crafter : listeners) {
			if (this.heat != heat)
				crafter.sendWindowProperty(this, 0, heat);
		}
		sync.detectAndSendChanges(listeners);
		this.heat = heat;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0) {
			te.clientHeat = data;
		}
	}

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if (id == 0)
			te.getTankWater().setFluid(stack);
		else if (id == 1)
			te.getTankSteam().setFluid(stack);
		else if (id == 2)
			te.getTankFuel().setFluid(stack);
	}
}
