package com.tom.factory.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityLaserEngraver;
import com.tom.factory.tileentity.TileEntityMachineBase;
import com.tom.handler.GuiHandler.GuiIDs;

public class LaserEngraver extends BlockMachineBase {

	public LaserEngraver() {
		super(Material.IRON, null, CoreInit.MachineFrameChrome);
	}

	@Override
	public TileEntityMachineBase createNewTileEntity(World worldIn, int meta) {
		return new TileEntityLaserEngraver();
	}

	@Override
	public boolean onBlockActivatedI(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		playerIn.openGui(CoreInit.modInstance, GuiIDs.laserEngraver.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, 0));
		list.add(new ItemStack(itemIn, 1, 1));
	}

	@Override
	public void registerModels() {
		Item item = Item.getItemFromBlock(this);
		String name = CoreInit.getNameForItem(item).replace("|", "");
		CoreInit.registerRender(item, 0, name);
		CoreInit.registerRender(item, 1, name);
	}
}
