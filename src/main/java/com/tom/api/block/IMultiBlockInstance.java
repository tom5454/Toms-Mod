package com.tom.api.block;

import net.minecraft.block.Block;

public interface IMultiBlockInstance extends ICustomItemBlock{
	Block[] getBlocks();
}
