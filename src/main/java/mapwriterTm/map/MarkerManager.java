package mapwriterTm.map;

import static org.lwjgl.opengl.ARBDepthClamp.GL_DEPTH_CLAMP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import mapwriterTm.Mw;
import mapwriterTm.config.Config;
import mapwriterTm.config.WorldConfig;
import mapwriterTm.map.Marker.RenderType;
import mapwriterTm.map.mapmode.MapMode;
import mapwriterTm.util.Logging;
import mapwriterTm.util.Reference;
import mapwriterTm.util.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.core.Minimap;
import com.tom.network.messages.MessageMarkerSync;

public class MarkerManager {

	public List<Marker> markerList = new ArrayList<>();
	public List<String> groupList = new ArrayList<>();

	public Map<Integer, Marker> markerListServer = new HashMap<>();

	public List<Marker> visibleMarkerList = new ArrayList<>();

	private String visibleGroupName = "none";

	public Marker selectedMarker = null;

	@SideOnly(Side.CLIENT)
	private static final double topTexPos = 32D / 256D, bottomTexPos = (256D - 32D) / 256D;
	private static final String markerListName = "markerList";
	private final boolean isClient;

	public MarkerManager(boolean isClient) {
		this.isClient = isClient;
	}

	public void load(Configuration config, String category) {
		/*NBTTagCompound config = null;
		try{
			config = CompressedStreamTools.read(file);
		}catch(Exception e){
			Logging.logWarning("error: could not load markers from config file");
		}*/
		this.markerList.clear();

		if (config != null && config.hasCategory(category)) {
			// int markerCount = config.getInteger("markerCount");
			// NBTTagList list = config.getTagList("markers", 10);
			this.visibleGroupName = "all";

			String[] list = config.get(category, markerListName, new String[]{}).getStringList();
			for (int i = 0;i < list.length;i++) {
				Marker marker = this.stringToMarker(list[i]);
				if (marker != null) {
					this.addMarker(marker);
				} else {
					// Logging.logWarning("error: could not load " + key + "
					// from config file");
				}
			}
		}

		this.update();
	}

	public void save(Configuration config, String category) {
		// config.removeCategory(config.getCategory(category));
		// config.get(category, "markerCount", 0).set(this.markerList.size());
		// config.get(category, "visibleGroup", "").set(this.visibleGroupName);
		// NBTTagCompound tag = new NBTTagCompound();
		// int i = 0;
		// NBTTagList list = new NBTTagList();
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
		// tag.setTag(category, list);
		/*try {
			CompressedStreamTools.write(tag, file);
		} catch (Exception e) {
			Logging.logWarning("error: could not save markers to config file");
		}*/
		// config.get(category, "markerCount", 0).set(i);
		if (config.hasChanged()) {
			config.save();
		}
	}

	public void setVisibleGroupName(String groupName) {
		if (groupName != null) {
			this.visibleGroupName = Utils.mungeStringForConfig(groupName);
		} else {
			this.visibleGroupName = "none";
		}
	}

	public String getVisibleGroupName() {
		return this.visibleGroupName;
	}

	public void clear() {
		this.markerList.clear();
		this.groupList.clear();
		this.visibleMarkerList.clear();
		this.visibleGroupName = "none";
		selectedMarker = null;
		markerListServer.clear();
	}

	public String markerToString(Marker marker) {
		/*if(marker.iconLocation != null && !marker.iconLocation.isEmpty() && !marker.iconLocation.equals("")){
			marker.iconLocation = "@";
		}
		return String.format("%s:%d:%d:%d:%d:%06x:%s:t:%s:%s",
				marker.name,
				marker.x, marker.y, marker.z,
				marker.dimension,
				marker.beamColour & 0xffffff,
				marker.groupName,
				marker.iconLocation.replace(":", "|"),
				marker.reloadable ? "t" : "f"
				);*/
		NBTTagCompound tag = new NBTTagCompound();
		if (marker != null)
			marker.writeToNBT(tag);
		if (tag.getBoolean("null"))
			return null;
		else
			return tag.toString();
		/*else return String.format("%s:%d:%d:%d:%d:%06x:%s:t:%s:%s",
			marker.name,
			marker.x, marker.y, marker.z,
			marker.dimension,
			marker.beamColour & 0xffffff,
			marker.groupName,
			"tm:minimap/marker.png".replace(":", "|"),
			marker.reloadable ? "t" : "f"
		);*/
	}

