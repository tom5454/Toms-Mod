package com.tom.defense;

import static com.tom.core.CoreInit.registerBlock;
import static com.tom.core.CoreInit.registerItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.IMod;
import com.tom.defense.block.BlockForceField;
import com.tom.defense.block.DefenseStation;
import com.tom.defense.block.FieldProjector;
import com.tom.defense.block.ForceCapacitor;
import com.tom.defense.block.ForceConverter;
import com.tom.defense.block.SecurityStation;
import com.tom.defense.item.IdentityCard;
import com.tom.defense.item.ItemCrushedMonazit;
import com.tom.defense.item.ItemFieldUpgrade;
import com.tom.defense.item.ItemMultiTool;
import com.tom.defense.item.ItemProjectorFieldType;
import com.tom.defense.tileentity.TileEntityDefenseStation;
import com.tom.defense.tileentity.TileEntityForceCapacitor;
import com.tom.defense.tileentity.TileEntityForceConverter;
import com.tom.defense.tileentity.TileEntityForceField;
import com.tom.defense.tileentity.TileEntityForceFieldProjector;
import com.tom.defense.tileentity.TileEntitySecurityStation;
import com.tom.handler.WorldHandler;
import com.tom.lib.Configs;
import com.tom.recipes.OreDict;
import com.tom.worldgen.WorldGen;

import com.tom.core.block.BlockOre;

@Mod(modid = DefenseInit.modid, name = DefenseInit.modName, version = Configs.version, dependencies = Configs.coreDependencies)
public class DefenseInit {
	public static final String modid = Configs.ModidL + "|defense";
	public static final String modName = Configs.ModName + " Defense";
	public static final Logger log = LogManager.getLogger(modName);

	public static ItemMultiTool multiTool;
	public static IdentityCard identityCard;
	public static Item rangeUpgrade, rangeWidthUpgrade, rangeHeightUpgrade, projectorLens, projectorFieldType,
			fieldUpgrade, efficiencyUpgrade, crushedMonazit;

