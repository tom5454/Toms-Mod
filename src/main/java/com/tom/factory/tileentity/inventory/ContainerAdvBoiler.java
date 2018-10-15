package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.factory.tileentity.TileEntityAdvBoiler;
import com.tom.network.messages.MessageFluidStackSync.FluidSynchronizer;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerAdvBoiler extends ContainerTomsMod implements IFluidContainer {
	private TileEntityAdvBoiler te;
	private FluidSynchronizer sync;
	private int heat, burnTime;

	public ContainerAdvBoiler(InventoryPlayer playerInv, TileEntityAdvBoiler te) {
		this.te = te;
		sync = new FluidSynchronizer(() -> te.getTankWater().getFluid(), () -> te.getTankSteam().getFluid());
		addSlotToContainer(new SlotFurnaceFuel(te.inv, 0, 43, 64));
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

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if (id == 0)
			te.getTankWater().setFluid(stack);
		else if (id == 1)
			te.getTankSteam().setFluid(stack);
	}
}
