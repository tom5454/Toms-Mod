package com.tom.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

import com.tom.api.item.IMagCard;
import com.tom.core.CoreInit;

public class ElectricalMagCard extends Item implements IMagCard {

	@Override
	public boolean isCodeEqual(ItemStack is, World world, String[] code, EntityPlayer player) {
		InventoryPlayer inv = player.inventory;
		NBTTagCompound modemTag = null;
		for(int i = 0;i<inv.getSizeInventory();i++){
			ItemStack c = inv.getStackInSlot(i);
			if(c != null && c.getItem() == CoreInit.connectionModem){
				modemTag = c.getTagCompound();
				break;
			}
		}
		if(modemTag != null){
			int t = modemTag.hasKey("tierMagCard") ? modemTag.getInteger("tierMagCard") : 0;
			if(modemTag.hasKey("magCodeList")){
				NBTTagList list = (NBTTagList) modemTag.getTag("magCodeList");
				for(int i = 0;i<list.tagCount();i++){
					String cCode = list.getStringTagAt(i);
					for(String c : code){
						if(cCode.equals(c)){
							return true;
						}
					}
				}
			}
			if(t > 0){
				//int numOfChars = t % 2;
				/*int numOfNums = t % 2;
				int numOfEvens = t % 2 == 1 ? t : (t - 1) % 2;*/
				for(String c : code){
					if(c.length() <= t){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isCopyable(ItemStack is, World world) {
		return false;
	}

	@Override
	public String[] getCodes(ItemStack is, World world, EntityPlayer player) {
		InventoryPlayer inv = player.inventory;
		NBTTagCompound modemTag = null;
		for(int i = 0;i<inv.getSizeInventory();i++){
			ItemStack c = inv.getStackInSlot(i);
			if(c != null && c.getItem() == CoreInit.connectionModem){
				modemTag = c.getTagCompound();
				break;
			}
		}
		if(modemTag != null){
			if(modemTag.hasKey("magCodeList")){
				NBTTagList list = (NBTTagList) modemTag.getTag("magCodeList");
				String[] ret = new String[list.tagCount()];
				for(int i = 0;i<list.tagCount();i++){
					ret[i] = list.getStringTagAt(i);
				}
				return ret;
			}
		}
		return new String[]{};
	}

	@Override
	public void write(String code, String name, ItemStack is, World world, EntityPlayer player) {
		InventoryPlayer inv = player.inventory;
		NBTTagCompound modemTag = null;
		ItemStack modemStack = null;
		for(int i = 0;i<inv.getSizeInventory();i++){
			ItemStack c = inv.getStackInSlot(i);
			if(c != null && c.getItem() == CoreInit.connectionModem){
				modemTag = c.getTagCompound();
				modemStack = c;
				break;
			}
		}
		if(modemTag == null && modemStack != null) modemTag = new NBTTagCompound();
		if(modemTag.hasKey("magCodeList")){
			NBTTagList list = (NBTTagList) modemTag.getTag("magCodeList");
			list.appendTag(new NBTTagString(code));
			modemTag.setTag("magCodeList", list);
		}else{
			NBTTagList list = new NBTTagList();
			list.appendTag(new NBTTagString(code));
			modemTag.setTag("magCodeList", list);
		}
	}

}
