package com.tom.api.tileentity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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

import com.tom.api.Capabilities;
import com.tom.api.ITileFluidHandler;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.EnergyType.EnergyHandlerNormal;
import com.tom.api.energy.EnergyType.EnergyHandlerProvider;
import com.tom.api.energy.EnergyType.EnergyHandlerReceiver;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.energy.IEnergyProvider;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.energy.IEnergyStorageHandler;
import com.tom.api.energy.IEnergyStorageTile;
import com.tom.api.energy.IRFMachine;
import com.tom.api.grid.IGridDevice;

public class TileEntityTomsModNoTicking extends TileEntity {
	@SuppressWarnings("rawtypes")
	protected Map<Capability, Map<EnumFacing, ?>> capabilityMap = new HashMap<>();
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
			EnumMap<EnumFacing, IItemHandler> itemHandlerSidedMap;
			capabilityMap.put(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandlerSidedMap = new EnumMap<>(EnumFacing.class));
			if (this instanceof ISidedInventory) {
				ISidedInventory thisInv = (ISidedInventory) this;
				for (EnumFacing f : EnumFacing.VALUES) {
					if (canHaveInventory(f))
						itemHandlerSidedMap.put(f, new SidedInvWrapper(thisInv, f));
				}
			} else {
				InvWrapper thisInv = new InvWrapper((IInventory) this);
				for (EnumFacing f : EnumFacing.VALUES) {
					if (canHaveInventory(f))
						itemHandlerSidedMap.put(f, thisInv);
				}
			}
		}
		if (this instanceof IEnergyStorageTile && canHaveEnergyHandler(null)) {
			EnumMap<EnumFacing, IEnergyStorageHandler> energyHandlerMap;
			capabilityMap.put(EnergyType.ENERGY_HANDLER_CAPABILITY, energyHandlerMap = new EnumMap<>(EnumFacing.class));
			if (this instanceof IEnergyHandler) {
				IEnergyHandler thisHandler = (IEnergyHandler) this;
				for (EnumFacing f : EnumFacing.VALUES) {
					if (canHaveEnergyHandler(f))
						energyHandlerMap.put(f, new EnergyHandlerNormal(thisHandler, f));
				}
			} else if (this instanceof IEnergyReceiver) {
				IEnergyReceiver thisHandler = (IEnergyReceiver) this;
				for (EnumFacing f : EnumFacing.VALUES) {
					if (canHaveEnergyHandler(f))
						energyHandlerMap.put(f, new EnergyHandlerReceiver(thisHandler, f));
				}
			} else if (this instanceof IEnergyProvider) {
				IEnergyProvider thisHandler = (IEnergyProvider) this;
				for (EnumFacing f : EnumFacing.VALUES) {
					if (canHaveEnergyHandler(f))
						energyHandlerMap.put(f, new EnergyHandlerProvider(thisHandler, f));
				}
			}
		}
		if (this instanceof ITileFluidHandler && canHaveFluidHandler(null)) {
			EnumMap<EnumFacing, IFluidHandler> fluidHandlerMap;
			capabilityMap.put(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, fluidHandlerMap = new EnumMap<>(EnumFacing.class));
			ITileFluidHandler thisFluid = (ITileFluidHandler) this;
			for (EnumFacing f : EnumFacing.VALUES) {
				if (canHaveFluidHandler(f))
					fluidHandlerMap.put(f, new FluidHandlerWrapper(f, thisFluid));
			}
		}
		if (this instanceof ISecuredTileEntity) {
			ISecuredTileEntity t = (ISecuredTileEntity) this;
			Map<EnumFacing, ISecuredTileEntity> map = new HashMap<>();
			map.put(null, t);
			for (EnumFacing f : EnumFacing.VALUES) {
				map.put(f, t);
			}
			capabilityMap.put(Capabilities.SECURED_TILE, map);
		}
		if (this instanceof IConfigurable) {
			IConfigurable t = (IConfigurable) this;
			Map<EnumFacing, IConfigurable> map = new HashMap<>();
			if (isConfigurableSide(null))
				map.put(null, t);
			for (EnumFacing f : EnumFacing.VALUES) {
				if (isConfigurableSide(f))
					map.put(f, t);
			}
			capabilityMap.put(Capabilities.CONFIGURABLE, map);
		}
		if (this instanceof IRFMachine) {
			capabilityMap.putAll(((IRFMachine) this).initCapabilities());
		}
		if (this instanceof IGridDevice) {
			Map<EnumFacing, IGridDevice<?>> map = new HashMap<>();
			IGridDevice<?> d = (IGridDevice<?>) this;
			for (EnumFacing f : EnumFacing.VALUES) {
				if (d.isValidConnection(f))
					map.put(f, d);
			}
			map.put(null, d);
			capabilityMap.put(Capabilities.GRID_DEVICE, map);
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
	protected final <T> T getInstance(Map<EnumFacing, ?> in, EnumFacing f, Capability<T> capability) {
		try {
			return (T) (in.get(f));
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
}
