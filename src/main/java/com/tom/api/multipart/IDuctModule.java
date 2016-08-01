package com.tom.api.multipart;

import com.tom.api.grid.IGrid;
import com.tom.api.grid.IGridDevice;
import com.tom.api.grid.IGridUpdateListener;

public interface IDuctModule<G extends IGrid<?,G>> extends IGridDevice<G>, IModule, IGridUpdateListener{
	
}
