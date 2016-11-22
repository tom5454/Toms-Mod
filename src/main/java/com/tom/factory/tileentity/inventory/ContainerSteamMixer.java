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
import com.tom.network.messages.MessageFluidStackSync;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamMixer extends ContainerTomsMod implements IFluidContainer{
	private TileEntitySteamMixer te;
	private FluidStack in, out;
	private int progress;
	public ContainerSteamMixer(InventoryPlayer inv, TileEntitySteamMixer te) {
		int x = 73, y = 45;
		addSlotToContainer(new Slot(te, 0, x, y));
		addSlotToContainer(new Slot(te, 1, x + 18, y));
		addSlotToContainer(new Slot(te, 2, x, y + 18));
		addSlotToContainer(new Slot(te, 3, x + 18, y + 18));
		addPlayerSlots(inv, 8, 94);
		this.te = te;
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUseable(te.getPos(), playerIn, te.getWorld(), te);
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		FluidStack in = te.getTankIn().getFluid();
		FluidStack out = te.getTankOut().getFluid();
		if(in != null)in = in.copy();
		if(out != null)out = out.copy();
		int progress = MathHelper.floor_double(((double)te.getField(0)) / TileEntitySteamMixer.MAX_PROCESS_TIME * 100);
		for(IContainerListener crafter : listeners){
			if(!TomsModUtils.areFluidStacksEqual(in, this.in))MessageFluidStackSync.sendTo(crafter, 0, in);
			if(!TomsModUtils.areFluidStacksEqual(out, this.out))MessageFluidStackSync.sendTo(crafter, 1, out);
			if(this.progress != progress)crafter.sendProgressBarUpdate(this, 0, progress);
		}
		this.progress = progress;
		this.in = in;
		this.out = out;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		if(id == 0)
			te.clientProgress = data;
	}

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if(id == 0)te.getTankIn().setFluid(stack);
		else if(id == 1)te.getTankOut().setFluid(stack);
	}
}
