package com.tom.proxy;

import com.tom.core.CoreInit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public abstract class CommonProxy{
	//public int SPECIAL_RENDER_TYPE_VALUE;
	public abstract void preInit();

	public abstract void init();

	public abstract void postInit();

	public abstract EntityPlayer getClientPlayer();

	@SuppressWarnings("rawtypes")
	public int getRenderIdForRenderer(Class clazz) {
		return 0;
	}
	public void registerRenders(){
		CoreInit.log.info("Skipping model loading, not client.");
	}
	public void registerKeyBindings(){

	}
	public void registerItemRender(Item item, int meta, String rL){

	}
	public abstract void serverStart();
}