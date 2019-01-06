package com.tom.energy.tileentity;

import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.block.IGridPowerGenerator;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.handler.TMPlayerHandler;

public class TileEntityCreativeGridPowerSource extends TileEntityTomsMod implements IGridPowerGenerator {
	protected TMPlayerHandler playerHandler;
	public String playerName;
	@Override
	public boolean isValid() {
		return !isInvalid();
	}

	@Override
	public long getMaxPowerGen() {
		return Integer.MAX_VALUE;
	}
	@Override
	public void updateEntity() {
		if(!world.isRemote && playerHandler != null){
			playerHandler.gridPower = 0;
			playerHandler.gridPowerGenerators.add(this);
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("placer", playerName);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		playerName = compound.getString("placer");
	}
	@Override
	public void onLoad() {
		tileOnLoad();
	}

	@Override
	public void updatePlayerHandler() {
		playerHandler = TMPlayerHandler.getPlayerHandlerForName(playerName);
	}

	@Override
	public String getOwnerName() {
		return playerName;
	}

	@Override
	public void setOwner(String owner) {
		this.playerName = owner;
	}
}
