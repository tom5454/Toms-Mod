package com.tom.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Maps;

import com.tom.api.inventory.ITooltipSlot;
import com.tom.core.CoreInit;
import com.tom.handler.EventHandler;
import com.tom.lib.Configs;
import com.tom.lib.GlobalFields;
import com.tom.lib.utils.EmptyEntry;
import com.tom.lib.utils.RenderUtil;
import com.tom.network.messages.MessageProfiler;
import com.tom.util.TMLogger;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.gui.GuiCamera;

import scala.actors.threadpool.Arrays;

@SideOnly(Side.CLIENT)
public class EventHandlerClient {
	protected static EventHandlerClient instance;
	// private Map<Predicate<ItemStack>, Entry<String, String>> tooltipOverride
	// = new HashMap<>();
	private Map<Item, Entry<String, String>> tooltipOverrideFast = new HashMap<>();
	private static final List<String> TOOLTIP = TomsModUtils.getStringList("Tom's Mod TextureMap display", "1: Scale Width", "2: Scale Height", "3: Move Sprite Horizontally.", "4: Move Sprite Vertically", "5: Scale Width & Height", "6: Reset", "Hold Sneak to invert effect");
	private static Minecraft mc = Minecraft.getMinecraft();
	public static FontRenderer lcdFont;
	public static int textureIns = 0;
	public static int textMapW = 512, textMapH = 256, textMapX = 0, textMapY = 0;

	public static EventHandlerClient getInstance() {
		return instance;
	}

	public EventHandlerClient() {
		instance = this;
	}

