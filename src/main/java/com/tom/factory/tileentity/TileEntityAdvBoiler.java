package com.tom.factory.tileentity;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.factory.block.BasicBoiler;
import com.tom.lib.Configs;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldType;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityAdvBoiler extends TileEntityTomsMod implements ITileFluidHandler, IInventory{
	private FluidTank tankWater = new FluidTank(Configs.BASIC_TANK_SIZE * 2);
	private FluidTank tankSteam = new FluidTank(Configs.BASIC_TANK_SIZE * 3);
	public InventoryBasic inv = new InventoryBasic("", false, 1);
	private double heat = 20;
	private int burnTime = 0, maxBurnTime = 0;
	public int clientHeat;
	public static final int MAX_TEMP = 1500;
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return ITileFluidHandler.Helper.getFluidHandlerFromTanks(new FluidTank[]{tankWater, tankSteam}, new Fluid[]{FluidRegistry.WATER, CoreInit.steam}, new boolean[]{true, false}, new boolean[]{false, false});
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("water", tankWater.writeToNBT(new NBTTagCompound()));
		tag.setTag("steam", tankSteam.writeToNBT(new NBTTagCompound()));
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < inv.getSizeInventory(); ++i)
		{
			if (inv.getStackInSlot(i) != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				inv.getStackInSlot(i).writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		tag.setTag("inventory", list);
		tag.setInteger("burnTime", burnTime);
		tag.setDouble("heat", heat);
		tag.setInteger("burnTimeMax", maxBurnTime);
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tankWater.readFromNBT(compound.getCompoundTag("water"));
		tankSteam.readFromNBT(compound.getCompoundTag("steam"));
		NBTTagList list = compound.getTagList("inventory", 10);
		inv.clear();
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < inv.getSizeInventory())
			{
				inv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
		heat = compound.getDouble("heat");
		burnTime = compound.getInteger("burnTime");
		maxBurnTime = compound.getInteger("burnTimeMax");
	}
	@Override
	public void updateEntity(IBlockState state) {
		if(worldObj.isRemote)return;
		if(this.burnTime < 1 && this.inv.getStackInSlot(0) != null && ((pos.getY() > 48 && pos.getY() < 150) || worldObj.getWorldType() == WorldType.FLAT)){
			ItemStack fss = this.inv.getStackInSlot(0);
			int itemBurnTime = TomsModUtils.getBurnTime(fss);
			if(itemBurnTime > 0){
				this.maxBurnTime = this.burnTime = itemBurnTime;
				this.inv.decrStackSize(0, 1);
				if(fss.getItem().getContainerItem(fss) != null){
					ItemStack s = fss.getItem().getContainerItem(fss);
					EnumFacing f = state.getValue(BasicBoiler.FACING);
					EnumFacing facing = f.getOpposite();
					BlockPos invP = pos.offset(facing);
					IInventory inv = TileEntityHopper.getInventoryAtPosition(worldObj, invP.getX(), invP.getY(), invP.getZ());
					if(inv != null)
						s = TileEntityHopper.putStackInInventoryAllSlots(inv, s, facing);
					if(s != null){
						EntityItem item = new EntityItem(worldObj, pos.getX()+0.5D, pos.getY()+1, pos.getZ()+0.5D, fss.getItem().getContainerItem(fss));
						item.motionX = facing.getFrontOffsetX() * 0.3;
						item.motionZ = facing.getFrontOffsetZ() * 0.3;
						worldObj.spawnEntityInWorld(item);
					}
				}
				heat = Math.min(0.1D + heat, MAX_TEMP);
			}else{
				if(fss != null){
					EntityItem item = new EntityItem(worldObj, pos.getX()+0.5D, pos.getY()+1, pos.getZ()+0.5D, fss);
					worldObj.spawnEntityInWorld(item);
				}
			}
			this.markDirty();
		}else if(burnTime > 0){
			burnTime = Math.max(burnTime - 2, 0);
			if(!state.getValue(BasicBoiler.ACTIVE)){
				TomsModUtils.setBlockState(worldObj, pos, state.withProperty(BasicBoiler.ACTIVE, true), 2);
				this.markDirty();
			}
			double increase = heat > 400 ? heat > 800 ? 0.09D : 0.1D : 0.12D;
			heat = Math.min(increase + heat, MAX_TEMP);
		}else{
			if(state.getValue(BasicBoiler.ACTIVE)){
				TomsModUtils.setBlockState(worldObj, pos, state.withProperty(BasicBoiler.ACTIVE, false), 2);
				this.markDirty();
			}
			heat = Math.max(heat - (heat / 500), 20);
			this.maxBurnTime = 0;
		}
		if(heat > 130 && tankWater.getFluidAmount() > 100 && tankSteam.getFluidAmount() != tankSteam.getCapacity()){
			int p = MathHelper.ceiling_double_int((heat - 130) / 20);
			tankWater.drainInternal(p, true);
			tankSteam.fillInternal(new FluidStack(CoreInit.steam, p), true);
			heat -= 0.08D;
		}
		if(tankSteam.getFluidAmount() > tankSteam.getCapacity() / 2){
			EnumFacing f = state.getValue(BasicBoiler.FACING);
			TileEntity tile = worldObj.getTileEntity(pos.offset(f.getOpposite()));
			if(tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f)){
				int extra = Math.min(tankSteam.getFluidAmount() - (tankSteam.getCapacity() / 2), 800);
				IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
				if(t != null){
					int filled = t.fill(new FluidStack(CoreInit.steam, extra), false);
					if(filled > 0){
						FluidStack drained = tankSteam.drainInternal(filled, false);
						if(drained != null && drained.amount > 0){
							int canDrain = Math.min(filled, Math.min(800, drained.amount));
							t.fill(tankSteam.drainInternal(canDrain, true), true);
						}
					}
				}
			}
		}
	}
	public double getHeat() {
		return heat;
	}
	public int getBurnTime() {
		return burnTime;
	}
	public void setBurnTime(int burnTime) {
		this.burnTime = burnTime;
	}
	public FluidTank getTankWater() {
		return tankWater;
	}
	public FluidTank getTankSteam() {
		return tankSteam;
	}
	public int getMaxBurnTime() {
		return maxBurnTime;
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
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}
	@Override
	public String getName() {
		return inv.getName();
	}
	@Override
	public boolean hasCustomName() {
		return inv.hasCustomName();
	}
	@Override
	public ITextComponent getDisplayName() {
		return inv.getDisplayName();
	}
	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return inv.isUseableByPlayer(player);
	}
	@Override
	public void openInventory(EntityPlayer player) {
		inv.openInventory(player);
	}
	@Override
	public void closeInventory(EntityPlayer player) {
		inv.closeInventory(player);
	}
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
	}
	@Override
	public int getField(int id) {
		return inv.getField(id);
	}
	@Override
	public void setField(int id, int value) {
		inv.setField(id, value);
	}
	@Override
	public int getFieldCount() {
		return inv.getFieldCount();
	}
	@Override
	public void clear() {
		inv.clear();
	}
}