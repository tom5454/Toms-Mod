package com.tom.proxy;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import com.tom.api.tileentity.TileEntityCamoable;
import com.tom.client.CustomModelLoader;
import com.tom.client.EventHandlerClient;
import com.tom.client.TileEntityCamoableSpecialRenderer;
import com.tom.client.TileEntityCustomRenderer;
import com.tom.client.TileEntityEnergyCellRenderBase;
import com.tom.client.TileEntityLaserRenderer;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.model.ModelControllerBox;
import com.tom.core.model.ModelMagReader;
import com.tom.core.model.ModelMonitor;
import com.tom.defense.client.TileEntityForceCapacitorRenderer;
import com.tom.defense.tileentity.TileEntityForceCapacitor;
import com.tom.handler.KeyInputHandler;
import com.tom.lib.Configs;
import com.tom.lib.GlobalFields;
import com.tom.lib.Keys;
import com.tom.model.IBaseModel;
import com.tom.storage.client.DriveCellsModel;
import com.tom.storage.client.TileEntityLimitableChestRenderer;
import com.tom.storage.client.TileEntityTankSpecialRenderer;
import com.tom.storage.tileentity.TileEntityAdvTank;
import com.tom.storage.tileentity.TileEntityBasicTank;
import com.tom.storage.tileentity.TileEntityDrive;
import com.tom.storage.tileentity.TileEntityEliteTank;
import com.tom.storage.tileentity.TileEntityLimitableChest;
import com.tom.storage.tileentity.TileEntityUltimateTank;
import com.tom.transport.model.FluidDuctSpecialRenderer;
import com.tom.transport.model.TileEntityConveyorRenderer;
import com.tom.transport.model.TileEntityConveyorSlopeRenderer;
import com.tom.transport.multipart.PartFluidDuct;
import com.tom.transport.tileentity.TileEntityConveyor;
import com.tom.transport.tileentity.TileEntityConveyorSlope;

import com.tom.core.tileentity.TileEntityControllerBox;
import com.tom.core.tileentity.TileEntityEnderSensor;
import com.tom.core.tileentity.TileEntityMagCardReader;
import com.tom.core.tileentity.TileEntityMonitor;
import com.tom.core.tileentity.TileEntityRSDoor;

import com.tom.energy.tileentity.TileEntityCreativeCell;
import com.tom.energy.tileentity.TileEntityEnergyCellMK1;
import com.tom.energy.tileentity.TileEntityEnergyCellMK2;
import com.tom.energy.tileentity.TileEntityEnergyCellMK3;
import com.tom.energy.tileentity.TileEntityLaserMK1;
import com.tom.energy.tileentity.TileEntityLaserMK2;
import com.tom.energy.tileentity.TileEntityLaserMK3;
import com.tom.energy.tileentity.TileEntityTransformerLaser;

import mcmultipart.client.multipart.MultipartRegistryClient;

public class ClientProxy extends CommonProxy{
	//private final List<CustomModelRenderer> blockRenderers = new ArrayList<CustomModelRenderer>();
	//public int SPECIAL_RENDER_TYPE_VALUE;
	@Override
	public void preInit(){
		/*{
    		StateMapperBase ignoreState = new StateMapperBase() {
    			@Override
    			protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
    				return EnderPlayerSensor.SmartBlockModel.modelResourceLocation;
    			}
    		};
    		ModelLoader.setCustomStateMapper(CoreInit.EnderPlayerSensor, ignoreState);
    	}
    	{
    		StateMapperBase ignoreState = new StateMapperBase() {
    			@Override
    			protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
    				return BlockRsDoor.SmartBlockModel.modelResourceLocation;
    			}
    		};
    		ModelLoader.setCustomStateMapper(CoreInit.blockRsDoor, ignoreState);
    	}*/
		CustomModelLoader.init();
		GlobalFields.tabletSounds = new ArrayList<ISound>();
	}

	@Override
	public void init(){
		MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}

	@Override
	public void postInit(){

	}

