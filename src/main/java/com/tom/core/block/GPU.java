package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.lib.Configs;

import com.tom.core.tileentity.TileEntityGPU;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT)
public class GPU extends BlockContainerTomsMod implements IPeripheralProvider {

	/*@SideOnly(Side.CLIENT)
	private IIcon top;
	@SideOnly(Side.CLIENT)
	private IIcon bottom;
	/*@SideOnly(Side.CLIENT)
	private IIcon GpuCable;
	@SideOnly(Side.CLIENT)*/
	/*private IIcon front;
	/*@SideOnly(Side.CLIENT)
	private IIcon energy;
	@SideOnly(Side.CLIENT)
	private IIcon computer;
	public boolean powered;*/

	protected GPU(Material material) {
		super(material);
	}

	public GPU() {
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:gpuFront");
		this.top = iconregister.registerIcon("minecraft:gpuTop");
		this.bottom = iconregister.registerIcon("minecraft:gpuBottom");
		//this.GpuCable = iconregister.registerIcon("minecraft:gpuCable");
		this.front = iconregister.registerIcon("minecraft:gpuFront");
		//this.energy = iconregister.registerIcon(powered ? "minecraft:gpuEnergyOn" : "minecraft:gpuEnergyOff");
		//this.computer = iconregister.registerIcon("minecraft:gpuComputer");
	}
	
	public IIcon getIcon(int side,int meta){
		if(side == 1){
			return top;
		}else if(side == 3){
			return front;
		}else if(side == 0){
			return this.bottom;
		/*}else if(side == 4){
			return this.computer;
		}else if(side == 2){
			return this.energy;
		}else if(side == 5){
			return this.GpuCable;*//*
									}else{
									return this.blockIcon;
									}
									}*/
	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityGPU();
	}

	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		return side != EnumFacing.UP && te instanceof TileEntityGPU ? (IPeripheral) te : null;
	}

}
