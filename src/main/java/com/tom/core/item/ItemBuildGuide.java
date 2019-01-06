package com.tom.core.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Joiner;

import com.tom.api.block.BlockMultiblockController;
import com.tom.api.tileentity.IMultiblockController;
import com.tom.lib.api.item.IScroller;
import com.tom.util.BlockData;
import com.tom.util.Counter;
import com.tom.util.CountingList;
import com.tom.util.MultiblockBlockChecker;
import com.tom.util.TomsModUtils;
import com.tom.util.TomsModUtils.BlockChecker;

import com.tom.core.tileentity.TileEntityTemplate;

public class ItemBuildGuide extends Item implements IScroller {
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		boolean b = update(world, pos, player.getHeldItem(hand), -1, true);
		return b ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
		if (stack.hasTagCompound() && !stack.getTagCompound().getTagList("ingList", 10).hasNoTags()) {
			int layer = stack.getTagCompound().getInteger("layer");
			tooltip.add(I18n.format("tomsmod.tooltip.templateLayer", layer < 0 ? I18n.format("tomsmod.tooltip.template.allLayers") : I18n.format("tomsmod.tooltip.template.layers", layer + 1, stack.getTagCompound().getInteger("maxlayer"))));
			if (GuiScreen.isShiftKeyDown()) {
				NBTTagList list = stack.getTagCompound().getTagList("ingList", 10);
				Joiner j = Joiner.on(I18n.format("tomsMod.tooltip.template"));
				Map<String, Counter> tooltips = new HashMap<>();
				for (int i = 0;i < list.tagCount();i++) {
					NBTTagCompound tag = list.getCompoundTagAt(i);
					int count = tag.getInteger("count");
					NBTTagList tagList = tag.getTagList("items", 10);
					NonNullList<ItemStack> stacks = NonNullList.withSize(tagList.tagCount(), ItemStack.EMPTY);
					TomsModUtils.loadAllItems(tagList, stacks);
					String s = j.join(stacks.stream().map(ItemStack::getDisplayName).iterator());
					if (tooltips.containsKey(s)) {
						tooltips.get(s).increaseCount(count + 1);
					} else {
						Counter c = new Counter();
						c.increaseCount(count + 1);
						tooltips.put(s, c);
					}
				}
				tooltips.entrySet().stream().map(e -> I18n.format("tomsMod.tooltip.template1", e.getValue().getCount(), e.getKey())).forEach(tooltip::add);
			} else {
				tooltip.add(I18n.format("tomsMod.tooltip.shiftToShow"));
			}
		}
	}

	@Override
	public void scroll(ItemStack stack, EntityPlayer player, ScrollDirection dir) {
		if (stack.hasTagCompound()) {
			int c = stack.getTagCompound().getInteger("layer") + (dir == ScrollDirection.UP ? 1 : -1);
			int max = stack.getTagCompound().getInteger("maxlayer");
			if (c >= max)
				c = -1;
			else if (c < -1)
				c = max - 1;
			update(player.world, TomsModUtils.readBlockPosFromNBT(stack.getTagCompound().getCompoundTag("master")), stack, c, false);
		}
	}

	@Override
	public boolean canScroll(ItemStack stack) {
		return true;
	}

	public boolean update(World world, BlockPos pos, ItemStack stack, int layer, boolean updateIngList) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof IMultiblockController && !world.isRemote) {
			IBlockState state = world.getBlockState(pos);
			if (state.getValue(BlockMultiblockController.STATE) == 0) {
				if (stack.hasTagCompound()) {
					NBTTagList list = stack.getTagCompound().getTagList("placed", 10);
					for (int i = 0;i < list.tagCount();i++) {
						TileEntityTemplate.remove(world, TomsModUtils.readBlockPosFromNBT(list.getCompoundTagAt(i)));
					}
				}
				IMultiblockController cont = (IMultiblockController) tile;
				Object[][] conf = cont.getConfig();
				Map<Character, MultiblockBlockChecker> materialMap = cont.getMaterialMap();
				if (materialMap == null)
					cont.getMultiblock(state);
				if (materialMap != null) {
					List<BlockChecker> list = TomsModUtils.getLayers(conf, materialMap, world, state.getValue(BlockMultiblockController.FACING), pos);
					NBTTagList locList = new NBTTagList();
					CountingList<BlockData> ingList = new CountingList<>();
					list.stream().filter(b -> b != null).filter(BlockChecker::notAir).filter(b -> b.isInLayer(layer)).forEach(b -> {
						MultiblockBlockChecker c = materialMap.get(b.getChar());
						if (c != null) {
							BlockData d = c.getData();
							if (d != null) {
								BlockPos loc = b.getLocation();
								if (TileEntityTemplate.place(world, loc, d)) {
									locList.appendTag(TomsModUtils.writeBlockPosToNewNBT(loc));
								}
								ingList.add(d);
							}
						}
					});
					if (!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());
					stack.getTagCompound().setTag("placed", locList);
					if (updateIngList) {
						NBTTagList ingTagList = new NBTTagList();
						ingList.streamResult().forEach(e -> {
							List<ItemStack> l = e.getKey().toStackList();
							NBTTagCompound tag = new NBTTagCompound();
							tag.setInteger("count", e.getValue().get());
							tag.setTag("items", TomsModUtils.saveAllItems(l));
							ingTagList.appendTag(tag);
						});
						stack.getTagCompound().setTag("ingList", ingTagList);
						stack.getTagCompound().setTag("master", TomsModUtils.writeBlockPosToNewNBT(pos));
						stack.getTagCompound().setInteger("maxlayer", conf.length - 1);
					}
					stack.getTagCompound().setInteger("layer", layer);
				}
			}
			return true;
		}
		return false;
	}
}
