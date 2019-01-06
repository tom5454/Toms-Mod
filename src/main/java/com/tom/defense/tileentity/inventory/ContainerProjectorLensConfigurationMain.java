package com.tom.defense.tileentity.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.tom.core.CoreInit;
import com.tom.defense.DefenseInit;
import com.tom.defense.ProjectorLensConfigEntry;
import com.tom.defense.item.ItemProjectorFieldType.FieldType;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.lib.network.GuiSyncHandler.IPacketReceiver;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerProjectorLensConfigurationMain extends ContainerTomsMod implements IPacketReceiver {
	public ItemStack lensStack;
	public List<ProjectorLensConfigEntry> entryList = new ArrayList<>();
	private boolean sendUpdate;

	// private int extra = -1;
	public ContainerProjectorLensConfigurationMain(EntityPlayer player) {
		this.lensStack = DefenseInit.multiTool.getLensStack(player.getHeldItemMainhand(), player);
		this.addPlayerSlotsExceptHeldItem(player.inventory, 8, 94);
		if (player.getHeldItemMainhand().getTagCompound() == null)
			player.getHeldItemMainhand().setTagCompound(new NBTTagCompound());
		/*NBTTagList list = player.getCurrentEquippedItem().getTagCompound().getTagList("inventory", 10);
		ItemStack[] stack = new ItemStack[3];
		for (int k = 0; k < list.tagCount(); ++k)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(k);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < stack.length)
			{
				stack[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		list = new NBTTagList();
		for(int i = 0;i<this.entryList.size();i++){
			if(!(extra >= 0 && i == extra)){
				NBTTagCompound tag = new NBTTagCompound();
				entryList.get(i).writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		lensStack.getTagCompound().setTag("entries", list);
		list = new NBTTagList();
		stack[2] = lensStack;
		for(int j = 0;j<stack.length;j++){
			if(stack[j] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[j].writeToNBT(tag);
				tag.setByte("Slot", (byte) j);
				list.appendTag(tag);
			}
		}
		if(!player.getCurrentEquippedItem().hasTagCompound())player.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());
		player.getCurrentEquippedItem().getTagCompound().setTag("inventory", list);*/
		entryList.clear();
		if (lensStack.getTagCompound() == null)
			lensStack.setTagCompound(new NBTTagCompound());
		{
			NBTTagList list = lensStack.getTagCompound().getTagList("entries", 10);
			NBTTagCompound extraTag = lensStack.getTagCompound().getCompoundTag("extra");
			boolean hasExtra = lensStack.getTagCompound().getBoolean("hasExtra");
			int pos = extraTag.getInteger("pos");
			for (int i = 0;i < list.tagCount();i++) {
				NBTTagCompound tag = list.getCompoundTagAt(i);
				if (hasExtra && i == pos)
					entryList.add(ProjectorLensConfigEntry.fromNBT(extraTag));
				else
					entryList.add(ProjectorLensConfigEntry.fromNBT(tag));
			}
		}
		lensStack.getTagCompound().setBoolean("hasExtra", false);
		this.sendUpdate = true;
		DefenseInit.multiTool.setLensStack(player.getHeldItemMainhand(), lensStack);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (this.sendUpdate) {
			NBTTagList list = new NBTTagList();
			for (int i = 0;i < this.entryList.size();i++) {
				NBTTagCompound tag = new NBTTagCompound();
				entryList.get(i).writeToClientNBTPacket(tag);
				list.appendTag(tag);
			}
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("l", list);
			syncHandler.sendNBTToGui(tag);
			this.sendUpdate = false;
		}
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (id == 0) {
			this.entryList.add(ProjectorLensConfigEntry.createNew());
			this.sendUpdate = true;
			if (lensStack.getTagCompound() == null)
				lensStack.setTagCompound(new NBTTagCompound());
			NBTTagList list = new NBTTagList();
			for (int i = 0;i < this.entryList.size();i++) {
				// if(!(extra >= 0 && i == extra)){
				NBTTagCompound tag = new NBTTagCompound();
				entryList.get(i).writeToNBT(tag);
				list.appendTag(tag);
				// }
			}
			lensStack.getTagCompound().setTag("entries", list);
			DefenseInit.multiTool.setLensStack(player.getHeldItemMainhand(), lensStack);
		} else if (id == 1) {
			player.openGui(CoreInit.modInstance, GuiIDs.projectorLensConfig.ordinal(), player.world, extra, 0, 0);
			// this.extra = extra;
			// DefenseInit.multiTool.setOpenGuiNextTick(player.getCurrentEquippedItem(),
			// null);
		} else if (id == 2) {
			if (this.entryList.size() > extra) {
				ProjectorLensConfigEntry r = this.entryList.remove(extra);
				if (r != null) {
					r.dropItems(player);
				}
				this.sendUpdate = true;
				if (lensStack.getTagCompound() == null)
					lensStack.setTagCompound(new NBTTagCompound());
				NBTTagList list = new NBTTagList();
				for (int i = 0;i < this.entryList.size();i++) {
					// if(!(extra >= 0 && i == extra)){
					NBTTagCompound tag = new NBTTagCompound();
					entryList.get(i).writeToNBT(tag);
					list.appendTag(tag);
					// }
				}
				lensStack.getTagCompound().setTag("entries", list);
				DefenseInit.multiTool.setLensStack(player.getHeldItemMainhand(), lensStack);
			}
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		/*if(playerIn.getCurrentEquippedItem().getTagCompound() == null)playerIn.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());
		NBTTagList list = playerIn.getCurrentEquippedItem().getTagCompound().getTagList("inventory", 10);
		ItemStack[] stack = new ItemStack[3];
		for (int k = 0; k < list.tagCount(); ++k)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(k);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < stack.length)
			{
				stack[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		stack[2] = lensStack;
		list = new NBTTagList();
		for(int j = 0;j<stack.length;j++){
			if(stack[j] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[j].writeToNBT(tag);
				tag.setByte("Slot", (byte) j);
				list.appendTag(tag);
			}
		}
		if(!playerIn.getCurrentEquippedItem().hasTagCompound())playerIn.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());
		playerIn.getCurrentEquippedItem().getTagCompound().setTag("inventory", list);*/
		if (lensStack.getTagCompound() == null)
			lensStack.setTagCompound(new NBTTagCompound());
		NBTTagList list = new NBTTagList();
		for (int i = 0;i < this.entryList.size();i++) {
			// if(!(extra >= 0 && i == extra)){
			NBTTagCompound tag = new NBTTagCompound();
			entryList.get(i).writeToNBT(tag);
			list.appendTag(tag);
			// }
		}
		lensStack.getTagCompound().setTag("entries", list);
		DefenseInit.multiTool.setLensStack(playerIn.getHeldItemMainhand(), lensStack);
	}

	public static class ContainerProjectorLensConfig extends ContainerTomsMod implements IPacketReceiver {
		public ProjectorLensConfigEntry entry;
		private EntityPlayer player;
		private final int id;
		private boolean sendUpdate;

		public ContainerProjectorLensConfig(ProjectorLensConfigEntry entry, EntityPlayer player, int id) {
			this.entry = entry;
			this.id = id;
			this.player = player;
			this.addSlotToContainer(new SlotProjectorModule(0, 43, 32));
			this.addSlotToContainer(new SlotUpgradeWidth(0, 25, 74));
			this.addSlotToContainer(new SlotUpgradeWidth(1, 61, 74));
			this.addSlotToContainer(new SlotUpgradeHeight(2, 43, 74));
			for (int i = 0;i < 6;++i) {
				for (int j = 0;j < 4;++j) {
					addSlotToContainer(new SlotUpgrade(j + i * 4 + 3, 97 + i * 18, 20 + j * 18));
				}
			}
			this.addPlayerSlotsExceptHeldItem(player.inventory, 25, 95);
			this.sendUpdate = true;
		}

		@Override
		public void buttonPressed(EntityPlayer player, int id, int extra) {
			if (id == 0)
				entry.setOffsetX(extra);
			else if (id == 1)
				entry.setOffsetY(extra);
			else if (id == 2)
				entry.setOffsetZ(extra);
			this.sendUpdate = true;
		}

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return true;
		}

		@Override
		public void onContainerClosed(EntityPlayer playerIn) {
			super.onContainerClosed(playerIn);
			if (playerIn.getHeldItemMainhand() != null && playerIn.getHeldItemMainhand().getItem() == DefenseInit.multiTool) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("pos", id);
				this.entry.writeToNBT(tag);
				if (!playerIn.world.isRemote)
					DefenseInit.multiTool.setOpenGuiNextTick(playerIn.getHeldItemMainhand(), tag);
			}
		}

		public class SlotProjectorModule extends Slot {
			private boolean lastValid;

			public SlotProjectorModule(int index, int xPosition, int yPosition) {
				super(entry.getInventory(false), index, xPosition, yPosition);
				lastValid = this.isItemValid(getStack());
			}

			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack != null && stack.getItem() == DefenseInit.projectorFieldType;
			}

			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				if (!this.isItemValid(getStack())) {
					entry.dropUpgrades(player);
				} else if (!lastValid) {

				}
			}
		}

		public class SlotUpgrade extends Slot {

			public SlotUpgrade(int index, int xPosition, int yPosition) {
				super(entry.getInventory(true), index, xPosition, yPosition);
			}

			@Override
			public boolean isHere(IInventory inv, int slotIn) {
				return inv == this.inventory && slotIn == this.getSlotIndex() && isValid();
			}

			@Override
			public boolean isItemValid(ItemStack stack) {
				return isValid() && stack != null && stack.getItem() == DefenseInit.fieldUpgrade && entry.getFieldType() != null;
			}

			private boolean isValid() {
				int index = this.getSlotIndex() - 3;
				FieldType t = entry.getFieldType();
				if (t == null)
					return false;
				int walls = t.getWalls();
				int column = index / 4;
				return walls > column;
			}
		}

		public class SlotUpgradeWidth extends Slot {

			public SlotUpgradeWidth(int index, int xPosition, int yPosition) {
				super(entry.getInventory(true), index, xPosition, yPosition);
			}

			@Override
			public boolean isItemValid(ItemStack stack) {
				int o1 = getSlotIndex() == 0 ? 2 : 0;
				int o2 = getSlotIndex() == 1 ? 2 : 1;
				return stack != null && stack.getItem() == DefenseInit.rangeWidthUpgrade && entry.getFieldType() != null && (entry.getFieldType().is2DOnly() ? (inventory.getStackInSlot(o1) == null || inventory.getStackInSlot(o2) == null) : true);
			}
		}

		public class SlotUpgradeHeight extends Slot {

			public SlotUpgradeHeight(int index, int xPosition, int yPosition) {
				super(entry.getInventory(true), index, xPosition, yPosition);
			}

			@Override
			public boolean isItemValid(ItemStack stack) {
				int o1 = getSlotIndex() == 0 ? 2 : 0;
				int o2 = getSlotIndex() == 1 ? 2 : 1;
				return stack != null && stack.getItem() == DefenseInit.rangeHeightUpgrade && entry.getFieldType() != null && (entry.getFieldType().is2DOnly() ? (inventory.getStackInSlot(o1) == null || inventory.getStackInSlot(o2) == null) : true);
			}
		}

		@Override
		public void detectAndSendChanges() {
			super.detectAndSendChanges();
			if (sendUpdate) {
				NBTTagCompound tag = new NBTTagCompound();
				entry.writeToClientNBTPacket(tag);
				syncHandler.sendNBTToGui(tag);
				this.sendUpdate = false;
			}
		}

		@Override
		public void receiveNBTPacket(EntityPlayer pl, NBTTagCompound message) {
			String s = message.getString("s");
			entry.setName(s);
		}
	}

	@Override
	public void receiveNBTPacket(EntityPlayer pl, NBTTagCompound message) {
		NBTTagList list = message.getTagList("l", 10);
		entryList.clear();
		for (int i = 0;i < list.tagCount();i++) {
			entryList.add(ProjectorLensConfigEntry.fromNBTClient(list.getCompoundTagAt(i)));
		}
	}
}
