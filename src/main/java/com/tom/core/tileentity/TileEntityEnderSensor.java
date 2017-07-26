package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.tileentity.ILookDetector;
import com.tom.api.tileentity.TileEntityCamoable;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.entity.EntityCamera;
import com.tom.lib.Configs;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageCamera;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityEnderSensor extends TileEntityCamoable implements IPeripheral, ILookDetector {
	public String[] methods = {"listMethods", "getPlayerDist", "setActive", "getActive"};
	private List<IComputerAccess> computers = new ArrayList<>();
	public List<EntityPlayer> players = new ArrayList<>();
	public ItemStack camoStack = null;
	public boolean active = true;
	public boolean transparent = false;
	private boolean connectedLastTickClient = false;
	private static final float f = 1.0F;

	@Override
	public String getType() {
		return "Ender_Player_Sensor";
	}

	@Override
	public String[] getMethodNames() {
		return this.methods;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] a) throws LuaException, InterruptedException {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if (method == 0) {
			Object[] o = new Object[methods.length];
			for (int i = 0;i < o.length;i++) {
				o[i] = methods[i];
			}
			return o;
		} else if (method == 1) {
			if (a.length > 0 && a[0] instanceof String) {
				String p = (String) a[0];
				for (EntityPlayer player : this.players) {
					if (player.getName().equals(p)) { return new Object[]{true, player.getDistance(xCoord, yCoord, zCoord)}; }
				}
				return new Object[]{false, -1};
			} else {
				throw new LuaException("Invalid Argument #1 string excepted");
			}
		} else if (method == 2) {
			if (a.length > 0 && a[0] instanceof Boolean) {
				this.active = (Boolean) a[0];
			} else {
				throw new LuaException("Invalid Argument #1 boolean excepted");
			}
		} else if (method == 3) { return new Object[]{this.active}; }
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

	@Override
	public void updateEntity() {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if (this.active) {
			int range = 15;
			if (world.isRemote) {
				Minecraft mc = Minecraft.getMinecraft();
				if (mc != null) {
					if (mc.getRenderViewEntity() instanceof EntityCamera && mc.getRenderViewEntity().getDistance(xCoord, yCoord, zCoord) <= range) {
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
						RayTraceResult mpos = world.rayTraceBlocks(vec3, vec31, true);
						if (mpos != null) {
							BlockPos pos = mpos.getBlockPos();
							if (pos != null && pos.getX() == xCoord && pos.getY() == yCoord && pos.getZ() == zCoord) {
								this.connectedLastTickClient = true;
								NetworkHandler.sendToServer(new MessageCamera(xCoord, yCoord, zCoord, true));
							}
						}
					} else if (this.connectedLastTickClient) {
						this.connectedLastTickClient = false;
						NetworkHandler.sendToServer(new MessageCamera(xCoord, yCoord, zCoord, false));
					}
				} // [
			} else {
				List<EntityPlayer> playersOld = new ArrayList<>(this.players);
				this.players.clear();
				List<?> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(xCoord - range, yCoord - range, zCoord - range, xCoord + range, yCoord + range, zCoord + range));
				for (Object o : players) {
					// float f = 1.0F;
					EntityPlayer player = (EntityPlayer) o;
					float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
					float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
					double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
					double d1 = player.prevPosY + (player.posY - player.prevPosY) * f;
					if (!world.isRemote && player instanceof EntityPlayer)
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
					RayTraceResult mpos = world.rayTraceBlocks(vec3, vec31, true);
					if (mpos != null) {
						BlockPos pos = mpos.getBlockPos();
						if (pos != null && pos.getX() == xCoord && pos.getY() == yCoord && pos.getZ() == zCoord) {
							this.players.add(player);
							if (playersOld.contains(player)) {
								playersOld.remove(player);
							} else {
								for (IComputerAccess c : this.computers) {
									c.queueEvent("ender_sensor_look_" + player.getName(), new Object[]{player.getName(), player.getDistance(xCoord, yCoord, zCoord), player.posX, player.posY, player.posZ});
								}
							}
						}
					}
				}
				if (!playersOld.isEmpty()) {
					for (EntityPlayer p : playersOld) {
						for (IComputerAccess c : this.computers) {
							c.queueEvent("ender_sensor_look_stop_" + p.getName(), new Object[]{p.getName()});
						}
					}
				}
			}
		}
	}

	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setBoolean("t", transparent);
		// ByteBufUtils.writeItemStack(buf, camoStack);
		NBTTagCompound t = new NBTTagCompound();
		if (camoStack != null)
			camoStack.writeToNBT(t);
		buf.setTag("c", t);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf) {
		this.transparent = buf.getBoolean("t");
		this.camoStack = TomsModUtils.loadItemStackFromNBT(buf.getCompoundTag("c"));
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.world.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		boolean camo = tag.getBoolean("camo");
		this.camoStack = camo ? TomsModUtils.loadItemStackFromNBT(tag.getCompoundTag("camoStack")) : null;
		this.transparent = tag.getBoolean("t");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (this.camoStack != null) {
			tag.setBoolean("camo", true);
			tag.setTag("camoStack", this.camoStack.writeToNBT(new NBTTagCompound()));
		} else {
			tag.setBoolean("camo", false);
		}
		tag.setBoolean("t", this.transparent);
		return tag;
	}

	@Override
	public void setConnect(boolean mode, EntityPlayer player) {
		if (mode && !this.players.contains(player)) {
			this.players.add(player);
			int xCoord = pos.getX();
			int yCoord = pos.getY();
			int zCoord = pos.getZ();
			for (IComputerAccess c : this.computers) {
				c.queueEvent("ender_sensor_look_out_" + player.getName(), new Object[]{player.getName(), player.getDistance(xCoord, yCoord, zCoord), player.posX, player.posY, player.posZ});
			}
		} else if (!mode && this.players.contains(player)) {
			this.players.remove(player);
			for (IComputerAccess c : this.computers) {
				c.queueEvent("ender_sensor_look_out_stop_" + player.getName(), new Object[]{player.getName()});
			}
		}
	}

	@Override
	public ItemStack getCamoStack() {
		return camoStack;
	}

	@SuppressWarnings("deprecation")
	@Override
	public AxisAlignedBB getBounds() {
		return blockType.getBoundingBox(world.getBlockState(pos), world, pos);
	}

	@Override
	public boolean doRender() {
		boolean isTransparent = camoStack != null && camoStack.getItem() == Item.getItemFromBlock(Blocks.GLASS) && transparent;
		return !isTransparent;
	}

	@Override
	public IBlockState getDefaultState() {
		return CoreInit.EnderPlayerSensor.getDefaultState();
	}
}
