package com.tom.core.item;

import java.util.List;

import com.tom.api.item.ICustomCraftingHandler;
import com.tom.core.research.handler.ResearchHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBigNoteBook extends Item implements ICustomCraftingHandler {

	@Override
	public void onCrafing(EntityPlayer crafter, ItemStack returnStack,
			IInventory crafingTableInventory) {
		returnStack.setTagCompound(new NBTTagCompound());
		returnStack.getTagCompound().setString("owner", crafter.getName());
		ResearchHandler.getHandlerFromName(crafter.getName());
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn,
			List<String> tooltip, boolean advanced) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("owner")){
			tooltip.add("Owner: "+stack.getTagCompound().getString("owner"));
		}
	}
	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack,
			EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ, EnumHand hand) {
		if(player.isSneaking()){
			if(stack.getTagCompound() == null)stack.setTagCompound(new NBTTagCompound());
			NBTTagCompound tag = stack.getTagCompound();
			String owner = tag.getString("owner");
			if(owner.equals(player.getName())) return EnumActionResult.FAIL;
			else{
				tag.setString("owner", player.getName());
				ResearchHandler.getHandlerFromName(player.getName());
				return EnumActionResult.SUCCESS;
			}
		}else return super.onItemUseFirst(stack, player, world, pos, side, hitX, hitY, hitZ, hand);
	}
	@Override
	public void onUsing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory,
			ItemStack stack) {

	}
}
