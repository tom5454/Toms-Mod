package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;

import com.tom.core.tileentity.TileEntityEnderMemory;

public class EnderMemory extends BlockContainerTomsMod {

	protected EnderMemory(Material arg0) {
		super(arg0);
	}

	public EnderMemory() {
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityEnderMemory();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState bs, EntityLivingBase entity, ItemStack itemstack) {
		TileEntityEnderMemory te = (TileEntityEnderMemory) world.getTileEntity(pos);
		String pName = entity.getName();
		te.playerName = pName;
	}

}
