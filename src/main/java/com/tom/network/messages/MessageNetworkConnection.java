package com.tom.network.messages;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.tom.api.tileentity.IAccessPoint;
import com.tom.core.CoreInit;
import com.tom.lib.network.MessageBase;

import io.netty.buffer.ByteBuf;

public class MessageNetworkConnection extends MessageBase<MessageNetworkConnection> {
	private List<Info> infos;
	public MessageNetworkConnection() {
		infos = new ArrayList<>();
	}
	public MessageNetworkConnection(List<IAccessPoint>[] connInfo) {
		infos = new ArrayList<>();
		if(!connInfo[0].isEmpty()){
			connInfo[0].forEach(a -> {
				infos.add(new Info(false, a.getName(), true));
			});
		}
		if(!connInfo[1].isEmpty()){
			connInfo[1].forEach(a -> {
				infos.add(new Info(true, a.getName(), true));
			});
		}
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		int size = buf.readShort();
		for(int i = 0;i<size;i++){
			infos.add(new Info(buf));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(infos.size());
		infos.forEach(a -> a.writeToBuf(buf));
	}

	@Override
	public void handleClientSide(MessageNetworkConnection message, EntityPlayer player) {
		message.infos.forEach(i -> Minecraft.getMinecraft().getToastGui().add(new MessageToast(i)));
	}

	@Override
	public void handleServerSide(MessageNetworkConnection message, EntityPlayer player) {

	}
	private static class Info {
		private boolean connected;
		private String name;
		private boolean ant;
		public Info(boolean connected, String name, boolean ant) {
			this.connected = connected;
			this.name = name;
			this.ant = ant;
		}
		public Info(ByteBuf buf) {
			name = ByteBufUtils.readUTF8String(buf);
			ant = buf.readBoolean();
			connected = buf.readBoolean();
		}
		public void writeToBuf(ByteBuf buf){
			ByteBufUtils.writeUTF8String(buf, name);
			buf.writeBoolean(ant);
			buf.writeBoolean(connected);
		}
	}
	public static class MessageToast implements IToast {
		private final Info info;
		private static final ItemStack DISPLAY = new ItemStack(CoreInit.Antenna, 1, 1);
		private boolean hasPlayedSound = false;

		public MessageToast(Info info)
		{
			this.info = info;
		}

		@Override
		public IToast.Visibility draw(GuiToast toastGui, long delta)
		{
			toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			toastGui.drawTexturedModalRect(0, 0, 0, 0, 160, 32);
			String title = info.connected ? I18n.format("tomsMod.antennaFound", info.name) : I18n.format("tomsMod.antennaLost", info.name);
			List<String> list = toastGui.getMinecraft().fontRenderer.listFormattedStringToWidth(title, 125);

			if (list.size() == 1)
			{
				//toastGui.getMinecraft().fontRenderer.drawString(I18n.format("advancements.toast." + displayinfo.getFrame().getName()), 30, 7, 16776960 | -16777216);
				toastGui.getMinecraft().fontRenderer.drawString(title, 30, 18, -1);
			}
			else
			{
				//int j = 1500;
				//float f = 300.0F;

				/*if (delta < 1500L)
				{
					//int k = MathHelper.floor(MathHelper.clamp((1500L - delta) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
					//toastGui.getMinecraft().fontRenderer.drawString(I18n.format("advancements.toast." + displayinfo.getFrame().getName()), 30, 11, i | k);
				}
				else
				{*/
				int i1 = MathHelper.floor(MathHelper.clamp((delta) / 400.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
				int l = 16 - list.size() * toastGui.getMinecraft().fontRenderer.FONT_HEIGHT / 2;

				for (String s : list)
				{
					toastGui.getMinecraft().fontRenderer.drawString(s, 30, l, 16777215 | i1);
					l += toastGui.getMinecraft().fontRenderer.FONT_HEIGHT;
				}
				//}
			}

			if (!this.hasPlayedSound && delta > 0L)
			{
				this.hasPlayedSound = true;

				/*if (displayinfo.getFrame() == FrameType.CHALLENGE)
				{
					toastGui.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
				}*/
			}

			RenderHelper.enableGUIStandardItemLighting();
			toastGui.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI((EntityLivingBase)null, DISPLAY, 8, 8);
			return delta >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
		}
	}
}
