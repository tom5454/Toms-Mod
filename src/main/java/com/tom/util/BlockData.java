package com.tom.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.core.tileentity.TileEntityHidden.BlockProperties;

public class BlockData {
	private final Block[] blocks;
	private final int[] metas;
	private final BlockProperties[] properties;

	/*public BlockData(IBlockState state) {
		blocks = new Block[]{state.getBlock()};
		metas = new int[]{state.getBlock().getMetaFromState(state)};
		hatch = new int[]{-1};
		properties = new BlockProperties[]{null};
	}
	public BlockData(Block block){
		blocks = new Block[]{block};
		metas = new int[]{-1};
		hatch = new int[]{-1};
	}
	public BlockData(Object[] in){
		blocks = new Block[in.length];
		metas = new int[in.length];
		hatch = new int[in.length];
		for(int i = 0;i<in.length;i++){
			Object o = in[i];
			if(o instanceof Block){
				blocks[i] = (Block) o;
				metas[i] = -1;
			}else if(o instanceof IBlockState){
				IBlockState s = (IBlockState) o;
				blocks[i] = s.getBlock();
				metas[i] = s.getBlock().getMetaFromState(s);
			}else if(o instanceof Object[]){
				Object[] o2 = (Object[]) o;
				blocks[i] = (Block) o2[0];
				metas[i] = (int) o2[1];
			}else throw new IllegalArgumentException("Invalid Value in the input array at " + i);
		}
	}*/
	public BlockData(Object stateO) {
		if (stateO instanceof Object[]) {
			Object[] o = (Object[]) stateO;
			/*Block[] blocks = new Block[o.length];
			int[] metas = new int[o.length];
			BlockProperties[] properties = new BlockProperties[o.length];*/
			List<BlockP> blockp = new ArrayList<>();
			for (int i = 0;i < o.length;i++) {
				if (o[i] instanceof BlockProperties) {
					getOrAdd(blockp).prop = (BlockProperties) o[i];
				} else if (o[i] instanceof IBlockState) {
					IBlockState s = (IBlockState) o[i];
					BlockP pr = new BlockP(s.getBlock(), s.getBlock().getMetaFromState(s));
					blockp.add(pr);
				} else if (o[i] instanceof Integer) {
					getOrAdd(blockp).meta = (int) o[i];
				} else if (o[i] instanceof Block) {
					BlockP pr = new BlockP((Block) o[i], -1);
					blockp.add(pr);
				} else if (o[i] instanceof Object[]) {
					Object[] o2 = (Object[]) o[i];
					BlockP pr = new BlockP(null, -1);
					for (int j = 0;j < o2.length;j++) {
						if (o2[j] instanceof BlockProperties) {
							pr.prop = (BlockProperties) o2[j];
						} else if (o2[j] instanceof Block) {
							pr.block = (Block) o2[j];
						} else if (o2[j] instanceof IBlockState) {
							IBlockState s = (IBlockState) o2[j];
							pr.block = s.getBlock();
							pr.meta = s.getBlock().getMetaFromState(s);
						} else if (o2[j] instanceof Integer) {
							pr.meta = (int) o2[j];
						}
					}
					blockp.add(pr);
				}
			}
			blocks = new Block[blockp.size()];
			metas = new int[blockp.size()];
			properties = new BlockProperties[blockp.size()];
			for (int i = 0;i < blockp.size();i++) {
				BlockP pr = blockp.get(i);
				blocks[i] = pr.block;
				metas[i] = pr.meta;
				properties[i] = pr.prop;
			}
		} else if (stateO instanceof IBlockState) {
			IBlockState s = (IBlockState) stateO;
			blocks = new Block[]{s.getBlock()};
			metas = new int[]{s.getBlock().getMetaFromState(s)};
			properties = new BlockProperties[]{null};
		} else {
			blocks = new Block[]{(Block) stateO};
			metas = new int[]{-1};
			properties = new BlockProperties[]{null};
		}
	}

	private static BlockP getOrAdd(List<BlockP> blockp) {
		if (blockp.isEmpty()) {
			BlockP b = new BlockP(null, -1);
			blockp.add(b);
			return b;
		} else {
			return blockp.get(0);
		}
	}

	public BlockData(Block[] blocks, int[] metas, BlockProperties[] props) {
		this.blocks = blocks;
		this.metas = metas;
		this.properties = props;
	}

	public boolean matches(Block block, int m) {
		for (int i = 0;i < blocks.length;i++) {
			if (block == blocks[i] && (metas[i] == -1 || metas[i] == m))
				return true;
		}
		return false;
	}

	public BlockProperties getProperties(Block block, int m) {
		for (int i = 0;i < blocks.length;i++) {
			if (block == blocks[i] && (metas[i] == -1 || metas[i] == m))
				return properties[i];
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(blocks);
		result = prime * result + Arrays.hashCode(metas);
		result = prime * result + Arrays.hashCode(properties);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockData other = (BlockData) obj;
		if (!Arrays.equals(blocks, other.blocks))
			return false;
		if (!Arrays.equals(metas, other.metas))
			return false;
		if (!Arrays.equals(properties, other.properties))
			return false;
		return true;
	}

	@SuppressWarnings("deprecation")
	public List<ItemStack> toStackList() {
		List<ItemStack> stacks = new ArrayList<>();
		for (int i = 0;i < blocks.length;i++) {
			if (blocks[i] != null) {
				if (metas[i] >= 0) {
					IBlockState state = blocks[i].getStateFromMeta(metas[i]);
					stacks.add(new ItemStack(blocks[i].getItemDropped(state, new Random(), 0), 1, blocks[i].damageDropped(state)));
				} else {
					stacks.add(new ItemStack(blocks[i]));
				}
			}
		}
		return stacks;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("prop", TomsModUtils.writeCollection(Arrays.asList(properties), BlockProperties::writeToNew));
		tag.setIntArray("metas", metas);
		tag.setTag("blocks", TomsModUtils.writeCollection(Arrays.asList(blocks), TomsModUtils::writeBlock));
		return tag;
	}

	public static BlockData load(NBTTagCompound tag) {
		List<Block> blocks = new ArrayList<>();
		List<BlockProperties> props = new ArrayList<>();
		TomsModUtils.readCollection(blocks, tag.getTagList("blocks", 10), TomsModUtils::readBlock);
		TomsModUtils.readCollection(props, tag.getTagList("props", 10), BlockProperties::load);
		return new BlockData(blocks.toArray(new Block[0]), tag.getIntArray("metas"), props.toArray(new BlockProperties[0]));
	}

	@SuppressWarnings("deprecation")
	public IBlockState getState(int i, int c) {
		return metas[i] != -1 ? blocks[i].getStateFromMeta(metas[i]) : blocks[i].getStateFromMeta(c);
	}

	private static class BlockP {
		Block block;
		int meta;
		BlockProperties prop;

		public BlockP(Block block, int meta, BlockProperties prop) {
			this.block = block;
			this.meta = meta;
			this.prop = prop;
		}

		public BlockP(Block block, int meta) {
			this.block = block;
			this.meta = meta;
		}
	}
}
