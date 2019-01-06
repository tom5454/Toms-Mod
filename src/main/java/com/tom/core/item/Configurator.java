package com.tom.core.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.item.IConfigurator;
import com.tom.handler.ConfiguratorHandler;
import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.ItemEnergyContainer;
import com.tom.util.TomsModUtils;

import com.tom.energy.tileentity.TileEntityBatteryBox;

public class Configurator extends ItemEnergyContainer implements IConfigurator {
	public static final double CONFIGURATOR_USAGE = 0.2;

	public Configurator() {
		super(1000, 5, 1);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		boolean ret = false;
		if (getEnergyStored(stack) < capacity && !worldIn.isRemote) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof TileEntityBatteryBox) {
				TileEntityBatteryBox te = (TileEntityBatteryBox) tile;
				double max = te.extractEnergy(facing, EnergyType.LV, 2, true);
				if (max > 0) {
					double rec = receiveEnergy(stack, max, true);
					if (rec > 0) {
						receiveEnergy(stack, te.extractEnergy(facing, EnergyType.LV, rec, false), false);
						ret = true;
					}
				}
			}
		}
		if (hand == EnumHand.MAIN_HAND)
			return ConfiguratorHandler.openConfigurator(stack, playerIn, worldIn, pos) || ret ? worldIn.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS : worldIn.isRemote ? EnumActionResult.PASS : EnumActionResult.FAIL;
		else if (ret)
			return worldIn.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
		else {
			TomsModUtils.sendNoSpamTranslateWithTag(playerIn, new Style().setColor(TextFormatting.RED), stack.getUnlocalizedName() + ".name", "tomsMod.invalidHandUseMain");
			return EnumActionResult.PASS;
		}
	}

	@Override
	public boolean isConfigurator(ItemStack stack, EntityPlayer player) {
		return true;
	}

	@Override
	public boolean use(ItemStack stack, EntityPlayer player, boolean simulate) {
		double extracted = this.extractEnergy(stack, CONFIGURATOR_USAGE, simulate);
		return extracted == CONFIGURATOR_USAGE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(getInfo(stack));
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1 - this.getPercentStored(stack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)){
			subItems.add(new ItemStack(this, 1, 0));
			ItemStack is = new ItemStack(this, 1, 0);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("Energy", capacity);
			is.setTagCompound(tag);
			subItems.add(is);
		}
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return !(stack.getTagCompound() != null && stack.getTagCompound().hasKey("isInCreativeTabIcon"));
	}

	/**
	 * Returns the packed int RGB value used to render the durability bar in the
	 * GUI. Defaults to a value based on the hue scaled as the damage decreases,
	 * but can be overriden.
	 *
	 * @param stack
	 *            Stack to get durability from
	 * @return A packed RGB value for the durability colour (0x00RRGGBB)
	 */
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1 - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
	}
}
