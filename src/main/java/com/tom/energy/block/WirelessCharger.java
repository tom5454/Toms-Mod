package com.tom.energy.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.api.block.IIconRegisterRequired;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.energy.item.WirelessChargerItemBlock;

import com.tom.energy.tileentity.TileEntityWirelessCharger;

public class WirelessCharger extends BlockContainerTomsMod implements IIconRegisterRequired{
	/*@SideOnly(Side.CLIENT)
	private IIcon off;*/
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public static final PropertyBool TYPE = PropertyBool.create("type");
	public WirelessCharger() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityWirelessCharger();
	}
	/*@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b){
		if(!world.isRemote){
			boolean redstone = false;
			if (Block.getIdFromBlock(b) > 0 && b.canProvidePower() && world.isBlockIndirectlyGettingPowered(x, y, z)){
				redstone = true;
			}else if(Block.getIdFromBlock(b) > 0 && b.canProvidePower()){
				redstone = false;
			}
			TileEntityWirelessCharger te = (TileEntityWirelessCharger) world.getTileEntity(pos);
			te.redstone = !redstone;
			te.markDirty();
			world.markBlockForUpdate(pos);
		}
	}
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityWirelessCharger te = (TileEntityWirelessCharger) world.getTileEntity(x, y, z);
		if(te.active) return this.blockIcon;
		else return this.off;
    }
    @SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
    	this.blockIcon = iconregister.registerIcon("minecraft:WirelessChargerOn");
    	this.off = iconregister.registerIcon("minecraft:WirelessChargerOff");
    }*/
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {ACTIVE, TYPE});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(ACTIVE, meta > 1).withProperty(TYPE, meta % 2 == 1);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		return (state.getValue(ACTIVE) ? 2 : 0) + (state.getValue(TYPE) ? 1 : 0);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
	{
		list.add(new ItemStack(itemIn, 1, 0));
		list.add(new ItemStack(itemIn, 1, 1));
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		tooltip.add(I18n.format("tomsMod.tooltip.accepts", stack.getMetadata() == 0 ? "HV" : "MV"));
		if(stack.hasTagCompound()){
			tooltip.add(I18n.format("tomsMod.tooltip.energyStored", stack.getTagCompound().getCompoundTag("BlockEntityTag").getDouble("energy")));
		}
	}
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(ACTIVE, false);
	}
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		TomsModUtils.setBlockState(worldIn, pos, state.withProperty(TYPE, stack.getMetadata() == 1));
	}
	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(TYPE) ? 1 : 0;
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityWirelessCharger te = (TileEntityWirelessCharger) worldIn.getTileEntity(pos);
		ItemStack s = new ItemStack(this, 1, damageDropped(state));
		s.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = new NBTTagCompound();
		te.writeToStackNBT(tag);
		s.getTagCompound().setTag("BlockEntityTag", tag);
		spawnAsEntity(worldIn, pos, s);
		super.breakBlock(worldIn, pos, state);
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
	public void registerIcons() {
		CoreInit.registerRender(Item.getItemFromBlock(this), 0, "tomsmodenergy:wirelessCharger");
		CoreInit.registerRender(Item.getItemFromBlock(this), 1, "tomsmodenergy:wirelessCharger");
	}
	@Override
	public ItemBlock createItemBlock() {
		return new WirelessChargerItemBlock();
	}
}
