package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.LV;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldType;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyProvider;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.util.TomsModUtils;

import com.tom.energy.block.Generator;

public class TileEntityGenerator extends TileEntityTomsMod implements IEnergyProvider, IInventory, ICustomMultimeterInformation {
	public int fuel = 0;
	public InventoryBasic fuelStack = new InventoryBasic("", false, 1);
	public ItemStack currentlyBurning = null;
	private EnergyStorage energy = new EnergyStorage(1000, 100);

	// private static final Random rand = new Random();
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return true;
	}

	@Override
	public void updateEntity(IBlockState state) {
		// System.out.println("update");
		if (world.isRemote)
			return;
		if (this.fuel < 1 && !this.fuelStack.getStackInSlot(0).isEmpty() && !energy.isFull() && ((pos.getY() > 48 && pos.getY() < 150) || world.getWorldType() == WorldType.FLAT)) {
			ItemStack fss = this.fuelStack.getStackInSlot(0);
			int itemBurnTime = TomsModUtils.getBurnTime(fss);
			if (itemBurnTime > 0) {
				this.fuel = itemBurnTime;
				this.currentlyBurning = this.fuelStack.getStackInSlot(0);
				this.fuelStack.setInventorySlotContents(0, ItemStack.EMPTY);
				if (fss.getItem().getContainerItem(fss) != null) {
					ItemStack s = fss.getItem().getContainerItem(fss);
					EnumFacing f = state.getValue(Generator.FACING);
					EnumFacing facing = f.getOpposite();
					BlockPos invP = pos.offset(facing);
					IInventory inv = TileEntityHopper.getInventoryAtPosition(world, invP.getX(), invP.getY(), invP.getZ());
					if (inv != null)
						s = TileEntityHopper.putStackInInventoryAllSlots(inv, inv, s, facing);
					if (!s.isEmpty()) {
						EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, fss.getItem().getContainerItem(fss));
						item.motionX = facing.getFrontOffsetX() * 0.3;
						item.motionZ = facing.getFrontOffsetZ() * 0.3;
						world.spawnEntity(item);
					}
				}
			} else {
				if (!fss.isEmpty()) {
					EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, fss);
					world.spawnEntity(item);
				}
			}
			this.markDirty();
		} else if (fuel > 0) {
			if (energy.receiveEnergy(1.2, true) == 1.2) {
				if (world.provider.getDimension() != -1 || world.getTotalWorldTime() % 2 == 0)
					fuel = fuel - 1;
				energy.receiveEnergy(world.provider.getDimension() == 1 && pos.getY() > 15 ? 1.2 : world.provider.getDimension() == -1 ? .6 : ((pos.getY() > 48 && pos.getY() < 150) || world.getWorldType() == WorldType.FLAT) ? 1.0 : .1, false);
			}
			// System.out.println(fuel);
			/*double var6 = pos.getX() + 0.5D;
			double var8 = pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
			double var10 = pos.getZ() + 0.5D;
			double var12 = 0.52D;
			double var14 = rand.nextDouble() * 0.6D - 0.3D;*/
			// worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
			// pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 1, 1, 1, 1);
			// worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
			// pos.getX(),pos.getY()+0.5D,pos.getZ(), 0.0D, 0.0D, 0.0D, new
			// int[1]);
			if (!state.getValue(Generator.ACTIVE)) {
				TomsModUtils.setBlockState(world, pos, state.withProperty(Generator.ACTIVE, true), 2);
				this.markDirty();
			}
		} else {
			if (state.getValue(Generator.ACTIVE)) {
				TomsModUtils.setBlockState(world, pos, state.withProperty(Generator.ACTIVE, false), 2);
				this.markDirty();
			}
			this.currentlyBurning = null;
		}
		// System.out.println("f:"+fuel);
		if (this.energy.getEnergyStored() > 0) {
			for (EnumFacing f : EnumFacing.VALUES) {
				// TileEntity receiver = worldObj.getTileEntity(pos.offset(f));
				// if(receiver instanceof IEnergyReceiver) {
				// System.out.println("send");
				EnumFacing fOut = f.getOpposite();
				// IEnergyReceiver recv = (IEnergyReceiver)receiver;
				LV.pushEnergyTo(world, pos, fOut, energy, false);
				// }
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.fuel = tag.getInteger("fuel");
		this.fuelStack.setInventorySlotContents(0, TomsModUtils.loadItemStackFromNBT(tag.getCompoundTag("fuelStack")));
		this.currentlyBurning = TomsModUtils.loadItemStackFromNBT(tag.getCompoundTag("currentlyBurning"));
		this.energy.readFromNBT(tag.getCompoundTag("energy"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("fuel", this.fuel);
		NBTTagCompound fuelTag = new NBTTagCompound();
		this.fuelStack.getStackInSlot(0).writeToNBT(fuelTag);
		tag.setTag("fuelStack", fuelTag);
		fuelTag = new NBTTagCompound();
		if (this.currentlyBurning != null)
			this.currentlyBurning.writeToNBT(fuelTag);
		tag.setTag("currentlyBurning", fuelTag);
		tag.setTag("energy", this.energy.writeToNBT(new NBTTagCompound()));
		return tag;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return type == LV ? energy.getEnergyStored() : 0;
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return type == LV ? energy.getMaxEnergyStored() : 0;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("Generator");
	}

	@Override
	public String getName() {
		return "tomsmod.gui.generator";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void clear() {
		this.fuelStack = null;
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
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		int bt = TomsModUtils.getBurnTime(is);
		boolean ret = slot == 0 ? bt > 0 : false;
		return ret;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUsable(pos, player, world, this);
	}

	@Override
	public void openInventory(EntityPlayer arg0) {

	}

	@Override
	public void setField(int arg0, int arg1) {

	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LV.getList();
	}

	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		if (!fuelStack.getStackInSlot(0).isEmpty()) {
			if (fuel > 0) {
				list.add(new TextComponentTranslation("tomsMod.chat.burnTime", fuel));
				list.add(new TextComponentTranslation("tomsMod.chat.currentlyBurning", currentlyBurning != null ? currentlyBurning.getTextComponent() : new TextComponentTranslation("tomsMod.na")));
				list.add(new TextComponentTranslation("tomsMod.chat.inventory", fuelStack != null ? fuelStack.getStackInSlot(0).getTextComponent() : new TextComponentTranslation("tomsMod.na")));
			} else {
				list.add(new TextComponentTranslation("tomsMod.chat.inventory", fuelStack != null ? fuelStack.getStackInSlot(0).getTextComponent() : new TextComponentTranslation("tomsMod.na")));
			}
		} else if (fuel > 0) {
			list.add(new TextComponentTranslation("tomsMod.chat.burnTime", fuel));
			list.add(new TextComponentTranslation("tomsMod.chat.currentlyBurning", currentlyBurning != null ? currentlyBurning.getTextComponent() : new TextComponentTranslation("tomsMod.na")));
		}
		return list;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return fuelStack.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return fuelStack.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return fuelStack.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		fuelStack.setInventorySlotContents(index, stack);
	}

	@Override
	public boolean isEmpty() {
		return fuelStack.isEmpty();
	}
}
