package com.tom.transport.multipart;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import com.tom.api.grid.IGridDevice;
import com.tom.api.grid.IGridUpdateListener;
import com.tom.api.multipart.ICustomPartBounds;
import com.tom.api.multipart.PartDuct;
import com.tom.api.multipart.PartModule;
import com.tom.apis.TomsModUtils;
import com.tom.transport.TransportInit;
import com.tom.transport.multipart.IInventoryGrid.GridInventory.IInventoryHandler;
import com.tom.transport.multipart.IInventoryGrid.GridInventory.IItemDuct;
import com.tom.transport.multipart.IInventoryGrid.GridInventory.InventoryData;
import com.tom.transport.multipart.IInventoryGrid.GridInventory.TransferingItemStack;

import io.netty.buffer.ByteBuf;

public class PartItemDuct extends PartDuct<IInventoryGrid> implements ICustomPartBounds, IGridUpdateListener, IItemDuct{
	private final AxisAlignedBB connectionBox;
	private static final String TAG_NAME = "tag";
	public PartItemDuct() {
		super(TransportInit.itemDuct,"tomsmodtransport:itemDuct",0.1875, 2);
		double start = 0.5 - 0.25;
		double stop = 0.5 + 0.25;
		this.connectionBox = new AxisAlignedBB(start, 0, start, stop, start, stop);
	}
	//private List<TransferingItemStack> transferingStacks = new ArrayList<TransferingItemStack>();
	private InventoryData[] inventoryDataArray = new InventoryData[6];
	@Override
	public boolean isValidConnection(EnumFacing side, TileEntity tile) {
		return tile != null && (tile instanceof IInventory || (tile instanceof ISidedInventory && ((ISidedInventory)tile).getSlotsForFace(side) != null));
	}

	@Override
	public void updateEntity() {
		if(worldObj.isRemote)return;
		/*for(int i = 0;i<this.transferingStacks.size();i++){
			TransferingItemStack s = this.transferingStacks.get(i);
			if(s != null)
				s.move(this);
		}*/
	}

