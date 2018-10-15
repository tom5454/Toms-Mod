package com.tom.defense.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;

import com.tom.api.block.IItemTile;
import com.tom.api.energy.IEnergyStorage;
import com.tom.api.item.IPowerLinkCard;
import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.item.ISwitch;
import com.tom.api.network.INBTPacketReceiver;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.IForceDevice;
import com.tom.api.tileentity.IForcePowerStation;
import com.tom.api.tileentity.IGuiTile;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.DamageSourceTomsMod;
import com.tom.defense.DefenseInit;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.block.ForceCapacitor;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.util.TomsModUtils;

public class TileEntityDefenseStation extends TileEntityTomsMod implements IForceDevice, ISidedInventory, IGuiTile, INBTPacketReceiver, IItemTile {
	public ForceDeviceControlType rsMode = ForceDeviceControlType.LOW_REDSTONE;
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	// private EnergyStorage energy = new EnergyStorage(10000000,100000,200000);
	public boolean active = false;
	private boolean firstStart = true, lastActive = false;
	public long clientEnergy = 0, clientMax;
	private static final int[] SLOTS = new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24},
			ITEMS = new int[]{25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45};
	public DefenseStationConfig config = DefenseStationConfig.INFORM;
	public String customName = "Defense Station";
	private boolean isWhiteList = false, useMeta = true, useNBT = true, useMod = false, playerKill = false;
	private int cooldown = 0;

	@Override
	public double receiveEnergy(double maxReceive, boolean simulate) {
		// return energy.receiveEnergy(maxReceive, simulate);
		return 0;
	}

	@Override
	public boolean isValid(BlockPos from) {
		BlockPos c = this.getCapacitorPos();
		return this.hasWorld() && c != null && c.equals(from) && pos != null && world.getBlockState(pos) != null && world.getBlockState(pos).getBlock() == DefenseInit.defenseStation;
	}

	public BlockPos getCapacitorPos() {
		return !inv.getStackInSlot(1).isEmpty() && inv.getStackInSlot(1).getItem() instanceof IPowerLinkCard ? ((IPowerLinkCard) inv.getStackInSlot(1).getItem()).getMaster(inv.getStackInSlot(1)) : null;
	}

	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		if (!world.isRemote) {
			// player.attackEntityFrom(DamageSourceTomsMod.fieldDamage, 8F);
			if (held != null && held.getItem() instanceof ISwitch && ((ISwitch) held.getItem()).isSwitch(held, player)) {
				if (rsMode == ForceDeviceControlType.SWITCH) {
					boolean canAccess = true;
					BlockPos securityStationPos = this.getSecurityStationPos();
					if (securityStationPos != null) {
						TileEntity tileentity = world.getTileEntity(securityStationPos);
						if (tileentity instanceof ISecurityStation) {
							ISecurityStation tile = (ISecurityStation) tileentity;
							canAccess = tile.canPlayerAccess(AccessType.SWITCH_DEVICES, player);
						}
					}
					if (canAccess) {
						this.active = !this.active;
						return true;
					} else {
						TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
						return false;
					}
				} else {
					TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.mnotSwitchable", new TextComponentTranslation(held.getUnlocalizedName() + ".name"));
					return false;
				}
			} else {
				boolean canAccess = true;
				BlockPos securityStationPos = this.getSecurityStationPos();
				if (securityStationPos != null) {
					TileEntity tileentity = world.getTileEntity(securityStationPos);
					if (tileentity instanceof ISecurityStation) {
						ISecurityStation tile = (ISecurityStation) tileentity;
						canAccess = tile.canPlayerAccess(AccessType.CONFIGURATION, player);
					}
				}
				if (canAccess) {
					player.openGui(CoreInit.modInstance, GuiIDs.defenseStation.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
				} else {
					TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
					return false;
				}
				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (id == 5) {
			rsMode = ForceDeviceControlType.get(extra);
		} else if (id == 4) {
			config = DefenseStationConfig.get(extra);
		} else if (id == 0) {
			this.isWhiteList = !this.isWhiteList;
		} else if (id == 1) {
			this.useMeta = !this.useMeta;
		} else if (id == 2) {
			this.useMod = !this.useMod;
		} else if (id == 3) {
			this.useNBT = !this.useNBT;
		} else if (id == 6) {
			this.playerKill = !this.playerKill;
		}
	}

	@Override
	public String getName() {
		return customName;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public int getSizeInventory() {
		return 46;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUsable(pos, player, world, this);
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return (int) (id == 0 ? this.clientEnergy : id == 1 ? clientMax : 0);
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0)
			this.clientEnergy = value;
		else if (id == 1)
			this.clientMax = value;
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		// energy.readFromNBT(compound);
		TomsModUtils.loadAllItems(compound.getTagList("inventory", 10), inv);
		rsMode = ForceDeviceControlType.get(compound.getInteger("redstone_mode"));
		this.active = compound.getBoolean("active");
		config = DefenseStationConfig.get(compound.getInteger("config"));
		this.customName = compound.getString("customName");
		this.setWhiteList(compound.getBoolean("whiteList"));
		this.setUseMeta(compound.getBoolean("useMeta"));
		this.setUseMod(compound.getBoolean("useMod"));
		this.setUseNBT(compound.getBoolean("useNBT"));
		this.setPlayerKill(compound.getBoolean("playerKill"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		// energy.writeToNBT(compound);
		compound.setTag("inventory", TomsModUtils.saveAllItems(inv));
		compound.setInteger("redstone_mode", rsMode.ordinal());
		compound.setBoolean("active", active);
		compound.setInteger("config", config.ordinal());
		compound.setString("customName", customName);
		compound.setBoolean("whiteList", isWhiteList());
		compound.setBoolean("useMeta", useMeta());
		compound.setBoolean("useMod", useMod());
		compound.setBoolean("useNBT", useNBT());
		compound.setBoolean("playerKill", isPlayerKill());
		return compound;
	}

	public void writeToStackNBT(NBTTagCompound compound) {
		// energy.writeToNBT(compound);
		NBTTagList list = new NBTTagList();
		for (int i = 0;i < inv.getSizeInventory();i++) {
			if (i != 2 && (i < 9 || i > 24)) {
				NBTTagCompound tag = new NBTTagCompound();
				inv.getStackInSlot(i).writeToNBT(tag);
				tag.setByte("Slot", (byte) i);
				list.appendTag(tag);
			}
		}
		compound.setTag("inventory", list);
		compound.setInteger("redstone_mode", rsMode.ordinal());
		compound.setInteger("config", config.ordinal());
		compound.setString("customName", customName);
		compound.setBoolean("whiteList", isWhiteList());
		compound.setBoolean("useMeta", useMeta());
		compound.setBoolean("useMod", useMod());
		compound.setBoolean("useNBT", useNBT());
		compound.setBoolean("playerKill", isPlayerKill());
	}

	@Override
	public BlockPos getSecurityStationPos() {
		return !inv.getStackInSlot(0).isEmpty() && inv.getStackInSlot(0).getItem() instanceof ISecurityStationLinkCard ? ((ISecurityStationLinkCard) inv.getStackInSlot(0).getItem()).getStation(inv.getStackInSlot(0)) : null;
	}

	@Override
	public void updateEntity(IBlockState currentState) {
		if (!world.isRemote) {
			BlockPos pos = this.getCapacitorPos();
			if (pos != null) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile instanceof IForcePowerStation) {
					IForcePowerStation te = (IForcePowerStation) tile;
					IEnergyStorage energy = te.getEnergyHandler(pos);
					if (!energy.isDummy()) {
						if (this.firstStart) {
							te.registerDevice(this);
						}
						if (rsMode == ForceDeviceControlType.HIGH_REDSTONE) {
							this.active = world.isBlockIndirectlyGettingPowered(this.pos) > 0;
						} else if (rsMode == ForceDeviceControlType.LOW_REDSTONE) {
							this.active = world.isBlockIndirectlyGettingPowered(this.pos) == 0;
						} else if (rsMode == ForceDeviceControlType.IGNORE) {
							this.active = true;
						}
						if (!lastActive && active) {
						}
						lastActive = active;
						BlockPos securityStationPos = this.getSecurityStationPos();
						this.clientEnergy = MathHelper.floor(energy.getEnergyStored());
						clientMax = energy.getMaxEnergyStored();
						TileEntity tileS = securityStationPos != null ? world.getTileEntity(securityStationPos) : null;
						AxisAlignedBB boundsN = getActionBounds(new AxisAlignedBB(0, 0, 0, 0, 0, 0));
						double usage1 = boundsN.maxX * boundsN.maxY * boundsN.maxZ * 2;
						double usage2 = usage1 / Config.defenseStationUsageDivider;
						cooldown--;
						TomsModUtils.setBlockStateWithCondition(world, this.pos, currentState, ForceCapacitor.ACTIVE, this.active && energy.getEnergyStored() > usage2 && securityStationPos != null && tileS instanceof ISecurityStation && te.isActive());
						if (this.active && cooldown < 1 && energy.getEnergyStored() > usage2 && securityStationPos != null && tileS instanceof ISecurityStation && te.isActive()) {
							/*int energyUsed = config.build(worldObj);
							double realEnergyUsed = 0.01 + (energyUsed / (100D + (stack[3] != null && stack[3].getItem() == DefenseInit.efficiencyUpgrade ? stack[3].stackSize * 50 : 0)));
							energy.extractEnergy(realEnergyUsed, false);
							this.lastDrained = MathHelper.floor_double(realEnergyUsed * 100);*/
							double killingUsage = ((this.getEfficiencyLevel() * 0.4) + 1) * (playerKill ? 1.5 : 1);
							ISecurityStation teS = (ISecurityStation) tileS;
							AxisAlignedBB posBox = new AxisAlignedBB(pos, pos);
							AxisAlignedBB informBounds = getInformBounds(posBox);
							AxisAlignedBB bounds = getActionBounds(posBox);
							energy.extractEnergy(usage2, false);
							cooldown = 5;
							if (config == DefenseStationConfig.KILL_HOSTILE) {
								List<EntityMob> mobs = world.getEntitiesWithinAABB(EntityMob.class, bounds);
								for (EntityMob mob : mobs) {
									double u = killingUsage * mob.getHealth();
									if (energy.getEnergyStored() < u)
										break;
									if (mob.attackEntityFrom(getDamageSource(), 999))
										energy.extractEnergy(u, false);
								}
								this.handleItems(energy, true, bounds);
							} else if (config == DefenseStationConfig.KILL_FRIENDLY) {
								List<EntityAnimal> animals = world.getEntitiesWithinAABB(EntityAnimal.class, bounds);
								for (EntityAnimal animal : animals) {
									if (animal instanceof EntityTameable) {
										EntityTameable t = (EntityTameable) animal;
										EntityLivingBase l = t.getOwner();
										if (l == null) {
											if (t.getOwnerId() == null || !teS.canPlayerAccess(AccessType.STAY_IN_AREA, t.getOwnerId())) {
												double u = killingUsage * animal.getHealth();
												if (energy.getEnergyStored() < u)
													break;
												if (animal.attackEntityFrom(getDamageSource(), 999))
													energy.extractEnergy(u, false);
											}
										} else if (l instanceof EntityPlayer) {
											if (!teS.canPlayerAccess(AccessType.STAY_IN_AREA, (EntityPlayer) l)) {
												double u = killingUsage * animal.getHealth();
												if (energy.getEnergyStored() < u)
													break;
												if (animal.attackEntityFrom(getDamageSource(), 999))
													energy.extractEnergy(u, false);
											}
										}
									} else {
										double u = killingUsage * animal.getHealth();
										if (energy.getEnergyStored() < u)
											break;
										if (animal.attackEntityFrom(getDamageSource(), 999))
											energy.extractEnergy(u, false);
									}
								}
								this.handleItems(energy, true, bounds);
							} else if (config == DefenseStationConfig.KILL_ALL) {
								List<EntityLivingBase> animals = new ArrayList<>(world.getEntitiesWithinAABB(EntityLivingBase.class, bounds));
								List<EntityPlayer> informPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, informBounds);
								for (EntityPlayer player : informPlayers) {
									if (!teS.canPlayerAccess(AccessType.STAY_IN_AREA, player)) {
										if (animals.contains(player)) {
											double u = killingUsage * player.getHealth() * 2;
											if (energy.getEnergyStored() < u)
												break;
											if (player.attackEntityFrom(getDamageSource(), 999))
												energy.extractEnergy(u, false);
											this.pullPlayerInventory(player, true, false);
											TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.beenWarned");
										} else {
											TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.deathWarning");
										}
									}
									animals.remove(player);
								}
								for (EntityLivingBase animal : animals) {
									if (animal instanceof EntityTameable) {
										EntityTameable t = (EntityTameable) animal;
										EntityLivingBase l = t.getOwner();
										if (l == null) {
											if (t.getOwnerId() == null || !teS.canPlayerAccess(AccessType.STAY_IN_AREA, t.getOwnerId())) {
												double u = killingUsage * animal.getHealth();
												if (energy.getEnergyStored() < u)
													break;
												if (animal.attackEntityFrom(getDamageSource(), 999))
													energy.extractEnergy(u, false);
											}
										} else if (l instanceof EntityPlayer) {
											if (!teS.canPlayerAccess(AccessType.STAY_IN_AREA, (EntityPlayer) l)) {
												double u = killingUsage * animal.getHealth();
												if (energy.getEnergyStored() < u)
													break;
												if (animal.attackEntityFrom(getDamageSource(), 999))
													energy.extractEnergy(u, false);
											}
										}
									} else {
										double u = killingUsage * animal.getHealth();
										if (energy.getEnergyStored() < u)
											break;
										if (animal.attackEntityFrom(getDamageSource(), 999))
											energy.extractEnergy(u, false);
									}
								}
								this.handleItems(energy, true, bounds);
							} else if (config == DefenseStationConfig.KILL) {
								List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bounds);
								List<EntityPlayer> informPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, informBounds);
								for (EntityPlayer player : informPlayers) {
									if (players.contains(player)) {
										if (!teS.canPlayerAccess(AccessType.STAY_IN_AREA, player)) {
											energy.extractEnergy(killingUsage * 2, false);
											this.pullPlayerInventory(player, true, false);
											player.attackEntityFrom(getDamageSource(), 999);
											TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.beenWarned");
											if (energy.getEnergyStored() < killingUsage * 2)
												break;
										}
									} else {
										TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.deathWarning");
									}
								}
								this.handleItems(energy, true, bounds);
							} else if (config == DefenseStationConfig.SEARCH_INVENTOTY) {
								List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bounds);
								List<EntityPlayer> informPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, informBounds);
								for (EntityPlayer player : informPlayers) {
									if (players.contains(player)) {
										if (!teS.canPlayerAccess(AccessType.STAY_IN_AREA, player)) {
											energy.extractEnergy(0.1D, false);
											boolean pulled = this.pullPlayerInventory(player, false, false);
											if (pulled)
												TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.beenWarned");
											if (energy.getEnergyStored() < 1D)
												break;
										}
									} else {
										if (this.pullPlayerInventory(player, false, true))
											TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.illegalGoods", new TextComponentString(this.customName));
									}
								}
								this.handleItems(energy, true, bounds);
							} else if (config == DefenseStationConfig.INFORM) {
								List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bounds);
								List<EntityPlayer> informPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, informBounds);
								for (EntityPlayer player : informPlayers) {
									if ((!teS.canPlayerAccess(AccessType.STAY_IN_AREA, player)) || (!teS.canPlayerAccess(AccessType.HAVE_INVENTORY, player))) {
										if (players.contains(player)) {
											TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.getOut");
										} else {
											TomsModUtils.sendNoSpamTranslate(player, TextFormatting.RED, "tomsMod.defense.scanningRange", new TextComponentString(this.customName));
										}
									}
								}
							}
						}
					} else {
						clientMax = -1;
						clientEnergy = -1;
					}
				} else {
					clientMax = -1;
					clientEnergy = -1;
				}
			} else {
				clientMax = -1;
				clientEnergy = -1;
			}
		}
	}

	private DamageSource getDamageSource() {
		return playerKill ? DamageSourceTomsMod.createPlayerSecutityDamage(TomsModUtils.getFakePlayer(world)) : DamageSourceTomsMod.securityDamage;
	}

	public static enum DefenseStationConfig {
		INFORM("tomsMod.defense.inform"), KILL("tomsMod.defense.kill"), SEARCH_INVENTOTY("tomsMod.defense.searchInv"), KILL_ALL("tomsMod.defense.killAll"), KILL_HOSTILE("tomsMod.defense.killHostile"), KILL_FRIENDLY("tomsMod.defense.killFriendly");
		public static final DefenseStationConfig[] VALUES = values();

		public static DefenseStationConfig get(int index) {
			return VALUES[index % VALUES.length];
		}

		private final String name;

		private DefenseStationConfig(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index > 8 && index < 25;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index > 8 && index < 25;
	}

	private double getEfficiencyLevel() {
		return !inv.getStackInSlot(2).isEmpty() && inv.getStackInSlot(2).getItem() == DefenseInit.efficiencyUpgrade ? inv.getStackInSlot(2).getCount() : 0;
	}

	private AxisAlignedBB getActionBounds(AxisAlignedBB box) {
		int uCX = !inv.getStackInSlot(3).isEmpty() ? inv.getStackInSlot(3).getCount() : 0;
		int uCY = !inv.getStackInSlot(4).isEmpty() ? inv.getStackInSlot(4).getCount() : 0;
		int uCZ = !inv.getStackInSlot(5).isEmpty() ? inv.getStackInSlot(5).getCount() : 0;
		return box.grow(2, 2, 2).grow(uCX, uCY, uCZ);
	}

	private AxisAlignedBB getInformBounds(AxisAlignedBB box) {
		int uCX = !inv.getStackInSlot(6).isEmpty() ? inv.getStackInSlot(6).getCount() : 0;
		int uCY = !inv.getStackInSlot(7).isEmpty() ? inv.getStackInSlot(7).getCount() : 0;
		int uCZ = !inv.getStackInSlot(8).isEmpty() ? inv.getStackInSlot(8).getCount() : 0;
		return this.getActionBounds(box).grow(2, 2, 2).grow(uCX, uCY, uCZ);
	}

	private void handleItems(IEnergyStorage energy, boolean pickup, AxisAlignedBB bounds) {
		List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, bounds.grow(1, 1, 1));
		for (EntityItem item : items) {
			if (energy.getEnergyStored() < 1)
				break;
			if (item == null) {
				continue;
			} else {
				ItemStack itemstack = item.getItem().copy();
				ItemStack itemstack1 = TileEntityHopper.putStackInInventoryAllSlots(this, this, itemstack, EnumFacing.UP);

				if (itemstack1 != null && itemstack1.getCount() != 0) {
					item.setItem(itemstack1);
				} else {
					item.setDead();
				}
				energy.extractEnergy(0.1D, false);
			}
		}
		for (int slot : SLOTS) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (!stack.isEmpty()) {
				inv.setInventorySlotContents(slot, TomsModUtils.pushStackToNeighbours(stack, world, pos, EnumFacing.VALUES));
			}
		}
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		this.customName = message.getString("n");
	}

	private boolean pullPlayerInventory(EntityPlayer player, boolean isComplete, boolean simulate) {
		if (isComplete) {
			for (int i = 0;i < player.inventory.getSizeInventory();i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if (!stack.isEmpty()) {
					stack = TileEntityHopper.putStackInInventoryAllSlots(this, this, stack, EnumFacing.UP);
					if (!stack.isEmpty()) {
						stack = TomsModUtils.pushStackToNeighbours(stack, world, pos, EnumFacing.VALUES);
						if (!stack.isEmpty()) {
							EntityItem item = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
							world.spawnEntity(item);
							stack = ItemStack.EMPTY;
						}
					}
				}
				player.inventory.setInventorySlotContents(i, stack);
			}
			return true;
		} else {
			boolean success = false;
			for (int i = 0;i < player.inventory.getSizeInventory();i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if (!stack.isEmpty()) {
					boolean inList = isItemInList(stack);
					if ((this.isWhiteList() && (!inList)) || (!this.isWhiteList() && inList)) {
						if (!simulate) {
							stack = TileEntityHopper.putStackInInventoryAllSlots(this, this, stack, EnumFacing.UP);
							if (!stack.isEmpty()) {
								stack = TomsModUtils.pushStackToNeighbours(stack, world, pos, EnumFacing.VALUES);
							}
							player.inventory.setInventorySlotContents(i, stack);
						}
						success = true;
					}
				}
			}
			return success;
		}
	}

	private boolean isItemInList(ItemStack stack) {
		for (int slot : ITEMS) {
			ItemStack matchTo = inv.getStackInSlot(slot);
			if (matchTo != null) {
				if (TomsModUtils.areItemStacksEqual(stack, matchTo, useMeta(), useNBT(), useMod())) { return true; }
			}
		}
		return false;
	}

	public NBTTagCompound getCustomNameMessage() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("n", customName);
		return tag;
	}

	public boolean isWhiteList() {
		return isWhiteList;
	}

	public void setWhiteList(boolean isWhiteList) {
		this.isWhiteList = isWhiteList;
	}

	public boolean useMeta() {
		return useMeta;
	}

	public void setUseMeta(boolean useMeta) {
		this.useMeta = useMeta;
	}

	public boolean useMod() {
		return useMod;
	}

	public void setUseMod(boolean useMod) {
		this.useMod = useMod;
	}

	public boolean useNBT() {
		return useNBT;
	}

	public void setUseNBT(boolean useNBT) {
		this.useNBT = useNBT;
	}

	public void setPlayerKill(boolean playerKill) {
		this.playerKill = playerKill;
	}

	public boolean isPlayerKill() {
		return playerKill;
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public void clear() {
		inv.clear();
	}

	public long getMaxEnergyStored() {
		return clientMax;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack stack = new ItemStack(state.getBlock());
		NBTTagCompound tag = new NBTTagCompound();
		writeToStackNBT(tag);
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setTag("BlockEntityTag", tag);
		stack.getTagCompound().setBoolean("stored", true);
		drops.add(stack);
	}
}
