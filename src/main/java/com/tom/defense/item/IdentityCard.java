package com.tom.defense.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.item.IIdentityCard;
import com.tom.api.item.IPowerLinkCard;
import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.tileentity.AccessType;
import com.tom.core.CoreInit;
import com.tom.defense.DefenseInit;
import com.tom.defense.tileentity.inventory.ContainerSecurityStation;
import com.tom.util.TomsModUtils;

public class IdentityCard extends Item implements IIdentityCard, IPowerLinkCard, ISecurityStationLinkCard, IModelRegisterRequired {
	public IdentityCard() {
		this.setMaxStackSize(4);
		this.setCreativeTab(DefenseInit.tabTomsModDefense);
		this.setUnlocalizedName("tmd.idCard");
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public String getUsername(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().hasKey("name") ? stack.getTagCompound().getString("name") : null;
	}

	@Override
	public void setUsername(ItemStack stack, String name) {
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setString("name", name);
		stack.setItemDamage(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote && this.getUsername(stack) != null) {
			stack.setItemDamage(1);
		} else if (!worldIn.isRemote && this.getMaster(stack) != null) {
			// this.setUsername(stack, "tom");
			stack.setItemDamage(2);
		} else if (!worldIn.isRemote && this.getStation(stack) != null) {
			// this.setUsername(stack, "tom");
			stack.setItemDamage(3);
		} else if (!worldIn.isRemote) {
			// this.setUsername(stack, "tom");
			// this.setMaster(stack,new BlockPos(1,1,1));
			// this.setStation(stack,new BlockPos(1,1,1));
			stack.setItemDamage(0);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + (this.getUsername(stack) == null ? (this.getMaster(stack) != null ? "_p" : (this.getStation(stack) != null ? "_s" : "_b")) : "");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
		String name = this.getUsername(stack);
		BlockPos master = this.getMaster(stack);
		BlockPos station = this.getStation(stack);
		EntityPlayer playerIn = Minecraft.getMinecraft().player;
		if (name != null) {
			tooltip.add(I18n.format("tomsMod.tooltip.username") + ": " + name);
			if (playerIn.openContainer instanceof ContainerSecurityStation) {
				ContainerSecurityStation c = (ContainerSecurityStation) playerIn.openContainer;
				if (c.getSlot(2).getStack() == stack) {
					tooltip.add("");
					tooltip.add("");
					tooltip.add("");
				}
			}
			tooltip.add(I18n.format("tomsMod.tooltip.rights"));
			List<AccessType> rights = this.getRights(stack);
			for (AccessType r : rights) {
				tooltip.add(I18n.format("tomsMod.tooltip.right", r.getName()));
			}
		}
		if (master != null)
			tooltip.add(I18n.format("tomsMod.tooltip.position", master.getX(), master.getY(), master.getZ()));
		if (station != null)
			tooltip.add(I18n.format("tomsMod.tooltip.position", station.getX(), station.getY(), station.getZ()));

	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public BlockPos getMaster(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().hasKey("master", 10) ? TomsModUtils.readBlockPosFromNBT(stack.getTagCompound().getCompoundTag("master")) : null;
	}

	@Override
	public BlockPos getStation(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().hasKey("station", 10) ? TomsModUtils.readBlockPosFromNBT(stack.getTagCompound().getCompoundTag("station")) : null;
	}

	@Override
	public void setStation(ItemStack stack, BlockPos pos) {
		stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound t = new NBTTagCompound();
		TomsModUtils.writeBlockPosToNBT(t, pos);
		stack.getTagCompound().setTag("station", t);
		stack.setItemDamage(3);
	}

	@Override
	public void setMaster(ItemStack stack, BlockPos pos) {
		stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound t = new NBTTagCompound();
		TomsModUtils.writeBlockPosToNBT(t, pos);
		stack.getTagCompound().setTag("master", t);
		stack.setItemDamage(2);
	}

	@Override
	public boolean isEmpty(ItemStack stack) {
		return stack.getItemDamage() == 0;
	}

	@Override
	public List<AccessType> getRights(ItemStack stack) {
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		int value = stack.getTagCompound().getInteger("rights");
		return decompileRights(value);
	}

	@Override
	public void setRights(ItemStack stack, List<AccessType> rights) {
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger("rights", compileRights(rights));
	}

	public static boolean contains(AccessType type, int value) {
		return (value & (1 << type.ordinal())) != 0;
	}

	public static int setValue(AccessType type, int valueTo, boolean value) {
		valueTo &= ~(1 << type.ordinal());
		if (value)
			valueTo |= 1 << type.ordinal();
		return valueTo;
	}

	public static int compileRights(List<AccessType> rights) {
		int value = 0;
		for (AccessType t : rights) {
			value = setValue(t, value, true);
		}
		return value;
	}

	public static List<AccessType> decompileRights(int value) {
		List<AccessType> list = new ArrayList<>();
		for (AccessType t : AccessType.VALUES) {
			if (contains(t, value))
				list.add(t);
		}
		return list;
	}

	@Override
	public void registerModels() {
		CoreInit.registerRender(this, 0, "tomsmoddefense:tmd.idCard_b");
		CoreInit.registerRender(this, 1, "tomsmoddefense:tmd.idCard");
		CoreInit.registerRender(this, 2, "tomsmoddefense:tmd.idCard_p");
		CoreInit.registerRender(this, 3, "tomsmoddefense:tmd.idCard_s");
	}
}
