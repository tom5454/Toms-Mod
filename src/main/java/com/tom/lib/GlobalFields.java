package com.tom.lib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.audio.ISound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.util.TomsModUtils;

public class GlobalFields {
	public static Object[][][][] EnderMemoryObj = {{/*public{ {playerName,Object}*/new Object[65536][2] /*}*/}, /*private{player{<playerName>},{Object0,Object1...}}*/TomsModUtils.fillObject(65536)};
	// public static Object[][][][] enderMemoryEvent= {{/*public{
	// {playerName,Object,eventTick}*/new Object[128][3]
	// /*}*/},/*private{player{<playerName>},{Object0,Object1...}}*/TomsMathHelper.fillObject2(64)};
	public static List<String> mobs = new ArrayList<>();
	public static List<String> animals = new ArrayList<>();
	public static List<String> other = new ArrayList<>();
	// public static Map<Block,Float> glassBlocks = new HashMap<Block,Float>();
	@SideOnly(Side.CLIENT)
	public static List<ISound> tabletSounds;
}
