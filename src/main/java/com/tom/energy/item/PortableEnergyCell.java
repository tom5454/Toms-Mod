package com.tom.energy.item;

import java.util.List;

import com.tom.api.energy.IEnergyContainerItem;
import com.tom.api.energy.ItemEnergyContainer;
import com.tom.energy.EnergyInit;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PortableEnergyCell extends ItemEnergyContainer {
	public PortableEnergyCell(){
		super(100000000,65536);
	}
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int par4, boolean par5) {
		if(entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) entity;
			InventoryPlayer inv = player.inventory;
			for(int i = 0;i<inv.getSizeInventory();i++){
				ItemStack itemStack = inv.getStackInSlot(i);
				if(itemStack != null && itemStack.getItem() instanceof IEnergyContainerItem && itemStack.getItem() != EnergyInit.portableEnergyCell && itemStack.getItem() != EnergyInit.portableSolarPanel){
					IEnergyContainerItem item = (IEnergyContainerItem) itemStack.getItem();
					if(is.getTagCompound() == null) is.setTagCompound(new NBTTagCompound());
					double energy = is.getTagCompound().hasKey("Energy") ? is.getTagCompound().getDouble("Energy") : 0;
					double receive = item.receiveEnergy(itemStack, Math.min(2048, energy), true);
					if(receive > 0){
						item.receiveEnergy(itemStack, receive, false);
						energy = energy - receive;
						is.getTagCompound().setDouble("Energy", energy);
					}
				}
			}
		}
	}
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean isAdvanced)
	{
		super.addInformation(itemStack, player, list, isAdvanced);
		double energy = this.getEnergyStored(itemStack);
		double per = energy / 100000;
		//System.out.println(per);
		int p = MathHelper.floor_double(per);
		list.add(I18n.format("tomsMod.tooltip.charge") + ": "+this.getMaxEnergyStored(itemStack)+"/"+energy+" "+p/10D+"%");
	}
}
