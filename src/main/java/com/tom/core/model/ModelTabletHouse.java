package com.tom.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
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

import com.tom.client.CustomModelLoader;

public class ModelTabletHouse implements IModel {
	private List<ResourceLocation> textures;
	public ModelTabletHouse() {
		textures = new ArrayList<>();
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_icons"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_side"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_side2"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_front"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_back"));
	}
	@Override
	public Collection<ResourceLocation> getTextures() {
		return textures;
	}
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		CustomModelLoader.log.info("Baking Tablet House model...");
		IModel baseModel = getModel(new ResourceLocation("tmobj:block/tablet.obj"));
		IModel tabTopModelOff = getModel(new ResourceLocation("tomsmodcore:item/tablet/tablet_top_off"));
		IModel display = getModel(new ResourceLocation("tomsmodcore:item/tablet/tablet_display"));
		IBakedModel base = baseModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_front")));
		BakedTabletModel model = new BakedTabletModel(tabTopModelOff.bake(state, format, bakedTextureGetter), base, display.bake(state, format, bakedTextureGetter));
		CustomModelLoader.log.info("Tablet House Model Baking Complete.");
		return model;
	}
	private static IModel getModel(ResourceLocation loc) {
		return ModelLoaderRegistry.getModelOrLogError(loc, "Couldn't load " + loc.toString() + " for tomsmodcore:tablet");
	}
	public static class TextureInjector implements Function<ResourceLocation, TextureAtlasSprite> {
		private Function<ResourceLocation, TextureAtlasSprite> def;
		private ResourceLocation loc;
		public TextureInjector(Function<ResourceLocation, TextureAtlasSprite> def, ResourceLocation loc) {
			this.def = def;
			this.loc = loc;
		}
		@Override
		public TextureAtlasSprite apply(ResourceLocation t) {
			if(t.getResourcePath().equalsIgnoreCase("texture")){
				return def.apply(loc);
			}
			return def.apply(t);
		}
	}
	public static class BakedTabletModel implements IBakedModel {
		private TabletOverrides overrides;
		private IBakedModel base, top, display;
		public BakedTabletModel(IBakedModel top, IBakedModel base, IBakedModel display) {
			this.top = top;
			this.base = base;
			this.display = display;
			overrides = new TabletOverrides(this);
		}
		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			return Collections.emptyList();
		}

		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return null;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return overrides;
		}
	}
	public static class TabletOverrides extends ItemOverrideList {
		private IBakedModel model;

		public TabletOverrides(BakedTabletModel parent) {
			super(Collections.emptyList());
			model = new ModelTablet.ItemBakedModel(parent.display, parent.base, parent.top);
		}
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack is, World world, EntityLivingBase entity) {
			return model;
		}
	}
}
