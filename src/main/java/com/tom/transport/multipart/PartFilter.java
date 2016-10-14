package com.tom.transport.multipart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import com.tom.api.grid.IGridDevice;
import com.tom.api.multipart.PartModule;
import com.tom.apis.TomsModUtils;
import com.tom.transport.TransportInit;
import com.tom.transport.multipart.IInventoryGrid.GridInventory.IInventoryHandler;
import com.tom.transport.multipart.IInventoryGrid.GridInventory.InventoryData;

import mcmultipart.raytrace.PartMOP;

public class PartFilter extends PartModule<IInventoryGrid> implements IInventoryHandler{
	public PartFilter() {
		this(EnumFacing.NORTH);
	}
	public PartFilter(EnumFacing face) {
		super(TransportInit.filter, 0.25, 0.25, face, "tomsmodtransport:tm.filter",1);
	}
	private ItemStack[] validItems = new ItemStack[15];
	private boolean checkNBT = true;
	private boolean checkMeta = true;
	private boolean checkMod = true;
	private boolean isLastDest = false;
	private boolean isNearestDest = false;
	//private int maxItemsInInventory = -1;
	private boolean isWhiteList = false;
	private static final String TAG_NBT_NAME = "config";
	private boolean allowOverSending = false;
	@Override
	public IInventoryGrid constructGrid() {
		return new IInventoryGrid();
	}

	@Override
	public void updateEntity() {

	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("checkMeta", checkMeta);
		tag.setBoolean("checkNBT", checkNBT);
		tag.setBoolean("checkMod", checkMod);
		tag.setBoolean("isLastDest", isLastDest);
		tag.setBoolean("isNearestDest", isNearestDest);
		tag.setBoolean("whitelist", isWhiteList);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < this.validItems.length; ++i)
		{
			if (this.validItems[i] != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				this.validItems[i].writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		tag.setTag("itemList", list);
		//tag.setInteger("max", this.maxItemsInInventory);
		tag.setBoolean("allowOverSending", allowOverSending);
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
		this.isLastDest = tag.getBoolean("isLastDest");
		this.isNearestDest = tag.getBoolean("isNearestDest");
		this.isWhiteList = tag.getBoolean("whitelist");
		NBTTagList list = tag.getTagList("itemList", 10);
		this.validItems = new ItemStack[15];
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < this.validItems.length)
			{
				this.validItems[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		//this.maxItemsInInventory = tag.getInteger("max");
		this.allowOverSending = tag.getBoolean("allowOverSending");
	}
	@Override
	public void onGridReload() {
		if(!getWorld2().isRemote){
			BlockPos offset = getPos2().offset(facing);
			TileEntity te = getWorld2().getTileEntity(offset);
			if(te instanceof IInventory){
				grid.getData().inventories.add(new InventoryData(this, offset, getPos2()));
			}
		}
	}
	@Override
	public ItemStack pushStack(ItemStack stack){
		for(int i = 0;i<validItems.length;i++){
			ItemStack c = validItems[i];
			if(c != null){
				if(TomsModUtils.areItemStacksEqual(stack, c, checkMeta, checkNBT, checkMod)){
					if(this.isWhiteList){
						TileEntity te = getWorld2().getTileEntity(getPos2().offset(facing));
						if(te instanceof IInventory){
							IInventory inv = TileEntityHopper.getInventoryAtPosition(worldObj, te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
							return TileEntityHopper.putStackInInventoryAllSlots(inv, stack, facing.getOpposite());
						}
						else return stack;
					}else{
						return stack;
					}
				}
			}
		}
		if(!this.isWhiteList){
			TileEntity te = getWorld2().getTileEntity(getPos2().offset(facing));
			if(te instanceof IInventory)return TileEntityHopper.putStackInInventoryAllSlots((IInventory) te, stack, facing.getOpposite());
			else return stack;
		}
		return stack;
	}
	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand,
			ItemStack heldItem, PartMOP hit) {
		return true;
	}

	@Override
	public ItemStack pullStack(ItemStack matchTo, ItemHandlerData data) {
		return null;
	}
	@Override
	public void onGridPostReload() {

	}
	@Override
	public boolean canPushStack(ItemStack is) {
		for(int i = 0;i<validItems.length;i++){
			ItemStack c = validItems[i];
			if(c != null){
				if(TomsModUtils.areItemStacksEqual(is, c, checkMeta, checkNBT, checkMod)){
					if(this.isWhiteList){
						TileEntity te = getWorld2().getTileEntity(getPos2().offset(facing));
						if(te instanceof IInventory){
							IInventory inv = (IInventory) te;
							for(int j = 0;j<inv.getSizeInventory();j++){
								ItemStack stack = inv.getStackInSlot(j);
								if(canInsertItemInSlot(inv, stack, j, facing.getOpposite())) return true;
							}
							return false;
						}
						else return false;
					}else{
						return false;
					}
				}
			}
		}
		if(!this.isWhiteList){
			TileEntity te = getWorld2().getTileEntity(getPos2().offset(facing));
			if(te instanceof IInventory){
				IInventory inv = (IInventory) te;
				for(int j = 0;j<inv.getSizeInventory();j++){
					ItemStack stack = inv.getStackInSlot(j);
					if(canInsertItemInSlot(inv, stack, j, facing.getOpposite())) return true;
				}
				return false;
			}
			else return false;
		}
		return false;
	}
	/**
	 * Can this hopper insert the specified item from the specified slot on the specified side?
	 */
	private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
	{
		return !inventoryIn.isItemValidForSlot(index, stack) ? false : !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canInsertItem(index, stack, side);
	}
	@Override
	public IGridDevice<IInventoryGrid> getDevice() {
		return this;
	}
	@Override
	public boolean isNearestDest() {
		return isNearestDest;
	}
	@Override
	public boolean isLastDest() {
		return isLastDest;
	}
	@Override
	public int getState() {
		return 0;
	}
}
