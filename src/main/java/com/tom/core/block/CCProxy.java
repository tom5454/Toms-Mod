package com.tom.core.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.tileentity.TileEntityCCProxy;
import com.tom.lib.Configs;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT)
public class CCProxy extends BlockContainerTomsMod implements IPeripheralProvider {
	/*@SideOnly(Side.CLIENT)
	protected IIcon side;
	@SideOnly(Side.CLIENT)
	protected IIcon front;
	@SideOnly(Side.CLIENT)
	protected IIcon back;*/
	protected CCProxy(Material arg0) {
		super(arg0);
	}
	public CCProxy(){
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityCCProxy();
	}
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing f) {
		TileEntityCCProxy te = (TileEntityCCProxy)world.getTileEntity(pos);
		return te;
	}
	/*@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState b,Block block){
		TileEntityCCProxy te = (TileEntityCCProxy)world.getTileEntity(pos);
		te.onNeibourChange();
	}*/
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState bs, EntityLivingBase entity, ItemStack itemstack){
		boolean shift = entity.isSneaking();
		TileEntity te = world.getTileEntity(pos);
		TileEntityCCProxy te2 = (TileEntityCCProxy) te;
		EnumFacing dir = shift ? TomsModUtils.getDirectionFacing(entity, true).getOpposite() : TomsModUtils.getDirectionFacing(entity, true);
		int d = dir.ordinal();
		int d2 = dir.getOpposite().ordinal();
		if (d == 0) te2.d = 5;
		else if(d == 1) te2.d = 4;
		else if(d == 2) te2.d = 0;
		else if(d == 3) te2.d = 1;
		else if(d == 4) te2.d = 2;
		else if(d == 5) te2.d = 3;
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
		te2.direction2 = d;
		te2.directionO2 = d2;
	}
	/*public void registerBlockIcons(IIconRegister i){
		this.blockIcon = i.registerIcon("minecraft:mbf");
		this.side = i.registerIcon("minecraft:mbf");
		this.front = i.registerIcon("minecraft:ccProxyFront");
		this.back = i.registerIcon("minecraft:ccProxyBack");
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityCCProxy te = (TileEntityCCProxy)world.getTileEntity(x, y, z);
		if(side == te.direction){
			return this.front;
		}else if(side == te.directionO){
			return this.back;
		}else{
			return this.side;
		}
	}*/
}
