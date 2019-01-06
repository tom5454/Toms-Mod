package com.tom.api.inventory;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.grid.StorageNetworkGrid;
import com.tom.api.grid.StorageNetworkGrid.ICraftingReportScreen;
import com.tom.lib.utils.RenderUtil;
import com.tom.storage.handler.AutoCraftingHandler;
import com.tom.storage.handler.ICache;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.InventoryCache;
import com.tom.storage.handler.NetworkCache;
import com.tom.util.TomsModUtils;

import io.netty.buffer.ByteBuf;

public class StoredItemStack implements ICraftable {
	public static class StoredItemStackComparator {
		StoredItemStack stack;

		public StoredItemStackComparator(StoredItemStack stack) {
			this.stack = stack;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (!(other instanceof StoredItemStack))
				return false;
			StoredItemStack o = (StoredItemStack) other;
			return TomsModUtils.areItemStacksEqual(stack.getStack(), o.getStack(), true, true, false) && stack.getQuantity() <= o.getQuantity();
		}
	}

	private ItemStack stack;
	private long itemCount;
	private static final String ITEM_COUNT_NAME = "c", ITEMSTACK_NAME = "s";
	private long level = -1;
	private ICraftable.CraftableProperties properties = new ICraftable.CraftableProperties();
	private boolean hasProperties = false;

	public void writeToNBT(NBTTagCompound tag) {
		tag.setLong(ITEM_COUNT_NAME, getQuantity());
		tag.setTag(ITEMSTACK_NAME, stack.writeToNBT(new NBTTagCompound()));
		tag.getCompoundTag(ITEMSTACK_NAME).removeTag("Count");
		if (hasProperties) {
			tag.setBoolean("p", hasProperties);
			tag.setTag("prop", properties.writeToNBT(new NBTTagCompound()));
		}
	}

	public static StoredItemStack readFromNBT(NBTTagCompound tag) {
		ItemStack cheat = TomsModUtils.loadItemStackFromNBT(tag);
		tag.getCompoundTag(ITEMSTACK_NAME).setByte("Count", (byte) 1);
		StoredItemStack stack = new StoredItemStack(!cheat.isEmpty() ? cheat : TomsModUtils.loadItemStackFromNBT(tag.getCompoundTag(ITEMSTACK_NAME)), !cheat.isEmpty() ? cheat.getCount() : tag.getLong(ITEM_COUNT_NAME));
		if (tag.getBoolean("p")) {
			stack.properties.readFromNBT(tag.getCompoundTag("prop"));
		}
		return !stack.stack.isEmpty() ? stack : null;
	}

	public void writeToPacket(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static StoredItemStack readFromPacket(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		return readFromNBT(tag);
	}

	public StoredItemStack(ItemStack stack, long stackSize) {
		if (stack == null)
			return;
		if (stackSize < 0)
			stackSize = stack.getCount();
		this.setQuantity(stackSize);
		this.stack = stack.copy();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other instanceof ItemStack)
			return isItemEqual((ItemStack) other);
		if (!(other instanceof StoredItemStack))
			return false;
		StoredItemStack o = (StoredItemStack) other;
		return TomsModUtils.areItemStacksEqual(stack, o.stack, true, true, false) && (getQuantity() == -1 ? o.getQuantity() == 0 : (o.getQuantity() == -1 ? getQuantity() == 0 : true));
	}

	@Override
	public StoredItemStack copy() {
		StoredItemStack s = new StoredItemStack(stack.copy(), getQuantity());
		s.level = level;
		return s;
	}

	public boolean isItemEqual(ItemStack other) {
		return TomsModUtils.areItemStacksEqual(stack, other, true, true, false);
	}

