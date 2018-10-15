package com.tom.core.tileentity;

import static com.tom.api.energy.EnergyType.LV;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.lib.api.tileentity.ITMPeripheral.ITMCompatPeripheral;
import com.tom.util.TomsModUtils;

import com.tom.core.block.HolotapeWriter;

public class TileEntityHolotapeWriter extends TileEntityTomsMod implements ITMCompatPeripheral, IEnergyReceiver, ISidedInventory {
	public TileEntityHolotapeWriter() {
	}
	protected EnergyStorage storage = new EnergyStorage(100000, 1000);
	private List<IComputer> computers = new ArrayList<>();
	public InventoryBasic inv = new InventoryBasic("", false, 1);
	private double i = 0;
	public boolean isWriting = false;
	private double iMax = 0;
	public boolean isValidH = false;
	public boolean hasH = false;
	public int direction = 0;

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		storage.readFromNBT(tag);
		this.inv.setInventorySlotContents(0, TomsModUtils.loadItemStackFromNBT(tag.getCompoundTag("holotape")));
		this.i = tag.getDouble("i");
		this.iMax = tag.getDouble("iMax");
		this.isWriting = tag.getBoolean("isWriting");
		this.direction = tag.getInteger("direction");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		storage.writeToNBT(tag);
		tag.setTag("holotape", inv.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
		tag.setDouble("i", i);
		tag.setDouble("iMax", iMax);
		tag.setBoolean("isWriting", this.isWriting);
		tag.setInteger("direction", this.direction);
		return tag;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {

		return type == LV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return this.canConnectEnergy(from, type) ? storage.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return storage.getEnergyStored();
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public String getType() {
		return "tm_holotape_writer";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"write", "isWritable", "getWritingProgress"};
	}

	@Override
	public Object[] callMethod(IComputer computer, int method, Object[] a) throws LuaException {
		if (method == 0) {
			if (!inv.getStackInSlot(0).isEmpty()) {
				if (inv.getStackInSlot(0).getTagCompound() == null)
					inv.getStackInSlot(0).setTagCompound(new NBTTagCompound());
				if (a.length > 1) {
					if ((inv.getStackInSlot(0).getTagCompound().hasKey("w") && !inv.getStackInSlot(0).getTagCompound().getBoolean("w")) || !inv.getStackInSlot(0).getTagCompound().hasKey("w")) {
						String data = a[1].toString();
						this.inv.getStackInSlot(0).getTagCompound().setString("name", a[0].toString());
						this.inv.getStackInSlot(0).getTagCompound().setString("data", data);
						this.inv.getStackInSlot(0).getTagCompound().setBoolean("w", true);
						int l = data.length();
						this.i = l;
						this.isWriting = true;
						this.iMax = l;
						markBlockForUpdate(pos);
					} else {
						throw new LuaException("This holotape has already written!");
					}
				} else {
					throw new LuaException("Invalid arguments, excepted (String name, String data)");
				}
			} else {
				throw new LuaException("No Holotape in the machine!");
			}
		} else if (method == 1) {
			return new Object[]{isValidH};
		} else if (method == 2) {
			if (this.isWriting) {
				double p = Math.floor(((this.iMax - this.i) / this.iMax) * 1000) / 10D;
				return new Object[]{true, p};
			} else {
				return new Object[]{false, 0};
			}
		}
		return new Object[0];
	}

	@Override
	public void attach(IComputer computer) {
		computers.add(computer);
	}

	@Override
	public void detach(IComputer computer) {
		computers.remove(computer);
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			if (i > 0 && this.isWriting) {
				double energy = Config.holotapeSpeed * 2;
				double extract = this.storage.extractEnergy(energy, true);
				if (extract == energy) {
					i = i - Config.holotapeSpeed;
					this.storage.extractEnergy(energy, false);
				}
			} else if (i != 0 && this.isWriting) {
				i = 0;
			} else if (i == 0 && this.isWriting) {
				for (IComputer c : this.computers) {
					c.queueEvent("holotape_done", new Object[]{c.getAttachmentName(), this.inv.getStackInSlot(0).getTagCompound().getString("name")});
				}
				this.isWriting = false;
				markBlockForUpdate(pos);
			}
			this.hasH = !this.inv.getStackInSlot(0).isEmpty();
			this.isValidH = this.isValidHolotape();
			if (this.hasH) {
				if (this.isWriting) {
					TomsModUtils.setBlockStateWithCondition(world, pos, HolotapeWriter.STATE, 3);
				} else {
					if (this.isValidH) {
						TomsModUtils.setBlockStateWithCondition(world, pos, HolotapeWriter.STATE, 2);
					} else {
						TomsModUtils.setBlockStateWithCondition(world, pos, HolotapeWriter.STATE, 1);
					}
				}
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, HolotapeWriter.STATE, 0);
			}
		}
	}

	public boolean isValidHolotape() {
		if (!inv.getStackInSlot(0).isEmpty()) {
			if (inv.getStackInSlot(0).getTagCompound() == null)
				inv.getStackInSlot(0).setTagCompound(new NBTTagCompound());
			return ((inv.getStackInSlot(0).getTagCompound().hasKey("w") && !inv.getStackInSlot(0).getTagCompound().getBoolean("w")) || !inv.getStackInSlot(0).getTagCompound().hasKey("w"));
		} else {
			return false;
		}
	}

	@Override
	public String getName() {
		return "Holotape Writer";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int s, ItemStack is) {
		return s == 0 && is != null && is.getItem() == CoreInit.holotape;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(pos) != this ? false : player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void closeInventory(EntityPlayer arg0) {

	}

	@Override
	public int getField(int arg0) {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void openInventory(EntityPlayer arg0) {

	}

	@Override
	public void setField(int arg0, int arg1) {

	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean canExtractItem(int arg0, ItemStack arg1, EnumFacing arg2) {
		return !this.isWriting;
	}

	@Override
	public boolean canInsertItem(int arg0, ItemStack arg1, EnumFacing arg2) {
		return true;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing arg0) {
		return new int[]{0};
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LV.getList();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public void clear() {
		inv.clear();
	}
}
