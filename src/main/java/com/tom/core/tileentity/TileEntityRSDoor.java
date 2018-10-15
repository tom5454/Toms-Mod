package com.tom.core.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import com.tom.api.tileentity.TileEntityCamoable;
import com.tom.core.CoreInit;
import com.tom.util.TomsModUtils;

public class TileEntityRSDoor extends TileEntityCamoable {
	public EnumFacing dir = EnumFacing.NORTH;
	public ItemStack camoStack = null;
	public boolean isBottom = false;
	public boolean mode = true;
	private EnumFacing dirC = EnumFacing.NORTH;

	public void place(EntityPlayer entityPlayer, boolean bottom, EnumFacing plD) {
		if (bottom) {
			int xCoord = pos.getX();
			int yCoord = pos.getY();
			int zCoord = pos.getZ();
			TileEntity teDoor2R = world.getTileEntity(new BlockPos(xCoord, yCoord + 1, zCoord));
			if (teDoor2R instanceof TileEntityRSDoor) {
				TileEntityRSDoor teDoor2 = (TileEntityRSDoor) teDoor2R;
				teDoor2.place(entityPlayer, false, plD);
			}
		}
		this.isBottom = bottom;
		dir = dirC = plD;
		markBlockForUpdate(this.pos);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.dir = EnumFacing.values()[tag.getInteger("d")];
		this.dirC = EnumFacing.values()[tag.getInteger("dC")];
		this.isBottom = tag.getBoolean("bottom");
		this.mode = tag.getBoolean("mode");
		boolean camo = tag.getBoolean("camo");
		this.camoStack = camo ? TomsModUtils.loadItemStackFromNBT(tag.getCompoundTag("camoStack")) : null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("d", this.dir.ordinal());
		tag.setInteger("dC", this.dirC.ordinal());
		tag.setBoolean("bottom", this.isBottom);
		tag.setBoolean("mode", this.mode);
		if (this.camoStack != null) {
			tag.setBoolean("camo", true);
			tag.setTag("camoStack", this.camoStack.writeToNBT(new NBTTagCompound()));
		} else {
			tag.setBoolean("camo", false);
		}
		return tag;
	}

