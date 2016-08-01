package com.tom.defense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tom.api.item.MultipartItem;
import com.tom.core.CoreInit;
import com.tom.defense.block.BlockForceField;
import com.tom.defense.block.DefenseStation;
import com.tom.defense.block.FieldProjector;
import com.tom.defense.block.ForceCapacitor;
import com.tom.defense.block.ForceConverter;
import com.tom.defense.block.SecurityStation;
import com.tom.defense.item.ForceDuct;
import com.tom.defense.item.IdentityCard;
import com.tom.defense.item.ItemFieldUpgrade;
import com.tom.defense.item.ItemFieldUpgrade.UpgradeType;
import com.tom.defense.item.ItemMultiTool;
import com.tom.defense.item.ItemProjectorFieldType;
import com.tom.defense.item.ItemProjectorFieldType.FieldType;
import com.tom.defense.multipart.PartForceDuct;
import com.tom.defense.tileentity.TileEntityDefenseStation;
import com.tom.defense.tileentity.TileEntityForceCapacitor;
import com.tom.defense.tileentity.TileEntityForceConverter;
import com.tom.defense.tileentity.TileEntityForceField;
import com.tom.defense.tileentity.TileEntityForceFieldProjector;
import com.tom.defense.tileentity.TileEntitySecurityStation;
import com.tom.handler.WorldHandler;
import com.tom.lib.Configs;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = DefenseInit.modid,name = "Tom's Mod Defense",version = Configs.version, dependencies = Configs.coreDependencies)
public class DefenseInit {
	public static final String modid = Configs.Modid + "|Defense";
	public static Logger log = LogManager.getLogger(modid);

	public static ItemMultiTool multiTool;
	public static IdentityCard identityCard;
	public static MultipartItem forceDuct;
	public static Item forceDuctEmpty, rangeUpgrade, rangeWidthUpgrade, rangeHeightUpgrade, projectorLens, projectorFieldType, fieldUpgrade, efficiencyUpgrade;

