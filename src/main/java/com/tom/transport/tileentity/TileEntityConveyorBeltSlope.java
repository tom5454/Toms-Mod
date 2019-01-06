package com.tom.transport.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.items.IItemHandler;

import com.tom.transport.block.ConveyorBeltSlope;
import com.tom.util.TomsModUtils;

public class TileEntityConveyorBeltSlope extends TileEntityConveyorBase implements IConveyorSlope {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tomsmodtransport:textures/models/conveyormodel.png");

	@Override
	public boolean pushItemOut(IBlockState currentState) {
		boolean sync = false;
		boolean down = currentState.getValue(ConveyorBeltSlope.IS_DOWN_SLOPE);
		EnumFacing f = down ? getFacing().getOpposite() : getFacing();
		BlockPos p = down ? pos.offset(f) : pos.offset(f).up();
		IItemHandler h = TomsModUtils.getItemHandler(world, p, f.getOpposite(), true);
		if (h != null) {
			inv.setInventorySlotContents(0, TomsModUtils.putStackInInventoryAllSlots(h, inv.getStackInSlot(0)));
			if (inv.getStackInSlot(0).isEmpty()) {
				sync = true;
				itemPos = 0;
			}
		} else {
			TileEntity t = world.getTileEntity(pos.offset(f).down());
			if (t instanceof IConveyorSlope) {
				IConveyorSlope c = (IConveyorSlope) t;
				if (c.isValid()) {
					inv.setInventorySlotContents(0, c.insert(inv.getStackInSlot(0), f.getOpposite()));
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
	public int getSpeed() {
		return 4;
	}

	@Override
	public ResourceLocation getTexture() {
		return TEXTURE;
	}

	@Override
	public boolean isValid() {
		return world.getBlockState(pos).getValue(ConveyorBeltSlope.IS_DOWN_SLOPE);
	}

	@Override
	public ItemStack insert(ItemStack stack, EnumFacing side) {
		EnumFacing facing = getFacing();
		if (facing == side && inv.getStackInSlot(0).isEmpty() && inv.getStackInSlot(1).isEmpty()) {
			stack = stack.copy();
			if (stack.getCount() > 1) {
				stack.shrink(1);
				ItemStack s = stack.copy();
				s.setCount(1);
				inv.setInventorySlotContents(0, s);
				return stack;
			} else {
				inv.setInventorySlotContents(0, stack.copy());
				return ItemStack.EMPTY;
			}
		}
		return stack;
	}

	@Override
	public int getPowerUse() {
		return 3;
	}
}
