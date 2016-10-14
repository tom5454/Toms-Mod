package mapwriterTm.util;

import java.util.HashSet;
import java.util.regex.Pattern;

import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Sets;

import com.tom.lib.Configs;

public final class Reference
{
	public static final String MOD_ID = "mapwritertm";
	public static final String MOD_ID_CAP = "MapWriterTm";
	public static final String MOD_NAME = "MapWriter in Tom's Mod";
	public static final String VERSION = Configs.version;
	public static final String MOD_GUIFACTORY_CLASS = "mapwriterTm.gui.ModGuiFactoryHandler";
	public static final String CLIENT_PROXY_CLASS = "mapwriterTm.forge.ClientProxy";
	public static final String SERVER_PROXY_CLASS = "mapwriterTm.forge.CommonProxy";

	public static final String VersionURL = "https://raw.githubusercontent.com/Vectron/Versions/master/MwVersion.json";
	public static final String ForgeVersionURL = "https://raw.githubusercontent.com/Vectron/Versions/master/ForgeMwVersion.json";

	public static final String catOptions = "options";
	public static final String catLargeMapConfig = "largemap";
	public static final String catSmallMapConfig = "smallmap";
	public static final String catFullMapConfig = "fullscreenmap";
	public static final String catMapPos = "mappos";

	public static final String PlayerTrailName = "player";

	public static final Pattern patternInvalidChars = Pattern.compile("[^\\p{L}\\p{Nd}_]");
	public static final Pattern patternInvalidChars2 = Pattern.compile("[^\\p{L}\\p{Nd}_ -]");

	public static final String catWorld = "world";
	public static final String catMarkers = "markers";
	public static final String worldDirConfigName = "mapwriter.cfg";
	public static final String blockColourSaveFileName = "MapWriterBlockColours.txt";
	public static final String blockColourOverridesFileName = "MapWriterBlockColourOverrides.txt";

	public static final ResourceLocation backgroundTexture = new ResourceLocation(
			"mapwriterTm",
			"textures/map/background.png");
	public static final ResourceLocation roundMapTexture = new ResourceLocation(
			"mapwriterTm",
			"textures/map/border_round.png");
	public static final ResourceLocation squareMapTexture = new ResourceLocation(
			"mapwriterTm",
			"textures/map/border_square.png");
	public static final ResourceLocation playerArrowTexture = new ResourceLocation(
			"mapwriterTm",
			"textures/map/arrow_player.png");
	public static final ResourceLocation northArrowTexture = new ResourceLocation(
			"mapwriterTm",
			"textures/map/arrow_north.png");
	public static final ResourceLocation leftArrowTexture = new ResourceLocation(
			"mapwriterTm",
			"textures/map/arrow_text_left.png");
	public static final ResourceLocation rightArrowTexture = new ResourceLocation(
			"mapwriterTm",
			"textures/map/arrow_text_right.png");
	public static final ResourceLocation DummyMapTexture = new ResourceLocation(
			"mapwriterTm",
			"textures/map/dummy_map.png");

	public static final HashSet<String> PROTOCOLS = Sets.newHashSet(new String[]
			{
					"http",
					"https"
			});
}
