package com.tom.thirdparty.waila;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.tom.api.tileentity.TileEntityTomsMod;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class WailaOverloadInfo implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (accessor.getTileEntity() instanceof TileEntityTomsMod && ((TileEntityTomsMod) accessor.getTileEntity()).isTickSpeeded()) {
			currenttip.add(TextFormatting.RED + "" + TextFormatting.BOLD + "" + TextFormatting.OBFUSCATED + "!!!" + TextFormatting.RESET + " " + TextFormatting.RED + "" + TextFormatting.BOLD + I18n.format("tomsMod.waila.speedingWarn") + " " + TextFormatting.OBFUSCATED + "!!!");
			currenttip.add(TextFormatting.RED + I18n.format("tomsMod.waila.speedingDisabledInfo"));
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		return null;
	}

}
