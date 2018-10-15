package com.tom.util;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Counter implements Runnable, Callable<Integer>, Supplier<Integer> {
	private int count;

	public Counter() {}

	public Counter(int count) {
		this.count = count;
	}

	@Override
	public void run() {
		increaseCount();
	}

	public int increaseCount() {
		return count++;
	}

	@Override
	public Integer call() throws Exception {
		return increaseCount();
	}

	public int getCount() {
		return count;
	}

	@Override
	public Integer get() {
		return count;
	}

	public void increaseCount(int amount) {
		count += amount;
	}
}
