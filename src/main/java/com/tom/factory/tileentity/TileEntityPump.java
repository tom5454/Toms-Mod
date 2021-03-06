package com.tom.factory.tileentity;

import java.util.List;
import java.util.Stack;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.util.TomsModUtils;

public class TileEntityPump extends TileEntityMachineBase implements ITileFluidHandler, ICustomMultimeterInformation {
	private EnergyStorage energy = new EnergyStorage(5000, 20);
	private FluidTank tank = new FluidTank(1000);
	private Stack<BlockPos> fluidBlocks = new Stack<>();
	private int cooldown;
	private int lvl = 1;
	private boolean clearing = false;

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		tank.setCanFill(false);
		return tank;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return true;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		IFluidHandler c = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		return c != null;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) != null;
	}

	@Override
	public String getName() {
		return "pump";
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return -1;
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return 20;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		// compound.setInteger("progress", progress);
		compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		compound.setTag("fluidblocks", TomsModUtils.writeCollection(fluidBlocks, TomsModUtils::writeBlockPosToNewNBT));
		compound.setInteger("lvl", lvl);
		compound.setInteger("cooldown", cooldown);
		compound.setBoolean("clearing", clearing);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tank.readFromNBT(compound.getCompoundTag("tank"));
		lvl = compound.getInteger("lvl");
		cooldown = compound.getInteger("cooldown");
		fluidBlocks.clear();
		TomsModUtils.readCollection(fluidBlocks, compound.getTagList("fluidblocks", 10), TomsModUtils::readBlockPosFromNBT);
		clearing = compound.getBoolean("clearing");
		// progress = compound.getInteger("progress");
	}

	@Override
	public void writeToStackNBT(NBTTagCompound tag) {
		super.writeToStackNBT(tag);
		tag.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		tag.setBoolean("clearing", clearing);
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			if (energy.getEnergyStored() > 1 && canRun()) {
				if (cooldown < 1) {
					if (fluidBlocks.isEmpty()) {
						checkFluidBlocks(pos.down(lvl));
						cooldown = 300;
						if (fluidBlocks.isEmpty()) {
							if (lvl > 30) {
								lvl = 1;
								cooldown += 50;
							} else
								lvl++;
						}
					} else {
						pump();
					}
				} else {
					cooldown -= MathHelper.floor(getMaxProgress() / 10D);
				}
				if (tank.getFluidAmount() > 0) {
					EnumFacing f = EnumFacing.DOWN;
					TileEntity tile = world.getTileEntity(pos.offset(f.getOpposite()));
					if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f)) {
						int extra = Math.min(tank.getFluidAmount(), 100);
						IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
						if (t != null) {
							int filled = t.fill(new FluidStack(tank.getFluid(), extra), false);
							if (filled > 0) {
								FluidStack drained = tank.drain(filled, false);
								if (drained != null && drained.amount > 0) {
									int canDrain = Math.min(filled, Math.min(500, drained.amount));
									t.fill(tank.drain(canDrain, true), true);
									energy.extractEnergy(0.05, false);
								}
							}
						}
					}
				}
			}
		}
	}

	private void checkFluidBlocks(BlockPos pos) {
		if (pos.getY() < 2) {
			lvl = 0;
			return;
		}
		fluidBlocks.clear();
		IBlockState stateB = world.getBlockState(pos);
		if (stateB.getBlock() instanceof BlockFluidBase || stateB.getBlock() instanceof BlockLiquid) {
			Stack<BlockPos> nextPos = new Stack<>();
			nextPos.add(pos);
			while (!nextPos.isEmpty()) {
				BlockPos p = nextPos.pop();
				if (!fluidBlocks.contains(p) && p.distanceSq(this.pos) < 4096) {
					fluidBlocks.add(p);
					for (EnumFacing f : EnumFacing.HORIZONTALS) {
						if (!fluidBlocks.contains(p.offset(f))) {
							IBlockState stateB2 = world.getBlockState(p.offset(f));
							if (stateB2.getBlock() instanceof BlockFluidBase || stateB2.getBlock() instanceof BlockLiquid) {
								nextPos.add(p.offset(f));
							}
						}
					}
				}
			}
		} else if (stateB.getMaterial() != Material.AIR) {
			lvl = 0;
		}
	}

	private void pump() {
		if (!fluidBlocks.isEmpty()) {
			if (tank.getFluid() == null) {
				BlockPos pos = fluidBlocks.pop();
				if (pos == null)
					return;
				IBlockState stateB = world.getBlockState(pos);
				if (stateB.getBlock() instanceof IFluidBlock || stateB.getBlock() instanceof BlockLiquid) {
					IFluidHandler h = FluidUtil.getFluidHandler(world, pos, EnumFacing.UP);
					if (h != null) {
						FluidStack drained = h.drain(1000, false);
						if (drained != null && drained.amount > 0) {
							int temp = drained.getFluid().getTemperature(world, pos);
							if (tank.fillInternal(drained, false) == 1000) {
								tank.fillInternal(h.drain(1000, true), true);
								energy.extractEnergy(1, false);
								cooldown = 120;
								if (!clearing && !(pos.getX() == this.pos.getX() && pos.getZ() == this.pos.getZ()))
									world.setBlockState(pos, temp > 300 ? Blocks.STONE.getDefaultState() : Blocks.DIRT.getDefaultState());
							} else {
								cooldown = 100;
							}
						} else {
							cooldown = 50;
						}
					} else {
						cooldown = 50;
					}
				} else {
					cooldown = 50;
				}
			} else {
				cooldown = 200;
			}
		} else {
			cooldown = 200;
		}
	}

	@Override
	public boolean canHaveInventory(EnumFacing f) {
		return false;
	}

	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		if (!fluidBlocks.isEmpty()) {
			BlockPos pos = fluidBlocks.peek();
			if (pos != null) {
				list.add(new TextComponentTranslation("tomsMod.chat.working", pos.getX(), pos.getY(), pos.getZ()));
			}
		}
		list.add(new TextComponentTranslation("tomsMod.chat.clearing", TomsModUtils.getYesNoMessage(clearing)));
		return list;
	}

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodfactory:textures/blocks/pumpSide.png");
	}

	@Override
	public int[] getOutputSlots() {
		return null;
	}

	@Override
	public int[] getInputSlots() {
		return null;
	}

	@Override
	public void checkItems() {
	}

	@Override
	public void finish() {
	}

	@Override
	public void updateProgress() {
	}

	public void changeClearing() {
		clearing = !clearing;
	}
}
