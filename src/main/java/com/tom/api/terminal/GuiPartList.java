package com.tom.api.terminal;

import java.util.ArrayList;
import java.util.List;

import com.tom.apis.BigEntry;

public class GuiPartList {
	public List<BigEntry<String, Integer, Integer, Integer, Integer>> hitboxes = new ArrayList<BigEntry<String, Integer, Integer, Integer, Integer>>();
	public int xCoord, yCoord, zCoord;

	public GuiPartList(int x, int y, int z) {
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
	}

	public int cX, cY;
	public String currentHitbox;
	public boolean eEsc;
}
