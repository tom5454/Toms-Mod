package com.tom.core;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandBackup extends CommandBase {

	@Override
	public String getCommandName() {
		return "tm_backup";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.tm_backup.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		AutoBackup.startBackup(sender.getName());
	}
	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}
}
