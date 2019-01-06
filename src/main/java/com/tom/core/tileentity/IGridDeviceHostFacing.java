package com.tom.core.tileentity;

import net.minecraft.util.EnumFacing;

import com.tom.lib.api.grid.IGrid;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridDeviceHost;

public interface IGridDeviceHostFacing {
	<G extends IGrid<?, G>> IGridDevice<G> getDevice(EnumFacing facing, Class<G> gridClass, Object... objects);
	public static class Wrapper implements IGridDeviceHost {
		private final EnumFacing facing;
		private final IGridDeviceHostFacing host;

		public Wrapper(IGridDeviceHostFacing host, EnumFacing facing) {
			this.host = host;
			this.facing = facing;
		}

		@Override
		public <G extends IGrid<?, G>> IGridDevice<G> getDevice(Class<G> gridClass, Object... objects) {
			return host.getDevice(facing, gridClass, objects);
		}

	}
}
