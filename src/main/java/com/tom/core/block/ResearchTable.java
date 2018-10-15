package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.TileEntityResearchTable;
import com.tom.core.tileentity.TileEntityResearchTable.ResearchTableType;

public class ResearchTable extends BlockContainerTomsMod {
	/** 0:Base,1:Left,2:Right */
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyEnum<ResearchTableType> TYPE = PropertyEnum.create("type", ResearchTableType.class);

	public ResearchTable() {
		super(Material.WOOD);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityResearchTable();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING, STATE, TYPE});
	}

	/*@Override
	public IBlockState getStateFromMeta(int meta)
	{
	    EnumFacing enumfacing = EnumFacing.getHorizontal(meta % 4).rotateY();

	    if (enumfacing.getAxis() == EnumFacing.Axis.Y)
	    {
	        enumfacing = EnumFacing.NORTH;
	    }
	    //System.out.println(enumfacing);
	    return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(STATE, (meta-1) / 4);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		EnumFacing enumfacing = state.getValue(FACING);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
	    {
	        enumfacing = EnumFacing.NORTH;
	    }
	    return (enumfacing.getHorizontalIndex()+1) * (state.getValue(STATE)+1);
	}*/
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean formed = (meta & 8) > 0;
		boolean isRight = (meta & 4) > 0;
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(STATE, formed ? isRight ? 2 : 1 : 0);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		boolean formed = state.getValue(STATE) > 0;
		boolean isRight = state.getValue(STATE) == 2;
		int i = 0;
		i = i | state.getValue(FACING).getHorizontalIndex();

		if (formed) {
			i |= 8;
		}

		if (isRight) {
			i |= 4;
		}

		return i;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (worldIn.isRemote)
			return true;
		int blockState = state.getValue(STATE);
		EnumFacing facing = state.getValue(FACING);
		if (blockState == 0) {
			if (heldItem != null && heldItem.getItem() == Item.getItemFromBlock(this)) {
				BlockPos offsetPos = pos.offset(facing.rotateY());
				IBlockState offsetBlockState = worldIn.getBlockState(offsetPos);
				if (offsetBlockState == null || offsetBlockState.getBlock() == null || offsetBlockState.getBlock().getMaterial(offsetBlockState) == Material.AIR) {
					if (!player.capabilities.isCreativeMode)
						heldItem.splitStack(1);
					TomsModUtils.setBlockState(worldIn, pos, state.withProperty(STATE, 2));
					worldIn.setBlockState(offsetPos, this.getDefaultState().withProperty(FACING, facing).withProperty(STATE, 1));
				} else {
					TomsModUtils.sendNoSpamTranslate(player, "tomsMod.chat.destObs");
				}
			}
		} else if (blockState == 1) {
			BlockPos parentPos = pos.offset(facing.rotateYCCW());
			IBlockState parentState = worldIn.getBlockState(parentPos);
			if (parentState != null && parentState.getBlock() == this) {
				parentState.getBlock().onBlockActivated(worldIn, parentPos, parentState, player, hand, side, hitX, hitY, hitZ);
			}
		} else if (blockState == 2) {
			if (heldItem != null && heldItem.getItem() == CoreInit.researchTableUpgrade) {
				TileEntityResearchTable te = (TileEntityResearchTable) worldIn.getTileEntity(pos);
				byte b = te.upgrade(heldItem.getMetadata());
				if (b == 2) {
					if (!player.capabilities.isCreativeMode)
						heldItem.splitStack(1);
				} else if (b == 1) {
					player.openGui(CoreInit.modInstance, GuiIDs.researchTable.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
				} else if (b == 3) {
					TomsModUtils.sendNoSpamTranslateWithTag(player, new Style(), heldItem.getUnlocalizedName() + ".name", "tomsMod.chat.upgradeFailed");
				}
			} else {
				player.openGui(CoreInit.modInstance, GuiIDs.researchTable.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, TomsModUtils.getDirectionFacing(placer, false).getOpposite()).withProperty(STATE, 0);
	}

	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState s) {
		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		this.breakBlock(worldIn, pos, state, true);
	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state, boolean first) {
		int blockState = state.getValue(STATE);
		if (blockState == 2) {
			TileEntityResearchTable te = (TileEntityResearchTable) worldIn.getTileEntity(pos);
			te.state = state;
			InventoryHelper.dropInventoryItems(worldIn, pos, te);
			te.dropUpgrades();
		}
		if (first) {
			EnumFacing facing = state.getValue(FACING);
			if (blockState != 0) {
				EnumFacing f;
				if (blockState == 1) {
					f = facing.rotateYCCW();
				} else {
					f = facing.rotateY();
				}
				IBlockState testState = worldIn.getBlockState(pos.offset(f));
				if (testState != null && testState.getBlock() == this) {
					if (facing == testState.getValue(FACING) && blockState != testState.getValue(STATE)) {
						((ResearchTable) testState.getBlock()).breakBlock(worldIn, pos, testState, false);
						worldIn.setBlockToAir(pos.offset(f));
						EntityItem drop2 = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(CoreInit.researchTable));
						worldIn.spawnEntity(drop2);
					}
				}
			}
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		return tile != null && tile instanceof TileEntityResearchTable ? state.withProperty(TYPE, ((TileEntityResearchTable) tile).getType()) : state.withProperty(TYPE, ResearchTableType.WOODEN);
	}

	@Override
	protected void dropInventory(World worldIn, BlockPos pos, IInventory te) {
	}
}
