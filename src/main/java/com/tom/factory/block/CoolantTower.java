package com.tom.factory.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockControllerBase;
import com.tom.api.tileentity.TileEntityControllerBase;
import com.tom.factory.tileentity.TileEntityCoolantTower;

public class CoolantTower extends BlockControllerBase {

	protected CoolantTower(Material p_i45386_1_) {
		super(p_i45386_1_);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityCoolantTower();
	}
	public CoolantTower(){
		this(Material.IRON);
	}

	@Override
	public void breakBlockI(World world, int x, int y, int z, IBlockState block) {

	}

	@Override
	public void onBlockPlacedByI(World world, int x, int y, int z,
			EntityLivingBase entity, ItemStack itemstack, TileEntity te) {

	}

	@Override
	public boolean onBlockActivatedI(World world, int x, int y, int z,
			EntityPlayer player, EnumFacing side, float hitX, float hitY,
			float hitZ, TileEntityControllerBase te) {
		return false;
	}

	@Override
	public void onNeighborBlockChangeI(IBlockAccess world, int x, int y, int z, IBlockState b, Block block) {

	}
}
