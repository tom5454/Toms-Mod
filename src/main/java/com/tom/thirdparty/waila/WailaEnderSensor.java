package com.tom.thirdparty.waila;

import java.util.List;

import com.tom.core.CoreInit;
import com.tom.core.tileentity.TileEntityEnderSensor;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WailaEnderSensor implements IWailaDataProvider{

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		NBTTagCompound tag = accessor.getNBTData();
		return tag.getBoolean("camo") ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("camoStack")) : new ItemStack(CoreInit.EnderPlayerSensor);
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te,
			NBTTagCompound tag, World world, BlockPos pos) {
		TileEntityEnderSensor t = (TileEntityEnderSensor) te;
		boolean camo = t.camoStack != null;
		tag.setBoolean("camo", camo);
		if(camo) tag.setTag("camoStack", t.camoStack.writeToNBT(new NBTTagCompound()));
		return tag;
	}

}
