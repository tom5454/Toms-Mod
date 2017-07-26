package com.tom.api.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockContainerTomsMod extends BlockContainer implements ICustomItemBlock {
	public BlockContainerTomsMod(Material material, MapColor mapColor) {
		super(material, mapColor);
		this.setHardness(5);
		this.setResistance(10);
	}

	public BlockContainerTomsMod(Material material) {
		this(material, material.getMaterialMapColor());
		this.setHardness(5);
		this.setResistance(10);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		int t = getRenderType();
		return (t == -1 ? EnumBlockRenderType.INVISIBLE : (t == 1 ? EnumBlockRenderType.LIQUID : (t == 2 ? EnumBlockRenderType.ENTITYBLOCK_ANIMATED : (t == 3 ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE))));
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te != null) {
			if (te instanceof IInventory) {
				// InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)
				// te);
				dropInventory(worldIn, pos, (IInventory) te);
			}
			/*if(te instanceof IFluidHandler){
				IFluidHandler f = (IFluidHandler) te;
				spillFluids(f, worldIn, pos);
			}*/
		}
		super.breakBlock(worldIn, pos, state);
	}

	public int getRenderType() {
		return 3;
	}

	protected AxisAlignedBB setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, EnumFacing dir) {
		switch (dir) {
		case DOWN:
			return new AxisAlignedBB(minX, 1.0F - maxZ, minY, maxX, 1.0F - minZ, maxY);
		case UP:
			return new AxisAlignedBB(minX, minZ, minY, maxX, maxZ, maxY);
		case NORTH:
			return new AxisAlignedBB(1.0F - maxX, minY, 1.0F - maxZ, 1.0F - minX, maxY, 1.0F - minZ);
		case EAST:
			return new AxisAlignedBB(minZ, minY, 1.0F - maxX, maxZ, maxY, 1.0F - minX);
		case WEST:
			return new AxisAlignedBB(1.0F - maxZ, minY, minX, 1.0F - minZ, maxY, maxX);
		default:
			return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		}
	}

	/*protected void spillFluids(IFluidHandler f, World world, BlockPos pos){
		FluidTankInfo[] info = f.getTankInfo(EnumFacing.DOWN);
		if(info != null && info.length > 0){
			for(int i = 0;i<info.length;i++){
				if(info[i] != null){
					FluidEvent.fireEvent(new FluidSpilledEvent(info[i].fluid, world, pos));
				}
			}
		}
	}*/
	protected void dropInventory(World worldIn, BlockPos pos, IInventory te) {
		InventoryHelper.dropInventoryItems(worldIn, pos, te);
	}

	@Override
	public ItemBlock createItemBlock() {
		return new ItemBlock(this);
	}
}
