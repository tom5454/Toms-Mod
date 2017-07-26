package com.tom.api.terminal;

import io.netty.buffer.ByteBuf;

public class TerminalWidow extends TerminalBase {
	public int xPos = 0;
	public int yPos = 0;

	public void writeToPacket(ByteBuf buf) {
		buf.writeInt(xPos);
		buf.writeInt(yPos);
		super.writeToPacket(buf);
	}

	public void readFromPacket(ByteBuf buf) {
		this.xPos = buf.readInt();
		this.yPos = buf.readInt();
		super.readFromPacket(buf);
	}
}
