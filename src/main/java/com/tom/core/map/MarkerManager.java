package com.tom.core.map;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.config.Configuration;

import com.tom.lib.api.map.Marker;
import com.tom.lib.api.map.RenderType;
import com.tom.network.messages.MessageMarkerSync;
import com.tom.util.TMLogger;

public class MarkerManager {
	private static final String markerListName = "markerList";
	public List<Marker> markerList = new ArrayList<>();
	public List<String> groupList = new ArrayList<>();

	public void load(Configuration config, String category) {
		this.markerList.clear();

		if (config != null && config.hasCategory(category)) {
			String[] list = config.get(category, markerListName, new String[]{}).getStringList();
			for (int i = 0;i < list.length;i++) {
				Marker marker = this.stringToMarker(list[i]);
				if (marker != null) {
					markerList.add(marker);
				} else {
				}
			}
		}

		this.update();
	}

	public void save(Configuration config, String category) {
		List<String> list = new ArrayList<>();
		for (int i = 0;i < markerList.size();i++) {
			Marker marker = markerList.get(i);
			String value = this.markerToString(marker);
			if (value != null) {
				list.add(value);
			}
		}
		String[] sList = list.toArray(new String[]{});
		config.get(category, markerListName, new String[]{}).set(sList);
		if (config.hasChanged()) {
			config.save();
		}
	}

	private String markerToString(Marker marker) {
		NBTTagCompound tag = new NBTTagCompound();
		if (marker != null)
			marker.writeToNBT(tag);
		if (tag.getBoolean("null"))
			return null;
		else
			return tag.toString();
	}
	public Marker stringToMarker(String s) {
		NBTTagCompound tag;
		try {
			tag = JsonToNBT.getTagFromJson(s);
			return Marker.fromNBT(tag);
		} catch (Exception e) {
			TMLogger.warn("Marker.fromString: invalid marker '%s'. Error: " + e.getMessage(), s != null ? s : "~~NULL~~");
		}

		return null;
	}

	public void clear() {
		markerList.clear();
	}

	public boolean delMarker(String name, String group) {
		Marker markerToDelete = null;
		for (int i = 0;i < markerList.size();i++) {
			Marker marker = markerList.get(i);
			if (((name == null) || marker.name.equals(name)) && ((group == null) || marker.groupName.equals(group))) {
				markerToDelete = marker;
				break;
			}
		}
		return this.delMarker(markerToDelete);
	}

	public boolean delMarker(Marker markerToDelete) {
		boolean result = this.markerList.remove(markerToDelete);
		this.save(MapHandler.serverConfig, MapHandler.catMarkers);

		return result;
	}

	public void addMarker(String name, String groupName, int x, int y, int z, int dimension, String icon, int beamColor, RenderType beamType, RenderType labelType, boolean reloadable, String beamTexture) {
		if (beamType == null)
			beamType = RenderType.NORMAL;
		if (labelType == null)
			labelType = RenderType.NORMAL;
		markerList.add(new Marker(name, groupName, x, y, z, dimension, icon, beamColor, beamType, labelType, beamTexture, reloadable));
		this.save(MapHandler.serverConfig, MapHandler.catMarkers);
	}

	public void update() {
		MessageMarkerSync.sendSyncMessage();
	}

}
