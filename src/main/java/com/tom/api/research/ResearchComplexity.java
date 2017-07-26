package com.tom.api.research;

public enum ResearchComplexity {
	BASIC("tomsMod.research.basic", 0, 0), BRONZE("tomsMod.reserach.bronze", 0, 1), ELECTRICAL("tomsMod.research.electrical", 0, 2), MV("tomsMod.research.mv", 0, 3), ADVANCED("tomsMod.research.adv", 0, 3), LABORATORY("tomsMod.research.lab", 1, 0);
	private final String name;
	private final int type, lvl;

	private ResearchComplexity(String name, int type, int lvl) {
		this.name = name;
		this.type = type;
		this.lvl = lvl;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isEqual(ResearchComplexity other) {
		return type == other.type && lvl <= other.lvl;
	}
}
