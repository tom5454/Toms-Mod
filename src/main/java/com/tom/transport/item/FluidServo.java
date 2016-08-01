package com.tom.transport.item;

import net.minecraft.util.EnumFacing;

import com.tom.api.item.ModuleItem;
import com.tom.api.multipart.PartModule;
import com.tom.transport.multipart.PartFluidServo;

public class FluidServo extends ModuleItem {

	@Override
	public PartModule<?> createPart(EnumFacing side) {
		return new PartFluidServo(side);
	}

}
