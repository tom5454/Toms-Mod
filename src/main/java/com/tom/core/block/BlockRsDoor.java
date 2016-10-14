package com.tom.core.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.CoreInit;

import com.tom.core.tileentity.TileEntityRSDoor;

public class BlockRsDoor extends BlockContainerTomsMod {
	public BlockRsDoor() {
		super(Material.WOOD);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityRSDoor();
	}
	@Override
	public Item getItemDropped(IBlockState state, Random par2Random, int par3){
		return CoreInit.rsDoor;
	}

	/*@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z)
	{
		return CoreInit.rsDoor;
	}*/

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		TileEntityRSDoor TE = (TileEntityRSDoor) source.getTileEntity(pos);
		return setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3F/16F, TE != null ? TE.dir : EnumFacing.NORTH);
	}
	@Override
	public boolean isOpaqueCube(IBlockState s){
		return false;
	}
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState block){
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		TileEntity tile = world.getTileEntity(pos);
		if(tile != null && tile instanceof TileEntityRSDoor){
			TileEntityRSDoor TE = (TileEntityRSDoor) tile;
			boolean bottom = TE.isBottom;
			Block b = world.getBlockState(new BlockPos(x, bottom ? y+1 : y-1, z)).getBlock();
			if(b == CoreInit.blockRsDoor){
				b.breakBlock(world, new BlockPos(x, bottom ? y+1 : y-1, z), block);
				world.setBlockState(new BlockPos(x, bottom ? y+1 : y-1, z), Blocks.AIR.getDefaultState());
			}
		}
		super.breakBlock(world, pos, block);
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		TileEntityRSDoor te = (TileEntityRSDoor) world.getTileEntity(pos);
		te.activate(player, true, heldItem);
		return true;
	}
	/*@Override
    public boolean renderAsNormalBlock(){
        return false;
    }
	/*@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		TileEntityRSDoor te = (TileEntityRSDoor)world.getTileEntity(x, y, z);
    	ItemStack stack = te.camoStack;
    	if(stack != null && stack.getItem() instanceof ItemBlock) {
    		Block block = ((ItemBlock)stack.getItem()).field_150939_a;
    		return block.getIcon(side, stack.getItemDamage());
    	} else {
    		return super.getIcon(world, x, y, z, side);
    	}
    }*/
	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:planks_oak");
	}*/
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target,
			World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(CoreInit.rsDoor);
	}
	/*public static class SmartBlockModel implements ISmartBlockModel{

		public SmartBlockModel(IBakedModel unCamouflagedModel/*, IBakedModel transparentModel*//*)
		{
			modelWhenNotCamouflaged = unCamouflagedModel;
			//modelWhenTransparent = transparentModel;
		}

		// create a tag (ModelResourceLocation) for our model.
		public static final ModelResourceLocation modelResourceLocation
		= new ModelResourceLocation("oak_planks");
		public static final ModelResourceLocation modelFResourceLocation
		= new ModelResourceLocation("oak_planks");
		/*public static final ModelResourceLocation modelTResourceLocation
          = new ModelResourceLocation("tomsmodcore:EnderS_transp");*/
	// This method is used to create a suitable IBakedModel based on the IBlockState of the block being rendered.
	// If IBlockState is an instance of IExtendedBlockState, you can use it to pass in any information you want.
	// Some folks return a new instance of the same ISmartBlockModel; I think it is more logical to return a different
	//   class which implements IBakedModel instead of ISmartBlockModel, but it's a matter of taste.
	//  BEWARE! Rendering is multithreaded so your ISmartBlockModel must be thread-safe, preferably immutable.
	/*@Override
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
				//if (copiedBlockIBlockState != UNCAMOUFLAGED_BLOCK) {
				// Retrieve the IBakedModel of the copied block and return it.
				if(copiedBlockIBlockState != null){
					IBakedModel copiedBlockModel = blockModelShapes.getModelForState(copiedBlockIBlockState);
					if (copiedBlockModel instanceof ISmartBlockModel) {
						copiedBlockModel = ((ISmartBlockModel) copiedBlockModel).handleBlockState(copiedBlockIBlockState);
					}
					retval = copiedBlockModel;
				}
				final List<BakedQuad> quads = TomsModUtils.bakeBlockModelToShape(iBlockState, TomsModUtils.getModelJSON(modelFResourceLocation), iExtendedBlockState.getValue(FACING));
				final IBakedModel val = retval;
				return new IBakedModel() {

					@Override
					public boolean isGui3d() {
						return val.isGui3d();
					}

					@Override
					public boolean isBuiltInRenderer() {
						return val.isBuiltInRenderer();
					}

					@Override
					public boolean isAmbientOcclusion() {
						return val.isAmbientOcclusion();
					}

					@Override
					public TextureAtlasSprite getParticleTexture() {
						return SmartBlockModel.this.getParticleTexture();
					}

					@Override
					public ItemCameraTransforms getItemCameraTransforms() {
						return val.getItemCameraTransforms();
					}

					@Override
					public List<BakedQuad> getGeneralQuads() {
						return quads;
					}

					@Override
					public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
						return quads;
					}
				};
			}
			return retval;
		}

		private IBakedModel modelWhenNotCamouflaged;
		//private IBakedModel modelWhenTransparent;

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
	/*@SuppressWarnings("rawtypes")
	@Override
	protected BlockStateContainer createBlockState() {
		IProperty [] listedProperties = new IProperty[0]; // no listed properties
		IUnlistedProperty [] unlistedProperties = new IUnlistedProperty[] {CAMOBLOCK, FACING};
		return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	}
	public static final UnlistedPropertyBlockState CAMOBLOCK = new UnlistedPropertyBlockState();
	public static final UnlistedPropertyFacing FACING = new UnlistedPropertyFacing();
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (state instanceof IExtendedBlockState && tile instanceof TileEntityRSDoor && ((TileEntityRSDoor)tile).camoStack != null) {  // avoid crash in case of mismatch
			IExtendedBlockState retval = (IExtendedBlockState)state;
			TileEntityRSDoor te = (TileEntityRSDoor) tile;
			retval = retval.withProperty(CAMOBLOCK, TomsModUtils.getBlockStateFrom(te.camoStack)).withProperty(FACING, te.dir);
			return retval;
		}
		return state;
	}*/
	/*@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}*/
	/*public static class UnlistedPropertyFacing implements IUnlistedProperty<EnumFacing>{

		@Override
		public String getName() {
			return "facing";
		}

		@Override
		public boolean isValid(EnumFacing value) {
			return true;
		}

		@Override
		public Class<EnumFacing> getType() {
			return EnumFacing.class;
		}

		@Override
		public String valueToString(EnumFacing value) {
			return value.getName();
		}

	}*/
	@Override
	public int getRenderType(){
		return 2;
	}
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}
	@Override
	public boolean isFullCube(IBlockState s)
	{
		return false;
	}
}
