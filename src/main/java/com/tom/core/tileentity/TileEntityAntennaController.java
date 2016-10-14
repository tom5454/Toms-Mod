package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.tileentity.TileEntityAntennaBase;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.Configs;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityAntennaController extends TileEntityTomsMod implements IPeripheral {
	/*private TileEntityAntennaController cu = this;
	protected IGridBlock gridBlock = new IGridBlock(){

		@Override
		public double getIdlePowerUsage() {
			return 12.0;
		}

		@Override
		public EnumSet<GridFlags> getFlags() {
			EnumSet<GridFlags> r = EnumSet.of(GridFlags.REQUIRE_CHANNEL);
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
		public EnumSet<ForgeDirection> getConnectableSides() {
			return EnumSet.allOf(ForgeDirection.class);
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
			return new ItemStack(CoreInit.AntennaController);
		}
		
	};
	private boolean isReady = true;
	private IGridNode node = null;*/
	private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	private List<TileEntityAntennaBase> antennas = new ArrayList<TileEntityAntennaBase>();
	/*@Override
	public IGridNode getGridNode(ForgeDirection dir) {
		return AEApi.instance().createGridNode(this.gridBlock);
	}

	@Override
	public AECableType getCableConnectionType(ForgeDirection dir) {
		return AECableType.SMART;
	}

	@Override
	public void securityBreak() {

	}

	@Override
	public String getType() {
		return "antennaController";
	}

	@Override
	public String[] getMethodNames() {
		return null;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		return null;
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
		return false;
	}

	@Override
	public void onUpdateTick() {

	}

	@Override
	public void removeNode(IGridNode gridNode, IGridHost machine) {
		if(machine instanceof TileEntityAntenna){
			TileEntityAntenna ant = (TileEntityAntenna) machine;
			this.antennas.remove(ant);
		}
	}

	@Override
	public void addNode(IGridNode gridNode, IGridHost machine) {
		if(machine instanceof TileEntityAntenna){
			TileEntityAntenna ant = (TileEntityAntenna) machine;
			this.antennas.add(ant);
		}
	}

	@Override
	public void onSplit(IGridStorage destinationStorage) {

	}

	@Override
	public void onJoin(IGridStorage sourceStorage) {

	}

	@Override
	public void populateGridStorage(IGridStorage destinationStorage) {

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
		IGrid grid = null;
		if( this.node == null )
		{
			grid = this.node.getGrid();	
		}
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
	}
	public void updateEntity(){
		if( this.node == null && !worldObj.isRemote && this.isReady ){
			this.node = AEApi.instance().createGridNode( this.gridBlock );
		}
	}*/
	public String[] methods = {"listMethods","sendMsg"};
	public void receive(String pName, Object msg){
		
	}
	@Override
	public String getType() {
		return "antennaController";
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
			if(a.length > 1 && a[0] instanceof String){
				String pName = (String) a[0];
				for(TileEntityAntennaBase ant : this.antennas){
					ant.sendMsg(pName, a[1]);
				}
			}
		}
		return null;
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
		return false;
	}
	public void link(TileEntityAntennaBase a){
		this.antennas.add(a);
	}
	public void disConnect(TileEntityAntennaBase te){
		this.antennas.remove(te);
	}
	public void queueEvent(String event, Object[] args){
		//System.out.println("queueEvent");
		for(IComputerAccess c : this.computers){
			c.queueEvent(event, args);
		}
	}
}
