package com.tom.api.energy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import com.tom.apis.EmptyEntry;
import com.tom.lib.Configs;

import cofh.api.energy.IEnergyProvider;

public interface IRFProvider extends IEnergyProvider, IRFMachine {
	long extractRF(EnumFacing side, long maxExtract, boolean simulate);

	@Override
	default int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return canConnectEnergy(from) ? (int) extractRF(from, maxExtract, simulate) : 0;
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
	static Entry<Capability, Map<EnumFacing, Object>> createTeslaCapability(IRFProvider provider) {
		Map<EnumFacing, Object> forge = new HashMap<>();
		for (EnumFacing f : EnumFacing.VALUES) {
			forge.put(f, new ITeslaProducer() {

				@Override
				public long takePower(long power, boolean simulated) {
					return provider.extractRF(f, power, simulated);
				}
			});
		}
		return new EmptyEntry<>(TeslaCapabilities.CAPABILITY_PRODUCER, forge);
	}
}
