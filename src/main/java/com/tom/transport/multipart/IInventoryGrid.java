package com.tom.transport.multipart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.tom.api.grid.GridBase;
import com.tom.api.grid.IGridDevice;
import com.tom.api.multipart.PartDuct;
import com.tom.api.multipart.PartModule;
import com.tom.apis.TomsModUtils;
import com.tom.transport.multipart.IInventoryGrid.GridInventory;

import io.netty.buffer.ByteBuf;
import mcmultipart.microblock.IMicroblock;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;

public class IInventoryGrid extends GridBase<GridInventory, IInventoryGrid> {
	private GridInventory currentStack = new GridInventory();

	@Override
	public IInventoryGrid importFromNBT(NBTTagCompound tag) {
		this.currentStack = new GridInventory().readFromNBT(tag);
		return this;
	}

	@Override
	public void updateGrid(World world, IGridDevice<IInventoryGrid> master) {
		this.currentStack.update();
	}

	@Override
	public GridInventory getData() {
		return currentStack;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		if(this.currentStack != null){
			this.currentStack.writeToNBT(tag);
		}
	}

	@Override
	public void setData(GridInventory data) {
		this.currentStack = data;
	}
	@Override
	public void onForceUpdateDone(){
		this.currentStack.inventories.clear();
	}
	public static class GridInventory{
		protected List<InventoryData> inventories = new ArrayList<InventoryData>();
		/*private List<TransferingItemStack> items = new ArrayList<TransferingItemStack>();
		 */public GridInventory readFromNBT(NBTTagCompound tag) {/*
			items = new ArrayList<TransferingItemStack>();
			NBTTagList list = tag.getTagList("inv", 10);
			for(int i = 0;i<list.tagCount();i++){
				TransferingItemStack is = TransferingItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
				if(is != null){
					items.add(is);
				}
			}*/
			 return this;
		 }
		 public void writeToNBT(NBTTagCompound tag) {/*
			NBTTagList list = new NBTTagList();
			for(int i = 0;i<items.size();i++){
				TransferingItemStack c = items.get(i);
				if(c != null){
					NBTTagCompound t = new NBTTagCompound();
					c.writeToNBT(t);
					list.appendTag(t);
				}
			}
			tag.setTag("inv", list);*/
		 }
		 /*
		public int getSizeInventory() {
			return items.size();
		}

		public ItemStack getStackInSlot(int index) {
			return items.size() > index ? items.get(index).stack : null;
		}

		public ItemStack decrStackSize(int slot, int amount) {
			if (this.getStackInSlot(slot) != null) {
				ItemStack itemstack;
				if (this.getStackInSlot(slot).stackSize <= amount) {
					itemstack = this.getStackInSlot(slot);
					this.setInventorySlotContents(slot,null);
					return itemstack;
				} else {
					itemstack = this.getStackInSlot(slot).splitStack(amount);

					if (this.getStackInSlot(slot).stackSize == 0) {
						this.setInventorySlotContents(slot,null);
					}
					return itemstack;
				}
			} else {
				return null;
			}
		}

		public ItemStack removeStackFromSlot(int index) {
			ItemStack is = this.getStackInSlot(index);
			if(is != null){
				items.remove(index);
			}
			return is;
		}

		public void setInventorySlotContents(int index, ItemStack stack) {
			TransferingItemStack s = items.size() > index ? items.get(index) : null;
			if(s == null){
				s = new TransferingItemStack();
				s.stack = stack;
				items.add(s);
				return;
			}
			s.stack = stack;
		}

		public void clear() {
			this.items.clear();
		}*/
		 public void update(){
			 /*for(int i = 0;i<items.size();i++){
				TransferingItemStack is = items.get(i);
				if(is != null){

				}
			}*/

		 }
		 public InventoryData findNearestInventory(BlockPos pos, ItemStack is, List<InventoryData> ignoredInventories){
			 Map<InventoryData,Double> nearestMap = new HashMap<InventoryData, Double>();
			 Map<InventoryData,Double> normalMap = new HashMap<InventoryData, Double>();
			 Map<InventoryData,Double> furthestMap = new HashMap<InventoryData, Double>();
			 for(int i = 0;i<this.inventories.size();i++){
				 InventoryData h = this.inventories.get(i);
				 if(h != null && (!ignoredInventories.contains(h))){
					 double dist = MathHelper.sqrt_double(h.ownerPos.distanceSq(pos));
					 if(h.inventory.isNearestDest())nearestMap.put(h, dist);
					 else if(h.inventory.isLastDest())furthestMap.put(h, dist);
					 else normalMap.put(h, dist);
				 }
			 }
			 if(!nearestMap.isEmpty()){
				 InventoryData d = null;
				 double shortest = Short.MAX_VALUE;
				 for(Entry<InventoryData,Double> e : nearestMap.entrySet()){
					 if(e.getKey() != null && e.getKey().inventory != null && e.getKey().inventory.canPushStack(is)){
						 double shortestOld = shortest;
						 shortest = Math.min(shortest, e.getValue());
						 if(shortestOld != shortest) d = e.getKey();
					 }
				 }
				 if(d != null){
					 return d;
				 }
			 }
			 if(!normalMap.isEmpty()){
				 InventoryData d = null;
				 double shortest = Short.MAX_VALUE;
				 for(Entry<InventoryData,Double> e : normalMap.entrySet()){
					 if(e.getKey() != null && e.getKey().inventory != null){
						 double shortestOld = shortest;
						 shortest = Math.min(shortest, e.getValue());
						 if(shortestOld != shortest) d = e.getKey();
					 }
				 }
				 if(d != null){
					 return d;
				 }
			 }
			 if(!furthestMap.isEmpty()){
				 InventoryData d = null;
				 double shortest = Short.MAX_VALUE;
				 for(Entry<InventoryData,Double> e : furthestMap.entrySet()){
					 if(e.getKey() != null && e.getKey().inventory != null){
						 double shortestOld = shortest;
						 shortest = Math.min(shortest, e.getValue());
						 if(shortestOld != shortest) d = e.getKey();
					 }
				 }
				 if(d != null){
					 return d;
				 }
			 }
			 return null;
		 }
		 @SuppressWarnings("unchecked")
		 public EnumFacing getPathToInventory(IInventoryHandler h, BlockPos from, EnumFacing[] connectedSides, World world){
			 IGridDevice<IInventoryGrid> dev = h.getDevice();
			 List<IGridDevice<IInventoryGrid>> connectedStorages = new ArrayList<IGridDevice<IInventoryGrid>>();
			 Stack<IGridDevice<IInventoryGrid>> traversingStorages = new Stack<IGridDevice<IInventoryGrid>>();
			 traversingStorages.add(dev);
			 int suction = 1024;
			 int added = 0;
			 while(!traversingStorages.isEmpty()) {
				 IGridDevice<IInventoryGrid> storage = traversingStorages.pop();
				 storage.setSuctionValue(suction);
				 for(EnumFacing d : EnumFacing.VALUES) {
					 if(storage.isConnected(d)){
						 TileEntity te = world.getTileEntity(storage.getPos2().offset(d));
						 if(te instanceof IGridDevice && !connectedStorages.contains(te)) {
							 try {
								 if(((IGridDevice<IInventoryGrid>)te).isValidConnection(d.getOpposite())){
									 traversingStorages.add((IGridDevice<IInventoryGrid>)te);
									 added++;
								 }
							 } catch (ClassCastException e) {
								 //Do nothing
							 }
						 }else{
							 PartDuct<IInventoryGrid> duct = getDuct(world, storage.getPos2().offset(d), d);
							 if(duct != null && !connectedStorages.contains(duct) && duct.isValidConnection(d.getOpposite())){
								 traversingStorages.add(duct);
								 added++;
								 List<PartModule<IInventoryGrid>> modules = duct.getModules();
								 if(modules != null && !modules.isEmpty()){
									 connectedStorages.addAll(modules);
									 for(PartModule<IInventoryGrid> m : modules){
										 m.setSuctionValue(suction-2);
									 }
								 }
							 }
						 }
					 }
				 }
				 if(added <= 0)suction--;
				 if(added > 0) added--;
			 }
			 /*connectedStorages = new ArrayList<IGridDevice<IInventoryGrid>>();
			traversingStorages = new Stack<IGridDevice<IInventoryGrid>>();
			traversingStorages.add(from.getDevice());
			int suctionMax = -1;
			boolean forceBreak = false;
			while(!traversingStorages.isEmpty()) {
				IGridDevice<IInventoryGrid> storage = traversingStorages.pop();
				storage.setSuctionValue(suction);
				for(EnumFacing d : EnumFacing.VALUES) {
					if(storage.isConnected(d)){
						TileEntity te = world.getTileEntity(storage.getPos().offset(d));
						if(te instanceof IGridDevice && !connectedStorages.contains(te)) {
							try {
								if(((IGridDevice<IInventoryGrid>)te).isValidConnection(d.getOpposite()) && ((IGridDevice<IInventoryGrid>)te).getSuctionValue() > suctionMax){
									traversingStorages.add((IGridDevice<IInventoryGrid>)te);
									suctionMax = ((IGridDevice<IInventoryGrid>)te).getSuctionValue();
								}
							} catch (ClassCastException e) {
								//Do nothing
							}
						}else{
							PartDuct<IInventoryGrid> duct = getDuct(world, storage.getPos().offset(d), d);
							if(duct != null && !connectedStorages.contains(duct) && duct.isValidConnection(d.getOpposite()) && duct.getSuctionValue() > suctionMax){
								List<PartModule<IInventoryGrid>> modules = duct.getModules();
								if(modules != null && !modules.isEmpty()){
									connectedStorages.addAll(modules);
									for(PartModule<IInventoryGrid> m : modules){
										if(m.getSuctionValue() > suctionMax){
											forceBreak = true;
											break;
										}
									}
									if(forceBreak)break;
								}
								traversingStorages.add(duct);
							}
						}
					}
				}
				if(forceBreak)break;
			}*/
			 int suctionMax = -1;
			 EnumFacing highestSuction = null;
			 for(EnumFacing d : connectedSides) {
				 TileEntity te = world.getTileEntity(from.offset(d));
				 if(te instanceof IGridDevice) {
					 try {
						 if(((IGridDevice<IInventoryGrid>)te).isValidConnection(d.getOpposite()) && ((IGridDevice<IInventoryGrid>)te).getSuctionValue() > suctionMax){
							 suctionMax = ((IGridDevice<IInventoryGrid>)te).getSuctionValue();
							 highestSuction = d;
						 }
					 } catch (ClassCastException e) {
						 //Do nothing
					 }
				 }else{
					 PartDuct<IInventoryGrid> duct = getDuct(world, from.offset(d), d);
					 if(duct != null && duct.isValidConnection(d.getOpposite()) && duct.getSuctionValue() > suctionMax){
						 List<PartModule<IInventoryGrid>> modules = duct.getModules();
						 highestSuction = d;
						 if(modules != null && !modules.isEmpty()){
							 for(PartModule<IInventoryGrid> m : modules){
								 if(m.getSuctionValue() > suctionMax){
									 return d.getOpposite();
								 }
							 }
						 }
					 }
				 }
			 }
			 return highestSuction != null ? highestSuction.getOpposite() : null;
		 }
		 @SuppressWarnings("unchecked")
		 private final PartDuct<IInventoryGrid> getDuct(World world, BlockPos blockPos, EnumFacing side) {
			 IMultipartContainer container = MultipartHelper.getPartContainer(world, blockPos);
			 if (container == null) {
				 return null;
			 }

			 if (side != null) {
				 ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(side));
				 if (part instanceof IMicroblock.IFaceMicroblock && !((IMicroblock.IFaceMicroblock) part).isFaceHollow()) {
					 return null;
				 }
			 }

			 ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
			 try{
				 if (part instanceof PartDuct<?>) {
					 return (PartDuct<IInventoryGrid>) part;
				 } else {
					 return null;
				 }
			 }catch (ClassCastException e){
				 return null;
			 }
		 }
		 public static class TransferingItemStack{
			 private static final String ITEM_STACK_TAG_NAME = "itemStack";
			 private static final String POSITION_TAG_NAME = "position";
			 private List<InventoryData> ignoredInventories = new ArrayList<InventoryData>();
			 private ItemStack stack;
			 private float position = 0;
			 private BlockPos pos;
			 private EnumFacing side;
			 private EnumFacing direction;
			 public TransferingItemStack(ItemStack stack) {
				 this.stack = stack;
			 }
			 private TransferingItemStack() {}
			 public void writeToNBT(NBTTagCompound tag) {
				 NBTTagCompound itemTag = new NBTTagCompound();
				 if(stack != null){
					 stack.writeToNBT(itemTag);
				 }
				 tag.setTag(ITEM_STACK_TAG_NAME, itemTag);
				 tag.setFloat(POSITION_TAG_NAME, position);
				 TomsModUtils.writeBlockPosToNBT(tag, pos);
				 tag.setInteger("side", side.ordinal());
				 tag.setInteger("dir", direction.ordinal());
			 }

