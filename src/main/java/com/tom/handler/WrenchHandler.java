package com.tom.handler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.ISecuredTileEntity;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.util.TomsModUtils;

public class WrenchHandler {
	public static boolean use(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float a, float b, float c, EnumHand hand) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof ISecuredTileEntity) {
			boolean canAccess = true;
			BlockPos securityStationPos = ((ISecuredTileEntity) tile).getSecurityStationPos();
			if (securityStationPos != null) {
				TileEntity tileentity = world.getTileEntity(securityStationPos);
				if (tileentity instanceof ISecurityStation) {
					ISecurityStation te = (ISecurityStation) tileentity;
					canAccess = te.canPlayerAccess(AccessType.CONFIGURATION, player);
				}
			}
			if (canAccess) {
				if (player.isSneaking()) {
					return useSneak(itemStack, player, world, pos, side, a, b, c, hand);
				} else {
					// if(false){
					// return useCrafting(itemStack, player, world, pos, side,
					// a, b, c);
					// }else{
					return useNormal(itemStack, player, world, pos, side, a, b, c);
					// }
				}
			} else {
				TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
				return false;
			}
		} else {
			if (player.isSneaking()) {
				return useSneak(itemStack, player, world, pos, side, a, b, c, hand);
			} else {
				// if(false){
				// return useCrafting(itemStack, player, world, pos, side, a, b,
				// c);
				// }else{
				return useNormal(itemStack, player, world, pos, side, a, b, c);
				// }
			}
		}
	}

	private static boolean useSneak(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float a, float b, float c, EnumHand hand) {
		IBlockState bl = world.getBlockState(pos);
		return bl.getBlock().onBlockActivated(world, pos, bl, player, hand, side, a, b, c);
	}

	private static boolean useNormal(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float a, float b, float c) {
		return false;
	}
	/*private static boolean useCrafting(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float a, float b, float c){
		boolean ret = false;
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();
		Block block11 = world.getBlockState(new BlockPos(x+1, y+1, z+1)).getBlock();
		Block block21 = world.getBlockState(new BlockPos(x+1, y+1, z-1)).getBlock();
		Block block31 = world.getBlockState(new BlockPos(x-1, y+1, z+1)).getBlock();
		Block block41 = world.getBlockState(new BlockPos(x-1, y+1, z-1)).getBlock();
		Block block12 = world.getBlockState(new BlockPos(x+1, y+2, z+1)).getBlock();
		Block block22 = world.getBlockState(new BlockPos(x+1, y+2, z-1)).getBlock();
		Block block32 = world.getBlockState(new BlockPos(x-1, y+2, z+1)).getBlock();
		Block block42 = world.getBlockState(new BlockPos(x-1, y+2, z-1)).getBlock();
		boolean block1 = block11 == block21 && block31 == block21 && block21 == block41 && block41 != null && block41 != Blocks.air;
		boolean block2 = block12 == block22 && block32 == block22 && block22 == block42 && block42 != null && block42 != Blocks.air;
		boolean block = block1 && block2;
		if(block){
			ret = MultiblockCrafterRecipeHandler.craft(world, block11, block12, player, x, y, z);
		}
		return ret;
	}*/
}
