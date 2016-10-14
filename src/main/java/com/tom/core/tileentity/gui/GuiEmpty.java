package com.tom.core.tileentity.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;

import com.tom.api.terminal.GuiPartList;
import com.tom.apis.BigEntry;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageTabGuiAction;

public class GuiEmpty extends GuiScreen {
	public GuiPartList gui;
	public GuiEmpty(GuiPartList g){
		this.gui = g;
	}
	public boolean doesGuiPauseGame(){
		return false;
	}
	protected void mouseClickMove(int x, int y, int b,long tick){
		//System.out.println(x+" "+y+" "+b+" "+tick);
	}
	protected void mouseClicked(int x, int y, int b){
		NetworkHandler.sendToServer(new MessageTabGuiAction(5,13,b));
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		gui.cX = mouseX;
		gui.cY = mouseY;
		gui.currentHitbox = null;
		String currentHitboxOld = gui.currentHitbox;
		if(gui.hitboxes != null)for(BigEntry<String, Integer,Integer,Integer,Integer> c : gui.hitboxes){
			if(c.getValue1() <= mouseX && mouseX >= c.getValue2()){
				if(c.getValue3() <= mouseY && mouseY >= c.getValue4()){
					gui.currentHitbox = c.getKey();
					break;
				}
			}
		}
		if((currentHitboxOld != null && !currentHitboxOld.equals(gui.currentHitbox) && gui.currentHitbox != null) || (currentHitboxOld == null && gui.currentHitbox != null)){
			NetworkHandler.sendToServer(new MessageTabGuiAction(gui.currentHitbox,mouseX,mouseY,true));
		}else{
			NetworkHandler.sendToServer(new MessageTabGuiAction(gui.currentHitbox,mouseX,mouseY));
		}
		
	}
	public void keyTyped(char character, int id) throws IOException{
		super.keyTyped(character, id);
		if(id == 1){
			if(gui.eEsc || (isShiftKeyDown())){
				NetworkHandler.sendToServer(new MessageTabGuiAction());
				return;
			}else{
				if(!(mc.currentScreen instanceof GuiEmpty)) mc.displayGuiScreen(new GuiEmpty(gui));
			}
		}
		NetworkHandler.sendToServer(new MessageTabGuiAction(character,id));
	}
}
