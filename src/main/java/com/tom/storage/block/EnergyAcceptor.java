package com.tom.storage.block;

import com.tom.api.block.BlockGridDevice;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.tileentity.TileEntityEnergyAcceptor;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class EnergyAcceptor extends BlockGridDevice {

	public EnergyAcceptor() {
		super(Material.IRON);
	}

	@Override
	public TileEntityGridDeviceBase<StorageNetworkGrid> createNewTileEntity(World worldIn, int meta) {
		return new TileEntityEnergyAcceptor();
	}

}
