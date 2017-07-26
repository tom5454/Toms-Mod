package com.tom.thirdparty.waila;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;

import com.tom.storage.tileentity.TMTank;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class WailaTank implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (WailaHandler.hasMultimeter(accessor, config)) {
			try {
				FluidStack fluid = FluidStack.loadFluidStackFromNBT(accessor.getNBTData().getCompoundTag("fluid"));
				String name = currenttip.get(0);
				try {
					name += String.format(" < %s >", fluid.getFluid().getLocalizedName(fluid));
				} catch (NullPointerException f) {
					name += " " + I18n.format("tomsMod.waila.empty");
				}
				currenttip.set(0, name);
			} catch (Exception e) {
				currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
			}
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (WailaHandler.hasMultimeter(accessor, config)) {
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(accessor.getNBTData().getCompoundTag("fluid"));
			if (fluid != null) {
				currenttip.add(I18n.format("tomsMod.waila.fluidStored", fluid.amount, accessor.getNBTData().getInteger("c")));
			} else {
				currenttip.add(I18n.format("tomsMod.waila.empty"));
				currenttip.add(I18n.format("tomsMod.waila.capacity", accessor.getNBTData().getInteger("c")));
			}
		} else {
			currenttip.add(I18n.format("tomsMod.waila.capacity", accessor.getNBTData().getInteger("c")));
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, BlockPos pos) {
		TMTank te = (TMTank) tile;
		NBTTagCompound t = new NBTTagCompound();
		if (te.getStack() != null)
			te.getStack().writeToNBT(t);
		tag.setTag("fluid", t);
		tag.setInteger("c", te.getCapacity());
		return tag;
	}

}
