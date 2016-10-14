package com.tom.factory.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityControllerBase;
import com.tom.recipes.handler.ElectrolyzerRecipesHandler;

public class TileEntityElectrolyzer extends TileEntityControllerBase {
	protected TileEntityElectrolyzer(int w, int h) {
		super(w, h);
	}
	public TileEntityElectrolyzer(){
		this(3,4);
		this.parts.add(MultiblockPartList.EnergyPort);
		this.parts.add(MultiblockPartList.FluidPort);
	}
	protected List<MultiblockPartList> parts = new ArrayList<MultiblockPartList>();
	@Override
	public List<MultiblockPartList> parts() {
		return this.parts;
	}
	
	@Override
	public void updateEntityI() {
		int[][] tePartEnergy = this.getTileEntityList(MultiblockPartList.EnergyPort);
		int m = 1;
		TileEntityMBFluidPort fInPort = this.getTileEntityList(true);
		int[][] fOutPort = this.getFluidOutput();
		if(fInPort != null && fOutPort != null){
			if(fInPort != null && fOutPort != null){
				int[] c = tePartEnergy[0];
				TileEntityMBEnergyPort te = (TileEntityMBEnergyPort) worldObj.getTileEntity(new BlockPos(c[0],c[1],c[2]));
				int[][] fluids = fOutPort;
				//System.out.println("updateO");
				if (ElectrolyzerRecipesHandler.processable(fInPort.getFluidStack(), te.getEnergyStored(), /*fOutPort[0] != null ? fOutPort[0].getFluidStack() : null, fOutPort[1] != null ? fOutPort[1].getFluidStack() : null, fOutPort[2] != null ? fOutPort[2].getFluidStack() : null, fOutPort[3] != null ? fOutPort[3].getFluidStack() : null,*/fluids[0],fluids[1],fluids[2],fluids[3],m, this.worldObj)) {
					//System.out.println("update");
					/*if(fOutPort.getFluid() == null || fOutPort.getFluid() == CoreInit.Hydrogen){
						if(tePartEnergy[0] != null){
							int[] c = tePartEnergy[0];
							TileEntityMBEnergyPort te = (TileEntityMBEnergyPort) worldObj.getTileEntity(c[0],c[1],c[2]);
							if(te.getEnergyStored() > 20){
								te.removeEnergy(20, false);
								fInPort.drain(3);
								if(fOutPort.getFluid() != null){
									fOutPort.fill(2);
								}else{
									fOutPort.fill(2, CoreInit.Hydrogen);
								}
								this.active = true;
							}else{
								this.active = false;
							}
						}else{
							this.active = false;
						}
					}else{
						this.active = false;
					}
					FluidStack input = fInPort.getFluidStack();
					ElectrolyzerRecipesHandler.get(input, 1);*/
					FluidStack input = fInPort.getFluidStack();
					int[] output = ElectrolyzerRecipesHandler.process(input, m, fluids[0],fluids[1],fluids[2],fluids[3], te.getEnergyStored(), true, this.worldObj);
					Fluid[] outputFluid = ElectrolyzerRecipesHandler.getFluid(input);
					fInPort.drain(output[0]);
					te.removeEnergy(ElectrolyzerRecipesHandler.getEnergyUsage(input, m), false);
					//System.out.println("process");
					for(int i = 0;i<fOutPort.length && i<4;i++){
						int[] currentCoords = fOutPort[i];
						TileEntityMBFluidPort current = (TileEntityMBFluidPort) this.worldObj.getTileEntity(new BlockPos(currentCoords[0], currentCoords[1], currentCoords[2]));
						if(current != null){
							//System.out.println("process");
							int a = output[i+1];
							if(a > 0){
								if(current.getFluidStack() != null) current.fill(a);
								else current.fill(a, outputFluid[i]);
							}
						}
					}
				}else{
					this.active = false;
				}
			}else{
				this.active = false;
			}
		}else{
			this.active = false;
		}
	}

	@Override
	public void validateI() {
		
	}

	@Override
	public void receiveMessage(int x, int y, int z, byte msg) {
		
	}
	@Override
	public void formI(int mX, int mY, int mZ) {
		
	}
	@Override
	public void deFormI(int mX, int mY, int mZ) {
		
	}
	@Override
	public void updateEntity(boolean redstone) {
		
	}

}
