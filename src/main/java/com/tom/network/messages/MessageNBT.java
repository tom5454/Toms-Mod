package com.tom.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.tom.api.multipart.IGuiMultipart;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.network.INBTPacketReceiver.IANBTPacketReceiver;
import com.tom.api.network.INBTPacketSender;
import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;
import com.tom.network.MessageBase;
import com.tom.network.NetworkHandler;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;

public class MessageNBT extends MessageBase<MessageNBT>{
	private NBTTagCompound tag;
	private BlockPos pos;
	private boolean isMultipart;
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
	public MessageNBT(NBTTagCompound tag, TileEntity tile) {
		this(tag,tile.getPos());
		isMultipart = false;
	}
	public MessageNBT(NBTTagCompound tag, IGuiMultipart tile) {
		this(tag,tile.getPos2());
		isMultipart = true;
		partPos = tile.getPosition().ordinal();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		isMultipart = buf.readBoolean();
		if(isMultipart)partPos = buf.readByte();
		try{
			tag = ByteBufUtils.readTag(buf);
		}catch(RuntimeException e){
			TMLogger.bigCatching(e, "Exception caught during the reading of an NBTTagCompound from ByteBuf in.");
			tag = new NBTTagCompound();
			tag.setBoolean("ERROR", true);
		}
		pos = TomsModUtils.readBlockPosFromPacket(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(isMultipart);
		if(isMultipart)buf.writeByte(partPos);
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
	private void handleClient(){
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen instanceof INBTPacketReceiver){
			((INBTPacketReceiver)mc.currentScreen).receiveNBTPacket(tag);
		}
	}
	@Override
	public void handleServerSide(MessageNBT message, EntityPlayer player) {
		if(message.pos != null){
			if(message.isMultipart && message.partPos >= 0){
				IMultipartContainer container = MultipartHelper.getPartContainer(player.worldObj, message.pos);
				if (container == null) {
					return;
				}
				ISlottedPart part = container.getPartInSlot(PartSlot.VALUES[message.partPos]);
				if(part instanceof INBTPacketReceiver){
					((INBTPacketReceiver)part).receiveNBTPacket(message.tag);
				}else if(part instanceof IANBTPacketReceiver){
					((IANBTPacketReceiver)part).receiveNBTPacket(message.tag, player);
				}
			}else{
				TileEntity tile = player.worldObj.getTileEntity(message.pos);
				if(tile instanceof INBTPacketReceiver){
					((INBTPacketReceiver)tile).receiveNBTPacket(message.tag);
					tile.markDirty();
				}else if(tile instanceof IANBTPacketReceiver){
					((IANBTPacketReceiver)tile).receiveNBTPacket(message.tag, player);
					tile.markDirty();
				}
			}
		}else{
			if(player.openContainer instanceof INBTPacketReceiver){
				((INBTPacketReceiver)player.openContainer).receiveNBTPacket(message.tag);
			}else if(player.openContainer instanceof IANBTPacketReceiver){
				((IANBTPacketReceiver)player.openContainer).receiveNBTPacket(message.tag, player);
			}
		}
	}
	public static class MessageNBTRequest extends MessageBase<MessageNBTRequest>{
		private BlockPos pos;
		public MessageNBTRequest(BlockPos pos) {
			this.pos = pos;
		}
		public MessageNBTRequest() {
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			pos = TomsModUtils.readBlockPosFromPacket(buf);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			TomsModUtils.writeBlockPosToPacket(buf, pos);
		}

		@Override
		public void handleClientSide(MessageNBTRequest message,
				EntityPlayer player) {
			if(message.pos != null){
				Minecraft mc = Minecraft.getMinecraft();
				if(mc.currentScreen instanceof INBTPacketSender){
					NBTTagCompound tag = new NBTTagCompound();
					((INBTPacketSender)mc.currentScreen).writeToNBTPacket(tag);
					NetworkHandler.sendToServer(new MessageNBT(tag, message.pos));
				}
			}
		}

		@Override
		public void handleServerSide(MessageNBTRequest message,
				EntityPlayer player) {
			if(message.pos != null){
				TileEntity tile = player.worldObj.getTileEntity(message.pos);
				if(tile instanceof INBTPacketSender){
					NBTTagCompound tag = new NBTTagCompound();
					((INBTPacketSender)tile).writeToNBTPacket(tag);
					NetworkHandler.sendTo(new MessageNBT(tag), (EntityPlayerMP) player);
				}
			}
		}

	}
}
