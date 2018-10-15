package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import com.tom.api.block.IItemTile;
import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyContainerItem;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.ILinkable;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.Configs;
import com.tom.util.Coords;
import com.tom.util.ExtraBlockHitInfo;
import com.tom.util.TomsModUtils;

import com.tom.energy.block.WirelessCharger;

public class TileEntityWirelessCharger extends TileEntityTomsMod implements IEnergyReceiver, ILinkable, IItemTile {
	private static final float RF = 0.9F;
	private EnergyStorage energy = new EnergyStorage(10000000, 10000, 10000);
	private List<Coords> linked = new ArrayList<>();
	public boolean redstone = true;
	public boolean active = false;
	public boolean hasRF = false;

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == getType(world.getBlockState(pos));
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		markBlockForUpdate(pos);
		return this.canConnectEnergy(from, type) ? this.energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getEnergyStored();
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getMaxEnergyStored();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.energy.writeToNBT(tag);
		NBTTagList list = new NBTTagList();
		for (Coords te : this.linked) {
			NBTTagCompound tagC = new NBTTagCompound();
			tagC.setInteger("x", te.xCoord);
			tagC.setInteger("y", te.yCoord);
			tagC.setInteger("z", te.zCoord);
			tagC.setInteger("side", te.facing.ordinal());
			list.appendTag(tagC);
		}
		tag.setTag("link", list);
		tag.setBoolean("rf", hasRF);
		return tag;
	}

	public void writeToStackNBT(NBTTagCompound tag) {
		energy.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy.readFromNBT(tag);
		NBTTagList list = (NBTTagList) tag.getTag("link");
		this.linked.clear();
		for (int i = 0;i < list.tagCount();i++) {
			NBTTagCompound tagC = list.getCompoundTagAt(i);
			int x = tagC.getInteger("x");
			int y = tagC.getInteger("y");
			int z = tagC.getInteger("z");
			int side = tagC.getInteger("side");
			this.linked.add(new Coords(x, y, z).setFacing(EnumFacing.VALUES[side]));
		}
		hasRF = tag.getBoolean("rf");
	}

	@Override
	public void updateEntity(IBlockState state) {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.redstone = !(world.isBlockIndirectlyGettingPowered(pos) > 0);
		if (!this.world.isRemote && this.redstone && this.energy.hasEnergy()) {
			if (!state.getValue(WirelessCharger.ACTIVE)) {
				TomsModUtils.setBlockState(world, pos, state.withProperty(WirelessCharger.ACTIVE, true));
			}
			for (int i = 0;i < linked.size();i++) {
				Coords c = linked.get(i);
				TileEntity tile = this.world.getTileEntity(new BlockPos(c.xCoord, c.yCoord, c.zCoord));
				if (tile != null && tile instanceof IEnergyReceiver) {
					IEnergyReceiver te = (IEnergyReceiver) tile;
					double distance = tile.getPos().distanceSq(xCoord, yCoord, zCoord);
					double loss = distance / Configs.wirelessChargerLoss;
					double energySend = this.energy.extractEnergy(te.receiveEnergy(c.facing, getType(state), MathHelper.floor(10000 / distance), true) + loss, true);
					if (energySend > 0 && loss < energySend) {
						this.energy.extractEnergy(energySend, false);
						te.receiveEnergy(c.facing, getType(state), energySend - loss, false);
					}
					if (!this.energy.hasEnergy())
						return;
				} else if (tile instanceof cofh.redstoneflux.api.IEnergyReceiver) {
					if (hasRF) {
						cofh.redstoneflux.api.IEnergyReceiver te = (cofh.redstoneflux.api.IEnergyReceiver) tile;
						double distance = tile.getPos().distanceSq(xCoord, yCoord, zCoord);
						double loss = distance / Configs.wirelessChargerLoss;
						int rec = te.receiveEnergy(c.facing, MathHelper.floor(100000 / distance), true);
						double energySend = energy.extractEnergy(EnergyType.fromRF(getType(state), rec, RF) + loss, true);
						if (energySend > 0 && loss < energySend) {
							this.energy.extractEnergy(energySend, false);
							te.receiveEnergy(c.facing, EnergyType.toRF(getType(state), energySend - loss, RF), false);
						}
					}
				} else {
					this.linked.remove(c);
				}
			}
			if (this.energy.hasEnergy()) {
				List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - 64, pos.getY() - 64, pos.getZ() - 64, pos.getX() + 65, pos.getY() + 65, pos.getZ() + 65));
				for (EntityPlayer player : entities) {
					if (player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 64) {
						InventoryPlayer inv = player.inventory;
						for (int i = 0;i < inv.getSizeInventory();i++) {
							ItemStack item = inv.getStackInSlot(i);
							if (item != null && item.getItem() instanceof IEnergyContainerItem) {
								IEnergyContainerItem c = (IEnergyContainerItem) item.getItem();
								double received = c.receiveEnergy(item, LV.convertFrom(getType(state), energy.getEnergyStored()), true);
								if (received > 0) {
									c.receiveEnergy(item, LV.convertFrom(getType(state), energy.extractEnergy(getType(state).convertFrom(LV, received), false)), false);
								}
							}
							if (hasRF && item.getItem() instanceof cofh.redstoneflux.api.IEnergyContainerItem) {
								cofh.redstoneflux.api.IEnergyContainerItem c = (cofh.redstoneflux.api.IEnergyContainerItem) item.getItem();
								int received = c.receiveEnergy(item, EnergyType.toRF(getType(state), energy.getEnergyStored(), RF), true);
								if (received > 0) {
									c.receiveEnergy(item, EnergyType.toRF(getType(state), energy.extractEnergy(EnergyType.fromRF(getType(state), received, RF), false), RF), false);
								}
							}
							if (!this.energy.hasEnergy())
								return;
						}
					}
				}
			}
		} else {
			if (!this.world.isRemote) {
				if (state.getValue(WirelessCharger.ACTIVE)) {
					TomsModUtils.setBlockState(world, pos, state.withProperty(WirelessCharger.ACTIVE, false));
				}
			}
		}
	}

	@Override
	public boolean link(int x, int y, int z, EnumFacing side, ExtraBlockHitInfo bhp, int dim) {
		TileEntity tile = this.world.getTileEntity(new BlockPos(x, y, z));
		if (tile != null && (tile instanceof IEnergyReceiver || (hasRF && tile instanceof cofh.redstoneflux.api.IEnergyReceiver)) && !this.linked.contains(tile)) {
			this.linked.add(new Coords(x, y, z).setFacing(side));
			return true;
		}
		return false;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return getType(world.getBlockState(pos)).getList();
	}

	public EnergyType getType(IBlockState state) {
		return state.getValue(WirelessCharger.TYPE) ? MV : HV;
	}

	public boolean applyRF() {
		if (!hasRF) {
			hasRF = true;
			return true;
		}
		return false;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack s = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
		s.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = new NBTTagCompound();
		writeToStackNBT(tag);
		s.getTagCompound().setTag("BlockEntityTag", tag);
		drops.add(s);
	}
}
