package com.tom.storage.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.grid.StorageNetworkGrid;
import com.tom.api.grid.StorageNetworkGrid.ICraftingPatternListener;
import com.tom.api.grid.StorageNetworkGrid.ICraftingRecipeContainer;
import com.tom.api.inventory.StoredItemStack;
import com.tom.storage.block.BlockInterface;
import com.tom.storage.block.BlockInterface.InterfaceFacing;
import com.tom.storage.handler.AutoCraftingHandler;
import com.tom.storage.handler.ICache;
import com.tom.storage.handler.InventoryCache;
import com.tom.util.TomsModUtils;

public class TileEntityInterface extends TileEntityChannel implements AutoCraftingHandler.ICraftingHandler<StoredItemStack>, ISidedInventory {
	/** Pattern, Phantom, Normal */
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	private static final int[] SLOTS = new int[]{18, 19, 20, 21, 22, 23, 24, 25, 26};
	private List<AutoCraftingHandler.ICraftingRecipe<StoredItemStack>> recipes = new ArrayList<>();
	private List<ItemStack> stacksToPush = new ArrayList<>();

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
	public List<AutoCraftingHandler.ICraftingRecipe<StoredItemStack>> getRecipes() {
		// return TomsModUtils.getListFromArray(recipe);
		return recipes;
	}

	@Override
	public void updateEntity(IBlockState currentState) {
		if (!world.isRemote) {
			if (isActiveForUpdate()) {
				grid.getSData().addCraftingHandler(this);
				for (int i = 0;i < stacksToPush.size();i++) {
					ItemStack s = stacksToPush.get(i);
					if (s != null) {
						ItemStack p = TomsModUtils.pushStackToNeighbours(s.copy(), world, pos, getFacings(currentState));
						if (p == null || p.getCount() < 1) {
							stacksToPush.remove(s);
						} else {
							s.setCount(p.getCount());
						}
					}
				}
				for (int i = 18;i < 27;i++) {
					int j = i - 9;
					if (!inv.getStackInSlot(i).isEmpty()) {
						if (inv.getStackInSlot(j).isEmpty() || !TomsModUtils.areItemStacksEqual(inv.getStackInSlot(i), inv.getStackInSlot(j), true, true, false))
							inv.setInventorySlotContents(i, grid.pushStack(inv.getStackInSlot(i)));
					}
					if (!inv.getStackInSlot(j).isEmpty()) {
						if (inv.getStackInSlot(i).isEmpty()) {
							inv.setInventorySlotContents(i, grid.pullStack(inv.getStackInSlot(j), inv.getStackInSlot(j).getCount()));
						} else if (TomsModUtils.areItemStacksEqual(inv.getStackInSlot(i), inv.getStackInSlot(j), true, true, false) && inv.getStackInSlot(i).getCount() < inv.getStackInSlot(j).getCount()) {
							ItemStack stack = grid.pullStack(inv.getStackInSlot(j), inv.getStackInSlot(j).getCount() - inv.getStackInSlot(i).getCount());
							if (!stack.isEmpty())
								inv.getStackInSlot(i).grow(stack.getCount());
						}
					}
				}
			} else {
				grid.getSData().removeCraftingHandler(this);
			}
		}
	}

