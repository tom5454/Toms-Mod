package com.tom.core.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import com.tom.api.client.MultiblockRenderer;
import com.tom.client.CustomModelLoader;
import com.tom.client.EventHandlerClient;

import com.tom.core.block.BlockHiddenRenderOld;

import com.tom.core.tileentity.TileEntityHidden;

public class ModelHidden implements IModel {
	private String name;
	private static Map<ResourceLocation, IBakedModel> models = new HashMap<>();
	private static int txins = -1;

	public ModelHidden(String name) {
		this.name = name;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return MultiblockRenderer.getCustomModels();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return Collections.emptyList();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		CustomModelLoader.log.info("Baking " + name + " Model...");
		if (txins != EventHandlerClient.textureIns) {
			txins = EventHandlerClient.textureIns;
			models = new HashMap<>();
			MultiblockRenderer.getCustomModels().forEach(r -> {
				IModel model = getModel(r);
				CustomModelLoader.log.info("Baking Submodel " + r.toString() + " for " + name + "...");
				models.put(r, model.bake(state, format, bakedTextureGetter));
			});
		} else {
			CustomModelLoader.log.info("Found cached models, skipping baking of submodels.");
		}
		BakedHiddenModel model = new BakedHiddenModel(bakedTextureGetter.apply(new ResourceLocation("blocks/stone")), models, ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter));
		CustomModelLoader.log.info(name + " Model Baking Complete.");
		return model;
	}

	private static IModel getModel(ResourceLocation loc) {
		return ModelLoaderRegistry.getModelOrLogError(loc, "Couldn't load " + loc.toString() + " for hidden block renderer");
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class BakedHiddenModel implements IBakedModel {
		private TextureAtlasSprite particle;
		private Map<ResourceLocation, IBakedModel> models;
		private IBakedModel missing;

		public BakedHiddenModel(TextureAtlasSprite particle, Map<ResourceLocation, IBakedModel> models, IBakedModel missing) {
			this.particle = particle;
			this.models = models;
			this.missing = missing;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			if (state instanceof IExtendedBlockState) {
				IBakedModel model;
				IBlockState renderState;
				TileEntityHidden te = ((IExtendedBlockState) state).getValue(BlockHiddenRenderOld.DATA);
				if (te.isRenderOld()) {
					IBlockState old = te.getRenderState();
					model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(old);
					renderState = old;
				} else {
					model = models.getOrDefault(te.getRender(), missing);
					renderState = Blocks.STONE.getDefaultState();
				}
				return model.getQuads(renderState, side, rand);
			}
			return Collections.emptyList();
		}

		@Override
		public boolean isAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return particle;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return new ItemOverrideList(Collections.emptyList());
		}

	}
}
