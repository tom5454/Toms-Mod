package com.tom.proxy;

import java.util.ArrayList;
import java.util.function.Function;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import com.tom.api.block.IMethod;
import com.tom.api.block.IMethod.IClientMethod;
import com.tom.api.client.MultiblockRenderer;
import com.tom.api.tileentity.TileEntityCamoable;
import com.tom.client.CustomModelLoader;
import com.tom.client.EventHandlerClient;
import com.tom.client.TileEntityCamoableSpecialRenderer;
import com.tom.client.TileEntityCustomRenderer;
import com.tom.client.TileEntityHiddenSpecialRenderer;
import com.tom.client.TileEntityMultiblockCustomRenderer;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.commands.CommandJEI;
import com.tom.core.commands.CommandProfiler;
import com.tom.core.model.ModelControllerBox;
import com.tom.core.model.ModelMagReader;
import com.tom.core.model.ModelMonitor;
import com.tom.core.model.TileEntityTemplateSpecialRenderer;
import com.tom.defense.client.TileEntityForceCapacitorRenderer;
import com.tom.defense.tileentity.TileEntityForceCapacitor;
import com.tom.factory.FactoryInit;
import com.tom.factory.client.ModelPlasticProcessorRotor;
import com.tom.factory.tileentity.TileEntityPlasticProcessor;
import com.tom.handler.KeyInputHandler;
import com.tom.lib.Configs;
import com.tom.lib.GlobalFields;
import com.tom.lib.Keys;
import com.tom.model.IBaseModel;
import com.tom.storage.client.DriveCellsModel;
import com.tom.storage.client.ModelControllerScreen;
import com.tom.storage.client.TileEntityLimitableChestRenderer;
import com.tom.storage.client.TileEntityTankSpecialRenderer;
import com.tom.storage.tileentity.TileEntityAdvTank;
import com.tom.storage.tileentity.TileEntityBasicTank;
import com.tom.storage.tileentity.TileEntityDrive;
import com.tom.storage.tileentity.TileEntityEliteTank;
import com.tom.storage.tileentity.TileEntityLimitableChest;
import com.tom.storage.tileentity.TileEntityStorageNetworkController;
import com.tom.storage.tileentity.TileEntityUltimateTank;
import com.tom.transport.model.FluidDuctSpecialRenderer;
import com.tom.transport.model.TileEntityConveyorOmniRenderer;
import com.tom.transport.model.TileEntityConveyorRenderer;
import com.tom.transport.model.TileEntityConveyorSlopeRenderer;
import com.tom.transport.multipart.PartFluidDuct;
import com.tom.transport.tileentity.TileEntityConveyorBeltSlope;
import com.tom.transport.tileentity.TileEntityConveyorFast;
import com.tom.transport.tileentity.TileEntityConveyorOmniFast;
import com.tom.transport.tileentity.TileEntityConveyorOmniSlow;
import com.tom.transport.tileentity.TileEntityConveyorSlow;

import com.tom.core.tileentity.TileEntityControllerBox;
import com.tom.core.tileentity.TileEntityEnderSensor;
import com.tom.core.tileentity.TileEntityHiddenSR;
import com.tom.core.tileentity.TileEntityMagCardReader;
import com.tom.core.tileentity.TileEntityMonitor;
import com.tom.core.tileentity.TileEntityRSDoor;
import com.tom.core.tileentity.TileEntityTemplate;

public class ClientProxy extends CommonProxy {
	// private final List<CustomModelRenderer> blockRenderers = new
	// ArrayList<CustomModelRenderer>();
	// public int SPECIAL_RENDER_TYPE_VALUE;
	@Override
	public void preInit() {
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
		GlobalFields.tabletSounds = new ArrayList<>();
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
		ClientCommandHandler.instance.registerCommand(new CommandProfiler());
		if (CoreInit.isDebugging)
			ClientCommandHandler.instance.registerCommand(new CommandJEI());
	}

