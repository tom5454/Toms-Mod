package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.tileentity.TileEntityTomsModNoTicking;
import com.tom.apis.BlockData;
import com.tom.core.CoreInit;

public class TileEntityTemplate extends TileEntityTomsModNoTicking {
	private BlockData data;
	private List<ItemStack> stacks = new ArrayList<>();
	private IBlockState state;
	private long last = 0;
	public int gllist = -1;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (data != null)
			data.writeToNBT(compound);
		compound.setBoolean("hasData", data != null);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.getBoolean("hasData")) {
			data = BlockData.load(compound);
			stacks = data.toStackList();
		} else {
			data = null;
		}
	}

	@Override
	public void writeToPacket(NBTTagCompound tag) {
		if (data != null)
			data.writeToNBT(tag);
		tag.setBoolean("hasData", data != null);
	}

	@Override
	public void readFromPacket(NBTTagCompound tag) {
		if (tag.getBoolean("hasData")) {
			data = BlockData.load(tag);
			stacks = data.toStackList();
		} else {
			data = null;
		}
	}

	public void setTemplate(BlockData data) {
		this.data = data;
		markBlockForUpdate();
	}

	public static boolean place(World world, BlockPos pos, BlockData data) {
		if (data != null && world.getBlockState(pos).getMaterial() == Material.AIR) {
			world.setBlockState(pos, CoreInit.blockTemplate.getDefaultState());
			TileEntityTemplate te = (TileEntityTemplate) world.getTileEntity(pos);
			te.setTemplate(data);
			return true;
		}
		return false;
	}

	public static void remove(World world, BlockPos pos) {
		if (pos != null && world.getBlockState(pos).getBlock() == CoreInit.blockTemplate) {
			world.setBlockToAir(pos);
		}
	}

	public BlockData getData() {
		return data;
	}

	public List<ItemStack> getStacks() {
		return stacks;
	}

	public IBlockState getState() {
		long c = world.getTotalWorldTime() / 40;
		if (last != c) {
			last = c;
			if (stacks.isEmpty())
				state = Blocks.STONE.getDefaultState();
			else
				state = data.getState((int) c % stacks.size(), (int) (world.getTotalWorldTime() / 15));
		}
		return state;
	}

	public boolean shouldRerender() {
		long c = world.getTotalWorldTime() % 40;
		return last != c;
	}

	public ItemStack getStack() {
		long c = world.getTotalWorldTime() / 40;
		return stacks.isEmpty() ? ItemStack.EMPTY : stacks.get((int) (c % stacks.size()));
	}
}
