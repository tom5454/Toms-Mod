package com.tom.core.transformers;

import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.tom.config.Config;

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

	public static void checkAndLogFillMessage(Object[] args, ICommandSender sender, MinecraftServer server, BlockPos blockpos2, BlockPos blockpos3, int j) throws CommandException {
		if (Config.commandFillMaxSize > 0 && j > Config.commandFillMaxSize) {
			throw new CommandException("commands.fill.tooManyBlocks", new Object[]{Integer.valueOf(j), Config.commandFillMaxSize});
		} else if (blockpos2.getY() >= 0 && blockpos3.getY() < 256) {
			if (Config.commandFillLogging) {
				boolean destroy = args.length > 7 && args[8].equals("destroy");
				if (j > (destroy ? 4096 : 8192)) {
					String senderName = sender.getName();
					String senderPos = "Dim: " + sender.getEntityWorld().provider.getDimension();
					if (!sender.getPosition().equals(new BlockPos(0, 0, 0))) {
						BlockPos senderBlockPos = sender.getPosition();
						senderPos = senderPos + ", X: " + senderBlockPos.getX() + ", Y: " + senderBlockPos.getY() + ", Z:" + senderBlockPos.getZ();
					} else if (!sender.getPositionVector().equals(new Vec3d(0, 0, 0))) {
						Vec3d senderBlockPos = sender.getPositionVector();
						senderPos = senderPos + ", X: " + senderBlockPos.x + ", Y: " + senderBlockPos.y + ", Z:" + senderBlockPos.z;
					} else {
						senderPos = "Object is not in world!";
					}
					server.logWarning((destroy ? "Destroying" : "Filling") + " " + j + " blocks. " + "Server may lag a bit. Command sender name: " + (senderName != null ? senderName : "~~NULL~~") + " Sender Position: " + senderPos + " " + "Filling from " + blockpos2.getX() + ", " + blockpos2.getY() + ", " + blockpos2.getZ() + " to " + blockpos3.getX() + ", " + blockpos3.getY() + ", " + blockpos3.getZ());
				}
			}
		} else {
			throw new CommandException("commands.fill.outOfWorld", new Object[0]);
		}
	}

	public static void checkAndLogCloneMessage(Object[] args, ICommandSender sender, MinecraftServer server, BlockPos blockpos2, BlockPos blockpos3, int j) throws CommandException {
		if (Config.commandFillMaxSize > 0 && j > Config.commandFillMaxSize) {
			throw new CommandException("commands.clone.tooManyBlocks", new Object[]{Integer.valueOf(j), Config.commandFillMaxSize});
		} else if (blockpos2.getY() >= 0 && blockpos3.getY() < 256) {
			if (Config.commandFillLogging) {
				if (j > 8192) {
					String senderName = sender.getName();
					String senderPos = "Dim: " + sender.getEntityWorld().provider.getDimension();
					if (!sender.getPosition().equals(new BlockPos(0, 0, 0))) {
						BlockPos senderBlockPos = sender.getPosition();
						senderPos = senderPos + ", X: " + senderBlockPos.getX() + ", Y: " + senderBlockPos.getY() + ", Z:" + senderBlockPos.getZ();
					} else if (!sender.getPositionVector().equals(new Vec3d(0, 0, 0))) {
						Vec3d senderBlockPos = sender.getPositionVector();
						senderPos = senderPos + ", X: " + senderBlockPos.x + ", Y: " + senderBlockPos.y + ", Z:" + senderBlockPos.z;
					} else {
						senderPos = "Object is not in world!";
					}
					server.logWarning("Cloning " + j + " blocks. " + "Server may lag a bit. Command sender name: " + (senderName != null ? senderName : "~~NULL~~") + " Sender Position: " + senderPos + " " + "Filling from " + blockpos2.getX() + ", " + blockpos2.getY() + ", " + blockpos2.getZ() + " to " + blockpos3.getX() + ", " + blockpos3.getY() + ", " + blockpos3.getZ());
				}
			}
		} else {
			throw new CommandException("commands.clone.outOfWorld", new Object[0]);
		}
	}
}
