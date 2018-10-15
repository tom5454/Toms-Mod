package com.tom.api.helpers;

import java.util.List;

import com.tom.lib.api.tileentity.ITMPeripheral.IComputer;

public class ComputerHelper {
	public static void queueEvent(List<IComputer> computers, String event, Object[] o) {
		for (IComputer computer : computers) {
			computer.queueEvent(event, o);
		}
	}
}
