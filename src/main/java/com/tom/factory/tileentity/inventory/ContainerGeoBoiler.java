package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntityGeoBoiler;
import com.tom.network.messages.MessageFluidStackSync;
import com.tom.network.messages.MessageFluidStackSync.IFluidContainer;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerGeoBoiler extends ContainerTomsMod implements IFluidContainer{
	private TileEntityGeoBoiler te;
	private FluidStack water, steam, lava;
	private int heat;

	public ContainerGeoBoiler(InventoryPlayer playerInv, TileEntityGeoBoiler te) {
		this.te = te;
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
		FluidStack lava = te.getTankLava().getFluid();
		if(water != null)water = water.copy();
		if(steam != null)steam = steam.copy();
		if(lava != null)lava = lava.copy();
		for(IContainerListener crafter : listeners){
			if(this.heat != heat)crafter.sendProgressBarUpdate(this, 0, heat);
			if(!TomsModUtils.areFluidStacksEqual(water, this.water))MessageFluidStackSync.sendTo(crafter, 0, water);
			if(!TomsModUtils.areFluidStacksEqual(steam, this.steam))MessageFluidStackSync.sendTo(crafter, 1, steam);
			if(!TomsModUtils.areFluidStacksEqual(lava, this.lava))MessageFluidStackSync.sendTo(crafter, 2, lava);
		}
		this.heat = heat;
		this.water = water;
		this.steam = steam;
		this.lava = lava;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		if(id == 0){
			te.clientHeat = data;
		}
	}

	@Override
	public void syncFluid(int id, FluidStack stack) {
		if(id == 0)te.getTankWater().setFluid(stack);
		else if(id == 1)te.getTankSteam().setFluid(stack);
		else if(id == 2)te.getTankLava().setFluid(stack);
	}
}
