package com.tom.thirdparty.theoneprobe;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.google.common.base.Function;

import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyStorageTile;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.config.Config;
import com.tom.energy.EnergyInit;
import com.tom.lib.Configs;
import com.tom.storage.tileentity.TMTank;
import com.tom.thirdparty.waila.IIntegratedMultimeter;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.apiimpl.client.ElementTextRender;
import mcjty.theoneprobe.network.NetworkTools;

public class TheOneProbeHandler implements Function<ITheOneProbe, Void>, IProbeInfoProvider {
	public static ITheOneProbe theOneProbeImp;

	@Override
	public Void apply(ITheOneProbe input) {
		theOneProbeImp = input;
		theOneProbeImp.registerProvider(this);
		TranslatedText.ID = theOneProbeImp.registerElementFactory(new TranslatedText.Factory());
		return null;
	}

	public static class TranslatedText implements IElement {
		public static class Factory implements IElementFactory {

			@Override
			public IElement createElement(ByteBuf buf) {
				return new TranslatedText(buf);
			}

		}

		private final String text;
		private final Object[] args;
		public static int ID;

		public TranslatedText(String text, Object... objects) {
			this.text = text;
			this.args = objects;
		}

		private static final String VALUE = "v";

		public TranslatedText(ByteBuf buf) {
			String text = NetworkTools.readStringUTF8(buf);
			NBTTagList list = ByteBufUtils.readTag(buf).getTagList("l", 10);
			args = new Object[list.tagCount()];
			for (int i = 0;i < list.tagCount();i++) {
				NBTTagCompound t = list.getCompoundTagAt(i);
				int j = t.getInteger("i");
				switch (t.getTagId(VALUE)) {
				case NBT.TAG_BYTE:
					args[j] = t.getByte(VALUE);
					break;
				case NBT.TAG_SHORT:
					args[j] = t.getShort(VALUE);
					break;
				case NBT.TAG_INT:
					args[j] = t.getInteger(VALUE);
					break;
				case NBT.TAG_LONG:
					args[j] = t.getLong(VALUE);
					break;
				case NBT.TAG_FLOAT:
					args[j] = t.getFloat(VALUE);
					break;
				case NBT.TAG_DOUBLE:
					args[j] = t.getDouble(VALUE);
					break;
				case NBT.TAG_STRING:
					args[j] = t.getString(VALUE);
					break;
				default:
					args[j] = t.getTag(VALUE).toString();
					break;
				}
			}
			this.text = I18n.format(text, args);
		}

		@Override
		public void render(int x, int y) {
			ElementTextRender.render(text, x, y);
		}

		@Override
		public int getWidth() {
			return ElementTextRender.getWidth(text);
		}

		@Override
		public int getHeight() {
			return 10;
		}

		@Override
		public void toBytes(ByteBuf buf) {
			NetworkTools.writeStringUTF8(buf, text);
			NBTTagList list = new NBTTagList();
			for (int i = 0;i < args.length;i++) {
				NBTTagCompound t = new NBTTagCompound();
				t.setByte("i", (byte) i);
				Object o = args[i];
				if (o == null)
					t.setString(VALUE, "null");
				else if (o instanceof Byte)
					t.setByte(VALUE, (byte) o);
				else if (o instanceof Short)
					t.setShort(VALUE, (short) o);
				else if (o instanceof Integer)
					t.setInteger(VALUE, (int) o);
				else if (o instanceof Long)
					t.setLong(VALUE, (long) o);
				else if (o instanceof Float)
					t.setFloat(VALUE, (float) o);
				else if (o instanceof Double)
					t.setDouble(VALUE, (double) o);
				else
					t.setString(VALUE, o.toString());
				list.appendTag(t);
			}
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("l", list);
			ByteBufUtils.writeTag(buf, tag);
		}

		@Override
		public int getID() {
			return ID;
		}
	}

	@Override
	public String getID() {
		return Configs.Modid;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		//Block block = blockState.getBlock();
		TileEntity tile = world.getTileEntity(data.getPos());
		EnumFacing side = data.getSideHit();
		if (tile instanceof TileEntityTomsMod) {
			TileEntityTomsMod te = (TileEntityTomsMod) tile;
			if (te.isTickSpeeded()) {
				probeInfo.vertical().text(TextStyleClass.ERROR + "" + TextFormatting.OBFUSCATED + "!!!" + TextFormatting.RESET + " " + TextFormatting.RED + "" + TextFormatting.BOLD + translate("tomsMod.waila.speedingWarn") + " " + TextFormatting.OBFUSCATED + "!!!").vertical().text(TextStyleClass.ERROR + translate("tomsMod.waila.speedingDisabledInfo"));
			}
			if (te instanceof TMTank) {
				TMTank tank = (TMTank) te;
				if (hasMultimeter(player, te)) {
					FluidStack s = tank.getStack();
					String name;
					try {
						name = translate(s.getFluid().getUnlocalizedName(s));
					} catch (NullPointerException f) {
						name = translate("tomsMod.waila.empty");
					}
					probeInfo.vertical().text(name);
					if (s != null) {
						probeInfo.vertical().element(new TranslatedText("tomsMod.waila.fluidStored", s.amount, tank.getCapacity()));
					} else {
						probeInfo.vertical().element(new TranslatedText("tomsMod.waila.capacity", tank.getCapacity()));
					}
				} else {
					probeInfo.vertical().element(new TranslatedText("tomsMod.waila.capacity", tank.getCapacity()));
				}
			} else if (te instanceof IEnergyStorageTile) {
				if (hasMultimeter(player, te)) {
					IEnergyStorageTile s = (IEnergyStorageTile) te;
					List<EnergyType> eL = s.getValidEnergyTypes();
					if (eL != null) {
						for (int i = 0;i < eL.size();i++) {
							EnergyType c = eL.get(i);
							double energyStored = s.getEnergyStored(side, c);
							long maxEnergyStored = s.getMaxEnergyStored(side, c);
							probeInfo.horizontal().text(translate("tomsMod.top.energyType")).text(" " + c.getColor() + c.toString());
							probeInfo.progress(MathHelper.floor(energyStored), maxEnergyStored, probeInfo.defaultProgressStyle().alternateFilledColor(0xFF11d7fe).filledColor(0xFF00abce));
						}
					}
				}
			}
		}
	}

	public static boolean hasMultimeter(EntityPlayer player, TileEntity te) {
		InventoryPlayer inv = player.inventory;
		boolean found = !Config.wailaUsesMultimeterForce || te instanceof IIntegratedMultimeter;
		if (!found) {
			for (int i = 0;i < 9 && i < inv.getSizeInventory();i++) {
				ItemStack s = inv.getStackInSlot(i);
				if (!s.isEmpty() && s.getItem() == EnergyInit.multimeter) {
					found = true;
					break;
				}
			}
		}
		return found;
	}

	private static String translate(String in) {
		return IProbeInfo.STARTLOC + in + IProbeInfo.ENDLOC;
	}
}
