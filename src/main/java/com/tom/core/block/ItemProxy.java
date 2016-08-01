package com.tom.core.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.CoreInit;
import com.tom.core.tileentity.TileEntityItemProxy;
import com.tom.handler.GuiHandler;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemProxy extends BlockContainerTomsMod {
	/*@SideOnly(Side.CLIENT)
	private IIcon bottom;
	@SideOnly(Side.CLIENT)
	private IIcon top;*/
	public ItemProxy() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityItemProxy();
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
		this.blockIcon = i.registerIcon("minecraft:tm/ItemProxySide");
		this.top = i.registerIcon("minecraft:tm/ItemProxyTop");
		this.bottom = i.registerIcon("minecraft:tm/ItemProxyBottom");
	}*/
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		TileEntityItemProxy te = (TileEntityItemProxy)world.getTileEntity(pos);
		boolean playerAccess = te.isPlayerAccess(player);
		if(!world.isRemote && playerAccess){
			player.openGui(CoreInit.modInstance, GuiHandler.GuiIDs.itemProxy.ordinal(), world, pos.getX(),pos.getY(),pos.getZ());
		}else if(world.isRemote && !playerAccess){
			player.addChatMessage(new TextComponentString(TextFormatting.RED + I18n.format("tomsMod.accessDenied")));
		}
		return true;
	}
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState bs, EntityLivingBase entity, ItemStack itemstack){
		TileEntityItemProxy te = (TileEntityItemProxy)world.getTileEntity(pos);
		String pName = entity.getName();
		//System.out.println(pName);
		te.owner = pName;
	}
}
