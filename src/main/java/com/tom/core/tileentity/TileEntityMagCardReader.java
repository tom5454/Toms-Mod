package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.tom.api.item.IMagCard;
import com.tom.api.tileentity.IWirelessPeripheralController;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.client.ICustomModelledTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityMagCardReader extends TileEntityTomsMod implements ICustomModelledTileEntity{

	public int direction = 0;
	public EnumFacing d = EnumFacing.DOWN;
	//private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	public boolean ledG = false;
	private int ledGTime = 0;
	private int ledRTime = 0;
	private static final int ledTimeMax = 4;
	public List<String> code = new ArrayList<String>();
	public boolean isCodeMode = false;
	public boolean ledR = false;
	private boolean isLedGFixed = false;
	public int posX = 0;
	public int posY = 0;
	public int posZ = 0;
	public boolean linked = false;

	/*@Override
	public void attach(IComputerAccess computer) {
		computers.add(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		computers.remove(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other == this;
	}*/

	public void onBlockActivated(EntityPlayer player, ItemStack is) {

	}
	@Override
	public void writeToPacket(NBTTagCompound buf){
		buf.setInteger("dir", direction);
		buf.setInteger("d", this.d.ordinal());
		buf.setBoolean("g", ledG);
		buf.setBoolean("r", ledR);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf){
		this.direction = buf.getInteger("dir");
		this.d = EnumFacing.values()[buf.getInteger("d")];
		this.ledG = buf.getBoolean("g");
		this.ledR = buf.getBoolean("r");
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.direction = tag.getInteger("direction");
		this.d = EnumFacing.values()[tag.getInteger("d")];
		NBTTagList list = (NBTTagList) tag.getTag("list");
		this.code.clear();
		for(int i = 0;i<list.tagCount();i++){
			this.code.add(list.getStringTagAt(i));
		}
		this.isCodeMode = tag.getBoolean("codeMode");
		this.posX = tag.getInteger("linkX");
		this.posY = tag.getInteger("linkY");
		this.posZ = tag.getInteger("linkZ");
		this.linked = tag.getBoolean("linked");
		//this.debug = tag.getBoolean("debug");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("direction", this.direction);
		tag.setInteger("d", this.d.ordinal());
		NBTTagList list = new NBTTagList();
		for(String s : this.code){
			list.appendTag(new NBTTagString(s));
		}
		tag.setTag("list", list);
		tag.setBoolean("codeMode", this.isCodeMode);
		tag.setInteger("linkX", this.posX);
		tag.setInteger("linkY", this.posY);
		tag.setInteger("linkZ", this.posZ);
		tag.setBoolean("linked", this.linked);
		//tag.setBoolean("debug", this.debug);
		return tag;
	}
	/*@Override
	public String getType() {
		return "tm_mag_card_device";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"isCodeMode","setCodeMode","addCode","removeCode","containsCode"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] a) throws LuaException,
			InterruptedException {
		if(method == 0){
			return new Object[]{this.isCodeMode};
		}else if(method == 1){
			this.isCodeMode = a.length > 0 ? (a[0] instanceof Boolean ? (Boolean) a[0] : true) : false;
		}else if(method == 2){
			if(a.length > 0 && a[0] instanceof String){
				this.code.add((String) a[0]);
			}
		}else if(method == 3){
			if(a.length > 0 && a[0] instanceof String){
				this.code.remove(a[0]);
			}
		}else if(method == 4){
			if(a.length > 0 && a[0] instanceof String){
				return new Object[]{this.code.contains((String) a[0])};
			}
		}
		return null;
	}*/


	public void activate(EntityPlayer player, ItemStack is){
		IMagCard card = (IMagCard) is.getItem();
		if(this.isCodeMode){
			Object[] obj = this.code.toArray();
			String[] string = new String[obj.length];
			for(int i = 0;i<obj.length;i++){
				string[i] = obj[i].toString();
			}
			boolean good = card.isCodeEqual(is, worldObj, string, player);
			if(good){
				this.ledGTime = ledTimeMax;
			}else{
				this.ledRTime = ledTimeMax;
			}
			this.queueEvent("tm_magcard_swipe", new Object[]{player.getName(),good});
			markBlockForUpdate(pos);
		}else{
			String[] args = card.getCodes(is, worldObj, player);
			Object[] a = new Object[args.length+1];
			for(int i = 0;i<args.length;i++){
				a[i+1] = args[i];
			}
			a[0] = player.getName();
			this.queueEvent("tm_magcard_swipe", a);
			this.ledGTime = ledTimeMax;
			markBlockForUpdate(pos);
		}
	}
	@Override
	public void updateEntity(){
		if(!this.worldObj.isRemote){
			if(this.linked){
				TileEntity tilee = worldObj.getTileEntity(new BlockPos(posX, posY, posZ));
				this.linked = tilee instanceof IWirelessPeripheralController;
			}
			if(this.ledRTime > 0){
				this.ledR = true;
				this.ledRTime = this.ledRTime - 1;
				this.ledG = false;
				markBlockForUpdate(pos);
			}else if(this.ledGTime > 0){
				this.ledG = true;
				this.ledGTime = this.ledGTime - 1;
				this.ledR = false;
				markBlockForUpdate(pos);
			}else{
				this.ledG = isLedGFixed;
				this.ledR = false;
				markBlockForUpdate(pos);
			}
		}
	}

	public void link(int x, int y, int z){
		//if(this.debug) System.out.println("link");
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.linked = true;
	}
	public void queueEvent(String event, Object[] a){
		if(this.linked){
			TileEntityWirelessPeripheral te = (TileEntityWirelessPeripheral) worldObj.getTileEntity(new BlockPos(posX, posY, posZ));
			te.queueEvent(event,a);
		}
	}
	@Override
	public EnumFacing getFacing() {
		return d;
	}
}
