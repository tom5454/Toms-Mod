package com.tom.core.tileentity;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.Configs;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityRedstonePort extends TileEntityTomsMod implements
IPeripheral {
	//private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	public boolean redstone = false;
	public int compRedstone = 0;
	@Override
	public String getType() {
		return "tm_rsPort";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"setOutput","getInput","setComparatorOutput","getOutput","getComparatorOutput"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] a) throws LuaException,
	InterruptedException {
		if(method == 0){
			if(a.length > 0 && a[0] instanceof Boolean){
				this.redstone = (Boolean) a[0];
			}else{
				throw new LuaException("Invalid Argument, Boolean exepted");
			}
		}else if(method == 1){
			return new Object[]{worldObj.isBlockIndirectlyGettingPowered(pos)};
		}else if(method == 2){
			if(a.length > 0 && a[0] instanceof Double){
				this.compRedstone = MathHelper.floor_double((Double) a[0]);
			}else{
				throw new LuaException("Invalid Argument, number exepted");
			}
		}else if(method == 3){
			return new Object[]{this.redstone};
		}else if(method == 4){
			return new Object[]{this.compRedstone};
		}
		this.markDirty();
		markBlockForUpdate(pos);
		//this.worldObj.markBlockRangeForRenderUpdate(xCoord+1, yCoord+1, zCoord+1, xCoord-1, yCoord-1, zCoord-1);
		this.worldObj.notifyBlockOfStateChange(pos, blockType);
		/*this.worldObj.markBlockForUpdate(xCoord, yCoord+1, zCoord);
		this.worldObj.markBlockForUpdate(xCoord, yCoord-1, zCoord);
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord+1);
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord-1);
		this.worldObj.markBlockForUpdate(xCoord+1, yCoord, zCoord);
		this.worldObj.markBlockForUpdate(xCoord-1, yCoord, zCoord);*/

		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {
		//computers.add(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		//computers.remove(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other == this;
	}

	public int getComparatorOutput() {
		return this.compRedstone < 15 ? this.compRedstone > 0 ? this.compRedstone : 0 : 15;
	}

	public boolean getOutput() {
		return this.redstone;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.compRedstone = tag.getInteger("comp");
		this.redstone = tag.getBoolean("rs");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("comp", this.compRedstone);
		tag.setBoolean("rs",this.redstone);
		return tag;
	}
}
