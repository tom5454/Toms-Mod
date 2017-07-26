package com.tom.api.terminal;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import io.netty.buffer.ByteBuf;

public class Terminal extends TerminalBase {
	public Terminal(int windows) {
		this.window = new TerminalWidow[windows];
		for (int i = 0;i < windows;i++) {
			this.window[i] = new TerminalWidow();
		}
	}

	public TerminalWidow[] window;

	public void writeToTerm(int id, String s, int color) {
		this.window[id].write(s, color);
	}

	public void printToTerm(int id, String s, int color) {
		this.window[id].print(s, color);
	}

	public void clearTerm(int id) {
		this.window[id].clear();
	}

	public void renderPictureInTerm(int id, String s) {
		this.window[id].renderPicture(s);
	}

	public void writeToPacket(ByteBuf buf) {
		buf.writeInt(this.window.length);
		super.writeToPacket(buf);
		for (TerminalWidow t : this.window) {
			t.writeToPacket(buf);
		}
	}

	public void readFromPacket(ByteBuf buf) {
		int lenth = buf.readInt();
		this.window = new TerminalWidow[lenth];
		super.readFromPacket(buf);
		for (int i = 0;i < lenth;i++) {
			this.window[i] = new TerminalWidow();
			this.window[i].readFromPacket(buf);
		}
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("cursor", this.cursor);
		tag.setInteger("curX", this.curPosX);
		tag.setInteger("curY", this.curPosY);
		tag.setInteger("curWX", this.curPosWX);
		tag.setInteger("curWY", this.curPosWY);
		tag.setString("n", this.name);
		tag.setBoolean("wm", this.writeMode);
		tag.setString("in", this.inputText);
		NBTTagList list = new NBTTagList();
		for (TerminalObject tO : this.term)
			list.appendTag(tO.exportToNBT());
		tag.setTag("l", list);
		return tag;
	}
}
