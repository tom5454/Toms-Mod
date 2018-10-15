package com.tom.storage.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import com.tom.client.CustomModelLoader;
import com.tom.storage.StorageInit;
import com.tom.storage.block.AdvStorageSystemRouter;

public class AdvRouterModel implements IModel {
	protected final List<ResourceLocation> textures;
	public static AdvRouterBakedModel bakedModel;

	public AdvRouterModel() {
		textures = new ArrayList<>();
		textures.add(new ResourceLocation("tomsmodstorage:blocks/advrouter/base_up"));
		textures.add(new ResourceLocation("tomsmodstorage:blocks/advrouter/base_down"));
		textures.add(new ResourceLocation("tomsmodstorage:blocks/advrouter/base_left"));
		textures.add(new ResourceLocation("tomsmodstorage:blocks/advrouter/base_right"));
		for (int i = 0;i < 3;i++) {
			textures.add(new ResourceLocation("tomsmodstorage:blocks/advrouter/center" + i));
			textures.add(new ResourceLocation("tomsmodstorage:blocks/advrouter/base_c_up" + i));
			textures.add(new ResourceLocation("tomsmodstorage:blocks/advrouter/base_c_down" + i));
			textures.add(new ResourceLocation("tomsmodstorage:blocks/advrouter/base_c_left" + i));
			textures.add(new ResourceLocation("tomsmodstorage:blocks/advrouter/base_c_right" + i));
		}
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return textures;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		CustomModelLoader.log.info("Baking Advanced Router Model...");
		IBlockState s = StorageInit.advRouter.getDefaultState();
		ResourceLocation loc = new ResourceLocation("tomsmodstorage:block/advrouterside");
		ResourceLocation locB = new ResourceLocation("tomsmodstorage:block/advrouter");
		IModel model = getModel(loc);
		IModel modelB = getModel(locB);
		List<BakedQuad>[][] models = new List[3][64];
		Map<EnumFacing, List<BakedQuad>[]>[] quads = new Map[3];
		quads[0] = new HashMap<>();
		quads[1] = new HashMap<>();
		quads[2] = new HashMap<>();
		TextureInjector[][] injectors = new TextureInjector[3][9];
		TextureInjector up = new TextureInjector(bakedTextureGetter, "tomsmodstorage:blocks/advrouter/base_up"),
				down = new TextureInjector(bakedTextureGetter, "tomsmodstorage:blocks/advrouter/base_down"),
				left = new TextureInjector(bakedTextureGetter, "tomsmodstorage:blocks/advrouter/base_left"),
				right = new TextureInjector(bakedTextureGetter, "tomsmodstorage:blocks/advrouter/base_right");
		for (int i = 0;i < 3;i++) {
			injectors[i][0] = new TextureInjector(bakedTextureGetter, "tomsmodstorage:blocks/advrouter/center" + i);
			injectors[i][1] = up;
			injectors[i][2] = down;
			injectors[i][3] = left;
			injectors[i][4] = right;
			injectors[i][5] = new TextureInjector(bakedTextureGetter, "tomsmodstorage:blocks/advrouter/base_c_up" + i);
			injectors[i][6] = new TextureInjector(bakedTextureGetter, "tomsmodstorage:blocks/advrouter/base_c_down" + i);
			injectors[i][7] = new TextureInjector(bakedTextureGetter, "tomsmodstorage:blocks/advrouter/base_c_left" + i);
			injectors[i][8] = new TextureInjector(bakedTextureGetter, "tomsmodstorage:blocks/advrouter/base_c_right" + i);
		}
		TRSRTransformation[] tr = new TRSRTransformation[6];
		for (EnumFacing f : EnumFacing.VALUES) {
			tr[f.ordinal()] = new TRSRTransformation(getMatrix(f));
		}
		for (int i = 0;i < models.length;i++)
			for (int j = 0;j < models[i].length;j++)
				models[i][j] = new ArrayList<>();
		for (int i = 0;i < quads.length;i++) {
			for (EnumFacing f : EnumFacing.VALUES) {
				List<BakedQuad>[] lists = new List[16];
				for (int j = 0;j < lists.length;j++) {
					List<BakedQuad> list = new ArrayList<>();
					list.addAll(model.bake(tr[f.ordinal()], format, injectors[i][0]).getQuads(s, null, 0));
					if ((j & (1 << 0)) != 0) {
						list.addAll(model.bake(tr[f.ordinal()], format, injectors[i][5]).getQuads(s, null, 0));
					} else {
						list.addAll(model.bake(tr[f.ordinal()], format, injectors[i][1]).getQuads(s, null, 0));
					}
					if ((j & (1 << 1)) != 0) {
						list.addAll(model.bake(tr[f.ordinal()], format, injectors[i][6]).getQuads(s, null, 0));
					} else {
						list.addAll(model.bake(tr[f.ordinal()], format, injectors[i][2]).getQuads(s, null, 0));
					}
					if ((j & (1 << 2)) != 0) {
						list.addAll(model.bake(tr[f.ordinal()], format, injectors[i][7]).getQuads(s, null, 0));
					} else {
						list.addAll(model.bake(tr[f.ordinal()], format, injectors[i][3]).getQuads(s, null, 0));
					}
					if ((j & (1 << 3)) != 0) {
						list.addAll(model.bake(tr[f.ordinal()], format, injectors[i][8]).getQuads(s, null, 0));
					} else {
						list.addAll(model.bake(tr[f.ordinal()], format, injectors[i][4]).getQuads(s, null, 0));
					}
					lists[j] = list;
				}
				quads[i].put(f, lists);
			}
		}
		for (int st = 0;st < models.length;st++) {
			for (int j = 0;j < models[st].length;j++) {
				List<EnumFacing> conn = new ArrayList<>();
				for (EnumFacing f : EnumFacing.VALUES) {
					if (connects(j, f)) {
						conn.add(f);
					}
				}
				for (EnumFacing side : EnumFacing.VALUES) {
					models[st][j].addAll(quads[st].get(side)[getModel(conn, side)]);
				}
			}
		}
		bakedModel = new AdvRouterBakedModel(models, /*quads,*/ modelB.bake(state, format, bakedTextureGetter), bakedTextureGetter.apply(new ResourceLocation("tomsmodstorage:blocks/advrouter_noc")));
		CustomModelLoader.log.info("Advanced Router Model Baking Complete.");
		return bakedModel;
	}