	@SideOnly(Side.CLIENT)
	public static final Map<ResourceLocation, IModel> models = Maps.newHashMap();

	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.TEXT && showTextureMap) {
			GlStateManager.color(1, 1, 1);
			mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderUtil.drawTexturedRect(textMapX, textMapY, textMapW, textMapH);
			ScaledResolution res = event.getResolution();
			drawString(TOOLTIP, res, 0xFFFFFF);
		}
	}

	private void drawString(List<String> s, ScaledResolution res, int color) {
		int width = mc.fontRenderer.getStringWidth(s.get(0));
		for (int i = 1;i < s.size();i++) {
			width = Math.max(width, mc.fontRenderer.getStringWidth(s.get(i)));
		}
		for (int i = 0;i < s.size();i++) {
			String text = s.get(i);
			mc.fontRenderer.drawString(text, res.getScaledWidth() - width - 2, res.getScaledHeight() - (mc.fontRenderer.FONT_HEIGHT + 2) * (s.size() - i), color);
		}
	}

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
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
	public void onRegisterTexture(TextureStitchEvent.Pre event) {
		TMLogger.info("Adding fluid textures");
		lcdFont = new LCDFontRenderer(mc.gameSettings, Configs.lcdFont, mc.renderEngine, false);
		TextureMap map = event.getMap();
		for (Entry<String, Fluid> entry : CoreInit.fluidList.entrySet()) {
			map.registerSprite(entry.getValue().getFlowing());
			map.registerSprite(entry.getValue().getStill());
		}
		map.registerSprite(new ResourceLocation("tomsmod:gui/gearbox_on"));
		textureIns++;
	}


	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			if (Minecraft.getMinecraft().player == null) {
				GlobalFields.mobs.clear();
				GlobalFields.animals.clear();
				GlobalFields.other.clear();
				GlobalFields.tabletSounds.clear();
			}
		}
	}

	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiCamera) {
			event.setCanceled(true);
		}
	}

	int i;

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == ElementType.HELMET && Minecraft.getMinecraft().currentScreen instanceof GuiCamera) {
			event.setCanceled(true);
		} else if (event.getType() == ElementType.TEXT && profile) {
			displayDebugInfo();
		}
		if (i > 0)
			i--;
	}

	@SubscribeEvent
	public void key(KeyInputEvent e) {
		if (!(profile || showTextureMap) || i > 0)
			return;
		int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
		if (!Keyboard.isKeyDown(Keyboard.getEventKey()))
			return;
		this.i = 5;
		if (profile) {
			if (i == 11) {
				EventHandler.key = 0;
				MessageProfiler.sendKey(null, profile);
			}

			for (int j = 0;j < 9;++j) {
				if (i == 2 + j) {
					EventHandler.key = j + 1;
					MessageProfiler.sendKey(list.size() > j + 1 ? list.get(j + 1).profilerName : "", profile);
					// this.updateDebugProfilerName(j + 1);
				}
			}
		} else if (showTextureMap) {
			for (int j = 0;j < 9;++j) {
				if (i == 2 + j) {
					updateTextureMap(j + 1);
				}
			}
		}
	}

	private void updateTextureMap(int j) {
		if (j == 1) {
			textMapW = Math.max(0, textMapW + (mc.player.isSneaking() ? -16 : 16));
		} else if (j == 2) {
			textMapH = Math.max(0, textMapH + (mc.player.isSneaking() ? -16 : 16));
		} else if (j == 3) {
			textMapX = textMapX - (mc.player.isSneaking() ? -16 : 16);
		} else if (j == 4) {
			textMapY = textMapY - (mc.player.isSneaking() ? -16 : 16);
		} else if (j == 5) {
			int i = (mc.player.isSneaking() ? -16 : 16);
			float r = (float) textMapW / textMapH;
			textMapW = Math.max(0, MathHelper.floor(textMapW + i * r));
			textMapH = Math.max(0, MathHelper.floor(textMapH + i));
		} else if (j == 6) {
			textMapW = 512;
			textMapH = 256;
			textMapX = 0;
			textMapY = 0;
		}
	}

	public String debugProfilerName = "root";
	public boolean profile;
	public List<Profiler.Result> list;
	public long lastUpdate;
	public Map<Integer, Pair<Double, Double>> tps;
	public double meanTime, meanTPS;
	public boolean showTextureMap;
	private static final DecimalFormat timeFormatter = new DecimalFormat("########0.000");

	private void displayDebugInfo() {
		if (profile && list != null && !list.isEmpty()) {
			List<Profiler.Result> list = TomsModUtils.copyOf(this.list);
			Profiler.Result profiler$result = list.remove(0);
			GlStateManager.clear(256);
			GlStateManager.matrixMode(5889);
			GlStateManager.enableColorMaterial();
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, 1000.0D, 3000.0D);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, 0.0F, -2000.0F);
			GlStateManager.glLineWidth(1.0F);
			GlStateManager.disableTexture2D();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			int j = mc.displayWidth - 160 - 10;
			int k = mc.displayHeight - 320;
			GlStateManager.enableBlend();
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			int loc = tps.size() * 16;
			int bgr = 80, bgg = 80, bgb = 80, bga = 120;
			int bgj = j, bgk = k - loc - 40, bgh = 320 + loc + 40;
			vertexbuffer.pos(bgj - 176.0F, bgk - 96.0F - 16.0F, 0.0D).color(bgr, bgg, bgb, bga).endVertex();
			vertexbuffer.pos(bgj - 176.0F, bgk + bgh, 0.0D).color(bgr, bgg, bgb, bga).endVertex();
			vertexbuffer.pos(bgj + 176.0F, bgk + bgh, 0.0D).color(bgr, bgg, bgb, bga).endVertex();
			vertexbuffer.pos(bgj + 176.0F, bgk - 96.0F - 16.0F, 0.0D).color(bgr, bgg, bgb, bga).endVertex();
			tessellator.draw();
			GlStateManager.disableBlend();
			double d0 = 0.0D;

			for (int l = 0;l < list.size();++l) {
				Profiler.Result profiler$result1 = list.get(l);
				int i1 = MathHelper.floor(profiler$result1.usePercentage / 4.0D) + 1;
				vertexbuffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
				int j1 = profiler$result1.getColor();
				int k1 = j1 >> 16 & 255;
			int l1 = j1 >> 8 & 255;
			int i2 = j1 & 255;
			vertexbuffer.pos(j, k, 0.0D).color(k1, l1, i2, 255).endVertex();

			for (int j2 = i1;j2 >= 0;--j2) {
				float f = (float) ((d0 + profiler$result1.usePercentage * j2 / i1) * (Math.PI * 2D) / 100.0D);
				float f1 = MathHelper.sin(f) * 160.0F;
				float f2 = MathHelper.cos(f) * 160.0F * 0.5F;
				vertexbuffer.pos(j + f1, k - f2, 0.0D).color(k1, l1, i2, 255).endVertex();
			}

			tessellator.draw();
			vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);

			for (int i3 = i1;i3 >= 0;--i3) {
				float f3 = (float) ((d0 + profiler$result1.usePercentage * i3 / i1) * (Math.PI * 2D) / 100.0D);
				float f4 = MathHelper.sin(f3) * 160.0F;
				float f5 = MathHelper.cos(f3) * 160.0F * 0.5F;
				vertexbuffer.pos(j + f4, k - f5, 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
				vertexbuffer.pos(j + f4, k - f5 + 10.0F, 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
			}

			tessellator.draw();
			d0 += profiler$result1.usePercentage;
			}

			DecimalFormat decimalformat = new DecimalFormat("##0.00");
			GlStateManager.enableTexture2D();
			String s = "";

			if (!"unspecified".equals(profiler$result.profilerName)) {
				s = s + "[0] ";
			}

			if (profiler$result.profilerName.isEmpty()) {
				s = s + "ROOT ";
			} else {
				s = s + profiler$result.profilerName + ' ';
			}
			int i = 0;
			mc.fontRenderer.drawStringWithShadow(I18n.format("commands.forge.tps.summary", "Overall", timeFormatter.format(meanTime), timeFormatter.format(meanTPS)), j - 160, k - 80 - 48 - loc + i * 16, 16777215);
			for (Entry<Integer, Pair<Double, Double>> e : tps.entrySet()) {
				int dimId = e.getKey();
				double worldTickTime = e.getValue().getKey(), worldTPS = e.getValue().getValue();
				mc.fontRenderer.drawStringWithShadow(I18n.format("commands.forge.tps.summary", String.format("Dim %d", dimId), timeFormatter.format(worldTickTime), timeFormatter.format(worldTPS)), j - 160, k - 80 - 32 - loc + i * 16, 16777215);
				i++;
			}

			mc.fontRenderer.drawStringWithShadow("Tom's Mod Server Profiler", j - 160, k - 80 - loc - 64, 16777215);
			mc.fontRenderer.drawStringWithShadow("Last Updated " + (mc.world.getTotalWorldTime() - lastUpdate) + " tick(s) ago", j - 160, k - 80 - 32, 16777215);
			int l = Math.max(mc.fontRenderer.getStringWidth(s) - 50, 160);
			mc.fontRenderer.drawStringWithShadow(s, j - l, k - 80 - 16, 16777215);
			s = decimalformat.format(profiler$result.totalUsePercentage) + "%";
			mc.fontRenderer.drawStringWithShadow(s, j + 160 - mc.fontRenderer.getStringWidth(s), k - 80 - 16, 16777215);

			for (int k2 = 0;k2 < list.size();++k2) {
				Profiler.Result profiler$result2 = list.get(k2);
				StringBuilder stringbuilder = new StringBuilder();

				if ("unspecified".equals(profiler$result2.profilerName)) {
					stringbuilder.append("[?] ");
				} else {
					stringbuilder.append("[").append(k2 + 1).append("] ");
				}

				String s1 = stringbuilder.append(profiler$result2.profilerName).toString();
				mc.fontRenderer.drawStringWithShadow(s1, j - 160, k + 80 + k2 * 8 + 20, profiler$result2.getColor());
				s1 = decimalformat.format(profiler$result2.usePercentage) + "%";
				mc.fontRenderer.drawStringWithShadow(s1, j + 160 - 50 - mc.fontRenderer.getStringWidth(s1), k + 80 + k2 * 8 + 20, profiler$result2.getColor());
				s1 = decimalformat.format(profiler$result2.totalUsePercentage) + "%";
				mc.fontRenderer.drawStringWithShadow(s1, j + 160 - mc.fontRenderer.getStringWidth(s1), k + 80 + k2 * 8 + 20, profiler$result2.getColor());
			}
			GlStateManager.clear(256);
			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.loadIdentity();
			ScaledResolution r = new ScaledResolution(mc);
			GlStateManager.ortho(0.0D, r.getScaledWidth_double(), r.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		}
	}

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void addTooltip(ItemTooltipEvent e) {
		if (!e.getItemStack().isEmpty()) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen instanceof GuiContainer) {
				GuiContainer c = (GuiContainer) mc.currentScreen;
				if (c.inventorySlots != null) {
					for (int i = 0;i < c.inventorySlots.inventorySlots.size();i++) {
						Slot s = c.inventorySlots.inventorySlots.get(i);
						if (s instanceof ITooltipSlot && e.getItemStack() == s.getStack() && ((ITooltipSlot) s).showTooltip()) {
							ITooltipSlot t = (ITooltipSlot) s;
							t.getTooltip(e.getToolTip());
						}
					}
				}
			}
			if (tooltipOverrideFast.containsKey(e.getItemStack().getItem())) {
				Entry<String, String> entry = tooltipOverrideFast.get(e.getItemStack().getItem());
				e.getToolTip().addAll((Collection<? extends String>) Arrays.asList((I18n.format(entry.getValue())).split("/n")).stream().map(s -> entry.getKey() + s).collect(Collectors.<String>toList()));
			}
		}
	}

	public static class LCDFontRenderer extends FontRenderer {

		public LCDFontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
			super(gameSettingsIn, location, textureManagerIn, unicode);
			readFontTexture();
		}

		private void readFontTexture() {
			IResource iresource = null;
			BufferedImage bufferedimage;

			try {
				iresource = getResource(this.locationFontTexture);
				bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
			} catch (IOException ioexception) {
				throw new RuntimeException(ioexception);
			} finally {
				IOUtils.closeQuietly(iresource);
			}

			int imgWidth = bufferedimage.getWidth();
			int imgHeight = bufferedimage.getHeight();
			int[] rgbArray = new int[imgWidth * imgHeight];
			bufferedimage.getRGB(0, 0, imgWidth, imgHeight, rgbArray, 0, imgWidth);
			// int imgHeight16 = 128 / 16;
			// int imgWidth16 = 128 / 16;
			// float field = 8.0F / imgWidth16;

			for (int i = 0;i < 256;++i) {
				this.charWidth[i] = 8;
				/*int j1 = i % 16;
				int k1 = i / 16;

				if (i == 32)
				{
					this.charWidth[i] = 4 * 8;
				}

				int l1;

				for (l1 = imgWidth16 - 1; l1 >= 0; --l1)
				{
					int i2 = j1 * imgWidth16 + l1;
					boolean flag1 = true;

					for (int j2 = 0; j2 < imgHeight16 && flag1; ++j2)
					{
						int k2 = (k1 * imgWidth16 + j2) * imgWidth;
						int color = rgbArray[(i2 + k2) * 8];
						if (new Color(color).equals(Color.white))
						{
							flag1 = false;
						}
					}

					if (!flag1)
					{
						break;
					}
				}

				++l1;
				this.charWidth[i] = (int)(0.5D + l1 * field) + 1 * 8;*/
			}
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			this.readFontTexture();
			this.readGlyphSizes();
		}

		private void readGlyphSizes() {
			IResource iresource = null;

			try {
				iresource = getResource(new ResourceLocation("font/glyph_sizes.bin"));
				iresource.getInputStream().read(this.glyphWidth);
			} catch (IOException ioexception) {
				throw new RuntimeException(ioexception);
			} finally {
				IOUtils.closeQuietly(iresource);
			}
		}
	}

	public static void addTooltipOverride(Item tool, String string, String textformatting) {
		instance.tooltipOverrideFast.put(tool, new EmptyEntry<>(textformatting, string));
	}

	public static void addTooltipOverride(Item tool, String string, TextFormatting textformatting) {
		addTooltipOverride(tool, string, textformatting.toString());
	}

	public static void drawString(String s, int x, int y, int color) {
		mc.fontRenderer.drawString(s, x, y, color);
	}
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		CoreInit.registerItemRenders();
	}
}
