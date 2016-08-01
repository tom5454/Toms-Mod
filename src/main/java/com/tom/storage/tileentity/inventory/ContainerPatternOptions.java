package com.tom.storage.tileentity.inventory;

import com.tom.api.inventory.IJEIAutoFillTerminal;
import com.tom.api.inventory.SlotPhantom;
import com.tom.api.tileentity.IPatternTerminal;
import com.tom.core.tileentity.inventory.ContainerTomsMod;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.storage.tileentity.inventory.ContainerBlockInterface.SlotPattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerPatternOptions extends ContainerTomsMod implements IJEIAutoFillTerminal{
	protected IPatternTerminal te;
	private int lastTime = -1, lastBucket = -1, lastStoredOnly = -1;
	private NBTTagCompound tagLast;
	public ContainerPatternOptions(InventoryPlayer playerInv, IPatternTerminal te) {
		this.te = te;
		for(int i = 0;i<3;i++)
			for(int j = 0;j<3;j++)
				addSlotToContainer(new SlotPhantom(te.getRecipeInv(), i * 3 + j, 35 + j * 18, 37 + i * 18, 64));
		for(int i = 0;i<5;i++)addSlotToContainer(new SlotPhantom(te.getRecipeInv(), 9 + i, 17 + i * 18, 19, 64));
		for(int i = 0;i<3;i++){
			addSlotToContainer(new SlotPhantom(te.getRecipeInv(), 14 + i * 2, 17, 37 + i * 18, 64));
			addSlotToContainer(new SlotPhantom(te.getRecipeInv(), 15 + i * 2, 89, 37 + i * 18, 64));
		}
		for(int i = 0;i<5;i++)addSlotToContainer(new SlotPhantom(te.getRecipeInv(), 20 + i, 17 + i * 18, 91, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 0, 145, 55, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 1, 145, 82, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 2, 170, 46, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 3, 188, 46, 64));
		for(int i = 0;i<2;i++){
			addSlotToContainer(new SlotPhantom(te.getResultInv(), 4 + i * 2, 170, 64 + i * 18, 64));
			addSlotToContainer(new SlotPhantom(te.getResultInv(), 5 + i * 2, 188, 64 + i * 18, 64));
		}
		addSlotToContainer(new SlotPattern(te.getPatternInv(), 0, 213, 175, false));
		addSlotToContainer(new SlotPattern(te.getPatternInv(), 1, 213, 213, true));
		addPlayerSlots(playerInv, 40, 166);
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int bucket = te.getProperties().useContainerItems ? 1 : 0, storedOnly = te.getProperties().storedOnly ? 1 : 0, time = te.getProperties().time;
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		boolean equals = tag.equals(tagLast);
		for(IContainerListener crafter : listeners){
			if(bucket != lastBucket)crafter.sendProgressBarUpdate(this, 0, bucket);
			if(storedOnly != lastStoredOnly)crafter.sendProgressBarUpdate(this, 1, storedOnly);
			if(time != lastTime)crafter.sendProgressBarUpdate(this, 2, time);
			if(!equals)sendTo(crafter, tag);
		}
		lastBucket = bucket;
		lastStoredOnly = storedOnly;
		lastTime = time;
		tagLast = tag;
	}
	private static void sendTo(IContainerListener l, NBTTagCompound tag){
		NetworkHandler.sendTo(new MessageNBT(tag), (EntityPlayerMP) l);
	}
	private void writeToNBT(NBTTagCompound tag){
		NBTTagList list = new NBTTagList();
		tag.setBoolean("isp", true);
		for(int i = 0;i<te.getPropertiesLength();i++){
			NBTTagCompound t = new NBTTagCompound();
			te.getPropertiesFor(i).writeToNBT(t);
			t.setByte("s", (byte) i);
			list.appendTag(t);
		}
		tag.setTag("l", list);
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		if(id == 0){
			te.getProperties().useContainerItems = data == 1;
		}else if(id == 1){
			te.getProperties().storedOnly = data == 1;
		}else if(id == 2){
			te.getProperties().time = data;
		}
	}
	@Override
	public void sendMessage(NBTTagCompound tag) {
		NetworkHandler.sendToServer(new MessageNBT(tag, te.getPos2()));
	}
}