	@Override
	public IInventoryGrid constructGrid() {
		return new IInventoryGrid();
	}
	/*@Override
	public void addExtraDrops(List<ItemStack> list){
		for(int i = 0;i<this.transferingStacks.size();i++){
			TransferingItemStack s = this.transferingStacks.get(i);
			if(s != null){
				list.add(s.getStack());
			}
		}
	}*/
	@Override
	public AxisAlignedBB getBoxForConnect() {
		return connectionBox;
	}
	@Override
	public void onGridReload() {
		if(!getWorld2().isRemote && grid.getData() != null){
			for(EnumFacing f : EnumFacing.VALUES){
				this.inventoryDataArray[f.ordinal()] = null;
				if(TomsModUtils.getModule(getWorld2(), getPos2(), f) != null){
					PartModule<IInventoryGrid> m = this.getModule(f);
					if(m != null && m instanceof IInventoryHandler){
						this.inventoryDataArray[f.ordinal()] = new InventoryData((IInventoryHandler) m,getPos2(),getPos2());
						continue;
					}
				}
				TileEntity testTe = getWorld2().getTileEntity(getPos2().offset(f));
				if(testTe instanceof IInventory){
					//grid.setData(TileEntityHopper.putStackInInventoryAllSlots((IInventory) testTe, grid.getData(), f.getOpposite()));
					final IInventory inv = TileEntityHopper.getInventoryAtPosition(getWorld2(), testTe.getPos().getX(), testTe.getPos().getY(), testTe.getPos().getZ());
					final EnumFacing facing = f.getOpposite();
					InventoryData d = new InventoryData(new IInventoryHandler(){

						@Override
						public ItemStack pushStack(ItemStack is) {
							return TileEntityHopper.putStackInInventoryAllSlots(inv, is, facing);
						}

						@Override
						public ItemStack pullStack(ItemStack matchTo, ItemHandlerData data) {
							if(matchTo == null || data == null){
								TileEntity te = getWorld2().getTileEntity(getPos2().offset(facing));
								if(te != null && te instanceof IInventory){
									IInventory inv = TileEntityHopper.getInventoryAtPosition(worldObj, te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
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
								TileEntity te = getWorld2().getTileEntity(getPos2().offset(facing));
								if(te != null && te instanceof IInventory){
									IInventory inv = TileEntityHopper.getInventoryAtPosition(worldObj, te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
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

						@Override
						public boolean canPushStack(ItemStack is) {
							return true;
						}

						@Override
						public BlockPos getPos2() {
							return PartItemDuct.this.getPos2();
						}

						@Override
						public EnumFacing getFacing() {
							return facing;
						}

						@Override
						public IGridDevice<IInventoryGrid> getDevice() {
							return PartItemDuct.this;
						}

						@Override
						public boolean isNearestDest() {
							return false;
						}

						@Override
						public boolean isLastDest() {
							return false;
						}

					}, testTe.getPos(), getPos2());
					grid.getData().inventories.add(d);
					this.inventoryDataArray[f.ordinal()] = d;
				}
			}
		}
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
		/*for(TransferingItemStack s : this.transferingStacks)
			s.updateDest(grid, getWorld(), getPos());*/
		//System.out.println(this.grid + " "+this.grid.getData()+" "+this.getPos() + " " + grid.getData().inventories);
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		//NBTTagCompound tag = nbt.getCompoundTag(TAG_NAME);
		/*NBTTagList list = tag.getTagList("items", 10);
		this.transferingStacks.clear();
		for(int i = 0;i<list.tagCount();i++)
			this.transferingStacks.add(TransferingItemStack.loadTransferingItemStackFromNBT(list.getCompoundTagAt(i)));*/
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		/*NBTTagList list = new NBTTagList();
		for(TransferingItemStack s : this.transferingStacks){
			NBTTagCompound t = new NBTTagCompound();
			s.writeToNBT(t);
			list.appendTag(t);
		}
		tag.setTag("items", list);*/
		nbt.setTag(TAG_NAME, tag);
		return nbt;
	}

	@Override
	public void addTransferingItem(TransferingItemStack item) {
		//this.transferingStacks.add(item);
		this.sendUpdatePacket();
	}

	@Override
	public InventoryData[] getConnectionData() {
		return this.inventoryDataArray;
	}

	@Override
	public EnumFacing[] getConnections() {
		List<EnumFacing> fList = new ArrayList<EnumFacing>();
		for(EnumFacing f : EnumFacing.VALUES){
			if(this.connects(f) || this.connectsM(f)){
				fList.add(f);
			}
		}
		return fList.toArray(new EnumFacing[]{});
	}

	@Override
	public void removeTransferingItem(TransferingItemStack item) {
		//if(this.transferingStacks.contains(item))this.transferingStacks.remove(item);
		this.sendUpdatePacket();
	}
	@Override
	public void writeToPacket(ByteBuf buf) {
		/*buf.writeByte(this.transferingStacks.size());
		for(TransferingItemStack s : this.transferingStacks){
			s.writeToPacket(buf);
		}*/
	}
	@Override
	public boolean readFromPacket(ByteBuf buf) {
		/*byte size = buf.readByte();
		this.transferingStacks.clear();
		for(int i = 0;i<size;i++){
			TransferingItemStack s = TransferingItemStack.readTransferingItemStackFromPacket(buf);
			if(s != null){
				this.transferingStacks.add(s);
			}
		}*/
		return false;
	}

	/*@Override
	public List<TransferingItemStack> getTransferingItemStacks() {
		return this.transferingStacks;
	}

	@Override
	public float getMovementSpeed() {
		return 1;
	}*/
	@Override
	public int getPropertyValue(EnumFacing side) {
		return connectsInv(side) ? 2 : connects(side) || connectsM(side) ? 1 : 0;
	}
}
