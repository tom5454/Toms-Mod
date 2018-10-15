package com.tom.core.tileentity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;

import com.tom.core.CoreInit;
import com.tom.util.BlockData;
import com.tom.util.TomsModUtils;

public class TileEntityHidden extends TileEntity {
	public static final Map<String, BiFunction<Integer, Integer, Integer>> ID_MAPPER_FUNC_LIST = new HashMap<>();
	public BlockPos master;
	public ItemStack drop = ItemStack.EMPTY;
	public BlockProperties blockProperties;
	public ItemStack pick = ItemStack.EMPTY;
	public Block blockOld, blockRender;
	public int metaOld, metaRender;
	public NBTTagCompound tileOld;
	public boolean killed = false;
	public int y;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound tag = new NBTTagCompound();
		drop.writeToNBT(tag);
		compound.setTag("drop", tag);
		tag = new NBTTagCompound();
		pick.writeToNBT(tag);
		compound.setTag("pick", tag);
		tag = new NBTTagCompound();
		if (blockProperties != null)
			blockProperties.writeToNBT(tag);
		compound.setTag("properties", tag);
		compound.setBoolean("hasMaster", master != null);
		compound.setInteger("layer", y);
		if (master != null) {
			tag = new NBTTagCompound();
			tag.setInteger("x", master.getX());
			tag.setInteger("y", master.getY());
			tag.setInteger("z", master.getZ());
			compound.setTag("masterPos", tag);
		}
		tag = new NBTTagCompound();
		if (tileOld != null) {
			tag.setTag("data", tileOld);
			tag.setBoolean("hasTile", true);
		} else {
			tag.setBoolean("hasTile", false);
		}
		tag.setInteger("metaOld", metaOld);
		tag.setString("blockOld", blockOld != null ? blockOld.delegate.name().toString() : "");
		compound.setTag("oldTile", tag);
		compound.setString("blockRender", blockRender != null ? blockRender.delegate.name().toString() : "");
		compound.setInteger("metaRender", metaRender);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		drop = TomsModUtils.loadItemStackFromNBT(compound.getCompoundTag("drop"));
		pick = TomsModUtils.loadItemStackFromNBT(compound.getCompoundTag("pick"));
		NBTTagCompound t = compound.getCompoundTag("properties");
		if (t.hasNoTags()) {
			blockProperties = null;
		} else
			blockProperties = BlockProperties.load(t);
		if (compound.getBoolean("hasMaster")) {
			t = compound.getCompoundTag("masterPos");
			master = new BlockPos(t.getInteger("x"), t.getInteger("y"), t.getInteger("z"));
		} else {
			master = null;
		}
		t = compound.getCompoundTag("oldTile");
		metaOld = t.getInteger("metaOld");
		blockOld = Block.REGISTRY.getObject(new ResourceLocation(t.getString("blockOld")));
		if (t.getBoolean("hasTile")) {
			tileOld = t.getCompoundTag("data");
		}
		y = compound.getInteger("layer");
		metaRender = compound.getInteger("metaRender");
		blockRender = Block.REGISTRY.getObject(new ResourceLocation(compound.getString("blockRender")));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (this.master != null) {
			TileEntity master = world.getTileEntity(this.master);
			return master != null && master instanceof ILinkableCapabilities ? ((ILinkableCapabilities) master).hasCapability(capability, facing, pos, blockProperties != null ? blockProperties.getID(y) : 0) : super.hasCapability(capability, facing);
		} else {
			return super.hasCapability(capability, facing);
		}
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (this.master != null) {
			TileEntity master = world.getTileEntity(this.master);
			return master != null && master instanceof ILinkableCapabilities ? ((ILinkableCapabilities) master).getCapability(capability, facing, pos, blockProperties != null ? blockProperties.getID(y) : 0) : super.getCapability(capability, facing);
		} else {
			return super.getCapability(capability, facing);
		}
	}

	public static interface ILinkableCapabilities {
		boolean hasCapability(Capability<?> capability, EnumFacing facing, BlockPos from, int id);

		<T> T getCapability(Capability<T> capability, EnumFacing facing, BlockPos from, int id);
	}

	public static void place(World world, BlockPos pos, BlockPos master, ItemStack pick, int y, BlockData data) {
		IBlockState old = world.getBlockState(pos);
		TileEntity oldTile = world.getTileEntity(pos);
		Item item = old.getBlock().getItemDropped(old, world.rand, 0);
		ItemStack drop = item != null ? new ItemStack(item, old.getBlock().quantityDropped(old, 0, world.rand), old.getBlock().damageDropped(old)) : null;
		int metaOld = old.getBlock().getMetaFromState(old);
		BlockProperties properties = data.getProperties(old.getBlock(), metaOld);
		if (properties != null) {
			if (properties.renderBlock) {
				if (properties.tesrID >= 0)
					world.setBlockState(pos, CoreInit.blockHiddenRenderOldTESR.getDefaultState());
				else
					world.setBlockState(pos, CoreInit.blockHiddenRenderOld.getDefaultState());
			} else {
				if (properties.tesrID >= 0)
					world.setBlockState(pos, CoreInit.blockHiddenTESR.getDefaultState());
				else
					world.setBlockState(pos, CoreInit.blockHidden.getDefaultState());
			}
		} else
			world.setBlockState(pos, CoreInit.blockHidden.getDefaultState());
		TileEntityHidden te = (TileEntityHidden) world.getTileEntity(pos);
		te.blockOld = old.getBlock();
		te.metaOld = metaOld;
		te.y = y;
		if (properties != null) {
			te.blockProperties = properties;
			if (properties.renderBlock) {
				if (properties.blockRender != null) {
					te.blockRender = properties.blockRender;
					te.metaRender = properties.metaRender;
				} else {
					te.blockRender = old.getBlock();
					te.metaRender = metaOld;
				}
			}
		}
		if (oldTile != null) {
			NBTTagCompound tag = new NBTTagCompound();
			oldTile.writeToNBT(tag);
			te.tileOld = tag;
		}
		te.master = master;
		te.pick = pick;
		te.drop = drop;
	}

	public void kill() {
		drop = ItemStack.EMPTY;
		killed = true;
		world.setBlockState(pos, getOldState());
		if (tileOld != null) {
			TileEntity tile = TileEntity.create(world, tileOld);
			tile.validate();
			world.setTileEntity(pos, tile);
		}
	}

	public static void kill(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityHidden) {
			((TileEntityHidden) tile).kill();
		}
	}

	@Override
	public final SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}

	@Override
	public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public final void handleUpdateTag(final NBTTagCompound compound) {
		if (world.isRemote)
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {

				@Override
				public void run() {
					pick = TomsModUtils.loadItemStackFromNBT(compound.getCompoundTag("pick"));
					NBTTagCompound t = compound.getCompoundTag("properties");
					if (t.hasNoTags()) {
						blockProperties = null;
					} else
						blockProperties = BlockProperties.load(t);
					if (compound.getBoolean("hasMaster")) {
						t = compound.getCompoundTag("masterPos");
						master = new BlockPos(t.getInteger("x"), t.getInteger("y"), t.getInteger("z"));
					} else {
						master = null;
					}
					metaRender = compound.getInteger("metaRender");
					blockRender = Block.REGISTRY.getObject(new ResourceLocation(compound.getString("blockRender")));
				}
			});
	}

	@Override
	public final NBTTagCompound getUpdateTag() {
		NBTTagCompound compound = super.getUpdateTag();
		NBTTagCompound tag = new NBTTagCompound();
		if (pick != null)
			pick.writeToNBT(tag);
		compound.setTag("pick", tag);
		tag = new NBTTagCompound();
		if (blockProperties != null)
			blockProperties.writeToNBT(tag);
		compound.setTag("properties", tag);
		compound.setBoolean("hasMaster", master != null);
		if (master != null) {
			tag = new NBTTagCompound();
			tag.setInteger("x", master.getX());
			tag.setInteger("y", master.getY());
			tag.setInteger("z", master.getZ());
			compound.setTag("masterPos", tag);
		}
		compound.setString("blockRender", blockRender != null ? blockRender.delegate.name().toString() : "");
		compound.setInteger("metaRender", metaRender);
		return compound;
	}

	public static class BlockProperties {
		public AxisAlignedBB box = Block.FULL_BLOCK_AABB;
		public boolean isLadder;
		public int tesrID = -1;
		public int id;
		public boolean renderBlock, renderResLoc;
		public String mapperID = "";
		public Block blockRender;
		public int metaRender;
		public ResourceLocation loc;

		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			tag.setDouble("minX", box.minX);
			tag.setDouble("minY", box.minY);
			tag.setDouble("minZ", box.minZ);
			tag.setDouble("maxX", box.maxX);
			tag.setDouble("maxY", box.maxY);
			tag.setDouble("maxZ", box.maxZ);
			tag.setInteger("tesrID", tesrID);
			tag.setBoolean("ladder", isLadder);
			tag.setInteger("id", id);
			tag.setBoolean("renderOld", renderBlock);
			tag.setString("mapper", mapperID);
			tag.setString("blockRender", blockRender != null ? blockRender.delegate.name().toString() : "");
			tag.setInteger("metaRender", metaRender);
			tag.setBoolean("renderResLoc", renderResLoc);
			if (loc != null)
				tag.setString("renderLoc", loc.toString());
			return tag;
		}

		public NBTTagCompound writeToNew() {
			return writeToNBT(new NBTTagCompound());
		}

		public int getID(int y) {
			return !mapperID.isEmpty() ? ID_MAPPER_FUNC_LIST.get(mapperID).apply(id, y) : id;
		}

		public static BlockProperties load(NBTTagCompound tag) {
			BlockProperties p = new BlockProperties();
			p.box = new AxisAlignedBB(tag.getDouble("minX"), tag.getDouble("minY"), tag.getDouble("minZ"), tag.getDouble("maxX"), tag.getDouble("maxY"), tag.getDouble("maxZ"));
			p.isLadder = tag.getBoolean("ladder");
			p.tesrID = tag.getInteger("tesrID");
			p.id = tag.getInteger("id");
			p.renderBlock = tag.getBoolean("renderOld");
			p.mapperID = tag.getString("mapper");
			p.metaRender = tag.getInteger("metaRender");
			p.blockRender = Block.REGISTRY.getObject(new ResourceLocation(tag.getString("blockRender")));
			p.renderResLoc = tag.getBoolean("renderResLoc");
			if (tag.hasKey("renderLoc"))
				p.loc = new ResourceLocation(tag.getString("renderLoc"));
			return p;
		}

		public BlockProperties setTesrID(int tesrID) {
			this.tesrID = tesrID;
			return this;
		}

		public BlockProperties setBox(AxisAlignedBB box) {
			this.box = box;
			return this;
		}

		public BlockProperties setLadder(boolean isLadder) {
			this.isLadder = isLadder;
			return this;
		}

		public BlockProperties setId(int id) {
			this.id = id;
			return this;
		}

		public BlockProperties setRenderOldBlock(boolean renderOldBlock) {
			this.renderBlock = renderOldBlock;
			return this;
		}

		public BlockProperties setMapperID(String mapperID) {
			this.mapperID = mapperID;
			return this;
		}

		public BlockProperties setBlockRender(Block blockRender) {
			this.blockRender = blockRender;
			return this;
		}

		public BlockProperties setMetaRender(int metaRender) {
			this.metaRender = metaRender;
			return this;
		}

		public BlockProperties setRenderResLoc(boolean renderResLoc) {
			this.renderResLoc = renderResLoc;
			return this;
		}

		public BlockProperties setRenderLoc(ResourceLocation loc) {
			this.loc = loc;
			return this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((blockRender == null) ? 0 : blockRender.hashCode());
			result = prime * result + ((box == null) ? 0 : box.hashCode());
			result = prime * result + id;
			result = prime * result + (isLadder ? 1231 : 1237);
			result = prime * result + ((loc == null) ? 0 : loc.hashCode());
			result = prime * result + ((mapperID == null) ? 0 : mapperID.hashCode());
			result = prime * result + metaRender;
			result = prime * result + (renderBlock ? 1231 : 1237);
			result = prime * result + (renderResLoc ? 1231 : 1237);
			result = prime * result + tesrID;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BlockProperties other = (BlockProperties) obj;
			if (blockRender == null) {
				if (other.blockRender != null)
					return false;
			} else if (!blockRender.equals(other.blockRender))
				return false;
			if (box == null) {
				if (other.box != null)
					return false;
			} else if (!box.equals(other.box))
				return false;
			if (id != other.id)
				return false;
			if (isLadder != other.isLadder)
				return false;
			if (loc == null) {
				if (other.loc != null)
					return false;
			} else if (!loc.equals(other.loc))
				return false;
			if (mapperID == null) {
				if (other.mapperID != null)
					return false;
			} else if (!mapperID.equals(other.mapperID))
				return false;
			if (metaRender != other.metaRender)
				return false;
			if (renderBlock != other.renderBlock)
				return false;
			if (renderResLoc != other.renderResLoc)
				return false;
			if (tesrID != other.tesrID)
				return false;
			return true;
		}
	}

	public TileEntity getMaster() {
		return master != null ? world.getTileEntity(master) : null;
	}

	public boolean blockEquals(BlockData data) {
		return data.matches(blockOld, metaOld);
	}

	@SuppressWarnings("deprecation")
	public IBlockState getOldState() {
		return blockOld == null ? Blocks.STONE.getDefaultState() : blockOld.getStateFromMeta(metaOld);
	}

	@SuppressWarnings("deprecation")
	public IBlockState getRenderState() {
		return blockRender == null ? Blocks.STONE.getDefaultState() : blockRender.getStateFromMeta(metaRender);
	}

	public static String registerMapper(String name, BiFunction<Integer, Integer, Integer> mapper) {
		ID_MAPPER_FUNC_LIST.put(name, mapper);
		return name;
	}

	public boolean isRenderOld() {
		return blockProperties != null ? !blockProperties.renderResLoc : true;
	}

	public ResourceLocation getRender() {
		return blockProperties != null ? blockProperties.loc : null;
	}
}
