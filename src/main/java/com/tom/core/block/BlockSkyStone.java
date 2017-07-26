package com.tom.core.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.ICustomItemBlock;
import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.block.IRegisterRequired;
import com.tom.apis.TMLogger;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.worldgen.WorldGen;
import com.tom.worldgen.WorldGen.OreGenEntry;

public class BlockSkyStone extends Block implements ICustomItemBlock, IRegisterRequired {
	public static final PropertyEnum<SkyStoneType> TYPE = PropertyEnum.<SkyStoneType>create("type", SkyStoneType.class);
	private final OreGenEntry entry;

	public BlockSkyStone() {
		super(Material.ROCK);
		setHardness(5.3F);
		setResistance(20.55F);
		setHarvestLevel("pickaxe", 3);
		entry = new OreGenEntry(WorldGen.OVERWORLD, () -> getStateFromMeta(1), 60, 1, 256, 1);
	}

	@Override
	public ItemBlock createItemBlock() {
		return new SkyStoneItemBlock(this);
	}

	public static class SkyStoneItemBlock extends ItemBlock implements IModelRegisterRequired {

		public SkyStoneItemBlock(Block block) {
			super(block);
			setHasSubtypes(true);
		}

		@Override
		public String getUnlocalizedName(ItemStack stack) {
			return super.getUnlocalizedName(stack) + "." + SkyStoneType.get(stack.getMetadata()).getName();
		}

		@Override
		public void registerModels() {
			CoreInit.registerRender(this, 0, "tomsmodcore:" + getUnlocalizedName().substring(5) + "." + SkyStoneType.NORMAL.getName());
			CoreInit.registerRender(this, 1, "tomsmodcore:" + getUnlocalizedName().substring(5) + "." + SkyStoneType.BURNT.getName());
		}
	}

	public static enum SkyStoneType implements IStringSerializable {
		NORMAL, BURNT;
		public static final SkyStoneType[] VALUES = values();

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		public static SkyStoneType get(int i) {
			return VALUES[i % VALUES.length];
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, SkyStoneType.get(meta));
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood
	 * returns 4 blocks)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, 0));
		list.add(new ItemStack(itemIn, 1, 1));
	}

	@Override
	public void register() {
		entry.ore = entry.oreInit.get();
		entry.name = "skyStone";
		if (Config.enableOreGen(entry)) {
			List<OreGenEntry> list = CoreInit.oreList.get(WorldGen.OVERWORLD);
			if (list == null) {
				list = new ArrayList<>();
				CoreInit.oreList.put(WorldGen.OVERWORLD, list);
			}
			list.add(entry);
		} else {
			String msg = "[Ore Gen] Burnt Skystone generation is disabled.";
			Config.warnMessages.add(msg);
			TMLogger.warn(msg);
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(TYPE).ordinal();
	}
}
