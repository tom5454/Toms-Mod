package com.tom.energy.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;
import com.tom.energy.EnergyInit;
import com.tom.lib.Configs;
import com.tom.lib.api.tileentity.IChunkLoader;
import com.tom.util.TomsModUtils;

import com.tom.energy.block.FusionController;

public class TileEntityFusionController extends TileEntityTomsMod implements ICustomMultimeterInformation, IChunkLoader {
	private int cycle = 0;
	private int timer = 0;
	private int timer2 = 0;
	public ForgeChunkManager.Ticket ticket;
	private boolean active = false;
	private boolean redstone = false;
	private int cooldown = 0;
	private int cooldownState = 0;
	private boolean started = false, t = false;

	public boolean active() {
		return this.active;
	}

	private boolean getBlock(int x, int y, int z, Block block) {
		boolean ret = block == world.getBlockState(new BlockPos(x, y, z)).getBlock();
		return ret;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.cycle = tag.getInteger("cycle");
		this.timer = tag.getInteger("timer");
		this.timer2 = tag.getInteger("timer2");
		this.cooldown = tag.getInteger("cooldown");
		this.cooldownState = tag.getInteger("cooldownState");
		this.started = tag.getBoolean("started");
		this.active = this.cycle == 15;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("cycle", this.cycle);
		tag.setInteger("timer", this.timer);
		tag.setInteger("timer2", this.timer2);
		tag.setInteger("cooldown", this.cooldown);
		tag.setInteger("cooldownState", this.cooldownState);
		tag.setBoolean("started", this.started);
		return tag;
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote) {
			int xCoord = pos.getX();
			int yCoord = pos.getY();
			int zCoord = pos.getZ();
			boolean formed = this.getMultiblock();
			this.redstone = world.isBlockIndirectlyGettingPowered(pos) > 0;
			if ((this.redstone || (this.cooldownState == 1 && this.cooldown != 0)) && ((this.injectorsReady() && this.charged(false)) || cycle(1)) && this.getMultiblock()) {
				if (this.redstone && this.injectorsReady() && this.charged(false)) {
					this.cooldownState = 0;
					this.cooldown = 0;
				}
				this.run();
				this.markDirty();
			} else {
				if (formed && this.started) {
					if (this.cooldownState == 0) {
						this.cooldownState = 1;
						this.cooldown = 21;
					} else if (this.cooldown == 0 && this.cooldownState == 1) {
						this.cooldownState = 0;
						this.cycle = 0;
						this.timer = 0;
						this.active = false;
						this.started = false;
						this.markDirty();
						if (ticket != null) {
							ForgeChunkManager.releaseTicket(ticket);
							ticket = null;
						}
					}
				} else {
					if (this.started) {
						this.cooldownState = 0;
						this.cycle = 0;
						this.cooldown = 0;
						this.timer = 0;
						this.active = false;
						this.started = false;
						world.setBlockToAir(pos);
						world.createExplosion(null, xCoord, yCoord, zCoord, 10.0F, Configs.machinesExplode);
					}
				}
			}
			if (this.timer > 0) {
				this.timer = this.timer - 1;
			}
			if (this.cooldown > 0) {
				this.cooldown = this.cooldown - 1;
			}
			if (formed) {
				if (this.active) {
					if (state.getValue(FusionController.STATE) != 2) {
						TomsModUtils.setBlockState(world, pos, state.withProperty(FusionController.STATE, 2));
					}
				} else {
					if (state.getValue(FusionController.STATE) != 1) {
						TomsModUtils.setBlockState(world, pos, state.withProperty(FusionController.STATE, 1));
					}
				}
			} else {
				if (state.getValue(FusionController.STATE) != 0) {
					TomsModUtils.setBlockState(world, pos, state.withProperty(FusionController.STATE, 0));
				}
			}
		}
	}

	public boolean getMultiblock() {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		int d = this.getDirection();
		int x = xCoord, y = yCoord, z = zCoord;
		boolean ret = d == 3 ? this.getBlock(x, y, z - 1, EnergyInit.FusionCore) : true;
		ret = ret && this.getHullPart(0, 2, d, 0) && this.getHullPart(0, -2, d, 0) && this.getInjector(-9, 4, d);
		ret = ret && this.getHullPart(-10, 2, d, 0) && this.getHullPart(-10, -2, d, 0) && this.getInjector(-9, -4, d);
		ret = ret && this.getBlock(2, 0, 0, d, CoreInit.MachineFrameChrome) && this.getBlock(1, 0, 1, d, EnergyInit.FusionCore) && this.getBlock(1, 0, -1, d, EnergyInit.FusionCore);
		ret = ret && this.getHullPart(-3, 5, d, 1) && this.getHullPart(-3, -5, d, 1) && this.getInjector(-1, 4, d);
		ret = ret && this.getHullPart(-7, 5, d, 1) && this.getHullPart(-7, -5, d, 1) && this.getInjector(-1, -4, d);
		ret = ret && this.getBlock(0, 0, 3, d, EnergyInit.FusionCore) && this.getBlock(0, 0, -3, d, EnergyInit.FusionCore);
		ret = ret && this.getBlock(-10, 0, 3, d, EnergyInit.FusionCore) && this.getBlock(-10, 0, -3, d, EnergyInit.FusionCore);
		ret = ret && this.getHullBlock(-10, 3, d) && this.getHullBlock(-10, -3, d) && this.getHullBlock(0, 3, d) && this.getHullBlock(0, -3, d);
		ret = ret && this.getBlock(1, 0, 3, d, CoreInit.MachineFrameChrome) && this.getBlock(1, 0, -3, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-11, 0, 3, d, CoreInit.MachineFrameChrome) && this.getBlock(-11, 0, -3, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-11, 0, 0, d, EnergyInit.FusionCore) && this.getBlock(-11, 0, 1, d, EnergyInit.FusionCore) && this.getBlock(-11, 0, -1, d, EnergyInit.FusionCore);
		ret = ret && this.getBlock(-12, 0, 0, d, CoreInit.MachineFrameChrome) && this.getBlock(-10, 0, 0, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(1, 1, 0, d, EnergyInit.FusionCharger) && this.getBlock(1, -1, 0, d, EnergyInit.FusionCharger);
		ret = ret && this.getBlock(1, 1, 1, d, EnergyInit.FusionFluidInjector) && this.getBlock(1, 1, -1, d, EnergyInit.FusionFluidInjector);
		ret = ret && this.getBlock(1, -1, 1, d, EnergyInit.FusionFluidInjector) && this.getBlock(1, -1, -1, d, EnergyInit.FusionFluidInjector);
		/*ret = ret && this.getBlock(-11, 1, 1, d, EnergyInit.FusionFluidInjector) && this.getBlock(-11, 1, -1, d, EnergyInit.FusionFluidInjector);
		ret = ret && this.getBlock(-11, -1, 1, d, EnergyInit.FusionFluidInjector) && this.getBlock(-11, -1, -1, d, EnergyInit.FusionFluidInjector);*/
		ret = ret && this.getBlock(-11, 1, 1, d, CoreInit.MachineFrameChrome) && this.getBlock(-11, 1, -1, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-11, -1, 1, d, CoreInit.MachineFrameChrome) && this.getBlock(-11, -1, -1, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-4, 0, 6, d, EnergyInit.FusionCore) && this.getBlock(-4, 0, -6, d, EnergyInit.FusionCore);
		ret = ret && this.getBlock(-5, 0, 6, d, EnergyInit.FusionCore) && this.getBlock(-5, 0, -6, d, EnergyInit.FusionCore);
		ret = ret && this.getBlock(-6, 0, 6, d, EnergyInit.FusionCore) && this.getBlock(-6, 0, -6, d, EnergyInit.FusionCore);
		ret = ret && this.getHullBlock(-2, 5, d) && this.getHullBlock(-2, -5, d);
		ret = ret && this.getHullBlock(-8, 5, d) && this.getHullBlock(-8, -5, d);
		ret = ret && this.getBlock(-2, 0, 5, d, EnergyInit.FusionCore) && this.getBlock(-2, 0, -5, d, EnergyInit.FusionCore);
		ret = ret && this.getBlock(-8, 0, 5, d, EnergyInit.FusionCore) && this.getBlock(-8, 0, -5, d, EnergyInit.FusionCore);
		ret = ret && this.getBlock(-11, 1, 0, d, EnergyInit.FusionCharger) && this.getBlock(-11, -1, 0, d, EnergyInit.FusionCharger);
		ret = ret && this.getBlock(0, 0, 1, d, EnergyInit.FusionFluidExtractor) && this.getBlock(0, 0, -1, d, EnergyInit.FusionFluidExtractor);
		ret = ret && this.getBlock(2, 0, 1, d, EnergyInit.FusionFluidExtractor) && this.getBlock(2, 0, -1, d, EnergyInit.FusionFluidExtractor);
		/*ret = ret && this.getBlock(-10, 0, 1, d, EnergyInit.FusionFluidExtractor) && this.getBlock(-10, 0, -1, d, EnergyInit.FusionFluidExtractor);
		ret = ret && this.getBlock(-12, 0, 1, d, EnergyInit.FusionFluidExtractor) && this.getBlock(-12, 0, -1, d, EnergyInit.FusionFluidExtractor);*/
		ret = ret && this.getBlock(-10, 0, 1, d, CoreInit.MachineFrameChrome) && this.getBlock(-10, 0, -1, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-12, 0, 1, d, CoreInit.MachineFrameChrome) && this.getBlock(-12, 0, -1, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-5, 1, 6, d, EnergyInit.FusionCharger) && this.getBlock(-5, 1, -6, d, EnergyInit.FusionCharger);
		ret = ret && this.getBlock(-5, -1, 6, d, EnergyInit.FusionCharger) && this.getBlock(-5, -1, -6, d, EnergyInit.FusionCharger);
		ret = ret && this.getBlock(-5, 0, 5, d, CoreInit.MachineFrameChrome) && this.getBlock(-5, 0, -5, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-5, 0, 7, d, CoreInit.MachineFrameChrome) && this.getBlock(-5, 0, -7, d, CoreInit.MachineFrameChrome);
		/*ret = ret && this.getBlock(-4, 0, 5, d, EnergyInit.FusionFluidExtractor) && this.getBlock(-4, 0, -5, d, EnergyInit.FusionFluidExtractor);
		ret = ret && this.getBlock(-4, 0, 7, d, EnergyInit.FusionFluidExtractor) && this.getBlock(-4, 0, -7, d, EnergyInit.FusionFluidExtractor);
		ret = ret && this.getBlock(-6, 0, 5, d, EnergyInit.FusionFluidExtractor) && this.getBlock(-6, 0, -5, d, EnergyInit.FusionFluidExtractor);
		ret = ret && this.getBlock(-6, 0, 7, d, EnergyInit.FusionFluidExtractor) && this.getBlock(-6, 0, -7, d, EnergyInit.FusionFluidExtractor);
		ret = ret && this.getBlock(-4, 1, 6, d, EnergyInit.FusionFluidInjector) &&  this.getBlock(-4, -1, 6, d, EnergyInit.FusionFluidInjector);
		ret = ret && this.getBlock(-6, 1, 6, d, EnergyInit.FusionFluidInjector) &&  this.getBlock(-6, -1, 6, d, EnergyInit.FusionFluidInjector);
		ret = ret && this.getBlock(-4, 1, -6, d, EnergyInit.FusionFluidInjector) && this.getBlock(-4, -1, -6, d, EnergyInit.FusionFluidInjector);
		ret = ret && this.getBlock(-6, 1, -6, d, EnergyInit.FusionFluidInjector) && this.getBlock(-6, -1, -6, d, EnergyInit.FusionFluidInjector);*/
		ret = ret && this.getBlock(-4, 0, 5, d, CoreInit.MachineFrameChrome) && this.getBlock(-4, 0, -5, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-4, 0, 7, d, CoreInit.MachineFrameChrome) && this.getBlock(-4, 0, -7, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-6, 0, 5, d, CoreInit.MachineFrameChrome) && this.getBlock(-6, 0, -5, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-6, 0, 7, d, CoreInit.MachineFrameChrome) && this.getBlock(-6, 0, -7, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-4, 1, 6, d, CoreInit.MachineFrameChrome) && this.getBlock(-4, -1, 6, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-6, 1, 6, d, CoreInit.MachineFrameChrome) && this.getBlock(-6, -1, 6, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-4, 1, -6, d, CoreInit.MachineFrameChrome) && this.getBlock(-4, -1, -6, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(-6, 1, -6, d, CoreInit.MachineFrameChrome) && this.getBlock(-6, -1, -6, d, CoreInit.MachineFrameChrome);
		return ret;
	}

	public int getDirection() {
		IBlockState state = world.getBlockState(pos);
		EnumFacing ret = state.getValue(FusionController.FACING);
		return ret == EnumFacing.EAST ? 0 : (ret == EnumFacing.WEST ? 1 : (ret == EnumFacing.SOUTH ? 2 : 3));
	}

	private double getBlockEnergy(int xPos, int yPos, int zPos, int d) {
		int[] current;
		double ret = 0;
		current = this.getCoord(xPos, yPos, zPos, d);
		int x = current[0], y = current[1], z = current[2];
		TileEntity tilee = world.getTileEntity(new BlockPos(x, y, z));
		if (tilee instanceof TileEntityFusionInjector) {
			ret = ((TileEntityFusionInjector) tilee).getEnergyStored();
		} else if (tilee instanceof TileEntityFusionCharger) {
			ret = ((TileEntityFusionCharger) tilee).getEnergyStored();
		} else {
			ret = -1;
		}
		return ret;
	}

	private boolean getBlock(int x, int y, int z, int d, Block block) {
		boolean ret;
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		if (d == 0) {
			ret = this.getBlock(xCoord + x, yCoord + y, zCoord + z, block);
		} else if (d == 1) {
			ret = this.getBlock(xCoord + (-x), yCoord + y, zCoord + z, block);
		} else if (d == 2) {
			ret = this.getBlock(xCoord + z, yCoord + y, zCoord + x, block);
		} else if (d == 3) {
			ret = this.getBlock(xCoord + z, yCoord + y, zCoord + (-x), block);
		} else {
			ret = false;
		}

		return ret;
	}

	private int[] getCoord(int x, int y, int z, int d) {
		return TomsModUtils.getRelativeCoordTable(TomsModUtils.getCoordTable(pos), x, y, z, d);
	}

	private boolean getHullBlock(int x, int z, int i) {
		return this.getBlock(x, 1, z, i, CoreInit.MachineFrameChrome) && this.getBlock(x, -1, z, i, CoreInit.MachineFrameChrome);
	}

	private boolean getHullPart(int x, int z, int d, int r) {
		int y = 0;
		boolean ret = this.getBlock(x, y, z, d, EnergyInit.FusionCore) && this.getHullBlock(x, z, d);
		if (r == 0) {
			ret = ret && this.getBlock(x + 1, y, z, d, CoreInit.MachineFrameChrome) && this.getBlock(x - 1, y, z, d, CoreInit.MachineFrameChrome);
		} else if (r == 1) {
			ret = ret && this.getBlock(x, y, z + 1, d, CoreInit.MachineFrameChrome) && this.getBlock(x, y, z - 1, d, CoreInit.MachineFrameChrome);
		}
		return ret;
	}

	private boolean getInjector(int x, int z, int d) {
		boolean ret = this.getBlock(x, 0, z, d, EnergyInit.FusionCore) && this.getBlock(x, 1, z, d, CoreInit.MachineFrameChrome);
		ret = ret && this.getBlock(x, -1, z, d, CoreInit.MachineFrameChrome) && this.getBlock(x + 1, 0, z, d, EnergyInit.FusionInjector);
		ret = ret && this.getBlock(x, 0, z + 1, d, EnergyInit.FusionInjector) && this.getBlock(x - 1, 0, z, d, EnergyInit.FusionInjector);
		ret = ret && this.getBlock(x, 0, z - 1, d, EnergyInit.FusionInjector);
		return ret;
	}

	public int getComparatorOutput() {
		return this.cycle;
	}

	public boolean charged(boolean force) {
		boolean ret;
		int d = this.getDirection();
		ret = this.getInjectorCharged(0, 4, d, force) && this.getInjectorCharged(-2, 4, d, force) && this.getInjectorCharged(-1, 3, d, force) && this.getInjectorCharged(-1, 5, d, force);
		ret = ret && this.getInjectorCharged(0, -4, d, force) && this.getInjectorCharged(-2, -4, d, force) && this.getInjectorCharged(-1, -3, d, force) && this.getInjectorCharged(-1, -5, d, force);
		ret = ret && this.getInjectorCharged(-8, -4, d, force) && this.getInjectorCharged(-10, -4, d, force) && this.getInjectorCharged(-9, -3, d, force) && this.getInjectorCharged(-9, -5, d, force);
		ret = ret && this.getInjectorCharged(-8, 4, d, force) && this.getInjectorCharged(-10, 4, d, force) && this.getInjectorCharged(-9, 3, d, force) && this.getInjectorCharged(-9, 5, d, force);
		return ret;
	}

	private boolean getInjectorCharged(int xPos, int zPos, int d, boolean force) {
		int[] current;
		boolean ret;
		current = this.getCoord(xPos, 0, zPos, d);
		int x = current[0], y = current[1], z = current[2];
		TileEntity tilee = world.getTileEntity(new BlockPos(x, y, z));
		ret = ((TileEntityFusionInjector) tilee).ready(force);
		return ret;
	}

	public void redstone() {
		this.redstone = true;
	}

	public boolean injectorsReady() {
		int d = this.getDirection();
		boolean ret = this.getFluid(1, 1, 1, d) >= Configs.FusionStartFluidAmmount && this.getFluid(1, 1, -1, d) >= Configs.FusionStartFluidAmmount;
		ret = ret && this.getFluid(1, -1, 1, d) >= Configs.FusionStartFluidAmmount && this.getFluid(1, -1, -1, d) >= Configs.FusionStartFluidAmmount;
		/*ret = ret && this.getFluid(-11, 1, 1, d) >= Configs.FusionStartFluidAmmount && this.getFluid(-11, 1, -1, d) >= Configs.FusionStartFluidAmmount;
		ret = ret && this.getFluid(-11, -1, 1, d) >= Configs.FusionStartFluidAmmount && this.getFluid(-11, -1, -1, d) >= Configs.FusionStartFluidAmmount;
		ret = ret && this.getFluid(-4, 1, 6, d) >= Configs.FusionStartFluidAmmount && this.getFluid(-4, 1, -6, d) >= Configs.FusionStartFluidAmmount;
		ret = ret && this.getFluid(-4, -1, 6, d) >= Configs.FusionStartFluidAmmount && this.getFluid(-4, -1, -6, d) >= Configs.FusionStartFluidAmmount;
		ret = ret && this.getFluid(-6, 1, 6, d) >= Configs.FusionStartFluidAmmount && this.getFluid(-6, 1, -6, d) >= Configs.FusionStartFluidAmmount;
		ret = ret && this.getFluid(-6, -1, 6, d) >= Configs.FusionStartFluidAmmount && this.getFluid(-6, -1, -6, d) >= Configs.FusionStartFluidAmmount;*/
		ret = ret && this.getBlockEnergy(1, 1, 0, d) >= Configs.chargerStart && this.getBlockEnergy(1, -1, 0, d) >= Configs.chargerStart;
		ret = ret && this.getBlockEnergy(-11, 1, 0, d) >= Configs.chargerStart && this.getBlockEnergy(-11, -1, 0, d) >= Configs.chargerStart;
		ret = ret && this.getBlockEnergy(-5, 1, 6, d) >= Configs.chargerStart && this.getBlockEnergy(-5, -1, 6, d) >= Configs.chargerStart;
		ret = ret && this.getBlockEnergy(-5, 1, -6, d) >= Configs.chargerStart && this.getBlockEnergy(-5, -1, -6, d) >= Configs.chargerStart;
		return ret;
	}

	private int getFluid(int xPos, int yPos, int zPos, int d) {
		int[] current;
		int ret = 0;
		current = this.getCoord(xPos, yPos, zPos, d);
		int x = current[0], y = current[1], z = current[2];
		TileEntity tilee = world.getTileEntity(new BlockPos(x, y, z));
		if (tilee instanceof TileEntityFusionFluidInjector) {
			ret = ((TileEntityFusionFluidInjector) tilee).getFluidAmount();
		} else if (tilee instanceof TileEntityFusionFluidExtractor) {
			ret = ((TileEntityFusionFluidExtractor) tilee).getAmount();
		}
		return ret;
	}

	public void redstoneOff() {
		this.redstone = false;
	}

	private boolean cycle(int c) {
		return this.cycle == c;
	}

	private boolean timer(int t) {
		return this.timer == t;
	}

	private void discharge() {
		int d = this.getDirection();
		this.discharge(0, 4, d);
		this.discharge(-2, 4, d);
		this.discharge(-1, 3, d);
		this.discharge(-1, 5, d);
		this.discharge(0, -4, d);
		this.discharge(-2, -4, d);
		this.discharge(-1, -3, d);
		this.discharge(-1, -5, d);
		this.discharge(-8, -4, d);
		this.discharge(-10, -4, d);
		this.discharge(-9, -3, d);
		this.discharge(-9, -5, d);
		this.discharge(-8, 4, d);
		this.discharge(-10, 4, d);
		this.discharge(-9, 3, d);
		this.discharge(-9, 5, d);
	}

	private void drain(int a) {
		int d = this.getDirection();
		this.drain(1, 1, 1, d, a);
		this.drain(1, 1, -1, d, a);
		this.drain(1, -1, 1, d, a);
		this.drain(1, -1, -1, d, a);
		/*this.drain(-11, 1, 1, d, a);
		this.drain(-11, 1, -1, d, a);
		this.drain(-11, -1, 1, d, a);
		this.drain(-11, -1, -1, d, a);
		this.drain(-4, 1, 6, d, a);
		this.drain(-4, 1, -6, d, a);
		this.drain(-4, -1, 6, d, a);
		this.drain(-4, -1, -6, d, a);
		this.drain(-6, 1, 6, d, a);
		this.drain(-6, 1, -6, d, a);
		this.drain(-6, -1, 6, d, a);
		this.drain(-6, -1, -6, d, a);*/
	}

	private void drain(int xPos, int yPos, int zPos, int d, int a) {
		int[] current;
		current = this.getCoord(xPos, yPos, zPos, d);
		int x = current[0], y = current[1], z = current[2];
		TileEntity tilee = world.getTileEntity(new BlockPos(x, y, z));
		((TileEntityFusionFluidInjector) tilee).remove(a);
	}

	private void discharge(int xPos, int yPos, int zPos, int d, int ammount) {
		int[] current;
		current = this.getCoord(xPos, yPos, zPos, d);
		int x = current[0], y = current[1], z = current[2];
		TileEntity tilee = world.getTileEntity(new BlockPos(x, y, z));
		if (tilee instanceof TileEntityFusionInjector) {
			((TileEntityFusionInjector) tilee).disCharge(ammount == -1);
		} else if (tilee instanceof TileEntityFusionCharger) {
			((TileEntityFusionCharger) tilee).disCharge(ammount);
		}
	}

	private void discharge(int xPos, int zPos, int d) {
		int[] current;
		current = this.getCoord(xPos, 0, zPos, d);
		int x = current[0], y = current[1], z = current[2];
		TileEntity tilee = world.getTileEntity(new BlockPos(x, y, z));
		((TileEntityFusionInjector) tilee).disCharge(true);
	}

	@Override
	public void invalidate() {
		if (ticket != null)
			ForgeChunkManager.releaseTicket(ticket);
		super.invalidate();
	}

	private void run() {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		int chunkX = xCoord >> 4;
		int chunkZ = zCoord >> 4;
		if (ticket == null)
			ticket = ForgeChunkManager.requestTicket(CoreInit.modInstance, world, Type.NORMAL);
		if (ticket != null)
			TomsModUtils.writeBlockPosToNBT(ticket.getModData(), pos);
		ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX, chunkZ));
		ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX + 1, chunkZ));
		ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX - 1, chunkZ));
		ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX, chunkZ + 1));
		ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX, chunkZ - 1));
		ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX + 1, chunkZ + 1));
		ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX - 1, chunkZ + 1));
		ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX + 1, chunkZ - 1));
		ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX - 1, chunkZ - 1));
		if (this.cycle(0)) {
			if (this.getMultiblock() && this.charged(true) && this.injectorsReady() && !this.started && this.redstone) {
				this.cycle = 1;
				this.timer = 100;
				this.drain(Configs.FusionStartFluidAmmount);
				this.discharge();
				this.started = true;
				System.out.println("Fusion Reactor Started at " + xCoord + ", " + yCoord + ", " + zCoord + ".");
			}
		} else if (this.cycle(1)) {
			if (this.timer(0)) {
				this.cycle = 2;
				this.timer = 6;
			}
		} else if (this.cycle(2)) {
			if (this.timer(0)) {
				this.cycle = 3;
				this.timer = 101;
			} else if (this.timer(2)) {
				this.dischargeChargers(Configs.chargerStart);
			}
		} else if (this.cycle(3)) {
			if (this.timer(0)) {
				this.cycle = 4;
				this.timer = 100;
			}
			this.dischargeChargers(Configs.ChargerUsage * 2);
			this.discharge(Configs.InjectorUsage * 2);
		} else if (this.cycle(4)) {
			if (this.timer(0)) {
				this.cycle = 5;
				this.timer = 100;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage);
		} else if (this.cycle(5)) {
			if (this.timer(0)) {
				this.cycle = 6;
				this.timer = 100;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage);
		} else if (this.cycle(6)) {
			if (this.timer(0)) {
				this.cycle = 7;
				this.timer = 60;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage);
		} else if (this.cycle(7)) {
			if (this.timer(0)) {
				this.cycle = 8;
				this.timer = 100;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage + (TomsModUtils.invertInt(this.timer, 100) * (Configs.InjectorUsage / 100)));
			if (this.timer % 4 == 0)
				this.drain(1);
		} else if (this.cycle(8)) {
			if (this.timer(0)) {
				this.cycle = 9;
				this.timer = 100;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage + (Configs.InjectorUsage / 2));
			if (this.timer % 10 == 0) {
				this.fill(1);
			} else if (this.timer % 10 == 5) {
				this.drain(1);
			}
		} else if (this.cycle(9)) {
			if (this.timer(0)) {
				this.cycle = 10;
				this.timer = 100;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage);
			this.drain(1);
			if (this.timer % 5 == 0) {
				this.fill(1);
			}
		} else if (this.cycle(10)) {
			if (this.timer(0)) {
				this.cycle = 11;
				this.timer = 100;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage);
			this.drain(2);
			if (this.timer % 2 == 0) {
				this.fill(1);
			}
		} else if (this.cycle(11)) {
			if (this.timer(0)) {
				this.cycle = 12;
				this.timer = 400;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage);
			this.drain(5);
			if (timer % 2 == 0)
				this.fill(1);
		} else if (this.cycle(12)) {
			if (this.timer(0)) {
				this.cycle = 13;
				this.timer = 1000;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage);
			this.drain(1);
			if (timer % 2 == 0)
				this.fill(1);
		} else if (this.cycle(13)) {
			if (this.timer(0)) {
				this.cycle = 14;
				this.timer = 2000;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage);
			if (t)
				this.drain(2);
			else
				this.fill(1);
			t = !t;
		} else if (this.cycle(14)) {
			if (this.timer(0)) {
				this.cycle = 15;
				this.active = true;
			}
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage);
			if (t)
				this.drain(3);
			else
				this.fill(timer % 3 == 0 ? 2 : 1);
			t = !t;
		} else if (this.cycle(15)) {
			this.dischargeChargers(Configs.ChargerUsage);
			this.discharge(Configs.InjectorUsage);
			if (t)
				this.drain(4);
			else
				this.fill(2);
			t = !t;
		}
	}

	private void dischargeChargers(int a) {
		int d = this.getDirection();
		this.discharge(1, 1, 0, d, a);
		this.discharge(1, -1, 0, d, a);
		this.discharge(-11, 1, 0, d, a);
		this.discharge(-11, -1, 0, d, a);
		this.discharge(-5, 1, 6, d, a);
		this.discharge(-5, -1, 6, d, a);
		this.discharge(-5, 1, -6, d, a);
		this.discharge(-5, -1, -6, d, a);
	}

	private void discharge(int a) {
		int d = this.getDirection();
		boolean b = true;
		this.discharge(0, 4, d, b, a);
		this.discharge(-2, 4, d, b, a);
		this.discharge(-1, 3, d, b, a);
		this.discharge(-1, 5, d, b, a);
		this.discharge(0, -4, d, b, a);
		this.discharge(-2, -4, d, b, a);
		this.discharge(-1, -3, d, b, a);
		this.discharge(-1, -5, d, b, a);
		this.discharge(-8, -4, d, b, a);
		this.discharge(-10, -4, d, b, a);
		this.discharge(-9, -3, d, b, a);
		this.discharge(-9, -5, d, b, a);
		this.discharge(-8, 4, d, b, a);
		this.discharge(-10, 4, d, b, a);
		this.discharge(-9, 3, d, b, a);
		this.discharge(-9, 5, d, b, a);
	}

	private void discharge(int xPos, int zPos, int d, boolean aaa, int a) {
		int[] current;
		current = this.getCoord(xPos, 0, zPos, d);
		int x = current[0], y = current[1], z = current[2];
		TileEntity tilee = world.getTileEntity(new BlockPos(x, y, z));
		((TileEntityFusionInjector) tilee).disCharge(a);
	}

	private void fill(int a) {
		int d = this.getDirection();
		this.fill(0, 1, d, a);
		this.fill(0, -1, d, a);
		this.fill(2, 1, d, a);
		this.fill(2, -1, d, a);
		/*this.fill(-10, 1, d, a);
		this.fill(-10, -1, d, a);
		this.fill(-12, 1, d, a);
		this.fill(-12, -1, d, a);
		this.fill(-4, 5, d, a);
		this.fill(-4, -5, d, a);
		this.fill(-6, 5, d, a);
		this.fill(-6, -5, d, a);
		this.fill(-4, 7, d, a);
		this.fill(-4, -7, d, a);
		this.fill(-6, 7, d, a);
		this.fill(-6, -7, d, a);*/
	}

	private void fill(int xPos, int zPos, int d, int a) {
		int[] current;
		current = this.getCoord(xPos, 0, zPos, d);
		int x = current[0], y = current[1], z = current[2];
		TileEntity tilee = world.getTileEntity(new BlockPos(x, y, z));
		((TileEntityFusionFluidExtractor) tilee).add(a);
	}

	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		boolean formed = this.getMultiblock();
		list.add(new TextComponentTranslation("tomsMod.chat.formed", TomsModUtils.getYesNoMessage(formed)));
		if (formed) {
			list.add(new TextComponentTranslation("tomsMod.chat.active", TomsModUtils.getYesNoMessage(active || cycle > 0)));
			if (!this.active && cycle == 0) {
				list.add(new TextComponentTranslation("tomsMod.chat.injectorsReady", TomsModUtils.getYesNoMessage(this.injectorsReady() && this.charged(true))));
			} else {
				list.add(new TextComponentTranslation("tomsMod.chat.cycle", new TextComponentString("15/" + this.cycle).setStyle(new Style().setColor(TextFormatting.YELLOW))));
			}
		}
		return list;
	}

	@Override
	public void onTicksped() {
		world.setBlockToAir(pos);
		world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 10.0F, Configs.machinesExplode);
	}

	@Override
	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
}
