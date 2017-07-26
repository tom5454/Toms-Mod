package com.tom.api.multipart;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;

import com.tom.api.tileentity.TileEntityTomsModNoTicking;

import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.ref.MCMPCapabilities;

public class MultipartTomsMod extends TileEntityTomsModNoTicking implements IMultipartTile {
	public void sendUpdatePacket() {
		markBlockForUpdate();
	}

	public void onNeighborTileChange(boolean force) {

	}

	public void markRenderUpdate() {
		world.markBlockRangeForRenderUpdate(pos, pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapabilityI(Capability<T> capability, EnumFacing facing) {
		return capability == MCMPCapabilities.MULTIPART_TILE ? (T) this : super.getCapabilityI(capability, facing);
	}

	@Override
	public boolean hasCapabilityI(Capability<?> capability, EnumFacing facing) {
		return capability == MCMPCapabilities.MULTIPART_TILE || super.hasCapabilityI(capability, facing);
	}
}
