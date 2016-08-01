package com.tom.api.item;

import java.util.List;

import com.tom.core.CoreInit;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemCraftingTool extends Item implements ICustomCraftingHandler{
	public ItemCraftingTool() {
		setMaxStackSize(1);
		setCreativeTab(CoreInit.tabTomsModWeaponsAndTools);
		setHasSubtypes(true);
	}
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("damage") < getDurability(stack) : true;
	}
	public abstract int getDurability(ItemStack stack);
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		if(hasContainerItem(itemStack)){
			itemStack = itemStack.copy();
			/*if(!itemStack.hasTagCompound()){
				itemStack.setTagCompound(new NBTTagCompound());
			}
			itemStack.getTagCompound().setInteger("damage", itemStack.getTagCompound().getInteger("damage") + 1);*/
			return itemStack;
		}
		return null;
	}
	/**
	 * Determines if the durability bar should be rendered for this item.
	 * Defaults to vanilla stack.isDamaged behavior.
	 * But modders can use this for any data they wish.
	 *
	 * @param stack The current Item Stack
	 * @return True if it should render the 'durability' bar.
	 */
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("damage") != getDurability(stack) : false;
	}

	/**
	 * Queries the percentage of the 'Durability' bar that should be drawn.
	 *
	 * @param stack The current ItemStack
	 * @return 1.0 for 100% 0 for 0%
	 */
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		int d = getDurability(stack);
		return stack.hasTagCompound() ? 1 - ((d - stack.getTagCompound().getInteger("damage") * 1D) / d) : 0;
	}
	@Override
	public ItemCraftingTool setUnlocalizedName(String unlocalizedName) {
		super.setUnlocalizedName(unlocalizedName);
		return this;
	}

	public int getToolDamage(ItemStack stack){
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("damage") : 0;
	}
	@Override
	public final void onUsing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory,
			ItemStack stack) {
		if(!onUsingInTable(crafter, returnStack, crafingTableInventory, stack)){
			if(!stack.hasTagCompound()){
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setInteger("damage", stack.getTagCompound().getInteger("damage") + 1);
		}
	}

	public boolean onUsingInTable(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory, ItemStack stack) {return false;}

	@Override
	public void onCrafing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory) {}

	/**
	 * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
	 */
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState blockIn, BlockPos pos, EntityLivingBase entityLiving)
	{
		boolean player = entityLiving instanceof EntityPlayer;
		if (blockIn.getBlockHardness(worldIn, pos) != 0.0D && (!player || (player && !((EntityPlayer)entityLiving).capabilities.isCreativeMode)))
		{
			if(!stack.hasTagCompound()){
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setInteger("damage", stack.getTagCompound().getInteger("damage") + 1);
		}

		return true;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		int damage = getToolDamage(stack), maxDamage = getDurability(stack);
		tooltip.add(I18n.format("tomsMod.tooltip.durability") + ": " + maxDamage + "/" + (maxDamage - damage));
	}
}
