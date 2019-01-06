package com.tom.handler;

import static com.tom.core.CoreInit.log;

import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

import com.tom.core.map.MapHandler;
import com.tom.lib.api.map.IMapAPI;

public class IMCHandler {
	public static void receive(IMCMessage msg) throws Exception {
		if (msg.key.equalsIgnoreCase("registerMapApi")) {
			if(msg.isStringMessage()){
				Class<?> clazz = Class.forName(msg.getStringValue());
				IMapAPI api = (IMapAPI) clazz.newInstance();
				MapHandler.mapHandlers.add(api);
				api.init();
				log.info("Successfully loaded map handler");
			} else {
				log.error(String.format("Mod %s sent an invalid message. The message has to be a 'string' type. Report this to the mod author.", msg.getSender()));
			}
		} else {
			log.error(String.format("Mod %s sent an unregistered message. Report this to the mod author.", msg.getSender()));
		}
	}
}
