package mapwriterTm.region;

import net.minecraft.block.state.IBlockState;

public interface IChunk {
	public IBlockState getBlockState(int x, int y, int z);

	public byte getBiome(int x, int z);

	public int getLightValue(int x, int y, int z);

	public int getMaxY();
}
