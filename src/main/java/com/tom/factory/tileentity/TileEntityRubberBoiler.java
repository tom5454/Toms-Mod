package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.IHeatSource;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.RubberBoiler;
import com.tom.lib.Configs;
import com.tom.recipes.OreDict;

public class TileEntityRubberBoiler extends TileEntityTomsMod implements ISidedInventory, ITileFluidHandler {
	public static final int MAX_PROGRESS = 300;
	private InventoryBasic inv = new InventoryBasic("", false, 2);
	private FluidTank resin = new FluidTank(4000);
	private FluidTank cresin = new FluidTank(1000);
	private int drained, progress = -1;
	private double heat = 20;
	public int maxHeat;
	public int clientHeat;

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
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

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		EnumFacing facing = getFacing();
		return facing.rotateY() == f ? Helper.getFluidHandlerFromTank(resin, true, false, CoreInit.resin) : facing.rotateYCCW() == f ? Helper.getFluidHandlerFromTank(cresin, false, true, CoreInit.concentratedResin) : null;
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
		return index == 0 ? OreDict.isOre(stack, "logRubber") : index == 1 ? false : false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{0, 1};
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
	public void updateEntity(IBlockState currentState) {
		if (!world.isRemote) {
			IHeatSource s = getHeatSource();
			boolean active = false;
			if (s != null) {
				heat = s.transferHeat(heat, 5);
				maxHeat = MathHelper.floor(s.getMaxHeat());
			}
			if (heat > 200) {
				if (drained > 0) {
					if (drained >= 5) {
						if (cresin.getFluidAmount() < cresin.getCapacity()) {
							drained -= 5;
							heat -= 0.01;
							cresin.fillInternal(new FluidStack(CoreInit.concentratedResin.get(), 1), true);
						}
						active = true;
					} else {
						if (resin.getFluidAmount() >= 1) {
							int e = MathHelper.floor(heat / 180);
							e = resin.drainInternal(e, true).amount;
							heat -= e * 0.01;
							drained += e;
							active = true;
						}
					}
				} else if (resin.getFluidAmount() >= 5) {
					int e = MathHelper.floor(heat / 200);
					e = resin.drainInternal(e, true).amount;
					heat -= e * 0.01;
					drained += e;
					active = true;
				}
				if (heat > 500 && resin.getFluidAmount() < resin.getCapacity()) {
					if (progress < 0) {
						if (OreDict.isOre(inv.getStackInSlot(0), "logRubber")) {
							decrStackSize(0, 1);
							progress = MAX_PROGRESS;
							active = true;
						}
					} else if (progress == 0) {
						ItemStack stack = inv.getStackInSlot(1);
						if (stack.isEmpty()) {
							inv.setInventorySlotContents(1, new ItemStack(Items.COAL, 1, 1));
							progress = -1;
						} else if (stack.getItem() == Items.COAL && stack.getMetadata() == 1 && stack.getCount() < stack.getMaxStackSize()) {
							stack.grow(1);
							progress = -1;
						}
						active = true;
					} else {
						if (progress % 3 == 0)
							resin.fill(new FluidStack(CoreInit.resin.get(), 1), true);
						progress--;
						heat -= 0.01;
						active = true;
						if (heat > 900) {
							if (progress % 3 == 0)
								resin.fill(new FluidStack(CoreInit.resin.get(), 1), true);
							progress--;
							heat -= 0.01;
						}
					}
				}
			}
			EnumFacing f = getFacing(currentState).rotateYCCW();
			if (cresin.getFluidAmount() > 0) {
				if (cresin.getFluid() != null && cresin.getFluidAmount() > 0) {
					TileEntity tile = world.getTileEntity(pos.offset(f));
					if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite())) {
						IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite());
						if (t != null) {
							int filled = t.fill(cresin.getFluid(), false);
							if (filled > 0) {
								FluidStack drained = cresin.drain(filled, false);
								if (drained != null && drained.amount > 0) {
									int canDrain = Math.min(filled, Math.min(Configs.fluidDuctMaxInsert, drained.amount));
									t.fill(cresin.drain(canDrain, true), true);
								}
							}
						}
					}
				}
			}
			TomsModUtils.setBlockStateWithCondition(world, pos, RubberBoiler.ACTIVE, active);
		}
	}

	public EnumFacing getFacing(IBlockState state) {
		return state.getValue(RubberBoiler.FACING);
	}

	public EnumFacing getFacing() {
		return getFacing(world.getBlockState(pos));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", TomsModUtils.saveAllItems(inv));
		compound.setDouble("heat", heat);
		compound.setTag("resin", resin.writeToNBT(new NBTTagCompound()));
		compound.setTag("cresin", cresin.writeToNBT(new NBTTagCompound()));
		compound.setInteger("progress1", drained);
		compound.setInteger("progress2", progress);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		TomsModUtils.loadAllItems(compound.getTagList("inventory", 10), inv);
		heat = compound.getDouble("heat");
		resin.readFromNBT(compound.getCompoundTag("resin"));
		cresin.readFromNBT(compound.getCompoundTag("cresin"));
		drained = compound.getInteger("progress1");
		progress = compound.getInteger("progress2");
	}

	public IHeatSource getHeatSource() {
		BlockPos p = pos.down();
		if (world.getBlockState(pos.down()).getBlock() == FactoryInit.steelBoiler) {
			p = pos.down(2);
		}
		TileEntity te = world.getTileEntity(p);
		return te instanceof IHeatSource ? (IHeatSource) te : null;
	}

	public FluidTank getTankIn() {
		return resin;
	}

	public FluidTank getTankOut() {
		return cresin;
	}

	public double getHeat() {
		return heat;
	}

	public int getProgress() {
		return progress;
	}

	public void setClientProgress(int data) {
		progress = data;
	}
}
