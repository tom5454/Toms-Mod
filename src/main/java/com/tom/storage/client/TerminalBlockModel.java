package com.tom.storage.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import com.tom.client.CustomModelLoader;
import com.tom.lib.utils.EmptyEntry;
import com.tom.storage.block.BasicTerminal;
import com.tom.storage.block.BlockTerminalBase;
import com.tom.storage.client.TerminalBlockModel.TerminalBakedModel.Builder;
import com.tom.storage.multipart.block.StorageNetworkCable.CableColor;
import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.TileEntityBasicTerminal.TerminalColor;
import com.tom.storage.tileentity.TileEntityBasicTerminal.TerminalFacing;
import com.tom.util.TomsModUtils;

public class TerminalBlockModel implements IModel {
	protected final List<ResourceLocation> textures;
	private static final ResourceLocation terminalCursor = new ResourceLocation("tomsmodstorage:blocks/terminal/cursor");
	private static final Map<Integer, String> textureCache = new HashMap<>();
	static {
		textureCache.put(1, terminalCursor.toString());
	}
	private final String nameN;
	private final boolean hasFront;
	private final BlockTerminalBase block;
	private final String imgLoc, imgLocL;

	public TerminalBlockModel(BlockTerminalBase b) {
		this.nameN = b.getName();
		this.hasFront = b.hasCustomFront();
		this.imgLoc = "tomsmodstorage:blocks/terminal/" + b.getCategory() + "/" + b.getUnlocalizedName().substring(8, b.getUnlocalizedName().length() - 6) + "_";
		this.imgLocL = imgLoc + "l_";
		textures = new ArrayList<>();
		if (hasFront) {
			textures.add(new ResourceLocation(imgLoc + "Front"));
			textures.add(new ResourceLocation(imgLoc + "FrontP"));
			textures.add(new ResourceLocation(imgLoc + "FrontOn"));
		} else {
			textures.add(new ResourceLocation("tomsmodstorage:blocks/terminalFront"));
			textures.add(new ResourceLocation("tomsmodstorage:blocks/terminalFrontP"));
			textures.add(new ResourceLocation("tomsmodstorage:blocks/terminalFrontOn"));
		}
		int[][][] imagesI = b.getImageIDs();
		int[][] images = imagesI[0];
		for (int i = 0;i < images.length;i++) {
			textures.add(new ResourceLocation(combine(imgLocL, images[i][0])));
		}
		images = imagesI[1];
		for (int i = 0;i < images.length;i++) {
			textures.add(new ResourceLocation(combine(imgLoc, images[i][0])));
		}
		textures.add(terminalCursor);
		block = b;
	}

	public static String combine(String imgLoc, int i) {
		if (i < 0) { return textureCache.getOrDefault(-i, combine(imgLoc, -i)); }
		return imgLoc + i;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return textures;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		CustomModelLoader.log.info("Baking " + nameN + " Model...");
		IBlockState s = block.getDefaultState();
		TerminalBakedModel.Builder builder = new Builder(bakedTextureGetter.apply(new ResourceLocation(hasFront ? imgLoc + "Front" : "tomsmodstorage:blocks/terminalFront")), block);
		ResourceLocation base = new ResourceLocation("tomsmodstorage:block/" + block.getUnlocalizedName().substring(8, block.getUnlocalizedName().length() - 6) + "_base");
		ResourceLocation front = new ResourceLocation("tomsmodstorage:block/terminalFront");
		ResourceLocation terminalItemTransformsL = new ResourceLocation("tomsmodstorage:block/terminalitemtransforms");
		IModel modelFront = getModel(front);
		IModel terminalItemTransforms = getModel(terminalItemTransformsL);
		TextureInjector injectorFront = new TextureInjector(bakedTextureGetter, hasFront ? imgLoc + "Front" : "tomsmodstorage:blocks/terminalFront");
		TextureInjector injectorFrontP = new TextureInjector(bakedTextureGetter, hasFront ? imgLoc + "FrontP" : "tomsmodstorage:blocks/terminalFrontP");
		TextureInjector injectorFrontOn = new TextureInjector(bakedTextureGetter, hasFront ? imgLoc + "FrontOn" : "tomsmodstorage:blocks/terminalFrontOn");
		builder.setItemCameraTransformsModel(terminalItemTransforms.bake(state, format, bakedTextureGetter));
		// load(null, builder, modelBase, modelFront, bakedTextureGetter,
		// injectorFront, injectorFrontOn, format, s);
		for (int ss = 0;ss < block.getStates();ss++) {
			for (int i = 0;i < TileEntityBasicTerminal.TerminalState.VALUES.length;i++) {
				TileEntityBasicTerminal.TerminalState termState = TileEntityBasicTerminal.TerminalState.VALUES[i];
				load(termState, builder, base, modelFront, bakedTextureGetter, injectorFront, injectorFrontP, injectorFrontOn, format, s, ss);
			}
		}
		TerminalBakedModel model = builder.makeModel();
		CustomModelLoader.log.info(nameN + " Model Baking Complete.");
		return model;
	}

