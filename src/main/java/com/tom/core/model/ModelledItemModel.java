package com.tom.core.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import com.google.common.collect.Sets;

import com.tom.client.CustomModelLoader;

import com.tom.core.item.ModelledItem;

import scala.actors.threadpool.Arrays;

public class ModelledItemModel implements IModel {
	private ModelledItem item;
	private Map<ResourceLocation, IModel> normalModels = new HashMap<>();

	public ModelledItemModel(ModelledItem item) {
		this.item = item;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<ResourceLocation> getTextures() {
		normalModels.clear();
		Set<ResourceLocation> textures = Sets.newHashSet();
		for (Entry<ResourceLocation, Integer> e : item.LOCATION_TO_META.entrySet()) {
			ResourceLocation loc = e.getKey();
			if (item.OVERRIDES.containsKey(e.getKey())) {
				ResourceLocation[][] o = item.OVERRIDES.get(e.getKey());
				textures.addAll(Arrays.asList(o[1]));
				loc = o[0][0];
			}
			if (!normalModels.containsKey(loc)) {
				normalModels.put(loc, getModel(loc));
			}
			IModel model = normalModels.get(loc);
			textures.addAll(model.getTextures().stream().filter(l -> !l.getResourcePath().startsWith("texture")).collect(Collectors.toSet()));
		}
		return textures;
	}

	private IModel getModel(ResourceLocation loc) {
		return ModelLoaderRegistry.getModelOrLogError(loc, "Couldn't load " + loc.toString() + " for " + item.delegate.name().toString().replaceAll("|", ""));
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		CustomModelLoader.log.info("Baking Modelled Item Model...");
		Map<Integer, IBakedModel> models = new HashMap<>();
		for (Entry<ResourceLocation, Integer> e : item.LOCATION_TO_META.entrySet()) {
			ResourceLocation loc = e.getKey();
			Function<ResourceLocation, TextureAtlasSprite> textureInj = bakedTextureGetter;
			if (item.OVERRIDES.containsKey(e.getKey())) {
				ResourceLocation[][] o = item.OVERRIDES.get(e.getKey());
				textureInj = new TextureInjector(bakedTextureGetter, o[1]);
				loc = o[0][0];
			}
			models.put(e.getValue(), normalModels.get(loc).bake(state, format, textureInj));
		}
		IBakedModel ret = new BakedModelledItemModel(models, bakedTextureGetter.apply(new ResourceLocation("blocks/stone")));
		CustomModelLoader.log.info("Modelled Item Model Baking Complete.");
		return ret;
	}

	public static class TextureInjector implements Function<ResourceLocation, TextureAtlasSprite> {
		private final Function<ResourceLocation, TextureAtlasSprite> func;
		private final ResourceLocation[] loc;

		public TextureInjector(Function<ResourceLocation, TextureAtlasSprite> func, ResourceLocation[] loc) {
			this.func = func;
			this.loc = loc;
		}

		@Override
		public TextureAtlasSprite apply(ResourceLocation input) {
			if (input.getResourcePath().startsWith("texture")) {
				String s = input.getResourcePath().substring("texture".length());
				int i = 0;
				try {
					i = Integer.parseInt(s);
				} catch (NumberFormatException e) {
				}
				return func.apply(loc[i]);
			} else if (input.getResourcePath().equalsIgnoreCase("missingno")) { return func.apply(loc[0]); }
			return func.apply(input);
		}
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class BakedModelledItemModel implements IBakedModel {
		private TextureAtlasSprite particle;
		private Map<Integer, IBakedModel> models;
		private ModelledItemOverrides overrides;

		public BakedModelledItemModel(Map<Integer, IBakedModel> models, TextureAtlasSprite particle) {
			this.particle = particle;
			this.models = models;
			this.overrides = new ModelledItemOverrides(models);
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			return models.get(0).getQuads(state, side, rand);
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

		@SuppressWarnings("deprecation")
		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return models.get(0).getItemCameraTransforms();
		}

		@Override
		public ItemOverrideList getOverrides() {
			return overrides;
		}

		public static class ModelledItemOverrides extends ItemOverrideList {
			private Map<Integer, IBakedModel> models;

			public ModelledItemOverrides(Map<Integer, IBakedModel> models) {
				super(Collections.emptyList());
				this.models = models;
			}

			@Override
			public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
				IBakedModel model = models.get(stack.getMetadata());
				return model != null ? model : originalModel;
			}
		}
	}
}
