package com.tom.storage.multipart;

import java.util.List;

import com.tom.api.multipart.IGuiMultipart;
import com.tom.api.multipart.PartModule;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.StorageInit;
import com.tom.storage.tileentity.gui.GuiImportBus;
import com.tom.storage.tileentity.inventory.ContainerImportBus;

import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class PartImportBus extends PartModule<StorageNetworkGrid> implements IGuiMultipart{
	public PartImportBus() {
		this(EnumFacing.NORTH);
	}
	private boolean checkNBT = true;
	private boolean checkMeta = true;
	private boolean checkMod = false;
	private boolean isWhiteList = true;
	private static final String TAG_NBT_NAME = "config";
	private int transferCooldown = 1;
	private ItemStack stuckStack = null;
	public InventoryBasic filterInv = new InventoryBasic("", false, 9);
	public InventoryBasic upgradeInv = new InventoryBasic("", false, 1);
	public PartImportBus(EnumFacing face) {
		super(StorageInit.importBus, 0.25, 0.25, face, "tomsmodstorage:tm.importBus", 2);
	}

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
			int pulledStacks = 0;
			int upgradeCount = getSpeedUpgradeCount();
			if(transferCooldown == 0){
				if(stuckStack != null){
					stuckStack = grid.pushStack(stuckStack);
					transferCooldown = 4;
				}else{
					BlockPos pos = getPos2().offset(facing);
					IInventory inv = TileEntityHopper.getInventoryAtPosition(worldObj, pos.getX(), pos.getY(), pos.getZ());
					if(inv != null){
						boolean allNull = true;
						for(int i = 0;i<filterInv.getSizeInventory();i++){
							ItemStack valid = filterInv.getStackInSlot(i);
							if(valid != null){
								allNull = false;
								ItemStack pulledStack = pullStack(inv, this, getMaxPacketSize(), valid, facing.getOpposite());
								if(pulledStack != null){
									pulledStack = grid.pushStack(pulledStack);
									if(pulledStack != null){
										stuckStack = pulledStack;
									}
									transferCooldown = upgradeCount > 1 ? 2 : 5;
									pulledStacks++;
									if(upgradeCount < 3 || pulledStacks > 1)break;
								}
							}
						}
						if(transferCooldown == 0)transferCooldown = upgradeCount > 2 ? 16 : 40;
						if(allNull){
							EnumFacing side = facing.getOpposite();
							int extract = getMaxPacketSize();
							for(int i = 0;i<inv.getSizeInventory();i++){
								ItemStack stack = inv.getStackInSlot(i);
								if(stack != null && canExtractItemFromSlot(inv, stack, i, side)){
									ItemStack pulledStack = inv.decrStackSize(i, extract);
									if(pulledStack != null){
										pulledStack = grid.pushStack(pulledStack);
										if(pulledStack != null){
											stuckStack = pulledStack;
										}
										pulledStacks++;
										transferCooldown = upgradeCount > 1 ? 2 : 5;
										if(upgradeCount < 3 || pulledStacks > 1)break;
									}
								}
							}
							if(transferCooldown == 0)transferCooldown = upgradeCount > 2 ? 16 : 40;
						}
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
	/**
	 * Can this import bus extract the specified item from the specified slot on the specified side?
	 */
	private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
	{
		return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
	}
	private static ItemStack pullStack(IInventory inv, PartImportBus data, int extract, ItemStack matchTo, EnumFacing side){
		for(int i = 0;i<inv.getSizeInventory();i++){
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null){
				if(TomsModUtils.areItemStacksEqual(stack, matchTo, data.checkMeta, data.checkNBT, data.checkMod)){
					if(data.isWhiteList){
						if(canExtractItemFromSlot(inv, stack, i, side)){
							return inv.decrStackSize(i, extract);
						}
					}
				}else if(!data.isWhiteList){
					if(canExtractItemFromSlot(inv, stack, i, side)){
						return inv.decrStackSize(i, extract);
					}
				}
			}
		}
		return null;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("checkMeta", checkMeta);
		tag.setBoolean("checkNBT", checkNBT);
		tag.setBoolean("checkMod", checkMod);
		tag.setBoolean("whitelist", isWhiteList);
		NBTTagCompound stuckStackTag = new NBTTagCompound();
		if(stuckStack != null)stuckStack.writeToNBT(stuckStackTag);
		tag.setTag("stuck", stuckStackTag);
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
		this.isWhiteList = tag.getBoolean("whitelist");
		NBTTagCompound stuckTag = tag.getCompoundTag("stuck");
		stuckStack = ItemStack.loadItemStackFromNBT(stuckTag);
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
	public Object getGui(EntityPlayer player) {
		return new GuiImportBus(this, player.inventory);
	}

	@Override
	public Object getContainer(EntityPlayer player) {
		return new ContainerImportBus(this, player.inventory);
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if(id == 0){
			isWhiteList = extra == 1;
		}
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
		return stackSize == 0 ? 16 : (stackSize == 1 ? 32 : 64);
	}
	private int getSpeedUpgradeCount(){
		return upgradeInv.getStackInSlot(0) != null ? upgradeInv.getStackInSlot(0).stackSize : 0;
	}
	public boolean isWhiteList(){
		return isWhiteList;
	}
	public void setWhiteList(boolean isWhiteList) {
		this.isWhiteList = isWhiteList;
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
