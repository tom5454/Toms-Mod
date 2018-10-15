package com.tom.api.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.multipart.IDuctModule;
import com.tom.api.multipart.PartDuct;
import com.tom.api.multipart.PartModule;
import com.tom.lib.api.grid.IGrid;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridUpdateListener;
import com.tom.lib.api.grid.IMultigridDevice;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.EnumCenterSlot;

public abstract class GridBase<D, T extends IGrid<D, T>> implements IGrid<D, T> {
	protected List<IGridDevice<T>> parts = new ArrayList<>();
	protected IGridDevice<T> master;

	@Override
	public void reloadGrid(World world, IGridDevice<T> master) {
		this.forceUpdateGrid(world, master);
		this.updateGrid(world, master);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void forceUpdateGrid(IBlockAccess world, IGridDevice<T> thisT) {
		// if(master == null) {
		// Profiler p = world instanceof World ? ((World)world).profiler : null;
		// if(p != null)p.startSection("forceUpdateGrid");
		invalidateAll();
		parts.clear();
		List<IGridDevice<T>> connectedStorages = new ArrayList<>();
		Stack<IGridDevice<T>> traversingStorages = new Stack<>();
		IGridDevice<T> masterOld = master;
		master = thisT;
		traversingStorages.add(thisT);
		// if(p != null)p.startSection("traversingStorages");
		while (!traversingStorages.isEmpty()) {
			IGridDevice<T> storage = traversingStorages.pop();
			if (storage != null && storage.isValid()) {
				if (storage.isMaster()) {
					master = storage;
				}
				connectedStorages.add(storage);
				if (storage instanceof PartDuct) {
					PartDuct<T> duct = (PartDuct<T>) storage;
					List<PartModule<T>> modules = duct.getModules();
					if (modules != null && !modules.isEmpty()) {
						for (int i = 0;i < modules.size();i++) {
							PartModule<T> m = modules.get(i);
							if (!connectedStorages.contains(m)) {
								connectedStorages.add(m);
							}
						}
					}
				} else if (storage instanceof PartModule) {
					PartDuct<T> baseDuct = ((PartModule<T>) storage).getBaseDuct();
					if (baseDuct != null && !connectedStorages.contains(baseDuct) && !traversingStorages.contains(baseDuct)) {
						traversingStorages.add(baseDuct);
					}
				}
				for (EnumFacing d : EnumFacing.VALUES) {
					if (storage.isConnected(d)) {
						final PartDuct<?> ductN = getDuct(world, storage.getPos2().offset(d), d);
						if (ductN != null) {
							if (ductN.isValidConnection(d.getOpposite())) {
								if (!connectedStorages.contains(ductN) && ductN.getGrid().getClass() == getClass()) {
									PartDuct<T> duct = (PartDuct<T>) ductN;
									traversingStorages.add(duct);
								}
							} else {
								PartModule<?> moduleInWay = ductN.getModule(d.getOpposite());
								if (moduleInWay != null && moduleInWay instanceof IMultigridDevice) {
									IMultigridDevice<?> multiModule = (IMultigridDevice<?>) moduleInWay;
									IGridDevice<?> other = multiModule.getOtherGridDevice();
									if (other != null && !connectedStorages.contains(other) && other.getGrid().getClass() == getClass()) {
										if (other.isValidConnection(multiModule.getSide())) {
											traversingStorages.add((IGridDevice<T>) other);
											if (other instanceof PartModule) {
												PartDuct<T> baseDuct = ((PartModule<T>) other).getBaseDuct();
												if (baseDuct != null && !connectedStorages.contains(baseDuct) && !traversingStorages.contains(baseDuct)) {
													traversingStorages.add(baseDuct);
												}
											}
										}
									}
								}
							}
						} else {
							TileEntity te = world.getTileEntity(storage.getPos2().offset(d));
							if (te instanceof IDuctModule<?>) {
								// Do nothing
							} else if (te instanceof IGridDevice && !connectedStorages.contains(te)) {
								try {
									IGridDevice<T> griddevice = (IGridDevice<T>) te;
									if (griddevice.isValidConnection(d.getOpposite()) && griddevice.getGrid().getClass() == getClass())
										traversingStorages.add((IGridDevice<T>) te);
								} catch (ClassCastException e) {
									// Do nothing
								}
							}
						}
					}
				}
			}
		}
		// if(p != null)p.endSection();
		if (masterOld != null && (!masterOld.isValid() || master.getPos2().equals(masterOld.getPos2()))) {
			NBTTagCompound tag = masterOld.getGridData();
			if (tag != null)
				setData(importFromNBT(tag).getData());
		}
		master.setGrid((T) this);
		master.getGrid().onForceUpdateDone();
		// if(p != null)p.startSection("updateListeners");
		List<IGridUpdateListener> listeners = new ArrayList<>();
		for (IGridDevice<T> storage : connectedStorages) {
			storage.setMaster(master, connectedStorages.size());
			if (storage instanceof IGridUpdateListener) {
				IGridUpdateListener l = ((IGridUpdateListener) storage);
				l.onGridReload();
				listeners.add(l);
			}
		}
		for (IGridUpdateListener l : listeners) {
			l.onGridPostReload();
		}
		// if(p != null)p.endSection();
		this.parts.addAll(connectedStorages);
		// if(p != null)p.endSection();
		// }
	}

	@Override
	public List<IGridDevice<T>> getParts() {
		return this.parts;
	}

	@Override
	public IGridDevice<T> getMaster() {
		return master;
	}

	@SuppressWarnings("unchecked")
	protected final PartDuct<?> getDuct(IBlockAccess world, BlockPos blockPos, EnumFacing side) {
		try {
			Optional<IMultipartContainer> container = MultipartHelper.getContainer(world, blockPos);
			if (!container.isPresent()) {
				TileEntity te = world.getTileEntity(blockPos);
				return te instanceof PartDuct<?> ? (PartDuct<T>) te : null;
			}
			Optional<IPartInfo> part = container.get().get(EnumCenterSlot.CENTER);
			if (part.isPresent() && part.get().getTile() instanceof PartDuct<?>) {
				return (PartDuct<T>) part.get().getTile();
			} else {
				return null;
			}
		} catch (ClassCastException e) {
			return null;
		}
	}

	public abstract void writeToNBT(NBTTagCompound tag);

	@Override
	public NBTTagCompound exportToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return tag;
	}

	@Override
	public void onForceUpdateDone() {

	}

	@Override
	public void setMaster(IGridDevice<T> master) {
		this.master = master;
	}

	@Override
	public void invalidate() {
	}

	@Override
	public void invalidateAll() {
		for (int i = 0;i < parts.size();i++)
			parts.get(i).invalidateGrid();
		parts.clear();
	}
}
