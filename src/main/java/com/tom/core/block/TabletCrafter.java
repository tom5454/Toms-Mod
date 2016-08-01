package com.tom.core.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.CoreInit;
import com.tom.core.tileentity.TileEntityTabletCrafter;
import com.tom.handler.GuiHandler;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TabletCrafter extends BlockContainerTomsMod {

	protected TabletCrafter(Material p_i45386_1_) {
		super(p_i45386_1_);
	}
	/*@SideOnly(Side.CLIENT)
	private IIcon bottom;
	@SideOnly(Side.CLIENT)
	private IIcon top;*/
	public TabletCrafter(){
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityTabletCrafter();
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(!world.isRemote){
			player.openGui(CoreInit.modInstance, GuiHandler.GuiIDs.tabletCrafter.ordinal(), world, pos.getX(),pos.getY(),pos.getZ());
		}
		return true;
	}
	/*public IIcon getIcon(int side,int meta){
		if(side == 0){
			return this.bottom;
		}else if(side == 1){
			return this.top;
		}else{
			return this.blockIcon;
		}
	}
	public void registerBlockIcons(IIconRegister i){
		this.blockIcon = i.registerIcon("minecraft:tm/tabletCrafter_side");
		this.top = i.registerIcon("minecraft:tm/tabletCrafter_top");
		this.bottom = i.registerIcon("minecraft:iron_block");
	}*/

}
