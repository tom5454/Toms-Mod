package com.tom.api.tileentity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import com.tom.api.Capabilities;
import com.tom.api.ITileFluidHandler;
import com.tom.lib.api.CapabilityGridDeviceHost;
import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.EnergyType.EnergyHandlerNormal;
import com.tom.lib.api.energy.EnergyType.EnergyHandlerProvider;
import com.tom.lib.api.energy.EnergyType.EnergyHandlerReceiver;
import com.tom.lib.api.energy.IEnergyHandler;
import com.tom.lib.api.energy.IEnergyProvider;
import com.tom.lib.api.energy.IEnergyReceiver;
import com.tom.lib.api.energy.IEnergyStorageTile;
import com.tom.lib.api.energy.IRFMachine;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridDeviceHost;
import com.tom.lib.api.tileentity.ITMPeripheral;
import com.tom.lib.api.tileentity.ITMPeripheral.ISidedTMPeripheral;
import com.tom.lib.network.GuiSyncHandler.IPacketReceiver;

import com.tom.core.tileentity.IGridDeviceHostFacing;

public class TileEntityTomsModNoTicking extends TileEntity implements IPacketReceiver {
	@SuppressWarnings("rawtypes")
	protected Map<Capability, Map<EnumFacing, Supplier<Object>>> capabilityMap = new HashMap<>();
	protected boolean /*added = false, */ initLater = false;

	public TileEntityTomsModNoTicking() {
		// Initialize capabilities.
		try {
			initializeCapabilities();
		} catch (Throwable e) {
			initLater = true;
		}
	}

