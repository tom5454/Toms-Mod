package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.Optional;

import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.tileentity.IPeripheralProxyControllable;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.lib.Configs;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@SuppressWarnings("deprecation")
@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityCCProxy extends TileEntityTomsMod implements IPeripheral{
	private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	public String pName = "tm_ender_memory";
	public String[] methods = {"listMethods","getName","call"};
	private String name = "tm_proxy";
	public int d = 0;
	private boolean firstStart = true;
	private IPeripheralProxyControllable current;
	public int direction = 0;
	public int directionO = 1;
	public int direction2 = 0;
	public int directionO2 = 1;
	@Override
	public String getType() {
		return this.name;
	}

	@Override
	public String[] getMethodNames() {
		return this.methods;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] a) throws LuaException,
	InterruptedException {
		if(method == 0){
			Object[] o = new Object[methods.length];
			for(int i = 0;i<o.length;i++){
				o[i] = methods[i];
			}
			return o;
		}else if(method == 1){
			this.onNeibourChange();
			if(current != null){
				return new Object[]{current.getName()};
			}else{
				throw new LuaException("There is no valid device in front of the Proxy");
			}
		}else if(method == 2){
			this.onNeibourChange();
			if(current != null){
				if(a.length > 0 && a[0] instanceof String){
					String m = (String) a[0];
					String[] methods = current.getMethodNames();
					if(m.equals("listMethods")){
						Object[] o = new Object[methods.length];
						for(int i = 0;i<o.length;i++){
							o[i] = methods[i];
						}
						return o;
					}else{
						for(int i = 0;i<methods.length;i++){
							if(m.equals(methods[i])){
								Object[] args = new Object[a.length-1];
								for(int j = 0;j<args.length;j++){
									args[j] = a[j+1];
								}
								return current.callMethod(computer, context, i, args);
							}
						}
						throw new LuaException("Method not found");
					}
				}else{
					throw new LuaException("Invalid argument 1, String excepted");
				}
			}else{
				throw new LuaException("There is no valid device in front of the Proxy");
			}
		}
		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {
		computers.add(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		computers.remove(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (other.getType() == this.getType());
	}
	@Override
	public void updateEntity(){
		if(!this.worldObj.isRemote && this.firstStart){
			this.firstStart = false;
			this.onNeibourChange();
		}
	}

	public void onNeibourChange(){
		int[] coords = TomsModUtils.getCoordTableUD(TomsModUtils.getCoordTable(pos), d);
		TileEntity tilee = worldObj.getTileEntity(new BlockPos(coords[0], coords[1], coords[2]));
		//System.out.println(coords[0]+" "+ coords[1]+" "+ coords[2] + " y" + super.yCoord);
		if(tilee instanceof IPeripheralProxyControllable){
			IPeripheralProxyControllable t = (IPeripheralProxyControllable) tilee;
			this.current = t;
		}else if(tilee instanceof IEnergyHandler){
			final IEnergyHandler t = (IEnergyHandler) tilee;
			this.current = new IPeripheralProxyControllable(){
				@Override
				public String[] getMethodNames() {
					return new String[]{"getEnergyStored","getMaxEnergyStored"};
				}
				@Override
				public Object[] callMethod(IComputerAccess computer,
						ILuaContext context, int method, Object[] arguments)
								throws LuaException, InterruptedException {
					if(method == 0){
						return new Object[]{t.getEnergyStored(TomsModUtils.getFD(d), EnergyType.HV)};
					}else if(method == 1){
						return new Object[]{t.getMaxEnergyStored(TomsModUtils.getFD(d), EnergyType.HV)};
					}
					return null;
				}

				@Override
				public String getName() {
					return "energyStorage";
				}

			};
		}else if(tilee instanceof IFluidHandler){
			final IFluidHandler t = (IFluidHandler) tilee;
			this.current = new IPeripheralProxyControllable(){
				@Override
				public String[] getMethodNames() {
					return new String[]{"getFluidAmount","getFluidName","getCapacity"};
				}

				@Override
				public Object[] callMethod(IComputerAccess computer,
						ILuaContext context, int method, Object[] arguments)
								throws LuaException, InterruptedException {
					if(method == 0){
						FluidTankInfo[] in = t.getTankInfo(TomsModUtils.getFD(d));
						Object[] o = new Object[in.length];
						for(int i = 0;i<o.length;i++){
							FluidStack f = in[i].fluid;
							if(f != null){
								o[i] = f.amount;
							}else{
								o[i] = 0;
							}

						}
						return o;
					}else if(method == 1){
						FluidTankInfo[] in = t.getTankInfo(TomsModUtils.getFD(d));
						Object[] o = new Object[in.length];
						for(int i = 0;i<o.length;i++){
							FluidStack f = in[i].fluid;
							if(f != null){
								o[i] = f.getFluid().getLocalizedName(f);
							}else{
								o[i] = null;
							}
						}
						return o;
					}else if(method == 2){
						FluidTankInfo[] in = t.getTankInfo(TomsModUtils.getFD(d));
						Object[] o = new Object[in.length];
						for(int i = 0;i<o.length;i++){
							o[i] = in[i].capacity;
						}
						return o;
					}
					return null;
				}

				@Override
				public String getName() {
					return "fluidTank";
				}

			};
		}else if(tilee instanceof IPeripheral){
			Block block = worldObj.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock();
			if(block instanceof IPeripheralProvider){
				IPeripheralProvider p = (IPeripheralProvider) block;
				int d = TomsModUtils.getFD(this.d).ordinal();
				int dir = 0;
				if (d == 5) dir = 4;
				else if(d == 4) dir = 5;
				else if (d == 3) dir = 2;
				else if(d == 2) dir = 3;
				else if(d == 0) dir = 1;
				else if(d == 1) dir = 0;
				final IPeripheral peripheral = p.getPeripheral(worldObj, new BlockPos(coords[0], coords[1], coords[2]), EnumFacing.VALUES[dir]);
				if(peripheral != null){
					this.current = new IPeripheralProxyControllable(){

						@Override
						public String[] getMethodNames() {
							return peripheral.getMethodNames();
						}

						@Override
						public Object[] callMethod(IComputerAccess computer,
								ILuaContext context, int method, Object[] arguments)
										throws LuaException, InterruptedException {
							return peripheral.callMethod(computer, context, method, arguments);
						}

						@Override
						public String getName() {
							return peripheral.getType();
						}

					};
				}else{
					this.current = null;
				}
			}else{
				this.current = null;
			}
		}else if(tilee instanceof IEnergyHandler && tilee instanceof IFluidHandler){
			final IFluidHandler t = (IFluidHandler) tilee;
			final IEnergyHandler t2 = (IEnergyHandler) tilee;
			this.current = new IPeripheralProxyControllable(){
				@Override
				public String[] getMethodNames() {
					return new String[]{"getFluidAmount","getFluidName","getCapacity","getEnergyStored","getMaxEnergyStored"};
				}

				@Override
				public Object[] callMethod(IComputerAccess computer,
						ILuaContext context, int method, Object[] arguments)
								throws LuaException, InterruptedException {
					if(method == 0){
						FluidTankInfo[] in = t.getTankInfo(TomsModUtils.getFD(d));
						Object[] o = new Object[in.length];
						for(int i = 0;i<o.length;i++){
							FluidStack f = in[i].fluid;
							if(f != null){
								o[i] = f.amount;
							}else{
								o[i] = 0;
							}

						}
						return o;
					}else if(method == 1){
						FluidTankInfo[] in = t.getTankInfo(TomsModUtils.getFD(d));
						Object[] o = new Object[in.length];
						for(int i = 0;i<o.length;i++){
							FluidStack f = in[i].fluid;
							if(f != null){
								o[i] = f.getFluid().getLocalizedName(f);
							}else{
								o[i] = null;
							}
						}
						return o;
					}else if(method == 2){
						FluidTankInfo[] in = t.getTankInfo(TomsModUtils.getFD(d));
						Object[] o = new Object[in.length];
						for(int i = 0;i<o.length;i++){
							o[i] = in[i].capacity;
						}
						return o;
					}else if(method == 3){
						return new Object[]{t2.getEnergyStored(TomsModUtils.getFD(d), EnergyType.HV)};
					}else if(method == 4){
						return new Object[]{t2.getMaxEnergyStored(TomsModUtils.getFD(d), EnergyType.HV)};
					}
					return null;
				}

				@Override
				public String getName() {
					return "fluidTank";
				}

			};
		}else{
			this.current = null;
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.d = tag.getInteger("d");
		this.direction = tag.getInteger("direction");
		this.directionO = tag.getInteger("directionOpposite");
		this.direction2 = tag.getInteger("direction2");
		this.directionO2 = tag.getInteger("direction2Opposite");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("d", this.d);
		tag.setInteger("direction",this.direction);
		tag.setInteger("directionOpposite",this.directionO);
		tag.setInteger("direction2",this.direction2);
		tag.setInteger("direction2Opposite",this.directionO2);
		return tag;
	}
	/*@Override
	public void writeToPacket(ByteBuf buf){
		buf.writeInt(d);
		buf.writeInt(direction);
		buf.writeInt(directionO);
	}

	@Override
	public void readFromPacket(ByteBuf buf){
		 this.d = buf.readInt();
		 this.direction = buf.readInt();
		 this.directionO = buf.readInt();
		 this.worldObj.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
	}*/
}
