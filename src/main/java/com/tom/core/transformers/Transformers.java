package com.tom.core.transformers;

import java.util.Map;

import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

public class Transformers {
	public static void injectNewFillCmd(MinecraftServer s) {
		Map<String, ICommand> cmdMap = s.getCommandManager().getCommands();
		if (cmdMap.containsKey("fill")) {
			if (!(cmdMap.get("fill") instanceof CommandFillTM)) {
				cmdMap.put("fill", new CommandFillTM());
			}
		}
		if (cmdMap.containsKey("clone")) {
			if (!(cmdMap.get("clone") instanceof CommandCloneTM)) {
				cmdMap.put("clone", new CommandCloneTM());
			}
		}
	}
}
