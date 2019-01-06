package com.tom.storage.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.grid.StorageNetworkGrid;
import com.tom.api.grid.StorageNetworkGrid.IAdvRouterTile;
import com.tom.api.grid.StorageNetworkGrid.IControllerTile;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.api.grid.IGrid;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridUpdateListener;
import com.tom.lib.api.grid.VirtualGridDevice;
import com.tom.storage.block.AdvStorageSystemRouter;
import com.tom.storage.handler.StorageData;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.IGridDeviceHostFacing;

public class TileEntityAdvRouter extends TileEntityTomsMod implements IAdvRouterTile, IGridDeviceHostFacing {
	public class DenseChannelSource extends VirtualGridDevice<StorageNetworkGrid> implements IGridUpdateListener {
		private final EnumFacing side;
		public DenseChannelSource(EnumFacing side) {
			this.side = side;
			grid.getData().dataSupplier = TileEntityAdvRouter.this::getData;
		}
		@Override
		public BlockPos getPos2() {
			return pos;
		}

		@Override
		public World getWorld2() {
			return world;
		}

		@Override
		public boolean isConnected(EnumFacing side) {
			return this.side == side;
		}

		@Override
		public boolean isValidConnection(EnumFacing side) {
			return this.side == side;
		}

		@Override
		public boolean isValid() {
			return !isInvalid();
		}

		@Override
		public StorageNetworkGrid constructGrid() {
			return new StorageNetworkGrid(true);
		}
		@Override
		public void onGridReload() {
			grid.getData().dataSupplier = TileEntityAdvRouter.this::getData;
		}
		@Override
		public void onGridPostReload() {
		}
	}
	public class ChannelSource extends VirtualGridDevice<StorageNetworkGrid> implements IGridUpdateListener {
		private final EnumFacing side;
		public ChannelSource(EnumFacing side) {
			this.side = side;
			grid.getData().dataSupplier = TileEntityAdvRouter.this::getData;
		}
		@Override
		public BlockPos getPos2() {
			return pos;
		}

		@Override
		public World getWorld2() {
			return world;
		}

		@Override
		public boolean isConnected(EnumFacing side) {
			return this.side == side;
		}

		@Override
		public boolean isValidConnection(EnumFacing side) {
			return this.side == side;
		}

		@Override
		public boolean isValid() {
			return !isInvalid();
		}

		@Override
		public StorageNetworkGrid constructGrid() {
			return new StorageNetworkGrid();
		}
		@Override
		public void onGridReload() {
			grid.getData().dataSupplier = TileEntityAdvRouter.this::getData;
		}
		@Override
		public void onGridPostReload() {
		}
	}

	private ChannelSource[] routerFaces;
	private DenseChannelSource[] routerFacesD;
	private IControllerTile controller;
	private StorageData data = new StorageData();

	public TileEntityAdvRouter() {
		routerFaces = new ChannelSource[6];
		routerFacesD = new DenseChannelSource[6];
		for (int i = 0;i < routerFaces.length;i++) {
			routerFaces[i] = new ChannelSource(EnumFacing.getFront(i));
			routerFacesD[i] = new DenseChannelSource(EnumFacing.getFront(i));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (int i = 0;i < routerFaces.length;i++) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("id", i);
			NBTTagCompound tagn = new NBTTagCompound();
			routerFaces[i].writeToNBT(tagn);
			NBTTagCompound tagd = new NBTTagCompound();
			routerFacesD[i].writeToNBT(tagd);
			tag.setTag("n", tagn);
			tag.setTag("d", tagd);
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
			NBTTagCompound tagn = tag.getCompoundTag("n");
			NBTTagCompound tagd = tag.getCompoundTag("d");
			routerFaces[tag.getInteger("id")].readFromNBT(tagn);
			routerFacesD[tag.getInteger("id")].readFromNBT(tagd);
		}
	}
	public StorageData getData() {
		return controller != null ? controller.getData() : data;
	}

	@Override
	public void updateEntity(IBlockState currentState) {
		if(controller != null){
			data = getData();
			data.update();
			for (int i = 0;i < routerFaces.length;i++) {
				routerFaces[i].update();
				routerFacesD[i].update();
			}
			if (!world.isRemote)
				TomsModUtils.setBlockStateWithCondition(world, pos, currentState, AdvStorageSystemRouter.STATE, getData().networkState.fullyActive() && getData().showChannels() ? 2 : getData().networkState.isPowered() && getData().hasEnergy() ? 1 : 0);
		}else{
			if (!world.isRemote)
				TomsModUtils.setBlockStateWithCondition(world, pos, currentState, AdvStorageSystemRouter.STATE, 0);
		}
	}

	public void neighborUpdateGrid(EnumFacing side) {
		for (int i = 0;i < routerFaces.length;i++) {
			routerFaces[i].neighborUpdateGrid(false);
			routerFacesD[i].neighborUpdateGrid(false);
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
	public BlockPos getSecurityStationPos() {
		return controller != null ? controller.getSecurityStationPos() : null;
	}

	@Override
	public IControllerTile getController() {
		return controller;
	}

	@Override
	public void setController(IControllerTile controller) {
		this.controller = controller;
	}
	@SuppressWarnings("unchecked")
	@Override
	public <G extends IGrid<?, G>> IGridDevice<G> getDevice(EnumFacing facing, Class<G> gridClass, Object... objects) {
		boolean dense = objects != null && objects.length > 0;
		if(gridClass == StorageNetworkGrid.class && facing != null){
			if(dense){
				return (IGridDevice<G>) routerFacesD[facing.ordinal()];
			}
			return (IGridDevice<G>) routerFaces[facing.ordinal()];
		}
		return null;
	}

	@Override
	public boolean isValid() {
		return !isInvalid();
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
