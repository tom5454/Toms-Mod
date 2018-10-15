package com.tom.storage.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.storage.block.AdvStorageSystemRouter;
import com.tom.storage.handler.StorageData;
import com.tom.storage.handler.StorageNetworkGrid.IAdvRouterTile;
import com.tom.storage.handler.StorageNetworkGrid.IChannelSource;
import com.tom.storage.handler.StorageNetworkGrid.IController;
import com.tom.storage.handler.StorageNetworkGrid.IControllerTile;
import com.tom.util.TomsModUtils;

public class TileEntityAdvRouter extends TileEntityTomsMod implements IAdvRouterTile {
	public static class ChannelSource extends com.tom.storage.tileentity.TileEntityStorageNetworkController.ChannelSource {

		public ChannelSource(IChannelSource tile, int i) {
			super(tile, i);
		}

		@Override
		public IControllerTile getController() {
			return ((TileEntityAdvRouter) getTile()).getController();
		}

		@Override
		protected int getIntFromSide(EnumFacing f) {
			return f.ordinal();
		}

		@Override
		protected EnumFacing getSideFromInt(int f) {
			return EnumFacing.VALUES[f];
		}

		@Override
		public void update() {
			if (!getTile().getWorld2().isRemote)
				if (getController() != null)
					grid.setActive(true);
				else
					grid.setActive(false);
			super.update();
		}
	}

	private ChannelSource[] routerFaces;
	private StorageData data = new StorageData();
	private IControllerTile controller;

	public TileEntityAdvRouter() {
		routerFaces = new ChannelSource[6];
		for (int i = 0;i < routerFaces.length;i++) {
			routerFaces[i] = new ChannelSource(this, i);
		}
	}

	@Override
	public void setData(StorageData data2) {
		data = data2;
	}

	@Override
	public IController getRouterOnSide(EnumFacing side) {
		return routerFaces[side.ordinal()];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (int i = 0;i < routerFaces.length;i++) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("id", i);
			routerFaces[i].writeToNBT(tag);
			list.appendTag(tag);
		}
		compound.setTag("data", list);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList list = compound.getTagList("data", 10);
		for (int i = 0;i < list.tagCount();i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			routerFaces[tag.getInteger("id")].readFromNBT(tag);
		}
	}

	@Override
	public StorageData getData() {
		return controller != null ? controller.getData() : data;
	}

	@Override
	public void updateEntity(IBlockState currentState) {
		getData().update();
		for (int i = 0;i < routerFaces.length;i++) {
			routerFaces[i].update();
		}
		if (!world.isRemote)
			TomsModUtils.setBlockStateWithCondition(world, pos, currentState, AdvStorageSystemRouter.STATE, getData().networkState.fullyActive() && getData().showChannels() ? 2 : getData().networkState.isPowered() && getData().hasEnergy() ? 1 : 0);
	}

	public void neighborUpdateGrid(EnumFacing side) {
		for (int i = 0;i < routerFaces.length;i++) {
			routerFaces[i].neighborUpdateGrid();
		}
	}

	@Override
	public World getWorld2() {
		return world;
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}

	@Override
	public boolean isValid() {
		return !isInvalid();
	}

	@Override
	public void markDirty2() {
		markDirty();
	}

	@Override
	public void updateData(boolean load) {

	}

	@Override
	public BlockPos getSecurityStationPos() {
		return controller != null ? controller.getSecurityStationPos() : data.getSecurityStationPos();
	}

	@Override
	public IControllerTile getController() {
		return controller;
	}

	@Override
	public void setController(IControllerTile controller) {
		this.controller = controller;
		if (controller == null)
			data = new StorageData();
	}
	/*public static void main(String[] args) {
		File in = new File(".", "advrouter/in");
		File in2 = new File(".", "advrouter/in2");
		File out = new File(".", "advrouter/out");
		in.mkdirs();
		in2.mkdirs();
		out.mkdirs();
		String[] l = in.list();
		String[] l2 = in2.list();
		for(String shapeF : l){
			BufferedImage shapeI = null;
			boolean[][] shape = null;
			try {
				shapeI = ImageIO.read(new File(in, shapeF));
				shape = new boolean[shapeI.getWidth()][shapeI.getHeight()];
				for(int x = 0;x<shapeI.getWidth();x++){
					for(int y = 0;y<shapeI.getHeight();y++){
						shape[x][y] = new Color(shapeI.getRGB(x, y), true).getAlpha() > 128;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(String typeF : l2){
				BufferedImage typeI = null;
				BufferedImage typeOut = null;
				try {
					typeI = ImageIO.read(new File(in2, typeF));
					typeOut = new BufferedImage(typeI.getWidth(), typeI.getHeight(), BufferedImage.TYPE_INT_ARGB);
					for(int x = 0;x<shapeI.getWidth();x++){
						for(int y = 0;y<typeI.getHeight();y++){
							if(shape[x][y % shapeI.getHeight()])typeOut.setRGB(x, y, typeI.getRGB(x, y));
						}
					}
					ImageIO.write(typeOut, "png", new File(out, shapeF.replace("0", typeF.substring(0, 1))));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
}
