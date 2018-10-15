package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.tom.api.tileentity.IAccessPoint;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.api.tileentity.ITMPeripheral.IComputer;
import com.tom.lib.api.tileentity.ITMPeripheral.ITMCompatPeripheral;

import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileEntityAntennaController extends TileEntityTomsMod implements ITMCompatPeripheral {
	private List<IComputerAccess> computers = new ArrayList<>();
	private List<IAccessPoint> antennas = new ArrayList<>();
	public String[] methods = {"listMethods", "sendMsg"};

	public void receive(String pName, Object msg) {

	}

	@Override
	public String getType() {
		return "antennaController";
	}

	@Override
	public String[] getMethodNames() {
		return this.methods;
	}

	@Override
	public Object[] callMethod(IComputer comp, int method, Object[] a) throws LuaException {
		if (method == 0) {
			Object[] o = new Object[methods.length];
			for (int i = 0;i < o.length;i++) {
				o[i] = methods[i];
			}
			return o;
		} else if (method == 1) {
			if (a.length > 1 && a[0] instanceof String) {
				String pName = (String) a[0];
				for (IAccessPoint ant : this.antennas) {
					//ant.sendMsg(pName, a[1]);
				}
			}
		}
		return null;
	}

	/*public void link(TileEntityAntennaBase a) {
		this.antennas.add(a);
	}

	public void disConnect(TileEntityAntennaBase te) {
		this.antennas.remove(te);
	}*/

	public void queueEvent(String event, Object[] args) {
		// System.out.println("queueEvent");
		for (IComputerAccess c : this.computers) {
			c.queueEvent(event, args);
		}
	}
}
