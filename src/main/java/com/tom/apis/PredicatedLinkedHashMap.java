package com.tom.apis;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Predicate;

public class PredicatedLinkedHashMap<K, V> extends LinkedHashMap<Predicate<K>, V> {

	private static final long serialVersionUID = 2334298365758753028L;
	private V defaultValue;

	public PredicatedLinkedHashMap(V defaultValue) {
		super();
		this.defaultValue = defaultValue;
	}

	public PredicatedLinkedHashMap(V defaultValue, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		this.defaultValue = defaultValue;
	}

	public PredicatedLinkedHashMap(V defaultValue, int initialCapacity) {
		super(initialCapacity);
		this.defaultValue = defaultValue;
	}

	public PredicatedLinkedHashMap(V defaultValue, Map<? extends Predicate<K>, ? extends V> m) {
		super(m);
		this.defaultValue = defaultValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		for (java.util.Map.Entry<Predicate<K>, V> s : entrySet()) {
			if (s.getKey().apply((K) key)) { return s.getValue(); }
		}
		return defaultValue;
	}
}
