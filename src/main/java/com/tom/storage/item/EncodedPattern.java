package com.tom.storage.item;

import java.util.ArrayList;
import java.util.List;

import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftable;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingRecipeContainer;
import com.tom.storage.multipart.StorageNetworkGrid.ISavedCraftingRecipe;
import com.tom.storage.multipart.StorageNetworkGrid.SavedCraftingRecipe;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EncodedPattern extends Item implements ICraftingRecipeContainer{
	public EncodedPattern() {
		setHasSubtypes(true);
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if(stack.getMetadata() == 0)return super.getUnlocalizedName();
		else return super.getUnlocalizedName() + "_encoded";
	}
	@Override
	public int getItemStackLimit(ItemStack stack) {
		if(stack.getMetadata() == 0)return 64;
		else return 1;
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if(stack.getMetadata() == 1 && playerIn.isSneaking()){
			writeRecipe(stack, null);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}else
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		if(stack.getMetadata() != 0){
			SavedCraftingRecipe recipe = readRecipe(stack);
			if(recipe != null && recipe.getOutputs() != null && recipe.getInputs() != null){
				tooltip.add(I18n.format("tomsMod.tooltip.recipe"));
				tooltip.add(I18n.format("tomsMod.tooltip.usesItems", (recipe.isStoredOnly() ? I18n.format("tomsMod.storage.storedOnly") : I18n.format("tomsMod.storage.storedAndCraftable"))));
				tooltip.add(I18n.format("tomsMod.tooltip.outputs"));
				List<ICraftable> outputs = new ArrayList<ICraftable>();
				List<ICraftable> inputs = new ArrayList<ICraftable>();
				for(ICraftable s : recipe.getOutputs()){
					if(s != null){
						StorageNetworkGrid.addCraftableToList(s, outputs);
					}
				}
				for(ICraftable s : recipe.getInputs()){
					if(s != null){
						StorageNetworkGrid.addCraftableToList(s, inputs);
					}
				}
				for(ICraftable s : outputs){
					tooltip.add(I18n.format("tomsMod.tooltip.patternItem", s.serializeStringTooltip()));
				}
				tooltip.add(I18n.format("tomsMod.tooltip.inputs"));
				for(ICraftable s : inputs){
					tooltip.add(I18n.format("tomsMod.tooltip.patternItem", s.serializeStringTooltip()));
				}
				tooltip.add("Use Container Items: " + (recipe.getRecipe(null).useContainerItems() ? I18n.format("tomsMod.chat.yes") : I18n.format("tomsMod.chat.no")));
			}else{
				tooltip.add(TextFormatting.RED + I18n.format("tomsMod.tooltip.invalidRecipe"));
			}
		}
	}
	public static void writeRecipe(ItemStack to, ISavedCraftingRecipe recipe){
		if(recipe == null){
			to.setTagCompound(null);
			to.setItemDamage(0);
			return;
		}
		if(!to.hasTagCompound())to.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = new NBTTagCompound();
		recipe.writeToNBT(tag);
		to.setItemDamage(1);
		to.getTagCompound().setTag("recipe", tag);
	}
	public static SavedCraftingRecipe readRecipe(ItemStack from){
		if(from.hasTagCompound())
			return SavedCraftingRecipe.loadFromNBT(from.getTagCompound().getCompoundTag("recipe"));
		else return null;
	}
	@Override
	public void setRecipe(ItemStack to, ISavedCraftingRecipe recipe) {
		writeRecipe(to, recipe);
	}
	@Override
	public SavedCraftingRecipe getRecipe(ItemStack from) {
		return readRecipe(from);
	}
}
