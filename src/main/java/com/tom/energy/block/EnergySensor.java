package com.tom.energy.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.energy.tileentity.TileEntityEnergySensor;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergySensor extends BlockContainerTomsMod {
	/*@SideOnly(Side.CLIENT)
	protected IIcon side;
	@SideOnly(Side.CLIENT)
	protected IIcon front;
	@SideOnly(Side.CLIENT)
	protected IIcon back;*/
	protected EnergySensor(Material arg0) {
		super(arg0);
	}
	public EnergySensor(){
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityEnergySensor();
	}
	/*@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b){
		TileEntityEnergySensor te = (TileEntityEnergySensor)world.getTileEntity(x, y, z);
		te.onNeibourChange();
	}*/
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState bs, EntityLivingBase entity, ItemStack itemstack){
		boolean shift = entity.isSneaking();
		TileEntity te = world.getTileEntity(pos);
		TileEntityEnergySensor te2 = (TileEntityEnergySensor) te;
		EnumFacing dir = shift ? TomsModUtils.getDirectionFacing(entity, true).getOpposite() : TomsModUtils.getDirectionFacing(entity, true);
		int d = dir.ordinal();
		int d2 = dir.getOpposite().ordinal();
		if (d == 0) te2.d = 5;
		else if(d == 1) te2.d = 4;
		else if(d == 2) te2.d = 0;
		else if(d == 3) te2.d = 1;
		else if(d == 4) te2.d = 2;
		else if(d == 5) te2.d = 3;
		te2.direction2 = d;
		te2.directionO2 = d2;
		if (d == 5) te2.direction = 4;
		else if(d == 4) te2.direction = 5;
		else if(d == 3) te2.direction = 2;
		else if(d == 2) te2.direction = 3;
		else if(d == 0) te2.direction = 0;
		else if(d == 1) te2.direction = 1;
		if (d2 == 5) te2.directionO = 4;
		else if(d2 == 4) te2.directionO = 5;
		else if(d2 == 3) te2.directionO = 2;
		else if(d2 == 2) te2.directionO = 3;
		else if(d2 == 0) te2.directionO = 0;
		else if(d2 == 1) te2.directionO = 1;
	}
	/*public void registerBlockIcons(IIconRegister i){
		this.blockIcon = i.registerIcon("minecraft:mbf");
		this.side = i.registerIcon("minecraft:mbf");
		this.front = i.registerIcon("minecraft:energySensorOut");
		this.back = i.registerIcon("minecraft:energySensorIn");
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityEnergySensor te = (TileEntityEnergySensor)world.getTileEntity(x, y, z);
		if(side == te.direction){
			return this.front;
		}else if(side == te.directionO){
			return this.back;
		}else{
			return this.side;
		}
	}*/
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(!world.isRemote){
			TileEntity tilee = world.getTileEntity(pos);
			if(tilee instanceof TileEntityEnergySensor){
				TileEntityEnergySensor te = (TileEntityEnergySensor)tilee;
				if(heldItem != null && CoreInit.isWrench(heldItem,player)){
					if(player.isSneaking()){
						te.energyRate--;
						if(te.energyRate == 0){
							te.energyRate = 6;
						}
					}else{
						te.energyRate++;
						if(te.energyRate == 7){
							te.energyRate = 1;
						}
					}
					te.markDirty();
					return true;
				}
			}
		}
		return true;
	}
	@Override
	public boolean hasComparatorInputOverride(IBlockState s) {
		return true;
	}
	@Override
	public int getComparatorInputOverride(IBlockState s, World world, BlockPos pos) {
		return ((TileEntityEnergySensor)world.getTileEntity(pos)).comparator;
	}
}
