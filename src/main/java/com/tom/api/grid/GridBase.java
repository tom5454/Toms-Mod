package com.tom.api.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.multipart.PartDuct;
import com.tom.api.multipart.PartModule;

import mcmultipart.microblock.IMicroblock;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;

public abstract class GridBase<D, T extends IGrid<D,T>> implements IGrid<D, T> {
	protected List<IGridDevice<T>> parts = new ArrayList<IGridDevice<T>>();
	protected IGridDevice<T> master;
	@Override
	public void reloadGrid(World world, IGridDevice<T> master) {
		this.forceUpdateGrid(world, master);
		this.updateGrid(world, master);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void forceUpdateGrid(IBlockAccess world, IGridDevice<T> thisT) {
		//if(master == null) {
		for(int i = 0;i<parts.size();i++)parts.get(i).invalidateGrid();
		parts.clear();
		List<IGridDevice<T>> connectedStorages = new ArrayList<IGridDevice<T>>();
		Stack<IGridDevice<T>> traversingStorages = new Stack<IGridDevice<T>>();
		IGridDevice<T> masterOld = master;
		master = thisT;
		traversingStorages.add(thisT);
		while(!traversingStorages.isEmpty()) {
			IGridDevice<T> storage = traversingStorages.pop();
			if(storage.isMaster()) {
				master = storage;
			}
			connectedStorages.add(storage);
			if(storage instanceof PartDuct){
				PartDuct<T> duct = (PartDuct<T>) storage;
				List<PartModule<T>> modules = duct.getModules();
				if(modules != null && !modules.isEmpty()){
					connectedStorages.addAll(modules);
				}
			}else if(storage instanceof PartModule){
				PartDuct<T> baseDuct = ((PartModule<T>)storage).getBaseDuct();
				if(baseDuct != null && !connectedStorages.contains(baseDuct) && !traversingStorages.contains(baseDuct)){
					traversingStorages.add(baseDuct);
				}
			}
			for(EnumFacing d : EnumFacing.VALUES) {
				if(storage.isConnected(d)){
					TileEntity te = world.getTileEntity(storage.getPos2().offset(d));
					if(te instanceof IGridDevice && !connectedStorages.contains(te)) {
						try {
							IGridDevice<T> griddevice = (IGridDevice<T>) te;
							if(griddevice.isValidConnection(d.getOpposite()) && griddevice.getGrid().getClass() == getClass())
								traversingStorages.add((IGridDevice<T>)te);
						} catch (ClassCastException e) {
							//Do nothing
						}
					}else{
						final PartDuct<?> ductN = getDuct(world, storage.getPos2().offset(d), d);
						if(ductN != null){
							if(ductN.isValidConnection(d.getOpposite())){
								if(!connectedStorages.contains(ductN) && ductN.getGrid().getClass() == getClass()){
									PartDuct<T> duct = (PartDuct<T>) ductN;
									traversingStorages.add(duct);
									List<PartModule<T>> modules = duct.getModules();
									if(modules != null && !modules.isEmpty()){
										connectedStorages.addAll(modules);
									}
								}
							}else{
								PartModule<?> moduleInWay = ductN.getModule(d.getOpposite());
								if(moduleInWay != null && moduleInWay instanceof IMultigridDevice){
									IMultigridDevice<?> multiModule = (IMultigridDevice<?>) moduleInWay;
									IGridDevice<?> other =  multiModule.getOtherGridDevice();
									if(other != null && !connectedStorages.contains(other) && other.getGrid().getClass() == getClass()){
										if(other.isValidConnection(multiModule.getSide())){
											traversingStorages.add((IGridDevice<T>) other);
											if(other instanceof PartDuct){
												PartDuct<T> duct = (PartDuct<T>) other;
												List<PartModule<T>> modules = duct.getModules();
												if(modules != null && !modules.isEmpty()){
													connectedStorages.addAll(modules);
												}
											}else if(other instanceof PartModule){
												PartDuct<T> baseDuct = ((PartModule<T>)other).getBaseDuct();
												if(baseDuct != null && !connectedStorages.contains(baseDuct) && !traversingStorages.contains(baseDuct)){
													traversingStorages.add(baseDuct);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if(masterOld != null && master.getPos2().equals(masterOld.getPos2()))
			setData(master.getGrid().getData());
		master.setGrid((T) this);
		master.getGrid().onForceUpdateDone();
		List<IGridUpdateListener> listeners = new ArrayList<IGridUpdateListener>();
		for(IGridDevice<T> storage : connectedStorages) {
			storage.setMaster(master, connectedStorages.size());
			if(storage instanceof IGridUpdateListener){
				IGridUpdateListener l = ((IGridUpdateListener) storage);
				l.onGridReload();
				listeners.add(l);
			}
		}
		for(IGridUpdateListener l : listeners){
			l.onGridPostReload();
		}
		this.parts.addAll(connectedStorages);
		//}
	}
	@Override
	public List<IGridDevice<T>> getParts() {
		return this.parts;
	}

	@Override
	public IGridDevice<T> getMaster() {
		return master;
	}
	protected final PartDuct<?> getDuct(IBlockAccess world, BlockPos blockPos, EnumFacing side) {
		IMultipartContainer container = MultipartHelper.getPartContainer(world, blockPos);
		if (container == null) {
			return null;
		}

		if (side != null) {
			ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(side));
			if (part instanceof IMicroblock.IFaceMicroblock && !((IMicroblock.IFaceMicroblock) part).isFaceHollow()) {
				return null;
			}
		}

		ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
		if (part instanceof PartDuct<?>) {
			return (PartDuct<?>) part;
		} else {
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
	public void onForceUpdateDone(){

	}
	@Override
	public void setMaster(IGridDevice<T> master) {
		this.master = master;
	}
}
