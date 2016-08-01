package com.tom.core.tileentity;

import com.tom.api.tileentity.TileEntityTabletAccessPointBase;
import com.tom.client.ICustomModelledTileEntity;
import com.tom.core.CoreInit;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityControllerBox extends TileEntityTabletAccessPointBase implements ICustomModelledTileEntity {
	public boolean hasModem = false;
	public boolean doorOpened = false;
	@Override
	public void writeToPacket(NBTTagCompound buf){
		buf.setBoolean("c", connected);
		buf.setInteger("dir", direction);
		buf.setInteger("d", this.d.ordinal());
		buf.setBoolean("l", locked);
		buf.setInteger("t", tier);
		buf.setBoolean("do", doorOpened);
		buf.setBoolean("m", hasModem);
	}
	public boolean onBlockActivated(EntityPlayer player, ItemStack is){
		if(is != null && is.getItem() == CoreInit.wrenchA){
			this.doorOpened = !this.doorOpened;
		}else if(is != null && is.getItem() == CoreInit.connectionBoxModem && !this.hasModem && this.doorOpened){
			this.hasModem = true;
			is.splitStack(1);
		}else if(is == null && this.hasModem && this.doorOpened){
			this.hasModem = false;
			EntityItem itemEntity = new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(CoreInit.connectionBoxModem));
			if(!worldObj.isRemote) worldObj.spawnEntityInWorld(itemEntity);
			/*player.inventory.setItemStack(new ItemStack(CoreInit.connectionBoxModem));
			player.inventory.markDirty();*/
		}
		this.markDirty();
		markBlockForUpdate(pos);
		return true;
	}
	@Override
	public void readFromPacket(NBTTagCompound buf){
		this.connected = buf.getBoolean("c");
		this.direction = buf.getInteger("dir");
		this.d = EnumFacing.values()[buf.getInteger("d")];
		this.locked = buf.getBoolean("l");
		this.tier = buf.getInteger("t");
		this.doorOpened = buf.getBoolean("do");
		this.hasModem = buf.getBoolean("m");
		this.worldObj.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(),pos.getX(), pos.getY(), pos.getZ());
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.hasModem = tag.getBoolean("hasModem");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setBoolean("hasModem", this.hasModem);
		return tag;
	}
	@Override
	public boolean canConnect(EntityPlayer player, ItemStack tabStack){
		return this.hasModem;
	}
	@Override
	public boolean isActive() {
		return this.active && this.linked && this.hasModem;
	}
	@Override
	public EnumFacing getFacing() {
		return d;
	}
}
