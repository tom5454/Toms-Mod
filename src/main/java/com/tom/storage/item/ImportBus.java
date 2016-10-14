package com.tom.storage.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.tom.api.item.MultipartItem;
import com.tom.storage.multipart.PartImportBus;

import mcmultipart.multipart.IMultipart;

public class ImportBus extends MultipartItem {

	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side,
			Vec3d hit, ItemStack stack, EntityPlayer player) {
		return new PartImportBus(side);
	}

}
