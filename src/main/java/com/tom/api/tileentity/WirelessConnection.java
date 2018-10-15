package com.tom.api.tileentity;

import com.tom.storage.handler.StorageData;

public class WirelessConnection {
	private StorageData data;
	private ProtectionType protectionType;
	private boolean protectedNetwork;
	private IProtection protection;
	private String name;

	public enum ProtectionType {
		NO_PROTECTION, PASSWORD, ITEM, LOGIN
		;
	}

	public static interface IProtection {

	}
}
