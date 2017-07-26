package com.tom.api.multipart;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.grid.IGrid;
import com.tom.api.grid.IGridDevice;
import com.tom.api.tileentity.ICable;
import com.tom.apis.Ticker;
import com.tom.apis.TomsModUtils;
import com.tom.handler.WorldHandler;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.util.MCMPWorldWrapper;

public abstract class PartDuct<G extends IGrid<?, G>> extends MultipartTomsMod implements ICable<G>, Ticker {
	public AxisAlignedBB[] BOXES;

	public static final AxisAlignedBB rotateFace(AxisAlignedBB box, EnumFacing facing) {
		switch (facing) {
		case DOWN:
		default:
			return box;
		case UP:
			return new AxisAlignedBB(box.minX, 1 - box.maxY, box.minZ, box.maxX, 1 - box.minY, box.maxZ);
		case NORTH:
			return new AxisAlignedBB(box.minX, box.minZ, box.minY, box.maxX, box.maxZ, box.maxY);
		case SOUTH:
			return new AxisAlignedBB(box.minX, box.minZ, 1 - box.maxY, box.maxX, box.maxZ, 1 - box.minY);
		case WEST:
			return new AxisAlignedBB(box.minY, box.minZ, box.minX, box.maxY, box.maxZ, box.maxX);
		case EAST:
			return new AxisAlignedBB(1 - box.maxY, box.minZ, box.minX, 1 - box.minY, box.maxZ, box.maxX);
		}
	}

	protected G grid;
	private boolean isMaster = false;
	private boolean secondTick = false;
	protected IGridDevice<G> master;
	protected double size = 0.1;
	private int suction = -1;

	protected void updateBox() {
		BOXES = new AxisAlignedBB[7];
		double start = 0.5 - size;
		double stop = 0.5 + size;
		BOXES[6] = new AxisAlignedBB(start, start, start, stop, stop, stop);
		for (int i = 0;i < 6;i++) {
			BOXES[i] = rotateFace(new AxisAlignedBB(start, 0, start, stop, start, stop), EnumFacing.getFront(i));
		}
	}

	private byte connectionCache = 0;
	private byte invConnectionCache = 0;
	private byte mConnectionCache = 0;
	private byte e1ConnectionCache = 0, e2ConnectionCache = 0;
	private NBTTagCompound last;
	private final String type;

	public PartDuct(String type, double size) {
		this.type = type;
		this.size = size;
		updateBox();
		grid = constructGrid();
	}

	public String createStringFromCache() {
		return connectionCache + "," + invConnectionCache + "," + mConnectionCache + "," + e1ConnectionCache + "," + e2ConnectionCache;
	}

	@Override
	public final void readFromPacket(NBTTagCompound buf) {
		double sizeOld = size;
		byte oldCC = connectionCache;
		byte oldICC = invConnectionCache;
		byte oldMCC = mConnectionCache;
		byte oe1 = e1ConnectionCache;
		byte oe2 = e2ConnectionCache;
		connectionCache = buf.getByte("cc");
		invConnectionCache = buf.getByte("icc");
		mConnectionCache = buf.getByte("mcc");
		e1ConnectionCache = buf.getByte("e1cc");
		e2ConnectionCache = buf.getByte("e2cc");
		boolean update = this.readFromPacketI(buf.getCompoundTag("tag"));
		if (sizeOld != size) {
			updateBox();
		}
		if (update || oldCC != connectionCache || oldICC != invConnectionCache || oldMCC != mConnectionCache || e1ConnectionCache != oe1 || e2ConnectionCache != oe2) {
			markRenderUpdate();
		}
	}

	@Override
	public final void markRenderUpdate() {
		onMarkRenderUpdate();
		super.markRenderUpdate();
	}

	protected void onMarkRenderUpdate() {
	}

