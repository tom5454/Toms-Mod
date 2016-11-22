package com.tom.factory.tileentity;

import static com.tom.api.energy.EnergyType.HV;
import static com.tom.api.energy.EnergyType.LV;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IConfigurable.IConfigurationOption.ConfigurationOptionMachine;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.defense.ForceDeviceControlType;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockMachineBase;

public abstract class TileEntityMachineBase extends TileEntityTomsMod implements ISidedInventory, IEnergyReceiver, IConfigurable {
	protected ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	protected EnergyType TYPE = HV;
	public boolean active = false;
	//private boolean lastActive = false;
	protected static final float[] TYPE_MULTIPLIER_SPEED = new float[]{1.0F, 0.85F, 0.7F};
	protected static final int[] MAX_SPEED_UPGRADE_COUNT = new int[]{24, 10, 4};
	protected int maxProgress = 1;
	protected int progress = -1;
	public ForceDeviceControlType rs;
	private boolean powersharing = false;
	private byte outputSides;
	private IConfigurationOption cfgOption;
	public TileEntityMachineBase() {
		rs = ForceDeviceControlType.IGNORE;
		cfgOption = new ConfigurationOptionMachine(outputSides, getFront(), new ResourceLocation("tomsmodfactory:textures/blocks/itemOutput.png"), getTop(), rs, this);
		updateSlots();
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
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		stack = new ItemStack[this.getSizeInventory()];
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
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
		getEnergy().readFromNBT(compound);
		TYPE = EnergyType.VALUES[compound.getInteger("energyType")];
		rs = ForceDeviceControlType.get(compound.getInteger("rsMode"));
		outputSides = compound.getByte("output");
		powersharing = compound.getBoolean("powersharing");
		updateSlots();
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
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
		getEnergy().writeToNBT(compound);
		compound.setInteger("energyType", TYPE.ordinal());
		compound.setInteger("rsMode", rs.ordinal());
		compound.setByte("output", outputSides);
		compound.setBoolean("powersharing", powersharing);
		return compound;
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == TYPE;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return TYPE.getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return canConnectEnergy(from, type) ? TYPE.convertFrom(LV, getEnergy().receiveEnergy(LV.convertFrom(TYPE, maxReceive), simulate)) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return getEnergy().getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return getEnergy().getMaxEnergyStored();
	}
	public abstract EnergyStorage getEnergy();

	public void setType(int meta){
		if(meta == 0){
			TYPE = HV;
		}else if(meta == 1){
			TYPE = EnergyType.MV;
		}else{
			TYPE = EnergyType.LV;
		}
	}
	public int getType(){
		return getMetaFromEnergyType(TYPE);
	}
	public void writeToStackNBT(NBTTagCompound tag){
		getEnergy().writeToNBT(tag);
		int i = getUpgradeSlot();
		if(i > -1){
			NBTTagList list = new NBTTagList();
			if(stack[i] != null){
				NBTTagCompound t = new NBTTagCompound();
				stack[i].writeToNBT(t);
				t.setByte("Slot", (byte) i);
				list.appendTag(t);
			}
			tag.setTag("inventory", list);
		}
		tag.setInteger("rsMode", rs.ordinal());
		tag.setByte("output", outputSides);
	}
	public abstract int getUpgradeSlot();
	public abstract int[] getOutputSlots();
	public abstract int[] getInputSlots();
	public void pushOutput(EnumFacing side){
	}

