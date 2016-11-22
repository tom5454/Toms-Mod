package com.tom.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import com.google.common.collect.Queues;

import com.tom.api.IValidationChecker;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.ISecuredTileEntity;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.defense.tileentity.TileEntityForceField;

import com.tom.core.block.BlockRubberWood;
import com.tom.core.block.BlockRubberWood.WoodType;

public class WorldHandler {
	private static final String CHUNK_LOADING_KEY = "chunkLoading";
	private static final String PLACED_BLOCKS_KEY = "placedBlocks";
	private static final int PLACED_BLOCK_LIFESPAN = 200;
	private static final Queue <Runnable> scheduledTasks = Queues.<Runnable>newArrayDeque();
	private static Map<Integer,WorldHandler> handlerMap = new HashMap<Integer, WorldHandler>();
	private static Logger log = LogManager.getLogger("Tom's Mod World Handler");
	private static File mainFolder;
	private static boolean serverStarted = false;
	private final int dimID;
	private final PlacedBlock PlacedBlockReader = new PlacedBlock(null, null, null);
	private List<Chunk> loadedChunks = new ArrayList<Chunk>();
	private List<BlockProtectionData> protectedAreasTickList = new ArrayList<BlockProtectionData>();
	private List<CustomBoundingBox> teleportProtectedAreas = new ArrayList<CustomBoundingBox>();
	public List<ChunkLoadingData> chunkLoading = new ArrayList<ChunkLoadingData>();
	public List<BlockProtectionData> protectedAreas = new ArrayList<BlockProtectionData>();
	public List<PlacedBlock> placedBlocks = new ArrayList<PlacedBlock>();
	public World worldObj;
	public static WorldHandler getWorldHandlerForDim(int dim){
		return handlerMap.get(dim);
	}
	public static void loadWorld(final World world){
		if(world.isRemote)return;
		final int dim = world.provider.getDimension();
		addTask(new Runnable() {

			@Override
			public void run() {
				log.info("Loading world: "+dim);
				if(handlerMap.containsKey(dim))
					handlerMap.remove(dim);
				handlerMap.put(dim, new WorldHandler(world));
				Configuration cfg = new Configuration(new File(mainFolder,"dim_"+dim+".tmcfg"));
				try{
					getWorldHandlerForDim(dim).readFromFile(cfg,world);
				}catch(Exception e){}
			}
		}, "Load World "+dim);
	}
	public static void saveWorld(int dim){
		//log.info("Saving world: "+dim);
		Configuration cfg = new Configuration(new File(mainFolder,"dim_"+dim+".tmcfg"));
		try{
			getWorldHandlerForDim(dim).writeToFile(cfg);
		}catch(Exception e){}
		cfg.save();
	}
	public static void unloadWorld(int dim){
		log.info("Unloading world: "+dim);
		saveWorld(dim);
		handlerMap.remove(dim);
	}
	public static boolean onEntitySpawning(PotentialSpawns spawns){
		return handlerMap.containsKey(spawns.getWorld().provider.getDimension()) ? handlerMap.get(spawns.getWorld().provider.getDimension()).onEntitySpawning(spawns.getPos(), spawns.getType(), spawns.getList()) : false;
	}
	public boolean onEntitySpawning(BlockPos pos, EnumCreatureType type, List<SpawnListEntry> list){
		return false;
	}
	public WorldHandler(World world) {
		this.dimID = world.provider.getDimension();
		this.worldObj = world;
	}
	public static void onServerStart(File file){
		log.info("Loading World Handler...");
		mainFolder = file;
		while(!scheduledTasks.isEmpty()){
			scheduledTasks.poll().run();
		}
		serverStarted = true;
		log.info("World Handler Loaded.");
	}
	public static void unloadChunkS(Chunk chunk){
		try{
			getWorldHandlerForDim(chunk.getWorld().provider.getDimension()).unloadChunk(chunk);
		}catch(Exception e){}
	}
	public static void loadChunkS(Chunk chunk){
		try {
			getWorldHandlerForDim(chunk.getWorld().provider.getDimension()).loadChunk(chunk);
		} catch (Exception e) {}
	}
	public static boolean breakBlockS(BreakEvent event){
		try {
			return getWorldHandlerForDim(event.getWorld().provider.getDimension()).breakBlock(event);
		} catch (Exception e) {}
		return false;
	}
	public static boolean placeBlockS(IBlockState bs, EntityPlayer placer, BlockPos pos){
		try {
			return getWorldHandlerForDim(placer.worldObj.provider.getDimension()).placeBlock(bs,placer, pos);
		} catch (Exception e) {}
		return false;
	}
	public void unloadChunk(Chunk chunk){
		loadedChunks.remove(chunk);
	}
	public void loadChunk(Chunk chunk){
		loadedChunks.add(chunk);
	}
	public boolean breakBlock(BreakEvent event){
		TileEntity tile = worldObj.getTileEntity(event.getPos());
		if(tile instanceof ISecurityStation){
			boolean canAccess = ((ISecurityStation) tile).canPlayerAccess(AccessType.RIGHTS_MODIFICATION, event.getPlayer());
			if(canAccess){
				return !this.canPlayerEdit(event.getPlayer(), event.getPos());
			}else{
				TomsModUtils.sendAccessDeniedMessageTo(event.getPlayer(), "tomsMod.chat.fieldSecurity");
				return true;
			}
		}
		if(tile instanceof ISecuredTileEntity){
			boolean canAccess = true;
			BlockPos securityStationPos = ((ISecuredTileEntity) tile).getSecurityStationPos();
			if(securityStationPos != null){
				TileEntity tileentity = worldObj.getTileEntity(securityStationPos);
				if(tileentity instanceof ISecurityStation){
					ISecurityStation te = (ISecurityStation) tileentity;
					canAccess = te.canPlayerAccess(AccessType.CONFIGURATION, event.getPlayer());
				}
			}
			if(canAccess){
				return !this.canPlayerEdit(event.getPlayer(), event.getPos());
			}else{
				TomsModUtils.sendAccessDeniedMessageTo(event.getPlayer(), "tomsMod.chat.fieldSecurity");
				return true;
			}
		}else if(tile instanceof TileEntityForceField){
			TileEntityForceField f = (TileEntityForceField) tile;
			return f.ownerPos == null;
		}
		return !this.canPlayerEdit(event.getPlayer(), event.getPos());
		//return event.state.getBlock() != Blocks.gravel;
	}
	public boolean placeBlock(IBlockState bs, EntityPlayer placer, BlockPos pos){
		boolean canEdit = canPlayerEdit(placer, pos);
		if(!canEdit && placeables.contains(new PlaceableBlock(bs))){
			this.placedBlocks.add(new PlacedBlock(pos, bs, placer.getName()));
			return false;
		}
		return !canEdit;
	}
	public void readFromFile(Configuration cfg,World world){
		Property p = cfg.get(Configuration.CATEGORY_GENERAL, CHUNK_LOADING_KEY, new String[]{});
		String[] values = p.getStringList();
		this.chunkLoading.clear();
		for(String s : values){
			try{
				NBTTagCompound tag = JsonToNBT.getTagFromJson(s);
				this.chunkLoading.add(ChunkLoadingData.readFromNBT(tag));
			}catch(Exception e){
				log.catching(e);
			}
		}
		p = cfg.get(Configuration.CATEGORY_GENERAL, PLACED_BLOCKS_KEY, new String[]{});
		values = p.getStringList();
		this.placedBlocks.clear();
		for(String s : values){
			try{
				NBTTagCompound tag = JsonToNBT.getTagFromJson(s);
				this.placedBlocks.add(PlacedBlockReader.readFromNBT(tag));
			}catch(Exception e){
				log.catching(e);
			}
		}
	}
	public void writeToFile(Configuration cfg){
		Property p = cfg.get(Configuration.CATEGORY_GENERAL, CHUNK_LOADING_KEY, new String[]{});
		List<String> values = new ArrayList<String>();
		for(int i = 0;i<this.chunkLoading.size();i++){
			NBTTagCompound tag = new NBTTagCompound();
			this.chunkLoading.get(i).writeToNBT(tag);
			values.add(tag.toString());
		}
		p.set(values.toArray(new String[]{}));
		p = cfg.get(Configuration.CATEGORY_GENERAL, PLACED_BLOCKS_KEY, new String[]{});
		values = new ArrayList<String>();
		for(int i = 0;i<this.placedBlocks.size();i++){
			NBTTagCompound tag = new NBTTagCompound();
			this.placedBlocks.get(i).writeToNBT(tag);
			values.add(tag.toString());
		}
		p.set(values.toArray(new String[]{}));
	}
	public static class ChunkLoadingData{
		public final int chunkX, chunkY, width, height;
		public final String owner;
		public static ChunkLoadingData readFromNBT(NBTTagCompound tag){
			return new ChunkLoadingData(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("w"), tag.getInteger("h"), tag.getString("owner"));
		}
		public void writeToNBT(NBTTagCompound tag){
			tag.setInteger("x", chunkX);
			tag.setInteger("y", chunkY);
			tag.setInteger("w", width);
			tag.setInteger("h", height);
			tag.setString("owner", owner);
		}
		public ChunkLoadingData(int chunkX, int chunkY, int width, int height, String owner) {
			this.chunkX = chunkX;
			this.chunkY = chunkY;
			this.width = width;
			this.height = height;
			this.owner = owner;
		}
	}
	public static class BlockProtectionData{
		public final ISecurityStation station;
		public final AxisAlignedBB box;
		public BlockProtectionData(ISecurityStation station,
				AxisAlignedBB box) {
			this.station = station;
			this.box = box;
		}
		public boolean isInside(double x, double y, double z) {
			return box.isVecInside(new Vec3d(x, y, z));
		}
	}
	public class PlacedBlock{
		public final BlockPos pos;
		public final IBlockState state;
		public final String placer;
		public int lifespan;
		public PlacedBlock(BlockPos pos, IBlockState state, String placer) {
			this.pos = pos;
			this.state = state;
			this.placer = placer;
			this.lifespan = PLACED_BLOCK_LIFESPAN;
		}
		@SuppressWarnings("deprecation")
		public PlacedBlock readFromNBT(NBTTagCompound tag){
			IBlockState state = null;
			try{
				state = findBlock(tag.getString("modid"), tag.getString("name")).getStateFromMeta(tag.getInteger("meta"));
			}catch(Exception e){log.catching(e);}
			PlacedBlock b = new PlacedBlock(TomsModUtils.readBlockPosFromNBT(tag), state,tag.getString("placer"));
			b.lifespan = tag.getInteger("life");
			return b;
		}
		public void writeToNBT(NBTTagCompound tag){
			TomsModUtils.writeBlockPosToNBT(tag, pos);
			tag.setString("placer", placer);
			tag.setInteger("meta", state.getBlock().getMetaFromState(state));
			ResourceLocation r = state.getBlock().delegate.name();
			tag.setString("modid", r.getResourceDomain());
			tag.setString("name", r.getResourcePath());
			tag.setInteger("life",this.lifespan);
		}
		public void update(){//[
			this.lifespan--;
			if(this.lifespan < 1){
				if(worldObj.getBlockState(pos).getBlock() == state.getBlock() && (!worldObj.isRemote)){
					state.getBlock().breakBlock(worldObj, pos, state);
					//state.getBlock().dropBlockAsItem(worldObj, pos, state, 0);
					EntityItem item = new EntityItem(worldObj, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(state.getBlock().getItemDropped(state, worldObj.rand, 0)));
					item.setNoPickupDelay();
					item.setAgeToCreativeDespawnTime();
					item.motionY = 0.6;
					worldObj.setBlockToAir(pos);
					worldObj.spawnEntityInWorld(item);
				}
				placedBlocks.remove(this);
			}
		}
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof PlacedBlock){
				PlacedBlock b = (PlacedBlock)obj;
				return b.pos.equals(pos) && b.placer.equals(placer) && b.state.getBlock() == this.state.getBlock();
			}
			return false;
		}
	}
	public static class PlaceableBlock{
		public final Block block;
		public final int meta;
		public PlaceableBlock(Block block, int meta) {
			this.block = block;
			this.meta = meta;
		}
		private PlaceableBlock(IBlockState bs) {
			this(bs.getBlock(),bs.getBlock().getMetaFromState(bs));
		}
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof PlaceableBlock){
				PlaceableBlock b = (PlaceableBlock) obj;
				return b.block == this.block && (b.meta == -1 || this.meta == -1 || this.meta == b.meta);
			}
			return false;
		}
	}
	public static final List<PlaceableBlock> placeables = new ArrayList<PlaceableBlock>();
	public static void addPlaceable(Block block){
		addPlaceable(block, -1);
	}
	public static Block findBlock(String modid, String name) {
		return Block.REGISTRY.getObject(new ResourceLocation(modid, name));
	}
	public static void addPlaceable(Block block, int meta){
		placeables.add(new PlaceableBlock(block, meta));
	}
	public static void onTick(World world, Phase phase){
		int dim = world.provider.getDimension();
		if(handlerMap.containsKey(dim)){
			handlerMap.get(dim).tick(world, phase);
		}
	}
	public void tick(World world, Phase phase){
		this.worldObj = world;
		switch(phase){
		case START:
			for(int i = 0;i<this.placedBlocks.size();i++){
				this.placedBlocks.get(i).update();
			}
			break;
		case END:
			protectedAreas.clear();
			protectedAreas.addAll(protectedAreasTickList);
			protectedAreasTickList.clear();
			break;
		default:
			break;
		}
	}
	public static boolean breakSpeed(BreakSpeed event){
		try {
			float f = getWorldHandlerForDim(event.getEntityPlayer().worldObj.provider.getDimension()).breakSpeed(event.getPos(), event.getEntityPlayer(), event.getOriginalSpeed());
			if(f < 0)return true;
			event.setNewSpeed(f);
		} catch (Exception e) {}
		return false;
	}
	public static boolean interact(PlayerInteractEvent event, Action a){
		try{
			return getWorldHandlerForDim(event.getEntityPlayer().worldObj.provider.getDimension()).interact(a, event.getPos(), event.getEntityPlayer(), event.getHand(), event.getFace());
		}catch(Exception e){}
		return false;
	}
	public boolean interact(Action action, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side){
		if(action == Action.LEFT_CLICK_BLOCK){
			IBlockState state = worldObj.getBlockState(pos);
			PlacedBlock b = new PlacedBlock(pos, state, player.getName());
			if(placedBlocks.contains(b)){
				if(worldObj.getBlockState(pos).getBlock() == state.getBlock() && (!player.worldObj.isRemote)){
					/*state.getBlock().breakBlock(worldObj, pos, state);
					//state.getBlock().dropBlockAsItem(worldObj, pos, state, 0);
					EntityItem item = new EntityItem(worldObj, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(state.getBlock().getItemDropped(state, worldObj.rand, 0)));
					item.setNoPickupDelay();
					item.setAgeToCreativeDespawnTime();
					item.motionY = 0.6;
					worldObj.setBlockToAir(pos);
					worldObj.spawnEntityInWorld(item);
					placedBlocks.remove(b);*/
					for(int i = 0;i<placedBlocks.size();i++){
						PlacedBlock bC = placedBlocks.get(i);
						if(bC.equals(b)){
							bC.lifespan = 1;
						}
					}
				}
			}
		}else if(action == Action.RIGHT_CLICK_BLOCK && (!player.worldObj.isRemote)){
			if(!useItem(player.worldObj, pos, player, hand, side)){
				TileEntity tile = worldObj.getTileEntity(pos);
				if(tile instanceof ISecuredTileEntity){
					boolean canAccess = true;
					BlockPos securityStationPos = ((ISecuredTileEntity) tile).getSecurityStationPos();
					if(securityStationPos != null){
						TileEntity tileentity = worldObj.getTileEntity(securityStationPos);
						if(tileentity instanceof ISecurityStation){
							ISecurityStation te = (ISecurityStation) tileentity;
							canAccess = te.canPlayerAccess(AccessType.CONFIGURATION, player);
						}
					}
					if(canAccess){
						return false;
					}else{
						TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
						return true;
					}
				}
			}
		}
		return false;
	}
	public float breakSpeed(BlockPos pos, EntityPlayer player, float originalSpeed){
		return this.getBlockData(pos) != null ? originalSpeed * 0.1F : originalSpeed;
		//return originalSpeed*20;
	}
	private BlockProtectionData getBlockData(BlockPos pos){
		for(int i = 0;i<this.protectedAreas.size();i++){
			BlockProtectionData d = this.protectedAreas.get(i);
			if(d.box.isVecInside(new Vec3d(pos))){
				return d;
			}
		}
		return null;
	}
	private boolean canPlayerEdit(EntityPlayer p, BlockPos pos){
		BlockProtectionData d = this.getBlockData(pos);
		if(d != null){
			return d.station.canPlayerAccess(AccessType.BLOCK_MODIFICATION, p);
		}
		return true;
	}
	public static enum Action{
		LEFT_CLICK_BLOCK, RIGHT_CLICK_BLOCK

	}
	public boolean useItem(World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side){
		ItemStack held = player.getHeldItem(hand);
		if(held != null && held.getItem() != null && held.getItem().getHarvestLevel(held, "axe", player, CoreInit.rubberWood.getDefaultState()) > 0){
			//ItemAxe axe = (ItemAxe) held.getItem(); BlockDoor
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() == CoreInit.rubberWood && canPlayerEdit(player, pos)){
				WoodType t = state.getValue(BlockRubberWood.TYPE);
				if(t.isHole() && t.getFacing() == side){
					world.setBlockState(pos, state.withProperty(BlockRubberWood.TYPE, state.getValue(BlockRubberWood.TYPE).getCut()));
					if(!player.capabilities.isCreativeMode)held.damageItem(1, player);
					SoundType st = Blocks.LOG.getSoundType(Blocks.LOG.getDefaultState(), world, BlockPos.ORIGIN, player);
					//player.playSound(st.getStepSound(), st.volume, st.pitch);
					worldObj.playSound(null, pos.getX(), pos.getY(), pos.getZ(), st.getPlaceSound(), SoundCategory.BLOCKS, st.volume, st.pitch - 0.2F);
					player.addExhaustion(0.5F);
					player.resetCooldown();
				}
			}
			/*}else if(held != null && held.getItem() == Items.BREAD){
			try{
				EnumFacing facing = TomsModUtils.getDirectionFacing(player, false);
				BlockPos pos2 = pos.up(2);
				StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(pos2.getX(),pos2.getY(), pos2.getZ(), 0, 0, 0, 11, 11, 10, facing);
				new VillageHouseScientist(new Start(), 0, world.rand, box, facing).addComponentParts(world, world.rand, box);
			}catch(Exception e){
				e.printStackTrace();
			}*/
		}
		return false;
	}
	public void addProtectedArea(AxisAlignedBB box, ISecurityStation station){
		protectedAreasTickList.add(new BlockProtectionData(station, box));
	}
	public int getDimensionId() {
		return dimID;
	}
	public static interface IWorldEventListener{
		void onTick(World world, Phase phase);
		boolean onEntitySpawning(BlockPos pos, EnumCreatureType type, List<SpawnListEntry> list);
	}
	public static void addTask(Runnable r, String type){
		if(serverStarted){
			r.run();
		}else{
			log.info("Task "+type+" has queued up.");
			scheduledTasks.add(r);
		}
	}
	public static void stopServer(){
		serverStarted = false;
	}
	public static void enderTeleportS(EnderTeleportEvent event) {
		try{
			getWorldHandlerForDim(event.getEntity().worldObj.provider.getDimension()).enderTeleport(event);
		}catch(Exception e){}
	}
	public void enderTeleport(EnderTeleportEvent event) {
		List<CustomBoundingBox> toRemove = new ArrayList<CustomBoundingBox>();
		for(int i = 0;i<teleportProtectedAreas.size();i++){
			if(teleportProtectedAreas.get(i).isValid()){
				boolean is = teleportProtectedAreas.get(i).isInside(event.getTargetX(), event.getTargetY(), event.getTargetZ());
				if(is){
					event.setCanceled(true);
					if(event.getEntityLiving() instanceof EntityPlayer){
						EntityPlayer player = (EntityPlayer) event.getEntityLiving();
						TomsModUtils.sendAccessDeniedMessageToWithTag(player, Items.ENDER_PEARL.getUnlocalizedName() + ".name");
					}
					break;
				}
			}else{
				toRemove.add(teleportProtectedAreas.get(i));
			}
		}
		for(int i = 0;i<toRemove.size();i++){
			teleportProtectedAreas.remove(toRemove.get(i));
		}
		if(!event.isCanceled()){
			for(int i = 0;i<protectedAreas.size();i++){
				boolean is = protectedAreas.get(i).isInside(event.getTargetX(), event.getTargetY(), event.getTargetZ());
				if(is){
					event.setCanceled(true);
					break;
				}
			}
		}
	}
	public static class CustomBoundingBox {
		private AxisAlignedBB box;
		private IValidationChecker validChecker;
		public boolean isValid(){
			return validChecker.isValid();
		}
		public boolean isInside(double x, double y, double z) {
			return box.isVecInside(new Vec3d(x, y, z));
		}
		public CustomBoundingBox(AxisAlignedBB box, IValidationChecker validChecker) {
			this.box = box;
			this.validChecker = validChecker;
		}
		public AxisAlignedBB getBox() {
			return box;
		}
		@Override
		public boolean equals(Object obj) {
			if(this == obj)return true;
			if(!(obj instanceof CustomBoundingBox))return false;
			return ((CustomBoundingBox)obj).box.equals(box);
		}
	}
	public static void registerNoTeleportZone(int dim, IValidationChecker checker, AxisAlignedBB box){
		try{
			getWorldHandlerForDim(dim).teleportProtectedAreas.add(new CustomBoundingBox(box, checker));
		}catch(Exception e){}
	}
}
