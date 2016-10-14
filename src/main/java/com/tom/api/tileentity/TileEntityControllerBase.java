package com.tom.api.tileentity;

import static com.tom.api.block.BlockControllerBase.STATE;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.FluidStack;

import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntityMBFluidPort;
import com.tom.factory.tileentity.TileEntityMBHatch;
import com.tom.factory.tileentity.TileEntityMBPressurePortBase;

public abstract class TileEntityControllerBase extends TileEntityMultiblockPartBase implements IMBController{
	protected TileEntityControllerBase(int w, int h){
		this.handler = new MultiblockHandler(this,w,h, MultiblockPartList.Casing);
		this.width = this.handler.width;
		this.height = this.handler.height;
	}
	protected TileEntityControllerBase(){
		this.handler = new MultiblockHandler(this,3,4, MultiblockPartList.Casing);
		this.width = this.handler.width;
		this.height = this.handler.height;
	}
	protected TileEntityControllerBase(MultiblockPartList frame){
		this.handler = new MultiblockHandler(this,3,4, frame);
		this.width = this.handler.width;
		this.height = this.handler.height;
	}
	protected TileEntityControllerBase(int w, int h, MultiblockPartList frame){
		this.handler = new MultiblockHandler(this,w,h, frame);
		this.width = this.handler.width;
		this.height = this.handler.height;
	}
	protected TileEntityControllerBase(int mode){
		this.handler = new MultiblockHandler(this,3,4, MultiblockPartList.Casing,mode);
		this.width = this.handler.width;
		this.height = this.handler.height;
	}
	protected TileEntityControllerBase(MultiblockPartList frame, int mode){
		this.handler = new MultiblockHandler(this,3,4, frame,mode);
		this.width = this.handler.width;
		this.height = this.handler.height;
	}
	protected TileEntityControllerBase(int w, int h, MultiblockPartList frame, int mode){
		this.handler = new MultiblockHandler(this,w,h, frame,mode);
		this.width = this.handler.width;
		this.height = this.handler.height;
	}
	protected TileEntityControllerBase(int w, int h,int mode){
		this.handler = new MultiblockHandler(this,w,h, MultiblockPartList.Casing,mode);
		this.width = this.handler.width;
		this.height = this.handler.height;
	}
	protected MultiblockHandler handler;
	protected int width ;
	protected int height;
	protected int heat = 0;
	protected ItemStack[] items = new ItemStack[]{};
	protected FluidStack[] fluids = new FluidStack[]{};
	public boolean active = false;
	//public int direction = 0;
	protected boolean lastActive = false;
	private TileEntityMBFluidPort lastest;
	protected boolean redstone = false;
	protected int redstoneMode = 1;
	protected List<MultiblockPartList> parts = new ArrayList<MultiblockPartList>();
	@Override
	public List<MultiblockPartList> parts() {
		return this.parts;
	}
	@Override
	public void updateEntity(IBlockState state){
		if(worldObj.isRemote)return;
		this.handler.updateEntity(this.worldObj);
		if(this.active != this.lastActive){
			markBlockForUpdate(pos);
		}
		this.redstone = worldObj.isBlockIndirectlyGettingPowered(pos)>0;
		boolean rs = this.getRedstone();
		if(rs && this.formed && !this.worldObj.isRemote) this.updateEntityI();
		if(this.formed) this.updateEntity(rs);
		if(formed){
			if(this.active){
				if(state.getValue(STATE) != 2){
					TomsModUtils.setBlockState(worldObj, pos, state.withProperty(STATE, 2));
				}
			}else{
				if(state.getValue(STATE) != 1){
					TomsModUtils.setBlockState(worldObj, pos, state.withProperty(STATE, 1));
				}
			}
		}else{
			if(state.getValue(STATE) != 0){
				TomsModUtils.setBlockState(worldObj, pos, state.withProperty(STATE, 0));
			}
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.handler.importFromNBT(tag.getCompoundTag("MultiblockHandler"));
		//this.direction = tag.getInteger("d");
		this.redstoneMode = tag.getInteger("redstone");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setTag("MultiblockHandler", this.handler.exportToNBT());
		//tag.setInteger("d", this.direction);
		tag.setInteger("redstone", this.redstoneMode);
		return tag;
	}
	public void blockBreak(){
		this.handler.breakBlock(this.worldObj);
		this.breakBlock();
		this.onBlockBreak();
	}
	protected void setSize(int w, int h){
		this.handler.width = w;
		this.handler.height = h;
	}
	@Override
	public void validate(){
		this.handler.validate(this.worldObj);
		this.handler.setParts(this.parts());
		this.validateI();
	}
	public void onNeighborChange(){
		this.handler.onNeighborChange(this.worldObj);
	}
	@Override
	public void receive(int x, int y, int z, int msg){
		this.handler.receive(x,y,z,msg, this.worldObj);
		if(msg == 1 || msg == 2){

		}else{
			if(msg > -127 && msg < 128){
				if (msg == 201){
					msg = 1;
				}else if (msg == 202){
					msg = 2;
				}
				this.receiveMessage(x, y, z, (byte) msg);
			}
		}

	}
	public int[][] getTileEntityList(MultiblockPartList partName){
		return this.handler.getTileEntityListFromMap(partName);
	}
	public TileEntityMBFluidPort getTileEntityList(boolean isInput, int p){
		List<int[]> current =  this.handler.deviceMap.get(MultiblockPartList.FluidPort);
		TileEntityMBFluidPort ret = this.lastest;
		int i = 0;
		if(current != null)
			for(int[] c : current){
				TileEntityMultiblockPartBase te = (TileEntityMultiblockPartBase) worldObj.getTileEntity(new BlockPos(c[0], c[1], c[2]));
				if(te != null && te instanceof TileEntityMBFluidPort){
					TileEntityMBFluidPort tilee = (TileEntityMBFluidPort) te;
					if(tilee.isInput() == isInput){
						ret = (TileEntityMBFluidPort) te;
						this.lastest = ret;
						i++;
					}
					if((i-1) == p){
						break;
					}
				}
			}
		return ret;
	}
	public boolean isActive(){
		return this.active;
	}
	/*@Override
	public void writeToPacket(NBTTagCompound buf){
		buf.writeInt(this.direction);
		buf.writeBoolean(this.active);
		buf.writeBoolean(this.formed);
	}
	@Override
	public void readFromPacket(ByteBuf buf){
		//this.direction = buf.readInt();
		this.active = buf.readBoolean();
		this.formed = buf.readBoolean();
		//System.out.println(formed);
		this.worldObj.markBlockRangeForRenderUpdate(pos.getX(),pos.getY(),pos.getZ(),pos.getX(),pos.getY(),pos.getZ());
	}*/
	public int[][] getFluidOutput(){
		List<int[]> current =  this.handler.deviceMap.get(MultiblockPartList.FluidPort);
		int[][] ret = new int[4][3];
		int i = 0;
		if(current != null)
			for(int[] c : current){
				TileEntityMultiblockPartBase te = (TileEntityMultiblockPartBase) worldObj.getTileEntity(new BlockPos(c[0], c[1], c[2]));
				if(te != null && te instanceof TileEntityMBFluidPort){
					TileEntityMBFluidPort tilee = (TileEntityMBFluidPort) te;
					if(!tilee.isInput()){
						ret[i] = c;
						//this.lastest = ret[i];
						i++;
					}
					if(i == 5) break;
				}
			}
		return ret;
	}
	@Override
	public MultiblockPartList getPartName() {
		return MultiblockPartList.Controller;
	}
	@Override
	public boolean isPlaceableOnSide() {
		return false;
	}
	public void onBlockBreak(){}

	public int[][] getItemPorts(boolean mode){
		List<int[]> current =  this.handler.deviceMap.get(MultiblockPartList.ItemHatch);
		int[][] ret = new int[4][3];
		int i = 0;
		if(current != null)
			for(int[] c : current){
				TileEntityMultiblockPartBase te = (TileEntityMultiblockPartBase) worldObj.getTileEntity(new BlockPos(c[0], c[1], c[2]));
				if(te != null && te instanceof TileEntityMBHatch){
					TileEntityMBHatch tilee = (TileEntityMBHatch) te;
					if(tilee.isInput() == mode){
						ret[i] = c;
						//this.lastest = ret[i];
						i++;
					}
					if(i == 5) break;
				}
			}
		return ret;
	}
	public abstract void updateEntity(boolean redstone);
	public void updateRedstoneMode(){
		this.redstoneMode++;
		if(this.redstoneMode == 2) this.redstoneMode = 0;
	}
	public boolean getRedstone(){
		if(this.redstoneMode == 0) return true;
		else if(this.redstoneMode == 1) return !this.redstone;
		else if(this.redstoneMode == 2) return this.redstone;
		else return false;
	}
	public TileEntityMBPressurePortBase getPressurePort(){
		List<int[]> current =  this.handler.deviceMap.get(MultiblockPartList.PressurePort);
		if(current != null)
			for(int[] c : current){
				TileEntityMultiblockPartBase te = (TileEntityMultiblockPartBase) worldObj.getTileEntity(new BlockPos(c[0], c[1], c[2]));
				if(te != null && te instanceof TileEntityMBPressurePortBase){
					TileEntityMBPressurePortBase tilee = (TileEntityMBPressurePortBase) te;
					return tilee;
				}
			}
		return null;
	}
	public TileEntityMBFluidPort getTileEntityList(boolean isInput){
		return this.getTileEntityList(isInput, 0);
	}
}
