package com.tom.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler;

public class NetworkInit {
	public static void init(){
		CoreInit.log.info("Init the Network");
		NetworkHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(CoreInit.modInstance, new GuiHandler());
	}
}
