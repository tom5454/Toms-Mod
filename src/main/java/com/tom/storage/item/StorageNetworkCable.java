package com.tom.storage.item;

import static com.tom.api.recipes.RecipeHelper.addRecipe;
import static com.tom.api.recipes.RecipeHelper.addShapelessRecipe;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IIconRegisterRequired;
import com.tom.api.block.IMethod.IClientMethod;
import com.tom.api.block.IRegisterRequired;
import com.tom.api.item.MultipartItem;
import com.tom.apis.EmptyEntry;
import com.tom.client.CustomModelLoader;
import com.tom.core.CoreInit;
import com.tom.recipes.OreDict;
import com.tom.storage.client.CableModel;
import com.tom.storage.multipart.PartStorageNetworkCable;

import mcmultipart.multipart.IMultipart;

public class StorageNetworkCable extends MultipartItem implements IRegisterRequired, IClientMethod, IIconRegisterRequired{
	public StorageNetworkCable() {
		setHasSubtypes(true);
	}

	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side,
			Vec3d hit, ItemStack stack, EntityPlayer player) {
		Entry<CableType, CableColor> e = getCableData(stack.getMetadata());
		return new PartStorageNetworkCable(e.getKey(), e.getValue());
	}

	@Override
	public void register() {
		CoreInit.proxy.runMethod(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void exec() {
		CustomModelLoader.addOverride(new ModelResourceLocation(new ResourceLocation("tomsmodstorage", "tm.cable"), "multipart"), new CableModel());
	}
	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		for(int t = 0;t<CableType.VALUES.length;t++){
			for(int c = 0;c<CableColor.VALUES.length;c++){
				subItems.add(new ItemStack(itemIn, 1, t * CableColor.VALUES.length + c));
			}
		}
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		Entry<CableType, CableColor> e = getCableData(stack.getMetadata());
		return super.getUnlocalizedName(stack) + "." + e.getKey().getName() + "_" + e.getValue().getName();
	}
	public static enum CableColor implements IStringSerializable{
		FLUIX     (-1, "fluix", 0x6f6fae),
		WHITE     (0,  "white"),
		ORANGE    (1,  "orange"),
		MAGENTA   (2,  "magenta"),
		LIGHT_BLUE(3,  "light_blue"),
		YELLOW    (4,  "yellow"),
		LIME      (5,  "lime"),
		PINK      (6,  "pink"),
		GRAY      (7,  "gray"),
		SILVER    (8,  "silver"),
		CYAN      (9,  "cyan"),
		PURPLE    (10, "purple"),
		BLUE      (11, "blue"),
		BROWN     (12, "brown"),
		GREEN     (13, "green"),
		RED       (14, "red"),
		BLACK     (15, "black"),
		;
		public static final CableColor[] VALUES = values();
		private final int meta, color;
		private final String unlocalizedName;
		private CableColor(int meta, String unlocalizedName, int color){
			this.meta = meta;
			this.unlocalizedName = unlocalizedName;
			this.color = color;
		}
		private CableColor(int meta, String unlocalizedName){
			int color = 0;
			if(meta != -1){
				color = EnumDyeColor.byMetadata(meta).getMapColor().colorValue;
			}
			this.meta = meta;
			this.unlocalizedName = unlocalizedName;
			this.color = color;
		}
		@Override
		public String getName() {
			return name().toLowerCase();
		}
		public String getDyeMeta() {
			return meta > -1 ? "dye" + unlocalizedName.substring(0, 1).toUpperCase() + unlocalizedName.substring(1) : "";
		}
		public String getUnlocalizedName() {
			return unlocalizedName;
		}
		public int getTint(){
			return color;
		}
	}
	public static enum CableType implements IStringSerializable{
		NORMAL(2D/16D),
		COVERED(3D/16D),
		SMART(3D/16D),
		DENSE(4D/16D)
		;
		public static final CableType[] VALUES = values();
		private final double size;

		private CableType(double size) {
			this.size = size;
		}
		@Override
		public String getName() {
			return name().toLowerCase();
		}
		public double getSize(){
			return size;
		}
	}
	public static Entry<CableType, CableColor> getCableData(int meta){
		int type = (meta / CableColor.VALUES.length) % CableType.VALUES.length;
		int color = meta % CableColor.VALUES.length;
		return new EmptyEntry<CableType, CableColor>(CableType.VALUES[type], CableColor.VALUES[color]);
	}

	@Override
	public void registerIcons() {
		for(int t = 0;t<CableType.VALUES.length;t++){
			for(int c = 0;c<CableColor.VALUES.length;c++){
				int meta = t * CableColor.VALUES.length + c;
				CoreInit.registerRender(this, meta, "tomsmodstorage:cable/" + getUnlocalizedName(new ItemStack(this, 1, meta)).substring(5));
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
	public void loadRecipes(){
		for(int t = 0;t<CableType.VALUES.length;t++){
			addShapelessRecipe(new ItemStack(this, 1, getMeta(CableType.VALUES[t], CableColor.FLUIX)), new Object[]{Items.WATER_BUCKET, "storageNetworkCableColored_" + CableType.VALUES[t].getName()});
			addRecipe(new ItemStack(this, 8, getMeta(CableType.VALUES[t], CableColor.FLUIX)), new Object[]{"CCC", "CWC", "CCC", 'W', Items.WATER_BUCKET, 'C', "storageNetworkCableColored_" + CableType.VALUES[t].getName()});
			for(int c = 0;c<CableColor.VALUES.length;c++){
				CableColor color = CableColor.VALUES[c];
				if(color != CableColor.FLUIX){
					createColorRecipe(CableType.VALUES[t], color);
				}
			}
		}
	}
	public void loadOreDict(){
		for(int t = 0;t<CableType.VALUES.length;t++){
			OreDict.registerOre("storageNetworkCable_" + CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(CableType.VALUES[t], CableColor.FLUIX)));
			OreDict.registerOre("storageNetworkCableFluix_" + CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(CableType.VALUES[t], CableColor.FLUIX)));
			OreDict.registerOre("storageNetworkCableColorless_" + CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(CableType.VALUES[t], CableColor.FLUIX)));
			for(int c = 0;c<CableColor.VALUES.length;c++){
				CableColor color = CableColor.VALUES[c];
				if(color == CableColor.FLUIX)continue;
				OreDict.registerOre("storageNetworkCable_" + CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(CableType.VALUES[t], color)));
				OreDict.registerOre("storageNetworkCableColored_" + CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(CableType.VALUES[t], color)));
				String colorName = color.getName();
				OreDict.registerOre("storageNetworkCable" + colorName.substring(0, 1).toUpperCase() + colorName.substring(1) + "_" + CableType.VALUES[t].getName(), new ItemStack(this, 1, getMeta(CableType.VALUES[t], color)));
			}
		}
	}
	private void createColorRecipe(CableType t, CableColor c){
		addRecipe(new ItemStack(this, 8, getMeta(t, c)), new Object[]{"CCC", "CWC", "CCC", 'W', c.getDyeMeta(), 'C', "storageNetworkCable_" + t.getName()});
	}
	public static int getMeta(CableType t, CableColor c){
		return t.ordinal() * CableColor.VALUES.length + c.ordinal();
	}
	//Model Generator
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
}
