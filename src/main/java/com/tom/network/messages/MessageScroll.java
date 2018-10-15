package com.tom.network.messages;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.FMLCommonHandler;

import com.tom.api.item.IScroller;
import com.tom.api.item.IScroller.ScrollDirection;
import com.tom.lib.network.MessageBase;

import io.netty.buffer.ByteBuf;

public class MessageScroll extends MessageBase<MessageScroll> {
	private int dir;

	public MessageScroll() {
	}

	public MessageScroll(ScrollDirection dir) {
		this.dir = dir.ordinal();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		dir = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(dir);
	}

	@Override
	public void handleClientSide(MessageScroll message, EntityPlayer player) {

	}

	@Override
	public void handleServerSide(MessageScroll message, EntityPlayer player) {
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
			ItemStack stack = player.getHeldItemMainhand();
			if (stack.getItem() instanceof IScroller) {
				IScroller s = (IScroller) stack.getItem();
				if (s.canScroll(stack)) {
					s.scroll(stack, player, ScrollDirection.VALUES[message.dir]);
				}
			}
		});
	}

}
