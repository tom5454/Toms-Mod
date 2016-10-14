package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import com.tom.lib.Configs;

import com.tom.core.tileentity.TileEntityMonitor;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Configs.COMPUTERCRAFT)
public class Monitor extends BlockMonitorBase implements IPeripheralProvider{
	/*@SideOnly(Side.CLIENT)
	private IIcon side;
	//@SideOnly(Side.CLIENT)
	//private IIcon back;
	@SideOnly(Side.CLIENT)
	private IIcon front;
	/*@SideOnly(Side.CLIENT)
	private IIcon side1;
	@SideOnly(Side.CLIENT)
	private IIcon side2;
	@SideOnly(Side.CLIENT)
	private IIcon side3;
	@SideOnly(Side.CLIENT)
	private IIcon side4;*/
	protected Monitor(Material arg0) {
		super(arg0);
	}
	public Monitor(){
		this(Material.GLASS);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	/*@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister){
		this.blockIcon = iconregister.registerIcon("minecraft:monitorSide");
//		this.back = iconregister.registerIcon("minecraft:monitorSide");
//		this.side1 = iconregister.registerIcon("minecraft:monitorSide");
//		this.side2 = iconregister.registerIcon("minecraft:monitorSide");
//		this.side3 = iconregister.registerIcon("minecraft:monitorSide");
//		this.side4 = iconregister.registerIcon("minecraft:monitorSide");
		this.front = iconregister.registerIcon("minecraft:monitor16");
//		this.blockIcon = iconregister.registerIcon("minecraft:tm/Gray");
		this.side = iconregister.registerIcon("minecraft:tm/transparent");
	}

	/*public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		return this.side;
	}*/
	/*public IIcon getIcon(IBlockAccess world, int xC, int yC, int zC, int side){
		TileEntity tilee = world.getTileEntity(xC, yC, zC);
		TileEntityMonitorBlack te = ((TileEntityMonitorBlack)tilee);
		if(side == te.direction){
			return this.front;
		}else{
			return this.blockIcon;
		}

	}
	public IIcon getIcon(int side, int meta){
		if(side == 3){
			return this.front;
		}else{
			return this.blockIcon;
		}
	}*/
	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityMonitor();
	}


	/*public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0){
			if (x == hitX){
				return true;
			}else{
				return false;
			}
		}else if (meta == 1){
			if (x + 1 == hitX){
				return true;
			}else{
				return false;
			}
		}else if (meta == 3){
			if (z == hitZ){
				return true;
			}else{
				return false;
			}
		}else if (meta == 4){
			if (z + 1 == hitZ){
				return true;
			}else{
				return false;
			}
		}else if (meta == 5){
			if (y == hitY){
				return true;
			}else{
				return false;
			}
		}else if (meta == 6){
			if (y + 1 == hitY){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}*/

	/*public void onBlockPlacedBy(World world, int x, int y, int z,EntityLivingBase entity, ItemStack itemstack){
		/*int meta = itemstack.getItemDamage();
		int direction = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		TileEntity tilee = world.getTileEntity(x, y, z);
		TileEntityMonitorBlack te = ((TileEntityMonitorBlack)tilee);
		te.setMeta(direction);
		te.setColor(meta);
		te.onBlockPlaced(world, x, y, z);
	}*/
	/*@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		//if(!world.isRemote){
		/*for(int i = 0;i<16;i++){
			for(int j = 0;j<16;j++){
				te.screen[i][j] = 0x000000;
			}
		}
		int s = 1;
		te.screen[s][0] = 0xFFFFFF;
		te.screen[s+1][0] = 0xFFFFFF;
		te.screen[s+2][0] = 0xFFFFFF;
		te.screen[s+2][1] = 0xFFFFFF;
		te.screen[s][1] = 0xFFFFFF;
		te.screen[s][2] = 0xFFFFFF;
		te.screen[s][3] = 0xFFFFFF;
		te.screen[s+1][3] = 0xFFFFFF;
		te.screen[s+2][3] = 0xFFFFFF;
		te.screen[s+2][2] = 0xFFFFFF;
		te.screen[s+4][1] = 0xFFFFFF;
		te.screen[s+5][0] = 0xFFFFFF;
		te.screen[s+5][1] = 0xFFFFFF;
		te.screen[s+5][2] = 0xFFFFFF;
		te.screen[s+5][3] = 0xFFFFFF;
		te.screen[s+7][3] = 0xFFFFFF;
		te.screen[s+10][3] = 0x00FA9A;
		te.screen[s+10][4] = 0xFFFFFF;
		te.screen[s+10][5] = 0xFFFFFF;

		te.screen[s+1][7] = 0xFFFFFF;
		te.screen[s+1][8] = 0xFFFFFF;
		te.screen[s+2][9] = 0xFFFFFF;
		te.screen[s+3][7] = 0xFFFFFF;
		te.screen[s+3][8] = 0xFFFFFF;
		te.screen[s+4][9] = 0xFFFFFF;
		te.screen[s+5][7] = 0xFFFFFF;
		te.screen[s+5][8] = 0xFFFFFF;
		te.screen[s+7][9] = 0xFFFFFF;
		te.screen[s+7][7] = 0xFFFFFF;
		te.screen[s+7][8] = 0xFFFFFF;
		te.screen[s+9][9] = 0xFFFFFF;
		te.screen[s+9][7] = 0xFFFFFF;
		te.screen[s+9][8] = 0xFFFFFF;
		te.screen[s+10][7] = 0xFFFFFF;
		/*te.screen[0][0][0] = 255;
		te.screen[0][0][1] = 255;
		te.screen[0][0][2] = 255;
		te.screen[0][0][3] = 255;*/
	//world.markBlockForUpdate(x, y, z);
	//}

	//}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		TileEntity tilee = world.getTileEntity(pos);
		TileEntityMonitor te = ((TileEntityMonitor)tilee);
		return te.onBlockActivated(!world.isRemote, side, hitX, hitY, hitZ, player);
	}
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		TileEntity tilee = world.getTileEntity(pos);
		if(tilee instanceof TileEntityMonitor){
			TileEntityMonitor te = ((TileEntityMonitor)tilee);
			return te.direction == side.ordinal() ? null : te;
		}
		return null;
	}

	/*public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		ItemStack itemstack = player.getHeldItem();
		if (itemstack != null){
			TileEntity tilee = world.getTileEntity(x, y, z);
			TileEntityMonitorBlack te = ((TileEntityMonitorBlack)tilee);
			if (itemstack.getItem() == mcreator_wrench.block){
				int meta = world.getBlockMetadata(x, y, z);
				te.setMeta(meta == 5 ? 0 : meta + 1);
				te.updateEntity();
				return true;
			} else {
				return false;
			}
		}else{
			return false;
		}
	}*/

	//@SideOnly(Side.CLIENT)
	//public void updateTick(World world, int i, int j, int k, Random random){

	//}
	@Override
	public boolean isSideSolid(IBlockState s, IBlockAccess world, BlockPos pos, EnumFacing side){
		return true;
	}
}
