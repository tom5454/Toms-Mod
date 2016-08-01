package com.tom.core.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TrProcessor extends Item {
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean isAdvanced)
	{
		super.addInformation(itemStack, player, list, isAdvanced);
		if (itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("tier")) {
			int tier = itemStack.getTagCompound().getInteger("tier");
			String d = itemStack.getTagCompound().hasKey("d") ? itemStack.getTagCompound().getString("d") : "ap";
			boolean s = itemStack.getTagCompound().hasKey("s") ? itemStack.getTagCompound().getBoolean("s") : false;
			if(!s) list.add(I18n.format("tomsMod.tooltip.tier")+": "+tier);
			list.add(I18n.format("tomsMod.tooltip.device", (d == "an" ? I18n.format("tile.antenna.name") : (d == "m" ? I18n.format("item.eMagCard.name") : I18n.format("tile.tabletAccessPoint.name")))));
		}else{
			list.add(TextFormatting.RED + I18n.format("tomsMod.tooltip.tierInvalid"));
		}
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack is,
			World world, EntityPlayer player, EnumHand hand) {
		if(player.capabilities.isCreativeMode){
			boolean sneak = player.isSneaking();
			boolean sprint = player.rotationPitch < -45;
			if (is.getTagCompound() != null && is.getTagCompound().hasKey("tier")) {
				if(!sprint){
					if(sneak){
						is.getTagCompound().setInteger("tier", is.getTagCompound().getInteger("tier") - 1);
					}else{
						is.getTagCompound().setInteger("tier", is.getTagCompound().getInteger("tier") + 1);
					}
				}else{
					if(sneak){
						is.getTagCompound().setBoolean("s", is.getTagCompound().hasKey("s") ? !is.getTagCompound().getBoolean("s") : true);
					}else{
						is.getTagCompound().setString("d", is.getTagCompound().hasKey("d") ? is.getTagCompound().getString("d") == "ap" ? "an" : (is.getTagCompound().getString("d") == "an" ? "m" : "ap") : "ap");
					}
				}
			}else if(sneak){
				is.setTagCompound(new NBTTagCompound());
				is.getTagCompound().setInteger("tier", 1);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, is);
	}
}
