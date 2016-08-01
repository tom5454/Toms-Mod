package com.tom.lib;

import net.minecraft.client.settings.KeyBinding;

public class Keys {
	public static KeyBinding UP;
	public static KeyBinding DOWN;
	public static KeyBinding LEFT;
	public static KeyBinding RIGHT;
	public static KeyBinding ENTER;
	public static KeyBinding BACK;
	public static KeyBinding INTERACT;
	public static KeyBinding MENU;
	public static KeyBinding CONFIG;
	public static boolean isPressed(KeyBinding key) {
		return key != null ? key.isPressed() : false;
	}
}
