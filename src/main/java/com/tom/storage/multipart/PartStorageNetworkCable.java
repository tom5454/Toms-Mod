package com.tom.storage.multipart;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.grid.IDenseGridDevice;
import com.tom.api.grid.IGridDevice;
import com.tom.api.multipart.IDuctModule;
import com.tom.api.multipart.PartDuct;
import com.tom.api.tileentity.ISecuredTileEntity;
import com.tom.client.EventHandlerClient;
import com.tom.handler.WorldHandler;
import com.tom.storage.handler.StorageNetworkGrid;
import com.tom.storage.handler.StorageNetworkGrid.Channel;
import com.tom.storage.handler.StorageNetworkGrid.IAdvRouterTile;
import com.tom.storage.handler.StorageNetworkGrid.IChannelUpdateListener;
import com.tom.storage.handler.StorageNetworkGrid.IControllerTile;
import com.tom.storage.handler.StorageNetworkGrid.IRouterTile;
import com.tom.storage.multipart.block.StorageNetworkCable;
import com.tom.storage.multipart.block.StorageNetworkCable.CableColor;
import com.tom.storage.multipart.block.StorageNetworkCable.CableType;

public class PartStorageNetworkCable extends PartDuct<StorageNetworkGrid> implements IChannelUpdateListener, ISecuredTileEntity, IDenseGridDevice {
	public StorageNetworkCable.CableType type = StorageNetworkCable.CableType.NORMAL;
	public CableColor color = CableColor.FLUIX;
	private Channel[] channel;
	private byte[] channelLast = new byte[6];
	private byte chLast, ch;
	private Channel internal;
	private CableData data = new CableData(this);
	public AxisAlignedBB module_connection = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

	public PartStorageNetworkCable() {
		super("tomsmodstorage:tm.cable", .1);
		this.channel = new Channel[6];
		this.internal = new Channel();
		this.internal.listeners.add(this);
		for (int i = 0;i < channel.length;i++)
			this.channel[i] = internal;
	}

	@Override
	protected void updateBox() {
		super.updateBox();
		double start = 0.5 - size;
		double stop = 0.5 + size;
		module_connection = new AxisAlignedBB(start, 0.1, start, stop, start, stop);
	}

	public void init(StorageNetworkCable.CableType t, CableColor c) {
		this.type = t;
		this.color = c;
		size = type != null ? type.getSize() : 0.1;
		updateBox();
	}

	@Override
	public boolean isValidConnection(EnumFacing side, TileEntity tile) {// ((IControllerTile)
																		// te).getControllerOnSide(d.getOpposite())
		if (type != StorageNetworkCable.CableType.DENSE && tile instanceof IGridDevice && !(tile instanceof IDuctModule<?>) && ((IGridDevice<?>) tile).getGrid().getClass() == this.grid.getClass())
			return true;
		if (tile instanceof IControllerTile)
			return ((IControllerTile) tile).getControllerOnSide(side.getOpposite()) != null;
		if (tile instanceof IRouterTile || tile instanceof IAdvRouterTile)
			return true;
		return false;
	}

	@Override
	public int isValidConnectionA(EnumFacing side, TileEntity tile) {
		if (type != StorageNetworkCable.CableType.DENSE && tile instanceof IGridDevice && !(tile instanceof IDuctModule<?>) && ((IGridDevice<?>) tile).getGrid().getClass() == this.grid.getClass()) {
			/*if(type == StorageNetworkGrid.CableType.DENSE)
				setChannel(side, ((IGridDevice<StorageNetworkGrid>)tile).getGrid().channel);*/
			return 2;
		}
		setChannel(side.getOpposite(), internal);
		if (tile instanceof IControllerTile)
			return ((IControllerTile) tile).getControllerOnSide(side.getOpposite()) != null ? type == StorageNetworkCable.CableType.DENSE ? 1 : 2 : 0;
		if (tile instanceof IRouterTile || tile instanceof IAdvRouterTile)
			return type == StorageNetworkCable.CableType.DENSE ? 1 : 2;
		return 0;
	}

