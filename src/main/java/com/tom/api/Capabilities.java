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
import com.tom.util.TMLogger;

public class Capabilities {
	@CapabilityInject(ISecuredTileEntity.class)
	public static Capability<ISecuredTileEntity> SECURED_TILE = null;
	@CapabilityInject(IConfigurable.class)
	public static Capability<IConfigurable> CONFIGURABLE = null;

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
	}

	public static class SecuredTile implements ISecuredTileEntity {

		@Override
		public BlockPos getSecurityStationPos() {
			return null;
		}
	}

	public static class ConfigurableTile implements IConfigurable {

		@Override
		public void receiveNBTPacket(EntityPlayer player, NBTTagCompound message) {

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

	public static ISecuredTileEntity getSecuredTileEntityAt(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		return te != null && te.hasCapability(SECURED_TILE, null) ? te.getCapability(SECURED_TILE, null) : null;
	}
}
