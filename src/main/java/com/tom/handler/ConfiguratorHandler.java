package com.tom.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.tom.api.Capabilities;
import com.tom.api.item.IConfigurator;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IConfigurable.ICustomConfigurationErrorMessage;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.util.TomsModUtils;

public class ConfiguratorHandler {
	public static boolean openConfigurator(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
		if (stack != null && stack.getItem() != null && stack.getItem() instanceof IConfigurator && ((IConfigurator) stack.getItem()).isConfigurator(stack, player)) {
			TileEntity tilee = world.getTileEntity(pos);
			if (tilee != null) {
				if (tilee instanceof IConfigurable) {
					if (!world.isRemote) {
						IConfigurable te = (IConfigurable) tilee;
						return configure(stack, player, world, pos, null, te);
					}
					return true;
				} else {
					Map<EnumFacing, IConfigurable> c = new HashMap<>();
					for (EnumFacing f : EnumFacing.VALUES) {
						if (tilee.hasCapability(Capabilities.CONFIGURABLE, f)) {
							IConfigurable con = tilee.getCapability(Capabilities.CONFIGURABLE, f);
							if (!c.containsValue(con)) {
								c.put(f, con);
							}
						}
					}
					if (tilee.hasCapability(Capabilities.CONFIGURABLE, null)) {
						IConfigurable con = tilee.getCapability(Capabilities.CONFIGURABLE, null);
						if (!c.containsValue(con)) {
							c.put(null, con);
						}
					}
					if (c.isEmpty()) {
						TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.nothingToConfigure");
					} else if (c.size() == 1) {
						if (!world.isRemote) {
							Entry<EnumFacing, IConfigurable> e = c.entrySet().stream().findFirst().get();
							return configure(stack, player, world, pos, e.getKey(), e.getValue());
						}
					} else {
						List<Entry<EnumFacing, IConfigurable>> c2 = c.entrySet().stream().filter(t -> t.getValue().canConfigure(player, stack)).collect(Collectors.toList());
						if (c2.isEmpty()) {
							List<ITextComponent[]> e = c.entrySet().stream().filter(t -> t.getValue() instanceof ICustomConfigurationErrorMessage).map(te -> ((ICustomConfigurationErrorMessage) te).getMessage(player, stack)).collect(Collectors.toList());
							if (e.isEmpty()) {
								TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.nothingToConfigure");
							} else {
								e.forEach(m -> TomsModUtils.sendNoSpam(player, m));
							}
						} else if (c2.size() == 1) {
							if (!world.isRemote) {
								Entry<EnumFacing, IConfigurable> e = c2.get(0);
								return configure(stack, player, world, pos, e.getKey(), e.getValue());
							}
						} else {
							IConfigurator item = (IConfigurator) stack.getItem();
							if (item.use(stack, player, true)) {
								player.openGui(CoreInit.modInstance, GuiIDs.configuratorChoose.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
								item.use(stack, player, false);
							}
						}
					}
				}
			} else {
				TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.nothingToConfigure");
			}
		}
		return false;
	}

	public static List<ConfigurableDevice> getDevices(EntityPlayer player, World world, BlockPos pos, ItemStack stack) {
		TileEntity tilee = world.getTileEntity(pos);
		if (tilee != null) {
			if (tilee instanceof IConfigurable) {
				return Collections.singletonList(new ConfigurableDevice((IConfigurable) tilee, null));
			} else {
				Map<EnumFacing, IConfigurable> c = new HashMap<>();
				for (EnumFacing f : EnumFacing.VALUES) {
					if (tilee.hasCapability(Capabilities.CONFIGURABLE, f)) {
						IConfigurable con = tilee.getCapability(Capabilities.CONFIGURABLE, f);
						if (!c.containsValue(con)) {
							c.put(f, con);
						}
					}
				}
				if (tilee.hasCapability(Capabilities.CONFIGURABLE, null)) {
					IConfigurable con = tilee.getCapability(Capabilities.CONFIGURABLE, null);
					if (!c.containsValue(con)) {
						c.put(null, con);
					}
				}
				if (c.isEmpty()) {
					TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.nothingToConfigure");
				} else if (c.size() == 1) {
					return Collections.singletonList(new ConfigurableDevice(c.entrySet().stream().findFirst().get()));
				} else {
					List<Entry<EnumFacing, IConfigurable>> c2 = c.entrySet().stream().filter(t -> t.getValue().canConfigure(player, stack)).collect(Collectors.toList());
					if (c2.isEmpty()) {
						List<ITextComponent[]> e = c.entrySet().stream().filter(t -> t.getValue() instanceof ICustomConfigurationErrorMessage).map(te -> ((ICustomConfigurationErrorMessage) te.getValue()).getMessage(player, stack)).collect(Collectors.toList());
						if (e.isEmpty()) {
							TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.nothingToConfigure");
						} else {
							e.forEach(m -> TomsModUtils.sendNoSpam(player, m));
						}
					} else if (c2.size() == 1) {
						return Collections.singletonList(new ConfigurableDevice(c2.get(0)));
					} else {
						return c2.stream().map(ConfigurableDevice::new).collect(Collectors.toList());
					}
				}
			}
		} else {
			TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.nothingToConfigure");
		}
		return null;
	}

	private static boolean configure(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, IConfigurable te) {
		IConfigurator item = (IConfigurator) stack.getItem();
		if (te.canConfigure(player, stack)) {
			BlockPos securityStationPos = te.getSecurityStationPos();
			boolean canAccess = true;
			if (securityStationPos != null) {
				TileEntity tileentity = world.getTileEntity(securityStationPos);
				if (tileentity instanceof ISecurityStation) {
					ISecurityStation tile = (ISecurityStation) tileentity;
					canAccess = tile.canPlayerAccess(AccessType.CONFIGURATION, player);
				}
			}
			if (item.use(stack, player, true)) {
				if (canAccess) {
					int[] p = TomsModUtils.createIntsFromBlockPos(pos);
					player.openGui(CoreInit.modInstance, GuiIDs.configurator.ordinal(), player.world, p[0], p[1], side == null ? -1 : side.ordinal());
				} else {
					TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
				}
				item.use(stack, player, false);
			}
		} else {
			if (te instanceof ICustomConfigurationErrorMessage) {
				ITextComponent[] msg = ((ICustomConfigurationErrorMessage) te).getMessage(player, stack);
				TomsModUtils.sendNoSpam(player, msg);
			} else {
				TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.nothingToConfigure");
			}
			return false;
		}
		return false;
	}

	public static final ConfigurableDevice DUMMY = new ConfigurableDevice(new ItemStack(Blocks.STONE), "...", null);

	public static class ConfigurableDevice {
		private final ItemStack stack;
		private final String name;
		private final EnumFacing side;
		private final IConfigurable te;

		public ConfigurableDevice(ItemStack stack, String name, EnumFacing side) {
			this.stack = stack;
			this.name = name;
			this.side = side;
			this.te = null;
		}

		public ConfigurableDevice(Entry<EnumFacing, IConfigurable> te) {
			this.stack = te.getValue().getStack();
			this.name = te.getValue().getConfigName();
			this.side = te.getKey();
			this.te = te.getValue();
		}

		public ConfigurableDevice(NBTTagCompound tag) {
			this.stack = TomsModUtils.loadItemStackFromNBT(tag.getCompoundTag("stack"));
			this.name = tag.getString("name");
			int f = tag.getInteger("side");
			if (f == -1)
				this.side = null;
			else
				this.side = EnumFacing.VALUES[f];
			this.te = null;
		}

		public ConfigurableDevice(IConfigurable tile, EnumFacing side) {
			this.stack = tile.getStack();
			this.name = tile.getConfigName();
			this.side = side;
			this.te = tile;
		}

		public ItemStack getStack() {
			return stack;
		}

		public String getName() {
			return name;
		}

		public EnumFacing getSide() {
			return side;
		}

		public List<ConfigurableDevice> toList() {
			return Collections.singletonList(this);
		}

		public NBTTagCompound write() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("name", name);
			tag.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
			tag.setInteger("side", side != null ? side.ordinal() : -1);
			return tag;
		}

		public IConfigurable getTile() {
			return te;
		}
	}
}
