package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.tileentity.IReceivable;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.BigEntry;
import com.tom.apis.EmptyBigEntry;
import com.tom.lib.Configs;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageTabGuiAction;

import com.tom.core.item.TabletHandler;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityTabletController extends TileEntityTomsMod implements
IPeripheral {
	public List<TabletHandler> tablets = new ArrayList<TabletHandler>();
	private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	public int lastId = 0;
	private boolean firstStart = true;
	public String[] methods = {"listMethods","sendTo","getModemStats","setAntenna","setAccessPointAntenna","getAntennas","getStats",
			"openGui","closeGui","getHitbox","splitString","getCursorPosition","getResolution","playSound","stopAllSounds","translate",
			"replaceString",
			//"print","clear","setCursorBlink","getCursorBlink","setCursorPos","getCursorPos","write","drawPicture","setTermName",
	/*"getTermName","setTermCursor","getTermCursor","setWriteMode","getWriteMode","getCurrentText"*/};

	@Override
	public String getType() {
		return "tabController";
	}

	@Override
	public String[] getMethodNames() {
		return this.methods;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] a) throws LuaException,
	InterruptedException {
		int plus = 8;
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if(method == 0){
			Object[] o = new Object[methods.length];
			for(int i = 0;i<o.length;i++){
				o[i] = methods[i];
			}
			return o;
		}else if(method == 1){
			if(a.length > 3 && a[0] instanceof String && a[1] instanceof String && a[2] instanceof String){
				String name = (String) a[0];
				String pName = (String) a[1];
				if(name.equalsIgnoreCase("ant") || name.equalsIgnoreCase("antenna") || name.equalsIgnoreCase("ap") || name.equalsIgnoreCase("accesspoint")){
					TabletHandler tab = this.getTablet(pName);
					if(tab != null){
						if(name.equalsIgnoreCase("ant") || name.equalsIgnoreCase("antenna")){
							if(tab.antAntenna && tab.connectedToAntenna){
								TileEntity tile = worldObj.getTileEntity(new BlockPos(tab.antX, tab.antY, tab.antZ));
								if(tile != null && tile instanceof IReceivable){
									((IReceivable)tile).receiveMsg(pName, a[3]);
									return new Object[]{true};
								}
							}
						}else if(name.equalsIgnoreCase("ap") || name.equalsIgnoreCase("accesspoint")){
							if(tab.apAntenna && tab.connectedToAccessPoint){
								TileEntity tile = worldObj.getTileEntity(new BlockPos(tab.apX, tab.apY, tab.apZ));
								if(tile != null && tile instanceof IReceivable){
									((IReceivable)tile).receiveMsg(pName, a[3]);
									return new Object[]{true};
								}
							}
						}
					}
					return new Object[]{false};
					/*IReceivable d = (IReceivable) te;
					d.receiveMsg((String) a[3], a[4]);
					return new Object[]{true};*/
				}else{
					throw new LuaException("Invalid device at name: "+a[0]);
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String,String,Object)");
			}
		}else if(method == 2){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					boolean hM = tab.hasModem;
					if(hM){
						NBTTagCompound modemTag = tab.modemTag;
						if(modemTag != null){
							int tier = modemTag.hasKey("tier") ? modemTag.getInteger("tier") : 0;
							int tierAnt = modemTag.hasKey("tierAnt") ? modemTag.getInteger("tierAnt") : 0;
							return new Object[]{hM,tier,tierAnt};
						}
					}
					return new Object[]{hM};
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String)");
			}
		}else if(method == 3){
			if(a.length > 1 && a[0] instanceof String && a[1] instanceof Boolean){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.antAntenna = (Boolean) a[1];
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String,boolean)");
			}
		}else if(method == 4){
			if(a.length > 1 && a[0] instanceof String && a[1] instanceof Boolean){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.apAntenna = (Boolean) a[1];
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String, Boolean)");
			}
		}else if(method == 5){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					return new Object[]{tab.antAntenna,tab.apAntenna,tab.connectedToAntenna,tab.connectedToAccessPoint};
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String)");
			}
		}else if(method == 6){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					return tab.obj;
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String)");
			}
		}else if(method == 7){
			if(a.length > 0 && a[0] instanceof String){
				EntityPlayer player = worldObj.getPlayerEntityByName((String) a[0]);
				if(player != null){
					boolean eEsc = a.length > 1 ? (a[1] instanceof Boolean ? (Boolean) a[1] : true) : true;
					String hitboxS = a.length > 2 ? (a[2] instanceof String ? a[2].toString() : null) : null;
					List<BigEntry<String, Integer,Integer,Integer,Integer>> hitboxes =
							new ArrayList<BigEntry<String, Integer, Integer, Integer, Integer>>();
					if(hitboxS != null){
						LuaHitbox hitbox = new LuaHitbox(hitboxS);
						for(LuaEntry c : hitbox.hitboxes)
							hitboxes.add(new EmptyBigEntry<String, Integer, Integer, Integer, Integer>(c.name,c.x,c.y,c.width,c.height));
					}
					NetworkHandler.sendTo(new MessageTabGuiAction(xCoord,yCoord,zCoord,hitboxes,eEsc), (EntityPlayerMP) player);
				}
			}
		}else if(method == 8){
			if(a.length > 0 && a[0] instanceof String){
				EntityPlayer player = worldObj.getPlayerEntityByName((String) a[0]);
				if(player != null){
					NetworkHandler.sendTo(MessageTabGuiAction.getCloseMessage(), (EntityPlayerMP) player);
				}
			}
		}else if(method == 9){ return new Object[]{new LuaHitbox()};
		}else if(method == 10){
			if(a.length > 1 && a[0] instanceof String && a[1] instanceof String){
				String string = (String) a[0];
				String[] s = string.split((String) a[1]);
				Object[] o = new Object[s.length];
				for(int i = 0;i<o.length;i++){
					o[i] = s[i];
				}
				return o;
			}
		}else if(method == 11){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					return new Object[]{tab.cHitBox, tab.cursorX, tab.cursorY};
				}
			}
		}else if(method == 12){//player_resulution_info
			if(a.length > 0 && a[0] instanceof String){
				EntityPlayer player = worldObj.getPlayerEntityByName((String) a[0]);
				if(player != null){
					NetworkHandler.sendTo(MessageTabGuiAction.getResolutionMessage(), (EntityPlayerMP) player);
					return context.pullEvent("player_resulution_info_"+player.getName());
				}
			}
		}else if(method == 13){
			if(a.length > 2 && a[0] instanceof String && a[1] instanceof String && a[2] instanceof Double){
				EntityPlayer player = worldObj.getPlayerEntityByName((String) a[0]);
				if(player != null){
					LuaSound s = new LuaSound(player,(String) a[1]);
					NetworkHandler.sendTo(new MessageTabGuiAction(s, true, new Float((Double)a[2])), (EntityPlayerMP) player);
					return new Object[]{s};
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String, String, Number)");
			}
		}else if(method == 14){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.sounds.clear();
				}
			}
		}else if(method == 15){
			if(a.length > 1 && a[0] instanceof String && a[1] instanceof String){
				EntityPlayer player = worldObj.getPlayerEntityByName((String) a[0]);
				if(player != null){
					NetworkHandler.sendTo(MessageTabGuiAction.getTranslationMessage((String) a[1]), (EntityPlayerMP) player);
					return context.pullEvent("player_translation_"+player.getName());
				}
			}
		}else if(method == 16){
			if(a.length > 2 && a[0] instanceof String && a[1] instanceof String && a[2] instanceof String){
				return new Object[]{a[0].toString().replace((String) a[1], (String) a[2])};
			}
		}else if(method == 9+plus){
			if(a.length > 2 && a[0] instanceof String && a[1] instanceof String && a[2] instanceof Double){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.term.print((String) a[1], MathHelper.floor_double((Double) a[2]));
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String, String, Number)");
			}
		}else if(method == 10+plus){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.term.clear();
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String)");
			}
		}else if(method == 11+plus){
			if(a.length > 1 && a[0] instanceof String && a[1] instanceof Boolean){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.term.cursor = (Boolean) a[1];
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String, Boolean)");
			}
		}else if(method == 12+plus){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					return new Object[]{tab.term.cursor};
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String)");
			}
		}else if(method == 13+plus){
			if(a.length > 2 && a[0] instanceof String && a[1] instanceof Double && a[2] instanceof Double){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.term.curPosX = MathHelper.floor_double((Double) a[1]);
					tab.term.curPosY = MathHelper.floor_double((Double) a[2]);
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String, Number, Number)");
			}
		}else if(method == 14+plus){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					return new Object[]{tab.term.curPosX, tab.term.curPosY};
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String)");
			}
		}else if(method == 15+plus){
			if(a.length > 2 && a[0] instanceof String && a[1] instanceof String && a[2] instanceof Double){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.term.write((String) a[1], MathHelper.floor_double((Double) a[2]));
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String, String, Number)");
			}
		}else if(method == 16+plus){
			if(a.length > 1 && a[0] instanceof String && a[1] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.term.renderPicture((String) a[1]);
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String, String)");
			}
		}else if(method == 17+plus){
			if(a.length > 1 && a[0] instanceof String && a[1] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.term.name = (String) a[1];
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String, String)");
			}
		}else if(method == 18+plus){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					return new Object[]{tab.term.name};
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String)");
			}
		}else if(method == 19+plus){
			if(a.length > 2 && a[0] instanceof String && a[1] instanceof Double && a[2] instanceof Double){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.term.curPosWX = MathHelper.floor_double((Double) a[1]);
					tab.term.curPosWY = MathHelper.floor_double((Double) a[2]);
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String, Number, Number)");
			}
		}else if(method == 20+plus){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					return new Object[]{tab.term.curPosWX, tab.term.curPosWY};
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String)");
			}
		}else if(method == 21+plus){
			if(a.length > 1 && a[0] instanceof String && a[1] instanceof Boolean){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					tab.term.writeMode = (Boolean) a[1];
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String, Boolean)");
			}
		}else if(method == 22+plus){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					return new Object[]{tab.term.writeMode};
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String)");
			}
		}else if(method == 23+plus){
			if(a.length > 0 && a[0] instanceof String){
				TabletHandler tab = this.getTablet((String) a[0]);
				if(tab != null){
					EntityPlayer user = this.worldObj.getPlayerEntityByName(tab.playerName);
					if(user != null){
						NetworkHandler.sendTo(new MessageTabGuiAction(), (EntityPlayerMP) user);
						int i = 0;
						while(true){
							Object[] o = context.pullEvent("tab_textBoxReceive");
							String pName = o.length > 0 ? (String) o[0] : "";
							if(pName.equals(tab.playerName)){
								return new Object[]{tab.term.inputText};
							}
							i = i + 1;
							if(i == 5){
								return new Object[]{tab.term.inputText};
							}
						}
					}
				}
			}else{
				throw new LuaException("Invalid arguments, excepted (String)");
			}
		}
		return null;
	}
	@Override
	public void attach(IComputerAccess computer) {
		computers.add(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		computers.remove(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("lastId", this.lastId);
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.lastId = tag.getInteger("lastId");
	}
	public int connectNewTablet(){
		this.lastId = this.lastId+1;
		this.tablets.add(new TabletHandler(lastId));
		return this.lastId;
	}
	public TabletHandler getTablet(int id){
		if(this.tablets.size() >= id && this.tablets.size() != 0){
			return this.tablets.get(id-1);
		}
		return null;
	}
	@Override
	public void updateEntity(){
		if(this.firstStart && !this.worldObj.isRemote){
			this.firstStart = false;
			for(int i = 0;i<this.lastId;i++){
				this.tablets.add(new TabletHandler(i));
			}
		}
	}
	public void queueEvent(String event, Object[] a){
		for(IComputerAccess c : this.computers){
			c.queueEvent(event, a);
		}
	}
	private TabletHandler getTablet(String pName){
		for(TabletHandler tab : this.tablets){
			if(tab.playerName.equals(pName)){
				return tab;
			}
		}
		return null;
	}
	public void getUpdates(String p,String e){
		TabletHandler tab = this.getTablet(p);
		if(tab != null){
			this.queueEvent(e, tab.obj);
		}
	}
	private class LuaEntry implements ILuaObject{
		public int x = 0,y = 0,width = 0,height = 0;
		public String name = "nil";
		public LuaEntry(String sIn) throws LuaException {
			String[] s = sIn.split(":");
			if(s.length < 4) throw new LuaException("Invalid Entry");
			else{
				try{
					this.x = Integer.parseInt(s[0]);
					this.y = Integer.parseInt(s[1]);
					this.width = Integer.parseInt(s[2]);
					this.height = Integer.parseInt(s[3]);
					this.name = s[4];
				}catch(Exception e){
					//e.printStackTrace();
					//System.out.println(s[1]);
					throw new LuaException("Invalid Entry");
				}
			}
		}

		public LuaEntry() {}

		@Override
		public String[] getMethodNames() {
			return new String[]{"setX","setY","setWidth","setHeight","setName","getX","getY","getWidth","getHeight","getName","export"};
		}

		@Override
		public Object[] callMethod(ILuaContext context,
				int method, Object[] a)
						throws LuaException, InterruptedException {
			if(method == 0){
				if(a.length > 0 && a[0] instanceof Double){
					this.x = MathHelper.floor_double((Double) a[0]);
				}
			}else if(method == 1){
				if(a.length > 0 && a[0] instanceof Double){
					this.y = MathHelper.floor_double((Double) a[0]);
				}
			}else if(method == 2){
				if(a.length > 0 && a[0] instanceof Double){
					this.width = MathHelper.floor_double((Double) a[0]);
				}
			}else if(method == 3){
				if(a.length > 0 && a[0] instanceof Double){
					this.height = MathHelper.floor_double((Double) a[0]);
				}
			}else if(method == 4){
				if(a.length > 0 && a[0] instanceof String){
					this.name = (String) a[0];
				}
			}else if(method == 5){
				return new Object[]{this.x};
			}else if(method == 6){
				return new Object[]{this.y};
			}else if(method == 7){
				return new Object[]{this.width};
			}else if(method == 8){
				return new Object[]{this.height};
			}else if(method == 9){
				return new Object[]{this.name};
			}else if(method == 10){
				return new Object[]{this.toString()};
			}
			return null;
		}
		@Override
		public String toString(){
			return this.x+":"+this.y+":"+this.width+":"+this.height+":"+this.name.replace(":", "|");
		}

	}
	private class LuaHitbox implements ILuaObject{
		public LuaHitbox(){}
		public LuaHitbox(String sIn) throws LuaException{
			String[] split = sIn.split("$");
			for(String s : split){
				if(!s.equals("$")) this.hitboxes.add(new LuaEntry(s));
			}
		}
		public List<LuaEntry> hitboxes = new ArrayList<LuaEntry>();

		@Override
		public String[] getMethodNames() {
			return new String[]{"getNewEntry","add","getByID","export"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method,
				Object[] a) throws LuaException,
		InterruptedException {
			if(method == 0){
				return new Object[]{new LuaEntry()};
			}else if(method == 1){
				if(a.length > 0){
					hitboxes.add(new LuaEntry(a[0].toString()));
					return new Object[]{hitboxes.size()};
				}else{
					//System.out.println(a[0]);
					throw new LuaException("Invalid Arguments, excepted (Entry (use getNewEntry() to get a new instance of it))");
				}
			}else if(method == 2){
				if(a.length > 0 && a[0] instanceof Double){
					int id = MathHelper.floor_double((Double) a[0]) - 1;
					if(id >= 0 && hitboxes.size() >= id){
						/*BigEntry<String, Integer,Integer,Integer,Integer> c = ;
						LuaEntry e = new LuaEntry();
						e.x = c.getValue1();
						e.y = c.getValue2();
						e.width = c.getValue3();
						e.height = c.getValue4();
						e.name = c.getKey();*/
						return new Object[]{hitboxes.get(id)};
					}else throw new LuaException("Index out of bounds excepted between 1 and "+hitboxes.size());
				}else throw new LuaException("Invalid Arguments, excepted (Number)");
			}else if(method == 3){
				return new Object[]{this.toString()};
			}
			return null;
		}
		@Override
		public String toString(){
			String ret = "";
			for(LuaEntry e : this.hitboxes){
				ret = ret+"$"+e.toString();
			}
			return ret.substring(1);
		}
	}
	public class LuaSound implements ILuaObject{
		public LuaSound(EntityPlayer player, String sound){
			this.player = player;
			this.sound = sound;
		}
		private final EntityPlayer player;
		public final String sound;
		private boolean isValid = true;
		@Override
		public String[] getMethodNames() {
			return new String[]{"stop"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method,
				Object[] arguments) throws LuaException, InterruptedException {
			if(!this.isValid) throw new LuaException("This object is already deleted");
			if(method == 0){
				NetworkHandler.sendTo(new MessageTabGuiAction(this, false, 0.0F), (EntityPlayerMP) player);
				this.isValid = false;
			}
			return null;
		}

	}
}
