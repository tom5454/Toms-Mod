package com.tom.storage.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.tom.api.inventory.StoredItemStack;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.apis.TomsModUtils;
import com.tom.storage.block.CraftingController;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.multipart.StorageNetworkGrid.CacheRegistry;
import com.tom.storage.multipart.StorageNetworkGrid.CalculatedCrafting;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftable;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingController;
import com.tom.storage.multipart.StorageNetworkGrid.RecipeToCraft;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TileEntityCraftingController extends TileEntityGridDeviceBase<StorageNetworkGrid> implements ICraftingController {
	private CalculatedCrafting crafting = null;
	private List<ICraftable> storedStacks = new ArrayList<ICraftable>();
	private List<ICraftable> requiredStacksToPull = new ArrayList<ICraftable>();
	private List<RecipeToCraft> recipes = new ArrayList<RecipeToCraft>();
	private int totalTime = 0, totalCraftingTime = 0, cancelTimer = 0;
	private boolean isCancelled = false;
	@Override
	public boolean hasJob() {
		return crafting != null || !storedStacks.isEmpty();
	}

	@Override
	public void queueCrafing(CalculatedCrafting crafting) {
		if(!hasJob()){
			this.crafting = crafting;
			List<ICraftable> pulledStacks = crafting.pullStacks(grid);
			for(int i = 0;i<pulledStacks.size();i++){
				StorageNetworkGrid.addCraftableToList(pulledStacks.get(i), storedStacks);
			}
			totalTime = 0;
			totalCraftingTime = 0;
			isCancelled = false;
			//hasJob();
		}
	}

	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<storedStacks.size();i++){
			NBTTagCompound t = new NBTTagCompound();
			ICraftable stack = storedStacks.get(i);
			//t.setInteger("ItemCount", stack.stackSize);
			//stack.writeToNBT(t);
			CacheRegistry.writeToNBT(stack, t);
			list.appendTag(t);
		}
		compound.setTag("itemsStored", list);
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList list = compound.getTagList("itemsStored", 10);
		storedStacks.clear();
		for(int i = 0;i<list.tagCount();i++){
			NBTTagCompound t = list.getCompoundTagAt(i);
			/*ItemStack stack = ItemStack.loadItemStackFromNBT(t);
			if(stack != null){
				stack.stackSize = t.getInteger("ItemCount");
				storedStacks.add(stack);
			}*/
			storedStacks.add(CacheRegistry.readFromNBT(t));
		}
	}

	@Override
	public int getMaxOperations() {
		return 100;
	}

	@Override
	public int getMaxMemory() {
		return -1;
	}

	@Override
	public ItemStack onStackInput(ItemStack stack) {
		//if(requiredStacksToPull.contains(new ItemStackComparator(stack))){
		List<ICraftable> toRemove = new ArrayList<ICraftable>();
		for(int i = 0;i<requiredStacksToPull.size();i++){
			ICraftable craftable = requiredStacksToPull.get(i);
			if(craftable instanceof StoredItemStack){
				ItemStack s = ((StoredItemStack)craftable).stack.copy();
				s.stackSize = ((StoredItemStack)craftable).itemCount;
				if(TomsModUtils.areItemStacksEqualOreDict(stack, s, true, true, false, true)){
					int min = Math.min(s.stackSize, stack.stackSize);
					s.stackSize -= min;
					((StoredItemStack)craftable).itemCount = s.stackSize;
					ItemStack stackToAdd = stack.splitStack(min);
					StorageNetworkGrid.addCraftableToList(new StoredItemStack(stackToAdd, stackToAdd.stackSize), storedStacks);
					if(s.stackSize < 1){
						//i = Math.max(i - 1, 0);
						//requiredStacksToPull.remove(i);
						toRemove.add(requiredStacksToPull.get(i));
					}
					if(stack.stackSize < 1){
						stack = null;
						break;
					}
				}
			}
		}
		for(int i = 0;i<toRemove.size();i++){
			boolean success = requiredStacksToPull.remove(toRemove.get(i));
			if(!success)System.err.println("Remove failed");
		}
		//}
		return stack;
	}
	public void sendCraftingDoneMessage(){
		if(crafting.queuedBy != null){
			EntityPlayer player = worldObj.getPlayerEntityByName(crafting.queuedBy);
			if(player != null){
				int secTime = MathHelper.ceiling_double_int(totalTime / 20D);
				int secTimeC = secTime - MathHelper.ceiling_double_int(crafting.time / 20D);
				ITextComponent c = crafting.mainStack.serializeTextComponent(TextFormatting.GREEN);
				if(secTimeC > 40)TomsModUtils.sendChatTranslate(player, new Style().setColor(TextFormatting.GREEN),  "tomsMod.chat.craftingDoneDelay", c, secTime / 60, secTime % 60, secTimeC / 60, secTimeC % 60);
				else TomsModUtils.sendChatTranslate(player, new Style().setColor(TextFormatting.GREEN),  "tomsMod.chat.craftingDone", c, secTime / 60, secTime % 60);
			}
		}
	}
	public ITextComponent[] serializeMessage(){
		//MachineCraftingHandler.addCrusherRecipe(new ItemStack(Items.COAL), TMResource.COAL.getStackNormal(Type.DUST));
		List<ITextComponent> list = new ArrayList<ITextComponent>();
		int id = grid.getData().getCpuID(this);
		ITextComponent idF = new TextComponentTranslation("tomsMod.storage.cpuId", id);
		if(crafting != null){
			int secTime = MathHelper.ceiling_double_int(totalTime / 20D);
			int secTimeR = MathHelper.ceiling_double_int((crafting.time - totalCraftingTime) / 20D);
			int secTimeC = secTime - secTimeR;
			ITextComponent c = crafting.mainStack.serializeTextComponent(TextFormatting.YELLOW);
			list.add(new TextComponentTranslation("tomsMod.chat.currentlyCrafting", idF, c, crafting.mainStack.getQuantity()));
			if(!requiredStacksToPull.isEmpty()){
				list.add(new TextComponentTranslation("tomsMod.chat.waitingFor"));
				for(int i = 0;i<requiredStacksToPull.size() && i < 4;i++){
					ICraftable s = requiredStacksToPull.get(i);
					if(s != null && s.hasQuantity()){
						ITextComponent sT = s.serializeTextComponent(TextFormatting.WHITE);
						list.add(new TextComponentTranslation("tomsMod.chat.tabulator", new TextComponentTranslation("tomsMod.chat.stackList" + (i == 3 ? "End" : ""), sT)));
					}
				}
			}
			if(!storedStacks.isEmpty()){
				list.add(new TextComponentTranslation("tomsMod.chat.stored"));
				for(int i = 0;i<storedStacks.size() && i < 8;i++){
					ICraftable s = storedStacks.get(i);
					if(s != null && s.hasQuantity()){
						ITextComponent sT = s.serializeTextComponent(TextFormatting.WHITE);
						list.add(new TextComponentTranslation("tomsMod.chat.tabulator", new TextComponentTranslation("tomsMod.chat.stackList" + (i == 7 ? "End" : ""), sT)));
					}
				}
			}
			//list.add(new TextComponentString(.getInputsIgnoreStacksize().toString()));
			if(!crafting.recipesToCraft.isEmpty()){
				List<ICraftable> next = crafting.recipesToCraft.peek().getInputsIgnoreStacksize();
				list.add(new TextComponentString("Next:"));
				for(int i = 0;i<next.size() && i < 6;i++){
					ICraftable s = next.get(i);
					if(s != null && s.hasQuantity()){
						ITextComponent sT = s.serializeTextComponent(TextFormatting.WHITE);
						list.add(new TextComponentTranslation("tomsMod.chat.tabulator", new TextComponentTranslation("tomsMod.chat.stackList" + (i == 5 ? "End" : ""), sT)));
					}
				}
			}
			if(secTimeC > 40)
				list.add(new TextComponentTranslation("tomsMod.chat.craftingListEndDelay", secTime / 60, secTime % 60, secTimeR / 60, secTimeR % 60, secTimeC / 60, secTimeC % 60));
			else
				list.add(new TextComponentTranslation("tomsMod.chat.craftingListEnd", secTime / 60, secTime % 60, secTimeR / 60, secTimeR % 60));
			list.add(new TextComponentTranslation("tomsMod.chat.help.shiftClickToCancel"));
		}
		else{
			list.add(new TextComponentTranslation("tomsMod.chat.noJobs", idF));
			if(!storedStacks.isEmpty()){
				list.add(new TextComponentTranslation("tomsMod.chat.stored"));
				for(int i = 0;i<storedStacks.size() && i < 6;i++){
					ICraftable s = storedStacks.get(i);
					if(s != null && s.hasQuantity()){
						ITextComponent sT = s.serializeTextComponent(TextFormatting.WHITE);
						list.add(new TextComponentTranslation("tomsMod.chat.tabulator", new TextComponentTranslation("tomsMod.chat.stackList" + (i == 5 ? "End" : ""), sT, s.getQuantity())));
					}
				}
			}
			//if(!requiredStacksToPull.isEmpty()) list.add(new TextComponentTranslation("tomsMod.chat.tabulator", new TextComponentString("Stack List stuck.")));
		}
		return list.toArray(new ITextComponent[]{});
	}
	public ITextComponent[] cancelCrafting(){
		List<ITextComponent> list = new ArrayList<ITextComponent>();
		int id = grid.getData().getCpuID(this);
		ITextComponent idF = new TextComponentTranslation("tomsMod.storage.cpuId", id);
		if(crafting != null){
			int secTime = MathHelper.ceiling_double_int(totalTime / 20D);
			int secTimeR = MathHelper.ceiling_double_int((crafting.time - totalCraftingTime) / 20D);
			ITextComponent c = crafting.mainStack.serializeTextComponent(TextFormatting.YELLOW);
			if(cancelTimer > 0){
				list.add(new TextComponentTranslation("tomsMod.chat.jobCancelled", idF, c, secTime / 60, secTime % 60, secTimeR / 60, secTimeR % 60));
				isCancelled = true;
				cancelTimer = 0;
			}else{
				list.add(new TextComponentTranslation("tomsMod.chat.jobCancelConfirm", idF, c, secTime / 60, secTime % 60, secTimeR / 60, secTimeR % 60));
				list.add(new TextComponentTranslation("tomsMod.chat.help.shiftClickToCancel"));
				cancelTimer = 100;
			}
		}
		else list.add(new TextComponentTranslation("tomsMod.chat.noJobs", idF));
		return list.toArray(new ITextComponent[]{});
	}
	public List<ICraftable> getStoredStacks(){
		return storedStacks;
	}

	@Override
	public void onGridReload() {
		grid.getData().addCraftingController(this);
	}

	@Override
	public void onGridPostReload() {

	}
	@Override
	public void updateEntity(IBlockState state) {
		if(!worldObj.isRemote){
			if(cancelTimer > 0)cancelTimer--;
			grid.getData().addCraftingController(this);
			handleCrafting();
			TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, CraftingController.ACTIVE, grid.isPowered());
		}
	}
	private void handleCrafting(){
		if(crafting != null){
			if(isCancelled){
				recipes.clear();
				crafting = null;
				totalTime = 0;
				isCancelled = false;
				requiredStacksToPull.clear();
				if(!storedStacks.isEmpty()){
					List<ICraftable> toRemove = new ArrayList<ICraftable>();
					for(int i = 0;i<storedStacks.size();i++){
						if(storedStacks.get(i) != null && storedStacks.get(i).hasQuantity()){
							ICraftable c = storedStacks.get(i).pushToGrid(grid);
							if(c != null){
								storedStacks.set(i, c);
							}else{
								toRemove.add(storedStacks.get(i));
							}
						}else{
							toRemove.add(storedStacks.get(i));
						}
					}
					for(int i = 0;i<toRemove.size();i++){
						storedStacks.remove(toRemove.get(i));
					}
				}
				return;
			}
			if(crafting.recipesToCraft.isEmpty() && requiredStacksToPull.isEmpty() && recipes.isEmpty()){
				sendCraftingDoneMessage();
				crafting = null;
				totalTime = 0;
				recipes.clear();
				if(!storedStacks.isEmpty()){
					List<ICraftable> toRemove = new ArrayList<ICraftable>();
					for(int i = 0;i<storedStacks.size();i++){
						if(storedStacks.get(i) != null && storedStacks.get(i).hasQuantity()){
							ICraftable c = storedStacks.get(i).pushToGrid(grid);
							if(c != null){
								storedStacks.set(i, c);
							}else{
								toRemove.add(storedStacks.get(i));
							}
						}else{
							toRemove.add(storedStacks.get(i));
						}
					}
					for(int i = 0;i<toRemove.size();i++){
						storedStacks.remove(toRemove.get(i));
					}
				}
				return;
			}
			int max = getMaxOperations() + 1;
			while(!crafting.recipesToCraft.isEmpty() && crafting.recipesToCraft.peek().execute(false, storedStacks) && recipes.size() < max){
				recipes.add(crafting.recipesToCraft.pop());
			}
			if(!recipes.isEmpty()){
				List<RecipeToCraft> requiredToRemove = new ArrayList<RecipeToCraft>();
				for(int i = 0;i<recipes.size();i++){
					if(recipes.get(i).execute(true, storedStacks)){
						recipes.get(i).addRequiredStacks(requiredStacksToPull);
						totalCraftingTime += recipes.get(i).getTime();
						requiredToRemove.add(recipes.get(i));
					}
				}
				for(int j = 0;j<requiredToRemove.size();j++){
					recipes.remove(requiredToRemove.get(j));
				}
			}
			totalTime++;
		}else{
			if(!storedStacks.isEmpty()){
				List<ICraftable> toRemove = new ArrayList<ICraftable>();
				for(int i = 0;i<storedStacks.size();i++){
					if(storedStacks.get(i) != null && storedStacks.get(i).hasQuantity()){
						ICraftable c = storedStacks.get(i).pushToGrid(grid);
						if(c != null){
							storedStacks.set(i, c);
						}else{
							toRemove.add(storedStacks.get(i));
						}
					}else{
						toRemove.add(storedStacks.get(i));
					}
				}
				for(int i = 0;i<toRemove.size();i++){
					storedStacks.remove(toRemove.get(i));
				}
			}
		}
	}
	//	@Override
	//	public void updateEntity() {
	//		if(!worldObj.isRemote){
	//			if(cancelTimer > 0)cancelTimer--;
	//			grid.getData().addCraftingController(this);
	//			if(isCanceled){
	//				recipes.clear();
	//				crafting = null;
	//				totalTime = 0;
	//				if(!storedStacks.isEmpty()){
	//					for(int i = 0;i<storedStacks.size();i++){
	//						ItemStack retStack = grid.pushStack(storedStacks.get(i));
	//						if(retStack == null || retStack.stackSize < 1)storedStacks.remove(i);
	//					}
	//				}
	//			}
	//			if(recipes.isEmpty()){
	//				if(crafting != null){
	//					if(!crafting.recipesToCraft.isEmpty()){
	//						recipes.add(crafting.recipesToCraft.pop());
	//						executed = false;
	//						requested = false;
	//						//timeChecked = false;
	//					}else{
	//						crafting = null;
	//						sendCraftingDoneMessage();
	//						totalTime = 0;
	//					}
	//				}else{
	//					if(!storedStacks.isEmpty()){
	//						for(int i = 0;i<storedStacks.size();i++){
	//							ItemStack retStack = grid.pushStack(storedStacks.get(i));
	//							if(retStack == null || retStack.stackSize < 1)storedStacks.remove(i);
	//						}
	//					}
	//				}
	//			}else{
	//				if(!executed){
	//					List<ItemStack> inputList = recipe.getInputsIgnoreStacksize();
	//					boolean found = true;
	//					for(int i = 0;i<inputList.size();i++){
	//						ItemStack inputStack = inputList.get(i);
	//						//if(!storedStacks.contains(new ItemStackComparator(inputStack))){
	//						boolean stackFound = false;
	//						for(int j = 0;j<storedStacks.size();j++){
	//							ItemStack storedStack = storedStacks.get(j);
	//							if(TomsModUtils.areItemStacksEqualOreDict(inputStack, storedStack, true, true, false, true)){
	//								stackFound = true;
	//								if(storedStack.stackSize < inputStack.stackSize){
	//									if(!requested){
	//										ItemStack s = inputStack.copy();
	//										s.stackSize = inputStack.stackSize - storedStack.stackSize;
	//										if(s.stackSize > 0)StorageNetworkGrid.addStackToListIgnoreStackSize(s, requiredStacksToPull);
	//									}
	//									found = false;
	//								}
	//								/*if(grid.getInventory().getStacks().contains(new StoredItemStackComparator(new StoredItemStack(inputStack, inputStack.stackSize)))){
	//								ItemStack pulledStack = grid.pullStack(inputStack);
	//								if(pulledStack != null){
	//									storedStacks.add(pulledStack);
	//									found = true;
	//								}else{
	//									found = false;
	//									break;
	//								}
	//							}else{
	//								found = false;
	//								break;
	//							}*/
	//							}
	//						}
	//						if(!stackFound){
	//							if(!requested)StorageNetworkGrid.addStackToListIgnoreStackSize(inputStack, requiredStacksToPull);
	//							found = false;
	//						}
	//					}
	//					requested = true;
	//					if(found){
	//						executed = recipe.execute();
	//						inputList = recipe.getInputs();
	//						for(int i = 0;i<inputList.size();i++){
	//							ItemStack inputStack = inputList.get(i);
	//							for(int j = 0;j<storedStacks.size();j++){
	//								ItemStack storedStack = storedStacks.get(j);
	//								if(TomsModUtils.areItemStacksEqualOreDict(inputStack, storedStack, true, true, false, true)){
	//									storedStack.stackSize -= inputStack.stackSize;
	//									if(storedStack.stackSize < 1)storedStacks.remove(storedStack);
	//									break;
	//								}
	//							}
	//						}
	//						List<ItemStack> outputStacks = recipe.getOutputs();
	//						for(int i = 0;i<outputStacks.size();i++){
	//							StorageNetworkGrid.addStackToListIgnoreStackSize(outputStacks.get(i).copy(), requiredStacksToPull);
	//						}
	//					}
	//				}else{
	//					boolean found = true;
	//					List<ItemStack> outputList = recipe.getOutputsIgnoreStacksize();
	//					/*for(int i = 0;i<outputList.size();i++){
	//						ItemStack outputStack = outputList.get(i);
	//						if(!storedStacks.contains(new ItemStackComparator(outputStack))){
	//							/*if(grid.getInventory().getStacks().contains(new StoredItemStackComparator(new StoredItemStack(inputStack, inputStack.stackSize)))){
	//								ItemStack pulledStack = grid.pullStack(inputStack);
	//								if(pulledStack != null){
	//									storedStacks.add(pulledStack);
	//									found = true;
	//								}else{
	//									found = false;
	//									break;
	//								}
	//							}else{
	//								found = false;
	//								break;
	//							}*/
	//					//requiredStacksToPull.add(inputStack);
	//					/*found = false;
	//						}
	//					}*/
	//					for(int i = 0;i<outputList.size();i++){
	//						ItemStack outputStack = outputList.get(i);
	//						//if(!storedStacks.contains(new ItemStackComparator(inputStack))){
	//						boolean stackFound = false;
	//						for(int j = 0;j<storedStacks.size();j++){
	//							ItemStack storedStack = storedStacks.get(j);
	//							if(TomsModUtils.areItemStacksEqualOreDict(outputStack, storedStack, true, true, false, true)){
	//								stackFound = true;
	//								if(storedStack.stackSize < outputStack.stackSize){
	//									ItemStack s = outputStack.copy();
	//									s.stackSize = outputStack.stackSize - storedStack.stackSize;
	//									if(!requested && s.stackSize > 0)StorageNetworkGrid.addStackToListIgnoreStackSize(s, requiredStacksToPull);
	//									found = false;
	//								}
	//								/*if(grid.getInventory().getStacks().contains(new StoredItemStackComparator(new StoredItemStack(inputStack, inputStack.stackSize)))){
	//								ItemStack pulledStack = grid.pullStack(inputStack);
	//								if(pulledStack != null){
	//									storedStacks.add(pulledStack);
	//									found = true;
	//								}else{
	//									found = false;
	//									break;
	//								}
	//							}else{
	//								found = false;
	//								break;
	//							}*/
	//							}
	//						}
	//						if(!stackFound){
	//							if(!requested)StorageNetworkGrid.addStackToListIgnoreStackSize(outputStack, requiredStacksToPull);
	//							found = false;
	//						}
	//					}
	//					if(found){
	//						totalCraftingTime += recipe.getTime();
	//						recipes.clear();
	//						if(crafting != null && !crafting.recipesToCraft.isEmpty()){
	//							recipes.add(crafting.recipesToCraft.pop());
	//							executed = false;
	//							requested = false;
	//							//timeChecked = false;
	//						}else{
	//							sendCraftingDoneMessage();
	//							totalTime = 0;
	//							crafting = null;
	//						}
	//					}
	//				}
	//				totalTime++;
	//			}
	//		}
	//	}
}
