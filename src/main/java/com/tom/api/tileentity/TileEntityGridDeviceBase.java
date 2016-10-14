package com.tom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.grid.IGrid;
import com.tom.api.grid.IGridDevice;

public abstract class TileEntityGridDeviceBase<G extends IGrid<?,G>> extends TileEntityTomsMod implements
IGridDevice<G> {
	protected G grid;
	protected IGridDevice<G> master;
	protected static final String GRID_TAG_NAME = "grid";
	private static final String MASTER_NBT_NAME = "isMaster";
	private boolean firstStart = true;
	private boolean secondTick = false;
	private boolean isMaster = false;
	private int suction = -1;
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
		//boolean wasMaster = isMaster;
		isMaster = master == this;
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
	public void invalidateGrid(){
		this.master = null;
		this.isMaster = false;
		this.grid = this.constructGrid();
	}
	public abstract G constructGrid();

	@Override
	public void setSuctionValue(int suction){
		this.suction = suction;
	}
	@Override
	public int getSuctionValue(){
		return this.suction;
	}

	@Override
	public void updateState() {

	}

	@Override
	public void setGrid(G newGrid) {
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
	public boolean isConnected(EnumFacing side) {
		return true;
	}
	@Override
	public boolean isValidConnection(EnumFacing side) {
		return true;
	}
	@Override
	public void preUpdate() {
		if(!this.worldObj.isRemote){
			if(firstStart){
				this.firstStart = false;
				this.secondTick = true;
				if(this.isMaster){
					grid.forceUpdateGrid(worldObj, this);
				}
			}
			if(secondTick){
				this.secondTick = false;
				if(master == null){
					grid.reloadGrid(worldObj, this);
				}
				this.markDirty();
			}
		}
		if(this.isMaster){
			grid.updateGrid(worldObj, this);
		}
		if(this.master == null){
			this.constructGrid().forceUpdateGrid(worldObj, this);
		}
	}
	@Override
	public BlockPos getPos2() {
		return pos;
	}
	@Override
	public World getWorld2() {
		return worldObj;
	}
}
