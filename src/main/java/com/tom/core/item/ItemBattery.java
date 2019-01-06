package com.tom.core.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.ItemEnergyContainer;

public class ItemBattery extends ItemEnergyContainer {

	public ItemBattery() {
		super(10000, 750, 1000);
		setMaxStackSize(16);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getEnergyStored(stack) > 0;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1 - (getEnergyStored(stack) / capacity);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return getEnergyStored(stack) > 0 ? 1 : super.getItemStackLimit(stack);
	}

	@Override
	public boolean canInteract(ItemStack container) {
		return container.getCount() == 1;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile != null && tile.hasCapability(EnergyType.ENERGY_HANDLER_CAPABILITY, facing)) {
			if (getEnergyStored(stack) > 0) {
				EnergyType.LV.pushEnergyTo(worldIn, pos.offset(facing), facing, getItemContainerAsStorage(stack, 500), false);
			}
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	/**
	 * allows items to add custom lines of information to the mouseover
	 * description
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(getInfo(stack));
	}
}
