package com.tom.handler;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;

import net.minecraftforge.common.AchievementPage;

import com.tom.core.CoreInit;
import com.tom.core.TMResource.CraftingMaterial;

public class AchievementHandler {
	public static AchievementPage page;
	private static Map<String, Achievement> achievementMap = new HashMap<String, Achievement>();
	public static Achievement researchTable, treetap, rubber;
	public static void init(){
		CoreInit.log.info("Loading Achievement Handler...");
		page = new AchievementPage("Tom's Mod");
		AchievementPage.registerAchievementPage(page);
		//addAchievement(1, 1, new ItemStack(Blocks.stone), "test2", addAchievement(0, 0, new ItemStack(Blocks.stone), "test", null));
		researchTable = addAchievement(0, 0, new ItemStack(CoreInit.researchTable), "researchTable", AchievementList.BUILD_WORK_BENCH);
		treetap = addAchievement(2, 0, new ItemStack(CoreInit.treeTap), "treetap", AchievementList.BUILD_WORK_BENCH);
		rubber = addAchievement(2, 2, CraftingMaterial.BOTTLE_OF_RUBBER.getStackNormal(), "rubber", treetap);
		//firstResearch = addAchievement(0, 2, new ItemStack(Items.paper), "firstResearch", researchTable);
	}

	public static Achievement addAchievement(int x, int y, ItemStack icon, String id, Achievement parent){
		CoreInit.log.info("Adding Achievement: "+id);
		Achievement achieve = new Achievement(id, id, x, y, icon, parent);
		achieve.initIndependentStat();
		achieve.registerStat();
		achievementMap.put(id, achieve);
		page.getAchievements().add(achieve);
		return achieve;
	}
	public static void giveAchievement(EntityPlayer player, String id){
		Achievement achieve = getAchievement(id);
		if(achieve != null) player.addStat(achieve);
	}

	public static Achievement getAchievement(String id) {
		return achievementMap.get(id);
	}
}
