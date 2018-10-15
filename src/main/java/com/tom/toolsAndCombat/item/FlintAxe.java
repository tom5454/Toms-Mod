package com.tom.toolsAndCombat.item;

import net.minecraft.item.ItemAxe;

public class FlintAxe extends ItemAxe {

	public FlintAxe(ToolMaterial mat, float attackSpeed) {
		super(ToolMaterial.WOOD);
		this.attackDamage = mat.getAttackDamage();
		this.attackSpeed = attackSpeed;
		toolMaterial = mat;
	}

}
