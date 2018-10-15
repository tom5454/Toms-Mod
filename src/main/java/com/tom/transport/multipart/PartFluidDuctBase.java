package com.tom.transport.multipart;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.multipart.ICustomPartBounds;
import com.tom.api.multipart.PartDuct;
import com.tom.lib.api.grid.IGridUpdateListener;

public class PartFluidDuctBase extends PartDuct<FluidGrid> implements ICustomPartBounds, ITileFluidHandler, IGridUpdateListener {
	private final AxisAlignedBB connectionBox;
	// private byte eConnectionCache = 0;
	// private int timer = 0;
	public FluidStack stack/*, stackOld*/;
	//public FluidTank tank = new FluidTank(500);
	public Integer render;
	private List<IFluidHandler> rec = new ArrayList<>();

	public PartFluidDuctBase() {
		super("tomsmodtransport:tm.fluidDuct", 0.1875);
		double start = 0.5 - 0.25;
		double stop = 0.5 + 0.25;
		this.connectionBox = new AxisAlignedBB(start, 0, start, stop, start, stop);
	}

	@Override
	public AxisAlignedBB getBoxForConnect() {
		return connectionBox;
	}

	@Override
	public boolean isValidConnection(EnumFacing side, TileEntity tile) {
		return tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
	}

	/*@Override
	public void updateEntity() {
		if(!world.isRemote){
			timer++;
			if(timer > Configs.updateRate){
				timer = 0;
				sendUpdatePacket();
			}
			updateTicker();
			for(EnumFacing f : EnumFacing.VALUES){
				/*if(connectsE(f)){
					if(grid.getData().getFluid() == null || grid.getData().getCapacity() >= grid.getData().getFluidAmount()){
						TileEntity tile = worldObj.getTileEntity(pos.offset(f));
						if(tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f)){
							IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
							if(t != null){
								FluidStack drained = t.drain(Configs.fluidDuctMaxExtract, false);
								if(drained != null && drained.amount > 0){
									//if(t.canDrain(f, t.drain(1, false).getFluid())){
									int filled = grid.getData().fill(drained, false);
									if(filled > 0){
										int canDrain = Math.min(filled, Math.min(Configs.fluidDuctMaxExtract, drained.amount));
										grid.getData().fill(t.drain(canDrain, true), true);
										/*for(int i = 0;i<grid.getParts().size();i++){
										((PartFluidDuct) grid.getParts().get(i)).sendUpdatePacket();
									}*/
	// grid.markForUpdate();
	// }
	/*}
								}
							}
						}
					}
				}else*/ /*if(connectsInv(f)){
						if(grid.getData().getFluid() != null && grid.getData().getFluidAmount() > 0){
						TileEntity tile = world.getTileEntity(pos.offset(f));
						if(tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite())){
							IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite());
							//if(t.canFill(f, grid.getData().getFluid().getFluid())){
							if(t != null){
								int filled = t.fill(grid.getData().getFluid(), false);
								if(filled > 0){
									FluidStack drained = grid.getData().drain(filled, false);
									if(drained != null && drained.amount > 0){
										int canDrain = Math.min(filled, Math.min(Configs.fluidDuctMaxInsert, drained.amount));
										t.fill(grid.getData().drain(canDrain, true), true);
										/*for(int i = 0;i<grid.getParts().size();i++){
										((PartFluidDuct) grid.getParts().get(i)).sendUpdatePacket();
									}*/
	// grid.markForUpdate();
	/*	}
								}
							}
							//}
						}
					}
				}
			}
		}
	}*/
	@Override
	public FluidGrid constructGrid() {
		return new FluidGrid();
	}

	@Override
	public int getPropertyValue(EnumFacing side) {
		return connectsInv(side) ? /*connectsE(side) ? 3 :*/ 2 : connects(side) || connectsM(side) ? 1 : 0;
	}

	@Override
	public boolean readFromPacketI(NBTTagCompound tag) {
		stack = FluidStack.loadFluidStackFromNBT(tag);
		return false;
	}

	@Override
	public void writeToPacketI(NBTTagCompound tag) {
		FluidStack stack = grid.getData().getFluid();
		if (stack != null)
			stack.writeToNBT(tag);
	}

	/*@Override
	public boolean onConnectionBoxClicked(EnumFacing side, EntityPlayer player, ItemStack stack, EnumHand hand) {
		if(CoreInit.isWrench(stack, player)){
			if(!player.worldObj.isRemote){
				if(connectsE(side))eConnectionCache &= ~(1 << side.ordinal());
				else eConnectionCache |= 1 << side.ordinal();
				sendUpdatePacket();
			}
			return true;
		}
		return false;
	}*/
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return grid.getData();
	}

	@Override
	public void onGridReload() {
		grid.rec.removeAll(rec);
		//grid.addTank(tank);
		rec.clear();
		for (EnumFacing f : EnumFacing.VALUES) {
			if (connectsInv(f)) {
				TileEntity tile = world.getTileEntity(pos.offset(f));
				if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite())) {
					IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite());
					if (t != null) {
						rec.add(t);
						grid.rec.add(t);
					}
				}
			}
		}
	}

	@Override
	public void onGridPostReload() {
	}

	@Override
	public void onNeighborTileChange(boolean force) {
		super.onNeighborTileChange(force);
		if (!force) {
			onGridReload();
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		//nbt.setTag("fluidTank", tank.writeToNBT(new NBTTagCompound()));
		return super.writeToNBT(nbt);
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		//tank.readFromNBT(nbt.getCompoundTag("fluidTank"));
	}
}
