package com.tom.transport.model;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
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

import com.tom.client.CustomModelLoader;
import com.tom.lib.utils.TomsUtils;
import com.tom.transport.block.ConveyorBeltOmniBase;

public class ConveyorModel implements IModel {
	private String baseModel;
	public ConveyorModel(String baseModel) {
		this.baseModel = baseModel;
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return getModel(new ResourceLocation(baseModel)).getTextures();
	}
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		CustomModelLoader.log.info("Baking Conveyor Model...");
		IBakedModel[][] model = new IBakedModel[6][6];
		IBakedModel missing = ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
		IModel base = getModel(new ResourceLocation(baseModel));
		for(EnumFacing p : EnumFacing.VALUES){
			for(EnumFacing f : EnumFacing.VALUES){
				if(p.getAxis() != f.getAxis()){
					TRSRTransformation transformation = getTransformation(p, f);
					model[p.ordinal()][f.ordinal()] = base.bake(transformation, format, bakedTextureGetter);
				}else{
					model[p.ordinal()][f.ordinal()] = missing;
				}
			}
		}
		ConveyorBakedModel m = new ConveyorBakedModel(model);
		CustomModelLoader.log.info("Conveyor Model Baking Complete.");
		return m;
	}
	public static TRSRTransformation getTransformation(EnumFacing p, EnumFacing f) {
		switch (p) {
		case UP:
			return TRSRTransformation.from(f);
		case DOWN:
			switch (f) {
			case NORTH:
				return TRSRTransformation.from(ModelRotation.X180_Y180);
			case SOUTH:
				return TRSRTransformation.from(ModelRotation.X180_Y0);
			case EAST:
				return TRSRTransformation.from(ModelRotation.X180_Y90);
			case WEST:
				return TRSRTransformation.from(ModelRotation.X180_Y270);
			default:
				break;
			}
		case NORTH:
			switch (f) {
			case UP:
				return TRSRTransformation.from(ModelRotation.X270_Y180);
			case DOWN:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(270, 180, 180));
			case EAST:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(270, 180, 90));
			case WEST:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(270, 180, 270));
			default:
				break;
			}
		case SOUTH:
			switch (f) {
			case UP:
				return TRSRTransformation.from(ModelRotation.X270_Y0);
			case DOWN:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(270, 0, 180));
			case EAST:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(270, 0, 90));
			case WEST:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(270, 0, 270));
			default:
				break;
			}
		case WEST:
			switch (f) {
			case UP:
				return TRSRTransformation.from(ModelRotation.X270_Y90);
			case DOWN:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(270, 270, 180));
			case NORTH:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(180, 180, 90));
			case SOUTH:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(0, 180, 270));
			default:
				break;
			}
		case EAST:
			switch (f) {
			case UP:
				return TRSRTransformation.from(ModelRotation.X270_Y270);
			case DOWN:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(270, 90, 180));
			case NORTH:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(0, 0, 90));
			case SOUTH:
				return new TRSRTransformation(TomsUtils.modelRotateMatrix(0, 180, 90));
			default:
				break;
			}
		default:
			break;
		}
		return TRSRTransformation.identity();
	}
	private IModel getModel(ResourceLocation loc) {
		return ModelLoaderRegistry.getModelOrLogError(loc, "Couldn't load " + loc.toString() + " for Conveyor Model");
	}
	public static class ConveyorBakedModel implements IBakedModel {
		private IBakedModel[][] model;

		public ConveyorBakedModel(IBakedModel[][] model) {
			this.model = model;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			EnumFacing p = state.getValue(ConveyorBeltOmniBase.POSITION);
			EnumFacing f = state.getValue(ConveyorBeltOmniBase.FACING);
			return model[p.ordinal()][f.ordinal()].getQuads(state, side, rand);
		}

		@Override
		public boolean isAmbientOcclusion() {
			return model[0][2].isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return model[0][2].isGui3d();
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return model[0][2].getParticleTexture();
		}

		@Override
		public ItemOverrideList getOverrides() {
			return model[0][2].getOverrides();
		}
	}
}
