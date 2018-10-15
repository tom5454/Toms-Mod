package com.tom.util;

public class ExtraBlockHitInfo {
	private float hitX, hitY, hitZ;

	public ExtraBlockHitInfo(float hitX, float hitY, float hitZ) {
		this.hitX = hitX;
		this.hitY = hitY;
		this.hitZ = hitZ;
	}

	public float getHitX() {
		return hitX;
	}

	public void setHitX(float hitX) {
		this.hitX = hitX;
	}

	public float getHitY() {
		return hitY;
	}

	public void setHitY(float hitY) {
		this.hitY = hitY;
	}

	public float getHitZ() {
		return hitZ;
	}

	public void setHitZ(float hitZ) {
		this.hitZ = hitZ;
	}
}
