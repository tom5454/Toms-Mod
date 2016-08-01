package com.tom.core.item;

import java.util.ArrayList;
import java.util.List;

import com.tom.api.terminal.Terminal;
import com.tom.core.tileentity.TileEntityTabletController.LuaSound;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class TabletHandler {
	public TabletHandler(int id){
		this.id = id;
	}
	public int id;
	public boolean connectedToAntenna = false;
	public boolean connectedToAccessPoint = false;
	public boolean antAntenna = true;
	public boolean apAntenna = true;
	public boolean isJammed = false;
	public int antX = 0;
	public int antY = 0;
	public int antZ = 0;
	public int apX = 0;
	public int apY = 0;
	public int apZ = 0;
	public int jX = 0;
	public int jY = 0;
	public int jZ = 0;
	public int cursorX = 0;
	public int cursorY = 0;
	public boolean in = false;
	public String cHitBox = "";
	public Terminal term = new Terminal(1);
	public void writeToPacket(ByteBuf buf){
		buf.writeBoolean(antAntenna);
		buf.writeBoolean(apAntenna);
		buf.writeBoolean(connectedToAntenna);
		buf.writeBoolean(connectedToAccessPoint);
		term.writeToPacket(buf);
	}
	public void readFromPacket(ByteBuf buf){
		this.antAntenna = buf.readBoolean();
		this.apAntenna = buf.readBoolean();
		this.connectedToAntenna = buf.readBoolean();
		this.connectedToAccessPoint = buf.readBoolean();
		this.term.readFromPacket(buf);
	}
	public NBTTagCompound modemTag = new NBTTagCompound();
	public String playerName = "";
	public boolean hasModem = false;
	public Object[] obj = new Object[]{"null","",false,false,0,0,0,0,0,0,0,0,0,false,0,0,0};
	public List<LuaSound> sounds = new ArrayList<LuaSound>();
}
	