	public Marker stringToMarker(String s) {
		// new style delimited with colons
		/*String[] split = s.split(":");
		if (split.length < 7) {
			// old style was space delimited
			split = s.split(" ");
		}*/
		// Marker marker = null;
		/*if (split.length > 6) {
			try {
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);
				int z = Integer.parseInt(split[3]);
				int dimension = Integer.parseInt(split[4]);
				int colour = 0xff000000 | Integer.parseInt(split[5], 16);
				boolean isIcon = split.length > 7 ? split[7].equals("t") : false;
				if(isIcon && split.length > 7){
					String icon = split[8].replace("|", ":");
					if(icon.equals("@"))icon = "";
					boolean reloadable = split.length > 9 ? split[9].equals("t") : true;
					if(reloadable){
						RenderType beamType = split.length > 10 ? RenderType.fromString(split[10]) : RenderType.NORMAL;
						RenderType labelType = split.length > 11 ? RenderType.fromString(split[11]) : RenderType.NORMAL;
						marker = new Marker(split[0], split[6], x, y, z, dimension, icon, colour,beamType,labelType,true);
					}
				}else if(split.length > 7){
					boolean reloadable = split[8].equals("t");
					if(reloadable) marker = new Marker(split[0], split[6], x, y, z, dimension, "", colour, RenderType.NORMAL, RenderType.NORMAL,true);
				}else marker = new Marker(split[0], split[6], x, y, z, dimension,"", colour, RenderType.NORMAL, RenderType.NORMAL,true);
		
			} catch (NumberFormatException e) {
				marker = null;
			}
		} else {
			Logging.log("Marker.stringToMarker: invalid marker '%s'", s);
		}*/
		NBTTagCompound tag;
		try {
			tag = JsonToNBT.getTagFromJson(s);
			return Marker.fromNBT(tag);
		} catch (Exception e) {
			Logging.logWarning("Marker.fromString: invalid marker '%s'. Error: " + e.getMessage(), s != null ? s : "~~NULL~~");
		}

		return null;
	}

	public void addMarker(Marker marker) {
		this.markerList.add(marker);
		this.save(getConfig(), Reference.catMarkers);
	}

	/*public void addMarker(String name, String groupName, int x, int y, int z, int dimension, int colour)
	{
		this.addMarker(new Marker(name, groupName, x, y, z, dimension,"tm:minimap/marker", colour, null, null, true));
	}
	public void addTexturedMarker(String name, String groupName, int x, int y, int z, int dimension, String icon, int color) {
		name = name.replace(":", "");
		groupName = groupName.replace(":", "");
		this.addMarker(name, groupName, x, y, z, dimension, color);
	}
	public void addMarker(String name, String groupName, int x, int y, int z, int dimension, int colour, boolean b) {
		name = name.replace(":", "");
		groupName = groupName.replace(":", "");
		Marker m = new Marker(name, groupName, x, y, z, dimension,"tm:minimap/marker", colour);
		m.reloadable = b;
		this.addMarker(m);
	}
	public void addTexturedMarker(String name, String groupName, int x, int y, int z, int dimension, String icon, boolean b) {
		name = name.replace(":", "");
		groupName = groupName.replace(":", "");
		Marker m = new Marker(name, groupName, x, y, z, dimension, icon, 0x0000FF);
		m.reloadable = b;
		this.addMarker(m);
	}*/
	public void addMarker(String name, String groupName, int x, int y, int z, int dimension, String icon, int beamColor, RenderType beamType, RenderType labelType, boolean reloadable, String beamTexture) {
		if (beamType == null)
			beamType = RenderType.NORMAL;
		if (labelType == null)
			labelType = RenderType.NORMAL;
		this.addMarker(new Marker(name, groupName, x, y, z, dimension, icon, beamColor, beamType, labelType, beamTexture, reloadable));
	}

	// returns true if the marker exists in the arraylist.
	// safe to pass null.
	public boolean delMarker(Marker markerToDelete) {
		if (this.selectedMarker == markerToDelete) {
			this.selectedMarker = null;
		}
		boolean result = this.markerList.remove(markerToDelete);
		this.save(getConfig(), Reference.catMarkers);

		return result;
	}

