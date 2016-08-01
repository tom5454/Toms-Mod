package com.tom.network;

import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler;

import net.minecraftforge.fml.common.network.NetworkRegistry;

public class NetworkInit {
	public static void init(){
		CoreInit.log.info("Init the Network");
		NetworkHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(CoreInit.modInstance, new GuiHandler());
	}
}
