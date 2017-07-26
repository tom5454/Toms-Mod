package com.tom.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;

import com.tom.core.tileentity.TileEntityEnderSensor;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.InterfaceList({@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT),
		/*@Optional.Interface(iface = "com.cricketcraft.chisel.api.IFacade", modid = Configs.CHISEL)*/})
public class EnderPlayerSensor extends BlockContainerTomsMod implements IPeripheralProvider {
	public EnderPlayerSensor() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	/*@SideOnly(Side.CLIENT)
	private IIcon tr;*/
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityEnderSensor();
	}

	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing f) {
		TileEntity te = world.getTileEntity(pos);
		return te instanceof TileEntityEnderSensor ? (IPeripheral) te : null;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (!world.isRemote) {
			TileEntityEnderSensor te = (TileEntityEnderSensor) world.getTileEntity(pos);
			if (te.camoStack != null && heldItem != null && CoreInit.isWrench(player, hand)) {
				if (player.isSneaking()) {
					ItemStack camoStack = te.camoStack;
					te.camoStack = null;
					EntityItem itemEntity = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), camoStack);
					if (!player.capabilities.isCreativeMode)
						world.spawnEntity(itemEntity);
					te.transparent = false;
				} else {
					if (te.camoStack.getItem() instanceof ItemBlock) {
						Block b = ((ItemBlock) te.camoStack.getItem()).block;
						if (b == Blocks.GLASS) {
							te.transparent = !te.transparent;
						}
					}
				}
			} else if (te.camoStack == null) {
				if (heldItem != null && heldItem.getItem() instanceof ItemBlock) {
					ItemStack camoStack = null;
					if (player.capabilities.isCreativeMode) {
						camoStack = heldItem.copy();
						camoStack.setCount(1);
					} else {
						camoStack = heldItem.splitStack(1);
					}
					te.camoStack = camoStack;
				}
				te.transparent = false;
			}
			te.markDirty();
			te.markBlockForUpdate(pos);
		}
		return true;
	}

	/*@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityEnderSensor te = (TileEntityEnderSensor)world.getTileEntity(x, y, z);
		if(!te.transparent){
			ItemStack stack = te.camoStack;
			if(stack != null && stack.getItem() instanceof ItemBlock) {
				Block block = ((ItemBlock)stack.getItem()).field_150939_a;
				return block.getIcon(side, stack.getItemDamage());
			} else {
				return super.getIcon(world, x, y, z, side);
			}
		}else return this.tr;
	}
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.tr = iconregister.registerIcon("minecraft:tm/transparent");
		this.blockIcon = iconregister.registerIcon("minecraft:tm/enderSensor_blank");
	}*/
	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public int getRenderType() {
		return 2;
	}
	/* @Optional.Method(modid = Configs.CHISEL)
	@Override
	public Block getFacade(IBlockAccess world, int x, int y, int z, int side) {
		TileEntityEnderSensor te = (TileEntityEnderSensor)world.getTileEntity(x, y, z);
		if(!te.transparent){
			ItemStack stack = te.camoStack;
			if(stack != null && stack.getItem() instanceof ItemBlock) {
				Block block = ((ItemBlock)stack.getItem()).field_150939_a;
				return block;
			}
		}
		return null;
	}
	@Optional.Method(modid = Configs.CHISEL)
	@Override
	public int getFacadeMetadata(IBlockAccess world, int x, int y, int z,
			int side) {
		TileEntityEnderSensor te = (TileEntityEnderSensor)world.getTileEntity(x, y, z);
		if(!te.transparent){
			ItemStack stack = te.camoStack;
			if(stack != null && stack.getItem() instanceof ItemBlock) {
				return stack.getItemDamage();
			}
		}
		return 0;
	}*/
	/*public static class SmartBlockModel implements ISmartBlockModel{
	
		public SmartBlockModel(IBakedModel unCamouflagedModel, IBakedModel transparentModel)
		{
			modelWhenNotCamouflaged = unCamouflagedModel;
			modelWhenTransparent = transparentModel;
		}
	
		// create a tag (ModelResourceLocation) for our model.
		public static final ModelResourceLocation modelResourceLocation
		= new ModelResourceLocation("tomsmodcore:EnderPlayerSensor");
		public static final ModelResourceLocation modelTResourceLocation
		= new ModelResourceLocation("tomsmodcore:EnderS_transp");
		// This method is used to create a suitable IBakedModel based on the IBlockState of the block being rendered.
		// If IBlockState is an instance of IExtendedBlockState, you can use it to pass in any information you want.
		// Some folks return a new instance of the same ISmartBlockModel; I think it is more logical to return a different
		//   class which implements IBakedModel instead of ISmartBlockModel, but it's a matter of taste.
		//  BEWARE! Rendering is multithreaded so your ISmartBlockModel must be thread-safe, preferably immutable.
		@Override
		public IBakedModel handleBlockState(IBlockState iBlockState)
		{
			IBakedModel retval = modelWhenNotCamouflaged;  // default
			//IBlockState UNCAMOUFLAGED_BLOCK = CoreInit.EnderPlayerSensor.getDefaultState();
			Minecraft mc = Minecraft.getMinecraft();
			BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
			BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
			// Extract the block to be copied from the IExtendedBlockState, previously set by Block.getExtendedState()
			// If the block is null, the block is not camouflaged so use the uncamouflaged model.
			if (iBlockState instanceof IExtendedBlockState) {
				IExtendedBlockState iExtendedBlockState = (IExtendedBlockState) iBlockState;
				IBlockState copiedBlockIBlockState = iExtendedBlockState.getValue(CAMOBLOCK);
				Boolean transparent = iExtendedBlockState.getValue(TRANSPARENT);
				transparent = transparent != null ? transparent : false;
				//if (copiedBlockIBlockState != UNCAMOUFLAGED_BLOCK) {
				// Retrieve the IBakedModel of the copied block and return it.
				if(transparent){
					retval = modelWhenTransparent;
				}else{
					if(copiedBlockIBlockState != null){
						IBakedModel copiedBlockModel = blockModelShapes.getModelForState(copiedBlockIBlockState);
						if (copiedBlockModel instanceof ISmartBlockModel) {
							copiedBlockModel = ((ISmartBlockModel) copiedBlockModel).handleBlockState(copiedBlockIBlockState);
						}
						retval = copiedBlockModel;
					}
				}
			}
			return retval;
		}
	
		private IBakedModel modelWhenNotCamouflaged;
		private IBakedModel modelWhenTransparent;
	
		// getTexture is used directly when player is inside the block.  The game will crash if you don't use something
		//   meaningful here.
		@Override
		public TextureAtlasSprite getParticleTexture() {
			return modelWhenNotCamouflaged.getParticleTexture();
		}
	
		// The methods below are all unused for CamouflageISmartBlockModelFactory because we always return a vanilla model
		//  from handleBlockState.
	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public List getFaceQuads(EnumFacing p_177551_1_) {
			throw new UnsupportedOperationException();
		}
	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public List getGeneralQuads() {
			throw new UnsupportedOperationException();
		}
	
		@Override
		public boolean isAmbientOcclusion() {
			throw new UnsupportedOperationException();
		}
	
		@Override
		public boolean isGui3d() {
			throw new UnsupportedOperationException();
		}
	
		@Override
		public boolean isBuiltInRenderer() {
			throw new UnsupportedOperationException();
		}
	
		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			throw new UnsupportedOperationException();
		}
	
	}*/
	/*public static class UnlistedPropertyBlockState implements IUnlistedProperty<IBlockState>
	{
		@Override
		public String getName() {
			return "UnlistedPropertyBlockState";
		}
	
		@Override
		public boolean isValid(IBlockState value) {
			return true;
		}
	
		@Override
		public Class<IBlockState> getType() {
			return IBlockState.class;
		}
	
		@Override
		public String valueToString(IBlockState value) {
			return value.toString();
		}
	}
	public static class UnlistedPropertyBoolean implements IUnlistedProperty<Boolean>
	{
		@Override
		public String getName() {
			return "UnlistedPropertyBlockState";
		}
	
		@Override
		public boolean isValid(Boolean value) {
			return true;
		}
	
		@Override
		public Class<Boolean> getType() {
			return Boolean.class;
		}
	
		@Override
		public String valueToString(Boolean value) {
			return value.toString();
		}
	
	
	}
	@SuppressWarnings("rawtypes")
	@Override
	protected BlockStateContainer createBlockState() {
		IProperty [] listedProperties = new IProperty[0]; // no listed properties
		IUnlistedProperty [] unlistedProperties = new IUnlistedProperty[] {CAMOBLOCK,TRANSPARENT};
		return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	}
	public static final UnlistedPropertyBlockState CAMOBLOCK = new UnlistedPropertyBlockState();
	public static final UnlistedPropertyBoolean TRANSPARENT = new UnlistedPropertyBoolean();
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (state instanceof IExtendedBlockState && tile instanceof TileEntityEnderSensor && ((TileEntityEnderSensor)tile).camoStack != null) {  // avoid crash in case of mismatch
			IExtendedBlockState retval = (IExtendedBlockState)state;
			TileEntityEnderSensor te = (TileEntityEnderSensor) tile;
			retval = retval.withProperty(CAMOBLOCK, TomsModUtils.getBlockStateFrom(te.camoStack)).withProperty(TRANSPARENT, te.transparent);
			return retval;
		}
		return state;
	}*/
	/*@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}*/
}