			 public static TransferingItemStack loadTransferingItemStackFromNBT(
					 NBTTagCompound tag) {
				 TransferingItemStack ret = new TransferingItemStack();
				 ret.stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(ITEM_STACK_TAG_NAME));
				 ret.pos = TomsModUtils.readBlockPosFromNBT(tag);
				 ret.position = tag.getFloat(POSITION_TAG_NAME);
				 ret.side = EnumFacing.VALUES[tag.getInteger("side")];
				 ret.direction = EnumFacing.VALUES[tag.getInteger("dir")];
				 return ret;
			 }
			 public void move(IItemDuct duct){
				 //float value = duct.getMovementSpeed();
				 float value = 0;
				 boolean isC = duct.getWorld2().isRemote;
				 float oldPos = this.position;
				 this.position = this.position + value;
				 if(!isC){
					 if(oldPos < 8 && this.position >= 8){
						 if(duct.getPos2().equals(pos)){
							 this.direction = this.side.getOpposite();
						 }else{
							 PartDuct<IInventoryGrid> d = this.getDuct(duct.getWorld2(), pos, null);
							 if(d != null && d instanceof IItemDuct){
								 if(d.getGrid() == duct.getGrid()){
									 if(d.connectsM(side)){
										 PartModule<IInventoryGrid> m = this.getModule(side, duct.getGrid(), duct.getWorld2());
										 if(m != null && m instanceof IInventoryHandler){
											 this.direction = duct.getGrid().getData().getPathToInventory((IInventoryHandler) m, duct.getPos2(), duct.getConnections(), duct.getWorld2());
										 }
									 }else if(d.connectsInv(side) && ((IItemDuct)d).getConnectionData()[side.ordinal()] != null){
										 this.direction = duct.getGrid().getData().getPathToInventory((IInventoryHandler) d, duct.getPos2(), duct.getConnections(), duct.getWorld2());
									 }
								 }
							 }
						 }
						 duct.sendUpdatePacket();
					 }else if(this.position >= 16){
						 if(duct.getPos2().equals(pos)){
							 if(duct.getConnectionData()[direction.ordinal()] != null){
								 if(duct.getConnectionData()[direction.ordinal()].inventory.canPushStack(stack)){
									 this.stack = duct.getConnectionData()[direction.ordinal()].inventory.pushStack(stack);
									 //duct.sendUpdatePacket();
								 }
							 }
							 if(stack != null){
								 InventoryData data = duct.getGrid().getData().findNearestInventory(pos, stack,ignoredInventories);
								 if(data != null){
									 TransferingItemStack tStack = getTransferingItemStackFromInventoryData(data, stack,this.direction.getOpposite());
									 tStack.ignoredInventories.add(duct.getConnectionData()[direction.ordinal()]);
									 duct.removeTransferingItem(this);
									 duct.addTransferingItem(tStack);
									 //duct.sendUpdatePacket();
								 }else{
									 EntityItem item = new EntityItem(duct.getWorld2(),pos.getX(),pos.getY(),pos.getZ(),stack);
									 duct.getWorld2().spawnEntityInWorld(item);
									 stack = null;
								 }
							 }else{
								 duct.removeTransferingItem(this);
							 }
						 }else{
							 PartDuct<IInventoryGrid> d = this.getDuct(duct.getWorld2(), duct.getPos2().offset(direction), null);
							 if(d != null && d instanceof IItemDuct){
								 duct.removeTransferingItem(this);
								 ((IItemDuct)d).addTransferingItem(this);
								 this.position = 0;
								 //((IItemDuct)d).sendUpdatePacket();
								 //duct.sendUpdatePacket();
							 }
						 }

					 }
				 }
			 }
			 public float getPosition() {
				 return position;
			 }
			 public BlockPos getPos() {
				 return pos;
			 }
			 public void setPos(BlockPos pos) {
				 this.pos = pos;
			 }
			 public EnumFacing getSide() {
				 return side;
			 }
			 public void setSide(EnumFacing side) {
				 this.side = side;
			 }
			 public ItemStack getStack() {
				 return stack;
			 }
			 public EnumFacing getDirection() {
				 return direction;
			 }
			 @Override
			 public String toString() {
				 return stack.toString()+":"+this.position;
			 }
			 public void updateDest(IInventoryGrid grid, World world, BlockPos cPos){
				 if(world.isRemote)return;
				 if(stack == null)return;
				 PartDuct<IInventoryGrid> d = this.getDuct(world, pos, null);
				 boolean isValid = false;
				 if(d != null){
					 if(d.getGrid() == grid){
						 if(d.connectsM(side)){
							 PartModule<IInventoryGrid> m = this.getModule(side, grid, world);
							 if(m != null && m instanceof IInventoryHandler){
								 if(((IInventoryHandler)m).canPushStack(stack)){
									 isValid = true;
								 }
							 }
						 }else if(d.connectsInv(side)){
							 isValid = true;
						 }
					 }
				 }
				 if(!isValid){
					 InventoryData data = grid.getData().findNearestInventory(cPos, stack,ignoredInventories);
					 if(d != null && data != null){
						 this.pos = data.inventory.getPos2();
						 this.side = data.inventory.getFacing();
					 }else{
						 EntityItem item = new EntityItem(world,cPos.getX()+0.5,cPos.getY()+0.5,cPos.getZ()+0.5, stack);
						 world.spawnEntityInWorld(item);
						 stack = null;
					 }
				 }
			 }
			 @SuppressWarnings("unchecked")
			 private final PartDuct<IInventoryGrid> getDuct(World world, BlockPos blockPos, EnumFacing side) {
				 IMultipartContainer container = MultipartHelper.getPartContainer(world, blockPos);
				 if (container == null) {
					 return null;
				 }

				 if (side != null) {
					 ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(side));
					 if (part instanceof IMicroblock.IFaceMicroblock && !((IMicroblock.IFaceMicroblock) part).isFaceHollow()) {
						 return null;
					 }
				 }

				 ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
				 try{
					 if (part instanceof PartDuct<?>) {
						 return (PartDuct<IInventoryGrid>) part;
					 } else {
						 return null;
					 }
				 }catch (ClassCastException e){
					 return null;
				 }
			 }
			 @SuppressWarnings("unchecked")
			 private final PartModule<IInventoryGrid> getModule(EnumFacing pos, IInventoryGrid grid, World world){
				 try{
					 PartModule<IInventoryGrid> m = (PartModule<IInventoryGrid>) TomsModUtils.getModule(world, getPos(), pos);
					 if(m.constructGrid().getClass() == grid.getClass())return m;
				 }catch(Exception e){
					 return null;
				 }
				 return null;
			 }
			 public static TransferingItemStack getTransferingItemStackFromInventoryData(InventoryData data, ItemStack is, EnumFacing dir){
				 TransferingItemStack t = new TransferingItemStack(is);
				 t.pos = data.ownerPos;
				 t.side = data.inventory.getFacing();
				 t.direction = dir;
				 return t;
			 }
			 public void writeToPacket(ByteBuf buf){
				 boolean isValid = this.stack != null;
				 buf.writeBoolean(isValid);
				 if(isValid){
					 ByteBufUtils.writeItemStack(buf, stack);
					 buf.writeFloat(position);
					 buf.writeByte(direction.ordinal());
					 TomsModUtils.writeBlockPosToPacket(buf, pos);
				 }
			 }
			 public static TransferingItemStack readTransferingItemStackFromPacket(ByteBuf buf){
				 boolean isValid = buf.readBoolean();
				 if(isValid){
					 TransferingItemStack t = new TransferingItemStack(ByteBufUtils.readItemStack(buf));
					 t.position = buf.readFloat();
					 t.direction = EnumFacing.VALUES[buf.readByte()];
					 t.pos = TomsModUtils.readBlockPosFromPacket(buf);
					 return t;
				 }
				 return null;
			 }
		 }
		 public static class InventoryData{
			 public final IInventoryHandler inventory;
			 public final BlockPos pos, ownerPos;
			 //public final List<ItemStack> validItems;
			 //public final boolean checkNBT;
			 //public final boolean checkMeta;
			 //public final boolean checkMod;
			 //public final int maxItemsInInventory;
			 //public final boolean isWhiteList;
			 /*public InventoryData(IInventory inventory, boolean checkNBT,
					boolean checkMeta, boolean checkMod, boolean lastDest,
					boolean nearestDest, int maxItemsInInventory,BlockPos pos, boolean isWhiteList, ItemStack... validItemStacks) {
				this(inventory, checkNBT, checkMeta, checkMod, lastDest, nearestDest, maxItemsInInventory, pos, isWhiteList, TomsModUtils.getListFromArray(validItemStacks));
			}
			public InventoryData(IInventory inventory, boolean checkNBT,
					boolean checkMeta, boolean checkMod, boolean isLastDest,
					boolean isNearestDest, int maxItemsInInventory,
					BlockPos pos, boolean isWhiteList,
					List<ItemStack> validItems) {
				this.inventory = inventory;
				this.validItems = validItems;
				this.checkNBT = checkNBT;
				this.checkMeta = checkMeta;
				this.checkMod = checkMod;
				this.isLastDest = isLastDest;
				this.isNearestDest = isNearestDest;
				this.maxItemsInInventory = maxItemsInInventory;
				this.pos = pos;
				this.isWhiteList = isWhiteList;
			}*/
			 public InventoryData(IInventoryHandler inventory, BlockPos pos,
					 BlockPos ownerPos) {
				 this.inventory = inventory;
				 this.pos = pos;
				 this.ownerPos = ownerPos;
			 }

		 }
		 public static interface IInventoryHandler{
			 ItemStack pushStack(ItemStack is);
			 ItemStack pullStack(ItemStack matchTo, ItemHandlerData data);
			 boolean canPushStack(ItemStack is);
			 BlockPos getPos2();
			 EnumFacing getFacing();
			 IGridDevice<IInventoryGrid> getDevice();
			 boolean isNearestDest();
			 boolean isLastDest();

			 public static class ItemHandlerData{
				 public final boolean checkNBT;
				 public final boolean checkMeta;
				 public final boolean checkMod;
				 public final boolean isWhiteList;
				 public final int maxStackSize;
				 public ItemHandlerData(boolean checkNBT, boolean checkMeta, boolean checkMod,
						 boolean isWhiteList, int maxStackSize) {
					 this.checkNBT = checkNBT;
					 this.checkMeta = checkMeta;
					 this.checkMod = checkMod;
					 this.isWhiteList = isWhiteList;
					 this.maxStackSize = maxStackSize;
				 }
			 }
		 }
		 public static interface IItemDuct{
			 void addTransferingItem(TransferingItemStack item);
			 void sendUpdatePacket();
			 void removeTransferingItem(TransferingItemStack item);
			 World getWorld2();
			 InventoryData[] getConnectionData();
			 EnumFacing[] getConnections();
			 BlockPos getPos2();
			 IInventoryGrid getGrid();
			 //List<TransferingItemStack> getTransferingItemStacks();
			 //float getMovementSpeed();
		 }
	}
}
