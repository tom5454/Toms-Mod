package com.tom.util;

import java.nio.ByteBuffer;

import net.minecraft.util.math.MathHelper;

public class ByteBuffers {
	public ByteBuffer[] myBuffers;
	public int index;
	public ByteBuffers(int size) {
		myBuffers = new ByteBuffer[MathHelper.ceil(size / 1024d)];
		for (int x = 0; x < myBuffers.length; x++) {
			myBuffers[x] = ByteBuffer.allocateDirect(1024);
		}
	}

	public void put(byte v) {
		int bufx = index / myBuffers.length;
		index++;
		myBuffers[bufx].put(v);
	}

	public void flip() {
		for (int i = 0;i < myBuffers.length;i++) {
			ByteBuffer byteBuffer = myBuffers[i];
			byteBuffer.flip();
		}
	}
}
