package com.tom.apis;

import java.util.Map.Entry;

public class EmptyEntry<K, V> implements Entry<K, V> {
	K key;
	V value;
	public EmptyEntry(K key){
		this.key = key;
	}
	public EmptyEntry(K key, V value){
		this.key = key;
		this.value = value;
	}
	@Override
	public K getKey() {
		return this.key;
	}

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public V setValue(V v) {
		V vOld = value;
		this.value = v;
		return vOld;
	}

}