	private void load(TileEntityBasicTerminal.TerminalState termState, Builder builder, ResourceLocation base, IModel modelFront, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, TextureInjector injectorFront, TextureInjector injectorFrontP, TextureInjector injectorFrontOn, VertexFormat format, IBlockState state, int ss) {
		int[][][] imagesI = block.getImageIDs();
		int[][] images = imagesI[1];
		int[][] imagesL = imagesI[0];
		IModel modelBase = getModel(block.getFrontModelMapper(ss, base));
		for (int f = 0;f < TerminalFacing.VALUES.length;f++) {
			final TRSRTransformation transformation = getTransformation(TerminalFacing.VALUES[f], false);
			final int ff = f;
			builder.putModel(termState, ss, TerminalFacing.VALUES[f], c -> {
				final TRSRTransformation transformationBase = getTransformation(TerminalFacing.VALUES[ff], block.mirrorModel(ss));
				List<IBakedModel> model;
				model = new ArrayList<>();
				if (termState == TileEntityBasicTerminal.TerminalState.ACTIVE) {
					for (int i = 0;i < images.length;i++) {
						TextureInjector injector = new TextureInjector(bakedTextureGetter, combine(imgLoc, images[i][0]));
						IBakedModel b = modelFront.bake(transformation, format, injector);
						b = images[i].length > 1 ? new TintedBakedModel(b, c, i, 1, block) : b;
						model.add(b);
					}
				} else if (termState == TileEntityBasicTerminal.TerminalState.LOADING) {
					for (int i = 0;i < imagesL.length;i++) {
						TextureInjector injector = new TextureInjector(bakedTextureGetter, combine(imgLocL, imagesL[i][0]));
						IBakedModel b = modelFront.bake(transformation, format, injector);
						b = imagesL[i].length > 1 ? new TintedBakedModel(b, c, i, 0, block) : b;
						model.add(b);
					}
				}
				return new EmptyEntry<>(modelBase.bake(transformationBase, format, termState == TileEntityBasicTerminal.TerminalState.OFF ? injectorFront : termState == TileEntityBasicTerminal.TerminalState.POWERED ? injectorFrontP : injectorFrontOn), model);
			});//
		}
	}

	private static Matrix4f getMatrix(TerminalFacing facing, boolean mirror) {
		if (mirror) {
			switch (facing) {
			case DOWN_EAST:
				return ModelRotation.X270_Y270.getMatrix();
			case DOWN_NORTH:
				return ModelRotation.X270_Y180.getMatrix();
			case DOWN_SOUTH:
				return ModelRotation.X270_Y0.getMatrix();
			case DOWN_WEST:
				return ModelRotation.X270_Y90.getMatrix();
			case EAST:
				return ModelRotation.X0_Y90.getMatrix();
			case NORTH:
				return ModelRotation.X0_Y0.getMatrix();
			case SOUTH:
				return ModelRotation.X0_Y180.getMatrix();
			case UP_EAST:
				return ModelRotation.X90_Y270.getMatrix();
			case UP_NORTH:
				return ModelRotation.X90_Y180.getMatrix();
			case UP_SOUTH:
				return ModelRotation.X90_Y0.getMatrix();
			case UP_WEST:
				return ModelRotation.X90_Y90.getMatrix();
			case WEST:
				return ModelRotation.X0_Y270.getMatrix();
			default:
				return new Matrix4f();
			}
		} else {
			switch (facing) {
			case DOWN_EAST:
				return ModelRotation.X270_Y270.getMatrix();
			case DOWN_NORTH:
				return ModelRotation.X270_Y180.getMatrix();
			case DOWN_SOUTH:
				return ModelRotation.X270_Y0.getMatrix();
			case DOWN_WEST:
				return ModelRotation.X270_Y90.getMatrix();
			case EAST:
				return ModelRotation.X0_Y270.getMatrix();
			case NORTH:
				return ModelRotation.X0_Y180.getMatrix();
			case SOUTH:
				return ModelRotation.X0_Y0.getMatrix();
			case UP_EAST:
				return ModelRotation.X90_Y270.getMatrix();
			case UP_NORTH:
				return ModelRotation.X90_Y180.getMatrix();
			case UP_SOUTH:
				return ModelRotation.X90_Y0.getMatrix();
			case UP_WEST:
				return ModelRotation.X90_Y90.getMatrix();
			case WEST:
				return ModelRotation.X0_Y90.getMatrix();
			default:
				return new Matrix4f();
			}
		}
	}

