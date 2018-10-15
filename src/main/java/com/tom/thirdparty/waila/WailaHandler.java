package com.tom.thirdparty.waila;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.tom.api.energy.IEnergyStorageTile;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.config.Config;
import com.tom.energy.EnergyInit;
import com.tom.storage.tileentity.TMTank;
import com.tom.util.TMLogger;

import com.tom.core.block.BlockHidden;
import com.tom.core.block.EnderMemory;
import com.tom.core.block.EnderPlayerSensor;

import com.tom.energy.block.FusionFluidExtractor;
import com.tom.energy.block.FusionFluidInjector;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaHandler {
	public static final String ENERGY_HANDLER_ID = "tmenergyhandler";

	public static void onWailaCall(IWailaRegistrar registrar) {
		TMLogger.info("Loading Waila handler");
		registrar.registerBodyProvider(new WailaEnderMemory(), EnderMemory.class);
		registrar.registerNBTProvider(new WailaEnderMemory(), EnderMemory.class);
		registrar.registerBodyProvider(new WailaFusionInjector(), FusionFluidInjector.class);
		registrar.registerNBTProvider(new WailaFusionInjector(), FusionFluidInjector.class);
		registrar.registerBodyProvider(new WailaFusionExtractor(), FusionFluidExtractor.class);
		registrar.registerNBTProvider(new WailaFusionExtractor(), FusionFluidExtractor.class);
		registrar.registerStackProvider(new WailaEnderSensor(), EnderPlayerSensor.class);
		registrar.registerNBTProvider(new WailaEnderSensor(), EnderPlayerSensor.class);
		Config.initWailaConfigs(registrar);
		registrar.registerBodyProvider(new WailaEnergyHandler(), IEnergyStorageTile.class);
		registrar.registerNBTProvider(new WailaEnergyHandler(), IEnergyStorageTile.class);
		registrar.registerHeadProvider(new WailaTank(), TMTank.class);
		registrar.registerBodyProvider(new WailaTank(), TMTank.class);
		registrar.registerNBTProvider(new WailaTank(), TMTank.class);
		registrar.registerBodyProvider(new WailaOverloadInfo(), TileEntityTomsMod.class);
		registrar.registerStackProvider(new WailaHidden(), BlockHidden.class);
		registrar.registerNBTProvider(new WailaHidden(), BlockHidden.class);
	}

	public static boolean hasMultimeter(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		InventoryPlayer inv = accessor.getPlayer().inventory;
		boolean found = !Config.isWailaUsesMultimeter(config);
		if (!found) {
			for (int i = 0;i < 9 && i < inv.getSizeInventory();i++) {
				ItemStack s = inv.getStackInSlot(i);
				if (s != null && s.getItem() == EnergyInit.multimeter) {
					found = true;
					break;
				}
			}
		}
		return found;
	}
}
