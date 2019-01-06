package com.tom.lib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.audio.ISound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GlobalFields {
	public static List<String> mobs = new ArrayList<>();
	public static List<String> animals = new ArrayList<>();
	public static List<String> other = new ArrayList<>();
	@SideOnly(Side.CLIENT)
	public static List<ISound> tabletSounds;
}
