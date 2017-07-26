package com.tom.api.research;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.oredict.OreDictionary;

import com.tom.recipes.OreDict;

public interface IScanningInformation {
	boolean equals(IScanningInformation other);

	void writeToNBTInternal(NBTTagCompound tag);

	IScanningInformation create(NBTTagCompound tag);

	String getUnlocalizedName();

	void addTooltip(List<String> textLines, String name, boolean shift, boolean advancedItemTooltips);

	public static class ScanningInformation implements IScanningInformation {
		private Block block;
		private int meta;
		private String unloc;

		public ScanningInformation(Block block, int meta, String unloc) {
			this.block = block;
			this.meta = meta;
			this.unloc = unloc;
		}

		@Override
		public boolean equals(IScanningInformation other) {
			if (other == null)
				return false;
			if (other instanceof ScanningInformation) {
				ScanningInformation info = (ScanningInformation) other;
				return info.block == block && (info.meta == meta || info.meta == -1 || meta == -1);
			}
			if (other instanceof OredictScanningInformation)
				return other.equals(this);
			return !(other instanceof OredictScanningInformation || other instanceof ScanningInformation) && other.equals(this);
		}

		@Override
		public void writeToNBTInternal(NBTTagCompound tag) {
			ResourceLocation b1 = block.delegate.name();
			tag.setString("blockName", b1.getResourcePath());
			tag.setString("modid", b1.getResourceDomain());
			tag.setInteger("meta", meta);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof IScanningInformation) { return this.equals((IScanningInformation) other); }
			return false;
		}

		@Override
		public IScanningInformation create(NBTTagCompound tag) {
			int meta = tag.getInteger("meta");
			Block block = Block.REGISTRY.getObject(new ResourceLocation(tag.getString("modid"), tag.getString("blockName")));
			if (block != null)
				return new ScanningInformation(block, meta, "");
			return null;
		}

		@Override
		public String getUnlocalizedName() {
			return block.getUnlocalizedName() + ".name";
		}

		@Override
		public void addTooltip(List<String> textLines, String name, boolean shift, boolean advancedItemTooltips) {
			if (advancedItemTooltips) {
				if (meta == -1)
					textLines.add("|  " + name + " " + TextFormatting.GRAY + block.delegate.name().toString());
				else
					textLines.add("|  " + name + ":" + meta + " " + TextFormatting.GRAY + block.delegate.name().toString());
			} else {
				if (meta == -1)
					textLines.add("|  " + name);
				else
					textLines.add("|  " + name + ":" + meta);
			}
		}

	}

	public static class OredictScanningInformation implements IScanningInformation {
		private String id;

		public OredictScanningInformation(String id) {
			this.id = id;
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean equals(IScanningInformation other) {
			if (other == null)
				return false;
			if (other instanceof OredictScanningInformation)
				return ((OredictScanningInformation) other).id == id;
			else if (other instanceof ScanningInformation) {
				ScanningInformation info = (ScanningInformation) other;
				IBlockState state = info.block.getStateFromMeta(info.meta);
				int stackMeta = state.getBlock().damageDropped(state);
				ItemStack stack = new ItemStack(state.getBlock(), 1, stackMeta);
				if (!stack.isEmpty()) { return OreDict.isOre(stack, id); }
			}
			return false;
		}

		@Override
		public void writeToNBTInternal(NBTTagCompound tag) {
			tag.setString("id", id);
		}

		@Override
		public IScanningInformation create(NBTTagCompound tag) {
			int meta = tag.getInteger("meta");
			Block block = Block.REGISTRY.getObject(new ResourceLocation(tag.getString("modid"), tag.getString("blockName")));
			if (block != null)
				return new ScanningInformation(block, meta, "");
			return null;
		}

		@Override
		public String getUnlocalizedName() {
			return "tomsMod.oredict";
		}

		@Override
		public void addTooltip(List<String> textLines, String name, boolean shift, boolean advancedItemTooltips) {
			NonNullList<ItemStack> ores = OreDictionary.getOres(name);
			textLines.add(name + ":" + id);
			if (shift) {
				ores.stream().map(ItemStack::getDisplayName).forEach(s -> textLines.add("|  " + s));
			}
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof IScanningInformation) { return this.equals((IScanningInformation) other); }
			return false;
		}
	}

	public static class Handler {
		private static final RegistryNamespaced<ResourceLocation, Class<? extends IScanningInformation>> REGISTRY = new RegistryNamespaced<>();
		private static final Map<ResourceLocation, IScanningInformation> READER = new HashMap<>();

		public static IScanningInformation fromNBT(NBTTagCompound tag) {
			String type = tag.getString("type");
			IScanningInformation info = READER.get(new ResourceLocation(type));
			if (info != null) { return info.create(tag); }
			return null;
		}

		public static NBTTagCompound writeToNBT(NBTTagCompound tag, IScanningInformation info) {
			ResourceLocation s = REGISTRY.getNameForObject(info.getClass());
			if (s == null) {
				throw new RuntimeException(info.getClass() + " is missing a mapping! This is a bug!");
			} else {
				info.writeToNBTInternal(tag);
				tag.setString("type", s.toString());
				return tag;
			}
		}

		public static <T extends IScanningInformation> void register(ResourceLocation loc, Class<T> clazz, T reader) {
			REGISTRY.putObject(loc, clazz);
			READER.put(loc, reader);
		}

		static {
			register(new ResourceLocation("tm:base"), ScanningInformation.class, new ScanningInformation(null, 0, ""));
			register(new ResourceLocation("tm:oredict"), OredictScanningInformation.class, new OredictScanningInformation(""));
		}
	}
}