	public static boolean connects(int i, EnumFacing side) {
		return (i & (1 << side.ordinal())) != 0;
	}

	public static int getModel(List<EnumFacing> conn, EnumFacing side) {
		switch (side) {
		case DOWN: {
			byte ret = 0;
			for (EnumFacing f : EnumFacing.HORIZONTALS) {
				if (conn.contains(f)) {
					ret |= 1 << f.getOpposite().ordinal() - 2;
				}
			}
			return ret;
		}
		case EAST: {
			byte ret = 0;
			for (EnumFacing f : EnumFacing.VALUES) {
				if (f.getAxis() != side.getAxis())
					if (conn.contains(f)) {
						ret |= 1 << (f == EnumFacing.UP ? 1 : f == EnumFacing.DOWN ? 0 : f.rotateYCCW().ordinal() - 2);
					}
			}
			return ret;
		}
		case NORTH: {
			byte ret = 0;
			for (EnumFacing f : EnumFacing.VALUES) {
				if (f.getAxis() != side.getAxis())
					if (conn.contains(f)) {
						ret |= 1 << (f == EnumFacing.UP ? 1 : f == EnumFacing.DOWN ? 0 : f.ordinal() - 2);
					}
			}
			return ret > 16 ? 0 : ret;
		}
		case SOUTH: {
			byte ret = 0;
			for (EnumFacing f : EnumFacing.VALUES) {
				if (f.getAxis() != side.getAxis())
					if (conn.contains(f)) {
						ret |= 1 << (f == EnumFacing.UP ? 1 : f == EnumFacing.DOWN ? 0 : f.getOpposite().ordinal() - 2);
					}
			}
			return ret;
		}
		case UP: {
			byte ret = 0;
			for (EnumFacing f : EnumFacing.HORIZONTALS) {
				if (conn.contains(f)) {
					ret |= 1 << f.ordinal() - 2;
				}
			}
			return ret;
		}
		case WEST: {
			byte ret = 0;
			for (EnumFacing f : EnumFacing.VALUES) {
				if (f.getAxis() != side.getAxis())
					if (conn.contains(f)) {
						ret |= 1 << (f == EnumFacing.UP ? 1 : f == EnumFacing.DOWN ? 0 : f.rotateY().ordinal() - 2);
					}
			}
			return ret;
		}
		default:
			break;
		}
		return 0;
	}

