package com.tom.network.messages;

import com.tom.network.MessageBase;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageMonitor extends MessageBase<MessageMonitor>{

	private String[] screen;
	private int color;
	
	public MessageMonitor(){}
	
	public MessageMonitor(String[] screen, int color){
		this.screen = screen;
		this.color = color;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.color = buf.readInt();
		for (int i=0; i<16; i++){
			String current = "";
			for (int i2=1; i2<5; i++){
				int currentScreen = buf.readInt();
				current = current + Integer.toString(currentScreen).substring(1);
			}
			this.screen[i] = current;
		}
		/*String screenNumS;
		String screenData;
		this.screenNum = buf.readInt();
		//Packet = 1<color><screenData>
		//Remove   ^
		screenNumS = Integer.toString(this.screenNum).substring(1);
		this.color = Integer.getInteger(screenNumS.substring(2,2));
		screenData = screenNumS.substring(2);
		//Get the screen variable
		for (int i = 0;i <16;i++){
			int iU = i == 0 ? 1 : i * 16;
			this.screen[i] = screenData.substring(iU, iU+16);
		}*/
		
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(color);
		for (int i=0; i<16; i++){
			for (int i2=1; i2<5; i++){
				int i2U = i2 == 0 ? 1 : i2 * 4;
				buf.writeInt(Integer.getInteger(this.screen[i].substring(i2U, i2U + 4)));
			}
		}
	}

	@Override
	public void handleClientSide(MessageMonitor message, EntityPlayer player) {
		
		
	}

	@Override
	public void handleServerSide(MessageMonitor message, EntityPlayer player) {
		
	}
	
}
