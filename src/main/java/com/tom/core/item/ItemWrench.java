package com.tom.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.item.IWrench;
import com.tom.handler.WrenchHandler;

public class ItemWrench extends Item implements IWrench {
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		return WrenchHandler.use(player.getHeldItem(hand), player, world, pos, side, hitX, hitY, hitZ, hand) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}

	@Override
	public boolean isWrench(ItemStack is, EntityPlayer player) {
		return true;
	}

	@Override
	public boolean hasContainerItem() {
		return true;
	}

	@Override
	public Item getContainerItem() {
		return this;
	}
}
