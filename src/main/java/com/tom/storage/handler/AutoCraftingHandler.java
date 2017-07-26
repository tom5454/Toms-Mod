package com.tom.storage.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Function;

import com.tom.api.grid.IGridUpdateListener;
import com.tom.api.inventory.StoredItemStack;
import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageCraftingReportSync;
import com.tom.network.messages.MessageCraftingReportSync.MessageType;
import com.tom.network.messages.MessageNBT;

public class AutoCraftingHandler {

	private static final long maxCycles = 1000000L;

	public static AutoCraftingHandler.CalculatedCrafting calculateCrafting(List<AutoCraftingHandler.ICraftingRecipe<?>> recipes, ICraftable stackToCraft, NetworkCache cache) throws AutoCraftingHandler.TooComplexCraftingException {
		LinkedList<ICraftable> toCraft = new LinkedList<>();
		// toCraft.add(stackToCraft.copy());
		ICraftable stackToCraft2 = stackToCraft.copy();
		stackToCraft2.setLevel(0);
		addCraftableToCraftList(stackToCraft2, toCraft);
		Map<Long, List<ICraftable>> secondaryOutList = new HashMap<>();
		List<ICraftable> requiredStacksToPull = new ArrayList<>();
		long time = 0;
		long operations = 0;
		long memoryUsage = 0;
		Map<Long, List<AutoCraftingHandler.RecipeToCraft>> recipesToCraft = new HashMap<>();
		long cycles = 0L;
		while (!toCraft.isEmpty()) {
			ICraftable cStack = toCraft.pop();
			List<AutoCraftingHandler.ICraftingRecipe<?>> validRecipes = new ArrayList<>();
			cStack.addValidRecipes(recipes, validRecipes);
			if (cStack.getLevel() != 0) {
				cStack.pull(cache, requiredStacksToPull);
			}
			cycles++;
			while (cStack.hasQuantity()) {
				boolean skip = false;
				for (Entry<Long, List<ICraftable>> e : secondaryOutList.entrySet()) {
					if (e.getKey() >= cStack.getLevel()) {
						List<ICraftable> secondaryOutListC = e.getValue();
						for (int i = 0;i < secondaryOutListC.size();i++) {
							ICraftable s = secondaryOutListC.get(i);
							if (cStack.isEqual(s)) {
								skip = true;
								/*int pulled = s.splitStack(Math.min(cStack.itemCount, s.stackSize)).stackSize;
								cStack.itemCount -= pulled;
								 */
								long pulled = cStack.handleSecondaryPull(s);
								memoryUsage += pulled;
								operations += 1;
								if (!s.hasQuantity()) {
									secondaryOutListC.remove(s);
								}
								break;
							}
						}
					}
				}
				cycles++;
				if (!skip) {
					/*boolean found = false;
					int lastC = -1;
					int rID = 0;*/
					if (validRecipes.isEmpty()) {
						// while(cStack.itemCount > 0){
						// int pulled = Math.min(cStack.itemCount,
						// cStack.stack.getMaxStackSize());
						// cStack.itemCount -= pulled;
						ICraftable s = cStack.copy();
						// s.stackSize = cStack.itemCount;
						operations += 1;
						cStack.setNoQuantity();
						// requiredStacksToPull.add(s);
						addCraftableToList(s, requiredStacksToPull);
						break;
						// }
					} else {
						for (int i = 0;i < validRecipes.size();i++) {
							AutoCraftingHandler.ICraftingRecipe<?> r = validRecipes.get(i);
							List<ICraftable> toAdd = new ArrayList<>();
							AutoCraftingHandler.RecipeReturnInformation info = cStack.useRecipe(r, cache, new AutoCraftingHandler.SecondaryOutList(secondaryOutList, cStack.getLevel()), requiredStacksToPull, getOrDef(recipesToCraft, cStack.getLevel()), toAdd);
							if (info.success) {
								time += info.time;
								operations += info.operations;
								memoryUsage += info.memoryUsage;
								for (int j = 0;j < toAdd.size();j++) {
									toAdd.get(j).setLevel(cStack.getLevel() + 1);
									addCraftableToCraftList(toAdd.get(j), toCraft);
								}
								break;
							}
						}
					}
					/*if(!found){
						if(rID == -1){
							ICraftingRecipe r = validRecipes.get(rID);
						}
					}*/
				}
				checkCycles(cycles);
			}
			checkCycles(cycles);
		}
		AutoCraftingHandler.CalculatedCrafting c = new AutoCraftingHandler.CalculatedCrafting();
		c.mainStack = stackToCraft;
		c.memorySize = memoryUsage;
		c.operationCount = operations;
		c.time = time;
		c.requiredStacks = requiredStacksToPull;
		c.recipesToCraft = compileMapToStack(recipesToCraft, true);
		c.secondaryOutList = compileMapToStack(secondaryOutList, false);
		return c;
	}

