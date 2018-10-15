package com.tom.util;

import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class IDList<V> {
	private BiMap<Long, V> values = HashBiMap.create();
	private long id;
	public long put(V val){
		if(values.containsValue(val))return values.inverse().getOrDefault(val, -1L);
		else{
			long id = newId();
			values.put(id, val);
			return id;
		}
	}
	private long newId(){
		return id++;
	}
	public void remove(long id){
		values.remove(id);
	}
	public void remove(V val){
		values.inverse().remove(val);
	}
	public void clear() {
		values.clear();
	}
	public boolean contains(V luaText) {
		return values.containsValue(luaText);
	}
	public Set<V> values() {
		return values.values();
	}
	public long getIDFor(V v){
		return values.inverse().getOrDefault(v, -1L);
	}
	public boolean isEmpty() {
		return values.isEmpty();
	}
	public int size() {
		return values.size();
	}
	public V get(long id) {
		return values.get(id);
	}
}
