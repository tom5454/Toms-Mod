package com.tom.network.messages;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.terminal.GuiPartList;
import com.tom.apis.BigEntry;
import com.tom.apis.EmptyBigEntry;
import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.lib.GlobalFields;
import com.tom.network.MessageBase;
import com.tom.network.NetworkHandler;

import com.tom.core.item.TabletHandler;

import com.tom.core.tileentity.TileEntityTabletController;
import com.tom.core.tileentity.TileEntityTabletController.LuaSound;
import com.tom.core.tileentity.TileEntityWirelessPeripheral;
import com.tom.core.tileentity.gui.GuiEmpty;
import com.tom.core.tileentity.gui.GuiTablet;

import io.netty.buffer.ByteBuf;

public class MessageTabGuiAction extends MessageBase<MessageTabGuiAction> {
	private int id,x,y,button,charId, z;
	private long num0;
	private char c;
	private String text;
	private boolean force, in, isOff;
	private float pitch;
	private List<BigEntry<String, Integer,Integer,Integer,Integer>> hitboxes;
	public MessageTabGuiAction(char c, int id){
		this.id = 0;
		this.c = c;
		this.charId = id;
	}
	public MessageTabGuiAction(int x, int y, int button){
		this.id = 1;
		this.x = x;
		this.y = y;
		this.button = button;
	}
	public MessageTabGuiAction(int x, int y, int button, long num3){
		this.id = 2;
		this.x = x;
		this.y = y;
		this.button = button;
		this.num0 = num3;
	}
	public MessageTabGuiAction(int x, int y, String text){
		this.id = 3;
		this.x = x;
		this.y = y;
		this.text = text;
	}
	public MessageTabGuiAction(String text, boolean force){
		this.id = 4;
		this.text = text;
		this.force = force;
	}
	public MessageTabGuiAction(){
		this.id = 5;
	}
	public MessageTabGuiAction(String text){
		this.id = 6;
		this.text = text;
	}
	public MessageTabGuiAction(int slot, boolean isOff){
		this.id = 7;
		this.button = slot;
		this.isOff = isOff;
	}
	public MessageTabGuiAction(int id, char c, int x, int y, int z){
		this.id = 8;
		this.c = c;
		this.charId = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public MessageTabGuiAction(String text, int x, int y, boolean in){
		this.id = 9;
		this.text = text;
		this.x = x;
		this.y = y;
		this.in = in;
	}
	public MessageTabGuiAction(String text, int x, int y){
		this.id = 10;
		this.x = x;
		this.y = y;
		this.text = text;
	}
	public MessageTabGuiAction(int x, int y, int z, List<BigEntry<String, Integer,Integer,Integer,Integer>> hitboxes, boolean eEsc){
		this.id = 11;
		this.x = x;
		this.y = y;
		this.z = z;
		this.hitboxes = hitboxes;
		this.in = eEsc;
	}
	public static MessageTabGuiAction getCloseMessage(){
		MessageTabGuiAction m = new MessageTabGuiAction();
		m.id = 12;
		return m;
	}
	public static MessageTabGuiAction getResolutionMessage(){
		MessageTabGuiAction m = new MessageTabGuiAction();
		m.id = 13;
		m.force = false;
		return m;
	}
	public MessageTabGuiAction(LuaSound sound, boolean mode, float pitch){
		this.id = 14;
		this.text = sound.sound;
		this.force = mode;
		this.pitch = pitch;
	}
	public static MessageTabGuiAction getTranslationMessage(String text){
		MessageTabGuiAction m = new MessageTabGuiAction(text);
		m.id = 15;
		return m;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		if(id == 0){
			this.c = buf.readChar();
			this.charId = buf.readInt();
		}else if(id == 1){
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.button = buf.readInt();
		}else if(id == 2){
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.button = buf.readInt();
			this.num0 = buf.readLong();
		}else if(id == 3){
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.text = ByteBufUtils.readUTF8String(buf);
		}else if(id == 4){
			this.text = ByteBufUtils.readUTF8String(buf);
			this.force = buf.readBoolean();
		}else if(id == 6){
			this.text = ByteBufUtils.readUTF8String(buf);
		}else if(id == 7){
			this.isOff = buf.readBoolean();
			this.button = buf.readInt();
		}else if(id == 8){
			this.c = buf.readChar();
			this.charId = buf.readInt();
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.z = buf.readInt();
		}else if(id == 9){
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.in = buf.readBoolean();
			this.text = ByteBufUtils.readUTF8String(buf);
		}else if(id == 10){
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.in = buf.readBoolean();
			if(in)this.text = ByteBufUtils.readUTF8String(buf);
		}else if(id == 11){
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.z = buf.readInt();
			this.in = buf.readBoolean();
			NBTTagCompound tag = ByteBufUtils.readTag(buf);
			NBTTagList list = (NBTTagList) tag.getTag("l");
			this.hitboxes = new ArrayList<BigEntry<String, Integer,Integer,Integer,Integer>>();
			for(int i = 0;i<list.tagCount();i++){
				NBTTagCompound t = list.getCompoundTagAt(i);
				int[] values = t.getIntArray("v");
				String key = t.getString("k");
				this.hitboxes.add(new EmptyBigEntry<String, Integer,Integer,Integer,Integer>(key, values[0],values[1],values[2],values[3]));
			}
		}else if(id == 13){
			this.force = buf.readBoolean();
			if(this.force){
				this.x = buf.readInt();
				this.y = buf.readInt();
			}
		}else if(id == 14){
			this.force = buf.readBoolean();
			this.pitch = buf.readFloat();
			this.text = ByteBufUtils.readUTF8String(buf);
		}else if(id == 15){
			this.text = ByteBufUtils.readUTF8String(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		if(id == 0){
			buf.writeChar(c);
			buf.writeInt(charId);
		}else if(id == 1){
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(button);
		}else if(id == 2){
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(button);
			buf.writeLong(num0);
		}else if(id == 3){
			buf.writeInt(x);
			buf.writeInt(y);
			ByteBufUtils.writeUTF8String(buf, text);
		}else if(id == 4){
			ByteBufUtils.writeUTF8String(buf, text);
			buf.writeBoolean(force);
		}else if(id == 6){
			ByteBufUtils.writeUTF8String(buf, text);
		}else if(id == 7){
			buf.writeBoolean(isOff);
			buf.writeInt(button);
		}else if(id == 8){
			buf.writeChar(c);
			buf.writeInt(charId);
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
		}else if(id == 9){
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeBoolean(in);
			ByteBufUtils.writeUTF8String(buf, text);
		}else if(id == 10){
			buf.writeInt(x);
			buf.writeInt(y);
			boolean in = text != null;
			buf.writeBoolean(in);
			if(in)ByteBufUtils.writeUTF8String(buf, text);
		}else if(id == 11){
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
			buf.writeBoolean(in);
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagList list = new NBTTagList();
			for(BigEntry<String, Integer,Integer,Integer,Integer> c : hitboxes){
				NBTTagCompound t = new NBTTagCompound();
				t.setString("k", c.getKey());
				t.setIntArray("v", new int[]{c.getValue1(), c.getValue2(), c.getValue3(),c.getValue4()});
				list.appendTag(t);
			}
			tag.setTag("l", list);
			ByteBufUtils.writeTag(buf, tag);
		}else if(id == 13){
			buf.writeBoolean(force);
			if(this.force){
				buf.writeInt(x);
				buf.writeInt(y);
			}
		}else if(id == 14){
			buf.writeBoolean(force);
			buf.writeFloat(pitch);
			ByteBufUtils.writeUTF8String(buf, text);
		}else if(id == 15){
			ByteBufUtils.writeUTF8String(buf, text);
		}
	}

	@Override
	public void handleClientSide(MessageTabGuiAction message,
			EntityPlayer player) {
		if(TomsModUtils.isClient())handle(message, player);
		else{
			TMLogger.bigError("Trying to call handleClientSide method on the SERVER side. This SHOULDN'T happen!");
		}
	}
	@SideOnly(Side.CLIENT)
	private void handle(MessageTabGuiAction message, EntityPlayer player){
		this.id = message.id;
		if(this.id == 4){
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if(gui instanceof GuiTablet) {
				GuiTablet g = (GuiTablet) gui;
				if(this.force){
					g.textFieldText = this.text;
				}else{
					g.textFieldText = g.textFieldText + this.text;
				}
				g.onTextfieldUpdate(0);
			}
		}else if(id == 5){
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if(gui instanceof GuiTablet) {
				GuiTablet g = (GuiTablet) gui;
				NetworkHandler.sendToServer(new MessageTabGuiAction(g.textField.getText()));
			}
		}else if(id == 11){
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if(gui instanceof GuiEmpty){
				((GuiEmpty)gui).gui.hitboxes = message.hitboxes;
				((GuiEmpty)gui).gui.eEsc = message.in;
			}else{
				GuiPartList g = new GuiPartList(message.x,message.y,message.z);
				g.hitboxes = message.hitboxes;
				g.eEsc = message.in;
				Minecraft.getMinecraft().displayGuiScreen(new GuiEmpty(g));
			}
		}else if(id == 12){
			Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
		}else if(id == 13){
			MessageTabGuiAction m = new MessageTabGuiAction();
			m.id = 13;
			Minecraft mc = Minecraft.getMinecraft();
			m.x = mc.displayWidth;
			m.y = mc.displayHeight;
			m.force = true;
			NetworkHandler.sendToServer(m);
		}else if(id == 14){
			boolean mode = message.force;
			SoundHandler sh = Minecraft.getMinecraft().getSoundHandler();
			if(mode){
				ResourceLocation loc = new ResourceLocation(message.text);
				ISound sound = new PositionedSoundRecord(loc, SoundCategory.MASTER, 0.25F, message.pitch, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
				CoreInit.log.debug("Playing sound: "+message.text);
				GlobalFields.tabletSounds.add(sound);
				sh.playSound(sound);
			}else{
				CoreInit.log.debug("Stopping the sound");
				ResourceLocation loc = new ResourceLocation(message.text);
				String locS = loc.getResourceDomain() + ":" + loc.getResourcePath();
				for(ISound cS : GlobalFields.tabletSounds){
					ResourceLocation locC = cS.getSoundLocation();
					String locCS = locC.getResourceDomain() + ":" + locC.getResourcePath();
					//CoreInit.log.info(locS + "|" + locCS);
					if(locS.equals(locCS)){
						CoreInit.log.info(sh.isSoundPlaying(cS));
						sh.stopSound(cS);
						sh.stopSounds();
						GlobalFields.tabletSounds.remove(cS);
						CoreInit.log.info("Sound playing stopped: " + message.text);
						break;
					}
				}
			}
		}else if(id == 15){
			String tr = I18n.format(message.text);
			MessageTabGuiAction m = new MessageTabGuiAction();
			m.id = 15;
			m.text = tr;
			NetworkHandler.sendToServer(m);
		}
	}
	@Override
	public void handleServerSide(MessageTabGuiAction message,
			EntityPlayer player) {
		this.id = message.id;
		if(id != 7 && id != 8){
			for(int i = 0;i<player.inventory.getSizeInventory();i++){
				ItemStack is = player.inventory.getStackInSlot(i);
				if(is != null && is.getItem() == CoreInit.Tablet){
					if(is.getTagCompound() != null && is.getTagCompound().hasKey("x") && is.getTagCompound().hasKey("y") && is.getTagCompound().hasKey("z") && is.getTagCompound().hasKey("id")){
						TileEntity tile = player.worldObj.getTileEntity(new BlockPos(is.getTagCompound().getInteger("x"), is.getTagCompound().getInteger("y"), is.getTagCompound().getInteger("z")));
						NBTTagCompound tabTag = is.getTagCompound();
						if(tile instanceof TileEntityTabletController){
							TileEntityTabletController te = (TileEntityTabletController) tile;
							if(this.id == 0){
								te.queueEvent("tablet_char", new Object[]{player.getName(), message.c, message.charId});
							}else if(this.id == 1){
								te.queueEvent("tablet_mouse", new Object[]{player.getName(), message.button, message.x, message.y});
							}else if(this.id == 3){
								te.queueEvent("tablet_input", new Object[]{player.getName(), message.text, message.x, message.y});
							}else if(this.id == 6){
								int id = tabTag.getInteger("id");
								TabletHandler tab = te.getTablet(id);
								if(tab != null){
									tab.term.inputText = message.text;
									te.queueEvent("tab_textBoxReceive", new Object[]{player.getName()});
								}
							}else if(this.id == 9){
								int id = tabTag.getInteger("id");
								TabletHandler tab = te.getTablet(id);
								if(tab != null){
									tab.cursorX = message.x;
									tab.cursorY = message.y;
									if(message.in){
										//te.queueEvent("tablet_hitbox", new Object[]{player.getName(), message.text, message.x, message.y});
										te.queueEvent("tablet_hitbox_"+player.getName(), new Object[]{message.text, message.x, message.y});
									}else{
										//te.queueEvent("tablet_hitbox_out", new Object[]{player.getName(), message.x, message.y});
										te.queueEvent("tablet_hitbox_out_"+player.getName(), new Object[]{message.x, message.y});
									}
									tab.in = message.in;
									tab.cHitBox = message.in ? message.text : null;
								}
							}else if(this.id == 10){
								int id = tabTag.getInteger("id");
								TabletHandler tab = te.getTablet(id);
								if(tab != null){
									tab.cursorX = message.x;
									tab.cursorY = message.y;
									tab.in = message.in;
									tab.cHitBox = message.in ? message.text : null;
								}
							}else if(this.id == 5){
								te.queueEvent("tablet_gui_close_"+player.getName(), new Object[]{});
							}else if(this.id == 13){
								te.queueEvent("player_resulution_info_"+player.getName(), new Object[]{message.x, message.y});
							}else if(id == 15){
								te.queueEvent("player_translation_"+player.getName(), new Object[]{message.text});
							}
						}
					}
				}
			}
		}else if(id == 7 && player.inventory.getStackInSlot(button) != null){
			player.inventory.getStackInSlot(button).getItem().onItemRightClick(player.inventory.getStackInSlot(button), player.worldObj, player, message.isOff ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
		}else if(id == 8){
			TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
			if(tile instanceof TileEntityWirelessPeripheral){
				TileEntityWirelessPeripheral te = (TileEntityWirelessPeripheral) tile;
				Character c = message.c;
				te.queueEvent("cam_char", new Object[]{c.toString(),message.charId});
			}
		}
	}
}
