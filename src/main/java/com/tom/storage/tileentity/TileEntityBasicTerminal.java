package com.tom.storage.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Function;

import com.tom.api.inventory.IStorageInventory;
import com.tom.api.tileentity.IConfigurable;
import com.tom.config.ConfigurationTerminal;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.storage.handler.AutoCraftingHandler;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.ITerminal;
import com.tom.storage.handler.NetworkState;
import com.tom.storage.handler.StorageData;
import com.tom.storage.multipart.block.StorageNetworkCable.CableColor;
import com.tom.storage.tileentity.gui.GuiTerminalBase;

public class TileEntityBasicTerminal extends TileEntityChannel implements ITerminal, IConfigurable {
	public int terminalMode = 0;
	private byte powered;
	private TerminalColor color = new TerminalColor(CableColor.FLUIX);
	private TileEntityBasicTerminal.TerminalState state = TileEntityBasicTerminal.TerminalState.OFF, stateLast;
	private IConfigurationOption cfg = new ConfigurationTerminal();

	@Override
	public void updateEntity(IBlockState currentState) {
		if (!world.isRemote) {
			if (isActive().isPowered()) {
				if (powered > 5) {
					state = isActive().fullyActive() ? TileEntityBasicTerminal.TerminalState.ACTIVE : isActive() == NetworkState.LOADING_CHANNELS ? TileEntityBasicTerminal.TerminalState.LOADING : TileEntityBasicTerminal.TerminalState.POWERED;
				} else {
					powered++;
				}
			} else {
				powered = 0;
				state = isActive().isPowered() ? TileEntityBasicTerminal.TerminalState.POWERED : TileEntityBasicTerminal.TerminalState.OFF;
			}
			if (stateLast != state || world.getTotalWorldTime() % 40 == 0) {
				stateLast = state;
				markBlockForUpdate(pos);
				world.checkLight(pos);
			}
		}
	}

	public static class TerminalColor {
		private final int color;
		private final int colorAlt;

		public int getColor() {
			return color;
		}

		public int getColorAlt() {
			return colorAlt;
		}

		public TerminalColor(int color, int colorAlt) {
			this.color = color;
			this.colorAlt = colorAlt;
		}

		public TerminalColor(CableColor color) {
			this.color = color.getTint();
			this.colorAlt = color.getTintAlt();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + color;
			result = prime * result + colorAlt;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TerminalColor other = (TerminalColor) obj;
			if (color != other.color)
				return false;
			if (colorAlt != other.colorAlt)
				return false;
			return true;
		}

