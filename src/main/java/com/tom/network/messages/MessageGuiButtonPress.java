package com.tom.network.messages;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import com.tom.api.multipart.IGuiMultipart;
import com.tom.api.tileentity.IGuiTile;
import com.tom.network.MessageBase;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;

public class MessageGuiButtonPress extends MessageBase<MessageGuiButtonPress>{
	private int id, x, y, z, extra, pos = -1;
	private boolean isSendingToContainer, isMultipart;
	public MessageGuiButtonPress(){}
	public MessageGuiButtonPress(int id, TileEntity tile){
		this.id = id;
		isMultipart = false;
		if(tile != null){
			this.x = tile.getPos().getX();
			this.y = tile.getPos().getY();
			this.z = tile.getPos().getZ();
			isSendingToContainer = false;
		}else{
			isSendingToContainer = true;
		}
		this.extra = 0;
	}
	public MessageGuiButtonPress(int id, TileEntity tile, int extra){
		this.id = id;
		isMultipart = false;
		if(tile != null){
			this.x = tile.getPos().getX();
			this.y = tile.getPos().getY();
			this.z = tile.getPos().getZ();
			isSendingToContainer = false;
		}else{
			isSendingToContainer = true;
		}
		this.extra = extra;
	}
	public MessageGuiButtonPress(int id, BlockPos pos){
		this.id = id;
		isMultipart = false;
		if(pos != null){
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			isSendingToContainer = false;
		}else{
			isSendingToContainer = true;
		}
		this.extra = 0;
	}
	public MessageGuiButtonPress(int id, BlockPos pos, int extra){
		this.id = id;
		isMultipart = false;
		if(pos != null){
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			isSendingToContainer = false;
		}else{
			isSendingToContainer = true;
		}
		this.extra = extra;
	}
	public MessageGuiButtonPress(int id){
		this.id = id;
		isMultipart = false;
		isSendingToContainer = true;
		this.extra = 0;
	}
	public MessageGuiButtonPress(int id, int extra){
		this.id = id;
		isMultipart = false;
		isSendingToContainer = true;
		this.extra = extra;
	}
	public MessageGuiButtonPress(int id, IGuiMultipart tile){
		this.id = id;
		isMultipart = true;
		if(tile != null){
			this.x = tile.getPos2().getX();
			this.y = tile.getPos2().getY();
			this.z = tile.getPos2().getZ();
			pos = tile.getPosition().ordinal();
			isSendingToContainer = false;
		}else{
			isSendingToContainer = true;
		}
		this.extra = 0;
	}
	public MessageGuiButtonPress(int id, IGuiMultipart tile, int extra){
		this.id = id;
		isMultipart = true;
		if(tile != null){
			this.x = tile.getPos2().getX();
			this.y = tile.getPos2().getY();
			this.z = tile.getPos2().getZ();
			pos = tile.getPosition().ordinal();
			isSendingToContainer = false;
		}else{
			isSendingToContainer = true;
		}
		this.extra = extra;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.isSendingToContainer = buf.readBoolean();
		this.isMultipart = buf.readBoolean();
		if(!this.isSendingToContainer){
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.z = buf.readInt();
			this.pos = buf.readInt();
		}
		this.extra = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeBoolean(isSendingToContainer);
		buf.writeBoolean(isMultipart);
		if(!this.isSendingToContainer){
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
			buf.writeInt(pos);
		}
		buf.writeInt(extra);
	}
	@Override
	public void handleClientSide(MessageGuiButtonPress message, EntityPlayer player) {}

	@Override
	public void handleServerSide(MessageGuiButtonPress message,
			EntityPlayer player) {
		if(message.isSendingToContainer){
			if(player.openContainer instanceof IGuiTile){
				((IGuiTile)player.openContainer).buttonPressed(player, message.id, message.extra);
			}
		}else if(message.isMultipart){
			IMultipartContainer container = MultipartHelper.getPartContainer(player.worldObj, new BlockPos(message.x, message.y, message.z));
			if (container == null) {
				return;
			}
			ISlottedPart part = container.getPartInSlot(PartSlot.VALUES[message.pos]);
			if(part instanceof IGuiMultipart){
				((IGuiMultipart)part).buttonPressed(player, message.id, message.extra);
			}
		}else{
			TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
			if(tile instanceof IGuiTile){
				((IGuiTile)tile).buttonPressed(player, message.id, message.extra);
				tile.markDirty();
			}
		}
	}

}
