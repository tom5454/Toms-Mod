package com.tom.handler;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.network.IGuiHandler;

import com.tom.api.multipart.IGuiMultipart;
import com.tom.defense.item.ItemMultiTool;
import com.tom.defense.tileentity.TileEntityDefenseStation;
import com.tom.defense.tileentity.TileEntityForceCapacitor;
import com.tom.defense.tileentity.TileEntityForceFieldProjector;
import com.tom.defense.tileentity.TileEntitySecurityStation;
import com.tom.defense.tileentity.gui.GuiDefenseStation;
import com.tom.defense.tileentity.gui.GuiForceCapacitor;
import com.tom.defense.tileentity.gui.GuiForceFieldProjector;
import com.tom.defense.tileentity.gui.GuiMultitoolEncoder;
import com.tom.defense.tileentity.gui.GuiProjectorLensConfigurationMain;
import com.tom.defense.tileentity.gui.GuiProjectorLensConfigurationMain.GuiProjectorLensConfig;
import com.tom.defense.tileentity.gui.GuiSecurityStation;
import com.tom.defense.tileentity.inventory.ContainerDefenseStation;
import com.tom.defense.tileentity.inventory.ContainerForceCapacitor;
import com.tom.defense.tileentity.inventory.ContainerForceFieldProjector;
import com.tom.defense.tileentity.inventory.ContainerMultitoolEncoder;
import com.tom.defense.tileentity.inventory.ContainerProjectorLensConfigurationMain;
import com.tom.defense.tileentity.inventory.ContainerProjectorLensConfigurationMain.ContainerProjectorLensConfig;
import com.tom.defense.tileentity.inventory.ContainerSecurityStation;
import com.tom.factory.tileentity.TileEntityAdvBoiler;
import com.tom.factory.tileentity.TileEntityAdvFluidBoiler;
import com.tom.factory.tileentity.TileEntityAlloySmelter;
import com.tom.factory.tileentity.TileEntityBasicBoiler;
import com.tom.factory.tileentity.TileEntityBlastFurnace;
import com.tom.factory.tileentity.TileEntityCoilerPlant;
import com.tom.factory.tileentity.TileEntityCokeOven;
import com.tom.factory.tileentity.TileEntityCrusher;
import com.tom.factory.tileentity.TileEntityElectricFurnace;
import com.tom.factory.tileentity.TileEntityElectricFurnaceAdv;
import com.tom.factory.tileentity.TileEntityElectricalRubberProcessor;
import com.tom.factory.tileentity.TileEntityFluidBoiler;
import com.tom.factory.tileentity.TileEntityFluidTransposer;
import com.tom.factory.tileentity.TileEntityGeoBoiler;
import com.tom.factory.tileentity.TileEntityIndustrialBlastFurnace;
import com.tom.factory.tileentity.TileEntityLaserEngraver;
import com.tom.factory.tileentity.TileEntityMixer;
import com.tom.factory.tileentity.TileEntityMultiblockController;
import com.tom.factory.tileentity.TileEntityPlasticProcessor;
import com.tom.factory.tileentity.TileEntityPlateBlendingMachine;
import com.tom.factory.tileentity.TileEntityRefinery;
import com.tom.factory.tileentity.TileEntityRubberBoiler;
import com.tom.factory.tileentity.TileEntitySolderingStation;
import com.tom.factory.tileentity.TileEntitySteamAlloySmelter;
import com.tom.factory.tileentity.TileEntitySteamCrusher;
import com.tom.factory.tileentity.TileEntitySteamFurnace;
import com.tom.factory.tileentity.TileEntitySteamFurnaceAdv;
import com.tom.factory.tileentity.TileEntitySteamMixer;
import com.tom.factory.tileentity.TileEntitySteamPlateBlender;
import com.tom.factory.tileentity.TileEntitySteamRubberProcessor;
import com.tom.factory.tileentity.TileEntitySteamSolderingStation;
import com.tom.factory.tileentity.TileEntityUVLightbox;
import com.tom.factory.tileentity.TileEntityWireMill;
import com.tom.factory.tileentity.gui.GuiAdvBoiler;
import com.tom.factory.tileentity.gui.GuiAdvElectricFurnace;
import com.tom.factory.tileentity.gui.GuiAdvFluidBoiler;
import com.tom.factory.tileentity.gui.GuiAdvSteamFurnace;
import com.tom.factory.tileentity.gui.GuiAlloySmelter;
import com.tom.factory.tileentity.gui.GuiBasicBoiler;
import com.tom.factory.tileentity.gui.GuiBlastFurnace;
import com.tom.factory.tileentity.gui.GuiCoiler;
import com.tom.factory.tileentity.gui.GuiCokeOven;
import com.tom.factory.tileentity.gui.GuiCrusher;
import com.tom.factory.tileentity.gui.GuiElectricFurnace;
import com.tom.factory.tileentity.gui.GuiElectricalRubberProcessor;
import com.tom.factory.tileentity.gui.GuiFluidBoiler;
import com.tom.factory.tileentity.gui.GuiFluidTransposer;
import com.tom.factory.tileentity.gui.GuiGeoBoiler;
import com.tom.factory.tileentity.gui.GuiIndustrialBlastFurnace;
import com.tom.factory.tileentity.gui.GuiLaserEngraver;
import com.tom.factory.tileentity.gui.GuiMBFuelRod;
import com.tom.factory.tileentity.gui.GuiMixer;
import com.tom.factory.tileentity.gui.GuiPlasticProcessor;
import com.tom.factory.tileentity.gui.GuiPlateBlendingMachine;
import com.tom.factory.tileentity.gui.GuiRefinery;
import com.tom.factory.tileentity.gui.GuiRubberBoiler;
import com.tom.factory.tileentity.gui.GuiSolderingStation;
import com.tom.factory.tileentity.gui.GuiSteamAlloySmelter;
import com.tom.factory.tileentity.gui.GuiSteamCrusher;
import com.tom.factory.tileentity.gui.GuiSteamFurnace;
import com.tom.factory.tileentity.gui.GuiSteamMixer;
import com.tom.factory.tileentity.gui.GuiSteamPlateBlender;
import com.tom.factory.tileentity.gui.GuiSteamRubberProcessor;
import com.tom.factory.tileentity.gui.GuiSteamSolderingStation;
import com.tom.factory.tileentity.gui.GuiUVLightbox;
import com.tom.factory.tileentity.gui.GuiWireMill;
import com.tom.factory.tileentity.inventory.ContainerAdvBoiler;
import com.tom.factory.tileentity.inventory.ContainerAdvElectricFurnace;
import com.tom.factory.tileentity.inventory.ContainerAdvFluidBoiler;
import com.tom.factory.tileentity.inventory.ContainerAdvSteamFurnace;
import com.tom.factory.tileentity.inventory.ContainerAlloySmelter;
import com.tom.factory.tileentity.inventory.ContainerBasicBoiler;
import com.tom.factory.tileentity.inventory.ContainerBlastFurnace;
import com.tom.factory.tileentity.inventory.ContainerCoiler;
import com.tom.factory.tileentity.inventory.ContainerCokeOven;
import com.tom.factory.tileentity.inventory.ContainerCrusher;
import com.tom.factory.tileentity.inventory.ContainerElectricFurnace;
import com.tom.factory.tileentity.inventory.ContainerElectricalRubberProcessor;
import com.tom.factory.tileentity.inventory.ContainerFluidBoiler;
import com.tom.factory.tileentity.inventory.ContainerFluidTransposer;
import com.tom.factory.tileentity.inventory.ContainerGeoBoiler;
import com.tom.factory.tileentity.inventory.ContainerIndustrialBlastFurnace;
import com.tom.factory.tileentity.inventory.ContainerLaserEngraver;
import com.tom.factory.tileentity.inventory.ContainerMBFuelRod;
import com.tom.factory.tileentity.inventory.ContainerMixer;
import com.tom.factory.tileentity.inventory.ContainerPlasticProcessor;
import com.tom.factory.tileentity.inventory.ContainerPlateBlendingMachine;
import com.tom.factory.tileentity.inventory.ContainerRefinery;
import com.tom.factory.tileentity.inventory.ContainerRubberBoiler;
import com.tom.factory.tileentity.inventory.ContainerSolderingStation;
import com.tom.factory.tileentity.inventory.ContainerSteamAlloySmelter;
import com.tom.factory.tileentity.inventory.ContainerSteamCrusher;
import com.tom.factory.tileentity.inventory.ContainerSteamFurnace;
import com.tom.factory.tileentity.inventory.ContainerSteamMixer;
import com.tom.factory.tileentity.inventory.ContainerSteamPlateBlender;
import com.tom.factory.tileentity.inventory.ContainerSteamRubberProcessor;
import com.tom.factory.tileentity.inventory.ContainerSteamSolderingStation;
import com.tom.factory.tileentity.inventory.ContainerUVLightbox;
import com.tom.factory.tileentity.inventory.ContainerWireMill;
import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.TileEntityCraftingTerminal;
import com.tom.storage.tileentity.TileEntityDrive;
import com.tom.storage.tileentity.TileEntityInterface;
import com.tom.storage.tileentity.TileEntityLimitableChest;
import com.tom.storage.tileentity.TileEntityPatternTerminal;
import com.tom.storage.tileentity.TileEntityStorageNetworkController;
import com.tom.storage.tileentity.gui.GuiBasicTerminal;
import com.tom.storage.tileentity.gui.GuiBlockInterface;
import com.tom.storage.tileentity.gui.GuiController;
import com.tom.storage.tileentity.gui.GuiCraftingTerminal;
import com.tom.storage.tileentity.gui.GuiDrive;
import com.tom.storage.tileentity.gui.GuiLimitableChest;
import com.tom.storage.tileentity.gui.GuiPatternOptions;
import com.tom.storage.tileentity.gui.GuiPatternTerminal;
import com.tom.storage.tileentity.inventory.ContainerBasicTerminal;
import com.tom.storage.tileentity.inventory.ContainerBlockInterface;
import com.tom.storage.tileentity.inventory.ContainerController;
import com.tom.storage.tileentity.inventory.ContainerCraftingTerminal;
import com.tom.storage.tileentity.inventory.ContainerDrive;
import com.tom.storage.tileentity.inventory.ContainerLimitableChest;
import com.tom.storage.tileentity.inventory.ContainerPatternOptions;
import com.tom.storage.tileentity.inventory.ContainerPatternTerminal;
import com.tom.util.TMLogger;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.TileEntityResearchTable;
import com.tom.core.tileentity.TileEntityTabletCrafter;
import com.tom.core.tileentity.gui.GuiConfigurator;
import com.tom.core.tileentity.gui.GuiConfigurator.GuiConfiguratorChoose;
import com.tom.core.tileentity.gui.GuiResearchTable;
import com.tom.core.tileentity.gui.GuiTablet;
import com.tom.core.tileentity.gui.GuiTabletCrafter;
import com.tom.core.tileentity.inventory.ContainerConfigurator;
import com.tom.core.tileentity.inventory.ContainerConfigurator.ContainerConfiguratorChoose;
import com.tom.core.tileentity.inventory.ContainerResearchTable;
import com.tom.core.tileentity.inventory.ContainerTablet;
import com.tom.core.tileentity.inventory.ContainerTabletCrafter;

