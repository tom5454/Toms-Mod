package com.tom.core;

public abstract class IMod{
	public abstract String getModID();
	public abstract boolean hadPreInit();
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof String)return getModID().equals(obj);
		return super.equals(obj);
	}
}