package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.lib.Configs;

import com.tom.core.tileentity.TileEntityEnderMemory;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT)
public class EnderMemory extends BlockContainerTomsMod implements IPeripheralProvider {

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
		// System.out.println(pName);
		te.playerName = pName;
	}

	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		return te instanceof TileEntityEnderMemory ? (IPeripheral) te : null;
	}

}
