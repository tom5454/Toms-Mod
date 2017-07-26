package com.tom.storage.multipart.block;

import static com.tom.api.recipes.RecipeHelper.addRecipe;
import static com.tom.api.recipes.RecipeHelper.addShapelessRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.item.MultipartItem;
import com.tom.api.multipart.BlockDuctBase;
import com.tom.api.multipart.ICustomPartBounds;
import com.tom.api.multipart.PartDuct;
import com.tom.apis.EmptyEntry;
import com.tom.client.CustomModelLoader;
import com.tom.core.CoreInit;
import com.tom.recipes.OreDict;
import com.tom.storage.StorageInit;
import com.tom.storage.client.CableModel;
import com.tom.storage.multipart.PartStorageNetworkCable;
import com.tom.storage.multipart.PartStorageNetworkCable.CableData;

import mcmultipart.api.container.IPartInfo;

public class StorageNetworkCable extends BlockDuctBase implements IModelRegisterRequired {
	/*@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side,
			Vec3d hit, ItemStack stack, EntityPlayer player) {
		Entry<CableType, CableColor> e = getCableData(stack.getMetadata());
		return new PartStorageNetworkCable(e.getKey(), e.getValue());
	}*/

	public StorageNetworkCable() {
		super(-1);
	}

	public static enum CableColor implements IStringSerializable {
		FLUIX(-1, "fluix", 0x6f6fae), WHITE(0, "white", 0xcccccc), ORANGE(1, "orange"), MAGENTA(2, "magenta"), LIGHT_BLUE(3, "lightBlue"), YELLOW(4, "yellow"), LIME(5, "lime"), PINK(6, "pink"), GRAY(7, "gray"), LIGHT_GRAY(8, "lightGray"), CYAN(9, "cyan"), PURPLE(10, "purple"), BLUE(11, "blue", 0x4740f5), BROWN(12, "brown"), GREEN(13, "green"), RED(14, "red"), BLACK(15, "black", 0x404040),;
		public static final CableColor[] VALUES = values();
		private final int meta, color, colorAlt;
		private final String unlocalizedName;

		private CableColor(int meta, String unlocalizedName, int color) {
			this(meta, unlocalizedName, color, color);
		}

		private CableColor(int meta, String unlocalizedName) {
			int color = 0;
			if (meta != -1) {
				color = EnumDyeColor.byMetadata(meta).getMapColor().colorValue;
			}
			this.meta = meta;
			this.unlocalizedName = unlocalizedName;
			this.color = color;
			this.colorAlt = color;
		}

		private CableColor(int meta, String unlocalizedName, int color, int alt) {
			this.meta = meta;
			this.unlocalizedName = unlocalizedName;
			this.color = color;
			this.colorAlt = alt;
		}

		private CableColor(int meta, String unlocalizedName, boolean unused, int alt) {
			int color = 0;
			if (meta != -1) {
				color = EnumDyeColor.byMetadata(meta).getMapColor().colorValue;
			}
			this.meta = meta;
			this.unlocalizedName = unlocalizedName;
			this.color = color;
			this.colorAlt = alt;
		}

		@Override
		public String getName() {
			return this == LIGHT_GRAY ? "silver" : name().toLowerCase();
		}

		public String getDyeMeta() {
			return meta > -1 ? "dye" + unlocalizedName.substring(0, 1).toUpperCase() + unlocalizedName.substring(1) : "";
		}

		public String getUnlocalizedName() {
			return unlocalizedName;
		}

		public int getTint() {
			return color;
		}

		public int getTintAlt() {
			return colorAlt;
		}

		public static CableColor get(int i) {
			return VALUES[Math.abs(i % VALUES.length)];
		}
	}

	public static Entry<StorageNetworkCable.CableType, CableColor> getCableData(int meta) {
		int type = (meta / CableColor.VALUES.length) % StorageNetworkCable.CableType.VALUES.length;
		int color = meta % CableColor.VALUES.length;
		return new EmptyEntry<>(StorageNetworkCable.CableType.VALUES[type], CableColor.VALUES[color]);
	}