	private static TRSRTransformation getTransformation(TerminalFacing facing, boolean mirror) {
		return new TRSRTransformation(getMatrix(facing, mirror));
	}

	private IModel getModel(ResourceLocation loc) {
		return ModelLoaderRegistry.getModelOrLogError(loc, "Couldn't load " + loc.toString() + " for " + nameN);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public static class TerminalBakedModel implements IBakedModel {
		private final Map<TileEntityBasicTerminal.TerminalState, Map<TerminalFacing, Function<TerminalColor, Entry<IBakedModel, List<IBakedModel>>>>>[] model;
		private final Map<TileEntityBasicTerminal.TerminalState, Map<TerminalFacing, Map<TerminalColor, Entry<IBakedModel, List<IBakedModel>>>>>[] modelCache;
		protected final TerminalItemModel itemModel;
		protected final boolean ambientOcclusion;
		protected final boolean gui3D;
		protected final TextureAtlasSprite particleTexture;
		protected final ItemCameraTransforms cameraTransforms;
		protected final BlockTerminalBase b;

		@SuppressWarnings({"deprecation", "unchecked"})
		public TerminalBakedModel(IBakedModel ibakedmodel, Map<TileEntityBasicTerminal.TerminalState, Map<TerminalFacing, Function<TerminalColor, Entry<IBakedModel, List<IBakedModel>>>>>[] model2, TextureAtlasSprite particleTexture, IBakedModel itemCameraTransformsModel, BlockTerminalBase b) {
			this.ambientOcclusion = ibakedmodel.isAmbientOcclusion();
			this.gui3D = ibakedmodel.isGui3d();
			this.particleTexture = particleTexture;
			this.cameraTransforms = ibakedmodel.getItemCameraTransforms();
			this.model = model2;
			this.b = b;
			this.modelCache = TomsModUtils.createArray(b.getStates(), () -> new EnumMap<>(TileEntityBasicTerminal.TerminalState.class), new EnumMap[0]);
			this.itemModel = new TerminalItemModel(this, itemCameraTransformsModel);
			for (int ss = 0;ss < b.getStates();ss++) {
				for (int i = 0;i < TileEntityBasicTerminal.TerminalState.VALUES.length;i++) {
					Map<TerminalFacing, Map<TerminalColor, Entry<IBakedModel, List<IBakedModel>>>> facing = new EnumMap<>(TerminalFacing.class);
					for (int f = 0;f < TerminalFacing.VALUES.length;f++) {
						facing.put(TerminalFacing.VALUES[f], new HashMap<>());
					}
					modelCache[ss].put(TileEntityBasicTerminal.TerminalState.VALUES[i], facing);
				}
			}
		}

		public static class Builder {
			private final TextureAtlasSprite particleTexture;
			private Map<TileEntityBasicTerminal.TerminalState, Map<TerminalFacing, Function<TerminalColor, Entry<IBakedModel, List<IBakedModel>>>>>[] model;
			private Entry<IBakedModel, List<IBakedModel>> first;
			private final BlockTerminalBase b;
			protected IBakedModel itemCameraTransformsModel;

			@SuppressWarnings("unchecked")
			public Builder(TextureAtlasSprite particleTexture, BlockTerminalBase b) {
				this.particleTexture = particleTexture;
				model = TomsModUtils.createArray(b.getStates(), () -> new EnumMap<>(TileEntityBasicTerminal.TerminalState.class), new EnumMap[0]);
				this.b = b;
			}

			public void putModel(TileEntityBasicTerminal.TerminalState state, int ss, TerminalFacing side, Function<TerminalColor, Entry<IBakedModel, List<IBakedModel>>> supplier) {
				if (first == null)
					first = supplier.apply(new TerminalColor(CableColor.FLUIX));
				if (!model[ss].containsKey(state)) {
					model[ss].put(state, new EnumMap<>(TerminalFacing.class));
				}
				model[ss].get(state).put(side, supplier);
			}

			public TerminalBakedModel makeModel() {
				return new TerminalBakedModel(first.getKey(), model, particleTexture, itemCameraTransformsModel, b);
			}

			public void setItemCameraTransformsModel(IBakedModel model) {
				itemCameraTransformsModel = model;
			}
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
			if (side != null)
				return Collections.emptyList();
			TerminalFacing f = TerminalFacing.NORTH;
			TerminalColor color = new TerminalColor(CableColor.FLUIX);
			TileEntityBasicTerminal.TerminalState termS = TileEntityBasicTerminal.TerminalState.OFF;
			if (state != null) {
				IExtendedBlockState s = (IExtendedBlockState) state;
				f = s.getValue(BasicTerminal.FACING);
				color = s.getValue(BasicTerminal.COLOR);
				termS = s.getValue(BasicTerminal.STATE);
			}
			f = f == null ? TerminalFacing.NORTH : f;
			color = color == null ? new TerminalColor(CableColor.FLUIX) : color;
			termS = termS == null ? TileEntityBasicTerminal.TerminalState.OFF : termS;
			return getQuads(f, color.getColor(), color.getColorAlt(), termS, state, side, rand);
		}

		public List<BakedQuad> getQuads(TerminalFacing f, int color, int colorAlt, TileEntityBasicTerminal.TerminalState termS, @Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
			int ss = state != null ? b.getState(state) : 0;
			Map<TerminalFacing, Map<TerminalColor, Entry<IBakedModel, List<IBakedModel>>>> facingToModel = modelCache[ss].get(termS);
			TerminalColor c = new TerminalColor(color, colorAlt);
			Map<TerminalColor, Entry<IBakedModel, List<IBakedModel>>> colorToModel = facingToModel.get(f);
			Entry<IBakedModel, List<IBakedModel>> model = colorToModel.get(c);
			if (model != null) {
				List<BakedQuad> quads = new ArrayList<>();
				quads.addAll(model.getKey().getQuads(state, side, rand));
				if (model.getValue() != null) {
					List<IBakedModel> m = model.getValue();
					for (int i = 0;i < m.size();i++) {
						quads.addAll(m.get(i).getQuads(state, side, rand));
					}
				}
				return quads;
			} else {
				colorToModel.put(c, model = this.model[ss].get(termS).get(f).apply(c));
				List<BakedQuad> quads = new ArrayList<>();
				quads.addAll(model.getKey().getQuads(state, side, rand));
				if (model.getValue() != null) {
					List<IBakedModel> m = model.getValue();
					for (int i = 0;i < m.size();i++) {
						quads.addAll(m.get(i).getQuads(state, side, rand));
					}
				}
				return quads;
			}
		}

		@Override
		public boolean isAmbientOcclusion() {
			return this.ambientOcclusion;
		}

		@Override
		public boolean isGui3d() {
			return this.gui3D;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return this.particleTexture;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return this.cameraTransforms;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return this.itemModel;
		}
	}

	public static class TerminalItemModel extends ItemOverrideList {
		private TerminalBakedModel parent;
		private IBakedModel itemCameraTransformsModel;

		public TerminalItemModel(TerminalBakedModel parent, IBakedModel itemCameraTransformsModel) {
			super(Collections.emptyList());
			this.parent = parent;
			this.itemCameraTransformsModel = itemCameraTransformsModel;
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			int color = CableColor.FLUIX.getTint();
			int colorAlt = CableColor.FLUIX.getTintAlt();
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("color", 3)/* && stack.getTagCompound().hasKey("colorAlt", 3)*/) {
				color = stack.getTagCompound().getInteger("color");
				colorAlt = /*stack.getTagCompound().getInteger("colorAlt")*/0;
			}
			return new WrappedBakedModel(parent, parent.getQuads(TerminalFacing.NORTH, color, colorAlt, TileEntityBasicTerminal.TerminalState.ACTIVE, null, null, 0), itemCameraTransformsModel);
		}
	}

