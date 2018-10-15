package com.tom.api.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.handler.TMWorldHandler;

public class TileEntityJammerBase extends TileEntityTomsMod implements IJammer {
	// public boolean connected = false;
	// public int posX = 0;
	// public int posY = 0;
	// public int posZ = 0;
	// public boolean linked = false;
	public boolean active = false;

	// public List<EntityPlayer> players = new ArrayList<EntityPlayer>();
	// public int direction = 0;
	// public ForgeDirection d = ForgeDirection.DOWN;
	// public boolean locked = false;
	// public int tier = 0;
	// private boolean debug = false;
	@Override
	public void updateEntity() {
		/*List<EntityPlayer> playersOld = new ArrayList<EntityPlayer>(this.players);
		if(this.linked){
			TileEntity tilee = worldObj.getTileEntity(posX, posY, posZ);
			this.linked = tilee instanceof IWirelessPeripheralController;
		}*/
		this.active = !(world.isBlockIndirectlyGettingPowered(pos) > 0);
		if (this.active) {
			/*List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(xCoord - this.getRange(), yCoord - this.getRange(), zCoord - this.getRange(), xCoord + this.getRange() + 1, yCoord + this.getRange() + 1, zCoord + this.getRange() + 1));
			// this.players.clear();
			// boolean c = false;
			// TileEntityWirelessPeripheral te = (TileEntityWirelessPeripheral)
			// worldObj.getTileEntity(posX, posY, posZ);
			for (Entity entity : entities) {
				if (entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					if (player.getDistance(xCoord, yCoord, zCoord) < this.getRange()) {
						PlayerHandler.getPlayerHandler(player).tabletHandler.jammers.add(this);
						/*{
							ItemStack held = player.getHeldItemMainhand();
							if (held != null && held.getItem() == CoreInit.Tablet) {
								Tablet tab = (Tablet) held.getItem();
								if (this.canConnect(player, held)) {
									tab.connect(player, world, xCoord, yCoord, zCoord, 2, held, 0, false);
									// if(this.debug)
									// System.out.println("Detect");
									// if(connect){
									// this.players.add(player);
									// System.out.println("connect");
									// c = true;
									// if(playersOld.contains(player)){
									// playersOld.remove(player);
									// }else{
									// te.queueEvent("TabletAttach", new
									// Object[]{player.getCommandSenderName()});
									// }
									// }
								}
							}
							InventoryPlayer inv = player.inventory;
							for (int i = 0;i < inv.getSizeInventory();i++) {
								ItemStack item = inv.getStackInSlot(i);
								if (item != null && item.getItem() == CoreInit.entityTracker && item.getTagCompound() != null) {
									item.getTagCompound().setBoolean("jammed", true);
									item.getTagCompound().setInteger("jx", xCoord);
									item.getTagCompound().setInteger("jy", yCoord);
									item.getTagCompound().setInteger("jz", zCoord);
								}
							}
						}
						{
							ItemStack held = player.getHeldItemOffhand();
							if (held != null && held.getItem() == CoreInit.Tablet) {
								Tablet tab = (Tablet) held.getItem();
								if (this.canConnect(player, held)) {
									tab.connect(player, world, xCoord, yCoord, zCoord, 2, held, 0, false);
									// if(this.debug)
									// System.out.println("Detect");
									// if(connect){
									// this.players.add(player);
									// System.out.println("connect");
									// c = true;
									// if(playersOld.contains(player)){
									// playersOld.remove(player);
									// }else{
									// te.queueEvent("TabletAttach", new
									// Object[]{player.getCommandSenderName()});
									// }
									// }
								}
							}
							InventoryPlayer inv = player.inventory;
							for (int i = 0;i < inv.getSizeInventory();i++) {
								ItemStack item = inv.getStackInSlot(i);
								if (item != null && item.getItem() == CoreInit.entityTracker && item.getTagCompound() != null) {
									item.getTagCompound().setBoolean("jammed", true);
									item.getTagCompound().setInteger("jx", xCoord);
									item.getTagCompound().setInteger("jy", yCoord);
									item.getTagCompound().setInteger("jz", zCoord);
								}
							}
						}*/
			/*	}
				}
			}
			// this.connected = c;
			// if(!playersOld.isEmpty()){
			// for(EntityPlayer p : playersOld){
			// te.queueEvent("TabletDetach", new
			// Object[]{p.getCommandSenderName()});
			// }
			// }
			this.markDirty();
			markBlockForUpdate(pos);*/
		}else{
			TMWorldHandler.removeJammer(this);
		}
	}

