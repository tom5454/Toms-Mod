package com.tom.api.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.lwjgl.opengl.GL11;

import mapwriterTm.util.Render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.apis.TomsModUtils;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.multipart.StorageNetworkGrid.CalculatedCrafting;
import com.tom.storage.multipart.StorageNetworkGrid.ClientCraftingStack;
import com.tom.storage.multipart.StorageNetworkGrid.CraftableProperties;
import com.tom.storage.multipart.StorageNetworkGrid.ICache;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftable;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingRecipe;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingReportScreen;
import com.tom.storage.multipart.StorageNetworkGrid.InventoryCache;
import com.tom.storage.multipart.StorageNetworkGrid.NetworkCache;
import com.tom.storage.multipart.StorageNetworkGrid.RecipeReturnInformation;
import com.tom.storage.multipart.StorageNetworkGrid.RecipeToCraft;

import io.netty.buffer.ByteBuf;

public class StoredItemStack implements ICraftable{
	public ItemStack stack;
	public int itemCount;
	public int maxStackSize = 64;
	private static final String ITEM_COUNT_NAME = "c", ITEMSTACK_NAME = "s";
	private boolean first = false;
	private CraftableProperties properties = new CraftableProperties();
	private boolean hasProperties = false;
	public void writeToNBT(NBTTagCompound tag){
		tag.setInteger(ITEM_COUNT_NAME, itemCount);
		if(stack != null)tag.setTag(ITEMSTACK_NAME, stack.writeToNBT(new NBTTagCompound()));
		tag.getCompoundTag(ITEMSTACK_NAME).removeTag("Count");
		if(hasProperties){
			tag.setBoolean("p", hasProperties);
			tag.setTag("prop", properties.writeToNBT(new NBTTagCompound()));
		}
	}
	public static StoredItemStack readFromNBT(NBTTagCompound tag){
		ItemStack cheat = ItemStack.loadItemStackFromNBT(tag);
		tag.getCompoundTag(ITEMSTACK_NAME).setByte("Count", (byte) 1);
		StoredItemStack stack = new StoredItemStack(cheat != null ? cheat : ItemStack.loadItemStackFromNBT(tag.getCompoundTag(ITEMSTACK_NAME)), cheat != null ? cheat.stackSize : tag.getInteger(ITEM_COUNT_NAME));
		if(tag.getBoolean("p")){
			stack.properties.readFromNBT(tag.getCompoundTag("prop"));
		}
		return stack;
	}
	public void writeToPacket(ByteBuf buf){
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		ByteBufUtils.writeTag(buf, tag);
	}
	public static StoredItemStack readFromPacket(ByteBuf buf){
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		return readFromNBT(tag);
	}
	public StoredItemStack(ItemStack stack, int stackSize) {
		if(stack == null)return;
		if(stackSize < 0)stackSize = stack.stackSize;
		this.itemCount = stackSize;
		this.stack = stack.copy();
	}
	private StoredItemStack(ItemStack stack) {
		this(stack, stack.stackSize);
	}
	public int getMaxStackSize(){
		return maxStackSize;
	}
	@Override
	public boolean equals(Object other) {
		if(this == other)return true;
		if(other instanceof ItemStack)return isItemEqual((ItemStack) other);
		if(!(other instanceof StoredItemStack))return false;
		StoredItemStack o = (StoredItemStack) other;
		return TomsModUtils.areItemStacksEqual(stack, o.stack, true, true, false) && (itemCount == -1 ? o.itemCount == 0 : (o.itemCount == -1 ? itemCount == 0 : true));
	}
	@Override
	public StoredItemStack copy(){
		StoredItemStack s = new StoredItemStack(stack.copy(),itemCount);
		s.first = first;
		return s;
	}
	public boolean isItemEqual(ItemStack other){
		return TomsModUtils.areItemStacksEqual(stack, other, true, true, false);
	}
	@Override
	public String toString() {
		return super.toString() + "_" + itemCount + "_X_" + stack.toString();
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addValidRecipes(List<ICraftingRecipe<?>> recipes, List<ICraftingRecipe<?>> validRecipes) {
		for(int i = 0;i<recipes.size();i++){
			ICraftingRecipe r = recipes.get(i);
			List<StoredItemStack> outputs = r.getOutputs();
			for(int j = 0;j<outputs.size();j++){
				StoredItemStack outS = outputs.get(j);
				if(TomsModUtils.areItemStacksEqualOreDict(outS.stack, stack, properties.useMeta, properties.useNBT, false, properties.useOreDict)){
					validRecipes.add(r);
					break;
				}
			}
		}
	}
	@Override
	public void pull(NetworkCache cache, List<ICraftable> requiredStacksToPull) {
		List<StoredItemStack> storedStacks = cache.<StoredItemStack>getCache(InventoryCache.class).getStored();
		List<ICraftable> toRemove = new ArrayList<ICraftable>();
		for(int i = 0;i<storedStacks.size();i++){
			StoredItemStack storedItemStack = storedStacks.get(i);
			if(TomsModUtils.areItemStacksEqualOreDict(storedItemStack.stack, stack, properties.useMeta, properties.useNBT, false, properties.useOreDict)){
				int pull = Math.min(itemCount, storedItemStack.itemCount);
				itemCount -= pull;
				storedItemStack.itemCount -= pull;
				if(pull > 0){
					/*while(pull > 0){
						int pulled = Math.min(pull, storedItemStack.stack.getMaxStackSize());
						pull -= pulled;
						memoryUsage += pulled;
						operations += 1;
						s.stackSize = pulled;

					}*/
					ItemStack s = storedItemStack.stack.copy();
					s.stackSize = pull;
					StorageNetworkGrid.addCraftableToList(new StoredItemStack(s, pull), requiredStacksToPull);
				}
				if(storedItemStack.itemCount < 1){
					toRemove.add(storedItemStack);
				}
			}
		}
		for(int i = 0;i<toRemove.size();i++){
			boolean success = storedStacks.remove(toRemove.get(i));
			if(!success)System.err.println("Remove failed");
		}
	}
	@Override
	public boolean hasQuantity() {
		return itemCount > 0;
	}
	@Override
	public boolean isEqual(ICraftable s) {
		return equals(s);
	}
	@Override
	public void removeQuantity(int value) {
		itemCount -= value;
	}
	@Override
	public int handleSecondaryPull(ICraftable s) {
		int pull = Math.min(itemCount, s.getQuantity());
		itemCount -= pull;
		s.removeQuantity(pull);
		return pull;
	}
	@Override
	public void setNoQuantity() {
		itemCount = 0;
	}
	@Override
	public void add(ICraftable other) {
		itemCount += other.getQuantity();
		//other.setNoQuantity();
	}
	@Override
	public RecipeReturnInformation useRecipe(ICraftingRecipe<?> rIn, NetworkCache cache, List<ICraftable> secondaryOutList, List<ICraftable> requiredStacksToPull, Stack<RecipeToCraft> recipesToCraft, Stack<ICraftable> toCraft) {
		@SuppressWarnings("unchecked")
		ICraftingRecipe<StoredItemStack> r = (ICraftingRecipe<StoredItemStack>) rIn;
		List<StoredItemStack> storedStacks = cache.getCache(InventoryCache.class).getStored();
		int time = 0, operations = 0, memoryUsage = 0;
		boolean doBreak = false;
		if(r.isStoredOnly()){
			List<StoredItemStack> inputs = r.getInputs();
			boolean contains = true;
			for(int j = 0;j<inputs.size();j++){
				StoredItemStack inStack = inputs.get(j).copy();
				boolean stackFound = false;
				for(int k = 0;k<storedStacks.size();k++){
					StoredItemStack storedStack = storedStacks.get(j);
					if(TomsModUtils.areItemStacksEqualOreDict(inStack.stack, storedStack.stack, properties.useMeta, properties.useNBT, false, properties.useOreDict)){
						if(storedStack.itemCount < inStack.itemCount){

						}else{
							stackFound = true;
						}
						break;
					}
				}
				if(!stackFound)contains = false;
			}
			if(contains){
				List<StoredItemStack> outputs = r.getOutputs();
				RecipeToCraft toC = new RecipeToCraft(r);
				/*if(recipesToCraft.contains(toC)){
					for(int j = 0;j<recipesToCraft.size();j++){
						RecipeToCraft c = recipesToCraft.get(i);
						if(c.equals(toC)){
							c.executionTime += 1;
							break;
						}
					}
				}else*/
				recipesToCraft.add(toC);
				time += r.getTime();
				operations += inputs.size();
				//int found =
				for(int j = 0;j<inputs.size();j++){
					StoredItemStack stack = inputs.get(j);
					memoryUsage += stack.itemCount;
					//StoredItemStack s = new StoredItemStack(stack, stack.stackSize);
					//toCraft.add(s);
					StorageNetworkGrid.addCraftableToCraftList(stack, toCraft);
					if(r.useContainerItems()){
						ItemStack containerItem = ForgeHooks.getContainerItem(stack.stack.copy().splitStack(1));
						if(containerItem != null){
							containerItem.stackSize = stack.itemCount;
							StorageNetworkGrid.addCraftableToList(new StoredItemStack(containerItem), secondaryOutList);
						}
					}
				}
				for(int j = 0;j<outputs.size();j++){
					StoredItemStack stack = outputs.get(j).copy();
					if(isEqual(stack)){
						int maxPull = Math.min(stack.itemCount, itemCount);
						stack.itemCount -= maxPull;
						itemCount -= maxPull;
						if(stack.itemCount > 0)StorageNetworkGrid.addCraftableToList(stack, secondaryOutList);
					}else{
						StorageNetworkGrid.addCraftableToList(stack, secondaryOutList);
					}
				}
				doBreak = true;
			}
		}else{
			List<StoredItemStack> inputs = r.getInputs();
			List<StoredItemStack> outputs = r.getOutputs();
			RecipeToCraft toC = new RecipeToCraft(r);
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
			//int found =
			for(int j = 0;j<inputs.size();j++){
				StoredItemStack stack = inputs.get(j).copy();
				memoryUsage += stack.itemCount;
				//StoredItemStack s = new StoredItemStack(stack, stack.stackSize);
				//toCraft.add(s);
				StorageNetworkGrid.addCraftableToCraftList(stack, toCraft);
				if(r.useContainerItems()){
					ItemStack containerItem = ForgeHooks.getContainerItem(stack.stack.copy().splitStack(1));
					if(containerItem != null){
						containerItem.stackSize = stack.itemCount;
						StorageNetworkGrid.addCraftableToList(new StoredItemStack(containerItem), secondaryOutList);
					}
				}
			}
			for(int j = 0;j<outputs.size();j++){
				StoredItemStack stack = outputs.get(j).copy();
				if(isEqual(stack)){
					int maxPull = Math.min(stack.itemCount, itemCount);
					stack.itemCount -= maxPull;
					itemCount -= maxPull;
					if(stack.itemCount > 0)StorageNetworkGrid.addCraftableToList(stack, secondaryOutList);
				}else{
					StorageNetworkGrid.addCraftableToList(stack, secondaryOutList);
				}
			}
			doBreak = true;
		}
		return new RecipeReturnInformation(doBreak, time, operations, memoryUsage);
	}
	@Override
	public void writeObjToNBT(NBTTagCompound t) {
		writeToNBT(t);
	}
	@Override
	public int getQuantity() {
		return itemCount;
	}
	@Override
	public void pullFromGrid(StorageNetworkGrid cache, List<ICraftable> pulledList) {
		int pulled = 0;
		while(pulled < itemCount){
			ItemStack stack = cache.getInventory().pullStack(this, itemCount);
			if(stack != null){
				StorageNetworkGrid.addCraftableToList(new StoredItemStack(stack, stack.stackSize), pulledList);
			}else{
				break;
			}
			pulled += stack.getMaxStackSize();
		}
	}
	@Override
	public ITextComponent serializeTextComponent(TextFormatting color) {
		ITextComponent t = stack.getTextComponent();
		t.getStyle().setColor(color);
		return new TextComponentTranslation("tomsMod.stack", t, itemCount);
	}
	@Override
	public void checkIfIngredientsAreAvailable(NetworkCache cache, List<ICraftable> missingStacks, CalculatedCrafting crafting) {
		boolean stackFound = false;
		List<StoredItemStack> storedStacks = cache.getCache(InventoryCache.class).getStored();
		for(int j = 0;j<storedStacks.size();j++){
			StoredItemStack storedStack = storedStacks.get(j);
			if(TomsModUtils.areItemStacksEqualOreDict(stack, storedStack.stack, properties.useMeta, properties.useNBT, false, properties.useOreDict)){
				stackFound = true;
				if(storedStack.itemCount < itemCount){
					//ItemStack copiedStack = stack.copy();
					int found = Math.min(storedStack.itemCount, itemCount);
					storedStack.itemCount -= found;
					int missing = itemCount;
					itemCount = found;
					missing -= found;
					//copiedStack.stackSize -= found;
					/*if(copiedStack.stackSize < 1){
						//crafting.requiredStacks.remove(this);
						copiedStack = null;
					}*/
					if(storedStack.itemCount < 1){
						storedStacks.remove(j);
					}
					if(missing > 0){
						ItemStack s = stack.copy();
						s.stackSize = 1;
						StoredItemStack storedS = new StoredItemStack(s, missing);
						//if(missingStacks.contains(storedS)){
						boolean f = false;
						for(int k = 0;k<missingStacks.size();k++){
							ICraftable mStack = missingStacks.get(k);
							if(mStack.equals(storedS)){
								((StoredItemStack)mStack).itemCount += storedS.itemCount;
								f = true;
								break;
							}
						}
						//}else{
						if(!f)missingStacks.add(storedS);
						//}
						//itemCount = 0;
					}
				}else{
					storedStack.itemCount -= itemCount;
					//itemCount = 0;
					break;
				}
			}
		}
		if(!stackFound){
			StoredItemStack storedS = copy();
			//if(missingStacks.contains(storedS)){
			boolean found = false;
			for(int k = 0;k<missingStacks.size();k++){
				ICraftable mStack = missingStacks.get(k);
				if(mStack.isEqual(storedS)){
					((StoredItemStack)mStack).itemCount += storedS.itemCount;
					found = true;
					break;
				}
			}
			//}else{
			if(!found)missingStacks.add(storedS);
			itemCount = 0;
			//}
			//crafting.requiredStacks.remove(this);
		}
	}
	@SuppressWarnings("rawtypes")
	@Override
	@SideOnly(Side.CLIENT)
	public void drawEntry(ClientCraftingStack s, int posX, int posY, int mouseX, int mouseY, ICraftingReportScreen screen, boolean isTooltip) {
		if(isTooltip){
			ItemStack stack = this.stack.copy();
			stack.stackSize = 1;
			String name = I18n.format(this.stack.getUnlocalizedName() + ".name");
			if (((StoredItemStack)s.mainStack).stack.getTagCompound() != null && this.stack.getTagCompound().hasKey("display", 10))
			{
				NBTTagCompound nbttagcompound = this.stack.getTagCompound().getCompoundTag("display");

				if (nbttagcompound.hasKey("Name", 8))
				{
					name = nbttagcompound.getString("Name");
				}
			}
			List<String> hovering = TomsModUtils.getStringList(name);
			if(s.stored > 0)hovering.add(I18n.format("tomsMod.storage.available", s.stored));
			if(s.toCraft > 0)hovering.add(I18n.format("tomsMod.storage.toCraft", s.toCraft));
			if(s.missing > 0)hovering.add(I18n.format("tomsMod.storage.missing", s.missing));
			for (int j = 0; j < hovering.size(); ++j)
			{
				if (j == 0)
				{
					hovering.set(j, stack.getRarity().rarityColor + hovering.get(j));
				}
				else
				{
					hovering.set(j, TextFormatting.GRAY + hovering.get(j));
				}
			}
			screen.drawHoveringText(hovering, mouseX, mouseY);
		}else{
			ItemStack stack = this.stack.copy();
			stack.stackSize = 1;
			if(s.missing > 0){
				Render.setColourWithAlphaPercent(0xFF0000,50);
				Render.drawRect(posX, posY, 67, 22);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
			screen.renderItemInGui(stack, posX + 45, posY + 3, -20, -20, 0xFFFFFF);
			GL11.glPushMatrix();
			GL11.glTranslated(posX + 10, posY + 11, screen.getZLevel());
			double scale = 0.5D;
			GL11.glScaled(scale, scale, scale);
			if(s.toCraft > 0)screen.getFontRenderer().drawString(I18n.format("tomsMod.storage.toCraft", TomsModUtils.formatNumber(s.toCraft)), 0, -screen.getFontRenderer().FONT_HEIGHT / 2, 4210752);
			if(s.stored > 0)screen.getFontRenderer().drawString(I18n.format("tomsMod.storage.available", TomsModUtils.formatNumber(s.stored)), 0, s.toCraft > 0 ? (-screen.getFontRenderer().FONT_HEIGHT * 2) : (-screen.getFontRenderer().FONT_HEIGHT / 2), 4210752);
			if(s.missing > 0)screen.getFontRenderer().drawString(I18n.format("tomsMod.storage.missing", TomsModUtils.formatNumber(s.missing)), 0, s.toCraft > 0 || s.stored > 0 ? (screen.getFontRenderer().FONT_HEIGHT) : (screen.getFontRenderer().FONT_HEIGHT / 2), 4210752);
			GL11.glPopMatrix();
		}
	}
	@Override
	public ICraftable pushToGrid(StorageNetworkGrid grid) {
		ItemStack s = stack.copy();
		s.stackSize = itemCount;
		s = grid.pushStack(s);
		return s != null && s.stackSize > 0 ? new StoredItemStack(s, s.stackSize) : null;
	}
	@Override
	public void setQuantity(int value) {
		itemCount = value;
	}
	@Override
	public Class<? extends ICache<?>> getCacheClass() {
		return InventoryCache.class;
	}
	@Override
	public String serializeStringTooltip() {
		return itemCount + " * " + TomsModUtils.getTranslatedName(stack);
	}
	@Override
	public boolean isFirst() {
		return first;
	}
	@Override
	public void setFirst() {
		first = true;
	}
	@Override
	public CraftableProperties getProperties() {
		return properties;
	}
	public StoredItemStack setHasProperites(CraftableProperties p) {
		this.hasProperties = true;
		this.properties = p;
		return this;
	}
}
