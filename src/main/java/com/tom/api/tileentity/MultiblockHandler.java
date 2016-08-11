package com.tom.api.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.block.BlockControllerBase;
import com.tom.apis.TomsModUtils;
import com.tom.lib.Configs;
import com.tom.lib.GlobalFields;

public class MultiblockHandler{
	public int height;
	public int width;
	protected boolean firstStart = true;
	public MultiblockHandler(TileEntityControllerBase te, int w, int h, MultiblockPartList frame, int mode){
		this.te = te;
		this.height = h;
		this.width = w;
		if(this.width < 0){
			throw new IllegalArgumentException("Width = "+this.width);
		}
		if(this.width < 0){
			throw new IllegalArgumentException("Height = "+this.height);
		}
		if(this.te == null){
			throw new IllegalArgumentException("You cannot create a handler for a null tileentity");
		}
		list.add(frame);
		list.add(MultiblockPartList.Controller);
		//int d = this.te.direction;
		//this.d = d;
		this.world = this.te.getWorld();
		this.coords = TomsModUtils.getCoordTable(this.te.getPos().getX(), this.te.getPos().getY(), this.te.getPos().getZ());
		this.mode = mode;
		this.frame = frame;

		//System.out.println(this.d);
	}
	public MultiblockHandler(TileEntityControllerBase te, int w, int h, MultiblockPartList frame){
		this(te,w,h,frame,1);
	}
	/**Direction*/
	public EnumFacing d = EnumFacing.NORTH;
	private World world;
	private TileEntityControllerBase te;
	protected boolean formed = false;
	protected List<MultiblockPartList> list = new ArrayList<MultiblockPartList>();
	protected Map<MultiblockPartList,List<int[]>> deviceMap = new HashMap<MultiblockPartList, List<int[]>>();
	protected int[] coords;
	/**Mode: 1 Normal, 2 Middle, 3 Top*/
	protected final int mode;
	private int i = 0;
	protected MultiblockPartList frame;

