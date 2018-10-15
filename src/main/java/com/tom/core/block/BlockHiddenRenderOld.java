package com.tom.core.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.client.CustomModelLoader;
import com.tom.core.CoreInit;
import com.tom.core.model.ModelHidden;

import com.tom.core.tileentity.TileEntityHidden;

public class BlockHiddenRenderOld extends BlockHidden implements IModelRegisterRequired {
	public static class UnlistedPropertyData implements IUnlistedProperty<TileEntityHidden> {

		@Override
		public String getName() {
			return "data";
		}

		@Override
		public boolean isValid(TileEntityHidden value) {
			return value != null;
		}

		@Override
		public Class<TileEntityHidden> getType() {
			return TileEntityHidden.class;
		}

		@Override
		public String valueToString(TileEntityHidden value) {
			return value.toString();
		}

	}

	public static final UnlistedPropertyData DATA = new UnlistedPropertyData();

	@Override
	public void registerModels() {
		String type = CoreInit.getNameForBlock(this).replace("|", "");
		CustomModelLoader.addOverride(new ResourceLocation(type), new ModelHidden(getName()));
	}

	public String getName() {
		return "Block Hiddden";
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (state instanceof IExtendedBlockState && te instanceof TileEntityHidden) {
			IExtendedBlockState s = (IExtendedBlockState) state;
			return s.withProperty(DATA, (TileEntityHidden) te);
		}
		return super.getExtendedState(state, world, pos);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty<?>[0], new IUnlistedProperty<?>[]{DATA});
	}
}
