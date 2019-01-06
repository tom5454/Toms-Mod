package com.tom.storage.tileentity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.grid.StorageNetworkGrid.ICraftingRecipeContainer;
import com.tom.api.grid.StorageNetworkGrid.IGridInputListener;
import com.tom.api.gui.GuiTomsMod;
import com.tom.api.inventory.StoredItemStack;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.tileentity.IPatternTerminal;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.ReturnData;
import com.tom.storage.StorageInit;
import com.tom.storage.handler.AutoCraftingHandler;
import com.tom.storage.handler.AutoCraftingHandler.CraftingPatternProperties;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.item.ItemCard.CardType;
import com.tom.util.TomsModUtils;

public class TileEntityPatternTerminal extends TileEntityBasicTerminal implements IPatternTerminal, IGridInputListener, IInventoryChangedListener, INBTPacketReceiver {
	private InventoryBasic recipeInv;
	private InventoryBasic resultInv;
	private InventoryBasic upgradeInv = new InventoryBasic("", false, 1);
	private InventoryBasic patternInv;
	private AutoCraftingBehaviour craftingB = AutoCraftingBehaviour.NO_OP;
	private boolean patternPulled = false;
	public CraftingPatternProperties properties = new CraftingPatternProperties();
	private ICraftable.CraftableProperties[] stackProperties;
	public boolean pattern;

	public TileEntityPatternTerminal() {
		recipeInv = new InventoryBasic("", false, 25);
		recipeInv.addInventoryChangeListener(this);
		resultInv = new InventoryBasic("", false, 8);
		patternInv = new InventoryBasic("", false, 2);
		patternInv.addInventoryChangeListener(new IInventoryChangedListener() {
			boolean v;

			@Override
			public void onInventoryChanged(IInventory invBasic) {
				boolean v = hasPattern();
				if (this.v != v) {
					this.v = v;
					if (world != null)
						markBlockForUpdate();
				}
			}
		});
		createStackProperties();
	}

