package com.tom.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.tom.api.item.ItemCraftingTool;
import com.tom.core.TMResource;
import com.tom.recipes.handler.MachineCraftingHandler;

public class Hammer extends ItemCraftingTool {
	@Override
	public int getDurability(ItemStack stack) {
		return TMResource.getDurability(stack.getMetadata());
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + TMResource.get(stack.getMetadata()).getName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(I18n.format("tomsMod.tooltip.tier") + ": " + TMResource.get(stack.getMetadata()).getToolTier());
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		TMResource.addHammersToList(subItems, itemIn);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();

		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double) (TMResource.get(stack.getMetadata()).getToolTier() * 3) + 1, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.5D, 0));
		}

		return multimap;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		IBlockState state = player.world.getBlockState(pos);
		List<ItemStack> stack = MachineCraftingHandler.getHammerResult(TMResource.get(itemstack.getMetadata()).getToolTier(), state, itemRand);
		if (stack == null)
			return false;
		else {
			damageItem(itemstack, 1, player);
			player.world.setBlockToAir(pos);
			player.world.playEvent(player, 2001, pos, Block.getStateId(state));
			stack.forEach(s -> Block.spawnAsEntity(player.world, pos, s));
			return true;
		}
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		int tool = TMResource.get(stack.getMetadata()).getToolTier();
		return MachineCraftingHandler.getHammerResult(tool, state, itemRand) != null ? tool * 10F : 1.0F;
	}

	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
		int tool = TMResource.get(stack.getMetadata()).getToolTier();
		return MachineCraftingHandler.getHammerResult(tool, state, itemRand) != null ? true : false;
	}
}
