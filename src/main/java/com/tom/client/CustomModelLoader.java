package com.tom.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class CustomModelLoader implements ICustomModelLoader {
	protected IResourceManager manager;
	protected static CustomModelLoader instance = new CustomModelLoader();
	public static Logger log = LogManager.getLogger("Tom's Mod Model Loader");
	private final Map<ResourceLocation, IModel> overrides = new HashMap<>();
	private final List<String> exceptions = new ArrayList<>();

	private CustomModelLoader() {
	}// OBJLoader

	public static void addOverride(ResourceLocation loc, IModel model) {
		log.info("Adding resource override: " + loc.toString());
		instance.overrides.put(loc, model);
	}

	@Override
	public void onResourceManagerReload(IResourceManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return overrides.containsKey(modelLocation) || overrides.containsKey(new ResourceLocation(modelLocation.getResourceDomain(), modelLocation.getResourcePath()));
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		if (overrides.containsKey(modelLocation))
			return overrides.get(modelLocation);
		ResourceLocation file = new ResourceLocation(modelLocation.getResourceDomain().replace("|", ""), modelLocation.getResourcePath());
		if (overrides.containsKey(file))
			return overrides.get(file);
		throw new RuntimeException("Invalid model: " + modelLocation);
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
