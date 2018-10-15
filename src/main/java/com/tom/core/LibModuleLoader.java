package com.tom.core;

import static com.tom.lib.api.module.ModuleManager.*;

import com.tom.lib.Configs;
import com.tom.lib.api.module.TMLibAddon;

@TMLibAddon(modid = Configs.Modid)
public class LibModuleLoader {
	@TMLibAddon
	public static String[] getModules(){
		return new String[]{DATA_STORAGE, PLAYER_HANDLER, WORLD_HANDLER};
	}
}
