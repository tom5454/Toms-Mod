package com.tom.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.OrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import com.tom.api.Capabilities;
import com.tom.api.IValidationChecker;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.IAccessPoint;
import com.tom.api.tileentity.IJammer;
import com.tom.api.tileentity.ISecuredTileEntity;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.defense.tileentity.TileEntityForceField;
import com.tom.lib.api.tileentity.IChunkLoader;
import com.tom.lib.api.tileentity.ICustomPacket;
import com.tom.lib.handler.IWorldHandler;
import com.tom.lib.handler.WorldHandler;
import com.tom.lib.network.messages.MessageTileBuf;
import com.tom.network.NetworkHandler;
import com.tom.util.TomsModUtils;

import com.tom.core.block.BlockRubberWood;
import com.tom.core.block.BlockRubberWood.WoodType;

public class TMWorldHandler implements IWorldHandler {
	private static final String ID = "tmhandler";
	// private static final String CHUNK_LOADING_KEY = "chunkLoading";
	private static final String PLACED_BLOCKS_KEY = "placedBlocks";
	private static Logger log = LogManager.getLogger("Tom's Mod World Handler");
	private final int dimID;
	private final PlacedBlock PlacedBlockReader = new PlacedBlock(null, null, null);
	private List<BlockProtectionData> protectedAreasTickList = new ArrayList<>();
	private List<CustomBoundingBox> teleportProtectedAreas = new ArrayList<>();
	public List<ChunkLoadingData> chunkLoading = new ArrayList<>();
	public List<BlockProtectionData> protectedAreas = new ArrayList<>();
	public List<PlacedBlock> placedBlocks = new ArrayList<>();
	public World worldObj;
	public Set<IAccessPoint> accessPoints = new HashSet<>();
	public Set<IJammer> jammers = new HashSet<>();
	public Set<ChunkPos> dirty = new HashSet<>();

	// private static Map<Integer, List<Ticket>> chunkTickets = new HashMap<>();
	public static TMWorldHandler getWorldHandlerForDim(int dim) {
		WorldHandler wh = WorldHandler.getWorldHandlerForDim(dim);
		return wh == null ? null : (TMWorldHandler) wh.getHandler(ID);
	}

