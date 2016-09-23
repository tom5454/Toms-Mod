package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntityRefinery;
import com.tom.network.messages.MessageFluidStackSync;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerRefinery extends ContainerTomsMod implements IFluidContainer{
	private TileEntityRefinery te;
	private FluidStack in, out1, out2, out3;
	private int heat, burnTime;
	public ContainerRefinery(InventoryPlayer playerInv, TileEntityRefinery te) {
		this.te = te;
		addSlotToContainer(new SlotFurnaceFuel(te, 0, 49, 63));
		addSlotToContainer(new SlotFurnaceFuel(te, 1, 30, 27));
		addSlotToContainer(new SlotFurnaceFuel(te, 2, 30, 45));
		addSlotToContainer(new SlotFurnaceFuel(te, 3, 30, 63));
		addPlayerSlots(playerInv, 8, 94);
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}
	@Override
	public void syncFluid(int id, FluidStack stack) {
		if(id == 0)te.getTankIn().setFluid(stack);
		else if(id == 1)te.getTankOut1().setFluid(stack);
		else if(id == 2)te.getTankOut2().setFluid(stack);
		else if(id == 3)te.getTankOut3().setFluid(stack);
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int heat = MathHelper.floor_double(te.getHeat());
		FluidStack in = te.getTankIn().getFluid();
		FluidStack out1 = te.getTankOut1().getFluid();
		FluidStack out2 = te.getTankOut2().getFluid();
		FluidStack out3 = te.getTankOut3().getFluid();
		if(in != null)in = in.copy();
		if(out1 != null)out1 = out1.copy();
		if(out2 != null)out2 = out2.copy();
		if(out3 != null)out3 = out3.copy();
		int burnTime = MathHelper.floor_double(((double)te.getBurnTime()) / te.getMaxBurnTime() * 100);
		for(IContainerListener crafter : listeners){
			if(this.heat != heat)crafter.sendProgressBarUpdate(this, 0, heat);
			if(!TomsModUtils.areFluidStacksEqual(in, this.in))MessageFluidStackSync.sendTo(crafter, 0, in);
			if(!TomsModUtils.areFluidStacksEqual(out1, this.out1))MessageFluidStackSync.sendTo(crafter, 1, out1);
			if(this.burnTime != burnTime)crafter.sendProgressBarUpdate(this, 1, burnTime);
			if(!TomsModUtils.areFluidStacksEqual(out2, this.out2))MessageFluidStackSync.sendTo(crafter, 2, out2);
			if(!TomsModUtils.areFluidStacksEqual(out3, this.out3))MessageFluidStackSync.sendTo(crafter, 3, out3);
		}
		this.heat = heat;
		this.in = in;
		this.out1 = out1;
		this.out2 = out2;
		this.out3 = out3;
		this.burnTime = burnTime;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		if(id == 0){
			te.clientHeat = data;
		}else if(id == 1)
			te.setBurnTime(data);
	}
}
