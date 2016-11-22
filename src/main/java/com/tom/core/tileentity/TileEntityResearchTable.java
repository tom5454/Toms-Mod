package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.event.ItemAdvCraftedEvent;
import com.tom.api.research.IScanningInformation;
import com.tom.api.research.IScanningInformation.ScanningInformation;
import com.tom.api.research.Research;
import com.tom.api.research.ResearchComplexity;
import com.tom.api.tileentity.IGuiTile;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.research.ResearchHandler;
import com.tom.core.research.ResearchHandler.ResearchInformation;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.recipes.OreDict;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.recipes.handler.AdvancedCraftingHandler.ReturnData;

import com.tom.core.block.ResearchTable;

public class TileEntityResearchTable extends TileEntityTomsMod implements
ISidedInventory, IGuiTile, ITileFluidHandler, IEnergyReceiver {
	/**
	 * 0: Big Note Book, 1:Note Book, 2:Ink, 3-6: Research Components, 7-15:Crafting in, 16:Crafting out,
	 * 17:Paper, 18:Crafting Extra
	 * */
	private ItemStack[] stack = new ItemStack[getSizeInventory()];
	private static final int[] slotsBottom = new int[]{16,18};
	private static final int[] slotsSide = new int[]{2,3,4,5,6,17};
	private int inkLevel = 0;
	private int craftingTime = 0;
	private int totalCrafingTime = 0;
	private int researchProgress = 0;
	private int totalResearchProgress = 0;
	public Research currentResearch = null;
	private ItemStack craftingStackOut = null;
	private ItemStack craftingStackExtra = null;
	public int craftingError = 0;
	private int craftingErrorShowTimer = 0;
	public boolean completed = false;
	private ResearchTableType type = ResearchTableType.WOODEN;
	public IBlockState state;
	private EnergyStorage energy = new EnergyStorage(10000);
	private FluidTank steam = new FluidTank(2000);
	public boolean craftAll = false;
	@Override
	public int getSizeInventory() {
		return 19;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		TileEntityResearchTable te = getMaster();
		return te != null ? te.stack[index] : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		TileEntityResearchTable te = getMaster();
		if(te == null)return null;
		if (te.stack[slot] != null) {
			ItemStack itemstack;
			if (te.stack[slot].stackSize <= amount) {
				itemstack = te.stack[slot];
				te.stack[slot] = null;
				return itemstack;
			} else {
				itemstack = te.stack[slot].splitStack(amount);

				if (te.stack[slot].stackSize == 0) {
					te.stack[slot] = null;
				}
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int arg0) {
		TileEntityResearchTable te = getMaster();
		if(te == null)return null;
		ItemStack is = te.stack[arg0];
		te.stack[arg0] = null;
		return is;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		TileEntityResearchTable te = getMaster();
		if(te == null)return;
		te.stack[index] = stack;
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
		return getMaster() == null ? false : (index > 2 && index < 7) || (index == 2 && stack != null && stack.getItem() == Items.DYE && stack.getItemDamage() == 0) || (index == 17 && stack != null && stack.getItem() == Items.PAPER);
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
		TileEntityResearchTable te = getMaster();
		if(te == null)return;
		te.stack = new ItemStack[te.getSizeInventory()];
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
		return getMaster() == null ? new int[]{} : side == EnumFacing.DOWN ? slotsBottom : slotsSide;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack,
			EnumFacing direction) {
		return getMaster() == null ? false : direction != EnumFacing.DOWN ? (index > 2 && index < 7) || (index == 2 && stack != null && stack.getItem() == Items.DYE && stack.getItemDamage() == 0) || (index == 17 && stack != null && stack.getItem() == Items.PAPER) : false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		return getMaster() == null ? false : index == 16 || index == 18;
	}
	@Override
	public void updateEntity(IBlockState state) {
		if(worldObj.isRemote || state.getValue(ResearchTable.STATE) != 2)return;
		if(this.craftingErrorShowTimer > 0)if(this.craftingErrorShowTimer-- == 1 && (craftingError == 2 || craftingError == 3 || craftingError == 4))craftingError = 0;
		if(stack[0] != null && stack[0].getItem() == CoreInit.bigNoteBook &&
				stack[0].getTagCompound() != null && stack[0].getTagCompound().hasKey("owner")){
			if(checkPower()){
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
					handlePower(this.currentResearch.getEnergyRequired());
				}
				if(this.totalCrafingTime > 0){
					if(this.totalCrafingTime == this.craftingTime){
						if(this.craft()){
							this.craftingTime = 0;
							this.totalCrafingTime = 0;
							this.craftingError = 0;
							if(craftAll){
								craftAll = startCrafting(null, true);
							}
						}else{
							this.craftingError = 1;
						}
					}else this.craftingTime++;
					if(craftingError == 0)handlePower(1);
				}
			}
		}else{
			this.currentResearch = null;
		}
		if(this.inkLevel < 1 && OreDict.isOre(stack[2], "dyeBlack")){
			this.inkLevel += 100;
			ItemStack s = ForgeHooks.getContainerItem(decrStackSize(2, 1));
			if(s != null){
				if(stack[2] == null){
					stack[2] = s.copy();
				}else{
					InventoryHelper.spawnItemStack(worldObj, pos.getX(), pos.getY(), pos.getZ(), s.copy());
				}
			}
		}
	}
	private boolean checkPower() {
		return type == ResearchTableType.BRONZE ? steam.getFluidAmount() > 1000 : (type == ResearchTableType.ELECTRICAL ? energy.getEnergyStored() > 10 : true);
	}

	private void handlePower(double m) {
		switch(type){
		case BRONZE:
			steam.drainInternal(MathHelper.floor_double(10 * m), true);
			break;
		case ELECTRICAL:
			energy.extractEnergy(m, false);
			break;
		case WOODEN:
			break;
		default:
			break;
		}
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
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList tagList = tag.getTagList("Items", 10);
		this.stack = new ItemStack[getSizeInventory()];
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
		this.type = ResearchTableType.VALUES[tag.getInteger("type")];
		this.energy.readFromNBT(tag);
		tagC = tag.getCompoundTag("steam");
		this.steam.readFromNBT(tagC);
		this.craftAll = tag.getBoolean("craftAll");
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
		tag.setInteger("type", type.ordinal());
		energy.writeToNBT(tag);
		tagC = new NBTTagCompound();
		steam.writeToNBT(tagC);
		tag.setTag("steam", tagC);
		tag.setBoolean("craftAll", craftAll);
		return tag;
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if(stack[0] != null && stack[0].getItem() == CoreInit.bigNoteBook &&
				stack[0].getTagCompound() != null && stack[0].getTagCompound().hasKey("owner")){
			if(id == 0){
				if(stack[1] != null && stack[1].getItem() == CoreInit.noteBook){
					if(stack[1].getTagCompound() == null)stack[1].setTagCompound(new NBTTagCompound());
					NBTTagList sList = stack[1].getTagCompound().hasKey("data", 9) ? stack[1].getTagCompound().getTagList("data", 10) : new NBTTagList();
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
				if(this.currentResearch != null && type.isResearchable(this.currentResearch.getComplexity())){
					if(stack[17] != null && stack[17].getItem() == Items.PAPER){
						boolean flag = true;
						List<ItemStack> stackList = this.currentResearch.getResearchRequirements();
						if(stackList != null){
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
							if(stackList != null){
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
				if(extra == -1)craftAll = false;
				else startCrafting(player, extra == 1);
			}
		}
	}
	private boolean startCrafting(EntityPlayer player, boolean all){
		if(this.totalCrafingTime < 1 && this.hasItemsInCrafting()){
			ResearchHandler h = getResearchHandler(stack[0]);
			if(h != null){
				ReturnData data = AdvancedCraftingHandler.craft(new ItemStack[]{stack[7],stack[8],
						stack[9],stack[10],stack[11],stack[12],stack[13],stack[14],stack[15]},
						h.getResearchesCompleted(), type.getLevel(), worldObj);
				if(data != null){
					if(data.hasAllResearches()){
						if(data.isRightLevel()){
							ItemAdvCraftedEvent.EventResult result = ItemAdvCraftedEvent.fire(h.name, stack, 7, data.getReturnStack(), data.getExtraStack(), data.getTime());
							if(result.canCraft){
								this.craftingStackOut = result.mainStack;
								this.craftingStackExtra = result.secondStack;
								this.totalCrafingTime = result.time;
								this.craftAll = all;
								for(int i = 7;i<16;i++){
									this.decrStackSize(i, 1);
								}
								return true;
							}else{
								craftingError = 4;
								craftingErrorShowTimer = 50;
								if(result.errorMessage != null){
									if(player != null)TomsModUtils.sendChatTranslate(player, TextFormatting.RED, "tomsMod.craftingFailedError", new TextComponentTranslation("tile.resTable.name"), result.errorMessage);
								}
								return false;
							}
						}else{
							craftingError = 3;
							craftingErrorShowTimer = 50;
							return false;
						}
					}else{
						craftingError = 2;
						craftingErrorShowTimer = 50;
						return false;
					}
				}
			}
		}
		return false;
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
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return null;
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return false;
	}
	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return null;
	}
	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return 0;
	}
	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return 0;
	}
	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return 0;
	}
	public ResearchHandler getResearchHanler() {
		return stack[0] != null ? getResearchHandler(stack[0]) : null;
	}
	@Override
	public void writeToPacket(NBTTagCompound tag) {
		tag.setInteger("t", type.ordinal());
	}
	@Override
	public void readFromPacket(NBTTagCompound tag) {
		type = ResearchTableType.VALUES[tag.getInteger("t")];
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}
	public static enum ResearchTableType implements IStringSerializable{
		WOODEN(CraftingLevel.BASIC_WOODEN, ResearchComplexity.BASIC, -1),
		BRONZE(CraftingLevel.BRONZE, ResearchComplexity.BRONZE, 0),
		ELECTRICAL(CraftingLevel.BASIC_ELECTRICAL, ResearchComplexity.ELECTRICAL, 1)
		;
		public static final ResearchTableType[] VALUES = values();
		private final CraftingLevel level;
		private final ResearchComplexity researchLevel;
		private final int upgradeMeta;
		private ResearchTableType(CraftingLevel lvl, ResearchComplexity rLvl, int upgradeMeta) {
			level = lvl;
			researchLevel = rLvl;
			this.upgradeMeta = upgradeMeta;
		}
		public boolean isResearchable(ResearchComplexity complexity) {
			return researchLevel.isEqual(complexity);
		}
		@Override
		public String getName() {
			return name().toLowerCase();
		}
		public CraftingLevel getLevel() {
			return level;
		}
		public static ResearchTableType getFromItem(int meta){
			for(int i = 0;i<VALUES.length;i++){
				if(VALUES[i].upgradeMeta == meta)return VALUES[i];
			}
			return null;
		}
	}
	public ResearchTableType getType(){
		TileEntityResearchTable te = getMaster();
		return te != null ? te.type : type;
	}
	public TileEntityResearchTable getMaster(){
		return getMaster(state == null ? worldObj.getBlockState(pos) : state);
	}
	public TileEntityResearchTable getMaster(IBlockState s){
		int state = s.getValue(ResearchTable.STATE);
		if(state == 0)return null;
		if(state == 2)return this;
		EnumFacing facing = s.getValue(ResearchTable.FACING);
		BlockPos parentPos = pos.offset(facing.rotateYCCW());
		TileEntity tile = worldObj.getTileEntity(parentPos);
		return tile instanceof TileEntityResearchTable ? (TileEntityResearchTable) tile : null;
	}

	public void dropUpgrades() {
		if(type.ordinal() > 0){
			double x = pos.getX() + .5D;
			double y = pos.getY() + .5D;
			double z = pos.getZ() + .5D;
			int o = type.ordinal();
			while(o > 0){
				int meta = ResearchTableType.VALUES[o].upgradeMeta;
				if(meta > -1){
					InventoryHelper.spawnItemStack(worldObj, x, y, z, new ItemStack(CoreInit.researchTableUpgrade, 1, meta));
				}
				o--;
			}
		}
	}

	public byte upgrade(int meta) {
		ResearchTableType t = ResearchTableType.getFromItem(meta);
		if(t == null)return 0;
		if(type.ordinal() + 1 == t.ordinal()){
			type = t;
			IBlockState s = worldObj.getBlockState(pos);
			EnumFacing facing = s.getValue(ResearchTable.FACING);
			BlockPos parentPos = pos.offset(facing.rotateY());
			markBlockForUpdate();
			markBlockForUpdate(parentPos);
			initializeCapabilities();
			return 2;
		}else{
			if(type.ordinal() >= t.ordinal())return 1;
			else{
				return 3;
			}
		}
	}
	@Override
	public boolean canHaveFluidHandler(EnumFacing f) {
		return type == ResearchTableType.BRONZE;
	}
}
