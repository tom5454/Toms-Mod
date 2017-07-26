package com.tom.apis;

public class Storage<V> implements IStorage<V> {
	public Storage() {
	}

	public Storage(V value) {
		this.value = value;
	}

	private V value;

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public void setValue(V newValue) {
		this.value = newValue;
	}

}
