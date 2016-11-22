package com.tom.api.tileentity;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import com.tom.api.ITileFluidHandler;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.EnergyType.EnergyHandlerNormal;
import com.tom.api.energy.EnergyType.EnergyHandlerProvider;
import com.tom.api.energy.EnergyType.EnergyHandlerReceiver;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.energy.IEnergyProvider;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.energy.IEnergyStorageHandler;
import com.tom.config.Config;
import com.tom.handler.EventHandler;

public class TileEntityTomsMod extends TileEntity implements ITickable {
	protected Map<EnumFacing, IItemHandler> itemHandlerSidedMap;
	protected Map<EnumFacing, IEnergyStorageHandler> energyHandlerMap;
	protected Map<EnumFacing, IFluidHandler> fluidHandlerMap;
	public boolean ticked = false;
	private boolean added = false, initLater = false;
	private int tickSpeedingTimer = 0;
	private boolean clientSpeeding;

	public TileEntityTomsMod() {
		//Initialize capabilities.
		try{
			initializeCapabilities();
		}catch(Exception e){
			initLater = true;
		}
	}
	protected final void initializeCapabilities(){
		itemHandlerSidedMap = new HashMap<EnumFacing, IItemHandler>();
		energyHandlerMap = new HashMap<EnumFacing, IEnergyStorageHandler>();
		fluidHandlerMap = new HashMap<EnumFacing, IFluidHandler>();
		if(this instanceof IInventory && canHaveInventory(null)){
			if(this instanceof ISidedInventory){
				ISidedInventory thisInv = (ISidedInventory) this;
				for(EnumFacing f : EnumFacing.VALUES){
					if(canHaveInventory(f))itemHandlerSidedMap.put(f, new SidedInvWrapper(thisInv, f));
				}
			}else{
				InvWrapper thisInv = new InvWrapper((IInventory) this);
				for(EnumFacing f : EnumFacing.VALUES){
					if(canHaveInventory(f))itemHandlerSidedMap.put(f, thisInv);
				}
			}
		}
		if(canHaveEnergyHandler(null)){
			if(this instanceof IEnergyHandler){
				IEnergyHandler thisHandler = (IEnergyHandler) this;
				for(EnumFacing f : EnumFacing.VALUES){
					if(canHaveEnergyHandler(f))energyHandlerMap.put(f, new EnergyHandlerNormal(thisHandler, f));
				}
			}else if(this instanceof IEnergyReceiver){
				IEnergyReceiver thisHandler = (IEnergyReceiver) this;
				for(EnumFacing f : EnumFacing.VALUES){
					if(canHaveEnergyHandler(f))energyHandlerMap.put(f, new EnergyHandlerReceiver(thisHandler, f));
				}
			}else if(this instanceof IEnergyProvider){
				IEnergyProvider thisHandler = (IEnergyProvider) this;
				for(EnumFacing f : EnumFacing.VALUES){
					if(canHaveEnergyHandler(f))energyHandlerMap.put(f, new EnergyHandlerProvider(thisHandler, f));
				}
			}
		}
		if(this instanceof ITileFluidHandler && canHaveFluidHandler(null)){
			ITileFluidHandler thisFluid = (ITileFluidHandler) this;
			for(EnumFacing f : EnumFacing.VALUES){
				if(canHaveFluidHandler(f))fluidHandlerMap.put(f, new FluidHandlerWrapper(f, thisFluid));
			}
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		initializeCapabilities();
	}
	@Override
	public final SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}
	@Override
	public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}
	@Override
	public final void handleUpdateTag(final NBTTagCompound tag) {
		if(worldObj.isRemote)Minecraft.getMinecraft().addScheduledTask(new Runnable() {

			@Override
			public void run() {
				clientSpeeding = tag.getBoolean("_ts");
				readFromPacket(tag);
			}
		});
	}
	@Override
	public final NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		writeToPacket(tag);
		tag.setBoolean("_ts", tickSpeedingTimer > 0);
		return tag;
	}

	public void writeToPacket(NBTTagCompound tag){}
	public void readFromPacket(NBTTagCompound tag){}
	public void updateEntity(){}
	public void updateEntity(IBlockState currentState){}
	public void preUpdate(IBlockState state){}
	public void postUpdate(IBlockState state){}
	public void onTicksped(){}
	public boolean canHaveInventory(EnumFacing f){return true;}
	public boolean canHaveFluidHandler(EnumFacing f){return true;}
	public boolean canHaveEnergyHandler(EnumFacing f){return true;}
	public TickSpeedupBehaviour getTickSpeedupBehaviour(){return TickSpeedupBehaviour.REQUIRES_CONFIG;}

	@Override
	public final void update() {
		if(!added){
			if(worldObj.isRemote)EventHandler.teList.addClient(this);
			else EventHandler.teList.add(this);
			added = true;
		}
		if(initLater){
			initLater = false;
			initializeCapabilities();
		}
		if(!ticked || getTickSpeedupBehaviour() == TickSpeedupBehaviour.NORMAL || (getTickSpeedupBehaviour() == TickSpeedupBehaviour.REQUIRES_CONFIG && Config.enableTickSpeeding)){
			ticked = true;
			IBlockState state = worldObj.getBlockState(pos);
			if(state.getBlock() != Blocks.AIR){
				preUpdate(state);
				this.updateEntity();
				this.updateEntity(state);
				postUpdate(state);
			}
			if(tickSpeedingTimer > 0){
				tickSpeedingTimer--;
				if(tickSpeedingTimer == 0)markBlockForUpdate();
			}
		}else{
			int old = tickSpeedingTimer;
			tickSpeedingTimer = 20;
			if(old == 0){
				markBlockForUpdate();
			}
			onTicksped();
		}
		if(worldObj.isRemote && (tickSpeedingTimer > 0 || clientSpeeding)){
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.25, pos.getY() + 1, pos.getZ() + 0.25, 0, 0.02F, 0);
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.25, pos.getY() + 1, pos.getZ() + 0.75, 0, 0.02F, 0);
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.75, pos.getY() + 1, pos.getZ() + 0.25, 0, 0.02F, 0);
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.75, pos.getY() + 1, pos.getZ() + 0.75, 0, 0.02F, 0);

			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0.05F, 0);
			double sideY = pos.getY() + 0.3;
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX(),     sideY, pos.getZ(),     0, 0.02F, 0);
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX(),     sideY, pos.getZ() + 1, 0, 0.02F, 0);
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 1, sideY, pos.getZ(),     0, 0.02F, 0);
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 1, sideY, pos.getZ() + 1, 0, 0.02F, 0);

			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX(),       sideY, pos.getZ() + 0.5, 0, 0.02F, 0);
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5, sideY, pos.getZ(), 0, 0.02F, 0);
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5, sideY, pos.getZ() + 1, 0, 0.02F, 0);
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 1,   sideY, pos.getZ() + 0.5, 0, 0.02F, 0);
		}
	}
	public final void markBlockForUpdate(BlockPos pos){
		markBlockForUpdate(worldObj, pos);
		//markDirty();
	}
	public static final void markBlockForUpdate(World world, BlockPos pos){
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		if(world instanceof WorldServer){
			((WorldServer)world).getPlayerChunkMap().markBlockForUpdate(pos);
		}
	}
	public final void markBlockForUpdate(){
		markBlockForUpdate(pos);
	}
	@Override
	public final <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return this.<T>getInstance(itemHandlerSidedMap, facing, capability);
		}
		if (capability == EnergyType.ENERGY_HANDLER_CAPABILITY){
			return this.<T>getInstance(energyHandlerMap, facing, capability);
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return this.<T>getInstance(fluidHandlerMap, facing, capability);
		}
		return super.getCapability(capability, facing);
	}
	@SuppressWarnings("unchecked")
	protected final <T> T getInstance(Map<EnumFacing, ?> in, EnumFacing f, Capability<T> capability){
		try{
			return (T) (f == null ? in.get(EnumFacing.DOWN) : in.get(f));
		}catch(ClassCastException e){}
		return super.getCapability(capability, f);
	}
	@Override
	public final boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandlerSidedMap.get(facing) != null) || (capability == EnergyType.ENERGY_HANDLER_CAPABILITY && energyHandlerMap.get(facing) != null) || (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fluidHandlerMap.get(facing) != null) || super.hasCapability(capability, facing);
	}
	@Override
	public final void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
	}
	public static enum TickSpeedupBehaviour{
		NORMAL, REQUIRES_CONFIG, DENY
	}
	public final boolean isTickSpeeded(){
		return tickSpeedingTimer > 0 || clientSpeeding;
	}
}
