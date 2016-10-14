package com.tom.core.tileentity.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import com.tom.core.CoreInit;
import com.tom.core.entity.EntityCamera;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageTabGuiAction;

import com.tom.core.tileentity.TileEntityCamera;

public class GuiCamera extends GuiScreen {
	private TileEntityCamera te;
	public GuiCamera(TileEntityCamera te){
		this.te = te;
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	@Override
	public void keyTyped(char character, int id) throws IOException{
		super.keyTyped(character, id);
		if(mc.getRenderViewEntity() instanceof EntityCamera){
			EntityCamera cam = (EntityCamera) mc.getRenderViewEntity();
			boolean c = cam.enableControls;
			if(id == 1){
				/*mc.renderViewEntity.setAngles(0F,0F);
				mc.renderViewEntity.cameraPitch = 0F;*/
				//mc.renderViewEntity.setAngles(cam.yawN, cam.pitchN);
				if(cam.eEsc || (isShiftKeyDown())){
					te.connectPlayerClient(false, false, false);
				}else{
					if(!(mc.currentScreen instanceof GuiCamera)) mc.displayGuiScreen(new GuiCamera(te));
				}
			}else if(id == 200 && c){
				//System.out.println("UP"+(mc.renderViewEntity.rotationPitch + 10F <= cam.pitchMax)+" "+mc.renderViewEntity.rotationPitch + " " + cam.pitchMax);
				//mc.renderViewEntity.rotationPitch -= 10F;
				if(mc.getRenderViewEntity().rotationPitch + (isShiftKeyDown() ? (isCtrlKeyDown() ? 20F : 5F) : (isCtrlKeyDown() ? 40F : 10F)) <= cam.pitchMax || cam.pitchMin == cam.pitchMax)
					mc.getRenderViewEntity().setAngles(0F,(isShiftKeyDown() ? (isCtrlKeyDown() ? 20F : 5F) : (isCtrlKeyDown() ? 40F : 10F)));
			}else if(id == 208 && c){
				if(mc.getRenderViewEntity().rotationPitch - (isShiftKeyDown() ? (isCtrlKeyDown() ? 20F : 5F) : (isCtrlKeyDown() ? 40F : 10F)) >= cam.pitchMin || cam.pitchMin == cam.pitchMax)
					mc.getRenderViewEntity().setAngles(0F,-(isShiftKeyDown() ? (isCtrlKeyDown() ? 20F : 5F) : (isCtrlKeyDown() ? 40F : 10F)));
			}else if(id == 203 && c){
				//System.out.println("LEFT");
				//mc.renderViewEntity.rotationYaw -= 10F;
				if(mc.getRenderViewEntity().rotationYaw - (isShiftKeyDown() ? (isCtrlKeyDown() ? 20F : 5F) : (isCtrlKeyDown() ? 40F : 10F)) >= cam.yawMin || cam.yawMin == cam.yawMax)
					mc.getRenderViewEntity().setAngles(-(isShiftKeyDown() ? (isCtrlKeyDown() ? 20F : 5F) : (isCtrlKeyDown() ? 40F : 10F)),0F);
			}else if(id == 205 && c){
				//System.out.println("RIGHT");
				//mc.renderViewEntity.rotationYaw += 10F;
				if(mc.getRenderViewEntity().rotationYaw + (isShiftKeyDown() ? (isCtrlKeyDown() ? 20F : 5F) : (isCtrlKeyDown() ? 40F : 10F)) <= cam.yawMax || cam.yawMin == cam.yawMax)
					mc.getRenderViewEntity().setAngles((isShiftKeyDown() ? (isCtrlKeyDown() ? 20F : 5F) : (isCtrlKeyDown() ? 40F : 10F)),0F);
			}else if(character == '1'){
				mc.thePlayer.inventory.currentItem = 0;
			}else if(character == '2'){
				mc.thePlayer.inventory.currentItem = 1;
			}else if(character == '3'){
				mc.thePlayer.inventory.currentItem = 2;
			}else if(character == '4'){
				mc.thePlayer.inventory.currentItem = 3;
			}else if(character == '5'){
				mc.thePlayer.inventory.currentItem = 4;
			}else if(character == '6'){
				mc.thePlayer.inventory.currentItem = 5;
			}else if(character == '7'){
				mc.thePlayer.inventory.currentItem = 6;
			}else if(character == '8'){
				mc.thePlayer.inventory.currentItem = 7;
			}else if(character == '9'){
				mc.thePlayer.inventory.currentItem = 8;
			}else{
				NetworkHandler.sendToServer(new MessageTabGuiAction(id,character,cam.contX,cam.contY,cam.contZ));
			}
		}
	}
	@Override
	protected void mouseClickMove(int x, int y, int b,long tick){
		//System.out.println(x+" "+y+" "+b+" "+tick);
	}
	@Override
	protected void mouseClicked(int x, int y, int b){
		//System.out.println(x+" "+y+" "+b);
		if(b == 1){
			ItemStack is = mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem);
			if(is != null){
				Item i = is.getItem();
				if(i != null && i == CoreInit.Tablet){
					NetworkHandler.sendToServer(new MessageTabGuiAction(mc.thePlayer.inventory.currentItem, b == 1));
					i.onItemRightClick(is, mc.theWorld, mc.thePlayer, b == 1 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				}
			}
		}
	}
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
}