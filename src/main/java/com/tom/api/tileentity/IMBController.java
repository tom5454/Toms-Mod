package com.tom.api.tileentity;

import java.util.List;
/**DO NOT IMPLEMENT THIS YOURSELF! Use/Extend to {@link com.tom.api.tileentity.TileEntityControllerBase}*/
public interface IMBController {
	List<MultiblockPartList> parts();
	void receive(int x, int y, int z, int msg);
	/**updateEntity(), this only run when the redstone enable it, Other method {@link com.tom.api.tileentity.TileEntityControllerBase#updateEntity(boolean)}*/
	void updateEntityI();
	void validateI();
	void receiveMessage(int x, int y, int z, byte msg);
}
