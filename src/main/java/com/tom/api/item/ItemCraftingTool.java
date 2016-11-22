package com.tom.api.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.core.CoreInit;

public abstract class ItemCraftingTool extends ItemDamagableCrafting {
	public ItemCraftingTool() {
		setCreativeTab(CoreInit.tabTomsModWeaponsAndTools);
		setHasSubtypes(true);
	}
	@Override
	public ItemCraftingTool setUnlocalizedName(String unlocalizedName) {
		super.setUnlocalizedName(unlocalizedName);
		return this;
	}
	/**
	 * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
	 */
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState blockIn, BlockPos pos, EntityLivingBase entityLiving)
	{
		boolean player = entityLiving instanceof EntityPlayer;
		if (blockIn.getBlockHardness(worldIn, pos) != 0.0D && (!player || (player && !((EntityPlayer)entityLiving).capabilities.isCreativeMode)))
		{
			if(!stack.hasTagCompound()){
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setInteger("damage", stack.getTagCompound().getInteger("damage") + 1);
		}

		return true;
	}
	@Override
	public ItemCraftingTool setRefillable(boolean refillable){
		this.refillable = refillable;
		return this;
	}
	@Override
	public int getItemDamage(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("damage") : 0;
	}
	@Override
	public void setItemDamage(ItemStack stack, int damage) {
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("damage", damage);
	}
}
