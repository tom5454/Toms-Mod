package com.tom.core.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.entity.EntityCamera;
import com.tom.core.tileentity.TileEntityCamera;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Camera extends BlockContainerTomsMod {

	public Camera() {
		super(Material.GLASS);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	/*@SideOnly(Side.CLIENT)
	private IIcon tr;*/
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityCamera();
	}
	/*@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		@SuppressWarnings("unchecked")
		List<Entity> entities = world.getEntitiesWithinAABB(EntityCamera.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
		for(Entity e : entities){
			if(e instanceof EntityCamera){
				e.setDead();
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}*/
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState bs, EntityLivingBase entity, ItemStack itemstack){
		if(!world.isRemote){
			TileEntityCamera te = (TileEntityCamera) world.getTileEntity(pos);
			//System.out.println(entity.rotationYaw + " " + entity.rotationPitch);
			//ForgeDirection l = TomsMathHelper.getDirectionFacing(entity, true);
			te.pitch = -entity.rotationPitch;
			te.yaw = 360 - entity.rotationYaw;
			//world.setBlockMetadataWithNotify(x, y, z, l.ordinal(), 3);
		}
	}
	@Override
	public boolean isOpaqueCube(IBlockState s){
		return false;
	}
	/*@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		return Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode ? this.blockIcon : this.tr;
    }
    @SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.tr = iconregister.registerIcon("minecraft:tm/transparent");
		this.blockIcon = iconregister.registerIcon("minecraft:tm/Gray");
	}*/
	/*@Override
	public void setBlockBoundsForItemRender(){
		setBlockBounds(0.188F,0.188F,0.188F,0.812F,0.812F,0.812F);
	}//*/
	/*private void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, ForgeDirection dir)
    {
        switch (dir) {
            case DOWN:
                setBlockBounds(minX, 1.0F - maxZ, minY, maxX, 1.0F - minZ, maxY);
                break;
            case UP:
                setBlockBounds(minX, minZ, minY, maxX, maxZ, maxY);
                break;
            case NORTH:
                setBlockBounds(1.0F - maxX, minY, 1.0F - maxZ, 1.0F - minX, maxY, 1.0F - minZ);
                break;
            case EAST:
                setBlockBounds(minZ, minY, 1.0F - maxX, maxZ, maxY, 1.0F - minX);
                break;
            case WEST:
                setBlockBounds(1.0F - maxZ, minY, minX, 1.0F - minZ, maxY, maxX);
                break;
            default:
                setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
                break;
        }
    }*/
	@Override
	public int getRenderType() {
		return -1;
	}
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState worldIn, World pos, BlockPos state) {
		try{
			Minecraft mc = Minecraft.getMinecraft();
			if(mc != null && mc.getRenderViewEntity() instanceof EntityCamera){
				return new AxisAlignedBB(0,0,0,0,0,0);
			}
		}catch(Exception e){

		}
		return new AxisAlignedBB(0.188F,0.188F,0.188F,0.812F,0.812F,0.812F);
	}
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return getCollisionBoundingBox(blockState, worldIn, pos);
	}
}
