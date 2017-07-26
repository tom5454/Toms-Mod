package com.tom.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import com.tom.core.CoreInit;

public class CustomModelLoader implements ICustomModelLoader {
	protected IResourceManager manager;
	protected static CustomModelLoader instance = new CustomModelLoader();
	public static Logger log = LogManager.getLogger("Tom's Mod Model Loader");
	private final Map<ResourceLocation, IModel> overrides = new HashMap<>();
	private final List<String> exceptions = new ArrayList<>();

	private CustomModelLoader() {
	}// OBJLoader
	// private boolean loading = false;

	public static void addOverride(ResourceLocation loc, IModel model) {
		log.info("Adding resource override: " + loc.toString());
		instance.overrides.put(loc, model);
	}

	@Override
	public void onResourceManagerReload(IResourceManager manager) {
		this.manager = manager;
		// cache.clear();
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		// if(modelLocation instanceof ModelResourceLocation &&
		// (((ModelResourceLocation)modelLocation).getResourcePath().startsWith("fluid")))log.info(((ModelResourceLocation)modelLocation).toString());
		/*boolean isIgnored = CoreInit.ignoredLocations.contains(modelLocation.getResourcePath());
		return (modelLocation.getResourceDomain().startsWith("tomsmod")) && !isIgnored && VariantLoader.INSTANCE.accepts(modelLocation);*/
		boolean isIgnored = CoreInit.ignoredLocations.contains(modelLocation.getResourcePath());
		return overrides.containsKey(modelLocation) || overrides.containsKey(new ResourceLocation(modelLocation.getResourceDomain(), modelLocation.getResourcePath())) || (modelLocation.getResourceDomain().startsWith("tomsmod")) && VariantL.accepts(modelLocation) && !isIgnored;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		if (overrides.containsKey(modelLocation))
			return overrides.get(modelLocation);
		ResourceLocation file = new ResourceLocation(modelLocation.getResourceDomain().replace("|", ""), modelLocation.getResourcePath());
		if (overrides.containsKey(file))
			return overrides.get(file);
		String variant = modelLocation instanceof ModelResourceLocation ? ((ModelResourceLocation) modelLocation).getVariant() : "inventory";
		IModel model = null;
		ModelResourceLocation modelL = new ModelResourceLocation(/*variant.equalsIgnoreCase("inventory") ? file.getResourceDomain() + ":models/item/" + file.getResourcePath() : */file.toString(), variant);
		boolean exceptionCaught = false;
		try {
			model = VariantL.loadModel(modelL);
		} catch (Exception e) {
			exceptionCaught = true;
			try {
				model = VariantL.loadModelV(modelL);
			} catch (Exception e2) {
				String exc = "Could not load model definition for variant " + modelL.toString() + ". Exceptions: " + e.toString() + ", " + e2.toString();
				exceptions.add(exc);
				throw e;
			}
		}
		if ((model == null || model == ModelLoaderRegistry.getMissingModel() || model.getDependencies().contains(new ResourceLocation("builtin/missing"))) && !exceptionCaught) {
			try {
				model = VariantL.loadModelV(modelL);
			} catch (Exception e) {
				try {
					model = VariantL.loadModelV(new ModelResourceLocation(modelL.getResourceDomain() + ":blockstates/" + modelL.getResourcePath(), modelL.getVariant()));
				} catch (Exception e2) {
					String exc = "Could not load model definition for variant " + modelL.toString() + ". Exceptions: " + e.toString() + ", " + e2.toString();
					exceptions.add(exc);
					throw e;
				}
			}
		}
		model = model == null || model == ModelLoaderRegistry.getMissingModel() ? ModelLoaderRegistry.getModelOrMissing(file) : model;
		return model;
	}

	public static CustomModelLoader getInstance() {
		return instance;
	}

	public static void init() {
		log.info("Loading Custom Model Loader for Tom's Mod");
		instance = new CustomModelLoader();
		ModelLoaderRegistry.registerLoader(instance);
	}

	public static void printExceptions() {
		for (String s : instance.exceptions) {
			log.error(s);
		}
	}

	public static void clearExceptions() {
		instance.exceptions.clear();
	}
}
