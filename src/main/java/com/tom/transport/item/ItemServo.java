package com.tom.transport.item;

import com.tom.api.item.ModuleItem;
import com.tom.api.multipart.PartModule;
import com.tom.transport.multipart.PartServo;

import net.minecraft.util.EnumFacing;

public class ItemServo extends ModuleItem {

	@Override
	public PartModule<?> createPart(EnumFacing side) {
		return new PartServo(side);
	}

}
