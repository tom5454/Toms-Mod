package com.tom.api.energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import com.tom.config.Config;

public enum EnergyType implements IStringSerializable {
	// LASER(32,"Laser",TextFormatting.YELLOW),
	HV(8, "HV", TextFormatting.DARK_AQUA), MV(4, "MV", TextFormatting.GOLD), LV(1, "LV", TextFormatting.GRAY), FORCE(16, "Force", TextFormatting.GREEN),;
	private double multiplier;
	private final String name;
	private final TextFormatting color;
	public static final EnergyType[] VALUES = values();

	public double getMultiplyer() {
		return multiplier;
	}

	private EnergyType(double m, String name, TextFormatting color) {
		multiplier = m;
		this.name = name;
		this.color = color;
	}

	public double convertFrom(EnergyType typeIn, double energy) {
		double toLv = energy * typeIn.multiplier;
		if (this == LV) { return toLv; }
		return toLv / this.multiplier;
	}

	public List<EnergyType> getList() {
		List<EnergyType> l = new ArrayList<>();
		l.add(this);
		return l;
	}

	public List<EnergyType> getList(EnergyType... other) {
		List<EnergyType> l = new ArrayList<>();
		l.add(this);
		l.addAll(Arrays.asList(other));
		return l;
	}

	public static List<EnergyType> asList(EnergyType... in) {
		return Arrays.asList(in);
	}

