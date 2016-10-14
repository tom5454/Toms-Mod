package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;

import com.tom.energy.tileentity.TileEntityFusionInjector;

public class FusionInjector extends BlockContainerTomsMod {

	/*@SideOnly(Side.CLIENT)
	private IIcon top;
	@SideOnly(Side.CLIENT)
	private IIcon side;
	@SideOnly(Side.CLIENT)
	private IIcon bottom;
	@SideOnly(Side.CLIENT)
	private IIcon topPowered;*/
	public static final PropertyBool READY = PropertyBool.create("ready");
	protected FusionInjector(Material arg0) {
		super(arg0);
	}
	public FusionInjector(){
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityFusionInjector();
	}
	/*public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
			player.openGui(CoreInit.modInstance, GuiHandler.GuiIDs.Injector.ordinal(), world, x, y, z);
		return true;
	}//*/
	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:fusionCase");
		this.side = iconregister.registerIcon("minecraft:fusionCase");
		this.top = iconregister.registerIcon("minecraft:fusionGlassInActive");
		this.bottom = iconregister.registerIcon("minecraft:fusionRf");
		this.topPowered = iconregister.registerIcon("minecraft:fusionGlassActive");
	}

	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		if(side == 1){
			return ((TileEntityInjector)world.getTileEntity(x, y, z)).ready(false) ? topPowered : top;
		}else if(side == 0){
			return this.bottom;
		}else{
			return this.blockIcon;
		}
	}*/
	@Override
	public boolean hasComparatorInputOverride(IBlockState s) {
		return true;
	}
	@Override
	public int getComparatorInputOverride(IBlockState s, World world, BlockPos pos) {
		TileEntityFusionInjector te = ((TileEntityFusionInjector)world.getTileEntity(pos));
		double energy = te.getEnergyStored();
		int rs = MathHelper.floor_double(energy / (te.getMaxEnergyStored() / 14));
		return te.ready(true) ? 15 : (te.getEnergyStored() == 0 ? 0 : rs != 0 ? rs : (te.ready(false) ? 1 : 0));
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {READY});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(READY, meta == 1);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		return state.getValue(READY) ? 1 : 0;
	}
}
