package com.tom.weaponsAndTools.item;

import net.minecraft.item.ItemAxe;

public class FlintAxe extends ItemAxe {

	public FlintAxe(ToolMaterial mat, float attackSpeed) {
		super(ToolMaterial.WOOD);
		this.damageVsEntity = mat.getDamageVsEntity();
		this.attackSpeed = attackSpeed;
		toolMaterial = mat;
	}

}
