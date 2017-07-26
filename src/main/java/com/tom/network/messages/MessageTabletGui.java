package com.tom.network.messages;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.tom.network.MessageBase;

import com.tom.core.tileentity.inventory.ContainerTablet;

import io.netty.buffer.ByteBuf;

public class MessageTabletGui extends MessageBase<MessageTabletGui> {

	// private TabletHandler tab;
	private ItemStack tabStack;

	public MessageTabletGui() {

	}

	public MessageTabletGui(ItemStack tabStack) {
		this.tabStack = tabStack;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		/*boolean tn = buf.readBoolean();
		int id = buf.readInt();
		this.nn = tn;
		if(tn){
			this.tab = new TabletHandler(id);
			this.tab.readFromPacket(buf);
		}*/
		tabStack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		/*boolean tn = tab != null;
		buf.writeBoolean(tn);
		buf.writeInt(tab.id);
		if(tn){
			this.tab.writeToPacket(buf);
		}*/
		ByteBufUtils.writeItemStack(buf, tabStack);
	}

	@Override
	public void handleClientSide(MessageTabletGui message, EntityPlayer player) {
		if (player.openContainer instanceof ContainerTablet) {
			((ContainerTablet) player.openContainer).tabStack = message.tabStack;
		}
	}

	@Override
	public void handleServerSide(MessageTabletGui message, EntityPlayer player) {
	}

}