	private static Matrix4f getMatrix(EnumFacing facing) {
		switch (facing) {
		case DOWN:
			return ModelRotation.X180_Y0.getMatrix();
		case UP:
			return ModelRotation.X0_Y0.getMatrix();
		case NORTH:
			return ModelRotation.X90_Y0.getMatrix();
		case SOUTH:
			return ModelRotation.X90_Y180.getMatrix();
		case WEST:
			return ModelRotation.X90_Y270.getMatrix();
		case EAST:
			return ModelRotation.X90_Y90.getMatrix();
		default:
			return new Matrix4f();
		}
	}

	private static IModel getModel(ResourceLocation loc) {
		return ModelLoaderRegistry.getModelOrLogError(loc, "Couldn't load " + loc.toString() + " for tomsmodstorage:tm.advrouterblock");
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class AdvRouterBakedModel implements IBakedModel {
		private List<BakedQuad>[][] models;
		// private Map<EnumFacing, List<BakedQuad>[]>[] quads;
		private IBakedModel model;
		private TextureAtlasSprite particle;

		public AdvRouterBakedModel(List<BakedQuad>[][] models, /*Map<EnumFacing, List<BakedQuad>[]>[] quads, */IBakedModel model, TextureAtlasSprite particle) {
			this.models = models;
			this.model = model;
			this.particle = particle;
			// this.quads = quads;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing s, long rand) {
			/*List<BakedQuad>[][] models = new List[3][64];
			for(int i = 0;i<models.length;i++)for(int j = 0;j<models[i].length;j++)models[i][j] = new ArrayList<>();
			for(int st = 0;st<models.length;st++){
				for(int j = 0;j<models[st].length;j++){
					List<EnumFacing> conn = new ArrayList<>();
					for(EnumFacing f : EnumFacing.VALUES){
						if(connects(j, f)){
							conn.add(f);
						}
					}
					for(EnumFacing side : EnumFacing.VALUES){
						models[st][j].addAll(quads[st].get(side)[getModel(conn, side)]);
					}
				}
			}*/
			int conn = 0;
			if (state instanceof IExtendedBlockState) {
				Byte c = ((IExtendedBlockState) state).getValue(AdvStorageSystemRouter.CONNECTIONS);
				if (c != null)
					conn = c;
			}
			if(state == null)
				return models[2][conn];
			else
				return models[state.getValue(AdvStorageSystemRouter.STATE)][conn];
		}

		@Override
		public boolean isAmbientOcclusion() {
			return model.isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return model.isGui3d();
		}

		@Override
		public boolean isBuiltInRenderer() {
			return model.isBuiltInRenderer();
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return particle;
		}

		@SuppressWarnings("deprecation")
		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return model.getItemCameraTransforms();
		}

		@Override
		public ItemOverrideList getOverrides() {
			return model.getOverrides();
		}

	}

	public static class TextureInjector implements Function<ResourceLocation, TextureAtlasSprite> {
		private final Function<ResourceLocation, TextureAtlasSprite> func;
		private final ResourceLocation texture;

		public TextureInjector(Function<ResourceLocation, TextureAtlasSprite> func, String name) {
			this.func = func;
			this.texture = new ResourceLocation(name);
		}

		@Override
		public TextureAtlasSprite apply(ResourceLocation input) {
			if (input.getResourcePath().equalsIgnoreCase("TEXTURE")) {
				return func.apply(texture);
			} else if (input.getResourcePath().equalsIgnoreCase("missingno")) { return func.apply(texture); }
			return func.apply(input);
		}
	}
}
