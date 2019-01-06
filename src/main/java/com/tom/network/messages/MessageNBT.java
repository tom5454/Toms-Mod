package com.tom.network.messages;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.tom.api.Capabilities;
import com.tom.api.multipart.IGuiMultipart;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.network.INBTPacketSender;
import com.tom.api.tileentity.IConfigurable;
import com.tom.lib.network.MessageBase;
import com.tom.network.NetworkHandler;
import com.tom.util.TMLogger;
import com.tom.util.TomsModUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.IPartSlot;
import mcmultipart.slot.SlotRegistry;

public class MessageNBT extends MessageBase<MessageNBT> {
	private NBTTagCompound tag;
	private BlockPos pos;
	private boolean isMultipart, isConfiguration;
	private int partPos = -1;

	public MessageNBT() {
		this.tag = new NBTTagCompound();
		isMultipart = false;
	}

	public MessageNBT(NBTTagCompound tag) {
		this.tag = tag;
		isMultipart = false;
	}

	public MessageNBT(NBTTagCompound tag, BlockPos pos) {
		this.tag = tag;
		this.pos = pos;
		isMultipart = false;
	}

	public MessageNBT(NBTTagCompound tag, BlockPos pos, int side) {
		this.tag = tag;
		this.pos = pos;
		isMultipart = false;
		isConfiguration = true;
		partPos = side;
	}

	public MessageNBT(NBTTagCompound tag, TileEntity tile) {
		this(tag, tile.getPos());
		isMultipart = false;
	}

