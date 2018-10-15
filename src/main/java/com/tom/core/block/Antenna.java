package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;

import com.tom.core.tileentity.TileEntityAntenna;

public class Antenna extends BlockContainerTomsMod implements IModelRegisterRequired {
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 3);

	protected Antenna(Material p_i45386_1_) {
		super(p_i45386_1_);
	}

	public Antenna() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityAntenna();
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{STATE});
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(STATE, meta % 4);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {// System.out.println("getMeta");
		return state.getValue(STATE);
	}

	@Override
	public void registerModels() {
		CoreInit.registerRender(Item.getItemFromBlock(this), 0, "tomsmodcore:antenna");
		CoreInit.registerRender(Item.getItemFromBlock(this), 1, "tomsmodcore:antenna_icon");
	}
	@Override
	public ItemBlock createItemBlock() {
		ItemBlock item = new ItemBlock(this);
		item.setHasSubtypes(true);
		return item;
	}
}
