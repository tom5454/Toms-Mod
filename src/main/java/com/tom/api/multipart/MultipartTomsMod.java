package com.tom.api.multipart;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import com.tom.api.ITileFluidHandler;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.EnergyType.EnergyHandlerNormal;
import com.tom.api.energy.EnergyType.EnergyHandlerProvider;
import com.tom.api.energy.EnergyType.EnergyHandlerReceiver;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.energy.IEnergyProvider;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.energy.IEnergyStorageHandler;
import com.tom.api.energy.IEnergyStorageTile;
import com.tom.api.tileentity.FluidHandlerWrapper;

import mcmultipart.multipart.Multipart;

public class MultipartTomsMod extends Multipart{
	Map<EnumFacing, IItemHandler> itemHandlerSidedMap;
	Map<EnumFacing, IEnergyStorageHandler> energyHandlerMap;
	Map<EnumFacing, IFluidHandler> fluidHandlerMap;
	public MultipartTomsMod() {
		//Initialize capabilities.
		itemHandlerSidedMap = new HashMap<EnumFacing, IItemHandler>();
		energyHandlerMap = new HashMap<EnumFacing, IEnergyStorageHandler>();
		fluidHandlerMap = new HashMap<EnumFacing, IFluidHandler>();
		if(this instanceof IInventory){
			if(this instanceof ISidedInventory){
				ISidedInventory thisInv = (ISidedInventory) this;
				for(EnumFacing f : EnumFacing.VALUES){
					itemHandlerSidedMap.put(f, new SidedInvWrapper(thisInv, f));
				}
			}else{
				IInventory thisInv = (IInventory) this;
				for(EnumFacing f : EnumFacing.VALUES){
					itemHandlerSidedMap.put(f, new InvWrapper(thisInv));
				}
			}
		}
		if(this instanceof IEnergyHandler){
			IEnergyHandler thisHandler = (IEnergyHandler) this;
			for(EnumFacing f : EnumFacing.VALUES){
				energyHandlerMap.put(f, new EnergyHandlerNormal(thisHandler, f));
			}
		}else if(this instanceof IEnergyReceiver){
			IEnergyReceiver thisHandler = (IEnergyReceiver) this;
			for(EnumFacing f : EnumFacing.VALUES){
				energyHandlerMap.put(f, new EnergyHandlerReceiver(thisHandler, f));
			}
		}else if(this instanceof IEnergyProvider){
			IEnergyProvider thisHandler = (IEnergyProvider) this;
			for(EnumFacing f : EnumFacing.VALUES){
				energyHandlerMap.put(f, new EnergyHandlerProvider(thisHandler, f));
			}
		}
		if(this instanceof ITileFluidHandler){
			ITileFluidHandler thisFluid = (ITileFluidHandler) this;
			for(EnumFacing f : EnumFacing.VALUES){
				fluidHandlerMap.put(f, new FluidHandlerWrapper(f, thisFluid));
			}
		}
	}
	@Override
	public final <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return this.<T>getInstance(itemHandlerSidedMap, facing, capability);
		}
		if (capability == EnergyType.ENERGY_HANDLER_CAPABILITY){
			return this.<T>getInstance(energyHandlerMap, facing, capability);
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return this.<T>getInstance(fluidHandlerMap, facing, capability);
		}
		return super.getCapability(capability, facing);
	}
	@SuppressWarnings("unchecked")
	private final <T> T getInstance(Map<EnumFacing, ?> in, EnumFacing f, Capability<T> capability){
		try{
			return (T) (f == null ? in.get(EnumFacing.DOWN) : in.get(f));
		}catch(ClassCastException e){}
		return super.getCapability(capability, f);
	}
	@Override
	public final boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this instanceof IInventory) || (capability == EnergyType.ENERGY_HANDLER_CAPABILITY && this instanceof IEnergyStorageTile) || (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this instanceof ITileFluidHandler) || super.hasCapability(capability, facing);
	}
	@Override
	public IBlockState getActualState(IBlockState state) {
		return getExtendedState(state);
	}
}
