package com.tom.network.messages;

import net.minecraft.entity.player.EntityPlayer;

import com.tom.core.Keybindings;
import com.tom.handler.KeyInputHandler;
import com.tom.lib.network.MessageBase;

import io.netty.buffer.ByteBuf;

public class MessageKey extends MessageBase<MessageKey> {
	private int key;
	private boolean up;

	public MessageKey() {
	}

	public MessageKey(Keybindings key, boolean up) {
		this.key = key.ordinal();
		this.up = up;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.key = buf.readInt();
		this.up = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(key);
		buf.writeBoolean(up);
	}

	@Override
	public void handleClientSide(MessageKey message, EntityPlayer player) {
	}

	@Override
	public void handleServerSide(MessageKey message, EntityPlayer player) {
		KeyInputHandler.instance.handlerKeyServer(Keybindings.values()[message.key], player, message.up);
	}

}