	public MessageNBT(NBTTagCompound tag, IGuiMultipart tile) {
		this(tag, tile.getPos2());
		IPartSlot slot = tile.getPosition();
		isMultipart = slot != null;
		partPos = slot != null ? SlotRegistry.INSTANCE.getSlotID(slot) : -1;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		isMultipart = buf.readBoolean();
		isConfiguration = buf.readBoolean();
		if (isMultipart || isConfiguration)
			partPos = buf.readInt();
		try {
			tag = CompressedStreamTools.read(new ByteBufInputStream(buf), NBTSizeTracker.INFINITE);
		} catch (Exception e) {
			TMLogger.bigCatching(e, "Exception caught during the reading of an NBTTagCompound from ByteBuf in.");
			tag = new NBTTagCompound();
			tag.setBoolean("ERROR", true);
		}
		pos = TomsModUtils.readBlockPosFromPacket(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(isMultipart);
		buf.writeBoolean(isConfiguration);
		if (isMultipart || isConfiguration)
			buf.writeInt(partPos);
		ByteBufUtils.writeTag(buf, tag);
		TomsModUtils.writeBlockPosToPacket(buf, pos);
	}

	@Override
	public void handleClientSide(final MessageNBT message, EntityPlayer player) {
		if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					message.handleClient();
				}
			});
		} else {
			message.handleClient();
		}
	}

	private void handleClient() {
		Minecraft mc = Minecraft.getMinecraft();
		if (pos != null) {
			if (isMultipart && partPos >= 0) {
				IMultipartContainer container = MultipartHelper.getContainer(mc.world, pos).orElse(null);
				if (container == null) { return; }
				Optional<IPartInfo> part = container.get(SlotRegistry.INSTANCE.getSlotFromID(partPos));
				if (!part.isPresent())
					return;
				if (part.get().getTile() instanceof INBTPacketReceiver) {
					((INBTPacketReceiver) part.get().getTile()).receiveNBTPacket(mc.player, tag);
				}
			} else if (isConfiguration) {
				TileEntity tile = mc.world.getTileEntity(pos);
				IConfigurable c = tile.getCapability(Capabilities.CONFIGURABLE, partPos == -1 ? null : EnumFacing.VALUES[partPos]);
				if (c != null) {
					((INBTPacketReceiver) c).receiveNBTPacket(mc.player, tag);
				}
			} else {
				TileEntity tile = mc.world.getTileEntity(pos);
				if (tile instanceof INBTPacketReceiver) {
					((INBTPacketReceiver) tile).receiveNBTPacket(mc.player, tag);
					tile.markDirty();
				}
			}

		}else{
			if (mc.currentScreen instanceof INBTPacketReceiver) {
				((INBTPacketReceiver) mc.currentScreen).receiveNBTPacket(mc.player, tag);
			}
		}
	}

	@Override
	public void handleServerSide(MessageNBT message, EntityPlayer player) {
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {

			@Override
			public void run() {
				if (message.pos != null) {
					if (message.isMultipart && message.partPos >= 0) {
						IMultipartContainer container = MultipartHelper.getContainer(player.world, message.pos).orElse(null);
						if (container == null) { return; }
						Optional<IPartInfo> part = container.get(SlotRegistry.INSTANCE.getSlotFromID(message.partPos));
						if (!part.isPresent())
							return;
						if (part.get().getTile() instanceof INBTPacketReceiver) {
							((INBTPacketReceiver) part.get().getTile()).receiveNBTPacket(player, message.tag);
						}
					} else if (message.isConfiguration) {
						TileEntity tile = player.world.getTileEntity(message.pos);
						IConfigurable c = tile.getCapability(Capabilities.CONFIGURABLE, message.partPos == -1 ? null : EnumFacing.VALUES[message.partPos]);
						if (c != null) {
							((INBTPacketReceiver) c).receiveNBTPacket(player, message.tag);
						}
					} else {
						TileEntity tile = player.world.getTileEntity(message.pos);
						if (tile instanceof INBTPacketReceiver) {
							((INBTPacketReceiver) tile).receiveNBTPacket(player, message.tag);
							tile.markDirty();
						}
					}
				} else {
					if (player.openContainer instanceof INBTPacketReceiver) {
						((INBTPacketReceiver) player.openContainer).receiveNBTPacket(player, message.tag);
					}
				}
			}
		});
	}

	public static class MessageNBTRequest extends MessageBase<MessageNBTRequest> {
		private BlockPos pos;
		private int side;

		public MessageNBTRequest(BlockPos pos) {
			this.pos = pos;
			this.side = -2;
		}

		public MessageNBTRequest(BlockPos pos, int side) {
			this.pos = pos;
			this.side = side;
		}

		public MessageNBTRequest() {
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			pos = TomsModUtils.readBlockPosFromPacket(buf);
			side = buf.readByte();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			TomsModUtils.writeBlockPosToPacket(buf, pos);
			buf.writeByte(side);
		}

		@Override
		public void handleClientSide(MessageNBTRequest message, EntityPlayer player) {
			if (message.pos != null) {
				Minecraft mc = Minecraft.getMinecraft();
				if (mc.currentScreen instanceof INBTPacketSender) {
					NBTTagCompound tag = new NBTTagCompound();
					((INBTPacketSender) mc.currentScreen).writeToNBTPacket(tag);
					NetworkHandler.sendToServer(new MessageNBT(tag, message.pos));
				}
			}
		}

		@Override
		public void handleServerSide(MessageNBTRequest message, EntityPlayer player) {
			if (message.pos != null) {
				TileEntity tile = player.world.getTileEntity(message.pos);
				if (message.side != -2) {
					IConfigurable c = tile.getCapability(Capabilities.CONFIGURABLE, message.side == -1 ? null : EnumFacing.VALUES[message.side]);
					if (c != null) {
						NBTTagCompound tag = new NBTTagCompound();
						c.writeToNBTPacket(tag);
						NetworkHandler.sendTo(new MessageNBT(tag), (EntityPlayerMP) player);
					}
				} else if (tile instanceof INBTPacketSender) {
					NBTTagCompound tag = new NBTTagCompound();
					((INBTPacketSender) tile).writeToNBTPacket(tag);
					NetworkHandler.sendTo(new MessageNBT(tag), (EntityPlayerMP) player);
				}
			}
		}

	}

	public static void sendToAll(NBTTagCompound tag, List<IContainerListener> l) {
		l.forEach(p -> NetworkHandler.sendTo(new MessageNBT(tag), (EntityPlayerMP) p));
	}
}
