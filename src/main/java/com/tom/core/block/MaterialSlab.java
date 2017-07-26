package com.tom.core.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.block.IMultiBlockInstance;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;

public class MaterialSlab implements IMultiBlockInstance, IModelRegisterRequired {
	public final PropertyInteger VARIANT;
	public final BlockMaterialSlab half, full;
	private final TMResource[] resources;
	private final ItemBlock itemBlock;

	public MaterialSlab(TMResource... resources) {
		VARIANT = PropertyInteger.create("type", 0, Math.max(Math.min(resources.length - 1, 7), 1));
		half = new BlockMaterialSlab();
		full = new BlockMaterialSlab() {
			@Override
			public boolean isDouble() {
				return true;
			}
		};
		this.resources = new TMResource[Math.min(resources.length, 8)];
		for (int i = 0;i < resources.length && i < 8;i++) {
			this.resources[i] = resources[i];
			resources[i].setSlab(this);
		}
		itemBlock = new ItemSlab(half, half, full);
	}

	public class BlockMaterialSlab extends BlockSlab {
		public BlockMaterialSlab() {
			super(Material.IRON);
			IBlockState iblockstate = this.blockState.getBaseState();

			if (!this.isDouble()) {
				iblockstate = iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
			}

			this.setDefaultState(iblockstate.withProperty(VARIANT, 0));
			this.setCreativeTab(CoreInit.tabTomsModMaterials);
		}

		@Override
		public String getUnlocalizedName(int meta) {
			return super.getUnlocalizedName() + "." + resources[meta % resources.length].getName();
		}

		@Override
		public IProperty<?> getVariantProperty() {
			return VARIANT;
		}

		@Override
		public Comparable<?> getTypeForItem(ItemStack stack) {
			return stack.getMetadata() % 4;
		}

		@Override
		@Nullable
		public Item getItemDropped(IBlockState state, Random rand, int fortune) {
			return Item.getItemFromBlock(half);
		}

		@Override
		public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
			return new ItemStack(half, 1, state.getValue(VARIANT));
		}

		/**
		 * returns a list of blocks with the same ID, but different meta (eg:
		 * wood returns 4 blocks)
		 */
		@Override
		@SideOnly(Side.CLIENT)
		public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
			if (itemIn != Item.getItemFromBlock(full)) {
				for (int i = 0;i < resources.length;i++) {
					list.add(new ItemStack(itemIn, 1, i));
				}
			}
		}

		/**
		 * Convert the given metadata into a BlockState for this Block
		 */
		@Override
		public IBlockState getStateFromMeta(int meta) {
			IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, meta & 7);

			if (!this.isDouble()) {
				iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
			}

			return iblockstate;
		}

		/**
		 * Convert the BlockState into the correct metadata value
		 */
		@Override
		public int getMetaFromState(IBlockState state) {
			int i = 0;
			i = i | state.getValue(VARIANT);

			if (!this.isDouble() && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
				i |= 8;
			}

			return i;
		}

		@Override
		protected BlockStateContainer createBlockState() {
			return this.isDouble() ? new BlockStateContainer(this, new IProperty[]{VARIANT}) : new BlockStateContainer(this, new IProperty[]{HALF, VARIANT});
		}

		/**
		 * Gets the metadata of the item this Block can drop. This method is
		 * called when the block gets destroyed. It returns the metadata of the
		 * dropped item based on the old metadata of the block.
		 */
		@Override
		public int damageDropped(IBlockState state) {
			return state.getValue(VARIANT);
		}

		public IBlockState getStateForType(TMResource tmResource) {
			for (int i = 0;i < resources.length;i++) {
				if (resources[i] == tmResource) { return getDefaultState().withProperty(VARIANT, i); }
			}
			return getDefaultState();
		}

		@Override
		public boolean isDouble() {
			return false;
		}
	}

	public MaterialSlab setUnlocalizedName(String name) {
		half.setUnlocalizedName(name);
		full.setUnlocalizedName("double_" + name);
		return this;
	}

	@Override
	public ItemBlock createItemBlock() {
		return itemBlock;
	}

	@Override
	public Block[] getBlocks() {
		return new Block[]{half, full};
	}

	public ItemStack getStackForType(TMResource tmResource, int a) {
		int v = 0;
		for (int i = 0;i < resources.length;i++) {
			if (resources[i] == tmResource) {
				v = i;
				break;
			}
		}
		return new ItemStack(half, a, v);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		for (int i = 0;i < resources.length;i++) {
			CoreInit.registerRender(itemBlock, i, "tomsmodcore:resources/slab_" + resources[i].getName());
		}
	}
}
