package com.tom.api.energy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import com.tom.apis.EmptyEntry;
import com.tom.lib.Configs;

import cofh.api.energy.IEnergyReceiver;

public interface IRFReceiver extends IEnergyReceiver, IRFMachine {
	long receiveRF(EnumFacing side, long maxReceive, boolean simulate);

	@Override
	default int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return canConnectEnergy(from) ? (int) receiveRF(from, maxReceive, simulate) : 0;
	}

	@Override
	@SuppressWarnings("rawtypes")
	default Map<Capability, Map<EnumFacing, Object>> initCapabilities() {
		Map<Capability, Map<EnumFacing, Object>> caps = new HashMap<>();
		if (Loader.isModLoaded(Configs.TESLA)) {
			Entry<Capability, Map<EnumFacing, Object>> c = createTeslaCapability(this);
			caps.put(c.getKey(), c.getValue());
		}
		Map<EnumFacing, Object> forge = new HashMap<>();
		for (EnumFacing f : EnumFacing.VALUES) {
			forge.put(f, new RFStorage(this, f));
		}
		caps.put(CapabilityEnergy.ENERGY, forge);
		return caps;
	}

	@SuppressWarnings("rawtypes")
	@Optional.Method(modid = Configs.TESLA)
	static Entry<Capability, Map<EnumFacing, Object>> createTeslaCapability(IRFReceiver receiver) {
		Map<EnumFacing, Object> forge = new HashMap<>();
		for (EnumFacing f : EnumFacing.VALUES) {
			forge.put(f, new ITeslaConsumer() {

				@Override
				public long givePower(long power, boolean simulated) {
					return receiver.receiveRF(f, power, simulated);
				}
			});
		}
		return new EmptyEntry<>(TeslaCapabilities.CAPABILITY_CONSUMER, forge);
	}
}
