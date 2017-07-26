package com.tom.storage.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.api.block.IModelRegisterRequired;
import com.tom.client.CustomModelLoader;
import com.tom.core.CoreInit;
import com.tom.storage.client.AdvRouterModel;
import com.tom.storage.handler.StorageNetworkGrid.IAdvRouterTile;
import com.tom.storage.handler.StorageNetworkGrid.IControllerTile;
import com.tom.storage.tileentity.TileEntityAdvRouter;

public class AdvStorageSystemRouter extends BlockContainerTomsMod implements IModelRegisterRequired {
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);
	public static final UnlistedPropertyConnection CONNECTIONS = new UnlistedPropertyConnection();

	public AdvStorageSystemRouter() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityAdvRouter();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityAdvRouter) {
			try {
				((TileEntityAdvRouter) tile).neighborUpdateGrid(EnumFacing.getFacingFromVector(neighbor.getX() - pos.getX(), neighbor.getY() - pos.getY(), neighbor.getZ() - pos.getZ()));
			} catch (Exception e) {
				// wrong tile
			}
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{STATE}, new IUnlistedProperty[]{CONNECTIONS});
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STATE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(STATE, meta % 3);
	}
	/*@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote){
			playerIn.openGui(CoreInit.modInstance, GuiIDs.storageNetworkController.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}*/

	@Override
	public void registerModels() {
		CustomModelLoader.addOverride(new ResourceLocation("tomsmodstorage", getUnlocalizedName().substring(5)), new AdvRouterModel());
		CoreInit.registerRender(Item.getItemFromBlock(this));
	}

	public static class UnlistedPropertyConnection implements IUnlistedProperty<Byte> {

		@Override
		public String getName() {
			return "connections";
		}

		@Override
		public boolean isValid(Byte value) {
			return value >= 0 && value < 64;
		}

		@Override
		public Class<Byte> getType() {
			return Byte.class;
		}

		@Override
		public String valueToString(Byte value) {
			return value.toString();
		}
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState s = (IExtendedBlockState) state;
			byte connect = 0;
			for (EnumFacing f : EnumFacing.VALUES) {
				TileEntity tile = world.getTileEntity(pos.offset(f));
				if (tile instanceof IAdvRouterTile || tile instanceof IControllerTile) {
					connect |= 1 << (f.ordinal());
				}
			}
			return s.withProperty(CONNECTIONS, connect);
		}
		return super.getExtendedState(state, world, pos);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
