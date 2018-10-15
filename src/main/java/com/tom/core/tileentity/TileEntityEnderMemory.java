package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import com.tom.api.helpers.ComputerHelper;
import com.tom.api.tileentity.TileEntityTomsModNoTicking;
import com.tom.handler.TMPlayerHandler;
import com.tom.lib.api.tileentity.ITMPeripheral;
import com.tom.lib.utils.EmptyEntry;
import com.tom.util.TomsModUtils;

public class TileEntityEnderMemory extends TileEntityTomsModNoTicking implements ITMPeripheral {
	public static Map<Integer, List<IComputer>> EnderMemoryIComputerAccess = new HashMap<>();
	/**
	 * { <br>
	 * {*public { <br>
	 * {playerName,Object},{playerName,Object},{playerName,Object} <br>
	 * } }, <br>
	 * {*private
	 * {player{<code>playerName</code>},{<code>Object0</code>,<code>Object1</code>...}}
	 * <br>
	 * }
	 *
	 * } }
	 */
	public static HashMap<Integer, Entry<String, Object>> globals = new HashMap<>();
	public String playerName = "";
	//private List<IComputer> computers = new ArrayList<>();
	public String pName = "tm_ender_memory";
	public String[] methods = {"listMethods", "setPublic", "setPrivate", "getPublic", "getPrivate", "getPublicPlayerName",
			"getPlayerName", "queuePublicEvent", "queuePrivateEvent", "queuePublicDescEvent", "queuePrivateDescEvent",
			"attactGlobalEventQueue", "detactGlobalEventQueue"};
	private String name = "tileEntityEnderMemory";

	@Override
	public String getType() {
		return this.pName;
	}

	public String getName() {
		return name;
	}

	@Override
	public String[] getMethodNames() {
		return this.methods;
	}

