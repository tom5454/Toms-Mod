package com.tom.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VariantL {
	@SuppressWarnings("rawtypes")
	private static Class variantClazz, vanillaClazz;
	private static Method variantAccepts, variantLoad, vanillaLoad;
	private static Object variantInstance, vanillaInstance;
	private static boolean hasInit;

	public static boolean accepts(ResourceLocation modelLocation) {
		findLoader();
		try {
			return (Boolean) variantAccepts.invoke(variantInstance, modelLocation);
		} catch (NullPointerException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
		} catch (ClassCastException e) {
		}
		return false;
	}

	public static IModel loadModel(ResourceLocation file) throws Exception {
		findLoader();
		try {
			return (IModel) variantLoad.invoke(variantInstance, file);
		} catch (NullPointerException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
		} catch (ClassCastException e) {
		}
		return ModelLoaderRegistry.getMissingModel();
	}

	public static IModel loadModelV(ResourceLocation file) throws Exception {
		findLoader();
		try {
			return (IModel) vanillaLoad.invoke(vanillaInstance, file);
		} catch (NullPointerException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
		} catch (ClassCastException e) {
		}
		return ModelLoaderRegistry.getMissingModel();
	}

	@SuppressWarnings("unchecked")
	private static void findLoader() {
		if (!hasInit) {
			hasInit = true;
			try {
				variantClazz = Class.forName("net.minecraftforge.client.model.ModelLoader$VariantLoader");
				variantAccepts = variantClazz.getMethod("accepts", new Class[]{ResourceLocation.class});
				variantAccepts.setAccessible(true);
				variantLoad = variantClazz.getMethod("loadModel", new Class[]{ResourceLocation.class});
				variantLoad.setAccessible(true);
				variantInstance = variantClazz.getEnumConstants()[0];
			} catch (ClassNotFoundException e) {
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			}
			try {
				vanillaClazz = Class.forName("net.minecraftforge.client.model.ModelLoader$VanillaLoader");
				vanillaLoad = vanillaClazz.getMethod("loadModel", new Class[]{ResourceLocation.class});
				vanillaLoad.setAccessible(true);
				vanillaInstance = vanillaClazz.getEnumConstants()[0];
			} catch (ClassNotFoundException e) {
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			}
		}
	}
}