	@Override
	public final void writeToPacket(NBTTagCompound buf) {
		buf.setByte("cc", connectionCache);
		buf.setByte("icc", invConnectionCache);
		buf.setByte("mcc", mConnectionCache);
		buf.setByte("e1cc", e1ConnectionCache);
		buf.setByte("e2cc", e2ConnectionCache);
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToPacketI(tag);
		buf.setTag("tag", tag);
	}

	public final boolean connects(EnumFacing side) {
		return (connectionCache & (1 << side.ordinal())) != 0;
	}

	public final boolean connectsInv(EnumFacing side) {
		return (invConnectionCache & (1 << side.ordinal())) != 0;
	}

	public final boolean connectsM(EnumFacing side) {
		return (mConnectionCache & (1 << side.ordinal())) != 0;
	}

	public final boolean connectsE1(EnumFacing side) {
		return (e1ConnectionCache & (1 << side.ordinal())) != 0;
	}

	public final boolean connectsE2(EnumFacing side) {
		return (e2ConnectionCache & (1 << side.ordinal())) != 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		connectionCache = nbt.getByte("cc");
		isMaster = nbt.getBoolean(MASTER_NBT_NAME);
		if (this.isMaster)
			grid.importFromNBT(nbt.getCompoundTag(GRID_TAG_NAME));
		invConnectionCache = nbt.getByte("icc");
		mConnectionCache = nbt.getByte("mcc");
		updateBox();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("cc", connectionCache);
		if (this.isMaster)
			nbt.setTag(GRID_TAG_NAME, grid.exportToNBT());
		nbt.setBoolean(MASTER_NBT_NAME, isMaster);
		nbt.setByte("icc", invConnectionCache);
		nbt.setByte("mcc", mConnectionCache);
		nbt.setByte("e1c", e1ConnectionCache);
		nbt.setByte("e2c", e2ConnectionCache);
		return nbt;
	}

	private void updateConnections(EnumFacing side) {
		if (side != null) {
			connectionCache &= ~(1 << side.ordinal());
			invConnectionCache &= ~(1 << side.ordinal());
			mConnectionCache &= ~(1 << side.ordinal());
			e1ConnectionCache &= ~(1 << side.ordinal());
			e2ConnectionCache &= ~(1 << side.ordinal());
			byte connectionType = internalConnects(side);
			if (connectionType > 0) {
				if (connectionType != 3) {
					PartDuct<G> pipe = getDuct(getPos2().offset(side), side.getOpposite());
					if (pipe != null) {
						byte otherCT = pipe.internalConnects(side.getOpposite());
						if (otherCT != 1 && otherCT != 3 && otherCT != 4 && otherCT != 5)
							return;
					}
				}
				if (connectionType == 1)
					connectionCache |= 1 << side.ordinal();
				else if (connectionType == 2)
					invConnectionCache |= 1 << side.ordinal();
				else if (connectionType == 3)
					mConnectionCache |= 1 << side.ordinal();
				else if (connectionType == 4)
					e1ConnectionCache |= 1 << side.ordinal();
				else if (connectionType == 5)
					e2ConnectionCache |= 1 << side.ordinal();
			}
		} else {
			for (EnumFacing facing : EnumFacing.VALUES) {
				updateConnections(facing);
			}
		}
	}

	private void updateNeighborInfo(boolean sendPacket) {
		if (!getWorld2().isRemote) {
			byte oc = connectionCache;
			byte oic = invConnectionCache;
			byte omc = mConnectionCache;
			byte oe1 = e1ConnectionCache;
			byte oe2 = e2ConnectionCache;
			world.profiler.startSection("updateConnections");
			for (EnumFacing dir : EnumFacing.VALUES) {
				updateConnections(dir);
			}
			world.profiler.endSection();
			world.profiler.startSection("forceUpdateGrid");
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
			world.profiler.endSection();
			if (sendPacket && (connectionCache != oc || invConnectionCache != oic || mConnectionCache != omc || e1ConnectionCache != oe1 || e2ConnectionCache != oe2)) {
				sendUpdatePacket();
			}
		}
	}

