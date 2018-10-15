package com.tom.storage.tileentity;

import java.util.Arrays;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.grid.GridEnergyStorage;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridUpdateListener;
import com.tom.storage.block.StorageSystemRouter;
import com.tom.storage.handler.NetworkState;
import com.tom.storage.handler.StorageData;
import com.tom.storage.handler.StorageNetworkGrid;
import com.tom.storage.handler.StorageNetworkGrid.IChannelLoadListener;
import com.tom.storage.handler.StorageNetworkGrid.IChannelSource;
import com.tom.storage.handler.StorageNetworkGrid.IRouter;
import com.tom.storage.handler.StorageNetworkGrid.IRouterTile;
import com.tom.util.TomsModUtils;

public class TileEntityRouter extends TileEntityTomsMod implements IRouterTile {
	private GridEnergyStorage energy = new GridEnergyStorage(100, 0);
	private int ticks;

	public static class ChannelSource implements IRouter, IChannelLoadListener {
		protected StorageNetworkGrid grid;
		protected IGridDevice<StorageNetworkGrid> master;
		protected static final String GRID_TAG_NAME = "grid";
		private static final String MASTER_NBT_NAME = "isMaster";
		private boolean firstStart = true;
		private boolean secondTick = false;
		private boolean isMaster = false;
		private int suction = -1;
		private int side;
		private boolean updateGrid, updateGrid2;
		private NBTTagCompound last;
		private boolean dense;
		private IChannelSource tile;

		public ChannelSource(IChannelSource tile, int i) {
			this.tile = tile;
			grid = constructGrid();
			side = i;
		}

		@Override
		public boolean isMaster() {
			return isMaster;
		}

		@Override
		public void setMaster(IGridDevice<StorageNetworkGrid> master, int size) {
			this.master = master;
			// boolean wasMaster = isMaster;
			isMaster = master == this;
			grid.invalidate();
			this.grid = master.getGrid();
			/*if(isMaster) {
				grid.reloadGrid(getWorld(), this);
			}*/
		}

		@Override
		public StorageNetworkGrid getGrid() {
			return grid;
		}

		@Override
		public IGridDevice<StorageNetworkGrid> getMaster() {
			grid.forceUpdateGrid(getWorld2(), this);
			return master;
		}

		@Override
		public void invalidateGrid() {
			grid.getData().removeEnergyStorage(((TileEntityRouter) tile).energy);
			this.master = null;
			this.isMaster = false;
			last = grid.exportToNBT();
			boolean wasDense = grid.isDense();
			grid.invalidate();
			this.grid = this.constructGrid();
			if (wasDense)
				((TileEntityRouter) tile).setData(new StorageData());
		}

		public StorageNetworkGrid constructGrid() {
			StorageNetworkGrid grid = new StorageNetworkGrid();
			// grid.setData(tile.getData());
			return grid;
		}

		@Override
		public void setSuctionValue(int suction) {
			this.suction = suction;
		}

		@Override
		public int getSuctionValue() {
			return this.suction;
		}

		@Override
		public void updateState() {
			updateGrid = true;
		}

		@Override
		public void setGrid(StorageNetworkGrid newGrid) {
			grid.invalidate();
			this.grid = newGrid;
		}

		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			compound.setTag(GRID_TAG_NAME, grid.exportToNBT());
			compound.setBoolean(MASTER_NBT_NAME, isMaster);
			return compound;
		}

		public void readFromNBT(NBTTagCompound compound) {
			grid.importFromNBT(compound.getCompoundTag(GRID_TAG_NAME));
			this.isMaster = compound.getBoolean(MASTER_NBT_NAME);
		}

		@Override
		public boolean isConnected(EnumFacing side) {
			return side.ordinal() == this.side;
		}

		@Override
		public boolean isValidConnection(EnumFacing side) {
			return side.ordinal() == this.side;
		}

		public void update() {
			if (!tile.getWorld2().isRemote) {
				if (((TileEntityRouter) tile).hasDense) {
					grid.setActive(true);
				} else {
					grid.setActive(false);
				}
				if (grid.isDense()) {
					if (grid.getData() != tile.getData()) {
						((TileEntityRouter) tile).setData(grid.getData());
						updateGrid = true;
					}
					dense = true;
				} else if (dense) {
					dense = false;
					boolean dense = false;
					for (ChannelSource s : ((TileEntityRouter) tile).routerFaces) {
						if (s.grid.isDense()) {
							dense = true;
							((TileEntityRouter) tile).setData(s.grid.getData());
							break;
						}
					}
					if (!dense) {
						((TileEntityRouter) tile).setData(new StorageData());
					}
				}
				if (updateGrid) {
					updateGrid();
					updateGrid = false;
				}
				if (tile.getWorld2().getTotalWorldTime() % 50 == 0)
					updateGrid = true;
				if (firstStart) {
					this.firstStart = false;
					this.secondTick = true;
					if (this.isMaster) {
						grid.setMaster(this);
						grid.forceUpdateGrid(tile.getWorld2(), this);
					}
					TileEntityTomsMod.markBlockForUpdate(tile.getWorld2(), tile.getPos2());
				}
				if (secondTick) {
					this.secondTick = false;
					if (master == null) {
						grid.reloadGrid(tile.getWorld2(), this);
					}
					tile.markDirty2();
					TileEntityTomsMod.markBlockForUpdate(tile.getWorld2(), tile.getPos2());
				}
				if (this.master == null && !secondTick) {
					if (updateGrid2) {
						this.constructGrid().forceUpdateGrid(tile.getWorld2(), this);
						updateGrid2 = false;
					} else {
						updateGrid2 = true;
					}
				}
				if (this.master != null && grid.getData() != tile.getData()) {
					updateAll(0, grid);
				}
			}
			if (isMaster) {
				grid.updateGrid(tile.getWorld2(), this);
			}
		}

