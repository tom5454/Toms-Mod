package com.tom.core.item;

import java.util.List;

import com.tom.api.energy.ItemEnergyContainer;
import com.tom.api.item.IConfigurator;
import com.tom.apis.TomsModUtils;
import com.tom.handler.ConfiguratorHandler;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Configurator extends ItemEnergyContainer implements IConfigurator{
	private static final double CONFIGURATOR_USAGE = 0.1;
	public Configurator() {
		super(1000, 1);
	}
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		if(hand == EnumHand.MAIN_HAND)return ConfiguratorHandler.openConfigurator(stack, playerIn, worldIn, pos) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
		else{
			TomsModUtils.sendNoSpamTranslateWithTag(playerIn, new Style().setColor(TextFormatting.RED), stack.getUnlocalizedName()+".name", "tomsMod.invalidHandUseMain");
			return EnumActionResult.FAIL;
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
	public void addInformation(ItemStack stack, EntityPlayer playerIn,
			List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		double energy = this.getEnergyStored(stack);
		double per = energy * 100 / capacity;
		int p = MathHelper.floor_double(per);
		tooltip.add(I18n.format("tomsMod.tooltip.charge") + ": "+this.getMaxEnergyStored(stack)+"/"+energy+" "+p+"%");
	}
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1-this.getPercentStored(stack);
	}
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab,
			List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		ItemStack is = new ItemStack(itemIn, 1, 0);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble("Energy", capacity);
		is.setTagCompound(tag);
		subItems.add(is);
	}
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return !(stack.getTagCompound() != null && stack.getTagCompound().hasKey("isInCreativeTabIcon"));
		//return false;
	}
}
