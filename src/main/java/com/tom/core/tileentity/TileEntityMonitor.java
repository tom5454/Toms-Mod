package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import com.tom.lib.Configs;
import com.tom.lib.api.tileentity.ITMPeripheral.ITMCompatPeripheral;

public class TileEntityMonitor extends TileEntityMonitorBase implements ITMCompatPeripheral {
	private int posX = 0;
	private int posY = 0;
	private int posZ = 0;

	public TileEntityMonitor() {
		super(16);
	}

	public int color;
	public String pName = "tm_basicMonitor";
	private List<IComputer> computers = new ArrayList<>();
	public String[] methods = {"fill", "filledRectangle", "rectangle", "listMethods", "setSize", "getSize", "sync", "clear", "addText", "addTexture"};

	@Override
	public String getType() {
		return this.pName;
	}

	@Override
	public String[] getMethodNames() {
		return this.methods;
	}

	@Override
	public Object[] callMethod(IComputer computer, int method, Object[] a) throws LuaException {
		Object[] ret = new Object[1];
		ret[0] = false;
		if (method == 0) {
			int color;
			if (a.length == 0) {
				color = 0x000000;
			} else if ((a[0] instanceof Double) && (a.length > 0)) {
				color = MathHelper.floor((Double) a[0]);
			} else {
				throw new LuaException("Bad argument #1 (expected number)");
			}
			if (color >= 0) {
				for (int i = 0;i < getSize();i++) {
					for (int j = 0;j < getSize();j++) {
						this.screen[i][j] = color;
					}
				}
				ret[0] = true;
			} else {
				throw new LuaException("Bad Argument #1, (too small number (" + color + ") minimum value is 0 )");
			}
		} else if (method == 1) {
			int color;
			if (a.length < 4) {
				throw new LuaException("Too few arguments (expected x,y,width,height,[color])");
			} else if ((a.length >= 4) && !(a[0] instanceof Double)) {
				throw new LuaException("Bad argument #1 (expected Number)");
			} else if ((a.length >= 4) && !(a[1] instanceof Double)) {
				throw new LuaException("Bad argument #2 (expected Number)");
			} else if ((a.length >= 4) && !(a[2] instanceof Double)) {
				throw new LuaException("Bad argument #3 (expected Number)");
			} else if ((a.length >= 4) && !(a[3] instanceof Double)) {
				throw new LuaException("Bad argument #4 (expected Number)");
			} else if ((a.length >= 5) && !(a[4] instanceof Double)) {
				throw new LuaException("Bad argument #5 (expected Number)");
			} else {
				if (a.length < 5) {
					color = 0x000000;
				} else {
					color = MathHelper.floor((Double) a[4]);
				}
				if (color < 0)
					throw new LuaException("Bad Argument #5, (too small number (" + color + ") minimum value is 0 )");
				int xStart = MathHelper.floor((Double) a[0]) - 1;
				int yStart = MathHelper.floor((Double) a[1]) - 1;
				int xStop = xStart + MathHelper.floor((Double) a[2]);
				int yStop = yStart + MathHelper.floor((Double) a[3]);
				if (xStart < getSize() + 1 && yStart < getSize() + 1 && xStart >= 0 && yStart >= 0) {
					xStop = xStop > getSize() ? getSize() : xStop;
					yStop = yStop > getSize() ? getSize() : yStop;
					for (int i = xStart;i < xStop;i++) {
						for (int y = yStart;y < yStop;y++) {
							this.screen[y][i] = color;
						}
					}
					ret[0] = true;
				} else {
					throw new LuaException("Out of boundary");
				}
			}
		} else if (method == 2) {
			int color;
			if (a.length < 4) {
				throw new LuaException("Too few arguments (expected x,y,width,height,[color])");
			} else if ((a.length >= 4) && !(a[0] instanceof Double)) {
				throw new LuaException("Bad argument #1 (expected Number)");
			} else if ((a.length >= 4) && !(a[1] instanceof Double)) {
				throw new LuaException("Bad argument #2 (expected Number)");
			} else if ((a.length >= 4) && !(a[2] instanceof Double)) {
				throw new LuaException("Bad argument #3 (expected Number)");
			} else if ((a.length >= 4) && !(a[3] instanceof Double)) {
				throw new LuaException("Bad argument #4 (expected Number)");
			} else if ((a.length >= 5) && !(a[4] instanceof Double)) {
				throw new LuaException("Bad argument #5 (expected Number)");
			} else {
				if (a.length < 5) {
					color = 0x000000;
				} else {
					color = MathHelper.floor((Double) a[4]);
				}
				if (color < 0)
					throw new LuaException("Bad Argument #5, (too small number (" + color + ") minimum value is 0 )");
				int xStart = MathHelper.floor((Double) a[0]) - 1;
				int yStart = MathHelper.floor((Double) a[1]) - 1;
				int xStop = xStart + MathHelper.floor((Double) a[2]);
				int yStop = yStart + MathHelper.floor((Double) a[3]);
				if (xStart < getSize() + 1 && yStart < getSize() + 1 && xStart >= 0 && yStart >= 0) {
					xStop = xStop > getSize() ? getSize() : xStop;
					yStop = yStop > getSize() ? getSize() : yStop;
					for (int i = xStart;i < xStop;i++) {
						for (int y = yStart;y < yStop;y++) {
							this.screen[i][y] = (i == xStart || i == xStop - 1) ? color : ((y == yStart || y == yStop - 1) ? color : this.screen[i][y]);
						}
					}
					ret[0] = true;
				} else {
					throw new LuaException("Out of boundary");
				}
			}
		} else if (method == 3) {
			Object[] o = new Object[methods.length];
			for (int i = 0;i < o.length;i++) {
				o[i] = methods[i];
			}
			return o;
		} else if (method == 4) {
			int s = MathHelper.floor((Double) a[0]);
			if (s < 1)
				throw new LuaException("Bad Argument #1, (too small number (" + s + ") minimum value is 1 )");
			if (s > Configs.monitorSize)
				throw new LuaException("Bad Argument #1, (too big number (" + s + ") maximum value is " + Configs.monitorSize + " )");
			this.screen = new int[s][s];
			this.setSize(s);
			ret[0] = true;
		} else if (method == 5) {
			return new Object[]{this.getSize(), this.getSize()};
		} else if (method == 6) {
			this.markDirty();
			markBlockForUpdate(pos);
			ret[0] = true;
		} else if (method == 7) {
			this.textList.clear();
			this.textureList.clear();
			ret[0] = true;
		} else if (method == 8) {
			if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double) {
				int x = MathHelper.floor((Double) a[0]);
				int y = MathHelper.floor((Double) a[1]);
				int c = a.length > 3 && a[3] instanceof Double ? MathHelper.floor((Double) a[3]) : 0xFFFFFF;
				String s = a[2].toString();
				LuaText t = new LuaText(x, y, c, s, 0, 1);
				this.textList.put(t);
				return new Object[]{true, t};
			} else {
				throw new LuaException("Invalid Arguments, excepted (number,number,String,[number])");
			}
		} else if (method == 9) {
			if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double) {
				int x = MathHelper.floor((Double) a[0]);
				int y = MathHelper.floor((Double) a[1]);
				int c = a.length > 3 && a[3] instanceof Double ? MathHelper.floor((Double) a[3]) : 0xFFFFFFFF;
				String s = a[2].toString();
				LuaTexture t = new LuaTexture(x, y, c, s, 0, false, 1, 1);
				this.textureList.put(t);
				return new Object[]{true, t};
			} else {
				throw new LuaException("Invalid Arguments, excepted (number,number,String,[number])");
			}
		}
		// screenS = this.read(this.getRow(xCoord, yCoord, zCoord));
		/*if (method == 0){
			if (arguments.length == 0){
				arguments[0] = false;
			}else if (!(arguments[0] instanceof Boolean) && !(arguments.length == 0)){
				throw new LuaException("Bad argument #1 (expected boolean)");
			}else{
				//Everything is good
				boolean mode = (Boolean) arguments[0];
				for (int i = 0;i < 16;i++)
						//screenS[i] = this.setRow(this.fill(mode));
				ret[0] = true;
			}
		}else if (method == 1){
			if (arguments.length < 4){
				throw new LuaException("Too few arguments (expected x,y,width,height,[color])");
			} else if(!(arguments[0] instanceof Integer) && !(arguments.length < 4)){
				throw new LuaException("Bad argument #1 (expected Integer)");
			} else if(!(arguments[1] instanceof Integer) && !(arguments.length < 4)){
				throw new LuaException("Bad argument #2 (expected Integer)");
			} else if(!(arguments[2] instanceof Integer) && !(arguments.length < 4)){
				throw new LuaException("Bad argument #3 (expected Integer)");
			} else if(!(arguments[3] instanceof Integer) && !(arguments.length < 4)){
				throw new LuaException("Bad argument #4 (expected Integer)");
			} else if(!(arguments[4] instanceof Boolean) && !(arguments.length < 5)){
				throw new LuaException("Bad argument #5 (expected Boolean)");
			} else {
				//Everything is good
				if (arguments.length > 5){
					arguments[4] = true;
				}
				//Get arguments
				boolean mode = (Boolean) arguments[4];
				int xStart = (Integer) arguments[0];
				int yStart = (Integer) arguments[1];
				int xStop = xStart + (Integer) arguments[2];
				int yStop = yStart + (Integer) arguments[3];
				//Regulate numbers
				if (xStart < 17 && yStart < 17 && xStart > 0 && yStart > 0){
					xStop = xStop > 16 ? 16 : xStop;
					yStop = yStop > 16 ? 16 : yStop;
					//Override
					for (int i = xStart; i < xStop;i++){
						//boolean[] current = this.read(this.getRow(xCoord, yCoord, zCoord), i);
						for (int y = yStart;y < yStop;y++){
							//current[y] = mode;
						}
						//this.screenS[i] = this.setRow(current);
					}
					ret[0] = true;
				} else {
					//Out of boundary
					ret[0] = false;
				}
			}
		}else if (method == 1){
			if (arguments.length < 4){
				throw new LuaException("Too few arguments (expected x,y,width,height,[color])");
			} else if(!(arguments[0] instanceof Integer) && !(arguments.length < 4)){
				throw new LuaException("Bad argument #1 (expected Integer)");
			} else if(!(arguments[1] instanceof Integer) && !(arguments.length < 4)){
				throw new LuaException("Bad argument #2 (expected Integer)");
			} else if(!(arguments[2] instanceof Integer) && !(arguments.length < 4)){
				throw new LuaException("Bad argument #3 (expected Integer)");
			} else if(!(arguments[3] instanceof Integer) && !(arguments.length < 4)){
				throw new LuaException("Bad argument #4 (expected Integer)");
			} else if(!(arguments[4] instanceof Boolean) && !(arguments.length < 5)){
				throw new LuaException("Bad argument #5 (expected Boolean)");
			} else {
				//Everything is good
				if (arguments.length > 5){
					arguments[4] = true;
				}
				//Get arguments
				boolean mode = (Boolean) arguments[4];
				int xStart = (Integer) arguments[0];
				int yStart = (Integer) arguments[1];
				int xStop = xStart + (Integer) arguments[2];
				int yStop = yStart + (Integer) arguments[3];
				//Regulate numbers
				if (xStart < 17 && yStart < 17 && xStart > 0 && yStart > 0){
					xStop = xStop > 16 ? 16 : xStop;
					yStop = yStop > 16 ? 16 : yStop;
					//Override
					for (int i = xStart; i < xStop;i++){
						//boolean[] current = this.read(this.getRow(xCoord,yCoord,zCoord),i);
						for (int y = yStart;y < yStop;y++){
							//current[y] = (i == xStart || i == xStop) ? mode : ((y == yStart || y == yStop) ? mode : current[y]);
						}
						//this.screenS[i] = this.setRow(current);
					}
					ret[0] = true;
				} else {
					//Out of boundary
					ret[0] = false;
				}
			}
		}else if (method == 3){

		}
		if ((Boolean) ret[0]){
			//this.write(screenS, this.getRow(xCoord, yCoord, zCoord));
		}*/
		return ret;
	}

	@Override
	public void attach(IComputer computer) {
		computers.add(computer);

	}

	@Override
	public void detach(IComputer computer) {
		computers.remove(computer);

	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		color = tag.getInteger("color");
		this.posX = tag.getInteger("posX");
		this.posY = tag.getInteger("posY");
		this.posZ = tag.getInteger("posZ");
		// screen[0] = tag.getIntArray("s");
		/*width = tag.getInteger("width");
		height = tag.getInteger("height");
		this.properties[0] = color;
		this.properties[1] = width;
		this.properties[2] = height;
		this.properties[3] = this.blockMetadata;*/
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("color", color);
		tag.setInteger("posX", this.posX);
		tag.setInteger("posY", this.posY);
		tag.setInteger("posZ", this.posZ);
		// tag.setIntArray("s", this.screen[0]);
		/*tag.setInteger("width", width);
		tag.setInteger("height", height);*/
		return tag;
	}
	/*private boolean[] getRow(String string){
		boolean[] ret = new boolean[16];
		for(int i=0; i<16; i++){
			String current = string.substring(i+1, i+1);
			if (current == "1"){
				ret[i] = true;
			} else {
				ret[i] = false;
			}
		}
		//return this.read(pos, line);
	}
	private int[] setRow(boolean[] table){
		//for (int i=1;i<table.length;i++){
		//	boolean current = table[i];
		//	string = string + (current ? "1" : "0");
		//}
		return TomsMathHelper.toInt(table);
	}

	/*public void writeToPacket(ByteBuf buf){
		buf.writeInt(this.color);
		int[][] current = TomsMathHelper.compressInt(this.read(this.getRow(xCoord, yCoord, zCoord)));
		for(int i = 0;i<16;i++){
			buf.writeInt(current[i][0]);
			buf.writeInt(current[i][1]);
		}
		//System.out.println("write to packet");
	}

	public void readFromPacket(ByteBuf buf){
		this.color = buf.readInt();
		int[][] current = {};
		for(int i = 0;i<16;i++){
			current[i][0] = buf.readInt();
			current[i][1] = buf.readInt();
		}
		screenS = TomsMathHelper.decompressInt(current);
	}*/

	public int getColor() {
		return this.color;
	}

	/*public void updateEntity(){
		//System.out.println("update");
		this.register(xCoord, yCoord, zCoord);
	}

	public void setMeta(int meta){
		this.blockMetadata = meta;
	}

	public void setColor(int color) {
		this.color = color;
	}

	/*public void onBlockPlaced(World world, int x, int y, int z){
		this.register(x, y, z);
	}

	private void register(int x, int y, int z){
		int[] list = TomsMathHelper.find(screen, x, y, z, color);
		boolean found = list[0] == 1;
		if (!found){
			int last = screen != null ? screen.length + 1 : 0;
			int[] coords = {xCoord, yCoord, zCoord};
			int[][][] current = {};
			int[][] properties = {};
			int[][] screenData = {};
			int[] row = {};
			properties[0] = coords;
			properties[1][0] = this.color;
			for(int i = 0;i<15;i++) row[i] = 0;
			for(int i = 0;i<15;i++) screenData[i] = row;
			current[0] = properties;
			current[1] = screenData;
			//Save
			screen[last] = current;
		}
	}

	private void write(int[][] table, int pos){
		screen[pos][1] = table;
	}

	private boolean[] read(int pos, int line){
		boolean[] ret = new boolean[15];
		for(int part = 0;part<16;part++){
		int current = screen[pos][1][line][part];
			if (current == 1){
				ret[part] = true;
			} else {
				ret[part] = false;
			}
		}
		return ret;
	}

	private int getRow(int x, int y, int z){
		int[] list = TomsMathHelper.find(screen, x, y, z, color);
		return list[1];
	}

	private boolean[] fill(boolean mode){
		boolean[] current = new boolean[15];
		for(int i=0;i<current.length;i++){
			current[i] = mode;
		}
		return current;
	}

	private int[][] read(int pos){
		return screen[pos][1];
	}

	public int[][] getScreen(){
		return screenS;
	}

	public int[] getProperties() {
		return this.properties;
	}*/
	public boolean onBlockActivated(boolean onServer, EnumFacing side, float x, float y, float z, EntityPlayer player) {
		// System.out.println("onBlockActivated");
		if (onServer && side.ordinal() == this.direction) {
			TileEntity tile = world.getTileEntity(new BlockPos(posX, posY, posZ));
			if (tile != null && tile instanceof TileEntityGPU) {
				if (this.direction > 1) {
					float yPos = 1F - y;
					float xPos = x == this.THICKNESS ? z : x;
					if (this.direction % 2 == 1)
						xPos = 1F - xPos;
					int xP = MathHelper.floor(xPos * this.getSize());
					int yP = MathHelper.floor(yPos * this.getSize());
					int xCoord = pos.getX();
					int yCoord = pos.getY();
					int zCoord = pos.getZ();
					((TileEntityGPU) tile).monitorClick(xCoord, yCoord, zCoord, xP, yP);
				} else {

				}
			}
			// System.out.println("m: " + x + " " + y + " " + z);
		}
		return side.ordinal() == this.direction;
	}

	@Override
	public TileEntityMonitorBase connect(int x, int y, int z) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		return this;
	}

}