	/*@Override
	public void updateEntity() {
		if(!world.isRemote){
			//if(world.getTotalWorldTime() % 50 == 0)markForUpdate();
			ch = -1;
			if(internal.setValue(getChannel()))markForUpdate();
			updateTicker();
		}
	}*/
	private byte getChannel() {
		if (grid.getData().networkState.showChannels()) {
			if (grid.channel.channel == 0) {
				if (ch >= 0)
					return ch;
				else {
					byte c = chLast;
					chLast = 0;
					return c;
				}
			} else
				return chLast = ch = grid.channel.channel;
		} else
			return 0;
	}

	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}

	@Override
	public int getPropertyValue(EnumFacing side) {
		return connectsS(side) ? 4 : connectsC(side) ? 3 : connectsM(side) ? 2 : connects(side) || connectsInv(side) ? 1 : 0;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("cableType", (byte) type.ordinal());
		nbt.setByte("cableColor", (byte) color.ordinal());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		type = StorageNetworkCable.CableType.VALUES[nbt.getByte("cableType")];
		color = CableColor.VALUES[nbt.getByte("cableColor")];
		size = type != null ? type.getSize() : 0.1;
		updateBox();
	}

	@Override
	public void writeToPacketI(NBTTagCompound tag) {
		tag.setByte("ct", (byte) type.ordinal());
		tag.setByte("cc", (byte) color.ordinal());
		tag.setByteArray("ch", getChannelBytes());
	}

	@Override
	public boolean readFromPacketI(NBTTagCompound tag) {
		StorageNetworkCable.CableType typeOld = this.type;
		CableColor colorOld = this.color;
		type = StorageNetworkCable.CableType.VALUES[tag.getByte("ct")];
		color = CableColor.VALUES[tag.getByte("cc")];
		double size = type != null ? type.getSize() : 0.1f;
		if (this.size != size) {
			this.size = size;
			updateBox();
		}
		return loadChannels(tag.getByteArray("ch")) || typeOld != type || colorOld != color;
	}

	public static class CableData implements IStringSerializable {
		private final PartStorageNetworkCable part;
		@SideOnly(Side.CLIENT)
		private int textureIns = 0;

		public CableData(PartStorageNetworkCable part) {
			this.part = part;
		}

		private boolean valid = false;
		@SideOnly(Side.CLIENT)
		private List<BakedQuad> model;

		@Override
		public String getName() {
			return "PartStorageNetworkCableData[type=" + part.type + ",color" + part.color + ",connection:[" + part.createStringFromCache() + "],channel:[" + part.createStringFromChannels() + "]]";
		}

		public int getValue(EnumFacing side) {
			return part.getPropertyValue(side);
		}

		public StorageNetworkCable.CableType getType() {
			return part.type;
		}

		public CableColor getColor() {
			return part.color;
		}

		@SideOnly(Side.CLIENT)
		public boolean isValid() {
			return valid && EventHandlerClient.textureIns == textureIns;
		}

		@SideOnly(Side.CLIENT)
		public List<BakedQuad> getModel() {
			return model;
		}

		@SideOnly(Side.CLIENT)
		public void setModel(List<BakedQuad> model) {
			this.model = model;
			valid = true;
			textureIns = EventHandlerClient.textureIns;
		}

		public int getChannel(EnumFacing side) {
			return part.channelLast[side.ordinal()];
		}

		protected void invalidate() {
			valid = false;
		}
	}

	public String createStringFromChannels() {
		return Arrays.toString(channel);
	}

	@Override
	protected byte canConnect(PartDuct<?> part, EnumFacing side) {
		if (part instanceof PartStorageNetworkCable) {
			PartStorageNetworkCable c = (PartStorageNetworkCable) part;
			if (c.color == CableColor.FLUIX || color == CableColor.FLUIX || c.color == color) {
				if (c.type == StorageNetworkCable.CableType.SMART && type == StorageNetworkCable.CableType.NORMAL) {
					setChannel(side.getOpposite(), internal);
					return 5;
				} else if (c.type == StorageNetworkCable.CableType.COVERED && type == StorageNetworkCable.CableType.NORMAL) {
					setChannel(side.getOpposite(), internal);
					return 4;
				} else if (c.type == StorageNetworkCable.CableType.DENSE && type == StorageNetworkCable.CableType.NORMAL) {
					// setChannel(side.getOpposite(), internal);
					return 0;
				} else if ((c.type == StorageNetworkCable.CableType.COVERED || c.type == StorageNetworkCable.CableType.NORMAL) && type == StorageNetworkCable.CableType.DENSE) {
					// setChannel(side.getOpposite(), internal);
					return 0;
				} else if (c.type == StorageNetworkCable.CableType.SMART && type == StorageNetworkCable.CableType.DENSE) {
					// setChannel(side.getOpposite(), c.getChannel(side));
					return 0;
				} else {
					setChannel(side.getOpposite(), internal);
					return 1;
				}
			} else {
				setChannel(side.getOpposite(), internal);
				return 0;
			}
		}
		setChannel(side.getOpposite(), internal);
		return super.canConnect(part, side);
	}

	public boolean connectsC(EnumFacing side) {
		return type == StorageNetworkCable.CableType.NORMAL ? connectsE1(side) || connectsInv(side) : type == StorageNetworkCable.CableType.DENSE ? connectsE1(side) : false;
	}

	public boolean connectsS(EnumFacing side) {
		return type == StorageNetworkCable.CableType.NORMAL ? connectsE2(side) : type == StorageNetworkCable.CableType.DENSE ? connectsInv(side) || connectsE2(side) : false;
	}

	@Override
	protected void onMarkRenderUpdate() {
		data.invalidate();
	}

	private void setChannel(EnumFacing side, Channel ch) {
		boolean notC = channel[side.ordinal()] != ch;
		if (notC && channel[side.ordinal()] != ch) {
			channel[side.ordinal()].listeners.remove(this);
		}
		if (notC && ch != internal)
			ch.listeners.add(this);
		if (channelLast[side.ordinal()] != ch.channel) {
			markForUpdate();
			channelLast[side.ordinal()] = ch.channel;
		}
		channel[side.ordinal()] = ch;
	}

	public Channel getChannel(EnumFacing side) {
		return /*type == StorageNetworkGrid.CableType.DENSE ? channel[side.ordinal()] : */internal;
	}

	private byte[] getChannelBytes() {
		byte[] bytes = new byte[EnumFacing.VALUES.length];
		boolean hasEnergy = grid.getData().isFullyActive();
		for (int i = 0;i < EnumFacing.VALUES.length;i++)
			bytes[i] = hasEnergy ? getChannel(EnumFacing.VALUES[i]).getValue() : 0;
		return bytes;
	}

	private boolean loadChannels(byte[] ch) {
		boolean d = false;
		for (int i = 0;i < channelLast.length;i++) {
			byte o = channelLast[i];
			d = d || o != ch[i];
			channelLast[i] = ch[i];
		}
		return d;
	}

	@Override
	public void channelUpdate() {
		internal.setValue(getChannel());
		markForUpdate();
	}

	public CableData getData() {
		return data;
	}

	@Override
	public BlockPos getSecurityStationPos() {
		return getGrid().getData().getSecurityStationPos();
	}

	@Override
	public void setMaster(IGridDevice<StorageNetworkGrid> master, int size) {
		super.setMaster(master, size);
		grid.channel.listeners.add(this);
		WorldHandler.queueTask(world.provider.getDimension(), this::channelUpdate);
	}

	@Override
	public boolean isDenseGridDevice() {
		return type == CableType.DENSE;
	}
}