	public TextFormatting getColor() {
		return this.color;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * @param world
	 *            instance
	 * @param pos
	 *            the position of the receiver
	 * @param side
	 *            {@link net.minecraft.util.EnumFacing} input side
	 * @param energyStored
	 *            current energy stored
	 * @param maxPush
	 *            max extract value
	 * @return Energy pushed
	 **/
	public double pushEnergyTo(World world, BlockPos pos, EnumFacing side, double energyStored, double maxPush, boolean simulate) {
		return pushEnergyTo(getHandlerFrom(world, pos, side), energyStored, maxPush, simulate);
	}

	public static IEnergyStorageHandler getHandlerFrom(World world, BlockPos pos, EnumFacing side) {
		TileEntity rec = world.getTileEntity(pos.offset(side.getOpposite()));
		if (rec != null && rec.hasCapability(ENERGY_HANDLER_CAPABILITY, side)) { return rec.getCapability(ENERGY_HANDLER_CAPABILITY, side); }
		return null;
	}

	public static IEnergyStorageHandler getHandlerFrom(World world, BlockPos pos, EnumFacing side, Predicate<TileEntity> checkIfValid) {
		TileEntity rec = world.getTileEntity(pos.offset(side.getOpposite()));
		if (rec != null && checkIfValid.test(rec) && rec.hasCapability(ENERGY_HANDLER_CAPABILITY, side)) { return rec.getCapability(ENERGY_HANDLER_CAPABILITY, side); }
		return null;
	}

	public double pushEnergyTo(IEnergyStorageHandler h, double energyStored, double maxPush, boolean simulate) {
		if (h != null) {
			double energyPushed = h.receiveEnergy(this, Math.min(maxPush, energyStored), true);
			if (energyPushed > 0) { return h.receiveEnergy(this, energyPushed, simulate); }
		}
		return 0;
	}

	public double pushEnergyTo(IEnergyStorageHandler h, IEnergyStorage energy, boolean simulate) {
		double energyPushed = this.pushEnergyTo(h, energy.getEnergyStored(), energy.getMaxExtract(), simulate);
		if (energyPushed > 0 && !simulate) {
			energy.extractEnergy(energyPushed, false);
		}
		return energyPushed;
	}

	public double pushEnergyTo(IEnergyStorageHandler h, IEnergyStorage energy, double max, boolean simulate) {
		double energyPushed = this.pushEnergyTo(h, energy.getEnergyStored(), Math.min(max, energy.getMaxExtract()), simulate);
		if (energyPushed > 0 && !simulate) {
			energy.extractEnergy(energyPushed, false);
		}
		return energyPushed;
	}

	public double pushEnergyTo(World world, BlockPos pos, EnumFacing side, IEnergyStorage energy, boolean simulate) {
		double energyPushed = this.pushEnergyTo(world, pos, side, energy.getEnergyStored(), energy.getMaxExtract(), simulate);
		if (energyPushed > 0 && !simulate) {
			energy.extractEnergy(energyPushed, false);
		}
		return energyPushed;
	}

	public static void init() {
		FORCE.multiplier = HV.multiplier * Config.forceMultipier;
		CapabilityManager.INSTANCE.register(IEnergyStorageHandler.class, new IStorage<IEnergyStorageHandler>() {

			@Override
			public NBTBase writeNBT(Capability<IEnergyStorageHandler> capability, IEnergyStorageHandler instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<IEnergyStorageHandler> capability, IEnergyStorageHandler instance, EnumFacing side, NBTBase nbt) {

			}

		}, new Callable<IEnergyStorageHandler>() {

			@Override
			public IEnergyStorageHandler call() throws Exception {
				return new EnergyHandlerNormal(EnumFacing.DOWN);
			}
		});
	}

	@CapabilityInject(IEnergyStorageHandler.class)
	public static Capability<IEnergyStorageHandler> ENERGY_HANDLER_CAPABILITY = null;

	public static class EnergyHandlerNormal implements IEnergyStorageHandler {
		IEnergyHandler h;
		EnumFacing from;

		public EnergyHandlerNormal(EnumFacing side) {
			from = side;
		}

		public EnergyHandlerNormal(IEnergyHandler h, EnumFacing side) {
			this.h = h;
			from = side;
		}

		@Override
		public boolean canConnectEnergy(EnergyType type) {
			return h != null ? h.canConnectEnergy(from, type) : false;
		}

		@Override
		public List<EnergyType> getValidEnergyTypes() {
			return h != null ? h.getValidEnergyTypes() : Collections.emptyList();
		}

		@Override
		public double receiveEnergy(EnergyType type, double maxReceive, boolean simulate) {
			return h != null ? h.canConnectEnergy(from, type) ? h.receiveEnergy(from, type, maxReceive, simulate) : 0 : 0;
		}

		@Override
		public double extractEnergy(EnergyType type, double maxExtract, boolean simulate) {
			return h != null ? h.canConnectEnergy(from, type) ? h.extractEnergy(from, type, maxExtract, simulate) : 0 : 0;
		}

		@Override
		public double getEnergyStored(EnergyType type) {
			return h != null ? h.getEnergyStored(from, type) : 0;
		}

		@Override
		public int getMaxEnergyStored(EnergyType type) {
			return h != null ? h.getMaxEnergyStored(from, type) : 0;
		}
	}

	public static class EnergyHandlerProvider implements IEnergyStorageHandler {
		IEnergyProvider h;
		EnumFacing from;

		public EnergyHandlerProvider(IEnergyProvider h, EnumFacing side) {
			this.h = h;
			from = side;
		}

		@Override
		public boolean canConnectEnergy(EnergyType type) {
			return h != null ? h.canConnectEnergy(from, type) : false;
		}

		@Override
		public List<EnergyType> getValidEnergyTypes() {
			return h != null ? h.getValidEnergyTypes() : Collections.emptyList();
		}

		@Override
		public double receiveEnergy(EnergyType type, double maxReceive, boolean simulate) {
			return 0;
		}

		@Override
		public double extractEnergy(EnergyType type, double maxExtract, boolean simulate) {
			return h != null ? h.canConnectEnergy(from, type) ? h.extractEnergy(from, type, maxExtract, simulate) : 0 : 0;
		}

		@Override
		public double getEnergyStored(EnergyType type) {
			return h != null ? h.getEnergyStored(from, type) : 0;
		}

		@Override
		public int getMaxEnergyStored(EnergyType type) {
			return h != null ? h.getMaxEnergyStored(from, type) : 0;
		}
	}

	public static class EnergyHandlerReceiver implements IEnergyStorageHandler {
		IEnergyReceiver h;
		EnumFacing from;

		public EnergyHandlerReceiver(IEnergyReceiver h, EnumFacing side) {
			this.h = h;
			from = side;
		}

		@Override
		public boolean canConnectEnergy(EnergyType type) {
			return h != null ? h.canConnectEnergy(from, type) : false;
		}

		@Override
		public List<EnergyType> getValidEnergyTypes() {
			return h != null ? h.getValidEnergyTypes() : Collections.emptyList();
		}

		@Override
		public double receiveEnergy(EnergyType type, double maxReceive, boolean simulate) {
			return h != null ? h.canConnectEnergy(from, type) ? h.receiveEnergy(from, type, maxReceive, simulate) : 0 : 0;
		}

		@Override
		public double extractEnergy(EnergyType type, double maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public double getEnergyStored(EnergyType type) {
			return h != null ? h.getEnergyStored(from, type) : 0;
		}

		@Override
		public int getMaxEnergyStored(EnergyType type) {
			return h != null ? h.getMaxEnergyStored(from, type) : 0;
		}
	}

	public static EnergyType get(int index) {
		return VALUES[MathHelper.abs(index % VALUES.length)];
	}

	public static int toRF(EnergyType type, double in, float multiplier) {
		return MathHelper.floor(LV.convertFrom(type, in) * multiplier);
	}

	public static double fromRF(EnergyType to, int in, float multiplier) {
		return to.convertFrom(LV, in / multiplier);
	}

	@Override
	public String getName() {
		return name.toLowerCase(Locale.ROOT);
	}

	public String toTooltip() {
		return color + name;
	}
}
