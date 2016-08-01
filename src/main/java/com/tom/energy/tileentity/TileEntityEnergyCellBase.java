package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.LASER;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IConfigurable.IConfigurationOption.ConfigurationOptionSide;
import com.tom.api.tileentity.ISecuredTileEntity;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.Configs;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityEnergyCellBase extends TileEntityTomsMod implements
IEnergyHandler, IConfigurable, IPeripheral, ISecuredTileEntity {
	public TileEntityEnergyCellBase(int capacity, int color){
		energy = new EnergyStorage(capacity, capacity / 500D * 11D, capacity / 50D);
		this.color = color;
	}
	public final int color;
	private ItemStack securityCardStack = null;
	private EnergyStorage energy;
	public byte outputSides = 0;
	//public EnumFacing facing = EnumFacing.NORTH;
	@SideOnly(Side.CLIENT)
	public double energyStoredClient;
	private IConfigurationOption cfgOption = new ConfigurationOptionSide(outputSides, new ResourceLocation("tomsmodenergy:textures/blocks/cellBase.png"), new ResourceLocation("tomsmodenergy:textures/blocks/energyCellOut.png"), this);
	/*@SideOnly(Side.CLIENT)
	public int displayList = -1;
	public boolean sideModified = true;*/
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == LASER;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type,
			double maxReceive, boolean simulate) {
		markBlockForUpdate();
		return this.canConnectEnergy(from, type) && (!contains(from)) ? energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type,
			double maxExtract, boolean simulate) {
		markBlockForUpdate();
		return this.canConnectEnergy(from, type) && contains(from) ? energy.extractEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.canConnectEnergy(from, type) ? energy.getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.canConnectEnergy(from, type) ? energy.getMaxEnergyStored() : 0;
	}
	@Override
	public void updateEntity(){
		if(worldObj.isRemote){
		}else{
			if(this.energy.getEnergyStored() > 0){
				//System.out.println(" "+outputSides);
				for(EnumFacing f : EnumFacing.VALUES){
					if(contains(f)){
						//TileEntity receiver = worldObj.getTileEntity(pos.offset(f));
						//if(receiver instanceof IEnergyReceiver) {
						/*//System.out.println("send");
						IEnergyReceiver recv = (IEnergyReceiver)receiver;
						EnumFacing fOut = f.getOpposite();
						if(recv.canConnectEnergy(fOut, LASER)) {
							//System.out.println("send2");
							double energyPushed = recv.receiveEnergy(fOut, LASER, Math.min(energy.getMaxExtract(), energy.getEnergyStored()), true);
							if(energyPushed > 0) {
								//System.out.println("push");
								this.markDirty();
								worldObj.markBlockForUpdate(pos);
								this.energy.extractEnergy(recv.receiveEnergy(fOut, LASER, energyPushed, false),false);
							}
						}*/
						if(LASER.pushEnergyTo(worldObj, pos, f.getOpposite(), energy, false) > 0)
							markBlockForUpdate();
						//}
					}
				}
			}
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		NBTTagCompound eTag = tag.getCompoundTag("energy");
		energy.readFromNBT(eTag);
		/*NBTTagList list = tag.getTagList("output", 10);
		this.outputSides.clear();
		for(int i = 0;i<list.tagCount();i++){
			NBTTagCompound t = list.getCompoundTagAt(i);
			this.outputSides.add(EnumFacing.VALUES[t.getInteger("id")]);
		}*/
		//this.facing = EnumFacing.VALUES[tag.getInteger("facing")];
		this.outputSides = tag.getByte("sides");
		//System.out.println(outputSides);
		securityCardStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("card"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("energy",energy.writeToNBT(new NBTTagCompound()));
		/*NBTTagList list = new NBTTagList();
		for(EnumFacing f : this.outputSides){
			NBTTagCompound t = new NBTTagCompound();
			t.setInteger("id", f.ordinal());
			list.appendTag(t);
		}
		tag.setTag("output", list);*/
		//tag.setInteger("facing", this.facing.ordinal());
		tag.setByte("sides", outputSides);
		//System.out.println(" "+outputSides);
		NBTTagCompound cardTag = new  NBTTagCompound();
		if(this.securityCardStack != null)this.securityCardStack.writeToNBT(cardTag);
		tag.setTag("card", cardTag);
		return tag;
	}
	public void writeToStackNBT(NBTTagCompound tag){
		tag.setTag("energy",energy.writeToNBT(new NBTTagCompound()));
		tag.setByte("sides", outputSides);
		NBTTagCompound cardTag = new  NBTTagCompound();
		if(this.securityCardStack != null)this.securityCardStack.writeToNBT(cardTag);
		tag.setTag("card", cardTag);
	}

	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setDouble("e",energy.getEnergyStored());
		//buf.writeInt(this.facing.ordinal());
		//buf.writeBoolean(sideModified);
		/*NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for(EnumFacing f : this.outputSides){
			NBTTagCompound t = new NBTTagCompound();
			t.setInteger("id", f.ordinal());
			list.appendTag(t);
		}
		tag.setTag("o", list);
		ByteBufUtils.writeTag(buf, tag);*/
		//this.sideModified = false;
		buf.setByte("o", outputSides);
		//System.out.println("write:"+this.outputSides);
	}
	@Override
	public void readFromPacket(NBTTagCompound buf) {
		this.energyStoredClient = buf.getDouble("e");
		//this.facing = EnumFacing.VALUES[buf.readInt()];
		//this.sideModified = buf.readBoolean();
		/*NBTTagCompound tag = ByteBufUtils.readTag(buf);
		NBTTagList list = tag.getTagList("o", 10);
		this.outputSides.clear();
		for(int i = 0;i<list.tagCount();i++){
			NBTTagCompound t = list.getCompoundTagAt(i);
			this.outputSides.add(EnumFacing.VALUES[t.getInteger("id")]);
		}*/
		this.outputSides = buf.getByte("o");
		//System.out.println("read:"+this.outputSides);
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}
	/*public void updateSideRender(){
		this.sideModified = true;
	}
	@SideOnly(Side.CLIENT)
	public void compileSideRender(){
		this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		double u1 = 0.0D,v1= 1.0D,u2= 1.0D,v2= 0.0D;
		int w = 1;
		int h = 1;
		int x = 0;
		int y = 0;
		int z = 0;
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("tomsmodenergy:textures/blocks/energyCellOutput.png"));
        try{
			for(EnumFacing f : this.outputSides){
				boolean isPositive = f.getAxisDirection() == AxisDirection.POSITIVE;
				if(f.getAxis() == Axis.X){
					if(isPositive){
						renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
						renderer.pos(x+1.001, y, z + w).tex(u2, v1).endVertex();
						renderer.pos(x+1.001, y, z).tex(u1, v1).endVertex();
						renderer.pos(x+1.001, y + h, z).tex(u1, v2).endVertex();
						renderer.pos(x+1.001, y + h, z + w).tex(u2, v2).endVertex();
						tessellator.draw();
						continue;
					}else{
						renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
						renderer.pos(x, y, z - w + 1).tex(u2, v1).endVertex();
						renderer.pos(x, y, z + 1).tex(u1, v1).endVertex();
						renderer.pos(x, y + h, z + 1).tex(u1, v2).endVertex();
						renderer.pos(x, y + h, z - w + 1).tex(u2, v2).endVertex();
						tessellator.draw();
						continue;
					}
				}else if(f.getAxis() == Axis.Y){
					if(isPositive){
						renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
						renderer.pos(x + w, y + 1.001, z).tex(u2, v1).endVertex();
						renderer.pos(x, y + 1.001, z).tex(u1, v1).endVertex();
						renderer.pos(x, y + 1.001, z + h).tex(u1, v2).endVertex();
						renderer.pos(x + w, y + 1.001, z + h).tex(u2, v2).endVertex();
						tessellator.draw();
						continue;
					}else{
						renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
						renderer.pos(x - w + 1, y, z).tex(u2, v1).endVertex();
						renderer.pos(x + 1, y, z).tex(u1, v1).endVertex();
						renderer.pos(x + 1, y, z + h).tex(u1, v2).endVertex();
						renderer.pos(x - w + 1, y, z + h).tex(u2, v2).endVertex();
						tessellator.draw();
						continue;
					}
				}else{
					if(isPositive){
						renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
						renderer.pos(x - w + 1, y, z+1.001).tex(u2, v1).endVertex();
						renderer.pos(x + 1, y, z+1.001).tex(u1, v1).endVertex();
						renderer.pos(x + 1, y + h, z+1.001).tex(u1, v2).endVertex();
						renderer.pos(x - w + 1, y + h, z+1.001).tex(u2, v2).endVertex();
						tessellator.draw();
						continue;
					}
				}
				renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
				renderer.pos(x + w, y, z).tex(u2, v1).endVertex();
				renderer.pos(x, y, z).tex(u1, v1).endVertex();
				renderer.pos(x, y + h, z).tex(u1, v2).endVertex();
				renderer.pos(x + w, y + h, z).tex(u2, v2).endVertex();
				tessellator.draw();
			}
		}catch(ConcurrentModificationException e){
			e.printStackTrace();
		}
        GL11.glEndList();
	}*/
	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LASER.getList();
	}

	@Override
	public IConfigurationOption getOption() {
		return cfgOption ;
	}

	@Override
	public boolean canConfigure(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound tag) {
		outputSides = tag.getByte("s");
		/*this.outputSides.clear();
		for(EnumFacing f : EnumFacing.VALUES){
			if((sideConfig & (1 << f.ordinal())) != 0){
				this.outputSides.add(f);
			}
		}*/
		//System.out.println(this.outputSides);
		markBlockForUpdate();
		markDirty();
	}

	@Override
	public void writeToNBTPacket(NBTTagCompound tag) {
		/*byte sideConfig = 0;
		for(EnumFacing f : this.outputSides){
			sideConfig |= 1 << f.ordinal();
		}*/
		tag.setByte("s", outputSides);
		//System.out.println(this.outputSides);
	}

	@Override
	public BlockPos getSecurityStationPos() {
		return this.securityCardStack != null && securityCardStack.getItem() instanceof ISecurityStationLinkCard ? ((ISecurityStationLinkCard)securityCardStack.getItem()).getStation(securityCardStack) : null;
	}

	@Override
	public void setCardStack(ItemStack stack) {
		this.securityCardStack = stack;
	}

	@Override
	public ItemStack getCardStack() {
		return securityCardStack;
	}
	public boolean contains(EnumFacing side) {
		return (outputSides & (1 << side.ordinal())) != 0;
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public String getType() {
		return "laser_energy_storage";
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public String[] getMethodNames() {
		return new String[]{"getEnergyStored","getMaxEnergyStored"};
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
	InterruptedException {
		if(method == 0){
			return new Object[]{energy.getEnergyStored()};
		}else if(method == 1){
			return new Object[]{energy.getMaxEnergyStored()};
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public void attach(IComputerAccess computer) {

	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public void detach(IComputerAccess computer) {

	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public boolean equals(IPeripheral other) {
		return other == this;
	}
	/*public void readFromStackNBT(ItemStack stack){
		if(stack.hasTagCompound()){
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("BlockEntityTag");
			NBTTagCompound eTag = tag.getCompoundTag("energy");
			energy.readFromNBT(eTag);
			this.outputSides = tag.getByte("sides");
			securityCardStack = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("card"));
		}
	}*/
	public double getStoredPer(){
		return energyStoredClient / energy.getMaxEnergyStored();
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}
}
