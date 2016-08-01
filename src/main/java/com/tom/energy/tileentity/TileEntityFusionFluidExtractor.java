package com.tom.energy.tileentity;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;
import com.tom.energy.block.FusionFluidExtractor;
import com.tom.lib.Configs;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityFusionFluidExtractor extends TileEntityTomsMod implements ITileFluidHandler{
	private final FluidTank tank = new FluidTank(Configs.BASIC_TANK_SIZE);

	//	@Override
	//	public boolean canDrain(EnumFacing arg0, Fluid arg1) {
	//		return this.isValidOutputSide(arg0) && arg1 == CoreInit.plasma && this.tank.getFluidAmount() > 0;
	//	}
	//
	//	@Override
	//	public boolean canFill(EnumFacing arg0, Fluid arg1) {
	//		return false;
	//	}
	//
	//	@Override
	//	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
	//		return this.isValidOutputSide(from) && tank.getFluid() != null && tank.getFluid().isFluidEqual(resource) ? tank.drain(resource.amount, doDrain): null;
	//	}
	//
	//	@Override
	//	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
	//		return this.isValidOutputSide(from) ? tank.drain(maxDrain, doDrain) : null;
	//	}
	//
	//	@Override
	//	public int fill(EnumFacing arg0, FluidStack arg1, boolean arg2) {
	//		return 0;
	//	}
	//
	//	@Override
	//	public FluidTankInfo[] getTankInfo(EnumFacing arg0) {
	//		return this.isValidOutputSide(arg0) ? new FluidTankInfo[]{new FluidTankInfo(this.tank)} : null;
	//	}
	private boolean isValidOutputSide(EnumFacing current){
		/*int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		Block block1 = worldObj.getBlockState(new BlockPos(x+2,y,z)).getBlock();
		Block block2 = worldObj.getBlockState(new BlockPos(x-2,y,z)).getBlock();
		Block block3 = worldObj.getBlockState(new BlockPos(x,y,z+2)).getBlock();
		Block block4 = worldObj.getBlockState(new BlockPos(x+1,y,z)).getBlock();
		Block block5 = worldObj.getBlockState(new BlockPos(x-1,y,z)).getBlock();
		Block block6 = worldObj.getBlockState(new BlockPos(x,y,z+1)).getBlock();
		boolean b1 = block1 == EnergyInit.FusionFluidExtractor && block4 == EnergyInit.FusionCore,
				b2 = block2 == EnergyInit.FusionFluidExtractor && block5 == EnergyInit.FusionCore,
				b3 = block3 == EnergyInit.FusionFluidExtractor && block6 == EnergyInit.FusionCore;
		EnumFacing ret = b1 ? EnumFacing.SOUTH : (b2 ? EnumFacing.NORTH : (b3 ? EnumFacing.WEST : EnumFacing.EAST));*/
		IBlockState state = worldObj.getBlockState(pos);
		if(state.getBlock() != Blocks.AIR){
			EnumFacing ret = state.getValue(FusionFluidExtractor.FACING);
			boolean r = ret == current;
			return r;
		}else{
			return false;
		}
	}
	public void add(){
		this.add(1);
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		tag.setTag("Tank", tankTag);
		//tag.setInteger("fluid", this.tank.getFluidAmount());
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		tank.readFromNBT(tag.getCompoundTag("Tank"));
		//this.tank.drain(this.tank.getFluidAmount(), true);
		//this.tank.fill(new FluidStack(CoreInit.fusionFuel, tag.getInteger("fluid")), true);
	}
	public int getAmount(){
		return this.tank.getFluidAmount();
	}

	public void add(int a) {
		FluidStack fluid;
		if(this.tank.getFluid() != null){
			fluid = this.tank.getFluid();
			fluid.amount = fluid.amount + a;
		}else{
			if (CoreInit.plasma == null){
				System.err.println("ERROR: Plasma is null");
			}
			fluid = new FluidStack(CoreInit.plasma, a);
		}
		this.tank.fill(fluid, true);
	}
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return this.isValidOutputSide(f) ? tank : null;
	}
}
