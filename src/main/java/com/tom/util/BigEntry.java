package com.tom.util;

public interface BigEntry<K, V1, V2, V3, V4> {
	public V1 getValue1();

	public V2 getValue2();

	public V3 getValue3();

	public V4 getValue4();

	public BigEntry<K, V1, V2, V3, V4> setValues(V1 v1, V2 v2, V3 v3, V4 v4);

	public K getKey();

	public void setKey(K key);
}
