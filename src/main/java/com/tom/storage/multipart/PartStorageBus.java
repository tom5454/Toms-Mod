package com.tom.storage.multipart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.inventory.IStorageInventory.BasicFilter;
import com.tom.api.inventory.IStorageInventory.BasicFilter.IFilteringInformation;
import com.tom.api.inventory.IStorageInventory.FilteredStorageInventory;
import com.tom.api.inventory.IStorageInventory.IStorageInv;
import com.tom.api.multipart.IGuiMultipart;
import com.tom.api.multipart.PartModule;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.StorageInit;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingHandler;
import com.tom.storage.multipart.StorageNetworkGrid.IStorageData;
import com.tom.storage.tileentity.gui.GuiStorageBus;
import com.tom.storage.tileentity.inventory.ContainerStorageBus;

import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;

public class PartStorageBus extends PartModule<StorageNetworkGrid> implements IGuiMultipart{
	public PartStorageBus() {
		this(EnumFacing.NORTH);
	}
	public PartStorageBus(EnumFacing face) {
		super(StorageInit.storageBus, 0.4, 0.25, face, "tomsmodstorage:tm.storageBus", 2);
	}
	private boolean checkNBT = true;
	private boolean checkMeta = true;
	private boolean checkMod = false;
	private static final String TAG_NBT_NAME = "config";
	private IStorageData data = null;
	public InventoryBasic filterInv = new InventoryBasic("", false, 36);
	@Override
	public void onGridReload() {

	}

	@Override
	public void onGridPostReload() {

	}

	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			BlockPos pos = getPos2().offset(facing);
			final IInventory inventory = TileEntityHopper.getInventoryAtPosition(worldObj, pos.getX(), pos.getY(), pos.getZ());
			if(inventory != null){
				if(data == null){
					data = new IStorageData() {
						IStorageInv inv = new FilteredStorageInventory(inventory, 0, new BasicFilter(filterInv, new IFilteringInformation() {

							@Override
							public boolean useNBT() {
								return checkNBT;
							}

							@Override
							public boolean useMod() {
								return checkMod;
							}

							@Override
							public boolean useMeta() {
								return checkMeta;
							}
						}), facing.getOpposite());
						@Override
						public IStorageInv getInventory() {
							return inv;
						}

						@Override
						public void update(ItemStack stack, IInventory inv, World world, int priority) {
							this.inv.update(stack, inv, priority);
						}

						@Override
						public ICraftingHandler<?> getCraftingHandler() {
							return null;
						}
					};
				}
				data.getInventory().update(null, inventory, 0);
				grid.getData().addStorageData(data);
				grid.drainEnergy(0.5D);
			}else{
				if(data != null){
					grid.getData().removeStorageData(data);
					data = null;
				}
			}
		}
	}

	@Override
	public int getState() {
		return grid.isPowered() ? 2 : 0;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("checkMeta", checkMeta);
		tag.setBoolean("checkNBT", checkNBT);
		tag.setBoolean("checkMod", checkMod);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < filterInv.getSizeInventory(); ++i)
		{
			if (filterInv.getStackInSlot(i) != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				filterInv.getStackInSlot(i).writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		tag.setTag("itemList", list);
		nbt.setTag(TAG_NBT_NAME, tag);
		return nbt;
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagCompound tag = nbt.getCompoundTag(TAG_NBT_NAME);
		this.checkMeta = tag.getBoolean("checkMeta");
		this.checkNBT = tag.getBoolean("checkNBT");
		this.checkMod = tag.getBoolean("checkMod");
		NBTTagList list = tag.getTagList("itemList", 10);
		filterInv.clear();
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < filterInv.getSizeInventory())
			{
				filterInv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
	}
	@Override
	public Object getGui(EntityPlayer player) {
		return new GuiStorageBus(this, player.inventory);
	}
	@Override
	public Object getContainer(EntityPlayer player) {
		return new ContainerStorageBus(this, player.inventory);
	}
	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {

	}
	@Override
	public PartSlot getPosition() {
		return PartSlot.getFaceSlot(facing);
	}
	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand,
			ItemStack heldItem, PartMOP hit) {
		BlockPos pos = getPos2();
		if(!player.worldObj.isRemote)player.openGui(CoreInit.modInstance, GuiIDs.getMultipartGuiId(facing).ordinal(), getWorld2(), pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}
