package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.helpers.ComputerCraftHelper;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.Configs;
import com.tom.lib.GlobalFields;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityEnderMemory extends TileEntityTomsMod implements IPeripheral{
	/**
	 * {
	 *<br>	 {*public
	 * 			{
	 * <br>			{playerName,Object},{playerName,Object},{playerName,Object}
	 *<br>		}
	 * 		},
	 *<br> 		{*private
	 *				{player{<code>playerName</code>},{<code>Object0</code>,<code>Object1</code>...}}
	 *<br>		}
	 *
	 * 		}
	 * }
	 * */
	public String playerName = "";
	private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	public String pName = "tm_ender_memory";
	public String[] methods = {"listMethods","setPublic","setPrivate","getPublic","getPrivate",
			"getPublicPlayerName","getPlayerName","queuePublicEvent","queuePrivateEvent",
			"queuePublicDescEvent","queuePrivateDescEvent"};
	private String name = "tileEntityEnderMemory";
	//private int eventCooldown = 0;
	public TileEntityEnderMemory(){
		super();
	}
	@Override
	public void validate() {

	}

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
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException, InterruptedException {
		Object[] ret = {false,new Object()};
		boolean retB = false;
		int m = method;
		if(m == 0){
			Object[] o = new Object[methods.length];
			for(int i = 0;i<o.length;i++){
				o[i] = methods[i];
			}
			return o;
		} else if(m == 1){
			if (arguments.length < 2){
				throw new LuaException("Too few arguments (expected num channel, Object)");
			} else if(!(arguments[0] instanceof Double) && !(arguments.length < 2)){
				throw new LuaException("Bad argument ("+arguments[0]+") #1 (expected number)");
			} else {
				int channel = MathHelper.floor_double((Double) arguments[0] - 1);
				if(channel > 65535){
					throw new LuaException("Bad argument #1 (too big number ("+(channel+1)+") maximum value is 65536 )");
				}else if(channel < 0){
					throw new LuaException("Bad argument #1 (too small number ("+(channel+1)+") minimum value is 1 )");
				}else {
					//Everything good
					GlobalFields.EnderMemoryObj[0][0][channel][0] = this.playerName;
					GlobalFields.EnderMemoryObj[0][0][channel][1] = arguments[1];
					retB = true;
				}
			}
		} else if(m == 2){
			if (arguments.length < 2){
				throw new LuaException("Too few arguments (expected num channel, Object)");
			} else if(!(arguments[0] instanceof Double) && !(arguments.length < 2)){
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor_double((Double) arguments[0] - 1);
				if(channel > 65535){
					throw new LuaException("Bad argument #1 (too big number ("+(channel+1)+") maximum value is 65536 )");
				}else if(channel < 0){
					throw new LuaException("Bad argument #1 (too small number ("+(channel+1)+") minimum value is 1 )");
				}else {
					//Everything good
					int[] list = find(GlobalFields.EnderMemoryObj[1], this.playerName);
					boolean found = list[0] == 1;
					if(found){
						int pos = list[1];
						GlobalFields.EnderMemoryObj[1][pos][1][channel] = arguments[1];
					}else{
						int pos = list[1];
						//newPlayer[1] = TomsMathHelper.fillObjectSimple(65536);
						GlobalFields.EnderMemoryObj[1][pos][0][0] = this.playerName;
						GlobalFields.EnderMemoryObj[1][pos][1][channel] = arguments[1];
					}
					retB = true;
				}
			}
		}else if(m == 3){
			if (arguments.length < 1){
				throw new LuaException("Too few arguments (expected num channel)");
			} else if(!(arguments[0] instanceof Double) && !(arguments.length < 1)){
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor_double((Double) arguments[0] - 1);
				if(channel > 65535){
					throw new LuaException("Bad argument #1 (too big number ("+(channel+1)+") maximum value is 65536 )");
				}else if(channel < 0){
					throw new LuaException("Bad argument #1 (too small number ("+(channel+1)+") minimum value is 1 )");
				}else {
					//Everything good
					ret[1] = GlobalFields.EnderMemoryObj[0][0][channel][1];
					retB = true;
				}
			}
		}else if(m == 4){
			if (arguments.length < 1){
				throw new LuaException("Too few arguments (expected num channel)");
			} else if(!(arguments[0] instanceof Double) && !(arguments.length < 1)){
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor_double((Double) arguments[0] - 1);
				if(channel > 65535){
					throw new LuaException("Bad argument #1 (too big number ("+(channel+1)+") maximum value is 65536 )");
				}else if(channel < 0){
					throw new LuaException("Bad argument #1 (too small number ("+(channel+1)+") minimum value is 1 )");
				}else {
					//Everything good
					retB = true;
					int[] list = find(GlobalFields.EnderMemoryObj[1], this.playerName);
					boolean found = list[0] == 1;
					if(found){
						int pos = list[1];
						ret[1] = GlobalFields.EnderMemoryObj[1][pos][1][channel];
					}else{
						ret[1] = null;
					}
				}
			}
		}else if(m == 5){
			if (arguments.length < 1){
				throw new LuaException("Too few arguments (expected int channel)");
			} else if(!(arguments[0] instanceof Double) && !(arguments.length < 1)){
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor_double((Double) arguments[0] - 1);
				if(channel > 65535){
					throw new LuaException("Bad argument #1 (too big number ("+(channel+1)+") maximum value is 65536 )");
				}else if(channel < 0){
					throw new LuaException("Bad argument #1 (too small number ("+(channel+1)+") minimum value is 1 )");
				}else {
					//Everything good
					retB = true;
					ret[1] = GlobalFields.EnderMemoryObj[0][0][channel][0];
				}
			}
		}else if(m == 6){
			ret[1] = this.playerName;
			retB = true;
			/**queuePublicEvent*/
		}else if(m == 7){
			if (arguments.length < 1){
				throw new LuaException("Too few arguments (expected int channel)");
			} else if(!(arguments[0] instanceof Double) && !(arguments.length < 1)){
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor_double((Double) arguments[0] - 1);
				if(channel > 65535){
					throw new LuaException("Bad argument #1 (too big number ("+(channel+1)+") maximum value is 65536 )");
				}else if(channel < 0){
					throw new LuaException("Bad argument #1 (too small number ("+(channel+1)+") minimum value is 1 )");
				}else {
					retB = true;
					for(List<IComputerAccess> c : GlobalFields.EnderMemoryIComputerAccess.values()){
						ComputerCraftHelper.queueEvent(c,"enderMemoryPublicEvent", new Object[]{channel + 1,this.playerName});
					}
				}
			}
		}else if(m == 8){
			if (arguments.length < 1){
				throw new LuaException("Too few arguments (expected int channel)");
			} else if(!(arguments[0] instanceof Double) && !(arguments.length < 1)){
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor_double((Double) arguments[0] - 1);
				if(channel > 65535){
					throw new LuaException("Bad argument #1 (too big number ("+(channel+1)+") maximum value is 65536 )");
				}else if(channel < 0){
					throw new LuaException("Bad argument #1 (too small number ("+(channel+1)+") minimum value is 1 )");
				}else {
					retB = true;
					Set<Entry<String, List<IComputerAccess>>> set = GlobalFields.EnderMemoryIComputerAccess.entrySet();
					for(Entry<String, List<IComputerAccess>> cSet : set){
						if (cSet.getKey().equals(this.playerName)) {
							ComputerCraftHelper.queueEvent(cSet.getValue(),"enderMemoryPrivateEvent", new Object[]{channel + 1});
						}
					}
				}
			}
		}else if(m == 9){
			if (arguments.length < 2){
				throw new LuaException("Too few arguments (expected int channel, Object)");
			} else if(!(arguments[0] instanceof Double) && !(arguments.length < 2)){
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor_double((Double) arguments[0] - 1);
				if(channel > 65535){
					throw new LuaException("Bad argument #1 (too big number ("+(channel+1)+") maximum value is 65536 )");
				}else if(channel < 0){
					throw new LuaException("Bad argument #1 (too small number ("+(channel+1)+") minimum value is 1 )");
				}else {
					retB = true;
					for(List<IComputerAccess> c : GlobalFields.EnderMemoryIComputerAccess.values()){
						ComputerCraftHelper.queueEvent(c,"enderMemoryPublicDescEvent", new Object[]{channel + 1,this.playerName, arguments[1]});
					}
				}
			}
		}else if(m == 10){
			if (arguments.length < 1){
				throw new LuaException("Too few arguments (expected int channel, Object)");
			} else if(!(arguments[0] instanceof Double) && !(arguments.length < 1)){
				throw new LuaException("Bad argument #1 (expected number)");
			} else {
				int channel = MathHelper.floor_double((Double) arguments[0] - 1);
				if(channel > 65535){
					throw new LuaException("Bad argument #1 (too big number ("+(channel+1)+") maximum value is 65536 )");
				}else if(channel < 0){
					throw new LuaException("Bad argument #1 (too small number ("+(channel+1)+") minimum value is 1 )");
				}else {
					retB = true;
					Set<Entry<String, List<IComputerAccess>>> set = GlobalFields.EnderMemoryIComputerAccess.entrySet();
					for(Entry<String, List<IComputerAccess>> cSet : set){
						if (cSet.getKey().equals(this.playerName)) {
							ComputerCraftHelper.queueEvent(cSet.getValue(), "enderMemoryPrivateDescEvent", new Object[]{channel + 1,arguments[1]});
						}
					}
				}
			}
		}
		ret[0] = retB;
		return ret;
	}

	@Override
	public void attach(IComputerAccess computer) {
		computers.add(computer);
		if(GlobalFields.EnderMemoryIComputerAccess.containsKey(playerName)){
			GlobalFields.EnderMemoryIComputerAccess.remove(playerName);
			GlobalFields.EnderMemoryIComputerAccess.put(playerName, computers);
		}else{
			GlobalFields.EnderMemoryIComputerAccess.put(playerName, computers);
		}
		//System.out.println("attach");
		//this.attach(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		computers.remove(computer);
		if(!GlobalFields.EnderMemoryIComputerAccess.containsValue(computers)){
			GlobalFields.EnderMemoryIComputerAccess.remove(playerName);
			GlobalFields.EnderMemoryIComputerAccess.put(playerName, computers);
		}
		//this.detach(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (other.getType() == this.getType());
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
	@Override
	public void updateEntity(){
		/*if(this.eventCooldown == 0){
			for(Object[][] o : GlobalFields.enderMemoryEvent[0]){

			}
			for(Object[] o : GlobalFields.enderMemoryEvent[1][0]){

			}
			this.eventCooldown = 10;
		}
		if(this.eventCooldown > 0) this.eventCooldown--;*/
	}
	private static int[] find(Object[][][] table, String pName){
		int[] returnData = {0,0};
		for(int i = 0;i<table.length;i++){
			returnData[1] = i;
			//current {{<x,y,z>},{<color>}}
			Object[] current = table[i][0];
			if(current.length == 0 || !(current[0] instanceof String)) continue;
			String p = (String) current[0];
			if(pName == p){
				returnData[0] = 1;
				break;
			}
		}
		return returnData;
	}
}