	// deletes the first marker with matching name and group.
	// if null is passed as either name or group it means "any".
	public boolean delMarker(String name, String group) {
		Marker markerToDelete = null;
		for (int i = 0;i < markerList.size();i++) {
			Marker marker = markerList.get(i);
			if (((name == null) || marker.name.equals(name)) && ((group == null) || marker.groupName.equals(group))) {
				markerToDelete = marker;
				break;
			}
		}
		// will return false if a marker matching the criteria is not found
		// (i.e. if markerToDelete is null)
		return this.delMarker(markerToDelete);
	}

	public void update() {
		if (!isClient) {
			MessageMarkerSync.sendSyncMessage();
		} else {
			this.visibleMarkerList.clear();
			this.groupList.clear();
			this.groupList.add("none");
			this.groupList.add("all");
			for (int i = 0;i < markerList.size();i++) {
				Marker marker = markerList.get(i);
				marker.isServerSided = false;
				if (marker.groupName.equals(this.visibleGroupName) || this.visibleGroupName.equals("all")) {
					this.visibleMarkerList.add(marker);
				}
				if (!this.groupList.contains(marker.groupName)) {
					this.groupList.add(marker.groupName);
				}
			}
			Collection<Marker> markerCollection = markerListServer.values();
			Marker[] markerList = markerCollection.toArray(new Marker[]{});
			for (int i = 0;i < markerList.length;i++) {
				markerList[i].isServerSided = true;
				if (markerList[i].groupName.equals(this.visibleGroupName) || this.visibleGroupName.equals("all")) {
					this.visibleMarkerList.add(markerList[i]);
				}
				if (!this.groupList.contains(markerList[i].groupName)) {
					this.groupList.add(markerList[i].groupName);
				}
			}
			if (!this.groupList.contains(this.visibleGroupName)) {
				this.visibleGroupName = "none";
			}
		}
	}

	public void nextGroup(int n) {
		if (this.groupList.size() > 0) {
			int i = this.groupList.indexOf(this.visibleGroupName);
			int size = this.groupList.size();
			if (i != -1) {
				i = (i + size + n) % size;
			} else {
				i = 0;
			}
			this.visibleGroupName = this.groupList.get(i);
		} else {
			this.visibleGroupName = "none";
			this.groupList.add("none");
		}
	}

	public void nextGroup() {
		this.nextGroup(1);
	}

	public int countMarkersInGroup(String group) {
		int count = 0;
		if (group.equals("all")) {
			count = this.markerList.size();
		} else {
			for (int i = 0;i < visibleMarkerList.size();i++) {
				Marker marker = visibleMarkerList.get(i);
				if (marker.groupName.equals(group)) {
					count++;
				}
			}
		}
		return count;
	}

	public void selectNextMarker() {
		if (this.visibleMarkerList.size() > 0) {
			int i = 0;
			if (this.selectedMarker != null) {
				i = this.visibleMarkerList.indexOf(this.selectedMarker);
				if (i == -1) {
					i = 0;
				}
			}
			i = (i + 1) % this.visibleMarkerList.size();
			this.selectedMarker = this.visibleMarkerList.get(i);
		} else {
			this.selectedMarker = null;
		}
	}

	public Marker getNearestMarker(int x, int z, int maxDistance) {
		int nearestDistance = maxDistance * maxDistance;
		Marker nearestMarker = null;
		for (int i = 0;i < visibleMarkerList.size();i++) {
			Marker marker = visibleMarkerList.get(i);
			int dx = x - marker.x;
			int dz = z - marker.z;
			int d = (dx * dx) + (dz * dz);
			if (d < nearestDistance) {
				nearestMarker = marker;
				nearestDistance = d;
			}
		}
		return nearestMarker;
	}

	public Marker getNearestMarkerInDirection(int x, int z, double desiredAngle) {
		int nearestDistance = 10000 * 10000;
		Marker nearestMarker = null;
		for (int i = 0;i < visibleMarkerList.size();i++) {
			Marker marker = visibleMarkerList.get(i);
			int dx = marker.x - x;
			int dz = marker.z - z;
			int d = (dx * dx) + (dz * dz);
			double angle = Math.atan2(dz, dx);
			// use cos instead of abs as it will wrap at 2 * Pi.
			// cos will be closer to 1.0 the closer desiredAngle and angle are.
			// 0.8 is the threshold corresponding to a maximum of
			// acos(0.8) = 37 degrees difference between the two angles.
			if ((Math.cos(desiredAngle - angle) > 0.8D) && (d < nearestDistance) && (d > 4)) {
				nearestMarker = marker;
				nearestDistance = d;
			}
		}
		return nearestMarker;
	}

