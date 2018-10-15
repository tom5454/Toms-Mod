package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockBlastFurnace;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;
import com.tom.util.TomsModUtils;

public class TileEntityBlastFurnace extends TileEntityTomsMod implements ISidedInventory {
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	protected int progress = -1;
	protected int burnTime = 0;
	private int totalBurnTime = 0;
	private int maxProgress = 0;
	private static final int[] SLOTS = new int[]{0, 1, 2};

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("inventory", TomsModUtils.saveAllItems(inv));
		tag.setInteger("progress", progress);
		tag.setInteger("burnTime", burnTime);
		tag.setInteger("maxProgress", maxProgress);
		tag.setInteger("totalBurnTime", totalBurnTime);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		TomsModUtils.loadAllItems(tag.getTagList("inventory", 10), inv);
		this.progress = tag.getInteger("progress");
		this.burnTime = tag.getInteger("burnTime");
		this.maxProgress = tag.getInteger("maxProgress");
		this.totalBurnTime = tag.getInteger("totalBurnTime");
	}

	@Override
	public int getSizeInventory() {
		return 3;
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
		return id == 0 ? progress : id == 1 ? maxProgress : id == 2 ? burnTime : id == 3 ? totalBurnTime : 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0)
			burnTime = value;
		else if (id == 1)
			progress = value;
		// else if(id == 1)maxProgress = value;
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public String getName() {
		return "blastFurnace";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack is, EnumFacing direction) {
		return index == 0 || (index == 2 && is != null && (is.getItem() == FactoryInit.coalCoke || (is.getItem() == Items.COAL && is.getMetadata() == 1)));
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
					if (burnTime > 0) {
						updateProgress();
					} else {
						if ((inv.getStackInSlot(2).getItem() == FactoryInit.coalCoke || (inv.getStackInSlot(2).getItem() == Items.COAL && inv.getStackInSlot(2).getMetadata() == 1) || inv.getStackInSlot(2).getItem() == Item.getItemFromBlock(FactoryInit.blockCoalCoke))) {
							totalBurnTime = burnTime = inv.getStackInSlot(2).getItem() == FactoryInit.coalCoke ? 3200 : (inv.getStackInSlot(2).getItem() == Item.getItemFromBlock(FactoryInit.blockCoalCoke) ? 28800 : 1600);
							decrStackSize(2, 1);
						}
					}
				} else if (progress == 0) {
					ItemStackChecker s = MachineCraftingHandler.getBlastFurnaceOutput(inv.getStackInSlot(0), ItemStack.EMPTY, 0);
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
					} else {
						progress = -1;
						maxProgress = 0;
					}
				} else {
					ItemStackChecker s = MachineCraftingHandler.getBlastFurnaceOutput(inv.getStackInSlot(0), ItemStack.EMPTY, 0);
					if (s != null) {
						if (!inv.getStackInSlot(1).isEmpty()) {
							if (TomsModUtils.areItemStacksEqual(inv.getStackInSlot(1), s.getStack(), true, true, false) && inv.getStackInSlot(1).getCount() + s.getStack().getCount() <= s.getStack().getMaxStackSize() && inv.getStackInSlot(0).getCount() >= s.getExtra()) {
								maxProgress = s.getExtra3();
								progress = maxProgress;
							}
						} else {
							maxProgress = s.getExtra3();
							progress = maxProgress;
						}
					}
					TomsModUtils.setBlockStateWithCondition(world, pos, BlockBlastFurnace.STATE, progress > 0 ? 2 : 1);
				}
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, BlockBlastFurnace.STATE, 0);
			}
		}
	}

	protected void updateProgress() {
		progress--;
		burnTime--;
	}

	public boolean checkIfMerged(IBlockState state) {
		EnumFacing facing = state.getValue(BlockBlastFurnace.FACING);
		BlockPos center = pos.offset(facing, 2);
		boolean isValid = check3x3(center.up(3)) && check3x3(center);
		isValid = isValid && checkMid(center.up()) && checkMid(center.up(2));
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

	private boolean checkMid(BlockPos center) {
		boolean ret = world.getBlockState(center).getBlock() == Blocks.LAVA;
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
		return world.getBlockState(pos).getBlock() == FactoryInit.blastFurnaceWall;
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
