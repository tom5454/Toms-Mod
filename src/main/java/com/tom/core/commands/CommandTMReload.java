package com.tom.core.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

import com.tom.core.CoreInit;

public class CommandTMReload extends CommandBase {

	@Override
	public String getName() {
		return "tm_reload";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.tmReload.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 0) {
			throw new WrongUsageException("commands.tmReload.usage", new Object[0]);
		} else {
			server.addScheduledTask(CoreInit::reload);
			notifyCommandListener(sender, this, "commands.tmReload.success", new Object[0]);
		}
	}
}
