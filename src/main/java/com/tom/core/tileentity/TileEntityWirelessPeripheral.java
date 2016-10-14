package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.tileentity.ILinkable;
import com.tom.api.tileentity.IWirelessPeripheralController;
import com.tom.api.tileentity.TileEntityTabletAccessPointBase;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.ExtraBlockHitInfo;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;

import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityWirelessPeripheral extends TileEntityTomsMod implements IPeripheral, ILinkable, IWirelessPeripheralController {
	private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	public String pName = "tm_wirelessPeripheral";
	public String[] methods = {"listMethods","call","getName","link","splitString"};
	private IPeripheral current;
	public int posX = 0;
	public int posY = 0;
	public int posZ = 0;
	public int lDim = 0;
	public EnumFacing linkedSide = EnumFacing.DOWN;
	public boolean linked = false;
	private boolean fs = true;
	protected IComputerAccess computerAccess = new IComputerAccess(){

		@Override
		public String mount(String desiredLocation, IMount mount) {
			return "null";
		}

		@Override
		public String mount(String desiredLocation, IMount mount,
				String driveName) {
			return "null";
		}

		@Override
		public String mountWritable(String desiredLocation, IWritableMount mount) {
			return "null";
		}

		@Override
		public String mountWritable(String desiredLocation,
				IWritableMount mount, String driveName) {
			return "null";
		}

		@Override
		public void unmount(String location) {

		}

		@Override
		public int getID() {
			return -1;
		}

		@Override
		public void queueEvent(String event, Object[] arguments) {
			for(IComputerAccess c : computers){
				c.queueEvent(event, arguments);
			}
		}

		@Override
		public String getAttachmentName() {
			return "WirelessPeripheral";
		}

	};
	@Override
	public String getType() {
		return this.pName;
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
				throw new LuaException("There is no valid device linked to the Peripheral");
			}
		}else if(method == 2){
			if(this.current != null){
				return new Object[]{this.current.getType()};
			}
		}else if(method == 3){
			if(a.length > 2 && a[0] instanceof Double && a[1] instanceof Double && a[2] instanceof Double){
				int x = MathHelper.floor_double((Double) a[0]);
				int y = MathHelper.floor_double((Double) a[1]);
				int z = MathHelper.floor_double((Double) a[2]);
				String side = a.length > 3 ? a[3].toString() : "up";
				return new Object[]{this.link(x, y, z, EnumFacing.valueOf(side),new ExtraBlockHitInfo(0,0,0),worldObj.provider.getDimension())};
			}else{
				throw new LuaException("Invalid arguments, excepted (number,number,number)");
			}
		}else if(method == 4){
			if(a.length > 1 && a[0] instanceof String && a[1] instanceof String){
				String string = (String) a[0];
				String[] s = string.split((String) a[1]);
				Object[] o = new Object[s.length];
				for(int i = 0;i<o.length;i++){
					o[i] = s[i];
				}
				return o;
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
		return other == this;
	}
	@Override
	public boolean link(int x, int y, int z, EnumFacing side, ExtraBlockHitInfo bhp, int dim){
		return this.link(x, y, z, false, dim);
	}
	public boolean link(final int x, final int y, final int z, boolean s, int dim){
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		Block block = TomsModUtils.getBlockState(worldObj, new BlockPos(x, y, z), dim).getBlock();
		//System.out.println("Link"+block);
		if(s && this.current != null)this.current.detach(computerAccess);
		if(block instanceof IPeripheralProvider){
			IPeripheralProvider peripheralProvider = (IPeripheralProvider) block;
			IPeripheral p = peripheralProvider.getPeripheral(TomsModUtils.getWorld(dim), new BlockPos(x, y, z), this.linkedSide);
			if(p == null) return false;
			else{
				if(!s){
					this.current = p;
					p.attach(computerAccess);
					this.posX = x;
					this.posY = y;
					this.posZ = z;
					this.linked = true;
				}
				return true;
			}
		}else if(block == CoreInit.TabletAccessPoint || block == CoreInit.ControllerBox){
			if(!s){
				TileEntityTabletAccessPointBase tilee = (TileEntityTabletAccessPointBase) TomsModUtils.getTileEntity(worldObj, new BlockPos(x, y, z), dim);
				tilee.link(xCoord, yCoord, zCoord);
				this.posX = x;
				this.posY = y;
				this.posZ = z;
				this.linked = true;
				final TileEntityTabletAccessPointBase te = tilee;
				this.current = new IPeripheral(){

					@Override
					public String getType() {
						return "TabletAcessPoint";
					}

					@Override
					public String[] getMethodNames() {
						return new String[]{"isActive","setActive","isConnected"};
					}

					@Override
					public Object[] callMethod(IComputerAccess computer,
							ILuaContext context, int method, Object[] a)
									throws LuaException, InterruptedException {
						if(method == 0){
							return new Object[]{te.active};
						}else if(method == 1){
							if(a.length > 0 && a[0] instanceof Boolean){
								te.active = (Boolean) a[0];
							}else{
								throw new LuaException("Invalid argument #1 boolean excepted");
							}
						}else if(method == 2){
							return new Object[]{te.connected};
						}
						return null;
					}

					@Override
					public void attach(IComputerAccess computer) {

					}

					@Override
					public void detach(IComputerAccess computer) {

					}

					@Override
					public boolean equals(IPeripheral other) {
						return false;
					}
				};
			}
			return true;
		}else if(block == CoreInit.Camera){
			TileEntityCamera tilee = (TileEntityCamera) TomsModUtils.getTileEntity(worldObj, new BlockPos(x, y, z),dim);
			tilee.link(xCoord, yCoord, zCoord);
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			this.linked = true;
			final TileEntityCamera te = tilee;
			this.current = new IPeripheral(){

				@Override
				public String getType() {
					return "tm_camera";
				}

				@Override
				public String[] getMethodNames() {
					return new String[]{"setLook","setDisabled","getDisabled","setPos","getPos","setRot","getRot"};
				}

				@Override
				public Object[] callMethod(IComputerAccess computer,
						ILuaContext context, int method, Object[] a)
								throws LuaException, InterruptedException {
					if(method == 0){
						if(a.length > 0 && a[0] instanceof String){
							String pName = (String) a[0];
							EntityPlayer player = te.getPlayer(pName);
							if(player != null){
								boolean mode = a.length > 1 ? (a[1] instanceof Boolean ? (Boolean) a[1] : false) : true;
								boolean eC = a.length > 2 ? (a[2] instanceof Boolean ? (Boolean) a[2] : true) : true;
								boolean eEsc = a.length > 3 ? (a[3] instanceof Boolean ? (Boolean) a[3] : true) : true;
								te.connectPlayer(player, mode,eC,eEsc);
							}
						}
					}else if(method == 1){
						boolean o = te.disabled;
						te.disabled = a.length > 0 ? (a[0] instanceof Boolean ? (Boolean) a[0] : false) : true;
						markBlockForUpdate(new BlockPos(x, y, z));
						return new Object[]{o};
					}else if(method == 2){
						return new Object[]{te.disabled};
					}else if(method == 3){
						if(a.length > 2 && a[0] instanceof Double && a[1] instanceof Double && a[2] instanceof Double){
							te.camPosX = (Double) a[0];
							te.camPosY = (Double) a[1];
							te.camPosZ = (Double) a[2];
							te.isRelativeCoord = a.length > 3 ? (a[3] instanceof Boolean ? (Boolean) a[3] : true) : true;
						}else{
							te.camPosX = 0;
							te.camPosY = 0;
							te.camPosZ = 0;
							te.isRelativeCoord = true;
						}
						//worldObj.markBlockForUpdate(x, y, z);
					}else if(method == 4){
						return new Object[]{te.camPosX,te.camPosY,te.camPosZ, te.getPos().getX(),te.getPos().getY(),te.getPos().getZ()};
					}else if(method == 5){
						if(a.length > 1 && a[0] instanceof Double && a[1] instanceof Double){
							te.yaw = new Float((Double) a[0]);
							te.pitch = new Float((Double) a[1]);
							if(a.length > 3 && a[2] instanceof Double && a[3] instanceof Double){
								te.yawMin = new Float((Double) a[2]);
								te.pitchMin = new Float((Double) a[3]);
								if(a.length > 5 && a[4] instanceof Double && a[5] instanceof Double){
									te.yawMax = new Float((Double) a[4]);
									te.pitchMax = new Float((Double) a[5]);
								}
							}
							//worldObj.markBlockForUpdate(x, y, z);
						}else{
						}
					}else if(method == 6){
						return new Object[]{te.yaw,te.pitch,te.yawMin,te.pitchMin,te.yawMax,te.pitchMax};
					}
					return null;
				}

				@Override
				public void attach(IComputerAccess computer) {

				}

				@Override
				public void detach(IComputerAccess computer) {

				}

				@Override
				public boolean equals(IPeripheral other) {
					return false;
				}

			};
			return true;
		}else if(block == CoreInit.MagCardReader){
			TileEntityMagCardReader tilee = (TileEntityMagCardReader) TomsModUtils.getTileEntity(worldObj, new BlockPos(x, y, z),dim);
			tilee.link(xCoord, yCoord, zCoord);
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			this.linked = true;
			final TileEntityMagCardReader te = tilee;
			this.current = new IPeripheral(){

				@Override
				public String getType() {
					return "tm_mag_card_device";
				}

				@Override
				public String[] getMethodNames() {
					return new String[]{"isCodeMode","setCodeMode","addCode","removeCode","containsCode"};
				}

				@Override
				public Object[] callMethod(IComputerAccess computer, ILuaContext context,
						int method, Object[] a) throws LuaException,
				InterruptedException {
					if(method == 0){
						return new Object[]{te.isCodeMode};
					}else if(method == 1){
						te.isCodeMode = a.length > 0 ? (a[0] instanceof Boolean ? (Boolean) a[0] : true) : false;
					}else if(method == 2){
						if(a.length > 0 && a[0] instanceof String){
							te.code.add((String) a[0]);
						}
					}else if(method == 3){
						if(a.length > 0 && a[0] instanceof String){
							te.code.remove(a[0]);
						}
					}else if(method == 4){
						if(a.length > 0 && a[0] instanceof String){
							return new Object[]{te.code.contains(a[0])};
						}
					}
					return null;
				}
				@Override
				public void attach(IComputerAccess computer) {

				}

				@Override
				public void detach(IComputerAccess computer) {

				}

				@Override
				public boolean equals(IPeripheral other) {
					return false;
				}

			};
			return true;
		}else return false;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.posX = tag.getInteger("linkX");
		this.posY = tag.getInteger("linkY");
		this.posZ = tag.getInteger("linkZ");
		this.linked = tag.getBoolean("linked");
		this.linkedSide = EnumFacing.VALUES[tag.getInteger("side")];
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("linkX", this.posX);
		tag.setInteger("linkY", this.posY);
		tag.setInteger("linkZ", this.posZ);
		tag.setBoolean("linked", this.linked);
		tag.setInteger("side", this.linkedSide.ordinal());
		return tag;
	}
	@Override
	public void updateEntity(){
		if(this.linked){
			/*Block block = worldObj.getBlock(posX, posY, posZ);
			if(block instanceof IPeripheralProvider){
				IPeripheralProvider peripheralProvider = (IPeripheralProvider) block;
				IPeripheral p = peripheralProvider.getPeripheral(worldObj, posX, posY, posZ, 0);
				if(p == null) p = peripheralProvider.getPeripheral(worldObj, posX, posY, posZ, 2);
				if(p == null){ this.linked = false; this.current = null;
				}else{
					this.current = p;
				}
			}else if(block == CoreInit.TabletAccessPoint || block == CoreInit.ControllerBox){

			}
			else this.current = null;*/
			this.linked = this.link(posX, posY, posZ, true, lDim);
		}else this.current = null;
		if(this.fs && !this.worldObj.isRemote){
			this.fs = false;
			if(this.current != null){
				this.current.attach(computerAccess);
			}
		}
	}
	@Override
	public void queueEvent(String event,Object[] args){
		Object[] a = new Object[args.length+1];
		for(int i = 0;i<args.length;i++){
			a[i+1] = args[i];
		}
		for(IComputerAccess c : computers){
			a[0] = c.getAttachmentName();
			c.queueEvent(event, a);
		}
	}
}
