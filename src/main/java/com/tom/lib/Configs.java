package com.tom.lib;

import java.util.UUID;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import com.tom.core.CoreInit;

/** {@link com.tom.config.Config} */
public final class Configs {
	public static final String ModidL = "tomsmod";
	public static final String Modid = "TomsMod";
	public static final String ModName = "Tom's Tech Mod";
	public static final String version = "2.1.3";
	public static final String CLIENT_PROXY_CLASS = "com.tom.proxy.ClientProxy";
	public static final String SERVER_PROXY_CLASS = "com.tom.proxy.ServerProxy";
	public static final int InjectorMaxEnergy = 4000000;
	public static final int InjectorMaxEnergyInput = 1000000;
	public static final int InjectorUsage = 100;
	public static final EnumFacing InjectorPort = EnumFacing.DOWN;
	public static final int ChargerMaxEnergy = 1000000;
	public static final int textureUpdateRate = 40;
	public static final float DUCT_MIN_POS = 0.375F;
	public static final float DUCT_MAX_POS = 0.625F;
	public static final int BASIC_TANK_SIZE = 10000;
	public static final int FusionStartFluidAmmount = 500;
	public static final int chargerStart = 1000;
	public static final int ChargerUsage = 10;
	public static final boolean machinesExplode = true;
	public static final int multiblockPressurePortVolume = 100000;
	public static final int updateRate = 100;
	public static final double EnergyCellCoreMax = 500000000;
	public static final int monitorSize = 64;
	public static final String MODEL_LOCATION = "tomsmod:textures/models/";
	public static final ResourceLocation antennaA = new ResourceLocation(MODEL_LOCATION + "TabletAccessPoint.png");
	public static final ResourceLocation antennaLight = new ResourceLocation(MODEL_LOCATION + "TabletAccessPointOn.png");
	public static final ResourceLocation antennaLightOn = new ResourceLocation(MODEL_LOCATION + "Antenna.png");
	public static final double wirelessChargerLoss = 4D;
	public static final int maxProcessorTier = 10;
	public static final ResourceLocation contBoxOff = new ResourceLocation(MODEL_LOCATION + "ControllerBox.png");
	public static final ResourceLocation cam = new ResourceLocation(MODEL_LOCATION + "Camera.png");
	public static final String keyCatergory = "keys.tomsmod.category";
	public static final String keyPrefix = "keys.tomsmod.key.";
	public static final ResourceLocation monitor = new ResourceLocation(MODEL_LOCATION + "monitor.png");
	public static final ResourceLocation pixel = new ResourceLocation("tm:pixel.png");
	public static final String modid_Short = "tm";
	public static final String coreDependencies = "required-after:" + CoreInit.modid + ";";
	public static final ResourceLocation driveModel = new ResourceLocation(MODEL_LOCATION + "driveCellsModel.png");
	public static final String fakePlayerName = "[TomsMod]";
	public static final int fluidDuctMaxInsert = 1000;
	public static final int fluidDuctMaxExtract = 500;
	public static final int LibVersion = 1;
	public static final String mainDependencies = "required-after:tomslib@[1." + LibVersion + ".0,1." + (LibVersion + 1) + ".0);";
	public static final String updateJson = "https://raw.githubusercontent.com/tom5454/Toms-Mod/master/version-check.json";
	public static final ResourceLocation controllerScreenModel = new ResourceLocation(MODEL_LOCATION + "controllerScreen.png");
	public static final ResourceLocation lcdFont = new ResourceLocation("tomsmod:textures/font/ascii_lcd.png");
	public static final ResourceLocation plasticProcessor = new ResourceLocation(MODEL_LOCATION + "plasticprocessor.png");
	public static final UUID tomsmodFakePlayerUUID = UUID.nameUUIDFromBytes(fakePlayerName.getBytes());
}