	@Override
	public String toString() {
		return super.toString() + "_" + getQuantity() + "_X_" + stack.toString();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addValidRecipes(List<AutoCraftingHandler.ICraftingRecipe<?>> recipes, List<AutoCraftingHandler.ICraftingRecipe<?>> validRecipes) {
		for (int i = 0;i < recipes.size();i++) {
			AutoCraftingHandler.ICraftingRecipe r = recipes.get(i);
			List<StoredItemStack> outputs = r.getOutputs();
			for (int j = 0;j < outputs.size();j++) {
				StoredItemStack outS = outputs.get(j);
				if (TomsModUtils.areItemStacksEqualOreDict(outS.stack, stack, properties.useMeta, properties.useNBT, false, properties.useOreDict)) {
					validRecipes.add(r);
					break;
				}
			}
		}
	}

	@Override
	public void pull(NetworkCache cache, List<ICraftable> requiredStacksToPull) {
		List<StoredItemStack> storedStacks = cache.<StoredItemStack>getCache(InventoryCache.class).getStored();
		List<ICraftable> toRemove = new ArrayList<>();
		for (int i = 0;i < storedStacks.size();i++) {
			StoredItemStack storedItemStack = storedStacks.get(i);
			if (TomsModUtils.areItemStacksEqualOreDict(storedItemStack.stack, stack, properties.useMeta, properties.useNBT, false, properties.useOreDict)) {
				long pull = Math.min(getQuantity(), storedItemStack.getQuantity());
				setQuantity(getQuantity() - pull);
				storedItemStack.setQuantity(storedItemStack.getQuantity() - pull);
				if (pull > 0) {
					/*while(pull > 0){
						int pulled = Math.min(pull, storedItemStack.stack.getMaxStackSize());
						pull -= pulled;
						memoryUsage += pulled;
						operations += 1;
						s.stackSize = pulled;

					}*/
					ItemStack s = storedItemStack.stack.copy();
					AutoCraftingHandler.addCraftableToList(new StoredItemStack(s, pull), requiredStacksToPull);
				}
				if (storedItemStack.getQuantity() < 1) {
					toRemove.add(storedItemStack);
				}
			}
		}
		for (int i = 0;i < toRemove.size();i++) {
			boolean success = storedStacks.remove(toRemove.get(i));
			if (!success)
				System.err.println("Remove failed");
		}
	}

	@Override
	public boolean hasQuantity() {
		return getQuantity() > 0;
	}

	@Override
	public boolean isEqual(ICraftable other) {
		if (other == this)
			return true;
		if (!(other instanceof StoredItemStack))
			return false;
		StoredItemStack o = (StoredItemStack) other;
		return TomsModUtils.areItemStacksEqualOreDict(stack, o.stack, properties.useMeta, properties.useNBT, false, properties.useOreDict) && (getQuantity() == -1 ? o.getQuantity() == 0 : (o.getQuantity() == -1 ? getQuantity() == 0 : true));
	}

	@Override
	public void removeQuantity(long value) {
		setQuantity(getQuantity() - value);
	}

	@Override
	public long handleSecondaryPull(ICraftable s) {
		long pull = Math.min(getQuantity(), s.getQuantity());
		setQuantity(getQuantity() - pull);
		s.removeQuantity(pull);
		return pull;
	}

	@Override
	public void setNoQuantity() {
		setQuantity(0);
	}

	@Override
	public void add(ICraftable other) {
		setQuantity(getQuantity() + other.getQuantity());
		// other.setNoQuantity();
	}

	@Override
	public AutoCraftingHandler.RecipeReturnInformation useRecipe(AutoCraftingHandler.ICraftingRecipe<?> rIn, NetworkCache cache, AutoCraftingHandler.SecondaryOutList secondaryOutList, List<ICraftable> requiredStacksToPull, List<AutoCraftingHandler.RecipeToCraft> recipesToCraft, List<ICraftable> toCraft) {
		@SuppressWarnings("unchecked")
		AutoCraftingHandler.ICraftingRecipe<StoredItemStack> r = (AutoCraftingHandler.ICraftingRecipe<StoredItemStack>) rIn;
		List<StoredItemStack> storedStacks = cache.getCache(InventoryCache.class).getStored();
		int time = 0, operations = 0, memoryUsage = 0;
		boolean doBreak = false;
		if (r.isStoredOnly()) {
			List<StoredItemStack> inputs = r.getInputs();
			boolean contains = true;
			ICraftable.CraftableProperties properties = new ICraftable.CraftableProperties();
			for (int j = 0;j < inputs.size();j++) {
				if (inputs.get(j).isEqual(this)) {
					properties = inputs.get(j).getProperties();
					break;
				}
			}
			for (int j = 0;j < inputs.size();j++) {
				StoredItemStack inStack = inputs.get(j).copy();
				for (int k = 0;k < storedStacks.size();k++) {
					StoredItemStack storedStack = storedStacks.get(j);
					if (TomsModUtils.areItemStacksEqualOreDict(inStack.stack, storedStack.stack, properties.useMeta, properties.useNBT, false, properties.useOreDict)) {
						if (storedStack.hasQuantity()) {
							long max = Math.min(inStack.itemCount, storedStack.itemCount);
							inStack.removeQuantity(max);
							storedStack.removeQuantity(max);
						}
					}

				}
				if (inStack.hasQuantity()) {
					long contain = secondaryOutList.contains(inStack);
					if (inStack.itemCount > contain) {
						contains = false;
						break;
					}
				}
				if (inStack.hasQuantity())
					contains = false;
			}
			if (contains) {
				List<StoredItemStack> outputs = r.getOutputs();
				AutoCraftingHandler.RecipeToCraft toC = new AutoCraftingHandler.RecipeToCraft(r);
				/*if(recipesToCraft.contains(toC)){
					for(int j = 0;j<recipesToCraft.size();j++){
						RecipeToCraft c = recipesToCraft.get(i);
						if(c.equals(toC)){
							c.executionTime += 1;
							break;
						}
					}
				}else*/
				toC.setResult(getLevel() == 0);
				recipesToCraft.add(toC);
				time += r.getTime();
				operations += inputs.size();
				// int found =
				for (int j = 0;j < inputs.size();j++) {
					StoredItemStack stack = inputs.get(j);
					memoryUsage += stack.getQuantity();
					// StoredItemStack s = new StoredItemStack(stack,
					// stack.stackSize);
					// toCraft.add(s);
					AutoCraftingHandler.addCraftableToCraftList(stack, toCraft);
					if (r.useContainerItems()) {
						ItemStack containerItem = ForgeHooks.getContainerItem(stack.stack.copy().splitStack(1));
						if (containerItem != null) {
							AutoCraftingHandler.addCraftableToList(new StoredItemStack(containerItem, stack.getQuantity()), secondaryOutList);
						}
					}
				}
				for (int j = 0;j < outputs.size();j++) {
					StoredItemStack stack = outputs.get(j).copy();
					if (isEqual(stack)) {
						long maxPull = Math.min(stack.getQuantity(), getQuantity());
						stack.setQuantity(stack.getQuantity() - maxPull);
						setQuantity(getQuantity() - maxPull);
						if (stack.getQuantity() > 0)
							AutoCraftingHandler.addCraftableToList(stack, secondaryOutList);
					} else {
						AutoCraftingHandler.addCraftableToList(stack, secondaryOutList);
					}
				}
				doBreak = true;
			}
		} else {
			List<StoredItemStack> inputs = r.getInputs();
			List<StoredItemStack> outputs = r.getOutputs();
			AutoCraftingHandler.RecipeToCraft toC = new AutoCraftingHandler.RecipeToCraft(r);
			toC.setResult(getLevel() == 0);
			/*if(recipesToCraft.contains(toC)){
				for(int j = 0;j<recipesToCraft.size();j++){
					RecipeToCraft c = recipesToCraft.get(j);
					if(c.equals(toC)){
						c.executionTime += 1;
						break;
					}
				}
			}else*/
			recipesToCraft.add(toC);
			time += r.getTime();
			operations += inputs.size();
			// int found =
			for (int j = 0;j < inputs.size();j++) {
				StoredItemStack stack = inputs.get(j).copy();
				memoryUsage += stack.getQuantity();
				// StoredItemStack s = new StoredItemStack(stack,
				// stack.stackSize);
				// toCraft.add(s);
				AutoCraftingHandler.addCraftableToCraftList(stack, toCraft);
				if (r.useContainerItems()) {
					ItemStack containerItem = ForgeHooks.getContainerItem(stack.stack.copy().splitStack(1));
					if (containerItem != null) {
						AutoCraftingHandler.addCraftableToList(new StoredItemStack(containerItem, stack.getQuantity()), secondaryOutList);
					}
				}
			}
			for (int j = 0;j < outputs.size();j++) {
				StoredItemStack stack = outputs.get(j).copy();
				if (isEqual(stack)) {
					long maxPull = Math.min(stack.getQuantity(), getQuantity());
					stack.setQuantity(stack.getQuantity() - maxPull);
					setQuantity(getQuantity() - maxPull);
					if (stack.getQuantity() > 0)
						AutoCraftingHandler.addCraftableToList(stack, secondaryOutList);
				} else {
					AutoCraftingHandler.addCraftableToList(stack, secondaryOutList);
				}
			}
			doBreak = true;
		}
		return new AutoCraftingHandler.RecipeReturnInformation(doBreak, time, operations, memoryUsage);
	}

	@Override
	public void writeObjToNBT(NBTTagCompound t) {
		writeToNBT(t);
	}

	@Override
	public long getQuantity() {
		return stack.isEmpty() ? 0 : itemCount;
	}

	@Override
	public ITextComponent serializeTextComponent(TextFormatting color) {
		ITextComponent t = stack.getTextComponent();
		t.getStyle().setColor(color);
		return new TextComponentTranslation("tomsMod.stack", t, getQuantity());
	}

	@Override
	public void checkIfIngredientsAreAvailable(NetworkCache cache, List<ICraftable> missingStacks, AutoCraftingHandler.CalculatedCrafting crafting) {
		boolean stackFound = false;
		List<StoredItemStack> storedStacks = cache.getCache(InventoryCache.class).getStored();
		for (int j = 0;j < storedStacks.size();j++) {
			StoredItemStack storedStack = storedStacks.get(j);
			if (TomsModUtils.areItemStacksEqualOreDict(stack, storedStack.stack, properties.useMeta, properties.useNBT, false, properties.useOreDict)) {
				stackFound = true;
				if (storedStack.getQuantity() < getQuantity()) {
					// ItemStack copiedStack = stack.copy();
					long found = Math.min(storedStack.getQuantity(), getQuantity());
					storedStack.setQuantity(storedStack.getQuantity() - found);
					long missing = getQuantity();
					setQuantity(found);
					missing -= found;
					// copiedStack.stackSize -= found;
					/*if(copiedStack.stackSize < 1){
						//crafting.requiredStacks.remove(this);
						copiedStack = null;
					}*/
					if (storedStack.getQuantity() < 1) {
						storedStacks.remove(j);
					}
					if (missing > 0) {
						ItemStack s = stack.copy();
						s.setCount(1);
						StoredItemStack storedS = new StoredItemStack(s, missing);
						// if(missingStacks.contains(storedS)){
						boolean f = false;
						for (int k = 0;k < missingStacks.size();k++) {
							ICraftable mStack = missingStacks.get(k);
							if (mStack.equals(storedS)) {
								((StoredItemStack) mStack).setQuantity(((StoredItemStack) mStack).getQuantity() + storedS.getQuantity());
								f = true;
								break;
							}
						}
						// }else{
						if (!f)
							missingStacks.add(storedS);
						// }
						// itemCount = 0;
					}
				} else {
					storedStack.setQuantity(storedStack.getQuantity() - itemCount);
					// itemCount = 0;
					break;
				}
			}
		}
		if (!stackFound) {
			StoredItemStack storedS = copy();
			// if(missingStacks.contains(storedS)){
			boolean found = false;
			for (int k = 0;k < missingStacks.size();k++) {
				ICraftable mStack = missingStacks.get(k);
				if (mStack.isEqual(storedS)) {
					((StoredItemStack) mStack).setQuantity(((StoredItemStack) mStack).getQuantity() + storedS.getQuantity());
					found = true;
					break;
				}
			}
			// }else{
			if (!found)
				missingStacks.add(storedS);
			setQuantity(0);
			// }
			// crafting.requiredStacks.remove(this);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	@SideOnly(Side.CLIENT)
	public void drawEntry(AutoCraftingHandler.ClientCraftingStack s, int posX, int posY, int mouseX, int mouseY, ICraftingReportScreen screen, boolean isTooltip) {
		if (isTooltip) {
			ItemStack stack = this.stack.copy();
			stack.setCount(1);
			String name = I18n.format(this.stack.getUnlocalizedName() + ".name");
			if (((StoredItemStack) s.mainStack).stack.getTagCompound() != null && this.stack.getTagCompound().hasKey("display", 10)) {
				NBTTagCompound nbttagcompound = this.stack.getTagCompound().getCompoundTag("display");

				if (nbttagcompound.hasKey("Name", 8)) {
					name = nbttagcompound.getString("Name");
				}
			}
			List<String> hovering = TomsModUtils.getStringList(name);
			if (s.stored > 0)
				hovering.add(I18n.format(s.crafting ? "tomsMod.storage.stored" : "tomsMod.storage.available", s.stored));
			if (s.toCraft > 0)
				hovering.add(I18n.format("tomsMod.storage.toCraft", s.toCraft));
			if (s.missing > 0)
				hovering.add(I18n.format(s.crafting ? "tomsMod.storage.crafting" : "tomsMod.storage.missing", s.missing));
			for (int j = 0;j < hovering.size();++j) {
				if (j == 0) {
					hovering.set(j, stack.getRarity().rarityColor + hovering.get(j));
				} else {
					hovering.set(j, TextFormatting.GRAY + hovering.get(j));
				}
			}
			screen.drawHoveringText(hovering, mouseX, mouseY);
		} else {
			ItemStack stack = this.stack.copy();
			stack.setCount(1);
			if (s.missing > 0) {
				RenderUtil.setColourWithAlphaPercent(0xFF0000, 50);
				RenderUtil.drawRect(posX, posY, 67, 22);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
			screen.renderItemInGui(stack, posX + 45, posY + 3, -20, -20, 0xFFFFFF);
			GL11.glPushMatrix();
			GL11.glTranslated(posX + 10, posY + 11, screen.getZLevel());
			double scale = 0.5D;
			GL11.glScaled(scale, scale, scale);
			if (s.toCraft > 0)
				screen.getFontRenderer().drawString(I18n.format("tomsMod.storage.toCraft", TomsModUtils.formatNumber(s.toCraft)), 0, -screen.getFontRenderer().FONT_HEIGHT / 2, 4210752);
			if (s.stored > 0)
				screen.getFontRenderer().drawString(I18n.format(s.crafting ? "tomsMod.storage.stored" : "tomsMod.storage.available", TomsModUtils.formatNumber(s.stored)), 0, s.toCraft > 0 ? (-screen.getFontRenderer().FONT_HEIGHT * 2) : (-screen.getFontRenderer().FONT_HEIGHT / 2), 4210752);
			if (s.missing > 0)
				screen.getFontRenderer().drawString(I18n.format(s.crafting ? "tomsMod.storage.crafting" : "tomsMod.storage.missing", TomsModUtils.formatNumber(s.missing)), 0, s.toCraft > 0 || s.stored > 0 ? (screen.getFontRenderer().FONT_HEIGHT) : (screen.getFontRenderer().FONT_HEIGHT / 2), 4210752);
			GL11.glPopMatrix();
		}
	}

	@Override
	public ICraftable pushToGrid(StorageNetworkGrid grid) {
		ICraftable s = grid.pushStack(copy());
		return s != null && s.hasQuantity() ? s : null;
	}

	@Override
	public void setQuantity(long value) {
		itemCount = value;
	}

	@Override
	public Class<? extends ICache<?>> getCacheClass() {
		return InventoryCache.class;
	}

	@Override
	public String serializeStringTooltip() {
		return getQuantity() + " * " + TomsModUtils.getTranslatedName(stack);
	}

	@Override
	public ICraftable.CraftableProperties getProperties() {
		return properties;
	}

	public StoredItemStack setHasProperites(ICraftable.CraftableProperties p) {
		this.hasProperties = true;
		this.properties = p;
		return this;
	}

	@Override
	public long getMaxStackSize() {
		return stack.getMaxStackSize();
	}

	public StoredItemStack copy(long newCount) {
		StoredItemStack ret = copy();
		ret.setQuantity(newCount);
		return ret;
	}

	public ItemStack getStack() {
		return stack;
	}

	public ItemStack getActualStack() {
		ItemStack s = stack.copy();
		s.setCount((int) itemCount);
		return s;
	}

	@Override
	public String getUnlocalizedName() {
		return stack.getUnlocalizedName();
	}

	@Override
	public String getDisplayName() {
		return stack.getDisplayName();
	}

	@Override
	public ResourceLocation getDelegateName() {
		return stack.getItem().delegate.name();
	}

	@Override
	public int getBaseBytes() {
		return 32;
	}

	@Override
	public int getQuantityBytes() {
		return MathHelper.ceil(itemCount / (getMaxStackSize() / 4F));
	}

	@Override
	public long getMaxQuantityForBytes(int bytes) {
		float f = getMaxStackSize() / 4F;
		long ret = MathHelper.lfloor(bytes * f);
		if (ret == 0 && f > 1 && itemCount % f != 0) {
			ret = MathHelper.lfloor(f - itemCount % f);
		}
		return ret < 1 ? 0 : ret;
	}

	@Override
	public long getLevel() {
		return level;
	}

	@Override
	public void setLevel(long level) {
		this.level = level;
	}
}
