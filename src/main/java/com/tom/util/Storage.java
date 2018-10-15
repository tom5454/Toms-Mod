package com.tom.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Storage<V> implements Consumer<V>, Supplier<V> {
	public Storage() {
	}

	public Storage(V value) {
		this.value = value;
	}

	private V value;

	@Override
	public V get() {
		return value;
	}

	@Override
	public void accept(V newValue) {
		this.value = newValue;
	}

}
