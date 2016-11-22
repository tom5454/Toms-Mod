package com.tom.core.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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

public class TileEntityHidden extends TileEntity {
	public BlockPos master;
	public ItemStack drop;
	public AxisAlignedBB bb = Block.FULL_BLOCK_AABB;
	public ItemStack pick;
	public Block blockOld;
	public int metaOld, id;
	public NBTTagCompound tileOld;
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound tag = new NBTTagCompound();
		if(drop != null)drop.writeToNBT(tag);
		compound.setTag("drop", tag);
		tag = new NBTTagCompound();
		if(pick != null)pick.writeToNBT(tag);
		compound.setTag("pick", tag);
		tag = new NBTTagCompound();
		tag.setDouble("minX", bb.minX);
		tag.setDouble("minY", bb.minY);
		tag.setDouble("minZ", bb.minZ);
		tag.setDouble("maxX", bb.maxX);
		tag.setDouble("maxY", bb.maxY);
		tag.setDouble("maxZ", bb.maxZ);
		compound.setTag("bb", tag);
		compound.setBoolean("hasMaster", master != null);
		if(master != null){
			tag = new NBTTagCompound();
			tag.setInteger("x", master.getX());
			tag.setInteger("y", master.getY());
			tag.setInteger("z", master.getZ());
			compound.setTag("masterPos", tag);
		}
		tag = new NBTTagCompound();
		if(tileOld != null){
			tag.setTag("data", tileOld);
			tag.setBoolean("hasTile", true);
		}else{
			tag.setBoolean("hasTile", false);
		}
		tag.setInteger("metaOld", metaOld);
		tag.setString("blockOld", blockOld != null ? blockOld.delegate.name().toString() : "");
		compound.setTag("oldTile", tag);
		compound.setInteger("partID", id);
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		drop = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("drop"));
		pick = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("pick"));
		NBTTagCompound t = compound.getCompoundTag("bb");
		bb = new AxisAlignedBB(t.getDouble("minX"), t.getDouble("minY"), t.getDouble("minZ"), t.getDouble("maxX"), t.getDouble("maxY"), t.getDouble("maxZ"));
		if(compound.getBoolean("hasMaster")){
			t = compound.getCompoundTag("masterPos");
			master = new BlockPos(t.getInteger("x"), t.getInteger("y"), t.getInteger("z"));
		}else{
			master = null;
		}
		t = compound.getCompoundTag("oldTile");
		metaOld = t.getInteger("metaOld");
		blockOld = Block.REGISTRY.getObject(new ResourceLocation(t.getString("blockOld")));
		if(t.getBoolean("hasTile")){
			tileOld = t.getCompoundTag("data");
		}
		id = compound.getInteger("partID");
	}
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(this.master != null){
			TileEntity master = worldObj.getTileEntity(this.master);
			return master != null && master instanceof ILinkableCapabilities ? ((ILinkableCapabilities)master).hasCapability(capability, facing, pos, id) : super.hasCapability(capability, facing);
		}else{
			return super.hasCapability(capability, facing);
		}
	}
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(this.master != null){
			TileEntity master = worldObj.getTileEntity(this.master);
			return master != null && master instanceof ILinkableCapabilities ? ((ILinkableCapabilities)master).getCapability(capability, facing, pos, id) : super.getCapability(capability, facing);
		}else{
			return super.getCapability(capability, facing);
		}
	}
	public static interface ILinkableCapabilities{
		boolean hasCapability(Capability<?> capability, EnumFacing facing, BlockPos from, int id);
		<T> T getCapability(Capability<T> capability, EnumFacing facing, BlockPos from, int id);
	}
	public boolean blockEquals(Block block, int meta) {
		return block == blockOld && (meta == metaOld || meta == -1);
	}
	public static void place(World world, BlockPos pos, BlockPos master, ItemStack pick, int id){
		IBlockState old = world.getBlockState(pos);
		TileEntity oldTile = world.getTileEntity(pos);
		AxisAlignedBB box = old.getBoundingBox(world, pos);
		Item item = old.getBlock().getItemDropped(old, world.rand, 0);
		ItemStack drop = item != null ? new ItemStack(item, old.getBlock().quantityDropped(old, 0, world.rand), old.getBlock().damageDropped(old)) : null;
		world.setBlockState(pos, CoreInit.blockHidden.getDefaultState());
		TileEntityHidden te = (TileEntityHidden) world.getTileEntity(pos);
		te.blockOld = old.getBlock();
		te.metaOld = old.getBlock().getMetaFromState(old);
		te.bb = box.expandXyz(0);
		if(oldTile != null){
			NBTTagCompound tag = new NBTTagCompound();
			oldTile.writeToNBT(tag);
			te.tileOld = tag;
		}
		te.master = master;
		te.pick = pick;
		te.drop = drop;
		te.id = id;
	}
	@SuppressWarnings("deprecation")
	public void kill(){
		drop = null;
		worldObj.setBlockState(pos, blockOld.getStateFromMeta(metaOld));
		if(tileOld != null){
			TileEntity tile = TileEntity.create(worldObj, tileOld);
			tile.validate();

			worldObj.setTileEntity(pos, tile);
		}
	}
	public static void kill(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityHidden){
			((TileEntityHidden)tile).kill();
		}
	}
	@Override
	public final SPacketUpdateTileEntity getUpdatePacket() {
		/*ByteBuf buf = Unpooled.buffer();
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());*/
		//return new FMLProxyPacket(new PacketBuffer(buf),TomsModAPIMain.Chanel2);
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}
	@Override
	public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}
	@Override
	public final void handleUpdateTag(final NBTTagCompound compound) {
		if(worldObj.isRemote)Minecraft.getMinecraft().addScheduledTask(new Runnable() {

			@Override
			public void run() {
				pick = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("pick"));
				NBTTagCompound t = compound.getCompoundTag("bb");
				bb = new AxisAlignedBB(t.getDouble("minX"), t.getDouble("minY"), t.getDouble("minZ"), t.getDouble("maxX"), t.getDouble("maxY"), t.getDouble("maxZ"));
				if(compound.getBoolean("hasMaster")){
					t = compound.getCompoundTag("masterPos");
					master = new BlockPos(t.getInteger("x"), t.getInteger("y"), t.getInteger("z"));
				}else{
					master = null;
				}
				id = compound.getInteger("partID");
			}
		});
	}
	@Override
	public final NBTTagCompound getUpdateTag() {
		NBTTagCompound compound = super.getUpdateTag();
		NBTTagCompound tag = new NBTTagCompound();
		if(pick != null)pick.writeToNBT(tag);
		compound.setTag("pick", tag);
		tag = new NBTTagCompound();
		tag.setDouble("minX", bb.minX);
		tag.setDouble("minY", bb.minY);
		tag.setDouble("minZ", bb.minZ);
		tag.setDouble("maxX", bb.maxX);
		tag.setDouble("maxY", bb.maxY);
		tag.setDouble("maxZ", bb.maxZ);
		compound.setTag("bb", tag);
		compound.setBoolean("hasMaster", master != null);
		if(master != null){
			tag = new NBTTagCompound();
			tag.setInteger("x", master.getX());
			tag.setInteger("y", master.getY());
			tag.setInteger("z", master.getZ());
			compound.setTag("masterPos", tag);
		}
		compound.setInteger("partID", id);
		return compound;
	}
}
