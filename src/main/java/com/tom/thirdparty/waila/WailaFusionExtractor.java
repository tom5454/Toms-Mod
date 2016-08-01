package com.tom.thirdparty.waila;

import java.util.List;

import com.tom.energy.tileentity.TileEntityFusionFluidExtractor;
import com.tom.lib.Configs;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WailaFusionExtractor implements IWailaDataProvider{

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		NBTTagCompound tag = accessor.getNBTData();
		int amount = tag.getInteger("amount");
		if(amount != 0)currenttip.add(I18n.format("fluid.tomsmodplasma") + " " + I18n.format("tomsMod.waila.fluidStored", Configs.BASIC_TANK_SIZE, amount));
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te,
			NBTTagCompound tag, World world, BlockPos pos) {
		TileEntityFusionFluidExtractor tile = (TileEntityFusionFluidExtractor)te;
		//tag.setString("player", tile.playerName);
		tag.setInteger("amount", tile.getAmount());
		return tag;
	}

}
