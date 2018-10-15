package com.tom.api.tileentity;

import com.tom.api.energy.IEnergyHandler;
import com.tom.lib.api.grid.IGrid;

public interface IEnergyCable<D extends IGrid<?, D>> extends IEnergyHandler, ICable<D> {

}
