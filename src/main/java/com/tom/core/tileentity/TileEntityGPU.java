package com.tom.core.tileentity;

import static com.tom.api.energy.EnergyType.LV;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;
import com.tom.core.font.Font;
import com.tom.core.font.Font.CustomFont;
import com.tom.lib.Configs;
import com.tom.lib.api.tileentity.ITMPeripheral;
import com.tom.thirdparty.waila.IIntegratedMultimeter;
import com.tom.util.DualOutputStream;
import com.tom.util.IDList;
import com.tom.util.TomsModUtils;

import com.tom.core.block.BlockMonitorBase;

public class TileEntityGPU extends TileEntityTomsMod implements ITMPeripheral, IEnergyReceiver, IIntegratedMultimeter/*, ILinkable*/ {
	public static void init(){
		Font f = null;
		try{f = Font.load("ascii");}catch(Throwable e){
			e.printStackTrace();
			f = Font.MISSING;
		}
		if(f == null)CoreInit.proxy.error("Missing ascii.bin file from the mod JAR!! Please redownload the mod from curse! DO NOT REPORT THIS!!");
		if(f == Font.MISSING)CoreInit.proxy.error("The ascii.bin file is corrupted, please check if its overwritten in your config folder (tomsmod/fonts/ascii.bin exists) if so delete it try restarting if it still errors please redownload the mod from curse! DO NOT REPORT THIS!!");
		fonts.put("ascii", f);
		DEF = f;
	}
	public static Map<String, Font> fonts = new HashMap<>();
	public Map<String, CustomFont> internalFonts = new HashMap<>();
	public static Font DEF;
	public Font selectedFont = DEF;
	public static final String[] methods = {"fill", "filledRectangle", "rectangle", "listMethods", "setSize", "getSize", "sync", "clear", "addText",
			"addTexture", "loadFromBackup", "getEnergyStored", "getMaxEnergyStored", "getEnergyUsage", "drawText", "getFont", "setFont",
			"getTextLength", "drawTextSmart", "setFontDefaultCharID", "getFontDefaultCharID", "addNewChar", "delChar", "freeChars",
			"clearChars", "drawChar", "drawBuffer"};
	public static final List<String> methods2 = Arrays.asList(methods);
	private List<IComputer> computers = new ArrayList<>();
	private EnergyStorage energy = new EnergyStorage(1000000, 10000, 100000);
	// private TileEntityMonitorBase[][] monitors;
	private static final int[][] blockPositions = new int[][]{{0, -1, 0}, {0, 1, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
	// private static final int[][] metaPositions = new
	// int[][]{{-1,0,-1},{-1,0,1},{-1,1,0},{1,1,0},{0,1,1},{0,1,-1}};
	// private static final EnumFacing[][] disabledFD = new
	// EnumFacing[][]{{UP,DOWN}};
	/*private int posX = 0;
	private int posY = 0;
	private int posZ = 0;*/
	// private Map<Integer,List<Entry<Integer,Integer>>> monitors = new
	// HashMap<Integer,List<Entry<Integer,Integer>>>();
	private int maxX = 0;
	private int maxY = 0;
	private int size = 16;
	private int[][] screen = new int[16][16];
	private int[][] screenOld = new int[16][16];
	public IDList<LuaText> textList = new IDList<>();
	public IDList<LuaTexture> textureList = new IDList<>();
	private boolean deviceOutOfPower = false;

	@Override
	public String getType() {
		return "tm_gpu";
	}

	@Override
	public String[] getMethodNames() {
		return methods;
	}

	@Override
	public Object[] call(IComputer computer, String methodIn, Object[] a) throws LuaException {
		if (!methodIn.equals("listMethods") || !methodIn.equals("getEnergyStored") || !methodIn.equals("getMaxEnergyStored")) {
			int energyR = methodIn.equals("getSize") || methodIn.equals("getEnergyUsage") ? 1 : methodIn.equals("drawTextSmart") ? 5 : 2;
			double ee = energy.extractEnergy(energyR, true);
			if (ee != energyR)
				throw new LuaException("Device is out of power");
			this.energy.extractEnergy(ee, false);
		}
		List<List<TileEntityMonitorBase>> monitors = this.connectMonitors(this.findMonitor());
		int maxX = this.maxX * this.size;
		int maxY = this.maxY * this.size;
		Object[] ret = new Object[1];
		ret[0] = false;
		int method = methods2.indexOf(methodIn);
		if (method == 0) {//fill
			int color;
			if (a.length == 0) {
				color = 0x000000;
			} else if ((a[0] instanceof Double) && (a.length > 0)) {
				color = MathHelper.floor((Double) a[0]);
			} else {
				throw new LuaException("Bad argument #1 (expected number)");
			}
			if (color >= 0) {
				for (int i = 0;i < maxX;i++) {
					for (int j = 0;j < maxY;j++) {
						this.screen[i][j] = color;
					}
				}
				ret[0] = true;
			} else {
				throw new LuaException("Bad Argument #1, (too small number (" + color + ") minimum value is 0 )");
			}
		} else if (method == 1) {//filledRectangle
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
				if (xStart < maxX + 1 && yStart < maxY + 1 && xStart >= 0 && yStart >= 0) {
					xStop = xStop > maxX ? maxX : xStop;
					yStop = yStop > maxY ? maxY : yStop;
					for (int i = xStart;i < xStop;i++) {
						for (int y = yStart;y < yStop;y++) {
							this.screen[i][y] = color;
						}
					}
					ret[0] = true;
				} else {
					throw new LuaException("Out of boundary");
				}
			}
		} else if (method == 2) {//rectangle
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
				if (xStart < maxX + 1 && yStart < maxY + 1 && xStart >= 0 && yStart >= 0) {
					xStop = xStop > maxX ? maxX : xStop;
					yStop = yStop > maxY ? maxY : yStop;
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
		} else if (method == 3) {//listMethods
			Object[] o = new Object[methods.length];
			for (int i = 0;i < o.length;i++) {
				o[i] = methods[i];
			}
			return o;
		} else if (method == 4) {//setSize
			int s = MathHelper.floor((Double) a[0]);
			if (s < 16)
				throw new LuaException("Bad Argument #1, (too small number (" + s + ") minimum value is 16 )");
			if (s > Configs.monitorSize)
				throw new LuaException("Bad Argument #1, (too big number (" + s + ") maximum value is " + Configs.monitorSize + " )");
			this.screen = new int[s * this.maxX][s * this.maxY];
			this.size = s;
			for (List<TileEntityMonitorBase> cMonList : monitors) {
				for (TileEntityMonitorBase mon : cMonList) {
					if (mon != null) {
						mon.setSize(this.size);
						mon.screen = new int[size][size];
					}
				}
			}
			ret[0] = true;
		} else if (method == 5) {//getSize
			return new Object[]{this.size, this.size, this.maxX, this.maxY, this.size * this.maxX, this.size * this.maxY};
		} else if (method == 6) {//sync
			this.sync();
			ret[0] = true;
		} else if (method == 7) {//clear
			this.textList.clear();
			this.textureList.clear();
			this.screen = new int[this.maxX*this.size][this.maxY*this.size];
			ret[0] = true;
		} else if (method == 8) {//addText
			if(a.length < 3){
				throw new LuaException("Too few arguments, excepted (number x,number y,String text, [number color])");
			}
			if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double && a[2] != null) {
				int x = MathHelper.floor((Double) a[0]);
				int y = MathHelper.floor((Double) a[1]);
				int c = a.length > 3 && a[3] instanceof Double ? MathHelper.floor((Double) a[3]) : 0xFFFFFF;
				String s = a[2].toString();
				LuaText t = new LuaText(x, y, c, s, 0, 1);
				this.textList.put(t);
				return new Object[]{true, t};
			} else {
				throw new LuaException("Invalid Arguments, excepted (number x,number y,String text, [number color])");
			}
		} else if (method == 9) {//addTexture
			if(a.length < 3){
				throw new LuaException("Too few arguments, excepted (number x,number y,String location, [number color])");
			}
			if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double && a[2] != null) {
				int x = MathHelper.floor((Double) a[0]);
				int y = MathHelper.floor((Double) a[1]);
				int c = a.length > 3 && a[3] instanceof Double ? MathHelper.floor((Double) a[3]) : 0xFFFFFFFF;
				String s = a[2].toString();
				LuaTexture t = new LuaTexture(x, y, c, s, 0, false, 1, 1);
				this.textureList.put(t);
				return new Object[]{true, t};
			} else {
				throw new LuaException("Invalid Arguments, excepted (number x,number y,String location, [number color])");
			}
		} else if (method == 10) {//loadFromBackup
			if (this.deviceOutOfPower) {
				this.deviceOutOfPower = false;
				this.screen = this.screenOld;
				this.sync();
				return new Object[]{true};
			} else
				return new Object[]{false};
		} else if (method == 11)//getEnergyStored
			return new Object[]{this.energy.getEnergyStored()};
		else if (method == 12)//getMaxEnergyStored
			return new Object[]{this.energy.getMaxEnergyStored()};
		else if (method == 13)//getEnergyUsage
			return new Object[]{this.maxX * this.maxY};
		else if (method == 14){//drawText
			if(a.length < 3){
				throw new LuaException("Too few arguments, excepted (number x,number y,String text, [number text_color], [number bg_color], [number size], [number padding])");
			}
			if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double && a[2] != null) {
				int x = MathHelper.floor((Double) a[0]) - 1;
				int y = MathHelper.floor((Double) a[1]) - 1;
				int c = a.length > 3 && a[3] instanceof Double ? MathHelper.floor((Double) a[3]) : 0xFFFFFFFF;
				int bg = a.length > 4 && a[4] instanceof Double ? MathHelper.floor((Double) a[4]) : -1;
				int size = a.length > 5 && a[5] instanceof Double ? MathHelper.floor((Double) a[5]) : 1;
				int padding = a.length > 6 && a[6] instanceof Double ? MathHelper.floor((Double) a[6]) : 1;
				String s = a[2].toString();
				char[] chars = s.toCharArray();
				int l = getTextLength(chars, size, padding);
				if(x < 0 || (l+x) > maxX){
					throw new LuaException("Out of boundary x");
				}
				if(y < 0 || (y + selectedFont.fontHeight) > maxY){
					throw new LuaException("Out of boundary y");
				}
				int wx = x;
				try{
					for (int i = 0;i < chars.length;i++) {
						char d = chars[i];
						int index = selectedFont.chars2.indexOf(d);
						if(index == -1)index = selectedFont.UNKNOWN;
						int[] charData = selectedFont.chars[index];
						int w = selectedFont.widths[index];
						if(d == ' '){
							w = 5;
							if(bg > -1){
								for (int j = 0;j < charData.length;j++) {
									for(int k = 0;k<w;k++){
										fill(wx, k, y, j, size, bg);
									}
								}
							}
						}else{
							for (int j = 0;j < charData.length;j++) {
								int b = charData[j];
								for(int k = 0;k<w;k++){
									if((b & (1 << k)) != 0){
										fill(wx, k, y, j, size, c);
									}else if(bg > -1){
										fill(wx, k, y, j, size, bg);
									}
								}
							}
						}
						if(bg > -1){
							for (int j = 0;j < charData.length;j++) {
								for(int k = 0;k<padding;k++){
									fill(wx, w + k, y, j, size, bg);
								}
							}
						}
						wx += ((w + padding) * size);
					}
				}catch(IndexOutOfBoundsException e){
					e.printStackTrace();
				}
				ret[0] = true;
			}else{
				throw new LuaException("Invalid Arguments, excepted (number x,number y,String text, [number text_color], [number bg_color], [number size], [number padding])");
			}
		}else if(method == 15){//getFont
			return new Object[]{selectedFont.name, selectedFont.editable()};
		}else if(method == 16){//setFont
			if(a.length > 0 && a[0] != null){
				Font f = getOrLoadFont(internalFonts, a[0].toString());
				if(f != null)selectedFont = f;
				ret[0] = true;
			}else throw new LuaException("Invalid Arguments, excepted (string font_name)");
		}else if(method == 17){//getTextLength
			if(a.length > 0 && a[0] != null){
				int size = a.length > 1 && a[1] instanceof Double ? MathHelper.floor((Double) a[1]) : 1;
				int padding = a.length > 2 && a[2] instanceof Double ? MathHelper.floor((Double) a[2]) : 1;
				return new Object[]{getTextLength(a[0].toString().toCharArray(), size, padding)};
			}else throw new LuaException("Invalid Arguments, excepted (string text, [number size], [number padding])");
		}else if(method == 18){//drawTextSmart
			if(a.length < 3){
				throw new LuaException("Too few arguments, excepted (number x,number y,String text, [number text_color], [number bg_color], [boolean force_unicode], [number size], [number padding])");
			}
			if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double && a[2] != null) {
				int x = MathHelper.floor((Double) a[0]) - 1;
				int y = MathHelper.floor((Double) a[1]) - 1;
				int c = a.length > 3 && a[3] instanceof Double ? MathHelper.floor((Double) a[3]) : 0xFFFFFFFF;
				int bg = a.length > 4 && a[4] instanceof Double ? MathHelper.floor((Double) a[4]) : -1;
				boolean force_unicode = a.length > 5 && a[5] instanceof Boolean ? (Boolean) a[5] : false;
				int sizeIn = a.length > 6 && a[6] instanceof Double ? MathHelper.floor((Double) a[6]) : 1;
				int padding = a.length > 7 && a[7] instanceof Double ? MathHelper.floor((Double) a[7]) : 1;
				String s = a[2].toString();
				char[] chars = s.toCharArray();
				int l = getTextLength(chars, sizeIn, padding);
				if(x < 0 || (l+x) > maxX){
					throw new LuaException("Out of boundary x");
				}
				if(y < 0 || (y + selectedFont.fontHeight) > maxY){
					throw new LuaException("Out of boundary y");
				}
				int wx = x;
				String font = selectedFont.name;
				try{
					for (int i = 0;i < chars.length;i++) {
						int size = sizeIn;
						char d = chars[i];
						if(!force_unicode && d < 256){
							size *= 2;
							selectedFont = getOrLoadFont(internalFonts, "ascii");
						}else{
							selectedFont = getOrLoadFont(internalFonts, String.format("unicode_page_%02x", d / 256));
						}
						int index = selectedFont.chars2.indexOf(d);
						if(index == -1)index = selectedFont.UNKNOWN;
						int[] charData = selectedFont.chars[index];
						int w = selectedFont.widths[index];
						if(d == ' '){
							w = 5;
							if(bg > -1){
								for (int j = 0;j < charData.length;j++) {
									for(int k = 0;k<w;k++){
										fill(wx, k, y, j, size, bg);
									}
								}
							}
						}else{
							for (int j = 0;j < charData.length;j++) {
								int b = charData[j];
								for(int k = 0;k<w;k++){
									if((b & (1 << k)) != 0){
										fill(wx, k, y, j, size, c);
									}else if(bg > -1){
										fill(wx, k, y, j, size, bg);
									}
								}
							}
						}
						if(bg > -1){
							for (int j = 0;j < charData.length;j++) {
								for(int k = 0;k<padding;k++){
									fill(wx, w + k, y, j, size, bg);
								}
							}
						}
						wx += ((w + padding) * size);
					}
				}catch(IndexOutOfBoundsException e){
					e.printStackTrace();
				}
				selectedFont = getOrLoadFont(internalFonts, font);
				ret[0] = true;
			}else{
				throw new LuaException("Invalid Arguments, excepted (number x,number y,String text, [number text_color], [number bg_color], [number size], [number padding])");
			}
		}else if(method == 19){//setFontDefaultCharID
			if(!selectedFont.editable())throw new LuaException("Selected font is not modifiable");
			if(a.length > 0 && a[0] instanceof Double){
				int id = MathHelper.floor((Double) a[0]) - 1;
				if (id < 0)
					throw new LuaException("Bad Argument #1, (too small number (" + id + ") minimum value is 1 )");
				if (id > 255)
					throw new LuaException("Bad Argument #1, (too big number (" + id + ") maximum value is 256 )");
				selectedFont.UNKNOWN = id;
				ret[0] = true;
			}
		}else if(method == 20){//getFontDefaultCharID
			return new Object[]{selectedFont.UNKNOWN};
		}else if(method == 21){//addNewChar
			if(!selectedFont.editable())throw new LuaException("Selected font is not modifiable");
			if(a.length > 17 && Arrays.stream(a).allMatch(e -> e != null)){
				String c = a[0].toString();
				if(c.length() != 1)throw new LuaException("Bad Argument #1 a sigle character expected");
				Object[] data = Arrays.copyOfRange(a, 1, 18);
				if(Arrays.stream(data).allMatch(e -> e instanceof Double)){
					int[] d = Arrays.stream(data).mapToDouble(n -> (Double)n).mapToInt(MathHelper::floor).toArray();
					return new Object[]{selectedFont.addChar(c, d)};
				}else{
					throw new LuaException("Bad Argument #2 - 19 Numbers expected");
				}
			}else throw new LuaException("Invalid arguments, expected (string char, number width, 16 x number char_data)");
		}else if(method == 22){//delChar
			if(!selectedFont.editable())throw new LuaException("Selected font is not modifiable");
			if(a.length > 0 && a[0] != null){
				String c = a[0].toString();
				if(c.length() != 1)throw new LuaException("Bad Argument #1 a sigle character expected");
				selectedFont.remove(c);
				ret[0] = true;
			}else throw new LuaException("Invalid arguments, expected (string char)");
		}else if(method == 23){//freeChars
			return new Object[]{selectedFont.freeChars()};
		}else if(method == 24){//clearChars
			selectedFont.clear();
			ret[0] = true;
		}else if(method == 25){//drawChar
			if(a.length < 3){
				throw new LuaException("Too few arguments, excepted (number x,number y,number char, [number text_color], [number bg_color], [number size])");
			}
			if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double && a[2] instanceof Double) {
				int x = MathHelper.floor((Double) a[0]) - 1;
				int y = MathHelper.floor((Double) a[1]) - 1;
				int index = MathHelper.floor((Double) a[2]) - 1;
				int c = a.length > 3 && a[3] instanceof Double ? MathHelper.floor((Double) a[3]) : 0xFFFFFFFF;
				int bg = a.length > 4 && a[4] instanceof Double ? MathHelper.floor((Double) a[4]) : -1;
				int size = a.length > 5 && a[5] instanceof Double ? MathHelper.floor((Double) a[5]) : 1;
				if(index == -1)index = selectedFont.UNKNOWN;
				int[] charData = selectedFont.chars[index];
				int w = selectedFont.widths[index];
				for (int j = 0;j < charData.length;j++) {
					int b = charData[j];
					for(int k = 0;k<w;k++){
						if((b & (1 << k)) != 0){
							fill(x, k, y, j, size, c);
						}else if(bg > -1){
							fill(x, k, y, j, size, bg);
						}
					}
				}
			}
		}else if(method == 26){//drawBuffer
			if(a.length < 4){
				throw new LuaException("Too few arguments, excepted (number x,number y,number char, number... data)");
			}
			if (a.length > 3 && a[0] instanceof Double && a[1] instanceof Double && a[2] instanceof Double) {
				try{
					int x = MathHelper.floor((Double) a[0]) - 1;
					int y = MathHelper.floor((Double) a[1]) - 1;
					int w = MathHelper.floor((Double) a[2]);
					int s = MathHelper.floor((Double) a[3]);
					Object[] data = Arrays.copyOfRange(a, 4, a.length);
					int[] d = Arrays.stream(data).mapToDouble(n -> (Double)n).mapToInt(MathHelper::floor).toArray();
					for(int i = 0;i<d.length;i++)fill(x, i % w, y, i / w, s, d[i]);
				}catch(ClassCastException e){
					throw new LuaException("Bad Argument #4 - " + (a.length + 1) + " Numbers expected");
				}
			}
		}
		return ret;
	}

	private void sync() {
		TomsModUtils.getServer().addScheduledTask(() -> {
			List<List<TileEntityMonitorBase>> monitors = this.connectMonitors(this.findMonitor());
			int index1 = 0;
			for (List<TileEntityMonitorBase> cMonList : monitors) {
				int index2 = cMonList.size() - 1;
				for (TileEntityMonitorBase mon : cMonList) {
					if (mon != null) {
						mon.screen = TomsModUtils.separateIntArray(screen, index1, index2, size, size);
						mon.textList.clear();
						mon.textureList.clear();
						int indexStart1 = index1 * size;
						int indexStart2 = index2 * size;
						int indexEnd1 = ((index1 + 1) * size) - 1;
						int indexEnd2 = ((index2 + 1) * size) - 1;
						for (LuaTexture t : this.textureList.values()) {
							if (t.xCoord > indexStart1 && t.xCoord < indexEnd1 && t.yCoord > indexStart2 && t.yCoord < indexEnd2) {
								mon.textureList.put(mon.new LuaTexture(t.xCoord % 64, t.yCoord % 64, t.c, t.s, t.rotation, t.colored, t.opacity, t.scale));
							}
						}
						for (LuaText t : this.textList.values()) {
							if (t.xCoord > indexStart1 && t.xCoord < indexEnd1 && t.yCoord > indexStart2 && t.yCoord < indexEnd2) {
								mon.textList.put(mon.new LuaText(t.xCoord % 64, t.yCoord % 64, t.c, t.s, t.rotation, t.scale));
							}
						}
						//mon.markDirty();
						// System.out.println(index1+":"+index2+"
						// s:"+mon.screen[1][1]+" s:"+this.screen[1][1]);
						mon.sync();
					}
					index2--;
				}
				index1++;
			}
		});
	}

	@Override
	public void attach(IComputer computer) {
		this.computers.add(computer);
	}

	@Override
	public void detach(IComputer computer) {
		this.computers.remove(computer);
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			int mons = this.maxX * this.maxY;
			double e = this.energy.extractEnergy(mons, false);
			if (e != mons) {
				int maxX = this.maxX * this.size;
				int maxY = this.maxY * this.size;
				this.screenOld = this.screen;
				this.screen = new int[maxX][maxY];
				this.sync();
				this.deviceOutOfPower = true;
			}
		}
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return from != EnumFacing.UP && type == LV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return this.canConnectEnergy(from, type) ? this.energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.canConnectEnergy(from, type) ? this.energy.getEnergyStored() : 0;
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.canConnectEnergy(from, type) ? this.energy.getMaxEnergyStored() : 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy.readFromNBT(tag.getCompoundTag("energy"));
		/*this.posX = tag.getInteger("posX");
		this.posY = tag.getInteger("posY");
		this.posZ = tag.getInteger("posZ");*/
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("energy", this.energy.writeToNBT(new NBTTagCompound()));
		/*tag.setInteger("posX", this.posX);
		tag.setInteger("posY", this.posY);
		tag.setInteger("posZ", this.posZ);*/
		return tag;
	}

	public TileEntityMonitorBase findMonitor() {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		Block blockU = world.getBlockState(new BlockPos(xCoord, yCoord + 1, zCoord)).getBlock();
		Block blockD = world.getBlockState(new BlockPos(xCoord, yCoord - 1, zCoord)).getBlock();
		Block blockXP = world.getBlockState(new BlockPos(xCoord + 1, yCoord, zCoord)).getBlock();
		Block blockXM = world.getBlockState(new BlockPos(xCoord - 1, yCoord, zCoord)).getBlock();
		Block blockZP = world.getBlockState(new BlockPos(xCoord, yCoord, zCoord + 1)).getBlock();
		Block blockZM = world.getBlockState(new BlockPos(xCoord, yCoord, zCoord - 1)).getBlock();
		Block[] blocks = {blockD, blockU, blockXP, blockXM, blockZP, blockZM};
		TileEntityMonitorBase base = null;
		for (int i = 0;i < 6;i++) {
			if (blocks[i] != null && blocks[i] instanceof BlockMonitorBase) {
				int[] p = blockPositions[i];
				base = (TileEntityMonitorBase) world.getTileEntity(new BlockPos(xCoord + p[0], yCoord + p[1], zCoord + p[2]));
				break;
			}
		}
		return base;
		/*this.monitors.clear();
		if(base != null){
			List<Entry<Integer,Integer>> l = new ArrayList<Entry<Integer, Integer>>();
			Entry<Integer,Integer> e = new EmptyEntry<Integer, Integer>(base.yCoord);
			e.setValue(base.zCoord);
			l.add(e);
			this.monitors.put(base.xCoord,l);
			//int dir = base.direction;
			/*EnumFacing dd = EnumFacing.values()[d];
			EnumFacing ddo = dd.getOpposite();*/
		/*List<TileEntityMonitorBase> connectedMonitors = new ArrayList<TileEntityMonitorBase>();
			Stack<TileEntityMonitorBase> traversingMonitors = new Stack<TileEntityMonitorBase>();
			TileEntityMonitorBase master = base;
			traversingMonitors.add(base);
			int direction = master.direction;
			while(!traversingMonitors.isEmpty()) {
				TileEntityMonitorBase storage = traversingMonitors.pop();
				connectedMonitors.add(storage);
				for(EnumFacing d : EnumFacing.VALID_DIRECTIONS) {
					if((direction == 0 || direction == 1) && d.offsetY != 0)continue;
					if((direction == 2 || direction == 3) && d.offsetZ != 0)continue;
					if((direction == 4 || direction == 5) && d.offsetX != 0)continue;
					TileEntity te = worldObj.getTileEntity(storage.xCoord + d.offsetX, storage.yCoord + d.offsetY, storage.zCoord + d.offsetZ);
					if(te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase)te).direction == master.direction) {
						traversingMonitors.add((TileEntityMonitorBase)te);
					}
				}
			}

			/*List<Integer> xList = new ArrayList<Integer>();
			List<Integer> yList = new ArrayList<Integer>();
			List<Integer> zList = new ArrayList<Integer>();
			for(TileEntityMonitorBase m : connectedMonitors){
				xList.add(m.xCoord);
				yList.add(m.yCoord);
				zList.add(m.zCoord);
			}
			boolean first = true;
			int last = 0;
			for(int x : xList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.min(last, x);
				}
			}
			int minX = last;
			first = true;
			for(int x : yList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.min(last, x);
				}
			}
			int minY = last;
			first = true;
			for(int x : zList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.min(last, x);
				}
			}
			int minZ = last;
			first = true;
			last = 0;
			for(int x : xList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.max(last, x);
				}
			}
			int maxX = last;
			first = true;
			for(int x : yList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.max(last, x);
				}
			}
			int maxY = last;
			first = true;
			for(int x : zList){
				if(first){
					last = x;
					first = false;
				}else{
					last = Math.max(last, x);
				}
			}
			int maxZ = last;

		 */// }
		/*
		TileEntityMonitorBase[] m = new TileEntityMonitorBase[6];
		if(blockU instanceof BlockMonitorBase) m[0] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
		if(blockD instanceof BlockMonitorBase) m[1] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
		if(blockXP instanceof BlockMonitorBase) m[2] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord+1, yCoord, zCoord);
		if(blockXM instanceof BlockMonitorBase) m[3] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord-1, yCoord, zCoord);
		if(blockZP instanceof BlockMonitorBase) m[4] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord, yCoord, zCoord+1);
		if(blockZM instanceof BlockMonitorBase) m[5] = (TileEntityMonitorBase) this.worldObj.getTileEntity(xCoord, yCoord, zCoord-1);
		TileEntityMonitorBase[][] mons = new TileEntityMonitorBase[6][];
		int i = 0;
		for(TileEntityMonitorBase mon : m){
			if(mon == null) continue;
			List<TileEntityMonitorBase> connectedStorages = new ArrayList<TileEntityMonitorBase>();
			Stack<TileEntityMonitorBase> traversingStorages = new Stack<TileEntityMonitorBase>();
			TileEntityMonitorBase master = mon;
			traversingStorages.add(mon);
			int direction = master.direction;
			while(!traversingStorages.isEmpty()) {
				TileEntityMonitorBase storage = traversingStorages.pop();
				connectedStorages.add(storage);
				for(EnumFacing d : EnumFacing.VALID_DIRECTIONS) {
					if((direction == 0 || direction == 1) && d.offsetY != 0)continue;
					if((direction == 2 || direction == 3) && d.offsetZ != 0)continue;
					if((direction == 4 || direction == 5) && d.offsetX != 0)continue;
					TileEntity te = worldObj.getTileEntity(storage.xCoord + d.offsetX, storage.yCoord + d.offsetY, storage.zCoord + d.offsetZ);
					if(te instanceof TileEntityMonitorBase && !connectedStorages.contains(te) && ((TileEntityMonitorBase)te).direction == master.direction) {
						traversingStorages.add((TileEntityMonitorBase)te);
					}
				}
			}
			mons[i] = connectedStorages.toArray(new TileEntityMonitorBase[connectedStorages.size()]);
			i++;
		}
		for(TileEntityMonitorBase[] cmS : mons){
			if(cmS == null) continue;
			int Bx = 0;
			int By = 0;
			int Bz = 0;
			boolean last = false;
			for(TileEntityMonitorBase cm : cmS){
				if(cm == null) continue;
				if(!last){
					Bx = cm.xCoord;
					By = cm.yCoord;
					Bz = cm.zCoord;
					last = true;
				}
				if(cm.direction == 0 || cm.direction == 1){
					if(Bx < cm.xCoord){
						Bx = cm.xCoord;
					}
					if(Bz < cm.zCoord){
						Bz = cm.zCoord;
					}
				}else{
					if(By < cm.yCoord){
						By = cm.yCoord;
					}
					if(cm.direction == 2 || cm.direction == 3){
						if(Bx < cm.xCoord){
							Bx = cm.xCoord;
						}
					}else{
						if(Bz < cm.zCoord){
							Bz = cm.zCoord;
						}
					}
				}
			}
		}*/
	}

	/*@Override
	public boolean link(int x, int y, int z) {
		TileEntity tilee = worldObj.getTileEntity(x, y, z);
		if(tilee instanceof TileEntityMonitorBase){

			return true;
		}
		return false;
	}*/
	public List<List<TileEntityMonitorBase>> connectMonitors(TileEntityMonitorBase base) {
		if (base != null) {
			int xCoord = pos.getX();
			int yCoord = pos.getY();
			int zCoord = pos.getZ();
			/*List<TileEntityMonitorBase> connectedMonitors = new ArrayList<TileEntityMonitorBase>();
			Stack<TileEntityMonitorBase> traversingMonitors = new Stack<TileEntityMonitorBase>();
			traversingMonitors.add(base);
			int direction = base.direction;
			int i = 1;
			while(!traversingMonitors.isEmpty()) {
			TileEntityMonitorBase storage = traversingMonitors.pop();
			connectedMonitors.add(storage);
			/*for(EnumFacing d : EnumFacing.VALID_DIRECTIONS) {
				if((direction == 0 || direction == 1) && d.offsetY != 0)continue;
				if((direction == 2 || direction == 3) && d.offsetZ != 0)continue;
				if((direction == 4 || direction == 5) && d.offsetX != 0)continue;
				TileEntity te = worldObj.getTileEntity(storage.xCoord + d.offsetX, storage.yCoord + d.offsetY, storage.zCoord + d.offsetZ);
				if(te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase)te).direction == master.direction) {
					traversingMonitors.add((TileEntityMonitorBase)te);
				}
			}*//*
				TileEntity te = TomsMathHelper.getTileEntity(worldObj, base.getOffset(i, 0, direction));
				if(te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase)te).direction == direction) {
				traversingMonitors.add((TileEntityMonitorBase)te);
				}else{
				break;
				}
				i++;
				}*/
			List<TileEntityMonitorBase> listX = this.getMonitorsRight(base);
			List<List<TileEntityMonitorBase>> mons = new ArrayList<>();
			List<TileEntityMonitorBase> listBY = this.getMonitorsUp(base);
			int maxSizeY = listBY.size() + 1;
			int maxSizeX = listX.size() + 1;
			for (TileEntityMonitorBase mon : listX) {
				List<TileEntityMonitorBase> cMons = this.getMonitorsUp(mon);
				// mons.add(cMons);
				maxSizeY = Math.min(maxSizeY, cMons.size());
			}
			for (TileEntityMonitorBase mon : listBY) {
				List<TileEntityMonitorBase> cMons = this.getMonitorsRight(mon);
				// mons.add(cMons);
				maxSizeX = Math.min(maxSizeX, cMons.size());
			}
			for (int x = 0;x < maxSizeX;x++) {
				List<TileEntityMonitorBase> cM = new ArrayList<>();
				for (int y = 0;y < maxSizeY;y++) {
					cM.add(((TileEntityMonitorBase) TomsModUtils.getTileEntity(world, base.getOffset(x, y, base.direction))).connect(xCoord, yCoord, zCoord));
				}
				mons.add(cM);
			}
			int oldX = this.maxX;
			int oldY = this.maxY;
			this.maxX = maxSizeX;
			this.maxY = maxSizeY;
			if (oldX != this.maxX || oldY != this.maxY) {
				this.screen = new int[this.maxX * this.size][this.maxY * this.size];
			}
			return mons;
		}
		return Collections.emptyList();
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
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		int[] ret;
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

	public List<TileEntityMonitorBase> getMonitorsUp(TileEntityMonitorBase master) {
		List<TileEntityMonitorBase> connectedMonitors = new ArrayList<>();
		if (master != null) {
			Stack<TileEntityMonitorBase> traversingMonitors = new Stack<>();
			traversingMonitors.add(master);
			int direction = master.direction;
			int i = 1;
			while (!traversingMonitors.isEmpty()) {
				TileEntityMonitorBase storage = traversingMonitors.pop();
				connectedMonitors.add(storage);
				/*for(EnumFacing d : EnumFacing.VALID_DIRECTIONS) {
				if((direction == 0 || direction == 1) && d.offsetY != 0)continue;
				if((direction == 2 || direction == 3) && d.offsetZ != 0)continue;
				if((direction == 4 || direction == 5) && d.offsetX != 0)continue;
				TileEntity te = worldObj.getTileEntity(storage.xCoord + d.offsetX, storage.yCoord + d.offsetY, storage.zCoord + d.offsetZ);
				if(te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase)te).direction == master.direction) {
					traversingMonitors.add((TileEntityMonitorBase)te);
				}
				}*/
				TileEntity te = TomsModUtils.getTileEntity(world, master.getOffset(0, i, direction));
				if (te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase) te).direction == direction) {
					traversingMonitors.add((TileEntityMonitorBase) te);
				} else {
					break;
				}
				i++;
			}
		}
		return connectedMonitors;
	}

	public List<TileEntityMonitorBase> getMonitorsRight(TileEntityMonitorBase master) {
		List<TileEntityMonitorBase> connectedMonitors = new ArrayList<>();
		if (master != null) {
			Stack<TileEntityMonitorBase> traversingMonitors = new Stack<>();
			traversingMonitors.add(master);
			int direction = master.direction;
			int i = 1;
			while (!traversingMonitors.isEmpty()) {
				TileEntityMonitorBase storage = traversingMonitors.pop();
				connectedMonitors.add(storage);
				/*for(EnumFacing d : EnumFacing.VALID_DIRECTIONS) {
				if((direction == 0 || direction == 1) && d.offsetY != 0)continue;
				if((direction == 2 || direction == 3) && d.offsetZ != 0)continue;
				if((direction == 4 || direction == 5) && d.offsetX != 0)continue;
				TileEntity te = worldObj.getTileEntity(storage.xCoord + d.offsetX, storage.yCoord + d.offsetY, storage.zCoord + d.offsetZ);
				if(te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase)te).direction == master.direction) {
					traversingMonitors.add((TileEntityMonitorBase)te);
				}
				}*/
				TileEntity te = TomsModUtils.getTileEntity(world, master.getOffset(i, 0, direction));
				if (te instanceof TileEntityMonitorBase && !connectedMonitors.contains(te) && ((TileEntityMonitorBase) te).direction == direction) {
					traversingMonitors.add((TileEntityMonitorBase) te);
				} else {
					break;
				}
				i++;
			}
		}
		return connectedMonitors;
	}

	public class LuaTexture implements ITMLuaObject {
		public String s = "";
		public double xCoord = 0;
		public double yCoord = 0;
		public int c = 0;
		public double rotation = 0;
		public boolean colored = false;
		public float opacity = 1;
		public float scale = 1;

		public LuaTexture() {
		}

		public LuaTexture(double x, double y, int c, String text, double rotation, boolean colored, float opacity, float scale) {
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
			return new String[]{"getTexture", "getX", "getY", "getColor", "setTexture", "setX", "setY", "setColor", "delete", "getRotation", "setRotation", "setColored", "getScale", "setScale"};
		}

		@Override
		public Object[] call(IComputer computer, String method, Object[] a) throws LuaException {
			if (!textureList.contains(this))
				throw new LuaException("This object was already deleted");
			switch(method){
			case "getTexture":
				return new Object[]{s};
			case "getX":
				return new Object[]{xCoord};
			case "getY":
				return new Object[]{yCoord};
			case "getColor":
				return new Object[]{this.colored, c, this.opacity};
			case "setTexture":
				if (a.length > 0 && a[0] != null) {
					this.s = a[0].toString();
				} else {
					throw new LuaException("Invalid Arguments, excepted (String)");
				}
				break;
			case "setX":
				if (a.length > 0 && a[0] instanceof Number) {
					this.xCoord = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
				break;
			case "setY":
				if (a.length > 0 && a[0] instanceof Number) {
					this.yCoord = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
				break;
			case "setColor":
				if (a.length > 0 && a[0] instanceof Double) {
					this.c = MathHelper.floor((Double) a[0]);
					if (a.length > 1 && a[1] instanceof Double) {
						this.opacity = new Float((Double) a[1]);
					}
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
				break;
			case "delete":
				textureList.remove(this);
				break;
			case "getRotation":
				return new Object[]{this.rotation};
			case "setRotation":
				if (a.length > 0 && a[0] instanceof Double) {
					this.rotation = (Double) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
				break;
			case "setColored":
				if (a.length > 0 && a[0] instanceof Boolean) {
					this.colored = (Boolean) a[0];
				} else {
					throw new LuaException("Invalid Arguments, excepted (boolean)");
				}
				break;
			case "getScale":
				return new Object[]{this.scale};
			case "setScale":
				if (a.length > 0 && a[0] instanceof Double) {
					this.scale = new Float((Double) a[0]);
				} else {
					throw new LuaException("Invalid Arguments, excepted (number)");
				}
			}
			/*if (method == 0)
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
					throw new dan200.computercraft.api.lua.LuaException("Invalid Arguments, excepted (String)");
				}
			} else if (method == 5) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.xCoord = (Double) a[0];
				} else {
					throw new dan200.computercraft.api.lua.LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 6) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.yCoord = (Double) a[0];
				} else {
					throw new dan200.computercraft.api.lua.LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 7) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.c = MathHelper.floor((Double) a[0]);
					if (a.length > 1 && a[1] instanceof Double) {
						this.opacity = (Double) a[1];
					}
				} else {
					throw new dan200.computercraft.api.lua.LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 8) {
				textureList.remove(this);
			} else if (method == 9)
				return new Object[]{this.rotation};
			else if (method == 10) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.rotation = (Double) a[0];
				} else {
					throw new dan200.computercraft.api.lua.LuaException("Invalid Arguments, excepted (number)");
				}
			} else if (method == 11) {
				if (a.length > 0 && a[0] instanceof Boolean) {
					this.colored = (Boolean) a[0];
				} else {
					throw new dan200.computercraft.api.lua.LuaException("Invalid Arguments, excepted (boolean)");
				}
			} else if (method == 12)
				return new Object[]{this.scale};
			else if (method == 13) {
				if (a.length > 0 && a[0] instanceof Double) {
					this.scale = new Float((Double) a[0]);
				} else {
					throw new dan200.computercraft.api.lua.LuaException("Invalid Arguments, excepted (number)");
				}
			}*/
			return null;
		}

		@Override
		public long getID() {
			return textureList.getIDFor(this);
		}

	}

	public class LuaText implements ITMLuaObject {
		public String s = "";
		public double xCoord = 0;
		public double yCoord = 0;
		public int c = 0;
		public double rotation = 0;
		public float scale = 1;

		public LuaText() {
		}

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
			return new String[]{"getText", "getX", "getY", "getColor", "setText", "setX", "setY", "setColor", "delete", "getRotation", "setRotation", "getScale", "setScale"};
		}

		@Override
		public Object[] call(IComputer context, String methodIn, Object[] a) throws LuaException {
			if (!textList.contains(this))
				throw new LuaException("This object was already deleted");
			int method = Arrays.binarySearch(getMethodNames(), methodIn);
			if (method == 0)
				return new Object[]{s};
			else if (method == 1)
				return new Object[]{xCoord};
			else if (method == 2)
				return new Object[]{yCoord};
			else if (method == 3)
				return new Object[]{c};
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
					this.c = MathHelper.floor((Double) a[0]);
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

	public void queueEvent(String event, Object[] args) {
		Object[] a = new Object[args.length + 1];
		for (int i = 0;i < args.length;i++) {
			a[i + 1] = args[i];
		}
		for (IComputer c : computers) {
			a[0] = c.getAttachmentName();
			c.queueEvent(event, a);
		}
	}

	public void monitorClick(int xC, int yC, int zC, int pX, int pY) {
		List<List<TileEntityMonitorBase>> monitors = this.connectMonitors(this.findMonitor());
		int index1 = 0;
		for (List<TileEntityMonitorBase> cMonList : monitors) {
			int index2 = cMonList.size() - 1;
			for (TileEntityMonitorBase mon : cMonList) {
				if (mon != null) {
					BlockPos monp = mon.getPos();
					if (monp.getX() == xC && monp.getY() == yC && monp.getZ() == zC) {
						int xP = pX + (this.size * index1);
						int yP = pY + (this.size * index2);
						this.queueEvent("tm_monitor_touch", new Object[]{xP + 1, yP + 1});
						break;
					}
				}
				index2--;
			}
			index1++;
		}
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LV.getList();
	}

	private int getTextLength(char[] chars, int size, int padding){
		int l = 0;
		for (int i = 0;i < chars.length;i++) {
			char d = chars[i];
			int index = selectedFont.chars2.indexOf(d);
			if(index == -1)index = selectedFont.UNKNOWN;
			int w = selectedFont.widths[index];
			if(d == ' '){
				w = 5;
			}
			l += ((w + padding) * size);
		}
		return l;
	}

	private static Font getOrLoadFont(Map<String, CustomFont> internalFonts, String s) throws LuaException {
		Font f = fonts.get(s);
		if(f != null)return f.getFont(internalFonts);
		try{
			f = Font.load(s);
		}catch(Throwable e){
			fonts.put(s, Font.MISSING);
			throw new LuaException(e.getMessage());
		}
		if(f == null){
			fonts.put(s, Font.MISSING);
			throw new LuaException("Font file not found");
		}
		fonts.put(s, f);
		return f.getFont(internalFonts);
	}

	private void fill(int x, int ox, int y, int oy, int size, int col){
		for(int i = 0;i<size;i++){
			for(int j = 0;j<size;j++){
				screen[x + (ox * size) + i][y + (oy * size) + j] = col;
			}
		}
	}
	public static void main(String[] args) {
		//if(true)return;
		File folder = new File(".", "font");
		File o = new File(".", "font_out");
		folder.mkdirs();
		o.mkdirs();
		File[] files = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				int i = name.lastIndexOf('.');
				if (i > 0 && i < name.length() - 1) {
					String desiredExtension = name.substring(i+1).
							toLowerCase(Locale.ENGLISH);
					return desiredExtension.equals("png");
				}
				return false;
			}
		});
		for(File f : files){
			DataOutputStream str = null;
			System.out.println(f.getName());
			try {
				BufferedImage imgIn = ImageIO.read(f);
				if(imgIn.getWidth() == imgIn.getHeight()){
					BufferedImage img = new BufferedImage(imgIn.getWidth(), imgIn.getWidth(), BufferedImage.TYPE_INT_ARGB);
					Graphics g = img.createGraphics();
					g.drawImage(imgIn, 0, 0, null);
					g.dispose();
					String chars = "";
					if(f.getName().startsWith("unicode_page_")){
						String pg = f.getName().substring("unicode_page_".length(), f.getName().length() - 4);
						int start = Integer.parseInt(pg, 16) * 256;
						System.out.println(start);
						StringBuilder b = new StringBuilder();
						for(int i = 0;i<256;i++){
							char c = (char) (start+i);
							b.append(c);
						}
						chars = b.toString();
					}
					File charsF = new File(f.getParentFile(), f.getName().substring(0, f.getName().length() - 4) + ".txt");
					if(charsF.exists()){
						BufferedReader r = new BufferedReader(new FileReader(charsF));
						StringBuilder builder = new StringBuilder();
						String line = r.readLine();
						while(line != null){
							builder.append(line);
							line = r.readLine();
						}
						r.close();
						chars = builder.toString();
					}
					if(chars.isEmpty())chars = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
					File out = new File(o, f.getName().substring(0, f.getName().length() - 4) + ".bin");
					System.out.println(out.getAbsolutePath());
					ByteArrayOutputStream s = new ByteArrayOutputStream();
					str = new DataOutputStream(new DualOutputStream(new FileOutputStream(out), s));
					char[] t = chars.toCharArray();
					int w = img.getWidth() / 16;
					System.out.println(w);
					str.writeByte(w);
					str.writeInt(t.length);
					for (int i = 0;i < t.length;i++) {
						char c = t[i];
						str.writeChar(c);
					}
					for(int i = 0;i<t.length;i++){
						int x = (i % 16) * w;
						int y = (i / 16) * w;
						int end = 1;
						for(int k = 0;k<w;k++){
							for(int j = 0;j<w;j++){
								int rgb = img.getRGB(x + k, y + j);
								if((rgb>>24) != 0x00){
									end = k + 1;
								}
							}
						}
						str.writeByte(end);
						for(int j = 0;j<w;j++){
							int d = 0;
							for(int k = 0;k<w;k++){
								int rgb = img.getRGB(x + k, y + j);
								if((rgb>>24) != 0x00)d |= 1 << k;
							}
							str.writeInt(d);
						}
					}
					str.close();
					System.out.println("new byte[]" + Arrays.toString(s.toByteArray()).replace('[', '{').replace(']', '}'));
				}else{
					System.err.println("Inavlid size image " + imgIn.getWidth() + "x" + imgIn.getHeight());
				}
			} catch (IOException e) {
				e.printStackTrace();
				IOUtils.closeQuietly(str);
			}
		}

	}
}