		private void updateAll(int depth, StorageNetworkGrid grid) {
			if (!grid.isDense()) {
				if (grid.getData().controllers.size() > 0) {
					for (int i = 0;i < grid.getData().controllers.size();i++)
						if (!tile.getData().controllers.contains(grid.getData().controllers.get(i)))
							tile.getData().controllers.add(grid.getData().controllers.get(i));
					grid.setData(tile.getData());
				} else
					grid.setData(tile.getData());
			}
		}

		@Override
		public BlockPos getPos2() {
			return tile.getPos2();
		}

		@Override
		public World getWorld2() {
			return tile.getWorld2();
		}

		public void neighborUpdateGrid() {
			updateGrid = true;
		}

		private void updateGrid() {
			if (master != null && master != this && master.isValid())
				master.updateState();
			else {
				if (master == null) {
					StorageNetworkGrid grid = this.constructGrid();
					grid.setMaster(this);
					grid.forceUpdateGrid(this.getWorld2(), this);
				} else {
					grid.forceUpdateGrid(this.getWorld2(), this);
				}
			}
		}

		@Override
		public IChannelSource getTile() {
			return tile;
		}

		@Override
		public boolean isValid() {
			return tile.isValid();
		}

		@Override
		public NBTTagCompound getGridData() {
			return last;
		}

		@Override
		public double getPowerDrained() {
			return 0;
		}

		@Override
		public int getPriority() {
			return 1000;
		}

		@Override
		public void onGridReload() {
			if (grid.isDense()) {
				if (grid.getData() != tile.getData()) {
					((TileEntityRouter) tile).setData(grid.getData());
					// updateGrid = true;
				}
				dense = true;
			} else if (dense) {
				dense = false;
				boolean dense = false;
				for (ChannelSource s : ((TileEntityRouter) tile).routerFaces) {
					if (s.grid.isDense()) {
						dense = true;
						((TileEntityRouter) tile).setData(s.grid.getData());
						break;
					}
				}
				if (!dense) {
					((TileEntityRouter) tile).setData(new StorageData());
				}
			} else {
				for (IRouter r : ((TileEntityRouter) tile).getRouters()) {
					if (r != this && (r.getGrid().isDense() || ((ChannelSource) r).dense))
						((IGridUpdateListener) r).onGridReload();
				}
			}
			grid.getData().addEnergyStorage(((TileEntityRouter) tile).energy);
		}

		@Override
		public void onGridPostReload() {

		}

		@Override
		public void onPartsUpdate() {
			grid.getData().addEnergyStorage(((TileEntityRouter) tile).energy);
		}

		@Override
		public void setActive(NetworkState state) {

		}

		public int getChannels() {
			return grid.getChannel();
		}

		public boolean isNormal() {
			return !dense;
		}
	}

	public InventoryBasic inv = new InventoryBasic("", false, 3);
	private ChannelSource[] routerFaces;

	public TileEntityRouter() {
		routerFaces = new ChannelSource[6];
		for (int i = 0;i < routerFaces.length;i++) {
			routerFaces[i] = new ChannelSource(this, i);
		}
	}

	@Override
	public void setData(StorageData data2) {
		data = data2;
	}

	@Override
	public IRouter getRouterOnSide(EnumFacing side) {
		return routerFaces[side.ordinal()];
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

	private StorageData data = new StorageData();
	private boolean hasDense;

	@Override
	public StorageData getData() {
		return data;
	}

	@Override
	public void updateEntity(IBlockState currentState) {
		if (ticks >= 0)
			ticks++;
		if (ticks > 30 && !world.isRemote) {
			data = new StorageData();
			for (int i = 0;i < routerFaces.length;i++) {
				routerFaces[i].getMaster();
			}
			ticks = -1;
			markDirty();
		} else {
			hasDense = false;
			for (int i = 0;i < routerFaces.length;i++) {
				if (routerFaces[i].dense) {
					hasDense = true;
					break;
				}
			}
			for (int i = 0;i < routerFaces.length;i++) {
				routerFaces[i].update();
			}
		}
		if (!world.isRemote)
			TomsModUtils.setBlockStateWithCondition(world, pos, currentState, StorageSystemRouter.STATE, getData().networkState.fullyActive() && getData().showChannels() ? 2 : getData().networkState.isPowered() && getData().hasEnergy() ? 1 : 0);
	}

	public void neighborUpdateGrid(EnumFacing side) {
		routerFaces[side.ordinal()].neighborUpdateGrid();
	}

	@Override
	public World getWorld2() {
		return world;
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}

	@Override
	public boolean isValid() {
		return !isInvalid();
	}

	@Override
	public void markDirty2() {
		markDirty();
	}

	@Override
	public void updateData(boolean load) {

	}

	@Override
	public BlockPos getSecurityStationPos() {
		return data.getSecurityStationPos();
	}

	@Override
	public int getChannelUsage() {
		int allChannels = Arrays.stream(routerFaces).filter(ChannelSource::isNormal).mapToInt(ChannelSource::getChannels).sum();
		return allChannels / 8 + (allChannels % 8 == 0 ? 0 : 1);
	}

	@Override
	public IRouter[] getRouters() {
		return routerFaces;
	}
}
