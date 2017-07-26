package com.tom.apis;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IStorage<V> extends Consumer<V>, Supplier<V> {
	V getValue();

	void setValue(V newValue);

	@Override
	default void accept(V t) {
		setValue(t);
	}

	@Override
	default V get() {
		return getValue();
	}
}
