package com.tom.transport.multipart;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import com.tom.api.grid.IGridDevice;
import com.tom.api.multipart.PartDuct;
import com.tom.api.multipart.PartModule;
import com.tom.apis.TomsModUtils;
import com.tom.transport.TransportInit;
import com.tom.transport.multipart.IInventoryGrid.GridInventory.IInventoryHandler;
import com.tom.transport.multipart.IInventoryGrid.GridInventory.IItemDuct;
import com.tom.transport.multipart.IInventoryGrid.GridInventory.InventoryData;

import mcmultipart.raytrace.PartMOP;

public class PartServo extends PartModule<IInventoryGrid> implements IInventoryHandler{
	public PartServo() {
		this(EnumFacing.NORTH);
	}
	public PartServo(EnumFacing face) {
		super(TransportInit.servo, 0.25, 0.25, face, "tomsmodtransport:tm.servo",1);
	}
	private ItemStack[] validItems = new ItemStack[15];
	private boolean checkNBT = true;
	private boolean checkMeta = true;
	private boolean checkMod = true;
	//private boolean isLastDest = false;
	//private boolean isNearestDest = false;
	//private int maxItemsInInventory = -1;
	private boolean isWhiteList = false;
	private static final String TAG_NBT_NAME = "config";
	private int maxStackSize = 64;
	private int transferCooldown = 1;
	private ItemStack stuckStack;
	@Override
	public IInventoryGrid constructGrid() {
		return new IInventoryGrid();
	}

