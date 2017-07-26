package com.tom.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.tom.apis.TomsModUtils.PacketNoSpamChat;
import com.tom.apis.TomsModUtils.PacketNoSpamChat.Handler;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;
import com.tom.network.messages.MessageCamera;
import com.tom.network.messages.MessageCraftingReportSync;
import com.tom.network.messages.MessageFluidStackSync;
import com.tom.network.messages.MessageGuiButtonPress;
import com.tom.network.messages.MessageKey;
import com.tom.network.messages.MessageMarkerSync;
import com.tom.network.messages.MessageMinimap;
import com.tom.network.messages.MessageMonitor;
import com.tom.network.messages.MessageNBT;
import com.tom.network.messages.MessageNBT.MessageNBTRequest;
import com.tom.network.messages.MessageProfiler;
import com.tom.network.messages.MessageProfiler.MessageProfilerS;
import com.tom.network.messages.MessageProgress;
import com.tom.network.messages.MessageScroll;
import com.tom.network.messages.MessageTabGuiAction;
import com.tom.network.messages.MessageTabletGui;

public class NetworkHandler {
	private static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Configs.Chanel1);

	public static void init() {
		CoreInit.log.info("Loading Messages");
		if (CoreInit.isCCLoaded) {
			register(MessageMonitor.class, MessageMonitor.class, Side.CLIENT, 0);
			register(MessageTabletGui.class, MessageTabletGui.class, Side.CLIENT, 1);
			register(MessageTabGuiAction.class, MessageTabGuiAction.class, Side.SERVER, 2);
			register(MessageTabGuiAction.class, MessageTabGuiAction.class, Side.CLIENT, 3);
			register(MessageCamera.class, MessageCamera.class, Side.CLIENT, 4);
			register(MessageCamera.class, MessageCamera.class, Side.SERVER, 5);
		}
		if (CoreInit.isMapEnabled) {
			register(MessageMinimap.class, MessageMinimap.class, Side.CLIENT, 6);
			register(MessageMarkerSync.class, MessageMarkerSync.class, Side.CLIENT, 7);
			register(MessageMarkerSync.class, MessageMarkerSync.class, Side.SERVER, 8);
		}
		register(MessageKey.class, MessageKey.class, Side.SERVER, 9);
		register(MessageGuiButtonPress.class, MessageGuiButtonPress.class, Side.SERVER, 10);
		register(Handler.class, PacketNoSpamChat.class, Side.CLIENT, 11);
		register(MessageNBT.class, MessageNBT.class, Side.CLIENT, 12);
		register(MessageNBT.class, MessageNBT.class, Side.SERVER, 13);
		register(MessageNBTRequest.class, MessageNBTRequest.class, Side.CLIENT, 14);
		register(MessageNBTRequest.class, MessageNBTRequest.class, Side.SERVER, 15);
		register(MessageFluidStackSync.class, MessageFluidStackSync.class, Side.CLIENT, 16);
		register(MessageCraftingReportSync.class, MessageCraftingReportSync.class, Side.CLIENT, 17);
		register(MessageProgress.class, MessageProgress.class, Side.CLIENT, 18);
		register(MessageProfiler.class, MessageProfiler.class, Side.CLIENT, 19);
		register(MessageProfilerS.class, MessageProfilerS.class, Side.SERVER, 20);
		register(MessageScroll.class, MessageScroll.class, Side.SERVER, 21);
	}

	public static void sendToServer(IMessage message) {
		INSTANCE.sendToServer(message);
	}

	public static void sendTo(IMessage message, EntityPlayerMP player) {
		INSTANCE.sendTo(message, player);
	}

	public static void sendToAllAround(IMessage message, TargetPoint point) {
		INSTANCE.sendToAllAround(message, point);
	}

	/**
	 * Will send the given packet to every player within 64 blocks of the XYZ of
	 * the XYZ packet.
	 * 
	 * @param message
	 * @param world
	 */
	@SuppressWarnings("rawtypes")
	public static void sendToAllAround(MessageXYZ message, World world) {
		sendToAllAround(message, new TargetPoint(world.provider.getDimension(), message.x, message.y, message.z, 64D));
	}

	public static void sendToAll(IMessage message) {
		INSTANCE.sendToAll(message);
	}

	public static void sendToDimension(IMessage message, int dimensionId) {
		INSTANCE.sendToDimension(message, dimensionId);
	}

	private static <REQ extends IMessage, REPLY extends IMessage> void register(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side, int id) {
		CoreInit.log.info("Registering Message {} with id {}", messageHandler.getName(), id);
		INSTANCE.registerMessage(messageHandler, requestMessageType, id, side);
	}
}
