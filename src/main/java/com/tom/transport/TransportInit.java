package com.tom.transport;

import static com.tom.core.CoreInit.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.tom.core.CoreInit;
import com.tom.core.IMod;
import com.tom.lib.Configs;
import com.tom.transport.block.BlockOpenCrate;
import com.tom.transport.block.ConveyorBeltExtract;
import com.tom.transport.block.ConveyorBeltFast;
import com.tom.transport.block.ConveyorBeltOmniFast;
import com.tom.transport.block.ConveyorBeltOmniSlow;
import com.tom.transport.block.ConveyorBeltSlope;
import com.tom.transport.block.ConveyorBeltSlow;
import com.tom.transport.block.SteamDuct;
import com.tom.transport.item.FluidDuct;
import com.tom.transport.item.FluidServo;
import com.tom.transport.multipart.FluidDuctOpaque;
import com.tom.transport.multipart.PartFluidDuct;
import com.tom.transport.multipart.PartFluidDuctOpaque;
import com.tom.transport.multipart.PartFluidServo;
import com.tom.transport.multipart.PartSteamDuct;
import com.tom.transport.tileentity.TileEntityConveyorBeltSlope;
import com.tom.transport.tileentity.TileEntityConveyorExtract;
import com.tom.transport.tileentity.TileEntityConveyorFast;
import com.tom.transport.tileentity.TileEntityConveyorOmniFast;
import com.tom.transport.tileentity.TileEntityConveyorOmniSlow;
import com.tom.transport.tileentity.TileEntityConveyorSlow;
import com.tom.transport.tileentity.TileEntityOpenCrate;

@Mod(modid = TransportInit.modid, name = TransportInit.modName, version = Configs.version, dependencies = Configs.coreDependencies)
public class TransportInit {
	public static final String modid = Configs.ModidL + "transport";
	public static final String modName = Configs.ModName + " Transport";
	public static final Logger log = LogManager.getLogger(modName);
	public static Block fluidDuct, steamDuct, fluidDuctOpaque, fluidServo, conveyorBeltSlow, conveyorBeltFast,
	conveyorBeltDouble, conveyorBeltSlope, conveyorBeltSlopeDouble, conveyorBeltOmnidirectionalSlow,
	conveyorBeltOmnidirectionalFast, conveyorBeltVerticalSlow, conveyorBeltVerticalFast, openCrate, conveyorBeltExtract;

	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent) {
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		/** Blocks */
		/*itemDuct = new ItemDuct().setCreativeTab(tabTomsModTransport).setUnlocalizedName("itemDuct");
		filter = new ItemFilter().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.filter");
		servo = new ItemServo().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.servo");*/
		conveyorBeltSlow = new ConveyorBeltSlow().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.conveyorBeltSlow");
		conveyorBeltFast = new ConveyorBeltFast().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.conveyorBeltFast");
		conveyorBeltOmnidirectionalSlow = new ConveyorBeltOmniSlow().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.conveyorBeltOmniSlow");
		conveyorBeltOmnidirectionalFast = new ConveyorBeltOmniFast().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.conveyorBeltOmniFast");
		conveyorBeltSlope = new ConveyorBeltSlope().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.conveyorBeltSlope");
		conveyorBeltExtract = new ConveyorBeltExtract().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.conveyorBeltExtract");
		openCrate = new BlockOpenCrate().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.openCrate");
		/** Multiparts */
		fluidDuct = new FluidDuct().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.fluidDuct");
		fluidServo = new FluidServo().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.fluidServo");
		fluidDuctOpaque = new FluidDuctOpaque().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.fluidDuctOpaque");
		steamDuct = new SteamDuct().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.steamDuct");
		/** Registry */
		/** Blocks */
		registerBlock(conveyorBeltSlow);
		registerBlock(conveyorBeltFast);
		registerBlock(conveyorBeltOmnidirectionalSlow);
		registerBlock(conveyorBeltOmnidirectionalFast);
		registerBlock(conveyorBeltSlope);
		registerBlock(conveyorBeltExtract);
		registerBlock(openCrate);
		/** Multiparts */
		registerBlock(fluidDuct);
		registerBlock(fluidDuctOpaque);
		registerBlock(fluidServo);
		registerBlock(steamDuct);
		/** TileEntities */
		registerTileEntity(TileEntityConveyorSlow.class, "conveyorSlow");
		registerTileEntity(TileEntityConveyorFast.class, "conveyorFast");
		registerTileEntity(TileEntityConveyorOmniSlow.class, "conveyorOmniSlow");
		registerTileEntity(TileEntityConveyorOmniFast.class, "conveyorOmniFast");
		registerTileEntity(TileEntityConveyorBeltSlope.class, "conveyorSlope");
		registerTileEntity(TileEntityConveyorExtract.class, "conveyorExtracting");
		registerTileEntity(TileEntityOpenCrate.class, "openCrate");
		/** Multiparts */
		registerTileEntity(PartFluidDuctOpaque.class, "part_fluidDuctO");
		registerTileEntity(PartFluidDuct.class, "part_fluidDuct");
		registerTileEntity(PartFluidServo.class, "part_fluidServo");
		registerTileEntity(PartSteamDuct.class, "part_steamDuct");
		hadPreInit = true;
		CoreInit.tryLoadAfterPreInit(log);
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in " + time + " milliseconds");
	}

	public static CreativeTabs tabTomsModTransport = new CreativeTabs("tabTomsModTransport") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(TransportInit.fluidDuct);
		}

	};
	private static boolean hadPreInit = false;

	@EventHandler
	public static void construction(FMLConstructionEvent event) {
		CoreInit.modids.add(new IMod() {
			@Override
			public String getModID() {
				return modid;
			}

			@Override
			public boolean hadPreInit() {
				return hadPreInit;
			}
		});
	}
}
