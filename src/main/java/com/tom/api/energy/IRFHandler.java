package com.tom.api.energy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import com.tom.apis.EmptyEntry;
import com.tom.lib.Configs;

public interface IRFHandler extends IRFProvider, IRFReceiver {
	@Override
	long extractRF(EnumFacing side, long maxExtract, boolean simulate);

	@Override
	long receiveRF(EnumFacing side, long maxReceive, boolean simulate);

	@Override
	default int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return canConnectEnergy(from) ? (int) receiveRF(from, maxReceive, simulate) : 0;
	}

	@Override
	default int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return canConnectEnergy(from) ? (int) extractRF(from, maxExtract, simulate) : 0;
	}

	@Override
	@SuppressWarnings("rawtypes")
	default Map<Capability, Map<EnumFacing, Object>> initCapabilities() {
		Map<Capability, Map<EnumFacing, Object>> caps = new HashMap<>();
		if (Loader.isModLoaded(Configs.TESLA)) {
			Entry<Capability, Map<EnumFacing, Object>>[] c = createTeslaCapability(this);
			for (Entry<Capability, Map<EnumFacing, Object>> e : c)
				caps.put(e.getKey(), e.getValue());
		}
		Map<EnumFacing, Object> forge = new HashMap<>();
		for (EnumFacing f : EnumFacing.VALUES) {
			forge.put(f, new RFStorage(this, f, false));
		}
		caps.put(CapabilityEnergy.ENERGY, forge);
		return caps;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Optional.Method(modid = Configs.TESLA)
	static Entry<Capability, Map<EnumFacing, Object>>[] createTeslaCapability(IRFHandler handler) {
		Map<EnumFacing, Object> forge = new HashMap<>();
		Map<EnumFacing, Object> forge2 = new HashMap<>();
		for (EnumFacing f : EnumFacing.VALUES) {
			forge.put(f, new ITeslaConsumer() {

				@Override
				public long givePower(long power, boolean simulated) {
					return handler.receiveRF(f, power, simulated);
				}
			});
			forge2.put(f, new ITeslaProducer() {

				@Override
				public long takePower(long power, boolean simulated) {
					return handler.extractRF(f, power, simulated);
				}
			});
		}
		return new EmptyEntry[]{new EmptyEntry<>(TeslaCapabilities.CAPABILITY_CONSUMER, forge), new EmptyEntry<>(TeslaCapabilities.CAPABILITY_PRODUCER, forge2)};
	}
}
