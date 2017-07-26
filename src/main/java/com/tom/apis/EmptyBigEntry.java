package com.tom.apis;

public class EmptyBigEntry<K, V1, V2, V3, V4> implements BigEntry<K, V1, V2, V3, V4> {
	K key;
	V1 v1;
	V2 v2;
	V3 v3;
	V4 v4;

	public EmptyBigEntry(K key, V1 v1, V2 v2, V3 v3, V4 v4) {
		this.key = key;
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;
	}

	public EmptyBigEntry() {
	}

	public EmptyBigEntry(K key) {
		this.key = key;
	}

	@Override
	public V1 getValue1() {
		return this.v1;
	}

	@Override
	public V2 getValue2() {
		return this.v2;
	}

	@Override
	public V3 getValue3() {
		return this.v3;
	}

	@Override
	public V4 getValue4() {
		return this.v4;
	}

	@Override
	public BigEntry<K, V1, V2, V3, V4> setValues(V1 v1, V2 v2, V3 v3, V4 v4) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;
		return this;
	}

	@Override
	public K getKey() {
		return this.key;
	}

	@Override
	public void setKey(K key) {
		this.key = key;
	}

}
