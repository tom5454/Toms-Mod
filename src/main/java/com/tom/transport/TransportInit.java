package com.tom.transport;

import static com.tom.core.CoreInit.registerBlock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.tom.api.item.MultipartItem;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;
import com.tom.transport.block.ConveyorBelt;
import com.tom.transport.block.ConveyorSlope;
import com.tom.transport.item.FluidDuct;
import com.tom.transport.item.FluidServo;
import com.tom.transport.item.ItemDuct;
import com.tom.transport.item.ItemFilter;
import com.tom.transport.item.ItemServo;
import com.tom.transport.multipart.PartFilter;
import com.tom.transport.multipart.PartFluidDuct;
import com.tom.transport.multipart.PartFluidServo;
import com.tom.transport.multipart.PartItemDuct;
import com.tom.transport.multipart.PartServo;
import com.tom.transport.tileentity.TileEntityConveyor;
import com.tom.transport.tileentity.TileEntityConveyorSlope;

@Mod(modid = TransportInit.modid,name = "Tom's Mod Transport",version = Configs.version, dependencies = Configs.coreDependencies)
public class TransportInit {
	public static final String modid = Configs.Modid + "|Transport";
	public static Logger log = LogManager.getLogger(modid);
	public static MultipartItem itemDuct, filter, servo, fluidDuct, fluidServo;
	public static Block conveyorBelt, fastConveyorBelt, doubleConveyorBelt, doubleConveyorBeltTurn;
	public static ConveyorSlope conveyorSlope;
	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent){
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		itemDuct = new ItemDuct().setCreativeTab(tabTomsModTransport).setUnlocalizedName("itemDuct");
		filter = new ItemFilter().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.filter");
		servo = new ItemServo().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.servo");
		conveyorBelt = new ConveyorBelt().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.conveyorBelt");
		conveyorSlope = new ConveyorSlope();
		fluidDuct = new FluidDuct().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.fluidDuct");
		fluidServo = new FluidServo().setCreativeTab(tabTomsModTransport).setUnlocalizedName("tm.fluidServo");
		CoreInit.registerMultipart(itemDuct, PartItemDuct.class, "tomsmodtransport");
		CoreInit.registerMultipart(filter, PartFilter.class, "tomsmodtransport");
		CoreInit.registerMultipart(servo, PartServo.class, "tomsmodtransport");
		CoreInit.registerMultipart(fluidDuct, PartFluidDuct.class, "tomsmodtransport");
		registerBlock(conveyorBelt, conveyorBelt.getUnlocalizedName().substring(5));
		registerBlock(conveyorSlope, conveyorSlope.getUnlocalizedName().substring(5));
		CoreInit.registerMultipart(fluidServo, PartFluidServo.class, "tomsmodtransport");
		GameRegistry.registerTileEntity(TileEntityConveyor.class, Configs.Modid + "conveyor");
		GameRegistry.registerTileEntity(TileEntityConveyorSlope.class, Configs.Modid + "conveyorSlope");
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in "+time+" milliseconds");
	}
	public static CreativeTabs tabTomsModTransport = new CreativeTabs("tabTomsModTransport"){

		@Override
		public Item getTabIconItem() {
			return new ItemStack(TransportInit.itemDuct).getItem();
		}

	};
	@EventHandler
	public static void construction(FMLConstructionEvent event){
		CoreInit.modids.add(modid);
	}
}
