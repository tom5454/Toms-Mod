package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.tom.api.item.IMagCard;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.block.MagCardDevice;
import com.tom.lib.Configs;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.Optional;
@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityMagCardDevice extends TileEntityTomsMod implements
IPeripheral {
	private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	public int direction = 0;
	//public boolean ledG = false;
	private int ledGTime = 0;
	private int ledRTime = 0;
	private static final int ledTimeMax = 4;
	private List<String> code = new ArrayList<String>();
	private boolean isCodeMode = false;
	//public boolean ledR = false;
	//private boolean isLedGFixed = false;
	private boolean writeMode = false;
	private String wName = "";
	private String wData = "";
	@Override
	public String getType() {
		return "tm_mag_card_device";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"write","isCodeMode","setCodeMode","addCode","removeCode","containsCode"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] a) throws LuaException,
	InterruptedException {
		if(method == 0){
			if(a.length > 1){
				wData = a[1].toString();
				wName = a[0].toString();
				this.writeMode = true;
				boolean mode = a.length > 2 ? (a[2] instanceof Boolean ? (Boolean) a[2] : false) : true;
				if(mode) return context.pullEvent("tm_magcard_write");
			}
		}else if(method == 1){
			return new Object[]{this.isCodeMode};
		}else if(method == 2){
			this.isCodeMode = a.length > 0 ? (a[0] instanceof Boolean ? (Boolean) a[0] : true) : false;
		}else if(method == 3){
			if(a.length > 0 && a[0] instanceof String){
				this.code.add((String) a[0]);
			}
		}else if(method == 4){
			if(a.length > 0 && a[0] instanceof String){
				this.code.remove(a[0]);
			}
		}else if(method == 5){
			if(a.length > 0 && a[0] instanceof String){
				return new Object[]{this.code.contains(a[0])};
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
		return other == this;
	}
	public void activate(EntityPlayer player, ItemStack is){
		IMagCard card = (IMagCard) is.getItem();
		if(this.writeMode){
			card.write(wData, wName, is, worldObj, player);
			this.writeMode = false;
			for(IComputerAccess c : computers){
				c.queueEvent("tm_magcard_write", new Object[]{c.getAttachmentName(),player.getName(),wName});
			}
		}else{
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
				for(IComputerAccess c : computers){
					c.queueEvent("tm_magcard_swipe", new Object[]{c.getAttachmentName(),player.getName(),good});
				}
				markBlockForUpdate(pos);
			}else{
				String[] args = card.getCodes(is, worldObj, player);
				Object[] a = new Object[args.length+2];
				for(int i = 0;i<args.length;i++){
					a[i+2] = args[i];
				}
				a[1] = player.getName();
				for(IComputerAccess c : computers){
					a[0] = c.getAttachmentName();
					c.queueEvent("tm_magcard_swipe", a);
				}
				this.ledGTime = ledTimeMax;
				markBlockForUpdate(pos);
			}
		}
	}
	//	@Override
	//	public void writeToPacket(ByteBuf buf){
	//		/*buf.writeBoolean(ledG);
	//		buf.writeBoolean(ledR);
	//		buf.writeInt(direction);*/
	//	}
	//
	//	@Override
	//	public void readFromPacket(ByteBuf buf){
	//		/*/this.ledG = buf.readBoolean();
	//		this.ledR = buf.readBoolean();
	//		this.direction = buf.readInt();
	//		int xCoord = pos.getX();
	//		int yCoord = pos.getY();
	//		int zCoord = pos.getZ();
	//		this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);*/
	//	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		this.direction = tag.getInteger("direction");
		NBTTagList list = (NBTTagList) tag.getTag("list");
		this.code.clear();
		for(int i = 0;i<list.tagCount();i++){
			this.code.add(list.getStringTagAt(i));
		}
		this.isCodeMode = tag.getBoolean("codeMode");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);
		tag.setInteger("direction", this.direction);
		NBTTagList list = new NBTTagList();
		for(String s : this.code){
			list.appendTag(new NBTTagString(s));
		}
		tag.setTag("list", list);
		tag.setBoolean("codeMode", this.isCodeMode);
		return tag;
	}
	@Override
	public void updateEntity(){
		if(!this.worldObj.isRemote){
			if(this.writeMode){
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, MagCardDevice.STATE, 3);
			}else if(this.ledRTime > 0){
				//this.ledR = true;
				this.ledRTime = this.ledRTime - 1;
				//this.ledG = false;
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, MagCardDevice.STATE, 1);
				//this.worldObj.markBlockForUpdate(pos);
			}else if(this.ledGTime > 0){
				//this.ledG = true;
				this.ledGTime = this.ledGTime - 1;
				//this.ledR = false;
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, MagCardDevice.STATE, 2);
				//this.worldObj.markBlockForUpdate(pos);
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, MagCardDevice.STATE, 0);
				//this.ledG = isLedGFixed;
				//this.ledR = false;
				//this.worldObj.markBlockForUpdate(pos);
			}
		}
	}
}