	protected final void initializeCapabilities() {
		capabilityMap = new HashMap<>();
		if (this instanceof IInventory && canHaveInventory(null)) {
			EnumMap<EnumFacing, Supplier<Object>> itemHandlerSidedMap;
			capabilityMap.put(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandlerSidedMap = new EnumMap<>(EnumFacing.class));
			if (this instanceof ISidedInventory) {
				ISidedInventory thisInv = (ISidedInventory) this;
				for (EnumFacing f : EnumFacing.VALUES) {
					if (canHaveInventory(f)){
						Object o = new SidedInvWrapper(thisInv, f);
						itemHandlerSidedMap.put(f, () -> o);
					}
				}
			} else {
				InvWrapper thisInv = new InvWrapper((IInventory) this);
				for (EnumFacing f : EnumFacing.VALUES) {
					if (canHaveInventory(f))
						itemHandlerSidedMap.put(f, () -> thisInv);
				}
			}
		}
		if (this instanceof IEnergyStorageTile && canHaveEnergyHandler(null)) {
			EnumMap<EnumFacing, Supplier<Object>> energyHandlerMap;
			capabilityMap.put(EnergyType.ENERGY_HANDLER_CAPABILITY, energyHandlerMap = new EnumMap<>(EnumFacing.class));
			if (this instanceof IEnergyHandler) {
				IEnergyHandler thisHandler = (IEnergyHandler) this;
				for (EnumFacing f : EnumFacing.VALUES) {
					if (canHaveEnergyHandler(f)){
						Object o = new EnergyHandlerNormal(thisHandler, f);
						energyHandlerMap.put(f, () -> o);
					}
				}
			} else if (this instanceof IEnergyReceiver) {
				IEnergyReceiver thisHandler = (IEnergyReceiver) this;
				for (EnumFacing f : EnumFacing.VALUES) {
					if (canHaveEnergyHandler(f)){
						Object o = new EnergyHandlerReceiver(thisHandler, f);
						energyHandlerMap.put(f, () -> o);
					}
				}
			} else if (this instanceof IEnergyProvider) {
				IEnergyProvider thisHandler = (IEnergyProvider) this;
				for (EnumFacing f : EnumFacing.VALUES) {
					if (canHaveEnergyHandler(f)){
						Object o = new EnergyHandlerProvider(thisHandler, f);
						energyHandlerMap.put(f, () -> o);
					}
				}
			}
		}
		if (this instanceof ITileFluidHandler && canHaveFluidHandler(null)) {
			EnumMap<EnumFacing, Supplier<Object>> fluidHandlerMap;
			capabilityMap.put(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, fluidHandlerMap = new EnumMap<>(EnumFacing.class));
			ITileFluidHandler thisFluid = (ITileFluidHandler) this;
			for (EnumFacing f : EnumFacing.VALUES) {
				if (canHaveFluidHandler(f)){
					Object o = new FluidHandlerWrapper(f, thisFluid);
					fluidHandlerMap.put(f, () -> o);
				}
			}
		}
		if (this instanceof ISecuredTileEntity) {
			ISecuredTileEntity t = (ISecuredTileEntity) this;
			Map<EnumFacing, Supplier<Object>> map = new HashMap<>();
			map.put(null, () -> t);
			for (EnumFacing f : EnumFacing.VALUES) {
				map.put(f, () -> t);
			}
			capabilityMap.put(Capabilities.SECURED_TILE, map);
		}
		if (this instanceof IConfigurable) {
			IConfigurable t = (IConfigurable) this;
			Map<EnumFacing, Supplier<Object>> map = new HashMap<>();
			if (isConfigurableSide(null))
				map.put(null, () -> t);
			for (EnumFacing f : EnumFacing.VALUES) {
				if (isConfigurableSide(f))
					map.put(f, () -> t);
			}
			capabilityMap.put(Capabilities.CONFIGURABLE, map);
		}
		if (this instanceof IRFMachine) {
			capabilityMap.putAll(((IRFMachine) this).initCapabilities());
		}
		if (this instanceof IGridDeviceHostFacing && canHaveDefaultGridHandler()) {
			Map<EnumFacing, Supplier<Object>> map = new HashMap<>();
			IGridDeviceHostFacing h = (IGridDeviceHostFacing) this;
			for (EnumFacing f : EnumFacing.VALUES) {
				IGridDeviceHostFacing.Wrapper w = new IGridDeviceHostFacing.Wrapper(h, f);
				map.put(f, () -> w);
			}
			IGridDeviceHostFacing.Wrapper w = new IGridDeviceHostFacing.Wrapper(h, null);
			map.put(null, () -> w);
			capabilityMap.put(CapabilityGridDeviceHost.GRID_DEVICE_HOST, map);
		}else if (this instanceof IGridDeviceHost && canHaveDefaultGridHandler()) {
			Map<EnumFacing, Supplier<Object>> map = new HashMap<>();
			IGridDeviceHost h = (IGridDeviceHost) this;
			for (EnumFacing f : EnumFacing.VALUES) {
				map.put(f, () -> h);
			}
			map.put(null, () -> h);
			capabilityMap.put(CapabilityGridDeviceHost.GRID_DEVICE_HOST, map);
		}else if (this instanceof IGridDevice && canHaveDefaultGridHandler()) {
			Map<EnumFacing, Supplier<Object>> map = new HashMap<>();
			for (EnumFacing f : EnumFacing.VALUES) {
				CapabilityGridDeviceHost.Wrapper wr = new CapabilityGridDeviceHost.Wrapper((IGridDevice<?>) this, f);
				map.put(f, () -> wr);
			}
			CapabilityGridDeviceHost.Wrapper wr = new CapabilityGridDeviceHost.Wrapper((IGridDevice<?>) this, null);
			map.put(null, () -> wr);
			capabilityMap.put(CapabilityGridDeviceHost.GRID_DEVICE_HOST, map);
		}
		if(this instanceof ITMPeripheral){
			ITMPeripheral.Handler.initCapabilities(capabilityMap, (ITMPeripheral) this);
		}
		if(this instanceof ISidedTMPeripheral){
			ITMPeripheral.Handler.initCapabilitiesSided(capabilityMap, (ISidedTMPeripheral) this);
		}
		initializeCapabilitiesI();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (initLater) {
			try {
				initializeCapabilities();
				initLater = false;
			} catch (Throwable e) {
				initLater = true;
			}
		}
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
	public void handleUpdateTag(final NBTTagCompound tag) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {

			@Override
			public void run() {
				readFromPacket(tag);
			}
		});
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		writeToPacket(tag);
		return tag;
	}

	public void writeToPacket(NBTTagCompound tag) {
	}

	public void readFromPacket(NBTTagCompound tag) {
	}

	public boolean canHaveInventory(EnumFacing f) {
		return true;
	}

	public boolean canHaveFluidHandler(EnumFacing f) {
		return true;
	}

	public boolean canHaveEnergyHandler(EnumFacing f) {
		return true;
	}
	public boolean canHaveDefaultGridHandler() {
		return true;
	}

	protected void initializeCapabilitiesI() {
	}

	protected boolean isConfigurableSide(EnumFacing f) {
		return true;
	}

	public final void markBlockForUpdate(BlockPos pos) {
		markBlockForUpdate(world, pos);
		// markDirty();
	}

	public static final void markBlockForUpdate(World world, BlockPos pos) {
		if (world == null || pos == null)
			return;
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 2);
		if (world instanceof WorldServer) {
			((WorldServer) world).getPlayerChunkMap().markBlockForUpdate(pos);
		}
	}

	public final void markBlockForUpdate() {
		markBlockForUpdate(pos);
	}

	@Override
	public final <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capabilityMap.containsKey(capability)) { return this.<T>getInstance(capabilityMap.get(capability), facing, capability); }
		return getCapabilityI(capability, facing);
	}

	public <T> T getCapabilityI(Capability<T> capability, EnumFacing facing) {
		return super.getCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	protected final <T> T getInstance(Map<EnumFacing, Supplier<Object>> in, EnumFacing f, Capability<T> capability) {
		try {
			return (T) (in.get(f).get());
		} catch (ClassCastException e) {
		}
		return getCapabilityI(capability, f);
	}

	@Override
	public final boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		Map<EnumFacing, ?> m = capabilityMap.get(capability);
		return (m != null && m.get(facing) != null) || hasCapabilityI(capability, facing);
	}

	public boolean hasCapabilityI(Capability<?> capability, EnumFacing facing) {
		return super.hasCapability(capability, facing);
	}

	@Override
	public void receiveNBTPacket(EntityPlayer from, NBTTagCompound message) {
		System.err.println(getClass() + ".receiveNBTPacket()");
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		System.err.println(getClass() + ".buttonPressed()");
	}
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getClass().getSimpleName());
	}
}
