package com.tom.api.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITMTickable extends ITickable {
	public default void updateEntity() {
	}

	public default void updateEntity(IBlockState currentState) {
	}

	public default void preUpdate(IBlockState state) {
	}

	public default void postUpdate(IBlockState state) {
	}

	public World getWorld2();

	public BlockPos getPos2();

	@Override
	default void update() {
		IBlockState state = getWorld2().getBlockState(getPos2());
		if (state.getBlock() != Blocks.AIR) {
			if (this instanceof TileEntityTomsModNoTicking) {
				TileEntityTomsModNoTicking t = (TileEntityTomsModNoTicking) this;
				if (t.initLater) {
					t.initLater = false;
					t.initializeCapabilities();
				}
			}
			preUpdate(state);
			this.updateEntity();
			this.updateEntity(state);
			postUpdate(state);
		}
	}
}
