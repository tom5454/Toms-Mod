package com.tom.network.messages;

import com.tom.core.tileentity.inventory.ContainerTablet;
import com.tom.network.AbstractPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketTabletGui extends AbstractPacket<PacketTabletGui> {

	//private TabletHandler tab;
	private ItemStack tabStack;
    public PacketTabletGui(){

    }
    public PacketTabletGui(ItemStack tabStack){
    	this.tabStack = tabStack;
    }

    @Override
    public void fromBytes(ByteBuf buf){
    	/*boolean tn = buf.readBoolean();
    	int id = buf.readInt();
    	this.nn = tn;
    	if(tn){
    		this.tab = new TabletHandler(id);
    		this.tab.readFromPacket(buf);
    	}*/
    	tabStack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf){
    	/*boolean tn = tab != null;
    	buf.writeBoolean(tn);
    	buf.writeInt(tab.id);
    	if(tn){
    		this.tab.writeToPacket(buf);
    	}*/
    	ByteBufUtils.writeItemStack(buf, tabStack);
    }

    @Override
    public void handleClientSide(PacketTabletGui message, EntityPlayer player){
        if(player.openContainer instanceof ContainerTablet) {
            ((ContainerTablet)player.openContainer).tabStack = message.tabStack;
        }
    }

    @Override
    public void handleServerSide(PacketTabletGui message, EntityPlayer player){}

}
