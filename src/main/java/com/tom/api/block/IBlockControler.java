package com.tom.api.block;

import com.tom.api.tileentity.TileEntityControllerBase;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IBlockControler {
	/**onBlockPlacedBy(World world, int x, int y, int z,EntityLivingBase entity, ItemStack itemstack){*/
	void onBlockPlacedByI(World world, int x, int y, int z,EntityLivingBase entity, ItemStack itemstack, TileEntity te);
	/**onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)*/
	boolean onBlockActivatedI(World world, int x, int y, int z, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ, TileEntityControllerBase te);
}
