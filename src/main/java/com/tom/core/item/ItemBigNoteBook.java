package com.tom.core.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.item.ICustomCraftingHandler;
import com.tom.core.research.ResearchHandler;

public class ItemBigNoteBook extends Item implements ICustomCraftingHandler {

	@Override
	public void onCrafing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory) {
		returnStack.setTagCompound(new NBTTagCompound());
		returnStack.getTagCompound().setString("owner", crafter.getName());
		ResearchHandler.getHandlerFromName(crafter.getName());
	}

	@Override
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("owner")) {
			tooltip.add("Owner: " + stack.getTagCompound().getString("owner"));
		}
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking() && !world.isRemote) {
			return setOwner(player, stack) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
		} else
			return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		if (player.isSneaking() && !world.isRemote) {
			ItemStack stack = player.getHeldItem(handIn);
			return setOwner(player, stack) ? new ActionResult<>(EnumActionResult.SUCCESS, stack) : new ActionResult<>(EnumActionResult.FAIL, stack);
		}else
			return super.onItemRightClick(world, player, handIn);
	}

	private boolean setOwner(EntityPlayer player, ItemStack stack){
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = stack.getTagCompound();
		String owner = tag.getString("owner");
		if (owner.equals(player.getName()))
			return false;
		else {
			tag.setString("owner", player.getName());
			ResearchHandler.getHandlerFromName(player.getName());
			return true;
		}
	}

	@Override
	public void onUsing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory, ItemStack stack) {
	}
}
