package com.tom.transport.multipart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.multipart.PartModule;
import com.tom.lib.Configs;
import com.tom.transport.TransportInit;

import mcmultipart.raytrace.PartMOP;

public class PartFluidServo extends PartModule<FluidGrid> {
	public PartFluidServo() {
		this(EnumFacing.NORTH);
	}
	public PartFluidServo(EnumFacing face) {
		super(TransportInit.fluidServo, 0.25, 0.25, face, "tomsmodtransport:tm.servo", 1);
	}

	@Override
	public void onGridReload() {

	}

	@Override
	public void onGridPostReload() {

	}

	@Override
	public FluidGrid constructGrid() {
		return new FluidGrid();
	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(grid.getData().getFluid() == null || grid.getData().getCapacity() >= grid.getData().getFluidAmount()){
				TileEntity tile = worldObj.getTileEntity(pos.offset(facing));
				if(tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)){
					IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
					if(t != null){
						FluidStack drained = t.drain(Configs.fluidDuctMaxExtract, false);
						if(drained != null && drained.amount > 0){
							//if(t.canDrain(f, t.drain(1, false).getFluid())){
							int filled = grid.getData().fill(drained, false);
							if(filled > 0){
								int canDrain = Math.min(filled, Math.min(Configs.fluidDuctMaxExtract, drained.amount));
								grid.getData().fill(t.drain(canDrain, true), true);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public int getState() {
		return 1;
	}
	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand,
			ItemStack heldItem, PartMOP hit) {
		return true;
	}
}
