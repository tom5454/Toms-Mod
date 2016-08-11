package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.event.ItemAdvCraftedEvent;
import com.tom.api.research.IResearch;
import com.tom.api.research.IScanningInformation;
import com.tom.api.research.IScanningInformation.ScanningInformation;
import com.tom.api.tileentity.IGuiTile;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.research.handler.ResearchHandler;
import com.tom.core.research.handler.ResearchHandler.ResearchInformation;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.recipes.handler.AdvancedCraftingHandler.ReturnData;

public class TileEntityResearchTable extends TileEntityTomsMod implements
ISidedInventory, IGuiTile {
	//public boolean isMaster = false;
	/**
	 * 0: Big Note Book, 1:Note Book, 2:Ink, 3-6: Research Components, 7-15:Crafting in, 16:Crafting out,
	 * 17:Paper, 18:Crafting Extra
	 * */
	private ItemStack[] stack = new ItemStack[this.getStackLimit()];
	private static final int[] slotsBottom = new int[]{16,18};
	private static final int[] slotsSide = new int[]{2,3,4,5,6,17,19};
	private int inkLevel = 0;
	private int craftingTime = 0;
	private int totalCrafingTime = 0;
	private int researchProgress = 0;
	private int totalResearchProgress = 0;
	public IResearch currentResearch = null;
	private ItemStack craftingStackOut = null;
	private ItemStack craftingStackExtra = null;
	public int craftingError = 0;
	private int craftingErrorShowTimer = 0;
	//private boolean startResearching = false;
	//public boolean formed = false;
	private int getStackLimit(){
		return 19;
	}
	@Override
	public int getSizeInventory() {
		return 18;
	}

	@Override
	public ItemStack getStackInSlot(int index) {

		return stack[index];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		//if(this.startResearching) return null;
		if (this.stack[slot] != null) {
			ItemStack itemstack;
			if (this.stack[slot].stackSize <= amount) {
				itemstack = this.stack[slot];
				this.stack[slot] = null;
				return itemstack;
			} else {
				itemstack = this.stack[slot].splitStack(amount);

				if (this.stack[slot].stackSize == 0) {
					this.stack[slot] = null;
				}
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int arg0) {
		//if(this.startResearching) return null;
		ItemStack is = stack[arg0];
		stack[arg0] = null;
		return is;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		//if(this.startResearching) return;
		this.stack[index] = stack;
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
		return (index > 2 && index < 7) || (index == 2 && stack != null && stack.getItem() == Items.DYE && stack.getItemDamage() == 0) || (index == 17 && stack != null && stack.getItem() == Items.PAPER);
	}

	@Override
	public int getField(int id) {
		return id == 0 ? this.inkLevel : (id == 1 ? this.craftingTime : (id == 2 ? this.totalCrafingTime :
			(id == 3 ? this.researchProgress : (id == 4 ? this.totalResearchProgress : 0))));
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0){
			this.inkLevel = value;
		}else if(id == 1){
			this.craftingTime = value;
		}else if(id == 2){
			this.totalCrafingTime = value;
		}else if(id == 3){
			this.researchProgress = value;
		}else if(id == 4){
			this.totalResearchProgress = value;
		}
	}

	@Override
	public int getFieldCount() {
		return 5;
	}

	@Override
	public void clear() {
		//if(this.startResearching) return;
		this.stack = new ItemStack[this.getStackLimit()];
	}

	@Override
	public String getName() {
		return "Research Table";
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
	public int[] getSlotsForFace(EnumFacing side) {
		//if(this.startResearching) return new int[]{};
		return side == EnumFacing.DOWN ? slotsBottom : slotsSide;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack,
			EnumFacing direction) {
		return direction != EnumFacing.DOWN ? (index > 2 && index < 7) || (index == 2 && stack != null && stack.getItem() == Items.DYE && stack.getItemDamage() == 0) || (index == 17 && stack != null && stack.getItem() == Items.PAPER) : false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		//if(this.startResearching) return false;
		return index == 16 || index == 18;
	}
	@Override
	public void updateEntity() {
		if(worldObj.isRemote)return;
		if(this.craftingErrorShowTimer > 0)if(this.craftingErrorShowTimer-- == 1 && (craftingError == 2 || craftingError == 3 || craftingError == 4))craftingError = 0;
		if(stack[0] != null && stack[0].getItem() == CoreInit.bigNoteBook &&
				stack[0].getTagCompound() != null && stack[0].getTagCompound().hasKey("owner")){
			if(this.totalResearchProgress > 0 && this.inkLevel > 0){
				if(this.researchProgress == this.totalResearchProgress){
					this.totalResearchProgress = 0;
					this.researchProgress = 0;
					ResearchHandler h = getResearchHandler(stack[0]);
					if(h != null){
						h.markResearchComplete(currentResearch);
						this.currentResearch = null;
					}
				}else this.researchProgress++;
				if(this.researchProgress % 10 == 0)this.inkLevel--;
			}
			if(this.totalCrafingTime > 0){
				if(this.totalCrafingTime == this.craftingTime){
					if(this.craft()){
						this.craftingTime = 0;
						this.totalCrafingTime = 0;
						this.craftingError = 0;
					}else{
						this.craftingError = 1;
					}
				}else this.craftingTime++;
			}
			/*if(this.startResearching){

			}*/
		}else{
			this.currentResearch = null;
		}
		if(this.inkLevel < 1 && this.stack[2] != null && this.stack[2].getItem() == Items.DYE && this.stack[2].getItemDamage() == 0){
			this.inkLevel += 100;
			this.stack[2].splitStack(1);
			if(stack[2].stackSize == 0){
				stack[2] = null;
			}
		}
		//this.totalCrafingTime = 200;
		//this.totalResearchProgress = 500;
	}
	private boolean craft() {
		if(stack[16] == null){
			if(this.craftingStackExtra == null){
				stack[16] = this.craftingStackOut;
				this.craftingStackOut = null;
				return true;
			}else if(stack[18] == null){
				stack[16] = this.craftingStackOut;
				stack[18] = this.craftingStackExtra;
				this.craftingStackExtra = null;
				this.craftingStackOut = null;
				return true;
			}else{
				if(stack[18].isItemEqual(this.craftingStackExtra) && ItemStack.areItemStackTagsEqual(craftingStackExtra, stack[18]) && stack[18].stackSize + this.craftingStackExtra.stackSize <= Math.min(getInventoryStackLimit(), this.craftingStackExtra.getMaxStackSize())){
					stack[18].stackSize += this.craftingStackExtra.stackSize;
					this.craftingStackExtra = null;
					stack[16] = this.craftingStackOut;
					this.craftingStackOut = null;
					return true;
				}
			}
		}else{
			if(stack[16].isItemEqual(this.craftingStackOut) && ItemStack.areItemStackTagsEqual(craftingStackOut, stack[16]) && stack[16].stackSize + this.craftingStackOut.stackSize <= Math.min(getInventoryStackLimit(), this.craftingStackOut.getMaxStackSize())){
				if(this.craftingStackExtra == null){
					stack[16].stackSize += this.craftingStackOut.stackSize;
					this.craftingStackOut = null;
					return true;
				}else if(stack[18] == null){
					stack[16].stackSize += this.craftingStackOut.stackSize;
					stack[18] = this.craftingStackExtra;
					this.craftingStackExtra = null;
					this.craftingStackOut = null;
					return true;
				}else{
					if(stack[18].isItemEqual(this.craftingStackExtra) && ItemStack.areItemStackTagsEqual(craftingStackExtra, stack[18]) && stack[18].stackSize + this.craftingStackExtra.stackSize <= Math.min(getInventoryStackLimit(), this.craftingStackExtra.getMaxStackSize())){
						stack[18].stackSize += this.craftingStackExtra.stackSize;
						this.craftingStackExtra = null;
						stack[16].stackSize += this.craftingStackOut.stackSize;
						this.craftingStackOut = null;
						return true;
					}
				}
			}
		}
		return false;
	}
	private void craftStart() {
		for(int i = 7;i<16;i++){
			this.decrStackSize(i, 1);
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList tagList = tag.getTagList("Items", 10);
		this.stack = new ItemStack[this.getStackLimit()];
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tabCompound1 = tagList.getCompoundTagAt(i);
			byte byte0 = tabCompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.stack.length) {
				this.stack[byte0] = ItemStack.loadItemStackFromNBT(tabCompound1);
			}
		}
		this.inkLevel = tag.getInteger("ink");
		this.craftingTime = tag.getInteger("crafingTime");
		this.totalCrafingTime = tag.getInteger("totalCrafingTime");
		this.researchProgress = tag.getInteger("researchProgress");
		this.totalResearchProgress = tag.getInteger("totalResearchProgress");
		this.currentResearch = ResearchHandler.getResearchByID(tag.getInteger("currentResearch"));
		NBTTagCompound tagC = tag.getCompoundTag("crafting");
		this.craftingStackOut = ItemStack.loadItemStackFromNBT(tagC.getCompoundTag("out"));
		this.craftingStackExtra = ItemStack.loadItemStackFromNBT(tagC.getCompoundTag("extra"));
		//this.formed = tag.getBoolean("formed");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList tagList = new NBTTagList();
		for (int i = 0; i < this.stack.length; i++) {
			if (this.stack[i] != null) {
				NBTTagCompound tagCompound1 = new NBTTagCompound();
				tagCompound1.setByte("Slot", (byte) i);
				this.stack[i].writeToNBT(tagCompound1);
				tagList.appendTag(tagCompound1);
			}
		}
		tag.setTag("Items", tagList);
		tag.setInteger("ink", this.inkLevel);
		tag.setInteger("crafingTime", this.craftingTime);
		tag.setInteger("totalCrafingTime", this.totalCrafingTime);
		tag.setInteger("researchProgress", this.researchProgress);
		tag.setInteger("totalResearchProgress", this.totalResearchProgress);
		tag.setInteger("currentResearch", ResearchHandler.getId(this.currentResearch));
		NBTTagCompound tagC = new NBTTagCompound();
		tagC.setTag("out", this.craftingStackOut != null ? this.craftingStackOut.writeToNBT(new NBTTagCompound()) : new NBTTagCompound());
		tagC.setTag("extra", this.craftingStackExtra != null ? this.craftingStackExtra.writeToNBT(new NBTTagCompound()) : new NBTTagCompound());
		tag.setTag("crafting", tagC);
		//tag.setBoolean("formed", this.formed);
		return tag;
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if(stack[0] != null && stack[0].getItem() == CoreInit.bigNoteBook &&
				stack[0].getTagCompound() != null && stack[0].getTagCompound().hasKey("owner")){
			if(id == 0){
				if(stack[1] != null && stack[1].getItem() == CoreInit.noteBook){
					if(stack[1].getTagCompound() == null)stack[1].setTagCompound(new NBTTagCompound());
					NBTTagList sList = stack[1].getTagCompound().hasKey("data", 9) ?
							stack[1].getTagCompound().getTagList("data", 10) : new NBTTagList();
							List<IScanningInformation> list = new ArrayList<IScanningInformation>();
							for(int i = 0;i<sList.tagCount();i++){
								list.add(ScanningInformation.fromNBT(sList.getCompoundTagAt(i)));
							}
							ResearchHandler h = getResearchHandler(stack[0]);
							if(h != null){
								int c = h.addScanningInformation(list, inkLevel);
								if(c > 0){
									inkLevel -= c;
								}
							}
				}
			}else if(id == 5){
				this.currentResearch = ResearchHandler.getResearchByID(extra);
			}else if(id == 2){
				ResearchHandler h = getResearchHandler(stack[0]);
				NBTTagCompound tag = new NBTTagCompound();
				h.writeToNBT(tag);
				NetworkHandler.sendTo(new MessageNBT(tag), (EntityPlayerMP) player);
			}else if(id == 1){
				//System.out.println(this.currentResearch);
				if(this.currentResearch != null){
					if(stack[17] != null && stack[17].getItem() == Items.PAPER){
						boolean flag = true;
						List<ItemStack> stackList = this.currentResearch.getResearchRequirements();
						//this.drawHoveringText(is.getTooltip(mc.thePlayer,this.mc.gameSettings.advancedItemTooltips), mX, my);
						//this.renderToolTip(is, mouseX, mouseY);
						if(stackList != null){
							//stackList = new ArrayList<ItemStack>(stackList);
							//ItemStack[] stackA = this.getStacks();
							List<ItemStack> inStacks = new ArrayList<ItemStack>();
							for(int i = 3;i<7;i++){
								ItemStack stack = this.stack[i];
								if(stack != null){
									inStacks.add(stack.copy());
								}
							}
							for(int i = 0;i<stackList.size();i++){
								ItemStack stack = stackList.get(i);
								if(stack != null){
									boolean flag1 = true;
									for(ItemStack inStack : inStacks){
										boolean equals = inStack.stackSize >= stack.stackSize && TomsModUtils.areItemStacksEqualOreDict(stack, inStack, true, false, false, true);
										if(equals){
											flag1 = false;
											break;
										}
									}
									if(flag1){
										flag = false;
									}
									if(i == 3)break;
								}
							}
						}
						if(flag){
							//stackList = this.currentResearch.getResearchRequirements();
							if(stackList != null){
								//stackList = new ArrayList<ItemStack>(stackList);
								for(int i = 3;i<7;i++){
									ItemStack stack = this.stack[i];
									for(int j = 0;j<stackList.size();j++){
										ItemStack listStack = stackList.get(j);
										if(stack != null){
											if(listStack != null){
												boolean equals = listStack.stackSize <= stack.stackSize && TomsModUtils.areItemStacksEqualOreDict(stack, listStack, true, false, false, true);
												if(equals){
													this.decrStackSize(i, listStack.stackSize);
													stackList.remove(listStack);
												}
											}else{
												return;
											}
										}
									}
								}
							}
							this.totalResearchProgress = this.currentResearch.getResearchTime();
							this.decrStackSize(17, 1);
						}
					}
				}
			}else if(id == 3){
				if(this.totalCrafingTime < 1 && this.hasItemsInCrafting()){
					ResearchHandler h = getResearchHandler(stack[0]);
					if(h != null){
						ReturnData data = AdvancedCraftingHandler.craft(new ItemStack[]{stack[7],stack[8],
								stack[9],stack[10],stack[11],stack[12],stack[13],stack[14],stack[15]},
								h.getResearchesCompleted(), CraftingLevel.BASIC, worldObj);
						if(data != null){
							if(data.hasAllResearches()){
								if(data.isRightLevel()){
									ItemAdvCraftedEvent.EventResult result = ItemAdvCraftedEvent.fire(h.name, stack, 7, data.getReturnStack(), data.getExtraStack(), data.getTime());
									if(result.canCraft){
										this.craftingStackOut = result.mainStack;
										this.craftingStackExtra = result.secondStack;
										this.totalCrafingTime = result.time;
										this.craftStart();
									}else{
										craftingError = 4;
										craftingErrorShowTimer = 50;
										if(result.errorMessage != null){
											TomsModUtils.sendChatTranslate(player, TextFormatting.RED, "tomsMod.craftingFailedError", new TextComponentTranslation("tile.resTable.name"), result.errorMessage);
										}
									}
								}else{
									craftingError = 3;
									craftingErrorShowTimer = 50;
								}
							}else{
								craftingError = 2;
								craftingErrorShowTimer = 50;
							}
						}
					}
				}
			}
		}
	}
	public static ResearchHandler getResearchHandler(ItemStack bookStack){
		NBTTagCompound bookTag = bookStack.getTagCompound();
		if(bookTag != null && bookTag.hasKey("owner")){
			return ResearchHandler.getHandlerFromName(bookTag.getString("owner"));
		}
		return null;
	}
	/**
	 * 0: Big Note Book, 1:Note Book, 2:Ink, 3-6: Research Components, 7-15:Crafing in, 16:Crafting out,
	 * 17:Paper
	 * */
	public ItemStack[] getStacks(){
		return this.stack;
	}
	public List<ResearchInformation> getAResearches(){
		ResearchHandler h = getResearchHandler(stack[0]);
		if(h != null){
			return h.getAvailableResearches();
		}
		return new ArrayList<ResearchInformation>();
	}
	public boolean hasItemsInCrafting(){
		return stack[7] != null || stack[8] != null || stack[9] != null || stack[10] != null ||
				stack[11] != null || stack[12] != null || stack[13] != null || stack[14] != null ||
				stack[15] != null;
	}
}
