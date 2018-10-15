package com.tom.api.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.lib.api.grid.IGrid;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.handler.WorldHandler;

public abstract class TileEntityGridDeviceBase<G extends IGrid<?, G>> extends TileEntityTomsMod implements IGridDevice<G> {
	protected G grid;
	protected IGridDevice<G> master;
	protected static final String GRID_TAG_NAME = "grid";
	private static final String MASTER_NBT_NAME = "isMaster";
	private boolean secondTick = false;
	private boolean isMaster = false;
	private int suction = -1;
	private NBTTagCompound last;

	public TileEntityGridDeviceBase() {
		grid = this.constructGrid();
	}

	@Override
	public boolean isMaster() {
		return isMaster;
	}

	@Override
	public void setMaster(IGridDevice<G> master, int size) {
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
	public G getGrid() {
		return grid;
	}

	@Override
	public IGridDevice<G> getMaster() {
		grid.forceUpdateGrid(getWorld2(), this);
		return master;
	}

	@Override
	public void invalidateGrid() {
		this.master = null;
		this.isMaster = false;
		WorldHandler.queueTask(world.provider.getDimension(), () -> {
			if (this.master == null && !secondTick)
				WorldHandler.queueTask(world.provider.getDimension(), () -> {
					if (this.master == null && !secondTick)
						this.constructGrid().forceUpdateGrid(world, this);
				});
		});
		last = grid.exportToNBT();
		grid.invalidate();
		this.grid = this.constructGrid();
	}

	public abstract G constructGrid();

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
		updateGrid();
	}

	@Override
	public void setGrid(G newGrid) {
		grid.invalidate();
		this.grid = newGrid;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag(GRID_TAG_NAME, grid.exportToNBT());
		compound.setBoolean(MASTER_NBT_NAME, isMaster);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		grid.importFromNBT(compound.getCompoundTag(GRID_TAG_NAME));
		this.isMaster = compound.getBoolean(MASTER_NBT_NAME);
	}

	@Override
	public final boolean isConnected(EnumFacing side) {
		return canConnectTo(side);
	}

	@Override
	public final boolean isValidConnection(EnumFacing side) {
		return canConnectTo(side);
	}

	@Override
	public void preUpdate(IBlockState state) {
		// world.profiler.startSection(pos.toString() + ":" +
		// world.provider.getDimension());
		// world.profiler.startSection("updateNeighborInfo");
		// world.profiler.endSection();
		if (!this.world.isRemote) {
			if (this.isMaster) {
				grid.updateGrid(getWorld2(), this);
			}
		}
		// world.profiler.startSection("updateI");
		// world.profiler.endSection();
		// world.profiler.endSection();
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}

	@Override
	public World getWorld2() {
		return world;
	}

	public void neighborUpdateGrid(boolean force) {
		if (force) {
			WorldHandler.queueTask(world.provider.getDimension(), grid::invalidateAll);
		} else
			updateGrid();
	}

	private void updateGrid() {
		if (master != null && master != this && master.isValid())
			master.updateState();
		else {
			if (master == null) {
				grid.invalidateAll();
				G grid = this.constructGrid();
				grid.setMaster(master);
				grid.forceUpdateGrid(this.getWorld2(), this);
			} else {
				grid.forceUpdateGrid(this.getWorld2(), this);
			}
		}
	}

	@Override
	public boolean isValid() {
		return !isInvalid();
	}

	@Override
	public NBTTagCompound getGridData() {
		return last;
	}

	@Override
	public void onLoad() {
		WorldHandler.queueTask(world.provider.getDimension(), () -> {
			if (this.isMaster) {
				grid.setMaster(this);
				grid.forceUpdateGrid(world, this);
			}
			this.markBlockForUpdate();
			secondTick = true;
			WorldHandler.queueTask(world.provider.getDimension(), () -> {
				if (master == null) {
					grid.reloadGrid(world, this);
				}
				this.markBlockForUpdate();
				this.markDirty();
				secondTick = false;
			});
		});
	}
	public boolean canConnectTo(EnumFacing f){
		return true;
	}
}
