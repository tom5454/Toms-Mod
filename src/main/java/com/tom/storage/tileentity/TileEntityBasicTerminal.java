package com.tom.storage.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.multipart.StorageNetworkGrid.ITerminal;

public class TileEntityBasicTerminal extends
TileEntityGridDeviceBase<StorageNetworkGrid> implements ITerminal{
	public boolean poweredClient = false;
	private boolean poweredLast = false;
	public int terminalMode = 0;
	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}
	@Override
	public void updateEntity(IBlockState currentState) {
		if(!worldObj.isRemote){
			grid.drainEnergy(1);
			poweredClient = grid.isPowered();
			if(poweredLast != poweredClient){
				poweredLast = poweredClient;
				markBlockForUpdate(pos);
				worldObj.checkLight(pos);
			}
		}
	}
	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setBoolean("p", poweredClient);
	}
	@Override
	public void readFromPacket(NBTTagCompound buf) {
		poweredClient = buf.getBoolean("p");
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
		worldObj.checkLight(pos);
	}
	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if(id == 0)terminalMode = extra % 3;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("termMode", terminalMode);
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		terminalMode = compound.getInteger("termMode");
	}
	@Override
	public int getTerminalMode() {
		return terminalMode;
	}
	@Override
	public void setClientPowered(boolean powered) {
		poweredClient = powered;
	}
	@Override
	public boolean getClientPowered() {
		return poweredClient;
	}
}
