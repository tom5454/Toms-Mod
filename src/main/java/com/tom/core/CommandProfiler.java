package com.tom.core;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import com.tom.client.EventHandlerClient;
import com.tom.network.messages.MessageProfiler;

public class CommandProfiler extends CommandBase {

	@Override
	public String getName() {
		return "tm_profiler";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "tm_profiler <name...>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1)
			throw new CommandException("Not enough args");
		if (sender instanceof EntityPlayer) {
			StringBuilder b = new StringBuilder();
			for (int i = 0;i < args.length;i++) {
				b.append(args[i]);
				if (i + 1 != args.length)
					b.append(' ');
			}
			String t = b.toString();
			System.out.println(t);
			MessageProfiler.sendKey(EventHandlerClient.getInstance().list.stream().anyMatch(r -> r.profilerName.equals(t)) ? t : "", EventHandlerClient.getInstance().profile);
		} else {
			throw new CommandException("Not a player");
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return CoreInit.proxy.<String[], List<String>>runClientFunction(args, a -> {
			StringBuilder b = new StringBuilder();
			for (int i = 0;i < args.length;i++) {
				b.append(args[i]);
				if (i + 1 != args.length)
					b.append(' ');
			}
			String t = b.toString();
			return getListOfStringsMatchingLastWord(new String[]{t}, EventHandlerClient.getInstance().list.subList(1, EventHandlerClient.getInstance().list.size()).stream().map(r -> r.profilerName).collect(Collectors.toList()));
		});
	}
}
