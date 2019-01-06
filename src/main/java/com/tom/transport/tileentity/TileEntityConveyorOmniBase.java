package com.tom.transport.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.items.IItemHandler;

import com.tom.transport.block.ConveyorBeltOmniBase;
import com.tom.util.TomsModUtils;

public abstract class TileEntityConveyorOmniBase extends TileEntityConveyorBase {
	public EnumFacing facing = EnumFacing.DOWN;

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return facing == side ? FRONT : facing.getOpposite() == side ? SLOT : SIDE;
	}

	@Override
	public boolean pushItemOut(IBlockState currentState) {
		boolean sync = false;
		IItemHandler h = TomsModUtils.getItemHandler(world, pos.offset(facing), facing.getOpposite(), true);
		if (h != null) {
			inv.setInventorySlotContents(0, TomsModUtils.putStackInInventoryAllSlots(h, inv.getStackInSlot(0)));
			if (inv.getStackInSlot(0).isEmpty()) {
				sync = true;
				itemPos = 0;
			}
		} else {
			TileEntity t = world.getTileEntity(pos.offset(facing).down());
			if (t instanceof IConveyorSlope) {
				IConveyorSlope c = (IConveyorSlope) t;
				if (c.isValid()) {
					inv.setInventorySlotContents(0, c.insert(inv.getStackInSlot(0), facing.getOpposite()));
					if (inv.getStackInSlot(0).isEmpty()) {
						sync = true;
						itemPos = 0;
					}
				}
			}
		}
		return sync;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("facing", facing.ordinal());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		facing = EnumFacing.getFront(compound.getInteger("facing"));
	}

	@Override
	public void writeToPacket(NBTTagCompound tag) {
		super.writeToPacket(tag);
		tag.setInteger("f", facing.ordinal());
	}

	@Override
	public void readFromPacket(NBTTagCompound tag) {
		super.readFromPacket(tag);
		facing = EnumFacing.getFront(tag.getInteger("f"));
	}

	@Override
	public EnumFacing getFacing() {
		return world.getBlockState(pos).getValue(ConveyorBeltOmniBase.POSITION);
	}
}
