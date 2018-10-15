package com.tom.core.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.recipes.ICustomJsonIngerdient;

public class ResourceItem extends Item {
	public ResourceItem(Type type) {
		super();
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CoreInit.tabTomsModMaterials);
		this.type = type;
		setUnlocalizedName(type.getName());
		type.setItem(this);
	}

	public final Type type;

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		return this.getUnlocalizedName() + "_" + TMResource.VALUES[meta].getName();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList itemList) {
		if(this.isInCreativeTab(tab)){
			for (TMResource r : TMResource.VALUES) {
				if (r.isValid(type))
					itemList.add(r.getStackNormal(type));
			}
		}
	}

	public static class CraftingItem extends Item implements IModelRegisterRequired, ICustomJsonIngerdient {
		public CraftingItem() {
			this.setUnlocalizedName("material");
			setHasSubtypes(true);
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		@SideOnly(Side.CLIENT)
		public void getSubItems(CreativeTabs tab, NonNullList itemList) {
			if(this.isInCreativeTab(tab)){
				for (CraftingMaterial r : CraftingMaterial.VALUES) {
					itemList.add(r.getStackNormal());
				}
			}
		}

		@Override
		public String getUnlocalizedName(ItemStack stack) {
			int meta = stack.getMetadata();
			return this.getUnlocalizedName() + "_" + CraftingMaterial.get(meta).getName();
		}

		@Override
		public CraftingItem setCreativeTab(CreativeTabs tab) {
			super.setCreativeTab(tab);
			return this;
		}

		@Override
		public boolean onEntityItemUpdate(EntityItem entityItem) {
			ItemStack s = CraftingMaterial.tick(entityItem.world, new BlockPos(entityItem), entityItem, entityItem.getItem());
			if (s == null || s.isEmpty()) {
				entityItem.setDead();
				return true;
			} else
				entityItem.setItem(s);
			return false;
		}

		@Override
		public int getItemStackLimit(ItemStack stack) {
			return CraftingMaterial.get(stack.getMetadata()).getMaxStackSize();
		}

		@Override
		public boolean showDurabilityBar(ItemStack stack) {
			return CraftingMaterial.get(stack.getMetadata()).hasDurabilityBar(stack);
		}

		@Override
		public double getDurabilityForDisplay(ItemStack stack) {
			return CraftingMaterial.get(stack.getMetadata()).getDurabilityBar(stack);
		}

		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
			return CraftingMaterial.get(player.getHeldItem(hand).getMetadata()).onItemUse(player.getHeldItem(hand), player, world, pos, side, hitX, hitY, hitZ, hand);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
			CraftingMaterial.get(stack.getMetadata()).getTooltip(stack, world, tooltip, advanced);
		}

		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
			return CraftingMaterial.inited ? CraftingMaterial.get(stack.getMetadata()).initCapabilities(stack, nbt) : null;
		}

		@Override
		public void registerModels() {
			for (CraftingMaterial t : CraftingMaterial.VALUES) {
				CoreInit.registerRender(this, t.ordinal(), "tomsmodcore:resources/crafting/" + t.getName());
			}
		}

		@Override
		public Map<String, Object> serialize(ItemStack stack, boolean serializeCount) {
			System.out.println("Serializing CraftingItem");
			Map<String, Object> ret = new HashMap<>();
			ret.put("type", "tomsmodcore:material");
			ret.put("id", CraftingMaterial.get(stack.getMetadata()).getName());
			if(serializeCount)ret.put("count", stack.getCount());
			return ret;
		}
	}
}