		public String getName() {
			return "TerminalColor: " + color + " " + colorAlt;
		}
	}

	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setInteger("color", color.getColor());
		// buf.setInteger("colorAlt", color.getColorAlt());
		buf.setInteger("s", state.ordinal());
	}

	@Override
	public void readFromPacket(NBTTagCompound buf) {
		color = new TerminalColor(buf.getInteger("color"), /*buf.getInteger("colorAlt")*/0);
		state = TileEntityBasicTerminal.TerminalState.VALUES[buf.getInteger("s")];
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.checkLight(pos);
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (id == 0)
			terminalMode = extra % 3;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("termMode", terminalMode);
		compound.setInteger("color", color.getColor());
		// compound.setInteger("colorAlt", color.getColorAlt());
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		terminalMode = compound.getInteger("termMode");
		color = new TerminalColor(compound.getInteger("color"), /*compound.getInteger("colorAlt")*/0);
	}

	@Override
	public int getTerminalMode() {
		return terminalMode;
	}

	@Override
	public void setClientState(TileEntityBasicTerminal.TerminalState state) {
		this.state = state;
	}

	@Override
	public double getPowerDrained() {
		return 1;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public int getMemoryUsage() {
		return 4;
	}

	public static enum TerminalFacing implements IStringSerializable {
		DOWN_NORTH(EnumFacing.DOWN), UP_NORTH(EnumFacing.UP), NORTH(EnumFacing.NORTH), SOUTH(EnumFacing.SOUTH), WEST(EnumFacing.WEST), EAST(EnumFacing.EAST), DOWN_SOUTH(EnumFacing.DOWN), DOWN_WEST(EnumFacing.DOWN), DOWN_EAST(EnumFacing.DOWN), UP_SOUTH(EnumFacing.UP), UP_WEST(EnumFacing.UP), UP_EAST(EnumFacing.UP),;
		public static final TerminalFacing[] VALUES = values();
		private final EnumFacing face;

		private TerminalFacing(EnumFacing face) {
			this.face = face;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		public static TerminalFacing getFront(int i) {
			return VALUES[MathHelper.abs(i) % VALUES.length];
		}

		public static TerminalFacing fromFacing(EnumFacing directionFacing, EnumFacing directionFacing2) {
			if (directionFacing.getAxis() != Axis.Y) {
				return VALUES[directionFacing.ordinal()];
			} else {
				if (directionFacing == EnumFacing.UP) {
					switch (directionFacing2) {
					case DOWN:
						break;
					case EAST:
						return UP_EAST;
					case NORTH:
						return UP_NORTH;
					case SOUTH:
						return UP_SOUTH;
					case UP:
						break;
					case WEST:
						return UP_WEST;
					default:
						break;
					}
					return UP_NORTH;
				} else {
					switch (directionFacing2) {
					case DOWN:
						break;
					case EAST:
						return DOWN_EAST;
					case NORTH:
						return DOWN_NORTH;
					case SOUTH:
						return DOWN_SOUTH;
					case UP:
						break;
					case WEST:
						return DOWN_WEST;
					default:
						break;
					}
					return DOWN_NORTH;
				}
			}
		}

		public EnumFacing getFace() {
			return face;
		}
	}

	public static enum TerminalState implements IStringSerializable {
		OFF, POWERED, LOADING, ACTIVE;
		public static final TerminalState[] VALUES = values();

		@Override
		public String getName() {
			return name().toLowerCase();
		}

	}

	public TerminalColor getColor() {
		return color;
	}

	@Override
	public TileEntityBasicTerminal.TerminalState getTerminalState() {
		return state;
	}

	@Override
	public boolean getClientPowered() {
		return state == TileEntityBasicTerminal.TerminalState.LOADING || state == TileEntityBasicTerminal.TerminalState.ACTIVE;
	}

	public void setColor(NBTTagCompound stackTag) {
		if (stackTag.hasKey("color", 3)/* && stackTag.hasKey("colorAlt", 3)*/) {
			color = new TerminalColor(stackTag.getInteger("color"), /*stackTag.getInteger("colorAlt")*/0);
		}
	}

	public void writeToStackNBT(NBTTagCompound stackTag) {
		stackTag.setInteger("color", color.getColor());
		// stackTag.setInteger("colorAlt", color.getColorAlt());
	}

	@Override
	public void receiveNBTPacket(EntityPlayer player, NBTTagCompound message) {
		color = new TerminalColor(message.getInteger("color"), /*message.getInteger("colorAlt")*/0);
		markBlockForUpdate(pos);
	}

	@Override
	public void writeToNBTPacket(NBTTagCompound tag) {
		tag.setInteger("color", color.getColor());
	}

	@Override
	public IConfigurationOption getOption() {
		return cfg;
	}

	@Override
	public boolean canConfigure(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void setCardStack(ItemStack stack) {

	}

	@Override
	public ItemStack getCardStack() {
		return ItemStack.EMPTY;
	}

	@Override
	public String getConfigName() {
		return getBlockType().getUnlocalizedName() + ".name";
	}

	@Override
	public BlockPos getSecurityStationPos() {
		return grid.getSData().getSecurityStationPos();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void sendUpdate(int id, int extra, GuiTerminalBase gui) {
		gui.sendButtonUpdateToTile(id, extra);
	}

	@Override
	public IStorageInventory getStorageInventory() {
		return grid.getInventory();
	}

	@Override
	public StorageData getData() {
		return grid.getSData();
	}

	@Override
	public void startCrafting(ICraftable stackToCraft, Function<AutoCraftingHandler.CraftingCalculationResult, Void> apply) {
		grid.startCrafting(stackToCraft, apply);
	}

	@Override
	public ItemStack pushStack(ItemStack itemstack) {
		return grid.pushStack(itemstack);
	}

	@Override
	public ItemStack pullStack(ItemStack p) {
		return grid.pullStack(p);
	}

	@Override
	public void sendUpdate(NBTTagCompound tag) {
		NetworkHandler.sendToServer(new MessageNBT(tag, getPos2()));
	}
}