	@Override
	public Object[] call(IComputer computer, String method, Object[] arguments) throws LuaException {
		Object[] ret = {false, new Object()};
		boolean retB = false;
		switch(method){
		case "listMethods":
			Object[] o = new Object[methods.length];
			for (int i = 0;i < o.length;i++) {
				o[i] = methods[i];
			}
			return o;
		case "setPublic":
			if (arguments.length < 2) {
				throw new LuaException("Too few arguments (expected num channel, Object)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 2)) {
				throw new LuaException("Bad argument (" + arguments[0] + ") #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					// Everything good
					Entry<String, Object> e = new EmptyEntry<>(playerName, arguments[1]);
					globals.put(channel, e);
					retB = true;
				}
			}
			break;
		case "setPrivate":
			if (arguments.length < 2) {
				throw new LuaException("Too few arguments (expected num channel, Object)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 2)) {
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					TMPlayerHandler p = getPlayerHandler();
					if(p != null){
						// Everything good
						p.EnderMemoryPrivate.put(channel, arguments[1]);
					}else{
						throw new LuaException("Missing player");
					}
					/*int[] list = find(GlobalFields.EnderMemoryObj[1], this.playerName);
					boolean found = list[0] == 1;
					if (found) {
						int pos = list[1];
						GlobalFields.EnderMemoryObj[1][pos][1][channel] = arguments[1];
					} else {
						int pos = list[1];
						// newPlayer[1] =
						// TomsMathHelper.fillObjectSimple(65536);
						GlobalFields.EnderMemoryObj[1][pos][0][0] = this.playerName;
						GlobalFields.EnderMemoryObj[1][pos][1][channel] = arguments[1];
					}*/
					retB = true;
				}
			}
			break;
		case "getPublic":
			if (arguments.length < 1) {
				throw new LuaException("Too few arguments (expected num channel)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 1)) {
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					// Everything good
					Entry<String, Object> e = globals.get(channel);
					ret[1] = e == null ? null : e.getValue();
					retB = true;
				}
			}
			break;
		case "getPrivate":
			if (arguments.length < 1) {
				throw new LuaException("Too few arguments (expected num channel)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 1)) {
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					TMPlayerHandler p = getPlayerHandler();
					if(p != null){
						// Everything good
						ret[1] = p.EnderMemoryPrivate.get(channel);
					}else{
						throw new LuaException("Missing player");
					}
					retB = true;
					/*int[] list = find(GlobalFields.EnderMemoryObj[1], this.playerName);
					boolean found = list[0] == 1;
					if (found) {
						int pos = list[1];
						ret[1] = GlobalFields.EnderMemoryObj[1][pos][1][channel];
					} else {
						ret[1] = null;
					}*/
				}
			}
			break;
		case "getPublicPlayerName":
			if (arguments.length < 1) {
				throw new LuaException("Too few arguments (expected int channel)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 1)) {
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					// Everything good
					retB = true;
					Entry<String, Object> e = globals.get(channel);
					ret[1] = e == null ? null : e.getKey();
				}
			}
			break;
		case "getPlayerName":
			ret[1] = this.playerName;
			retB = true;
			break;
		case "queuePublicEvent":
			if (arguments.length < 1) {
				throw new LuaException("Too few arguments (expected int channel)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 1)) {
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					retB = true;
					for (List<IComputer> c : EnderMemoryIComputerAccess.values()) {
						ComputerHelper.queueEvent(c, "enderMemoryPublicEvent", new Object[]{channel + 1, this.playerName});
					}
				}
			}
			break;
		case "queuePrivateEvent":
			if (arguments.length < 1) {
				throw new LuaException("Too few arguments (expected int channel)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 1)) {
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					retB = true;
					TMPlayerHandler p = getPlayerHandler();
					if(p != null){
						// Everything good
						ComputerHelper.queueEvent(p.EnderMemoryIComputerAccess, "enderMemoryPrivateEvent", new Object[]{channel + 1});
					}else{
						throw new LuaException("Missing player");
					}
					/*Set<Entry<String, List<IComputer>>> set = EnderMemoryIComputerAccess.entrySet();
					for (Entry<String, List<IComputer>> cSet : set) {
						if (cSet.getKey().equals(this.playerName)) {
							ComputerHelper.queueEvent(cSet.getValue(), "enderMemoryPrivateEvent", new Object[]{channel + 1});
						}
					}*/
				}
			}
			break;
		case "queuePublicDescEvent":
			if (arguments.length < 2) {
				throw new LuaException("Too few arguments (expected int channel, Object)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 2)) {
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					retB = true;
					for (List<IComputer> c : EnderMemoryIComputerAccess.values()) {
						ComputerHelper.queueEvent(c, "enderMemoryPublicDescEvent", new Object[]{channel + 1, this.playerName, arguments[1]});
					}
				}
			}
			break;
		case "queuePrivateDescEvent":
			if (arguments.length < 1) {
				throw new LuaException("Too few arguments (expected int channel, Object)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 1)) {
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					retB = true;
					/*Set<Entry<String, List<IComputer>>> set = EnderMemoryIComputerAccess.entrySet();
					for (Entry<String, List<IComputer>> cSet : set) {
						if (cSet.getKey().equals(this.playerName)) {
							ComputerHelper.queueEvent(cSet.getValue(), "enderMemoryPrivateDescEvent", new Object[]{channel + 1, arguments[1]});
						}
					}*/
					TMPlayerHandler p = getPlayerHandler();
					if(p != null){
						// Everything good
						ComputerHelper.queueEvent(p.EnderMemoryIComputerAccess, "enderMemoryPrivateDescEvent", new Object[]{channel + 1, arguments[1]});
					}else{
						throw new LuaException("Missing player");
					}
				}
			}
			break;
		case "attactGlobalEventQueue":
			if (arguments.length < 0) {
				throw new LuaException("Too few arguments (expected int channel)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 0)) {
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					TomsModUtils.getOrPut(EnderMemoryIComputerAccess, channel, ArrayList::new).add(computer);
				}
			}
			break;
		case "detactGlobalEventQueue":
			if (arguments.length < 0) {
				throw new LuaException("Too few arguments (expected int channel)");
			} else if (!(arguments[0] instanceof Double) && !(arguments.length < 0)) {
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor((Double) arguments[0] - 1);
				if (channel > 65535) {
					throw new LuaException("Bad argument #1 (too big number (" + (channel + 1) + ") maximum value is 65536 )");
				} else if (channel < 0) {
					throw new LuaException("Bad argument #1 (too small number (" + (channel + 1) + ") minimum value is 1 )");
				} else {
					List<IComputer> c = EnderMemoryIComputerAccess.get(channel);
					if(c != null){
						c.remove(computer);
					}
				}
			}
			break;
		}
		ret[0] = retB;
		return ret;
	}

	@Override
	public void attach(IComputer computer) {
		//computers.add(computer);
		//EnderMemoryIComputerAccess.put(playerName, computers);
		// System.out.println("attach");
		// this.attach(computer);
		TMPlayerHandler p = getPlayerHandler();
		if(p != null){
			p.EnderMemoryIComputerAccess.add(computer);
		}
	}

	@Override
	public void detach(IComputer computer) {
		//computers.remove(computer);
		//EnderMemoryIComputerAccess.put(playerName, computers);
		// this.detach(computer);
		TMPlayerHandler p = getPlayerHandler();
		if(p != null){
			p.EnderMemoryIComputerAccess.remove(computer);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.playerName = tag.getString("player");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setString("player", this.playerName);
		return tag;
	}

	/*@Override
	public void updateEntity() {
		/*if(this.eventCooldown == 0){
			for(Object[][] o : GlobalFields.enderMemoryEvent[0]){

			}
			for(Object[] o : GlobalFields.enderMemoryEvent[1][0]){

			}
			this.eventCooldown = 10;
		}
		if(this.eventCooldown > 0) this.eventCooldown--;*/
	/*}

	private static int[] find(Object[][][] table, String pName) {
		int[] returnData = {0, 0};
		for (int i = 0;i < table.length;i++) {
			returnData[1] = i;
			// current {{<x,y,z>},{<color>}}
			Object[] current = table[i][0];
			if (current.length == 0 || !(current[0] instanceof String))
				continue;
			String p = (String) current[0];
			if (pName == p) {
				returnData[0] = 1;
				break;
			}
		}
		return returnData;
	}*/
	public TMPlayerHandler getPlayerHandler(){
		return TMPlayerHandler.getPlayerHandlerForName(pName);
	}
}