	private byte internalConnects(EnumFacing side) {
		PartModule<G> m = this.getModule(side);
		Optional<IMultipartContainer> container = MultipartHelper.getContainer(getWorld2(), pos);
		if (m != null && this.isModuleValid(side, m)) { return 3; }
		if (TomsModUtils.getModule(getWorld2(), getPos2(), side) != null) { return 0; }
		byte check = checkDuct(getPos2().offset(side), side.getOpposite());
		if (check > 0) { return check; }
		if (container.isPresent() && !TomsModUtils.occlusionTest(container.get(), this, BOXES[side.ordinal()])) { return 0; }
		if (container.isPresent() && this instanceof ICustomPartBounds) {
			if (!TomsModUtils.occlusionTest(container.get(), this, rotateFace(((ICustomPartBounds) this).getBoxForConnect(), side)))
				return 0;
		}
		TileEntity tile = getNeighbourTile(side);
		check = (byte) (tile != null && !(tile instanceof PartDuct || tile instanceof IModule) ? isValidConnectionA(side, tile) : 0);
		return check;
	}

	public abstract boolean isValidConnection(EnumFacing side, TileEntity tile);

	public int isValidConnectionA(EnumFacing side, TileEntity tile) {
		return isValidConnection(side, tile) ? 2 : 0;
	}

