package com.tom.thirdparty.computercraft;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.multipart.IModule;
import com.tom.apis.TomsModUtils;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class MultipartProvider implements IPeripheralProvider {
	public static final MultipartProvider INSTANCE = new MultipartProvider();
	private MultipartProvider() {}
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		IModule m = TomsModUtils.getModule(world, pos, side);
		if(m instanceof IPeripheralPart){
			return ((IPeripheralPart)m).getPeripheral(side);
		}
		return null;
	}

}
