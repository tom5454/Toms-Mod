package com.tom.factory.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidUtil;

import com.tom.api.block.BlockMultiblockPart;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityMBFluidPort;

public class MultiblockFluidHatch extends BlockMultiblockPart {
	/*@SideOnly(Side.CLIENT)
	private IIcon out;*/
	protected MultiblockFluidHatch(Material arg0) {
		super(arg0);
	}
	public MultiblockFluidHatch(){
		this(Material.IRON);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityMBFluidPort();
	}
	/*public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityMBFluidPort te = (TileEntityMBFluidPort)world.getTileEntity(x, y, z);
		boolean input = te.isInput();
		if(!input){
			return this.out;
		}else{
			return this.blockIcon;
		}
	}
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.out = iconregister.registerIcon("minecraft:mbFluidOut");
		this.blockIcon = iconregister.registerIcon("minecraft:mbFluidIn");
	}*/
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(!world.isRemote){
			TileEntity tilee = world.getTileEntity(pos);
			if(tilee instanceof TileEntityMBFluidPort){
				TileEntityMBFluidPort te = (TileEntityMBFluidPort)tilee;
				if(heldItem != null){
					if(CoreInit.isWrench(heldItem,player)){
						//te.setMode(!te.isInput());
						TomsModUtils.setBlockState(world, pos, state.withProperty(OUTPUT, !state.getValue(OUTPUT)));
						return true;
					}else /*if(heldItem.getItem() instanceof IFluidContainerItem){
						IFluidContainerItem fluidItem = (IFluidContainerItem) heldItem.getItem();
						if(!world.isRemote && fluidItem.getFluid(heldItem) != null){
							FluidStack drained = fluidItem.drain(heldItem, fluidItem.getCapacity(heldItem), false);
							int fill = te.fill(side, drained, false);
							if(fill == fluidItem.getCapacity(heldItem)){
								te.fill(side, fluidItem.drain(heldItem, fill, !player.capabilities.isCreativeMode), true);
							}
						}else{
							FluidStack drained = te.drain(side, fluidItem.getCapacity(heldItem), false);
							int fill = fluidItem.fill(heldItem, drained, false);
							if(fill == fluidItem.getCapacity(heldItem)){
								fluidItem.fill(heldItem, te.drain(side, fill, true), true);
							}
						}
						return true;
					}else if(heldItem.getItem() == Items.lava_bucket){
						if(!world.isRemote){
							int fill = te.fill(side, new FluidStack(FluidRegistry.LAVA,1000), false);
							if(fill == 1000){
								te.fill(side, new FluidStack(FluidRegistry.LAVA,1000), true);
								if(!player.capabilities.isCreativeMode)player.setHeldItem(hand, new ItemStack(Items.bucket));
							}
						}
						return true;
					}else if(heldItem.getItem() == Items.water_bucket){
						if(!world.isRemote){
							int fill = te.fill(side, new FluidStack(FluidRegistry.WATER,1000), false);
							if(fill == 1000){
								te.fill(side, new FluidStack(FluidRegistry.WATER,1000), true);
								if(!player.capabilities.isCreativeMode)player.setHeldItem(hand, new ItemStack(Items.bucket));
							}
						}
						return true;
					}else if(heldItem.getItem() == Items.bucket){

					}else{
						//player.openGui(CoreInit.modInstance, GuiHandler.GuiIDs.mbFluidHatch.ordinal(), world, x, y, z);
					}*/{
						return FluidUtil.interactWithFluidHandler(heldItem, te.getTankOnSide(side), player);
					}
				}else{
					//player.openGui(CoreInit.modInstance, GuiHandler.GuiIDs.mbFluidHatch.ordinal(), world, x, y, z);
				}
			}/*else{
				System.out.println(tilee);
			}//*/
		}
		return true;
	}
	@Override
	public void breakBlockI(World world, int x, int y, int z, IBlockState block) {

	}
	public static final PropertyBool OUTPUT = PropertyBool.create("out");
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {OUTPUT});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(OUTPUT, meta == 1);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		return state.getValue(OUTPUT) ? 1 : 0;
	}
	@Override
	public void onNeighborBlockChangeI(IBlockAccess world, int x, int y, int z, IBlockState b, Block block) {

	}
}
