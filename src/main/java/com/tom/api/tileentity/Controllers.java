package com.tom.api.tileentity;

/*import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;*/

import net.minecraft.tileentity.TileEntity;

public class Controllers {
	//public static BiMap<Class<? extends TileEntity>,Integer> tileentityIds = HashBiMap.create();
	//public static int lastId = -1;
	public static boolean tileEntityExists(TileEntity tilee){
		return tilee instanceof TileEntityControllerBase;
	}

	public static void send(TileEntity tilee, int msg, int x, int y, int z) {
		TileEntityControllerBase te = (TileEntityControllerBase)tilee;
		te.receive(x, y, z, msg);
	}
	/*public static void init(){
		
	}
	public static int getTileEntityId(TileEntity te){
		return tileEntityExists(te) && tileentityIds.containsKey(te.getClass()) ? tileentityIds.get(te.getClass()) : -1;
	}
	protected static void put(Class<? extends TileEntity> c){
		tileentityIds.put(c, lastId + 1);
		lastId = lastId + 1;
	}*/
}
