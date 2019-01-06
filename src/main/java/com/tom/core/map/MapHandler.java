package com.tom.core.map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import com.tom.core.CoreInit;
import com.tom.lib.api.map.IMapAPI;
import com.tom.lib.api.map.Marker;
import com.tom.lib.api.map.RenderType;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageMarker;
import com.tom.util.TMLogger;

public class MapHandler {
	public static final String catMarkers = "markers";
	public static final String worldDirConfigName = "serverMarkers.cfg";
	public static List<IMapAPI> mapHandlers = new ArrayList<>();
	public static Configuration serverConfig;
	public static MarkerManager markerManagerServer;
	public static void init(File file) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			CoreInit.log.info("Init Mapwriter Client");
			if (Minecraft.getMinecraft().isSingleplayer()) {
				CoreInit.log.info("Init Mapwriter Server");
				markerManagerServer = new MarkerManager();
				serverConfig = new Configuration(file);
				markerManagerServer.load(serverConfig, catMarkers);
				serverConfig.addCustomCategoryComment(catMarkers, "Server Sided Markers saved here.");
				serverConfig.save();
			}
		} else {
			CoreInit.log.info("Init Mapwriter Server");
			markerManagerServer = new MarkerManager();
			serverConfig = new Configuration(file);
			markerManagerServer.load(serverConfig, catMarkers);
			serverConfig.addCustomCategoryComment(catMarkers, "Server Sided Markers saved here.");
			serverConfig.save();
		}
	}

	public static void close() {
		// save(WorldConfig.getInstance().worldConfiguration,
		// Reference.catMarkers);
		if (markerManagerServer != null) {
			CoreInit.log.info("Closing Mapwriter Server");
			markerManagerServer.save(serverConfig, catMarkers);
			markerManagerServer.clear();
			serverConfig.save();
		}
	}
	public static boolean deleteWayPoint(String group, String markerName) {
		return mapHandlers.stream().map(h -> h.deleteWayPoint(group, markerName)).anyMatch(v -> v);
	}

	public static boolean deleteWayPointServer(String group, String markerName) {
		if (markerManagerServer == null) {
			TMLogger.bigWarn("Attempt to delete a waypoint on the server marker manager on the Client");
			return false;
		}
		boolean s = markerManagerServer.delMarker(markerName, group);
		markerManagerServer.update();
		return s;
	}
	public static void createTexturedWayPoint(String group, int mx, int my, int mz, int dim, String markerName, String icon, int beamColor, RenderType beamRenderType, RenderType labelRenderType, boolean reloadable, String beamTexture) {
		mapHandlers.forEach(h -> h.createTexturedWayPoint(group, mx, my, mz, dim, markerName, icon, beamColor, beamRenderType, labelRenderType, reloadable, beamTexture));
	}
	public static void createTexturedWayPointServer(String group, int mx, int my, int mz, int dim, String markerName, String icon, int beamColor, RenderType beamRenderType, RenderType labelRenderType, boolean reloadable, String beamTexture) {
		if (markerManagerServer == null) {
			TMLogger.bigWarn("Attempt to create a waypoint on the server marker manager on the Client");
			return;
		}
		markerManagerServer.addMarker(markerName, group, mx, my, mz, dim, icon, beamColor, beamRenderType, labelRenderType, reloadable, beamTexture);
		markerManagerServer.update();
	}

	public static void sendWaypointCreation(String group, int mx, int my, int mz, int dim, String markerName, String icon, int beamColor, RenderType beamRenderType, RenderType labelRenderType, boolean reloadable, String beamTexture, EntityPlayerMP p) {
		NetworkHandler.sendTo(new MessageMarker(group, mx, my, mz, dim, markerName, icon, beamColor, beamRenderType, labelRenderType, reloadable, beamTexture), p);
	}

	public static void sendWaypointRemove(String group, String markerName, EntityPlayerMP p) {
		NetworkHandler.sendTo(new MessageMarker(group, markerName), p);
	}

	public static void putSyncedMarker(int id, Marker marker) {
		mapHandlers.forEach(h -> h.addSyncedMarker(id, marker));
	}

	public static void update() {
		mapHandlers.forEach(IMapAPI::updateMarkers);
	}

	public static void onPlayerDeath(int mx, int my, int mz, int dim) {
		mapHandlers.forEach(h -> h.onPlayerDeath(mx, my, mz, dim));
	}
}