	@Override
	public EntityPlayer getClientPlayer(){
		return Minecraft.getMinecraft().thePlayer;
	}
	/*@SuppressWarnings("rawtypes")
	@Override
    public int getRenderIdForRenderer(Class clazz){
        for(CustomModelRenderer renderer : blockRenderers) {
            if(renderer.getClass() == clazz) return renderer.getRenderId();
        }
        throw new IllegalArgumentException("Renderer " + clazz.getCanonicalName() + " isn't registered");
    }*/
	@Override
	public void registerRenders(){
		//SPECIAL_RENDER_TYPE_VALUE = RenderingRegistry.getNextAvailableRenderId();
		//blockRenderers.add(new MonitorRenderer());
		/*for(CustomModelRenderer renderer : blockRenderers) {
            RenderingRegistry.registerBlockHandler(renderer);
        }*/
		//RenderingRegistry.registerBlockHandler(new RenderModelBase());
		/*registerBaseModelRenderer(CoreInit.AntBase, TileEntityAntBase.class, new AntBase());
        registerBaseModelRenderer(CoreInit.AntMid, TileEntityAntMid.class, new AntMid());
        registerBaseModelRenderer(CoreInit.AntTop, TileEntityAntTop.class, new AntTop());*/
		if(CoreInit.isCCLoaded){
			registerBaseModelRenderer(TileEntityMagCardReader.class, new ModelMagReader());
			if(Config.enableAdventureItems){
				registerBaseModelRenderer(TileEntityControllerBox.class, new ModelControllerBox());
				//registerBaseModelRenderer(CoreInit.Camera, TileEntityCamera.class, new ModelCamera());
			}
			registerBaseModelRenderer(TileEntityMonitor.class, new ModelMonitor());
		}
		CoreInit.registerItemRenders();
		//MultipartRegistryClient.bindMultipartSpecialRenderer(PartItemDuct.class, new ItemDuctCustomRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyCellMK1.class, new TileEntityEnergyCellRenderBase());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyCellMK2.class, new TileEntityEnergyCellRenderBase());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyCellMK3.class, new TileEntityEnergyCellRenderBase());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserMK1.class, new TileEntityLaserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserMK2.class, new TileEntityLaserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserMK3.class, new TileEntityLaserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLimitableChest.class, new TileEntityLimitableChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyor.class, new TileEntityConveyorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorSlope.class, new TileEntityConveyorSlopeRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityForceCapacitor.class, new TileEntityForceCapacitorRenderer());
		registerBaseModelRenderer(TileEntityDrive.class, new DriveCellsModel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCreativeCell.class, new TileEntityEnergyCellRenderBase());
		registerCamoableRenderer(TileEntityRSDoor.class);
		registerCamoableRenderer(TileEntityEnderSensor.class);
		MultipartRegistryClient.bindMultipartSpecialRenderer(PartFluidDuct.class, new FluidDuctSpecialRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBasicTank.class, new TileEntityTankSpecialRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvTank.class, new TileEntityTankSpecialRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEliteTank.class, new TileEntityTankSpecialRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUltimateTank.class, new TileEntityTankSpecialRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransformerLaser.class, new TileEntityLaserRenderer());
		//RenderingRegistry.registerEntityRenderingHandler(EntityCamera.class, new RenderEntityCamera(new ModelEntityCamera(), 0.5F));
	}
	public static <T extends TileEntity> void registerBaseModelRenderer(Class<T> tileEntityClass, IBaseModel model){
		/*if(model instanceof BaseModel) {
            ((BaseModel)model).rotatable = ((IRotatable)block).isRotatable();
        }*/
		//registerBaseModelRenderer(Item.getItemFromBlock(block), tileEntityClass, model);
		ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, new TileEntityCustomRenderer<T>(model));
	}
	public static <T extends TileEntityCamoable> void registerCamoableRenderer(Class<T> tileEntityClass){
		ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, new TileEntityCamoableSpecialRenderer());
	}

	/*private static void registerBaseModelRenderer(Item item, Class<? extends TileEntity> tileEntityClass, IBaseModel model){
        //RenderModelBase renderer = new RenderModelBase(model);
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, new TileEntityCustomRenderer<TileEntity>(model));
        //MinecraftForgeClient.registerItemRenderer(item, renderer);
    }*/
	@Override
	public void registerKeyBindings(){
		if(CoreInit.isCCLoaded && Config.enableAdventureItems){
			ClientRegistry.registerKeyBinding(Keys.UP = new KeyBinding(Configs.keyPrefix + "up", Keyboard.KEY_UP, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.DOWN = new KeyBinding(Configs.keyPrefix + "down", Keyboard.KEY_DOWN, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.LEFT = new KeyBinding(Configs.keyPrefix + "left", Keyboard.KEY_LEFT, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.RIGHT = new KeyBinding(Configs.keyPrefix + "right", Keyboard.KEY_RIGHT, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.ENTER = new KeyBinding(Configs.keyPrefix + "enter", 28, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.BACK = new KeyBinding(Configs.keyPrefix + "back", 14, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.INTERACT = new KeyBinding(Configs.keyPrefix + "interact", Keyboard.KEY_X, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.MENU = new KeyBinding(Configs.keyPrefix + "menu", Keyboard.KEY_Y, Configs.keyCatergory));
		}
		ClientRegistry.registerKeyBinding(Keys.CONFIG = new KeyBinding(Configs.keyPrefix + "config", Keyboard.KEY_C, Configs.keyCatergory));
	}
	@Override
	public void registerItemRender(Item item, int meta, String name){
		CoreInit.registerRender(item, meta, name);
	}

	@Override
	public void serverStart() {
		CustomModelLoader.printExceptions();
	}

	@Override
	public void construction() {

	}
}