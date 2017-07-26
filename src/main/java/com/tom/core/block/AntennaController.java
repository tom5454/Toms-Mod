package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.lib.Configs;

import com.tom.core.tileentity.TileEntityAntennaController;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT)
public class AntennaController extends BlockContainerTomsMod implements IPeripheralProvider {

	protected AntennaController(Material p_i45386_1_) {
		super(p_i45386_1_);
	}

	public AntennaController() {
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityAntennaController();
	}

	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing f) {
		TileEntity te = world.getTileEntity(pos);
		return te instanceof TileEntityAntennaController ? (IPeripheral) te : null;
	}

}
