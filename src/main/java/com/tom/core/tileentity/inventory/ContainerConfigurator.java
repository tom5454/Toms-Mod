package com.tom.core.tileentity.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IGuiTile;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.handler.ConfiguratorHandler;
import com.tom.handler.ConfiguratorHandler.ConfigurableDevice;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.network.messages.MessageNBT;

public class ContainerConfigurator extends ContainerTomsMod {
	public static class MissingConfigurableTileExteption extends RuntimeException {
		private static final long serialVersionUID = -996460640879172057L;

		public MissingConfigurableTileExteption(World world, BlockPos pos, EnumFacing side) {
			super("Missing Configurable Tile at " + pos.toString() + " dim: " + world.provider.getDimension() + " on side: " + (side == null ? "center" : side.getName()));
		}
	}

	public IConfigurable te;
	public List<SlotData> slotData = new ArrayList<>();
	public int side;

	/*public ContainerConfigurator(InventoryPlayer playerInv, IConfigurable configurable) {
		this.te = configurable;
		List<Slot> slotList = new ArrayList<>();
		te.getOption().addSlotsToList(te, slotList, 60, 62);//100-te.getOption().getWidth(), 70-te.getOption().getHeight();
		slotList.stream().map(SlotData::new).forEach(v -> {
			slotData.add(v);
			super.addSlotToContainer(v.s);
		});
		this.addPlayerSlots(playerInv, 8, 94);
	}*/
	public ContainerConfigurator(EntityPlayer player, World world, BlockPos pos, int s) {
		List<ConfigurableDevice> l = ConfiguratorHandler.getDevices(player, world, pos, player.getHeldItem(EnumHand.MAIN_HAND));
		EnumFacing side = s == -1 ? null : EnumFacing.VALUES[s];
		if (l == null)
			this.te = (IConfigurable) world.getTileEntity(pos);
		else
			this.te = l.stream().filter(p -> p.getSide() == side).findFirst().map(ConfigurableDevice::getTile).orElse(null);
		List<Slot> slotList = new ArrayList<>();
		if (te == null)
			return;
		te.getOption().addSlotsToList(te, slotList, 60, 62);// 100-te.getOption().getWidth(),
															// 70-te.getOption().getHeight();
		slotList.stream().map(SlotData::new).forEach(v -> {
			slotData.add(v);
			super.addSlotToContainer(v.s);
		});
		this.addPlayerSlots(player.inventory, 8, 94);
		this.side = s;
	}

	@Override
	protected Slot addSlotToContainer(Slot slotIn) {
		slotData.add(new SlotData(slotIn));
		return super.addSlotToContainer(slotIn);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te != null;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (te != null) {
			IInventory inv = te.getOption().getInventory();
			if (inv != null)
				inv.closeInventory(playerIn);
		}
	}

	public static class SlotData {
		public Slot s;
		public int x, y;

		public SlotData(Slot s) {
			this.s = s;
			x = s.xPos;
			y = s.yPos;
		}

		public void setOffset(int x, int y) {
			s.xPos = this.x + x;
			s.yPos = this.y + y;
		}
	}

	public static class ContainerConfiguratorChoose extends ContainerTomsMod implements IGuiTile {
		private List<ConfigurableDevice> d;
		private boolean sent;
		private BlockPos pos;

		public ContainerConfiguratorChoose(World world, BlockPos pos, EntityPlayer player) {
			d = ConfiguratorHandler.getDevices(player, world, pos, player.getHeldItem(EnumHand.MAIN_HAND));
			this.pos = pos;
		}

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return d != null;
		}

		@Override
		public void detectAndSendChanges() {
			super.detectAndSendChanges();
			if (!sent) {
				NBTTagList l = new NBTTagList();
				d.stream().map(ConfigurableDevice::write).forEach(l::appendTag);
				sent = true;
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("l", l);
				MessageNBT.sendToAll(tag, listeners);
			}
		}

		@Override
		public void buttonPressed(EntityPlayer player, int id, int extra) {
			int[] p = TomsModUtils.createIntsFromBlockPos(pos);
			player.openGui(CoreInit.modInstance, GuiIDs.configurator.ordinal(), player.world, p[0], p[1], id);
		}
	}
}
