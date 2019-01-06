package com.tom.api.block;

import com.tom.lib.api.IValidationChecker;
import com.tom.lib.api.tileentity.IOwnable;

public interface IGridPowerGenerator extends IValidationChecker, IOwnable {
	long getMaxPowerGen();
}
