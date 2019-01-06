package com.tom.thirdparty.waila;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.IEnergyStorageTile;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class WailaEnergyHandler implements IWailaDataProvider {

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
		NBTTagCompound tag = accessor.getNBTData();
		if (!tag.hasNoTags()) {
			if (WailaHandler.hasMultimeter(accessor, config) || tag.getBoolean("integrated")) {
				for (int i = 0;i < tag.getInteger("size");i++) {
					NBTTagCompound t = tag.getCompoundTag(accessor.getSide().getName() + "_" + i);
					if (!t.hasNoTags())
						currenttip.add(I18n.format("tomsMod.waila.energyStored") + " " + TextFormatting.values()[t.getInteger("c")] + t.getString("type") + TextFormatting.RESET + ": " + t.getLong("MaxEnergy") + "/" + t.getDouble("Energy"));
				}
			}
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if (te instanceof IEnergyStorageTile) {
			IEnergyStorageTile s = (IEnergyStorageTile) te;
			List<EnergyType> eL = s.getValidEnergyTypes();
			if (eL != null) {
				tag.setInteger("size", eL.size());
				for (int i = 0;i < eL.size();i++) {
					for (EnumFacing side : EnumFacing.VALUES) {
						EnergyType c = eL.get(i);
						double energyStored = s.getEnergyStored(side, c);
						long maxEnergyStored = s.getMaxEnergyStored(side, c);
						NBTTagCompound t = new NBTTagCompound();
						t.setDouble("Energy", energyStored);
						t.setLong("MaxEnergy", maxEnergyStored);
						t.setString("type", c.toString());
						t.setInteger("c", c.getColor().ordinal());
						tag.setTag(side.getName() + "_" + i, t);
					}
				}
				tag.setBoolean("integrated", s instanceof IIntegratedMultimeter);
			}
		}
		return tag;
	}
}
