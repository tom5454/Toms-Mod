package com.tom.core.item;

import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.block.BlockRubberWood;
import com.tom.core.block.BlockRubberWood.WoodType;
import com.tom.core.block.BlockTreeTap;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemTreeTap extends Item {
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = worldIn.getBlockState(pos);
		if(state != null && state.getBlock() == CoreInit.rubberWood){
			WoodType t = state.getValue(BlockRubberWood.TYPE);
			if(facing != t.getFacing()){
				TomsModUtils.sendNoSpamTranslate(playerIn, TextFormatting.RED, "tomsMod.chat.treeTapFailWrongSide");
				return EnumActionResult.FAIL;
			}else{
				if(t.isHole()){
					TomsModUtils.sendNoSpamTranslate(playerIn, TextFormatting.RED, "tomsMod.chat.treeTapFailNotOpened");
					return EnumActionResult.FAIL;
				}else if(t.isCutHole()){
					IBlockState stateF = worldIn.getBlockState(pos.offset(facing));
					if(stateF.getBlock().isReplaceable(worldIn, pos.offset(facing))){
						if (stack.stackSize != 0 && playerIn.canPlayerEdit(pos.offset(facing), facing, stack) && worldIn.canBlockBePlaced(CoreInit.blockTreetap, pos.offset(facing), false, facing, (Entity)null, stack)){
							worldIn.setBlockState(pos.offset(facing), CoreInit.blockTreetap.getDefaultState().withProperty(BlockTreeTap.FACING, facing).withProperty(BlockTreeTap.STATE, 0), 3);
							SoundType soundtype = Blocks.PLANKS.getSoundType();
							worldIn.playSound(playerIn, pos.offset(facing), soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
							--stack.stackSize;
							return EnumActionResult.SUCCESS;
						}else
							return EnumActionResult.FAIL;
					}else
						return EnumActionResult.PASS;
				}else return EnumActionResult.FAIL;
			}
		}else
			return EnumActionResult.PASS;
	}
}
