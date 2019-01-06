package com.tom.energy.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import com.tom.api.tileentity.IPeripheralProxyControllable;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.IEnergyHandler;
import com.tom.lib.api.energy.IEnergyReceiver;
import com.tom.util.TomsModUtils;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileEntityEnergySensor extends TileEntityTomsMod implements IEnergyHandler, IPeripheralProxyControllable {
	public int direction = 0;
	public int directionO = 1;
	public int d = 0;
	public int direction2 = 0;
	public int directionO2 = 1;
	public int comparator = 0;
	public double energyPushedLast = 0;
	public int energyRate = 6;

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		EnumFacing front = EnumFacing.values()[this.direction2];
		EnumFacing back = EnumFacing.values()[this.directionO2];
		return (from == back || from == front)/* && type == UHV*/;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		EnumFacing s = EnumFacing.values()[this.direction2];
		// System.out.println("rec");
		if (!world.isRemote && from == s) {
			// System.out.println("rec2");
			TileEntity receiver = getTileEntityReceiver();
			if (receiver instanceof IEnergyReceiver) {
				// System.out.println("send");
				IEnergyReceiver recv = (IEnergyReceiver) receiver;
				if (recv.canConnectEnergy(getRotation(), EnergyType.HV)) {
					// System.out.println("send2");
					double extracted = Math.min(maxReceive, Math.pow(10, this.energyRate));
					double energyPushed = recv.receiveEnergy(getRotation(), EnergyType.HV, extracted, true);
					if (energyPushed > 0) {
						// System.out.println("push");
						this.markDirty();
						if (!simulate) {
							this.comparator = 1;
							for (int i = 1;i < 16;i++) {
								if (energyPushed < Math.pow(Math.pow(10, this.energyRate) / 15, i)) {
									this.comparator = i;
									break;
								}
							}
							this.energyPushedLast = energyPushed;
						}
						return recv.receiveEnergy(getRotation(), EnergyType.HV, energyPushed, simulate);
					}
				}
			}
		}
		return 0;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {

		return 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return 0;
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return MathHelper.floor(Math.pow(10, this.energyRate));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.d = tag.getInteger("d");
		this.direction = tag.getInteger("direction");
		this.directionO = tag.getInteger("directionOpposite");
		this.direction2 = tag.getInteger("direction2");
		this.directionO2 = tag.getInteger("direction2Opposite");
		this.energyRate = tag.getInteger("energyRate");
	}

	/*@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("d", this.d);
		tag.setInteger("direction",this.direction);
		tag.setInteger("directionOpposite",this.directionO);
		tag.setInteger("direction2",this.direction2);
		tag.setInteger("direction2Opposite",this.directionO2);
		tag.setInteger("energyRate", this.energyRate);

	}
	@Override
	public void writeToPacket(ByteBuf buf){
		buf.writeInt(d);
		buf.writeInt(direction);
		buf.writeInt(directionO);
		buf.writeInt(direction2);
		buf.writeInt(directionO2);
	}

	@Override
	public void readFromPacket(ByteBuf buf){
		 this.d = buf.readInt();
		 this.direction = buf.readInt();
		 this.directionO = buf.readInt();
		 this.direction2 = buf.readInt();
		 this.directionO2 = buf.readInt();
		 int xCoord = pos.getX();
		 int yCoord = pos.getY();
		 int zCoord = pos.getZ();
		 this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}*/
	private TileEntity getTileEntityReceiver() {
		int[] coords = TomsModUtils.getCoordTableUD(TomsModUtils.getCoordTable(pos), d);
		int x = coords[0];
		int y = coords[1];
		int z = coords[2];
		// System.out.println(coords[0]+" "+coords[1]+" "+coords[2]);
		return world.getTileEntity(new BlockPos(x, y, z));
	}

	private EnumFacing getRotation() {
		return EnumFacing.values()[this.direction2];
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"getLastEnergyPacket", "getMaxPacketSize", "getComparatorOutput", "getEnergyRate", "setEnergyRate"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		if (method == 0) {
			return new Object[]{this.energyPushedLast};
		} else if (method == 1) {
			return new Object[]{MathHelper.floor(Math.pow(10, this.energyRate))};
		} else if (method == 2) {
			return new Object[]{this.comparator};
		} else if (method == 3) {
			return new Object[]{this.energyRate};
		} else if (method == 4) {
			if (arguments[0] instanceof Double) {
				this.energyRate = MathHelper.floor((Double) arguments[0]);
				return new Object[]{true};
			} else {
				throw new LuaException("Bad argument #1 number excepted");
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return "energySensor";
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return /*UHV.getList()*/null;
	}
}
