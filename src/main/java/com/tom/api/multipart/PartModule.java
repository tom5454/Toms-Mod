package com.tom.api.multipart;

import java.util.List;
import java.util.Optional;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.tileentity.ITMTickable;
import com.tom.lib.api.grid.IGrid;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.handler.WorldHandler;
import com.tom.util.TomsModUtils;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.util.MCMPWorldWrapper;

public abstract class PartModule<G extends IGrid<?, G>> extends MultipartTomsMod implements IDuctModule<G>, ITMTickable {
	protected G grid;
	protected static final String GRID_TAG_NAME = "grid";
	private static final String MASTER_NBT_NAME = "isMaster";
	private NBTTagCompound last;
	private boolean isMaster = false;
	private boolean secondTick = false;
	protected IGridDevice<G> master;
	private int suction = -1;
	private int state, lastState;

	public PartModule() {
		grid = constructGrid();
	}

	@Override
	public void updateEntity() {
		// world.profiler.startSection("part:"+this.getClass());
		if (!this.world.isRemote) {
			state = getState();
			if (lastState != state) {
				sendUpdatePacket();
			}
			if (this.isMaster) {
				grid.updateGrid(getWorld2(), this);
			}
		}
		this.updateEntityI();
		// world.profiler.endSection();
	}

	@Override
	public void sendUpdatePacket() {
		lastState = state = getState();
		super.sendUpdatePacket();
	}

	public TileEntity getNeighbourTile(EnumFacing side) {
		return side != null ? getWorld2().getTileEntity(getPos2().offset(side)) : null;
	}

	@Override
	public final void writeToPacket(NBTTagCompound tag) {
		tag.setInteger("s", state);
		NBTTagCompound t = new NBTTagCompound();
		writeToPacketI(t);
		tag.setTag("tag", t);
	}

	@Override
	public final void readFromPacket(NBTTagCompound tag) {
		int stateOld = state;
		state = tag.getInteger("s");
		boolean update = readFromPacketI(tag.getCompoundTag("tag"));
		if (stateOld != state || update) {
			markRenderUpdate();
		}
	}

	public void writeToPacketI(NBTTagCompound tag) {
	}

	public boolean readFromPacketI(NBTTagCompound tag) {
		return false;
	}

	protected void scheduleRenderUpdate() {
		getWorld2().markBlockRangeForRenderUpdate(getPos2(), getPos2());
	}

	@Override
	public void onNeighborTileChange(boolean force) {
		if (force)
			WorldHandler.queueTask(world.provider.getDimension(), grid::invalidateAll);
		else
			updateNeighborInfo(true);
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
	public boolean isConnected(EnumFacing side) {
		return false;
	}

	protected void updateFirst() {

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

	private void updateNeighborInfo(boolean sendPacket) {
		if (!getWorld2().isRemote) {
			// byte oc = connectionCache;

			// for (EnumFacing dir : EnumFacing.VALUES) {
			// updateConnections(dir);
			// }
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
			// if (sendPacket && connectionCache != oc) {
			// sendUpdatePacket();
			// }
		}
	}

	public abstract void updateEntityI();

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		grid.importFromNBT(nbt.getCompoundTag(GRID_TAG_NAME));
		isMaster = nbt.getBoolean(MASTER_NBT_NAME);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setTag(GRID_TAG_NAME, grid.exportToNBT());
		nbt.setBoolean(MASTER_NBT_NAME, isMaster);
		return nbt;
	}

	@Override
	public EnumFacing getFacing() {
		return getFacing0();
	}

	protected EnumFacing getFacing0() {
		IMultipartContainer c = MultipartHelper.getContainer(world, pos).orElse(null);
		if (c == null) {
			IBlockState state = world.getBlockState(pos);
			return state.getBlock() != Blocks.AIR ? state.getValue(BlockModuleBase.FACING) : EnumFacing.DOWN;
		}
		IPartInfo i = TomsModUtils.getPartInfo(c, this);
		return i == null ? EnumFacing.DOWN : i.getState().getValue(BlockModuleBase.FACING);
	}

	@Override
	public boolean isValidConnection(EnumFacing side) {
		return true;
	}

	@SuppressWarnings("unchecked")
	public PartDuct<G> getBaseDuct() {
		Optional<IMultipartContainer> c = MultipartHelper.getContainer(world, pos);
		if (c.isPresent()) {
			try {
				IPartInfo part = c.get().get(EnumCenterSlot.CENTER).orElse(null);
				if (part != null && part.getTile() instanceof PartDuct<?> && ((PartDuct<G>) part.getTile()).grid.getClass() == grid.getClass()) {
					return (PartDuct<G>) part.getTile();
				} else {
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
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
		updateNeighborInfo(true);
	}

	@Override
	public void setGrid(G newGrid) {
		grid.invalidate();
		this.grid = newGrid;
	}

	public abstract int getState();

	public void addExtraDrops(List<ItemStack> list) {
	}

	@Override
	public BlockPos getPos2() {
		return getPos();
	}

	@Override
	public World getWorld2() {
		World world = getWorld();
		return world instanceof MCMPWorldWrapper ? ((MCMPWorldWrapper) world).getActualWorld() : world;
	}

	@Override
	public boolean isValid() {
		return getWorld() != null && getPos() != null;
	}

	@Override
	public NBTTagCompound getGridData() {
		return last;
	}

	public int getStateClient() {
		return state;
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
				updateNeighborInfo(true);
				this.markBlockForUpdate();
				this.markDirty();
				secondTick = false;
			});
		});
	}

	@Override
	public void setWorld(World worldIn) {
		super.setWorld(worldIn);
		this.world = this.getWorld2();
		this.pos = this.getPos2();
	}

	@Override
	public void setPartInfo(IPartInfo info) {
		super.setPartInfo(info);
		this.world = this.getWorld2();
		this.pos = this.getPos2();
	}
	@Override
	public boolean canHaveDefaultGridHandler() {
		return false;
	}
}
