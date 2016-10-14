package com.tom.network.messages;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import com.tom.api.tileentity.ILookDetector;
import com.tom.network.MessageBase;

import com.tom.core.tileentity.TileEntityCamera;

import io.netty.buffer.ByteBuf;

public class MessageCamera extends MessageBase<MessageCamera>{
	private int camX, camY, camZ;
	private boolean mode, eC, isClient, isRelative, eEsc;
	private float yaw,pitch,yawMax,pitchMax,yawMin,pitchMin;
	private double cX,cY,cZ;
	public MessageCamera(int x, int y, int z, boolean mode, boolean eC, boolean eEsc, TileEntityCamera cam){
		this.camX = x;
		this.camY = y;
		this.camZ = z;
		this.mode = mode;
		this.eC = eC;
		this.cX = cam.camPosX;
		this.cY = cam.camPosY;
		this.cZ = cam.camPosZ;
		this.yawMin = Math.min(cam.yawMin, cam.yawMax);
		this.yawMax = Math.max(cam.yawMin, cam.yawMax);
		this.pitchMin = Math.min(cam.pitchMin, cam.pitchMax);
		this.pitchMax = Math.max(cam.pitchMin, cam.pitchMax);
		this.isClient = true;
		this.yaw = cam.yaw;
		this.pitch = cam.pitch;
		this.isRelative = cam.isRelativeCoord;
		this.eEsc = eEsc;
	}
	public MessageCamera(){}
	public MessageCamera(int x, int y, int z, boolean mode){
		this.camX = x;
		this.camY = y;
		this.camZ = z;
		this.mode = mode;
		this.isClient = false;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		this.isClient = buf.readBoolean();
		this.camX = buf.readInt();
		this.camY = buf.readInt();
		this.camZ = buf.readInt();
		this.mode = buf.readBoolean();
		if(this.isClient){
			this.eC = buf.readBoolean();
			this.pitch = buf.readFloat();
			this.yaw = buf.readFloat();
			this.pitchMin = buf.readFloat();
			this.pitchMax = buf.readFloat();
			this.yawMin = buf.readFloat();
			this.yawMax = buf.readFloat();
			this.cX = buf.readDouble();
			this.cY = buf.readDouble();
			this.cZ = buf.readDouble();
			this.isRelative = buf.readBoolean();
			this.eEsc = buf.readBoolean();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(isClient);
		buf.writeInt(camX);
		buf.writeInt(camY);
		buf.writeInt(camZ);
		buf.writeBoolean(mode);
		if(this.isClient){
			buf.writeBoolean(eC);
			buf.writeFloat(pitch);
			buf.writeFloat(yaw);
			buf.writeFloat(pitchMin);
			buf.writeFloat(pitchMax);
			buf.writeFloat(yawMin);
			buf.writeFloat(yawMax);
			buf.writeDouble(cX);
			buf.writeDouble(cY);
			buf.writeDouble(cZ);
			buf.writeBoolean(isRelative);
			buf.writeBoolean(eEsc);
		}
	}

	@Override
	public void handleClientSide(MessageCamera message, EntityPlayer player) {
		TileEntityCamera camTe = (TileEntityCamera) player.worldObj.getTileEntity(new BlockPos(message.camX, message.camY, message.camZ));
		camTe.setValues(message.yaw, message.pitch, message.yawMin, message.pitchMin, message.yawMax, message.pitchMax, message.cX, message.cY, message.cZ, message.isRelative);
		camTe.connectPlayerClient(message.mode,message.eC, message.eEsc);
	}

	@Override
	public void handleServerSide(MessageCamera message, EntityPlayer player) {
		TileEntity tileentity = player.worldObj.getTileEntity(new BlockPos(message.camX, message.camY, message.camZ));
		if(tileentity instanceof ILookDetector){
			ILookDetector te = (ILookDetector) tileentity;
			te.setConnect(message.mode, player);
		}
	}

}
