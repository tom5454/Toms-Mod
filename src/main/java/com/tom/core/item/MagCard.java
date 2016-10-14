package com.tom.core.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.item.IMagCard;

public class MagCard extends Item implements IMagCard{

	@Override
	public boolean isCodeEqual(ItemStack is, World world, String[] code, EntityPlayer player) {
		if(is.getTagCompound() != null && is.getTagCompound().hasKey("code")){
			String c = is.getTagCompound().getString("code");
			for(String cC : code){
				if(c.equals(cC)){
					return true;
				}
			}
		}
		return false;
	}
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List list, boolean isAdvanced)
	{
		super.addInformation(is, player, list, isAdvanced);
		if(is.getTagCompound() != null && is.getTagCompound().hasKey("name")){
			String name = is.getTagCompound().getString("name");
			list.add(name);
		}
	}
	@Override
	public boolean isCopyable(ItemStack is, World world) {
		return is.getTagCompound() != null && is.getTagCompound().hasKey("copy") ? is.getTagCompound().getBoolean("copy") : false;
	}
	@Override
	public String[] getCodes(ItemStack is, World world, EntityPlayer player) {
		if(is.getTagCompound() != null && is.getTagCompound().hasKey("code")){
			String c = is.getTagCompound().getString("code");
			return new String[]{c};
		}
		return new String[]{};
	}
	@Override
	public void write(String code, String name, ItemStack is, World world, EntityPlayer player) {
		if(is.getTagCompound() == null) is.setTagCompound(new NBTTagCompound());
		is.getTagCompound().setString("code", code);
		is.getTagCompound().setString("name", name);
	}

}
