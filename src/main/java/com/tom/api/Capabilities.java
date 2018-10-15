package com.tom.api;

import java.util.concurrent.Callable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.ISecuredTileEntity;
import com.tom.lib.api.grid.IGrid;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.util.TMLogger;

public class Capabilities {
	@CapabilityInject(ISecuredTileEntity.class)
	public static Capability<ISecuredTileEntity> SECURED_TILE = null;
	@CapabilityInject(IConfigurable.class)
	public static Capability<IConfigurable> CONFIGURABLE = null;
	@CapabilityInject(IGridDevice.class)
	public static Capability<IGridDevice<?>> GRID_DEVICE = null;

	@SuppressWarnings("rawtypes")
	public static void init() {
		TMLogger.info("Loading Capabilities");
		CapabilityManager.INSTANCE.register(ISecuredTileEntity.class, new IStorage<ISecuredTileEntity>() {

			@Override
			public NBTBase writeNBT(Capability<ISecuredTileEntity> capability, ISecuredTileEntity instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<ISecuredTileEntity> capability, ISecuredTileEntity instance, EnumFacing side, NBTBase nbt) {

			}

		}, new Callable<ISecuredTileEntity>() {

			@Override
			public ISecuredTileEntity call() throws Exception {
				return new SecuredTile();
			}
		});
		CapabilityManager.INSTANCE.register(IConfigurable.class, new IStorage<IConfigurable>() {

			@Override
			public NBTBase writeNBT(Capability<IConfigurable> capability, IConfigurable instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<IConfigurable> capability, IConfigurable instance, EnumFacing side, NBTBase nbt) {

			}

		}, new Callable<IConfigurable>() {

			@Override
			public IConfigurable call() throws Exception {
				return new ConfigurableTile();
			}
		});
		CapabilityManager.INSTANCE.register(IGridDevice.class, new IStorage<IGridDevice>() {

			@Override
			public NBTBase writeNBT(Capability<IGridDevice> capability, IGridDevice instance, EnumFacing side) {
				NBTTagCompound nbt = new NBTTagCompound();
				if (instance.isMaster())
					nbt.setTag(IGridDevice.GRID_TAG_NAME, instance.getGrid().exportToNBT());
				nbt.setBoolean(IGridDevice.MASTER_NBT_NAME, instance.isMaster());
				return nbt;
			}

			@Override
			public void readNBT(Capability<IGridDevice> capability, IGridDevice instance, EnumFacing side, NBTBase nbtIn) {
				NBTTagCompound nbt = (NBTTagCompound) nbtIn;
				boolean isMaster = nbt.getBoolean(IGridDevice.MASTER_NBT_NAME);
				if (isMaster)
					instance.getGrid().importFromNBT(nbt.getCompoundTag(IGridDevice.GRID_TAG_NAME));
			}

		}, new Callable<IGridDevice>() {

			@Override
			public IGridDevice call() throws Exception {
				return new GridDeviceTile();
			}
		});
	}

	public static class SecuredTile implements ISecuredTileEntity {

		@Override
		public BlockPos getSecurityStationPos() {
			return null;
		}
	}

	public static class ConfigurableTile implements IConfigurable {

		@Override
		public void receiveNBTPacket(NBTTagCompound message) {

		}

		@Override
		public void writeToNBTPacket(NBTTagCompound tag) {

		}

		@Override
		public IConfigurationOption getOption() {
			return null;
		}

		@Override
		public boolean canConfigure(EntityPlayer player, ItemStack stack) {
			return false;
		}

		@Override
		public BlockPos getPos2() {
			return BlockPos.ORIGIN;
		}

		@Override
		public void setCardStack(ItemStack stack) {

		}

		@Override
		public ItemStack getCardStack() {
			return ItemStack.EMPTY;
		}

		@Override
		public String getConfigName() {
			return "DUMMY";
		}

	}

	@SuppressWarnings("rawtypes")
	public static class GridDeviceTile implements IGridDevice {

		@Override
		public boolean isMaster() {
			return false;
		}

		@Override
		public void setMaster(IGridDevice master, int size) {
		}

		@Override
		public BlockPos getPos2() {
			return BlockPos.ORIGIN;
		}

		@Override
		public World getWorld2() {
			return null;
		}

		@Override
		public IGrid getGrid() {
			return null;
		}

		@Override
		public IGridDevice getMaster() {
			return null;
		}

		@Override
		public boolean isConnected(EnumFacing side) {
			return false;
		}

		@Override
		public boolean isValidConnection(EnumFacing side) {
			return false;
		}

		@Override
		public void invalidateGrid() {
		}

		@Override
		public void setSuctionValue(int suction) {
		}

		@Override
		public int getSuctionValue() {
			return 0;
		}

		@Override
		public void updateState() {
		}

		@Override
		public void setGrid(IGrid newGrid) {
		}

		@Override
		public boolean isValid() {
			return false;
		}

		@Override
		public NBTTagCompound getGridData() {
			return null;
		}
	}

	public static ISecuredTileEntity getSecuredTileEntityAt(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		return te != null && te.hasCapability(SECURED_TILE, null) ? te.getCapability(SECURED_TILE, null) : null;
	}
}
