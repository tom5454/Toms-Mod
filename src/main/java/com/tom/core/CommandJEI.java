package com.tom.core;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import com.tom.thirdparty.jei.JEIHandler;

import mezz.jei.JustEnoughItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.startup.JeiStarter;
import mezz.jei.startup.ProxyCommonClient;

public class CommandJEI extends CommandBase {

	@Override
	public String getName() {
		return "jei_reload";
	}

	@Override
	public String getUsage(ICommandSender sender) {//
		return "jei_reload";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		try {
			Field starter = ProxyCommonClient.class.getDeclaredField("starter");
			starter.setAccessible(true);
			JeiStarter s = (JeiStarter) starter.get(JustEnoughItems.getProxy());
			Field plugins = ProxyCommonClient.class.getDeclaredField("plugins");
			plugins.setAccessible(true);
			List<IModPlugin> p = (List<IModPlugin>) plugins.get(JustEnoughItems.getProxy());
			if (!p.stream().anyMatch(o -> o instanceof JEIHandler)) {
				p.add(new JEIHandler());
			}
			if (s.hasStarted()) {
				s.start(p);
			}
		} catch (Exception e) {
			throw new CommandException("JEI Reload failed");
		}
	}
}
