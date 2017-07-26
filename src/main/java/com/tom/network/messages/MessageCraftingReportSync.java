package com.tom.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;

import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.tom.api.network.INBTPacketReceiver;
import com.tom.network.MessageBase;

import io.netty.buffer.ByteBuf;

public class MessageCraftingReportSync extends MessageBase<MessageCraftingReportSync> {
	public MessageCraftingReportSync() {
	}

	private NBTTagCompound main, extra;
	private long opertaions;
	private long memory;
	private long time;
	private short[] cpus;
	private int missingLength, recipesLength, stacksLenght;

	public MessageCraftingReportSync(NBTTagCompound main, long opertaions, long memory, long time, short[] cpus, int missingLength, int recipesLength, int stacksLenght, NBTTagCompound extra) {
		this.main = main;
		this.opertaions = opertaions;
		this.memory = memory;
		this.time = time;
		this.cpus = cpus;
		this.missingLength = missingLength;
		this.recipesLength = recipesLength;
		this.stacksLenght = stacksLenght;
		this.extra = extra;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		memory = buf.readLong();
		opertaions = buf.readLong();
		time = buf.readLong();
		int length = buf.readShort();
		cpus = new short[length];
		for (int i = 0;i < length;i++) {
			cpus[i] = buf.readShort();
		}
		missingLength = buf.readInt();
		recipesLength = buf.readInt();
		stacksLenght = buf.readInt();
		main = ByteBufUtils.readTag(buf);
		extra = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(memory);
		buf.writeLong(opertaions);
		buf.writeLong(time);
		buf.writeShort(cpus.length);
		for (int i = 0;i < cpus.length;i++) {
			buf.writeShort(cpus[i]);
		}
		buf.writeInt(missingLength);
		buf.writeInt(recipesLength);
		buf.writeInt(stacksLenght);
		ByteBufUtils.writeTag(buf, main);
		ByteBufUtils.writeTag(buf, extra);
	}

	@Override
	public void handleClientSide(MessageCraftingReportSync message, EntityPlayer player) {
		final NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", 0);
		tag.setInteger("ms", message.missingLength);
		tag.setInteger("sls", message.stacksLenght);
		tag.setInteger("rls", message.recipesLength);
		NBTTagCompound m = new NBTTagCompound();
		m.setLong("t", message.time);
		m.setTag("m", message.main);
		m.setLong("o", message.opertaions);
		m.setLong("s", message.memory);
		NBTTagList list = new NBTTagList();
		for (short s : message.cpus) {
			list.appendTag(new NBTTagInt(s));
		}
		m.setTag("p", list);
		tag.setTag("m", m);
		tag.setTag("e", message.extra);
		tag.setBoolean("r", true);
		tag.setBoolean("s", true);
		if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					handleClient(tag);
				}
			});
		} else {
			handleClient(tag);
		}
	}

	private void handleClient(NBTTagCompound tag) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen instanceof INBTPacketReceiver) {
			((INBTPacketReceiver) mc.currentScreen).receiveNBTPacket(tag);
		}
	}

	@Override
	public void handleServerSide(MessageCraftingReportSync message, EntityPlayer player) {

	}

	public static enum MessageType {
		MAIN, NORMAL, MISSING, RECIPE
	}
}
