package com.tom.api.helpers;

import java.util.List;

import dan200.computercraft.api.peripheral.IComputerAccess;

public class ComputerCraftHelper {
	public static void queueEvent(List<IComputerAccess> computers, String event, Object[] o) {
		for (IComputerAccess computer : computers) {
			computer.queueEvent(event, o);
		}
	}
}
