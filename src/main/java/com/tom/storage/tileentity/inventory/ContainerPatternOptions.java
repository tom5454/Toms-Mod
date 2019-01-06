package com.tom.storage.tileentity.inventory;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.inventory.IJEIAutoFillTerminal;
import com.tom.api.inventory.SlotPhantom;
import com.tom.api.tileentity.IPatternTerminal;
import com.tom.storage.tileentity.inventory.ContainerBlockInterface.SlotPattern;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.api.slot.EnumFaceSlot;
import mcmultipart.api.slot.IPartSlot;

public class ContainerPatternOptions extends ContainerTomsMod implements IJEIAutoFillTerminal {
	public IPatternTerminal te;

	public ContainerPatternOptions(InventoryPlayer playerInv, IPatternTerminal te) {
		this.te = te;
		init(playerInv, te);
	}

	public ContainerPatternOptions(EntityPlayer player, World world, BlockPos createBlockPos, int s) {
		TileEntity tile = world.getTileEntity(createBlockPos);
		if (tile != null && tile instanceof IPatternTerminal) {
			te = (IPatternTerminal) tile;
		} else {
			IPartSlot slot = s == -1 ? EnumCenterSlot.CENTER : EnumFaceSlot.fromFace(EnumFacing.VALUES[s]);
			IMultipartContainer container = MultipartHelper.getContainer(world, createBlockPos).orElse(null);
			if (container != null) {
				Optional<IPartInfo> part = container.get(slot);
				if (part.isPresent() && part.get().getTile() instanceof IPatternTerminal) {
					te = (IPatternTerminal) part.get().getTile();
				}
			}
		}
		if (te == null)
			return;
		init(player.inventory, te);
	}
	private void init(InventoryPlayer playerInv, IPatternTerminal te){
		for (int i = 0;i < 3;i++)
			for (int j = 0;j < 3;j++)
				addSlotToContainer(new SlotPhantom(te.getRecipeInv(), i * 3 + j, 35 + j * 18, 37 + i * 18, 64));
		for (int i = 0;i < 5;i++)
			addSlotToContainer(new SlotPhantom(te.getRecipeInv(), 9 + i, 17 + i * 18, 19, 64));
		for (int i = 0;i < 3;i++) {
			addSlotToContainer(new SlotPhantom(te.getRecipeInv(), 14 + i * 2, 17, 37 + i * 18, 64));
			addSlotToContainer(new SlotPhantom(te.getRecipeInv(), 15 + i * 2, 89, 37 + i * 18, 64));
		}
		for (int i = 0;i < 5;i++)
			addSlotToContainer(new SlotPhantom(te.getRecipeInv(), 20 + i, 17 + i * 18, 91, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 0, 145, 55, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 1, 145, 82, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 2, 170, 46, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 3, 188, 46, 64));
		for (int i = 0;i < 2;i++) {
			addSlotToContainer(new SlotPhantom(te.getResultInv(), 4 + i * 2, 170, 64 + i * 18, 64));
			addSlotToContainer(new SlotPhantom(te.getResultInv(), 5 + i * 2, 188, 64 + i * 18, 64));
		}
		addSlotToContainer(new SlotPattern(te.getPatternInv(), 0, 213, 175, false));
		addSlotToContainer(new SlotPattern(te.getPatternInv(), 1, 213, 213, true));
		addPlayerSlots(playerInv, 40, 166);
		syncHandler.registerBoolean(0, () -> te.getProperties().useContainerItems, b -> te.getProperties().useContainerItems = b);
		syncHandler.registerBoolean(1, () -> te.getProperties().storedOnly, b -> te.getProperties().storedOnly = b);
		syncHandler.registerShort(2, () -> te.getProperties().time, i -> te.getProperties().time = i);
		syncHandler.registerTag(0, this::writeToNBT, t -> {});
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te != null;
	}

	private void writeToNBT(NBTTagCompound tag) {
		NBTTagList list = new NBTTagList();
		tag.setBoolean("isp", true);
		for (int i = 0;i < te.getPropertiesLength();i++) {
			NBTTagCompound t = new NBTTagCompound();
			te.getPropertiesFor(i).writeToNBT(t);
			t.setByte("s", (byte) i);
			list.appendTag(t);
		}
		tag.setTag("l", list);
	}

	@Override
	public void sendMessage(NBTTagCompound tag) {
		te.sendUpdate(tag);
	}
}
