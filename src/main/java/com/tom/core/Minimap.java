package com.tom.core;

import java.io.File;

import com.tom.apis.TMLogger;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageMinimap;

import mapwriterTm.Mw;
import mapwriterTm.map.Marker.RenderType;
import mapwriterTm.map.MarkerManager;
import mapwriterTm.util.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class Minimap {
	public static MarkerManager markerManagerServer;
	public static Configuration serverConfig;
	protected static void init(File file){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			CoreInit.log.info("Init Mapwriter Client");
			if(Mw.getInstance().mc.isSingleplayer()){
				CoreInit.log.info("Init Mapwriter Server");
				markerManagerServer = new MarkerManager(false);
				serverConfig = new Configuration(file);
				markerManagerServer.load(serverConfig, Reference.catMarkers);
				serverConfig.addCustomCategoryComment(Reference.catMarkers, "Server Sided Markers saved here.");
				serverConfig.save();
			}
		}else{
			CoreInit.log.info("Init Mapwriter Server");
			markerManagerServer = new MarkerManager(false);
			serverConfig = new Configuration(file);
			markerManagerServer.load(serverConfig, Reference.catMarkers);
			serverConfig.addCustomCategoryComment(Reference.catMarkers, "Server Sided Markers saved here.");
			serverConfig.save();
		}
	}
	protected static void close(){
		//save(WorldConfig.getInstance().worldConfiguration, Reference.catMarkers);
		if(markerManagerServer != null){
			CoreInit.log.info("Closing Mapwriter Server");
			markerManagerServer.save(serverConfig, Reference.catMarkers);
			markerManagerServer.clear();
			serverConfig.save();
		}
	}
	/*public static void createWayPoint(String group,int mx,int my,int mz,int dim,String markerName,int color){
		markerManager.addMarker(markerName, group, mx, my, mz, dim, color);
		markerManager.setVisibleGroupName("all");
		markerManager.update();
	}
	 */public static boolean deleteWayPoint(String group,String markerName){
		 boolean s = Mw.getInstance().markerManager.delMarker(markerName, group);
		 Mw.getInstance().markerManager.setVisibleGroupName("all");
		 Mw.getInstance().markerManager.update();
		 return s;
	 }
	 public static boolean deleteWayPointServer(String group,String markerName){
		 if(markerManagerServer == null){
			 TMLogger.bigWarn("Attempt to delete a waypoint on the server marker manager on the Client");
			 return false;
		 }
		 boolean s = markerManagerServer.delMarker(markerName, group);
		 markerManagerServer.setVisibleGroupName("all");
		 markerManagerServer.update();
		 return s;
	 }/*
	public static void createTexturedWayPoint(String group,int mx,int my,int mz,int dim,String markerName,String icon){
		markerManager.addTexturedMarker(markerName, group, mx, my, mz, dim, icon);
		markerManager.setVisibleGroupName("all");
		markerManager.update();
	}*/
	 /*public static void sendWaypointCreation(String group,int mx,int my,int mz,int dim,String markerName,String icon, EntityPlayerMP p){
		NetworkHandler.sendTo(new MessageMinimap(group,mx,my,mz,dim,markerName, icon), p);
	}*/
	 /*public static void sendWaypointCreation(String group,int mx,int my,int mz,int dim,String markerName,int color,EntityPlayerMP p){
		NetworkHandler.sendTo(new MessageMinimap(markerName, group,mx,my,mz,dim,color), p);
	}
	public static void sendWaypointCreation(String group,int mx,int my,int mz,int dim,String markerName,String icon, EntityPlayerMP p){
		NetworkHandler.sendTo(new MessageMinimap(markerName,group,mx,my,mz,dim,icon), p);
	}*/
	 /*public static void sendWaypointCreation(String group,int mx,int my,int mz,int dim,String markerName,int color,EntityPlayerMP p){
		NetworkHandler.sendTo(new MessageMinimap(markerName,group,mx,my,mz,dim,color), p);
	}
	public static void createTexturedWayPoint(String group,int mx,int my,int mz,int dim,String markerName,String icon, int border, int borderA, boolean borderedA, boolean bordered, boolean reloadable){
		Mw.instance.markerManager.addTexturedMarker(markerName, group, mx, my, mz, dim, icon, border, borderA, bordered,borderedA, reloadable);
		Mw.instance.markerManager.setVisibleGroupName("all");
		Mw.instance.markerManager.update();
	}
	public static void createWayPoint(String group,int mx,int my,int mz,int dim,String markerName,int color, int border, int borderA, boolean borderedA, boolean bordered, boolean reloadable){
		Mw.instance.markerManager.addMarker(markerName, group, mx, my, mz, dim, color, border, borderA,bordered,borderedA, reloadable);
		Mw.instance.markerManager.setVisibleGroupName("all");
		Mw.instance.markerManager.update();
	}
	public static void createTexturedWayPoint(String group,int mx,int my,int mz,int dim,String markerName,String icon, int border, int borderA, boolean borderedA, boolean bordered){
		Mw.instance.markerManager.addTexturedMarker(markerName, group, mx, my, mz, dim, icon, border, borderA, bordered,borderedA, true);
		Mw.instance.markerManager.setVisibleGroupName("all");
		Mw.instance.markerManager.update();
	}
	public static void createWayPoint(String group,int mx,int my,int mz,int dim,String markerName,int color, int border, int borderA, boolean borderedA, boolean bordered){
		Mw.instance.markerManager.addMarker(markerName, group, mx, my, mz, dim, color, border, borderA,bordered,borderedA, true);
		Mw.instance.markerManager.setVisibleGroupName("all");
		Mw.instance.markerManager.update();
	}
	public static void sendWaypointCreation(String group,int mx,int my,int mz,int dim,String markerName,String icon, int border, int borderA, boolean borderedA, boolean bordered,boolean reloadable, EntityPlayerMP p){
		NetworkHandler.sendTo(new MessageMinimap(markerName,group,mx,my,mz,dim,icon,border,borderA,bordered,borderedA), p);
	}
	public static void sendWaypointCreation(String group,int mx,int my,int mz,int dim,String markerName,int color, int border, int borderA, boolean bordered, boolean borderedA,boolean reloadable,EntityPlayerMP p){
		NetworkHandler.sendTo(new MessageMinimap(markerName,group,mx,my,mz,dim,color,border,borderA,bordered,borderedA), p);
	}
	public static void sendWaypointCreation(String group,int mx,int my,int mz,int dim,String markerName,String icon, int border, int borderA, boolean borderedA, boolean bordered, EntityPlayerMP p){
		sendWaypointCreation(group,mx,my,mz,dim,markerName,icon, border, borderA, bordered, borderedA,true,p);
	}
	public static void sendWaypointCreation(String group,int mx,int my,int mz,int dim,String markerName,int color, int border, int borderA, boolean bordered, boolean borderedA,EntityPlayerMP p){
		sendWaypointCreation(group,mx,my,mz,dim,markerName,color, border, borderA, bordered, borderedA,true,p);
	}*/
	 /*public static void createTexturedWayPoint(String group, int mx, int my, int mz, int dim, String markerName, String icon, boolean b) {
		markerManager.addTexturedMarker(markerName, group, mx, my, mz, dim, icon,b);
		markerManager.setVisibleGroupName("all");
		markerManager.update();
	}*/
	 public static void createTexturedWayPoint(String group,int mx,int my,int mz,int dim,String markerName,String icon, int beamColor, RenderType beamRenderType, RenderType labelRenderType, boolean reloadable, String beamTexture){
		 Mw.getInstance().markerManager.addMarker(markerName, group, mx, my, mz, dim, icon, beamColor, beamRenderType, labelRenderType, reloadable, beamTexture);
		 Mw.getInstance().markerManager.setVisibleGroupName("all");
		 Mw.getInstance().markerManager.update();
	 }
	 public static void createTexturedWayPointServer(String group,int mx,int my,int mz,int dim,String markerName,String icon, int beamColor, RenderType beamRenderType, RenderType labelRenderType, boolean reloadable, String beamTexture){
		 if(markerManagerServer == null){
			 TMLogger.bigWarn("Attempt to create a waypoint on the server marker manager on the Client");
			 return;
		 }
		 markerManagerServer.addMarker(markerName, group, mx, my, mz, dim, icon, beamColor, beamRenderType, labelRenderType, reloadable, beamTexture);
		 markerManagerServer.setVisibleGroupName("all");
		 markerManagerServer.update();
	 }
	 public static void sendWaypointCreation(String group,int mx,int my,int mz,int dim,String markerName,String icon, int beamColor, RenderType beamRenderType, RenderType labelRenderType, boolean reloadable, String beamTexture, EntityPlayerMP p){
		 NetworkHandler.sendTo(new MessageMinimap(group,mx,my,mz,dim,markerName,icon,beamColor,beamRenderType,labelRenderType,reloadable, beamTexture), p);
	 }
	 public static void sendWaypointRemove(String group,String markerName,EntityPlayerMP p){
		 NetworkHandler.sendTo(new MessageMinimap(group,markerName), p);
	 }
}