	@SideOnly(Side.CLIENT)
	public void drawMarkers(MapMode mapMode, MapView mapView) {
		for (int i = 0;i < visibleMarkerList.size();i++) {
			Marker marker = visibleMarkerList.get(i);
			// only draw markers that were set in the current dimension
			if (mapView.getDimension() == marker.dimension) {
				marker.draw(mapMode, mapView, false);
			}
		}
		Mw.getInstance().hasSelected = this.selectedMarker != null;
		if (this.selectedMarker != null) {
			this.selectedMarker.draw(mapMode, mapView, true);
		}
	}

	@SideOnly(Side.CLIENT)
	public void drawMarkersWorld(float partialTicks) {
		if (Minecraft.getMinecraft().getRenderManager().renderViewEntity == null)
			return;
		if (!Config.drawMarkersInWorld && !Config.drawMarkersNameInWorld) { return; }
		for (int i = 0;i < visibleMarkerList.size();i++) {
			Marker m = visibleMarkerList.get(i);
			if (m.dimension == Minecraft.getMinecraft().player.dimension) {
				if (Config.drawMarkersInWorld) {
					if (m.beamType != RenderType.NONE)
						drawBeam(m, partialTicks);
				}
				if (Config.drawMarkersNameInWorld) {
					if (m.labelType != RenderType.NONE)
						drawLabel(m);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void drawBeam(Marker m, float partialTicks) {
		if (m.beamType == RenderType.NORMAL) {
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer worldrenderer = tessellator.getBuffer();

			float f2 = Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks;
			double d3 = f2 * 0.025D * -1.5D;
			// the height of the beam always to the max height
			double d17 = 255.0D;

			double x = m.x - TileEntityRendererDispatcher.staticPlayerX;
			double y = 0.0D - TileEntityRendererDispatcher.staticPlayerY;
			double z = m.z - TileEntityRendererDispatcher.staticPlayerZ;

			GlStateManager.pushMatrix();
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.depthMask(false);

			worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			// size of the square from middle to edge
			double d4 = 0.2D;

			double d5 = 0.5D + (Math.cos(d3 + 2.356194490192345D) * d4);
			double d6 = 0.5D + (Math.sin(d3 + 2.356194490192345D) * d4);
			double d7 = 0.5D + (Math.cos(d3 + (Math.PI / 4D)) * d4);
			double d8 = 0.5D + (Math.sin(d3 + (Math.PI / 4D)) * d4);
			double d9 = 0.5D + (Math.cos(d3 + 3.9269908169872414D) * d4);
			double d10 = 0.5D + (Math.sin(d3 + 3.9269908169872414D) * d4);
			double d11 = 0.5D + (Math.cos(d3 + 5.497787143782138D) * d4);
			double d12 = 0.5D + (Math.sin(d3 + 5.497787143782138D) * d4);

			float fRed = m.getRed();
			float fGreen = m.getGreen();
			float fBlue = m.getBlue();
			float fAlpha = 0.125f;

			worldrenderer.pos(x + d5, y + d17, z + d6).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d5, y, z + d6).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d7, y, z + d8).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d7, y + d17, z + d8).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d11, y + d17, z + d12).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d11, y, z + d12).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d9, y, z + d10).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d9, y + d17, z + d10).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d7, y + d17, z + d8).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d7, y, z + d8).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d11, y, z + d12).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d11, y + d17, z + d12).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d9, y + d17, z + d10).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d9, y, z + d10).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d5, y, z + d6).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d5, y + d17, z + d6).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			tessellator.draw();

			worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			// size of the square from middle to edge
			d4 = 0.5D;

			d5 = 0.5D + (Math.sin(d3 + 2.356194490192345D) * d4);
			d6 = 0.5D + (Math.cos(d3 + 2.356194490192345D) * d4);
			d7 = 0.5D + (Math.sin(d3 + (Math.PI / 4D)) * d4);
			d8 = 0.5D + (Math.cos(d3 + (Math.PI / 4D)) * d4);
			d9 = 0.5D + (Math.sin(d3 + 3.9269908169872414D) * d4);
			d10 = 0.5D + (Math.cos(d3 + 3.9269908169872414D) * d4);
			d11 = 0.5D + (Math.sin(d3 + 5.497787143782138D) * d4);
			d12 = 0.5D + (Math.cos(d3 + 5.497787143782138D) * d4);

			worldrenderer.pos(x + d5, y + d17, z + d6).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d5, y, z + d6).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d7, y, z + d8).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d7, y + d17, z + d8).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d11, y + d17, z + d12).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d11, y, z + d12).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d9, y, z + d10).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d9, y + d17, z + d10).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d7, y + d17, z + d8).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d7, y, z + d8).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d11, y, z + d12).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d11, y + d17, z + d12).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d9, y + d17, z + d10).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d9, y, z + d10).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d5, y, z + d6).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(x + d5, y + d17, z + d6).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			tessellator.draw();

			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		} else if (m.beamIconLocation != null && !m.beamIconLocation.isEmpty() && !m.beamIconLocation.equals("") && !m.beamIconLocation.equals("normal")) {
			float growFactor = 0;
			Minecraft mc = Minecraft.getMinecraft();
			RenderManager renderManager = mc.getRenderManager();

			double x = (0.5D + m.x) - TileEntityRendererDispatcher.staticPlayerX;
			double y = (0.5D + m.y) - TileEntityRendererDispatcher.staticPlayerY;
			double z = (0.5D + m.z) - TileEntityRendererDispatcher.staticPlayerZ;

			double distance = m.getDistanceToMarker(renderManager.renderViewEntity);

			int strTextWidth = 32;

			float f = (float) (1.0F + ((distance) * growFactor));
			float f1 = 0.016666668F * f;

			GlStateManager.pushMatrix();
			double max = Math.max(m.y, TileEntityRendererDispatcher.staticPlayerY);
			double posD = TileEntityRendererDispatcher.staticPlayerY - m.y;
			boolean tooSmall = posD < 8;
			double yPosEnd = tooSmall ? y - 0.5 : m.y - Math.min(max, m.y + 64) - 4;
			double yTPos = tooSmall ? y - 0.5 : yPosEnd + 4;
			if (tooSmall)
				yPosEnd = -8;
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer worldrenderer = tessellator.getBuffer();
			GlStateManager.enableTexture2D();
			ResourceLocation t = new ResourceLocation(m.beamIconLocation + ".png");
			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.depthMask(false);
			GlStateManager.disableLighting();
			GL11.glEnable(GL_DEPTH_CLAMP);
			float alpha = 1;
			GlStateManager.color(1, 1, 1, alpha);
			Mw.getInstance().mc.renderEngine.bindTexture(t);
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, yTPos, z);
				GL11.glNormal3f(0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				// GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F,
				// 0.0F);
				GlStateManager.scale(-f1, -f1, f1);
				worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				double topPos = (yPosEnd / f1) - 32;
				worldrenderer.pos(strTextWidth + 1, (topPos + 32), 0.0D).tex(topTexPos, topTexPos).endVertex();
				worldrenderer.pos(strTextWidth + 1, (topPos - 32), 0.0D).tex(topTexPos, 0).endVertex();
				worldrenderer.pos(-strTextWidth - 1, (topPos - 32), 0.0D).tex(0, 0).endVertex();
				worldrenderer.pos(-strTextWidth - 1, (topPos + 32), 0.0D).tex(0, topTexPos).endVertex();
				tessellator.draw();
				worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				worldrenderer.pos(-strTextWidth - 1, (-2 / f1), 0.0D).tex(topTexPos, bottomTexPos).endVertex();
				worldrenderer.pos(-strTextWidth - 1, (yPosEnd / f1), 0.0D).tex(topTexPos, topTexPos).endVertex();
				worldrenderer.pos(strTextWidth + 1, (yPosEnd / f1), 0.0D).tex(0, topTexPos).endVertex();
				worldrenderer.pos(strTextWidth + 1, (-2 / f1), 0.0D).tex(0, bottomTexPos).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y + 1.5, z);
				GL11.glNormal3f(0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				// GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F,
				// 0.0F);
				GlStateManager.scale(-f1, -f1, f1);
				worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				worldrenderer.pos(-strTextWidth - 1, (64), 0.0D).tex(topTexPos, 1).endVertex();
				worldrenderer.pos(-strTextWidth - 1, (0), 0.0D).tex(topTexPos, bottomTexPos).endVertex();
				worldrenderer.pos(strTextWidth + 1, (0), 0.0D).tex(0, bottomTexPos).endVertex();
				worldrenderer.pos(strTextWidth + 1, (64), 0.0D).tex(0, 1).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}
			GlStateManager.depthMask(true);
			GL11.glDisable(GL_DEPTH_CLAMP);
			GlStateManager.enableDepth();
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	public static void drawLabel(Marker m) {
		float growFactor = m.labelType != RenderType.ICON ? 0.17F : 0;
		Minecraft mc = Minecraft.getMinecraft();
		RenderManager renderManager = mc.getRenderManager();
		FontRenderer fontrenderer = mc.fontRenderer;
		double x = (0.5D + m.x) - TileEntityRendererDispatcher.staticPlayerX;
		double y = (0.5D + m.y) - TileEntityRendererDispatcher.staticPlayerY;
		double z = (0.5D + m.z) - TileEntityRendererDispatcher.staticPlayerZ;

		float fRed = m.getRed();
		float fGreen = m.getGreen();
		float fBlue = m.getBlue();
		float fAlpha = 0.2f;

		double distance = m.getDistanceToMarker(renderManager.renderViewEntity);

		String strText = m.name;
		String strDistance = " (" + (int) distance + "m)";

		int strTextWidth = m.labelType == RenderType.NORMAL ? fontrenderer.getStringWidth(strText) / 2 : 32;
		int strDistanceWidth = m.labelType == RenderType.NORMAL ? fontrenderer.getStringWidth(strDistance) / 2 : 0;
		int offstet = 9;
		float f;
		/*if(m.labelType != RenderType.ICON){
			f = (float) (1.0F + ((distance) * growFactor));
		}else{
		
		}*/
		f = (float) (1.0F + ((distance) * growFactor));
		float f1 = 0.016666668F * f;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		if (m.labelType != RenderType.ICON)
			GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-f1, -f1, f1);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		if (m.labelType != RenderType.ICON)
			GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GL11.glEnable(GL_DEPTH_CLAMP);

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer worldrenderer = tessellator.getBuffer();

		GlStateManager.disableTexture2D();
		if (m.labelType == RenderType.NORMAL) {
			worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			worldrenderer.pos(-strTextWidth - 1, (-1), 0.0D).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(-strTextWidth - 1, (8), 0.0D).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(strTextWidth + 1, (8), 0.0D).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(strTextWidth + 1, (-1), 0.0D).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			tessellator.draw();
		} else {
			GlStateManager.enableTexture2D();
			if (m.iconLocation != null && !m.iconLocation.isEmpty() && !m.iconLocation.equals("") && !m.iconLocation.equals("normal")) {
				ResourceLocation t = new ResourceLocation(m.iconLocation + ".png");
				Mw.getInstance().mc.renderEngine.bindTexture(t);
				worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				worldrenderer.pos(-strTextWidth - 1, (-1), 0.0D).tex(1, 1).endVertex();
				worldrenderer.pos(-strTextWidth - 1, (32), 0.0D).tex(1, 0).endVertex();
				worldrenderer.pos(+strTextWidth + 1, (32), 0.0D).tex(0, 0).endVertex();
				worldrenderer.pos(+strTextWidth + 1, (-1), 0.0D).tex(0, 1).endVertex();
				tessellator.draw();
			} else {
				/*ResourceLocation t = new ResourceLocation("tm:minimap/marker.png");
				Mw.getInstance().mc.renderEngine.bindTexture(t);*/
				Marker.drawMarkerTexture(-16 - 1, -1, 32, 32, m.color, 0.0D);
			}
			GlStateManager.disableTexture2D();
		}

		if (m.labelType == RenderType.NORMAL) {
			worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			worldrenderer.pos(-strDistanceWidth - 1, -1 + offstet, 0.0D).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(-strDistanceWidth - 1, 8 + offstet, 0.0D).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(strDistanceWidth + 1, 8 + offstet, 0.0D).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			worldrenderer.pos(strDistanceWidth + 1, -1 + offstet, 0.0D).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			tessellator.draw();
		}
		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(true);
		if (m.labelType == RenderType.NORMAL) {
			fontrenderer.drawString(strText, -strTextWidth, 0, -1);
			fontrenderer.drawString(strDistance, -strDistanceWidth, offstet, -1);
		}

		GL11.glDisable(GL_DEPTH_CLAMP);
		GlStateManager.enableDepth();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	private Configuration getConfig() {
		return isClient ? WorldConfig.getInstance().worldConfiguration : Minimap.serverConfig;
	}
}