	public static class WrappedBakedModel implements IBakedModel {
		private IBakedModel parent;
		private List<BakedQuad> quads;
		private IBakedModel itemCameraTransformsModel;

		public WrappedBakedModel(IBakedModel parent, List<BakedQuad> quads, IBakedModel itemCameraTransformsModel) {
			this.parent = parent;
			this.quads = quads;
			this.itemCameraTransformsModel = itemCameraTransformsModel;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			return Collections.unmodifiableList(quads);
		}

		@Override
		public boolean isAmbientOcclusion() {
			return parent.isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return parent.isGui3d();
		}

		@Override
		public boolean isBuiltInRenderer() {
			return parent.isBuiltInRenderer();
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return parent.getParticleTexture();
		}

		@SuppressWarnings("deprecation")
		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return itemCameraTransformsModel.getItemCameraTransforms();
		}

		@Override
		public ItemOverrideList getOverrides() {
			return parent.getOverrides();
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

	public static class TintedBakedQuad extends BakedQuad {

		public TintedBakedQuad(BakedQuad quad, int tint) {
			super(tint(quad.getVertexData(), tint), 1, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
		}

		private static int[] tint(int[] vertexData, int tint) {
			int[] vd = new int[vertexData.length];
			System.arraycopy(vertexData, 0, vd, 0, vertexData.length);
			vd[3] = tint;
			vd[10] = tint;
			vd[17] = tint;
			vd[24] = tint;
			return vd;
		}
	}

	public static class TintedBakedModel implements IBakedModel {
		private List<BakedQuad> stateToQuads;
		private final IBakedModel model;
		private final int tint;

		public TintedBakedModel(IBakedModel model, TerminalColor colorIn, int id, int imgID, BlockTerminalBase block) {
			this.model = model;
			int[] e = block.getImageIDs()[imgID][id];
			int l = e[1];
			Color c = new Color(l == 1 ? colorIn.getColorAlt() : colorIn.getColor());
			int add = e.length > 2 ? e[2] : 0;
			Color color = e.length > 3 ? new Color(e[3]) : Color.white;
			tint = new Color(MathHelper.clamp(MathHelper.floor((c.getBlue() + add) * (color.getBlue() / 255F)), 0, 255), MathHelper.clamp(MathHelper.floor((c.getGreen() + add) * (color.getGreen() / 255F)), 0, 255), MathHelper.clamp(MathHelper.floor((c.getRed() + add) * (color.getRed() / 255F)), 0, 255)).getRGB();
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			if (side != null)
				return Collections.emptyList();
			/*float red, green, blue;
			if(c.getRed() > c.getGreen() && c.getRed() > c.getBlue()){
				red = color.getBlue() / 255F;
				if(c.getGreen() > c.getBlue()){
					green = color.getGreen() / 255F;
					blue = color.getRed() / 255F;
				}else{
					blue = color.getGreen() / 255F;
					green = color.getRed() / 255F;
				}
			}else if(c.getGreen() > c.getRed() && c.getGreen() > c.getBlue()){
				green = color.getBlue() / 255F;
				if(c.getRed() > c.getBlue()){
					red = color.getGreen() / 255F;
					blue = color.getRed() / 255F;
				}else{
					blue = color.getGreen() / 255F;
					red = color.getRed() / 255F;
				}
			}else if(c.getBlue() > c.getRed() && c.getBlue() > c.getGreen()){
				blue = color.getBlue() / 255F;
				if(c.getRed() > c.getGreen()){
					red = color.getGreen() / 255F;
					green = color.getRed() / 255F;
				}else{
					green = color.getGreen() / 255F;
					red = color.getRed() / 255F;
				}
			}*/
			// if(stateToQuads == null){
			List<BakedQuad> quads = model.getQuads(state, side, rand);
			List<BakedQuad> quadsOut = new ArrayList<>();
			for (int i = 0;i < quads.size();i++) {
				quadsOut.add(new TintedBakedQuad(quads.get(i), tint));
			}
			stateToQuads = quadsOut;
			// }
			return stateToQuads;
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
			return model.getParticleTexture();
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
}
