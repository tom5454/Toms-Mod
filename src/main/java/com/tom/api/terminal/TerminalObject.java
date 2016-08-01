package com.tom.api.terminal;

import net.minecraft.nbt.NBTTagCompound;

public class TerminalObject {
	public int xPos = 0;
	public int yPos = 0;
	public TerminalObjectTypes type;
	public String t;
	public int c = 0;
	public TerminalObject(int xPos, int yPos, String s, int c){
		this.xPos = xPos;
		this.yPos = yPos;
		this.type = TerminalObjectTypes.String;
		this.t = s;
		this.c = c;
	}
	public TerminalObject(int xPos, int yPos, String s){
		this.xPos = xPos;
		this.yPos = yPos;
		this.type = TerminalObjectTypes.Icon;
		this.t = s;
	}
	public TerminalObject(NBTTagCompound tag){
		this.type = TerminalObjectTypes.values()[tag.getInteger("type")];
		this.xPos = tag.getInteger("xPos");
		this.yPos = tag.getInteger("yPos");
		this.c = tag.getInteger("c");
		this.t = tag.getString("t");
	}
	public NBTTagCompound exportToNBT(){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", this.type.ordinal());
		tag.setInteger("xPos",this.xPos);
		tag.setInteger("yPos",this.yPos);
		tag.setInteger("c",this.c);
		tag.setString("t",this.t);
		return tag;
	}
}
