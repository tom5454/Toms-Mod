package com.tom.api.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class MultiblockRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
	private static final Map<Block, Map<Integer, MultiblockRenderer<?>>> TESR_REGISTRY = new HashMap<>();
	private static final Set<ResourceLocation> CUSTOM_MODELS = new HashSet<>();

	public abstract void render(double x, double y, double z, float partialTicks, T tile);

	public static <T extends TileEntity> void registerTESR(Block controller, int id, MultiblockRenderer<T> renderer) {
		if (!TESR_REGISTRY.containsKey(controller)) {
			TESR_REGISTRY.put(controller, new HashMap<>());
		}
		renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
		TESR_REGISTRY.get(controller).put(id, renderer);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TileEntity> void render(IBlockState state, BlockPos pos, T master, int id, double x, double y, double z, float partialTicks) {
		Map<Integer, MultiblockRenderer<?>> r = TESR_REGISTRY.get(state.getBlock());
		if (r != null) {
			MultiblockRenderer<T> tesr = (MultiblockRenderer<T>) r.get(id);
			if (tesr != null) {
				tesr.render(x, y, z, partialTicks, master);
			} else {
				drawNameplate(pos, "Unregistered id: " + id, x, y, z, 64);
			}
		}
	}

	public static void drawNameplate(BlockPos pos, String str, double x, double y, double z, int maxDistance) {
		Entity entity = TileEntityRendererDispatcher.instance.entity;
		double d0 = pos.distanceSq(entity.posX, entity.posY, entity.posZ);

		if (d0 <= maxDistance * maxDistance) {
			float f = TileEntityRendererDispatcher.instance.entityYaw;
			float f1 = TileEntityRendererDispatcher.instance.entityPitch;
			EntityRenderer.drawNameplate(Minecraft.getMinecraft().fontRenderer, str, (float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F, 0, f, f1, false, false);
		}
	}

	public static Set<ResourceLocation> getCustomModels() {
		return CUSTOM_MODELS;
	}

	public static void registerCustomModel(ResourceLocation loc) {
		CUSTOM_MODELS.add(loc);
	}
}
