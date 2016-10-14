package com.tom.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.core.CoreInit;
import com.tom.core.Keybindings;
import com.tom.lib.Keys;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageKey;

import com.tom.core.tileentity.TileEntityTabletController;

public class KeyInputHandler{
	public static final KeyInputHandler instance = new KeyInputHandler();
	@SideOnly(Side.CLIENT)
	private Keybindings getPressedKey(){
		if(Keys.isPressed(Keys.UP)){
			return Keybindings.UP;
		}else if(Keys.isPressed(Keys.DOWN)){
			return Keybindings.DOWN;
		}else if(Keys.isPressed(Keys.LEFT)){
			return Keybindings.LEFT;
		}else if(Keys.isPressed(Keys.RIGHT)){
			return Keybindings.RIGHT;
		}else if(Keys.isPressed(Keys.BACK)){
			return Keybindings.BACK;
		}else if(Keys.isPressed(Keys.ENTER)){
			return Keybindings.ENTER;
		}else if(Keys.isPressed(Keys.INTERACT)){
			return Keybindings.INTERACT;
		}else if(Keys.isPressed(Keys.MENU)){
			return Keybindings.MENU;
		}
		return Keybindings.UNKNOWN;
	}
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleKeyInputEvent(InputEvent.KeyInputEvent event){
		Keybindings key = this.getPressedKey();
		if(key != Keybindings.UNKNOWN) NetworkHandler.sendToServer(new MessageKey(key));

	}
	public void handlerKeyServer(Keybindings key, EntityPlayer player){
		switch(key){
		case BACK:
			this.handleTablet("back", player);
			break;
		case DOWN:
			this.handleTablet("down", player);
			break;
		case ENTER:
			this.handleTablet("enter", player);
			break;
		case LEFT:
			this.handleTablet("left", player);
			break;
		case RIGHT:
			this.handleTablet("right", player);
			break;
		case UP:
			this.handleTablet("up", player);
			break;
		case INTERACT:
			this.handleTablet("interact", player);
			break;
		case MENU:
			this.handleTablet("menu", player);
			break;
		default:
			break;
		}

	}
	private void handleTablet(String key, EntityPlayer player){
		InventoryPlayer inv = player.inventory;
		for(int i = 0;i<inv.getSizeInventory();i++){
			ItemStack is = inv.getStackInSlot(i);
			if(is != null && is.getItem() == CoreInit.Tablet){
				if(is.getTagCompound() != null && is.getTagCompound().hasKey("x") && is.getTagCompound().hasKey("y") && is.getTagCompound().hasKey("z") && is.getTagCompound().hasKey("id")){
					TileEntity tile = player.worldObj.getTileEntity(new BlockPos(is.getTagCompound().getInteger("x"), is.getTagCompound().getInteger("y"), is.getTagCompound().getInteger("z")));
					if(tile instanceof TileEntityTabletController){
						TileEntityTabletController te = (TileEntityTabletController) tile;
						te.queueEvent("tablet_button", new Object[]{player.getName(), key});
						return;
					}
				}
			}
		}
	}
}
