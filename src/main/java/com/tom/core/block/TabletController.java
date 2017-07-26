package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.lib.Configs;

import com.tom.core.tileentity.TileEntityTabletController;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT)
public class TabletController extends BlockContainerTomsMod implements IPeripheralProvider {
	/*@SideOnly(Side.CLIENT)
	private IIcon online;*/
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

	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		return te instanceof TileEntityTabletController ? (IPeripheral) te : null;
	}
	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:tm/Antenna2");
		this.online = iconregister.registerIcon("minecraft:tm/TabContSideAct");
	}
	public IIcon getIcon(int side, int meta){
		if(side > 1) return this.online;
		else return this.blockIcon;
	}*/

}
