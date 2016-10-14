package com.tom.core.tileentity.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import com.tom.api.terminal.TerminalObject;
import com.tom.api.terminal.TerminalObjectTypes;
import com.tom.core.entity.EntityCamera;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageTabGuiAction;

import com.tom.core.tileentity.inventory.ContainerTablet;

public class GuiTablet extends GuiTomsMod {
	private ItemStack tabStack;
	private int i = 0;
	private boolean cur = false;
	private boolean writeMode = false;
	public GuiTextField textField;
	public String textFieldText = "";
	public GuiTablet(ItemStack is,World world,EntityPlayer player) {
		super(new ContainerTablet(is, world, player), "GuiTab3");
		this.tabStack = is;
		/*if(is.getTagCompound() != null && is.getTagCompound().hasKey("x") && is.getTagCompound().hasKey("y") && is.getTagCompound().hasKey("z") && is.getTagCompound().hasKey("id")){
			TileEntity tile = world.getTileEntity(is.getTagCompound().getInteger("x"), is.getTagCompound().getInteger("y"), is.getTagCompound().getInteger("z"));
			if(tile instanceof TileEntityTabletController){
				TileEntityTabletController te = (TileEntityTabletController) tile;
				int id = is.getTagCompound().getInteger("id");
				this.tab = te.getTablet(id);
			}else{
				this.tab = null;
			}
		}else{
			this.tab = null;
		}*/
		this.xSize = MathHelper.floor_double(this.xSize * 1.46D);
	}

	/*@Override
	public VisiblityData modifyVisiblity(GuiContainer arg0, VisiblityData d) {
		d.showNEI = false;
		return d;
	}*/
	@Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String title = "TERMINAL";
		ContainerTablet tabCont = (ContainerTablet) this.inventorySlots;
		this.tabStack = tabCont.tabStack;
		NBTTagCompound tag = tabStack != null ? (tabStack.getTagCompound() != null ? (tabStack.getTagCompound().hasKey("terminal") ? tabStack.getTagCompound().getCompoundTag("terminal") : null) : null) : null;
		if(tag != null){
			title = tag.hasKey("n") ? tag.getString("n") : title;
			if(tag.hasKey("l")){
				NBTTagList list = (NBTTagList) tag.getTag("l");
				int cLast = 0;
				for(int i = 0;i<list.tagCount();i++){
					NBTTagCompound cTag = list.getCompoundTagAt(i);
					TerminalObject cO = new TerminalObject(cTag);
					boolean string = cO.type == TerminalObjectTypes.String;
					//double pxX = this.xSize / 499D;
					//double pxY = this.ySize / 282D;
					//int x = MathHelper.floor_double(cO.xPos * pxX);
					//int y = MathHelper.floor_double(cO.yPos * pxY);
					int x = cO.xPos+5;
					int y = cO.yPos+13;
					if(string){
						int color = cO.c;
						String text = cO.t;
						fontRendererObj.drawString(text,x,y,color);
						cLast = color;
					}else{
						//String rL = cO.t;
						//(rL, x, y);
					}
				}
				boolean cursor = tag.hasKey("cursor") ? tag.getBoolean("cursor") : false;
				this.i = this.i + 1;
				if(this.i > 3){
					this.i = 0;
					if(cursor){
						cur = !cur;
					}
				}
				if(cursor && this.cur){
					int x = tag.hasKey("curWX") ? tag.getInteger("curWX") : 1;
					int y = tag.hasKey("curWY") ? tag.getInteger("curWY") : 1;
					fontRendererObj.drawString("_",x+5,y+13,cLast);
				}
				boolean writeMode = tag.hasKey("wm") ? tag.getBoolean("wm") : false;
				if(writeMode != this.writeMode){
					this.textField.setCursorPositionZero();
				}
				this.writeMode = writeMode;
				//this.textFieldText = tag.hasKey("in") ? tag.getString("in") : "";
				//if(this.writeMode){
					//int x = tag.hasKey("curWX") ? tag.getInteger("curWX") : 1;
					//int y = tag.hasKey("curWY") ? tag.getInteger("curWY") : 1;
					//this.textField.xPosition = guiLeft+5;
					//this.textField.yPosition = guiTop+13;
				//}
				//this.textField.setTextColor(cLast);
				//this.textField.setVisible(this.writeMode);
				this.textField.setFocused(this.writeMode);
			}
		}
		fontRendererObj.drawString(title, xSize / 2 - fontRendererObj.getStringWidth(title) / 2, 2, 4210752);
	}
	@Override
	protected void keyTyped(char key, int num1)throws IOException{
		super.keyTyped(key, num1);
		System.out.println("keyTyped key: " + key + " num1: " + num1);
		if(num1 == 1){
			if(mc.getRenderViewEntity() instanceof EntityCamera){
				mc.displayGuiScreen(new GuiCamera(((EntityCamera)mc.getRenderViewEntity()).te));
			}
		}else if(this.writeMode && num1 != 15){
			//NetworkHandler.sendToServer(new MessageTabGuiAction(textField.xPosition-guiLeft-5, textField.yPosition-guiTop-13, textField.getText()));
			if(num1 == 28){
				NetworkHandler.sendToServer(new MessageTabGuiAction(textField.xPosition-guiLeft-5, textField.yPosition-guiTop-13, textField.getText()));
				this.textField.setVisible(false);
				this.textField.setCursorPositionZero();
			}
		}else{
			NetworkHandler.sendToServer(new MessageTabGuiAction(key,num1));
		}
	}
	@Override
    public void onTextfieldUpdate(int id){
        if(id == 0) textField.setText(this.textFieldText);
    }
	@Override
	protected void mouseClicked(int x, int y, int button)throws IOException{
		super.mouseClicked(x, y, button);
		System.out.println("mouseClicked "+x+" "+y+" "+button);
		if(writeMode) textField.mouseClicked(x, y, button);
		else NetworkHandler.sendToServer(new MessageTabGuiAction(x-guiLeft-5,y-guiTop-13,button));
	}
	@Override
	protected void mouseClickMove(int num0, int num1, int num2,long num3){
		super.mouseClickMove(num0, num1, num2, num3);
		System.out.println("mouseClickMove "+num0+" "+num1+" "+num2+" "+num3);
	}
	@Override
    public void initGui(){
        super.initGui();
        textField = new GuiTextField(0, fontRendererObj, guiLeft+150, guiTop+20, 150, 12);
        //textField.setVisible(false);
        textField.setMaxStringLength(100);
        //textField.setCanLoseFocus(true);
	}
	public int guiTop(){
		return guiTop;
	}
	public int guiLeft(){
		return guiLeft;
	}
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTick){
        super.drawScreen(mouseX, mouseY, partialTick);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        textField.drawTextBox();
    }
}
