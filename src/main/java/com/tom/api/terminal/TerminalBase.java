package com.tom.api.terminal;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class TerminalBase {
	public List<TerminalObject> term = new ArrayList<TerminalObject>();
	public void clear(){
		this.term.clear();
	}
	public boolean cursor = false;
	public int curPosX = 0;
	public int curPosY = 0;
	public int curPosWX = 0;
	public int curPosWY = 0;
	public String name = "Terminal";
	public String inputText = "";
	public boolean writeMode = false;
	public void print(String s, int color){
		this.term.add(new TerminalObject(this.curPosX, this.curPosY, s, color));
		this.curPosX = 1;
		this.curPosY = this.curPosY+9;
	}
	public void write(String s, int color){
		this.term.add(new TerminalObject(this.curPosX, this.curPosY, s, color));
		this.curPosX = (this.curPosX + s.length() * 6) - 1;
	}
	public void renderPicture(String s){
		this.term.add(new TerminalObject(curPosX, curPosY, s));
	}
	public void writeToPacket(ByteBuf buf){
		buf.writeBoolean(cursor);
		buf.writeInt(curPosX);
		buf.writeInt(curPosY);
		buf.writeInt(this.term.size());
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("n", this.name);
		NBTTagList list = new NBTTagList();
		for(TerminalObject tO : this.term)
			list.appendTag(tO.exportToNBT());
		tag.setTag("l",list);
		ByteBufUtils.writeTag(buf, tag);
	}
	public void readFromPacket(ByteBuf buf){
		this.cursor = buf.readBoolean();
		this.curPosX = buf.readInt();
		this.curPosY = buf.readInt();
		int size = buf.readInt();
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		this.name = tag.getString("n");
		NBTTagList list = tag.getTagList("l", size);
		for(int i = 0;i<list.tagCount();i++)
			this.term.add(new TerminalObject(list.getCompoundTagAt(i)));
	}
}
