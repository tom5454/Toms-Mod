package com.tom.api.energy;

import java.util.Map;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;

public interface IRFMachine {
	@SuppressWarnings("rawtypes")
	Map<Capability, Map<EnumFacing, Object>> initCapabilities();
}
