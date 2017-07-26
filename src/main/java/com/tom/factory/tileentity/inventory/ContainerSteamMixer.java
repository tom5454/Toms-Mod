package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntitySteamMixer;
import com.tom.network.messages.MessageFluidStackSync.FluidSynchronizer;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamMixer extends ContainerTomsMod implements IFluidContainer {
	private TileEntitySteamMixer te;
	private FluidSynchronizer sync;
	private int progress;

	public ContainerSteamMixer(InventoryPlayer inv, TileEntitySteamMixer te) {
		int x = 73, y = 45;
		sync = new FluidSynchronizer(() -> te.getTankIn().getFluid(), () -> te.getTankOut().getFluid());
		addSlotToContainer(new Slot(te, 0, x, y));
		addSlotToContainer(new Slot(te, 1, x + 18, y));
		addSlotToContainer(new Slot(te, 2, x, y + 18));
		addSlotToContainer(new Slot(te, 3, x + 18, y + 18));
		addPlayerSlots(inv, 8, 94);
		this.te = te;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUsable(te.getPos(), playerIn, te.getWorld(), te);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int progress = MathHelper.floor(((double) te.getField(0)) / TileEntitySteamMixer.MAX_PROCESS_TIME * 100);
		for (IContainerListener crafter : listeners) {
			if (this.progress != progress)
				crafter.sendWindowProperty(this, 0, progress);
		}
		sync.detectAndSendChanges(listeners);
		this.progress = progress;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0)
			te.clientProgress = data;
	}

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if (id == 0)
			te.getTankIn().setFluid(stack);
		else if (id == 1)
			te.getTankOut().setFluid(stack);
	}
}
