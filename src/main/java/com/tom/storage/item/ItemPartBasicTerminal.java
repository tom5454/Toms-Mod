package com.tom.storage.item;

import net.minecraft.world.World;

import com.tom.storage.multipart.PartBasicTerminal;
import com.tom.storage.multipart.PartTerminal;

public class ItemPartBasicTerminal extends ItemPartTerminal {

	public ItemPartBasicTerminal() {
		super(0.35, 0.15);
	}

	@Override
	public PartTerminal createNewTileEntity(World worldIn, int meta) {
		return new PartBasicTerminal();
	}

	@Override
	public boolean hasCustomFront() {
		return false;
	}

	@Override
	public String getName() {
		return "Basic Part Terminal";
	}

	@Override
	public String getCategory() {
		return "basic";
	}

	@Override
	public int[][][] getImageIDs() {
		return new int[][][]{{{1, 0, 0}, {2, 0, -30}}, {{1, 0, -30}, {2, 0, -20}, {-1}, {4}, {5, 0, -100}}};
	}

}