	@Override
	public void updateEntity() {
		if(worldObj.isRemote)return;
		if(this.transferCooldown > 0){
			this.transferCooldown--;
		}else{
			if(stuckStack != null){
				InventoryData data = this.grid.getData().findNearestInventory(pos, stuckStack,new ArrayList<InventoryData>());
				List<InventoryData> tried = new ArrayList<InventoryData>();
				while(data != null && stuckStack != null){
					//duct.addTransferingItem(TransferingItemStack.getTransferingItemStackFromInventoryData(data, is, facing.getOpposite()));
					tried.add(data);
					if(data.inventory.canPushStack(stuckStack)){
						stuckStack = data.inventory.pushStack(stuckStack);
						this.transferCooldown = 8;
						if(stuckStack != null && stuckStack.stackSize > 0){
							BlockPos pos = getPos2().offset(facing);
							IInventory inv = TileEntityHopper.getInventoryAtPosition(getWorld2(), pos.getX(), pos.getY(), pos.getZ());
							if(inv != null){
								stuckStack = TileEntityHopper.putStackInInventoryAllSlots(inv, stuckStack, facing.getOpposite());
							}
							if(stuckStack != null && stuckStack.stackSize < 1)stuckStack = null;
							this.transferCooldown = 16;
						}
					}
					data = this.grid.getData().findNearestInventory(pos, stuckStack,tried);
				}
			}else{
				PartDuct<IInventoryGrid> baseDuct = this.getBaseDuct();
				if(baseDuct != null && baseDuct instanceof IItemDuct){
					//IItemDuct duct = (IItemDuct) baseDuct;
					boolean allNull = true;
					for(ItemStack i : validItems){
						if(i != null){
							allNull = false;
							ItemStack is = this.pullStack(i, new ItemHandlerData(checkMeta, checkNBT, checkMod, isWhiteList, maxStackSize));
							if(is != null){
								InventoryData data = this.grid.getData().findNearestInventory(pos, is,new ArrayList<InventoryData>());
								List<InventoryData> tried = new ArrayList<InventoryData>();
								while(data != null && is != null){
									//duct.addTransferingItem(TransferingItemStack.getTransferingItemStackFromInventoryData(data, is, facing.getOpposite()));
									tried.add(data);
									if(data.inventory.canPushStack(is)){
										is = data.inventory.pushStack(is);
										this.transferCooldown = 8;
										if(is != null && is.stackSize > 0){
											BlockPos pos = getPos2().offset(facing);
											IInventory inv = TileEntityHopper.getInventoryAtPosition(getWorld2(), pos.getX(), pos.getY(), pos.getZ());
											if(inv != null){
												is = TileEntityHopper.putStackInInventoryAllSlots(inv, is, facing.getOpposite());
											}
										}
									}
									data = this.grid.getData().findNearestInventory(pos, is, tried);
								}
								if(is != null && is.stackSize > 0){
									stuckStack = is;
									this.transferCooldown = 16;
									break;
								}
							}
						}
					}
					if(allNull){
						ItemStack is = this.pullStack(null, new ItemHandlerData(checkMeta, checkNBT, checkMod, isWhiteList, maxStackSize));
						if(is != null){
							InventoryData data = this.grid.getData().findNearestInventory(pos, is,new ArrayList<InventoryData>());
							List<InventoryData> tried = new ArrayList<InventoryData>();
							while(data != null && is != null){
								//duct.addTransferingItem(TransferingItemStack.getTransferingItemStackFromInventoryData(data, is, facing.getOpposite()));
								tried.add(data);
								if(data.inventory.canPushStack(is)){
									is = data.inventory.pushStack(is);
									this.transferCooldown = 8;
									if(is != null && is.stackSize > 0){
										BlockPos pos = getPos2().offset(facing);
										IInventory inv = TileEntityHopper.getInventoryAtPosition(getWorld2(), pos.getX(), pos.getY(), pos.getZ());
										if(inv != null){
											is = TileEntityHopper.putStackInInventoryAllSlots(inv, is, facing.getOpposite());
										}
									}
								}
								data = this.grid.getData().findNearestInventory(pos, is, tried);
							}
							if(is != null && is.stackSize > 0){
								stuckStack = is;
								this.transferCooldown = 16;
							}
						}
					}
				}
			}
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("checkMeta", checkMeta);
		tag.setBoolean("checkNBT", checkNBT);
		tag.setBoolean("checkMod", checkMod);
		//tag.setBoolean("isLastDest", isLastDest);
		//tag.setBoolean("isNearestDest", isNearestDest);
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
		tag.setInteger("max", maxStackSize);
		tag.setInteger("cooldown", this.transferCooldown);
		nbt.setTag(TAG_NBT_NAME, tag);
		NBTTagCompound stuckTag = new NBTTagCompound();
		if(stuckStack != null)stuckStack.writeToNBT(stuckTag);
		nbt.setTag("stuck", stuckTag);
		return nbt;
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagCompound tag = nbt.getCompoundTag(TAG_NBT_NAME);
		this.checkMeta = tag.getBoolean("checkMeta");
		this.checkNBT = tag.getBoolean("checkNBT");
		this.checkMod = tag.getBoolean("checkMod");
		//this.isLastDest = tag.getBoolean("isLastDest");
		//this.isNearestDest = tag.getBoolean("isNearestDest");
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
		this.maxStackSize = tag.getInteger("max");
		this.transferCooldown = tag.getInteger("cooldown");
		stuckStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stuck"));
	}
	@Override
	public void onGridReload() {

	}
	@Override
	public ItemStack pushStack(ItemStack stack){
		return stack;
	}

	@Override
	public ItemStack pullStack(ItemStack matchTo, ItemHandlerData data) {
		if(matchTo == null || data == null){
			BlockPos pos = getPos2().offset(facing);
			IInventory inv = TileEntityHopper.getInventoryAtPosition(getWorld2(), pos.getX(), pos.getY(), pos.getZ());
			if(inv != null){
				EnumFacing side = facing.getOpposite();
				int extract = data != null && data.maxStackSize > 0 ? data.maxStackSize : 64;
				for(int i = 0;i<inv.getSizeInventory();i++){
					ItemStack stack = inv.getStackInSlot(i);
					if(stack != null && canExtractItemFromSlot(inv, stack, i, side)){
						return inv.decrStackSize(i, extract);
					}
				}
			}
		}else{
			BlockPos pos = getPos2().offset(facing);
			IInventory inv = TileEntityHopper.getInventoryAtPosition(getWorld2(), pos.getX(), pos.getY(), pos.getZ());
			if(inv != null){
				EnumFacing side = facing.getOpposite();
				int extract = data != null && data.maxStackSize > 0 ? data.maxStackSize : 64;
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
			}
		}
		return null;
	}
	/**
	 * Can this item duct extract the specified item from the specified slot on the specified side?
	 */
	private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
	{
		return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
	}
	@Override
	public void onGridPostReload() {

	}
	@Override
	public boolean canPushStack(ItemStack is) {
		/*for(int i = 0;i<validItems.length;i++){
			ItemStack c = validItems[i];
			if(c != null){
				if(TomsModUtils.areItemStacksEqual(is, c, checkMeta, checkNBT, checkMod)){
					if(this.isWhiteList){
						TileEntity te = getWorld().getTileEntity(getPos().offset(facing));
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
			TileEntity te = getWorld().getTileEntity(getPos().offset(facing));
			if(te instanceof IInventory){
				IInventory inv = (IInventory) te;
				for(int j = 0;j<inv.getSizeInventory();j++){
					ItemStack stack = inv.getStackInSlot(j);
					if(canInsertItemInSlot(inv, stack, j, facing.getOpposite())) return true;
				}
				return false;
			}
			else return false;
		}*/
		return false;
	}
	@Override
	public IGridDevice<IInventoryGrid> getDevice() {
		return this;
	}
	@Override
	public boolean isNearestDest() {
		return false;
	}
	@Override
	public boolean isLastDest() {
		return false;
	}
	@Override
	public int getState() {
		return 1;
	}
	@Override
	public boolean onPartActivated(EntityPlayer player, EnumHand hand,
			ItemStack heldItem, PartMOP hit) {
		return true;
	}
}