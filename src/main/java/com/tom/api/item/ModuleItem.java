package com.tom.api.item;

import com.tom.api.multipart.PartModule;

import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ModuleItem extends MultipartItem {
	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side,
			Vec3d hit, ItemStack stack, EntityPlayer player) {
		return this.createPart(side);
	}
	public abstract PartModule<?> createPart(EnumFacing side);
}
