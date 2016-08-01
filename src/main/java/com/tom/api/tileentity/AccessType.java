package com.tom.api.tileentity;

import java.util.ArrayList;
import java.util.List;

public enum AccessType {
	BLOCK_MODIFICATION("Block Modification"), CONFIGURATION("Configuration"), FIELD_TRANSPORT("Field Transport"), STAY_IN_AREA("Stay in Area"), HAVE_INVENTORY("Have Inventory"), SWITCH_DEVICES("Use Switch"),
	RIGHTS_MODIFICATION("Modify Rights")
	;
	public static final AccessType[] VALUES = values();
	private final String name;
	private AccessType(String name) {
		this.name = name;
	}
	public static List<AccessType> getFullList(){
		List<AccessType> list = new ArrayList<AccessType>();
		for(AccessType a : VALUES){
			list.add(a);
		}
		return list;
	}
	public String getName() {
		return name;
	}
	public static AccessType get(int index){
		return VALUES[index % VALUES.length];
	}
}
