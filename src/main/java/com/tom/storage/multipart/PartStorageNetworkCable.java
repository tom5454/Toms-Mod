package com.tom.storage.multipart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.grid.IGridDevice;
import com.tom.api.multipart.PartDuct;
import com.tom.storage.StorageInit;
import com.tom.storage.item.StorageNetworkCable;
import com.tom.storage.item.StorageNetworkCable.CableColor;
import com.tom.storage.item.StorageNetworkCable.CableType;
import com.tom.storage.multipart.StorageNetworkGrid.IChannelUpdateListener;

public class PartStorageNetworkCable extends PartDuct<StorageNetworkGrid> implements IChannelUpdateListener{
	public CableType type = CableType.NORMAL;
	public CableColor color = CableColor.FLUIX;
	public static final UnlistedPropertyData DATA = new UnlistedPropertyData("data");
	private Channel[] channel;
	private byte[] channelLast = new byte[6];
	private Channel internal;
	private CableData data = new CableData(this);
	public PartStorageNetworkCable() {
		this(CableType.NORMAL, CableColor.FLUIX);
	}
	public PartStorageNetworkCable(CableType t, CableColor c) {
		super(StorageInit.cable, "tomsmodstorage:tm.cable", CableType.NORMAL.getSize(), -1);
		this.type = t;
		this.color = c;
		this.channel = new Channel[6];
		this.internal = new Channel();
		this.internal.listeners.add(this);
		for(int i = 0;i<channel.length;i++)this.channel[i] = internal;
	}
	@Override
	public boolean isValidConnection(EnumFacing side, TileEntity tile) {
		return tile instanceof IGridDevice && ((IGridDevice<?>)tile).getGrid().getClass() == this.grid.getClass();
	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(type == CableType.DENSE){
				/*long time = System.currentTimeMillis() / 2500;
				if(internal.setValue((int) (time % 9)))sendUpdatePacket(true);*/
				if(internal.setValue(8))sendUpdatePacket(true);
			}else if(worldObj.getTotalWorldTime() % 100 == 2){
				//if(internal.setValue(worldObj.rand.nextInt(9)))sendUpdatePacket(true);
				if(internal.setValue(4))sendUpdatePacket(true);
			}
		}
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
		type = CableType.VALUES[nbt.getByte("cableType")];
		color = CableColor.VALUES[nbt.getByte("cableColor")];
	}
	@Override
	protected void updateBox() {
		size = type != null ? type.getSize() : CableType.NORMAL.getSize();
		super.updateBox();
	}
	@Override
	public void writeToPacket(NBTTagCompound tag) {
		tag.setByte("ct", (byte) type.ordinal());
		tag.setByte("cc", (byte) color.ordinal());
		tag.setByteArray("ch", getChannelBytes());
	}
	@Override
	public boolean readFromPacket(NBTTagCompound tag) {
		CableType typeOld = this.type;
		CableColor colorOld = this.color;
		type = CableType.VALUES[tag.getByte("ct")];
		color = CableColor.VALUES[tag.getByte("cc")];
		return typeOld != type || colorOld != color || loadChannels(tag.getByteArray("ch"));
	}
	public static class UnlistedPropertyData implements IUnlistedProperty<CableData> {

		private final String name;

		public UnlistedPropertyData(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isValid(CableData value) {
			return value != null;
		}

		@Override
		public Class<CableData> getType() {
			return CableData.class;
		}

		@Override
		public String valueToString(CableData value) {
			return value.getName();
		}
	}
	public static class CableData implements IStringSerializable{
		private final PartStorageNetworkCable part;
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

		public CableType getType() {
			return part.type;
		}

		public CableColor getColor() {
			return part.color;
		}
		public boolean isValid(){
			return valid;
		}
		@SideOnly(Side.CLIENT)
		public List<BakedQuad> getModel(){
			return model;
		}
		@SideOnly(Side.CLIENT)
		public void setModel(List<BakedQuad> model){
			this.model = model;
			valid = true;
		}
		public int getChannel(EnumFacing side) {
			return part.channelLast[side.ordinal()];
		}
		protected void invalidate(){
			valid = false;
		}
	}
	@Override
	public IBlockState getExtendedState(IBlockState state) {
		if(state instanceof IExtendedBlockState){
			IExtendedBlockState s = (IExtendedBlockState) state;
			/*state = s.withProperty(TYPE, type).withProperty(COLOR, color)
					.withProperty(DOWN, getPropertyValue(EnumFacing.DOWN))
					.withProperty(UP, getPropertyValue(EnumFacing.UP))
					.withProperty(NORTH, getPropertyValue(EnumFacing.NORTH))
					.withProperty(SOUTH, getPropertyValue(EnumFacing.SOUTH))
					.withProperty(WEST, getPropertyValue(EnumFacing.WEST))
					.withProperty(EAST, getPropertyValue(EnumFacing.EAST))
					.withProperty(CHANNEL, channel);*/
			return s.withProperty(DATA, data);
		}
		return state;
	}
	public String createStringFromChannels() {
		return Arrays.toString(channel);
	}
	@Override
	protected IUnlistedProperty<?>[] getUnlistedProperties() {
		//return new IUnlistedProperty<?>[]{TYPE, COLOR, UP, DOWN, NORTH, SOUTH, EAST, WEST, CHANNEL};
		return new IUnlistedProperty<?>[]{DATA};
	}
	@Override
	protected IProperty<?>[] getProperties() {
		return new IProperty<?>[0];
	}
	@Override
	public ItemStack getPick() {
		return new ItemStack(StorageInit.cable, 1, StorageNetworkCable.getMeta(type, color));
	}
	@Override
	protected byte canConnect(PartDuct<?> part, EnumFacing side) {
		if(part instanceof PartStorageNetworkCable){
			PartStorageNetworkCable c = (PartStorageNetworkCable) part;
			if(c.color == CableColor.FLUIX || color == CableColor.FLUIX || c.color == color){
				if(c.type == CableType.SMART && type == CableType.NORMAL){
					setChannel(side.getOpposite(), internal);
					return 5;
				}else if(c.type == CableType.COVERED && type == CableType.NORMAL){
					setChannel(side.getOpposite(), internal);
					return 4;
				}else if(c.type == CableType.DENSE && type == CableType.NORMAL){
					setChannel(side.getOpposite(), internal);
					return 4;
				}else if((c.type == CableType.COVERED || c.type == CableType.NORMAL) && type == CableType.DENSE){
					setChannel(side.getOpposite(), internal);
					return 4;
				}else if(c.type == CableType.SMART && type == CableType.DENSE){
					setChannel(side.getOpposite(), c.getChannel(side));
					return 5;
				}else{
					setChannel(side.getOpposite(), internal);
					return 1;
				}
			}else{
				setChannel(side.getOpposite(), internal);
				return 0;
			}
		}
		setChannel(side.getOpposite(), internal);
		return super.canConnect(part, side);
	}
	public boolean connectsC(EnumFacing side){
		return type == CableType.NORMAL ? connectsE1(side) || connectsInv(side) : type == CableType.DENSE ? connectsE1(side) : false;
	}
	public boolean connectsS(EnumFacing side){
		return type == CableType.NORMAL ? connectsE2(side) : type == CableType.DENSE ? connectsInv(side) || connectsE2(side) : false;
	}
	@Override
	protected void onMarkRenderUpdate() {
		data.invalidate();
	}
	private void setChannel(EnumFacing side, Channel ch){
		boolean notC = channel[side.ordinal()] != ch;
		if(notC && channel[side.ordinal()] != ch){
			channel[side.ordinal()].listeners.remove(this);
		}
		if(notC && ch != internal)
			ch.listeners.add(this);
		if(channelLast[side.ordinal()] != ch.channel){
			markForUpdate();
			channelLast[side.ordinal()] = ch.channel;
		}
		channel[side.ordinal()] = ch;
	}
	public Channel getChannel(EnumFacing side){
		return type == CableType.DENSE ? channel[side.ordinal()] : internal;
	}
	public class Channel {
		protected List<IChannelUpdateListener> listeners = new ArrayList<IChannelUpdateListener>();
		private byte channel = 0;
		public boolean setValue(int ch) {
			byte o = channel;
			channel = (byte) (ch % (getMaxChannels() + 1));
			if(o != channel){
				for(int i = 0;i<listeners.size();i++){
					listeners.get(i).channelUpdate();
				}
				return true;
			}
			return false;
		}

		public byte getValue() {
			return channel;
		}
		@Override
		public String toString() {
			return "ChannelCount:" + channel;
		}
	}
	private byte[] getChannelBytes() {
		byte[] bytes = new byte[channel.length];
		for(int i = 0;i<channel.length;i++)
			bytes[i] = channel[i].getValue();
		return bytes;
	}
	private boolean loadChannels(byte[] ch){
		boolean d = false;
		for(int i = 0;i<channel.length;i++){
			byte o = channel[i].getValue();
			d = d || o != ch[i];
			channelLast[i] = ch[i];
		}
		return d;
	}
	@Override
	public void channelUpdate() {
		sendUpdatePacket(true);
	}
	private byte getMaxChannels(){
		return (byte) (type == CableType.DENSE ? 9 : 8);
	}
}
