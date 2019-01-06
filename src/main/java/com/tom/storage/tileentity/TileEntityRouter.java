package com.tom.storage.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.grid.GridEnergyStorage;
import com.tom.api.grid.StorageNetworkGrid;
import com.tom.lib.api.grid.IGrid;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridUpdateListener;
import com.tom.lib.api.grid.VirtualGridDevice;
import com.tom.storage.block.StorageSystemRouter;
import com.tom.storage.handler.StorageData;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.IGridDeviceHostFacing;

public class TileEntityRouter extends TileEntityChannel implements IGridDeviceHostFacing {
	public class RouterSide extends VirtualGridDevice<StorageNetworkGrid> implements IGridUpdateListener {
		private final EnumFacing side;
		public RouterSide(EnumFacing side) {
			this.side = side;
			grid.getData().dataSupplier = TileEntityRouter.this::getSData;
		}

		@Override
		public BlockPos getPos2() {
			return pos;
		}

		@Override
		public World getWorld2() {
			return world;
		}

		@Override
		public boolean isConnected(EnumFacing side) {
			return this.side == side;
		}

		@Override
		public boolean isValidConnection(EnumFacing side) {
			return this.side == side;
		}

		@Override
		public boolean isValid() {
			return !isInvalid();
		}

		@Override
		public StorageNetworkGrid constructGrid() {
			return new StorageNetworkGrid();
		}

		@Override
		public void onGridReload() {
			grid.getData().dataSupplier = TileEntityRouter.this::getSData;
		}

		@Override
		public void onGridPostReload() {

		}

	}

	private GridEnergyStorage energy = new GridEnergyStorage(100, 0);

	public InventoryBasic inv = new InventoryBasic("", false, 3);
	private RouterSide[] routerFaces;

	public TileEntityRouter() {
		routerFaces = new RouterSide[6];
		for (int i = 0;i < routerFaces.length;i++) {
			routerFaces[i] = new RouterSide(EnumFacing.getFront(i));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (int i = 0;i < routerFaces.length;i++) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("id", i);
			routerFaces[i].writeToNBT(tag);
			list.appendTag(tag);
		}
		compound.setTag("data", list);
		compound.setTag("inventory", TomsModUtils.saveAllItems(inv));
		compound.setTag("energy", energy.writeToNBT(new NBTTagCompound()));
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList list = compound.getTagList("data", 10);
		for (int i = 0;i < list.tagCount();i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			routerFaces[tag.getInteger("id")].readFromNBT(tag);
		}
		list = compound.getTagList("inventory", 10);
		TomsModUtils.loadAllItems(list, inv);
		energy.readFromNBT(compound.getCompoundTag("energy"));
	}

	public void neighborUpdateGrid(EnumFacing side) {
		routerFaces[side.ordinal()].neighborUpdateGrid(false);
	}
	//int allChannels = Arrays.stream(routerFaces).filter(ChannelSource::isNormal).mapToInt(ChannelSource::getChannels).sum();
	//return allChannels / 8 + (allChannels % 8 == 0 ? 0 : 1);

	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <G extends IGrid<?, G>> IGridDevice<G> getDevice(EnumFacing facing, Class<G> gridClass, Object... objects) {
		boolean dense = objects != null && objects.length > 0;
		if(gridClass == StorageNetworkGrid.class){
			if(dense){
				return (IGridDevice<G>) this;
			}
			if(facing != null)return (IGridDevice<G>) routerFaces[facing.ordinal()];
		}
		return null;
	}
	public StorageData getSData(){
		return grid.getSData();
	}
	@Override
	public void updateEntity(IBlockState currentState) {
		for (int i = 0;i < routerFaces.length;i++) {
			routerFaces[i].update();
		}
		if (!world.isRemote && world.getTotalWorldTime() % 5 == 0){
			StorageData d = getSData();
			TomsModUtils.setBlockStateWithCondition(world, pos, currentState, StorageSystemRouter.STATE, d.networkState.fullyActive() && d.showChannels() ? 2 : d.networkState.isPowered() && d.hasEnergy() ? 1 : 0);
		}
	}

	@Override
	public double getPowerDrained() {
		return 2;
	}

	@Override
	public int getPriority() {
		return 10;
	}
}
