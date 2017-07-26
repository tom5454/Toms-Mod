package com.tom.storage.multipart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import com.google.common.base.Function;

import com.tom.api.inventory.IStorageInventory;
import com.tom.api.multipart.IGuiMultipart;
import com.tom.api.tileentity.IConfigurable;
import com.tom.apis.TomsModUtils;
import com.tom.config.ConfigurationTerminal;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.storage.block.BlockTerminalBase;
import com.tom.storage.handler.AutoCraftingHandler;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.ITerminal;
import com.tom.storage.handler.NetworkState;
import com.tom.storage.handler.StorageData;
import com.tom.storage.item.ItemPartTerminal;
import com.tom.storage.multipart.block.StorageNetworkCable.CableColor;
import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.TileEntityBasicTerminal.TerminalColor;
import com.tom.storage.tileentity.gui.GuiTerminalBase;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.EnumFaceSlot;
import mcmultipart.api.slot.IPartSlot;

public abstract class PartTerminal extends PartChannelModule implements ITerminal, IConfigurable, IGuiMultipart {
	public int terminalMode = 0;
	private byte powered;
	private TerminalColor color = new TerminalColor(CableColor.FLUIX);
	private TileEntityBasicTerminal.TerminalState state = TileEntityBasicTerminal.TerminalState.OFF, stateLast;
	private IConfigurationOption cfg = new ConfigurationTerminal();

	@Override
	public void writeToPacketI(NBTTagCompound buf) {
		buf.setInteger("color", color.getColor());
		// buf.setInteger("colorAlt", color.getColorAlt());
		buf.setInteger("ts", state.ordinal());
	}

	@Override
	public boolean readFromPacketI(NBTTagCompound buf) {
		color = new TerminalColor(buf.getInteger("color"), /*buf.getInteger("colorAlt")*/0);
		state = TileEntityBasicTerminal.TerminalState.VALUES[buf.getInteger("ts")];
		world.checkLight(pos);
		return true;
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
	public void updateEntityI() {
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
				sendUpdatePacket();
				world.checkLight(pos);
			}
		}
	}

	@Override
	protected EnumFacing getFacing0() {
		IMultipartContainer c = MultipartHelper.getContainer(world, pos).orElse(null);
		if (c == null) { return world.getBlockState(pos).getValue(BlockTerminalBase.FACING).getFace(); }
		IPartInfo i = TomsModUtils.getPartInfo(c, this);
		return i == null ? EnumFacing.DOWN : i.getState().getValue(BlockTerminalBase.FACING).getFace();
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
		if (stackTag != null && stackTag.hasKey("color", 3)/* && stackTag.hasKey("colorAlt", 3)*/) {
			color = new TerminalColor(stackTag.getInteger("color"), /*stackTag.getInteger("colorAlt")*/0);
		}
	}

	public void writeToStackNBT(NBTTagCompound stackTag) {
		stackTag.setInteger("color", color.getColor());
		// stackTag.setInteger("colorAlt", color.getColorAlt());
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
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
	public BlockPos getSecurityStationPos() {
		return grid.getData().getSecurityStationPos();
	}

	@Override
	public IPartSlot getPosition() {
		return EnumFaceSlot.fromFace(getFacing());
	}

	@Override
	protected boolean isConfigurableSide(EnumFacing f) {
		return f == getFacing0();
	}

	public ItemStack createStack(ItemPartTerminal itemPartTerminal) {
		ItemStack ret = new ItemStack(itemPartTerminal);
		NBTTagCompound tag = new NBTTagCompound();
		writeToStackNBT(tag);
		ret.setTagCompound(tag);
		return ret;
	}

	@Override
	public abstract ItemStack getStack();

	@Override
	public String getConfigName() {
		return getStack().getUnlocalizedName();
	}

	@Override
	public void sendUpdate(int id, int extra, GuiTerminalBase gui) {
		gui.sendButtonUpdateT(id, this, extra, getPos2());
	}

	@Override
	public IStorageInventory getStorageInventory() {
		return grid.getInventory();
	}

	@Override
	public StorageData getData() {
		return grid.getData();
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
