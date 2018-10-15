package com.tom.network.messages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;

import com.tom.lib.network.MessageBase;
import com.tom.network.NetworkHandler;

import io.netty.buffer.ByteBuf;

public class MessageProgress extends MessageBase<MessageProgress> {
	private boolean update = false;
	private IContainerListener crafter;
	private Map<Integer, Integer> messages = new HashMap<Integer, Integer>();

	public MessageProgress(IContainerListener crafter) {
		this.crafter = crafter;
	}

	public MessageProgress() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int size = buf.readByte();
		for (int i = 0;i < size;i++) {
			int key = buf.readByte();
			messages.put(key, buf.readInt());
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(messages.size());
		for (Entry<Integer, Integer> e : messages.entrySet()) {
			buf.writeByte(e.getKey());
			buf.writeInt(e.getValue());
		}
	}

	@Override
	public void handleClientSide(final MessageProgress message, final EntityPlayer player) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {

			@Override
			public void run() {
				for (Entry<Integer, Integer> e : message.messages.entrySet())
					player.openContainer.updateProgressBar(e.getKey(), e.getValue());
			}
		});
	}

	@Override
	public void handleServerSide(MessageProgress message, EntityPlayer player) {
	}

	public void send() {
		if (update) {
			NetworkHandler.sendTo(this, (EntityPlayerMP) crafter);
		}
	}

	public void add(int id, int value) {
		update = true;
		messages.put(id, value);
	}
}