	/*private boolean getPart(int x, int y, int z){
		int[] coords = TomsMathHelper.getRelativeCoordTable(TomsMathHelper.getCoordTable(x, y, z), x, y, z, this.d);
		TileEntity tilee = world.getTileEntity(coords[0], coords[1], coords[2]);
		if(tilee instanceof MultiblockParts){
			MultiblockParts tileentity = (MultiblockParts) tilee;
			return !tileentity.isFormed();
		}else{
			return false;
		}
	}*/
	public void form(World world){
		//TileEntityMultiblockPartBase[][][] list = this.getParts(world);
		//System.out.println(list);
		for(int i1 = 0;i1<this.width;i1++){
			for(int i2 = 0;i2<this.height;i2++){
				for(int i3 = 0;i3<this.width;i3++){
					//TileEntityMultiblockPartBase current = list[i1][i2][i3];
					int cX = i1 - (this.width - 1) / 2,
							cY = i2 - 1,
							cZ = i3 - (this.width - 1) / 2;
					/*int[] coords2 = TomsMathHelper.getCoordTable(this.te.xCoord, this.te.yCoord, this.te.zCoord);
					int[] coords = TomsMathHelper.getRelativeCoordTable(TomsMathHelper.getRelativeCoordTable(coords2, cX, cY, cZ, this.d),1,0,0,this.d);*/
					int[] coords = this.getCoords(cX, cY, cZ);
					TileEntity tilee = world.getTileEntity(new BlockPos(coords[0], coords[1], coords[2]));
					//System.out.println(tilee);
					//System.out.println("instanceof"+(tilee instanceof TileEntityMultiblockPartBase));
					if(tilee instanceof TileEntityMultiblockPartBase){
						//System.out.println("form");
						TileEntityMultiblockPartBase current = (TileEntityMultiblockPartBase) tilee;
						if(current instanceof TileEntityMultiblockCasingBase){
							TileEntityMultiblockCasingBase current2 = (TileEntityMultiblockCasingBase) current;
							current2.form(this.getTexture(i1, i2, i3), this.te.getPos().getX(), this.te.getPos().getY(), this.te.getPos().getZ());
						}else{
							current.form(this.te.getPos().getX(), this.te.getPos().getY(), this.te.getPos().getZ());
						}
					}
				}
			}
		}
		this.formed = true;
		te.markBlockForUpdate(this.te.getPos());
		world.markBlockRangeForRenderUpdate(this.te.getPos().getX() + (this.width - 1) / 2, this.te.getPos().getY()
				- 1, this.te.getPos().getZ() + (this.width - 1) / 2, this.te.getPos().getX() - (this.width - 1) / 2,
				this.te.getPos().getY() + this.height - 1, this.te.getPos().getZ() - (this.width - 1) / 2);
	}
	public void deForm(World world){
		for(int i1 = 0;i1<this.width;i1++){
			for(int i2 = 0;i2<this.height;i2++){
				for(int i3 = 0;i3<this.width;i3++){
					//TileEntityMultiblockPartBase current = list[i1][i2][i3];
					int cX = i1 - (this.width - 1) / 2,
							cY = i2 - 1,
							cZ = i3 - (this.width - 1) / 2;
					/*int[] coords2 = TomsMathHelper.getCoordTable(this.te.xCoord, this.te.yCoord, this.te.zCoord);
					int[] coords = TomsMathHelper.getRelativeCoordTable(TomsMathHelper.getRelativeCoordTable(coords2, cX, cY, cZ, this.d),1,0,0,this.d);*/
					int[] coords = this.getCoords(cX, cY, cZ);
					TileEntity tilee = world.getTileEntity(new BlockPos(coords[0], coords[1], coords[2]));
					if(tilee instanceof TileEntityMultiblockPartBase){
						TileEntityMultiblockPartBase current = (TileEntityMultiblockPartBase) tilee;
						current.deForm(this.te.getPos().getX(), this.te.getPos().getY(), this.te.getPos().getZ());
					}
				}
			}
		}
		this.formed = false;
		te.markBlockForUpdate(this.te.getPos());
		world.markBlockRangeForRenderUpdate(this.te.getPos().getX() + (this.width - 1) / 2, this.te.getPos().getY() - 1,this.te.getPos().getZ() + (this.width - 1) / 2, this.te.getPos().getX() - (this.width - 1) / 2, this.te.getPos().getY() + this.height - 1, this.te.getPos().getZ() - (this.width - 1) / 2);
	}
	protected void breakBlock(World world){
		this.world = world;
		if(this.formed)
			this.deForm(world);
	}
	public void updateEntity(World world){
		if(this.firstStart && !world.isRemote){
			this.firstStart = false;
			this.d = world.getBlockState(te.getPos()).getValue(BlockControllerBase.FACING);
			this.onNeighborChange(world);
		}
		if(this.width < 0){
			throw new IllegalArgumentException("Width = "+this.width+" It's a bug report to the mod author");
		}
		if(this.width < 0){
			throw new IllegalArgumentException("Height = "+this.height+" It's a bug report to the mod author");
		}
		if(this.te == null){
			throw new IllegalArgumentException("You cannot create a handler for a null tileentity! It's a bug report to the mod author");
		}
		this.i++;
		if(i>Configs.updateRate){
			this.i = 0;
			this.update(world);
		}
	}
	public NBTTagCompound exportToNBT(NBTTagCompound tag){
		tag.setBoolean("formed", this.formed);
		//tag.setInteger("d", this.d);
		return tag;
	}
	public NBTTagCompound exportToNBT(){
		return exportToNBT(new NBTTagCompound());
	}
	public void importFromNBT(NBTTagCompound tag){
		this.formed = tag.getBoolean("formed");
		//this.d = tag.getInteger("d");
	}
	/*private TileEntityMultiblockPartBase getPartTileEntity(int x, int y, int z){
		int[] coords = TomsMathHelper.getRelativeCoordTable(this.coords, x, y, z, this.d);
		TileEntity tilee = world.getTileEntity(coords[0], coords[1], coords[2]);
		if(tilee instanceof TileEntityMultiblockPartBase){
			return (TileEntityMultiblockPartBase) tilee;
		}else{
			return null;
		}

	}*/
	private MultiblockPartList getPartName(MultiblockParts te){
		return te.getPartName();
	}
	/*
	 * @return TileEntityMultiblockPartBase[][][] { { {#row}, {#row} /#layer }, { { #layer } }
	protected TileEntityMultiblockPartBase[][][] getParts(World world){
		TileEntityMultiblockPartBase[][][] ret = {{{}}};
		for(int w = 0;w<this.width;w++){
			for(int h = 0;h<this.height;h++){
				for(int w2 = 0;w2<this.width;w2++){
					int cX = w - (this.width - 1) / 2,
						cY = h - 1,
						cZ = w2 - (this.width - 1) / 2;
					int[] coords = TomsMathHelper.getRelativeCoordTable(TomsMathHelper.getCoordTable(xCoord, yCoord, zCoord), cX, cY, cZ, this.d);
					TileEntity tilee = world.getTileEntity(coords[0], coords[1], coords[2]);
					if(tilee instanceof TileEntityMultiblockPartBase){
						ret[w][h][w2] = (TileEntityMultiblockPartBase) tilee;
					}else{
						return new TileEntityMultiblockPartBase[][][]{{{}}};
					}
					//ret[w][h][w2] = this.getPartTileEntity(cX, cY, cZ);
				}
			}
		}
		return ret;
	}*/
	private int getTexture(int i1, int i2, int i3){
		if(i1 == 0 || i1 == this.width - 1){
			if(i3 == 0 || i3 == this.width - 1){
				if(i2 > 0 && i2 < this.height - 1){
					//return new int[]{0,0,1,1,1,1};
					return 1;
				}else{
					//return new int[]{2,2,2,2,2,2};
					return 2;
				}
			}else{
				if(i2 == 0 || i2 == this.height - 1){
					//return new int[]{4,4,4,4,4,4};
					return getSideRot(d, false);
				}else{
					//return new int[]{3,3,3,3,3,3};
					return 3;
				}
			}
		}else{
			if(i3 == 0 || i3 == this.width - 1){
				if(i2 == 0 || i2 == this.height - 1){
					return getSideRot(d, true);
				}
			}
			//return new int[]{3,3,3,3,3,3};
			return 3;
		}
	}
	private static int getSideRot(EnumFacing d, boolean invert){
		return d.getAxis() == (invert ? Axis.Z : Axis.X) ? 5 : 4;
	}
	/**@return Object[]{<br> boolean blocks in place,<br> boolean buses found, <br>if buses not found ?<br> List missing buses <br>else<br> boolean buses at the right place*/
	public Object[] testParts(World world){
		List<MultiblockPartList> found = new ArrayList<MultiblockPartList>();
		Map<MultiblockPartList,List<int[]>> map = new HashMap<MultiblockPartList, List<int[]>>();
		//int d = this.te.direction;
		/*if(d == 4) d = 0;
		else if(d == 5) d = 1;
		else if(d == 2) d = 2;
		else d = 3;*/
		//EnumFacing f1 = EnumFacing.getHorizontal(d);
		//this.d = f1.rotateYCCW().ordinal()-2;
		this.d = world.getBlockState(te.getPos()).getValue(BlockControllerBase.FACING);
		//System.out.println(d);
		this.world = world;
		boolean sideError = false;
		boolean cFrame = this.frame != MultiblockPartList.Casing;
		//System.out.println("awh"+this.width+","+this.height);
		for(int w = 0;w<this.width;w++){
			for(int h = 0;h<this.height;h++){
				for(int w2 = 0;w2<this.width;w2++){
					int cX = w - (this.width - 1) / 2,
							cY = h - 1,
							cZ = w2 - (this.width - 1) / 2;
					/*int[] coords2 = TomsMathHelper.getCoordTable(this.te.xCoord, this.te.yCoord, this.te.zCoord);
					int[] coords = TomsMathHelper.getRelativeCoordTable(TomsMathHelper.getRelativeCoordTable(coords2, cX, cY, cZ, this.d),1,0,0,this.d);*/
					int[] coords = this.getCoords(cX, cY, cZ);
					//System.out.println("coords:"+coords[0]+" "+coords[1]+" "+coords[2]);
					//world.setBlock(coords[0], coords[1], coords[2], CoreInit.MultiblockCase);
					//System.out.println(world.getBlock(coords[0], coords[1], coords[2]));
					TileEntity tilee = world.getTileEntity(new BlockPos(coords[0], coords[1], coords[2]));
					/*System.out.println(tilee);
					System.out.println("d="+this.d);
					System.out.println("instanceof:"+(tilee instanceof TileEntityMultiblockPartBase));//*///[-13, 71, 76]
					if(tilee instanceof TileEntityMultiblockPartBase){
						MultiblockPartList current = this.getPartName((MultiblockParts) tilee);
						if(!found.contains(current)){
							found.add(current);
						}
						boolean isFrame = false;
						//System.out.println(current);
						if(current != this.frame){
							if(cFrame){
								isFrame = GlobalFields.MBFrames.contains(current);
								if(isFrame){
									return new Object[]{false};
								}
							}
							//System.out.println(current);
							if(map.containsKey(current)){
								List<int[]> currentList = map.get(current);
								currentList.add(coords);
								//System.out.println(current);
								map.remove(current);
								map.put(current, currentList);
							}else{
								List<int[]> list = new ArrayList<int[]>();
								list.add(coords);
								map.put(current, list);
								//System.out.println(current);
							}
							//System.out.println("b"+coords[0]+" "+coords[1]+" "+coords[2]);
						}
						//System.out.println("aInXYZ "+coords[0]+" "+coords[1]+" "+coords[2]);
						TileEntityMultiblockPartBase tileentity = (TileEntityMultiblockPartBase) tilee;
						//if(!tileentity.isPart()) return new Object[]{false};
						//boolean isPlaceableOnSide = tileentity.isPlaceableOnSide();
						boolean isSide = false;
						boolean isTop = false;
						boolean isBottom = false;
						boolean isCorner = false;
						MultiblockPartSides place = tileentity.isPlaceable();
						if(w == 0 || w == this.width-1){
							if(w2 == 0 || w2 == this.width-1){
								isSide = true;
							}else if(h == 0 || h == this.height - 1){
								isSide = true;
							}
						}else if((h == 0 || h == this.height-1) && (w == 0 || w == this.width-1 || w2 == 0 || w2 == this.width-1)){
							isSide = true;
						}

						if(h == this.height-1 && ((w > 0 && w < this.width-1) && (w2 > 0 && w2 < this.width-1))){
							isTop = true;
						}
						if(h == 0 && ((w > 0 && w < this.width-1) && (w2 > 0 && w2 < this.width-1))){
							isBottom = true;
						}
						if(w == 0 || w == this.width - 1){
							if(w2 == 0 || w2 == this.width - 1){
								if(h == 0 || h == this.height-1){
									isCorner = true;
								}

							}
						}
						//System.out.println("c:" + isCorner + " s:" + isSide +  " t:" + isTop + " b:" + isBottom + h);
						if(isSide && !(place == MultiblockPartSides.All || place == MultiblockPartSides.SideOnly || place == MultiblockPartSides.SidesCornersOnly)){
							//sideError = true;
							System.out.println("Side Error S at " + coords[0]+" "+coords[1]+" "+coords[2]);
						}else if(isBottom && !(place == MultiblockPartSides.All || place == MultiblockPartSides.Bottom || place == MultiblockPartSides.Middle)){
							//sideError = true;
							System.out.println("Side Error B at " + coords[0]+" "+coords[1]+" "+coords[2]);
						}else if(isTop && !(place == MultiblockPartSides.All || place == MultiblockPartSides.Top || place == MultiblockPartSides.Middle)){
							//sideError = true;
							System.out.println("Side Error T at " + coords[0]+" "+coords[1]+" "+coords[2]);
						}else if(isCorner && !(place == MultiblockPartSides.All || place == MultiblockPartSides.CornersOnly || place == MultiblockPartSides.SidesCornersOnly)){
							//sideError = true;
							System.out.println("Side Error C at " + coords[0]+" "+coords[1]+" "+coords[2]);
						}

						//System.out.println(101);
					}else{
						return new Object[]{false};
					}
				}
			}
		}
		this.deviceMap = map;
		List<MultiblockPartList> missing = new ArrayList<MultiblockPartList>();
		for(MultiblockPartList part : this.list){
			boolean current = false;
			for(MultiblockPartList part2 : found){
				if(part.equals(part2)){
					current = true;
					break;
				}
			}
			if(!current){
				missing.add(part);
			}
		}
		if(missing.isEmpty()){
			return new Object[]{true,true,!sideError};
		}else{
			return new Object[]{true,false,missing};
		}
	}
	public void setParts(List<MultiblockPartList> list){
		for(MultiblockPartList l : list){
			if(this.list.contains(l)){
				continue;
			}else{
				this.list.add(l);
			}
		}
	}
	public void validate(World world) {
		this.world = world;
	}
	/**Checks the multiblock state
	 * @return
	 * 0 when not formed<br>
	 * 1 when formed BUT haven't find the buses<br>
	 * 2 when everything is good<br>
	 * 3 when a bus is placed at the side of the multiblock
	 * */
	public byte test(World world){
		Object[] o = this.testParts(world);
		boolean form = (Boolean) o[0];
		if(form){
			boolean bus = (Boolean) o[2];
			if(bus){
				boolean busOk = (Boolean) o[3];
				if(busOk){
					return 2;
				}else{
					return 3;
				}
			}else{
				return 1;
			}
		}else{
			return 0;
		}
	}
	public boolean isReadyToForm(World world){
		this.world = world;
		return this.test(world) == 2;
	}
	@SuppressWarnings("unchecked")
	public List<MultiblockPartList> getMissing(World world){
		this.world = world;
		Object[] o = this.testParts(world);
		byte state = this.test(o);
		if(state == 1 && o[2] instanceof List){
			return (List<MultiblockPartList>) o[2];
		}
		return new ArrayList<MultiblockPartList>();
	}
	public byte test(Object[] o){
		boolean form = (Boolean) o[0];
		if(form){
			boolean bus = (Boolean) o[1];
			if(bus){
				boolean busOk = (Boolean) o[2];
				if(busOk){
					return 2;
				}else{
					return 3;
				}
			}else{
				return 1;
			}
		}else{
			return 0;
		}
	}
	public void onNeighborChange(World world){
		if(!world.isRemote){
			this.world = world;
			TileEntityControllerBase te = (TileEntityControllerBase) world.getTileEntity(this.te.getPos());
			this.te = te;
			List<MultiblockPartList> l = te.parts();
			l.add(this.frame);
			l.add(MultiblockPartList.Controller);
			this.list = l;
			Object[] o = this.testParts(world);
			byte state = this.test(o);
			//System.out.println(te);
			//System.out.println(state);
			if(state != 2 && this.formed){
				this.deForm(world);
			}else if(state == 2){
				this.form(world);
			}
		}
	}
	public void receive(int msg, int x, int y, int z, World world){
		this.world = world;
		if(msg == 0){
			this.breakBlock(world);
		}else if(msg == 1){
			this.onNeighborChange(world);
		}
	}
	public int[][] getTileEntityListFromMap(MultiblockPartList partName){
		//System.out.println(partName);
		List<int[]> current =  this.deviceMap.get(partName);
		int[][] ret = {{}};
		//System.out.println("asd");
		if(current != null){
			Object[] current2 = current.toArray();
			for(int i = 0;i<current2.length;i++){
				int[] c = (int[]) current2[i];
				TileEntity te = world.getTileEntity(new BlockPos(c[0], c[1], c[2]));
				//System.out.println(c[0]+" "+c[1]+" "+c[2]);
				if(te instanceof TileEntityMultiblockPartBase && !(te instanceof TileEntityControllerBase)){
					//TileEntityMultiblockPartBase te2 = (TileEntityMultiblockPartBase) te;
					//System.out.println(te);
					//System.out.println(i);
					ret[i] = c;
				}/*else{
					return new TileEntityMultiblockPartBase[]{};
				}*/
			}
		}
		return ret;
	}
	private int[] getCoords(int cX, int cY, int cZ){
		int[] coords2 = TomsModUtils.getCoordTable(this.te.getPos().getX(), this.te.getPos().getY(), this.te.getPos().getZ());
		int mX = 1;
		int mY = 0;
		int mZ = 0;
		if(this.mode == 2){
			mX = 0;
		}else if(this.mode == 3){
			mX = 0;
			mY = this.height-1;
		}
		int dV = 0;
		switch(d){
		case NORTH:
			dV = 3;
			break;
		case SOUTH:
			dV = 2;
			break;
		case WEST:
			dV = 1;
			break;
		default:
			break;

		}
		int[] coords = TomsModUtils.getRelativeCoordTable(TomsModUtils.getRelativeCoordTable(coords2, cX, cY, cZ, dV),mX,mY,mZ,dV);
		//BlockPos offset = new BlockPos(0,0,0).offset(d.rotateY(),cX).offset(d,cZ).add(0, cY, 0).add(0, mY, 0).offset(d.rotateY(),mX).offset(d,mZ);
		//BlockPos pos = offset.add(te.getPos());
		//int[] coords = new int[]{pos.getX(),pos.getY(),pos.getZ()};
		//System.out.println("b"+coords[0]+" "+coords[1]+" "+coords[2]+" "+dV/*+" "+offset.getX()+" "+offset.getY()+" "+offset.getZ()*/);
		//world.setBlockState(new BlockPos(coords[0],coords[1]+10,+coords[2]), Blocks.STONE.getDefaultState());
		return coords;
	}
	private void update(World world){
		if(!world.isRemote){
			this.onNeighborChange(world);
		}
	}
}
