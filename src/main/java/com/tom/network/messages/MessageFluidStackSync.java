package com.tom.network.messages;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.tom.apis.TomsModUtils;
import com.tom.network.MessageBase;
import com.tom.network.NetworkHandler;

import io.netty.buffer.ByteBuf;

public class MessageFluidStackSync extends MessageBase<MessageFluidStackSync> {
	private int id;
	private FluidStack stack;

	public MessageFluidStackSync() {
	}

	public MessageFluidStackSync(int id, FluidStack stack) {
		this.id = id;
		this.stack = stack;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		stack = FluidStack.loadFluidStackFromNBT(tag);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		NBTTagCompound tag = new NBTTagCompound();
		if (stack != null)
			stack.writeToNBT(tag);
		ByteBufUtils.writeTag(buf, tag);
	}

	@Override
	public void handleClientSide(final MessageFluidStackSync message, final EntityPlayer player) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {

			@Override
			public void run() {
				if (player.openContainer instanceof IFluidContainer) {
					((IFluidContainer) player.openContainer).syncFluid(message.id, message.stack);
				}
			}
		});
	}

	@Override
	public void handleServerSide(MessageFluidStackSync message, EntityPlayer player) {
	}

	public static void sendTo(IContainerListener crafter, int id, FluidStack stack) {
		NetworkHandler.sendTo(new MessageFluidStackSync(id, stack), (EntityPlayerMP) crafter);
	}

	public static interface IFluidContainer {
		void syncFluid(int id, FluidStack stack);
	}

	public static class FluidSynchronizer {
		private boolean first = true;
		private FluidStack[] last;
		private Supplier<FluidStack>[] fluids;

		@SafeVarargs
		public FluidSynchronizer(Supplier<FluidStack>... fluids) {
			this.fluids = fluids;
			last = new FluidStack[fluids.length];
		}

		public void detectAndSendChanges(List<IContainerListener> listeners) {
			if (first) {
				for (int i = 0;i < fluids.length;i++) {
					FluidStack f = fluids[i].get();
					final int fi = i;
					listeners.forEach(l -> sendTo(l, fi, f));
					last[i] = f != null ? f.copy() : null;
				}
			} else {
				for (int i = 0;i < fluids.length;i++) {
					FluidStack fl = last[i];
					FluidStack f = fluids[i].get();
					final int fi = i;
					if (!TomsModUtils.areFluidStacksEqual(fl, f)) {
						listeners.forEach(l -> sendTo(l, fi, f));
					}
					last[i] = f != null ? f.copy() : null;
				}
			}
		}
	}
}
