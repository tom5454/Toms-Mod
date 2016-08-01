package com.tom.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ComputerRegulatorClass {
	private static Class<?> computerCraft;
	private static Field serverRegistry;
	private static Method getMethod;
	private static Class<?> serverRegClass;
	public static void regulate(int id, String method){
		try {
			Class<?> c = serverRegistry.getType().getSuperclass();
			Object get = serverRegistry.get("");
			Object cast = c.cast(get);
			Object o = getMethod.invoke(cast,id);
			o.getClass().getMethod(method).invoke(o);
		} catch (Exception e) {e.printStackTrace();}
	}
	public static void init(){
		try {
			computerCraft = Class.forName( "dan200.computercraft.ComputerCraft");
			serverRegistry = computerCraft.getDeclaredField("serverComputerRegistry");
			serverRegClass = serverRegistry.getDeclaringClass().getSuperclass();
			getMethod = serverRegClass.getDeclaredMethod("get", new Class[]{int.class});//.getDeclaredMethod("get", new Class[]{int.class});
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
