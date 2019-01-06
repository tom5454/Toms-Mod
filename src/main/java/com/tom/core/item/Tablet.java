package com.tom.core.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.item.ILinkContainer;
import com.tom.api.item.IWirelessDevice;
import com.tom.api.tileentity.IConnector;
import com.tom.client.CustomModelLoader;
import com.tom.core.CoreInit;
import com.tom.core.model.ModelTablet;
import com.tom.handler.TMPlayerHandler;
import com.tom.lib.api.energy.ItemEnergyContainer;
import com.tom.util.TomsModUtils;

public class Tablet extends ItemEnergyContainer implements ILinkContainer, IModelRegisterRequired, IWirelessDevice {
	/*private static int[] access;
	private static int[] Ant;
	private static int[] antaccess;
	private static int[] NoC;
	private static int[] Jammed;
	private static int empty;
	private static int itemIcon;
	private static int i = 0;*/

	public Tablet() {
		super(100000, 1024);
		this.setMaxStackSize(1);
	}

	public boolean connect(EntityPlayer player, ItemStack is, IConnector conn) {
		if (is.getTagCompound() != null) {
			NBTTagCompound tag = is.getTagCompound();
			if (tag.hasKey("active")) {
				if (tag.getBoolean("active")) {
					TabletHandler tab = this.getTablet(player);
					if (tab != null) {
						if (conn.getLevel() > 0 || conn.locked()) {
							InventoryPlayer inv = player.inventory;
							NBTTagCompound modemTag = null;
							for (int i = 0;i < inv.getSizeInventory();i++) {
								ItemStack c = inv.getStackInSlot(i);
								if (c != null && c.getItem() == CoreInit.connectionModem) {
									modemTag = c.getTagCompound();
									break;
								}
							}
							if (modemTag != null) {
								int t = modemTag.hasKey("tier") ? modemTag.getInteger("tier") : 0;
								if (t >= conn.getLevel() && tab.apAntenna) {
									if (conn.locked()) {
										if (modemTag.hasKey("linkList")) {
											NBTTagList list = (NBTTagList) modemTag.getTag("linkList");
											boolean found = false;
											for (int i = 0;i < list.tagCount();i++) {
												NBTTagCompound cTag = list.getCompoundTagAt(i);
												int x = conn.getPos2().getX(), y = conn.getPos2().getY(), z = conn.getPos2().getZ();
												if (cTag.getInteger("x") == x && cTag.getInteger("y") == y && cTag.getInteger("z") == z) {
													found = true;
													break;
												}
											}
											if (found) {
												if(tab.connectedAccessPoints.add(conn)){
													this.sendUpdates("tablet_connect_locked", is, player, player.world, tab, true);
												}
												return !tab.jammedLast;
											}
										}
									} else {
										if(tab.connectedAccessPoints.add(conn)){
											this.sendUpdates("tablet_connect_locked", is, player, player.world, tab, true);
										}
										return !tab.jammedLast;
									}
								}
							}
						} else {
							if(tab.connectedAccessPoints.add(conn)){
								this.sendUpdates("tablet_connect", is, player, player.world, tab, true);
							}
							return !tab.jammedLast;
						}
					}
				}
			}
		}
		return false;
	}

	public void receive(World world, Object o, ItemStack is, EntityPlayer player) {
		TabletHandler te = this.getTablet(player);
		te.queueEvent("tablet_receive", new Object[]{player.getName(), o});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		/*itemIcon = regIcon("tomsmodcore:tablet/TabletOff", "inventory");
		NoC = this.registerIconArray("TabNoC");
		access = this.registerIconArray("TabletAPC");
		Ant = this.registerIconArray("TabletAC");
		antaccess = this.registerIconArray("TabletAllC");
		Jammed = this.registerIconArray("TabletJ");
		empty = regIcon("tomsmodcore:tablet/TabletEmpty", "inventory");*/
		CustomModelLoader.addOverride(new ResourceLocation("tomsmodcore:tablet"), new ModelTablet());
		CoreInit.registerRender(this, 0, "tomsmodcore:tablet");
	}

	/*private static int regIcon(String string, String string2) {
		i++;
		CoreInit.registerRender(CoreInit.Tablet, i - 1, string);
		return i - 1;
	}*/

