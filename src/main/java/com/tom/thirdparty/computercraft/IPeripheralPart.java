package com.tom.thirdparty.computercraft;

import net.minecraft.util.EnumFacing;

import dan200.computercraft.api.peripheral.IPeripheral;

public interface IPeripheralPart {
	IPeripheral getPeripheral(EnumFacing side);
}
