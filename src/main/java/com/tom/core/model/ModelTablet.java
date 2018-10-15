package com.tom.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import com.tom.client.CustomModelLoader;
import com.tom.core.CoreInit;
import com.tom.util.TomsModUtils;

public class ModelTablet implements IModel {
	private List<ResourceLocation> textures;
	public ModelTablet() {
		textures = new ArrayList<>();
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_icons"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_connected1"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_connected2"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_connected3"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_connected4"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_connected5"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_disconnected"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_jammed"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_battery_full"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_battery25"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_battery5"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_battery50"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_battery75"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_connected"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_disconnected"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_side"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_side2"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_front"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_front_nop"));
		textures.add(new ResourceLocation("tomsmodcore:items/tablet/tablet_back"));
	}
	@Override
	public Collection<ResourceLocation> getTextures() {
		return textures;
	}
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		CustomModelLoader.log.info("Baking Tablet model...");
		IModel baseModel = getModel(new ResourceLocation("tmobj:block/tablet.obj"));
		IModel tabTopModel = getModel(new ResourceLocation("tomsmodcore:item/tablet/tablet_top"));
		IModel tabTopModelOff = getModel(new ResourceLocation("tomsmodcore:item/tablet/tablet_top_off"));
		IModel batteryModel = getModel(new ResourceLocation("tomsmodcore:item/tablet/tablet_battery"));
		IModel antModel = getModel(new ResourceLocation("tomsmodcore:item/tablet/tablet_ant"));
		IModel connModel = getModel(new ResourceLocation("tomsmodcore:item/tablet/tablet_conn"));
		IModel display = getModel(new ResourceLocation("tomsmodcore:item/tablet/tablet_display"));
		IBakedModel base = baseModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_front")));
		IBakedModel baseNop = baseModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_front_nop")));
		IBakedModel[] ant = new IBakedModel[]{
				antModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_disconnected"))),
				antModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_jammed"))),
				antModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_connected1"))),
				antModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_connected2"))),
				antModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_connected3"))),
				antModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_connected4"))),
				antModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_ant_connected5")))
		}, battery = new IBakedModel[]{
				batteryModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_battery5"))),
				batteryModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_battery25"))),
				batteryModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_battery50"))),
				batteryModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_battery75"))),
				batteryModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_battery_full")))
		}, connection = new IBakedModel[]{
				connModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_disconnected"))),
				connModel.bake(state, format, new TextureInjector(bakedTextureGetter, new ResourceLocation("tomsmodcore:items/tablet/tablet_connected")))
		}, top = new IBakedModel[]{
				tabTopModelOff.bake(state, format, bakedTextureGetter),
				tabTopModel.bake(state, format, bakedTextureGetter)
		};
		BakedTabletModel model = new BakedTabletModel(ant, battery, connection, top, base, baseNop, display.bake(state, format, bakedTextureGetter));
		CustomModelLoader.log.info("Tablet Model Baking Complete.");
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
		private IBakedModel[] ant, battery, connection, top;
		private IBakedModel base, baseNop, display;
		public BakedTabletModel(IBakedModel[] ant, IBakedModel[] battery, IBakedModel[] connection, IBakedModel[] top, IBakedModel base, IBakedModel baseNop, IBakedModel display) {
			this.ant = ant;
			this.battery = battery;
			this.connection = connection;
			this.top = top;
			this.base = base;
			this.baseNop = baseNop;
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
		private BakedTabletModel parent;
		private IBakedModel off, empty;

		public TabletOverrides(BakedTabletModel parent) {
			super(Collections.emptyList());
			this.parent = parent;
			empty = new ItemBakedModel(parent.display, parent.baseNop);
			off = new ItemBakedModel(parent.display, parent.base, parent.top[0]);
		}
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack is, World world, EntityLivingBase entity) {
			if (is.getTagCompound() != null) {
				NBTTagCompound tag = is.getTagCompound();
				if (tag.hasKey("active")) {
					boolean act = tag.getBoolean("active");
					boolean bat = tag.hasKey("batEmpty") ? tag.getBoolean("batEmpty") : false;
					if (bat) {
						return empty;
					} else if (act) {
						if (tag.hasKey("Energy")) {
							double per = CoreInit.Tablet.getEnergyStored(is) * 100 / CoreInit.Tablet.getMaxEnergyStored(is);
							int p = MathHelper.floor(per);
							// System.out.println(per);
							boolean ant = tag.hasKey("ant") ? tag.getBoolean("ant") : false;
							boolean ap = tag.hasKey("ap") ? tag.getBoolean("ap") : false;
							boolean j = tag.hasKey("j") ? tag.getBoolean("j") : false;
							List<IBakedModel> models = new ArrayList<>();
							models.add(parent.base);
							models.add(parent.top[1]);
							if(j){
								models.add(parent.connection[0]);
								models.add(parent.ant[1]);
							}else{
								if(ant){
									BlockPos pos = TomsModUtils.readBlockPosFromNBT(tag.getCompoundTag("antpos"));
									double distance = pos != null && entity != null ? pos.distanceSqToCenter(entity.posX, entity.posY, entity.posZ) : 0;
									int maxRange = tag.getInteger("antrange");
									maxRange = maxRange == 0 ? 1 : maxRange;
									double distp = 1 - (distance / (maxRange*maxRange));
									if(distp >= .8){
										models.add(parent.ant[6]);
									}else if(distp >= .6){
										models.add(parent.ant[5]);
									}else if(distp >= .4){
										models.add(parent.ant[4]);
									}else if(distp >= .2){
										models.add(parent.ant[3]);
									}else
										models.add(parent.ant[2]);
								}else{
									models.add(parent.ant[0]);
								}
								if(ap){
									models.add(parent.connection[1]);
								}else{
									models.add(parent.connection[0]);
								}
							}
							if (p >= 80) {
								models.add(parent.battery[4]);
							} else if (p < 80 && p > 40) {
								models.add(parent.battery[3]);
							} else if (p > 30 && p < 41) {
								models.add(parent.battery[2]);
							} else if (p > 10 && p < 31) {
								models.add(parent.battery[1]);
							} else if (p < 11) {
								models.add(parent.battery[0]);
							}
							return new ItemBakedModel(parent.display, models);
						}
						return off;
					} else {
						return off;
					}
				} else {
					return off;
				}
			} else {
				return off;
			}
		}
	}
	public static class ItemBakedModel implements IBakedModel {
		private List<IBakedModel> models;
		private ItemCameraTransforms display;
		public ItemBakedModel(IBakedModel display, IBakedModel... models) {
			this(display, Arrays.asList(models));
		}
		@SuppressWarnings("deprecation")
		public ItemBakedModel(IBakedModel display, List<IBakedModel> models) {
			this.models = models;
			this.display = display.getItemCameraTransforms();
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			List<BakedQuad> quads = new ArrayList<>();
			models.stream().map(m -> m.getQuads(state, side, rand)).forEach(quads::addAll);
			return quads;
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
			return ItemOverrideList.NONE;
		}
		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			/*return new ItemCameraTransforms(
					new ItemTransformVec3f(new Vector3f(0, 0, 0), (Vector3f) new Vector3f(0, 2.5f, .5f).scale(0.0625F), new Vector3f(0.5f, 0.5f, 0.5f)),//thirdperson_leftIn
					new ItemTransformVec3f(new Vector3f(0, 0, 0), (Vector3f) new Vector3f(0, 2.5f, .5f).scale(0.0625F), new Vector3f(0.5f, 0.5f, 0.5f)),//thirdperson_rightIn
					new ItemTransformVec3f(new Vector3f(10, -80, 20), (Vector3f) new Vector3f(0, 4f, 0).scale(0.0625F), new Vector3f(0.55f, 0.55f, 0.55f)),//firstperson_leftIn
					new ItemTransformVec3f(new Vector3f(10, -80, 20), (Vector3f) new Vector3f(0, 4f, .5f).scale(0.0625F), new Vector3f(0.55f, 0.55f, 0.55f)),//firstperson_rightIn
					ItemTransformVec3f.DEFAULT,//headIn
					new ItemTransformVec3f(new Vector3f(0, 0, 0), (Vector3f) new Vector3f(0, 0, 0).scale(0.0625F), new Vector3f(1, 1, 1)),//guiIn
					new ItemTransformVec3f(new Vector3f(0, 0, 0), (Vector3f) new Vector3f(0, 3, 0).scale(0.0625F), new Vector3f(0.5f, .5f, .5f)),//groundIn
					new ItemTransformVec3f(new Vector3f(0, 180, 0), (Vector3f) new Vector3f(0, 0, 0).scale(0.0625F), new Vector3f(0.6f, .6f, .6f))//fixedIn
					);*/
			/*return new ItemCameraTransforms(
					new ItemTransformVec3f(new Vector3f(0, 0, 0), (Vector3f) new Vector3f(0, 2.5f, .5f).scale(0.0625F), new Vector3f(0.5f, 0.5f, 0.5f)),//thirdperson_leftIn
					new ItemTransformVec3f(new Vector3f(0, 0, 0), (Vector3f) new Vector3f(0, 2.5f, .5f).scale(0.0625F), new Vector3f(0.5f, 0.5f, 0.5f)),//thirdperson_rightIn
					new ItemTransformVec3f(new Vector3f(10, -80, 20), (Vector3f) new Vector3f(0, 4f, 0).scale(0.0625F), new Vector3f(0.55f, 0.55f, 0.55f)),//firstperson_leftIn
					new ItemTransformVec3f(new Vector3f(0, 0, 0), (Vector3f) new Vector3f(0, 4f, .5f).scale(0.0625F), new Vector3f(0.55f, 0.55f, 0.55f)),//firstperson_rightIn
					ItemTransformVec3f.DEFAULT,//headIn
					new ItemTransformVec3f(new Vector3f(0, 0, 0), (Vector3f) new Vector3f(0, 0, 0).scale(0.0625F), new Vector3f(1, 1, 1)),//guiIn
					new ItemTransformVec3f(new Vector3f(0, 0, 0), (Vector3f) new Vector3f(0, 3, 0).scale(0.0625F), new Vector3f(0.5f, .5f, .5f)),//groundIn
					new ItemTransformVec3f(new Vector3f(0, 180, 0), (Vector3f) new Vector3f(0, 0, 0).scale(0.0625F), new Vector3f(5, 5, 5))//fixedIn
					);*/
			return display;
		}
	}
}
