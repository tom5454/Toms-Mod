package com.tom.storage.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockGridDevice;
import com.tom.api.inventory.StoredItemStack;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.apis.TomsModUtils;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftable;
import com.tom.storage.tileentity.TileEntityCraftingController;

public class CraftingController extends BlockGridDevice {
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public CraftingController() {
		super(Material.IRON);
	}

	@Override
	public TileEntityGridDeviceBase<StorageNetworkGrid> createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCraftingController();
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {ACTIVE});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(ACTIVE, meta == 1);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(ACTIVE) ? 1 : 0;
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote){
			TileEntityCraftingController te = (TileEntityCraftingController) worldIn.getTileEntity(pos);
			if(playerIn.isSneaking())TomsModUtils.sendNoSpam(playerIn, te.cancelCrafting());
			else TomsModUtils.sendNoSpam(playerIn, te.serializeMessage());
		}
		return true;
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityCraftingController te = (TileEntityCraftingController) worldIn.getTileEntity(pos);
		List<ICraftable> s = te.getStoredStacks();
		if(!s.isEmpty()){
			for(int i = 0;i<s.size();i++){
				ICraftable stack = s.get(i);
				if(stack != null && stack instanceof StoredItemStack && stack.hasQuantity()){
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), ((StoredItemStack)stack).stack);
				}
			}
			s.clear();
		}
		super.breakBlock(worldIn, pos, state);
	}
}
