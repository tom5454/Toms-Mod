package com.tom.api.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.apis.ExtraBlockHitInfo;
import com.tom.core.CoreInit;

import com.tom.core.block.AntennaController;

import com.tom.core.item.Tablet;

import com.tom.core.tileentity.TileEntityAntennaController;

public class TileEntityAntennaBase extends TileEntityTomsMod implements IEnergyReceiver, ILinkable, IReceivable {

	public List<EntityPlayer> players = new ArrayList<>();
	public boolean online = false;
	public boolean powered = false;
	private EnergyStorage energy = new EnergyStorage(200000, 1000, 1000);
	public int posX = 0;
	public int posY = 0;
	public int posZ = 0;
	public boolean linked = false;
	public boolean active = true;
	public boolean redstone = true;
	// public boolean connected = false;
	/*private TileEntityAntenna cu = this;
	protected IGridBlock gridBlock = new IGridBlock(){
	
		@Override
		public double getIdlePowerUsage() {
			return 8.0;
		}
	
		@Override
		public EnumSet<GridFlags> getFlags() {
			EnumSet<GridFlags> r = EnumSet.of(GridFlags.CANNOT_CARRY, GridFlags.REQUIRE_CHANNEL);
			return r;
		}
	
		@Override
		public boolean isWorldAccessible() {
			return true;
		}
	
		@Override
		public DimensionalCoord getLocation() {
			return new DimensionalCoord(cu);
		}
	
		@Override
		public AEColor getGridColor() {
			return AEColor.Transparent;
		}
	
		@Override
		public void onGridNotification(GridNotification notification) {
	
		}
	
		@Override
		public void setNetworkStatus(IGrid grid, int channelsInUse) {
	
		}
	
		@Override
		public EnumSet<EnumFacing> getConnectableSides() {
			return EnumSet.of(EnumFacing.DOWN);
		}
	
		@Override
		public IGridHost getMachine() {
			return cu;
		}
	
		@Override
		public void gridChanged() {
	
		}
	
		@Override
		public ItemStack getMachineRepresentation() {
			return new ItemStack(CoreInit.Antenna);
		}
	
	};
	private boolean isReady = true;
	private IGridNode node = null;*/
	// public boolean formed = true;
	private boolean firstStart = true;

