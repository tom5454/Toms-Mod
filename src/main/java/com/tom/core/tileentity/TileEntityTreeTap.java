package com.tom.core.tileentity;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.block.BlockTreeTap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityTreeTap extends TileEntityTomsMod {
	private ItemStack bottleStack;
	private int progress = 0;
	public ItemStack getBottleStack() {
		return bottleStack;
	}
	public void setBottleStack(ItemStack bottleStack) {
		this.bottleStack = bottleStack;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound tag = new NBTTagCompound();
		if(bottleStack != null)bottleStack.writeToNBT(tag);
		compound.setTag("item", tag);
		compound.setInteger("progress", progress);
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		bottleStack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("item"));
		progress = compound.getInteger("progress");
	}
	@Override
	public void updateEntity(IBlockState state) {
		if(!worldObj.isRemote){
			IBlockState home = worldObj.getBlockState(pos.offset(state.getValue(BlockTreeTap.FACING).getOpposite()));
			if(home != null && home.getBlock() == CoreInit.rubberWood){
				if(bottleStack != null){
					if(bottleStack.getItem() == Items.GLASS_BOTTLE){
						progress++;
						if(progress >= 1000){
							int a = bottleStack.stackSize;
							bottleStack = CraftingMaterial.BOTTLE_OF_RUBBER.getStackNormal(a);
							progress = 0;
							TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, BlockTreeTap.STATE, 2);
						}else TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, BlockTreeTap.STATE, 1);
					}else if(bottleStack.isItemEqual(CraftingMaterial.BOTTLE_OF_RUBBER.getStackNormal())){
						TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, BlockTreeTap.STATE, 2);
						progress = 0;
					}else{
						InventoryHelper.spawnItemStack(worldObj, pos.getX()+0.5, pos.getY()+0.6, pos.getZ() + 0.5, bottleStack);
						bottleStack = null;
						progress = 0;
						TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, BlockTreeTap.STATE, 0);
					}
				}else{
					progress = 0;
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, BlockTreeTap.STATE, 0);
				}
			}else{
				InventoryHelper.spawnItemStack(worldObj, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(CoreInit.treeTap));
				worldObj.setBlockToAir(pos);
			}
		}
	}
}