	/*public int getIcon(ItemStack is) {
		if (is.getTagCompound() != null) {
			NBTTagCompound tag = is.getTagCompound();
			if (tag.hasKey("active")) {
				boolean act = tag.getBoolean("active");
				boolean bat = tag.hasKey("batEmpty") ? tag.getBoolean("batEmpty") : false;
				if (bat) {
					return empty;
				} else if (act) {
					if (tag.hasKey("Energy")) {
						double per = this.getEnergyStored(is) * 100 / this.getMaxEnergyStored(is);
						int p = MathHelper.floor(per);
						// System.out.println(per);
						boolean ant = tag.hasKey("ant") ? tag.getBoolean("ant") : false;
						boolean ap = tag.hasKey("ap") ? tag.getBoolean("ap") : false;
						boolean j = tag.hasKey("j") ? tag.getBoolean("j") : false;
						int[] c = j ? Jammed : (ant ? (ap ? antaccess : Ant) : (ap ? access : NoC));
						if (p > 80) {
							return c[0];
						} else if (p < 81 && p > 40) {
							return c[1];
						} else if (p > 30 && p < 41) {
							return c[2];
						} else if (p > 10 && p < 31) {
							return c[3];
						} else if (p < 11) { return c[4]; }
					}
					return itemIcon;
				} else {
					return itemIcon;
				}
			} else {
				return itemIcon;
			}
		} else {
			return itemIcon;
		}
	}*/

