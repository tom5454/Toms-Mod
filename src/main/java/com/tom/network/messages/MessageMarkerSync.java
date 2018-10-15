package com.tom.network.messages;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.tom.api.map.Marker;
import com.tom.api.map.RenderType;
import com.tom.core.map.MapHandler;
import com.tom.lib.network.MessageBase;
import com.tom.network.NetworkHandler;
import com.tom.util.TomsModUtils;

import io.netty.buffer.ByteBuf;

public class MessageMarkerSync extends MessageBase<MessageMarkerSync> {
	private byte type;
	private int mx, my, mz, dim, color, id;
	private String group, markerName, icon, beamTexture;
	private RenderType beam, label;

	public MessageMarkerSync() {
		type = 0;
	}

	/*public MessageMarkerSync(MarkerManager manager) {
		type = 1;
		groupList = manager.groupList;
	}*/
	public MessageMarkerSync(Marker marker, int id, boolean last) {
		type = (byte) (last ? 3 : 2);
		this.id = id;
		this.mx = marker.x;
		this.my = marker.y;
		this.mz = marker.z;
		this.dim = marker.dimension;
		this.color = marker.color;
		this.beam = marker.beamType;
		this.beamTexture = "normal";
		this.group = marker.groupName;
		this.icon = marker.iconLocation;
		this.label = marker.labelType;
		this.markerName = marker.name;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		type = buf.readByte();
		if (type == 1) {
			/*int groupAmount = buf.readShort();
			for(int i = 0;i<groupAmount;i++){
				groupList.add(ByteBufUtils.readUTF8String(buf));
			}*/
		} else if (type == 2 || type == 3) {
			this.id = buf.readInt();
			this.mx = buf.readInt();
			this.my = buf.readInt();
			this.mz = buf.readInt();
			this.dim = buf.readInt();
			this.group = ByteBufUtils.readUTF8String(buf);
			this.markerName = ByteBufUtils.readUTF8String(buf);
			this.icon = ByteBufUtils.readUTF8String(buf);
			this.beamTexture = ByteBufUtils.readUTF8String(buf);
			this.color = buf.readInt();
			this.beam = RenderType.VALUES[buf.readByte()];
			this.label = RenderType.VALUES[buf.readByte()];
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(type);
		if (type == 1) {
			/*buf.writeShort(groupList.size());
			for(int i = 0;i<groupList.size();i++){
				ByteBufUtils.writeUTF8String(buf, groupList.get(i));
			}*/
		} else if (type == 2 || type == 3) {
			buf.writeInt(id);
			buf.writeInt(mx);
			buf.writeInt(my);
			buf.writeInt(mz);
			buf.writeInt(dim);
			ByteBufUtils.writeUTF8String(buf, group);
			ByteBufUtils.writeUTF8String(buf, markerName);
			ByteBufUtils.writeUTF8String(buf, icon);
			ByteBufUtils.writeUTF8String(buf, beamTexture);
			buf.writeInt(color);
			buf.writeByte(beam.ordinal());
			buf.writeByte(label.ordinal());
		}
	}

	@Override
	public void handleClientSide(final MessageMarkerSync m, EntityPlayer player) {
		if (m.type == 2 || m.type == 3) {
			MapHandler.putSyncedMarker(m.id, new Marker(m.markerName, m.group, m.mx, m.my, m.mz, m.dim, m.icon, m.color, m.beam, m.label, m.beamTexture, false));
			if (m.type == 3)
				MapHandler.update();
		}
	}

	@Override
	public void handleServerSide(MessageMarkerSync message, EntityPlayer player) {
		if (message.type == 0) {
			sendSyncMessageTo(player);
		}
	}

	public static void sendSyncMessageTo(EntityPlayer player) {
		for (int i = 0;i < MapHandler.markerManagerServer.markerList.size();i++) {
			NetworkHandler.sendTo(new MessageMarkerSync(MapHandler.markerManagerServer.markerList.get(i), i, i + 1 == MapHandler.markerManagerServer.markerList.size()), (EntityPlayerMP) player);
		}
	}

	public static void sendSyncMessage() {
		for (EntityPlayerMP player : TomsModUtils.getServer().getPlayerList().getPlayers()) {
			sendSyncMessageTo(player);
		}
	}
}
