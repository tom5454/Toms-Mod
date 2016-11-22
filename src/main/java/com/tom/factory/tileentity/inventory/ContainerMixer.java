package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntityMixer;
import com.tom.network.messages.MessageFluidStackSync;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerMixer extends ContainerTomsMod implements IFluidContainer{
	private TileEntityMixer te;
	private FluidStack in, out1, out2;
	private int lastEnergy, lastProgress;
	private int lastMaxProgress = -1;
	public ContainerMixer(InventoryPlayer playerInv, TileEntityMixer te) {
		this.te = te;
		int x = 72, y = 43;
		addSlotToContainer(new Slot(te, 0, x, y));
		addSlotToContainer(new Slot(te, 1, x + 18, y));
		addSlotToContainer(new Slot(te, 2, x, y + 18));
		addSlotToContainer(new Slot(te, 3, x + 18, y + 18));
		addSlotToContainer(new SlotSpeedUpgrade(te, 4, 152, 74, 24));
		addPlayerSlots(playerInv, 8, 94);
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}
	@Override
	public void syncFluid(int id, FluidStack stack) {
		if(id == 0)te.getTankIn().setFluid(stack);
		else if(id == 1)te.getTankIn2().setFluid(stack);
		else if(id == 2)te.getTankOut().setFluid(stack);
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		FluidStack in = te.getTankIn().getFluid();
		FluidStack in2 = te.getTankIn2().getFluid();
		FluidStack out = te.getTankOut().getFluid();
		if(in != null)in = in.copy();
		if(in2 != null)in2 = in2.copy();
		if(out != null)out = out.copy();
		for(IContainerListener crafter : listeners){
			if(!TomsModUtils.areFluidStacksEqual(in, this.in))MessageFluidStackSync.sendTo(crafter, 0, in);
			if(!TomsModUtils.areFluidStacksEqual(in2, this.out1))MessageFluidStackSync.sendTo(crafter, 1, in2);
			if(!TomsModUtils.areFluidStacksEqual(out, this.out2))MessageFluidStackSync.sendTo(crafter, 2, out);
			MessageProgress msg = new MessageProgress(crafter);
			if(lastEnergy != te.getClientEnergyStored())msg.add(0, te.getClientEnergyStored());
			if(lastProgress != te.getField(0))crafter.sendProgressBarUpdate(this, 1, te.getField(0));
			if(lastMaxProgress != te.getField(1))crafter.sendProgressBarUpdate(this, 2, te.getField(1));
			msg.send();
		}
		this.in = in;
		this.out1 = in2;
		this.out2 = out;
		lastEnergy = te.getClientEnergyStored();
		lastProgress = te.getField(0);
		lastMaxProgress = te.getField(1);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		if(id == 0)te.clientEnergy = data;
		else if(id == 1)te.setField(0, data);
		else if(id == 2)te.setField(1, data);
	}
}