	private static <T> Stack<T> compileMapToStack(Map<Long, List<T>> recipesToCraft, boolean sort) {
		Stack<T> ret = new Stack<>();
		for (Entry<Long, List<T>> e : recipesToCraft.entrySet()) {
			List<T> list = e.getValue();
			if (sort)
				list = sortList(list);
			ret.addAll(list);
		}
		return ret;
	}

	private static <T> List<T> sortList(List<T> list) {
		List<T> ret = new ArrayList<>();
		List<T> values = new ArrayList<>();
		for (int i = 0;i < list.size();i++) {
			if (!values.contains(list.get(i)))
				values.add(list.get(i));
		}
		for (int i = 0;i < values.size();i++) {
			T value = values.get(i);
			for (int j = 0;j < list.size();j++) {
				if (value.equals(list.get(j))) {
					ret.add(list.get(j));
				}
			}
		}
		return ret;
	}

	private static <T> List<T> getOrDef(Map<Long, List<T>> map, Long key) {
		if (map.get(key) == null)
			map.put(key, new ArrayList<T>());
		return map.get(key);
	}

	private static void checkCycles(long cycles) throws AutoCraftingHandler.TooComplexCraftingException {
		if (cycles > maxCycles)
			throw new AutoCraftingHandler.TooComplexCraftingException();
	}

	public static void addStackToList(ItemStack stack, List<ItemStack> stackList) {
		for (int i = 0;i < stackList.size();i++) {
			ItemStack s = stackList.get(i);
			if (TomsModUtils.areItemStacksEqualOreDict(stack, s, true, true, false, true)) {
				int spaceInStack = s.getMaxStackSize() - s.getCount();
				if (spaceInStack > 0) {
					s.grow(stack.splitStack(Math.min(spaceInStack, stack.getCount())).getCount());
					if (stack.getCount() < 1) { return; }
				}
			}
		}
		if (stack.getCount() > 0) {
			while (stack.getCount() > 0) {
				ItemStack s = stack.copy();
				int spaceInStack = stack.getMaxStackSize();
				s.setCount(stack.splitStack(Math.min(spaceInStack, stack.getCount())).getCount());
				stackList.add(s);
			}
		}
	}

	public static void addStackToListIgnoreStackSize(ItemStack stack, List<ItemStack> stackList) {
		for (int i = 0;i < stackList.size();i++) {
			ItemStack s = stackList.get(i);
			if (TomsModUtils.areItemStacksEqualOreDict(stack, s, true, true, false, true)) {
				s.grow(stack.getCount());
				return;
			}
		}
		/*if(stack.stackSize > 0){
			while(stack.stackSize > 0){
				ItemStack s = stack.copy();
				int spaceInStack = stack.getMaxStackSize();
				s.stackSize = stack.splitStack(Math.min(spaceInStack, stack.stackSize)).stackSize;
		
			}
		}*/
		stackList.add(stack.copy());
	}

	public static void addCraftableToList(ICraftable stack, List<ICraftable> stackList) {
		for (int i = 0;i < stackList.size();i++) {
			ICraftable s = stackList.get(i);
			if (stack.isEqual(s)) {
				s.add(stack);
				return;
			}
		}
		/*if(stack.stackSize > 0){
			while(stack.stackSize > 0){
				ItemStack s = stack.copy();
				int spaceInStack = stack.getMaxStackSize();
				s.stackSize = stack.splitStack(Math.min(spaceInStack, stack.stackSize)).stackSize;
		
			}
		}*/
		stackList.add(stack.copy());
	}

	public static void addCraftableToCraftList(ICraftable stack, List<ICraftable> stackList) {
		for (int i = 0;i < stack.getQuantity();i++) {
			ICraftable c = stack.copy();
			c.setQuantity(1);
			stackList.add(c);
		}
	}

	public static void addCraftableToList(ICraftable stack, AutoCraftingHandler.SecondaryOutList secondaryOutList) {
		secondaryOutList.add(stack);
	}

