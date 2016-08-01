package com.tom.factory.tileentity.inventory;

import com.tom.apis.TomsModUtils;
import com.tom.core.tileentity.inventory.ContainerTomsMod;
import com.tom.factory.tileentity.TileEntityBasicBoiler;
import com.tom.network.messages.MessageFluidStackSync;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBasicBoiler extends ContainerTomsMod implements IFluidContainer{
	private TileEntityBasicBoiler te;
	private FluidStack water, steam;
	private int heat, burnTime;

	public ContainerBasicBoiler(InventoryPlayer playerInv, TileEntityBasicBoiler te) {
		this.te = te;
		addSlotToContainer(new SlotFurnaceFuel(te.inv, 0, 43, 64));
		addPlayerSlots(playerInv, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUseable(te.getPos(), playerIn, te.getWorld(), te);
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int heat = MathHelper.floor_double(te.getHeat());
		FluidStack water = te.getTankWater().getFluid();
		FluidStack steam = te.getTankSteam().getFluid();
		if(water != null)water = water.copy();
		if(steam != null)steam = steam.copy();
		int burnTime = MathHelper.floor_double(((double)te.getBurnTime()) / te.getMaxBurnTime() * 100);
		for(IContainerListener crafter : listeners){
			if(this.heat != heat)crafter.sendProgressBarUpdate(this, 0, heat);
			if(!TomsModUtils.areFluidStacksEqual(water, this.water))MessageFluidStackSync.sendTo(crafter, 0, water);
			if(!TomsModUtils.areFluidStacksEqual(steam, this.steam))MessageFluidStackSync.sendTo(crafter, 1, steam);
			if(this.burnTime != burnTime)crafter.sendProgressBarUpdate(this, 1, burnTime);
		}
		this.heat = heat;
		this.water = water;
		this.steam = steam;
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

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if(id == 0)te.getTankWater().setFluid(stack);
		else if(id == 1)te.getTankSteam().setFluid(stack);
	}
}
