package com.tom.api.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.config.Config;

public class TileEntityTomsMod extends TileEntityTomsModNoTicking implements ITMTickable {
	public boolean ticked = false;
	private int tickSpeedingTimer = 0;
	private long lastTickWorldTime;
	private boolean clientSpeeding;

	@Override
	public final void handleUpdateTag(final NBTTagCompound tag) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {

			@Override
			public void run() {
				clientSpeeding = tag.getBoolean("_ts");
				readFromPacket(tag);
			}
		});
	}

	@Override
	public final NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		writeToPacket(tag);
		tag.setBoolean("_ts", tickSpeedingTimer > 0);
		return tag;
	}

	public void onTicksped() {
	}

	public TickSpeedupBehaviour getTickSpeedupBehaviour() {
		return TickSpeedupBehaviour.REQUIRES_CONFIG;
	}

	@Override
	public final void update() {
		if (initLater) {
			initLater = false;
			initializeCapabilities();
		}
		long worldTime = world.getTotalWorldTime();
		if (worldTime != lastTickWorldTime) {
			ticked = false;
			lastTickWorldTime = worldTime;
		}
		if (!ticked || getTickSpeedupBehaviour() == TickSpeedupBehaviour.NORMAL || (getTickSpeedupBehaviour() == TickSpeedupBehaviour.REQUIRES_CONFIG && Config.enableTickSpeeding)) {
			ticked = true;
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() != Blocks.AIR) {
				preUpdate(state);
				this.updateEntity();
				this.updateEntity(state);
				postUpdate(state);
			}
			if (tickSpeedingTimer > 0) {
				tickSpeedingTimer--;
				if (tickSpeedingTimer == 0)
					markBlockForUpdate();
			}
		} else {
			int old = tickSpeedingTimer;
			tickSpeedingTimer = 20;
			if (old == 0) {
				markBlockForUpdate();
			}
			onTicksped();
		}
		if (world.isRemote && (tickSpeedingTimer > 0 || clientSpeeding)) {
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.25, pos.getY() + 1, pos.getZ() + 0.25, 0, 0.02F, 0);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.25, pos.getY() + 1, pos.getZ() + 0.75, 0, 0.02F, 0);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.75, pos.getY() + 1, pos.getZ() + 0.25, 0, 0.02F, 0);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.75, pos.getY() + 1, pos.getZ() + 0.75, 0, 0.02F, 0);

			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0.05F, 0);
			double sideY = pos.getY() + 0.3;
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX(), sideY, pos.getZ(), 0, 0.02F, 0);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX(), sideY, pos.getZ() + 1, 0, 0.02F, 0);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 1, sideY, pos.getZ(), 0, 0.02F, 0);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 1, sideY, pos.getZ() + 1, 0, 0.02F, 0);

			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX(), sideY, pos.getZ() + 0.5, 0, 0.02F, 0);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5, sideY, pos.getZ(), 0, 0.02F, 0);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5, sideY, pos.getZ() + 1, 0, 0.02F, 0);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 1, sideY, pos.getZ() + 0.5, 0, 0.02F, 0);
		}
	}

	public static enum TickSpeedupBehaviour {
		NORMAL, REQUIRES_CONFIG, DENY
	}

	public final boolean isTickSpeeded() {
		return tickSpeedingTimer > 0 || clientSpeeding;
	}

	@Override
	public World getWorld2() {
		return world;
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}
}
