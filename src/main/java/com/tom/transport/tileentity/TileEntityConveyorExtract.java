package com.tom.transport.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.items.IItemHandler;

import com.tom.util.TomsModUtils;

public class TileEntityConveyorExtract extends TileEntityConveyorOmniBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tomsmodtransport:textures/models/conveyormodel.png");
	@Override
	public int getPowerUse() {
		return 10;
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
	public boolean tryPull(IBlockState currentState) {
		IItemHandler h = TomsModUtils.getItemHandler(world, pos.offset(facing.getOpposite()), facing, true);
		if (h != null) {
			for (int i = 0;i < h.getSlots();i++) {
				if (!h.extractItem(i, 1, true).isEmpty()) {
					ItemStack pulledStack = h.extractItem(i, 1, false);
					if (!pulledStack.isEmpty()) {
						inv.setInventorySlotContents(0, pulledStack);
						return true;
					}
				}
			}
		}
		return false;
	}
	@Override
	public boolean isTickerValid() {
		return !isInvalid() && playerHandler != null;
	}
}
