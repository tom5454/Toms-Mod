package com.tom.apis;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Counter implements Runnable, Callable<Integer>, Supplier<Integer> {
	private int count;

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
