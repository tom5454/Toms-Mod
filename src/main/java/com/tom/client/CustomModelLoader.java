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

public class CustomModelLoader implements ICustomModelLoader{
	protected IResourceManager manager;
	protected static CustomModelLoader instance = new CustomModelLoader();
	public static Logger log = LogManager.getLogger("Tom's Mod Model Loader");
	private final Map<ResourceLocation, IModel> overrides = new HashMap<ResourceLocation, IModel>();
	private final List<String> exceptions = new ArrayList<String>();
	private CustomModelLoader(){}//OBJLoader
	//private boolean loading = false;
	public static void addOverride(ResourceLocation loc, IModel model)
	{
		log.info("Adding resource override: "+loc.toString());
		instance.overrides.put(loc, model);
	}

	@Override
	public void onResourceManagerReload(IResourceManager manager)
	{
		this.manager = manager;
		//cache.clear();
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		//if(modelLocation instanceof ModelResourceLocation && (((ModelResourceLocation)modelLocation).getResourcePath().startsWith("fluid")))log.info(((ModelResourceLocation)modelLocation).toString());
		/*boolean isIgnored = CoreInit.ignoredLocations.contains(modelLocation.getResourcePath());
		return (modelLocation.getResourceDomain().startsWith("tomsmod")) && !isIgnored && VariantLoader.INSTANCE.accepts(modelLocation);*/
		boolean isIgnored = CoreInit.ignoredLocations.contains(modelLocation.getResourcePath());
		return overrides.containsKey(modelLocation) || (modelLocation.getResourceDomain().startsWith("tomsmod")) && VariantL.accepts(modelLocation) && !isIgnored;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		if(overrides.containsKey(modelLocation))return overrides.get(modelLocation);
		//		//loading = true;
		//		ResourceLocation file = new ResourceLocation(modelLocation.getResourceDomain().replace("|", ""), modelLocation.getResourcePath());
		//		/*if(file.getResourceDomain().startsWith("tm")){
		//			String other = CoreInit.multipartModidList.get(file.toString());
		//			if(other != null){
		//				file = new ResourceLocation(other, file.getResourcePath());
		//			}
		//		}*/
		//		String variant = modelLocation instanceof ModelResourceLocation ? ((ModelResourceLocation)modelLocation).getVariant() : "inventory";
		//		IModel model = null;
		//		String name = file.toString();
		//		/*if(variant.equals("inventory") && file.getResourcePath().startsWith("blockstates")){
		//			name = file.getResourceDomain()+":"+(file.getResourcePath().substring("blockstates/".length()));
		//		}*/
		//		ModelResourceLocation modelL = new ModelResourceLocation(/*variant.equalsIgnoreCase("inventory") ? file.getResourceDomain() + ":models/item/" + file.getResourcePath() : */name, variant);
		//		boolean exceptionCaught = false;
		//		try
		//		{
		//			/*IResource resource = null;
		//				try
		//				{
		//					resource = manager.getResource(file);
		//				}
		//				catch (FileNotFoundException e)
		//				{
		//					if (modelLocation.getResourcePath().startsWith("models/block/"))
		//						resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
		//					else if (modelLocation.getResourcePath().startsWith("models/item/"))
		//						resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
		//					else throw e;
		//				}finally{
		//					if(resource != null){
		//						model = ModelLoaderRegistry.getModel(resource.getResourceLocation());
		//					}
		//				}*/
		//			//model = VariantL.loadModel(modelL);
		//			//model = ModelLoaderRegistry.getModel(modelL);
		//			model = VariantLoader.INSTANCE.loadModel(modelL);
		//		}
		//		catch (Exception e)
		//		{
		//			exceptionCaught = true;
		//			try
		//			{
		//				/*IResource resource = null;
		//					try
		//					{
		//						resource = manager.getResource(file);
		//					}
		//					catch (FileNotFoundException e)
		//					{
		//						if (modelLocation.getResourcePath().startsWith("models/block/"))
		//							resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
		//						else if (modelLocation.getResourcePath().startsWith("models/item/"))
		//							resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
		//						else throw e;
		//					}finally{
		//						if(resource != null){
		//							model = ModelLoaderRegistry.getModel(resource.getResourceLocation());
		//						}
		//					}*/
		//				//model = VariantL.loadModelV(modelL);
		//				//model = ModelLoaderRegistry.getModel(modelL);
		//				model = VanillaLoader.INSTANCE.loadModel(modelL);
		//			}
		//			catch (Exception e2)
		//			{
		//				String exc = "Could not load model definition for variant " + modelL.toString() + ". Exceptions: " + e.toString() + ", " + e2.toString();
		//				//log.error(exc);
		//				exceptions.add(exc);
		//				//loading = false;
		//				throw e;
		//			}
		//		}
		//		if((model == null || model == ModelLoaderRegistry.getMissingModel() || model.getDependencies().contains(new ResourceLocation("builtin/missing"))) && !exceptionCaught){
		//			try
		//			{
		//				/*IResource resource = null;
		//					try
		//					{
		//						resource = manager.getResource(file);
		//					}
		//					catch (FileNotFoundException e)
		//					{
		//						if (modelLocation.getResourcePath().startsWith("models/block/"))
		//							resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
		//						else if (modelLocation.getResourcePath().startsWith("models/item/"))
		//							resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
		//						else throw e;
		//					}finally{
		//						if(resource != null){
		//							model = ModelLoaderRegistry.getModel(resource.getResourceLocation());
		//						}
		//					}*/
		//				//model = VariantL.loadModelV(modelL);
		//				//model = ModelLoaderRegistry.getModel(modelL);
		//				model = VanillaLoader.INSTANCE.loadModel(modelL);
		//			}
		//			catch (Exception e)
		//			{
		//				try
		//				{
		//					/*IResource resource = null;
		//						try
		//						{
		//							resource = manager.getResource(file);
		//						}
		//						catch (FileNotFoundException e)
		//						{
		//							if (modelLocation.getResourcePath().startsWith("models/block/"))
		//								resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
		//							else if (modelLocation.getResourcePath().startsWith("models/item/"))
		//								resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
		//							else throw e;
		//						}finally{
		//							if(resource != null){
		//								model = ModelLoaderRegistry.getModel(resource.getResourceLocation());
		//							}
		//						}*/
		//					//model = VariantL.loadModelV(new ModelResourceLocation(modelL.getResourceDomain() + ":blockstates/"+modelL.getResourcePath(), modelL.getVariant()));
		//					//model = ModelLoaderRegistry.getModel(modelL);
		//					model = VanillaLoader.INSTANCE.loadModel(modelL);
		//				}
		//				catch (Exception e2)
		//				{
		//					String exc = "Could not load model definition for variant " + modelL.toString() + ". Exceptions: " + e.toString() + ", " + e2.toString();
		//					//log.error(exc);
		//					exceptions.add(exc);
		//					//loading = false;
		//					throw e;
		//				}
		//			}
		//		}
		//		model = model == null || model == ModelLoaderRegistry.getMissingModel() ? ModelLoaderRegistry.getModelOrMissing(file) : model;
		//		//cache.put(file, model);
		//		//loading = false;
		//		return model;

		ResourceLocation file = new ResourceLocation(modelLocation.getResourceDomain().replace("|", ""), modelLocation.getResourcePath());
		/*if(file.getResourceDomain().startsWith("tm")){
			String other = CoreInit.multipartModidList.get(file.toString());
			if(other != null){
				file = new ResourceLocation(other, file.getResourcePath());
			}
		}*/
		String variant = modelLocation instanceof ModelResourceLocation ? ((ModelResourceLocation)modelLocation).getVariant() : "inventory";
		IModel model = null;
		ModelResourceLocation modelL = new ModelResourceLocation(/*variant.equalsIgnoreCase("inventory") ? file.getResourceDomain() + ":models/item/" + file.getResourcePath() : */file.toString(), variant);
		boolean exceptionCaught = false;
		try
		{
			/*IResource resource = null;
				try
				{
					resource = manager.getResource(file);
				}
				catch (FileNotFoundException e)
				{
					if (modelLocation.getResourcePath().startsWith("models/block/"))
						resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
					else if (modelLocation.getResourcePath().startsWith("models/item/"))
						resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
					else throw e;
				}finally{
					if(resource != null){
						model = ModelLoaderRegistry.getModel(resource.getResourceLocation());
					}
				}*/
			model = VariantL.loadModel(modelL);
		}
		catch (Exception e)
		{
			exceptionCaught = true;
			try
			{
				/*IResource resource = null;
					try
					{
						resource = manager.getResource(file);
					}
					catch (FileNotFoundException e)
					{
						if (modelLocation.getResourcePath().startsWith("models/block/"))
							resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
						else if (modelLocation.getResourcePath().startsWith("models/item/"))
							resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
						else throw e;
					}finally{
						if(resource != null){
							model = ModelLoaderRegistry.getModel(resource.getResourceLocation());
						}
					}*/
				model = VariantL.loadModelV(modelL);
			}
			catch (Exception e2)
			{
				String exc = "Could not load model definition for variant " + modelL.toString() + ". Exceptions: " + e.toString() + ", " + e2.toString();
				//log.error(exc);
				exceptions.add(exc);
				throw e;
			}
		}
		if((model == null || model == ModelLoaderRegistry.getMissingModel() || model.getDependencies().contains(new ResourceLocation("builtin/missing"))) && !exceptionCaught){
			try
			{
				/*IResource resource = null;
					try
					{
						resource = manager.getResource(file);
					}
					catch (FileNotFoundException e)
					{
						if (modelLocation.getResourcePath().startsWith("models/block/"))
							resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
						else if (modelLocation.getResourcePath().startsWith("models/item/"))
							resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
						else throw e;
					}finally{
						if(resource != null){
							model = ModelLoaderRegistry.getModel(resource.getResourceLocation());
						}
					}*/
				model = VariantL.loadModelV(modelL);
			}
			catch (Exception e)
			{
				try
				{
					/*IResource resource = null;
						try
						{
							resource = manager.getResource(file);
						}
						catch (FileNotFoundException e)
						{
							if (modelLocation.getResourcePath().startsWith("models/block/"))
								resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
							else if (modelLocation.getResourcePath().startsWith("models/item/"))
								resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
							else throw e;
						}finally{
							if(resource != null){
								model = ModelLoaderRegistry.getModel(resource.getResourceLocation());
							}
						}*/
					model = VariantL.loadModelV(new ModelResourceLocation(modelL.getResourceDomain() + ":blockstates/"+modelL.getResourcePath(), modelL.getVariant()));
				}
				catch (Exception e2)
				{
					String exc = "Could not load model definition for variant " + modelL.toString() + ". Exceptions: " + e.toString() + ", " + e2.toString();
					//log.error(exc);
					exceptions.add(exc);
					throw e;
				}
			}
		}
		model = model == null || model == ModelLoaderRegistry.getMissingModel() ? ModelLoaderRegistry.getModelOrMissing(file) : model;
		//cache.put(file, model);
		return model;
	}
	public static CustomModelLoader getInstance(){
		return instance;
	}
	public static void init(){
		log.info("Loading Custom Model Loader for Tom's Mod");
		instance = new CustomModelLoader();
		ModelLoaderRegistry.registerLoader(instance);
	}
	public static void printExceptions(){
		for(String s : instance.exceptions){
			log.error(s);
		}
	}
	public static void clearExceptions(){
		instance.exceptions.clear();
	}
}