	@Override
	public void updateEntity() {
		if (firstStart) {
			this.firstStart = false;
			TileEntity te = world.getTileEntity(new BlockPos(posX, posY, posZ));
			if (te instanceof TileEntityAntennaController) {
				((TileEntityAntennaController) te).link(this);
			}
		}
		this.redstone = !(world.isBlockIndirectlyGettingPowered(pos) > 0);
		/*if( this.node == null && !worldObj.isRemote && this.isReady ){
			this.node = AEApi.instance().createGridNode( this.gridBlock );
		}
		this.powered = this.isPowered();
		this.connected = this.isActive();
		if(this.node != null){
			IReadOnlyCollection<IGridConnection> connections = this.node.getConnections();
			for(IGridConnection c : connections){
				System.out.println(c.toString());
			}
		}
		IGrid grid = this.getGrid();
		if(grid != null){
			IGridCache c = grid.getCache(TileEntityAntennaController.class);
			if(c != null && c instanceof TileEntityAntennaController){
				TileEntityAntennaController te = (TileEntityAntennaController) c;
		
			}
		}*/
		boolean pow = this.powered;
		if (!world.isRemote) {
			this.powered = this.energy.getEnergyStored() > 100;
			if (this.powered != pow)
				markBlockForUpdate(pos);
			this.online = this.linked;
		}
		if (this.linked) {
			Block block = world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
			this.linked = block instanceof AntennaController;
			markBlockForUpdate(pos);
		}
		if (this.online && this.powered && this.redstone) {
			List<EntityPlayer> playersOld = new ArrayList<>(this.players);
			List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - 64, pos.getY() - 64, pos.getZ() - 64, pos.getX() + 65, pos.getY() + 65, pos.getZ() + 65));
			this.players.clear();
			this.energy.extractEnergy(12, false);
			for (Entity entity : entities) {
				if (entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					if (player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 64) {
						InventoryPlayer inv = player.inventory;
						for (int i = 0;i < inv.getSizeInventory();i++) {
							ItemStack item = inv.getStackInSlot(i);
							if (item != null && item.getItem() == CoreInit.Tablet) {
								Tablet tab = (Tablet) item.getItem();
								if (this.canConnect(player, item)) {
									boolean connect = tab.connect(player, world, pos.getX(), pos.getY(), pos.getZ(), 1, item);
									if (connect) {
										this.players.add(player);
										if (playersOld.contains(player)) {
											playersOld.remove(player);
										} else {
											this.queueEvent("TabletConnect", new Object[]{player.getName(), i});
										}
									}
								}
							}
						}
					}
				}
			}
			if (!playersOld.isEmpty()) {
				for (EntityPlayer p : playersOld) {
					this.queueEvent("TabletDisConnect", new Object[]{p.getName()});
				}
			}
		}
		/*this.connected = !this.players.isEmpty();
		if(!this.players.isEmpty()){ this.markDirty(); this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);}*/
	}

	/*public void update(){
		boolean f = false;
		TileEntity tilee1 = this.worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
		if(tilee1 instanceof TileEntityAntBase) {
			TileEntity tilee2 = this.worldObj.getTileEntity(xCoord, yCoord+2, zCoord);
			if(tilee2 instanceof TileEntityAntMid) {
				TileEntity tilee3 = this.worldObj.getTileEntity(xCoord, yCoord+3, zCoord);
				if(tilee3 instanceof TileEntityAntMid) {
					TileEntity tilee = this.worldObj.getTileEntity(xCoord, yCoord+4, zCoord);
					if(tilee instanceof TileEntityAntTop) {
						f = true;
					}
				}
			}
		}
		this.formed = f;
	}*/
	public void queueEvent(String e, Object[] a) {
		TileEntity te = world.getTileEntity(new BlockPos(posX, posY, posZ));
		// System.out.println("queueEvent:"+e);
		if (te instanceof TileEntityAntennaController) {
			((TileEntityAntennaController) te).queueEvent(e, a);
		}
	}

	@Override
	public void receiveMsg(String pName, Object msg) {
		if (this.active && this.linked) {
			TileEntityAntennaController te = (TileEntityAntennaController) world.getTileEntity(new BlockPos(posX, posY, posZ));
			te.queueEvent("antenna_receive", new Object[]{pName, msg});
		}
	}

	public void sendMsg(String pName, Object msg) {
		if (this.active && this.linked) {
			for (EntityPlayer player : this.players) {
				if (player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 64 && player.getName().equals(pName)) {
					InventoryPlayer inv = player.inventory;
					for (int i = 0;i < inv.getSizeInventory();i++) {
						ItemStack item = inv.getStackInSlot(i);
						if (item != null && item.getItem() == CoreInit.Tablet) {
							Tablet tab = (Tablet) item.getItem();
							tab.receive(world, msg, item, player);
						}
					}
				}
			}
		}
	}

	/*@Override
	public IGridNode getGridNode(EnumFacing dir) {
		return dir == EnumFacing.DOWN ? AEApi.instance().createGridNode(this.gridBlock) : null;
	}
	@Override
	public AECableType getCableConnectionType(EnumFacing dir) {
		return AECableType.SMART;
	}
	@Override
	public void securityBreak() {
		this.worldObj.getBlock(xCoord, yCoord, zCoord).breakBlock(worldObj, xCoord, yCoord, zCoord, blockType, blockMetadata);
	}
	public boolean isActive()
	{
		if( this.node == null )
		{
			return false;
		}
	
		return this.node.isActive();
	}
	
	public boolean isPowered()
	{
		IEnergyGrid eg = this.getEnergy();
		if(eg != null){
			return this.getEnergy().isNetworkPowered();
		}else return false;
	
	}
	
	public IEnergyGrid getEnergy()
	{
		final IGrid grid = this.getGrid();
		if( grid == null )
		{
			return null;
		}
		final IEnergyGrid eg = grid.getCache( IEnergyGrid.class );
		if( eg == null )
		{
	
		}
		return eg;
	
	}
	public IGrid getGrid()
	{
		if( this.node == null )
		{
			return null;
		}
		IGrid grid = this.node.getGrid();
		if( grid == null )
		{
		}
		return grid;
	}
	public void invalidate()
	{
		this.isReady = false;
		if( this.node != null )
		{
			this.node.destroy();
			this.node = null;
		}
	}
	
	public void onChunkUnload()
	{
		this.isReady = false;
		this.invalidate();
	}*/
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return from == EnumFacing.DOWN && type == HV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		this.markDirty();
		markBlockForUpdate(pos);
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
	public void writeToPacket(NBTTagCompound buf) {
		buf.setBoolean("p", this.powered);
		buf.setBoolean("o", this.online);
		buf.setBoolean("r", this.redstone);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf) {
		this.powered = buf.getBoolean("p");
		this.online = buf.getBoolean("o");
		this.redstone = buf.getBoolean("r");
		this.world.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy.readFromNBT(tag);
		this.posX = tag.getInteger("linkX");
		this.posY = tag.getInteger("linkY");
		this.posZ = tag.getInteger("linkZ");
		this.linked = tag.getBoolean("linked");
		this.redstone = tag.getBoolean("rs");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.energy.writeToNBT(tag);
		tag.setInteger("linkX", this.posX);
		tag.setInteger("linkY", this.posY);
		tag.setInteger("linkZ", this.posZ);
		tag.setBoolean("linked", this.linked);
		tag.setBoolean("rs", this.redstone);
		return tag;
	}

	@Override
	public boolean link(int x, int y, int z, EnumFacing side, ExtraBlockHitInfo bhp, int dim) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		// System.out.println(te);
		if (te instanceof TileEntityAntennaController) {
			((TileEntityAntennaController) te).link(this);
			this.linked = true;
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			this.markDirty();
			markBlockForUpdate(pos);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onChunkUnload() {
		TileEntity te = world.getTileEntity(new BlockPos(posX, posY, posZ));
		if (te instanceof TileEntityAntennaController) {
			((TileEntityAntennaController) te).disConnect(this);
		}
	}

	public boolean isActive() {
		return this.redstone && this.powered && this.linked && this.online;
	}

	public boolean canConnect(EntityPlayer player, ItemStack tabStack) {
		return true;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}
}
