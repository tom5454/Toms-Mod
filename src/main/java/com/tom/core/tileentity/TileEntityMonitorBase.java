package com.tom.core.tileentity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.client.EventHandlerClient;
import com.tom.client.ICustomModelledTileEntity;
import com.tom.core.CoreInit;
import com.tom.handler.TMWorldHandler;
import com.tom.lib.api.tileentity.ICustomPacket;
import com.tom.lib.api.tileentity.ITMPeripheral.IComputer;
import com.tom.lib.api.tileentity.ITMPeripheral.ITMLuaObject;
import com.tom.lib.api.tileentity.ITMPeripheral.LuaException;
import com.tom.util.IDList;
import com.tom.util.TomsModUtils;

import com.tom.core.block.BlockMonitorBase;

public class TileEntityMonitorBase extends TileEntityTomsMod implements ICustomModelledTileEntity, ICustomPacket {
	protected TileEntityMonitorBase(int size) {
		setSize(size);
		this.screen = new int[size][size];
		this.THICKNESS = 1;
		this.direction = 0;
		this.textList = new IDList<>();
		this.textureList = new IDList<>();
	}

	public int[][] screen;
	private int size;
	public float THICKNESS;
	public IDList<LuaText> textList;
	public IDList<LuaTexture> textureList;
	public int direction;
	public Integer renderer, txid;
	public boolean needsReRender = true;
	public ByteBuffer buffer;