	private void createStackProperties() {
		int size = recipeInv.getSizeInventory() + resultInv.getSizeInventory();
		stackProperties = new ICraftable.CraftableProperties[size];
		for (int i = 0;i < size;i++) {
			stackProperties[i] = new ICraftable.CraftableProperties();
		}
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (id == 0)
			terminalMode = extra % 3;
		else if (id == 1) {
			recipeInv.clear();
			resultInv.clear();
			properties = new CraftingPatternProperties();
		} else if (id == 2) {
			if (isActive().fullyActive()) {
				if (!patternInv.getStackInSlot(1).isEmpty()) {
					if (patternInv.getStackInSlot(1).getItem() instanceof ICraftingRecipeContainer) {
						((ICraftingRecipeContainer) patternInv.getStackInSlot(1).getItem()).setRecipe(patternInv.getStackInSlot(1), AutoCraftingHandler.SavedCraftingRecipe.createFromStacks(recipeInv, resultInv, properties, stackProperties));
					}
				} else if (!patternInv.getStackInSlot(0).isEmpty() && patternInv.getStackInSlot(0).getItem() instanceof ICraftingRecipeContainer) {
					ItemStack stack = patternInv.decrStackSize(0, 1);
					((ICraftingRecipeContainer) stack.getItem()).setRecipe(stack, AutoCraftingHandler.SavedCraftingRecipe.createFromStacks(recipeInv, resultInv, properties, stackProperties));
					patternInv.setInventorySlotContents(1, stack);
				}
				if (craftingB != AutoCraftingBehaviour.NO_OP && (patternInv.getStackInSlot(0).isEmpty() || patternInv.getStackInSlot(0).getCount() < 32)) {
					ItemStack stack;
					switch (craftingB) {
					case CRAFT_ONLY:
						grid.getSData().queueCrafting(new StoredItemStack(new ItemStack(StorageInit.craftingPattern), 1), player, -1);
						patternPulled = true;
						break;
					case NO_OP:
						break;
					case STORED_ONLY:
						stack = grid.pullStack(new ItemStack(StorageInit.craftingPattern), 1);
						if (!stack.isEmpty() && stack.getItem() == StorageInit.craftingPattern && stack.getMetadata() == 0) {
							if (!patternInv.getStackInSlot(0).isEmpty()) {
								if (patternInv.getStackInSlot(0).getCount() < 32 && patternInv.getStackInSlot(0).getItem() == StorageInit.craftingPattern && patternInv.getStackInSlot(0).getMetadata() == 0) {
									patternInv.getStackInSlot(0).grow(1);
									stack.shrink(1);
									if (!stack.isEmpty() && stack.getCount() > 0 && stack.getItem() != Items.AIR) {
										stack = grid.pushStack(stack);
										if (!stack.isEmpty() && stack.getCount() > 0 && stack.getItem() != Items.AIR) {
											EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
											world.spawnEntity(item);
										}
									}
								} else {
									stack = grid.pushStack(stack);
									if (!stack.isEmpty() && stack.getCount() > 0 && stack.getItem() != Items.AIR) {
										EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
										world.spawnEntity(item);
									}
								}
							}
						} else {
							stack = grid.pushStack(stack);
							if (!stack.isEmpty() && stack.getCount() > 0 && stack.getItem() != Items.AIR) {
								EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
								world.spawnEntity(item);
							}
						}
						break;
					case USE_STORED_AND_CRAFT:
						stack = grid.getInventory().pullStack(new StoredItemStack(new ItemStack(StorageInit.craftingPattern), 1), 1).getStack();
						if (!stack.isEmpty() && stack.getItem() == StorageInit.craftingPattern && stack.getMetadata() == 0) {
							if (!patternInv.getStackInSlot(0).isEmpty()) {
								if (patternInv.getStackInSlot(0).getCount() < 32 && patternInv.getStackInSlot(0).getItem() == StorageInit.craftingPattern && patternInv.getStackInSlot(0).getMetadata() == 0) {
									patternInv.getStackInSlot(0).grow(1);
									stack.shrink(1);
									if (!stack.isEmpty() && stack.getCount() > 0 && stack.getItem() != Items.AIR) {
										stack = grid.pushStack(stack);
										if (!stack.isEmpty() && stack.getCount() > 0 && stack.getItem() != Items.AIR) {
											EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
											world.spawnEntity(item);
										}
									}
								} else {
									stack = grid.pushStack(stack);
									if (!stack.isEmpty() && stack.getCount() > 0 && stack.getItem() != Items.AIR) {
										EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
										world.spawnEntity(item);
									}
								}
							}
						} else {
							stack = grid.pushStack(stack);
							if (!stack.isEmpty() && stack.getCount() > 0 && stack.getItem() != Items.AIR) {
								EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
								world.spawnEntity(item);
							}
							grid.getSData().queueCrafting(new StoredItemStack(new ItemStack(StorageInit.craftingPattern), 1), player, -1);
							patternPulled = true;
						}
						break;
					default:
						break;
					}
				}
			}
		} else if (id == 3)
			craftingB = AutoCraftingBehaviour.get(extra);
		else if (id == 4)
			properties.useContainerItems = extra == 1;
		else if (id == -9)
			player.openGui(CoreInit.modInstance, GuiIDs.patternTerminal.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		else if (id == -10)
			properties.time = extra;
		else if (id == -11)
			properties.storedOnly = extra == 1;
		else if (id == 5)
			player.openGui(CoreInit.modInstance, GuiIDs.patternOptions.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("termMode", terminalMode);
		compound.setTag("recipeList", TomsModUtils.saveAllItems(recipeInv));
		compound.setTag("upgradeList", TomsModUtils.saveAllItems(upgradeInv));
		compound.setTag("patternInv", TomsModUtils.saveAllItems(patternInv));
		compound.setInteger("craftingB", craftingB.ordinal());
		compound.setTag("resultList", TomsModUtils.saveAllItems(resultInv));
		compound.setTag("patternProperties", properties.writeToNBT(new NBTTagCompound()));
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		terminalMode = compound.getInteger("termMode");
		TomsModUtils.loadAllItems(compound.getTagList("recipeList", 10), recipeInv);
		TomsModUtils.loadAllItems(compound.getTagList("upgradeList", 10), upgradeInv);
		TomsModUtils.loadAllItems(compound.getTagList("patternInv", 10), patternInv);
		TomsModUtils.loadAllItems(compound.getTagList("resultList", 10), resultInv);
		craftingB = AutoCraftingBehaviour.get(compound.getInteger("craftingB"));
		properties = CraftingPatternProperties.loadFromNBT(compound.getCompoundTag("patternProperties"));
	}

	@Override
	public boolean hasPattern() {
		return (patternInv.getStackInSlot(1).getItem() instanceof ICraftingRecipeContainer) || (!patternInv.getStackInSlot(0).isEmpty() && patternInv.getStackInSlot(0).getItem() instanceof ICraftingRecipeContainer);
	}

	@Override
	public int getCraftingBehaviour() {
		return CardType.CRAFTING.equal(upgradeInv.getStackInSlot(0)) ? craftingB.ordinal() : -1;
	}

	public static enum AutoCraftingBehaviour {
		USE_STORED_AND_CRAFT, STORED_ONLY, CRAFT_ONLY, NO_OP;
		public static final AutoCraftingBehaviour[] VALUES = values();

		public static AutoCraftingBehaviour get(int index) {
			return VALUES[MathHelper.abs(index % VALUES.length)];
		}
	}

	@Override
	public void setCraftingBehaviour(int data) {
		craftingB = AutoCraftingBehaviour.get(data);
	}

	@Override
	public <T extends ICraftable> T onStackInput(T stackIn) {
		if (patternPulled) {
			if (stackIn instanceof StoredItemStack) {
				StoredItemStack stack = (StoredItemStack) stackIn;
				if (!stack.getStack().isEmpty() && stack.getStack().getItem() == StorageInit.craftingPattern && stack.getStack().getMetadata() == 0) {
					if (!patternInv.getStackInSlot(0).isEmpty()) {
						if (patternInv.getStackInSlot(0).getCount() < 32 && patternInv.getStackInSlot(0).getItem() == StorageInit.craftingPattern && patternInv.getStackInSlot(0).getMetadata() == 0) {
							patternInv.getStackInSlot(0).grow(1);
							patternInv.markDirty();
							stack.removeQuantity(1);
							patternPulled = false;
							if (!stack.hasQuantity())
								return null;
						}
					} else {
						stack.removeQuantity(1);
						ItemStack s = stack.getStack().copy();
						s.setCount(1);
						patternInv.setInventorySlotContents(0, s);
						patternPulled = false;
						if (!stack.hasQuantity())
							return null;
					}
				}
			}
		}
		return stackIn;
	}

	@Override
	public void receiveNBTPacket(EntityPlayer pl, NBTTagCompound message) {
		if (message.hasKey("color", 3)) {
			super.receiveNBTPacket(pl, message);
		} else if (message.getByte("cfg") == 1) {
			int slot = message.getByte("slot");
			byte amount = message.getByte("amount");
			if (slot >= recipeInv.getSizeInventory()) {
				if (resultInv.getStackInSlot(slot - recipeInv.getSizeInventory()) != null) {
					resultInv.getStackInSlot(slot - recipeInv.getSizeInventory()).setCount(amount);
				}
			} else {
				if (recipeInv.getStackInSlot(slot) != null) {
					recipeInv.getStackInSlot(slot).setCount(amount);
				}
			}
		} else if (message.getByte("cfg") == 2) {
			getPropertiesFor(message.getByte("slot")).readFromNBT(message);
		} else {
			NBTTagList list = message.getTagList("i", 10);
			recipeInv.clear();
			for (int i = 0;i < list.tagCount();++i) {
				NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("s") & 255;

				if (j >= 0 && j < recipeInv.getSizeInventory()) {
					recipeInv.setInventorySlotContents(j, TomsModUtils.loadItemStackFromNBT(nbttagcompound));
				}
			}
			list = message.getTagList("o", 10);
			resultInv.clear();
			for (int i = 0;i < list.tagCount();++i) {
				NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("s") & 255;

				if (j >= 0 && j < resultInv.getSizeInventory()) {
					resultInv.setInventorySlotContents(j, TomsModUtils.loadItemStackFromNBT(nbttagcompound));
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
		gui.sendButtonUpdateToTile(id, extra);
	}

	@Override
	public ICraftable.CraftableProperties getPropertiesFor(int id) {
		return stackProperties[id];
	}

	@Override
	public int getPropertiesLength() {
		return stackProperties.length;
	}

	@Override
	public double getPowerDrained() {
		return 1.2;
	}

	@Override
	public void onInventoryChanged(IInventory inv) {
		if (inv == recipeInv) {
			ReturnData t = AdvancedCraftingHandler.craft(TomsModUtils.getStackArrayFromInventory(recipeInv), null, null, world);
			if (t != null) {
				resultInv.setInventorySlotContents(0, t.getReturnStack());
				resultInv.setInventorySlotContents(1, t.getExtraStack());
				resultInv.setInventorySlotContents(2, ItemStack.EMPTY);
			} else {
				ItemStack s = TomsModUtils.getMathchingRecipe(recipeInv, world);
				if (!s.isEmpty()) {
					resultInv.setInventorySlotContents(0, s);
					resultInv.setInventorySlotContents(1, ItemStack.EMPTY);
					resultInv.setInventorySlotContents(2, ItemStack.EMPTY);
				}
			}
		}
	}

	@Override
	public IInventory getUpgradeInv() {
		return upgradeInv;
	}

	@Override
	public void readFromPacket(NBTTagCompound buf) {
		super.readFromPacket(buf);
		pattern = buf.getBoolean("p");
	}

	@Override
	public void writeToPacket(NBTTagCompound buf) {
		super.writeToPacket(buf);
		buf.setBoolean("p", hasPattern());
	}
}
