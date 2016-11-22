package com.tom.core.transformers;

import java.util.Deque;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandClone;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import com.google.common.collect.Lists;

import com.tom.core.Tweaks;

public class CommandCloneTM extends CommandClone{
	/**
	 * Callback for when the command is executed
	 */
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 9)
		{
			throw new WrongUsageException("commands.clone.usage", new Object[0]);
		}
		else
		{
			sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockpos = parseBlockPos(sender, args, 0, false);
			BlockPos blockpos1 = parseBlockPos(sender, args, 3, false);
			BlockPos blockpos2 = parseBlockPos(sender, args, 6, false);
			StructureBoundingBox structureboundingbox = new StructureBoundingBox(blockpos, blockpos1);
			StructureBoundingBox structureboundingbox1 = new StructureBoundingBox(blockpos2, blockpos2.add(structureboundingbox.getLength()));
			int i = structureboundingbox.getXSize() * structureboundingbox.getYSize() * structureboundingbox.getZSize();
			Tweaks.checkAndLogCloneMessage(args, sender, server, blockpos1, blockpos1, i);
			boolean flag = false;
			Block block = null;
			int j = -1;

			if ((args.length < 11 || !"force".equals(args[10]) && !"move".equals(args[10])) && structureboundingbox.intersectsWith(structureboundingbox1))
			{
				throw new CommandException("commands.clone.noOverlap", new Object[0]);
			}
			else
			{
				if (args.length >= 11 && "move".equals(args[10]))
				{
					flag = true;
				}

				if (structureboundingbox.minY >= 0 && structureboundingbox.maxY < 256 && structureboundingbox1.minY >= 0 && structureboundingbox1.maxY < 256)
				{
					World world = sender.getEntityWorld();

					if (world.isAreaLoaded(structureboundingbox) && world.isAreaLoaded(structureboundingbox1))
					{
						boolean flag1 = false;

						if (args.length >= 10)
						{
							if ("masked".equals(args[9]))
							{
								flag1 = true;
							}
							else if ("filtered".equals(args[9]))
							{
								if (args.length < 12)
								{
									throw new WrongUsageException("commands.clone.usage", new Object[0]);
								}

								block = getBlockByText(sender, args[11]);

								if (args.length >= 13)
								{
									j = parseInt(args[12], 0, 15);
								}
							}
						}

						List<StaticCloneData> list = Lists.<StaticCloneData>newArrayList();
						List<StaticCloneData> list1 = Lists.<StaticCloneData>newArrayList();
						List<StaticCloneData> list2 = Lists.<StaticCloneData>newArrayList();
						Deque<BlockPos> deque = Lists.<BlockPos>newLinkedList();
						BlockPos blockpos3 = new BlockPos(structureboundingbox1.minX - structureboundingbox.minX, structureboundingbox1.minY - structureboundingbox.minY, structureboundingbox1.minZ - structureboundingbox.minZ);

						for (int k = structureboundingbox.minZ; k <= structureboundingbox.maxZ; ++k)
						{
							for (int l = structureboundingbox.minY; l <= structureboundingbox.maxY; ++l)
							{
								for (int i1 = structureboundingbox.minX; i1 <= structureboundingbox.maxX; ++i1)
								{
									BlockPos blockpos4 = new BlockPos(i1, l, k);
									BlockPos blockpos5 = blockpos4.add(blockpos3);
									IBlockState iblockstate = world.getBlockState(blockpos4);

									if ((!flag1 || iblockstate.getBlock() != Blocks.AIR) && (block == null || iblockstate.getBlock() == block && (j < 0 || iblockstate.getBlock().getMetaFromState(iblockstate) == j)))
									{
										TileEntity tileentity = world.getTileEntity(blockpos4);

										if (tileentity != null)
										{
											NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());
											list1.add(new StaticCloneData(blockpos5, iblockstate, nbttagcompound));
											deque.addLast(blockpos4);
										}
										else if (!iblockstate.isFullBlock() && !iblockstate.isFullCube())
										{
											list2.add(new StaticCloneData(blockpos5, iblockstate, (NBTTagCompound)null));
											deque.addFirst(blockpos4);
										}
										else
										{
											list.add(new StaticCloneData(blockpos5, iblockstate, (NBTTagCompound)null));
											deque.addLast(blockpos4);
										}
									}
								}
							}
						}

						if (flag)
						{
							for (BlockPos blockpos6 : deque)
							{
								TileEntity tileentity1 = world.getTileEntity(blockpos6);

								if (tileentity1 instanceof IInventory)
								{
									((IInventory)tileentity1).clear();
								}

								world.setBlockState(blockpos6, Blocks.BARRIER.getDefaultState(), 2);
							}

							for (BlockPos blockpos7 : deque)
							{
								world.setBlockState(blockpos7, Blocks.AIR.getDefaultState(), 3);
							}
						}

						List<StaticCloneData> list3 = Lists.<StaticCloneData>newArrayList();
						list3.addAll(list);
						list3.addAll(list1);
						list3.addAll(list2);
						List<StaticCloneData> list4 = Lists.<StaticCloneData>reverse(list3);

						for (StaticCloneData commandclone$staticclonedata : list4)
						{
							TileEntity tileentity2 = world.getTileEntity(commandclone$staticclonedata.pos);

							if (tileentity2 instanceof IInventory)
							{
								((IInventory)tileentity2).clear();
							}

							world.setBlockState(commandclone$staticclonedata.pos, Blocks.BARRIER.getDefaultState(), 2);
						}

						i = 0;

						for (StaticCloneData commandclone$staticclonedata1 : list3)
						{
							if (world.setBlockState(commandclone$staticclonedata1.pos, commandclone$staticclonedata1.blockState, 2))
							{
								++i;
							}
						}

						for (StaticCloneData commandclone$staticclonedata2 : list1)
						{
							TileEntity tileentity3 = world.getTileEntity(commandclone$staticclonedata2.pos);

							if (commandclone$staticclonedata2.nbt != null && tileentity3 != null)
							{
								commandclone$staticclonedata2.nbt.setInteger("x", commandclone$staticclonedata2.pos.getX());
								commandclone$staticclonedata2.nbt.setInteger("y", commandclone$staticclonedata2.pos.getY());
								commandclone$staticclonedata2.nbt.setInteger("z", commandclone$staticclonedata2.pos.getZ());
								tileentity3.readFromNBT(commandclone$staticclonedata2.nbt);
								tileentity3.markDirty();
							}

							world.setBlockState(commandclone$staticclonedata2.pos, commandclone$staticclonedata2.blockState, 2);
						}

						for (StaticCloneData commandclone$staticclonedata3 : list4)
						{
							world.notifyNeighborsRespectDebug(commandclone$staticclonedata3.pos, commandclone$staticclonedata3.blockState.getBlock());
						}

						List<NextTickListEntry> list5 = world.getPendingBlockUpdates(structureboundingbox, false);

						if (list5 != null)
						{
							for (NextTickListEntry nextticklistentry : list5)
							{
								if (structureboundingbox.isVecInside(nextticklistentry.position))
								{
									BlockPos blockpos8 = nextticklistentry.position.add(blockpos3);
									world.scheduleBlockUpdate(blockpos8, nextticklistentry.getBlock(), (int)(nextticklistentry.scheduledTime - world.getWorldInfo().getWorldTotalTime()), nextticklistentry.priority);
								}
							}
						}

						if (i <= 0)
						{
							throw new CommandException("commands.clone.failed", new Object[0]);
						}
						else
						{
							sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, i);
							notifyCommandListener(sender, this, "commands.clone.success", new Object[] {Integer.valueOf(i)});
						}
					}
					else
					{
						throw new CommandException("commands.clone.outOfWorld", new Object[0]);
					}
				}
				else
				{
					throw new CommandException("commands.clone.outOfWorld", new Object[0]);
				}
			}
		}
	}
	static class StaticCloneData
	{
		public final BlockPos pos;
		public final IBlockState blockState;
		public final NBTTagCompound nbt;

		public StaticCloneData(BlockPos posIn, IBlockState stateIn, NBTTagCompound compoundIn)
		{
			this.pos = posIn;
			this.blockState = stateIn;
			this.nbt = compoundIn;
		}
	}
}
