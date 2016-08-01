package com.tom.apis;

public interface Function<T,R> {
	R apply(T t);
	public static interface BiFunction<T,U,R>{
		R apply(T t, U u);
	}
}
