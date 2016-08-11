package com.tom.core.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.core.CoreInit;
import com.tom.core.TMResource;

public class MaterialBlock extends Block {
	public final List<TMResource> resourceList;
	public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 15);
	public final MaterialBlockItemBlock itemBlock;
	public MaterialBlock(TMResource... resources) {
		super(Material.IRON);
		resourceList = getListNullCheckWithCap(16, resources);
		//TYPE = PropertyEnum.create("type", TMResource.class, null); BlockObsidian
		setCreativeTab(CoreInit.tabTomsModMaterials);
		for(int i = 0;i<resourceList.size();i++){
			resourceList.get(i).setBlock(this, i);
		}
		itemBlock = new MaterialBlockItemBlock(this);
	}
	private static final List<TMResource> getListNullCheckWithCap(int cap, TMResource... in){
		List<TMResource> list = new ArrayList<TMResource>(cap);
		if(in != null){
			for(TMResource a : in){
				if(a != null)
					if(cap != list.size())
						list.add(a);
					else
						break;
			}
		}
		return list;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
	{
		for(int i = 0;i<resourceList.size();i++){
			list.add(new ItemStack(itemIn, 1, i));
		}
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE);
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, meta);
	}
	@Override
	public MaterialBlock setUnlocalizedName(String name) {
		super.setUnlocalizedName(name);
		return this;
	}
	@SideOnly(Side.CLIENT)
	public void registerRender(){
		for(int i = 0;i<resourceList.size();i++){
			CoreInit.registerRender(itemBlock, i, "tomsmodcore:resources/block_"+resourceList.get(i).getName());
		}
	}
	public static class MaterialBlockItemBlock extends ItemBlock{
		public final MaterialBlock block;
		public MaterialBlockItemBlock(MaterialBlock block) {
			super(block);
			this.block = block;
			this.setMaxDamage(0);
			this.setHasSubtypes(true);
		}
		/**
		 * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
		 * placed as a Block (mostly used with ItemBlocks).
		 */
		@Override
		public int getMetadata(int damage)
		{
			return damage;
		}

		/**
		 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
		 * different names based on their damage or NBT.
		 */
		@Override
		public String getUnlocalizedName(ItemStack stack)
		{
			return super.getUnlocalizedName() + "." + (block.resourceList.size() > stack.getMetadata() ? block.resourceList.get(stack.getMetadata()).getName() : "iron");
		}
	}
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		int meta = getMetaFromState(state);
		return resourceList.size() > meta ? resourceList.get(meta).getBlockStackNormal(1) : resourceList.get(0).getBlockStackNormal(1);
	}
	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped(IBlockState state)
	{
		return state.getValue(TYPE);
	}
}
