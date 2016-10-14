package com.tom.network.messages;

import net.minecraft.entity.player.EntityPlayer;

import com.tom.core.Keybindings;
import com.tom.handler.KeyInputHandler;
import com.tom.network.MessageBase;

import io.netty.buffer.ByteBuf;

public class MessageKey extends MessageBase<MessageKey> {
	private int key;
	public MessageKey(){}
	public MessageKey(Keybindings key){
		this.key = key.ordinal();
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		this.key = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(key);
	}

	@Override
	public void handleClientSide(MessageKey message, EntityPlayer player) {}

	@Override
	public void handleServerSide(MessageKey message, EntityPlayer player) {
		KeyInputHandler.instance.handlerKeyServer(Keybindings.values()[message.key], player);
	}

}
