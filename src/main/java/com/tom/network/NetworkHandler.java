package com.tom.network;

import net.minecraftforge.fml.relauncher.Side;

import com.tom.core.CoreInit;
import com.tom.lib.network.LibNetworkHandler;
import com.tom.network.messages.MessageCamera;
import com.tom.network.messages.MessageCraftingReportSync;
import com.tom.network.messages.MessageFluidStackSync;
import com.tom.network.messages.MessageGuiButtonPress;
import com.tom.network.messages.MessageKey;
import com.tom.network.messages.MessageMarker;
import com.tom.network.messages.MessageMarkerSync;
import com.tom.network.messages.MessageMonitor;
import com.tom.network.messages.MessageNBT;
import com.tom.network.messages.MessageNBT.MessageNBTRequest;
import com.tom.network.messages.MessageNetworkConnection;
import com.tom.network.messages.MessageProfiler;
import com.tom.network.messages.MessageProfiler.MessageProfilerS;
import com.tom.network.messages.MessageProgress;
import com.tom.network.messages.MessageScroll;
import com.tom.network.messages.MessageTabGuiAction;
import com.tom.network.messages.MessageTabletGui;

public class NetworkHandler extends LibNetworkHandler {
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
		register(MessageMarker.class, MessageMarker.class, Side.CLIENT, 6);
		register(MessageMarkerSync.class, MessageMarkerSync.class, Side.CLIENT, 7);
		register(MessageMarkerSync.class, MessageMarkerSync.class, Side.SERVER, 8);
		register(MessageKey.class, MessageKey.class, Side.SERVER, 9);
		register(MessageGuiButtonPress.class, MessageGuiButtonPress.class, Side.SERVER, 10);
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
		register(MessageNetworkConnection.class, MessageNetworkConnection.class, Side.CLIENT, 22);
	}

}
