package com.tom.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.audio.ISound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.MultiblockPartList;
import com.tom.apis.TomsModUtils;

import dan200.computercraft.api.peripheral.IComputerAccess;

public class GlobalFields {
	public static Object[][][][] EnderMemoryObj = {{/*public{ {playerName,Object}*/TomsModUtils.fillObjectSimple(65536) /*}*/},/*private{player{<playerName>},{Object0,Object1...}}*/TomsModUtils.fillObject(65536)};
	//public static Object[][][][] enderMemoryEvent= {{/*public{ {playerName,Object,eventTick}*/new Object[128][3] /*}*/},/*private{player{<playerName>},{Object0,Object1...}}*/TomsMathHelper.fillObject2(64)};
	public static Map<String,List<IComputerAccess>> EnderMemoryIComputerAccess = new HashMap<String, List<IComputerAccess>>();
	public static List<MultiblockPartList> MBFrames = new ArrayList<MultiblockPartList>();
	public static List<String> mobs = new ArrayList<String>();
	public static List<String> animals = new ArrayList<String>();
	public static List<String> other = new ArrayList<String>();
	//public static Map<Block,Float> glassBlocks = new HashMap<Block,Float>();
	@SideOnly(Side.CLIENT)
	public static List<ISound> tabletSounds;
}
