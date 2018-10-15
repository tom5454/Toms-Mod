package com.tom.util;

import java.util.HashMap;
import java.util.Map;

public class DefaultedHashMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = -8646327278574987117L;
	private final V defaultValue;

	public DefaultedHashMap(V defaultValue) {
		super();
		this.defaultValue = defaultValue;
	}

	public DefaultedHashMap(V defaultValue, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		this.defaultValue = defaultValue;
	}

	public DefaultedHashMap(V defaultValue, int initialCapacity) {
		super(initialCapacity);
		this.defaultValue = defaultValue;
	}

	public DefaultedHashMap(V defaultValue, Map<? extends K, ? extends V> m) {
		super(m);
		this.defaultValue = defaultValue;
	}

	@Override
	public V get(Object key) {
		V ret = super.get(key);
		return ret != null ? ret : defaultValue;
	}

	public V getDefaultValue() {
		return defaultValue;
	}
}
