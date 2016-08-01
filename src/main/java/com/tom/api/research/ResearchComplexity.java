package com.tom.api.research;

public enum ResearchComplexity {
	BASIC("tomsMod.research.basic"), ADVANCED("tomsMod.research.adv"), LABORATORY("tomsMod.research.lab")
	;
	private String name;
	private ResearchComplexity(String name){
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}
}
