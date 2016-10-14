package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.ILookDetector;
import com.tom.api.tileentity.IWirelessPeripheralController;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.entity.EntityCamera;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageCamera;

import com.tom.core.tileentity.gui.GuiCamera;

public class TileEntityCamera extends TileEntityTomsMod implements ILookDetector{
	public int posX = 0;
	public int posY = 0;
	public int posZ = 0;
	public boolean linked = false;
	public double camPosX = 0;
	public double camPosY = 0;
	public double camPosZ = 0;
	public float yaw = 0;
	public float pitch = 0;
	public float yawMin = 0;
	public float pitchMin = 0;
	public float yawMax = 0;
	public float pitchMax = 0;
	public boolean disabled = false;
	private boolean connectedLastTickClient = false;
	private static final float f = 1.0F;
	public List<EntityPlayer> players = new ArrayList<EntityPlayer>();
	private int i = 0;
	public boolean isRelativeCoord = true;
	//private UUID uuid = null;
	//public EntityCamera cam;
	//private EntityPlayerMP cameraPlayer;
	public void link(int x, int y, int z){
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.linked = true;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.posX = tag.getInteger("linkX");
		this.posY = tag.getInteger("linkY");
		this.posZ = tag.getInteger("linkZ");
		this.linked = tag.getBoolean("linked");
		this.camPosX = tag.getDouble("camX");
		this.camPosY = tag.getDouble("camY");
		this.camPosZ = tag.getDouble("camZ");
		this.yaw = tag.getFloat("yaw");
		this.pitch = tag.getFloat("pitch");
		this.yawMin = tag.getFloat("yawMin");
		this.pitchMin = tag.getFloat("pitchMin");
		this.yawMax = tag.getFloat("yawMax");
		this.pitchMax = tag.getFloat("pitchMax");
		this.isRelativeCoord = tag.getBoolean("rc");
		/*String uuidString = tag.getString("uuid");
		if(!uuidString.equals("")) {
			this.uuid = UUID.fromString(uuidString);
		}*/
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("linkX", this.posX);
		tag.setInteger("linkY", this.posY);
		tag.setInteger("linkZ", this.posZ);
		tag.setBoolean("linked", this.linked);
		tag.setDouble("camX", this.camPosX);
		tag.setDouble("camY", this.camPosY);
		tag.setDouble("camZ", this.camPosZ);
		tag.setFloat("yaw",this.yaw);
		tag.setFloat("pitch",this.pitch);
		tag.setFloat("yawMin",this.yawMin);
		tag.setFloat("pitchMin",this.pitchMin);
		tag.setFloat("yawMax",this.yawMax);
		tag.setFloat("pitchMax",this.pitchMax);
		tag.setBoolean("rc", this.isRelativeCoord);
		/*/if(this.uuid != null) {
			tag.setString("uuid", this.uuid.toString());
		}*/
		return tag;
	}
	public void connectPlayer(EntityPlayer player, boolean mode, boolean eC, boolean eEsc){
		if(!this.worldObj.isRemote){
			int xCoord = pos.getX();
			int yCoord = pos.getY();
			int zCoord = pos.getZ();
			//this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			NetworkHandler.sendTo(new MessageCamera(xCoord, yCoord, zCoord, mode,eC,eEsc,this), (EntityPlayerMP) player);
			//if(!mode) cameraPlayer.setPositionAndRotation(xCoord+0.5D, yCoord+0.5D, zCoord+0.5D, 0, this.blockMetadata*90);
		}
	}
	@SideOnly(Side.CLIENT)
	public void connectPlayerClient(boolean mode, boolean eC, boolean eEsc){
		Minecraft mc = Minecraft.getMinecraft();
		//this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		if(mode){
			if(!(mc.currentScreen instanceof GuiCamera)){
				mc.displayGuiScreen(new GuiCamera(this));
			}
			/*if(mc.renderViewEntity instanceof EntityCamera){
				mc.renderViewEntity.setPositionAndRotation(xCoord+0.5D+this.camPosX, yCoord-1D+this.camPosY, zCoord+0.5D+this.camPosZ,
						this.yaw,this.pitch);
				((EntityCamera)mc.renderViewEntity).contX = posX;
				((EntityCamera)mc.renderViewEntity).contY = posY;
				((EntityCamera)mc.renderViewEntity).contZ = posZ;
				((EntityCamera)mc.renderViewEntity).enableControls = eC;
				((EntityCamera)mc.renderViewEntity).yawMin = Math.min(yawMin, yawMax);
				((EntityCamera)mc.renderViewEntity).yawMax = Math.max(yawMin, yawMax);
				((EntityCamera)mc.renderViewEntity).pitchMin = Math.min(pitchMin, pitchMax);
				((EntityCamera)mc.renderViewEntity).pitchMax = Math.max(pitchMin, pitchMax);
				((EntityCamera)mc.renderViewEntity).te = this;
				mc.entityRenderer.updateRenderer();
				if(mc.renderGlobal != null) {
					mc.renderGlobal.setWorldAndLoadRenderers(mc.theWorld);
				}
			}else{*/
			boolean r = mc.getRenderViewEntity() instanceof EntityCamera;
			double camX = this.isRelativeCoord ? pos.getX()+0.5D+this.camPosX : this.camPosX;
			double camY = this.isRelativeCoord ? pos.getY()+1D+this.camPosY : this.camPosY;
			double camZ = this.isRelativeCoord ? pos.getZ()+0.5D+this.camPosZ : this.camPosZ;
			mc.setRenderViewEntity(new EntityCamera(worldObj, camX, camY, camZ,
					this.yaw,this.pitch,eC, this.yawMin, this.pitchMin, this.yawMax, this.pitchMax, posX, posY, posZ,eEsc,this));
			mc.entityRenderer.updateRenderer();
			if(!r && mc.renderGlobal != null) {
				mc.renderGlobal.setWorldAndLoadRenderers(mc.theWorld);
			}
			//}
			/*mc.renderViewEntity = new EntityCamera(worldObj, xCoord+0.5D+this.camPosX, yCoord-1D+this.camPosY, zCoord+0.5D+this.camPosZ,
					this.yaw,this.pitch,true);
			mc.entityRenderer.updateRenderer();
			if(mc.renderGlobal != null) {
				mc.renderGlobal.setWorldAndLoadRenderers(mc.theWorld);
			}*/
			//mc.fontRenderer.drawString("Camera", 0, 0, 0xFFFFFFFF);
		}else{
			mc.displayGuiScreen((GuiScreen)null);
			mc.setRenderViewEntity(mc.thePlayer);
			mc.entityRenderer.updateRenderer();
			mc.entityRenderer.updateCameraAndRender(0.1F, 0);
			if(mc.renderGlobal != null) {
				mc.renderGlobal.setWorldAndLoadRenderers(mc.theWorld);
			}
		}
	}
	public EntityPlayer getPlayer(String pName){
		return this.worldObj.getPlayerEntityByName(pName);
	}
	@Override
	public void updateEntity() {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if(!this.disabled){
			int range = 15;
			if(worldObj.isRemote){
				i = i + 1;
				Minecraft mc = Minecraft.getMinecraft();
				if(mc != null){
					if(mc.getRenderViewEntity() instanceof EntityCamera && mc.getRenderViewEntity().getDistance(xCoord, yCoord, zCoord) <= range){
						EntityCamera cam = (EntityCamera) mc.getRenderViewEntity();
						float f1 = cam.prevRotationPitch + (cam.rotationPitch - cam.prevRotationPitch) * f;
						float f2 = cam.prevRotationYaw + (cam.rotationYaw - cam.prevRotationYaw) * f;
						double d0 = cam.prevPosX + (cam.posX - cam.prevPosX) * f;
						double d1 = cam.prevPosY + (cam.posY - cam.prevPosY) * f + 1.5D;
						double d2 = cam.prevPosZ + (cam.posZ - cam.prevPosZ) * f;
						Vec3d vec3 = new Vec3d(d0, d1, d2);
						float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
						float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
						float f5 = -MathHelper.cos(-f1 * 0.017453292F);
						float f6 = MathHelper.sin(-f1 * 0.017453292F);
						float f7 = f4 * f5;
						float f8 = f3 * f5;
						double d3 = range;
						Vec3d vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
						RayTraceResult mpos = worldObj.rayTraceBlocks(vec3, vec31, true);
						if(mpos != null){
							BlockPos pos = mpos.getBlockPos();
							if(pos != null && pos.getX() == xCoord && pos.getY() == yCoord && pos.getZ() == zCoord) {
								this.connectedLastTickClient = true;
								NetworkHandler.sendToServer(new MessageCamera(xCoord, yCoord, zCoord, true));
							}
						}
					}else if(this.connectedLastTickClient){
						this.connectedLastTickClient = false;
						NetworkHandler.sendToServer(new MessageCamera(xCoord, yCoord, zCoord, false));
					}
				}
				if(i > 10){
					this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
					i = 0;
				}
			}else{
				List<EntityPlayer> playersOld = new ArrayList<EntityPlayer>(this.players);
				this.players.clear();
				List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(xCoord - range, yCoord - range, zCoord - range, xCoord + range, yCoord + range, zCoord + range));
				for(EntityPlayer player : players) {
					float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
					float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
					double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
					double d1 = player.prevPosY + (player.posY - player.prevPosY) * f;
					if (!worldObj.isRemote && player instanceof EntityPlayer)
						d1 += 1.62D;
					double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
					Vec3d vec3 = new Vec3d(d0, d1, d2);
					float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
					float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
					float f5 = -MathHelper.cos(-f1 * 0.017453292F);
					float f6 = MathHelper.sin(-f1 * 0.017453292F);
					float f7 = f4 * f5;
					float f8 = f3 * f5;
					double d3 = range;
					Vec3d vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
					RayTraceResult mpos = worldObj.rayTraceBlocks(vec3, vec31, true);
					if(mpos != null){
						BlockPos pos = mpos.getBlockPos();
						if(pos != null && pos.getX() == xCoord && pos.getY() == yCoord && pos.getZ() == zCoord) {
							this.players.add(player);
							if(playersOld.contains(player)){
								playersOld.remove(player);
							}else{
								//for(IComputerAccess c : this.computers){
								queueEvent("camera_look_"+player.getName(), new Object[]{player.getName(),player.getDistance(xCoord, yCoord, zCoord),player.posX, player.posY,player.posZ});
								//}
							}
						}
					}
				}
				if(!playersOld.isEmpty()){
					for(EntityPlayer p : playersOld){
						//for(IComputerAccess c : this.computers){
						queueEvent("camera_look_stop_"+p.getName(), new Object[]{p.getName()});
						//}
					}
				}
			}
		}
	}
	@Override
	public void writeToPacket(NBTTagCompound tag){
		tag.setDouble("camX", this.camPosX);
		tag.setDouble("camY", this.camPosY);
		tag.setDouble("camZ", this.camPosZ);
		tag.setFloat("yaw",this.yaw);
		tag.setFloat("pitch",this.pitch);
		tag.setFloat("yawMin",this.yawMin);
		tag.setFloat("pitchMin",this.pitchMin);
		tag.setFloat("yawMax",this.yawMax);
		tag.setFloat("pitchMax",this.pitchMax);
		tag.setBoolean("rc", this.isRelativeCoord);
	}

	@Override
	public void readFromPacket(NBTTagCompound tag){
		this.camPosX = tag.getDouble("camX");
		this.camPosY = tag.getDouble("camY");
		this.camPosZ = tag.getDouble("camZ");
		this.yaw = tag.getFloat("yaw");
		this.pitch = tag.getFloat("pitch");
		this.yawMin = tag.getFloat("yawMin");
		this.pitchMin = tag.getFloat("pitchMin");
		this.yawMax = tag.getFloat("yawMax");
		this.pitchMax = tag.getFloat("pitchMax");
		this.isRelativeCoord = tag.getBoolean("rc");
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
	@Override
	public void setConnect(boolean mode, EntityPlayer player) {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if(mode && !this.players.contains(player)){
			this.players.add(player);
			//for(IComputerAccess c : this.computers){
			this.queueEvent("camera_look_out_"+player.getName(), new Object[]{player.getName(),player.getDistance(xCoord, yCoord, zCoord),player.posX, player.posY,player.posZ});
			//}
		}else if(!mode && this.players.contains(player)){
			this.players.remove(player);
			//for(IComputerAccess c : this.computers){
			this.queueEvent("camera_look_out_stop_"+player.getName(), new Object[]{player.getName()});
			//}
		}
	}
	public void queueEvent(String e, Object[] o){
		TileEntity tilee = this.worldObj.getTileEntity(new BlockPos(posX, posY, posZ));
		if(tilee instanceof IWirelessPeripheralController){
			IWirelessPeripheralController te = (IWirelessPeripheralController) tilee;
			te.queueEvent(e, o);
		}
	}
	public void setValues(float yaw, float pitch, float yawMin,float pitchMin, float yawMax, float pitchMax, double cX, double cY, double cZ, boolean isRelative){
		this.camPosX = cX;
		this.camPosY = cY;
		this.camPosZ = cZ;
		this.yawMin = Math.min(yawMin, yawMax);
		this.yawMax = Math.max(yawMin, yawMax);
		this.pitchMin = Math.min(pitchMin, pitchMax);
		this.pitchMax = Math.max(pitchMin, pitchMax);
		this.yaw = yaw;
		this.pitch = pitch;
		this.isRelativeCoord = isRelative;
	}
}
