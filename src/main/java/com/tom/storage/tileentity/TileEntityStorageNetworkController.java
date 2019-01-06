package com.tom.storage.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.tom.api.grid.GridEnergyStorage;
import com.tom.api.grid.StorageNetworkGrid;
import com.tom.api.grid.StorageNetworkGrid.IAdvRouterTile;
import com.tom.api.grid.StorageNetworkGrid.IControllerTile;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.client.ICustomModelledTileEntity;
import com.tom.lib.api.grid.IGrid;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridUpdateListener;
import com.tom.lib.api.grid.VirtualGridDevice;
import com.tom.storage.block.BlockStorageNetworkController;
import com.tom.storage.handler.NetworkState;
import com.tom.storage.handler.StorageData;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.IGridDeviceHostFacing;

public class TileEntityStorageNetworkController extends TileEntityTomsMod implements IControllerTile, ICustomModelledTileEntity, ICustomMultimeterInformation, IGridDeviceHostFacing {
	private GridEnergyStorage energy = new GridEnergyStorage(1000, 0);
	private boolean lastActive, active;
	public InventoryBasic inv = new InventoryBasic("", false, 3);
	private StorageData data = new StorageData();
	private ControllerState state = ControllerState.OFF;
	private int bootTimer = 0;
	private String cmd = "", cmdOld = "", msg = "";
	private String clientProgressbar = "";
	private boolean active2;
	private int reboot;
	private boolean runCmd;
	private int msgTimer;
	private List<IAdvRouterTile> extenders = new ArrayList<>();
	public class DenseChannelSource extends VirtualGridDevice<StorageNetworkGrid> implements IGridUpdateListener {
		private final EnumFacing side;
		public DenseChannelSource(EnumFacing side) {
			this.side = side;
			grid.getData().dataSupplier = TileEntityStorageNetworkController.this::getData;
		}
		@Override
		public BlockPos getPos2() {
			return pos;
		}

		@Override
		public World getWorld2() {
			return world;
		}

		@Override
		public boolean isConnected(EnumFacing side) {
			return this.side == side;
		}

		@Override
		public boolean isValidConnection(EnumFacing side) {
			return this.side == side;
		}

		@Override
		public boolean isValid() {
			return !isInvalid();
		}

		@Override
		public StorageNetworkGrid constructGrid() {
			return new StorageNetworkGrid(true);
		}
		@Override
		public void onGridReload() {
			grid.getData().dataSupplier = TileEntityStorageNetworkController.this::getData;
		}
		@Override
		public void onGridPostReload() {
		}
	}
	public class ChannelSource extends VirtualGridDevice<StorageNetworkGrid> implements IGridUpdateListener {
		private final EnumFacing side;
		public ChannelSource(EnumFacing side) {
			this.side = side;
			grid.getData().dataSupplier = TileEntityStorageNetworkController.this::getData;
		}
		@Override
		public BlockPos getPos2() {
			return pos;
		}

		@Override
		public World getWorld2() {
			return world;
		}

		@Override
		public boolean isConnected(EnumFacing side) {
			return this.side == side;
		}

		@Override
		public boolean isValidConnection(EnumFacing side) {
			return this.side == side;
		}

		@Override
		public boolean isValid() {
			return !isInvalid();
		}

		@Override
		public StorageNetworkGrid constructGrid() {
			return new StorageNetworkGrid();
		}
		@Override
		public void onGridReload() {
			grid.getData().dataSupplier = TileEntityStorageNetworkController.this::getData;
		}
		@Override
		public void onGridPostReload() {
		}
	}

	private ChannelSource[] routerFaces;
	private DenseChannelSource[] routerFacesD;

