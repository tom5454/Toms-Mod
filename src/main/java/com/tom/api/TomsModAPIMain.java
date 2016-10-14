package com.tom.api;

import java.lang.reflect.Method;
import java.nio.file.NoSuchFileException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.recipes.TomsModRecipeHelper;

public class TomsModAPIMain {
	public static Logger log = LogManager.getLogger("TomsMod|Api");
	private static Class<?> tweaksClass;
	private static boolean tweaksTried = false, loadFailed = false;

	public static void registerTransparentBlock(Block block, float transparency, int meta){
		if(block != null){
			NBTTagCompound tag = new NBTTagCompound();
			ResourceLocation b1 = block.delegate.name();
			tag.setString("blockName", b1.getResourcePath());
			tag.setString("modid", b1.getResourceDomain());
			tag.setFloat("t", transparency);
			tag.setInteger("m", meta);
			TomsModRecipeHelper.sendMessage(tag, "glass", 2);
		}
	}
	public static void registerTransparentBlock(Block block, float transparency){
		registerTransparentBlock(block, transparency, 0);
	}
	public static boolean useWrench(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, EnumHand hand, float a, float b, float c){
		return callMethod("wrench", false, itemStack,player,world,pos,side,a,b,c, hand);
	}
	private static void initTweaksClassIfNecessary(){
		if(!tweaksTried && tweaksClass ==  null){
			log.info("Searching for com.tom.core.Tweaks class...");
			try{
				Class<?> c = Class.forName("com.tom.core.Tweaks");
				if(c != null){
					log.info("Class found. Integration loaded.");
					tweaksClass = c;
				}else{
					throw new NoSuchFileException("com.tom.core.Tweaks.class");
				}
			}catch(Exception e){
				log.warn("Failed to load the Tweaks class. Maybe Tom's Mod not installed?");
				log.warn(e.getMessage());
				loadFailed = true;
			}
			tweaksTried = true;
		}
	}
	@SuppressWarnings("unchecked")
	private static <T> T callMethod(String name, T _default, Object... params){
		initTweaksClassIfNecessary();
		if(!loadFailed){
			try{
				Class<?>[] classes = new Class[params.length];
				for(int i = 0;i<params.length;i++){
					if(params[i] != null){
						classes[i] = params[i].getClass();
					}else{
						classes[i] = Object.class;
					}
				}
				Method m = tweaksClass.getMethod(name, classes);
				T t = (T) m.invoke(null, params);
				return t != null ? t : _default;
			}catch(Exception e){
				log.error("Exception caught while handling method. Method: "+name+", Exception: "+e.getMessage());
			}
		}
		return _default;
	}
	public static void init(){
		initTweaksClassIfNecessary();
	}
}