	@Override
	public void registerModels() {
		CustomModelLoader.addOverride(new ResourceLocation("tomsmodstorage", getUnlocalizedName().substring(5)), new CableModel());
		Item item = Item.getItemFromBlock(this);
		for (int t = 0;t < StorageNetworkCable.CableType.VALUES.length;t++) {
			for (int c = 0;c < CableColor.VALUES.length;c++) {
				int meta = t * CableColor.VALUES.length + c;
				CoreInit.registerRender(item, meta, "tomsmodstorage:cable/" + item.getUnlocalizedName(new ItemStack(this, 1, meta)).substring(5));
			}
		}
	}

	@Override
	public StorageNetworkCable setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}

	@Override
	public StorageNetworkCable setUnlocalizedName(String unlocalizedName) {
		super.setUnlocalizedName(unlocalizedName);
		return this;
	}

	public void loadRecipes() {
		for (int t = 0;t < StorageNetworkCable.CableType.VALUES.length;t++) {
			addShapelessRecipe(new ItemStack(this, 1, getMeta(StorageNetworkCable.CableType.VALUES[t], CableColor.FLUIX)), new Object[]{Items.WATER_BUCKET, "storageNetworkCableColored_" + StorageNetworkCable.CableType.VALUES[t].getName()});
			addRecipe(new ItemStack(this, 8, getMeta(StorageNetworkCable.CableType.VALUES[t], CableColor.FLUIX)), new Object[]{"CCC", "CWC", "CCC", 'W', Items.WATER_BUCKET, 'C', "storageNetworkCableColored_" + StorageNetworkCable.CableType.VALUES[t].getName()});
			for (int c = 0;c < CableColor.VALUES.length;c++) {
				CableColor color = CableColor.VALUES[c];
				if (color != CableColor.FLUIX) {
					createColorRecipe(StorageNetworkCable.CableType.VALUES[t], color);
				}
			}
		}
	}

	public void loadOreDict() {
		for (int t = 0;t < StorageNetworkCable.CableType.VALUES.length;t++) {
			OreDict.registerOre("storageNetworkCable_" + StorageNetworkCable.CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(StorageNetworkCable.CableType.VALUES[t], CableColor.FLUIX)));
			OreDict.registerOre("storageNetworkCableFluix_" + StorageNetworkCable.CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(StorageNetworkCable.CableType.VALUES[t], CableColor.FLUIX)));
			OreDict.registerOre("storageNetworkCableColorless_" + StorageNetworkCable.CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(StorageNetworkCable.CableType.VALUES[t], CableColor.FLUIX)));
			for (int c = 0;c < CableColor.VALUES.length;c++) {
				CableColor color = CableColor.VALUES[c];
				if (color == CableColor.FLUIX)
					continue;
				OreDict.registerOre("storageNetworkCable_" + StorageNetworkCable.CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(StorageNetworkCable.CableType.VALUES[t], color)));
				OreDict.registerOre("storageNetworkCableColored_" + StorageNetworkCable.CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(StorageNetworkCable.CableType.VALUES[t], color)));
				String colorName = color.getName();
				OreDict.registerOre("storageNetworkCable" + colorName.substring(0, 1).toUpperCase() + colorName.substring(1) + "_" + StorageNetworkCable.CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(StorageNetworkCable.CableType.VALUES[t], color)));
			}
		}
	}

	private void createColorRecipe(StorageNetworkCable.CableType t, CableColor c) {
		addRecipe(new ItemStack(this, 8, getMeta(t, c)), new Object[]{"CCC", "CWC", "CCC", 'W', c.getDyeMeta(), 'C', "storageNetworkCable_" + t.getName()});
	}

	public static int getMeta(StorageNetworkCable.CableType t, CableColor c) {
		return t.ordinal() * CableColor.VALUES.length + c.ordinal();
	}
	// Model Generator
	/*public static void main(String[] args) {
		File outFolder = new File(".", "StorageNetworkCable_Output");
		System.out.println("Folder: " + outFolder.getAbsolutePath());
		String[] colors = new String[]{"Fluix", "White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};
		String[] types = new String[]{"Glass", "Covered", "Smart", "Large Bandwidth"};
		String main = "%t Cable - %c";
		try {
			outFolder.mkdirs();
			PrintWriter lang = new PrintWriter(new FileWriter(new File(outFolder, "lang.lang")));
			for(int t = 0;t<CableType.VALUES.length;t++){
				for(int c = 0;c<CableColor.VALUES.length;c++){
					File f = new File(outFolder, "tm.cable." + CableType.VALUES[t].getName() + "_" + CableColor.VALUES[c].getName() + ".json");
					System.out.println(f.getAbsolutePath());
					String texture = "cable_" + (CableType.VALUES[t] == CableType.NORMAL ? CableColor.VALUES[c].getName() : (CableType.VALUES[t] == CableType.DENSE ? CableType.SMART.getName() + "_" + CableColor.VALUES[c].getName() : (CableType.VALUES[t].getName() + "_" + CableColor.VALUES[c].getName())));
					PrintWriter out = new PrintWriter(new FileWriter(f));
					out.println("{");
					out.println("  \"parent\": \"tomsmodstorage:block/cable_item_" + CableType.VALUES[t].getName() + "\",");
					out.println("  \"textures\": {");
					out.println("    \"duct\": \"tomsmodstorage:blocks/cable/" + texture + "\"");
					out.println("  }");
					out.println("}");
					out.close();
					String loc = main.replace("%t", types[t]).replace("%c", colors[c]);
					lang.println("item.tm.cable." + CableType.VALUES[t].getName() + "_" + CableColor.VALUES[c].getName() + ".name=" + loc);
				}
			}
			lang.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	@Override
	public PartDuct<?> createNewTileEntity(World worldIn, int meta) {
		return new PartStorageNetworkCable();
	}

	@Override
	public ItemBlock createItemBlock() {
		ItemBlock b = new MultipartItem(this) {
			/**
			 * returns a list of items with the same ID, but different meta (eg:
			 * dye returns 16 items)
			 */
			@Override
			@SideOnly(Side.CLIENT)
			public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
				for (int t = 0;t < StorageNetworkCable.CableType.VALUES.length;t++) {
					for (int c = 0;c < CableColor.VALUES.length;c++) {
						subItems.add(new ItemStack(itemIn, 1, t * CableColor.VALUES.length + c));
					}
				}
			}

			@Override
			public String getUnlocalizedName(ItemStack stack) {
				Entry<StorageNetworkCable.CableType, CableColor> e = getCableData(stack.getMetadata());
				return super.getUnlocalizedName(stack) + "." + e.getKey().getName() + "_" + e.getValue().getName();
			}
		};
		b.setHasSubtypes(true);
		return b;
	}

	public static final UnlistedPropertyData DATA = new UnlistedPropertyData("data");

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos, PartDuct<?> duct) {
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState s = (IExtendedBlockState) state;
			/*state = s.withProperty(TYPE, type).withProperty(COLOR, color)
					.withProperty(DOWN, getPropertyValue(EnumFacing.DOWN))
					.withProperty(UP, getPropertyValue(EnumFacing.UP))
					.withProperty(NORTH, getPropertyValue(EnumFacing.NORTH))
					.withProperty(SOUTH, getPropertyValue(EnumFacing.SOUTH))
					.withProperty(WEST, getPropertyValue(EnumFacing.WEST))
					.withProperty(EAST, getPropertyValue(EnumFacing.EAST))
					.withProperty(CHANNEL, channel);*/
			return s.withProperty(DATA, ((PartStorageNetworkCable) duct).getData());
		}
		return state;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos, PartDuct<?> duct) {
		return state;
	}

	public static class UnlistedPropertyData implements IUnlistedProperty<CableData> {

		private final String name;

		public UnlistedPropertyData(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isValid(CableData value) {
			return value != null;
		}

		@Override
		public Class<CableData> getType() {
			return CableData.class;
		}

		@Override
		public String valueToString(CableData value) {
			return value.getName();
		}
	}

	public static enum CableType implements IStringSerializable {
		NORMAL(2D / 16D), COVERED(3D / 16D), SMART(3D / 16D), DENSE(5D / 16D);
		public static final CableType[] VALUES = values();
		private final double size;

		private CableType(double size) {
			this.size = size;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		public double getSize() {
			return size;
		}
	}

	@Override
	protected IUnlistedProperty<?>[] getUnlistedProperties() {
		// return new IUnlistedProperty<?>[]{TYPE, COLOR, UP, DOWN, NORTH,
		// SOUTH, EAST, WEST, CHANNEL};
		return new IUnlistedProperty<?>[]{DATA};
	}

	@Override
	protected IProperty<?>[] getProperties() {
		return new IProperty<?>[0];
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		PartStorageNetworkCable duct = (PartStorageNetworkCable) world.getTileEntity(pos);
		return new ItemStack(StorageInit.cable, 1, StorageNetworkCable.getMeta(duct.type, duct.color));
	}

	@Override
	public ItemStack getPickPart(IPartInfo part, RayTraceResult hit, EntityPlayer player) {
		PartStorageNetworkCable duct = (PartStorageNetworkCable) part.getTile();
		return new ItemStack(StorageInit.cable, 1, StorageNetworkCable.getMeta(duct.type, duct.color));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		Entry<StorageNetworkCable.CableType, CableColor> e = getCableData(stack.getMetadata());
		((PartStorageNetworkCable) worldIn.getTileEntity(pos)).init(e.getKey(), e.getValue());
	}

	@Override
	public void onPartPlacedBy(IPartInfo part, EntityLivingBase placer, ItemStack stack) {
		Entry<StorageNetworkCable.CableType, CableColor> e = getCableData(stack.getMetadata());
		((PartStorageNetworkCable) part.getTile()).init(e.getKey(), e.getValue());
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(StorageInit.cable);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IPartInfo part, int fortune) {
		PartStorageNetworkCable duct = (PartStorageNetworkCable) part.getTile();
		return Collections.singletonList(new ItemStack(StorageInit.cable, 1, StorageNetworkCable.getMeta(duct.type, duct.color)));
	}

	@Override
	public List<ItemStack> getWrenchDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune, PartDuct<?> ductIn) {
		PartStorageNetworkCable duct = (PartStorageNetworkCable) ductIn;
		return Collections.singletonList(new ItemStack(StorageInit.cable, 1, StorageNetworkCable.getMeta(duct.type, duct.color)));
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		PartStorageNetworkCable duct = (PartStorageNetworkCable) worldIn.getTileEntity(pos);
		if (!player.capabilities.isCreativeMode)
			spawnAsEntity(worldIn, pos, new ItemStack(StorageInit.cable, 1, StorageNetworkCable.getMeta(duct.type, duct.color)));
	}

	@Override
	public void onPartHarvested(IPartInfo part, EntityPlayer player) {
		PartStorageNetworkCable duct = (PartStorageNetworkCable) part.getTile();
		if (!player.capabilities.isCreativeMode)
			spawnAsEntity(part.getActualWorld(), part.getPartPos(), new ItemStack(StorageInit.cable, 1, StorageNetworkCable.getMeta(duct.type, duct.color)));
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
	}

	@Override
	public void addCollisionBoxToList(IPartInfo part, AxisAlignedBB bbIn, List<AxisAlignedBB> list, Entity entity, boolean unused) {
		PartStorageNetworkCable duct = (PartStorageNetworkCable) part.getTile();
		BlockPos pos = duct.getPos();
		AxisAlignedBB mask = bbIn.offset(-pos.getX(), -pos.getY(), -pos.getZ());
		if (duct.BOXES[6].intersects(mask)) {
			list.add(duct.BOXES[6].offset(pos));
		}
		for (EnumFacing f : EnumFacing.VALUES) {
			if (duct.BOXES[f.ordinal()].intersects(mask)) {
				if ((duct.connects(f) || duct.connectsInv(f) || duct.connectsE1(f) || duct.connectsE2(f)))
					list.add(duct.BOXES[f.ordinal()].offset(pos));
				else if (duct.connectsM(f)) {
					AxisAlignedBB b = PartDuct.rotateFace(duct.module_connection, f);
					if (b.intersects(mask))
						list.add(b.offset(pos));
				}
			}
			if (duct.connectsInv(f) && this instanceof ICustomPartBounds) {
				AxisAlignedBB b = PartDuct.rotateFace(((ICustomPartBounds) this).getBoxForConnect(), f);
				if (b.intersects(mask))
					list.add(b.offset(pos));
			}
		}
	}
}
