package com.tom.network.messages;

import com.tom.api.network.INBTPacketReceiver;
import com.tom.network.MessageBase;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class MessageCraftingReportSync extends MessageBase<MessageCraftingReportSync>{
	public MessageCraftingReportSync() {
	}
	private NBTTagCompound main;
	private int opertaions;
	private int memory;
	private int time;
	private short[] cpus;
	private int missingLength, recipesLength, stacksLenght;

	public MessageCraftingReportSync(NBTTagCompound main, int opertaions, int memory, int time, short[] cpus,
			int missingLength, int recipesLength, int stacksLenght) {
		this.main = main;
		this.opertaions = opertaions;
		this.memory = memory;
		this.time = time;
		this.cpus = cpus;
		this.missingLength = missingLength;
		this.recipesLength = recipesLength;
		this.stacksLenght = stacksLenght;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		memory = buf.readInt();
		opertaions = buf.readInt();
		time = buf.readInt();
		int length = buf.readShort();
		cpus = new short[length];
		for(int i = 0;i<length;i++){
			cpus[i] = buf.readShort();
		}
		missingLength = buf.readInt();
		recipesLength = buf.readInt();
		stacksLenght = buf.readInt();
		main = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(memory);
		buf.writeInt(opertaions);
		buf.writeInt(time);
		buf.writeShort(cpus.length);
		for(int i = 0;i<cpus.length;i++){
			buf.writeShort(cpus[i]);
		}
		buf.writeInt(missingLength);
		buf.writeInt(recipesLength);
		buf.writeInt(stacksLenght);
		ByteBufUtils.writeTag(buf, main);
	}

	@Override
	public void handleClientSide(MessageCraftingReportSync message, EntityPlayer player) {
		final NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", 0);
		tag.setInteger("ms", message.missingLength);
		tag.setInteger("sls", message.stacksLenght);
		tag.setInteger("rls", message.recipesLength);
		NBTTagCompound m = new NBTTagCompound();
		m.setInteger("t", message.time);
		m.setTag("m", message.main);
		m.setInteger("o", message.opertaions);
		m.setInteger("s", message.memory);
		NBTTagList list = new NBTTagList();
		for(short s : message.cpus){
			list.appendTag(new NBTTagInt(s));
		}
		m.setTag("p", list);
		tag.setTag("m", m);
		tag.setBoolean("r", true);
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
		if(mc.currentScreen instanceof INBTPacketReceiver){
			((INBTPacketReceiver)mc.currentScreen).receiveNBTPacket(tag);
		}
	}

	@Override
	public void handleServerSide(MessageCraftingReportSync message, EntityPlayer player) {

	}
	public static enum MessageType{
		MAIN, NORMAL, MISSING, RECIPE
	}
}
