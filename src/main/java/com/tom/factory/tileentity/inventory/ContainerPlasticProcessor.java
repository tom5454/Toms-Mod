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
import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntityPlasticProcessor;
import com.tom.network.messages.MessageFluidStackSync;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerPlasticProcessor extends ContainerTomsMod implements IFluidContainer{
	private TileEntityPlasticProcessor te;
	private FluidStack water, kerosene, lpg, creosote;
	private int energy, lastProgress;
	public ContainerPlasticProcessor(InventoryPlayer playerInv, TileEntityPlasticProcessor te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 118, 44));
		addSlotToContainer(new Slot(te, 1, 118, 62));
		addSlotToContainer(new SlotOutput(te, 2, 152, 18));
		addSlotToContainer(new SlotSpeedUpgrade(te, 3, 152, 74, 4));
		addPlayerSlots(playerInv, 8, 94);
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}
	@Override
	public void syncFluid(int id, FluidStack stack) {
		if(id == 0)te.getTankWater().setFluid(stack);
		else if(id == 1)te.getTankKerosene().setFluid(stack);
		else if(id == 2)te.getTankLPG().setFluid(stack);
		else if(id == 3)te.getTankCreosote().setFluid(stack);
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int energy = te.getClientEnergyStored();
		FluidStack water = te.getTankWater().getFluid();
		FluidStack kerosene = te.getTankKerosene().getFluid();
		FluidStack lpg = te.getTankLPG().getFluid();
		FluidStack creosote = te.getTankCreosote().getFluid();
		if(water != null)water = water.copy();
		if(kerosene != null)kerosene = kerosene.copy();
		if(lpg != null)lpg = lpg.copy();
		if(creosote != null)creosote = creosote.copy();
		for(IContainerListener crafter : listeners){
			MessageProgress msg = new MessageProgress(crafter);
			if(this.energy != energy)msg.add(0, energy);
			if(!TomsModUtils.areFluidStacksEqual(water, this.water))MessageFluidStackSync.sendTo(crafter, 0, water);
			if(!TomsModUtils.areFluidStacksEqual(kerosene, this.kerosene))MessageFluidStackSync.sendTo(crafter, 1, kerosene);
			if(this.lastProgress != te.getField(0))crafter.sendProgressBarUpdate(this, 1, te.getField(0));
			if(!TomsModUtils.areFluidStacksEqual(lpg, this.lpg))MessageFluidStackSync.sendTo(crafter, 2, lpg);
			if(!TomsModUtils.areFluidStacksEqual(creosote, this.creosote))MessageFluidStackSync.sendTo(crafter, 3, creosote);
			msg.send();
		}
		this.energy = energy;
		this.water = water;
		this.kerosene = kerosene;
		this.lpg = lpg;
		this.creosote = creosote;
		this.lastProgress = te.getField(0);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		if(id == 0){
			te.clientEnergy = data;
		}else if(id == 1)
			te.setField(0, data);
	}
}