package com.tom.factory.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.factory.tileentity.TileEntityAdvWaterCollector;

public class AdvWaterCollector extends BlockContainerTomsMod {

	public AdvWaterCollector() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityAdvWaterCollector();
	}
	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(I18n.format("tomsmod.tooltip.advwatercollector_power"));
	}
}
