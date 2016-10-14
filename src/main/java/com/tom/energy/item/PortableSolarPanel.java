package com.tom.energy.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.energy.IEnergyContainerItem;
import com.tom.apis.TomsModUtils;
import com.tom.energy.EnergyInit;

public class PortableSolarPanel extends Item implements IEnergyContainerItem{
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int par4, boolean par5) {
		long ticks = world.getWorldTime();
		if(is.getTagCompound() == null) is.setTagCompound(new NBTTagCompound());
		if(world.isDaytime()){
			float biomeTemp = world.getBiomeForCoordsBody(new BlockPos(entity)).getTemperature();
			int light = world.getLightFor(EnumSkyBlock.SKY, new BlockPos(entity));
			int tier = is.getTagCompound().hasKey("tier") ? is.getTagCompound().getInteger("tier") : 0;
			double e = ((tier + 1) * 20000) * (light / 15);
			double tempPer = biomeTemp * 0.2;
			e /= (0.8D + tempPer);
			//long div = ticks / 12000;
			long ticksCR = ticks - 6000;
			long ticksC = ticksCR < 0 ? -ticksCR : ticksCR;
			double ticksM = (6000 / e) - (ticksC / e);
			double energy = is.getTagCompound().hasKey("Energy") ? is.getTagCompound().getDouble("Energy") : 0;
			if(energy < 1000){
				energy = Math.min(energy + ticksM, 1000);
				is.getTagCompound().setDouble("Energy", energy);
			}
		}
		if(entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) entity;
			InventoryPlayer inv = player.inventory;
			for(int i = 0;i<inv.getSizeInventory();i++){
				ItemStack itemStack = inv.getStackInSlot(i);
				if(itemStack != null && itemStack.getItem() == EnergyInit.portableEnergyCell){
					PortableEnergyCell item = (PortableEnergyCell) itemStack.getItem();
					double energy = is.getTagCompound().hasKey("Energy") ? is.getTagCompound().getDouble("Energy") : 0;
					double receive = item.receiveEnergy(itemStack, Math.min(this.getMaxExtract(is), energy), true);
					if(receive > 0){
						item.receiveEnergy(itemStack, receive, false);
						energy = energy - receive;
						is.getTagCompound().setDouble("Energy", energy);
					}
				}
			}
		}
		/*long ticksL = ticks % 12000;
		long divL = div % 2;
		long ticksCR = ticksL - 6000;
		long ticksC = ticksCR < 0 ? -ticksCR : ticksCR;
		double e = tier == 0 ? 120000D : (tier == 1 ? 60000D : (tier == 2 ? 30000D : (tier == 3 ? 10000D : (20000 / tier))));
		double ticksM = (6000 / e) - (ticksC / e);
		boolean active = divL == 0;
		Vec3d vec3 = new Vec3d(entity.posX, entity.posY, entity.posZ);
		Vec3d vec31 = vec3.addVector(0,256,0);
		RayTraceResult pos = world.rayTraceBlocks(vec3, vec31, true);
		double power = is.getTagCompound().hasKey("power") ? is.getTagCompound().getDouble("power") : 0D;
		power = power - (0.00001D * power);
		boolean block = (pos != null && pos.typeOfHit == RayTraceResult.Type.BLOCK &&
				GlobalFields.glassBlocks.containsKey(entity.worldObj.getBlockState(pos.getBlockPos()).getBlock()));
		if((pos == null || (pos != null && pos.typeOfHit != RayTraceResult.Type.BLOCK) || block) && active){
			if(block){
				float m = GlobalFields.glassBlocks.get(entity.worldObj.getBlockState(pos.getBlockPos()).getBlock());
				ticksM = ticksM * m;
			}
			if(power < 2)power = power + ticksM;
			int energy = is.getTagCompound().hasKey("Energy") ? is.getTagCompound().getInteger("Energy") : 0;
			if(power >= 1 && energy < 1000){
				int amount = MathHelper.floor_double(power - power % 1);
				power = power - amount;
				energy = energy + amount;
				//System.out.println(power%1);
				is.getTagCompound().setDouble("Energy", energy);
			}
			is.getTagCompound().setDouble("power", power);
		}
		//System.out.println(ticks + " " +ticksL + " " + div);*/
	}

	@Override
	public double receiveEnergy(ItemStack container, double maxReceive,
			boolean simulate) {
		return 0;
	}

	@Override
	public double extractEnergy(ItemStack container, double maxExtract, boolean simulate) {

		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
			return 0;
		}
		double energy = container.getTagCompound().getDouble("Energy");

		double energyExtracted = Math.min(energy, Math.min(this.getMaxExtract(container), maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
			container.getTagCompound().setDouble("Energy", energy);
		}
		return energyExtracted;
	}

	@Override
	public double getEnergyStored(ItemStack container) {

		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
			return 0;
		}
		return container.getTagCompound().getDouble("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return 1000;
	}
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean isAdvanced)
	{
		super.addInformation(itemStack, player, list, isAdvanced);
		float biomeTemp = player.worldObj.getBiomeForCoordsBody(new BlockPos(player)).getTemperature();
		long ticks = player.worldObj.getWorldTime();
		if(player.worldObj.isDaytime()){
			int light = player.worldObj.getLightFor(EnumSkyBlock.SKY, new BlockPos(player));
			int tier = itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("tier") ? itemStack.getTagCompound().getInteger("tier") : 0;
			double e = ((tier + 1) * 20000) * (light / 15);
			double tempPer = biomeTemp * 0.2;
			e /= (0.8D + tempPer);
			//long div = ticks / 12000;
			long ticksCR = ticks - 6000;
			long ticksC = ticksCR < 0 ? -ticksCR : ticksCR;
			double ticksM = (6000 / e) - (ticksC / e);
			/*double energy = itemStack.getTagCompound().hasKey("Energy") ? itemStack.getTagCompound().getDouble("Energy") : 0;
			if(energy < 1000){
				energy = Math.min(energy + ticksM, 1000);
				itemStack.getTagCompound().setDouble("Energy", energy);
			}*/
			double ticksM2 = (ticksM/0.6D);
			double ticksM3 = Math.floor(ticksM2 * 1000) / 5;
			double energy = Math.floor(this.getEnergyStored(itemStack) * 100) / 100;
			double per = energy * 100 / 1000;
			int p = MathHelper.floor_double(per);
			list.add(I18n.format("tomsMod.tooltip.charge") + ": "+this.getMaxEnergyStored(itemStack)+"/"+energy+" "+p+"%");
			TomsModUtils.addActiveTag(list, true);
			list.add(I18n.format("tomsMod.tooltip.tier")+": "+(tier+1));
			list.add(I18n.format("tomsMod.tooltip.efficiency", ticksM3+"%"));
		}else
			TomsModUtils.addActiveTag(list, false);
		/*double energy = this.getEnergyStored(itemStack);
		long ticks = player.worldObj.getWorldTime();
		if(itemStack.getTagCompound() == null) itemStack.setTagCompound(new NBTTagCompound());
		double power = Math.floor((itemStack.getTagCompound().hasKey("power") ? itemStack.getTagCompound().getDouble("power") : 0D) * 10000) / 10000;
		double per = energy * 100 / 1000;
		long ticksL = ticks % 12000;
		long div = ticks / 12000;
		long divL = div % 2;
		long ticksCR = ticksL - 6000;
		int tier = itemStack.getTagCompound().hasKey("tier") ? itemStack.getTagCompound().getInteger("tier") : 0;
		long ticksC = ticksCR < 0 ? -ticksCR : ticksCR;
		boolean dayTime = divL == 0;
		double e = tier == 0 ? 120000D : (tier == 1 ? 60000D : (tier == 2 ? 30000D : (tier == 3 ? 10000D : (20000 / tier))));
		double ticksM = (6000 / e) - (ticksC / e);
		double ticksM2 = (ticksM/0.6D);
		//System.out.println(per);
		int p = MathHelper.floor_double(per);
		list.add(I18n.format("tomsMod.tooltip.charge") + ": "+(this.getMaxEnergyStored(itemStack)+2)+"/"+(power+energy)+" "+p+"%");
		Vec3d vec3 = new Vec3d(player.posX, player.posY, player.posZ);
		Vec3d vec31 = vec3.addVector(0,256,0);
		RayTraceResult pos = player.worldObj.rayTraceBlocks(vec3, vec31, true);
		boolean block = (pos != null && pos.typeOfHit == RayTraceResult.Type.BLOCK &&
				GlobalFields.glassBlocks.containsKey(player.worldObj.getBlockState(pos.getBlockPos()).getBlock()));
		boolean active = (pos == null || (pos != null && pos.typeOfHit != RayTraceResult.Type.BLOCK) || block) && dayTime;
		if(block){
			float m = GlobalFields.glassBlocks.get(player.worldObj.getBlockState(pos.getBlockPos()).getBlock());
			ticksM2 = ticksM2 * m;
		}
		double ticksM3 = active ? Math.floor(ticksM2 * 1000) / 10 : 0;
		TomsModUtils.addActiveTag(list, active);
		list.add(I18n.format("tomsMod.tooltip.tier")+": "+(tier+1));
		list.add(I18n.format("tomsMod.tooltip.efficiency", ticksM3+"%"));*/
	}
	public int getMaxExtract(ItemStack is){
		int tier = is.getTagCompound().hasKey("tier") ? is.getTagCompound().getInteger("tier")+1 : 1;
		return 5*tier;
	}
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged;
	}
}
