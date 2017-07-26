package com.tom.core.tileentity;

import static com.tom.api.energy.EnergyType.LV;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.lib.Configs;
import com.tom.thirdparty.waila.IIntegratedMultimeter;

import com.tom.core.block.BlockMonitorBase;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityGPU extends TileEntityTomsMod implements IPeripheral, IEnergyReceiver, IIntegratedMultimeter/*, ILinkable*/ {

	public String[] methods = {"fill", "filledRectangle", "rectangle", "listMethods", "setSize", "getSize", "sync", "clear", "addText", "addTexture", "loadFromBackup", "getEnergyStored", "getMaxEnergyStored", "getEnergyUsage"};
	private List<IComputerAccess> computers = new ArrayList<>();
	private EnergyStorage energy = new EnergyStorage(1000000, 10000, 100000);
	// private TileEntityMonitorBase[][] monitors;
	private static final int[][] blockPositions = new int[][]{{0, -1, 0}, {0, 1, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
	// private static final int[][] metaPositions = new
	// int[][]{{-1,0,-1},{-1,0,1},{-1,1,0},{1,1,0},{0,1,1},{0,1,-1}};
	// private static final EnumFacing[][] disabledFD = new
	// EnumFacing[][]{{UP,DOWN}};
	/*private int posX = 0;
	private int posY = 0;
	private int posZ = 0;*/
	// private Map<Integer,List<Entry<Integer,Integer>>> monitors = new
	// HashMap<Integer,List<Entry<Integer,Integer>>>();
	private int maxX = 0;
	private int maxY = 0;
	private int sizeX = 16;
	private int sizeY = 16;
	private List<List<TileEntityMonitorBase>> monitors = new ArrayList<>();
	private int[][] screen = new int[16][16];
	private int[][] screenOld = new int[16][16];
	public List<LuaText> textList = new ArrayList<>();
	public List<LuaTexture> textureList = new ArrayList<>();
	private boolean firstStart = true;
	private boolean deviceOutOfPower = false;

	@Override
	public String getType() {
		return "tm_gpu";
	}

	@Override
	public String[] getMethodNames() {
		return this.methods;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] a) throws LuaException, InterruptedException {
		if (method != 3 || method != 11 || method != 12) {
			int energyR = method == 5 || method == 13 ? 1 : 2;
			double ee = energy.extractEnergy(energyR, true);
			if (ee != energyR)
				throw new LuaException("Device is out of power");
			this.energy.extractEnergy(ee, false);
			this.connectMonitors(this.findMonitor());
		}
		int maxX = this.maxX * this.sizeX;
		int maxY = this.maxY * this.sizeY;
		Object[] ret = new Object[1];
		ret[0] = false;
		if (method == 0) {
			int color;
			if (a.length == 0) {
				color = 0x000000;
			} else if ((a[0] instanceof Double) && (a.length > 0)) {
				color = MathHelper.floor((Double) a[0]);
			} else {
				throw new LuaException("Bad argument #1 (expected number)");
			}
			if (color >= 0) {
				for (int i = 0;i < maxX;i++) {
					for (int j = 0;j < maxY;j++) {
						this.screen[i][j] = color;
					}
				}
				ret[0] = true;
			} else {
				throw new LuaException("Bad Argument #1, (too small number (" + color + ") minimum value is 0 )");
			}
		} else if (method == 1) {
			int color;
			if (a.length < 4) {
				throw new LuaException("Too few arguments (expected x,y,width,height,[color])");
			} else if ((a.length >= 4) && !(a[0] instanceof Double)) {
				throw new LuaException("Bad argument #1 (expected Number)");
			} else if ((a.length >= 4) && !(a[1] instanceof Double)) {
				throw new LuaException("Bad argument #2 (expected Number)");
			} else if ((a.length >= 4) && !(a[2] instanceof Double)) {
				throw new LuaException("Bad argument #3 (expected Number)");
			} else if ((a.length >= 4) && !(a[3] instanceof Double)) {
				throw new LuaException("Bad argument #4 (expected Number)");
			} else if ((a.length >= 5) && !(a[4] instanceof Double)) {
				throw new LuaException("Bad argument #5 (expected Number)");
			} else {
				if (a.length < 5) {
					color = 0x000000;
				} else {
					color = MathHelper.floor((Double) a[4]);
				}
				if (color < 0)
					throw new LuaException("Bad Argument #5, (too small number (" + color + ") minimum value is 0 )");
				int xStart = MathHelper.floor((Double) a[0]) - 1;
				int yStart = MathHelper.floor((Double) a[1]) - 1;
				int xStop = xStart + MathHelper.floor((Double) a[2]);
				int yStop = yStart + MathHelper.floor((Double) a[3]);
				if (xStart < maxX + 1 && yStart < maxY + 1 && xStart >= 0 && yStart >= 0) {
					xStop = xStop > maxX ? maxX : xStop;
					yStop = yStop > maxY ? maxY : yStop;
					for (int i = xStart;i < xStop;i++) {
						for (int y = yStart;y < yStop;y++) {
							this.screen[i][y] = color;
						}
					}
					ret[0] = true;
				} else {
					throw new LuaException("Out of boundary");
				}
			}
		} else if (method == 2) {
			int color;
			if (a.length < 4) {
				throw new LuaException("Too few arguments (expected x,y,width,height,[color])");
			} else if ((a.length >= 4) && !(a[0] instanceof Double)) {
				throw new LuaException("Bad argument #1 (expected Number)");
			} else if ((a.length >= 4) && !(a[1] instanceof Double)) {
				throw new LuaException("Bad argument #2 (expected Number)");
			} else if ((a.length >= 4) && !(a[2] instanceof Double)) {
				throw new LuaException("Bad argument #3 (expected Number)");
			} else if ((a.length >= 4) && !(a[3] instanceof Double)) {
				throw new LuaException("Bad argument #4 (expected Number)");
			} else if ((a.length >= 5) && !(a[4] instanceof Double)) {
				throw new LuaException("Bad argument #5 (expected Number)");
			} else {
				if (a.length < 5) {
					color = 0x000000;
				} else {
					color = MathHelper.floor((Double) a[4]);
				}
				if (color < 0)
					throw new LuaException("Bad Argument #5, (too small number (" + color + ") minimum value is 0 )");
				int xStart = MathHelper.floor((Double) a[0]) - 1;
				int yStart = MathHelper.floor((Double) a[1]) - 1;
				int xStop = xStart + MathHelper.floor((Double) a[2]);
				int yStop = yStart + MathHelper.floor((Double) a[3]);
				if (xStart < maxX + 1 && yStart < maxY + 1 && xStart >= 0 && yStart >= 0) {
					xStop = xStop > maxX ? maxX : xStop;
					yStop = yStop > maxY ? maxY : yStop;
					for (int i = xStart;i < xStop;i++) {
						for (int y = yStart;y < yStop;y++) {
							this.screen[i][y] = (i == xStart || i == xStop - 1) ? color : ((y == yStart || y == yStop - 1) ? color : this.screen[i][y]);
						}
					}
					ret[0] = true;
				} else {
					throw new LuaException("Out of boundary");
				}
			}
		} else if (method == 3) {
			Object[] o = new Object[methods.length];
			for (int i = 0;i < o.length;i++) {
				o[i] = methods[i];
			}
			return o;
		} else if (method == 4) {
			int s = MathHelper.floor((Double) a[0]);
			if (s < 1)
				throw new LuaException("Bad Argument #1, (too small number (" + s + ") minimum value is 1 )");
			if (s > Configs.monitorSize)
				throw new LuaException("Bad Argument #1, (too big number (" + s + ") maximum value is " + Configs.monitorSize + " )");
			this.screen = new int[s * this.maxX][s * this.maxY];
			this.sizeX = s;
			this.sizeY = s;
			for (List<TileEntityMonitorBase> cMonList : this.monitors) {
				for (TileEntityMonitorBase mon : cMonList) {
					if (mon != null) {
						mon.sizeX = this.sizeX;
						mon.sizeY = this.sizeY;
						mon.screen = new int[sizeX][sizeY];
					}
				}
			}
			ret[0] = true;
		} else if (method == 5) {
			return new Object[]{this.sizeX, this.sizeY, this.maxX, this.maxY, this.sizeX * this.maxX, this.sizeY * this.maxY};
		} else if (method == 6) {
			this.sync();
			ret[0] = true;
		} else if (method == 7) {
			this.textList.clear();
			this.textureList.clear();
			// this.screen = new
			// int[this.maxX*this.sizeX][this.maxY*this.sizeY];
			ret[0] = true;
		} else if (method == 8) {
			if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double) {
				int x = MathHelper.floor((Double) a[0]);
				int y = MathHelper.floor((Double) a[1]);
				int c = a.length > 3 && a[3] instanceof Double ? MathHelper.floor((Double) a[3]) : 0xFFFFFF;
				String s = a[2].toString();
				LuaText t = new LuaText(x, y, c, s, 0, 1);
				this.textList.add(t);
				return new Object[]{true, t};
			} else {
				throw new LuaException("Invalid Arguments, excepted (number,number,String,[number])");
			}
		} else if (method == 9) {
			if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double) {
				int x = MathHelper.floor((Double) a[0]);
				int y = MathHelper.floor((Double) a[1]);
				int c = a.length > 3 && a[3] instanceof Double ? MathHelper.floor((Double) a[3]) : 0xFFFFFFFF;
				String s = a[2].toString();
				LuaTexture t = new LuaTexture(x, y, c, s, 0, false, 1, 1);
				this.textureList.add(t);
				return new Object[]{true, t};
			} else {
				throw new LuaException("Invalid Arguments, excepted (number,number,String,[number])");
			}
		} else if (method == 10) {
			if (this.deviceOutOfPower) {
				this.deviceOutOfPower = false;
				this.screen = this.screenOld;
				this.sync();
				return new Object[]{true};
			} else
				return new Object[]{false};
		} else if (method == 11)
			return new Object[]{this.energy.getEnergyStored()};
		else if (method == 12)
			return new Object[]{this.energy.getMaxEnergyStored()};
		else if (method == 13)
			return new Object[]{this.maxX * this.maxY};
		return ret;
	}

	private void sync() {
		TomsModUtils.getServer().addScheduledTask(() -> {
			this.connectMonitors(this.findMonitor());
			int index1 = 0;
			for (List<TileEntityMonitorBase> cMonList : this.monitors) {
				int index2 = cMonList.size() - 1;
				for (TileEntityMonitorBase mon : cMonList) {
					if (mon != null) {
						mon.screen = TomsModUtils.separateIntArray(screen, index1, index2, sizeX, sizeY);
						mon.textList.clear();
						mon.textureList.clear();
						int indexStart1 = index1 * sizeX;
						int indexStart2 = index2 * sizeY;
						int indexEnd1 = ((index1 + 1) * sizeX) - 1;
						int indexEnd2 = ((index2 + 1) * sizeY) - 1;
						for (LuaTexture t : this.textureList) {
							if (t.xCoord > indexStart1 && t.xCoord < indexEnd1 && t.yCoord > indexStart2 && t.yCoord < indexEnd2) {
								mon.textureList.add(mon.new LuaTexture(t.xCoord % 64, t.yCoord % 64, t.c, t.s, t.rotation, t.colored, t.opacity, t.scale));
							}
						}
						for (LuaText t : this.textList) {
							if (t.xCoord > indexStart1 && t.xCoord < indexEnd1 && t.yCoord > indexStart2 && t.yCoord < indexEnd2) {
								mon.textList.add(mon.new LuaText(t.xCoord % 64, t.yCoord % 64, t.c, t.s, t.rotation, t.scale));
							}
						}
						mon.markDirty();
						// System.out.println(index1+":"+index2+"
						// s:"+mon.screen[1][1]+" s:"+this.screen[1][1]);
						markBlockForUpdate(mon.getPos());
					}
					index2--;
				}
				index1++;
			}
		});
	}

	@Override
	public void attach(IComputerAccess computer) {
		this.computers.add(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		this.computers.remove(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other == this;
	}

	@Override
	public void updateEntity() {
		/*if(worldObj.isRemote)
			return;
		
		int meta = getBlockMetadata();
		int range = 80;
		List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(xCoord - range, yCoord - range, zCoord - range, xCoord + range, yCoord + range, zCoord + range));
		
		boolean looking = false;
		
		for(EntityPlayer player : players) {
			ItemStack helm = player.getCurrentArmor(3);
			if(helm != null && helm.getItem() == Item.getItemFromBlock(Blocks.pumpkin))
				continue;
		
			MovingObjectPosition pos = ToolCommons.raytraceFromEntity(worldObj, player, true, 64);
			if(pos != null && pos.blockX == xCoord && pos.blockY == yCoord && pos.blockZ == zCoord) {
				looking = true;
				break;
			}
		}
		int newMeta = looking ? 15 : 0;
		if(newMeta != meta)
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, newMeta, 1 | 2);*/
		if (!world.isRemote) {
			int mons = this.maxX * this.maxY;
			double e = this.energy.extractEnergy(mons, false);
			// System.out.println("s:"+this.screen.length+"
			// s1:"+this.screen[0].length);
			if (e != mons) {
				int maxX = this.maxX * this.sizeX;
				int maxY = this.maxY * this.sizeY;
				this.screenOld = this.screen;
				this.screen = new int[maxX][maxY];
				this.sync();
				this.deviceOutOfPower = true;
			}
			if (firstStart) {
				this.firstStart = false;
				this.connectMonitors(this.findMonitor());
			}
		}
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return from != EnumFacing.UP && type == LV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return this.canConnectEnergy(from, type) ? this.energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.canConnectEnergy(from, type) ? this.energy.getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.canConnectEnergy(from, type) ? this.energy.getMaxEnergyStored() : 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy.readFromNBT(tag.getCompoundTag("energy"));
		/*this.posX = tag.getInteger("posX");
		this.posY = tag.getInteger("posY");
		this.posZ = tag.getInteger("posZ");*/
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("energy", this.energy.writeToNBT(new NBTTagCompound()));
		/*tag.setInteger("posX", this.posX);
		tag.setInteger("posY", this.posY);
		tag.setInteger("posZ", this.posZ);*/
		return tag;
	}

	public TileEntityMonitorBase findMonitor() {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		Block blockU = world.getBlockState(new BlockPos(xCoord, yCoord + 1, zCoord)).getBlock();
		Block blockD = world.getBlockState(new BlockPos(xCoord, yCoord - 1, zCoord)).getBlock();
		Block blockXP = world.getBlockState(new BlockPos(xCoord + 1, yCoord, zCoord)).getBlock();
		Block blockXM = world.getBlockState(new BlockPos(xCoord - 1, yCoord, zCoord)).getBlock();
		Block blockZP = world.getBlockState(new BlockPos(xCoord, yCoord, zCoord + 1)).getBlock();
		Block blockZM = world.getBlockState(new BlockPos(xCoord, yCoord, zCoord - 1)).getBlock();
		Block[] blocks = {blockD, blockU, blockXP, blockXM, blockZP, blockZM};
		TileEntityMonitorBase base = null;
		for (int i = 0;i < 6;i++) {
			if (blocks[i] != null && blocks[i] instanceof BlockMonitorBase) {
				int[] p = blockPositions[i];
				base = (TileEntityMonitorBase) world.getTileEntity(new BlockPos(xCoord + p[0], yCoord + p[1], zCoord + p[2]));
				break;
			}
		}
		return base;
		/*this.monitors.clear();
		if(base != null){
			List<Entry<Integer,Integer>> l = new ArrayList<Entry<Integer, Integer>>();
			Entry<Integer,Integer> e = new EmptyEntry<Integer, Integer>(base.yCoord);
			e.setValue(base.zCoord);
			l.add(e);
			this.monitors.put(base.xCoord,l);
			//int dir = base.direction;
			/*EnumFacing dd = EnumFacing.values()[d];
			EnumFacing ddo = dd.getOpposite();*/
		/*List<TileEntityMonitorBase> connectedMonitors = new ArrayList<TileEntityMonitorBase>();
			Stack<TileEntityMonitorBase> traversingMonitors = new Stack<TileEntityMonitorBase>();
			TileEntityMonitorBase master = base;
			traversingMonitors.add(base);
			int direction = master.direction;
			while(!traversingMonitors.isEmpty()) {
				TileEntityMonitorBase storage = traversingMonitors.pop();
				connectedMonitors.add(storage);
				for(EnumFacing d : EnumFacing.VALID_DIRECTIONS) {
					if((direction == 0 || direction == 1) && d.offsetY != 0)continue;
					if((direction == 2 || direction == 3) && d.offsetZ != 0)continue;
					if((direction == 4 || direction == 5) && d.offsetX != 0)continue;
					TileEntity te = worldObj.getTileEntity(storage.xCoord + d.offsetX, storage.yCoord + d.offsetY, storage.zCoord + d.offsetZ);
					if(te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase)te).direction == master.direction) {
						traversingMonitors.add((TileEntityMonitorBase)te);
					}
				}
			}
		
			/*List<Integer> xList = new ArrayList<Integer>();
			List<Integer> yList = new ArrayList<Integer>();
			List<Integer> zList = new ArrayList<Integer>();
			for(TileEntityMonitorBase m : connectedMonitors){
				xList.add(m.xCoord);
				yList.add(m.yCoord);
				zList.add(m.zCoord);
			}
			boolean first = true;
			int last = 0;
			for(int x : xList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.min(last, x);
				}
			}
			int minX = last;
			first = true;
			for(int x : yList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.min(last, x);
				}
			}
			int minY = last;
			first = true;
			for(int x : zList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.min(last, x);
				}
			}
			int minZ = last;
			first = true;
			last = 0;
			for(int x : xList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.max(last, x);
				}
			}
			int maxX = last;
			first = true;
			for(int x : yList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.max(last, x);
				}
			}
			int maxY = last;
			first = true;
			for(int x : zList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.max(last, x);
				}
			}
			int maxZ = last;
		
		 */// }
		/*
		TileEntityMonitorBase[] m = new TileEntityMonitorBase[6];
		if(blockU instanceof BlockMonitorBase) m[0] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
		if(blockD instanceof BlockMonitorBase) m[1] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
		if(blockXP instanceof BlockMonitorBase) m[2] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord+1, yCoord, zCoord);
		if(blockXM instanceof BlockMonitorBase) m[3] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord-1, yCoord, zCoord);
		if(blockZP instanceof BlockMonitorBase) m[4] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord, yCoord, zCoord+1);
		if(blockZM instanceof BlockMonitorBase) m[5] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord, yCoord, zCoord-1);
		TileEntityMonitorBase[][] mons = new TileEntityMonitorBase[6][];
		int i = 0;
		for(TileEntityMonitorBase mon : m){
			if(mon == null) continue;
			List<TileEntityMonitorBase> connectedStorages = new ArrayList<TileEntityMonitorBase>();
			Stack<TileEntityMonitorBase> traversingStorages = new Stack<TileEntityMonitorBase>();
			TileEntityMonitorBase master = mon;
			traversingStorages.add(mon);
			int direction = master.direction;
			while(!traversingStorages.isEmpty()) {
				TileEntityMonitorBase storage = traversingStorages.pop();
				connectedStorages.add(storage);
				for(EnumFacing d : EnumFacing.VALID_DIRECTIONS) {
					if((direction == 0 || direction == 1) && d.offsetY != 0)continue;
					if((direction == 2 || direction == 3) && d.offsetZ != 0)continue;
					if((direction == 4 || direction == 5) && d.offsetX != 0)continue;
					TileEntity te = worldObj.getTileEntity(storage.xCoord + d.offsetX, storage.yCoord + d.offsetY, storage.zCoord + d.offsetZ);
					if(te instanceof TileEntityMonitorBase && !connectedStorages.contains(te) && ((TileEntityMonitorBase)te).direction == master.direction) {
						traversingStorages.add((TileEntityMonitorBase)te);
					}
				}
			}
			mons[i] = connectedStorages.toArray(new TileEntityMonitorBase[connectedStorages.size()]);
			i++;
		}
		for(TileEntityMonitorBase[] cmS : mons){
			if(cmS == null) continue;
			int Bx = 0;
			int By = 0;
			int Bz = 0;
			boolean last = false;
			for(TileEntityMonitorBase cm : cmS){
				if(cm == null) continue;
				if(!last){
					Bx = cm.xCoord;
					By = cm.yCoord;
					Bz = cm.zCoord;
					last = true;
				}
				if(cm.direction == 0 || cm.direction == 1){
					if(Bx < cm.xCoord){
						Bx = cm.xCoord;
					}
					if(Bz < cm.zCoord){
						Bz = cm.zCoord;
					}
				}else{
					if(By < cm.yCoord){
						By = cm.yCoord;
					}
					if(cm.direction == 2 || cm.direction == 3){
						if(Bx < cm.xCoord){
							Bx = cm.xCoord;
						}
					}else{
						if(Bz < cm.zCoord){
							Bz = cm.zCoord;
						}
					}
				}
			}
		}*/
	}

	/*@Override
	public boolean link(int x, int y, int z) {
		TileEntity tilee = worldObj.getTileEntity(x, y, z);
		if(tilee instanceof TileEntityMonitorBase){
	
			return true;
		}
		return false;
	}*/
	public boolean connectMonitors(TileEntityMonitorBase base) {
		if (base != null) {
			int xCoord = pos.getX();
			int yCoord = pos.getY();
			int zCoord = pos.getZ();
			this.monitors.clear();
			/*List<TileEntityMonitorBase> connectedMonitors = new ArrayList<TileEntityMonitorBase>();
			Stack<TileEntityMonitorBase> traversingMonitors = new Stack<TileEntityMonitorBase>();
			traversingMonitors.add(base);
			int direction = base.direction;
			int i = 1;
			while(!traversingMonitors.isEmpty()) {
			TileEntityMonitorBase storage = traversingMonitors.pop();
			connectedMonitors.add(storage);
			/*for(EnumFacing d : EnumFacing.VALID_DIRECTIONS) {
				if((direction == 0 || direction == 1) && d.offsetY != 0)continue;
				if((direction == 2 || direction == 3) && d.offsetZ != 0)continue;
				if((direction == 4 || direction == 5) && d.offsetX != 0)continue;
				TileEntity te = worldObj.getTileEntity(storage.xCoord + d.offsetX, storage.yCoord + d.offsetY, storage.zCoord + d.offsetZ);
				if(te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase)te).direction == master.direction) {
					traversingMonitors.add((TileEntityMonitorBase)te);
				}
			}*//*
				TileEntity te = TomsMathHelper.getTileEntity(worldObj, base.getOffset(i, 0, direction));
				if(te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase)te).direction == direction) {
				traversingMonitors.add((TileEntityMonitorBase)te);
				}else{
				break;
				}
				i++;
				}*/
			List<TileEntityMonitorBase> listX = this.getMonitorsRight(base);
			List<List<TileEntityMonitorBase>> mons = new ArrayList<>();
			List<TileEntityMonitorBase> listBY = this.getMonitorsUp(base);
			int maxSizeY = listBY.size() + 1;
			int maxSizeX = listX.size() + 1;
			for (TileEntityMonitorBase mon : listX) {
				List<TileEntityMonitorBase> cMons = this.getMonitorsUp(mon);
				// mons.add(cMons);
				maxSizeY = Math.min(maxSizeY, cMons.size());
			}
			for (TileEntityMonitorBase mon : listBY) {
				List<TileEntityMonitorBase> cMons = this.getMonitorsRight(mon);
				// mons.add(cMons);
				maxSizeX = Math.min(maxSizeX, cMons.size());
			}
			for (int x = 0;x < maxSizeX;x++) {
				List<TileEntityMonitorBase> cM = new ArrayList<>();
				for (int y = 0;y < maxSizeY;y++) {
					cM.add(((TileEntityMonitorBase) TomsModUtils.getTileEntity(world, base.getOffset(x, y, base.direction))).connect(xCoord, yCoord, zCoord));
				}
				mons.add(cM);
			}
			this.monitors = mons;
			int oldX = this.maxX;
			int oldY = this.maxY;
			this.maxX = maxSizeX;
			this.maxY = maxSizeY;
			if (oldX != this.maxX || oldY != this.maxY) {
				this.screen = new int[this.maxX * this.sizeX][this.maxY * this.sizeY];
			}
		}
		return true;
	}

	public int[] getOffset(int x, int y, int d) {
		int[] ret;
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if (d == 0) {
			ret = TomsModUtils.getCoordTable(xCoord - x, yCoord, zCoord - y);
		} else if (d == 1) {
			ret = TomsModUtils.getCoordTable(xCoord + x, yCoord, zCoord + y);
		} else if (d == 2) {
			ret = TomsModUtils.getCoordTable(xCoord - x, yCoord + y, zCoord);
		} else if (d == 3) {
			ret = TomsModUtils.getCoordTable(xCoord + x, yCoord + y, zCoord);
		} else if (d == 4) {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord + y, zCoord + x);
		} else if (d == 5) {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord + y, zCoord - x);
		} else {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord);
		}
		return ret;
	}

	public int[] getOffset(int x, int y, int z, int d) {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		int[] ret;
		if (d == 0) {
			ret = new int[]{xCoord - x, zCoord - y};
		} else if (d == 1) {
			ret = new int[]{xCoord + x, zCoord + y};
		} else if (d == 2) {
			ret = new int[]{xCoord - x, yCoord + y};
		} else if (d == 3) {
			ret = new int[]{xCoord + x, yCoord + y};
		} else if (d == 4) {
			ret = new int[]{zCoord + x, yCoord + y};
		} else if (d == 5) {
			ret = new int[]{zCoord - x, yCoord + y};
		} else {
			ret = new int[]{xCoord, yCoord};
		}
		return ret;
	}

	public List<TileEntityMonitorBase> getMonitorsUp(TileEntityMonitorBase master) {
		List<TileEntityMonitorBase> connectedMonitors = new ArrayList<>();
		if (master != null) {
			Stack<TileEntityMonitorBase> traversingMonitors = new Stack<>();
			traversingMonitors.add(master);
			int direction = master.direction;
			int i = 1;
			while (!traversingMonitors.isEmpty()) {
				TileEntityMonitorBase storage = traversingMonitors.pop();
				connectedMonitors.add(storage);
				/*for(EnumFacing d : EnumFacing.VALID_DIRECTIONS) {
				if((direction == 0 || direction == 1) && d.offsetY != 0)continue;
				if((direction == 2 || direction == 3) && d.offsetZ != 0)continue;
				if((direction == 4 || direction == 5) && d.offsetX != 0)continue;
				TileEntity te = worldObj.getTileEntity(storage.xCoord + d.offsetX, storage.yCoord + d.offsetY, storage.zCoord + d.offsetZ);
				if(te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase)te).direction == master.direction) {
					traversingMonitors.add((TileEntityMonitorBase)te);
				}
				}*/
				TileEntity te = TomsModUtils.getTileEntity(world, master.getOffset(0, i, direction));
				if (te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase) te).direction == direction) {
					traversingMonitors.add((TileEntityMonitorBase) te);
				} else {
					break;
				}
				i++;
			}
		}
		return connectedMonitors;
	}

	public List<TileEntityMonitorBase> getMonitorsRight(TileEntityMonitorBase master) {
		List<TileEntityMonitorBase> connectedMonitors = new ArrayList<>();
		if (master != null) {
			Stack<TileEntityMonitorBase> traversingMonitors = new Stack<>();
			traversingMonitors.add(master);
			int direction = master.direction;
			int i = 1;
			while (!traversingMonitors.isEmpty()) {
				TileEntityMonitorBase storage = traversingMonitors.pop();
				connectedMonitors.add(storage);
				/*for(EnumFacing d : EnumFacing.VALID_DIRECTIONS) {
				if((direction == 0 || direction == 1) && d.offsetY != 0)continue;
				if((direction == 2 || direction == 3) && d.offsetZ != 0)continue;
				if((direction == 4 || direction == 5) && d.offsetX != 0)continue;
				TileEntity te = worldObj.getTileEntity(storage.xCoord + d.offsetX, storage.yCoord + d.offsetY, storage.zCoord + d.offsetZ);
				if(te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase)te).direction == master.direction) {
					traversingMonitors.add((TileEntityMonitorBase)te);
				}
				}*/
				TileEntity te = TomsModUtils.getTileEntity(world, master.getOffset(i, 0, direction));
				if (te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase) te).direction == direction) {
					traversingMonitors.add((TileEntityMonitorBase) te);
				} else {
					break;
				}
				i++;
			}
		}
		return connectedMonitors;
	}

	public class LuaTexture implements ILuaObject {
		public String s = "";
		public double xCoord = 0;
		public double yCoord = 0;
		public int c = 0;
		public double rotation = 0;
		public boolean colored = false;
		public double opacity = 1;
		public float scale = 1;

		public LuaTexture() {
		}

		public LuaTexture(double x, double y, int c, String text, double rotation, boolean colored, double opacity, float scale) {
			this.xCoord = x;
			this.yCoord = y;
			this.c = c;
			this.s = text;
			this.rotation = rotation;
			this.colored = colored;
			this.opacity = opacity;
			this.scale = scale;
		}

		@Override
		public String[] getMethodNames() {
			return new String[]{"getTexture", "getX", "getY", "getColor", "setTexture", "setX", "setY", "setColor", "delete", "getRotation", "setRotation", "setColored", "getScale", "setScale"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] a) throws LuaException, InterruptedException {
			if (!textureList.contains(this))
				throw new LuaException("This object was already deleted");
			if (method == 0)
				return new Object[]{s};
			else if (method == 1)
				return new Object[]{xCoord};
			else if (method == 2)
				return new Object[]{yCoord};
			else if (method == 3)
				return new Object[]{this.colored, c, this.opacity};
			else if (method == 4) {
				if (a.length > 0 && a[0] != null) {
					this.s = a[0].toString();
				} else {
					throw new LuaException("Invalid Arguments, excepted (String)");
				}
			} else if (method == 5) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.xCoord = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 6) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.yCoord = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 7) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.c = MathHelper.floor((Double) a[0]);
					if (a.length > 1 && a[1] instanceof Double) {
						this.opacity = (Double) a[1];
					}
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 8) {
				textureList.remove(this);
			} else if (method == 9)
				return new Object[]{this.rotation};
			else if (method == 10) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.rotation = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 11) {
				if (a.length > 0 && a[0] instanceof Boolean) {
					this.colored = (Boolean) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (boolean)");
				}
			} else if (method == 12)
				return new Object[]{this.scale};
			else if (method == 13) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.scale = new Float((Double) a[0]);
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}
			return null;
		}

	}

	public class LuaText implements ILuaObject {
		public String s = "";
		public double xCoord = 0;
		public double yCoord = 0;
		public int c = 0;
		public double rotation = 0;
		public float scale = 1;

		public LuaText() {
		}

		public LuaText(double x, double y, int c, String text, double rotation, float scale) {
			this.xCoord = x;
			this.yCoord = y;
			this.c = c;
			this.s = text;
			this.rotation = rotation;
			this.scale = scale;
		}

		@Override
		public String[] getMethodNames() {
			return new String[]{"getText", "getX", "getY", "getColor", "setText", "setX", "setY", "setColor", "delete", "getRotation", "setRotation", "getScale", "setScale"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] a) throws LuaException, InterruptedException {
			if (!textList.contains(this))
				throw new LuaException("This object was already deleted");
			if (method == 0)
				return new Object[]{s};
			else if (method == 1)
				return new Object[]{xCoord};
			else if (method == 2)
				return new Object[]{yCoord};
			else if (method == 3)
				return new Object[]{c};
			else if (method == 4) {
				if (a.length > 0 && a[0] != null) {
					this.s = a[0].toString();
				} else {
					throw new LuaException("Invalid Arguments, excepted (String)");
				}
			} else if (method == 5) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.xCoord = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 6) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.yCoord = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 7) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.c = MathHelper.floor((Double) a[0]);
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 8) {
				textList.remove(this);
			} else if (method == 9)
				return new Object[]{this.rotation};
			else if (method == 10) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.rotation = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 11)
				return new Object[]{this.scale};
			else if (method == 12) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.scale = new Float((Double) a[0]);
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}
			return null;
		}
	}

	public void queueEvent(String event, Object[] args) {
		Object[] a = new Object[args.length + 1];
		for (int i = 0;i < args.length;i++) {
			a[i + 1] = args[i];
		}
		for (IComputerAccess c : computers) {
			a[0] = c.getAttachmentName();
			c.queueEvent(event, a);
		}
	}

	public void monitorClick(int xC, int yC, int zC, int pX, int pY) {
		this.connectMonitors(this.findMonitor());
		int index1 = 0;
		for (List<TileEntityMonitorBase> cMonList : this.monitors) {
			int index2 = cMonList.size() - 1;
			for (TileEntityMonitorBase mon : cMonList) {
				if (mon != null) {
					BlockPos monp = mon.getPos();
					if (monp.getX() == xC && monp.getY() == yC && monp.getZ() == zC) {
						int xP = pX + (this.sizeX * index1);
						int yP = pY + (this.sizeY * index2);
						this.queueEvent("tm_monitor_touch", new Object[]{xP + 1, yP + 1});
						break;
					}
				}
				index2--;
			}
			index1++;
		}
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LV.getList();
	}
}
