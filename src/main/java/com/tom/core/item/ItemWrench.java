package com.tom.core.item;

import com.tom.api.item.IWrench;
import com.tom.handler.WrenchHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWrench extends Item implements IWrench{
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumHand hand, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		return WrenchHandler.use(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ, hand) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}
	@Override
	public boolean isWrench(ItemStack is, EntityPlayer player) {
		return true;
	}
}