	public static Block forceConverter, forceCapacitor, securityStation, fieldProjector, blockForce, defenseStation;
	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent){
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		multiTool = new ItemMultiTool();
		identityCard = new IdentityCard();
		forceDuct = new ForceDuct().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.forceDuct");
		forceConverter = new ForceConverter().setCreativeTab(tabTomsModDefense).setUnlocalizedName("forceTransformer");
		forceDuctEmpty = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.forceDuct_e");
		forceCapacitor = new ForceCapacitor().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.forceCapacitor");
		rangeUpgrade = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.rangeUpgrade");
		securityStation = new SecurityStation().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.securityStation");
		rangeWidthUpgrade = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.rangeWidthUpgrade");
		rangeHeightUpgrade = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.rangeHeightUpgrade");
		projectorLens = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.projectorLens").setMaxStackSize(1);
		projectorFieldType = new ItemProjectorFieldType().setCreativeTab(tabTomsModDefense).setUnlocalizedName("projectorFieldTypeController").setMaxStackSize(1).setHasSubtypes(true).setMaxDamage(0);
		fieldUpgrade = new ItemFieldUpgrade().setCreativeTab(tabTomsModDefense).setUnlocalizedName("projectorUpgrade").setHasSubtypes(true).setMaxDamage(0);
		fieldProjector = new FieldProjector().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tmd.fieldProjector");
		blockForce = new BlockForceField().setBlockUnbreakable().setResistance(18000000F).setHardness(-1F).setUnlocalizedName("tmd.force");
		efficiencyUpgrade = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.efficiencyUpgrade");
		defenseStation = new DefenseStation().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tmd.defenseStation");
		CoreInit.addItemToGameRegistry(multiTool, multiTool.getUnlocalizedName().substring(5));
		CoreInit.addItemToGameRegistry(identityCard, identityCard.getUnlocalizedName().substring(5));
		registerItem(forceDuctEmpty, forceDuctEmpty.getUnlocalizedName().substring(5));
		registerItem(rangeUpgrade, rangeUpgrade.getUnlocalizedName().substring(5));
		registerItem(rangeWidthUpgrade, rangeWidthUpgrade.getUnlocalizedName().substring(5));
		registerItem(rangeHeightUpgrade, rangeHeightUpgrade.getUnlocalizedName().substring(5));
		registerItem(projectorLens, projectorLens.getUnlocalizedName().substring(5));
		registerItem(efficiencyUpgrade, efficiencyUpgrade.getUnlocalizedName().substring(5));
		CoreInit.addItemToGameRegistry(projectorFieldType, projectorFieldType.getUnlocalizedName().substring(5));
		CoreInit.addItemToGameRegistry(fieldUpgrade, fieldUpgrade.getUnlocalizedName().substring(5));
		CoreInit.registerMultipart(forceDuct, PartForceDuct.class, "forceDuct", "tomsmoddefense");
		registerBlock(forceConverter, forceConverter.getUnlocalizedName().substring(5));
		registerBlock(forceCapacitor, forceCapacitor.getUnlocalizedName().substring(5));
		registerBlock(securityStation, securityStation.getUnlocalizedName().substring(5));
		registerBlock(fieldProjector, fieldProjector.getUnlocalizedName().substring(5));
		registerBlock(blockForce, blockForce.getUnlocalizedName().substring(5));
		registerBlock(defenseStation, defenseStation.getUnlocalizedName().substring(5));
		GameRegistry.registerTileEntity(TileEntityForceConverter.class, Configs.Modid + ":forceTransformer");
		GameRegistry.registerTileEntity(TileEntityForceCapacitor.class, Configs.Modid + ":forceCapacitor");
		GameRegistry.registerTileEntity(TileEntitySecurityStation.class, Configs.Modid + ":securityStation");
		GameRegistry.registerTileEntity(TileEntityForceFieldProjector.class, Configs.Modid + ":FFProjector");
		GameRegistry.registerTileEntity(TileEntityForceField.class, Configs.Modid + ":forceField");
		GameRegistry.registerTileEntity(TileEntityDefenseStation.class, Configs.Modid + ":defenseStation");
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in "+time+" milliseconds");
	}
	public static CreativeTabs tabTomsModDefense = new CreativeTabs("tabTomsModDefense"){

		@Override
		public Item getTabIconItem() {return Items.APPLE;}
		@Override
		public ItemStack getIconItemStack(){
			//isInCreativeTabIcon
			ItemStack is = new ItemStack(multiTool,1,2);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("isInCreativeTabIcon", true);
			is.setTagCompound(tag);
			return is;
		}

	};
	public static void registerItem(Item item, String registerName){
		CoreInit.itemList.add(item);
		CoreInit.addItemToGameRegistry(item, registerName);
	}
	@SideOnly(Side.CLIENT)
	public static void registerRenders(){
		log.info("Loading Renderers");
		CoreInit.registerRender(multiTool, 0, "tomsmoddefense:"+multiTool.getUnlocalizedName(new ItemStack(multiTool,1,0)).substring(5));
		CoreInit.registerRender(multiTool, 1, "tomsmoddefense:"+multiTool.getUnlocalizedName(new ItemStack(multiTool,1,1)).substring(5));
		CoreInit.registerRender(multiTool, 2, "tomsmoddefense:"+multiTool.getUnlocalizedName(new ItemStack(multiTool,1,2)).substring(5));
		CoreInit.registerRender(multiTool, 3, "tomsmoddefense:"+multiTool.getUnlocalizedName(new ItemStack(multiTool,1,3)).substring(5));
		CoreInit.registerRender(multiTool, 4, "tomsmoddefense:"+multiTool.getUnlocalizedName(new ItemStack(multiTool,1,4)).substring(5));
		CoreInit.registerRender(identityCard, 0, "tomsmoddefense:tmd.idCard_b");
		CoreInit.registerRender(identityCard, 1, "tomsmoddefense:tmd.idCard");
		CoreInit.registerRender(identityCard, 2, "tomsmoddefense:tmd.idCard_p");
		CoreInit.registerRender(identityCard, 3, "tomsmoddefense:tmd.idCard_s");
		CoreInit.registerRender(projectorFieldType, 0, "tomsmoddefense:projectorModule_"+FieldType.get(0).getName());
		CoreInit.registerRender(projectorFieldType, 1, "tomsmoddefense:projectorModule_"+FieldType.get(1).getName());
		CoreInit.registerRender(projectorFieldType, 2, "tomsmoddefense:projectorModule_"+FieldType.get(2).getName());
		CoreInit.registerRender(projectorFieldType, 3, "tomsmoddefense:projectorModule_"+FieldType.get(3).getName());
		CoreInit.registerRender(fieldUpgrade, 0, "tomsmoddefense:projectorUpgrade_"+UpgradeType.get(0).getName());
		CoreInit.registerRender(fieldUpgrade, 1, "tomsmoddefense:projectorUpgrade_"+UpgradeType.get(1).getName());
		CoreInit.registerRender(fieldUpgrade, 2, "tomsmoddefense:projectorUpgrade_"+UpgradeType.get(2).getName());
		CoreInit.registerRender(fieldUpgrade, 3, "tomsmoddefense:projectorUpgrade_"+UpgradeType.get(3).getName());
	}
	public static void registerPlaceables(){
		WorldHandler.addPlaceable(Blocks.LEVER);
		WorldHandler.addPlaceable(Blocks.STONE_BUTTON);
		WorldHandler.addPlaceable(Blocks.WOODEN_BUTTON);
		WorldHandler.addPlaceable(Blocks.COBBLESTONE);
		WorldHandler.addPlaceable(Blocks.REDSTONE_BLOCK);
	}
	public static void registerBlock(Block block, String name) {
		CoreInit.addBlockToGameRegistry(block, name);
		Item item = Item.getItemFromBlock(block);
		CoreInit.itemList.add(item);
		CoreInit.blockList.add(block);
	}
	@EventHandler
	public static void construction(FMLConstructionEvent event){
		CoreInit.modids.add(modid);
	}
}
