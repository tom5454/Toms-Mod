package com.tom.api.multipart;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.grid.IGrid;
import com.tom.api.grid.IGridDevice;
import com.tom.api.item.ModuleItem;
import com.tom.api.item.MultipartItem;
import com.tom.api.tileentity.ICable;
import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcmultipart.MCMultiPartMod;
import mcmultipart.client.multipart.AdvancedParticleManager;
import mcmultipart.microblock.IMicroblock;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.INormallyOccludingPart;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.MultipartRegistry;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;

public abstract class PartDuct<G extends IGrid<?,G>> extends MultipartTomsMod implements ISlottedPart, ITickable, ICable<G>, INormallyOccludingPart {
	protected AxisAlignedBB[] BOXES;
	protected static final AxisAlignedBB rotateFace(AxisAlignedBB box, EnumFacing facing) {
		switch (facing) {
		case DOWN:
		default:
			return box;
		case UP:
			return new AxisAlignedBB(box.minX, 1 - box.maxY, box.minZ, box.maxX, 1 - box.minY, box.maxZ);
		case NORTH:
			return new AxisAlignedBB(box.minX, box.minZ, box.minY, box.maxX, box.maxZ, box.maxY);
		case SOUTH:
			return new AxisAlignedBB(box.minX, box.minZ, 1 - box.maxY, box.maxX, box.maxZ, 1 - box.minY);
		case WEST:
			return new AxisAlignedBB(box.minY, box.minZ, box.minX, box.maxY, box.maxZ, box.maxX);
		case EAST:
			return new AxisAlignedBB(1 - box.maxY, box.minZ, box.minX, 1 - box.minY, box.maxZ, box.maxX);
		}
	}
	protected G grid;
	/**0:NoC, 1:Normal, 2:DuctPort, 3:Module*/
	private final int maxIntValueInProperties;
	public static class PropertyList{
		public final PropertyInteger UP;
		public final PropertyInteger DOWN;
		public final PropertyInteger NORTH;
		public final PropertyInteger EAST;
		public final PropertyInteger SOUTH;
		public final PropertyInteger WEST;
		public PropertyList(PropertyInteger up, PropertyInteger down, PropertyInteger north, PropertyInteger east,
				PropertyInteger south, PropertyInteger west) {
			UP = up;
			DOWN = down;
			NORTH = north;
			EAST = east;
			SOUTH = south;
			WEST = west;
		}
	}
	protected final PropertyList propertyList;
	private static Map<Integer, PropertyList> propertyListMap = new HashMap<Integer, PropertyList>();
	protected static final String GRID_TAG_NAME = "grid";
	private static final String MASTER_NBT_NAME = "isMaster";
	public final ItemStack pick;
	private boolean isMaster = false;
	private boolean firstStart = true;
	private boolean secondTick = false;
	protected IGridDevice<G> master;
	protected final ResourceLocation modelLocation;
	protected double size;
	private int suction = -1;
	public PartDuct(ItemStack drop, String modelL, double size, int maxStates) {
		super();
		if(drop == null){
			drop = new ItemStack(Blocks.STONE);
			TMLogger.bigWarn("Multipart created with null drop.");
		}
		this.pick = drop;
		this.modelLocation = new ResourceLocation(modelL);
		this.grid = this.constructGrid();
		this.size = size;
		updateBox();
		if(maxStates == -1){
			maxIntValueInProperties = -1;
			propertyList = null;
		}else{
			maxIntValueInProperties = Math.min(maxStates, 3);
			if(!propertyListMap.containsKey(maxIntValueInProperties)){
				PropertyInteger UP = PropertyInteger.create("up",0,maxIntValueInProperties);
				PropertyInteger DOWN = PropertyInteger.create("down",0,maxIntValueInProperties);
				PropertyInteger NORTH = PropertyInteger.create("north",0,maxIntValueInProperties);
				PropertyInteger EAST = PropertyInteger.create("east",0,maxIntValueInProperties);
				PropertyInteger SOUTH = PropertyInteger.create("south",0,maxIntValueInProperties);
				PropertyInteger WEST = PropertyInteger.create("west",0,maxIntValueInProperties);
				propertyListMap.put(maxIntValueInProperties, new PropertyList(UP, DOWN, NORTH, EAST, SOUTH, WEST));
			}
			propertyList = propertyListMap.get(maxIntValueInProperties);
		}
	}
	protected void updateBox(){
		updateBox = true;
		BOXES = new AxisAlignedBB[7];
		double start = 0.5 - size;
		double stop = 0.5 + size;
		BOXES[6] = new AxisAlignedBB(start, start, start, stop, stop, stop);
		for (int i = 0; i < 6; i++) {
			BOXES[i] = rotateFace(new AxisAlignedBB(start, 0, start, stop, start, stop), EnumFacing.getFront(i));
		}
	}
	public PartDuct(MultipartItem drop, String modelL, double size, int maxStates) {
		this(new ItemStack(drop),modelL, size, maxStates);
	}
	public PartDuct(MultipartItem drop, String modelL, int maxStates) {
		this(new ItemStack(drop),modelL, 0.25, maxStates);
	}
	private boolean neighborBlockChanged, packetQueued, updateBox, updateGrid;
	protected World worldObj;
	protected BlockPos pos;
	private byte connectionCache = 0;
	private byte invConnectionCache = 0;
	private byte mConnectionCache = 0;
	private byte e1ConnectionCache = 0, e2ConnectionCache = 0;
	public String createStringFromCache(){
		return connectionCache + "," + invConnectionCache + "," + mConnectionCache + "," + e1ConnectionCache + "," + e2ConnectionCache;
	}
	//private boolean requestUpdate;
	@Override
	public void update() {
		this.worldObj = this.getWorld2();
		this.pos = this.getPos2();
		if (neighborBlockChanged) {
			updateNeighborInfo(true);
			neighborBlockChanged = false;
		}
		if(!this.worldObj.isRemote){
			if(worldObj.getTotalWorldTime() % 40 == 0)neighborBlockChanged = true;
			if(firstStart){
				this.firstStart = false;
				this.secondTick = true;
				if(this.isMaster){
					grid.setMaster(this);
					grid.forceUpdateGrid(worldObj, this);
				}
				this.sendUpdatePacket(true);
			}
			if(secondTick){
				this.secondTick = false;
				if(master == null){
					grid.reloadGrid(worldObj, this);
				}
				this.sendUpdatePacket(true);
				this.markDirty();
			}
			if(this.master == null && !secondTick){
				if(updateGrid){
					this.constructGrid().forceUpdateGrid(worldObj, this);
					updateGrid = false;
				}else{
					updateGrid = true;
				}
			}
			if(packetQueued){
				packetQueued = false;
				sendUpdatePacket(true);
			}
		}
		this.updateEntity();
		if(this.isMaster){
			grid.updateGrid(getWorld2(), this);
		}
	}

	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return EnumSet.of(PartSlot.CENTER);
	}
	public void handlePacket(ByteBuf buf) {
		byte oldCC = connectionCache;
		byte oldICC = invConnectionCache;
		byte oldMCC = mConnectionCache;
		byte oe1 = e1ConnectionCache;
		byte oe2 = e2ConnectionCache;
		connectionCache = buf.readByte();
		invConnectionCache = buf.readByte();
		mConnectionCache = buf.readByte();
		e1ConnectionCache = buf.readByte();
		e2ConnectionCache = buf.readByte();
		boolean updateBox = buf.readBoolean();
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		boolean update = this.readFromPacket(tag);
		if(updateBox){
			updateBox();
		}
		if (update || oldCC != connectionCache || oldICC != invConnectionCache || oldMCC != mConnectionCache || e1ConnectionCache != oe1 || e2ConnectionCache != oe2) {
			markRenderUpdate();
		}
	}

	@Override
	public void markRenderUpdate(){
		onMarkRenderUpdate();
		super.markRenderUpdate();
	}

	protected void onMarkRenderUpdate() {}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		super.readUpdatePacket(buf);

		if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
			final ByteBuf buf2 = Unpooled.copiedBuffer(buf);

			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					handlePacket(buf2);
				}
			});
		} else {
			handlePacket(buf);
		}
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		super.writeUpdatePacket(buf);
		buf.writeByte(connectionCache);
		buf.writeByte(invConnectionCache);
		buf.writeByte(mConnectionCache);
		buf.writeByte(e1ConnectionCache);
		buf.writeByte(e2ConnectionCache);
		buf.writeBoolean(updateBox);
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToPacket(tag);
		ByteBufUtils.writeTag(buf, tag);
		updateBox = false;
	}
	@Override
	public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
		return getPick();
	}
	public ItemStack getPick(){
		return pick.copy();
	}
	@Override
	public List<ItemStack> getDrops() {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(getPick());
		addExtraDrops(drops);
		return drops;
	}
	@Override
	public float getHardness(PartMOP hit) {
		return 0.3F;
	}
	@Override
	public Material getMaterial() {
		return Material.GLASS;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state) {
		if(propertyList == null)throw new IllegalStateException("Missing method definition in " + getClass() + " getExtendedState(net.minecraft.block.state.IBlockState) THIS IS A BUG");
		return state
				.withProperty(propertyList.DOWN, getPropertyValue(EnumFacing.DOWN))
				.withProperty(propertyList.UP, getPropertyValue(EnumFacing.UP))
				.withProperty(propertyList.NORTH, getPropertyValue(EnumFacing.NORTH))
				.withProperty(propertyList.SOUTH, getPropertyValue(EnumFacing.SOUTH))
				.withProperty(propertyList.WEST, getPropertyValue(EnumFacing.WEST))
				.withProperty(propertyList.EAST, getPropertyValue(EnumFacing.EAST));
	}
	@Override
	public BlockStateContainer createBlockState() {
		IProperty<?>[] properties = getProperties();
		IUnlistedProperty<?>[] unlistedProperties = getUnlistedProperties();
		if(properties == null){
			properties = new IProperty[]{propertyList.DOWN,
					propertyList.UP,
					propertyList.NORTH,
					propertyList.SOUTH,
					propertyList.WEST,
					propertyList.EAST};
		}
		if(unlistedProperties != null && unlistedProperties.length > 0)
			return new ExtendedBlockState(MCMultiPartMod.multipart, properties, unlistedProperties);
		else
			return new BlockStateContainer(MCMultiPartMod.multipart, properties);
	}
	protected IUnlistedProperty<?>[] getUnlistedProperties(){
		return null;
	}
	protected IProperty<?>[] getProperties(){
		return new IProperty[]{propertyList.DOWN,
				propertyList.UP,
				propertyList.NORTH,
				propertyList.SOUTH,
				propertyList.WEST,
				propertyList.EAST};
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		addSelectionBoxes(list);
		return list.get(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(AdvancedParticleManager advancedEffectRenderer) {
		advancedEffectRenderer.addBlockDestroyEffects(getPos2(),
				Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(getExtendedState(MultipartRegistry.getDefaultState(this).getBaseState())));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(PartMOP partMOP, AdvancedParticleManager advancedEffectRenderer) {
		return true;
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		double start = 0.5 - size;
		double stop = 0.5 + size;
		list.add(new AxisAlignedBB(start, start, start, stop, stop, stop));
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(BOXES[6]);
		for (EnumFacing f : EnumFacing.VALUES) {
			if (connects(f) || connectsM(f) || connectsInv(f) || connectsE1(f) || connectsE2(f)) {
				list.add(BOXES[f.ordinal()]);
			}
			if(connectsInv(f) && this instanceof ICustomPartBounds){
				list.add(rotateFace(((ICustomPartBounds)this).getBoxForConnect(), f));
			}
		}
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if (BOXES[6].intersectsWith(mask)) {
			list.add(BOXES[6]);
		}
		for (EnumFacing f : EnumFacing.VALUES) {
			if (BOXES[f.ordinal()].intersectsWith(mask) && (connects(f) || connectsM(f) || connectsInv(f) || connectsE1(f) || connectsE2(f))) {
				list.add(BOXES[f.ordinal()]);
			}
			if(connectsInv(f) && this instanceof ICustomPartBounds){
				AxisAlignedBB b = rotateFace(((ICustomPartBounds)this).getBoxForConnect(), f);
				if(b.intersectsWith(mask))list.add(b);
			}
		}
	}

	public boolean connects(EnumFacing side) {
		return (connectionCache & (1 << side.ordinal())) != 0;
	}
	public boolean connectsInv(EnumFacing side) {
		return (invConnectionCache & (1 << side.ordinal())) != 0;
	}
	public boolean connectsM(EnumFacing side) {
		return (mConnectionCache & (1 << side.ordinal())) != 0;
	}
	public boolean connectsE1(EnumFacing side) {
		return (e1ConnectionCache & (1 << side.ordinal())) != 0;
	}
	public boolean connectsE2(EnumFacing side) {
		return (e2ConnectionCache & (1 << side.ordinal())) != 0;
	}
	/*@Override
	public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
		return layer == EnumWorldBlockLayer.CUTOUT;
	}*/
	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}
	@Override
	public ResourceLocation getModelPath() {
		//return this.modelLocation;
		return getType();
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		connectionCache = nbt.getByte("cc");
		isMaster = nbt.getBoolean(MASTER_NBT_NAME);
		if(this.isMaster)grid.importFromNBT(nbt.getCompoundTag(GRID_TAG_NAME));
		invConnectionCache = nbt.getByte("icc");
		mConnectionCache = nbt.getByte("mcc");
		updateBox();
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("cc", connectionCache);
		if(this.isMaster)nbt.setTag(GRID_TAG_NAME, grid.exportToNBT());
		nbt.setBoolean(MASTER_NBT_NAME, isMaster);
		nbt.setByte("icc", invConnectionCache);
		nbt.setByte("mcc", mConnectionCache);
		nbt.setByte("e1c", e1ConnectionCache);
		nbt.setByte("e2c", e2ConnectionCache);
		return nbt;
	}
	private void updateConnections(EnumFacing side) {
		if (side != null) {
			connectionCache &= ~(1 << side.ordinal());
			invConnectionCache &= ~(1 << side.ordinal());
			mConnectionCache &= ~(1 << side.ordinal());
			e1ConnectionCache &= ~(1 << side.ordinal());
			e2ConnectionCache &= ~(1 << side.ordinal());
			byte connectionType = internalConnects(side);
			if (connectionType > 0) {
				PartDuct<G> pipe = getDuct(getPos2().offset(side), side.getOpposite());
				if (pipe != null) {
					byte otherCT = pipe.internalConnects(side.getOpposite());
					if(otherCT != 1 && otherCT != 3 && otherCT != 4 && otherCT != 5)
						return;
				}

				if(connectionType == 1)
					connectionCache |= 1 << side.ordinal();
				else if(connectionType == 2)
					invConnectionCache |= 1 << side.ordinal();
				else if(connectionType == 3)
					mConnectionCache |= 1 << side.ordinal();
				else if(connectionType == 4)
					e1ConnectionCache |= 1 << side.ordinal();
				else if(connectionType == 5)
					e2ConnectionCache |= 1 << side.ordinal();
			}
		} else {
			for (EnumFacing facing : EnumFacing.VALUES) {
				updateConnections(facing);
			}
		}
	}
	private void updateNeighborInfo(boolean sendPacket) {
		if (!getWorld2().isRemote) {
			byte oc = connectionCache;
			byte oic = invConnectionCache;
			byte omc = mConnectionCache;
			byte oe1 = e1ConnectionCache;
			byte oe2 = e2ConnectionCache;

			for (EnumFacing dir : EnumFacing.VALUES) {
				updateConnections(dir);
			}
			if(master != null) master.updateState();
			G grid = this.constructGrid();
			grid.setMaster(master);
			grid.forceUpdateGrid(this.getWorld2(), this);
			if (sendPacket && (connectionCache != oc || invConnectionCache != oic || mConnectionCache != omc || e1ConnectionCache != oe1 || e2ConnectionCache != oe2)) {
				sendUpdatePacket();
			}
		}
	}
	private byte internalConnects(EnumFacing side) {
		ISlottedPart part = getContainer().getPartInSlot(PartSlot.getFaceSlot(side));
		if (part instanceof IMicroblock.IFaceMicroblock) {
			if (!((IMicroblock.IFaceMicroblock) part).isFaceHollow()) {
				return 0;
			}
		}
		PartModule<G> m = this.getModule(side);
		if(m != null && this.isModuleValid(side, m)){
			return 3;
		}
		if(TomsModUtils.getModule(getWorld2(), getPos2(), side) != null){
			return 0;
		}
		if (!TomsModUtils.occlusionTest(this, BOXES[side.ordinal()])) {
			return 0;
		}
		byte check = checkDuct(getPos2().offset(side), side.getOpposite());
		if (check != 0) {
			return check;
		}
		if (this instanceof ICustomPartBounds) {
			if(!TomsModUtils.occlusionTest(this, rotateFace(((ICustomPartBounds)this).getBoxForConnect(), side)))return 0;
		}
		TileEntity tile = getNeighbourTile(side);
		check = (byte) (tile != null ? isValidConnectionA(side, tile) : 0);
		return check;
	}

	public abstract boolean isValidConnection(EnumFacing side, TileEntity tile);
	public int isValidConnectionA(EnumFacing side, TileEntity tile){
		return isValidConnection(side, tile) ? 2 : 0;
	}
	public abstract void updateEntity();

	@SuppressWarnings("unchecked")
	public final PartDuct<G> getDuct(BlockPos blockPos, EnumFacing side) {
		IMultipartContainer container = MultipartHelper.getPartContainer(getWorld2(), blockPos);
		if (container == null) {
			return null;
		}

		if (side != null) {
			ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(side));
			if (part instanceof IMicroblock.IFaceMicroblock && !((IMicroblock.IFaceMicroblock) part).isFaceHollow()) {
				return null;
			}
		}

		ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
		try{
			if (part instanceof PartDuct<?> && canConnect((PartDuct<?>) part, side) != 0) {
				return (PartDuct<G>) part;
			} else {
				return null;
			}
		}catch (ClassCastException e){
			return null;
		}
	}
	public final byte checkDuct(BlockPos blockPos, EnumFacing side) {
		IMultipartContainer container = MultipartHelper.getPartContainer(getWorld2(), blockPos);
		if (container == null) {
			return 0;
		}

		if (side != null) {
			ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(side));
			if (part instanceof IMicroblock.IFaceMicroblock && !((IMicroblock.IFaceMicroblock) part).isFaceHollow()) {
				return 0;
			}
		}

		ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
		try{
			if (part instanceof PartDuct<?>) {
				return canConnect((PartDuct<?>) part, side);
			} else {
				return 0;
			}
		}catch (ClassCastException e){
			return 0;
		}
	}
	@SuppressWarnings("unchecked")
	protected byte canConnect(PartDuct<?> part, EnumFacing side){
		try{
			if (part instanceof PartDuct<?> && ItemStack.areItemStacksEqual(pick, ((PartDuct<G>)part).pick)) {
				return 1;
			} else {
				return 0;
			}
		}catch (ClassCastException e){
			return 0;
		}
	}
	public TileEntity getNeighbourTile(EnumFacing side) {
		return side != null ? getWorld2().getTileEntity(getPos2().offset(side)) : null;
	}
	@Override
	public void onAdded() {
		updateNeighborInfo(false);
		scheduleRenderUpdate();
	}
	protected void scheduleRenderUpdate() {
		getWorld2().markBlockRangeForRenderUpdate(getPos2(), getPos2());
	}

	@Override
	public void onNeighborBlockChange(Block block) {
		neighborBlockChanged = true;
	}
	@Override
	public void onPartChanged(IMultipart part) {
		neighborBlockChanged = true;
	}
	@Override
	public void onLoaded() {
		neighborBlockChanged = true;
	}
	@Override
	public boolean isMaster() {
		return isMaster;
	}

	@Override
	public void setMaster(IGridDevice<G> master, int size) {
		this.master = master;
		//boolean wasMaster = isMaster;
		isMaster = master == this;
		this.grid = master.getGrid();
		/*if(isMaster) {
        	grid.reloadGrid(getWorld(), this);
        }*/
	}
	@Override
	public G getGrid() {
		return grid;
	}
	@Override
	public IGridDevice<G> getMaster() {
		grid.forceUpdateGrid(getWorld2(), this);
		return master;
	}
	@Override
	public boolean isConnected(EnumFacing side) {
		return this.connects(side) || this.connectsInv(side) || connectsE1(side) || connectsE2(side);
	}
	public boolean readFromPacket(NBTTagCompound tag){
		return false;
	}
	public void writeToPacket(NBTTagCompound tag){

	}
	protected void updateFirst(){

	}
	@Override
	public final boolean isValidConnection(EnumFacing side){
		return TomsModUtils.occlusionTest(this, BOXES[side.ordinal()]);
		//return false;
	}
	@Override
	public void invalidateGrid(){
		this.master = null;
		this.isMaster = false;
		this.grid = this.constructGrid();
	}
	public abstract G constructGrid();
	public final List<PartModule<G>> getModules(){
		List<PartModule<G>> modules = new ArrayList<PartModule<G>>();
		for(EnumFacing f : EnumFacing.VALUES){
			PartModule<G> p = this.getModule(f);
			if(p != null){
				modules.add(p);
			}
		}
		return modules;
	}
	@SuppressWarnings("unchecked")
	public final PartModule<G> getModule(EnumFacing pos){
		try{
			PartModule<G> m = (PartModule<G>) TomsModUtils.getModule(getWorld2(), getPos2(), pos);
			if(m != null && m.grid.getClass() == this.grid.getClass()){
				return m;
			}
		}catch(Exception e){
			return null;
		}
		return null;
	}
	public void addExtraDrops(List<ItemStack> list){}

	public abstract int getPropertyValue(EnumFacing side);
	//return this.connectsM(side) ? 3 : (this.connectsInv(side) ? 2 : (this.connects(side) ? 1 : 0));
	public double getSize(){
		return size;
	}
	@Override
	public final boolean onActivated(EntityPlayer player, EnumHand hand,
			ItemStack stack, PartMOP hit) {
		if(!player.worldObj.isRemote && player.isSneaking() && CoreInit.isWrench(stack, player)){
			for (ItemStack stacks : getDrops()) {
				EntityItem item = new EntityItem(player.worldObj, pos.getX(), pos.getY(), pos.getZ(), stacks);
				item.setDefaultPickupDelay();
				player.worldObj.spawnEntityInWorld(item);
			}
			getContainer().removePart(this);
			return true;
		}
		Vec3d hitSub = hit.hitVec.subtract(new Vec3d(getPos2()));
		Vec3d hitPos = new Vec3d(hitSub.xCoord > 0.5 ? hitSub.xCoord-0.1 : hitSub.xCoord+0.1,
				hitSub.yCoord > 0.5 ? hitSub.yCoord-0.1 : hitSub.yCoord+0.1,
						hitSub.zCoord > 0.5 ? hitSub.zCoord-0.1 : hitSub.zCoord+0.1);
		List<AxisAlignedBB> boxList = new ArrayList<AxisAlignedBB>();
		this.addSelectionBoxes(boxList);
		AxisAlignedBB currentBox = null;
		for(AxisAlignedBB b : boxList){
			if(b != null && b.isVecInside(hitPos)){
				currentBox = b;
				break;
			}
		}
		if(currentBox != null){
			EnumFacing side = null;
			for(int i = 0;i<6;i++){
				EnumFacing f = EnumFacing.getFront(i);
				if(connectsInv(f) && this instanceof ICustomPartBounds){
					if(rotateFace(((ICustomPartBounds)this).getBoxForConnect(), f).equals(currentBox)){
						side = f;
					}else if(BOXES[i].equals(currentBox)){
						side = f;
					}
				}
			}
			if(side != null){
				if(!onConnectionBoxClicked(side, player, stack, hand)){
					if(stack != null && stack.getItem() instanceof ModuleItem){
						if(!getWorld2().isRemote){
							PartModule<?> m = ((ModuleItem)stack.getItem()).createPart(side);
							if(TomsModUtils.occlusionTest(this, m.BOX)){
								if(this.getContainer().canAddPart(m)){
									this.getContainer().addPart(m);
									stack.splitStack(1);
									player.inventoryContainer.detectAndSendChanges();
									markDirty();
									return true;
								}
							}
						}else return true;
					}else return false;
				}else return true;
			}
			return false;
		}
		return false;
	}
	@Override
	public void setSuctionValue(int suction){
		this.suction = suction;
	}
	@Override
	public int getSuctionValue(){
		return this.suction;
	}
	@Override
	public void updateState(){
		this.neighborBlockChanged = true;
	}
	@Override
	public void setGrid(G newGrid){
		this.grid = newGrid;
	}
	public boolean isModuleValid(EnumFacing pos, PartModule<G> module){
		return true;
	}
	public AxisAlignedBB getBoxForSide(EnumFacing side){
		return BOXES[side.ordinal()];
	}
	public boolean onConnectionBoxClicked(EnumFacing dir, EntityPlayer player, ItemStack stack, EnumHand hand){
		return false;
	}
	@Override
	public BlockPos getPos2(){
		return getPos();
	}
	@Override
	public World getWorld2(){
		return getWorld();
	}
	public void markForUpdate(){
		packetQueued = true;
	}
}
