package com.tom.energy.item;

import net.minecraft.item.ItemBlock;

import com.tom.energy.EnergyInit;

public class WirelessChargerItemBlock extends ItemBlock {

	public WirelessChargerItemBlock() {
		super(EnergyInit.wirelessCharger);
		setHasSubtypes(true);
	}
}
