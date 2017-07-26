package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockCokeOven;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntityCokeOven extends TileEntityTomsMod implements ISidedInventory, ITileFluidHandler {
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	private FluidTank tank;

	public TileEntityCokeOven() {
		tank = new FluidTank(64000);
		tank.setCanFill(false);
	}

	private int progress = -1;
	private static final int[] SLOTS = new int[]{0, 1};
	private int maxProgress = 0;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		tag.setTag("inventory", TomsModUtils.saveAllItems(inv));
		tag.setInteger("progress", progress);
		tag.setInteger("maxProgress", maxProgress);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag.getCompoundTag("tank"));
		TomsModUtils.loadAllItems(tag.getTagList("inventory", 10), inv);
		this.progress = tag.getInteger("progress");
		this.maxProgress = tag.getInteger("maxProgress");
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUsable(pos, player, world, this);
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return id == 0 ? progress : id == 1 ? maxProgress : 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0)
			progress = value;
		// else if(id == 1)maxProgress = value;
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public String getName() {
		return "cokeOven";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote) {
			if (checkIfMerged(state)) {
				if (progress > 0) {
					progress--;
				} else if (progress == 0) {
					ItemStackChecker s = MachineCraftingHandler.getCokeOvenOutput(inv.getStackInSlot(0));
					if (s != null) {
						if (!inv.getStackInSlot(1).isEmpty()) {
							if (TomsModUtils.areItemStacksEqual(inv.getStackInSlot(1), s.getStack(), true, true, false) && inv.getStackInSlot(1).getCount() + s.getStack().getCount() <= s.getStack().getMaxStackSize() && inv.getStackInSlot(0).getCount() >= s.getExtra()) {
								inv.getStackInSlot(1).grow(s.getStack().getCount());
								progress = -1;
								maxProgress = 0;
								decrStackSize(0, s.getExtra());
							}
						} else {
							progress = -1;
							maxProgress = 0;
							inv.setInventorySlotContents(1, s.getStack());
							decrStackSize(0, s.getExtra());
						}
						tank.fillInternal(new FluidStack(CoreInit.creosoteOil.get(), s.getExtra2()), true);
					} else {
						progress = -1;
						maxProgress = 0;
					}
				} else {
					ItemStackChecker s = MachineCraftingHandler.getCokeOvenOutput(inv.getStackInSlot(0));
					if (s != null) {
						if (s.getExtra2() + tank.getFluidAmount() <= tank.getCapacity())
							if (!inv.getStackInSlot(1).isEmpty()) {
								if (TomsModUtils.areItemStacksEqual(inv.getStackInSlot(1), s.getStack(), true, true, false) && inv.getStackInSlot(1).getCount() + s.getStack().getCount() <= s.getStack().getMaxStackSize() && inv.getStackInSlot(1).getCount() >= s.getExtra()) {
									maxProgress = s.getExtra3();
									progress = maxProgress;
								}
							} else {
								maxProgress = s.getExtra3();
								progress = maxProgress;
							}
					}
					TomsModUtils.setBlockStateWithCondition(world, pos, BlockCokeOven.STATE, progress > 0 ? 2 : 1);
				}
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, BlockCokeOven.STATE, 0);
			}
		}
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return tank;
	}

	public boolean checkIfMerged(IBlockState state) {
		EnumFacing facing = state.getValue(BlockCokeOven.FACING);
		BlockPos center = pos.offset(facing, 2);
		boolean isValid = check3x3(center) && check3x3h(center.up()) && check3x3(center.up(2));
		return isValid;
	}

	private boolean check3x3(BlockPos center) {
		boolean ret = isWall(center);
		if (ret) {
			for (EnumFacing f : EnumFacing.HORIZONTALS) {
				ret = ret && isWall(center.offset(f)) && isWall(center.offset(f).offset(f.rotateY()));
				if (!ret)
					return false;
			}
		}
		return ret;
	}

	private boolean isWall(BlockPos pos) {
		return world.getBlockState(pos).getBlock() == FactoryInit.cokeOvenWall;
	}

	private boolean check3x3h(BlockPos center) {
		boolean ret = true;
		for (EnumFacing f : EnumFacing.HORIZONTALS) {
			ret = ret && isWall(center.offset(f)) && isWall(center.offset(f).offset(f.rotateY()));
			if (!ret)
				return false;
		}
		return ret;
	}

	public FluidTank getTank() {
		return tank;
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
