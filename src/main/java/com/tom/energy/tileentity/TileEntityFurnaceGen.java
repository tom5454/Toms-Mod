package com.tom.energy.tileentity;

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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldType;

import com.tom.api.block.IGridPowerGenerator;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.handler.TMPlayerHandler;
import com.tom.util.TomsModUtils;

import com.tom.energy.block.Generator;

public class TileEntityFurnaceGen extends TileEntityTomsMod implements IInventory, IGridPowerGenerator, ICustomMultimeterInformation {
	private InventoryBasic inv = new InventoryBasic("fg", false, 1);
	private int totalBurnTime, burnTime;
	private ItemStack currentlyBurning;
	protected TMPlayerHandler playerHandler;
	public String playerName;

	@Override
	public boolean isValid() {
		return !isInvalid();
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
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public String getName() {
		return "tm.fg";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void onLoad() {
		tileOnLoad();
	}

	@Override
	public void updatePlayerHandler() {
		playerHandler = TMPlayerHandler.getPlayerHandlerForName(playerName);
	}

	@Override
	public String getOwnerName() {
		return playerName;
	}

	@Override
	public void setOwner(String owner) {
		this.playerName = owner;
	}

	@Override
	public long getMaxPowerGen() {
		return 1000;
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
		return index == 0 && TomsModUtils.getBurnTime(stack) > 0;
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
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("totalBurnTime", totalBurnTime);
		compound.setInteger("burnTime", burnTime);
		TomsModUtils.writeInventory("inv", compound, inv);
		compound.setString("placer", playerName);
		NBTTagCompound fuelTag = new NBTTagCompound();
		if (this.currentlyBurning != null)
			this.currentlyBurning.writeToNBT(fuelTag);
		compound.setTag("currentlyBurning", fuelTag);
		return super.writeToNBT(compound);
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		totalBurnTime = compound.getInteger("totalBurnTime");
		burnTime = compound.getInteger("burnTime");
		TomsModUtils.loadAllItems(compound, "inv", inv);
		playerName = compound.getString("placer");
		this.currentlyBurning = TomsModUtils.loadItemStackFromNBT(compound.getCompoundTag("currentlyBurning"));
		super.readFromNBT(compound);
	}
	@Override
	public void updateEntity(IBlockState state) {
		if(!world.isRemote && playerHandler != null){
			if((totalBurnTime - burnTime) < 1 && (pos.getY() > 48 && pos.getY() < 150) || world.getWorldType() == WorldType.FLAT){
				ItemStack fss = this.inv.getStackInSlot(0);
				int itemBurnTime = TomsModUtils.getBurnTime(fss);
				if (itemBurnTime > 0) {
					burnTime = 0;
					this.totalBurnTime = itemBurnTime*20;
					this.currentlyBurning = this.inv.getStackInSlot(0);
					this.inv.setInventorySlotContents(0, ItemStack.EMPTY);
					if (!fss.getItem().getContainerItem(fss).isEmpty()) {
						ItemStack s = fss.getItem().getContainerItem(fss);
						BlockPos invP = pos.up();
						IInventory inv = TileEntityHopper.getInventoryAtPosition(world, invP.getX(), invP.getY(), invP.getZ());
						if (inv != null)
							s = TileEntityHopper.putStackInInventoryAllSlots(inv, inv, s, EnumFacing.DOWN);
						if (!s.isEmpty()) {
							EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, fss.getItem().getContainerItem(fss));
							item.motionY = 0.2;
							world.spawnEntity(item);
						}
					}
					if (!state.getValue(Generator.ACTIVE)) {
						TomsModUtils.setBlockState(world, pos, state.withProperty(Generator.ACTIVE, true), 2);
					}
				} else {
					if (!fss.isEmpty()) {
						EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, fss);
						world.spawnEntity(item);
					}
					if (state.getValue(Generator.ACTIVE)) {
						TomsModUtils.setBlockState(world, pos, state.withProperty(Generator.ACTIVE, false), 2);
					}
				}
				this.markDirty();
			}else if((totalBurnTime - burnTime) > 0){
				burnTime++;
				playerHandler.gridPower = Math.max(playerHandler.gridPower-1000, 0);
				playerHandler.gridPowerGenerators.add(this);
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
		}
	}
	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		if (!inv.getStackInSlot(0).isEmpty()) {
			if ((totalBurnTime - burnTime) > 0) {
				list.add(new TextComponentTranslation("tomsMod.chat.burnTime", totalBurnTime - burnTime));
				list.add(new TextComponentTranslation("tomsMod.chat.currentlyBurning", currentlyBurning != null && !currentlyBurning.isEmpty() ? currentlyBurning.getTextComponent() : new TextComponentTranslation("tomsMod.na")));
				list.add(new TextComponentTranslation("tomsMod.chat.inventory", !inv.getStackInSlot(0).isEmpty() ? inv.getStackInSlot(0).getTextComponent() : new TextComponentTranslation("tomsMod.na")));
			} else {
				list.add(new TextComponentTranslation("tomsMod.chat.inventory", !inv.getStackInSlot(0).isEmpty() ? inv.getStackInSlot(0).getTextComponent() : new TextComponentTranslation("tomsMod.na")));
			}
		} else if ((totalBurnTime - burnTime) > 0) {
			list.add(new TextComponentTranslation("tomsMod.chat.burnTime", totalBurnTime - burnTime));
			list.add(new TextComponentTranslation("tomsMod.chat.currentlyBurning", currentlyBurning != null && !currentlyBurning.isEmpty() ? currentlyBurning.getTextComponent() : new TextComponentTranslation("tomsMod.na")));
		}
		if(playerHandler != null){
			if(playerHandler.underpowered)list.add(new TextComponentTranslation("tomsMod.chat.underpowered"));
		}
		return list;
	}
}
