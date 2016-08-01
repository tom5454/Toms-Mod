package com.tom.storage.multipart;

import java.util.List;

import com.tom.api.multipart.IGuiMultipart;
import com.tom.api.multipart.PartModule;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.StorageInit;
import com.tom.storage.tileentity.gui.GuiExportBus;
import com.tom.storage.tileentity.inventory.ContainerExportBus;

import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class PartExportBus extends PartModule<StorageNetworkGrid> implements IGuiMultipart{
	public PartExportBus() {
		this(EnumFacing.NORTH);
	}
	public PartExportBus(EnumFacing face) {
		super(StorageInit.exportBus, 0.25, 0.25, face, "tomsmodstorage:tm.exportBus", 2);
	}
	private boolean checkNBT = true;
	private boolean checkMeta = true;
	private boolean checkMod = false;
	private static final String TAG_NBT_NAME = "config";
	private int transferCooldown = 1;
	public InventoryBasic filterInv = new InventoryBasic("", false, 9);
	public InventoryBasic upgradeInv = new InventoryBasic("", false, 1);
	private ItemStack stuckStack = null;
	@Override
	public void onGridReload() {

	}

	@Override
	public void onGridPostReload() {

	}

	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			grid.drainEnergy(0.5D);
			int upgradeCount = getSpeedUpgradeCount();
			if(transferCooldown == 0){
				if(stuckStack != null){
					stuckStack = grid.pushStack(stuckStack);
					transferCooldown = 10;
				}else{
					BlockPos pos = getPos2().offset(facing);
					IInventory inv = TileEntityHopper.getInventoryAtPosition(worldObj, pos.getX(), pos.getY(), pos.getZ());
					if(inv != null){
						for(int i = 0;i<filterInv.getSizeInventory();i++){
							ItemStack valid = filterInv.getStackInSlot(i);
							if(valid != null){
								valid = valid.copy();
								valid.stackSize = Math.min(getMaxPacketSize(), valid.getMaxStackSize());
								ItemStack pulledStack = grid.pullStack(valid, checkMeta, checkNBT, checkMod);
								if(pulledStack != null){
									pulledStack = TileEntityHopper.putStackInInventoryAllSlots(inv, pulledStack, facing.getOpposite());
									if(pulledStack != null){
										pulledStack = grid.pushStack(pulledStack);
										if(pulledStack != null){
											stuckStack = pulledStack;
										}
									}
									transferCooldown = upgradeCount > 3 ? (upgradeCount > 5 ? 2 : 5) : 16;
									break;
								}
							}
						}
						if(transferCooldown == 0)transferCooldown = upgradeCount > 4 ? 24 : 60;
					}
				}
			}else if(transferCooldown > 0)transferCooldown--;
			else transferCooldown = 1;
		}
	}

	@Override
	public int getState() {
		return grid.isPowered() ? 2 : 0;
	}

	@Override
	public Object getGui(EntityPlayer player) {
		return new GuiExportBus(this, player.inventory);
	}

	@Override
	public Object getContainer(EntityPlayer player) {
		return new ContainerExportBus(this, player.inventory);
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {

	}

	@Override
	public PartSlot getPosition() {
		return PartSlot.getFaceSlot(facing);
	}
	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand,
			ItemStack heldItem, PartMOP hit) {
		BlockPos pos = getPos2();
		if(!player.worldObj.isRemote)player.openGui(CoreInit.modInstance, GuiIDs.getMultipartGuiId(facing).ordinal(), getWorld2(), pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	private int getMaxPacketSize(){
		int stackSize = getSpeedUpgradeCount();
		return stackSize == 0 ? 1 : (stackSize == 1 ? 2 : (stackSize == 2 ? 4 : (stackSize == 3 ? 8 : (stackSize == 4 ? 16 : (stackSize == 5 ? 24 : (stackSize == 6 ? 32 : (stackSize == 7 ? 48 : 64)))))));
	}
	private int getSpeedUpgradeCount(){
		return upgradeInv.getStackInSlot(0) != null ? upgradeInv.getStackInSlot(0).stackSize : 0;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("checkMeta", checkMeta);
		tag.setBoolean("checkNBT", checkNBT);
		tag.setBoolean("checkMod", checkMod);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < filterInv.getSizeInventory(); ++i)
		{
			if (filterInv.getStackInSlot(i) != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				filterInv.getStackInSlot(i).writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		tag.setTag("itemList", list);
		list = new NBTTagList();
		for (int i = 0; i < upgradeInv.getSizeInventory(); ++i)
		{
			if (upgradeInv.getStackInSlot(i) != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				upgradeInv.getStackInSlot(i).writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		tag.setTag("upgradeList", list);
		tag.setInteger("cooldown", this.transferCooldown);
		nbt.setTag(TAG_NBT_NAME, tag);
		return nbt;
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagCompound tag = nbt.getCompoundTag(TAG_NBT_NAME);
		this.checkMeta = tag.getBoolean("checkMeta");
		this.checkNBT = tag.getBoolean("checkNBT");
		this.checkMod = tag.getBoolean("checkMod");
		NBTTagList list = tag.getTagList("itemList", 10);
		filterInv.clear();
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < filterInv.getSizeInventory())
			{
				filterInv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
		list = tag.getTagList("upgradeList", 10);
		upgradeInv.clear();
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < upgradeInv.getSizeInventory())
			{
				upgradeInv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
		this.transferCooldown = tag.getInteger("cooldown");
	}
	@Override
	public void addExtraDrops(List<ItemStack> list) {
		if(stuckStack != null){
			list.add(stuckStack);
			stuckStack = null;
		}
		for(int i = 0;i<upgradeInv.getSizeInventory();i++){
			ItemStack stack = upgradeInv.removeStackFromSlot(i);
			if(stack != null)list.add(stack);
		}
	}
}
