package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;

import com.tom.core.tileentity.TileEntityCommandExecutor;

public class CommandExecutor extends BlockContainerTomsMod {

	public CommandExecutor() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityCommandExecutor();
	}

}
