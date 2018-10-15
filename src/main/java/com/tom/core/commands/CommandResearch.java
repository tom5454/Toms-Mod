package com.tom.core.commands;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import com.tom.api.research.Research;
import com.tom.core.research.ResearchHandler;

public class CommandResearch extends CommandBase {

	@Override
	public String getName() {
		return "tm_research";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.tmReserach.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) { throw new WrongUsageException("commands.tmResearch.usage", new Object[0]); }
		boolean clearS = "clearScans".equalsIgnoreCase(args[0]);
		if (args.length < (clearS ? 1 : 2)) {
			throw new WrongUsageException("commands.tmResearch.usage", new Object[0]);
		} else {
			if (clearS) {
				final EntityPlayerMP entityplayermp = args.length >= 2 ? getPlayer(server, sender, args[1]) : getCommandSenderAsPlayer(sender);
				final ResearchHandler h = ResearchHandler.getHandlerFromName(entityplayermp.getName());
				h.clearScans();
			} else if ("*".equals(args[1]) || (ResearchHandler.getResearchByName(args[1]) != null)) {
				final EntityPlayerMP entityplayermp = args.length >= 3 ? getPlayer(server, sender, args[2]) : getCommandSenderAsPlayer(sender);
				boolean flag = "give".equalsIgnoreCase(args[0]);
				boolean flag1 = "take".equalsIgnoreCase(args[0]);
				boolean all = "*".equals(args[1]);

				if (flag || flag1) {
					if (all) {
						final ResearchHandler h = ResearchHandler.getHandlerFromName(entityplayermp.getName());
						if (flag) {
							for (Research research : ResearchHandler.getAllResearches()) {
								h.markResearchComplete(research);
							}

							notifyCommandListener(sender, this, "commands.tmResearch.give.success.all", new Object[]{entityplayermp.getName()});
						} else if (flag1) {
							for (Research research : Lists.reverse(ResearchHandler.getAllResearches())) {
								h.removeResearch(research);
							}

							notifyCommandListener(sender, this, "commands.tmResearch.take.success.all", new Object[]{entityplayermp.getName()});
						}
					} else {
						final Research r = ResearchHandler.getResearchByName(args[1]);
						final ResearchHandler h = ResearchHandler.getHandlerFromName(entityplayermp.getName());
						if (r != null && r.isValid()) {
							if (flag) {
								if (h.isCompleted(r)) { throw new CommandException("commands.tmResearch.alreadyHave", new Object[]{entityplayermp.getName(), r.createChatComponent()}); }

								List<Research> list = Lists.<Research>newArrayList();

								if (r.getParents() != null && !r.getParents().isEmpty()) {
									Stack<Research> stack = new Stack<>();
									stack.add(r);
									while (!stack.isEmpty()) {
										Research r1 = stack.pop();
										if (r1 != null && r1.getParents() != null && !r1.getParents().isEmpty()) {
											stack.addAll(r1.getParents());
										}
										list.add(r1);
									}
								}

								for (Research research : Lists.reverse(list)) {
									h.markResearchComplete(research);
								}
							} else if (flag1) {
								if (!h.isCompleted(r)) { throw new CommandException("commands.tmResearch.dontHave", new Object[]{entityplayermp.getName(), r.createChatComponent()}); }

								List<Research> list1 = Lists.newArrayList(Iterators.filter(ResearchHandler.getAllResearches().iterator(), new Predicate<Research>() {
									@Override
									public boolean apply(@Nullable Research in) {
										return h.isCompleted(r) && in != r;
									}
								}));
								List<Research> list2 = Lists.newArrayList(list1);

								for (Research research : list1) {
									boolean flag2 = false;
									if (research.getParents() != null && !research.getParents().isEmpty()) {
										Stack<Research> stack = new Stack<>();
										stack.add(research);
										while (!stack.isEmpty()) {
											Research r1 = stack.pop();
											if (r1 != null && r1.getParents() != null && !r1.getParents().isEmpty()) {
												stack.addAll(r1.getParents());
											}
											if (r1 == r) {
												flag2 = true;
											}
										}
									}

									if (!flag2) {
										if (research.getParents() != null && !research.getParents().isEmpty()) {
											Stack<Research> stack = new Stack<>();
											stack.add(research);
											while (!stack.isEmpty()) {
												Research r1 = stack.pop();
												if (r1 != null && r1.getParents() != null && !r1.getParents().isEmpty()) {
													stack.addAll(r1.getParents());
												}
												list2.remove(r1);
											}
										}
									}
								}

								for (Research research : list2) {
									h.removeResearch(research);
								}
							}
						}

						if (flag) {
							h.markResearchComplete(r);
							notifyCommandListener(sender, this, "commands.tmResearch.give.success.one", new Object[]{entityplayermp.getName(), r.createChatComponent()});
						} else if (flag1) {
							h.removeResearch(r);
							notifyCommandListener(sender, this, "commands.tmResearch.take.success.one", new Object[]{r.createChatComponent(), entityplayermp.getName()});
						}
					}
				}
			} else {
				throw new CommandException("commands.tmResearch.unknown", new Object[]{args[1]});
			}
		}
	}

	/**
	 * Return whether the specified command parameter index is a username
	 * parameter.
	 */
	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 2;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			/**
			 * Returns a List of strings (chosen from the given strings) which
			 * the last word in the given string array is a beginning-match for.
			 * (Tab completion).
			 */
			return getListOfStringsMatchingLastWord(args, new String[]{"give", "take", "clearScans"});
		} else if (args.length != 2) {
			return args.length == 3 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.<String>emptyList();
		} else {
			List<String> list = Lists.<String>newArrayList();

			for (Research research : ResearchHandler.getAllResearches()) {
				list.add(research.delegate.name().toString());
			}

			return getListOfStringsMatchingLastWord(args, list);
		}
	}
}
