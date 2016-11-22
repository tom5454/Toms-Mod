package com.tom.storage.client;

/*import static com.tom.storage.multipart.PartStorageNetworkCable.CHANNEL;
import static com.tom.storage.multipart.PartStorageNetworkCable.COLOR;
import static com.tom.storage.multipart.PartStorageNetworkCable.DOWN;
import static com.tom.storage.multipart.PartStorageNetworkCable.EAST;
import static com.tom.storage.multipart.PartStorageNetworkCable.NORTH;
import static com.tom.storage.multipart.PartStorageNetworkCable.SOUTH;
import static com.tom.storage.multipart.PartStorageNetworkCable.TYPE;
import static com.tom.storage.multipart.PartStorageNetworkCable.UP;
import static com.tom.storage.multipart.PartStorageNetworkCable.WEST;*/
import static com.tom.storage.multipart.PartStorageNetworkCable.DATA;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelProcessingHelper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import com.tom.client.CustomModelLoader;
import com.tom.storage.item.StorageNetworkCable.CableColor;
import com.tom.storage.item.StorageNetworkCable.CableType;
import com.tom.storage.multipart.PartStorageNetworkCable.CableData;
//import com.tom.storage.multipart.PartStorageNetworkCable.UnlistedPropertyInt;

public class CableModel implements IModel {
	protected final List<ResourceLocation> textures;
	public static CableBakedModel bakedModel;
	private static final Axis[] axisValues = Axis.values();
	public CableModel() {
		textures = new ArrayList<ResourceLocation>();
		for(int t = 0;t<CableType.VALUES.length;t++){
			for(int c = 0;c<CableColor.VALUES.length;c++){
				String texture = "cable_" + (CableType.VALUES[t] == CableType.NORMAL ? CableColor.VALUES[c].getName() : CableType.VALUES[t].getName() + "_" + CableColor.VALUES[c].getName());
				textures.add(new ResourceLocation("tomsmodstorage:blocks/cable/" + texture));
			}
		}
		for(int i = 0;i<=9;i++){
			textures.add(new ResourceLocation("tomsmodstorage", "blocks/cable/cable_channel_" + i));
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

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		CustomModelLoader.log.info("Baking Cable Model...");
		CableBakedModel.Builder builder = new CableBakedModel.Builder(bakedTextureGetter.apply(new ResourceLocation("tomsmodstorage:blocks/cable/cable_fluix")));
		ResourceLocation locInvC = new ResourceLocation("tomsmodstorage:block/cable_c");
		ResourceLocation locChInvC = new ResourceLocation("tomsmodstorage:block/cable_c_ch");
		IModel modelInvC = getModel(locInvC);
		IModel modelInvCCh = getModel(locChInvC);
		for(int t = 0;t<CableType.VALUES.length;t++){
			CableType type = CableType.VALUES[t];
			String name = type.getName();
			boolean hasChannels = type == CableType.DENSE || type == CableType.SMART;
			boolean adv = type != CableType.NORMAL;
			boolean isDense = type == CableType.DENSE;
			ResourceLocation locC = new ResourceLocation("tomsmodstorage:block/cable_" + name + "_closed");
			ResourceLocation locO = new ResourceLocation("tomsmodstorage:block/cable_" + name + "_open");
			ResourceLocation locOS = new ResourceLocation("tomsmodstorage:block/cable_" + name + "_open_smart");
			ResourceLocation locOCC = new ResourceLocation("tomsmodstorage:block/cable_" + name + "_open_covered");
			ResourceLocation locOC = new ResourceLocation("tomsmodstorage:block/cable_" + name + "_open_c");
			ResourceLocation locChO = new ResourceLocation("tomsmodstorage:block/cable_" + name + "_open_ch");
			ResourceLocation locChOS = new ResourceLocation("tomsmodstorage:block/cable_" + name + "_open_smart_ch");
			ResourceLocation locChOC = new ResourceLocation("tomsmodstorage:block/cable_" + name + "_open_c_ch");
			ResourceLocation locS = new ResourceLocation("tomsmodstorage:block/cable_" + name + "_s");
			ResourceLocation locChS = new ResourceLocation("tomsmodstorage:block/cable_" + name + "_s_ch");
			IModel modelClosed = getModel(locC);
			IModel modelOpen = getModel(locO);
			IModel modelOpenC = getModel(locOC);
			IModel modelOpenCh = hasChannels ? getModel(locChO) : null;
			IModel modelOpenCCh = hasChannels ? getModel(locChOC) : null;
			IModel modelS = adv ? getModel(locS) : null;
			IModel modelSCh = adv && hasChannels ? getModel(locChS) : null;
			IModel modelSC = isDense ? getModel(locOS) : null;
			IModel modelSCCh = isDense ? getModel(locChOS) : null;
			IModel modelC = isDense ? getModel(locOCC) : null;
			for(int c = 0;c<CableColor.VALUES.length;c++){
				CableColor color = CableColor.VALUES[c];
				TextureInjector injector = new TextureInjector(bakedTextureGetter, type, color);
				TextureInjector injectorS = new TextureInjector(bakedTextureGetter, CableType.SMART, color);
				if(type == CableType.NORMAL)injectorS.setTexture2(new ResourceLocation("tomsmodstorage:blocks/cable/cable_" + color.getName()));
				else if(type == CableType.DENSE)injectorS.setTexture2(new ResourceLocation("tomsmodstorage:blocks/cable/cable_dense_" + color.getName()));
				TextureInjector injectorC = new TextureInjector(bakedTextureGetter, CableType.COVERED, color);
				if(type == CableType.NORMAL)injectorC.setTexture2(new ResourceLocation("tomsmodstorage:blocks/cable/cable_" + color.getName()));
				else if(type == CableType.DENSE)injectorC.setTexture2(new ResourceLocation("tomsmodstorage:blocks/cable/cable_dense_" + color.getName()));
				for(int j = 0;j<EnumFacing.VALUES.length;j++){
					TRSRTransformation transformation = new TRSRTransformation(getMatrix(EnumFacing.VALUES[j]));
					builder.clearQuickAccess();
					builder.putModel(type, color, EnumFacing.VALUES[j], 0, -1, modelClosed.bake(transformation, format, injector));
					builder.putModel(type, color, EnumFacing.VALUES[j], 1, -1, modelOpen.bake(transformation, format, injector));
					builder.putModel(type, color, EnumFacing.VALUES[j], 2, -1, modelOpenC.bake(transformation, format, injector));
					if(type == CableType.NORMAL){
						builder.putModel(type, color, EnumFacing.VALUES[j], 3, -1, modelInvC.bake(transformation, format, injectorC));
						builder.putModel(type, color, EnumFacing.VALUES[j], 4, -1, modelInvC.bake(transformation, format, injectorS));
					}else if(isDense){
						builder.putModel(type, color, EnumFacing.VALUES[j], 3, -1, modelC.bake(transformation, format, injectorC));
						builder.putModel(type, color, EnumFacing.VALUES[j], 4, -1, modelSC.bake(transformation, format, injector));
					}else{
						builder.putModel(type, color, EnumFacing.VALUES[j], 3, -1, 1);
					}
					for(int i = 0;i<=9;i++){
						if(i < 9 || type == CableType.DENSE){
							builder.clearQuickAccess();
							TextureInjector injectorChannel = new TextureInjector(bakedTextureGetter, type, i);
							if(hasChannels){
								builder.putModel(type, color, EnumFacing.VALUES[j], 1, i, new TintedBakedModel(modelOpenCh.bake(transformation, format, injectorChannel), color));
								builder.putModel(type, color, EnumFacing.VALUES[j], 2, i, new TintedBakedModel(modelOpenCCh.bake(transformation, format, injectorChannel), color));
							}
							if(type == CableType.NORMAL){
								builder.putModel(type, color, EnumFacing.VALUES[j], 4, i, new TintedBakedModel(modelInvCCh.bake(transformation, format, injectorChannel), color));
							}else if(isDense){
								builder.putModel(type, color, EnumFacing.VALUES[j], 4, i, new TintedBakedModel(modelSCCh.bake(transformation, format, injectorChannel), color));
							}
						}
					}
				}
				if(adv){
					for(int j = 0;j<axisValues.length;j++){
						TRSRTransformation transformation = new TRSRTransformation(getMatrix(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axisValues[j])));
						builder.putModelOverride(type, color, -1, axisValues[j], modelS.bake(transformation, format, injector));
						if(hasChannels){
							for(int i = 0;i<=9;i++){
								if(i < 9 || type == CableType.DENSE){
									TextureInjector injectorChannel = new TextureInjector(bakedTextureGetter, type, i);
									builder.putModelOverride(type, color, i, axisValues[j], new TintedBakedModel(modelSCh.bake(transformation, format, injectorChannel), color));
								}
							}
						}
					}
				}
			}
		}
		CableBakedModel ret = builder.makeModel();
		bakedModel = ret;
		CustomModelLoader.log.info("Cable Model Baking Complete.");
		return ret;
	}
	private static IModel getModel(ResourceLocation loc) {
		return ModelProcessingHelper.uvlock(ModelLoaderRegistry.getModelOrLogError(loc, "Couldn't load " + loc.toString() + " for tomsmodstorage:tm.cable"), true);
	}
	private static Matrix4f getMatrix(EnumFacing facing){
		switch(facing){
		case DOWN: return ModelRotation.X180_Y0.getMatrix();
		case UP: return ModelRotation.X0_Y0.getMatrix();
		case NORTH: return ModelRotation.X90_Y0.getMatrix();
		case SOUTH: return ModelRotation.X90_Y180.getMatrix();
		case WEST: return ModelRotation.X90_Y270.getMatrix();
		case EAST: return ModelRotation.X90_Y90.getMatrix();
		default: return new Matrix4f();
		}
	}
	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}
	public static class TextureInjector implements Function<ResourceLocation, TextureAtlasSprite>{
		private final Function<ResourceLocation, TextureAtlasSprite> func;
		private final ResourceLocation texture;
		private ResourceLocation texture2;
		public TextureInjector(Function<ResourceLocation, TextureAtlasSprite> func, CableType type, CableColor color) {
			this.func = func;
			String texture = "tomsmodstorage:blocks/cable/cable_";
			if(type == CableType.NORMAL){
				texture = texture + color.getName();
			}else if(type == CableType.DENSE){
				texture = texture + CableType.SMART.getName() + "_" + color.getName();
			}else{
				texture = texture + type.getName() + "_" + color.getName();
			}
			this.texture = new ResourceLocation(texture);
			if(type == CableType.DENSE){
				texture2 = new ResourceLocation("tomsmodstorage", "blocks/cable/cable_" + type.getName() + "_" + color.getName());
			}else if(type == CableType.SMART){
				texture2 = new ResourceLocation("tomsmodstorage", "blocks/cable/cable_" + CableType.COVERED.getName() + "_" + color.getName());
			}
		}
		public TextureInjector(Function<ResourceLocation, TextureAtlasSprite> func, CableType type, int channel) {
			this.func = func;
			this.texture = new ResourceLocation("tomsmodstorage", "blocks/cable/cable_channel_" + channel);
		}
		public TextureInjector setTexture2(ResourceLocation texture2) {
			this.texture2 = texture2;
			return this;
		}
		@Override
		public TextureAtlasSprite apply(ResourceLocation input) {
			if(input.getResourcePath().equals("TEXTURE")){
				return func.apply(texture);
			}else if(input.getResourcePath().equals("TEXTURE2")){
				if(texture2 == null)texture2 = new ResourceLocation("blocks/stone");
				return func.apply(texture2);
			}
			return func.apply(input);
		}
	}
	/*public static class BlockStateChecker implements Predicate<IBlockState>{
		private final CableType type;
		private final CableColor color;
		private final EnumFacing facing;
		private final int state;
		private final int channel;

		public BlockStateChecker(CableType type, CableColor color, EnumFacing facing, int state, int channel) {
			this.type = type;
			this.color = color;
			this.facing = facing;
			this.state = state;
			this.channel = channel;
		}

		@Override
		public boolean apply(IBlockState input) {
			IExtendedBlockState s = (IExtendedBlockState) input;
			return s.getValue(COLOR) == color && s.getValue(TYPE) == type && s.getValue(getProperty(facing)) == state && ((type == CableType.DENSE || type == CableType.SMART) && channel > -1 ? s.getValue(CHANNEL) == channel : true);
		}
	}
	public static class BlockStateCheckerAxis implements Predicate<IBlockState>{
		private final Axis axis;

		public BlockStateCheckerAxis(Axis axis) {
			this.axis = axis;
		}

		@Override
		public boolean apply(IBlockState input) {
			IExtendedBlockState s = (IExtendedBlockState) input;
			for(int i = 0;i<axisValues.length;i++){
				if(axisValues[i] == axis){
					if(!(s.getValue(getProperty(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis))) == 1 && s.getValue(getProperty(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis))) == 1))return false;
				}else{
					if(!(s.getValue(getProperty(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis))) == 0 && s.getValue(getProperty(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis))) == 0))return false;
				}
			}
			return true;
		}
	}
	private static UnlistedPropertyInt getProperty(EnumFacing f){
		switch(f){
		case DOWN:
			return DOWN;
		case EAST:
			return EAST;
		case NORTH:
			return NORTH;
		case SOUTH:
			return SOUTH;
		case UP:
			return UP;
		case WEST:
			return WEST;
		default:
			return DOWN;
		}
	}*/
	public static class TintedBakedQuad extends BakedQuad{

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
	public static class TintedBakedModel implements IBakedModel{
		private List<BakedQuad> stateToQuads;
		private final IBakedModel model;
		private final int tint;

		public TintedBakedModel(IBakedModel model, CableColor color) {
			this.model = model;
			Color c = new Color(color.getTint());
			this.tint = new Color(c.getBlue(), c.getGreen(), c.getRed()).getRGB();
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			if(side != null)return Collections.emptyList();
			if(stateToQuads == null){
				List<BakedQuad> quads = model.getQuads(state, side, rand);
				List<BakedQuad> quadsOut = new ArrayList<BakedQuad>();
				for(int i = 0;i<quads.size();i++){
					quadsOut.add(new TintedBakedQuad(quads.get(i), tint));
				}
				stateToQuads = quadsOut;
			}
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
	public static class CableBakedModel implements IBakedModel {
		private final Map<CableType, Map<CableColor, Map<EnumFacing, Map<Integer, Map<Integer, IBakedModel>>>>> model;
		private final Map<CableType, Map<CableColor, Map<Integer, Map<Axis, IBakedModel>>>> axisOverrides;
		protected final boolean ambientOcclusion;
		protected final boolean gui3D;
		protected final TextureAtlasSprite particleTexture;
		protected final ItemCameraTransforms cameraTransforms;
		protected final ItemOverrideList overrides;

		@SuppressWarnings("deprecation")
		public CableBakedModel(IBakedModel ibakedmodel, Map<CableType, Map<CableColor, Map<EnumFacing, Map<Integer, Map<Integer, IBakedModel>>>>> model, TextureAtlasSprite particleTexture, Map<CableType, Map<CableColor, Map<Integer, Map<Axis, IBakedModel>>>> modelO) {
			this.ambientOcclusion = ibakedmodel.isAmbientOcclusion();
			this.gui3D = ibakedmodel.isGui3d();
			this.particleTexture = particleTexture;
			this.cameraTransforms = ibakedmodel.getItemCameraTransforms();
			this.overrides = ibakedmodel.getOverrides();
			this.model = model;
			this.axisOverrides = modelO;
		}

		public static class Builder {
			private final Map<CableType, Map<CableColor, Map<EnumFacing, Map<Integer, Map<Integer, IBakedModel>>>>> model = Maps.<CableType, Map<CableColor, Map<EnumFacing, Map<Integer, Map<Integer, IBakedModel>>>>>newLinkedHashMap();
			private final Map<CableType, Map<CableColor, Map<Integer, Map<Axis, IBakedModel>>>> modelO = Maps.<CableType, Map<CableColor, Map<Integer, Map<Axis, IBakedModel>>>>newLinkedHashMap();
			private final List<IBakedModel> last = new ArrayList<IBakedModel>();
			private final TextureAtlasSprite particleTexture;
			private IBakedModel first;
			public Builder(TextureAtlasSprite particleTexture) {
				this.particleTexture = particleTexture;
			}
			public void putModelOverride(CableType type, CableColor color, int channel, Axis axis, IBakedModel bake) {
				if(!modelO.containsKey(type)){
					modelO.put(type, new HashMap<CableColor, Map<Integer, Map<Axis, IBakedModel>>>());
				}
				Map<CableColor, Map<Integer, Map<Axis, IBakedModel>>> map = modelO.get(type);
				if(!map.containsKey(color)){
					map.put(color, new HashMap<Integer, Map<Axis, IBakedModel>>());
				}
				Map<Integer, Map<Axis, IBakedModel>> m = map.get(color);
				if(!m.containsKey(channel)){
					m.put(channel, new HashMap<Axis, IBakedModel>());
				}
				m.get(channel).put(axis, bake);
			}
			public void clearQuickAccess(){
				last.clear();
			}

			public void putModel(CableType type, CableColor color, EnumFacing side, int state, int channel, IBakedModel bake) {
				if(!model.containsKey(type)){
					model.put(type, new HashMap<CableColor, Map<EnumFacing, Map<Integer, Map<Integer, IBakedModel>>>>());
				}
				Map<CableColor, Map<EnumFacing, Map<Integer, Map<Integer, IBakedModel>>>> map = model.get(type);
				if(!map.containsKey(color)){
					map.put(color, new HashMap<EnumFacing, Map<Integer, Map<Integer, IBakedModel>>>());
				}
				Map<EnumFacing, Map<Integer, Map<Integer, IBakedModel>>> m = map.get(color);
				if(!m.containsKey(side)){
					m.put(side, new HashMap<Integer, Map<Integer, IBakedModel>>());
				}
				Map<Integer, Map<Integer, IBakedModel>> stateMap = m.get(side);
				if(!stateMap.containsKey(state)){
					stateMap.put(state, new HashMap<Integer, IBakedModel>());
				}
				stateMap.get(state).put(channel, bake);
				last.add(bake);
				if(first == null)first = bake;
			}

			public void putModel(CableType type, CableColor color, EnumFacing side, int state, int channel, int lastId) {
				putModel(type, color, side, state, channel, last.get(lastId));
			}

			public CableBakedModel makeModel() {
				return new CableBakedModel(first, model, particleTexture, modelO);
			}
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
			if(side != null)return Collections.emptyList();
			IExtendedBlockState s = (IExtendedBlockState) state;
			CableData data = s.getValue(DATA);
			if(data.isValid() && data.getModel() != null){
				return data.getModel();
			}
			List<BakedQuad> quads = new ArrayList<BakedQuad>();
			CableType t = data.getType();
			CableColor c = data.getColor();
			Map<EnumFacing, Map<Integer, Map<Integer, IBakedModel>>> m = model.get(t).get(c);
			Map<CableColor, Map<Integer, Map<Axis, IBakedModel>>> am = axisOverrides.get(t);
			Axis a = null;
			Map<Integer, Map<Axis, IBakedModel>> axisMap = null;
			if(am != null){
				axisMap = am.get(c);
			}
			boolean setAxis = false, invalid = false;
			if(axisMap != null){
				for(int j = 0;j<axisValues.length;j++){
					Axis axis = axisValues[j];
					int cS1 = data.getValue(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis));
					int cS2 = data.getValue(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis));
					if(cS1 == 1 && cS2 == 1){
						if(!setAxis){
							a = axis;
							setAxis = true;
						}else{
							a = null;
						}
					}else if(cS1 != 0 || cS2 != 0){
						invalid = true;
						break;
					}
				}
			}
			if(!invalid && a != null && axisMap != null){
				quads.addAll(axisMap.get(-1).get(a).getQuads(state, side, rand));
				Map<Axis, IBakedModel> ch = axisMap.get(data.getChannel(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, a)));
				if(ch != null)quads.addAll(ch.get(a).getQuads(state, side, rand));
			}else{
				for(int j = 0;j<EnumFacing.VALUES.length;j++){
					EnumFacing f = EnumFacing.VALUES[j];
					int cS = data.getValue(f);
					quads.addAll(m.get(f).get(cS).get(-1).getQuads(state, side, rand));
					IBakedModel ch = m.get(f).get(cS).get(data.getChannel(f));
					if(ch != null)quads.addAll(ch.getQuads(state, side, rand));
				}
			}
			data.setModel(quads);
			return quads;
		}
		@Override
		public boolean isAmbientOcclusion()
		{
			return this.ambientOcclusion;
		}

		@Override
		public boolean isGui3d()
		{
			return this.gui3D;
		}

		@Override
		public boolean isBuiltInRenderer()
		{
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture()
		{
			return this.particleTexture;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms()
		{
			return this.cameraTransforms;
		}

		@Override
		public ItemOverrideList getOverrides()
		{
			return this.overrides;
		}
	}
}