import com.tom.energy.tileentity.TileEntityCharger;
import com.tom.energy.tileentity.gui.GuiCharger;
import com.tom.energy.tileentity.inventory.ContainerCharger;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.api.slot.EnumFaceSlot;

public class GuiHandler implements IGuiHandler {

	public enum GuiIDs {
		tabletCrafter, tablet, /*itemProxy, */researchTable, limitableChest, forceCapacitor,
		configurator, securityStation, multitoolWriter, projectorLensConfigMain, projectorLensConfig,
		forceFieldProjector, defenseStation, basicTerminalBlock, drive, multipartMid, multipartUp,
		multipartDown, multipartNorth, multipartSouth, multipartEast, multipartWest, blockInterface,
		patternTerminal, crusher, plateBlendingMachine, wireMill, coilerPlant, basicBoiler, advBoiler,
		steamCrusher, steamFurnace, steamPlateBlender, steamFurnaceAdv, electricFurnace, alloySmelter,
		steamAlloySmelter, electricFurnaceAdv, blockCraftingTerminal, patternOptions, steamSolderingStation,
		cokeOven, blastFurnace, solderingStation, fluidTransposer, industrialBlastFurnace, refinery, charger,
		geoBoiler, fluidBoiler, advFluidBoiler, uvLightbox, plasticProcessor, steamMixer, mixer, laserEngraver,
		mbfuelrod, storageNetworkController, configuratorChoose, rubberBoiler, patternOptionsPart, rubberProcessor,
		craftingProgress, eRubberProcessor,