	public void activate(EntityPlayer player, boolean first, ItemStack held, EnumHand hand) {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if (!this.world.isRemote) {
			if (held != null && CoreInit.isWrench(player, hand)) {
				if (player.isSneaking()) {
					if (camoStack != null) {
						ItemStack camoStack = this.camoStack;
						this.camoStack = null;
						EntityItem itemEntity = new EntityItem(world, xCoord, yCoord, zCoord, camoStack);
						if (!player.capabilities.isCreativeMode)
							world.spawnEntity(itemEntity);
						if (isBottom) {
							TileEntity teDoor2R = world.getTileEntity(new BlockPos(xCoord, yCoord + 1, zCoord));
							if (teDoor2R instanceof TileEntityRSDoor) {
								TileEntityRSDoor teDoor2 = (TileEntityRSDoor) teDoor2R;
								teDoor2.camoStack = null;
								markBlockForUpdate(teDoor2.pos);
							}
						} else {
							TileEntity teDoor2R = world.getTileEntity(new BlockPos(xCoord, yCoord - 1, zCoord));
							if (teDoor2R instanceof TileEntityRSDoor) {
								TileEntityRSDoor teDoor2 = (TileEntityRSDoor) teDoor2R;
								teDoor2.camoStack = null;
								markBlockForUpdate(teDoor2.pos);
							}
						}
					}
				} else {
					this.mode = !this.mode;
					if (first) {
						TomsModUtils.sendNoSpamTranslate(player, "tomsMod.chat.rsDoorMode" + (mode ? "0" : "1"));
						if (isBottom) {
							TileEntity teDoor2R = world.getTileEntity(new BlockPos(xCoord, yCoord + 1, zCoord));
							if (teDoor2R instanceof TileEntityRSDoor) {
								TileEntityRSDoor teDoor2 = (TileEntityRSDoor) teDoor2R;
								teDoor2.activate(player, false, held, hand);
							}
						} else {
							TileEntity teDoor2R = world.getTileEntity(new BlockPos(xCoord, yCoord - 1, zCoord));
							if (teDoor2R instanceof TileEntityRSDoor) {
								TileEntityRSDoor teDoor2 = (TileEntityRSDoor) teDoor2R;
								teDoor2.activate(player, false, held, hand);
							}
						}
					}
				}
			} else {
				boolean elseB = false;
				if (camoStack == null) {
					ItemStack playerItem = held;
					if (playerItem != null && playerItem.getItem() instanceof ItemBlock) {
						ItemStack camoStack = null;
						if (player.capabilities.isCreativeMode) {
							camoStack = playerItem.copy();
							camoStack.setCount(1);
						} else {
							camoStack = playerItem.splitStack(1);
						}
						this.camoStack = camoStack;
						if (isBottom) {
							TileEntity teDoor2R = world.getTileEntity(new BlockPos(xCoord, yCoord + 1, zCoord));
							if (teDoor2R instanceof TileEntityRSDoor) {
								TileEntityRSDoor teDoor2 = (TileEntityRSDoor) teDoor2R;
								teDoor2.camoStack = camoStack.copy();
								markBlockForUpdate(teDoor2.pos);
							}
						} else {
							TileEntity teDoor2R = world.getTileEntity(new BlockPos(xCoord, yCoord - 1, zCoord));
							if (teDoor2R instanceof TileEntityRSDoor) {
								TileEntityRSDoor teDoor2 = (TileEntityRSDoor) teDoor2R;
								teDoor2.camoStack = camoStack.copy();
								markBlockForUpdate(teDoor2.pos);
							}
						}
					} else
						elseB = true;
				} else
					elseB = true;
				if (elseB) {
					boolean rs = (world.isBlockIndirectlyGettingPowered(pos) > 0) || (this.isBottom ? world.isBlockIndirectlyGettingPowered(new BlockPos(xCoord, yCoord + 1, zCoord)) > 0 : world.isBlockIndirectlyGettingPowered(new BlockPos(xCoord, yCoord - 1, zCoord)) > 0);
					boolean mode = this.mode ? rs : !rs;
					if (mode) {
						if (dir == dirC) {
							this.playDoorSound(true);
							dir = dir.rotateAround(Axis.Y);
						} else {
							this.playDoorSound(false);
							dir = dirC;
						}
					}
					if (first) {
						if (isBottom) {
							TileEntity teDoor2R = world.getTileEntity(new BlockPos(xCoord, yCoord + 1, zCoord));
							if (teDoor2R instanceof TileEntityRSDoor) {
								TileEntityRSDoor teDoor2 = (TileEntityRSDoor) teDoor2R;
								teDoor2.activate(player, false, held, hand);
							}
						} else {
							TileEntity teDoor2R = world.getTileEntity(new BlockPos(xCoord, yCoord - 1, zCoord));
							if (teDoor2R instanceof TileEntityRSDoor) {
								TileEntityRSDoor teDoor2 = (TileEntityRSDoor) teDoor2R;
								teDoor2.activate(player, false, held, hand);
							}
						}
					}
				}
			}
			markBlockForUpdate(pos);
		}
	}

	@Override
	public void updateEntity() {
		if (!this.world.isRemote) {
			int xCoord = pos.getX();
			int yCoord = pos.getY();
			int zCoord = pos.getZ();
			boolean rs = world.isBlockIndirectlyGettingPowered(pos) > 0 || (this.isBottom ? world.isBlockIndirectlyGettingPowered(new BlockPos(xCoord, yCoord + 1, zCoord)) > 0 : world.isBlockIndirectlyGettingPowered(new BlockPos(xCoord, yCoord - 1, zCoord)) > 0);
			boolean mode = this.mode ? rs : !rs;
			if (!mode) {
				if (this.dir != this.dirC) {
					this.dir = this.dirC;
					this.playDoorSound(false);
					markBlockForUpdate(pos);
				}
			}
		}
	}

	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setInteger("d", this.dir.ordinal());
		// ByteBufUtils.writeItemStack(buf, camoStack);
		NBTTagCompound tag = new NBTTagCompound();
		if (camoStack != null)
			camoStack.writeToNBT(tag);
		buf.setTag("c", tag);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf) {
		this.dir = EnumFacing.values()[buf.getInteger("d")];
		this.camoStack = TomsModUtils.loadItemStackFromNBT(buf.getCompoundTag("c"));
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.world.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}

	private void playDoorSound(boolean open) {// BlockDoor
		/*int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();*/
		// worldObj.playSound(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F,
		// "random.door_close",0.2F,1);
		world.playEvent((EntityPlayer) null, open ? 1005 : 1011, pos, 0);
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
	public IBlockState getDefaultState() {
		return Blocks.PLANKS.getDefaultState();
	}
}