	@Override
	public void postInit() {

	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
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
	public void registerRenders() {
		// SPECIAL_RENDER_TYPE_VALUE =
		// RenderingRegistry.getNextAvailableRenderId();
		// blockRenderers.add(new MonitorRenderer());
		/*for(CustomModelRenderer renderer : blockRenderers) {
		    RenderingRegistry.registerBlockHandler(renderer);
		}*/
		// RenderingRegistry.registerBlockHandler(new RenderModelBase());
		/*registerBaseModelRenderer(CoreInit.AntBase, TileEntityAntBase.class, new AntBase());
		registerBaseModelRenderer(CoreInit.AntMid, TileEntityAntMid.class, new AntMid());
		registerBaseModelRenderer(CoreInit.AntTop, TileEntityAntTop.class, new AntTop());*/
		if (CoreInit.isCCLoaded) {
			registerBaseModelRenderer(TileEntityMagCardReader.class, new ModelMagReader());
			if (Config.enableAdventureItems) {
				registerBaseModelRenderer(TileEntityControllerBox.class, new ModelControllerBox());
				// registerBaseModelRenderer(CoreInit.Camera,
				// TileEntityCamera.class, new ModelCamera());
			}
			registerBaseModelRenderer(TileEntityMonitor.class, new ModelMonitor());
		}
		// MultipartRegistryClient.bindMultipartSpecialRenderer(PartItemDuct.class,
		// new ItemDuctCustomRenderer());
		/*ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyCellMK1.class, new TileEntityEnergyCellRenderBase());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyCellMK2.class, new TileEntityEnergyCellRenderBase());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyCellMK3.class, new TileEntityEnergyCellRenderBase());*/
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserMK1.class, new TileEntityLaserRenderer());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserMK2.class, new TileEntityLaserRenderer());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserMK3.class, new TileEntityLaserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLimitableChest.class, new TileEntityLimitableChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityForceCapacitor.class, new TileEntityForceCapacitorRenderer());
		registerBaseModelRenderer(TileEntityDrive.class, new DriveCellsModel());
		// ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCreativeCell.class,
		// new TileEntityCreativeCellRenderer());
		registerCamoableRenderer(TileEntityRSDoor.class);
		registerCamoableRenderer(TileEntityEnderSensor.class);
		ClientRegistry.bindTileEntitySpecialRenderer(PartFluidDuct.class, new FluidDuctSpecialRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBasicTank.class, new TileEntityTankSpecialRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvTank.class, new TileEntityTankSpecialRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEliteTank.class, new TileEntityTankSpecialRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUltimateTank.class, new TileEntityTankSpecialRenderer());
		// ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransformerLaser.class,
		// new TileEntityLaserRenderer());
		registerBaseModelRenderer(TileEntityStorageNetworkController.class, new ModelControllerScreen());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHiddenSR.class, new TileEntityHiddenSpecialRenderer());
		registerBaseModelRenderer(TileEntityPlasticProcessor.class, FactoryInit.plasticProcessor, 0, new ModelPlasticProcessorRotor());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorSlow.class, new TileEntityConveyorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorFast.class, new TileEntityConveyorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorOmniSlow.class, new TileEntityConveyorOmniRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorOmniFast.class, new TileEntityConveyorOmniRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorBeltSlope.class, new TileEntityConveyorSlopeRenderer());
		MultiblockRenderer.registerCustomModel(new ResourceLocation("tmobj:block/multiblock.obj"));
		MultiblockRenderer.registerCustomModel(new ResourceLocation("tmobj:block/multiblockadv.obj"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTemplate.class, new TileEntityTemplateSpecialRenderer());
		// RenderingRegistry.registerEntityRenderingHandler(EntityCamera.class,
		// new RenderEntityCamera(new ModelEntityCamera(), 0.5F));
	}

	public static <T extends TileEntity> void registerBaseModelRenderer(Class<T> tileEntityClass, IBaseModel model) {
		/*if(model instanceof BaseModel) {
		    ((BaseModel)model).rotatable = ((IRotatable)block).isRotatable();
		}*/
		// registerBaseModelRenderer(Item.getItemFromBlock(block),
		// tileEntityClass, model);
		ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, new TileEntityCustomRenderer<T>(model));
	}

	public static <T extends TileEntity> void registerBaseModelRenderer(Class<T> tileEntityClass, Block block, int id, IBaseModel model) {
		/*if(model instanceof BaseModel) {
		    ((BaseModel)model).rotatable = ((IRotatable)block).isRotatable();
		}*/
		// registerBaseModelRenderer(Item.getItemFromBlock(block),
		// tileEntityClass, model);
		// ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, new
		// TileEntityCustomRenderer<T>(model));
		MultiblockRenderer.<T>registerTESR(block, id, (MultiblockRenderer<T>) new TileEntityMultiblockCustomRenderer<T>(model));
	}

	public static <T extends TileEntityCamoable> void registerCamoableRenderer(Class<T> tileEntityClass) {
		ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, new TileEntityCamoableSpecialRenderer());
	}

	/*private static void registerBaseModelRenderer(Item item, Class<? extends TileEntity> tileEntityClass, IBaseModel model){
	    //RenderModelBase renderer = new RenderModelBase(model);
	    ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, new TileEntityCustomRenderer<TileEntity>(model));
	    //MinecraftForgeClient.registerItemRenderer(item, renderer);
	}*/
	@Override
	public void registerKeyBindings() {
		if (Config.enableAdventureItems) {
			ClientRegistry.registerKeyBinding(Keys.UP = new KeyBinding(Configs.keyPrefix + "up", Keyboard.KEY_UP, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.DOWN = new KeyBinding(Configs.keyPrefix + "down", Keyboard.KEY_DOWN, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.LEFT = new KeyBinding(Configs.keyPrefix + "left", Keyboard.KEY_LEFT, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.RIGHT = new KeyBinding(Configs.keyPrefix + "right", Keyboard.KEY_RIGHT, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.ENTER = new KeyBinding(Configs.keyPrefix + "enter", 28, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.BACK = new KeyBinding(Configs.keyPrefix + "back", 14, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.INTERACT = new KeyBinding(Configs.keyPrefix + "interact", Keyboard.KEY_X, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.MENU = new KeyBinding(Configs.keyPrefix + "menu", Keyboard.KEY_Y, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.FUNCTION1 = new KeyBinding(Configs.keyPrefix + "func1", Keyboard.KEY_V, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.FUNCTION2 = new KeyBinding(Configs.keyPrefix + "func2", Keyboard.KEY_B, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.FUNCTION3 = new KeyBinding(Configs.keyPrefix + "func3", Keyboard.KEY_G, Configs.keyCatergory));
			ClientRegistry.registerKeyBinding(Keys.FUNCTION4 = new KeyBinding(Configs.keyPrefix + "func4", Keyboard.KEY_H, Configs.keyCatergory));
		}
		ClientRegistry.registerKeyBinding(Keys.CONFIG = new KeyBinding(Configs.keyPrefix + "config", Keyboard.KEY_C, Configs.keyCatergory));
		ClientRegistry.registerKeyBinding(Keys.PROFILE = new KeyBinding(Configs.keyPrefix + "profile", Keyboard.KEY_NUMPAD9, Configs.keyCatergory));
		ClientRegistry.registerKeyBinding(Keys.SHOW_TETXURE_MAP = new KeyBinding(Configs.keyPrefix + "showtexturemap", Keyboard.KEY_NUMPAD8, Configs.keyCatergory));
	}

	@Override
	public void registerItemRender(Item item, int meta, String name) {
		CoreInit.registerRender(item, meta, name);
	}

	@Override
	public void serverStart() {
		CustomModelLoader.printExceptions();
	}

	@Override
	public void construction() {

	}

	@Override
	public void runMethod(IMethod m) {
		if (m instanceof IClientMethod) {
			m.exec();
		}
	}

	@Override
	public <I, R> R runClientFunction(I i, Function<I, R> m) {
		return m.apply(i);
	}
	@Override
	public void delTexture(Integer id) {
		if(id != null)GL11.glDeleteTextures(id);
	}
	@Override
	public void delList(Integer id) {
		if(id != null)GL11.glDeleteLists(id, 1);
	}
	@Override
	public void error(String string) {
		throw new CustomModLoadingErrorDisplayException() {
			private static final long serialVersionUID = 5435172220591885091L;
			private double zLevel;
			@Override
			public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {

			}

			@Override
			public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
				drawGradientRect(0, 0, errorScreen.width, errorScreen.height, -12574688, -11530224);
				errorScreen.drawCenteredString(fontRenderer, "", errorScreen.width / 2, 90, 16777215);
				errorScreen.drawCenteredString(fontRenderer, string, errorScreen.width / 2, 110, 16777215);
			}
			/**
			 * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
			 * topColor, bottomColor
			 */
			protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
			{
				float f = (startColor >> 24 & 255) / 255.0F;
				float f1 = (startColor >> 16 & 255) / 255.0F;
				float f2 = (startColor >> 8 & 255) / 255.0F;
				float f3 = (startColor & 255) / 255.0F;
				float f4 = (endColor >> 24 & 255) / 255.0F;
				float f5 = (endColor >> 16 & 255) / 255.0F;
				float f6 = (endColor >> 8 & 255) / 255.0F;
				float f7 = (endColor & 255) / 255.0F;
				GlStateManager.disableTexture2D();
				GlStateManager.enableBlend();
				GlStateManager.disableAlpha();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.shadeModel(7425);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(right, top, this.zLevel).color(f1, f2, f3, f).endVertex();
				bufferbuilder.pos(left, top, this.zLevel).color(f1, f2, f3, f).endVertex();
				bufferbuilder.pos(left, bottom, this.zLevel).color(f5, f6, f7, f4).endVertex();
				bufferbuilder.pos(right, bottom, this.zLevel).color(f5, f6, f7, f4).endVertex();
				tessellator.draw();
				GlStateManager.shadeModel(7424);
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
				GlStateManager.enableTexture2D();
			}
		};
	}
}