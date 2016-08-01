package com.tom.core.item;

import java.util.ArrayList;
import java.util.List;

import com.tom.api.energy.ItemEnergyContainer;
import com.tom.api.tileentity.TileEntityJammerBase;
import com.tom.apis.TomsModUtils;
import com.tom.core.Minimap;
import com.tom.lib.GlobalFields;

import mapwriterTm.Mw;
import mapwriterTm.map.Marker.RenderType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEntityTracker extends ItemEnergyContainer {
	public ItemEntityTracker(){
		super(10000000);
	}
	/*@SideOnly(Side.CLIENT)
	private IIcon off;
	@SideOnly(Side.CLIENT)
	private IIcon jammed;*/
	private static final int updateRate = 20;
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean isAdvanced)
	{
		super.addInformation(itemStack, player, list, isAdvanced);
		double energy = this.getEnergyStored(itemStack);
		double per = energy * 100 / 10000000;
		//System.out.println(per);
		int p = MathHelper.floor_double(per);
		boolean active = itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("active") ? itemStack.getTagCompound().getBoolean("active") : false;
		list.add(I18n.format("tomsMod.tooltip.charge") + ": "+this.getMaxEnergyStored(itemStack)+"/"+energy+" "+p+"%");
		TomsModUtils.addActiveTag(list, active);
	}
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int par4, boolean par5) {
		NBTTagCompound tag = is.getTagCompound();
		boolean powered = this.extractEnergy(is, 10, true) == 10;
		boolean active = tag != null && tag.hasKey("active") ? tag.getBoolean("active") : false;
		boolean jammed = tag != null && tag.hasKey("jammed") ? tag.getBoolean("jammed") : false;
		int meta = is.getItemDamage();
		if(active && jammed){
			if(meta != 2)is.setItemDamage(2);
		}else if(active){
			if(meta != 1)is.setItemDamage(1);
		}else{
			if(meta != 0)is.setItemDamage(0);
		}
		List<EntityMob> mobs = new ArrayList<EntityMob>();
		List<EntityAnimal> animals = new ArrayList<EntityAnimal>();
		List<EntityLiving> other = new ArrayList<EntityLiving>();
		int i = tag != null && tag.hasKey("timer") ? tag.getInteger("timer") : 0;
		if(i == updateRate && entity instanceof EntityPlayer){
			tag.setInteger("timer", 0);
			if(world.isRemote){
				//System.out.println("update");
				if(!jammed){
					List<?> entities = powered && active ? world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(entity.posX - 32, entity.posY - 32, entity.posZ - 32, entity.posX + 33, entity.posY + 33, entity.posZ + 33)) : new ArrayList<Object>();
					for(Object o : entities){
						if(o instanceof EntityLiving && !o.equals(entity)){
							if(o instanceof EntityMob){
								mobs.add((EntityMob) o);
							}else if(o instanceof EntityAnimal){
								animals.add((EntityAnimal) o);
							}else if(o instanceof INpc || o instanceof EntityPlayer){
								other.add((EntityLiving) o);
							}
						}
					}
				}
				for(String a : GlobalFields.animals){
					/*int int1 = GlobalFields.animals.indexOf(a);
					String name = a.hasCustomNameTag() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
							+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
								int1+">";*/
					//System.out.println(a);
					//System.out.println();
					Minimap.deleteWayPoint("animals", a);
				}
				for(String a : GlobalFields.mobs){
					/*int int1 = GlobalFields.mobs.indexOf(a);
					String name = a.hasCustomNameTag() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
							+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
								int1+">";*/
					Minimap.deleteWayPoint("enemies", a);
				}
				for(String a : GlobalFields.other){
					/*int int1 = GlobalFields.other.indexOf(a);
					String name = a.hasCustomNameTag() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
							+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
								int1+">";*/
					Minimap.deleteWayPoint("other", a);
				}
				GlobalFields.animals.clear();
				GlobalFields.mobs.clear();
				GlobalFields.other.clear();
				if(!jammed){
					for(EntityAnimal a : animals){
						int int1 = animals.indexOf(a);
						String name = a.hasCustomName() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
								+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
								int1+">";
						Minimap.createTexturedWayPoint("animals", MathHelper.floor_double(a.posX), MathHelper.floor_double(a.posY), MathHelper.floor_double(a.posZ),
								a.dimension, name, "tm:minimap/entityFriendly",0, RenderType.NONE, RenderType.NONE,false, "");
						GlobalFields.animals.add(name);
					}
					for(EntityMob a : mobs){
						int int1 = mobs.indexOf(a);
						String name = a.hasCustomName() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
								+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
								int1+">";
						Minimap.createTexturedWayPoint("enemies",  MathHelper.floor_double(a.posX), MathHelper.floor_double(a.posY), MathHelper.floor_double(a.posZ),
								a.dimension, name, "tm:minimap/entityEnemy",0, RenderType.NONE, RenderType.NONE, false, "");
						GlobalFields.mobs.add(name);
					}
					for(EntityLiving a : other){
						int int1 = other.indexOf(a);
						String name = a.hasCustomName() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
								+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
								int1+">";
						Minimap.createTexturedWayPoint("other",  MathHelper.floor_double(a.posX), MathHelper.floor_double(a.posY), MathHelper.floor_double(a.posZ),
								a.dimension, name, "tm:minimap/entityVillager",0, RenderType.NONE, RenderType.NONE,false, "");
						GlobalFields.other.add(name);
					}
				}else{
					String name = "Unknown";
					int jx = is.getTagCompound().hasKey("jx") ? is.getTagCompound().getInteger("jx") : 0;
					int jy = is.getTagCompound().hasKey("jy") ? is.getTagCompound().getInteger("jy") : 0;
					int jz = is.getTagCompound().hasKey("jz") ? is.getTagCompound().getInteger("jz") : 0;
					int jd = is.getTagCompound().hasKey("jd") ? is.getTagCompound().getInteger("jd") : 0;
					Minimap.createTexturedWayPoint("other",  jx, jy,jz,
							jd, name, "tm:minimap/entityUnknown",0, RenderType.NONE, RenderType.NONE, false, "");
					GlobalFields.other.add(name);
				}
				/*GlobalFields.animals = new ArrayList<EntityAnimal>(animals);
				GlobalFields.mobs = new ArrayList<EntityMob>(mobs);
				GlobalFields.other = new ArrayList<EntityLiving>(other);*/
			}else{
				this.extractEnergy(is, 10, false);
				int jx = is.getTagCompound().hasKey("jx") ? is.getTagCompound().getInteger("jx") : 0;
				int jy = is.getTagCompound().hasKey("jy") ? is.getTagCompound().getInteger("jy") : 0;
				int jz = is.getTagCompound().hasKey("jz") ? is.getTagCompound().getInteger("jz") : 0;
				TileEntity tile = world.getTileEntity(new BlockPos(jx, jy, jz));
				if(!(tile instanceof TileEntityJammerBase && ((TileEntityJammerBase)tile).isActive()) || entity.getDistance(jx, jy, jz) > ((TileEntityJammerBase)tile).getRange()){
					is.getTagCompound().setBoolean("jammed", false);
				}
			}
		}else if(!powered && active){
			tag.setBoolean("active", false);
			tag.setInteger("timer",updateRate);
			for(String a : GlobalFields.animals){
				/*int int1 = GlobalFields.animals.indexOf(a);
				String name = a.hasCustomNameTag() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
						+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
							int1+">";*/
				//System.out.println(a);
				//System.out.println();
				Minimap.deleteWayPoint("animals", a);
			}
			for(String a : GlobalFields.mobs){
				/*int int1 = GlobalFields.mobs.indexOf(a);
				String name = a.hasCustomNameTag() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
						+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
							int1+">";*/
				Minimap.deleteWayPoint("enemies", a);
			}
			for(String a : GlobalFields.other){
				/*int int1 = GlobalFields.other.indexOf(a);
				String name = a.hasCustomNameTag() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
						+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
							int1+">";*/
				Minimap.deleteWayPoint("other", a);
			}
			Mw.getInstance().markerManager.setVisibleGroupName("all");
			Mw.getInstance().markerManager.update();
		}else if(powered && active){
			tag.setInteger("timer", i+1);
		}
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack is,
			World world, EntityPlayer player, EnumHand hand) {
		this.onItemUse(is, player, world);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, is);
	}
	private void onItemUse(ItemStack is, EntityPlayer player, World world){
		if(is.getTagCompound() != null){
			NBTTagCompound tag = is.getTagCompound();
			if(tag.hasKey("active")){
				if(player.isSneaking()){
					if(this.getEnergyStored(is) < 10){

					}else{
						boolean active = tag.getBoolean("active");
						is.getTagCompound().setBoolean("active", !active);
						if(active && world.isRemote){
							//System.out.println("turn off");
							for(String a : GlobalFields.animals){
								/*int int1 = GlobalFields.animals.indexOf(a);
								String name = a.hasCustomNameTag() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
										+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
											int1+">";*/
								//System.out.println(a);
								//System.out.println();
								Minimap.deleteWayPoint("animals", a);
							}
							for(String a : GlobalFields.mobs){
								/*int int1 = GlobalFields.mobs.indexOf(a);
								String name = a.hasCustomNameTag() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
										+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
											int1+">";*/
								Minimap.deleteWayPoint("enemies", a);
							}
							for(String a : GlobalFields.other){
								/*int int1 = GlobalFields.other.indexOf(a);
								String name = a.hasCustomNameTag() ? a.getCustomNameTag() : a.getName() + "<"+a.getMaxHealth()+"HP/"+a.getHealth()+"HP, "
										+(Math.floor(a.posX*100D)/100D)+", "+(Math.floor(a.posY*100D)/100D)+", "+(Math.floor(a.posZ*100D)/100D)+", "+
											int1+">";*/
								Minimap.deleteWayPoint("other", a);
							}
							Mw.getInstance().markerManager.setVisibleGroupName("all");
							Mw.getInstance().markerManager.update();
						}
					}
				}
			}else{
				is.getTagCompound().setBoolean("active", true);
			}
		}else{
			is.setTagCompound(new NBTTagCompound());
			is.getTagCompound().setBoolean("active", true);
		}
	}
	/*public void registerIcons(IIconRegister i){
		this.itemIcon = i.registerIcon("minecraft:tm/radarActive");
		this.off = i.registerIcon("minecraft:tm/radarOff");
		this.jammed = i.registerIcon("minecraft:tm/radarJammed");
	}
	public IIcon getIcon(ItemStack is, int i){
		return this.getIcon(is);
	}
	public IIcon getIconIndex(ItemStack is){
		return this.getIcon(is);
	}
	private IIcon getIcon(ItemStack is){
		NBTTagCompound tag = is.getTagCompound() != null ? is.getTagCompound() : new NBTTagCompound();
		boolean active = tag != null && tag.hasKey("active") ? tag.getBoolean("active") : false;
		boolean jammed = tag != null && tag.hasKey("jammed") ? tag.getBoolean("jammed") : false;
		return active ? jammed ? this.jammed : this.itemIcon : this.off;
	}*/
	/**if(is.getTagCompound() != null && entity instanceof EntityPlayer){
			NBTTagCompound tag = is.getTagCompound();
			if(tag.hasKey("active")){
				if(tag.getBoolean("active")){*/
}
