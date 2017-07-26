package com.tom.network.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.profiler.Profiler.Result;
import net.minecraft.server.MinecraftServer;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.tom.client.EventHandlerClient;
import com.tom.handler.PlayerHandler;
import com.tom.network.MessageBase;
import com.tom.network.NetworkHandler;

import io.netty.buffer.ByteBuf;

public class MessageProfiler extends MessageBase<MessageProfiler> {
	private List<Result> profilingData;
	private double meanTPS, meanTickTime;
	private Map<Integer, Pair<Double, Double>> dimTps;
	private String loc;

	public MessageProfiler(List<Result> profilingData, String loc) {
		this.profilingData = profilingData;
		this.loc = loc;
		dimTps = new HashMap<>();
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		for (Integer dimId : DimensionManager.getIDs()) {
			double worldTickTime = mean(server.worldTickTimes.get(dimId)) * 1.0E-6D;
			double worldTPS = Math.min(1000.0 / worldTickTime, 20);
			dimTps.put(dimId, Pair.of(worldTickTime, worldTPS));
		}
		meanTickTime = mean(server.tickTimeArray) * 1.0E-6D;
		meanTPS = Math.min(1000.0 / meanTickTime, 20);
	}

	private static long mean(long[] values) {
		long sum = 0l;
		for (long v : values) {
			sum += v;
		}

		return sum / values.length;
	}

	public MessageProfiler() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		meanTickTime = buf.readDouble();
		meanTPS = buf.readDouble();
		loc = ByteBufUtils.readUTF8String(buf);
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		NBTTagList l = tag.getTagList("l", 10);
		profilingData = new ArrayList<>();
		dimTps = new HashMap<>();
		for (int i = 0;i < l.tagCount();i++) {
			NBTTagCompound t = l.getCompoundTagAt(i);
			profilingData.add(new Result(t.getString("n"), t.getDouble("u"), t.getDouble("t")));
		}
		l = tag.getTagList("t", 10);
		for (int i = 0;i < l.tagCount();i++) {
			NBTTagCompound t = l.getCompoundTagAt(i);
			dimTps.put(t.getInteger("i"), Pair.of(t.getDouble("k"), t.getDouble("v")));
		}
	}

	int i;
	double j, k;

	@Override
	public void toBytes(final ByteBuf buf) {
		buf.writeDouble(meanTickTime);
		buf.writeDouble(meanTPS);
		ByteBufUtils.writeUTF8String(buf, loc);
		i = 0;
		j = 0;
		k = 0;
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList l = new NBTTagList();
		profilingData.forEach(r -> {
			if (i > 24) {
				j += r.usePercentage;
				k += r.totalUsePercentage;
				return;
			}
			i++;
			NBTTagCompound t = new NBTTagCompound();
			l.appendTag(t);
			t.setDouble("u", r.usePercentage);
			t.setDouble("t", r.totalUsePercentage);
			t.setString("n", r.profilerName);
		});
		if (profilingData.size() > 24) {
			NBTTagCompound t = new NBTTagCompound();
			l.appendTag(t);
			t.setDouble("u", j);
			t.setDouble("t", k);
			t.setString("n", "Other " + (profilingData.size() - 24) + " elements...");
		}
		tag.setTag("l", l);
		NBTTagList list = new NBTTagList();
		for (Entry<Integer, Pair<Double, Double>> e : dimTps.entrySet()) {
			NBTTagCompound t = new NBTTagCompound();
			t.setInteger("i", e.getKey());
			list.appendTag(t);
			t.setDouble("k", e.getValue().getKey());
			t.setDouble("v", e.getValue().getValue());
		}
		tag.setTag("t", list);
		ByteBufUtils.writeTag(buf, tag);
	}

	@Override
	public void handleClientSide(MessageProfiler message, EntityPlayer player) {
		EventHandlerClient.getInstance().tps = message.dimTps;
		EventHandlerClient.getInstance().meanTime = message.meanTickTime;
		EventHandlerClient.getInstance().meanTPS = message.meanTPS;
		if (message.profilingData.size() > 1) {
			EventHandlerClient.getInstance().list = message.profilingData;
			EventHandlerClient.getInstance().lastUpdate = player.world.getTotalWorldTime();
		}
	}

	@Override
	public void handleServerSide(MessageProfiler message, EntityPlayer player) {

	}

	public static class MessageProfilerS extends MessageBase<MessageProfilerS> {
		private String s;
		private boolean p;

		public MessageProfilerS(String s, boolean p) {
			this.s = s;
			this.p = p;
		}

		public MessageProfilerS() {
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			p = buf.readBoolean();
			if (buf.readBoolean())
				s = ByteBufUtils.readUTF8String(buf);
			else
				s = null;
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(p);
			buf.writeBoolean(s != null);
			if (s != null) {
				ByteBufUtils.writeUTF8String(buf, s);
			}
		}

		@Override
		public void handleClientSide(MessageProfilerS message, EntityPlayer player) {

		}

		@Override
		public void handleServerSide(MessageProfilerS message, EntityPlayer player) {
			PlayerHandler p = PlayerHandler.getPlayerHandler(player);
			p.profiling = message.p;
			p.setProfiler(message.s);
		}

	}

	public static void sendKey(String s, boolean p) {
		NetworkHandler.sendToServer(new MessageProfilerS(s, p));
	}
}