	@Override
	public boolean executeRecipe(AutoCraftingHandler.ICraftingRecipe<StoredItemStack> recipe, boolean doExecute) {
		if (!stacksToPush.isEmpty() || !isActive().fullyActive())
			return false;
		List<StoredItemStack> toPush = recipe.getInputs();
		List<StoredItemStack> returnItems = recipe.getOutputs();
		int id = -1;
		AutoCraftingHandler.SavedCraftingRecipe savedRecipe = null;
		for (int i = 0;i < 9;i++) {
			if (!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getItem() instanceof ICraftingRecipeContainer && ((ICraftingRecipeContainer) inv.getStackInSlot(i).getItem()).getRecipe(inv.getStackInSlot(i)) != null) {
				AutoCraftingHandler.SavedCraftingRecipe r = ((ICraftingRecipeContainer) inv.getStackInSlot(i).getItem()).getRecipe(inv.getStackInSlot(i));
				if (r.recipeEquals(recipe)) {
					id = i;
					savedRecipe = r;
					break;
				}
			}
		}
		if (toPush != null && id >= 0 && savedRecipe != null) {
			IBlockState currentState = world.getBlockState(pos);
			if (returnItems != null && world.getBlockState(pos.down()).getBlock() == Blocks.COMMAND_BLOCK) {
				if (doExecute) {
					for (int i = 0;i < returnItems.size();i++) {
						StoredItemStack stack = returnItems.get(i);
						if (stack != null) {
							ItemStack s = stack.getStack().copy();
							s.setCount((int) stack.getQuantity());
							ItemStack stack2 = TomsModUtils.pushStackToNeighbours(s, world, pos, getFacings(currentState));
							if (stack2 != null) {
								stacksToPush.add(stack2.copy());
							}
						}
					}
				}
				return true;
			}
			boolean listenerFound = false;
			for (EnumFacing f : getFacings(currentState)) {
				BlockPos p = pos.offset(f);
				TileEntity tile = world.getTileEntity(p);
				if (tile instanceof ICraftingPatternListener) {
					listenerFound = true;
					ICraftingPatternListener l = (ICraftingPatternListener) tile;
					if (l.pushRecipe(savedRecipe.getInputs(), doExecute)) { return true; }
				}
			}
			if (!listenerFound) {
				if (doExecute) {
					for (int i = 0;i < toPush.size();i++) {
						StoredItemStack stack = toPush.get(i);
						if (stack != null) {
							ItemStack s = stack.getStack().copy();
							s.setCount((int) stack.getQuantity());
							ItemStack stack2 = TomsModUtils.pushStackToNeighbours(s, world, pos, getFacings(currentState));
							if (stack2 != null) {
								stacksToPush.add(stack2.copy());
							}
						}
					}
				}
				return true;
			}
			return false;
		} else
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
		return index > 8 ? true : !stack.isEmpty() && stack.getItem() instanceof ICraftingRecipeContainer && ((ICraftingRecipeContainer) stack.getItem()).getRecipe(stack) != null;
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
		inv.clear();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		TomsModUtils.loadAllItems(compound.getTagList("Items", 10), inv);
		NBTTagList list = compound.getTagList("itemsStored", 10);
		stacksToPush.clear();
		for (int i = 0;i < list.tagCount();i++) {
			NBTTagCompound t = list.getCompoundTagAt(i);
			ItemStack stack = TomsModUtils.loadItemStackFromNBT(t);
			if (stack != null) {
				stack.setCount(t.getInteger("ItemCount"));
				stacksToPush.add(stack);
			}
		}
		loadRecipes();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("Items", TomsModUtils.saveAllItems(inv));
		NBTTagList list = new NBTTagList();
		for (int i = 0;i < stacksToPush.size();i++) {
			NBTTagCompound t = new NBTTagCompound();
			ItemStack stack = stacksToPush.get(i);
			t.setInteger("ItemCount", stack.getCount());
			stack.writeToNBT(t);
			list.appendTag(t);
		}
		compound.setTag("itemsStored", list);
		return compound;
	}

	public void writeToStackNBT(NBTTagCompound compound) {
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0;i < getSizeInventory() && i < 18;++i) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setByte("Slot", (byte) i);
			inv.getStackInSlot(i).writeToNBT(nbttagcompound);
			nbttaglist.appendTag(nbttagcompound);
		}

		compound.setTag("Items", nbttaglist);
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

	private void loadRecipes() {
		if (world != null)
			world.profiler.startSection(pos.toString());
		recipes.clear();
		for (int i = 0;i < 9;i++) {
			if (world != null)
				world.profiler.startSection("slot:" + i);
			if (!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getItem() instanceof ICraftingRecipeContainer && ((ICraftingRecipeContainer) inv.getStackInSlot(i).getItem()).getRecipe(inv.getStackInSlot(i)) != null) {
				AutoCraftingHandler.SavedCraftingRecipe recipe = ((ICraftingRecipeContainer) inv.getStackInSlot(i).getItem()).getRecipe(inv.getStackInSlot(i));
				if (recipe != null) {
					AutoCraftingHandler.ICraftingRecipe<StoredItemStack> r = recipe.getRecipe(this, i);
					if (r != null) {
						recipes.add(r);
					}
				}
			}
			if (world != null)
				world.profiler.endSection();
		}
		if (world != null)
			world.profiler.endSection();
	}

	@Override
	public void onGridReload() {
		loadRecipes();
		grid.getSData().addCraftingHandler(this);
	}

	@Override
	public void onGridPostReload() {

	}

	public List<ItemStack> getStacksToPush() {
		return stacksToPush;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends ICache<StoredItemStack>> Class<C> getCraftableCacheClass() {
		return (Class<C>) InventoryCache.class;
	}

	@Override
	public double getPowerDrained() {
		return 0.8;
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

	private EnumFacing[] getFacings(IBlockState state) {
		InterfaceFacing f = state.getValue(BlockInterface.FACING);
		return f == InterfaceFacing.NONE ? EnumFacing.VALUES : new EnumFacing[]{EnumFacing.VALUES[f.ordinal() - 1]};
	}

	@Override
	public int getDim() {
		return world.provider.getDimension();
	}

	@Override
	public int getExtraData() {
		return 0;
	}
}
