package com.tom.network.messages;

import com.tom.core.Minimap;
import com.tom.network.MessageBase;

import io.netty.buffer.ByteBuf;
import mapwriterTm.Mw;
import mapwriterTm.map.Marker.RenderType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class MessageMinimap extends MessageBase<MessageMinimap> {
	private int mx, my, mz,dim,color/*,borderColor,borderColorActive*/;
	private String group,markerName,icon, beamTexture;
	private boolean /*bordered, borderedA,*/ reloadable = true;
	private byte type;
	private RenderType beam, label;
	public MessageMinimap(){
	}
	public MessageMinimap(EntityPlayer player){
		this.type = 2;
		this.mx = MathHelper.floor_double(player.posX);
		this.my = MathHelper.floor_double(player.posY);
		this.mz = MathHelper.floor_double(player.posZ);
		this.dim = player.worldObj.provider.getDimension();
	}

	public MessageMinimap(String group,int mx,int my,int mz,int dim,String markerName,String icon, int beamColor, RenderType beamRenderType, RenderType labelRenderType, boolean reloadable, String beamTexture) {
		this.mx = mx;
		this.my = my;
		this.mz = mz;
		this.dim = dim;
		this.color = beamColor;
		this.group = group;
		this.markerName = markerName;
		this.icon = icon;
		this.reloadable = reloadable;
		this.type = 0;
		this.beam = beamRenderType;
		this.label = labelRenderType;
		this.beamTexture = beamTexture;
	}

	/*public MessageMinimap(String markerName, String group,int mx,int my,int mz,int dim,String icon){
		this.id = 0;
		this.markerName = markerName;
		this.mx = mx;
		this.my = my;
		this.mz = mz;
		this.dim = dim;
		this.icon = icon;
		this.group = group;
		this.color = 0;
		//this.borderColorActive = 0xffffffff;
		//this.borderColor = 0xff000000;
		//this.bordered = false;
		//this.borderedA = true;
	}
	public MessageMinimap(String markerName, String group,int mx,int my,int mz,int dim,int color){
		this.id = 1;
		this.markerName = markerName;
		this.mx = mx;
		this.my = my;
		this.mz = mz;
		this.dim = dim;
		this.icon = "";
		this.group = group;
		this.color = color;
		//this.borderColorActive = 0xffffffff;
		//this.borderColor = 0xff000000;
		//this.bordered = true;
		//this.borderedA = true;
	}*/
	public MessageMinimap(String group,String markerName){
		this.type = 1;
		this.markerName = markerName;
		this.group = group;
	}
	/*public MessageMinimap(String name, String groupName, int x, int y, int z, int dimension, int colour, int border, int borderA, boolean borderedA, boolean bordered) {
		this.markerName = name;
		this.mx = x;
		this.my = y;
		this.mz = z;
		this.dim = dimension;
		this.icon = "";
		this.group = groupName;
		this.color = colour;
		this.borderColorActive = borderA;
		this.borderColor = border;
		this.bordered = bordered;
		this.borderedA = borderedA;
		this.id = 3;
	}
	public MessageMinimap(String name, String groupName, int x, int y, int z, int dimension, String icon, int border, int borderA, boolean borderedA, boolean bordered) {
		this.markerName = name;
		this.mx = x;
		this.my = y;
		this.mz = z;
		this.dim = dimension;
		this.icon = icon;
		this.group = groupName;
		this.color = 0;
		this.borderColorActive = borderA;
		this.borderColor = border;
		this.bordered = bordered;
		this.borderedA = borderedA;
		this.id = 4;
	}
	public MessageMinimap(String name, String groupName, int x, int y, int z, int dimension, int colour, int border, int borderA, boolean borderedA, boolean bordered, boolean reloadable) {
		this.markerName = name;
		this.mx = x;
		this.my = y;
		this.mz = z;
		this.dim = dimension;
		this.icon = "";
		this.group = groupName;
		this.color = colour;
		this.borderColorActive = borderA;
		this.borderColor = border;
		this.bordered = bordered;
		this.borderedA = borderedA;
		this.reloadable = reloadable;
		this.id = 3;
	}
	public MessageMinimap(String name, String groupName, int x, int y, int z, int dimension, String icon, int border, int borderA, boolean borderedA, boolean bordered, boolean reloadable) {
		this.markerName = name;
		this.mx = x;
		this.my = y;
		this.mz = z;
		this.dim = dimension;
		this.icon = icon;
		this.group = groupName;
		this.color = 0;
		this.borderColorActive = borderA;
		this.borderColor = border;
		this.bordered = bordered;
		this.borderedA = borderedA;
		this.reloadable = reloadable;
		this.id = 4;
	}*/
	@Override
	public void fromBytes(ByteBuf buf) {
		this.type = buf.readByte();
		if(type == 0){
			this.mx = buf.readInt();
			this.my = buf.readInt();
			this.mz = buf.readInt();
			this.dim = buf.readInt();
			this.reloadable = buf.readBoolean();
			this.group = ByteBufUtils.readUTF8String(buf);
			this.markerName = ByteBufUtils.readUTF8String(buf);
			this.icon = ByteBufUtils.readUTF8String(buf);
			this.beamTexture = ByteBufUtils.readUTF8String(buf);
			this.color = buf.readInt();
			this.beam = RenderType.VALUES[buf.readByte()];
			this.label = RenderType.VALUES[buf.readByte()];
			//if(id != 1) this.icon = ByteBufUtils.readUTF8String(buf);
			//else if(id != 0) this.color = buf.readInt();
			/*if(id == 3 || id == 4){
				this.bordered = buf.readBoolean();
				this.borderedA = buf.readBoolean();
				this.borderColor = buf.readInt();
				this.borderColorActive = buf.readInt();
			}*/
		}else if(type == 1){
			this.group = ByteBufUtils.readUTF8String(buf);
			this.markerName = ByteBufUtils.readUTF8String(buf);
		}else if(type == 2){
			this.mx = buf.readInt();
			this.my = buf.readInt();
			this.mz = buf.readInt();
			this.dim = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(type);
		if(type == 0){
			buf.writeInt(mx);
			buf.writeInt(my);
			buf.writeInt(mz);
			buf.writeInt(dim);
			buf.writeBoolean(reloadable);
			ByteBufUtils.writeUTF8String(buf, group);
			ByteBufUtils.writeUTF8String(buf, markerName);
			ByteBufUtils.writeUTF8String(buf, icon);
			ByteBufUtils.writeUTF8String(buf, beamTexture);
			buf.writeInt(color);
			buf.writeByte(beam.ordinal());
			buf.writeByte(label.ordinal());
			//if(icon != null) ByteBufUtils.writeUTF8String(buf, icon);
			//else if(isDelete != 0) buf.writeInt(color);
			/*if(id == 3 || id == 4){
				buf.writeBoolean(bordered);
				buf.writeBoolean(borderedA);
				buf.writeInt(borderColor);
				buf.writeInt(borderColorActive);
			}*/
		}else if(type == 1){
			ByteBufUtils.writeUTF8String(buf, group);
			ByteBufUtils.writeUTF8String(buf, markerName);
		}else if(type == 2){
			buf.writeInt(mx);
			buf.writeInt(my);
			buf.writeInt(mz);
			buf.writeInt(dim);
		}
	}

	@Override
	public void handleClientSide(MessageMinimap message, EntityPlayer player) {
		this.type = message.type;
		if(type == 0){
			Minimap.deleteWayPoint(message.group, message.markerName);
		}else if(type == 1){
			Minimap.createTexturedWayPoint(message.group, message.mx, message.my, message.mz, message.dim, message.markerName, message.icon, message.color, message.beam, message.label, message.reloadable, message.beamTexture);
		}else if(type == 2)Mw.getInstance().onPlayerDeath(message.mx, message.my, message.mz, message.dim);
		/*if(id == 0){
			Minimap.createTexturedWayPoint(message.group, message.mx, message.my, message.mz, message.dim, message.markerName, message.icon);
		}else if(id == 1){
			Minimap.createWayPoint(message.group, message.mx, message.my, message.mz, message.dim, message.markerName, message.color);
		}else if(id == 2){
			Minimap.deleteWayPoint(message.group, message.markerName);
		}/*else if(id == 3){
			Minimap.createWayPoint(message.group, message.mx, message.my, message.mz, message.dim, message.markerName, message.color, message.borderColor, message.borderColorActive, message.bordered, message.borderedA);
		}else if(id == 4){
			Minimap.createTexturedWayPoint(message.group, message.mx, message.my, message.mz, message.dim, message.markerName, message.icon, message.borderColor, message.borderColorActive, message.bordered, message.borderedA);
		}*/
	}

	@Override
	public void handleServerSide(MessageMinimap message, EntityPlayer player) {

	}

}
