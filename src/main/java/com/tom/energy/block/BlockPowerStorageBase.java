package com.tom.energy.block;

import static com.tom.api.energy.EnergyType.LASER;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.api.energy.IEnergyHandler;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;

import com.tom.energy.tileentity.TileEntityEnergyCellBase;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT)
public abstract class BlockPowerStorageBase extends BlockContainerTomsMod  implements IPeripheralProvider{

	public BlockPowerStorageBase() {
		super(Material.IRON, MapColor.BLUE);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack is, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(is != null && CoreInit.isWrench(is,player)){
			if(!world.isRemote){
				if(player.isSneaking()){
					world.setBlockToAir(pos);
					return true;
				}
				TileEntityEnergyCellBase te = (TileEntityEnergyCellBase) world.getTileEntity(pos);
				boolean c = te.contains(side);
				if(c)te.outputSides &= ~(1 << side.ordinal());
				else te.outputSides |= 1 << side.ordinal();
				//System.out.println(" "+te.outputSides);
				te.markBlockForUpdate(pos);
				te.markDirty();
			}
			return true;
		}
		return false;
	}

	/*@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState state, EntityLivingBase entity, ItemStack itemstack){
    	EnumFacing f = TomsModUtils.getDirectionFacing(entity, false);
    	TileEntityEnergyCellBase te = (TileEntityEnergyCellBase) world.getTileEntity(pos);
    	te.facing = f;
	}*/
	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		return te instanceof TileEntityEnergyCellBase ? (IPeripheral)te : null;
	}
	@Override
	public int getComparatorInputOverride(IBlockState s, World worldIn, BlockPos pos) {
		IEnergyHandler h = (IEnergyHandler) worldIn.getTileEntity(pos);
		return MathHelper.floor_double(15 * (h.getEnergyStored(null, LASER) / h.getMaxEnergyStored(null, LASER)));
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
			tooltip.add(I18n.format("tomsMod.tooltip.energyStored", stack.getTagCompound().getCompoundTag("BlockEntityTag").getCompoundTag("energy").getDouble("Energy")));
		}
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
		if(tile instanceof TileEntityEnergyCellBase){
			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityEnergyCellBase)tile).writeToStackNBT(tag);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setTag("BlockEntityTag", tag);
		}
		spawnAsEntity(worldIn, pos, stack);
		super.breakBlock(worldIn, pos, state);
	}
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}
}
