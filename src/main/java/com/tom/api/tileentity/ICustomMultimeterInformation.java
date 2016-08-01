package com.tom.api.tileentity;

import java.util.List;

import net.minecraft.util.text.ITextComponent;

public interface ICustomMultimeterInformation {
	List<ITextComponent> getInformation(List<ITextComponent> list);
}