	public static Block forceConverter, forceCapacitor, securityStation, fieldProjector, blockForce, defenseStation,
			oreMonazit;

	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent) {
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		oreMonazit = new BlockOre(5, WorldGen.OVERWORLD, 2, 2, 2).addExtraState(WorldGen.END, 55, 12, BlockMatcher.forBlock(Blocks.END_STONE)::apply, "end").setUnlocalizedName("oreMonazit");
		crushedMonazit = new ItemCrushedMonazit().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.crushedMonazit").setHasSubtypes(true);
		if (Config.enableDefenseSystem) {
			/** Items */
			multiTool = new ItemMultiTool();
			identityCard = new IdentityCard();
			rangeUpgrade = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.rangeUpgrade");
			rangeWidthUpgrade = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.rangeWidthUpgrade");
			rangeHeightUpgrade = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.rangeHeightUpgrade");
			projectorLens = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.projectorLens").setMaxStackSize(1);
			projectorFieldType = new ItemProjectorFieldType().setCreativeTab(tabTomsModDefense).setUnlocalizedName("projectorFieldTypeController").setMaxStackSize(1).setHasSubtypes(true).setMaxDamage(0);
			fieldUpgrade = new ItemFieldUpgrade().setCreativeTab(tabTomsModDefense).setUnlocalizedName("projectorUpgrade").setHasSubtypes(true).setMaxDamage(0);
			efficiencyUpgrade = new Item().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.efficiencyUpgrade");
			/** Blocks */
			forceConverter = new ForceConverter().setCreativeTab(tabTomsModDefense).setUnlocalizedName("forceTransformer");
			forceCapacitor = new ForceCapacitor().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.forceCapacitor");
			securityStation = new SecurityStation().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tm.securityStation");
			fieldProjector = new FieldProjector().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tmd.fieldProjector");
			blockForce = new BlockForceField().setBlockUnbreakable().setResistance(18000000F).setHardness(-1F).setUnlocalizedName("tmd.force");
			defenseStation = new DefenseStation().setCreativeTab(tabTomsModDefense).setUnlocalizedName("tmd.defenseStation");
			/** Registry */
			/** Items */
			registerItem(multiTool, multiTool.getUnlocalizedName().substring(5));
			OreDict.registerOre("tomsmodwrench", new ItemStack(DefenseInit.multiTool, 1, 0));
			registerItem(identityCard, identityCard.getUnlocalizedName().substring(5));
			registerItem(rangeUpgrade, rangeUpgrade.getUnlocalizedName().substring(5));
			registerItem(rangeWidthUpgrade, rangeWidthUpgrade.getUnlocalizedName().substring(5));
			registerItem(rangeHeightUpgrade, rangeHeightUpgrade.getUnlocalizedName().substring(5));
			registerItem(projectorLens, projectorLens.getUnlocalizedName().substring(5));
			registerItem(efficiencyUpgrade, efficiencyUpgrade.getUnlocalizedName().substring(5));
			registerItem(projectorFieldType, projectorFieldType.getUnlocalizedName().substring(5));
			registerItem(fieldUpgrade, fieldUpgrade.getUnlocalizedName().substring(5));
			/** Blocks */
			registerBlock(forceConverter, forceConverter.getUnlocalizedName().substring(5));
			registerBlock(forceCapacitor, forceCapacitor.getUnlocalizedName().substring(5));
			registerBlock(securityStation, securityStation.getUnlocalizedName().substring(5));
			registerBlock(fieldProjector, fieldProjector.getUnlocalizedName().substring(5));
			CoreInit.addOnlyBlockToGameRegisty(blockForce, blockForce.getUnlocalizedName().substring(5));
			registerBlock(defenseStation, defenseStation.getUnlocalizedName().substring(5));
			/** TileEntities */
			GameRegistry.registerTileEntity(TileEntityForceConverter.class, Configs.Modid + ":forceTransformer");
			GameRegistry.registerTileEntity(TileEntityForceCapacitor.class, Configs.Modid + ":forceCapacitor");
			GameRegistry.registerTileEntity(TileEntitySecurityStation.class, Configs.Modid + ":securityStation");
			GameRegistry.registerTileEntity(TileEntityForceFieldProjector.class, Configs.Modid + ":FFProjector");
			GameRegistry.registerTileEntity(TileEntityForceField.class, Configs.Modid + ":forceField");
			GameRegistry.registerTileEntity(TileEntityDefenseStation.class, Configs.Modid + ":defenseStation");
		}
		registerBlock(oreMonazit, oreMonazit.getUnlocalizedName().substring(5));
		registerItem(crushedMonazit, crushedMonazit.getUnlocalizedName().substring(5));
		hadPreInit = true;
		CoreInit.tryLoadAfterPreInit(log);
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in " + time + " milliseconds");
	}

	public static CreativeTabs tabTomsModDefense = new CreativeTabs("tabTomsModDefense") {

		@Override
		public ItemStack getTabIconItem() {
			ItemStack is = new ItemStack(multiTool, 1, 2);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("isInCreativeTabIcon", true);
			is.setTagCompound(tag);
			return is;
		}
	};

	public static void registerPlaceables() {
		WorldHandler.addPlaceable(Blocks.LEVER);
		WorldHandler.addPlaceable(Blocks.STONE_BUTTON);
		WorldHandler.addPlaceable(Blocks.WOODEN_BUTTON);
		WorldHandler.addPlaceable(Blocks.COBBLESTONE);
		WorldHandler.addPlaceable(Blocks.REDSTONE_BLOCK);
	}

	private static boolean hadPreInit = false;

	@EventHandler
	public static void construction(FMLConstructionEvent event) {
		CoreInit.modids.add(new IMod() {
			@Override
			public String getModID() {
				return modid;
			}

			@Override
			public boolean hadPreInit() {
				return hadPreInit;
			}
		});
	}
}