		;
		private static final GuiIDs[] VALUES = values();

		public static GuiIDs getMultipartGuiId(EnumFacing pos) {
			return pos == null ? multipartMid : (pos == EnumFacing.UP ? multipartUp : pos == EnumFacing.DOWN ? multipartDown : pos == EnumFacing.NORTH ? multipartNorth : pos == EnumFacing.SOUTH ? multipartSouth : pos == EnumFacing.WEST ? multipartWest : multipartEast);
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (GuiIDs.VALUES[ID]) {
		case tabletCrafter:
			return new ContainerTabletCrafter(player.inventory, (TileEntityTabletCrafter) world.getTileEntity(new BlockPos(x, y, z)));
		case tablet:
			return new ContainerTablet(player.getHeldItemMainhand(), world, player);
			/*case itemProxy:
			return new ContainerItemProxy(player.inventory, (TileEntityItemProxy) world.getTileEntity(new BlockPos(x, y, z)));*/
		case researchTable:
			return new ContainerResearchTable(player.inventory, (TileEntityResearchTable) world.getTileEntity((new BlockPos(x, y, z))));
		case limitableChest:
			return new ContainerLimitableChest(player.inventory, (TileEntityLimitableChest) world.getTileEntity(new BlockPos(x, y, z)));
		case forceCapacitor:
			return new ContainerForceCapacitor(player.inventory, (TileEntityForceCapacitor) world.getTileEntity(new BlockPos(x, y, z)));
		case securityStation:
			return new ContainerSecurityStation(player.inventory, (TileEntitySecurityStation) world.getTileEntity(new BlockPos(x, y, z)));
		case multitoolWriter:
			return new ContainerMultitoolEncoder(player, player.getHeldItemMainhand());
		case projectorLensConfigMain:
			if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ItemMultiTool)
				return new ContainerProjectorLensConfigurationMain(player);
			else {
				TMLogger.error("Invalid held item");
				return null;
			}
		case projectorLensConfig:
			if (player.openContainer instanceof ContainerProjectorLensConfigurationMain) {
				ContainerProjectorLensConfigurationMain c = (ContainerProjectorLensConfigurationMain) player.openContainer;
				if (c.entryList.size() > x) { return new ContainerProjectorLensConfig(c.entryList.get(x), player, x); }
			}
			TMLogger.error("Invalid open container");
			return null;
		case forceFieldProjector:
			return new ContainerForceFieldProjector(player.inventory, (TileEntityForceFieldProjector) world.getTileEntity(new BlockPos(x, y, z)));
		case defenseStation:
			return new ContainerDefenseStation(player.inventory, (TileEntityDefenseStation) world.getTileEntity(new BlockPos(x, y, z)));
		case basicTerminalBlock:
			return new ContainerBasicTerminal(player.inventory, (TileEntityBasicTerminal) world.getTileEntity(new BlockPos(x, y, z)));
		case drive:
			return new ContainerDrive(player.inventory, (TileEntityDrive) world.getTileEntity(new BlockPos(x, y, z)));
		case multipartDown:
			return getContainerFromMultipart(world, EnumFacing.DOWN, new BlockPos(x, y, z), player);
		case multipartUp:
			return getContainerFromMultipart(world, EnumFacing.UP, new BlockPos(x, y, z), player);
		case multipartNorth:
			return getContainerFromMultipart(world, EnumFacing.NORTH, new BlockPos(x, y, z), player);
		case multipartSouth:
			return getContainerFromMultipart(world, EnumFacing.SOUTH, new BlockPos(x, y, z), player);
		case multipartEast:
			return getContainerFromMultipart(world, EnumFacing.EAST, new BlockPos(x, y, z), player);
		case multipartWest:
			return getContainerFromMultipart(world, EnumFacing.WEST, new BlockPos(x, y, z), player);
		case multipartMid:
			return getContainerFromMultipart(world, null, new BlockPos(x, y, z), player);
		case blockInterface:
			return new ContainerBlockInterface(player.inventory, (TileEntityInterface) world.getTileEntity(new BlockPos(x, y, z)));
		case patternTerminal:
			return new ContainerPatternTerminal(player.inventory, (TileEntityPatternTerminal) world.getTileEntity(new BlockPos(x, y, z)));
		case crusher:
			return new ContainerCrusher(player.inventory, (TileEntityCrusher) world.getTileEntity(new BlockPos(x, y, z)));
		case plateBlendingMachine:
			return new ContainerPlateBlendingMachine(player.inventory, (TileEntityPlateBlendingMachine) world.getTileEntity(new BlockPos(x, y, z)));
		case wireMill:
			return new ContainerWireMill(player.inventory, (TileEntityWireMill) world.getTileEntity(new BlockPos(x, y, z)));
		case coilerPlant:
			return new ContainerCoiler(player.inventory, (TileEntityCoilerPlant) world.getTileEntity(new BlockPos(x, y, z)));
		case basicBoiler:
			return new ContainerBasicBoiler(player.inventory, (TileEntityBasicBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case advBoiler:
			return new ContainerAdvBoiler(player.inventory, (TileEntityAdvBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case steamCrusher:
			return new ContainerSteamCrusher(player.inventory, (TileEntitySteamCrusher) world.getTileEntity(new BlockPos(x, y, z)));
		case alloySmelter:
			return new ContainerAlloySmelter(player.inventory, (TileEntityAlloySmelter) world.getTileEntity(new BlockPos(x, y, z)));
		case electricFurnace:
			return new ContainerElectricFurnace(player.inventory, (TileEntityElectricFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case electricFurnaceAdv:
			return new ContainerAdvElectricFurnace(player.inventory, (TileEntityElectricFurnaceAdv) world.getTileEntity(new BlockPos(x, y, z)));
		case steamAlloySmelter:
			return new ContainerSteamAlloySmelter(player.inventory, (TileEntitySteamAlloySmelter) world.getTileEntity(new BlockPos(x, y, z)));
		case steamFurnace:
			return new ContainerSteamFurnace(player.inventory, (TileEntitySteamFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case steamFurnaceAdv:
			return new ContainerAdvSteamFurnace(player.inventory, (TileEntitySteamFurnaceAdv) world.getTileEntity(new BlockPos(x, y, z)));
		case steamPlateBlender:
			return new ContainerSteamPlateBlender(player.inventory, (TileEntitySteamPlateBlender) world.getTileEntity(new BlockPos(x, y, z)));
		case blockCraftingTerminal:
			return new ContainerCraftingTerminal(player.inventory, (TileEntityCraftingTerminal) world.getTileEntity(new BlockPos(x, y, z)));
		case patternOptions:
			return new ContainerPatternOptions(player.inventory, (TileEntityPatternTerminal) world.getTileEntity(new BlockPos(x, y, z)));
		case steamSolderingStation:
			return new ContainerSteamSolderingStation(player.inventory, (TileEntitySteamSolderingStation) world.getTileEntity(new BlockPos(x, y, z)));
		case cokeOven:
			return new ContainerCokeOven(player.inventory, (TileEntityCokeOven) world.getTileEntity(new BlockPos(x, y, z)));
		case blastFurnace:
			return new ContainerBlastFurnace(player.inventory, (TileEntityBlastFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case solderingStation:
			return new ContainerSolderingStation(player.inventory, (TileEntitySolderingStation) world.getTileEntity(new BlockPos(x, y, z)));
		case fluidTransposer:
			return new ContainerFluidTransposer(player.inventory, (TileEntityFluidTransposer) world.getTileEntity(new BlockPos(x, y, z)));
		case industrialBlastFurnace:
			return new ContainerIndustrialBlastFurnace(player.inventory, (TileEntityIndustrialBlastFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case refinery:
			return new ContainerRefinery(player.inventory, (TileEntityRefinery) world.getTileEntity(new BlockPos(x, y, z)));
		case charger:
			return new ContainerCharger(player.inventory, (TileEntityCharger) world.getTileEntity(new BlockPos(x, y, z)));
		case geoBoiler:
			return new ContainerGeoBoiler(player.inventory, (TileEntityGeoBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case fluidBoiler:
			return new ContainerFluidBoiler(player.inventory, (TileEntityFluidBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case advFluidBoiler:
			return new ContainerAdvFluidBoiler(player.inventory, (TileEntityAdvFluidBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case uvLightbox:
			return new ContainerUVLightbox(player.inventory, (TileEntityUVLightbox) world.getTileEntity(new BlockPos(x, y, z)));
		case steamMixer:
			return new ContainerSteamMixer(player.inventory, (TileEntitySteamMixer) world.getTileEntity(new BlockPos(x, y, z)));
		case mixer:
			return new ContainerMixer(player.inventory, (TileEntityMixer) world.getTileEntity(new BlockPos(x, y, z)));
		case plasticProcessor:
			return new ContainerPlasticProcessor(player.inventory, (TileEntityPlasticProcessor) world.getTileEntity(new BlockPos(x, y, z)));
		case laserEngraver:
			return new ContainerLaserEngraver(player.inventory, (TileEntityLaserEngraver) world.getTileEntity(new BlockPos(x, y, z)));
		case storageNetworkController:
			return new ContainerController(player.inventory, (TileEntityStorageNetworkController) world.getTileEntity(new BlockPos(x, y, z)));
		case configuratorChoose:
			return new ContainerConfiguratorChoose(world, new BlockPos(x, y, z), player);
		case configurator:
			return new ContainerConfigurator(player, world, TomsModUtils.createBlockPos(x, y), z);
		case rubberBoiler:
			return new ContainerRubberBoiler(player.inventory, (TileEntityRubberBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case patternOptionsPart:
			return new ContainerPatternOptions(player, world, TomsModUtils.createBlockPos(x, y), z);
		case rubberProcessor:
			return new ContainerSteamRubberProcessor(player.inventory, (TileEntitySteamRubberProcessor) world.getTileEntity(new BlockPos(x, y, z)));
		case eRubberProcessor:
			return new ContainerElectricalRubberProcessor(player.inventory, (TileEntityElectricalRubberProcessor) world.getTileEntity(new BlockPos(x, y, z)));
		case mbfuelrod:
			return new ContainerMBFuelRod(player.inventory, (TileEntityMultiblockController) world.getTileEntity(new BlockPos(x, y, z)));
		case craftingProgress:
			TomsModUtils.sendChatTranslate(player, "WIP");
			return null;
		default:
			break;
		}
		throw new IllegalArgumentException("No gui with id " + ID);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (GuiIDs.VALUES[ID]) {
		case tabletCrafter:
			return new GuiTabletCrafter(player.inventory, (TileEntityTabletCrafter) world.getTileEntity(new BlockPos(x, y, z)));
		case tablet:
			return new GuiTablet(player.getHeldItemMainhand(), world, player);
			/*case itemProxy:
			return new GuiItemProxy(player.inventory, (TileEntityItemProxy) world.getTileEntity(new BlockPos(x, y, z)));*/
		case researchTable:
			return new GuiResearchTable(player.inventory, (TileEntityResearchTable) world.getTileEntity((new BlockPos(x, y, z))));
		case limitableChest:
			return new GuiLimitableChest(player.inventory, (TileEntityLimitableChest) world.getTileEntity(new BlockPos(x, y, z)));
		case forceCapacitor:
			return new GuiForceCapacitor(player.inventory, (TileEntityForceCapacitor) world.getTileEntity(new BlockPos(x, y, z)));
		case securityStation:
			return new GuiSecurityStation(player.inventory, (TileEntitySecurityStation) world.getTileEntity(new BlockPos(x, y, z)));
		case multitoolWriter:
			return new GuiMultitoolEncoder(player, player.getHeldItemMainhand());
		case projectorLensConfigMain:
			if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ItemMultiTool)
				return new GuiProjectorLensConfigurationMain(player);
			else {
				TMLogger.error("Invalid held item");
				return null;
			}
		case projectorLensConfig:
			if (player.openContainer instanceof ContainerProjectorLensConfigurationMain) {
				ContainerProjectorLensConfigurationMain c = (ContainerProjectorLensConfigurationMain) player.openContainer;
				if (c.entryList.size() > x) { return new GuiProjectorLensConfig(c.entryList.get(x), player, x); }
			}
			TMLogger.error("Invalid open container");
			return null;
		case forceFieldProjector:
			return new GuiForceFieldProjector(player.inventory, (TileEntityForceFieldProjector) world.getTileEntity(new BlockPos(x, y, z)));
		case defenseStation:
			return new GuiDefenseStation(player.inventory, (TileEntityDefenseStation) world.getTileEntity(new BlockPos(x, y, z)));
		case basicTerminalBlock:
			return new GuiBasicTerminal(player.inventory, (TileEntityBasicTerminal) world.getTileEntity(new BlockPos(x, y, z)));
		case drive:
			return new GuiDrive(player.inventory, (TileEntityDrive) world.getTileEntity(new BlockPos(x, y, z)));
		case multipartDown:
			return getGuiFromMultipart(world, EnumFacing.DOWN, new BlockPos(x, y, z), player);
		case multipartUp:
			return getGuiFromMultipart(world, EnumFacing.UP, new BlockPos(x, y, z), player);
		case multipartNorth:
			return getGuiFromMultipart(world, EnumFacing.NORTH, new BlockPos(x, y, z), player);
		case multipartSouth:
			return getGuiFromMultipart(world, EnumFacing.SOUTH, new BlockPos(x, y, z), player);
		case multipartEast:
			return getGuiFromMultipart(world, EnumFacing.EAST, new BlockPos(x, y, z), player);
		case multipartWest:
			return getGuiFromMultipart(world, EnumFacing.WEST, new BlockPos(x, y, z), player);
		case multipartMid:
			return getGuiFromMultipart(world, null, new BlockPos(x, y, z), player);
		case blockInterface:
			return new GuiBlockInterface(player.inventory, (TileEntityInterface) world.getTileEntity(new BlockPos(x, y, z)));
		case patternTerminal:
			return new GuiPatternTerminal(player.inventory, (TileEntityPatternTerminal) world.getTileEntity(new BlockPos(x, y, z)));
		case crusher:
			return new GuiCrusher(player.inventory, (TileEntityCrusher) world.getTileEntity(new BlockPos(x, y, z)));
		case plateBlendingMachine:
			return new GuiPlateBlendingMachine(player.inventory, (TileEntityPlateBlendingMachine) world.getTileEntity(new BlockPos(x, y, z)));
		case wireMill:
			return new GuiWireMill(player.inventory, (TileEntityWireMill) world.getTileEntity(new BlockPos(x, y, z)));
		case coilerPlant:
			return new GuiCoiler(player.inventory, (TileEntityCoilerPlant) world.getTileEntity(new BlockPos(x, y, z)));
		case basicBoiler:
			return new GuiBasicBoiler(player.inventory, (TileEntityBasicBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case advBoiler:
			return new GuiAdvBoiler(player.inventory, (TileEntityAdvBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case steamCrusher:
			return new GuiSteamCrusher(player.inventory, (TileEntitySteamCrusher) world.getTileEntity(new BlockPos(x, y, z)));
		case alloySmelter:
			return new GuiAlloySmelter(player.inventory, (TileEntityAlloySmelter) world.getTileEntity(new BlockPos(x, y, z)));
		case electricFurnace:
			return new GuiElectricFurnace(player.inventory, (TileEntityElectricFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case electricFurnaceAdv:
			return new GuiAdvElectricFurnace(player.inventory, (TileEntityElectricFurnaceAdv) world.getTileEntity(new BlockPos(x, y, z)));
		case steamAlloySmelter:
			return new GuiSteamAlloySmelter(player.inventory, (TileEntitySteamAlloySmelter) world.getTileEntity(new BlockPos(x, y, z)));
		case steamFurnace:
			return new GuiSteamFurnace(player.inventory, (TileEntitySteamFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case steamFurnaceAdv:
			return new GuiAdvSteamFurnace(player.inventory, (TileEntitySteamFurnaceAdv) world.getTileEntity(new BlockPos(x, y, z)));
		case steamPlateBlender:
			return new GuiSteamPlateBlender(player.inventory, (TileEntitySteamPlateBlender) world.getTileEntity(new BlockPos(x, y, z)));
		case blockCraftingTerminal:
			return new GuiCraftingTerminal(player.inventory, (TileEntityCraftingTerminal) world.getTileEntity(new BlockPos(x, y, z)));
		case patternOptions:
			return new GuiPatternOptions(player.inventory, (TileEntityPatternTerminal) world.getTileEntity(new BlockPos(x, y, z)));
		case steamSolderingStation:
			return new GuiSteamSolderingStation(player.inventory, (TileEntitySteamSolderingStation) world.getTileEntity(new BlockPos(x, y, z)));
		case cokeOven:
			return new GuiCokeOven(player.inventory, (TileEntityCokeOven) world.getTileEntity(new BlockPos(x, y, z)));
		case blastFurnace:
			return new GuiBlastFurnace(player.inventory, (TileEntityBlastFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case solderingStation:
			return new GuiSolderingStation(player.inventory, (TileEntitySolderingStation) world.getTileEntity(new BlockPos(x, y, z)));
		case fluidTransposer:
			return new GuiFluidTransposer(player.inventory, (TileEntityFluidTransposer) world.getTileEntity(new BlockPos(x, y, z)));
		case industrialBlastFurnace:
			return new GuiIndustrialBlastFurnace(player.inventory, (TileEntityIndustrialBlastFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case refinery:
			return new GuiRefinery(player.inventory, (TileEntityRefinery) world.getTileEntity(new BlockPos(x, y, z)));
		case charger:
			return new GuiCharger(player.inventory, (TileEntityCharger) world.getTileEntity(new BlockPos(x, y, z)));
		case geoBoiler:
			return new GuiGeoBoiler(player.inventory, (TileEntityGeoBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case fluidBoiler:
			return new GuiFluidBoiler(player.inventory, (TileEntityFluidBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case advFluidBoiler:
			return new GuiAdvFluidBoiler(player.inventory, (TileEntityAdvFluidBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case uvLightbox:
			return new GuiUVLightbox(player.inventory, (TileEntityUVLightbox) world.getTileEntity(new BlockPos(x, y, z)));
		case steamMixer:
			return new GuiSteamMixer(player.inventory, (TileEntitySteamMixer) world.getTileEntity(new BlockPos(x, y, z)));
		case mixer:
			return new GuiMixer(player.inventory, (TileEntityMixer) world.getTileEntity(new BlockPos(x, y, z)));
		case plasticProcessor:
			return new GuiPlasticProcessor(player.inventory, (TileEntityPlasticProcessor) world.getTileEntity(new BlockPos(x, y, z)));
		case laserEngraver:
			return new GuiLaserEngraver(player.inventory, (TileEntityLaserEngraver) world.getTileEntity(new BlockPos(x, y, z)));
		case storageNetworkController:
			return new GuiController(player.inventory, (TileEntityStorageNetworkController) world.getTileEntity(new BlockPos(x, y, z)));
		case configuratorChoose:
			return new GuiConfiguratorChoose(world, new BlockPos(x, y, z), player);
		case configurator:
			return new GuiConfigurator(player, world, TomsModUtils.createBlockPos(x, y), z);
		case rubberBoiler:
			return new GuiRubberBoiler(player.inventory, (TileEntityRubberBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case patternOptionsPart:
			return new GuiPatternOptions(player, world, TomsModUtils.createBlockPos(x, y), z);
		case rubberProcessor:
			return new GuiSteamRubberProcessor(player.inventory, (TileEntitySteamRubberProcessor) world.getTileEntity(new BlockPos(x, y, z)));
		case eRubberProcessor:
			return new GuiElectricalRubberProcessor(player.inventory, (TileEntityElectricalRubberProcessor) world.getTileEntity(new BlockPos(x, y, z)));
		case mbfuelrod:
			return new GuiMBFuelRod(player.inventory, (TileEntityMultiblockController) world.getTileEntity(new BlockPos(x, y, z)));
		default:
			break;
		}
		throw new IllegalArgumentException("No gui with id " + ID);
	}

	private Object getContainerFromMultipart(World world, EnumFacing partPos, BlockPos pos, EntityPlayer player) {
		IMultipartContainer container = MultipartHelper.getContainer(world, pos).orElse(null);
		if (container == null) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof IGuiMultipart) { return ((IGuiMultipart) tile).getContainer(player); }
			return null;
		}
		Optional<IPartInfo> part = container.get(partPos == null ? EnumCenterSlot.CENTER : EnumFaceSlot.fromFace(partPos));
		if (part.isPresent() && part.get().getTile() instanceof IGuiMultipart) { return ((IGuiMultipart) part.get().getTile()).getContainer(player); }
		return null;
	}

	private Object getGuiFromMultipart(World world, EnumFacing partPos, BlockPos pos, EntityPlayer player) {
		IMultipartContainer container = MultipartHelper.getContainer(world, pos).orElse(null);
		if (container == null) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof IGuiMultipart) { return ((IGuiMultipart) tile).getGui(player); }
			return null;
		}
		Optional<IPartInfo> part = container.get(partPos == null ? EnumCenterSlot.CENTER : EnumFaceSlot.fromFace(partPos));
		if (part.isPresent() && part.get().getTile() instanceof IGuiMultipart) { return ((IGuiMultipart) part.get().getTile()).getGui(player); }
		return null;
	}
}
