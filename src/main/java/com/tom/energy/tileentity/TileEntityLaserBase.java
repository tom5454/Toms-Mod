package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.LASER;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;

import com.tom.energy.block.BlockLaserBase;

public class TileEntityLaserBase extends TileEntityTomsMod implements
IEnergyReceiver {
	protected EnergyStorage energy;
	private int i = 0;
	private BlockPos receiver;
	public final String beamTexture;
	public BlockPos getReceiver() {
		return receiver;
	}
	public TileEntityLaserBase(int energyTransfer, String beamTexture) {
		super();
		this.energy = new EnergyStorage(energyTransfer * 10, energyTransfer * 4, energyTransfer);
		this.beamTexture = beamTexture;
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == LASER && this.getFacing() != from;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LASER.getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type,
			double maxReceive, boolean simulate) {
		return this.canConnectEnergy(from, type) ? this.energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getMaxEnergyStored();
	}
	public EnumFacing getFacing(){
		IBlockState state = worldObj.getBlockState(pos);
		return state.getValue(BlockLaserBase.FACING);
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		NBTTagCompound eTag = tag.getCompoundTag("energy");
		energy.readFromNBT(eTag);
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("energy",energy.writeToNBT(new NBTTagCompound()));
		return tag;
	}
	@Override
	public void updateEntity(){
		if(!this.worldObj.isRemote){
			BlockPos posOld = this.receiver;
			i = i + 1;
			EnumFacing facing = getFacing();
			if(i == 40){
				i = 0;
				Vec3d vec1 = new Vec3d(pos.offset(facing));
				Vec3d vec2 = new Vec3d(pos.offset(facing, 32));
				RayTraceResult mpos = worldObj.rayTraceBlocks(vec1, vec2, true);
				if(mpos != null && mpos.typeOfHit == RayTraceResult.Type.BLOCK){
					BlockPos cPos = mpos.getBlockPos();
					if(!TomsModUtils.isEqual(pos, cPos)){
						TileEntity r = worldObj.getTileEntity(cPos);
						if(r instanceof IEnergyReceiver){
							IEnergyReceiver rec = (IEnergyReceiver) r;
							if(rec.canConnectEnergy(facing.getOpposite(), LASER)){
								this.receiver = cPos;
								markBlockForUpdate(pos);
							}else this.receiver = null;
						}else this.receiver = null;
					}else this.receiver = null;
				}
			}
			if(this.receiver != null && this.energy.getEnergyStored() > 0){
				TileEntity tilee = worldObj.getTileEntity(receiver);
				if(tilee != null && tilee instanceof IEnergyReceiver){
					IEnergyReceiver r = (IEnergyReceiver) tilee;
					EnumFacing f = facing.getOpposite();
					double energyReceive = r.receiveEnergy(f, LASER, energy.extractEnergy(energy.getMaxExtract(), true), true);
					if(energyReceive > 0){
						r.receiveEnergy(f, LASER, this.energy.extractEnergy(energyReceive, false), false);
					}
				}else this.receiver = null;
			}
			if(this.receiver == null && posOld != null){
				markBlockForUpdate(pos);
			}
		}
	}
	@Override
	public void writeToPacket(NBTTagCompound buf) {
		TomsModUtils.writeBlockPosToNBT(buf, receiver);
	}
	@Override
	public void readFromPacket(NBTTagCompound buf) {
		this.receiver = TomsModUtils.readBlockPosFromNBT(buf);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}
}
