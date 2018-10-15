package com.tom.core.item;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import com.tom.api.tileentity.IAccessPoint;
import com.tom.api.tileentity.IConnector;
import com.tom.api.tileentity.IJammer;

import io.netty.buffer.ByteBuf;

public class TabletHandler {
	public TabletHandler(String player) {
		this.playerName = player;
	}
	public Set<IAccessPoint> antenna = new HashSet<>();
	public IAccessPoint connectedAntenna;
	public Set<IConnector> connectedAccessPoints = new HashSet<>();
	public Set<IJammer> jammers = new HashSet<>();
	public boolean antAntenna = true;
	public boolean apAntenna = true;
	public int cursorX = 0;
	public int cursorY = 0;
	public boolean in = false;
	public String cHitBox = "";
	public boolean jammedLast = false;
	public int tabCX, tabCY, tabCZ;

	public void writeToPacket(ByteBuf buf) {
		buf.writeBoolean(antAntenna);
		buf.writeBoolean(apAntenna);
	}

	public void readFromPacket(ByteBuf buf) {
		this.antAntenna = buf.readBoolean();
		this.apAntenna = buf.readBoolean();
	}

	public NBTTagCompound modemTag = new NBTTagCompound();
	public String playerName = "";
	public boolean hasModem = false;
	public Object[] obj = new Object[]{"null", "", false, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0, 0, 0};
	//public List<LuaSound> sounds = new ArrayList<>();
	public boolean connectedToTabC;

	public void queueEvent(String string, Object[] objects) {

	}

	public void getUpdates(String name, String event) {

	}

	public boolean clean(double posX, double posY, double posZ) {
		boolean update = false;
		/*Iterator<IAccessPoint> ant = antenna.iterator();
		while(ant.hasNext()){
			if(!ant.next().isAccessible(posX, posY, posZ)){
				ant.remove();
				update = true;
			}
		}*/
		if(connectedAntenna != null && !connectedAntenna.isAccessible(posX, posY, posZ)){
			update = true;
			connectedAntenna = null;
		}
		Iterator<IConnector> accessPoints = connectedAccessPoints.iterator();
		while(accessPoints.hasNext()){
			if(!accessPoints.next().isAccessible(posX, posY, posZ)){
				accessPoints.remove();
				update = true;
			}
		}
		Iterator<IJammer> jammer = jammers.iterator();
		while(jammer.hasNext()){
			if(!jammer.next().isAccessible(posX, posY, posZ)){
				jammer.remove();
				update = true;
			}
		}
		return update;
	}

	public boolean maches(BlockPos controller) {
		return connectedToTabC && controller.getX() == tabCX && tabCY == controller.getY() && tabCZ == controller.getZ();
	}

	/*public static TabletHandler getTabletHandler(ItemStack stack) {
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
			String s;
			do {
				s = "" + TomsModUtils.randomLong();
			} while (!tabletHandlers.containsKey(new ResourceLocation(s)));
			stack.getTagCompound().setString("name", s);
			ResourceLocation loc = new ResourceLocation(s);
			TabletHandler tab = new TabletHandler(loc);
			tabletHandlers.put(loc, tab);
			return tab;
		}else{
			ResourceLocation loc = new ResourceLocation(stack.getTagCompound().getString("name"));
			TabletHandler tab = tabletHandlers.get(loc);
			if(tab == null){
				tabletHandlers.put(loc, tab = new TabletHandler(loc));
			}
			return tab;
		}
	}*/
}
