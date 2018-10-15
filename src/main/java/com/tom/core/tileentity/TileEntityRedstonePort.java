package com.tom.core.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.api.tileentity.ITMPeripheral.ITMCompatPeripheral;

public class TileEntityRedstonePort extends TileEntityTomsMod implements ITMCompatPeripheral {
	// private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	public boolean redstone = false;
	public int compRedstone = 0;

	@Override
	public String getType() {
		return "tm_rsPort";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"setOutput", "getInput", "setComparatorOutput", "getOutput", "getComparatorOutput"};
	}

	@Override
	public Object[] callMethod(IComputer computer, int method, Object[] a) throws LuaException {
		if (method == 0) {
			if (a.length > 0 && a[0] instanceof Boolean) {
				this.redstone = (Boolean) a[0];
			} else {
				throw new LuaException("Invalid Argument, Boolean exepted");
			}
		} else if (method == 1) {
			return new Object[]{world.isBlockIndirectlyGettingPowered(pos)};
		} else if (method == 2) {
			if (a.length > 0 && a[0] instanceof Double) {
				this.compRedstone = MathHelper.floor((Double) a[0]);
			} else {
				throw new LuaException("Invalid Argument, number exepted");
			}
		} else if (method == 3) {
			return new Object[]{this.redstone};
		} else if (method == 4) { return new Object[]{this.compRedstone}; }
		this.markDirty();
		markBlockForUpdate(pos);
		// this.worldObj.markBlockRangeForRenderUpdate(xCoord+1, yCoord+1,
		// zCoord+1, xCoord-1, yCoord-1, zCoord-1);
		this.world.notifyNeighborsOfStateChange(pos, blockType, true);
		/*this.worldObj.markBlockForUpdate(xCoord, yCoord+1, zCoord);
		this.worldObj.markBlockForUpdate(xCoord, yCoord-1, zCoord);
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord+1);
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord-1);
		this.worldObj.markBlockForUpdate(xCoord+1, yCoord, zCoord);
		this.worldObj.markBlockForUpdate(xCoord-1, yCoord, zCoord);*/

		return null;
	}

	@Override
	public void attach(IComputer computer) {
		// computers.add(computer);
	}

	@Override
	public void detach(IComputer computer) {
		// computers.remove(computer);
	}

	public int getComparatorOutput() {
		return this.compRedstone < 15 ? this.compRedstone > 0 ? this.compRedstone : 0 : 15;
	}

	public boolean getOutput() {
		return this.redstone;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.compRedstone = tag.getInteger("comp");
		this.redstone = tag.getBoolean("rs");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("comp", this.compRedstone);
		tag.setBoolean("rs", this.redstone);
		return tag;
	}
}
