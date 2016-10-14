package com.tom.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.tom.api.item.IConfigurator;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IConfigurable.ICustomConfigurationErrorMessage;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;

public class ConfiguratorHandler {
	public static boolean openConfigurator(ItemStack stack, EntityPlayer player,
			World world, BlockPos pos){
		if(stack != null && stack.getItem() != null && stack.getItem() instanceof IConfigurator && ((IConfigurator)stack.getItem()).isConfigurator(stack, player)){
			TileEntity tilee = world.getTileEntity(pos);
			if(tilee != null && tilee instanceof IConfigurable){
				if(!world.isRemote){
					IConfigurable te = (IConfigurable) tilee;
					IConfigurator item = (IConfigurator)stack.getItem();
					if(te.canConfigure(player, stack)){
						BlockPos securityStationPos = te.getSecurityStationPos();
						boolean canAccess = true;
						if(securityStationPos != null){
							TileEntity tileentity = world.getTileEntity(securityStationPos);
							if(tileentity instanceof ISecurityStation){
								ISecurityStation tile = (ISecurityStation) tileentity;
								canAccess = tile.canPlayerAccess(AccessType.CONFIGURATION, player);
							}
						}
						if(item.use(stack, player, true)){
							if(canAccess){
								player.openGui(CoreInit.modInstance, GuiIDs.configurator.ordinal(), world, pos.getX(),pos.getY(),pos.getZ());
								//NetworkHandler.sendTo(new ConfiguratorDataPacket(new OpeningDataHandler()), (EntityPlayerMP) player);
								//NBTTagCompound tag = new NBTTagCompound();
								//te.writeToNBTPacket(tag);
								//NetworkHandler.sendTo(new MessageNBT(tag), (EntityPlayerMP) player);
							}else{
								TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
							}
							item.use(stack, player, false);
						}
					}else{
						if(tilee instanceof ICustomConfigurationErrorMessage){
							ITextComponent[] msg = ((ICustomConfigurationErrorMessage)tilee).getMessage(player, stack);
							TomsModUtils.sendNoSpam(player, msg);
						}else{
							TomsModUtils.sendNoSpamTranslate(player,new Style().setColor(TextFormatting.RED), "tomsMod.chat.nothingToConfigure");
						}
						return false;
					}
				}
				return true;
			}else{
				TomsModUtils.sendNoSpamTranslate(player,new Style().setColor(TextFormatting.RED), "tomsMod.chat.nothingToConfigure");
			}
		}
		return false;
	}
	/*public static class ConfiguratorDataPacket extends MessageBase<ConfiguratorDataPacket>{
		private ByteBufDataHandler h;
		public ConfiguratorDataPacket() {

		}

		public ConfiguratorDataPacket(ByteBufDataHandler h) {
			this.h = h;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			h.readFrom(buf);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			h.writeTo(buf);
		}

		@Override
		public void handleClientSide(ConfiguratorDataPacket message,
				EntityPlayer player) {
			message.h.handleClient(player);
		}

		@Override
		public void handleServerSide(ConfiguratorDataPacket message,
				EntityPlayer player) {
			message.h.handleServer(player);
		}
		private static interface ByteBufDataHandler{
			void writeTo(ByteBuf buf);
			void readFrom(ByteBuf buf);
			void handleClient(EntityPlayer player);
			void handleServer(EntityPlayer player);
			ByteBufDataHandler newInstance();
		}
		static class OpeningDataHandler implements ByteBufDataHandler{
			public OpeningDataHandler() {
			}
			private IConfigurable te;
			private NBTTagCompound tag;
			@Override
			public void writeTo(ByteBuf buf) {
				NBTTagCompound tag = new NBTTagCompound();
				te.writeToNBTPacket(tag);
				ByteBufUtils.writeTag(buf, tag);
			}

			@Override
			public void readFrom(ByteBuf buf) {
				tag = ByteBufUtils.readTag(buf);
			}

			@Override
			public void handleClient(EntityPlayer player) {
				if(player.openContainer instanceof ContainerConfigurator){
					((ContainerConfigurator)player.openContainer).te.getOption().readFromNBTPacket(tag);
				}
			}

			@Override
			public void handleServer(EntityPlayer player) {}

			@Override
			public ByteBufDataHandler newInstance() {
				return new OpeningDataHandler();
			}
		}
	}*/
}
