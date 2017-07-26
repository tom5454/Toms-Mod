package com.tom.api.grid;

public interface IGridAccess<G extends IGrid<?, G>> {
	G getGrid();
}