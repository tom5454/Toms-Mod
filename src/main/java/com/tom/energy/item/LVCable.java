package com.tom.energy.item;

import com.tom.api.item.MultipartItem;
import com.tom.energy.multipart.PartLVCable;

import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LVCable extends MultipartItem {

	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side,
			Vec3d hit, ItemStack stack, EntityPlayer player) {
		return new PartLVCable();
	}

}
