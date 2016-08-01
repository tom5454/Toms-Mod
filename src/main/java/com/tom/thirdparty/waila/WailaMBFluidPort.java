package com.tom.thirdparty.waila;

import java.util.List;

import com.tom.factory.tileentity.TileEntityMBFluidPort;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class WailaMBFluidPort implements IWailaDataProvider{

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
		boolean input = tag.getBoolean("in");
		currenttip.add(I18n.format("tomsMod.waila.fluidStored", Configs.BASIC_TANK_SIZE, amount));
		currenttip.add(I18n.format("tomsMod.waila.mode") + " " + (input ? TextFormatting.RED + I18n.format("tomsMod.waila.input") : TextFormatting.BLUE + I18n.format("tomsMod.waila.output")));
		currenttip.add(I18n.format("tomsMod.waila.fluidTank") + " " + TextFormatting.YELLOW + I18n.format(tag.getString("f")));
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
		TileEntityMBFluidPort tile = (TileEntityMBFluidPort)te;
		//tag.setString("player", tile.playerName);
		tag.setInteger("amount", tile.getFluidAmmount());
		tag.setBoolean("in", tile.isInput());
		String string = tile.getFluid() == null ? "tomsMod.waila.empty" : tile.getFluid().getUnlocalizedName(tile.getFluidStack());
		tag.setString("f",string);
		return tag;
	}

}