	@Override
	public void writeToPacket(DataOutputStream buf) throws IOException {
		buf.writeByte(direction);
		buf.writeShort(size);
		for (int i = 0;i < size;i++) {
			for (int j = 0;j < size;j++) {
				int v = screen[j][i];
				buf.write((v >>> 16) & 0xFF);
				buf.write((v >>>  8) & 0xFF);
				buf.write(v & 0xFF);
				buf.write((v >>> 24) & 0xFF);//Change to RGBA
			}
		}
		buf.writeShort(this.textList.size());
		buf.writeShort(this.textureList.size());
		for (int i = 0;i < textList.size();i++) {
			LuaText e = textList.get(i);
			buf.writeShort(e.text.length());
			buf.write(e.text.getBytes());
			buf.writeDouble(e.xCoord);
			buf.writeDouble(e.yCoord);
			buf.writeDouble(e.rotation);
			buf.writeFloat(e.scale);
			buf.writeInt(e.color);
		}
		for(int i = 0;i < textureList.size();i++){
			LuaTexture e = textureList.get(i);
			buf.writeShort(e.s.length());
			buf.write(e.s.getBytes());
			buf.writeDouble(e.xCoord);
			buf.writeDouble(e.yCoord);
			buf.writeDouble(e.rotation);
			buf.writeFloat(e.scale);
			buf.writeFloat(e.opacity);
			buf.writeInt(e.color);
			buf.writeBoolean(e.colored);
		}
	}
	@Override
	public void readPacket(byte[] data) {
		Minecraft.getMinecraft().mcProfiler.startSection("readPacket");
		textList.clear();
		textureList.clear();
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			DataInputStream is = new DataInputStream(bis);
			direction = is.readByte();
			size = is.readShort();
			setSize(size);
			if(buffer != null){
				buffer.rewind();
				if(buffer.remaining() != size*size*4)buffer = null;
			}
			if(buffer == null)buffer = BufferUtils.createByteBuffer(size * size * 4);
			int start = data.length - bis.available();
			buffer.put(data, start, size*size*4);
			bis.skip(size*size*4);
			buffer.flip();
			readFromPacket(is);
		} catch(IOException e){}
		Minecraft.getMinecraft().mcProfiler.endSection();
	}
	@Override
	public void readFromPacket(DataInputStream buf) throws IOException {
		short textlistsize = buf.readShort();
		short texturelistsize = buf.readShort();
		for (int i = 0;i < textlistsize;i++) {
			short text = buf.readShort();
			byte[] data = new byte[text*2];
			buf.read(data);
			double x = buf.readDouble();
			double y = buf.readDouble();
			double r = buf.readDouble();
			float s = buf.readFloat();
			int c = buf.readInt();
			textList.put(new LuaText(x, y, c, new String(data), r, s));
		}
		for (int i = 0;i < texturelistsize;i++) {
			short text = buf.readShort();
			byte[] data = new byte[text*2];
			buf.read(data);
			double x = buf.readDouble();
			double y = buf.readDouble();
			double r = buf.readDouble();
			float s = buf.readFloat();
			float o = buf.readFloat();
			int c = buf.readInt();
			boolean colored = buf.readBoolean();
			textureList.put(new LuaTexture(x, y, c, new String(data), r, colored, o, s));
		}
		needsReRender = true;
	}

	//	@Override
	//	public NBTTagCompound getPacket() {
	//		NBTTagCompound buf = new NBTTagCompound();
	//		buf.setInteger("d", direction);
	//		// buf.writeInt(this.textList.size());
	//		// buf.writeInt(this.textureList.size());
	//		buf.setInteger("size", size);
	//		NBTTagCompound tag = new NBTTagCompound();
	//		NBTTagList list = new NBTTagList();
	//		for (int i = 0;i < screen.length;i++) {
	//			/*NBTTagList list1 = new NBTTagList();
	//			NBTTagCompound list1C = new NBTTagCompound();
	//			for(int j = 0;j<screen[i].length;j++){
	//				NBTTagCompound cTag = new NBTTagCompound();
	//				/*cTag.setInteger("r", screen[i][j][0]);
	//				cTag.setInteger("g", screen[i][j][1]);
	//				cTag.setInteger("b", screen[i][j][2]);
	//				cTag.setInteger("a", screen[i][j][3]);
	//				cTag.setInteger("h", screen[i][j][4]);
	//				cTag.setIntArray("c", screen[i][j]);
	//				list1.appendTag(cTag);
	//			}
	//			list1C.setTag("l", list1);
	//			list.appendTag(list1C);*/
	//			NBTTagCompound cTag = new NBTTagCompound();
	//			cTag.setIntArray("c", this.screen[i]);
	//			list.appendTag(cTag);
	//		}
	//		tag.setTag("s", list);
	//		NBTTagList tagList = new NBTTagList();
	//		for (LuaText c : this.textList.values()) {
	//			NBTTagCompound cTag = new NBTTagCompound();
	//			cTag.setString("t", c.s);
	//			cTag.setInteger("c", c.c);
	//			cTag.setDouble("x", c.xCoord);
	//			cTag.setDouble("y", c.yCoord);
	//			cTag.setDouble("r", c.rotation);
	//			cTag.setFloat("s", c.scale);
	//			tagList.appendTag(cTag);
	//		}
	//		tag.setTag("l", tagList);
	//		tagList = new NBTTagList();
	//		for (LuaTexture c : this.textureList.values()) {
	//			NBTTagCompound cTag = new NBTTagCompound();
	//			cTag.setString("t", c.s);
	//			cTag.setInteger("c", c.c);
	//			cTag.setDouble("x", c.xCoord);
	//			cTag.setDouble("y", c.yCoord);
	//			cTag.setDouble("r", c.rotation);
	//			cTag.setBoolean("b", c.colored);
	//			cTag.setDouble("o", c.opacity);
	//			cTag.setFloat("s", c.scale);
	//			tagList.appendTag(cTag);
	//		}
	//		tag.setTag("t", tagList);
	//		// ByteBufUtils.writeTag(buf, tag);
	//		buf.setTag("s", tag);
	//		// this.t = tag;
	//		// System.out.println("write to packet");
	//		return buf;
	//	}
	//
	//	@Override
	//	public void receiveNBTPacket(NBTTagCompound buf) {
	//		this.direction = buf.getInteger("d");
	//		int size = buf.getInteger("size");
	//		setSize(size);
	//		//int[][] screenOld = screen;
	//		if(screen.length != size)this.screen = new int[size][size];
	//		// int txListSize = buf.readInt();
	//		// int txeListSize = buf.readInt();
	//		// System.out.println(size);
	//		NBTTagCompound tag = buf.getCompoundTag("s");
	//		// System.out.println(tag.toString());
	//		NBTTagList list = (NBTTagList) tag.getTag("s");
	//		// System.out.println(list.toString());
	//		for (int i = 0;i < size;i++) {
	//			/*NBTTagCompound list1C = list.getCompoundTagAt(i);
	//			NBTTagList list1 = list1C.getTagList("l", size);
	//			for(int j = 0;j<size;j++){
	//				NBTTagCompound cTag = list1.getCompoundTagAt(j);
	//				int[] r = cTag.getIntArray("c");
	//				screen[i][j] = r;
	//			}*/
	//			screen[i] = list.getCompoundTagAt(i).getIntArray("c");
	//			swap(screen[i]);
	//		}
	//		NBTTagList tagList = (NBTTagList) tag.getTag("l");
	//		textList.clear();
	//		for (int i = 0;i < tagList.tagCount();i++) {
	//			NBTTagCompound cTag = tagList.getCompoundTagAt(i);
	//			LuaText cC = new LuaText(cTag.getDouble("x"), cTag.getDouble("y"), cTag.getInteger("c"), cTag.getString("t"), cTag.getDouble("r"), cTag.getFloat("s"));
	//			this.textList.put(cC);
	//		}
	//		tagList = (NBTTagList) tag.getTag("t");
	//		textureList.clear();
	//		for (int i = 0;i < tagList.tagCount();i++) {
	//			NBTTagCompound cTag = tagList.getCompoundTagAt(i);
	//			LuaTexture cC = new LuaTexture(cTag.getDouble("x"), cTag.getDouble("y"), cTag.getInteger("c"), cTag.getString("t"), cTag.getDouble("r"), cTag.getBoolean("b"), cTag.getDouble("o"), cTag.getFloat("s"));
	//			this.textureList.put(cC);
	//		}
	//		needsReRender = true;
	//		/*if(!Arrays.deepEquals(screenOld, screen)){
	//			canReRender = false;
	//			Thread t = new Thread(this::drawBase);
	//			t.setDaemon(true);
	//			t.setName("Monitor Rerenderer " + t.getName());
	//			t.start();
	//		}else canReRender = true;*/
	//		// System.out.println("read from packet");
	//	}

	@SideOnly(Side.CLIENT)
	public void draw() {
		Minecraft mc = Minecraft.getMinecraft();
		mc.mcProfiler.startSection("rebuild");
		mc.mcProfiler.startSection("setup");
		if (this.renderer == null)this.renderer = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(this.renderer, GL11.GL_COMPILE);
		GL11.glPushMatrix();// main
		GL11.glPushMatrix();// render
		double zDepth = 0;
		GL11.glScalef(1.0F, -1F, -1F);
		GL11.glTranslatef(-0.13F, -1.13F, (1 - this.THICKNESS) - 0.51F);
		float scale = 1F / this.size;
		GL11.glScalef(scale, scale, scale);
		GL11.glRotatef(180, 0, 0, 1);
		GL11.glTranslatef(-10 * (this.size / 16), -10 * (this.size / 16), 0);
		IDList<LuaText> textList = this.textList;
		IDList<LuaTexture> textureList = this.textureList;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder tes = tessellator.getBuffer();
		GlStateManager.disableLighting();
		int i = 15 << 20 | 15 << 4;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();// Objects
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4d(1, 1, 1, 1);
		mc.mcProfiler.endSection();
		mc.mcProfiler.startSection("updateTexture");
		if(txid == null)txid = create();
		GlStateManager.bindTexture(txid);
		if(buffer != null)GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, size, size, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		mc.mcProfiler.endSection();
		mc.mcProfiler.startSection("draw_Texture");
		try {
			tes.begin(7, DefaultVertexFormats.POSITION_TEX);
			int x = 0, y = 0, width = size, height = size;
			tes.pos(x + 0, y + height, zDepth).tex(0, 1).endVertex();
			tes.pos(x + width, y + height, zDepth).tex(1, 1).endVertex();
			tes.pos(x + width, y + 0, zDepth).tex(1, 0).endVertex();
			tes.pos(x + 0, y + 0, zDepth).tex(0, 0).endVertex();
			tessellator.draw();
		} catch (NullPointerException e) {
			CoreInit.log.error("Monitor Render: null pointer exception");
		}
		mc.mcProfiler.endSection();
		mc.mcProfiler.startSection("objects");
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();// Objects
		if (!textList.isEmpty() || !textureList.isEmpty()) {
			GL11.glPushMatrix();// text&texture
			// float scale2 = scale/2;
			GL11.glScalef(0.2F, 0.2F, 0.2F);
			GL11.glPushMatrix();// text
			GL11.glTranslatef(0, 0, -0.1F);
			for (LuaText c : textList.values()) {
				GL11.glPushMatrix();// textIn
				GL11.glRotated(c.rotation, 0, 0, 1);
				GL11.glScalef(c.scale, c.scale, c.scale);
				EventHandlerClient.drawString(c.text, MathHelper.floor(c.xCoord * 2), MathHelper.floor(c.yCoord * 2), c.color);
				GL11.glPopMatrix();// textIn
			}
			// Render.drawString(1,1,0xFFFFFFFF,"hello");
			GL11.glPopMatrix();// text
			GL11.glPushMatrix();// texture
			GL11.glTranslatef(0, 0, -0.2F);
			for (LuaTexture c : textureList.values()) {
				GL11.glPushMatrix();// textureIn
				GL11.glRotated(c.rotation, 0, 0, 1);
				GL11.glScalef(c.scale, c.scale, c.scale);
				try {
					mc.renderEngine.bindTexture(new ResourceLocation(c.s + ".png"));
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					tes.begin(GL11.GL_QUADS, c.colored ? DefaultVertexFormats.POSITION_TEX_COLOR : DefaultVertexFormats.POSITION_TEX);
					// if(c.colored)tes.setColorRGBA_I(c.c, 1000);
					// t.setColorRGBA_I(1,
					// MathHelper.floor_double(c.opacity*1000));
					double x = c.xCoord;
					double y = c.yCoord;
					int w = 1;
					int h = 1;
					double u1 = 0;
					double u2 = 1;
					double v1 = 0;
					double v2 = 1;
					tes.pos(x + w, y, zDepth).tex(u2, v1);
					tes.pos(x, y, zDepth).tex(u1, v1);
					tes.pos(x, y + h, zDepth).tex(u1, v2);
					tes.pos(x + w, y + h, zDepth).tex(u2, v2);
					tessellator.draw();
					GL11.glDisable(GL11.GL_BLEND);
				} catch (NullPointerException e) {
					CoreInit.log.error("Monitor Render: null pointer exception");
				}
				GL11.glPopMatrix();// textureIn
			}
			// Render.drawString(1,1,0xFFFFFFFF,"hello");
			GL11.glPopMatrix();// texture
			GL11.glPopMatrix();// text&texture
		}
		mc.mcProfiler.endSection();
		mc.mcProfiler.startSection("finish");
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();// render
		GL11.glColor4d(1, 1, 1, 1);
		GL11.glPopMatrix();// main
		GL11.glEndList();
		mc.mcProfiler.endSection();
		mc.mcProfiler.endSection();
		this.needsReRender = false;

	}
	//private static final int BYTES_PER_PIXEL = 16;
	/*@SideOnly(Side.CLIENT)
	private void drawBase(boolean retry){
		//buffer = new ByteBuffers(size * size * BYTES_PER_PIXEL);
		try {
			if(buffer == null)buffer = BufferUtils.createByteBuffer(size * size * BYTES_PER_PIXEL);
			buffer.rewind();
			for(int y = 0; y < size; y++){
				/*for(int x = 0; x < size; x++){
				int pixel = screen[x][y];
				buffer.put((byte) ((pixel >> 16) & 0xFF));//R
				buffer.put((byte) ((pixel >> 8) & 0xFF));//G
				buffer.put((byte) (pixel & 0xFF));//B
				//buffer.put((byte) ((pixel >> 24) & 0xFF));//A
				buffer.put((byte) 255);
			}*/
	/*buffer.put(screen[y]);
			}
			buffer.flip();
		}catch (Exception e) {
			e.printStackTrace();
			if(retry){
				buffer = null;
				drawBase(false);
			}
		}
	}*/

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.direction = tag.getInteger("direction");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("direction", this.direction);
		// tag.setTag("packet", this.t);
		return tag;
	}

	public TileEntityMonitorBase connect(int x, int y, int z) {
		return this;
	}

	public int[] getOffset(int x, int y, int d) {
		int[] ret;
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if (d == 0) {
			ret = TomsModUtils.getCoordTable(xCoord - x, yCoord, zCoord - y);
		} else if (d == 1) {
			ret = TomsModUtils.getCoordTable(xCoord + x, yCoord, zCoord + y);
		} else if (d == 2) {
			ret = TomsModUtils.getCoordTable(xCoord - x, yCoord + y, zCoord);
		} else if (d == 3) {
			ret = TomsModUtils.getCoordTable(xCoord + x, yCoord + y, zCoord);
		} else if (d == 4) {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord + y, zCoord + x);
		} else if (d == 5) {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord + y, zCoord - x);
		} else {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord);
		}
		return ret;
	}

	public int[] getOffset(int x, int y, int z, int d) {
		int[] ret;
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if (d == 0) {
			ret = new int[]{xCoord - x, zCoord - y};
		} else if (d == 1) {
			ret = new int[]{xCoord + x, zCoord + y};
		} else if (d == 2) {
			ret = new int[]{xCoord - x, yCoord + y};
		} else if (d == 3) {
			ret = new int[]{xCoord + x, yCoord + y};
		} else if (d == 4) {
			ret = new int[]{zCoord + x, yCoord + y};
		} else if (d == 5) {
			ret = new int[]{zCoord - x, yCoord + y};
		} else {
			ret = new int[]{xCoord, yCoord};
		}
		return ret;
	}
	private static final String[] LuaTexture_METHODS = new String[]{"getTexture", "getX", "getY", "getColor", "setTexture", "setX", "setY", "setColor", "delete", "getRotation", "setRotation", "setColored", "getScale", "setScale"},
			LuaText_METHOD = new String[]{"getText", "getX", "getY", "getColor", "setText", "setX", "setY", "setColor", "delete", "getRotation", "setRotation", "getScale", "setScale"};
	public class LuaTexture implements ITMLuaObject {
		public String s = "";
		public double xCoord = 0;
		public double yCoord = 0;
		public int color = 0;
		public double rotation = 0;
		public boolean colored = false;
		public float opacity = 1;
		public float scale = 1;

		public LuaTexture() {
		}

		public LuaTexture(double x, double y, int c, String text, double rotation, boolean colored, float opacity, float scale) {
			this.xCoord = x;
			this.yCoord = y;
			this.color = c;
			this.s = text;
			this.rotation = rotation;
			this.colored = colored;
			this.opacity = opacity;
			this.scale = scale;
		}

		@Override
		public String[] getMethodNames() {
			return LuaTexture_METHODS;
		}

		@Override
		public Object[] call(IComputer c, String methodIn, Object[] a) throws LuaException {
			if (!textureList.contains(this))
				throw new LuaException("This object was already deleted");
			int method = Arrays.binarySearch(LuaTexture_METHODS, methodIn);
			if (method == 0)
				return new Object[]{s};
			else if (method == 1)
				return new Object[]{xCoord};
			else if (method == 2)
				return new Object[]{yCoord};
			else if (method == 3)
				return new Object[]{this.colored, c, this.opacity};
			else if (method == 4) {
				if (a.length > 0 && a[0] != null) {
					this.s = a[0].toString();
				} else {
					throw new LuaException("Invalid Arguments, excepted (String)");
				}
			} else if (method == 5) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.xCoord = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 6) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.yCoord = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 7) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.color = MathHelper.floor((Double) a[0]);
					if (a.length > 1 && a[1] instanceof Double) {
						this.opacity = new Float((Double) a[0]);
					}
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 8) {
				textureList.remove(this);
			} else if (method == 9)
				return new Object[]{this.rotation};
			else if (method == 10) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.rotation = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 11) {
				if (a.length > 0 && a[0] instanceof Boolean) {
					this.colored = (Boolean) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (boolean)");
				}
			} else if (method == 12)
				return new Object[]{this.scale};
			else if (method == 13) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.scale = new Float((Double) a[0]);
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}
			return null;
		}
		@Override
		public long getID() {
			return textureList.getIDFor(this);
		}
	}

	public class LuaText implements ITMLuaObject {
		public String text = "";
		public double xCoord = 0;
		public double yCoord = 0;
		public int color = 0;
		public double rotation = 0;
		public float scale = 1;

		public LuaText() {
		}

		public LuaText(double x, double y, int c, String text, double rotation, float scale) {
			this.xCoord = x;
			this.yCoord = y;
			this.color = c;
			this.text = text;
			this.rotation = rotation;
			this.scale = scale;
		}

		@Override
		public String[] getMethodNames() {
			return LuaText_METHOD;
		}

		@Override
		public Object[] call(IComputer context, String methodIn, Object[] a) throws LuaException {
			if (!textList.contains(this))
				throw new LuaException("This object was already deleted");
			int method = Arrays.binarySearch(LuaText_METHOD, methodIn);
			if (method == 0)
				return new Object[]{text};
			else if (method == 1)
				return new Object[]{xCoord};
			else if (method == 2)
				return new Object[]{yCoord};
			else if (method == 3)
				return new Object[]{color};
			else if (method == 4) {
				if (a.length > 0 && a[0] != null) {
					this.text = a[0].toString();
				} else {
					throw new LuaException("Invalid Arguments, excepted (String)");
				}
			} else if (method == 5) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.xCoord = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 6) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.yCoord = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 7) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.color = MathHelper.floor((Double) a[0]);
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 8) {
				textList.remove(this);
			} else if (method == 9)
				return new Object[]{this.rotation};
			else if (method == 10) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.rotation = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 11)
				return new Object[]{this.scale};
			else if (method == 12) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.scale = new Float((Double) a[0]);
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}
			return null;
		}
		@Override
		public long getID() {
			return textList.getIDFor(this);
		}
	}

	@Override
	public void postUpdate(IBlockState state) {
		int d = state.getValue(BlockMonitorBase.FACING).ordinal();
		if (d == 5)
			this.direction = 4;
		else if (d == 4)
			this.direction = 5;
		else if (d == 3)
			this.direction = 2;
		else if (d == 2)
			this.direction = 3;
		else if (d == 0)
			this.direction = 1;
		else if (d == 1)
			this.direction = 0;
	}

	@Override
	public EnumFacing getFacing() {
		return world.getBlockState(pos).getValue(BlockMonitorBase.FACING);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		int prevSize = size;
		this.size = size;
		if(size != prevSize)
			buffer = null;
	}
	@SideOnly(Side.CLIENT)
	private int create(){
		int textureID = GlStateManager.generateTexture();
		GlStateManager.bindTexture(textureID);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, size, size, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		return textureID;
	}

	public void sync() {
		TMWorldHandler.markDirty(world, pos);
	}
	@Override
	public void invalidate() {
		super.invalidate();
		CoreInit.proxy.delTexture(txid);
		CoreInit.proxy.delList(renderer);
		txid = null;
	}
}
