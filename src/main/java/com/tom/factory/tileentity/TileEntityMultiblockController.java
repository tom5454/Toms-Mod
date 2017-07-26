package com.tom.factory.tileentity;

import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import com.tom.api.block.BlockMultiblockCasing.CasingConnectionType;
import com.tom.apis.MultiblockBlockChecker;
import com.tom.factory.FactoryInit;

import com.tom.core.tileentity.TileEntityHidden.BlockProperties;

public abstract class TileEntityMultiblockController extends TileEntityMultiblock {
	public static void init() {
	}

	public static final BlockProperties face = new BlockProperties().setBlockRender(FactoryInit.MultiblockCase).setMetaRender(CasingConnectionType.Full.ordinal()).setRenderOldBlock(true);
	public static final BlockProperties faceAdv = new BlockProperties().setBlockRender(FactoryInit.AdvancedMultiblockCasing).setMetaRender(CasingConnectionType.Full.ordinal()).setRenderOldBlock(true);
	public static final BlockProperties energyPort = new BlockProperties().setId(9).setRenderOldBlock(true);
	public static final BlockProperties hatch = new BlockProperties().setId(1).setRenderOldBlock(true);// In
	public static final BlockProperties hatch2 = new BlockProperties().setId(2).setRenderOldBlock(true);// Out
	public static final BlockProperties fhatch = new BlockProperties().setId(3).setRenderOldBlock(true);// In
	public static final BlockProperties fhatch2 = new BlockProperties().setId(4).setRenderOldBlock(true);// Out
	public static final Object[] FACE = new Object[]{new Object[]{FactoryInit.MultiblockCase, face}, new Object[]{FactoryInit.MultiblockEnergyPort, energyPort}, new Object[]{FactoryInit.MultiblockHatch, 0, hatch}, new Object[]{FactoryInit.MultiblockHatch, 1, hatch2}, new Object[]{FactoryInit.MultiblockFluidHatch, 0, fhatch}, new Object[]{FactoryInit.MultiblockFluidHatch, 1, fhatch2}};
	public static final Object[] FACE_ADV = new Object[]{new Object[]{FactoryInit.AdvancedMultiblockCasing, faceAdv}, new Object[]{FactoryInit.MultiblockEnergyPort, energyPort}, new Object[]{FactoryInit.MultiblockHatch, 0, hatch}, new Object[]{FactoryInit.MultiblockHatch, 1, hatch2}, new Object[]{FactoryInit.MultiblockFluidHatch, 0, fhatch}, new Object[]{FactoryInit.MultiblockFluidHatch, 1, fhatch2}};
	public static final BlockProperties hatch3 = new BlockProperties().setId(5).setRenderOldBlock(true);// In
																										// Bottom
	public static final BlockProperties hatch4 = new BlockProperties().setId(6).setRenderOldBlock(true);// Out
																										// Bottom
	public static final BlockProperties fhatch3 = new BlockProperties().setId(7).setRenderOldBlock(true);// In
																											// Bottom
	public static final BlockProperties fhatch4 = new BlockProperties().setId(8).setRenderOldBlock(true);// Out
																											// Bottom
	public static final Object[] FACE2 = new Object[]{new Object[]{FactoryInit.MultiblockCase, face}, new Object[]{FactoryInit.MultiblockEnergyPort, energyPort}, new Object[]{FactoryInit.MultiblockHatch, 0, hatch3}, new Object[]{FactoryInit.MultiblockHatch, 1, hatch4}, new Object[]{FactoryInit.MultiblockFluidHatch, 0, fhatch3}, new Object[]{FactoryInit.MultiblockFluidHatch, 1, fhatch4}};
	public static final Object[] FACE_ADV2 = new Object[]{new Object[]{FactoryInit.AdvancedMultiblockCasing, faceAdv}, new Object[]{FactoryInit.MultiblockEnergyPort, energyPort}, new Object[]{FactoryInit.MultiblockHatch, 0, hatch3}, new Object[]{FactoryInit.MultiblockHatch, 1, hatch4}, new Object[]{FactoryInit.MultiblockFluidHatch, 0, fhatch3}, new Object[]{FactoryInit.MultiblockFluidHatch, 1, fhatch4}};
	public static final BlockProperties fuelrod = new BlockProperties().setId(10).setRenderOldBlock(true);
	public static final BlockProperties center = new BlockProperties().setRenderOldBlock(true).setRenderResLoc(true).setRenderLoc(new ResourceLocation("tmobj:block/multiblock.obj"));
	public static final BlockProperties centerAdv = new BlockProperties().setRenderOldBlock(true).setRenderResLoc(true).setRenderLoc(new ResourceLocation("tmobj:block/multiblockadv.obj"));
	public static final Object[] FUELROD = new Object[]{FactoryInit.MultiblockFuelRod, fuelrod};
	public static final Object[][] NORMAL = new Object[][]{{'C', FactoryInit.MultiblockCase, 'T', new Object[]{FactoryInit.MultiblockCase, face}, 'F', FACE, 'f', FACE2, 'r', new Object[]{FactoryInit.MultiblockCase, center}}, {"CCC", "CTC", "CCC"}, {"C@C", "FrF", "CFC"}, {"CfC", "fCf", "CfC"}, {"CCC", "CTC", "CCC"}};
	public static final Object[][] ADV = new Object[][]{{'C', FactoryInit.AdvancedMultiblockCasing, 'T', new Object[]{FactoryInit.AdvancedMultiblockCasing, faceAdv}, 'F', FACE_ADV, 'f', FACE_ADV2, 'R', FUELROD, 'r', new Object[]{FactoryInit.AdvancedMultiblockCasing, centerAdv}}, {"CCC", "CTC", "CCC"}, {"C@C", "FrF", "CFC"}, {"CfC", "fCf", "CfC"}, {"CCC", "CRC", "CCC"}};
	private final boolean adv;
	private Map<Character, MultiblockBlockChecker> materialMap;

	protected TileEntityMultiblockController(boolean adv) {
		this.adv = adv;
	}

	@Override
	public Object[][] getConfig() {
		return adv ? ADV : NORMAL;
	}

	@Override
	public Map<Character, MultiblockBlockChecker> getMaterialMap() {
		return materialMap;
	}

	@Override
	protected void setMaterialMap(Map<Character, MultiblockBlockChecker> value) {
		materialMap = value;
	}

	public abstract int[] getSlots(int id);

	public abstract IInventory getInventory(int id);
}