	// @Override
	// public void writeToPacket(ByteBuf buf){
	// buf.writeBoolean(active);
	// /*buf.writeInt(direction);
	// buf.writeInt(this.d.ordinal());
	// buf.writeBoolean(locked);
	// buf.writeInt(tier);*/
	// }
	//
	// @Override
	// public void readFromPacket(ByteBuf buf){
	// this.active = buf.readBoolean();
	// /*this.direction = buf.readInt();
	// this.d = ForgeDirection.values()[buf.readInt()];
	// this.locked = buf.readBoolean();
	// this.tier = buf.readInt();*/
	// int xCoord = pos.getX();
	// int yCoord = pos.getY();
	// int zCoord = pos.getZ();
	// this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord,
	// xCoord, yCoord, zCoord);
	// }
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		/*this.direction = tag.getInteger("direction");
		this.d = ForgeDirection.values()[tag.getInteger("d")];
		this.posX = tag.getInteger("linkX");
		this.posY = tag.getInteger("linkY");
		this.posZ = tag.getInteger("linkZ");
		this.linked = tag.getBoolean("linked");
		this.locked = tag.getBoolean("locked");
		this.tier = tag.getInteger("tier");
		//this.debug = tag.getBoolean("debug");*/
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		/*tag.setInteger("direction", this.direction);
		tag.setInteger("d", this.d.ordinal());
		tag.setInteger("linkX", this.posX);
		tag.setInteger("linkY", this.posY);
		tag.setInteger("linkZ", this.posZ);
		tag.setBoolean("linked", this.linked);
		tag.setBoolean("locked",this.locked);
		tag.setInteger("tier",this.tier);
		//tag.setBoolean("debug", this.debug);*/
		return tag;
	}

	/*public void link(int x, int y, int z){
		//if(this.debug) System.out.println("link");
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.linked = true;
	}
	public void receiveMsg(String pName, Object msg){
		if(this.active && this.linked){
			TileEntityWirelessPeripheral te = (TileEntityWirelessPeripheral) worldObj.getTileEntity(posX, posY, posZ);
			te.queueEvent("tablet_receive", new Object[]{pName,msg});
		}
	}
	public void sendMsg(String pName, Object msg){
		if(this.active && this.linked){
			for(EntityPlayer player : this.players){
				if(player.getCommandSenderName().equals(pName)){
					ItemStack held = player.getHeldItem();
					if(held != null && held.getItem() == CoreInit.Tablet){
						Tablet tab = (Tablet) held.getItem();
						tab.receive(worldObj, msg,held,player);
						break;
					}
				}
			}
		}
	}*/
	public boolean isActive() {
		return this.active;
	}

	/*public void setTier(int tier){
		this.tier = tier;
	}
	public void setLocked(boolean locked){
		this.locked = locked;
	}*/
	public boolean canConnect(EntityPlayer player, ItemStack tabStack) {
		return true;
	}

	public int getRange() {
		return 32;
	}


	@Override
	public boolean isValid() {
		return !isInvalid();
	}

	@Override
	public boolean isAccessible(double x, double y, double z) {
		return isValid() && pos.distanceSqToCenter(x, y, z) <= getRange() * getRange();
	}

}
