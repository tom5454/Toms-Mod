package com.tom.defense.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.item.IPowerLinkCard;
import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.item.ISwitch;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.IForceDevice;
import com.tom.api.tileentity.IForcePowerStation;
import com.tom.api.tileentity.IGuiTile;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.DamageSourceTomsMod;
import com.tom.defense.DefenseInit;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.block.ForceCapacitor;
import com.tom.handler.GuiHandler.GuiIDs;

public class TileEntityDefenseStation extends TileEntityTomsMod implements
IForceDevice, ISidedInventory, IGuiTile, INBTPacketReceiver {

	public ForceDeviceControlType rsMode = ForceDeviceControlType.LOW_REDSTONE;
	private ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	private EnergyStorage energy = new EnergyStorage(10000000,100000,200000);
	public boolean active = false;
	private boolean firstStart = true, lastActive = false;
	public int clientEnergy = 0;
	private static final int[] SLOTS = new int[]{9,10,11,12,
			13,14,15,16,
			17,18,19,20,
			21,22,23,24},
			ITEMS = new int[]{25,26,27,28,29,30,31,
					32,33,34,35,36,37,38,
					39,40,41,42,43,44,45
	};
	public DefenseStationConfig config = DefenseStationConfig.INFORM;
	public String customName = "Defense Station";
	private boolean isWhiteList = false, useMeta = true, useNBT = true, useMod = false;
	@Override
	public double receiveEnergy(double maxReceive, boolean simulate) {
		return energy.receiveEnergy(maxReceive, simulate);
	}
	@Override
	public boolean isValid(BlockPos from) {
		BlockPos c = this.getCapacitorPos();
		return this.hasWorldObj() && c != null && c.equals(from) && pos != null && worldObj.getBlockState(pos) != null && worldObj.getBlockState(pos).getBlock() == DefenseInit.defenseStation;
	}
	public BlockPos getCapacitorPos(){
		return stack[1] != null && stack[1].getItem() instanceof IPowerLinkCard ? ((IPowerLinkCard)stack[1].getItem()).getMaster(stack[1]) : null;
	}
	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		if(!worldObj.isRemote){
			//player.attackEntityFrom(DamageSourceTomsMod.fieldDamage, 8F);
			if(held != null && held.getItem() instanceof ISwitch && ((ISwitch)held.getItem()).isSwitch(held, player)){
				if(rsMode == ForceDeviceControlType.SWITCH){
					boolean canAccess = true;
					BlockPos securityStationPos = this.getSecurityStationPos();
					if(securityStationPos != null){
						TileEntity tileentity = worldObj.getTileEntity(securityStationPos);
						if(tileentity instanceof ISecurityStation){
							ISecurityStation tile = (ISecurityStation) tileentity;
							canAccess = tile.canPlayerAccess(AccessType.SWITCH_DEVICES, player);
						}
					}
					if(canAccess){
						this.active = !this.active;
						return true;
					}else{
						TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
						return false;
					}
				}else{
					TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.mnotSwitchable", new TextComponentTranslation(held.getUnlocalizedName()+".name"));
					return false;
				}
			}else{
				boolean canAccess = true;
				BlockPos securityStationPos = this.getSecurityStationPos();
				if(securityStationPos != null){
					TileEntity tileentity = worldObj.getTileEntity(securityStationPos);
					if(tileentity instanceof ISecurityStation){
						ISecurityStation tile = (ISecurityStation) tileentity;
						canAccess = tile.canPlayerAccess(AccessType.CONFIGURATION, player);
					}
				}
				if(canAccess){
					player.openGui(CoreInit.modInstance, GuiIDs.defenseStation.ordinal(), worldObj, pos.getX(),pos.getY(),pos.getZ());
				}else{
					TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
					return false;
				}
				return true;
			}
		}else{
			return true;
		}
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if(id == 5){
			rsMode = ForceDeviceControlType.get(extra);
		}else if(id == 4){
			config = DefenseStationConfig.get(extra);
		}else if(id == 0){
			this.isWhiteList = !this.isWhiteList;
		}else if(id == 1){
			this.useMeta = !this.useMeta;
		}else if(id == 2){
			this.useMod = !this.useMod;
		}else if(id == 3){
			this.useNBT = !this.useNBT;
		}
	}

	@Override
	public String getName() {
		return customName;
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
		return 46;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return stack[index];
	}

	@Override
	public ItemStack decrStackSize(int slot, int par2) {
		if (this.stack[slot] != null) {
			ItemStack itemstack;
			if (this.stack[slot].stackSize <= par2) {
				itemstack = this.stack[slot];
				this.stack[slot] = null;
				return itemstack;
			} else {
				itemstack = this.stack[slot].splitStack(par2);

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
	public ItemStack removeStackFromSlot(int index) {
		ItemStack is = stack[index];
		stack[index] = null;
		return is;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
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
		return true;
	}

	@Override
	public int getField(int id) {
		return id == 0 ? this.clientEnergy : 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)this.clientEnergy = value;
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		this.stack = new ItemStack[this.getSizeInventory()];
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
		stack = new ItemStack[this.getSizeInventory()];
		NBTTagList list = compound.getTagList("inventory", 10);
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < this.stack.length)
			{
				this.stack[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		rsMode = ForceDeviceControlType.get(compound.getInteger("redstone_mode"));
		this.active = compound.getBoolean("active");
		config = DefenseStationConfig.get(compound.getInteger("config"));
		this.customName = compound.getString("customName");
		this.setWhiteList(compound.getBoolean("whiteList"));
		this.setUseMeta(compound.getBoolean("useMeta"));
		this.setUseMod(compound.getBoolean("useMod"));
		this.setUseNBT(compound.getBoolean("useNBT"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<stack.length;i++){
			if(stack[i] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[i].writeToNBT(tag);
				tag.setByte("Slot", (byte) i);
				list.appendTag(tag);
			}
		}
		compound.setTag("inventory", list);
		compound.setInteger("redstone_mode", rsMode.ordinal());
		compound.setBoolean("active", active);
		compound.setInteger("config", config.ordinal());
		compound.setString("customName", customName);
		compound.setBoolean("whiteList", isWhiteList());
		compound.setBoolean("useMeta", useMeta());
		compound.setBoolean("useMod", useMod());
		compound.setBoolean("useNBT", useNBT());
		return compound;
	}
	public void writeToStackNBT(NBTTagCompound compound) {
		energy.writeToNBT(compound);
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<stack.length;i++){
			if(i != 2 && (i < 9 || i > 24) && stack[i] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[i].writeToNBT(tag);
				tag.setByte("Slot", (byte) i);
				list.appendTag(tag);
			}
		}
		compound.setTag("inventory", list);
		compound.setInteger("redstone_mode", rsMode.ordinal());
		compound.setInteger("config", config.ordinal());
		compound.setString("customName", customName);
		compound.setBoolean("whiteList", isWhiteList());
		compound.setBoolean("useMeta", useMeta());
		compound.setBoolean("useMod", useMod());
		compound.setBoolean("useNBT", useNBT());
	}
	public BlockPos getSecurityStationPos() {
		return stack[0] != null && stack[0].getItem() instanceof ISecurityStationLinkCard ? ((ISecurityStationLinkCard)stack[0].getItem()).getStation(stack[0]) : null;
	}
	@Override
	public void updateEntity(IBlockState currentState) {
		if(!worldObj.isRemote){
			if(this.firstStart){
				BlockPos pos = this.getCapacitorPos();
				if(pos != null){
					TileEntity tile = worldObj.getTileEntity(pos);
					if(tile instanceof IForcePowerStation) {
						IForcePowerStation te = (IForcePowerStation) tile;
						te.registerDevice(this);
					}
				}
			}
			if(rsMode == ForceDeviceControlType.HIGH_REDSTONE){
				this.active = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
			}else if(rsMode == ForceDeviceControlType.LOW_REDSTONE){
				this.active = worldObj.isBlockIndirectlyGettingPowered(pos) == 0;
			}else if(rsMode == ForceDeviceControlType.IGNORE){
				this.active = true;
			}
			if(!lastActive && active){
			}
			lastActive = active;
			BlockPos securityStationPos = this.getSecurityStationPos();
			this.clientEnergy  = MathHelper.floor_double(this.energy.getEnergyStored());
			TomsModUtils.setBlockStateWithCondition(worldObj, pos, currentState, ForceCapacitor.ACTIVE, this.active && this.energy.getEnergyStored() > 0.1D && securityStationPos != null);
			TileEntity tile = securityStationPos != null ? worldObj.getTileEntity(securityStationPos) : null;
			if(this.active && this.energy.getEnergyStored() > 100D && securityStationPos != null && tile instanceof ISecurityStation){
				/*int energyUsed = config.build(worldObj);
				double realEnergyUsed = 0.01 + (energyUsed / (100D + (stack[3] != null && stack[3].getItem() == DefenseInit.efficiencyUpgrade ? stack[3].stackSize * 50 : 0)));
				energy.extractEnergy(realEnergyUsed, false);
				this.lastDrained = MathHelper.floor_double(realEnergyUsed * 100);*/
				double killingUsage = ((this.getEfficiencyLevel() * 0.4) + 1);
				ISecurityStation te = (ISecurityStation) tile;
				if(config == DefenseStationConfig.KILL_HOSTILE){
					AxisAlignedBB bounds = getActionBounds();
					List<EntityMob> mobs = worldObj.getEntitiesWithinAABB(EntityMob.class, bounds);
					for(EntityMob mob : mobs){
						double u = killingUsage * mob.getHealth();
						if(this.energy.getEnergyStored() < u)break;
						if(mob.attackEntityFrom(DamageSourceTomsMod.securityDamage, 999))
							energy.extractEnergy(u, false);
					}
					this.handleItems(true, bounds);
				}else if(config == DefenseStationConfig.KILL_FRIENDLY){
					AxisAlignedBB bounds = getActionBounds();
					List<EntityAnimal> animals = worldObj.getEntitiesWithinAABB(EntityAnimal.class, bounds);
					for(EntityAnimal animal : animals){
						double u = killingUsage * animal.getHealth();
						if(this.energy.getEnergyStored() < u)break;
						if(animal.attackEntityFrom(DamageSourceTomsMod.securityDamage, 999))
							energy.extractEnergy(u, false);
					}
					this.handleItems(true, bounds);
				}else if(config == DefenseStationConfig.KILL_ALL){
					AxisAlignedBB bounds = getActionBounds();
					AxisAlignedBB informBounds = getInformBounds();
					List<EntityLivingBase> animals = new ArrayList<EntityLivingBase>(worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bounds));
					List<EntityPlayer> informPlayers = worldObj.getEntitiesWithinAABB(EntityPlayer.class, informBounds);
					for(EntityPlayer player : informPlayers){
						if(animals.contains(player)){
							if(!te.canPlayerAccess(AccessType.STAY_IN_AREA, player)){
								double u = killingUsage * player.getHealth() * 2;
								if(this.energy.getEnergyStored() < u)break;
								if(player.attackEntityFrom(DamageSourceTomsMod.securityDamage, 999))
									energy.extractEnergy(u, false);
								this.pullPlayerInventory(player, true, false);
								TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.beenWarned");
							}
						}else{
							TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.deathWarning");
						}
						animals.remove(player);
					}
					for(EntityLivingBase animal : animals){
						double u = killingUsage * animal.getHealth();
						if(this.energy.getEnergyStored() < u)break;
						if(animal.attackEntityFrom(DamageSourceTomsMod.securityDamage, 999))
							energy.extractEnergy(u, false);
					}
					this.handleItems(true, bounds);
				}else if(config == DefenseStationConfig.KILL){
					AxisAlignedBB bounds = getActionBounds();
					AxisAlignedBB informBounds = getInformBounds();
					List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, bounds);
					List<EntityPlayer> informPlayers = worldObj.getEntitiesWithinAABB(EntityPlayer.class, informBounds);
					for(EntityPlayer player : informPlayers){
						if(players.contains(player)){
							if(!te.canPlayerAccess(AccessType.STAY_IN_AREA, player)){
								energy.extractEnergy(killingUsage * 2, false);
								this.pullPlayerInventory(player, true, false);
								player.attackEntityFrom(DamageSourceTomsMod.securityDamage, 999);
								TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.beenWarned");
								if(this.energy.getEnergyStored() < killingUsage * 2)break;
							}
						}else{
							TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.deathWarning");
						}
					}
					this.handleItems(true, bounds);
				}else if(config == DefenseStationConfig.SEARCH_INVENTOTY){
					AxisAlignedBB bounds = getActionBounds();
					AxisAlignedBB informBounds = getInformBounds();
					List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, bounds);
					List<EntityPlayer> informPlayers = worldObj.getEntitiesWithinAABB(EntityPlayer.class, informBounds);
					for(EntityPlayer player : informPlayers){
						if(players.contains(player)){
							if(!te.canPlayerAccess(AccessType.STAY_IN_AREA, player)){
								energy.extractEnergy(0.1D, false);
								boolean pulled = this.pullPlayerInventory(player, false, false);
								if(pulled)TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.beenWarned");
								if(this.energy.getEnergyStored() < 1D)break;
							}
						}else{
							if(this.pullPlayerInventory(player, false, true))TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.illegalGoods", new TextComponentString(this.customName));
						}
					}
					this.handleItems(true, bounds);
				}else if(config == DefenseStationConfig.INFORM){
					AxisAlignedBB bounds = getActionBounds();
					AxisAlignedBB informBounds = getInformBounds();
					List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, bounds);
					List<EntityPlayer> informPlayers = worldObj.getEntitiesWithinAABB(EntityPlayer.class, informBounds);
					for(EntityPlayer player : informPlayers){
						if((!te.canPlayerAccess(AccessType.STAY_IN_AREA, player)) || (!te.canPlayerAccess(AccessType.HAVE_INVENTORY, player))){
							if(players.contains(player)){
								TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.getOut");
							}else{
								TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.scanningRange", new TextComponentString(this.customName));
							}
						}
					}
				}
			}
		}
	}
	public static enum DefenseStationConfig{
		INFORM("tomsMod.defense.inform"),
		KILL("tomsMod.defense.kill"),
		SEARCH_INVENTOTY("tomsMod.defense.searchInv"),
		KILL_ALL("tomsMod.defense.killAll"),
		KILL_HOSTILE("tomsMod.defense.killHostile"),
		KILL_FRIENDLY("tomsMod.defense.killFriendly")
		;
		public static final DefenseStationConfig[] VALUES = values();
		public static DefenseStationConfig get(int index){
			return VALUES[index % VALUES.length];
		}
		private final String name;

		private DefenseStationConfig(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			EnumFacing direction) {
		return index > 8 && index < 25;
	}
	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		return index > 8 && index < 25;
	}
	private double getEfficiencyLevel(){
		return stack[2] != null && stack[2].getItem() == DefenseInit.efficiencyUpgrade ? stack[2].stackSize : 0;
	}
	private AxisAlignedBB getActionBounds(){
		int uCX = stack[3] != null ? stack[3].stackSize : 0;
		int uCY = stack[4] != null ? stack[4].stackSize : 0;
		int uCZ = stack[5] != null ? stack[5].stackSize : 0;
		AxisAlignedBB box = new AxisAlignedBB(pos, pos);
		return box.expand(2, 2, 2).expand(uCX, uCY, uCZ);
	}
	private AxisAlignedBB getInformBounds(){
		int uCX = stack[6] != null ? stack[6].stackSize : 0;
		int uCY = stack[7] != null ? stack[7].stackSize : 0;
		int uCZ = stack[8] != null ? stack[8].stackSize : 0;
		return this.getActionBounds().expand(2, 2, 2).expand(uCX, uCY, uCZ);
	}
	private void handleItems(boolean pickup, AxisAlignedBB bounds){
		List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, bounds.expand(1, 1, 1));
		for(EntityItem item : items){
			if(energy.getEnergyStored() < 1)break;
			if (item == null)
			{
				continue;
			}
			else
			{
				ItemStack itemstack = item.getEntityItem().copy();
				ItemStack itemstack1 = TileEntityHopper.putStackInInventoryAllSlots(this, itemstack, EnumFacing.UP);

				if (itemstack1 != null && itemstack1.stackSize != 0)
				{
					item.setEntityItemStack(itemstack1);
				}
				else
				{
					item.setDead();
				}
				energy.extractEnergy(0.1D, false);
			}
		}
		for(int slot : SLOTS){
			ItemStack stack = this.stack[slot];
			if(stack != null){
				this.stack[slot] = TomsModUtils.pushStackToNeighbours(stack, worldObj, pos, EnumFacing.VALUES);
			}
		}
	}
	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		this.customName = message.getString("n");
	}
	private boolean pullPlayerInventory(EntityPlayer player, boolean isComplete, boolean simulate){
		if(isComplete){
			for(int i = 0;i<player.inventory.getSizeInventory();i++){
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(stack != null){
					stack = TileEntityHopper.putStackInInventoryAllSlots(this, stack, EnumFacing.UP);
					if(stack != null){
						stack = TomsModUtils.pushStackToNeighbours(stack, worldObj, pos, EnumFacing.VALUES);
						if(stack != null){
							EntityItem item = new EntityItem(worldObj,player.posX, player.posY, player.posZ,stack);
							worldObj.spawnEntityInWorld(item);
							stack = null;
						}
					}
				}
				player.inventory.setInventorySlotContents(i, stack);
			}
			return true;
		}else{
			boolean success = false;
			for(int i = 0;i<player.inventory.getSizeInventory();i++){
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(stack != null){
					boolean inList = isItemInList(stack);
					if((this.isWhiteList() && (!inList)) || (!this.isWhiteList() && inList)){
						if(!simulate){
							stack = TileEntityHopper.putStackInInventoryAllSlots(this, stack, EnumFacing.UP);
							if(stack != null){
								stack = TomsModUtils.pushStackToNeighbours(stack, worldObj, pos, EnumFacing.VALUES);
							}
							player.inventory.setInventorySlotContents(i, stack);
						}
						success = true;
					}
				}
			}
			return success;
		}
	}
	private boolean isItemInList(ItemStack stack){
		for(int slot : ITEMS){
			ItemStack matchTo = this.stack[slot];
			if(matchTo != null){
				if(TomsModUtils.areItemStacksEqual(stack, matchTo, useMeta(), useNBT(), useMod())){
					return true;
				}
			}
		}
		return false;
	}
	public NBTTagCompound getCustomNameMessage(){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("n", customName);
		return tag;
	}
	public boolean isWhiteList() {
		return isWhiteList;
	}
	public void setWhiteList(boolean isWhiteList) {
		this.isWhiteList = isWhiteList;
	}
	public boolean useMeta() {
		return useMeta;
	}
	public void setUseMeta(boolean useMeta) {
		this.useMeta = useMeta;
	}
	public boolean useMod() {
		return useMod;
	}
	public void setUseMod(boolean useMod) {
		this.useMod = useMod;
	}
	public boolean useNBT() {
		return useNBT;
	}
	public void setUseNBT(boolean useNBT) {
		this.useNBT = useNBT;
	}
	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}
	@Override
	public BlockPos getPos2() {
		return pos;
	}
}
