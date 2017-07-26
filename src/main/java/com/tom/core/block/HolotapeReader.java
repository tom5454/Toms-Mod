package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;

import com.tom.core.tileentity.TileEntityHolotapeReader;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT)
public class HolotapeReader extends BlockContainerTomsMod implements IPeripheralProvider {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);

	/*@SideOnly(Side.CLIENT)
	protected IIcon side;
	@SideOnly(Side.CLIENT)
	protected IIcon vIIcon;
	@SideOnly(Side.CLIENT)
	protected IIcon ivIIcon;*/
	public HolotapeReader() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	/*public void registerBlockIcons(IIconRegister i){
		this.blockIcon = i.registerIcon("minecraft:tm/holoReader");
		this.side = i.registerIcon("minecraft:tm/holoDeviceSide");
		this.vIIcon = i.registerIcon("minecraft:tm/holoReaderV");
		this.ivIIcon = i.registerIcon("minecraft:tm/holoReaderIv");
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityHolotapeReader te = (TileEntityHolotapeReader)world.getTileEntity(x, y, z);
		if(side == te.direction){
			if(te.hasH){
				if(te.isValidH) return this.vIIcon;
				else return this.ivIIcon;
			}else return this.blockIcon;
		}else return this.side;
	}*/
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityHolotapeReader();
	}

	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		return te instanceof TileEntityHolotapeReader ? (IPeripheral) te : null;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		TileEntityHolotapeReader te = (TileEntityHolotapeReader) world.getTileEntity(pos);
		if (!heldItem.isEmpty() && heldItem.getItem() == CoreInit.holotape) {
			if (!te.hasH) {
				if (!world.isRemote) {
					ItemStack t = heldItem.splitStack(1);
					te.holotape.setInventorySlotContents(0, t);
					te.markBlockForUpdate(pos);
				}
				return true;
			}
		} else {
			if (te.hasH) {
				if (!world.isRemote) {
					ItemStack holotape = te.holotape.removeStackFromSlot(0);
					EntityItem itemEntity = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), holotape);
					world.spawnEntity(itemEntity);
					te.markBlockForUpdate(pos);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState bs, EntityLivingBase entity, ItemStack itemstack) {
		EnumFacing f = TomsModUtils.getDirectionFacing(entity, false);
		world.setBlockState(pos, bs.withProperty(FACING, f).withProperty(STATE, 0), 2);
		TileEntity te = world.getTileEntity(pos);
		TileEntityHolotapeReader te2 = (TileEntityHolotapeReader) te;
		int d = f.ordinal();
		if (d == 5)
			te2.direction = 4;
		else if (d == 4)
			te2.direction = 5;
		else if (d == 3)
			te2.direction = 2;
		else if (d == 2)
			te2.direction = 3;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING, STATE});
	}

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
}
