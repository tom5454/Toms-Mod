package com.tom.api.multipart;

import com.tom.lib.api.grid.IGrid;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridUpdateListener;

public interface IDuctModule<G extends IGrid<?, G>> extends IGridDevice<G>, IModule, IGridUpdateListener {

}
