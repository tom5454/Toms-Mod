package com.tom.defense;

import static com.tom.core.CoreInit.registerBlock;
import static com.tom.core.CoreInit.registerItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockMatcher;
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
import com.tom.defense.item.ItemMultiTool;
import com.tom.defense.item.ItemProjectorFieldType;
import com.tom.defense.multipart.PartForceDuct;
import com.tom.defense.tileentity.TileEntityDefenseStation;
import com.tom.defense.tileentity.TileEntityForceCapacitor;
import com.tom.defense.tileentity.TileEntityForceConverter;
import com.tom.defense.tileentity.TileEntityForceField;
import com.tom.defense.tileentity.TileEntityForceFieldProjector;
import com.tom.defense.tileentity.TileEntitySecurityStation;
import com.tom.handler.WorldHandler;
import com.tom.lib.Configs;
import com.tom.worldgen.WorldGen;

import com.tom.core.block.BlockOre;

@Mod(modid = DefenseInit.modid,name = "Tom's Mod Defense",version = Configs.version, dependencies = Configs.coreDependencies)
public class DefenseInit {
	public static final String modid = Configs.Modid + "|Defense";
	public static Logger log = LogManager.getLogger(modid);

	public static ItemMultiTool multiTool;
	public static IdentityCard identityCard;
	public static MultipartItem forceDuct;
	public static Item forceDuctEmpty, rangeUpgrade, rangeWidthUpgrade, rangeHeightUpgrade, projectorLens, projectorFieldType, fieldUpgrade, efficiencyUpgrade;

	public static Block forceConverter, forceCapacitor, securityStation, fieldProjector, blockForce, defenseStation, oreMonazit;

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
		oreMonazit = new BlockOre(5, WorldGen.OVERWORLD, 2, 2).addExtraState(WorldGen.END, 55, 12, BlockMatcher.forBlock(Blocks.END_STONE), "end").setUnlocalizedName("oreMonazit");
		registerItem(multiTool, multiTool.getUnlocalizedName().substring(5));
		registerItem(identityCard, identityCard.getUnlocalizedName().substring(5));
		registerItem(forceDuctEmpty, forceDuctEmpty.getUnlocalizedName().substring(5));
		registerItem(rangeUpgrade, rangeUpgrade.getUnlocalizedName().substring(5));
		registerItem(rangeWidthUpgrade, rangeWidthUpgrade.getUnlocalizedName().substring(5));
		registerItem(rangeHeightUpgrade, rangeHeightUpgrade.getUnlocalizedName().substring(5));
		registerItem(projectorLens, projectorLens.getUnlocalizedName().substring(5));
		registerItem(efficiencyUpgrade, efficiencyUpgrade.getUnlocalizedName().substring(5));
		registerItem(projectorFieldType, projectorFieldType.getUnlocalizedName().substring(5));
		registerItem(fieldUpgrade, fieldUpgrade.getUnlocalizedName().substring(5));
		CoreInit.registerMultipart(forceDuct, PartForceDuct.class, "forceDuct", "tomsmoddefense");
		registerBlock(forceConverter, forceConverter.getUnlocalizedName().substring(5));
		registerBlock(forceCapacitor, forceCapacitor.getUnlocalizedName().substring(5));
		registerBlock(securityStation, securityStation.getUnlocalizedName().substring(5));
		registerBlock(fieldProjector, fieldProjector.getUnlocalizedName().substring(5));
		CoreInit.addOnlyBlockToGameRegisty(blockForce, blockForce.getUnlocalizedName().substring(5));
		registerBlock(defenseStation, defenseStation.getUnlocalizedName().substring(5));
		registerBlock(oreMonazit, oreMonazit.getUnlocalizedName().substring(5));
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
	public static void registerPlaceables(){
		WorldHandler.addPlaceable(Blocks.LEVER);
		WorldHandler.addPlaceable(Blocks.STONE_BUTTON);
		WorldHandler.addPlaceable(Blocks.WOODEN_BUTTON);
		WorldHandler.addPlaceable(Blocks.COBBLESTONE);
		WorldHandler.addPlaceable(Blocks.REDSTONE_BLOCK);
	}
	@EventHandler
	public static void construction(FMLConstructionEvent event){
		CoreInit.modids.add(modid);
	}
}