	@SuppressWarnings("unchecked")
	public final PartDuct<G> getDuct(BlockPos blockPos, EnumFacing side) {
		try {
			Optional<IMultipartContainer> container = MultipartHelper.getContainer(getWorld2(), blockPos);
			if (!container.isPresent()) {
				TileEntity te = world.getTileEntity(blockPos);
				return te instanceof PartDuct<?> ? (PartDuct<G>) te : null;
			}
			Optional<IPartInfo> part = container.get().get(EnumCenterSlot.CENTER);
			if (part.isPresent() && part.get().getTile() instanceof PartDuct<?> && TomsModUtils.occlusionTest(container.get(), this, ((PartDuct<G>) part.get().getTile()).BOXES[side.getOpposite().ordinal()]) && canConnect((PartDuct<?>) part.get().getTile(), side) != 0) {
				return (PartDuct<G>) part.get().getTile();
			} else {
				return null;
			}
		} catch (ClassCastException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public final byte checkDuct(BlockPos blockPos, EnumFacing side) {
		Optional<IMultipartContainer> container = MultipartHelper.getContainer(getWorld2(), blockPos);
		if (!container.isPresent()) {
			TileEntity te = world.getTileEntity(blockPos);
			return te instanceof PartDuct<?> ? canConnect((PartDuct<?>) te, side) : 0;
		}

		Optional<IPartInfo> part = container.get().get(EnumCenterSlot.CENTER);
		try {
			if (part.isPresent() && part.get().getTile() instanceof PartDuct<?>) {
				if (TomsModUtils.occlusionTest(container.get(), this, ((PartDuct<G>) part.get().getTile()).BOXES[side.ordinal()])) {
					byte c = canConnect((PartDuct<?>) part.get().getTile(), side);
					if (c != 0) {
						return c;
					} else
						return 0;
				} else
					return -1;
			} else {
				return 0;
			}
		} catch (ClassCastException e) {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	protected byte canConnect(PartDuct<?> part, EnumFacing side) {
		try {
			if (part instanceof PartDuct<?> && type.equals(((PartDuct<G>) part).type)) {
				return 1;
			} else {
				return 0;
			}
		} catch (ClassCastException e) {
			return 0;
		}
	}

	public TileEntity getNeighbourTile(EnumFacing side) {
		return side != null ? getWorld2().getTileEntity(getPos2().offset(side)) : null;
	}

	protected void scheduleRenderUpdate() {
		getWorld2().markBlockRangeForRenderUpdate(getPos2(), getPos2());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onNeighborTileChange(boolean force) {
		if (force) {
			WorldHandler.queueTask(world.provider.getDimension(), () -> {
				grid.invalidateAll();
				WorldHandler.queueTask(world.provider.getDimension(), () -> {
					WorldHandler.queueTask(world.provider.getDimension(), () -> {
						for (EnumFacing f : EnumFacing.VALUES) {
							BlockPos blockPos = pos.offset(f);
							Optional<IMultipartContainer> container = MultipartHelper.getContainer(getWorld2(), blockPos);
							if (!container.isPresent()) {
								TileEntity te = world.getTileEntity(blockPos);
								if (te instanceof IGridDevice<?>) {
									IGridDevice<?> d = (IGridDevice<?>) te;
									if (d.getGrid().getClass() == grid.getClass()) {
										d.invalidateGrid();
									}
								}
								continue;
							}

							Optional<IPartInfo> part = container.get().get(EnumCenterSlot.CENTER);
							try {
								if (part.isPresent() && part.get().getTile() instanceof PartDuct<?> && ((PartDuct) part.get().getTile()).getGrid().getClass() == grid.getClass()) {
									((PartDuct) part.get().getTile()).invalidateGrid();
								}
							} catch (ClassCastException e) {
							}
						}
					});
				});
			});
		} else
			updateNeighborInfo(true);
	}

	@Override
	public void onLoad() {
		WorldHandler.queueTask(world.provider.getDimension(), () -> {
			if (this.isMaster) {
				grid.setMaster(this);
				grid.forceUpdateGrid(world, this);
			}
			this.sendUpdatePacket();
			secondTick = true;
			WorldHandler.queueTask(world.provider.getDimension(), () -> {
				if (master == null) {
					grid.reloadGrid(world, this);
				}
				updateNeighborInfo(true);
				this.sendUpdatePacket();
				this.markDirty();
				secondTick = false;
			});
		});
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
		if (isMaster) {
			WorldHandler.queueTask(world.provider.getDimension(), () -> {
				if (isMaster && !world.isRemote && useServerTickHandler())
					WorldHandler.addTicker(world.provider.getDimension(), this);
			});
		}
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
		return this.connects(side) || this.connectsInv(side) || connectsE1(side) || connectsE2(side);
	}

	public boolean readFromPacketI(NBTTagCompound tag) {
		return false;
	}

	public void writeToPacketI(NBTTagCompound tag) {

	}

	protected void updateFirst() {

	}

	@Override
	public final boolean isValidConnection(EnumFacing side) {
		return TomsModUtils.occlusionTest(MultipartHelper.getContainer(getWorld2(), pos).orElse(null), this, BOXES[side.ordinal()]);
		// return false;
	}

	@Override
	public void invalidateGrid() {
		this.master = null;
		WorldHandler.queueTask(world.provider.getDimension(), () -> {
			if (this.master == null && !secondTick)
				WorldHandler.queueTask(world.provider.getDimension(), () -> {
					if (this.master == null && !secondTick)
						this.constructGrid().forceUpdateGrid(world, this);
				});
		});
		this.isMaster = false;
		last = grid.exportToNBT();
		this.grid.invalidate();
		this.grid = this.constructGrid();
	}

	public abstract G constructGrid();

	public final List<PartModule<G>> getModules() {
		List<PartModule<G>> modules = new ArrayList<>();
		for (EnumFacing f : EnumFacing.VALUES) {
			PartModule<G> p = this.getModule(f);
			if (p != null) {
				modules.add(p);
			}
		}
		return modules;
	}

	@SuppressWarnings("unchecked")
	public final PartModule<G> getModule(EnumFacing pos) {
		try {
			PartModule<G> m = (PartModule<G>) TomsModUtils.getModule(getWorld2(), getPos2(), pos);
			if (m != null && m.grid.getClass() == this.grid.getClass()) { return m; }
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public void addExtraDrops(List<ItemStack> list) {
	}

	public abstract int getPropertyValue(EnumFacing side);

	// return this.connectsM(side) ? 3 : (this.connectsInv(side) ? 2 :
	// (this.connects(side) ? 1 : 0));
	public double getSize() {
		return size;
	}

	/*@Override
	public final boolean onActivated(EntityPlayer player, EnumHand hand,
			ItemStack stack, PartMOP hit) {
		if(!player.worldObj.isRemote && player.isSneaking() && CoreInit.isWrench(stack, player)){
			for (ItemStack stacks : getDrops()) {
				EntityItem item = new EntityItem(player.worldObj, pos.getX(), pos.getY(), pos.getZ(), stacks);
				item.setDefaultPickupDelay();
				player.worldObj.spawnEntityInWorld(item);
			}
			getContainer().removePart(this);
			return true;
		}
		Vec3d hitSub = hit.hitVec.subtract(new Vec3d(getPos2()));
		Vec3d hitPos = new Vec3d(hitSub.xCoord > 0.5 ? hitSub.xCoord-0.1 : hitSub.xCoord+0.1,
				hitSub.yCoord > 0.5 ? hitSub.yCoord-0.1 : hitSub.yCoord+0.1,
						hitSub.zCoord > 0.5 ? hitSub.zCoord-0.1 : hitSub.zCoord+0.1);
		List<AxisAlignedBB> boxList = new ArrayList<>();
		this.addSelectionBoxes(boxList);
		AxisAlignedBB currentBox = null;
		for(AxisAlignedBB b : boxList){
			if(b != null && b.isVecInside(hitPos)){
				currentBox = b;
				break;
			}
		}
		if(currentBox != null){
			EnumFacing side = null;
			for(int i = 0;i<6;i++){
				EnumFacing f = EnumFacing.getFront(i);
				if(connectsInv(f) && this instanceof ICustomPartBounds){
					if(rotateFace(((ICustomPartBounds)this).getBoxForConnect(), f).equals(currentBox)){
						side = f;
					}else if(BOXES[i].equals(currentBox)){
						side = f;
					}
				}
			}
			if(side != null){
				if(!onConnectionBoxClicked(side, player, stack, hand)){
					if(stack != null && stack.getItem() instanceof ModuleItem){
						if(!getWorld2().isRemote){
							PartModule<?> m = ((ModuleItem)stack.getItem()).createPart(side);
							if(TomsModUtils.occlusionTest(this, m.BOX)){
								if(this.getContainer().canAddPart(m)){
									this.getContainer().addPart(m);
									stack.splitStack(1);
									player.inventoryContainer.detectAndSendChanges();
									markDirty();
									return true;
								}
							}
						}else return true;
					}else return false;
				}else return true;
			}
			return false;
		}
		return false;
	}*/
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
		this.grid.invalidate();
		this.grid = newGrid;
	}

	public boolean isModuleValid(EnumFacing pos, PartModule<G> module) {
		return true;
	}

	public AxisAlignedBB getBoxForSide(EnumFacing side) {
		return BOXES[side.ordinal()];
	}

	public boolean onConnectionBoxClicked(EnumFacing dir, EntityPlayer player, ItemStack stack, EnumHand hand) {
		return false;
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

	public void markForUpdate() {
		sendUpdatePacket();
	}

	@Override
	public boolean isValid() {
		return getWorld() != null && getPos() != null;
	}

	@Override
	public NBTTagCompound getGridData() {
		return last;
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
	public void updateTicker() {
		if (this.isMaster) {
			grid.updateGrid(getWorld2(), this);
		}
	}

	@Override
	public boolean isTickerValid() {
		return isMaster && !world.isRemote && useServerTickHandler();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		WorldHandler.removeTicker(world.provider.getDimension(), this);
	}

	@Override
	public void validate() {
		super.validate();
		WorldHandler.queueTask(world.provider.getDimension(), () -> {
			if (isMaster && !world.isRemote && useServerTickHandler())
				WorldHandler.addTicker(world.provider.getDimension(), this);
		});
	}

	protected boolean useServerTickHandler() {
		return !(this instanceof ITickable);
	}

	@Override
	public void onPartLoad() {
		super.onPartLoad();
	}
}
