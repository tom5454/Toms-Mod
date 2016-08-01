package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.HV;
import static com.tom.api.energy.EnergyType.LV;
import static com.tom.api.energy.EnergyType.MV;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyContainerItem;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.ILinkable;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.Coords;
import com.tom.apis.ExtraBlockHitInfo;
import com.tom.apis.TomsModUtils;
import com.tom.lib.Configs;

import com.tom.energy.block.WirelessCharger;

public class TileEntityWirelessCharger extends TileEntityTomsMod implements
IEnergyReceiver, ILinkable {
	private EnergyStorage energy = new EnergyStorage(10000000,10000,10000);
	private List<Coords> linked = new ArrayList<Coords>();
	public boolean redstone = true;
	public boolean active = false;
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == getType(worldObj.getBlockState(pos));
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive,
			boolean simulate) {
		this.markDirty();
		markBlockForUpdate(pos);
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
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		this.energy.writeToNBT(tag);
		NBTTagList list = new NBTTagList();
		for(Coords te : this.linked){
			NBTTagCompound tagC = new NBTTagCompound();
			tagC.setInteger("x", te.xCoord);
			tagC.setInteger("y", te.yCoord);
			tagC.setInteger("z", te.zCoord);
			tagC.setInteger("side", te.facing.ordinal());
			list.appendTag(tagC);
		}
		tag.setTag("link", list);
		return tag;
	}
	public void writeToStackNBT(NBTTagCompound tag) {
		energy.writeToNBT(tag);
		NBTTagList list = new NBTTagList();
		for(Coords te : this.linked){
			NBTTagCompound tagC = new NBTTagCompound();
			tagC.setInteger("x", te.xCoord);
			tagC.setInteger("y", te.yCoord);
			tagC.setInteger("z", te.zCoord);
			tagC.setInteger("side", te.facing.ordinal());
			list.appendTag(tagC);
		}
		tag.setTag("link", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.energy.readFromNBT(tag);
		NBTTagList list = (NBTTagList) tag.getTag("link");
		this.linked.clear();
		for(int i = 0;i<list.tagCount();i++){
			NBTTagCompound tagC = list.getCompoundTagAt(i);
			int x = tagC.getInteger("x");
			int y = tagC.getInteger("y");
			int z = tagC.getInteger("z");
			int side = tagC.getInteger("side");
			this.linked.add(new Coords(x, y, z).setFacing(EnumFacing.VALUES[side]));
		}
	}
	@Override
	public void updateEntity(IBlockState state){
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.redstone = !(worldObj.isBlockIndirectlyGettingPowered(pos) > 0);
		if(!this.worldObj.isRemote && this.redstone && this.energy.hasEnergy()){
			/*List<Entity> entities = worldObj.getEntitiesWithinAABB(TileEntity.class, AxisAlignedBB.getBoundingBox(xCoord - 16, yCoord - 16, zCoord - 16, xCoord + 17, yCoord + 17, zCoord + 17));
			//System.out.println(entities.toString());
			for(Entity tile : entities){
				if(tile instanceof IEnergyHandler){
					IEnergyHandler te = (IEnergyHandler) tile;
					double distance = tile.getDistance(xCoord, yCoord, zCoord);
					int loss = MathHelper.floor_double(distance / Configs.wirelessChargerLoss);
					int energySend = this.energy.extractEnergy(te.receiveEnergy(EnumFacing.DOWN, MathHelper.floor_double(10000 / distance / 10), true) + loss, true);
					if(energySend > 0){
						this.energy.extractEnergy(energySend, false);
						te.receiveEnergy(EnumFacing.DOWN, energySend - loss, false);
					}
				}
			}*/
			if(!state.getValue(WirelessCharger.ACTIVE)){
				TomsModUtils.setBlockState(worldObj, pos, state.withProperty(WirelessCharger.ACTIVE, true));
			}
			for(int i = 0;i<linked.size();i++){
				Coords c = linked.get(i);
				TileEntity tile = this.worldObj.getTileEntity(new BlockPos(c.xCoord, c.yCoord, c.zCoord));
				if(tile != null && tile instanceof IEnergyReceiver){
					IEnergyReceiver te = (IEnergyReceiver) tile;
					double distance = tile.getPos().distanceSq(xCoord, yCoord, zCoord);
					double loss = distance / Configs.wirelessChargerLoss;
					double energySend = this.energy.extractEnergy(te.receiveEnergy(c.facing, getType(state), MathHelper.floor_double(10000 / distance), true) + loss, true);
					if(energySend > 0 && loss < energySend){
						this.energy.extractEnergy(energySend, false);
						te.receiveEnergy(c.facing, getType(state), energySend - loss, false);
					}
					if(!this.energy.hasEnergy())return;
				}else{
					this.linked.remove(c);
				}
			}
			if(this.energy.hasEnergy()){
				List<EntityPlayer> entities = worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - 64, pos.getY() - 64, pos.getZ() - 64, pos.getX() + 65, pos.getY() + 65, pos.getZ() + 65));
				for(EntityPlayer player : entities){
					if(player.getDistance(pos.getX(),pos.getY(),pos.getZ()) < 64){
						InventoryPlayer inv = player.inventory;
						for(int i = 0;i<inv.getSizeInventory();i++){
							ItemStack item = inv.getStackInSlot(i);
							if(item != null && item.getItem() instanceof IEnergyContainerItem){
								IEnergyContainerItem c = (IEnergyContainerItem) item.getItem();
								double received = c.receiveEnergy(item, LV.convertFrom(getType(state), energy.getEnergyStored()), true);
								if(received > 0){
									c.receiveEnergy(item, LV.convertFrom(getType(state), energy.extractEnergy(getType(state).convertFrom(LV, received), false)), false);
								}
							}
							if(!this.energy.hasEnergy())return;
						}
					}
				}
			}
		}else{
			if(!this.worldObj.isRemote){
				if(state.getValue(WirelessCharger.ACTIVE)){
					TomsModUtils.setBlockState(worldObj, pos, state.withProperty(WirelessCharger.ACTIVE, false));
				}
			}
		}
	}
	//	@Override
	//	public void writeToPacket(ByteBuf buf){
	//		buf.writeBoolean(this.redstone && this.energy.getEnergyStored() > 1000);
	//	}
	//
	//	@Override
	//	public void readFromPacket(ByteBuf buf){
	//		this.active = buf.readBoolean();
	//		int xCoord = pos.getX();
	//		int yCoord = pos.getY();
	//		int zCoord = pos.getZ();
	//		this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	//	}

	@Override
	public boolean link(int x, int y, int z, EnumFacing side, ExtraBlockHitInfo bhp, int dim) {
		TileEntity tile = this.worldObj.getTileEntity(new BlockPos(x, y, z));
		if(tile != null && tile instanceof IEnergyReceiver && !this.linked.contains(tile)){
			this.linked.add(new Coords(x,y,z).setFacing(side));
			return true;
		}
		return false;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return getType(worldObj.getBlockState(pos)).getList();
	}
	public EnergyType getType(IBlockState state){
		return state.getValue(WirelessCharger.TYPE) ? MV : HV;
	}
}
