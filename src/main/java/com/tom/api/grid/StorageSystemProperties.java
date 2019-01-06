package com.tom.api.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;

import com.tom.api.grid.StorageNetworkGrid.IControllerTile;
import com.tom.api.item.IControllerBoard;
import com.tom.api.item.IFan;
import com.tom.api.item.IHeatSink;
import com.tom.api.item.IMemoryItem;
import com.tom.api.item.IProcessor;

@SuppressWarnings("unused")
public interface StorageSystemProperties extends ITickable {
	MemoryProperties[] getMaxMemory();

	CoreProperties[] getCores();

	int getMaxCompatibleTowers();

	int getMaxChannels();

	int getMaxAutoCrafting();

	int getMaxAutoCraftingOperations();

	int getMaxAutoCraftingStorage();

	int getMaxStorage();

	boolean addTask(Task t);

	public static class DefaultStorageSystemProperties implements StorageSystemProperties {
		private Board b = new DummyBoard();
		private MemoryProperties mem = new MemoryProperties(b, 1);
		private MemoryProperties[] memory = new MemoryProperties[]{mem};
		private CoreProperties core = new CoreProperties(b, 1);
		private CoreProperties[] cores = new CoreProperties[]{core};
		private int memoryUsed;

		@Override
		public MemoryProperties[] getMaxMemory() {
			return memory;
		}

		@Override
		public CoreProperties[] getCores() {
			return cores;
		}

		@Override
		public int getMaxCompatibleTowers() {
			return 0;
		}

		@Override
		public int getMaxChannels() {
			return 16;
		}

		@Override
		public int getMaxAutoCrafting() {
			return 0;
		}

		@Override
		public int getMaxAutoCraftingOperations() {
			return 0;
		}

		@Override
		public int getMaxAutoCraftingStorage() {
			return 0;
		}

		@Override
		public int getMaxStorage() {
			return 4096;
		}

		@Override
		public boolean addTask(Task t) {
			return false;
		}

		@Override
		public void update() {

		}

	}

	public static class ControllerStorageSystemProperties implements StorageSystemProperties {
		private final IControllerTile c;

		public ControllerStorageSystemProperties(IControllerTile c) {
			this.c = c;
		}

		@Override
		public MemoryProperties[] getMaxMemory() {
			return null;
		}

		@Override
		public CoreProperties[] getCores() {
			return null;
		}

		@Override
		public int getMaxCompatibleTowers() {
			return 0;
		}

		@Override
		public int getMaxChannels() {
			return 0;
		}

		@Override
		public int getMaxAutoCrafting() {
			return 0;
		}

		@Override
		public int getMaxAutoCraftingOperations() {
			return 0;
		}

		@Override
		public int getMaxAutoCraftingStorage() {
			return 0;
		}

		@Override
		public int getMaxStorage() {
			return 0;
		}

		@Override
		public boolean addTask(Task t) {
			return false;
		}

		@Override
		public void update() {

		}
	}

	public static class CoreProperties {
		public CoreProperties(Board b, int core) {

		}
	}

	public static class MemoryProperties {
		public MemoryProperties(Board board, int slot) {

		}
	}

	public static class DummyBoard extends Board {
		private static final ItemStack[] EMPTY = new ItemStack[0];

		public DummyBoard() {
			super(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, EMPTY, ItemStack.EMPTY);
		}

	}

	public static class Board {
		private final ItemStack board, processor, heatSink, fan, chipset;
		private final ItemStack[] memory;

		public Board(ItemStack board, ItemStack processor, ItemStack heatSink, ItemStack fan, ItemStack[] memory, ItemStack chipset) {
			this.board = board;
			this.processor = processor;
			this.heatSink = heatSink;
			this.fan = fan;
			this.chipset = chipset;
			this.memory = memory;
		}

		public MemoryProperties[] getMemory() {
			if (board.getItem() instanceof IControllerBoard) {
				IControllerBoard b = (IControllerBoard) board.getItem();
				int memory = b.getMaxMemorySlots(board);
				int[] types = b.getCompatibleMemorySlotTypes(board);
				List<MemoryProperties> list = new ArrayList<>();
				for (int i = 0;i < memory && i < this.memory.length;i++) {
					ItemStack m = this.memory[i];
					if (m.getItem() instanceof IMemoryItem) {
						IMemoryItem mem = (IMemoryItem) m.getItem();
						int t = mem.getTier(m);
						if (Arrays.binarySearch(types, t) != -1) {
							list.add(new MemoryProperties(this, i));
						}
					}
				}
				return list.toArray(new MemoryProperties[0]);
			} else
				return new MemoryProperties[0];
		}

		public CoreProperties[] getCores() {
			if (board.getItem() instanceof IControllerBoard && processor.getItem() instanceof IProcessor && heatSink.getItem() instanceof IHeatSink && fan.getItem() instanceof IFan) {
				IControllerBoard b = (IControllerBoard) board.getItem();
				IProcessor p = (IProcessor) processor.getItem();
				int[] types = b.getCompatibleProcessorSlotTypes(board);
				int t = p.getProcessorTier(processor);
				if (Arrays.binarySearch(types, t) != -1) {
					int cores = p.getCoreCount(processor);
					CoreProperties[] ret = new CoreProperties[cores];
					for (int i = 0;i < cores;i++)
						ret[i] = new CoreProperties(this, i);
					return ret;
				}
			}
			return new CoreProperties[0];
		}
	}

	public static interface Task extends Runnable {
		int getOperations();

		int getMemoryUsage();
	}
}