	/*private int[] registerIconArray(String icon) {
		return new int[]{regIcon("tomsmodcore:tablet/" + icon + "Full", "inventory"), regIcon("tomsmodcore:tablet/" + icon + "75", "inventory"), regIcon("tomsmodcore:tablet/" + icon + "50", "inventory"), regIcon("tomsmodcore:tablet/" + icon + "25", "inventory"), regIcon("tomsmodcore:tablet/" + icon + "5", "inventory")};
	}*/

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag isAdvanced) {
		list.add(getInfo(itemStack));
	}

	/*public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		this.onItemUse(is, player, world);
		return false;
	}
	/*public boolean onItemUseFirst(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		this.onItemUse(is, player, world);
		return false;
	}*/
	private void onItemUse(ItemStack is, EntityPlayer player, World world) {
		if (is.getTagCompound() != null) {
			NBTTagCompound tag = is.getTagCompound();
			if (tag.hasKey("active")) {
				if (player.isSneaking()) {
					if (this.getEnergyStored(is) < 10) {
						is.getTagCompound().setBoolean("batEmpty", true);
						is.getTagCompound().setInteger("batTim", 20);
					} else {
						is.getTagCompound().setBoolean("active", !tag.getBoolean("active"));
					}
				} else {
					if (this.getEnergyStored(is) > 10 && !world.isRemote && tag.getBoolean("active")) {
						/*player.openGui(CoreInit.modInstance, GuiHandler.GuiIDs.tablet.ordinal(), world, (int)player.posX, (int)player.posY, (int)player.posZ);
						NetworkHandler.sendTo(new PacketTabletGui(this.getTablet(is, world)), (EntityPlayerMP)player);*/
						this.activate(is, player, world, this.getTablet(player));
					}
				}
			} else {
				is.getTagCompound().setBoolean("active", true);
			}
		} else {
			is.setTagCompound(new NBTTagCompound());
			is.getTagCompound().setBoolean("batEmpty", true);
			is.getTagCompound().setInteger("batTim", 20);
		}
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int par4, boolean par5) {
		if (is.getTagCompound() != null && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			NBTTagCompound tag = is.getTagCompound();
			if (tag.hasKey("active")) {
				if (tag.getBoolean("active")) {
					double energyEx = this.extractEnergy(is, .1, true);
					if (energyEx == .1) {
						this.extractEnergy(is, .1, false);
						//TileEntityTabletController te = this.getTabletController(is, world);
						TabletHandler tab = this.getTablet(player);
						/*double x = entity.posX;
						double y = entity.posY;
						double z = entity.posZ;*/
						if (!world.isRemote && tab != null) {
							boolean update = tab.clean(player.posX, player.posY, player.posZ);
							if(!tag.hasKey("tabid", NBT.TAG_ANY_NUMERIC))tag.setLong("tabid", Math.abs(itemRand.nextLong()));
							tab.tabCX = tag.getInteger("x");
							tab.tabCY = tag.getInteger("y");
							tab.tabCZ = tag.getInteger("z");
							tab.connectedToTabC = tag.getBoolean("connected");
							InventoryPlayer inv = ((EntityPlayer) entity).inventory;
							NBTTagCompound modemTag = null;
							for (int i = 0;i < inv.getSizeInventory();i++) {
								ItemStack c = inv.getStackInSlot(i);
								if (c != null && c.getItem() == CoreInit.connectionModem) {
									modemTag = c.getTagCompound();
									break;
								}
							}
							if (modemTag != null) {
								tab.modemTag = modemTag;
								tab.hasModem = true;
							} else {
								tab.hasModem = false;
							}
							tab.playerName = entity.getName();
							tag.setBoolean("ant", !tab.jammedLast && !tab.antenna.isEmpty());
							tag.setBoolean("ap", !tab.jammedLast && !tab.connectedAccessPoints.isEmpty());
							tag.setBoolean("j", tab.jammedLast);
							tag.setTag("antpos", TomsModUtils.writeBlockPosToNewNBT(!tab.jammedLast && !tab.antenna.isEmpty() ? tab.antenna.stream().findFirst().get().getPos2() : null));
							tag.setInteger("antrange", !tab.jammedLast && !tab.antenna.isEmpty() ? tab.antenna.stream().findFirst().get().getMaxRange() : 16);
							/*NBTTagList sList = new NBTTagList();
							for(LuaSound s : tab.sounds){
								sList.appendTag(new NBTTagString(s.sound.replace(':', '|')));
							}
							is.getTagCompound().setTag("soundList", sList);*/
							if (update)
								this.sendUpdates("tablet_update", is, (EntityPlayer) entity, world, tab, false);
							//is.getTagCompound().setTag("terminal", tab.term.writeToNBT());
							this.sendUpdates("", is, (EntityPlayer) entity, world, tab, false);
						} else if (world.isRemote) {
							/*NBTTagList l = (NBTTagList) is.getTagCompound().getTag("soundList");
							List<ISound> backup = new ArrayList<ISound>(GlobalFields.tabletSounds);
							for(int i = 0;i<l.tagCount();i++){
								String s = l.getStringTagAt(i);
								boolean f = false;
								ResourceLocation loc = new ResourceLocation(s);
								String locS = loc.getResourceDomain() + ":" + loc.getResourcePath();
								for(ISound cS : GlobalFields.tabletSounds){
									ResourceLocation locC = cS.getPositionedSoundLocation();
									String locCS = locC.getResourceDomain() + ":" + locC.getResourcePath();
									//CoreInit.log.info(locS + "|" + locCS);
									if(locS.equals(locCS)){
										f = true;
										backup.remove(cS);
										break;
									}
								}
								SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
								if(!f){
									ISound sound = PositionedSoundRecord.func_147673_a(loc);
									CoreInit.log.info("Playing sound");
									GlobalFields.tabletSounds.add(sound);
									soundHandler.playSound(sound);
								}
								for(ISound cS : backup){
									GlobalFields.tabletSounds.remove(cS);
									soundHandler.stopSound(cS);
									CoreInit.log.info("Stopping sound");
								}
							}*/
						}

					} else {
						tag.setBoolean("active", false);
						this.sendUpdates("discharge", is, (EntityPlayer) entity, world, this.getTablet(player), true);
					}
				}
			}
			if (is.getTagCompound().hasKey("batTim") && is.getTagCompound().hasKey("batEmpty") && is.getTagCompound().getBoolean("batEmpty")) {
				int t = is.getTagCompound().getInteger("batTim");
				if (t > 0) {
					is.getTagCompound().setInteger("batTim", t - 1);
				} else {
					is.getTagCompound().setBoolean("batEmpty", false);
				}
			}
		}
		//is.setItemDamage(this.getIcon(is));
	}

	private TabletHandler getTablet(EntityPlayer player) {
		/*if (is.getTagCompound() != null && is.getTagCompound().hasKey("x") && is.getTagCompound().hasKey("y") && is.getTagCompound().hasKey("z") && is.getTagCompound().hasKey("id")) {
			TileEntity tile = world.getTileEntity(new BlockPos(is.getTagCompound().getInteger("x"), is.getTagCompound().getInteger("y"), is.getTagCompound().getInteger("z")));
			if (tile instanceof TileEntityTabletController) {
				TileEntityTabletController te = (TileEntityTabletController) tile;
				int id = is.getTagCompound().getInteger("id");
				return te.getTablet(id);
			}
		}*/
		return TMPlayerHandler.getPlayerHandler(player).tabletHandler;
	}

	/*private TileEntityTabletController getTabletController(ItemStack is, World world) {
		if (is.getTagCompound() != null && is.getTagCompound().hasKey("x") && is.getTagCompound().hasKey("y") && is.getTagCompound().hasKey("z") && is.getTagCompound().hasKey("id")) {
			TileEntity tile = world.getTileEntity(new BlockPos(is.getTagCompound().getInteger("x"), is.getTagCompound().getInteger("y"), is.getTagCompound().getInteger("z")));
			if (tile instanceof TileEntityTabletController) {
				TileEntityTabletController te = (TileEntityTabletController) tile;
				// int id = is.getTagCompound().getInteger("id");
				return te;
			}
		}
		return null;
	}*/

	/*private TabletHandler getTablet(TileEntityTabletController te, ItemStack is) {
		if (te != null) {
			int id = is.getTagCompound().getInteger("id");
			return te.getTablet(id);
		}
		return null;

	}*/

	/*public int[] getConnected(int id, World world, ItemStack is) {
		if (is.getTagCompound() != null) {
			NBTTagCompound tag = is.getTagCompound();
			if (tag.hasKey("active")) {
				if (tag.getBoolean("active")) {
					TabletHandler tab = this.getTablet(is, world);
					if (tab != null) {
						if (id == 0) {
							if (tab.connectedToAccessPoint) { return new int[]{1, tab.apX, tab.apY, tab.apZ}; }
						} else if (id == 1) {
							if (tab.connectedToAntenna) { return new int[]{1, tab.antX, tab.antY, tab.antZ}; }
						}
					}
				}
			}
		}
		return new int[]{0};
	}*/

	private void activate(ItemStack is, EntityPlayer player, World world, TabletHandler tab) {
		this.sendUpdates("tablet_activate", is, player, world, tab, true);
		// player.openGui(CoreInit.modInstance,
		// GuiHandler.GuiIDs.tablet.ordinal(), world, (int)player.posX,
		// (int)player.posY, (int)player.posZ);
	}

	private void sendUpdates(String event, ItemStack is, EntityPlayer player, World world, TabletHandler tab, boolean force) {
		if (!world.isRemote) {
			/*int energy = is.getTagCompound().getInteger("Energy");
			double yaw = new Double(player.rotationYaw);
			double pitch = new Double(player.rotationPitch);*/
			//tab.obj = new Object[]{player.getName(), tab.connectedToAccessPoint, tab.connectedToAntenna, tab.apX, tab.apY, tab.apZ, tab.antX, tab.antY, tab.antZ, player.posX, player.posY, player.posZ, yaw, pitch, energy, tab.isJammed, tab.jX, tab.jY, tab.jZ};
			if (force)
				tab.getUpdates(player.getName(), event);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		this.onItemUse(playerIn.getHeldItem(hand), playerIn, worldIn);
		return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if(world.getBlockState(pos).getBlock() == CoreInit.TabletController){
			if(!world.isRemote){
				CoreInit.TabletController.onBlockActivated(world, pos, world.getBlockState(pos), player, hand, side, hitX, hitY, hitZ);
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.PASS;
		}
		this.onItemUse(player.getHeldItem(hand), player, world);
		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || (oldStack != null && newStack != null && (oldStack.getMetadata() != newStack.getMetadata() || oldStack.getItem() != newStack.getItem()));
	}

	@Override
	public Tablet setUnlocalizedName(String unlocalizedName) {
		super.setUnlocalizedName(unlocalizedName);
		return this;
	}

	@Override
	public Tablet setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}
}
