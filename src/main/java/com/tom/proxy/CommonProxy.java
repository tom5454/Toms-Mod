package com.tom.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import com.tom.api.block.IMethod;
import com.tom.core.CoreInit;

public abstract class CommonProxy{//ClientProxy
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

	public abstract void construction();
	public abstract void runMethod(IMethod m);
}