package com.tom.energy.block;

import static com.tom.api.energy.EnergyType.LV;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.api.energy.IEnergyHandler;
import com.tom.apis.TomsModUtils;
import com.tom.lib.Configs;

import com.tom.energy.tileentity.TileEntityBatteryBox;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT)
public class BatteryBox extends BlockContainerTomsMod implements IPeripheralProvider{
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public BatteryBox() {
		super(Material.WOOD);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBatteryBox();
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getFront(meta % 6);

		/*if (enumfacing.getAxis() == EnumFacing.Axis.Y)
	    {
	        enumfacing = EnumFacing.NORTH;
	    }*/
		//System.out.println("getState");
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		return state.getValue(FACING).getIndex();
	}
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		EnumFacing f = TomsModUtils.getDirectionFacing(placer, true);
		return getDefaultState().withProperty(FACING,f.getOpposite());
	}

	@Override
	public boolean isFullCube(IBlockState s) {
		return false;
	}
	@Override
	public boolean isFullBlock(IBlockState s) {
		return false;
	}
	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}
	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		return te instanceof TileEntityBatteryBox ? (IPeripheral)te : null;
	}
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}
	@Override
	public int getComparatorInputOverride(IBlockState s, World worldIn, BlockPos pos) {
		IEnergyHandler h = (IEnergyHandler) worldIn.getTileEntity(pos);
		return MathHelper.floor_double(15 * (h.getEnergyStored(null, LV) / h.getMaxEnergyStored(null, LV)));
	}
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		if(stack.hasTagCompound()){
			tooltip.add(I18n.format("tomsMod.tooltip.energyStored", stack.getTagCompound().getCompoundTag("BlockEntityTag").getDouble("energy")));
		}
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
		if(tile instanceof TileEntityBatteryBox){
			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityBatteryBox)tile).writeToStackNBT(tag);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setTag("BlockEntityTag", tag);
		}
		spawnAsEntity(worldIn, pos, stack);
		super.breakBlock(worldIn, pos, state);
	}
}
