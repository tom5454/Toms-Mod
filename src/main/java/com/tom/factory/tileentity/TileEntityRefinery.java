package com.tom.factory.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import com.google.common.base.Predicate;

import com.tom.api.ITileFluidHandler.Helper;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.Checker;
import com.tom.apis.Checker.CheckerPredicate;
import com.tom.apis.Checker.RunnableStorage;
import com.tom.apis.TomsModUtils;
import com.tom.apis.WorldPos;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.SlabState;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockComponents.ComponentVariants;
import com.tom.factory.block.BlockRefinery;

import com.tom.core.tileentity.TileEntityHidden;
import com.tom.core.tileentity.TileEntityHidden.ILinkableCapabilities;

public class TileEntityRefinery extends TileEntityTomsMod implements ILinkableCapabilities, IInventory{
	private static final Object[][] CONFIG = new Object[][]{{'_', TMResource.STEEL.getSlab(SlabState.BOTTOM),
		'H', getComponent(ComponentVariants.OUTPUT_HATCH) , 'F', getComponent(ComponentVariants.REFINERY_HEATER), 'M', CoreInit.MachineFrameSteel.getDefaultState(), 'E', getComponent(ComponentVariants.ENGINEERING_BLOCK), 'S', TMResource.STEEL.getBlockState(), 'A', TMResource.ALUMINUM.getBlockState(), 'f', CoreInit.steelFence},
		{"__@__", "_FFF_", "HFFFH", "_FFF_", "_____"}, //1
		{"     ", " MMM ", " MAM ", " MMM ", "  f  "}, //2
		{"     ", " MMM ", " MAM ", " MMM ", "  f  "}, //3
		{"     ", " MEM ", " EAE ", " MEM ", "  H  "}, //4
		{"     ", " MEM ", " EAE ", " MEM ", "  f  "}, //5
		{"     ", " EME ", " MAM ", " EME ", "  H  "}, //6
		{"     ", " MEM ", " EAE ", " MEM ", "  f  "}, //7
		{"     ", " SMS ", " SMS ", " SMS ", "  H  "}, //8
		{"  _  ", "_SSS_", "_S S_", "_SSS_", "  E  "}, //9
		{"     ", " ___ ", " _S_ ", " ___ ", "     "}, //10
		{"     ", "     ", "  _  ", "     ", "     "}, //11
	};
	private static final Predicate<Character> HATCH_PREDICATE = new Predicate<Character>() {

		@Override
		public boolean apply(Character input) {
			return input.charValue() == 'H';
		}
	};
	public TileEntityRefinery() {
		tankIn = new FluidTank(50000);
		tankOut1 = new FluidTank(20000);
		tankOut2 = new FluidTank(20000);
		tankOut3 = new FluidTank(20000);
		handlers = new IFluidHandler[]{null, Helper.getFluidHandlerFromTank(tankIn, CoreInit.oil, true, false), null, null, Helper.getFluidHandlerFromTank(tankOut1, CoreInit.fuel, false, true), null, Helper.getFluidHandlerFromTank(tankOut2, CoreInit.lpg, false ,true), null, Helper.getFluidHandlerFromTank(tankOut3, CoreInit.kerosene, false, true)};
	}
	/**2 Fuel, 2 LPG, 1 Kerosene*/
	private FluidTank tankIn, tankOut1, tankOut2, tankOut3;
	private IFluidHandler[] handlers;
	private double heat = 0;
	private int burnTime = 0, maxBurnTime = 0;
	public int clientHeat;
	public static final int MAX_TEMP = 1500;
	private ItemStack[] stack = new ItemStack[getSizeInventory()];
	private static final Map<Character, CheckerPredicate<WorldPos>> materialMap = new HashMap<Character, CheckerPredicate<WorldPos>>();
	private RunnableStorage killList = new RunnableStorage(true);
	private static final CheckerPredicate<WorldPos> AIR = new CheckerPredicate<WorldPos>() {

		@Override
		public int apply(WorldPos worldPos) {
			IBlockState input = worldPos.world.getBlockState(worldPos.pos);
			return input.getMaterial() == Material.AIR ? 2 : 0;
		}
	};
	static{
		Object[][] o = CONFIG;
		for(int i = 0;i<o[0].length;i+=2){
			char c = (Character) o[0][i];
			Object stateO = o[0][i+1];
			int m = 0;
			Block b = null;
			if(stateO instanceof IBlockState){
				IBlockState state = (IBlockState) o[0][i+1];
				b = state.getBlock();
				m = b.getMetaFromState(state);
			}else{
				m = -1;
				b = (Block) stateO;
			}
			final int meta = m;
			final Block block = b;
			materialMap.put(c, new CheckerPredicate<WorldPos>(){

				@Override
				public int apply(WorldPos worldPos) {
					IBlockState input = worldPos.world.getBlockState(worldPos.pos);
					if(input.getBlock() == CoreInit.blockHidden){
						TileEntityHidden te = (TileEntityHidden) worldPos.world.getTileEntity(worldPos.pos);
						if(worldPos.num1 == 2){
							te.kill();
							return 0;
						}
						return te.blockEquals(block, meta) ? 1 : 0;
					}else{
						if(worldPos.num1 == 1){
							TileEntityHidden.place(worldPos.world, worldPos.pos, worldPos.pos2, new ItemStack(FactoryInit.refinery), worldPos.num2);
							return 0;
						}else{
							int m = input.getBlock().getMetaFromState(input);
							return input.getBlock() == block && (m == meta || meta == -1) ? 2 : 0;
						}
					}
				}

			});
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing, BlockPos from, int id) {
		return id > 0 ? capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && id == 1) : false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing, BlockPos from, int id) {
		if(id > 0){
			if (id == 1 && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
				return this.<T>getInstance(itemHandlerSidedMap, facing, capability);
			}
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
				return id < handlers.length ? (T) (handlers[id]) : null;
			}
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	private static IBlockState getComponent(ComponentVariants variant){
		return FactoryInit.components.getStateFromMeta(variant.ordinal());
	}
	private boolean getMultiblock(IBlockState state){
		return getLayers(worldObj, state.getValue(BlockRefinery.FACING), pos, killList);
	}
	private static boolean getLayers(final World world, final EnumFacing facing, final BlockPos pos, RunnableStorage killList){
		List<Checker> list = new ArrayList<Checker>();
		final MutableBlockPos corner = new MutableBlockPos(pos);
		for(int l = 1;l<CONFIG.length;l++){
			final int m = l - 1;
			Object[] objA = CONFIG[l];
			for(int k = 0;k<objA.length;k++){
				Object o = objA[k];
				final int n = k;
				char[] cA = o.toString().toCharArray();
				for(int i = 0;i<cA.length;i++){
					final int j = i;
					final char c = cA[i];
					if(c == '@'){
						corner.setPos(pos.offset(facing.rotateY(), -i).offset(facing, -k).offset(EnumFacing.DOWN, -(l-1)));
						//System.out.println(corner);
					}else if(c == ' '){
						list.add(new Checker() {

							@Override
							public int apply(int doRun) {
								return AIR.apply(new WorldPos(world, corner.offset(facing.rotateY(), j).offset(facing, n).offset(EnumFacing.UP, m), pos, 0, 0));
							}
						});
					}else{
						list.add(new Checker() {

							@Override
							public int apply(int doRun) {
								CheckerPredicate<WorldPos> predicate = materialMap.get(c);
								if(predicate == null)predicate = AIR;
								return predicate.apply(new WorldPos(world, corner.offset(facing.rotateY(), j).offset(facing, n).offset(EnumFacing.UP, m), pos, doRun, HATCH_PREDICATE.apply(c) ? m+1 : 0));
							}
						});
					}
				}
			}
		}
		return TomsModUtils.checkAll(list, killList);
	}
	@Override
	public void updateEntity(IBlockState state){
		if(!worldObj.isRemote){
			if(getMultiblock(state)){
				if(this.burnTime < 1 && this.getStackInSlot(0) != null && ((pos.getY() > 48 && pos.getY() < 150) || worldObj.getWorldType() == WorldType.FLAT)){
					ItemStack fss = this.getStackInSlot(0);
					int itemBurnTime = TomsModUtils.getBurnTime(fss);
					if(itemBurnTime > 0){
						this.maxBurnTime = this.burnTime = itemBurnTime;
						this.decrStackSize(0, 1);
						if(fss.getItem().getContainerItem(fss) != null){
							ItemStack s = fss.getItem().getContainerItem(fss);
							EnumFacing f = state.getValue(BlockRefinery.FACING);
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
						heat = Math.min(0.06D + heat, MAX_TEMP);
					}else{
						if(fss != null){
							EntityItem item = new EntityItem(worldObj, pos.getX()+0.5D, pos.getY()+1, pos.getZ()+0.5D, fss);
							worldObj.spawnEntityInWorld(item);
						}
					}
					this.markDirty();
				}else if(burnTime > 0){
					burnTime = Math.max(burnTime - 5, 0);
					if(state.getValue(BlockRefinery.STATE) == 1){
						TomsModUtils.setBlockState(worldObj, pos, state.withProperty(BlockRefinery.STATE, 2), 2);
						this.markDirty();
					}
					double increase = heat > 400 ? heat > 800 ? 0.08D : 0.09D : 0.11D;
					heat = Math.min(increase + heat, MAX_TEMP);
				}else{
					if(state.getValue(BlockRefinery.STATE) == 2){
						TomsModUtils.setBlockState(worldObj, pos, state.withProperty(BlockRefinery.STATE, 1), 2);
						this.markDirty();
					}
					heat = Math.max(heat - (heat / 500), 20);
					this.maxBurnTime = 0;
				}
				if(stack[0] == null){
					stack[0] = stack[1];
					stack[1] = stack[2];
					stack[2] = stack[3];
					stack[3] = null;
				}else if(stack[0].stackSize < stack[0].getMaxStackSize()){
					for(int i = 1;i<stack.length;i++){
						/*if(stack[i-1] == null){
							stack[i-1] = stack[i];
							stack[i] = null;
						}*/
						if(ItemStack.areItemsEqual(stack[0], stack[i]) && ItemStack.areItemStackTagsEqual(stack[0], stack[i])){
							int space = stack[0].getMaxStackSize() - stack[0].stackSize;
							ItemStack s = decrStackSize(i, space);
							if(s != null)stack[0].stackSize = s.stackSize;
						}
						/*if(stack[i - 1] != null && stack[i] != null && ItemStack.areItemsEqual(stack[i-1], stack[i]) && ItemStack.areItemStackTagsEqual(stack[i-1], stack[i])){
							int space = stack[i-1].getMaxStackSize() - stack[i-1].stackSize;
							ItemStack s = decrStackSize(i, space);
							if(s != null)stack[i-1].stackSize = s.stackSize;
						}*/
					}
				}
				process();
				if(heat > 900)process();
				if(heat > 1498)process();
				if(state.getValue(BlockRefinery.STATE) == 0)TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, BlockRefinery.STATE, 1);
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, BlockRefinery.STATE, 0);
				killList.run();
			}
		}
	}
	private void process(){
		if(tankIn.getFluidAmount() >= 5){
			int t = 2;
			if(tankIn.getFluid() != null && tankIn.getFluid().getFluid() == CoreInit.oil && heat > 500){
				if((tankOut1.getFluid() == null || (tankOut1.getFluid().getFluid() == CoreInit.fuel && tankOut1.getFluidAmount()+t <= tankOut1.getCapacity())) &&
						(tankOut2.getFluid() == null || (tankOut2.getFluid().getFluid() == CoreInit.lpg && tankOut2.getFluidAmount()+t <= tankOut2.getCapacity())) &&
						(tankOut3.getFluid() == null || (tankOut3.getFluid().getFluid() == CoreInit.kerosene && tankOut3.getFluidAmount()+(t/2) <= tankOut3.getCapacity()))){
					tankIn.drainInternal(5, true);//(heat > 900 ? heat > 1498 ? 15 : 10 : 5
					tankOut1.fillInternal(new FluidStack(CoreInit.fuel, t), true);
					tankOut2.fillInternal(new FluidStack(CoreInit.lpg, t), true);
					tankOut3.fillInternal(new FluidStack(CoreInit.kerosene, t/2), true);
				}
			}
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound tankTag = new NBTTagCompound();
		tankIn.writeToNBT(tankTag);
		compound.setTag("oil", tankTag);
		tankTag = new NBTTagCompound();
		tankOut2.writeToNBT(tankTag);
		compound.setTag("lpg", tankTag);
		tankTag = new NBTTagCompound();
		tankOut1.writeToNBT(tankTag);
		compound.setTag("fuel", tankTag);
		tankTag = new NBTTagCompound();
		tankOut3.writeToNBT(tankTag);
		compound.setTag("kerosene", tankTag);
		compound.setDouble("heat", heat);
		NBTTagList tagList = new NBTTagList();
		for (int i = 0; i < this.stack.length; i++) {
			if (this.stack[i] != null) {
				NBTTagCompound tagCompound1 = new NBTTagCompound();
				tagCompound1.setByte("Slot", (byte) i);
				this.stack[i].writeToNBT(tagCompound1);
				tagList.appendTag(tagCompound1);
			}
		}
		compound.setTag("Items", tagList);
		compound.setInteger("burnTime", burnTime);
		compound.setInteger("burnTimeMax", maxBurnTime);
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagCompound tankTag = compound.getCompoundTag("oil");
		tankIn.readFromNBT(tankTag);
		tankTag = compound.getCompoundTag("lpg");
		tankOut2.readFromNBT(tankTag);
		tankTag = compound.getCompoundTag("fuel");
		tankOut1.readFromNBT(tankTag);
		tankTag = compound.getCompoundTag("kerosene");
		tankOut3.readFromNBT(tankTag);
		heat = compound.getDouble("heat");
		NBTTagList tagList = compound.getTagList("Items", 10);
		this.stack = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tabCompound1 = tagList.getCompoundTagAt(i);
			byte byte0 = tabCompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.stack.length) {
				this.stack[byte0] = ItemStack.loadItemStackFromNBT(tabCompound1);
			}
		}
		burnTime = compound.getInteger("burnTime");
		maxBurnTime = compound.getInteger("burnTimeMax");
	}

	@Override
	public String getName() {
		return "refinery";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return stack[index];
	}

	@Override
	public ItemStack decrStackSize(int slot, int par2) {
		//if(slot == 5) return null;
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
		ItemStack s = stack[index];
		stack[index] = null;
		return s;
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
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		stack = new ItemStack[getSizeInventory()];
	}

	public double getHeat() {
		return heat;
	}

	public int getBurnTime() {
		return burnTime;
	}

	public int getMaxBurnTime() {
		return maxBurnTime;
	}

	public FluidTank getTankIn() {
		return tankIn;
	}

	public FluidTank getTankOut1() {
		return tankOut1;
	}

	public FluidTank getTankOut2() {
		return tankOut2;
	}

	public FluidTank getTankOut3() {
		return tankOut3;
	}

	public void setBurnTime(int data) {
		this.burnTime = data;
	}
}