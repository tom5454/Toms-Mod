package com.tom.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Maps;

import com.tom.apis.TMLogger;
import com.tom.core.CoreInit;
import com.tom.handler.EventHandler;
import com.tom.lib.GlobalFields;

import com.tom.core.tileentity.gui.GuiCamera;

@SideOnly(Side.CLIENT)
public class EventHandlerClient {
	protected static EventHandlerClient instance;
	private List<ResourceLocation> loadedLocations = new ArrayList<ResourceLocation>();
	public static EventHandlerClient getInstance() {
		return instance;
	}
	public EventHandlerClient() {
		instance = this;
	}
	@SideOnly(Side.CLIENT)
	public static final Map<ResourceLocation, IModel> models = Maps.newHashMap();
	@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent event)
	{

	}
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent.Post event)
	{

	}
	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event)
	{
		models.clear();
		/*{
			Object object =  event.getModelRegistry().getObject(EnderPlayerSensor.SmartBlockModel.modelResourceLocation);
			Object object2 =  event.getModelRegistry().getObject(EnderPlayerSensor.SmartBlockModel.modelTResourceLocation);
			if (object instanceof IBakedModel && object2 instanceof IBakedModel) {
				IBakedModel existingModel = (IBakedModel)object;
				IBakedModel existingModel2 = (IBakedModel)object2;
				EnderPlayerSensor.SmartBlockModel customModel = new EnderPlayerSensor.SmartBlockModel(existingModel,existingModel2);
				event.getModelRegistry().putObject(EnderPlayerSensor.SmartBlockModel.modelResourceLocation, customModel);
				//event.modelRegistry.putObject(EnderPlayerSensor.SmartBlockModel.modelTResourceLocation, customModel);
			}
		}
		{
			Object object =  event.getModelRegistry().getObject(BlockRsDoor.SmartBlockModel.modelResourceLocation);
			if (object instanceof IBakedModel) {
				IBakedModel existingModel = (IBakedModel)object;
				BlockRsDoor.SmartBlockModel customModel = new BlockRsDoor.SmartBlockModel(existingModel);
				event.getModelRegistry().putObject(BlockRsDoor.SmartBlockModel.modelResourceLocation, customModel);
				//event.modelRegistry.putObject(EnderPlayerSensor.SmartBlockModel.modelTResourceLocation, customModel);
			}
		}*/
	}
	@SubscribeEvent
	public void onRegisterTexture(TextureStitchEvent.Pre event){
		TMLogger.info("Adding fluid textures");
		loadedLocations.clear();
		TextureMap map = event.getMap();
		for(Entry<String, Fluid> entry : CoreInit.fluidList.entrySet()){
			//map.registerSprite(new ResourceLocation("tomsmodcore", "blocks/" + name + "_still"));
			//map.registerSprite(new ResourceLocation("tomsmodcore", "blocks/" + name + "_flow"));
			if(!loadedLocations.contains(entry.getValue().getFlowing())){
				map.registerSprite(entry.getValue().getFlowing());
				loadedLocations.add(entry.getValue().getFlowing());
			}
			if(!loadedLocations.contains(entry.getValue().getStill())){
				map.registerSprite(entry.getValue().getStill());
				loadedLocations.add(entry.getValue().getStill());
			}
		}
	}
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START)
		{
			EventHandler.teList.update(true);
			// run the cleanup code when Mw is loaded and the player becomes
			// null.
			// a bit hacky, but simpler than checking if the connection has
			// closed.
			if (Minecraft.getMinecraft().thePlayer == null)
			{
				GlobalFields.mobs.clear();
				GlobalFields.animals.clear();
				GlobalFields.other.clear();
				GlobalFields.tabletSounds.clear();
			}
		}
	}
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		if(Minecraft.getMinecraft().currentScreen instanceof GuiCamera){
			event.setCanceled(true);
		}
	}
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRenderOverlay(RenderGameOverlayEvent event) {
		if(event.getType() == ElementType.HELMET && Minecraft.getMinecraft().currentScreen instanceof GuiCamera){
			event.setCanceled(true);
		}
	}
}
