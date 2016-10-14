package com.tom.core.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.tom.api.research.IScanningInformation;
import com.tom.api.research.IScanningInformation.ScanningInformation;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;

public class ItemMGlass extends Item {
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		if(!playerIn.isSneaking())return EnumActionResult.FAIL;
		ItemStack bookStack = null;
		for(int i = 0;i<playerIn.inventory.getSizeInventory();i++){
			ItemStack current = playerIn.inventory.getStackInSlot(i);
			if(current != null && current.getItem() == CoreInit.noteBook){
				bookStack = current;
				break;
			}
		}
		if(bookStack != null){
			if(bookStack.getTagCompound() == null)bookStack.setTagCompound(new NBTTagCompound());
			NBTTagList sList = bookStack.getTagCompound().hasKey("data", 9) ? bookStack.getTagCompound().getTagList("data", 10) : new NBTTagList();
			IBlockState blockState = worldIn.getBlockState(pos);
			Block block = blockState.getBlock();
			int meta = block.getMetaFromState(blockState);
			for(int i = 0;i<sList.tagCount();i++){
				NBTTagCompound t = sList.getCompoundTagAt(i);
				if(t.getInteger("meta") == meta && block == Block.REGISTRY.getObject(new ResourceLocation(t.getString("modid"), t.getString("blockName")))){
					return EnumActionResult.FAIL;
				}
			}
			IScanningInformation info = new ScanningInformation(block,meta);
			NBTTagCompound infoTag = new NBTTagCompound();
			info.writeToNBT(infoTag);
			sList.appendTag(infoTag);
			bookStack.getTagCompound().setTag("data", sList);
			TomsModUtils.sendNoSpamTranslate(playerIn, TextFormatting.GREEN, "tomsMod.chat.scanningSuccess");
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
}