	public static void init() {
		log.info("Loading World Handler...");
		WorldHandler.registerHandler(ID, TMWorldHandler::new);
		ForgeChunkManager.setForcedChunkLoadingCallback(CoreInit.modInstance, new OrderedLoadingCallback() {

			@Override
			public void ticketsLoaded(List<Ticket> tickets, World world) {

			}

			@Override
			public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount) {
				log.info("Loading chunk loading tickets...");
				List<Ticket> ticketsRet = new ArrayList<>();
				for (Ticket t : tickets) {
					BlockPos pos = TomsModUtils.readBlockPosFromNBT(t.getModData());
					if (pos != null) {
						TileEntity tile = world.getTileEntity(pos);
						if (tile instanceof IChunkLoader) {
							ticketsRet.add(t);
							((IChunkLoader) tile).setTicket(t);
						}
					}
				}
				return ticketsRet;
			}

		});
	}

	@Override
	public boolean onEntitySpawning(BlockPos pos, EnumCreatureType type, List<SpawnListEntry> list) {
		return false;
	}

	public TMWorldHandler(World world) {
		this.dimID = world.provider.getDimension();
		this.worldObj = world;
	}

	public static boolean breakBlockS(BreakEvent event) {
		try {
			return getWorldHandlerForDim(event.getWorld().provider.getDimension()).breakBlock(event);
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean placeBlockS(IBlockState bs, EntityPlayer placer, BlockPos pos) {
		try {
			return getWorldHandlerForDim(placer.world.provider.getDimension()).placeBlock(bs, placer, pos);
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public void unloadChunk(Chunk chunk) {
	}

	@Override
	public void loadChunk(Chunk chunk) {
	}

	public boolean breakBlock(BreakEvent event) {
		TileEntity tile = worldObj.getTileEntity(event.getPos());
		if (tile instanceof ISecurityStation) {
			boolean canAccess = ((ISecurityStation) tile).canPlayerAccess(AccessType.RIGHTS_MODIFICATION, event.getPlayer());
			if (canAccess) {
				return !this.canPlayerEdit(event.getPlayer(), event.getPos());
			} else {
				TomsModUtils.sendAccessDeniedMessageTo(event.getPlayer(), "tomsMod.chat.fieldSecurity");
				return true;
			}
		}
		if (tile instanceof ISecuredTileEntity) {
			boolean canAccess = true;
			BlockPos securityStationPos = ((ISecuredTileEntity) tile).getSecurityStationPos();
			if (securityStationPos != null) {
				TileEntity tileentity = worldObj.getTileEntity(securityStationPos);
				if (tileentity instanceof ISecurityStation) {
					ISecurityStation te = (ISecurityStation) tileentity;
					canAccess = te.canPlayerAccess(AccessType.CONFIGURATION, event.getPlayer());
				}
			}
			if (canAccess) {
				return !this.canPlayerEdit(event.getPlayer(), event.getPos());
			} else {
				TomsModUtils.sendAccessDeniedMessageTo(event.getPlayer(), "tomsMod.chat.fieldSecurity");
				return true;
			}
		} else if (tile instanceof TileEntityForceField) {
			TileEntityForceField f = (TileEntityForceField) tile;
			return f.ownerPos == null;
		}
		return !this.canPlayerEdit(event.getPlayer(), event.getPos());
		// return event.state.getBlock() != Blocks.gravel;
	}

	public boolean placeBlock(IBlockState bs, EntityPlayer placer, BlockPos pos) {
		boolean canEdit = canPlayerEdit(placer, pos);
		if (!canEdit && placeables.contains(new PlaceableBlock(bs))) {
			this.placedBlocks.add(new PlacedBlock(pos, bs, placer.getName()));
			return false;
		}
		return !canEdit;
	}

	@Override
	public void load(NBTTagCompound cfg) {
		if (cfg == null)
			return;
		// NBTTagList values = cfg.getTagList(CHUNK_LOADING_KEY, 10);
		/*this.chunkLoading.clear();
		for(int i = 0;i<values.tagCount();i++){
			try{
				this.chunkLoading.add(ChunkLoadingData.readFromNBT(values.getCompoundTagAt(i)));
			}catch(Exception e){
				log.catching(e);
			}
		}*/
		NBTTagList values = cfg.getTagList(PLACED_BLOCKS_KEY, 10);
		this.placedBlocks.clear();
		for (int i = 0;i < values.tagCount();i++) {
			try {
				this.placedBlocks.add(PlacedBlockReader.readFromNBT(values.getCompoundTagAt(i)));
			} catch (Exception e) {
				log.catching(e);
			}
		}
	}

	@Override
	public void save(NBTTagCompound cfg) {
		NBTTagList values = new NBTTagList();
		/*for(int i = 0;i<this.chunkLoading.size();i++){
			NBTTagCompound tag = new NBTTagCompound();
			this.chunkLoading.get(i).writeToNBT(tag);
			values.appendTag(tag);
		}
		cfg.setTag(CHUNK_LOADING_KEY, values);
		values = new NBTTagList();*/
		for (int i = 0;i < this.placedBlocks.size();i++) {
			NBTTagCompound tag = new NBTTagCompound();
			this.placedBlocks.get(i).writeToNBT(tag);
			values.appendTag(tag);
		}
		cfg.setTag(PLACED_BLOCKS_KEY, values);
	}

	public static class ChunkLoadingData {
		public final int chunkX, chunkY, width, height;
		public final String owner;

		public static ChunkLoadingData readFromNBT(NBTTagCompound tag) {
			return new ChunkLoadingData(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("w"), tag.getInteger("h"), tag.getString("owner"));
		}

		public void writeToNBT(NBTTagCompound tag) {
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

	public static class BlockProtectionData {
		public final ISecurityStation station;
		public final AxisAlignedBB box;

		public BlockProtectionData(ISecurityStation station, AxisAlignedBB box) {
			this.station = station;
			this.box = box;
		}

		public boolean isInside(double x, double y, double z) {
			return box.contains(new Vec3d(x, y, z));
		}
	}

	public class PlacedBlock {
		public final BlockPos pos;
		public final IBlockState state;
		public final String placer;
		public int lifespan;
		public boolean invalid;

		public PlacedBlock(BlockPos pos, IBlockState state, String placer) {
			this.pos = pos;
			this.state = state;
			this.placer = placer;
			this.lifespan = Config.placedBlockLifespan;
		}

		@SuppressWarnings("deprecation")
		public PlacedBlock readFromNBT(NBTTagCompound tag) {
			IBlockState state = null;
			try {
				state = findBlock(tag.getString("modid"), tag.getString("name")).getStateFromMeta(tag.getInteger("meta"));
			} catch (Exception e) {
				log.catching(e);
			}
			PlacedBlock b = new PlacedBlock(TomsModUtils.readBlockPosFromNBT(tag), state, tag.getString("placer"));
			b.lifespan = tag.getInteger("life");
			return b;
		}

		public void writeToNBT(NBTTagCompound tag) {
			TomsModUtils.writeBlockPosToNBT(tag, pos);
			tag.setString("placer", placer);
			tag.setInteger("meta", state.getBlock().getMetaFromState(state));
			ResourceLocation r = state.getBlock().delegate.name();
			tag.setString("modid", r.getResourceDomain());
			tag.setString("name", r.getResourcePath());
			tag.setInteger("life", this.lifespan);
		}

		public void update() {
			if (lifespan < 0)
				return;
			this.lifespan--;
			if (this.lifespan < 1) {
				if (worldObj.getBlockState(pos).getBlock() == state.getBlock() && (!worldObj.isRemote)) {
					state.getBlock().breakBlock(worldObj, pos, state);
					// state.getBlock().dropBlockAsItem(worldObj, pos, state,
					// 0);
					EntityItem item = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(state.getBlock().getItemDropped(state, worldObj.rand, 0)));
					item.setNoPickupDelay();
					item.setAgeToCreativeDespawnTime();
					item.motionY = 0.6;
					worldObj.setBlockToAir(pos);
					worldObj.spawnEntity(item);
				}
				invalid = true;
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PlacedBlock) {
				PlacedBlock b = (PlacedBlock) obj;
				return b.pos.equals(pos) && b.placer.equals(placer) && b.state.getBlock() == this.state.getBlock();
			}
			return false;
		}
	}

	public static class PlaceableBlock {
		public final Block block;
		public final int meta;

		public PlaceableBlock(Block block, int meta) {
			this.block = block;
			this.meta = meta;
		}

		private PlaceableBlock(IBlockState bs) {
			this(bs.getBlock(), bs.getBlock().getMetaFromState(bs));
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PlaceableBlock) {
				PlaceableBlock b = (PlaceableBlock) obj;
				return b.block == this.block && (b.meta == -1 || this.meta == -1 || this.meta == b.meta);
			}
			return false;
		}
	}

	public static final List<PlaceableBlock> placeables = new ArrayList<>();

	public static void addPlaceable(Block block) {
		addPlaceable(block, -1);
	}

	public static Block findBlock(String modid, String name) {
		return Block.REGISTRY.getObject(new ResourceLocation(modid, name));
	}

	public static void addPlaceable(Block block, int meta) {
		placeables.add(new PlaceableBlock(block, meta));
	}

	@Override
	public void updatePre(World world) {
		this.worldObj = world;
		Iterator<PlacedBlock> iter = placedBlocks.iterator();
		while(iter.hasNext()){
			PlacedBlock p = iter.next();
			p.update();
			if(p.invalid){
				iter.remove();
			}
		}
		world.profiler.startSection("[Tom's Mod] Remove Invalid Entries from Lists");
		if(world.getTotalWorldTime() % 20 == 0){
			Iterator<IAccessPoint> itr = accessPoints.iterator();
			while(itr.hasNext()){
				if(!itr.next().isAccessPointValid())itr.remove();
			}
			Iterator<IJammer> itr2 = jammers.iterator();
			while(itr2.hasNext()){
				if(!itr2.next().isValid())itr2.remove();
			}
		}
		world.profiler.endSection();
	}

	@Override
	public void updatePost(World world) {
		protectedAreas.clear();
		protectedAreas.addAll(protectedAreasTickList);
		protectedAreasTickList.clear();
		if(!dirty.isEmpty()){
			dirty.forEach(c -> {
				List<EntityPlayerMP> l = EventHandler.watch.get(c);
				if(l != null && !l.isEmpty())l.forEach(p -> sendTo(c, p));
			});
			dirty.clear();
		}
	}

	private void sendTo(ChunkPos c, EntityPlayerMP p) {
		Chunk chunk = worldObj.getChunkFromChunkCoords(c.x, c.z);
		chunk.getTileEntityMap().values().stream().filter(t -> t instanceof ICustomPacket).map(t -> new MessageTileBuf(((ICustomPacket)t))).forEach(m -> NetworkHandler.sendTo(m, p));
	}

	public static boolean breakSpeed(BreakSpeed event) {
		try {
			float f = getWorldHandlerForDim(event.getEntityPlayer().world.provider.getDimension()).breakSpeed(event.getPos(), event.getEntityPlayer(), event.getOriginalSpeed());
			if (f < 0)
				return true;
			event.setNewSpeed(f);
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean interact(PlayerInteractEvent event, Action a) {
		try {
			return getWorldHandlerForDim(event.getEntityPlayer().world.provider.getDimension()).interact(a, event.getPos(), event.getEntityPlayer(), event.getHand(), event.getFace());
		} catch (Exception e) {
		}
		return false;
	}

	public boolean interact(Action action, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side) {
		if (action == Action.LEFT_CLICK_BLOCK) {
			IBlockState state = worldObj.getBlockState(pos);
			PlacedBlock b = new PlacedBlock(pos, state, player.getName());
			if (placedBlocks.contains(b)) {
				if (worldObj.getBlockState(pos).getBlock() == state.getBlock() && (!player.world.isRemote)) {
					/*state.getBlock().breakBlock(worldObj, pos, state);
					//state.getBlock().dropBlockAsItem(worldObj, pos, state, 0);
					EntityItem item = new EntityItem(worldObj, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(state.getBlock().getItemDropped(state, worldObj.rand, 0)));
					item.setNoPickupDelay();
					item.setAgeToCreativeDespawnTime();
					item.motionY = 0.6;
					worldObj.setBlockToAir(pos);
					worldObj.spawnEntityInWorld(item);
					placedBlocks.remove(b);*/
					for (int i = 0;i < placedBlocks.size();i++) {
						PlacedBlock bC = placedBlocks.get(i);
						if (bC.equals(b)) {
							bC.lifespan = 1;
						}
					}
				}
			}
		} else if (action == Action.RIGHT_CLICK_BLOCK && (!player.world.isRemote)) {
			if (!useItem(player.world, pos, player, hand, side)) {
				ISecuredTileEntity tile = Capabilities.getSecuredTileEntityAt(worldObj, pos);
				if (tile != null) {
					boolean canAccess = true;
					BlockPos securityStationPos = tile.getSecurityStationPos();
					if (securityStationPos != null) {
						TileEntity tileentity = worldObj.getTileEntity(securityStationPos);
						if (tileentity instanceof ISecurityStation) {
							ISecurityStation te = (ISecurityStation) tileentity;
							canAccess = te.canPlayerAccess(AccessType.CONFIGURATION, player);
						}
					}
					if (canAccess || (player.capabilities.isCreativeMode && TomsModUtils.getServer().getPlayerList().canSendCommands(player.getGameProfile()))) {
						return false;
					} else {
						TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
						return true;
					}
				}
			}
		}
		return false;
	}

	public float breakSpeed(BlockPos pos, EntityPlayer player, float originalSpeed) {
		return this.getBlockData(pos) != null ? originalSpeed * 0.1F : originalSpeed;
		// return originalSpeed*20;
	}

	private BlockProtectionData getBlockData(BlockPos pos) {
		for (int i = 0;i < this.protectedAreas.size();i++) {
			BlockProtectionData d = this.protectedAreas.get(i);
			if (d.box.contains(new Vec3d(pos))) { return d; }
		}
		return null;
	}

	private boolean canPlayerEdit(EntityPlayer p, BlockPos pos) {
		BlockProtectionData d = this.getBlockData(pos);
		if (d != null) { return d.station.canPlayerAccess(AccessType.BLOCK_MODIFICATION, p); }
		return true;
	}

	public static enum Action {
		LEFT_CLICK_BLOCK, RIGHT_CLICK_BLOCK
	}

	public boolean useItem(World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side) {
		ItemStack held = player.getHeldItem(hand);
		if (held != null && held.getItem() != null && held.getItem().getHarvestLevel(held, "axe", player, CoreInit.rubberWood.getDefaultState()) > 0) {
			// ItemAxe axe = (ItemAxe) held.getItem(); BlockDoor
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() == CoreInit.rubberWood && canPlayerEdit(player, pos)) {
				WoodType t = state.getValue(BlockRubberWood.TYPE);
				if (t.isHole() && t.getFacing() == side) {
					world.setBlockState(pos, state.withProperty(BlockRubberWood.TYPE, state.getValue(BlockRubberWood.TYPE).getCut()));
					if (!player.capabilities.isCreativeMode)
						held.damageItem(1, player);
					SoundType st = Blocks.LOG.getSoundType(Blocks.LOG.getDefaultState(), world, BlockPos.ORIGIN, player);
					worldObj.playSound(null, pos.getX(), pos.getY(), pos.getZ(), st.getPlaceSound(), SoundCategory.BLOCKS, st.volume, st.pitch - 0.2F);
					player.addExhaustion(0.5F);
					player.resetCooldown();
				} else if (t == WoodType.NORMAL && player.capabilities.isCreativeMode && side.getAxis() != Axis.Y) {
					world.setBlockState(pos, state.withProperty(BlockRubberWood.TYPE, BlockRubberWood.WoodType.getNorm(side.ordinal() - 2)));
					SoundType st = Blocks.LOG.getSoundType(Blocks.LOG.getDefaultState(), world, BlockPos.ORIGIN, player);
					worldObj.playSound(null, pos.getX(), pos.getY(), pos.getZ(), st.getPlaceSound(), SoundCategory.BLOCKS, st.volume, st.pitch - 0.2F);
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

	public void addProtectedArea(AxisAlignedBB box, ISecurityStation station) {
		protectedAreasTickList.add(new BlockProtectionData(station, box));
	}

	public int getDimensionId() {
		return dimID;
	}

	public static interface IWorldEventListener {
		void onTick(World world, Phase phase);

		boolean onEntitySpawning(BlockPos pos, EnumCreatureType type, List<SpawnListEntry> list);
	}

	public static void enderTeleportS(EnderTeleportEvent event) {
		try {
			getWorldHandlerForDim(event.getEntity().world.provider.getDimension()).enderTeleport(event);
		} catch (Exception e) {
		}
	}

	public void enderTeleport(EnderTeleportEvent event) {
		List<CustomBoundingBox> toRemove = new ArrayList<>();
		for (int i = 0;i < teleportProtectedAreas.size();i++) {
			if (teleportProtectedAreas.get(i).isValid()) {
				boolean is = teleportProtectedAreas.get(i).isInside(event.getTargetX(), event.getTargetY(), event.getTargetZ());
				if (is) {
					event.setCanceled(true);
					if (event.getEntityLiving() instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) event.getEntityLiving();
						TomsModUtils.sendAccessDeniedMessageToWithTag(player, Items.ENDER_PEARL.getUnlocalizedName() + ".name");
					}
					break;
				}
			} else {
				toRemove.add(teleportProtectedAreas.get(i));
			}
		}
		for (int i = 0;i < toRemove.size();i++) {
			teleportProtectedAreas.remove(toRemove.get(i));
		}
		if (!event.isCanceled()) {
			for (int i = 0;i < protectedAreas.size();i++) {
				boolean is = protectedAreas.get(i).isInside(event.getTargetX(), event.getTargetY(), event.getTargetZ());
				if (is) {
					event.setCanceled(true);
					break;
				}
			}
		}
	}

	public static class CustomBoundingBox {
		private AxisAlignedBB box;
		private IValidationChecker validChecker;

		public boolean isValid() {
			return validChecker.isValid();
		}

		public boolean isInside(double x, double y, double z) {
			return box.contains(new Vec3d(x, y, z));
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
			if (this == obj)
				return true;
			if (!(obj instanceof CustomBoundingBox))
				return false;
			return ((CustomBoundingBox) obj).box.equals(box);
		}
	}

	public static void registerNoTeleportZone(int dim, IValidationChecker checker, AxisAlignedBB box) {
		try {
			getWorldHandlerForDim(dim).teleportProtectedAreas.add(new CustomBoundingBox(box, checker));
		} catch (Exception e) {
		}
	}

	public static List<IAccessPoint> getAccessPoints(World world, double x, double y, double z){
		return getWorldHandlerForDim(world.provider.getDimension()).getAccessPoints(x, y, z);
	}

	public List<IAccessPoint> getAccessPoints(double x, double y, double z){
		return !jammers.stream().filter(IJammer::isValid).anyMatch(j -> j.isAccessible(x, y, z)) ? accessPoints.stream().filter(IAccessPoint::isAccessPointValid).filter(a -> a.isAccessible(x, y, z)).collect(Collectors.toList()) : Collections.emptyList();
	}

	public static void addAccessPoint(IAccessPoint point){
		getWorldHandlerForDim(point.getWorld2().provider.getDimension()).accessPoints.add(point);
	}
	public static void addJammer(IJammer jammer) {
		getWorldHandlerForDim(jammer.getWorld2().provider.getDimension()).jammers.add(jammer);
	}
	public static void removeJammer(IJammer jammer) {
		getWorldHandlerForDim(jammer.getWorld2().provider.getDimension()).jammers.remove(jammer);
	}

	public void markDirty(ChunkPos chunk) {
		dirty.add(chunk);
	}

	public static void markDirty(World world, ChunkPos chunk) {
		getWorldHandlerForDim(world.provider.getDimension()).markDirty(chunk);
	}
	public static void markDirty(World world, BlockPos pos) {
		markDirty(world, new ChunkPos(pos));
	}

	@Override
	public String getID() {
		return ID;
	}
}
