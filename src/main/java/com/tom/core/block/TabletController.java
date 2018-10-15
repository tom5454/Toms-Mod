package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.CoreInit;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.TileEntityTabletController;

public class TabletController extends BlockContainerTomsMod {
	protected TabletController(Material p_i45386_1_) {
		super(p_i45386_1_);
	}

	public TabletController() {
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityTabletController();
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.getHeldItem(hand).getItem() == CoreInit.Tablet){
			if(!worldIn.isRemote){
				if(!playerIn.getHeldItem(hand).hasTagCompound())playerIn.getHeldItem(hand).setTagCompound(new NBTTagCompound());
				NBTTagCompound tag = playerIn.getHeldItem(hand).getTagCompound();
				if(tag.getBoolean("connected") && tag.getInteger("x") == pos.getX() && tag.getInteger("y") == pos.getY() && tag.getInteger("z") == pos.getZ()){
					tag.setBoolean("connected", false);
					TomsModUtils.sendNoSpamTranslate(playerIn, "tomsMod.tabletUnlinked");
				}else{
					tag.setInteger("x", pos.getX());
					tag.setInteger("y", pos.getY());
					tag.setInteger("z", pos.getZ());
					tag.setBoolean("connected", true);
					TomsModUtils.sendNoSpamTranslate(playerIn, "tomsMod.tabletLinked");
				}
			}
			return true;
		}
		return false;
	}
}
