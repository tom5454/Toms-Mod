package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.LASER;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldType;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyProvider;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;

import com.tom.energy.block.Generator;

public class TileEntityGenerator extends TileEntityTomsMod implements
IEnergyProvider, IInventory, ICustomMultimeterInformation {
	public int fuel = 0;
	public ItemStack fuelStack = null;
	public ItemStack currentlyBurning = null;
	private EnergyStorage energy = new EnergyStorage(1000,100);
	//private static final Random rand = new Random();
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return true;
	}
	@Override
	public void updateEntity(IBlockState state){
		//System.out.println("update");
		if(worldObj.isRemote)return;
		if(this.fuel < 1 && this.fuelStack != null && !energy.isFull() && ((pos.getY() > 48 && pos.getY() < 150) || worldObj.getWorldType() == WorldType.FLAT)){
			ItemStack fss = this.fuelStack;
			int itemBurnTime = TomsModUtils.getBurnTime(fss);
			if(itemBurnTime > 0){
				this.fuel = itemBurnTime;
				this.currentlyBurning = this.fuelStack;
				this.fuelStack = null;
				if(fss.getItem().getContainerItem(fss) != null){
					ItemStack s = fss.getItem().getContainerItem(fss);
					EnumFacing f = state.getValue(Generator.FACING);
					EnumFacing facing = f.getOpposite();
					BlockPos invP = pos.offset(facing);
					IInventory inv = TileEntityHopper.getInventoryAtPosition(worldObj, invP.getX(), invP.getY(), invP.getZ());
					if(inv != null)
						s = TileEntityHopper.putStackInInventoryAllSlots(inv, s, facing);
					if(s != null){
						EntityItem item = new EntityItem(worldObj, pos.getX()+0.5D, pos.getY()+1, pos.getZ()+0.5D, fss.getItem().getContainerItem(fss));
						item.motionX = facing.getFrontOffsetX() * 0.3;
						item.motionZ = facing.getFrontOffsetZ() * 0.3;
						worldObj.spawnEntityInWorld(item);
					}
				}
			}else{
				if(fss != null){
					EntityItem item = new EntityItem(worldObj, pos.getX()+0.5D, pos.getY()+1, pos.getZ()+0.5D, fss);
					worldObj.spawnEntityInWorld(item);
				}
			}
			this.markDirty();
		}else if(fuel > 0){
			fuel = fuel - 1;
			energy.receiveEnergy(worldObj.provider.getDimension() == 1 && pos.getY() > 15 ? 1.2 : ((pos.getY() > 48 && pos.getY() < 150) || worldObj.getWorldType() == WorldType.FLAT) ? 1.0 : .1, false);
			//System.out.println(fuel);
			/*double var6 = pos.getX() + 0.5D;
            double var8 = pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double var10 = pos.getZ() + 0.5D;
            double var12 = 0.52D;
            double var14 = rand.nextDouble() * 0.6D - 0.3D;*/
			//worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 1, 1, 1, 1);
			//worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX(),pos.getY()+0.5D,pos.getZ(), 0.0D, 0.0D, 0.0D, new int[1]);
			if(!state.getValue(Generator.ACTIVE)){
				TomsModUtils.setBlockState(worldObj, pos, state.withProperty(Generator.ACTIVE, true), 2);
				this.markDirty();
			}
		}else{
			if(state.getValue(Generator.ACTIVE)){
				TomsModUtils.setBlockState(worldObj, pos, state.withProperty(Generator.ACTIVE, false), 2);
				this.markDirty();
			}
			this.currentlyBurning = null;
		}
		//System.out.println("f:"+fuel);
		if(this.energy.getEnergyStored() > 0){
			for(EnumFacing f : EnumFacing.VALUES){
				//	TileEntity receiver = worldObj.getTileEntity(pos.offset(f));
				//if(receiver instanceof IEnergyReceiver) {
				//System.out.println("send");
				EnumFacing fOut = f.getOpposite();
				//IEnergyReceiver recv = (IEnergyReceiver)receiver;
				LASER.pushEnergyTo(worldObj, pos, fOut, energy, false);
				//}
			}
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.fuel = tag.getInteger("fuel");
		this.fuelStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("fuelStack"));
		this.currentlyBurning = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("currentlyBurning"));
		this.energy.readFromNBT(tag.getCompoundTag("energy"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("fuel", this.fuel);
		NBTTagCompound fuelTag = new NBTTagCompound();
		if(this.fuelStack != null) this.fuelStack.writeToNBT(fuelTag);
		tag.setTag("fuelStack", fuelTag);
		fuelTag = new NBTTagCompound();
		if(this.currentlyBurning != null) this.currentlyBurning.writeToNBT(fuelTag);
		tag.setTag("currentlyBurning", fuelTag);
		tag.setTag("energy", this.energy.writeToNBT(new NBTTagCompound()));
		return tag;
	}
	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract,
			boolean simulate) {
		return 0;
	}
	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return type == LASER ? energy.getEnergyStored() : 0;
	}
	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return type == LASER ? energy.getMaxEnergyStored() : 0;
	}
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("Generator");
	}
	@Override
	public String getName() {
		return "tomsmod.gui.generator";
	}
	@Override
	public boolean hasCustomName() {
		return false;
	}
	@Override
	public void clear() {
		this.fuelStack = null;
	}
	@Override
	public void closeInventory(EntityPlayer arg0) {

	}
	@Override
	public ItemStack decrStackSize(int slot, int count) {
		if(slot == 0){
			if (this.fuelStack != null)
			{
				ItemStack var3;

				if (this.fuelStack.stackSize <= count)
				{
					var3 = this.fuelStack;
					this.fuelStack = null;
					return var3;
				}
				else
				{
					var3 = this.fuelStack.splitStack(count);

					if (this.fuelStack.stackSize == 0)
					{
						this.fuelStack = null;
					}

					return var3;
				}
			}
			else
			{
				return null;
			}
		}
		return null;
	}
	@Override
	public int getField(int arg0) {
		return 0;
	}
	@Override
	public int getFieldCount() {
		return 0;
	}
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	@Override
	public int getSizeInventory() {
		return 1;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? this.fuelStack : null;
	}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		int bt = TomsModUtils.getBurnTime(is);
		boolean ret = slot == 0 ? bt > 0 : false;
		return ret;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUseable(pos, player, worldObj, this);
	}
	@Override
	public void openInventory(EntityPlayer arg0) {

	}
	@Override
	public ItemStack removeStackFromSlot(int slot) {
		if(slot == 0){
			ItemStack is = this.fuelStack;
			fuelStack = null;
			return is;
		}
		return null;
	}
	@Override
	public void setField(int arg0, int arg1) {

	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {
		if(slot == 0){
			this.fuelStack = is;
		}
	}
	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LASER.getList();
	}
	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		if(fuelStack != null){
			if(fuel > 0){
				list.add(new TextComponentTranslation("tomsMod.chat.burnTime",fuel));
				list.add(new TextComponentTranslation("tomsMod.chat.currentlyBurning",currentlyBurning != null ? currentlyBurning.getTextComponent() : new TextComponentTranslation("tomsMod.na")));
				list.add(new TextComponentTranslation("tomsMod.chat.inventory",fuelStack != null ? fuelStack.getTextComponent() : new TextComponentTranslation("tomsMod.na")));
			}else{
				list.add(new TextComponentTranslation("tomsMod.chat.inventory",fuelStack != null ? fuelStack.getTextComponent() : new TextComponentTranslation("tomsMod.na")));
			}
		}else if(fuel > 0){
			list.add(new TextComponentTranslation("tomsMod.chat.burnTime",fuel));
			list.add(new TextComponentTranslation("tomsMod.chat.currentlyBurning",currentlyBurning != null ? currentlyBurning.getTextComponent() : new TextComponentTranslation("tomsMod.na")));
		}
		return list;
	}
}
