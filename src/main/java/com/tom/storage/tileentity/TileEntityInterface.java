package com.tom.storage.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.inventory.StoredItemStack;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.apis.TomsModUtils;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingHandler;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingPatternListener;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingRecipe;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingRecipeContainer;
import com.tom.storage.multipart.StorageNetworkGrid.SavedCraftingRecipe;

public class TileEntityInterface extends
TileEntityGridDeviceBase<StorageNetworkGrid> implements ICraftingHandler<StoredItemStack>, ISidedInventory{
	/**Pattern, Phantom, Normal*/
	private ItemStack[] stacks = new ItemStack[this.getSizeInventory()];
	private static final int[] SLOTS = new int[]{18,19,20,21,22,23,24,25,26};
	private List<ICraftingRecipe<StoredItemStack>> recipes = new ArrayList<ICraftingRecipe<StoredItemStack>>();
	private List<ItemStack> stacksToPush = new ArrayList<ItemStack>();
	/*ICraftingRecipe recipe = new ICraftingRecipe(){

		@Override
		public boolean isStoredOnly() {
			return false;
		}

		@Override
		public List<ItemStack> getOutputs() {
			return TomsModUtils.getListFromArray(new ItemStack(Blocks.stone));
		}

		@Override
		public List<ItemStack> getInputs() {
			return TomsModUtils.getListFromArray(new ItemStack(Blocks.cobblestone));
		}

		@Override
		public int getTime() {
			return 14;
		}

		@Override
		public boolean execute() {
			System.out.println("execute");
			ItemStack s = TomsModUtils.pushStackToNeighbours(new ItemStack(Blocks.cobblestone), worldObj, pos, EnumFacing.VALUES);
			return s == null;
		}

	};*/
	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public List<ICraftingRecipe<StoredItemStack>> getRecipes() {
		//return TomsModUtils.getListFromArray(recipe);
		return recipes;
	}
	@Override
	public void updateEntity(IBlockState currentState) {
		if(!worldObj.isRemote){
			grid.getData().addCraftingHandler(this);
			for(int i = 0;i<stacksToPush.size();i++){
				ItemStack s = stacksToPush.get(i);
				if(s != null){
					ItemStack p = TomsModUtils.pushStackToNeighbours(s.copy(), worldObj, pos, EnumFacing.VALUES);
					if(p == null || p.stackSize < 1){
						stacksToPush.remove(s);
					}else{
						s.stackSize = p.stackSize;
					}
				}
			}
			for(int i = 18;i<27;i++){
				int j = i - 9;
				if(stacks[i] != null){
					if(stacks[j] == null || !TomsModUtils.areItemStacksEqual(stacks[i], stacks[j], true, true, false))stacks[i] = grid.pushStack(stacks[i]);
				}
				if(stacks[j] != null){
					if(stacks[i] == null){
						stacks[i] = grid.getInventory().pullStack(new StoredItemStack(stacks[j], stacks[j].stackSize), stacks[j].stackSize);
					}else if(TomsModUtils.areItemStacksEqual(stacks[i], stacks[j], true, true, false) && stacks[i].stackSize < stacks[j].stackSize){
						ItemStack stack = grid.getInventory().pullStack(new StoredItemStack(stacks[j], stacks[j].stackSize - stacks[i].stackSize), stacks[j].stackSize - stacks[i].stackSize);
						if(stack != null)stacks[i].stackSize += stack.stackSize;
					}
				}
			}
		}
	}

	@Override
	public boolean executeRecipe(ICraftingRecipe<StoredItemStack> recipe, boolean doExecute) {
		if(!stacksToPush.isEmpty())return false;
		List<StoredItemStack> toPush = recipe.getInputs();
		int id = -1;
		SavedCraftingRecipe savedRecipe = null;
		for(int i = 0;i<9;i++){
			if(stacks[i] != null && stacks[i].getItem() instanceof ICraftingRecipeContainer && ((ICraftingRecipeContainer)stacks[i].getItem()).getRecipe(stacks[i]) != null){
				SavedCraftingRecipe r = ((ICraftingRecipeContainer)stacks[i].getItem()).getRecipe(stacks[i]);
				if(r.recipeEquals(recipe)){
					id = i;
					savedRecipe = r;
					break;
				}
			}
		}
		if(toPush != null && id >= 0 && savedRecipe != null){
			boolean listenerFound = false;
			for(EnumFacing f : EnumFacing.VALUES){
				BlockPos p = pos.offset(f);
				TileEntity tile = worldObj.getTileEntity(p);
				if(tile instanceof ICraftingPatternListener){
					listenerFound = true;
					ICraftingPatternListener l = (ICraftingPatternListener) tile;
					if(l.pushRecipe(savedRecipe.getInputs(), doExecute)){
						return true;
					}
				}
			}
			if(!listenerFound){
				if(doExecute){
					for(int i = 0;i<toPush.size();i++){
						StoredItemStack stack = toPush.get(i);
						if(stack != null){
							ItemStack stack2 = TomsModUtils.pushStackToNeighbours(stack.stack, worldObj, pos, EnumFacing.VALUES);
							if(stack2 != null){
								stacksToPush.add(stack2.copy());
							}
						}
					}
				}
				return true;
			}
			return false;
		}else
			return false;
	}

	@Override
	public String getName() {
		return "interface";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public int getSizeInventory() {
		return 27;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return stacks[index];
	}

	@Override
	public ItemStack decrStackSize(int slot, int par2) {
		//if(slot == 5) return null;
		if (this.stacks[slot] != null) {
			ItemStack itemstack;
			if (this.stacks[slot].stackSize <= par2) {
				itemstack = this.stacks[slot];
				this.stacks[slot] = null;
				return itemstack;
			} else {
				itemstack = this.stacks[slot].splitStack(par2);

				if (this.stacks[slot].stackSize == 0) {
					this.stacks[slot] = null;
				}
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUseable(pos, player, worldObj, this);
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index > 8 ? true : stack != null && stack.getItem() instanceof ICraftingRecipeContainer && ((ICraftingRecipeContainer)stack.getItem()).getRecipe(stack) != null;
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
	public void clear() {

	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		this.stacks = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < this.stacks.length)
			{
				this.stacks[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		NBTTagList list = compound.getTagList("itemsStored", 10);
		stacksToPush.clear();
		for(int i = 0;i<list.tagCount();i++){
			NBTTagCompound t = list.getCompoundTagAt(i);
			ItemStack stack = ItemStack.loadItemStackFromNBT(t);
			if(stack != null){
				stack.stackSize = t.getInteger("ItemCount");
				stacksToPush.add(stack);
			}
		}
		loadRecipes();
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.stacks.length; ++i)
		{
			if (this.stacks[i] != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				this.stacks[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		compound.setTag("Items", nbttaglist);
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<stacksToPush.size();i++){
			NBTTagCompound t = new NBTTagCompound();
			ItemStack stack = stacksToPush.get(i);
			t.setInteger("ItemCount", stack.stackSize);
			stack.writeToNBT(t);
			list.appendTag(t);
		}
		compound.setTag("itemsStored", list);
		return compound;
	}
	public void writeToStackNBT(NBTTagCompound compound) {
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.stacks.length && i < 18; ++i)
		{
			if (this.stacks[i] != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				this.stacks[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		compound.setTag("Items", nbttaglist);
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		if (this.stacks[index] != null)
		{
			ItemStack itemstack = this.stacks[index];
			this.stacks[index] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.stacks[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index > 17;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index > 17;
	}
	@Override
	public void markDirty() {
		super.markDirty();
		loadRecipes();
	}
	private void loadRecipes(){
		recipes.clear();
		for(int i = 0;i<9;i++){
			if(stacks[i] != null && stacks[i].getItem() instanceof ICraftingRecipeContainer && ((ICraftingRecipeContainer) stacks[i].getItem()).getRecipe(stacks[i]) != null){
				SavedCraftingRecipe recipe = ((ICraftingRecipeContainer) stacks[i].getItem()).getRecipe(stacks[i]);
				if(recipe != null){
					ICraftingRecipe<StoredItemStack> r = recipe.getRecipe(this);
					if(r != null){
						recipes.add(r);
					}
				}
			}
		}
	}

	@Override
	public void onGridReload() {
		loadRecipes();
		grid.getData().addCraftingHandler(this);
	}

	@Override
	public void onGridPostReload() {

	}

	@Override
	public Class<StoredItemStack> getCraftableClass() {
		return StoredItemStack.class;
	}
	public List<ItemStack> getStacksToPush() {
		return stacksToPush;
	}
}
