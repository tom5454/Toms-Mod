package com.tom.network.messages;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import com.tom.api.multipart.IGuiMultipart;
import com.tom.api.tileentity.IGuiTile;
import com.tom.lib.network.MessageBase;

import io.netty.buffer.ByteBuf;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.IPartSlot;
import mcmultipart.slot.SlotRegistry;

public class MessageGuiButtonPress extends MessageBase<MessageGuiButtonPress> {
	private int id, x, y, z, extra, pos = -1;
	private boolean isSendingToContainer, isMultipart;

	public MessageGuiButtonPress() {
	}

	public MessageGuiButtonPress(int id, TileEntity tile) {
		this.id = id;
		isMultipart = false;
		if (tile != null) {
			this.x = tile.getPos().getX();
			this.y = tile.getPos().getY();
			this.z = tile.getPos().getZ();
			isSendingToContainer = false;
		} else {
			isSendingToContainer = true;
		}
		this.extra = 0;
	}

	public MessageGuiButtonPress(int id, TileEntity tile, int extra) {
		this.id = id;
		isMultipart = false;
		if (tile != null) {
			this.x = tile.getPos().getX();
			this.y = tile.getPos().getY();
			this.z = tile.getPos().getZ();
			isSendingToContainer = false;
		} else {
			isSendingToContainer = true;
		}
		this.extra = extra;
	}

	public MessageGuiButtonPress(int id, BlockPos pos) {
		this.id = id;
		isMultipart = false;
		if (pos != null) {
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			isSendingToContainer = false;
		} else {
			isSendingToContainer = true;
		}
		this.extra = 0;
	}

	public MessageGuiButtonPress(int id, BlockPos pos, int extra) {
		this.id = id;
		isMultipart = false;
		if (pos != null) {
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			isSendingToContainer = false;
		} else {
			isSendingToContainer = true;
		}
		this.extra = extra;
	}

	public MessageGuiButtonPress(int id) {
		this.id = id;
		isMultipart = false;
		isSendingToContainer = true;
		this.extra = 0;
	}

	public MessageGuiButtonPress(int id, int extra) {
		this.id = id;
		isMultipart = false;
		isSendingToContainer = true;
		this.extra = extra;
	}

	public MessageGuiButtonPress(int id, IGuiMultipart tile) {
		this.id = id;
		if (tile != null) {
			this.x = tile.getPos2().getX();
			this.y = tile.getPos2().getY();
			this.z = tile.getPos2().getZ();
			setPos(tile);
			isSendingToContainer = false;
		} else {
			isSendingToContainer = true;
		}
		this.extra = 0;
	}

	public MessageGuiButtonPress(int id, IGuiMultipart tile, int extra) {
		this.id = id;
		if (tile != null) {
			this.x = tile.getPos2().getX();
			this.y = tile.getPos2().getY();
			this.z = tile.getPos2().getZ();
			setPos(tile);
			isSendingToContainer = false;
		} else {
			isSendingToContainer = true;
		}
		this.extra = extra;
	}

	private void setPos(IGuiMultipart tile) {
		IPartSlot slot = tile.getPosition();
		if (slot != null)
			pos = SlotRegistry.INSTANCE.getSlotID(slot);
		isMultipart = slot != null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.isSendingToContainer = buf.readBoolean();
		this.isMultipart = buf.readBoolean();
		if (!this.isSendingToContainer) {
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
		if (!this.isSendingToContainer) {
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
			buf.writeInt(pos);
		}
		buf.writeInt(extra);
	}

	@Override
	public void handleClientSide(MessageGuiButtonPress message, EntityPlayer player) {
	}

	@Override
	public void handleServerSide(MessageGuiButtonPress message, EntityPlayer player) {
		if (message.isSendingToContainer) {
			if (player.openContainer instanceof IGuiTile) {
				((IGuiTile) player.openContainer).buttonPressed(player, message.id, message.extra);
			}
		} else if (message.isMultipart) {
			IMultipartContainer container = MultipartHelper.getContainer(player.world, new BlockPos(message.x, message.y, message.z)).orElse(null);
			if (container == null) { return; }
			Optional<IPartInfo> part = container.get(SlotRegistry.INSTANCE.getSlotFromID(message.pos));
			if (part.isPresent() && part.get().getTile() instanceof IGuiMultipart) {
				((IGuiMultipart) part.get().getTile()).buttonPressed(player, message.id, message.extra);
			}
		} else {
			TileEntity tile = player.world.getTileEntity(new BlockPos(message.x, message.y, message.z));
			if (tile instanceof IGuiTile) {
				((IGuiTile) tile).buttonPressed(player, message.id, message.extra);
				tile.markDirty();
			}
		}
	}

}
