package com.tom.apis;

import java.util.HashMap;
import java.util.Map;

public class EqualCheckMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = -8427775880938572096L;

	public EqualCheckMap() {
		super();
	}

	public EqualCheckMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public EqualCheckMap(int initialCapacity) {
		super(initialCapacity);
	}

	public EqualCheckMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	@Override
	public V get(Object key) {
		for(java.util.Map.Entry<K, V> s : entrySet()){
			if(s.getKey().equals(key))return s.getValue();
		}
		return null;
	}
}