	public static AutoCraftingHandler.CalculatedClientCrafting readCalculatedCraftingFromNBT(NBTTagCompound tag) {
		AutoCraftingHandler.CalculatedClientCrafting cc = new AutoCraftingHandler.CalculatedClientCrafting();
		List<AutoCraftingHandler.ClientCraftingStack<?>> list = new ArrayList<>();
		NBTTagList tagList = tag.getTagList("c", 10);
		for (int i = 0;i < tagList.tagCount();i++) {
			NBTTagCompound t = tagList.getCompoundTagAt(i);
			addCraftableToList(CacheRegistry.readFromNBT(t), list, 0);
		}
		tagList = tag.getTagList("l", 10);
		for (int i = 0;i < tagList.tagCount();i++) {
			NBTTagCompound t = tagList.getCompoundTagAt(i);
			addCraftableToList(CacheRegistry.readFromNBT(t), list, 1);
		}
		cc.cpus = new ArrayList<>();
		NBTTagList l = tag.getTagList("p", 3);
		for (int i = 0;i < l.tagCount();i++) {
			cc.cpus.add(((NBTTagInt) l.get(i)).getInt());
		}
		l = tag.getTagList("mi", 10);
		boolean hasMissing = false;
		for (int i = 0;i < l.tagCount();i++) {
			ICraftable s = CacheRegistry.readFromNBT(l.getCompoundTagAt(i));
			if (s != null) {
				addCraftableToList(s, list, -1);
				hasMissing = true;
			}
		}
		cc.stacks = list;
		cc.mainStack = CacheRegistry.readFromNBT(tag.getCompoundTag("m"));
		cc.cpuId = -1;
		cc.time = tag.getInteger("t");
		cc.memory = tag.getInteger("s");
		cc.operations = tag.getInteger("o");
		cc.hasMissing = hasMissing;
		return cc;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void addCraftableToList(ICraftable stack, List<AutoCraftingHandler.ClientCraftingStack<?>> stackList, int toStored) {
		for (int i = 0;i < stackList.size();i++) {
			AutoCraftingHandler.ClientCraftingStack s = stackList.get(i);
			if (stack.isEqual(s.mainStack)) {
				if (toStored == 1)
					s.stored += stack.getQuantity();
				else if (toStored == -1)
					s.missing += stack.getQuantity();
				else
					s.toCraft += stack.getQuantity();
				return;
			}
		}
		AutoCraftingHandler.ClientCraftingStack s = new AutoCraftingHandler.ClientCraftingStack();
		s.mainStack = stack.copy();
		if (toStored == 1)
			s.stored += stack.getQuantity();
		else if (toStored == -1)
			s.missing += stack.getQuantity();
		else
			s.toCraft += stack.getQuantity();
		stackList.add(s);
	}

	public static void addStackToListIgnoreStackSize(ItemStack stack, Stack<StoredItemStack> stackList) {
		for (int i = 0;i < stackList.size();i++) {
			StoredItemStack s = stackList.get(i);
			if (TomsModUtils.areItemStacksEqualOreDict(stack, s.getStack(), true, true, false, true)) {
				s.removeQuantity(-stack.getCount());
				return;
			}
		}
		stackList.add(new StoredItemStack(stack, stack.getCount()));
	}

	public static void sendCompiledCraftingTo(AutoCraftingHandler.CompiledCalculatedCrafting c, EntityPlayerMP player) {
		sendCompiledCraftingTo(c, player, new NBTTagCompound());
	}

	public static void sendCompiledCraftingTo(AutoCraftingHandler.CompiledCalculatedCrafting c, EntityPlayerMP player, NBTTagCompound extra) {
		NetworkHandler.sendTo(new MessageCraftingReportSync(c.main, c.opertaions, c.memory, c.time, c.cpus, c.missingStacks.size(), c.recipes.size(), c.toPull.size(), extra), player);
		sendTagList(c.missingStacks, player, MessageType.MISSING);
		sendTagList(c.recipes, player, MessageType.RECIPE);
		sendTagList(c.toPull, player, MessageType.NORMAL);
	}

	private static void sendTagList(List<NBTTagCompound> tagList, EntityPlayerMP player, MessageType id) {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		tag.setTag("m", list);
		tag.setByte("id", (byte) id.ordinal());
		tag.setBoolean("r", true);
		tag.setBoolean("s", true);
		for (int i = 0;i < tagList.size();i++) {
			list.appendTag(tagList.get(i));
		}
		NetworkHandler.sendTo(new MessageNBT(tag), player);
	}

	public static interface ICraftingRecipe<T extends ICraftable> {
		boolean isStoredOnly();

		List<T> getOutputs();

		List<T> getInputs();

		int getTime();

		boolean execute(boolean doExecute);

		void writeToNBT(NBTTagCompound tag);

		boolean useContainerItems();

		BlockPos getPos2();

		int getDim();

		int getExtraData();

		int getSlot();
	}

	public static interface ICraftingHandler<T extends ICraftable> extends StorageNetworkGrid.IPrioritized, IGridUpdateListener {
		List<ICraftingRecipe<T>> getRecipes();

		boolean executeRecipe(ICraftingRecipe<T> recipe, boolean doExecute);

		<C extends ICache<T>> Class<C> getCraftableCacheClass();

		BlockPos getPos2();

		int getDim();

		int getExtraData();
	}

	public static class CalculatedCrafting {
		public Stack<RecipeToCraft> recipesToCraft;
		public long time;
		public long operationCount;
		public long memorySize;
		public List<ICraftable> requiredStacks;
		public List<ICraftable> secondaryOutList;
		public ICraftable mainStack;
		public String queuedBy;
		public int totalTime = 0;

		public void writeToClientNBTPacket(CompiledCalculatedCrafting c) {
			c.time = time;
			List<ICraftable> toCraft = new ArrayList<>();
			for (int i = 0;i < recipesToCraft.size();i++) {
				recipesToCraft.get(i).getOutputsIgnoreStacksize().forEach(s -> addCraftableToList(s, toCraft));
			}
			for (int i = 0;i < toCraft.size();i++) {
				NBTTagCompound t = new NBTTagCompound();
				CacheRegistry.writeToNBT(toCraft.get(i), t);
				c.recipes.add(t);
			}
			List<ICraftable> toPull = new ArrayList<>();
			for (int i = 0;i < requiredStacks.size();i++) {
				ICraftable o = requiredStacks.get(i);
				if (o.hasQuantity()) {
					addCraftableToList(o, toPull);
				}
			}
			for (int i = 0;i < toPull.size();i++) {
				NBTTagCompound t = new NBTTagCompound();
				CacheRegistry.writeToNBT(toPull.get(i), t);
				c.toPull.add(t);
			}
			NBTTagCompound t = new NBTTagCompound();
			CacheRegistry.writeToNBT(mainStack, t);
			c.main = t;
			c.opertaions = operationCount;
			c.memory = memorySize;
		}
	}

	public static class RecipeToCraft {
		private ICraftingRecipe<?> recipe;
		public List<ICraftable> stackToCraftClient;
		private boolean result;

		// public int time = 0;
		public RecipeToCraft(ICraftingRecipe<?> r) {
			recipe = r;
		}

		public boolean isResult() {
			return result;
		}

		public void setResult(boolean result) {
			this.result = result;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (!(other instanceof RecipeToCraft))
				return false;
			RecipeToCraft o = (RecipeToCraft) other;
			return recipe == o.recipe;
		}

		public boolean execute(boolean doExecute, List<ICraftable> stored) {
			/*for(int i = 0;i<(executionTime - executed);i++){
				if(!recipe.execute()){
					executed += i;
					return false;
				}
			}
			executed = executionTime;*/
			List<ICraftable> inputs = getInputsIgnoreStacksize();
			// List<ItemStack> inputs2 = getInputsIgnoreStacksize();
			Map<Integer, Long> ids = new HashMap<>();
			for (int i = 0;i < stored.size();i++) {
				ICraftable storedS = stored.get(i).copy();
				List<ICraftable> requiredToRemove = new ArrayList<>();
				for (int j = 0;j < inputs.size();j++) {
					ICraftable input = inputs.get(j);
					if (input.isEqual(storedS)) {
						ids.put(i, input.handleSecondaryPull(storedS));
						if (!input.hasQuantity())
							requiredToRemove.add(input);
						break;
					}
				}
				for (int j = 0;j < requiredToRemove.size();j++) {
					inputs.remove(requiredToRemove.get(j));
				}
			}
			if (inputs.isEmpty() && recipe.execute(doExecute)) {
				if (doExecute) {
					List<ICraftable> requiredToRemove = new ArrayList<>();
					for (Entry<Integer, Long> i : ids.entrySet()) {
						stored.get(i.getKey()).removeQuantity(i.getValue());
						if (!stored.get(i.getKey()).hasQuantity())
							requiredToRemove.add(stored.get(i.getKey()));
					}
					for (int j = 0;j < requiredToRemove.size();j++) {
						stored.remove(requiredToRemove.get(j));
					}
				}
				return true;
			} else
				return false;
		}

		/*@Override
		public List<ItemStack> getOutputs() {
			List<ItemStack> stackList = new ArrayList<ItemStack>();
			List<ItemStack> outputs = recipe.getOutputs();
			for(int j = 0;j<outputs.size();j++){
				ItemStack s = outputs.get(j).copy();
				int req = s.stackSize * executed;
				while(req > 0){
					int max = Math.min(s.getMaxStackSize(), req);
					req -= max;
					ItemStack s2 = s.copy();
					s2.stackSize = max;
					stackList.add(s2);
				}
			}
			return stackList;
		}
		@Override
		public List<ItemStack> getInputs() {
			List<ItemStack> stackList = new ArrayList<ItemStack>();
			List<ItemStack> inputs = recipe.getInputs();
			for(int j = 0;j<inputs.size();j++){
				ItemStack s = inputs.get(j).copy();
				int req = s.stackSize * executed;
				while(req > 0){
					int max = Math.min(s.getMaxStackSize(), req);
					req -= max;
					ItemStack s2 = s.copy();
					s2.stackSize = max;
					stackList.add(s2);
				}
			}
			return stackList;
		}*/
		public int getTime() {
			return recipe.getTime();
		}

		public List<ICraftable> getInputsIgnoreStacksize() {
			List<ICraftable> stackList = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<ICraftable> inputs = (List<ICraftable>) recipe.getInputs();
			for (int i = 0;i < inputs.size();i++) {
				ICraftable s = inputs.get(i).copy();
				boolean f = false;
				for (int j = 0;j < stackList.size();j++) {
					if (s.isEqual(stackList.get(j))) {
						/*int req = s.stackSize;
						ItemStack s2 = s.copy();
						s2.stackSize = req;
						stackList.add(s2);*/
						stackList.get(j).add(s);
						f = true;
					}
				}
				if (!f)
					stackList.add(s);
			}
			return stackList;
		}

		public List<ICraftable> getOutputsIgnoreStacksize() {
			List<ICraftable> stackList = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<ICraftable> outputs = (List<ICraftable>) recipe.getOutputs();
			for (int i = 0;i < outputs.size();i++) {
				ICraftable s = outputs.get(i).copy();
				boolean f = false;
				for (int j = 0;j < stackList.size();j++) {
					if (s.isEqual(stackList.get(j))) {
						/*int req = s.stackSize;
						ItemStack s2 = s.copy();
						s2.stackSize = req;
						stackList.add(s2);*/
						stackList.get(j).add(s);
						f = true;
					}
				}
				if (!f)
					stackList.add(s);
			}
			return stackList;
		}

		public void writeToClientNBTPacket(NBTTagCompound tag) {
			NBTTagList list = new NBTTagList();
			List<ICraftable> outputs = getOutputsIgnoreStacksize();
			for (int i = 0;i < outputs.size();i++) {
				ICraftable o = outputs.get(i);
				// int count = o.stackSize;
				// o = o.copy();
				// o.stackSize = 1;
				NBTTagCompound t = new NBTTagCompound();
				CacheRegistry.writeToNBT(o, t);
				// t.removeTag("Count");
				// t.setInteger("Count", count);
				list.appendTag(t);
			}
			tag.setTag("l", list);
		}

		public static RecipeToCraft loadFromClientNBT(NBTTagCompound tag) {
			RecipeToCraft r = new RecipeToCraft(null);
			r.stackToCraftClient = new ArrayList<>();
			NBTTagList list = tag.getTagList("l", 10);
			for (int i = 0;i < list.tagCount();i++) {
				NBTTagCompound t = list.getCompoundTagAt(i);
				/*int count = t.getInteger("Count");
				t.removeTag("Count");
				t.setByte("Count", (byte) 1);
				ItemStack o = ItemStack.loadItemStackFromNBT(t);
				o.stackSize = count;*/
				r.stackToCraftClient.add(CacheRegistry.readFromNBT(t));
			}
			return r;
		}

		public void addRequiredStacks(List<ICraftable> requiredStacks) {
			List<ICraftable> outputs = getOutputsIgnoreStacksize();
			for (int i = 0;i < outputs.size();i++) {
				ICraftable c = outputs.get(i);
				c.setLevel(isResult() ? 0 : -1);
				addCraftableToList(c, requiredStacks);
			}
		}

		@Override
		public String toString() {
			return getInputsIgnoreStacksize().toString() + "_@_" + getOutputsIgnoreStacksize().toString();
		}
		/*public void writeToNBT(NBTTagCompound tag){
			TomsModUtils.writeBlockPosToNBT(tag, recipe.getPos2());
			tag.setInteger("dim", recipe.getDim());
			tag.setInteger("extra", recipe.getExtraData());
			tag.setInteger("slot", recipe.getSlot());
			tag.setBoolean("result", result);
		}
		public static RecipeToCraft loadFromNBT(NBTTagCompound tag){
			RecipeToCraft c = new RecipeToCraft(null);
			c.pos = TomsModUtils.readBlockPosFromNBT(tag);
			c.dim = tag.getInteger("dim");
			c.extra = tag.getInteger("extra");
			c.slot = tag.getInteger("slot");
			c.result = tag.getBoolean("result");
			return c;
		}
		private int dim, extra, slot;
		private BlockPos pos;
		public boolean load(StorageData data){
			ICraftingHandler<?> h = data.getCraftingHandler(pos, dim, extra);
			if(h != null){
				ICraftingRecipe<?> rec = h.getRecipes().stream().filter(r -> r.getSlot() == slot).findFirst().orElse(null);
				if(rec != null){
					recipe = rec;
					pos = null;
					return true;
				}
			}
			return false;
		}
		public boolean isValid(){
			return recipe != null;
		}*/
	}

	public static class CalculatedClientCrafting {
		public List<ClientCraftingStack<?>> stacks;
		public ICraftable mainStack;
		public int cpuId;
		public long operations, memory, time;
		public List<Integer> cpus;
		public boolean hasMissing;
	}

	public static class ClientCraftingStack<T extends ICraftable> {
		public T mainStack;
		public int toCraft, stored, missing;
		public boolean crafting;

		@SuppressWarnings("unchecked")
		public ClientCraftingStack<T> copy() {
			ClientCraftingStack<T> c = new ClientCraftingStack<>();
			c.toCraft = toCraft;
			c.mainStack = (T) mainStack.copy();
			c.stored = stored;
			c.missing = missing;
			c.crafting = crafting;
			return c;
		}

		@SideOnly(Side.CLIENT)
		public void draw(int posX, int posY, int mouseX, int mouseY, StorageNetworkGrid.ICraftingReportScreen screen, boolean isTooltip) {
			mainStack.drawEntry(this, posX, posY, mouseX, mouseY, screen, isTooltip);
		}
	}

	public static interface ISavedCraftingRecipe {
		boolean isStoredOnly();

		ICraftable[] getOutputs();

		ICraftable[] getInputs();

		int getTime();

		void setTime(int time);

		void setStoredOnly(boolean isStoredOnly);

		void setInputs(ICraftable[] stacks);

		void setOutputs(ICraftable[] stacks);

		void writeToNBT(NBTTagCompound tag);

		void readFromNBT(NBTTagCompound tag);
	}

	public static class SavedCraftingRecipe implements ISavedCraftingRecipe {
		private ICraftable[] inputs, outputs;
		private StorageNetworkGrid.CraftingPatternProperties p;

		// private int[] lastTime;
		// private boolean timed = false;
		@Override
		public boolean isStoredOnly() {
			return p.storedOnly;
		}

		@Override
		public ICraftable[] getOutputs() {
			return outputs;
		}

		@Override
		public ICraftable[] getInputs() {
			return inputs;
		}

		@Override
		public int getTime() {
			return p.time;
		}

		@Override
		public void setTime(int time) {
			this.p.time = time;
		}

		@Override
		public void setStoredOnly(boolean isStoredOnly) {
			p.storedOnly = isStoredOnly;
		}

		@Override
		public void setInputs(ICraftable[] stacks) {
			inputs = stacks;
		}

		@Override
		public void setOutputs(ICraftable[] stacks) {
			outputs = stacks;
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			p.writeToNBT(tag);
			// tag.setIntArray("lastTime", lastTime);
			// tag.setBoolean("timed", timed);
			NBTTagList list = new NBTTagList();
			if (inputs != null && inputs.length > 0) {
				for (int i = 0;i < inputs.length;i++) {
					ICraftable s = inputs[i];
					if (s != null) {
						NBTTagCompound t = new NBTTagCompound();
						CacheRegistry.writeToNBT(s, t);
						t.setInteger("Slot", i);
						list.appendTag(t);
					}
				}
			}
			tag.setTag("in", list);
			list = new NBTTagList();
			if (outputs != null && outputs.length > 0) {
				for (int i = 0;i < outputs.length;i++) {
					ICraftable s = outputs[i];
					if (s != null) {
						NBTTagCompound t = new NBTTagCompound();
						CacheRegistry.writeToNBT(s, t);
						t.setInteger("Slot", i);
						list.appendTag(t);
					}
				}
			}
			tag.setTag("out", list);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			// TomsModUtils.getServer().profiler.startSection("readSavedCraftingRecipe");
			p = StorageNetworkGrid.CraftingPatternProperties.loadFromNBT(tag);
			// lastTime = tag.getIntArray("lastTime");
			// timed = tag.getBoolean("timed");
			NBTTagList list = tag.getTagList("in", 10);
			// TomsModUtils.getServer().profiler.startSection("in:" +
			// list.tagCount());
			inputs = new ICraftable[25];
			outputs = new ICraftable[8];
			boolean rewrite = false;
			for (int i = 0;i < list.tagCount();i++) {
				NBTTagCompound t = list.getCompoundTagAt(i);
				int slot = t.getInteger("Slot");
				if (slot >= 0 && slot < 25) {
					if ((inputs[slot] = CacheRegistry.readFromNBT(t)) == null)
						rewrite = true;
				}
			}
			// TomsModUtils.getServer().profiler.endSection();
			list = tag.getTagList("out", 10);
			// TomsModUtils.getServer().profiler.startSection("out:" +
			// list.tagCount());
			for (int i = 0;i < list.tagCount();i++) {
				NBTTagCompound t = list.getCompoundTagAt(i);
				int slot = t.getInteger("Slot");
				if (slot >= 0 && slot < 8) {
					if ((outputs[slot] = CacheRegistry.readFromNBT(t)) == null)
						rewrite = true;
				}
			}
			if (rewrite)
				writeToNBT(tag);
			// TomsModUtils.getServer().profiler.endSection();
			// TomsModUtils.getServer().profiler.endSection();
		}

		public static SavedCraftingRecipe loadFromNBT(NBTTagCompound tag) {
			SavedCraftingRecipe r = new SavedCraftingRecipe();
			r.readFromNBT(tag);
			return r;
		}

		public <T extends ICraftable> ICraftingRecipe<T> getRecipe(final ICraftingHandler<T> handler, int slot) {
			return new ICraftingRecipe<T>() {

				@Override
				public boolean isStoredOnly() {
					return p.storedOnly;
				}

				@Override
				public int getTime() {
					// return timed ? time : TomsModUtils.average_int(lastTime);
					return p.time;
				}

				@SuppressWarnings("unchecked")
				@Override
				public List<T> getOutputs() {
					List<T> list = new ArrayList<>();
					if (outputs != null) {
						for (ICraftable a : outputs) {
							if (a != null)
								list.add((T) a.copy());
						}
					}
					return list;
				}

				@SuppressWarnings("unchecked")
				@Override
				public List<T> getInputs() {
					List<T> list = new ArrayList<>();
					if (inputs != null) {
						for (ICraftable a : inputs) {
							if (a != null)
								list.add((T) a.copy());
						}
					}
					return list;
				}

				@Override
				public boolean execute(boolean doExecute) {
					return handler.executeRecipe(this, doExecute);
				}

				@Override
				public void writeToNBT(NBTTagCompound tag) {
					SavedCraftingRecipe.this.writeToNBT(tag);
				}

				@Override
				public boolean useContainerItems() {
					return p.useContainerItems;
				}

				@Override
				public int getSlot() {
					return slot;
				}

				@Override
				public BlockPos getPos2() {
					return handler.getPos2();
				}

				@Override
				public int getDim() {
					return handler.getDim();
				}

				@Override
				public int getExtraData() {
					return handler.getExtraData();
				}

				/*@Override
				public void addTime(int time) {
					lastTime = TomsModUtils.array_intAddLimit(lastTime, time, 20);
				}*/
			};
		}

		public static SavedCraftingRecipe createFromStacks(IInventory inv, IInventory resultInventory, StorageNetworkGrid.CraftingPatternProperties properties, ICraftable.CraftableProperties[] props) {
			SavedCraftingRecipe r = new SavedCraftingRecipe();
			if (inv.getSizeInventory() > 8 && resultInventory.getSizeInventory() > 2) {
				r.inputs = new ICraftable[inv.getSizeInventory()];
				r.outputs = new ICraftable[resultInventory.getSizeInventory()];
				for (int i = 0;i < r.inputs.length;i++) {
					r.inputs[i] = deserializeItemStack(inv.getStackInSlot(i), props[i]);
				}
				for (int i = 0;i < r.outputs.length;i++) {
					r.outputs[i] = deserializeItemStack(resultInventory.getStackInSlot(i), props[inv.getSizeInventory() + i]);
				}
			}
			r.p = properties.copy();
			// r.timed = properties.timed;
			return r;
		}

		private static ICraftable deserializeItemStack(ItemStack stack, ICraftable.CraftableProperties properties) {
			return !stack.isEmpty() ? new StoredItemStack(stack, stack.getCount()).setHasProperites(properties) : null;
		}

		@SuppressWarnings({"rawtypes"})
		public boolean recipeEquals(ICraftingRecipe recipe) {
			// List<ICraftable> stackList = recipe.getInputs();
			/*List<ICraftable> list = new ArrayList<ICraftable>();
			if(inputs != null){
				for(ICraftable a : inputs){
					if(a != null)
						list.add(a.copy());
				}
			}*/
			/*if(list.size() != stackList.size())return false;
			for(int i = 0;i<list.size();i++){
				ICraftable s1 = list.get(i);
				ICraftable s2 = stackList.get(i);
				if(!s1.isEqual(s2)){
					return false;
				}
			}*/
			/*if(inputs == null)return false;
			if(inputs.length != stackList.size())return false;
			for(int i = 0;i<stackList.size();i++){
				if(!(inputs[i] == stackList.get(i) || (inputs[i] != null && inputs[i].isEqual(stackList.get(i))))){
					return false;
				}
			}*/
			NBTTagCompound tag = new NBTTagCompound();
			recipe.writeToNBT(tag);
			NBTTagCompound tag2 = new NBTTagCompound();
			writeToNBT(tag2);
			return tag.equals(tag2);
		}
	}

	public static class RecipeReturnInformation {
		public final boolean success;
		public final int time, operations, memoryUsage;

		public RecipeReturnInformation(boolean success, int time, int operations, int memoryUsage) {
			this.success = success;
			this.time = time;
			this.operations = operations;
			this.memoryUsage = memoryUsage;
		}
	}

	public static class TooComplexCraftingException extends Exception {
		private static final long serialVersionUID = -947645277184685155L;

		public TooComplexCraftingException() {
			super("tomsMod.autoCraftingError");
		}
	}

	public static class CompiledCalculatedCrafting {
		NBTTagCompound main;
		long opertaions;
		long memory;
		long time;
		short[] cpus = new short[0];
		List<NBTTagCompound> missingStacks = new ArrayList<>();
		List<NBTTagCompound> recipes = new ArrayList<>();
		List<NBTTagCompound> toPull = new ArrayList<>();

		public void sendTo(EntityPlayerMP player) {
			sendCompiledCraftingTo(this, player);
		}

		public void sendTo(EntityPlayerMP player, NBTTagCompound extra) {
			sendCompiledCraftingTo(this, player, extra);
		}
	}

	public static class ToCraftList extends LinkedList<ICraftable> {
		private static final long serialVersionUID = 4979050496757678875L;

		@Override
		public boolean add(ICraftable e) {
			addFirst(e);
			return true;
		}
	}

	public static class SecondaryOutList {
		private final Map<Long, List<ICraftable>> secondaryOutList;
		private final long id;

		public SecondaryOutList(Map<Long, List<ICraftable>> secondaryOutList, long id) {
			this.secondaryOutList = secondaryOutList;
			this.id = id;
		}

		public void add(ICraftable e) {
			addCraftableToList(e, getOrDef(secondaryOutList, id));
		}

		public long contains(ICraftable c) {
			long contain = 0;
			for (Entry<Long, List<ICraftable>> e : secondaryOutList.entrySet()) {
				if (e.getKey() >= id) {
					List<ICraftable> secondaryOutListC = e.getValue();
					for (int i = 0;i < secondaryOutListC.size();i++) {
						ICraftable s = secondaryOutListC.get(i);
						if (s.isEqual(c)) {
							contain += s.getQuantity();
						}
					}
				}
			}
			return contain;
		}
	}

	public static class CraftingCalculationThread extends Thread {
		private final Function<CraftingCalculationResult, Void> apply;
		private final StorageData data;
		private final ICraftable stackToCraft;

		public CraftingCalculationThread(StorageData data, ICraftable stackToCraft, Function<CraftingCalculationResult, Void> r) {
			super("Tom's Mod Auto-Crafting Calculation Thread");
			setDaemon(true);
			this.apply = r;
			this.data = data;
			this.stackToCraft = stackToCraft;
		}

		@Override
		public void run() {
			TMLogger.info("Calculating crafting...");
			CraftingCalculationResult r;
			try {
				r = new CraftingCalculationResult(null, data.calculateCrafting(stackToCraft));
			} catch (Throwable e) {
				r = new CraftingCalculationResult(e, null);
			}
			final CraftingCalculationResult fr = r;
			TomsModUtils.getServer().addScheduledTask(new Runnable() {

				@Override
				public void run() {
					apply.apply(fr);
				}
			});
			TMLogger.info("Crafting Calculated");
		}

	}

	public static class CraftingCalculationResult {
		public final Throwable e;
		public final CalculatedCrafting c;

		public CraftingCalculationResult(Throwable e, CalculatedCrafting c) {
			this.e = e;
			this.c = c;
		}
	}
}
