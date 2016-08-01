package com.tom.api.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.tom.core.CoreInit;
import com.tom.core.item.Tablet;
import com.tom.core.tileentity.TileEntityWirelessPeripheral;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityTabletAccessPointBase extends TileEntityTomsMod implements
IReceivable {

	public boolean connected = false;
	public int posX = 0;
	public int posY = 0;
	public int posZ = 0;
	public boolean linked = false;
	public boolean active = true;
	public List<EntityPlayer> players = new ArrayList<EntityPlayer>();
	public int direction = 0;
	public EnumFacing d = EnumFacing.DOWN;
	public boolean locked = false;
	public int tier = 0;
	//private boolean debug = false;
	@Override
	public void updateEntity(){
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		List<EntityPlayer> playersOld = new ArrayList<EntityPlayer>(this.players);
		if(this.linked){
			TileEntity tilee = worldObj.getTileEntity(new BlockPos(posX, posY, posZ));
			this.linked = tilee instanceof IWirelessPeripheralController;
		}
		if(this.active && this.linked){
			List<EntityPlayer> entities = worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(xCoord - 4, yCoord - 4, zCoord - 4, xCoord + 5, yCoord + 5, zCoord + 5));
			this.players.clear();
			boolean c = false;
			TileEntityWirelessPeripheral te = (TileEntityWirelessPeripheral) worldObj.getTileEntity(new BlockPos(posX, posY, posZ));
			for(Entity entity : entities) {
				if(entity instanceof EntityPlayer){
					EntityPlayer player = (EntityPlayer) entity;
					if(player.getDistance(xCoord, yCoord, zCoord) < 5){
						{
							ItemStack held = player.getHeldItemMainhand();
							if(held != null && held.getItem() == CoreInit.Tablet){
								Tablet tab = (Tablet) held.getItem();
								if(this.canConnect(player, held)){
									boolean connect = tab.connect(player, worldObj, xCoord, yCoord, zCoord,0, held, this.tier, this.locked);
									//if(this.debug) System.out.println("Detect");
									if(connect){
										this.players.add(player);
										//System.out.println("connect");
										c = true;
										if(playersOld.contains(player)){
											playersOld.remove(player);
										}else{
											te.queueEvent("TabletAttach", new Object[]{player.getName()});
										}
									}
								}
							}
						}
						{
							ItemStack held = player.getHeldItemOffhand();
							if(held != null && held.getItem() == CoreInit.Tablet){
								Tablet tab = (Tablet) held.getItem();
								if(this.canConnect(player, held)){
									boolean connect = tab.connect(player, worldObj, xCoord, yCoord, zCoord,0, held, this.tier, this.locked);
									//if(this.debug) System.out.println("Detect");
									if(connect){
										this.players.add(player);
										//System.out.println("connect");
										c = true;
										if(playersOld.contains(player)){
											playersOld.remove(player);
										}else{
											te.queueEvent("TabletAttach", new Object[]{player.getName()});
										}
									}
								}
							}
						}
					}
				}
			}
			this.connected = c;
			if(!playersOld.isEmpty()){
				for(EntityPlayer p : playersOld){
					te.queueEvent("TabletDetach", new Object[]{p.getName()});
				}
			}
			this.markDirty();
			markBlockForUpdate(pos);
		}
	}
	@Override
	public void writeToPacket(NBTTagCompound buf){
		buf.setBoolean("c", connected);
		buf.setInteger("dir", direction);
		buf.setInteger("d", this.d.ordinal());
		buf.setBoolean("l", locked);
		buf.setInteger("t", tier);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf){
		this.connected = buf.getBoolean("c");
		this.direction = buf.getInteger("dir");
		this.d = EnumFacing.values()[buf.getInteger("d")];
		this.locked = buf.getBoolean("l");
		this.tier = buf.getInteger("t");
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.direction = tag.getInteger("direction");
		this.d = EnumFacing.values()[tag.getInteger("d")];
		this.posX = tag.getInteger("linkX");
		this.posY = tag.getInteger("linkY");
		this.posZ = tag.getInteger("linkZ");
		this.linked = tag.getBoolean("linked");
		this.locked = tag.getBoolean("locked");
		this.tier = tag.getInteger("tier");
		//this.debug = tag.getBoolean("debug");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("direction", this.direction);
		tag.setInteger("d", this.d.ordinal());
		tag.setInteger("linkX", this.posX);
		tag.setInteger("linkY", this.posY);
		tag.setInteger("linkZ", this.posZ);
		tag.setBoolean("linked", this.linked);
		tag.setBoolean("locked",this.locked);
		tag.setInteger("tier",this.tier);
		//tag.setBoolean("debug", this.debug);
		return tag;
	}
	public void link(int x, int y, int z){
		//if(this.debug) System.out.println("link");
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.linked = true;
	}
	@Override
	public void receiveMsg(String pName, Object msg){
		if(this.active && this.linked){
			TileEntityWirelessPeripheral te = (TileEntityWirelessPeripheral) worldObj.getTileEntity(new BlockPos(posX, posY, posZ));
			te.queueEvent("tablet_receive", new Object[]{pName,msg});
		}
	}
	public void sendMsg(String pName, Object msg){
		if(this.active && this.linked){
			for(EntityPlayer player : this.players){
				if(player.getName().equals(pName)){
					ItemStack held = player.getHeldItemMainhand();
					if(held != null && held.getItem() == CoreInit.Tablet){
						Tablet tab = (Tablet) held.getItem();
						tab.receive(worldObj, msg,held,player);
						break;
					}else{
						held = player.getHeldItemOffhand();
						if(held != null && held.getItem() == CoreInit.Tablet){
							Tablet tab = (Tablet) held.getItem();
							tab.receive(worldObj, msg,held,player);
							break;
						}
					}
				}
			}
		}
	}
	public boolean isActive() {
		return this.active && this.linked;
	}
	public void setTier(int tier){
		this.tier = tier;
	}
	public void setLocked(boolean locked){
		this.locked = locked;
	}
	public boolean canConnect(EntityPlayer player, ItemStack tabStack){
		return true;
	}
}
