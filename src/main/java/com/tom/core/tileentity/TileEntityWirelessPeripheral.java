package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import com.tom.api.tileentity.ILinkable;
import com.tom.api.tileentity.IWirelessPeripheralController;
import com.tom.api.tileentity.TileEntityTabletAccessPointBase;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;
import com.tom.lib.api.CapabilityPeripheral;
import com.tom.lib.api.tileentity.ITMPeripheral.ITMCompatPeripheral;
import com.tom.util.ExtraBlockHitInfo;
import com.tom.util.TomsModUtils;

public class TileEntityWirelessPeripheral extends TileEntityTomsMod implements ITMCompatPeripheral, ILinkable, IWirelessPeripheralController {
	private List<IComputer> computers = new ArrayList<>();
	public String pName = "tm_wirelessPeripheral";
	public String[] methods = {"listMethods", "call", "getName", "link", "splitString"};
	private ITMPeripheralCap current;
	public int posX = 0;
	public int posY = 0;
	public int posZ = 0;
	public int lDim = 0;
	public EnumFacing linkedSide = EnumFacing.DOWN;
	public boolean linked = false;
	private boolean fs = true;
	protected IComputer computerAccess = new IComputer() {

		@Override
		public void queueEvent(String event, Object[] arguments) {
			for (IComputer c : computers) {
				c.queueEvent(event, arguments);
			}
		}

		@Override
		public String getAttachmentName() {
			return "WirelessPeripheral";
		}

		@Override
		public Object[] map(Object[] in) {
			return in;
		}

		@Override
		public Object[] pullEvent(String string) throws LuaException {
			return null;
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
	public Object[] callMethod(IComputer computer, int method, Object[] a) throws LuaException {
		if (method == 0) {
			Object[] o = new Object[methods.length];
			for (int i = 0;i < o.length;i++) {
				o[i] = methods[i];
			}
			return o;
		} else if (method == 1) {
			if (current != null) {
				if (a.length > 0 && a[0] instanceof String) {
					String m = (String) a[0];
					String[] methods = current.getMethodNames();
					if (m.equals("listMethods")) {
						Object[] o = new Object[methods.length];
						for (int i = 0;i < o.length;i++) {
							o[i] = methods[i];
						}
						return o;
					} else {
						for (int i = 0;i < methods.length;i++) {
							if (m.equals(methods[i])) {
								Object[] args = new Object[a.length - 1];
								for (int j = 0;j < args.length;j++) {
									args[j] = a[j + 1];
								}
								return current.call(computer, m, args);
							}
						}
						throw new LuaException("Method not found");
					}
				} else {
					throw new LuaException("Invalid argument 1, String excepted");
				}
			} else {
				throw new LuaException("There is no valid device linked to the Peripheral");
			}
		} else if (method == 2) {
			if (this.current != null) { return new Object[]{this.current.getType()}; }
		} else if (method == 3) {
			if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double && a[2] instanceof Double) {
				int x = MathHelper.floor((Double) a[0]);
				int y = MathHelper.floor((Double) a[1]);
				int z = MathHelper.floor((Double) a[2]);
				String side = a.length > 3 ? a[3].toString() : "up";
				return new Object[]{this.link(x, y, z, EnumFacing.valueOf(side), new ExtraBlockHitInfo(0, 0, 0), world.provider.getDimension())};
			} else {
				throw new LuaException("Invalid arguments, excepted (number,number,number)");
			}
		} else if (method == 4) {
			if (a.length > 1 && a[0] instanceof String && a[1] instanceof String) {
				String string = (String) a[0];
				String[] s = string.split((String) a[1]);
				Object[] o = new Object[s.length];
				for (int i = 0;i < o.length;i++) {
					o[i] = s[i];
				}
				return o;
			}
		}
		return null;
	}

	@Override
	public void attach(IComputer computer) {
		computers.add(computer);
	}

	@Override
	public void detach(IComputer computer) {
		computers.remove(computer);
	}

	@Override
	public boolean link(int x, int y, int z, EnumFacing side, ExtraBlockHitInfo bhp, int dim) {
		return this.link(x, y, z, false, dim);
	}

	public boolean link(final int x, final int y, final int z, boolean s, int dim) {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		Block block = TomsModUtils.getBlockState(world, new BlockPos(x, y, z), dim).getBlock();
		// System.out.println("Link"+block);
		if (s && this.current != null)
			this.current.detach(computerAccess);
		TileEntity tile = TomsModUtils.getTileEntity(world, new BlockPos(x, y, z), dim);
		if(tile != null && tile.hasCapability(CapabilityPeripheral.PERIPHERAL, this.linkedSide)){
			ITMPeripheralCap p = tile.getCapability(CapabilityPeripheral.PERIPHERAL, this.linkedSide);
			if (p == null)
				return false;
			else {
				if (!s) {
					this.current = p;
					p.attach(computerAccess);
					this.posX = x;
					this.posY = y;
					this.posZ = z;
					this.linked = true;
				}
				return true;
			}
		} else if (block == CoreInit.TabletAccessPoint || block == CoreInit.ControllerBox) {
			if (!s) {
				TileEntityTabletAccessPointBase tilee = (TileEntityTabletAccessPointBase) tile;
				tilee.link(xCoord, yCoord, zCoord);
				this.posX = x;
				this.posY = y;
				this.posZ = z;
				this.linked = true;
				final TileEntityTabletAccessPointBase te = tilee;
				this.current = new TMPeripheralCap(new ITMCompatPeripheral() {

					@Override
					public String getType() {
						return "TabletAcessPoint";
					}

					@Override
					public String[] getMethodNames() {
						return new String[]{"isActive", "setActive", "isConnected"};
					}

					@Override
					public Object[] callMethod(IComputer computer, int method, Object[] a) throws LuaException {
						if (method == 0) {
							return new Object[]{te.active};
						} else if (method == 1) {
							if (a.length > 0 && a[0] instanceof Boolean) {
								te.active = (Boolean) a[0];
							} else {
								throw new LuaException("Invalid argument #1 boolean excepted");
							}
						} else if (method == 2) { return new Object[]{te.connected}; }
						return null;
					}
				});
			}
			return true;
		} else if (block == CoreInit.Camera) {
			TileEntityCamera tilee = (TileEntityCamera) tile;
			tilee.link(xCoord, yCoord, zCoord);
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			this.linked = true;
			final TileEntityCamera te = tilee;
			this.current = new TMPeripheralCap(new ITMCompatPeripheral() {

				@Override
				public String getType() {
					return "tm_camera";
				}

				@Override
				public String[] getMethodNames() {
					return new String[]{"setLook", "setDisabled", "getDisabled", "setPos", "getPos", "setRot", "getRot"};
				}

				@Override
				public Object[] callMethod(IComputer computer, int method, Object[] a) throws LuaException {
					if (method == 0) {
						if (a.length > 0 && a[0] instanceof String) {
							String pName = (String) a[0];
							EntityPlayer player = te.getPlayer(pName);
							if (player != null) {
								boolean mode = a.length > 1 ? (a[1] instanceof Boolean ? (Boolean) a[1] : false) : true;
								boolean eC = a.length > 2 ? (a[2] instanceof Boolean ? (Boolean) a[2] : true) : true;
								boolean eEsc = a.length > 3 ? (a[3] instanceof Boolean ? (Boolean) a[3] : true) : true;
								te.connectPlayer(player, mode, eC, eEsc);
							}
						}
					} else if (method == 1) {
						boolean o = te.disabled;
						te.disabled = a.length > 0 ? (a[0] instanceof Boolean ? (Boolean) a[0] : false) : true;
						markBlockForUpdate(new BlockPos(x, y, z));
						return new Object[]{o};
					} else if (method == 2) {
						return new Object[]{te.disabled};
					} else if (method == 3) {
						if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double && a[2] instanceof Double) {
							te.camPosX = (Double) a[0];
							te.camPosY = (Double) a[1];
							te.camPosZ = (Double) a[2];
							te.isRelativeCoord = a.length > 3 ? (a[3] instanceof Boolean ? (Boolean) a[3] : true) : true;
						} else {
							te.camPosX = 0;
							te.camPosY = 0;
							te.camPosZ = 0;
							te.isRelativeCoord = true;
						}
						// worldObj.markBlockForUpdate(x, y, z);
					} else if (method == 4) {
						return new Object[]{te.camPosX, te.camPosY, te.camPosZ, te.getPos().getX(), te.getPos().getY(), te.getPos().getZ()};
					} else if (method == 5) {
						if (a.length > 1 && a[0] instanceof Double && a[1] instanceof Double) {
							te.yaw = new Float((Double) a[0]);
							te.pitch = new Float((Double) a[1]);
							if (a.length > 3 && a[2] instanceof Double && a[3] instanceof Double) {
								te.yawMin = new Float((Double) a[2]);
								te.pitchMin = new Float((Double) a[3]);
								if (a.length > 5 && a[4] instanceof Double && a[5] instanceof Double) {
									te.yawMax = new Float((Double) a[4]);
									te.pitchMax = new Float((Double) a[5]);
								}
							}
							// worldObj.markBlockForUpdate(x, y, z);
						} else {
						}
					} else if (method == 6) { return new Object[]{te.yaw, te.pitch, te.yawMin, te.pitchMin, te.yawMax, te.pitchMax}; }
					return null;
				}

			});
			return true;
		} else if (block == CoreInit.MagCardReader) {
			TileEntityMagCardReader tilee = (TileEntityMagCardReader) tile;
			tilee.link(xCoord, yCoord, zCoord);
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			this.linked = true;
			final TileEntityMagCardReader te = tilee;
			this.current = new TMPeripheralCap(new ITMCompatPeripheral() {

				@Override
				public String getType() {
					return "tm_mag_card_device";
				}

				@Override
				public String[] getMethodNames() {
					return new String[]{"isCodeMode", "setCodeMode", "addCode", "removeCode", "containsCode"};
				}

				@Override
				public Object[] callMethod(IComputer computer, int method, Object[] a) throws LuaException {
					if (method == 0) {
						return new Object[]{te.isCodeMode};
					} else if (method == 1) {
						te.isCodeMode = a.length > 0 ? (a[0] instanceof Boolean ? (Boolean) a[0] : true) : false;
					} else if (method == 2) {
						if (a.length > 0 && a[0] instanceof String) {
							te.code.add((String) a[0]);
						}
					} else if (method == 3) {
						if (a.length > 0 && a[0] instanceof String) {
							te.code.remove(a[0]);
						}
					} else if (method == 4) {
						if (a.length > 0 && a[0] instanceof String) { return new Object[]{te.code.contains(a[0])}; }
					}
					return null;
				}

			});
			return true;
		} else
			return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.posX = tag.getInteger("linkX");
		this.posY = tag.getInteger("linkY");
		this.posZ = tag.getInteger("linkZ");
		this.linked = tag.getBoolean("linked");
		this.linkedSide = EnumFacing.VALUES[tag.getInteger("side")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("linkX", this.posX);
		tag.setInteger("linkY", this.posY);
		tag.setInteger("linkZ", this.posZ);
		tag.setBoolean("linked", this.linked);
		tag.setInteger("side", this.linkedSide.ordinal());
		return tag;
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			if (this.linked) {
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
			} else
				this.current = null;
			if (this.fs && !this.world.isRemote) {
				this.fs = false;
				if (this.current != null) {
					this.current.attach(computerAccess);
				}
			}
		}
	}

	@Override
	public void queueEvent(String event, Object[] args) {
		Object[] a = new Object[args.length + 1];
		for (int i = 0;i < args.length;i++) {
			a[i + 1] = args[i];
		}
		for (IComputer c : computers) {
			a[0] = c.getAttachmentName();
			c.queueEvent(event, a);
		}
	}
}
