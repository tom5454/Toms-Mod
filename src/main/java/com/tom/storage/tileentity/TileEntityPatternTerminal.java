package com.tom.storage.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.StoredItemStack;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.tileentity.IPatternTerminal;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.ReturnData;
import com.tom.storage.StorageInit;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.multipart.StorageNetworkGrid.CraftableProperties;
import com.tom.storage.multipart.StorageNetworkGrid.CraftingPatternProperties;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingRecipeContainer;
import com.tom.storage.multipart.StorageNetworkGrid.IGridInputListener;
import com.tom.storage.multipart.StorageNetworkGrid.SavedCraftingRecipe;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class TileEntityPatternTerminal extends TileEntityGridDeviceBase<StorageNetworkGrid> implements IPatternTerminal, IGridInputListener, IInventoryChangedListener, INBTPacketReceiver{
	public boolean poweredClient = false;
	private boolean poweredLast = false;
	public int terminalMode = 0;
	public InventoryBasic recipeInv;
	public InventoryBasic resultInv;
	public InventoryBasic upgradeInv = new InventoryBasic("", false, 1);
	public InventoryBasic patternInv = new InventoryBasic("", false, 2);
	private AutoCraftingBehaviour craftingB = AutoCraftingBehaviour.NO_OP;
	private boolean patternPulled = false;
	public CraftingPatternProperties properties = new CraftingPatternProperties();
	private CraftableProperties[] stackProperties;
	public TileEntityPatternTerminal() {
		recipeInv = new InventoryBasic("", false, 25);
		recipeInv.addInventoryChangeListener(this);
		resultInv = new InventoryBasic("", false, 8);
		createStackProperties();
	}
	private void createStackProperties(){
		int size = recipeInv.getSizeInventory() + resultInv.getSizeInventory();
		stackProperties = new CraftableProperties[size];
		for(int i = 0;i<size;i++){
			stackProperties[i] = new CraftableProperties();
		}
	}
	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}
	@Override
	public void updateEntity(IBlockState currentState) {
		if(!worldObj.isRemote){
			grid.drainEnergy(1);
			grid.getData().addInputListener(this);
			poweredClient = grid.isPowered();
			if(poweredLast != poweredClient){
				poweredLast = poweredClient;
				markBlockForUpdate(pos);
			}
		}
	}
	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setBoolean("p", poweredClient);
	}
	@Override
	public void readFromPacket(NBTTagCompound buf) {
		poweredClient = buf.getBoolean("p");
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}
	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if(id == 0)terminalMode = extra % 3;
		else if(id == 1){
			recipeInv.clear();
			resultInv.clear();
			properties = new CraftingPatternProperties();
		}
		else if(id == 2){
			if(patternInv.getStackInSlot(1) != null){
				if(patternInv.getStackInSlot(1).getItem() instanceof ICraftingRecipeContainer){
					((ICraftingRecipeContainer)patternInv.getStackInSlot(1).getItem()).setRecipe(patternInv.getStackInSlot(1), SavedCraftingRecipe.createFromStacks(recipeInv, resultInv, properties, stackProperties));
				}
			}else if(patternInv.getStackInSlot(0) != null && patternInv.getStackInSlot(0).getItem() instanceof ICraftingRecipeContainer){
				ItemStack stack = patternInv.decrStackSize(0, 1);
				((ICraftingRecipeContainer)stack.getItem()).setRecipe(stack, SavedCraftingRecipe.createFromStacks(recipeInv, resultInv, properties, stackProperties));
				patternInv.setInventorySlotContents(1, stack);
			}
			if(craftingB != AutoCraftingBehaviour.NO_OP && (patternInv.getStackInSlot(0) == null || patternInv.getStackInSlot(0).stackSize < 32)){
				ItemStack stack;
				switch(craftingB){
				case CRAFT_ONLY:
					grid.getData().queueCrafting(new StoredItemStack(new ItemStack(StorageInit.craftingPattern), 1), 1, player, -1);
					patternPulled = true;
					break;
				case NO_OP:
					break;
				case STORED_ONLY:
					stack = grid.getInventory().pullStack(new StoredItemStack(new ItemStack(StorageInit.craftingPattern), 1), 1);
					if(stack != null && stack.getItem() == StorageInit.craftingPattern && stack.getMetadata() == 0){
						if(patternInv.getStackInSlot(0) != null){
							if(patternInv.getStackInSlot(0).stackSize < 32 && patternInv.getStackInSlot(0).getItem() == StorageInit.craftingPattern && patternInv.getStackInSlot(0).getMetadata() == 0){
								patternInv.getStackInSlot(0).stackSize++;
								stack.stackSize--;
								if(stack != null && stack.stackSize > 0 && stack.getItem() != null){
									stack = grid.getInventory().pushStack(stack);
									if(stack != null && stack.stackSize > 0 && stack.getItem() != null){
										EntityItem item = new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
										worldObj.spawnEntityInWorld(item);
									}
								}
							}else{
								stack = grid.getInventory().pushStack(stack);
								if(stack != null && stack.stackSize > 0 && stack.getItem() != null){
									EntityItem item = new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
									worldObj.spawnEntityInWorld(item);
								}
							}
						}
					}else{
						stack = grid.getInventory().pushStack(stack);
						if(stack != null && stack.stackSize > 0 && stack.getItem() != null){
							EntityItem item = new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
							worldObj.spawnEntityInWorld(item);
						}
					}
					break;
				case USE_STORED_AND_CRAFT:
					stack = grid.getInventory().pullStack(new StoredItemStack(new ItemStack(StorageInit.craftingPattern), 1), 1);
					if(stack != null && stack.getItem() == StorageInit.craftingPattern && stack.getMetadata() == 0){
						if(patternInv.getStackInSlot(0) != null){
							if(patternInv.getStackInSlot(0).stackSize < 32 && patternInv.getStackInSlot(0).getItem() == StorageInit.craftingPattern && patternInv.getStackInSlot(0).getMetadata() == 0){
								patternInv.getStackInSlot(0).stackSize++;
								stack.stackSize--;
								if(stack != null && stack.stackSize > 0 && stack.getItem() != null){
									stack = grid.getInventory().pushStack(stack);
									if(stack != null && stack.stackSize > 0 && stack.getItem() != null){
										EntityItem item = new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
										worldObj.spawnEntityInWorld(item);
									}
								}
							}else{
								stack = grid.getInventory().pushStack(stack);
								if(stack != null && stack.stackSize > 0 && stack.getItem() != null){
									EntityItem item = new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
									worldObj.spawnEntityInWorld(item);
								}
							}
						}
					}else{
						stack = grid.getInventory().pushStack(stack);
						if(stack != null && stack.stackSize > 0 && stack.getItem() != null){
							EntityItem item = new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
							worldObj.spawnEntityInWorld(item);
						}
						grid.getData().queueCrafting(new StoredItemStack(new ItemStack(StorageInit.craftingPattern), 1), 1, player, -1);
						patternPulled = true;
					}
					break;
				default:
					break;
				}
			}
		}else if(id == 3)craftingB = AutoCraftingBehaviour.get(extra);
		else if(id == 4)properties.useContainerItems = extra == 1;
		else if(id == -9)player.openGui(CoreInit.modInstance, GuiIDs.patternTerminal.ordinal(), worldObj, pos.getX(), pos.getY(), pos.getZ());
		else if(id == -10)properties.time = extra;
		else if(id == -11)properties.storedOnly = extra == 1;
		else if(id == 5)player.openGui(CoreInit.modInstance, GuiIDs.patternOptions.ordinal(), worldObj, pos.getX(), pos.getY(), pos.getZ());
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("termMode", terminalMode);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < recipeInv.getSizeInventory(); ++i)
		{
			if (recipeInv.getStackInSlot(i) != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				recipeInv.getStackInSlot(i).writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		compound.setTag("recipeList", list);
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
		compound.setTag("upgradeList", list);
		list = new NBTTagList();
		for (int i = 0; i < patternInv.getSizeInventory(); ++i)
		{
			if (patternInv.getStackInSlot(i) != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				patternInv.getStackInSlot(i).writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		compound.setTag("patternInv", list);
		compound.setInteger("craftingB", craftingB.ordinal());
		list = new NBTTagList();
		for (int i = 0; i < resultInv.getSizeInventory(); ++i)
		{
			if (resultInv.getStackInSlot(i) != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				resultInv.getStackInSlot(i).writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		compound.setTag("resultList", list);
		compound.setTag("patternProperties", properties.writeToNBT(new NBTTagCompound()));
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		terminalMode = compound.getInteger("termMode");
		NBTTagList list = compound.getTagList("recipeList", 10);
		recipeInv.clear();
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < recipeInv.getSizeInventory())
			{
				recipeInv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
		list = compound.getTagList("upgradeList", 10);
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
		list = compound.getTagList("patternInv", 10);
		patternInv.clear();
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < patternInv.getSizeInventory())
			{
				patternInv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
		craftingB = AutoCraftingBehaviour.get(compound.getInteger("craftingB"));
		list = compound.getTagList("resultList", 10);
		resultInv.clear();
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < resultInv.getSizeInventory())
			{
				resultInv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
		properties = CraftingPatternProperties.loadFromNBT(compound.getCompoundTag("patternProperties"));
	}
	@Override
	public int getTerminalMode() {
		return terminalMode;
	}
	@Override
	public boolean hasPattern() {
		return (patternInv.getStackInSlot(1) != null && patternInv.getStackInSlot(1).getItem() instanceof ICraftingRecipeContainer) || (patternInv.getStackInSlot(0) != null && patternInv.getStackInSlot(0).getItem() instanceof ICraftingRecipeContainer);
	}
	public int getCraftingBehaviour() {
		return upgradeInv.getStackInSlot(0) != null && upgradeInv.getStackInSlot(0).getItem() == StorageInit.craftingCard ? craftingB.ordinal() : -1;
	}
	public static enum AutoCraftingBehaviour{
		USE_STORED_AND_CRAFT, STORED_ONLY, CRAFT_ONLY, NO_OP
		;
		public static final AutoCraftingBehaviour[] VALUES = values();
		public static AutoCraftingBehaviour get(int index){
			return VALUES[MathHelper.abs_int(index % VALUES.length)];
		}
	}
	public void setCraftingBehaviour(int data) {
		craftingB = AutoCraftingBehaviour.get(data);
	}
	@Override
	public ItemStack onStackInput(ItemStack stack) {
		if(patternPulled){
			if(stack != null && stack.getItem() == StorageInit.craftingPattern && stack.getMetadata() == 0){
				if(patternInv.getStackInSlot(0) != null){
					if(patternInv.getStackInSlot(0).stackSize < 32 && patternInv.getStackInSlot(0).getItem() == StorageInit.craftingPattern && patternInv.getStackInSlot(0).getMetadata() == 0){
						patternInv.getStackInSlot(0).stackSize++;
						patternInv.markDirty();
						stack.splitStack(1);
						patternPulled = false;
						if(stack.stackSize < 1)return null;
					}
				}else{
					patternInv.setInventorySlotContents(0, stack.splitStack(1));
					patternPulled = false;
					if(stack.stackSize < 1)return null;
				}
			}
		}
		return stack;
	}
	@Override
	public void markDirty() {
		super.markDirty();
	}
	@Override
	public void onInventoryChanged(InventoryBasic inv) {
		if(inv == recipeInv){
			ReturnData t = AdvancedCraftingHandler.craft(TomsModUtils.getStackArrayFromInventory(recipeInv), null, null);
			if(t != null){
				resultInv.setInventorySlotContents(0, t.getReturnStack());
				resultInv.setInventorySlotContents(1, t.getExtraStack());
				resultInv.setInventorySlotContents(2, null);
			}else{
				ItemStack s = TomsModUtils.getMathchingRecipe(recipeInv, worldObj);
				if(s != null){
					resultInv.setInventorySlotContents(0, s);
					resultInv.setInventorySlotContents(1, null);
					resultInv.setInventorySlotContents(2, null);
				}
			}
		}
	}
	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		if(message.getByte("cfg") == 1){
			int slot = message.getByte("slot");
			byte amount = message.getByte("amount");
			if(slot >= recipeInv.getSizeInventory()){
				if(resultInv.getStackInSlot(slot - recipeInv.getSizeInventory()) != null){
					resultInv.getStackInSlot(slot - recipeInv.getSizeInventory()).stackSize = amount;
				}
			}else{
				if(recipeInv.getStackInSlot(slot) != null){
					recipeInv.getStackInSlot(slot).stackSize = amount;
				}
			}
		}else if(message.getByte("cfg") == 2){
			getPropertiesFor(message.getByte("slot")).readFromNBT(message);
		}else{
			NBTTagList list = message.getTagList("i", 10);
			recipeInv.clear();
			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("s") & 255;

				if (j >= 0 && j < recipeInv.getSizeInventory())
				{
					recipeInv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
				}
			}
			list = message.getTagList("o", 10);
			resultInv.clear();
			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("s") & 255;

				if (j >= 0 && j < resultInv.getSizeInventory())
				{
					resultInv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
				}
			}
			properties = new CraftingPatternProperties();
			properties.useContainerItems = message.getBoolean("c");
			createStackProperties();
		}
	}
	@Override
	public IInventory getRecipeInv() {
		return recipeInv;
	}
	@Override
	public IInventory getResultInv() {
		return resultInv;
	}
	@Override
	public IInventory getPatternInv() {
		return patternInv;
	}
	@Override
	public ItemStack getButtonStack() {
		return new ItemStack(StorageInit.patternTerminal);
	}
	@Override
	public CraftingPatternProperties getProperties() {
		return properties;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void sendUpdate(GuiTomsMod gui, int id, int extra) {
		gui.sendButtonUpdate(id, pos, extra);
	}
	@Override
	public CraftableProperties getPropertiesFor(int id) {
		return stackProperties[id];
	}
	@Override
	public void sendUpdate(NBTTagCompound message) {
		NetworkHandler.sendToServer(new MessageNBT(message, pos));
	}
	@Override
	public int getPropertiesLength() {
		return stackProperties.length;
	}
}
