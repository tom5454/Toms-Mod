package com.tom.core;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class DamageSourceTomsMod {
	public static DamageSource fieldDamage = new DamageSource("fieldDamage"){
		/**
		 * Gets the death message that is displayed when the player dies
		 */
		@Override
		public ITextComponent getDeathMessage(EntityLivingBase p_151519_1_)
		{
			return new TextComponentTranslation("death.friedByField", new Object[] {p_151519_1_.getDisplayName()});
		}
	}.setDamageBypassesArmor().setDamageIsAbsolute();
	public static DamageSource securityDamage = new DamageSource("securityDamage"){
		/**
		 * Gets the death message that is displayed when the player dies
		 */
		@Override
		public ITextComponent getDeathMessage(EntityLivingBase p_151519_1_)
		{
			return new TextComponentTranslation("death.friedByDefense", new Object[] {p_151519_1_.getDisplayName()});
		}
	}.setDamageBypassesArmor().setDamageIsAbsolute();
}
