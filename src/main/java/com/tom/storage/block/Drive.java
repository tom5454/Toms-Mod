package com.tom.storage.block;

import java.util.List;
import java.util.Random;

import com.tom.api.block.BlockGridDevice;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.apis.TomsModUtils;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.tileentity.TileEntityDrive;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Drive extends BlockGridDevice {
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public Drive() {
		super(Material.IRON);
	}
	@Override
	public TileEntityGridDeviceBase<StorageNetworkGrid> createNewTileEntity(World worldIn, int meta) {
		return new TileEntityDrive();
	}
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return super
				.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, TomsModUtils.getDirectionFacing(placer, true).getOpposite());
	}
	@Override
	public boolean isOpaqueCube(IBlockState s){
		return false;
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta % 6));
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,FACING);
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		TileEntityDrive te = (TileEntityDrive) worldIn.getTileEntity(pos);
		return te.onBlockActivated(playerIn, heldItem);
	}
	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		ItemStack stack = new ItemStack(this);
		if(tile instanceof TileEntityDrive){
			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityDrive)tile).writeToStackNBT(tag);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setTag("BlockEntityTag", tag);
			stack.getTagCompound().setBoolean("stored", true);
		}
		spawnAsEntity(worldIn, pos, stack);
		super.breakBlock(worldIn, pos, state);
	}
	@Override
	protected void dropInventory(World worldIn, BlockPos pos, IInventory te) {}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("stored")){
			tooltip.add(I18n.format("tomsMod.tooltip.itemsStored"));
		}
	}
}
