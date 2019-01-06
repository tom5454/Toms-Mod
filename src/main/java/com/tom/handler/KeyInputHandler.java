package com.tom.handler;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.client.EventHandlerClient;
import com.tom.core.CoreInit;
import com.tom.core.Keybindings;
import com.tom.lib.Keys;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageKey;
import com.tom.network.messages.MessageProfiler;

import com.tom.core.tileentity.TileEntityTabletController;

public class KeyInputHandler {
	public static final KeyInputHandler instance = new KeyInputHandler();
	public Set<Keybindings> down = new HashSet<>();

	@SideOnly(Side.CLIENT)
	private Keybindings getPressedKey() {
		if (Keys.isPressed(Keys.CONFIG)) {
			return Keybindings.CONFIG;
			/*} else if (Keys.isPressed(Keys.UP)) {
			return Keybindings.UP;
		} else if (Keys.isPressed(Keys.DOWN)) {
			return Keybindings.DOWN;
		} else if (Keys.isPressed(Keys.LEFT)) {
			return Keybindings.LEFT;
		} else if (Keys.isPressed(Keys.RIGHT)) {
			return Keybindings.RIGHT;
		} else if (Keys.isPressed(Keys.BACK)) {
			return Keybindings.BACK;
		} else if (Keys.isPressed(Keys.ENTER)) {
			return Keybindings.ENTER;
		} else if (Keys.isPressed(Keys.INTERACT)) {
			return Keybindings.INTERACT;
		} else if (Keys.isPressed(Keys.MENU)) {
			return Keybindings.MENU;*/
		} else if (Keys.isPressed(Keys.PROFILE)) {
			return Keybindings.PROFILE;
		} else if (Keys.isPressed(Keys.SHOW_TETXURE_MAP)) {
			return Keybindings.TEXTURE_MAP;
			/*} else if (Keys.isPressed(Keys.FUNCTION1)) {
			return Keybindings.FUNCTION1;
		} else if (Keys.isPressed(Keys.FUNCTION2)) {
			return Keybindings.FUNCTION2;
		} else if (Keys.isPressed(Keys.FUNCTION3)) {
			return Keybindings.FUNCTION3;
		} else if (Keys.isPressed(Keys.FUNCTION4)){
			return Keybindings.FUNCTION4;*/
		}
		return Keybindings.UNKNOWN;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleKeyInputEvent(InputEvent.KeyInputEvent event) {
		Keybindings key = this.getPressedKey();
		if (key != Keybindings.UNKNOWN)
			NetworkHandler.sendToServer(new MessageKey(key, false));

	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void clientUpdateEvent(TickEvent.ClientTickEvent event) {
		Minecraft.getMinecraft().mcProfiler.startSection("[Tom's Mod] Key Handler");
		interact(Keys.UP, Keybindings.UP);
		interact(Keys.DOWN, Keybindings.DOWN);
		interact(Keys.LEFT, Keybindings.LEFT);
		interact(Keys.RIGHT, Keybindings.RIGHT);

		interact(Keys.BACK, Keybindings.BACK);
		interact(Keys.ENTER, Keybindings.ENTER);
		interact(Keys.MENU, Keybindings.MENU);
		interact(Keys.INTERACT, Keybindings.INTERACT);

		interact(Keys.FUNCTION1, Keybindings.FUNCTION1);
		interact(Keys.FUNCTION2, Keybindings.FUNCTION2);
		interact(Keys.FUNCTION3, Keybindings.FUNCTION3);
		interact(Keys.FUNCTION4, Keybindings.FUNCTION4);
		Minecraft.getMinecraft().mcProfiler.endSection();
	}

	public void handlerKeyServer(Keybindings key, EntityPlayer player, boolean up) {
		switch (key) {
		case BACK:
			this.handleTablet("back", player, up);
			break;
		case DOWN:
			this.handleTablet("down", player, up);
			break;
		case ENTER:
			this.handleTablet("enter", player, up);
			break;
		case LEFT:
			this.handleTablet("left", player, up);
			break;
		case RIGHT:
			this.handleTablet("right", player, up);
			break;
		case UP:
			this.handleTablet("up", player, up);
			break;
		case INTERACT:
			this.handleTablet("interact", player, up);
			break;
		case MENU:
			this.handleTablet("menu", player, up);
			break;
		case PROFILE:
			EventHandlerClient.getInstance().profile = !EventHandlerClient.getInstance().profile;
			MessageProfiler.sendKey("", EventHandlerClient.getInstance().profile);
			break;
		case TEXTURE_MAP:
			EventHandlerClient.getInstance().showTextureMap = !EventHandlerClient.getInstance().showTextureMap;
			break;
		case CONFIG:

			break;
		case FUNCTION1:
			this.handleTablet("func1", player, up);
			break;
		case FUNCTION2:
			this.handleTablet("func2", player, up);
			break;
		case FUNCTION3:
			this.handleTablet("func3", player, up);
			break;
		case FUNCTION4:
			this.handleTablet("func4", player, up);
			break;
		default:
			break;
		}

	}

	private void handleTablet(String key, EntityPlayer player, boolean up) {
		InventoryPlayer inv = player.inventory;
		for (int i = 0;i < 9;i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (is != null && is.getItem() == CoreInit.Tablet) {
				if (is.getTagCompound() != null && is.getTagCompound().hasKey("x") && is.getTagCompound().hasKey("y") && is.getTagCompound().hasKey("z")) {
					TileEntity tile = player.world.getTileEntity(new BlockPos(is.getTagCompound().getInteger("x"), is.getTagCompound().getInteger("y"), is.getTagCompound().getInteger("z")));
					if (tile instanceof TileEntityTabletController) {
						TileEntityTabletController te = (TileEntityTabletController) tile;
						if(up)key = key + "_up";
						te.queueEvent("tablet_button", new Object[]{player.getName(), key});
						return;
					}
				}
			}
		}
	}

	public void interact(KeyBinding key, Keybindings k){
		if(key == null)return;
		if(key.isKeyDown()){
			if(!down.contains(k)){
				down.add(k);
				NetworkHandler.sendToServer(new MessageKey(k, false));
			}
		}else if(down.contains(k)){
			down.remove(k);
			NetworkHandler.sendToServer(new MessageKey(k, true));
		}
	}
}
