package com.tom.apis;

import java.util.function.Function;

public class MultiblockBlockChecker {
	private final Function<WorldPos, Integer> func;
	private final BlockData data;

	public MultiblockBlockChecker(Function<WorldPos, Integer> func, BlockData data) {
		this.func = func;
		this.data = data;
	}

	public int apply(WorldPos pos) {
		return func.apply(pos);
	}

	public BlockData getData() {
		return data;
	}
}