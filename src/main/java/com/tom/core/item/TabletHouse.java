package com.tom.core.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.client.CustomModelLoader;
import com.tom.core.CoreInit;
import com.tom.core.model.ModelTabletHouse;

public class TabletHouse extends Item implements IModelRegisterRequired {

	@Override
	public void registerModels() {
		CustomModelLoader.addOverride(new ResourceLocation("tomsmodcore:tablethouse"), new ModelTabletHouse());
		CoreInit.registerRender(this, 0, "tomsmodcore:tablethouse");
	}

}