	public TileEntityStorageNetworkController() {
		routerFaces = new ChannelSource[5];
		routerFacesD = new DenseChannelSource[5];
		for (int i = 0;i < routerFaces.length;i++) {
			routerFaces[i] = new ChannelSource(EnumFacing.getFront(i));
			routerFacesD[i] = new DenseChannelSource(EnumFacing.getFront(i));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (int i = 0;i < routerFaces.length;i++) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("id", i);
			NBTTagCompound tagn = new NBTTagCompound();
			routerFaces[i].writeToNBT(tagn);
			NBTTagCompound tagd = new NBTTagCompound();
			routerFacesD[i].writeToNBT(tagd);
			tag.setTag("n", tagn);
			tag.setTag("d", tagd);
			list.appendTag(tag);
		}
		compound.setTag("data", list);
		list = new NBTTagList();
		for (int i = 0;i < inv.getSizeInventory();++i) {
			if (inv.getStackInSlot(i) != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				inv.getStackInSlot(i).writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		compound.setTag("inventory", list);
		compound.setInteger("state", state.ordinal());
		compound.setString("command", cmd);
		compound.setBoolean("active", active2);
		energy.writeToNBT(compound);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList list = compound.getTagList("data", 10);
		for (int i = 0;i < list.tagCount();i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			NBTTagCompound tagn = tag.getCompoundTag("n");
			NBTTagCompound tagd = tag.getCompoundTag("d");
			routerFaces[tag.getInteger("id")].readFromNBT(tagn);
			routerFacesD[tag.getInteger("id")].readFromNBT(tagd);
		}
		state = ControllerState.VALUES[compound.getInteger("state")];
		list = compound.getTagList("inventory", 10);
		TomsModUtils.loadAllItems(list, inv);
		cmd = compound.getString("command");
		active2 = compound.getBoolean("active");
		energy.readFromNBT(compound);
	}

	@Override
	public StorageData getData() {
		return data;
	}

	private void checkConnections(BlockPos pos, Stack<IAdvRouterTile> traversingStorages) {
		for (EnumFacing f : EnumFacing.VALUES) {
			TileEntity tile = world.getTileEntity(pos.offset(f));
			if (tile instanceof IAdvRouterTile) {
				traversingStorages.add((IAdvRouterTile) tile);
			} else if (tile instanceof IControllerTile) {
				if (!data.controllers.contains(tile)) {
					data.controllers.add((IControllerTile) tile);
					checkConnections(tile.getPos(), traversingStorages);
				}
			}
		}
	}

	@Override
	public void updateEntity(IBlockState currentState) {
		data.update();
		if (!world.isRemote) {
			data.addEnergyStorage(energy);
			if (world.getTotalWorldTime() % 5 == 0) {
				extenders.forEach(e -> e.setController(null));
				extenders.clear();
				Stack<IAdvRouterTile> traversingStorages = new Stack<>();
				for (EnumFacing f : EnumFacing.VALUES) {
					if (f != EnumFacing.UP) {
						TileEntity tile = world.getTileEntity(pos.offset(f));
						if (tile instanceof IAdvRouterTile) {
							traversingStorages.add((IAdvRouterTile) tile);
						}
					}
				}
				while (!traversingStorages.isEmpty()) {
					IAdvRouterTile storage = traversingStorages.pop();
					if (storage != null && storage.isValid()) {
						if (!extenders.contains(storage)) {
							extenders.add(storage);
							storage.setController(this);
							checkConnections(storage.getPos2(), traversingStorages);
						}
					}
				}
			}
			if (reboot > 0) {
				reboot--;
				if (reboot == 0) {
					active2 = true;
				}
			}
			boolean active = isActive();
			boolean sendUpdate = false;
			if (active)
				data.extractEnergy(6, false);
			if (!data.controllers.contains(this))
				data.controllers.add(this);
			ControllerState stateOld = state;
			if (active && data.controllers.size() > 1) {
				state = ControllerState.ERROR_CONFLICT;
				if (world.getTotalWorldTime() % 100 == 0) {
					data.removeEnergyStorage(energy);
					data = new StorageData();
					data.addEnergyStorage(energy);
					data.setActive(NetworkState.LOADING_CHANNELS);
					for (int i = 0;i < routerFaces.length;i++) {
						routerFaces[i].update();
						routerFacesD[i].update();
					}
				}
			} else if (stateOld.getState() < 2 && active) {
				bootTimer = 300;
				state = ControllerState.BOOTING;
				data.removeEnergyStorage(energy);
				data = new StorageData();
				data.addEnergyStorage(energy);
				for (int i = 0;i < routerFaces.length;i++) {
					routerFaces[i].update();
					routerFacesD[i].update();
				}
				sendUpdate = true;
			} else if (stateOld == ControllerState.BOOTING && active) {
				bootTimer--;
				if (bootTimer < 1) {
					state = ControllerState.ONLINE;
				}
			} else
				state = active ? ControllerState.ONLINE : ControllerState.OFF;
			if (state == ControllerState.ONLINE) {
				data.setActive(NetworkState.ACTIVE);
			} else if (state == ControllerState.BOOTING) {
				data.setActive(bootTimer < 180 ? NetworkState.LOADING_CHANNELS : NetworkState.POWERED_ONLY);
			} else {
				data.setActive(NetworkState.POWERED_ONLY);
			}
			if (state == ControllerState.ONLINE) {
				if (runCmd) {
					runCmd = false;
					msgTimer = 60;
					msg = runCmd();
					cmd = "";
					sendUpdate = true;
				}
			}
			if (msgTimer >= 1) {
				msgTimer--;
				if (msgTimer == 0) {
					sendUpdate = true;
					msg = "";
				}
			}
			if (sendUpdate || state != stateOld || (bootTimer > 0 && bootTimer % 23 == 0) || !cmdOld.equals(cmd))
				markBlockForUpdate();
			cmdOld = cmd;
			TomsModUtils.setBlockStateWithCondition(world, pos, currentState, BlockStorageNetworkController.STATE, state.getState());
		}
		for (int i = 0;i < routerFaces.length;i++) {
			routerFaces[i].update();
			routerFacesD[i].update();
		}
	}

	public void neighborUpdateGrid(EnumFacing side) {
		if (side != EnumFacing.UP){
			routerFaces[getIntFromSide(side)].neighborUpdateGrid(false);
			routerFacesD[getIntFromSide(side)].neighborUpdateGrid(false);
		}
	}

	private static int getIntFromSide(EnumFacing f) {
		return f == EnumFacing.DOWN ? 0 : f.ordinal() - 1;
	}

	private static EnumFacing getSideFromInt(int f) {
		return f == 0 ? EnumFacing.DOWN : EnumFacing.VALUES[f + 1];
	}

	public boolean isActive() {
		if (!active2)
			return false;
		boolean a = data.extractEnergy(6, true) == 6;
		if (lastActive && !a) {
			lastActive = false;
			return true;
		}
		if (active && !lastActive && !a) {
			active = false;
			return true;
		}
		return active = lastActive = a;
	}
	/*private boolean isActive2(){
		for(int i = 0;i<controllerFaces.length;i++){
			if(!controllerFaces[i].active)return false;
		}
		return true;
	}*/

	@Override
	public EnumFacing getFacing() {
		return world.getBlockState(pos).getValue(BlockStorageNetworkController.FACING);
	}

	public ControllerState getState() {
		return state;
	}

	public static enum ControllerState {
		OFF("", "", 0), BOOTING("Booting%%%", "", 2) {
			@Override
			public String getText1(long time, int length, String[] cmd) {
				long t = time % 12;
				return super.getText1(time, length, cmd).replace("%%%", (t > 4 ? t > 8 ? "..." : ".. " : ".  "));
			}

			@Override
			public String getText2(long time, int length, String[] cmd) {
				return fillString(cmd[1], length, time, false);
			}
		},
		ONLINE("System Online", "> @%", 2) {
			@Override
			public String getText2(long time, int length, String[] cmd) {
				if (cmd[2].isEmpty())
					return fillString(msg2.replace("%", cmd[0] + (time % 12 > 6 ? "_" : " ")), length, time, false);
				else
					return fillString(cmd[2], length, time, true);
			}
		},
		ERROR_CONFLICT("SYSTEM ERROR", "100: CONTROLLER CONFLICT", 1), ERROR_NO_DISK("SYSTEM ERROR", "101: NO BOOTABLE MEDIUM FOUND", 1), ERROR_MISSING_RAM("SYSTEM ERROR", "102: NO RAM FOUND", 1), ERROR_MISSING_CPU("SYSTEM ERROR", "103: NO CPU FOUND", 1), ERROR_MISSING_ROM("SYSTEM ERROR", "104: NO ROM FOUND", 1), ERROR_SYSTEM_OVERLOAD("SYSTEM ERROR", "105: CACHE OVERFLOW", 1),;
		protected final String msg1, msg2;
		private final int state;
		public static final ControllerState[] VALUES = values();

		private ControllerState(String msg, String msg2, int state) {
			this.state = state;
			this.msg1 = msg;
			this.msg2 = msg2;
		}

		public String getText1(long time, int length, String[] cmd) {
			return fillString(msg1, length, time, true);
		}

		public String getText2(long time, int length, String[] cmd) {
			return fillString(msg2, length, time, true);
		}

		public static String fillString(String in, int length, long time, boolean scroll) {
			int index = in.indexOf('@');
			if (in.length() == length) {
				if (index == -1)
					return in;
				return in.substring(0, index) + in.substring(index + 1, in.length()) + " ";
			}
			if (in.length() > length) {
				if (index > -1) {
					if (scroll) {
						String f = in.substring(0, index);
						String p = in.substring(index + 1, in.length());
						int charsP = p.length() + 2;
						int l = length - f.length();
						int c = (int) ((time / 10) % charsP);
						return f + (p + "  " + p.substring(0, l)).substring(c, c + l);
					} else {
						String f = in.substring(0, index);
						String p = in.substring(index + 1, in.length());
						int l = length - f.length();
						return f + p.substring(p.length() - l);
					}
				} else {
					int charsP = in.length() + 2;
					int c = scroll ? (int) ((time / 10) % charsP) : charsP - 1;
					return (in + "  " + in.substring(0, length)).substring(c, c + length);
				}
			}
			if (index > -1) {
				int toApply = length - in.length();
				StringBuilder b = new StringBuilder(in.substring(0, index) + in.substring(index + 1, in.length()) + " ");
				for (int i = 0;i < toApply;i++)
					b.append(" ");
				return b.toString();
			} else {
				int toApply = length - in.length();
				StringBuilder b = new StringBuilder(in);
				for (int i = 0;i < toApply;i++)
					b.append(" ");
				return b.toString();
			}
		}

		public int getState() {
			return state;
		}
	}

	@Override
	public void writeToPacket(NBTTagCompound tag) {
		tag.setInteger("s", state.ordinal());
		tag.setString("c", cmd);
		tag.setInteger("b", bootTimer);
		tag.setString("m", msg);
		tag.setBoolean("a", active2);
	}

	@Override
	public void readFromPacket(NBTTagCompound tag) {
		state = ControllerState.VALUES[tag.getInteger("s")];
		cmd = tag.getString("c");
		bootTimer = tag.getInteger("b");
		setClientProgressbar(bootTimer / 23);
		msg = tag.getString("m");
		active2 = tag.getBoolean("a");
	}

	@Override
	public World getWorld2() {
		return world;
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}

	@Override
	public boolean isValid() {
		return !isInvalid();
	}

	@Override
	public void markDirty2() {
		markDirty();
	}

	@Override
	public void receiveNBTPacket(EntityPlayer pl, NBTTagCompound message) {
		String cmdOld = cmd;
		cmd = message.getString("c");
		if (!cmdOld.equals(cmd))
			markBlockForUpdate();
		active2 = message.getBoolean("a");
		runCmd |= message.getBoolean("run");
	}

	public String[] getCmd() {
		return new String[]{cmd, clientProgressbar, msg};
	}

	public void setClientProgressbar(int p) {
		StringBuilder b = new StringBuilder();
		for (int i = 0;i < 13 - p;i++)
			b.append("\u2588");
		clientProgressbar = b.toString();
	}

	private String runCmd() {
		if (cmd.equals("reboot")) {
			this.active2 = false;
			reboot = 2;
			msgTimer = 0;
			return "";
		} else if (cmd.equals("shutdown")) {
			this.active2 = false;
			msgTimer = 0;
			return "";
		}
		return "Unknown command";
	}

	public boolean canType() {
		return state == ControllerState.ONLINE && msg.isEmpty();
	}

	public boolean isActiveByPlayer() {
		return active2;
	}

	public void setActiveByPlayer(boolean active) {
		this.active2 = active;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	@Override
	public void updateData(boolean load) {
		if (load)
			data.addEnergyStorage(energy);
		else
			data.removeEnergyStorage(energy);
	}

	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		list.add(new TextComponentTranslation("tomsMod.chat.energyStored", new TextComponentString("Unit").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)), data.getEnergyStored(), data.getMaxEnergyStored()));
		return list;
	}

	@Override
	public BlockPos getSecurityStationPos() {
		return null;
	}

	@Override
	public void setData(StorageData data) {
	}
	@SuppressWarnings("unchecked")
	@Override
	public <G extends IGrid<?, G>> IGridDevice<G> getDevice(EnumFacing facing, Class<G> gridClass, Object... objects) {
		if(facing == EnumFacing.UP)return null;
		boolean dense = objects != null && objects.length > 0;
		if(gridClass == StorageNetworkGrid.class && facing != null){
			if(dense)
				return (IGridDevice<G>) routerFacesD[getIntFromSide(facing)];
			else
				return (IGridDevice<G>) routerFaces[getIntFromSide(facing)];
		}
		return null;
	}
}
