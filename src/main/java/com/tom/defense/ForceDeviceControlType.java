package com.tom.defense;

import net.minecraft.util.ResourceLocation;

public enum ForceDeviceControlType {
	HIGH_REDSTONE("tomsmod.gui.highRedstone",new ResourceLocation("textures/blocks/redstone_torch_on.png")),
	LOW_REDSTONE("tomsmod.gui.lowRedstone",new ResourceLocation("textures/blocks/redstone_torch_off.png")),
	SWITCH("tomsmod.gui.switch",new ResourceLocation("tomsmoddefense:textures/items/multitool/switch.png")),
	;
	public static final ForceDeviceControlType[] VALUES = values();
	public final String name;
	public final ResourceLocation iconLocation;
	private ForceDeviceControlType(String name, ResourceLocation iconLocation) {
		this.name = name;
		this.iconLocation = iconLocation;
	}
	public static ForceDeviceControlType get(int index){
		return VALUES[index % VALUES.length];
	}
}
