package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.client.ICustomModelledTileEntity;
import com.tom.core.CoreInit;

import com.tom.core.block.BlockMonitorBase;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;

public class TileEntityMonitorBase extends TileEntityTomsMod implements ICustomModelledTileEntity{
	protected TileEntityMonitorBase(int sizeX, int sizeY){
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.screen = new int[sizeX][sizeY];
		this.THICKNESS = 1;
		this.direction = 0;
		this.textList = new ArrayList<LuaText>();
		this.textureList = new ArrayList<LuaTexture>();
	}
	protected TileEntityMonitorBase(int size){
		this(size,size);
	}
	public int[][] screen;
	public int sizeX;
	public int sizeY;
	public float THICKNESS;
	public List<LuaText> textList;
	public List<LuaTexture> textureList;
	public int direction;
	public Integer renderer;
	public boolean needsReRender = true;
	@Override
	public void writeToPacket(NBTTagCompound buf){
		buf.setInteger("d", direction);
		buf.setInteger("sx", sizeX);
		buf.setInteger("sy", sizeY);
		//buf.writeInt(this.textList.size());
		//buf.writeInt(this.textureList.size());
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<screen.length;i++){
			/*NBTTagList list1 = new NBTTagList();
			NBTTagCompound list1C = new NBTTagCompound();
			for(int j = 0;j<screen[i].length;j++){
				NBTTagCompound cTag = new NBTTagCompound();
				/*cTag.setInteger("r", screen[i][j][0]);
				cTag.setInteger("g", screen[i][j][1]);
				cTag.setInteger("b", screen[i][j][2]);
				cTag.setInteger("a", screen[i][j][3]);
				cTag.setInteger("h", screen[i][j][4]);
				cTag.setIntArray("c", screen[i][j]);
				list1.appendTag(cTag);
			}
			list1C.setTag("l", list1);
			list.appendTag(list1C);*/
			NBTTagCompound cTag = new NBTTagCompound();
			cTag.setIntArray("c", this.screen[i]);
			list.appendTag(cTag);
		}
		tag.setTag("s", list);
		NBTTagList tagList = new NBTTagList();
		for(LuaText c : this.textList){
			NBTTagCompound cTag = new NBTTagCompound();
			cTag.setString("t", c.s);
			cTag.setInteger("c", c.c);
			cTag.setDouble("x", c.xCoord);
			cTag.setDouble("y", c.yCoord);
			cTag.setDouble("r", c.rotation);
			cTag.setFloat("s", c.scale);
			tagList.appendTag(cTag);
		}
		tag.setTag("l", tagList);
		tagList = new NBTTagList();
		for(LuaTexture c : this.textureList){
			NBTTagCompound cTag = new NBTTagCompound();
			cTag.setString("t", c.s);
			cTag.setInteger("c", c.c);
			cTag.setDouble("x", c.xCoord);
			cTag.setDouble("y", c.yCoord);
			cTag.setDouble("r", c.rotation);
			cTag.setBoolean("b", c.colored);
			cTag.setDouble("o", c.opacity);
			cTag.setFloat("s", c.scale);
			tagList.appendTag(cTag);
		}
		tag.setTag("t", tagList);
		//ByteBufUtils.writeTag(buf, tag);
		buf.setTag("s", tag);
		//this.t = tag;
		//System.out.println("write to packet");
	}
	@Override
	public void readFromPacket(NBTTagCompound buf){
		this.direction = buf.getInteger("d");
		this.sizeX = buf.getInteger("sx");
		this.sizeY = buf.getInteger("sy");
		this.screen = new int[sizeX][sizeY];
		//int txListSize = buf.readInt();
		//int txeListSize = buf.readInt();
		//System.out.println(size);
		NBTTagCompound tag = buf.getCompoundTag("s");
		//System.out.println(tag.toString());
		NBTTagList list = (NBTTagList) tag.getTag("s");
		//System.out.println(list.toString());
		for(int i = 0;i<sizeX;i++){
			/*NBTTagCompound list1C = list.getCompoundTagAt(i);
			NBTTagList list1 = list1C.getTagList("l", size);
			for(int j = 0;j<size;j++){
				NBTTagCompound cTag = list1.getCompoundTagAt(j);
				int[] r = cTag.getIntArray("c");
				screen[i][j] = r;
			}*/
			screen[i] = list.getCompoundTagAt(i).getIntArray("c");
		}
		NBTTagList tagList = (NBTTagList) tag.getTag("l");
		textList.clear();
		for(int i = 0;i<tagList.tagCount();i++){
			NBTTagCompound cTag = tagList.getCompoundTagAt(i);
			LuaText cC = new LuaText(cTag.getDouble("x"), cTag.getDouble("y"), cTag.getInteger("c"), cTag.getString("t"),cTag.getDouble("r"),cTag.getFloat("s"));
			this.textList.add(cC);
		}
		tagList = (NBTTagList) tag.getTag("t");
		textureList.clear();
		for(int i = 0;i<tagList.tagCount();i++){
			NBTTagCompound cTag = tagList.getCompoundTagAt(i);
			LuaTexture cC = new LuaTexture(cTag.getDouble("x"), cTag.getDouble("y"), cTag.getInteger("c"), cTag.getString("t"),cTag.getDouble("r"),cTag.getBoolean("b"),cTag.getDouble("o"),cTag.getFloat("s"));
			this.textureList.add(cC);
		}
		//System.out.println("read from packet");
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		{
			if(this.renderer != null)GLAllocation.deleteDisplayLists(this.renderer);
			this.renderer = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(this.renderer, GL11.GL_COMPILE);
			GL11.glPushMatrix();//main
			//Shape1.render(size);
			GL11.glPushMatrix();//render
			double zDepth = 0;
			GL11.glScalef(1.0F, -1F, -1F);
			//TomsMathHelper.rotateMatrixByMetadata(tile.direction);
			GL11.glTranslatef(-0.13F,-1.13F,(1-this.THICKNESS)-0.51F);
			float scale = 1F / this.sizeX;
			GL11.glScalef(scale,scale,scale);
			//GL11.glDisable(GL11.GL_LIGHTING);
			Minecraft mc = Minecraft.getMinecraft();
			GL11.glRotatef(180, 0, 0, 1);
			GL11.glTranslatef(-10 * (this.sizeX/16),-10 * (this.sizeX/16),0);
			//ResourceLocation tex = new ResourceLocation("tm:minimap/selection2.png");
			//mc.renderEngine.bindTexture(tex);
			//Render.drawTexturedRect(0, 0, 20, 20);
			int[][] screen = this.screen;
			List<LuaText> textList = this.textList;
			List<LuaTexture> textureList = this.textureList;
			//mc.renderEngine.bindTexture(Configs.pixel);
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer tes = tessellator.getBuffer();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPushMatrix();//Objects
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			for(int xP = 0;xP<screen.length;xP++){
				for(int yP = 0;yP<screen[xP].length;yP++){
					//int xRP = xP * 2;
					//int yRP = yP * 2;
					if(screen[xP][yP] != 0){
						try {
							tes.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_COLOR );
							//tes.setColorRGBA_I(screen[xP][yP], 1000);
							int color = screen[xP][yP];
							tes.pos(xP + 1, yP,     zDepth).color(((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f, ((color) & 0xff) / 255.0f, 1.0F).endVertex();
							tes.pos(xP,     yP,     zDepth).color(((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f, ((color) & 0xff) / 255.0f, 1.0F).endVertex();
							tes.pos(xP,     yP + 1, zDepth).color(((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f, ((color) & 0xff) / 255.0f, 1.0F).endVertex();
							tes.pos(xP + 1, yP + 1, zDepth).color(((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f, ((color) & 0xff) / 255.0f, 1.0F).endVertex();
							tessellator.draw();
						} catch (NullPointerException e) {
							CoreInit.log.error("Monitor Render: null pointer exception");
						}
					}
				}
			}
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();//Objects
			if(!textList.isEmpty() || !textureList.isEmpty()){
				GL11.glPushMatrix();//text&texture
				//float scale2 = scale/2;
				GL11.glScalef(0.2F,0.2F,0.2F);
				GL11.glPushMatrix();//text
				GL11.glTranslatef(0,0,-0.1F);
				for(LuaText c : textList){
					GL11.glPushMatrix();//textIn
					GL11.glRotated(c.rotation, 0, 0, 1);
					GL11.glScalef(c.scale,c.scale,c.scale);
					mc.getRenderManager().getFontRenderer().drawString(c.s, MathHelper.floor_double(c.xCoord * 2), MathHelper.floor_double(c.yCoord * 2), c.c);
					GL11.glPopMatrix();//textIn
				}
				//Render.drawString(1,1,0xFFFFFFFF,"hello");
				GL11.glPopMatrix();//text
				GL11.glPushMatrix();//texture
				GL11.glTranslatef(0,0,-0.2F);
				for(LuaTexture c : textureList){
					GL11.glPushMatrix();//textureIn
					GL11.glRotated(c.rotation, 0, 0, 1);
					GL11.glScalef(c.scale,c.scale,c.scale);
					try {
						mc.renderEngine.bindTexture(new ResourceLocation(c.s+".png"));
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						tes.begin(GL11.GL_QUADS , c.colored ? DefaultVertexFormats.POSITION_TEX_COLOR :  DefaultVertexFormats.POSITION_TEX);
						//if(c.colored)tes.setColorRGBA_I(c.c, 1000);
						//t.setColorRGBA_I(1, MathHelper.floor_double(c.opacity*1000));
						double x = c.xCoord;
						double y = c.yCoord;
						int w = 1;
						int h = 1;
						double u1 = 0;
						double u2 = 1;
						double v1 = 0;
						double v2 = 1;
						tes.pos(x + w, y,     zDepth).tex(u2, v1);
						tes.pos(x,     y,     zDepth).tex(u1, v1);
						tes.pos(x,     y + h, zDepth).tex(u1, v2);
						tes.pos(x + w, y + h, zDepth).tex(u2, v2);
						tessellator.draw();
						GL11.glDisable(GL11.GL_BLEND);
					} catch (NullPointerException e) {
						CoreInit.log.error("Monitor Render: null pointer exception");
					}
					GL11.glPopMatrix();//textureIn
				}
				//Render.drawString(1,1,0xFFFFFFFF,"hello");
				GL11.glPopMatrix();//texture
				GL11.glPopMatrix();//text&texture
			}
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();//render
			GL11.glPopMatrix();//main
			GL11.glEndList();
			this.needsReRender = false;
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.direction = tag.getInteger("direction");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("direction", this.direction);
		//tag.setTag("packet", this.t);
		return tag;
	}
	public TileEntityMonitorBase connect(int x, int y, int z) {return this;}
	public int[] getOffset(int x, int y, int d){
		int[] ret;
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if(d == 0){
			ret = TomsModUtils.getCoordTable(xCoord - x, yCoord, zCoord - y);
		}else if(d == 1){
			ret = TomsModUtils.getCoordTable(xCoord + x, yCoord, zCoord + y);
		}else if(d == 2){
			ret = TomsModUtils.getCoordTable(xCoord - x, yCoord + y, zCoord);
		}else if(d == 3){
			ret = TomsModUtils.getCoordTable(xCoord + x, yCoord + y, zCoord);
		}else if(d == 4){
			ret = TomsModUtils.getCoordTable(xCoord, yCoord + y, zCoord + x);
		}else if(d == 5){
			ret = TomsModUtils.getCoordTable(xCoord, yCoord + y, zCoord - x);
		}else{
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord);
		}
		return ret;
	}
	public int[] getOffset(int x, int y,int z, int d){
		int[] ret;
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if(d == 0){
			ret = new int[]{xCoord - x, zCoord - y};
		}else if(d == 1){
			ret = new int[]{xCoord + x, zCoord + y};
		}else if(d == 2){
			ret = new int[]{xCoord - x, yCoord + y};
		}else if(d == 3){
			ret = new int[]{xCoord + x, yCoord + y};
		}else if(d == 4){
			ret = new int[]{zCoord + x, yCoord + y};
		}else if(d == 5){
			ret = new int[]{zCoord - x, yCoord + y};
		}else{
			ret = new int[]{xCoord, yCoord};
		}
		return ret;
	}
	public class LuaTexture implements ILuaObject{
		public String s = "";
		public double xCoord = 0;
		public double yCoord = 0;
		public int c = 0;
		public double rotation = 0;
		public boolean colored = false;
		public double opacity = 1;
		public float scale = 1;
		public LuaTexture() {}
		public LuaTexture(double x, double y, int c, String text, double rotation, boolean colored,double opacity,float scale) {
			this.xCoord = x;
			this.yCoord = y;
			this.c = c;
			this.s = text;
			this.rotation = rotation;
			this.colored = colored;
			this.opacity = opacity;
			this.scale = scale;
		}
		@Override
		public String[] getMethodNames() {
			return new String[]{"getTexture","getX","getY","getColor","setTexture","setX","setY","setColor","delete","getRotation","setRotation","setColored","getScale","setScale"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method,
				Object[] a) throws LuaException, InterruptedException {
			if(!textureList.contains(this)) throw new LuaException("This object was already deleted");
			if(method == 0) return new Object[]{s};
			else if(method == 1) return new Object[]{xCoord};
			else if(method == 2) return new Object[]{yCoord};
			else if(method == 3) return new Object[]{this.colored,c,this.opacity};
			else if(method == 4){
				if(a.length > 0 && a[0] != null){
					this.s = a[0].toString();
				}else{
					throw new LuaException("Invalid Arguments, excepted (String)");
				}
			}else if(method == 5){
				if(a.length > 0 && a[0] instanceof Double){
					this.xCoord = (Double) a[0];
				}else{
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}else if(method == 6){
				if(a.length > 0 && a[0] instanceof Double){
					this.yCoord = (Double) a[0];
				}else{
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}else if(method == 7){
				if(a.length > 0 && a[0] instanceof Double){
					this.c = MathHelper.floor_double((Double) a[0]);
					if(a.length > 1 && a[1] instanceof Double){
						this.opacity = (Double) a[1];
					}
				}else{
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}else if(method == 8){
				textureList.remove(this);
			}else if(method == 9) return new Object[]{this.rotation};
			else if(method == 10){
				if(a.length > 0 && a[0] instanceof Double){
					this.rotation = (Double) a[0];
				}else{
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}else if(method == 11){
				if(a.length > 0 && a[0] instanceof Boolean){
					this.colored = (Boolean) a[0];
				}else{
					throw new LuaException("Invalid Arguments, excepted (boolean)");
				}
			}else if(method == 12) return new Object[]{this.scale};
			else if(method == 13){
				if(a.length > 0 && a[0] instanceof Double){
					this.scale = new Float((Double) a[0]);
				}else{
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}
			return null;
		}

	}
	public class LuaText implements ILuaObject{
		public String s = "";
		public double xCoord = 0;
		public double yCoord = 0;
		public int c = 0;
		public double rotation = 0;
		public float scale = 1;
		public LuaText() {}
		public LuaText(double x, double y, int c, String text, double rotation, float scale) {
			this.xCoord = x;
			this.yCoord = y;
			this.c = c;
			this.s = text;
			this.rotation = rotation;
			this.scale = scale;
		}

		@Override
		public String[] getMethodNames() {
			return new String[]{"getText","getX","getY","getColor","setText","setX","setY","setColor","delete","getRotation","setRotation","getScale","setScale"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method,
				Object[] a) throws LuaException, InterruptedException {
			if(!textList.contains(this)) throw new LuaException("This object was already deleted");
			if(method == 0) return new Object[]{s};
			else if(method == 1) return new Object[]{xCoord};
			else if(method == 2) return new Object[]{yCoord};
			else if(method == 3) return new Object[]{c};
			else if(method == 4){
				if(a.length > 0 && a[0] != null){
					this.s = a[0].toString();
				}else{
					throw new LuaException("Invalid Arguments, excepted (String)");
				}
			}else if(method == 5){
				if(a.length > 0 && a[0] instanceof Double){
					this.xCoord = (Double) a[0];
				}else{
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}else if(method == 6){
				if(a.length > 0 && a[0] instanceof Double){
					this.yCoord = (Double) a[0];
				}else{
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}else if(method == 7){
				if(a.length > 0 && a[0] instanceof Double){
					this.c = MathHelper.floor_double((Double) a[0]);
				}else{
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}else if(method == 8){
				textList.remove(this);
			}else if(method == 9) return new Object[]{this.rotation};
			else if(method == 10){
				if(a.length > 0 && a[0] instanceof Double){
					this.rotation = (Double) a[0];
				}else{
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}else if(method == 11) return new Object[]{this.scale};
			else if(method == 12){
				if(a.length > 0 && a[0] instanceof Double){
					this.scale = new Float((Double) a[0]);
				}else{
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}
			return null;
		}

	}
	@Override
	public void postUpdate() {
		int d = worldObj.getBlockState(pos).getValue(BlockMonitorBase.FACING).ordinal();
		if (d == 5) this.direction = 4;
		else if(d == 4) this.direction = 5;
		else if (d == 3) this.direction = 2;
		else if(d == 2) this.direction = 3;
		else if(d == 0) this.direction = 1;
		else if(d == 1) this.direction = 0;
	}
	@Override
	public EnumFacing getFacing() {
		return worldObj.getBlockState(pos).getValue(BlockMonitorBase.FACING);
	}
}
