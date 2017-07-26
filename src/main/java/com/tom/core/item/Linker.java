package com.tom.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.tom.api.item.ILinkContainer;
import com.tom.api.tileentity.ILinkable;
import com.tom.apis.ExtraBlockHitInfo;
import com.tom.apis.TomsModUtils;

public class Linker extends Item implements ILinkContainer {
	public Linker() {
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer entity, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack itemStack = entity.getHeldItem(hand);
		if (!world.isRemote) {
			if (entity.isSneaking()) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("x", pos.getX());
				tag.setInteger("y", pos.getY());
				tag.setInteger("z", pos.getZ());
				tag.setInteger("side", side.ordinal());
				tag.setFloat("hX", hitX);
				tag.setFloat("hY", hitY);
				tag.setFloat("hZ", hitZ);
				tag.setInteger("dim", world.provider.getDimension());
				itemStack.setTagCompound(tag);
				TomsModUtils.sendNoSpamTranslate(entity, "tomsMod.chat.posSaved");
				return EnumActionResult.SUCCESS;
			} else {
				TileEntity tilee = world.getTileEntity(pos);
				NBTTagCompound tag = itemStack.getTagCompound();
				if (tag != null && tilee instanceof ILinkable && tag.hasKey("x") && tag.hasKey("y") && tag.hasKey("z")) {
					ILinkable te = (ILinkable) tilee;
					boolean success = te.link(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"), EnumFacing.VALUES[tag.getInteger("side")], new ExtraBlockHitInfo(tag.getFloat("hX"), tag.getFloat("hY"), tag.getFloat("hZ")), tag.getInteger("dim"));
					if (success)
						TomsModUtils.sendNoSpamTranslate(entity, TextFormatting.GREEN, "tomsMod.chat.linkSuccessful");
					return success ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
				}
			}
			return EnumActionResult.FAIL;
		}
		return entity.isSneaking() ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}
}
