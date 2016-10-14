package com.tom.api.multipart;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.grid.IGrid;
import com.tom.api.grid.IGridDevice;
import com.tom.api.item.MultipartItem;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcmultipart.MCMultiPartMod;
import mcmultipart.client.multipart.AdvancedParticleManager;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.INormallyOccludingPart;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartRegistry;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;

public abstract class PartModule<G extends IGrid<?,G>> extends MultipartTomsMod implements ISlottedPart, ITickable, IDuctModule<G>, INormallyOccludingPart{
	protected AxisAlignedBB BOX;
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
	protected static final String GRID_TAG_NAME = "grid";
	private static final String MASTER_NBT_NAME = "isMaster";
	protected static final String FACING_NAME = "facing";
	protected static final PropertyDirection FACING = PropertyDirection.create(FACING_NAME);
	protected final PropertyInteger STATE;
	protected final int maxState;
	public final ItemStack pick;
	private boolean isMaster = false;
	private boolean firstStart = true;
	private boolean secondTick = false;
	protected IGridDevice<G> master;
	private boolean neighborBlockChanged;
	protected World worldObj;
	protected BlockPos pos;
	protected EnumFacing facing;
	protected final double size, deep;
	public final ResourceLocation model_location;
	private int suction = -1, clientState = 0, lastState = -1;
	public PartModule(ItemStack drop, double size, double deep, EnumFacing face, String modelLocation, int maxState) {
		super();
		this.pick = drop;
		this.facing = face;
		this.constructBox();
		this.grid = this.constructGrid();
		this.size = size;
		this.deep = deep;
		this.model_location = new ResourceLocation(modelLocation);
		STATE = PropertyInteger.create("state", 0, maxState);
		this.maxState = maxState;
	}
	public PartModule(MultipartItem drop, double size, double deep, EnumFacing face, String modelLocation, int maxState){
		this(new ItemStack(drop),size,deep,face, modelLocation, maxState);
	}

	@Override
	public void update() {
		this.worldObj = this.getWorld2();
		this.pos = this.getPos2();
		if (neighborBlockChanged) {
			updateNeighborInfo(true);
			neighborBlockChanged = false;
		}
		if(!this.worldObj.isRemote){
			boolean checkState = true;
			if(firstStart){
				this.firstStart = false;
				this.secondTick = true;
				if(this.isMaster){
					grid.setMaster(this);
					grid.forceUpdateGrid(worldObj, this);
				}
				this.sendUpdatePacket(true);
				checkState = false;
			}
			if(secondTick){
				this.secondTick = false;
				if(master == null){
					grid.reloadGrid(worldObj, this);
				}
				this.sendUpdatePacket(true);
				this.markDirty();
				checkState = false;
			}
			if(checkState){
				int state = getState();
				if(state != lastState){
					lastState = state;
					clientState = state;
					this.sendUpdatePacket(true);
				}
				if(this.master == null){
					grid.forceUpdateGrid(worldObj, this);
				}
			}
		}
		this.updateEntity();
		if(this.isMaster){
			grid.updateGrid(getWorld2(), this);
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
		return false;
	}
	public void readFromPacket(ByteBuf buf){

	}
	public void writeToPacket(ByteBuf buf){

	}
	protected void updateFirst(){

	}

	@Override
	public void invalidateGrid(){
		this.master = null;
		this.isMaster = false;
		this.grid = this.constructGrid();
	}
	public abstract G constructGrid();
	private void updateNeighborInfo(boolean sendPacket) {
		if (!getWorld2().isRemote) {
			//byte oc = connectionCache;

			//for (EnumFacing dir : EnumFacing.VALUES) {
			//updateConnections(dir);
			//}
			constructGrid().forceUpdateGrid(this.getWorld2(), this);
			//if (sendPacket && connectionCache != oc) {
			//sendUpdatePacket();
			//}
		}
	}
	public abstract void updateEntity();
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		grid.importFromNBT(nbt.getCompoundTag(GRID_TAG_NAME));
		isMaster = nbt.getBoolean(MASTER_NBT_NAME);
		this.facing = EnumFacing.VALUES[nbt.getInteger(FACING_NAME)];
		this.constructBox();
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setTag(GRID_TAG_NAME, grid.exportToNBT());
		nbt.setBoolean(MASTER_NBT_NAME, isMaster);
		nbt.setInteger(FACING_NAME, this.facing.ordinal());
		return nbt;
	}
	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		list.add(BOX);
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		this.constructBox();
		list.add(BOX);
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if(mask != null && mask.intersectsWith(BOX))list.add(BOX);
	}
	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return EnumSet.of(PartSlot.getFaceSlot(this.getFacing()));
	}
	public void handlePacket(ByteBuf buf) {
		this.facing = EnumFacing.VALUES[buf.readInt()];
		this.constructBox();
		int state = buf.readInt();
		clientState = Math.min(Math.max(state, 0), maxState);
		this.readFromPacket(buf);
		markRenderUpdate();
	}

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
		buf.writeInt(facing.ordinal());
		buf.writeInt(clientState);
		this.writeToPacket(buf);
	}
	@Override
	public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
		return pick.copy();
	}

	@Override
	public List<ItemStack> getDrops() {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(this.pick.copy());
		addExtraDrops(drops);
		return drops;
	}
	@Override
	public EnumFacing getFacing(){
		return facing != null ? facing : EnumFacing.DOWN;
	}
	/*@Override
	public EnumSet<PartSlot> getOccludedSlots() {
		return this.getSlotMask();
	}*/
	protected void constructBox(){
		double start = 0.5 - size;
		double stop = 0.5 + size;
		BOX = rotateFace(new AxisAlignedBB(start,0,start,stop,deep,stop), facing);
		//System.out.println(BOX);
	}
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(MCMultiPartMod.multipart,FACING,STATE);
	}
	@Override
	public IBlockState getExtendedState(IBlockState state) {
		return state.withProperty(FACING, this.getFacing()).withProperty(STATE, clientState);
	}
	@Override
	public Material getMaterial() {
		return Material.GLASS;
	}
	@Override
	public boolean isValidConnection(EnumFacing side) {
		return true;
	}
	@Override
	public ResourceLocation getModelPath() {
		return this.model_location;
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
	public float getHardness(PartMOP hit) {
		return 0.3F;
	}
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		addSelectionBoxes(list);
		return list.get(0);
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
	public void onRemoved() {
		//System.out.println("onRemoved");
		//new Throwable().printStackTrace();
	}
	/*@Override
	public void onClicked(EntityPlayer player, ItemStack stack, PartMOP hit) {
		//System.out.println("onClicked");
		//this.harvest(player, hit);
	}*/
	@SuppressWarnings("unchecked")
	public PartDuct<G> getBaseDuct(){
		ISlottedPart part = getContainer().getPartInSlot(PartSlot.CENTER);
		try{
			if (part instanceof PartDuct<?> && ((PartDuct<G>) part).grid.getClass() == grid.getClass()) {
				return (PartDuct<G>) part;
			} else {
				return null;
			}
		}catch (Exception e){
			return null;
		}
	}
	@Override
	public void setSuctionValue(int suction){
		this.suction  = suction;
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
	public abstract int getState();
	public void addExtraDrops(List<ItemStack> list){}
	@Override
	public BlockPos getPos2(){
		return getPos();
	}
	@Override
	public World getWorld2(){
		return getWorld();
	}
}
