package com.tom.api.tileentity;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;


public interface IPeripheralProxyControllable {
	 String[] getMethodNames();
	 Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException;
	 String getName();
}