	public int getMaxProgress(){
		return !worldObj.isRemote ? MathHelper.floor_double(getMaxProcessTimeNormal() / TYPE_MULTIPLIER_SPEED[getType()]) : maxProgress;
	}
	@Override
	public int getField(int id) {
		return id == 0 ? progress : id == 1 ? maxProgress : 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)progress = value;
		else if(id == 1)maxProgress = value;
	}
	@Override
	public final void preUpdate(IBlockState state) {
		if(!worldObj.isRemote){
			maxProgress = getMaxProgress();
			if(rs == ForceDeviceControlType.HIGH_REDSTONE){
				this.active = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
			}else if(rs == ForceDeviceControlType.LOW_REDSTONE){
				this.active = worldObj.isBlockIndirectlyGettingPowered(pos) == 0;
			}else if(rs == ForceDeviceControlType.IGNORE){
				this.active = true;
			}
			//lastActive = active;
			if(powersharing && getEnergy().getEnergyStoredPer() > 0.5F && worldObj.getTotalWorldTime() % 10 == 0){
				EnumFacing facing = state.getValue(BlockMachineBase.FACING);
				sharePower(facing.rotateY());
				sharePower(facing.rotateYCCW());
			}
		}
	}
	private void sharePower(EnumFacing f){
		TileEntityMachineBase t = this;
		int d = 0;
		while(t != null && d < 8 && getEnergy().getEnergyStoredPer() > 0.5F){
			d++;
			TileEntity te = worldObj.getTileEntity(pos.offset(f, d));
			if(te != null && te instanceof TileEntityMachineBase){
				t = (TileEntityMachineBase) te;
				double r = t.receiveEnergy(f.getOpposite(), TYPE, 250, true);
				if(r > 0){
					t.receiveEnergy(f, TYPE, getEnergy().extractEnergy(r, false), false);
					r = t.receiveEnergy(f.getOpposite(), TYPE, 250, true);
					if(r > 0){
						t.receiveEnergy(f, TYPE, getEnergy().extractEnergy(r, false), false);
						r = t.receiveEnergy(f.getOpposite(), TYPE, 250, true);
						if(r > 0){
							t.receiveEnergy(f, TYPE, getEnergy().extractEnergy(r, false), false);
						}
					}
				}
			}else{
				t = null;
			}
		}
	}
	@Override
	public final void postUpdate(IBlockState state) {
		if(outputSides != 0){
			int[] out = getOutputSlots();
			EnumFacing facing = state.getValue(BlockMachineBase.FACING);
			for(int i = 0;i<EnumFacing.VALUES.length;i++){
				EnumFacing f = EnumFacing.VALUES[i];
				EnumFacing f2 = f;
				switch(f){
				case DOWN:
					f2 = EnumFacing.DOWN;
					break;
				case EAST:
					f2 = facing.rotateYCCW();
					break;
				case NORTH:
					f2 = facing;
					break;
				case SOUTH:
					f2 = facing.getOpposite();
					break;
				case UP:
					f2 = EnumFacing.UP;
					break;
				case WEST:
					f2 = facing.rotateY();
					break;
				default:
					break;
				}
				if(contains(f)){
					pushOutput(f2);
					if(out != null){
						for(int j = 0;j<out.length;j++){
							int s = out[j];
							stack[s] = TomsModUtils.pushStackToNeighbours(stack[s], worldObj, pos, new EnumFacing[]{f2});
						}
					}
				}
			}
		}
	}
	public abstract int getMaxProcessTimeNormal();
	public abstract ResourceLocation getFront();
	public ResourceLocation getTop(){
		return null;
	}

	public static int getMetaFromEnergyType(EnergyType type){
		return type == HV ? 0 : type == EnergyType.MV ? 1 : 2;
	}
	public int getSpeedUpgradeCount(){
		int slot = getUpgradeSlot();
		return Math.min(slot < 0 ? 0 : stack[slot] != null && stack[slot].getItem() == FactoryInit.speedUpgrade ? stack[slot].stackSize : 0, MAX_SPEED_UPGRADE_COUNT[getType()]);
	}
	public int getMaxSpeedUpgradeCount(){
		return MAX_SPEED_UPGRADE_COUNT[getType()];
	}
	@Override
	public IConfigurationOption getOption() {
		return cfgOption;
	}

	@Override
	public boolean canConfigure(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}

	@Override
	public BlockPos getSecurityStationPos() {
		return null;
	}

	@Override
	public void setCardStack(ItemStack stack) {

	}

	@Override
	public ItemStack getCardStack() {
		return null;
	}
	private int[][] SLOTS = new int[6][0];
	@Override
	public void receiveNBTPacket(NBTTagCompound tag) {
		outputSides = tag.getByte("s");
		rs = ForceDeviceControlType.get(tag.getInteger("r"));
		powersharing = tag.getBoolean("p");
		updateSlots();
	}

	@Override
	public void writeToNBTPacket(NBTTagCompound tag) {
		tag.setByte("s", outputSides);
		tag.setInteger("r", rs.ordinal());
		tag.setBoolean("p", powersharing);
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS[side.ordinal()];
	}
	protected boolean canRun(){
		if(rs == ForceDeviceControlType.HIGH_REDSTONE){
			this.active = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
		}else if(rs == ForceDeviceControlType.LOW_REDSTONE){
			this.active = worldObj.isBlockIndirectlyGettingPowered(pos) == 0;
		}else if(rs == ForceDeviceControlType.IGNORE){
			this.active = true;
		}
		return active;
	}
	public boolean contains(EnumFacing side) {
		return (outputSides & (1 << side.ordinal())) != 0;
	}
	protected void updateSlots(){
		int[] in = getInputSlots();
		int[] out = getOutputSlots();
		SLOTS = new int[6][];
		for(int i = 0;i<EnumFacing.VALUES.length;i++){
			int size = 0;
			if(in != null){
				size += in.length;
			}
			if(out != null && contains(EnumFacing.VALUES[i])){
				size += out.length;
			}
			SLOTS[i] = new int[size];
			if(in != null){
				for(int j = 0;j<in.length;j++){
					SLOTS[i][j] = in[j];
				}
			}
			if(out != null && contains(EnumFacing.VALUES[i])){
				for(int j = 0;j<out.length;j++){
					SLOTS[i][j] = out[j];
				}
			}
		}
	}
}
