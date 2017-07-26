package com.tom.apis;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.oredict.OreDictionary;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.mojang.authlib.GameProfile;

import com.google.common.base.Joiner;

import com.tom.api.gui.GuiNumberValueBox;
import com.tom.api.multipart.IModule;
import com.tom.api.multipart.MultipartTomsMod;
import com.tom.apis.Checker.RunnableStorage;
import com.tom.client.EventHandlerClient;
import com.tom.config.Config;
import com.tom.factory.FactoryInit;
import com.tom.lib.Configs;
import com.tom.network.NetworkHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

import com.tom.core.block.BlockHidden;

import com.tom.core.tileentity.TileEntityHidden;
import com.tom.core.tileentity.inventory.ContainerTomsMod;

import io.netty.buffer.ByteBuf;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.multipart.MultipartOcclusionHelper;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.api.slot.EnumFaceSlot;
import mcmultipart.api.slot.IPartSlot;

public final class TomsModUtils {
	private static final int DIVISION_BASE = 1000;
	private static final char[] ENCODED_POSTFIXES = "KMGTPE".toCharArray();
	public static final MultiblockBlockChecker AIR = new MultiblockBlockChecker(worldPos -> worldPos.world.getBlockState(worldPos.pos).getMaterial() == Material.AIR ? 2 : 0, null);
	private static MinecraftServer server;
	private static Format format;
	private static GameProfile profile;
	private static final Joiner comma = Joiner.on(", ").skipNulls();
	private static InventoryCrafting craftingInv = new InventoryCrafting(new ContainerTomsMod() {
		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return true;
		}

		@Override
		public void onCraftMatrixChanged(IInventory inventoryIn) {
		};
	}, 3, 3);

	public static void constructFakePlayer() {
		log.info("Tom's Mod Fake Player: " + Configs.fakePlayerName + ", UUID: " + Config.tomsmodFakePlayerUUID.toString());
		profile = new GameProfile(Config.tomsmodFakePlayerUUID, Configs.fakePlayerName);
	}

	private static Field modifiersField;
	static {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat(".#;0.#");
		format.setDecimalFormatSymbols(symbols);
		format.setRoundingMode(RoundingMode.DOWN);
		TomsModUtils.format = format;
		try {
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
		} catch (Throwable e) {
			throw new RuntimeException("?!?!?", e);
		}
	}
	@SideOnly(Side.CLIENT)
	private static final int DELETION_ID = 2525277;
	@SideOnly(Side.CLIENT)
	private static int lastAdded;

	public static Logger log = LogManager.getLogger("Tom's Mod Utils");

	public static int invertInt(int num, int max) {
		return num < max ? max - num : 0;
	}

	// Table {{{{<x,y,z>},{<color>}}, {{<screenData>}} ,{{<x,y,z,color>},
	// {<screenData>}}, {{<x,y,z,color>}, {<screenData>}}}
	// return {<(int) boolean>, <slot>}
	public static int[] find(int[][][][] table, int x, int y, int z, int color) {
		int[] returnData = {0, 0};
		for (int i = 0;i < table.length;i++) {
			// current {{<x,y,z>},{<color>}}
			int[][] current = table[i][0];
			int[] coords = current[0];
			int xC = coords[0];
			int yC = coords[1];
			int zC = coords[2];
			int colorC = current[1][0];
			if (xC == x && yC == y && zC == z && colorC == color) {
				returnData[0] = 1;
				returnData[1] = i;
				break;
			}
		}
		return returnData;
	}

	public static int[] toInt(boolean[] table) {
		int[] current = {};
		for (int i = 0;i < table.length;i++)
			current[i] = table[i] ? 1 : 0;
		return current;
	}

	public static Object[][][] fillObject(int amount) {
		Object[][][] ret = new Object[amount][][];
		Object[][] empty = new Object[][]{{new Object()}, new Object[65536]};
		for (int i = 0;i < amount;i++) {
			ret[i] = empty;
		}
		return ret;
	}

	public static int[] getCoordTable(int x, int y, int z) {
		return new int[]{x, y, z};
	}

	public static int[] getCoordTable(BlockPos pos) {
		return getCoordTable(pos.getX(), pos.getY(), pos.getZ());
	}

	public static int[] getRelativeCoordTable(int[] current, int x, int y, int z, int d) {
		int[] ret = {};
		int xCoord = current[0], yCoord = current[1], zCoord = current[2];
		if (d == 0) {
			ret = TomsModUtils.getCoordTable(xCoord + x, yCoord + y, zCoord + z);
		} else if (d == 1) {
			ret = TomsModUtils.getCoordTable(xCoord + (-x), yCoord + y, zCoord + z);
		} else if (d == 2) {
			ret = TomsModUtils.getCoordTable(xCoord + z, yCoord + y, zCoord + x);
		} else if (d == 3) {
			ret = TomsModUtils.getCoordTable(xCoord + z, yCoord + y, zCoord + (-x));
		} else {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord);
		}
		return ret;
	}

	public static int[] energyCalculator(int maxEnergy, int energy, int maxEnergyInput, int maxReceive, boolean canConnectEnergy) {
		int energyReceive = 0;
		if (maxEnergy > energy && canConnectEnergy) {
			int canReceive = (energy + maxEnergyInput) < maxEnergy ? maxEnergyInput : maxEnergy - energy;
			energyReceive = maxReceive >= canReceive ? canReceive : maxReceive;
		}
		energy = energy + energyReceive;
		return new int[]{energyReceive, energy, energyReceive};
	}

	public static int[] energyCalculator(int maxEnergy, int energy, int maxEnergyInput, int maxReceive) {
		return energyCalculator(maxEnergy, energy, maxReceive, maxEnergyInput, true);
	}

	public static int[] energyCalculator(int energy, int maxReceive) {
		return energyCalculator(120000, energy, 1000, maxReceive);
	}

	public static boolean isUseable(int xCoord, int yCoord, int zCoord, EntityPlayer player, World worldObj, TileEntity thisT) {
		return isUsable(new BlockPos(xCoord, yCoord, zCoord), player, worldObj, thisT);
	}

	public static boolean isUsable(BlockPos pos, EntityPlayer player, World worldObj, TileEntity thisT) {
		return worldObj.getTileEntity(pos) != thisT ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	public static boolean isUseable(EntityPlayer player, TileEntity thisT) {
		return thisT.getWorld().getTileEntity(thisT.getPos()) != thisT ? false : player.getDistanceSq(thisT.getPos().getX() + 0.5D, thisT.getPos().getY() + 0.5D, thisT.getPos().getZ() + 0.5D) <= 64.0D;
	}

	public static EnumFacing getDirectionFacing(EntityLivingBase entity, boolean includeUpAndDown) {
		double yaw = entity.rotationYaw;
		while (yaw < 0)
			yaw += 360;
		yaw = yaw % 360;
		if (includeUpAndDown) {
			if (entity.rotationPitch > 45)
				return EnumFacing.DOWN;
			else if (entity.rotationPitch < -45)
				return EnumFacing.UP;
		}
		if (yaw < 45)
			return EnumFacing.SOUTH;
		else if (yaw < 135)
			return EnumFacing.WEST;
		else if (yaw < 225)
			return EnumFacing.NORTH;
		else if (yaw < 315)
			return EnumFacing.EAST;

		else
			return EnumFacing.SOUTH;
	}

	public static int getIntFromEnumFacing(EnumFacing d) {
		if (d == EnumFacing.UP) {
			return 1;
		} else if (d == EnumFacing.DOWN)
			return 0;
		else if (d == EnumFacing.NORTH)
			return 1;
		else if (d == EnumFacing.SOUTH)
			return 2;
		else if (d == EnumFacing.EAST)
			return 3;
		else if (d == EnumFacing.WEST)
			return 4;
		else
			return 0;
	}

	public static int[] getCoordTableUD(int[] current, int d) {
		int[] ret = {};
		int xCoord = current[0], yCoord = current[1], zCoord = current[2];
		if (d == 0) {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord + 1);
		} else if (d == 1) {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord - 1);
		} else if (d == 2) {
			ret = TomsModUtils.getCoordTable(xCoord + 1, yCoord, zCoord);
		} else if (d == 3) {
			ret = TomsModUtils.getCoordTable(xCoord - 1, yCoord, zCoord);
		} else if (d == 4) {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord + 1, zCoord);
		} else if (d == 5) {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord - 1, zCoord);
		} else {
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord);
		}
		return ret;
	}

	public static EnumFacing getFD(int d) {
		if (d == 0) {
			return EnumFacing.NORTH;
		} else if (d == 1) {
			return EnumFacing.SOUTH;
		} else if (d == 2) {
			return EnumFacing.WEST;
		} else if (d == 3) {
			return EnumFacing.EAST;
		} else if (d == 4) {
			return EnumFacing.UP;
		} else if (d == 5) {
			return EnumFacing.DOWN;
		} else {
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	public static double rotateMatrixByMetadata(int metadata) {
		if (metadata == 0)
			metadata = 1;
		else if (metadata == 1)
			metadata = 0;
		EnumFacing facing = EnumFacing.VALUES[metadata];
		double metaRotation;
		switch (facing) {
		case UP:
			metaRotation = 0;
			GL11.glRotated(90, 1, 0, 0);
			GL11.glTranslated(0, -1, -1);
			break;
		case DOWN:
			metaRotation = 0;
			GL11.glRotated(-90, 1, 0, 0);
			GL11.glTranslated(0, -1, 1);
			break;
		case NORTH:
			metaRotation = 180;
			break;
		case EAST:
			metaRotation = 270;
			break;
		case SOUTH:
			metaRotation = 0;
			break;
		default:
			metaRotation = 90;
			break;
		}
		GL11.glRotated(metaRotation, 0, 1, 0);
		return metaRotation;
	}

	public static TileEntity getTileEntity(World world, int[] coords) {
		return world.getTileEntity(new BlockPos(coords[0], coords[1], coords[2]));
	}

	public static int[][] separateIntArray(int[][] in, int index1, int index2, int size1, int size2) {
		int[][] ret = new int[size1][size2];
		try {
			int indexStart1 = index1 * size1;
			int indexStart2 = index2 * size2;
			int indexEnd1 = ((index1 + 1) * size1);
			int indexEnd2 = ((index2 + 1) * size2);
			int i2 = 0;
			for (int x = indexStart1;x < indexEnd1;x++) {
				int i1 = 0;
				for (int y = indexStart2;y < indexEnd2;y++) {
					ret[i2][i1] = in[x][y];
					i1++;
				}
				i2++;
			}
		} catch (Exception e) {
			log.error("ERROR: Calculation error");
		/*e.printStackTrace();*/}
		return ret;
	}

	public static int getBurnTime(ItemStack is) {
		return TileEntityFurnace.getItemBurnTime(is);
	}

	public static void setBlockState(World worldIn, BlockPos pos, IBlockState state, int flags) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		worldIn.setBlockState(pos, state, flags);
		if (tileentity != null) {
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
	}

	public static void setBlockState(World worldIn, BlockPos pos, IBlockState state) {
		setBlockState(worldIn, pos, state, 2);
	}

	public static void setBlockState(World worldIn, int x, int y, int z, IBlockState state) {
		setBlockState(worldIn, getBlockPos(x, y, z), state);
	}

	public static void setBlockState(World worldIn, int x, int y, int z, IBlockState state, int flags) {
		setBlockState(worldIn, getBlockPos(x, y, z), state, flags);
	}

	public static BlockPos getBlockPos(int x, int y, int z) {
		return new BlockPos(x, y, z);
	}

	/*@SuppressWarnings("unchecked")
	public static <A,B extends A> List<A> getListFromArray(B... in){
		List<A> list = new ArrayList<A>();
		if(in != null){
			for(A a : in){
				list.add(a);
			}
		}
		return list;
	}*/
	@SideOnly(Side.CLIENT)
	private static void sendNoSpamMessages(ITextComponent[] messages) {
		GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
		for (int i = DELETION_ID + messages.length - 1;i <= lastAdded;i++) {
			chat.deleteChatLine(i);
		}
		for (int i = 0;i < messages.length;i++) {
			chat.printChatMessageWithOptionalDeletion(messages[i], DELETION_ID + i);
		}
		lastAdded = DELETION_ID + messages.length - 1;
	}

	/**
	 * Sends a chat message to the client, deleting past messages also sent via
	 * this method.
	 *
	 * Credit to RWTema for the idea
	 *
	 * @param player
	 *            The player to send the chat message to
	 * @param lines
	 *            The chat lines to send.
	 */
	public static void sendNoSpam(EntityPlayer player, ITextComponent... lines) {
		if (lines.length > 0)
			NetworkHandler.sendTo(new PacketNoSpamChat(lines), (EntityPlayerMP) player);
	}

	/**
	 * @author tterrag1098
	 *
	 *         Ripped from EnderCore (and slightly altered)
	 */
	public static class PacketNoSpamChat implements IMessage {

		private ITextComponent[] chatLines;

		public PacketNoSpamChat() {
			chatLines = new ITextComponent[0];
		}

		private PacketNoSpamChat(ITextComponent... lines) {
			// this is guaranteed to be >1 length by accessing methods
			this.chatLines = lines;
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(chatLines.length);
			for (ITextComponent c : chatLines) {
				ByteBufUtils.writeUTF8String(buf, ITextComponent.Serializer.componentToJson(c));
			}
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			chatLines = new ITextComponent[buf.readInt()];
			for (int i = 0;i < chatLines.length;i++) {
				chatLines[i] = ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(buf));
			}
		}

		public static class Handler implements IMessageHandler<PacketNoSpamChat, IMessage> {

			@Override
			public IMessage onMessage(final PacketNoSpamChat message, MessageContext ctx) {
				Minecraft.getMinecraft().addScheduledTask(new Runnable() {

					@Override
					public void run() {
						sendNoSpamMessages(message.chatLines);
					}
				});
				return null;
			}
		}
	}

	public static ITextComponent getChatMessageFromString(String in, Object... args) {
		return new TextComponentTranslation(in, args);

	}

	public static boolean isEqual(BlockPos pos1, BlockPos pos2) {
		return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ();
	}

	public static RayTraceResult rayTrace(World world, Vec3d pos1, Vec3d pos2) {
		return world.rayTraceBlocks(pos1, pos2, true);
	}

	public static void writeBlockPosToPacket(ByteBuf buf, BlockPos pos) {
		boolean hasPos = pos != null;
		buf.writeBoolean(hasPos);
		if (hasPos) {
			buf.writeInt(pos.getX());
			buf.writeInt(pos.getY());
			buf.writeInt(pos.getZ());
		}
	}

	public static BlockPos readBlockPosFromPacket(ByteBuf buf) {
		if (buf.readBoolean()) { return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()); }
		return null;
	}

	public static ITextComponent getYesNoMessage(boolean value) {
		return new TextComponentTranslation("tomsMod.chat." + (value ? "yes" : "no")).setStyle(new Style().setColor(value ? TextFormatting.GREEN : TextFormatting.RED));
	}

	public static void sendNoSpamTranslate(EntityPlayer player, String key, Object... args) {
		if (!player.world.isRemote)
			sendNoSpam(player, new TextComponentTranslation(key, args));
	}

	public static void sendNoSpamTranslate(EntityPlayer player, Style style, String key, Object... args) {
		if (!player.world.isRemote)
			sendNoSpam(player, new TextComponentTranslation(key, args).setStyle(style));
	}

	public static <T extends Comparable<T>> void setBlockStateWithCondition(World worldObj, BlockPos pos, IBlockState state, IProperty<T> p, T valueE) {
		try {
			if (state.getValue(p) != valueE)
				setBlockState(worldObj, pos, state.withProperty(p, valueE), 2);
		} catch (Exception e) {
			log.catching(e);
		}
	}

	public static TileEntity getTileEntity(World worldIn, BlockPos pos, int dim) {
		if (worldIn.isRemote) {
			log.error("world is remote");
			return null;
		}
		World world = worldIn;
		if (world.provider.getDimension() != dim) {
			if (server == null) {
				log.error("MinecraftServer.getServer() == null");
				FMLLog.bigWarning("MinecraftServer.getServer() == null");
				return null;
			}
			world = server.getWorld(dim);
		}
		if (world == null) {
			log.error("world == null");
			return null;
		}
		return world.getTileEntity(pos);
	}

	public static IBlockState getBlockState(World worldIn, BlockPos pos, int dim) {
		if (worldIn.isRemote) {
			log.error("world is remote");
			return null;
		}
		World world = worldIn;
		if (world.provider.getDimension() != dim) {
			if (server == null) {
				log.error("MinecraftServer.getServer() == null");
				FMLLog.bigWarning("MinecraftServer.getServer() == null");
				return null;
			}
			world = server.getWorld(dim);
		}
		if (world == null) {
			log.error("world == null");
			return null;
		}
		return world.getBlockState(pos);
	}

	public static World getWorld(int dim) {
		if (server == null) {
			log.error("MinecraftServer.getServer() == null");
			FMLLog.bigWarning("MinecraftServer.getServer() == null");
			return null;
		}
		return server.getWorld(dim);
	}

	public static <T extends Comparable<T>> void setBlockStateWithCondition(World worldObj, BlockPos pos, IProperty<T> p, T valueE) {
		setBlockStateWithCondition(worldObj, pos, worldObj.getBlockState(pos), p, valueE);
	}

	/*public static List<ItemStack> getItemStackList(ItemStack... stacks){
		List<ItemStack> list = new ArrayList<ItemStack>();
		for(ItemStack is : stacks){
			list.add(is);
		}
		return list;
	}*/
	public static boolean or(boolean... bs) {
		boolean r = false;
		for (boolean b : bs) {
			r = b || r;
		}
		return r;
	}

	public static boolean and(boolean... bs) {
		boolean r = true;
		for (boolean b : bs) {
			r = b && r;
		}
		return r;
	}

	public static IBlockState getBlockStateFromMeta(int meta, PropertyInteger propState, PropertyDirection propDir, IBlockState defState, int max) {
		EnumFacing facing = EnumFacing.NORTH;
		int state = 0;
		switch (meta) {
		case 0:
			break;
		case 1:
			facing = EnumFacing.SOUTH;
			break;
		case 2:
			facing = EnumFacing.EAST;
			break;
		case 3:
			facing = EnumFacing.WEST;
			break;
		case 4:
			state = 1;
			break;
		case 5:
			state = 1;
			facing = EnumFacing.SOUTH;
			break;
		case 6:
			state = 1;
			facing = EnumFacing.EAST;
			break;
		case 7:
			state = 1;
			facing = EnumFacing.WEST;
			break;
		case 8:
			state = 2;
			break;
		case 9:
			state = 2;
			facing = EnumFacing.SOUTH;
			break;
		case 10:
			state = 2;
			facing = EnumFacing.EAST;
			break;
		case 11:
			state = 2;
			facing = EnumFacing.WEST;
			break;
		case 12:
			state = 3;
			break;
		case 13:
			state = 3;
			facing = EnumFacing.SOUTH;
			break;
		case 14:
			state = 3;
			facing = EnumFacing.EAST;
			break;
		case 15:
			state = 3;
			facing = EnumFacing.WEST;
			break;
		default:
			break;
		}
		return defState.withProperty(propState, Math.min(state, max)).withProperty(propDir, facing);
	}

	public static int getMetaFromState(EnumFacing facing, int state) {
		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}
		switch (facing) {
		case EAST:
			switch (state) {
			case 0:
				return 2;
			case 1:
				return 6;
			case 2:
				return 10;
			case 3:
				return 14;
			default:
				return 2;
			}
		case NORTH:
			switch (state) {
			case 0:
				return 0;
			case 1:
				return 4;
			case 2:
				return 8;
			case 3:
				return 12;
			default:
				return 0;
			}
		case SOUTH:
			switch (state) {
			case 0:
				return 1;
			case 1:
				return 5;
			case 2:
				return 9;
			case 3:
				return 13;
			default:
				return 1;
			}
		case WEST:
			switch (state) {
			case 0:
				return 3;
			case 1:
				return 7;
			case 2:
				return 11;
			case 3:
				return 15;
			default:
				return 3;
			}
		default:
			switch (state) {
			case 0:
				return 0;
			case 1:
				return 4;
			case 2:
				return 8;
			case 3:
				return 12;
			default:
				return 0;
			}
		}
	}

	public static File getSavedFile() {
		File file = null;
		if (server != null) {
			if (server.isSinglePlayer()) {
				String s1 = server.getFile("saves").getAbsolutePath() + File.separator + server.getFolderName() + File.separator + "tm";
				file = new File(s1);
			} else {
				String f = server.getFile("a").getAbsolutePath();
				String s1 = f.substring(0, f.length() - 1) + File.separator + server.getFolderName() + File.separator + "tm";
				file = new File(s1);
			}
		}
		return file;
	}

	/*public static <A,B extends A> A[] toArray(B... in){
		return in;
	}*/
	public static IModule getModule(World world, BlockPos blockPos, EnumFacing pos) {
		if (pos == null)
			return null;
		IMultipartContainer container = MultipartHelper.getContainer(world, blockPos).orElse(null);
		if (container == null) { return null; }

		IPartInfo part = container.get(EnumFaceSlot.fromFace(pos)).orElse(null);
		if (part != null && part.getTile() instanceof IModule) { return (IModule) part.getTile(); }
		return null;
	}

	public static void writeBlockPosToNBT(NBTTagCompound tag, BlockPos pos) {
		if (pos != null) {
			tag.setInteger("posX", pos.getX());
			tag.setInteger("posY", pos.getY());
			tag.setInteger("posZ", pos.getZ());
		} else {
			tag.setBoolean("null", true);
		}
	}

	public static NBTTagCompound writeBlockPosToNewNBT(BlockPos pos) {
		NBTTagCompound tag = new NBTTagCompound();
		writeBlockPosToNBT(tag, pos);
		return tag;
	}

	public static BlockPos readBlockPosFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("null") || !tag.hasKey("posX") || !tag.hasKey("posY") || !tag.hasKey("posZ"))
			return null;
		return new BlockPos(tag.getInteger("posX"), tag.getInteger("posY"), tag.getInteger("posZ"));
	}

	public static boolean areItemStacksEqual(ItemStack stack, ItemStack matchTo, boolean checkMeta, boolean checkNBT, boolean checkMod) {
		if (stack.isEmpty() && matchTo.isEmpty())
			return false;
		if (!stack.isEmpty() && !matchTo.isEmpty()) {
			if (checkMod) {
				String modname = stack.getItem().delegate.name().getResourceDomain();
				return modname != null && modname.equals(matchTo.getItem().delegate.name().getResourceDomain());
			} else {
				if (stack.getItem() == matchTo.getItem()) {
					boolean equals = true;
					if (checkMeta) {
						equals = equals && (stack.getItemDamage() == matchTo.getItemDamage() || stack.getMetadata() == OreDictionary.WILDCARD_VALUE || matchTo.getMetadata() == OreDictionary.WILDCARD_VALUE);
					}
					if (checkNBT) {
						equals = equals && ItemStack.areItemStackTagsEqual(stack, matchTo);
					}
					return equals;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static IBlockState getBlockStateFrom(ItemBlock block, int meta) {
		IBlockState state;
		try {
			state = block.getBlock().getStateFromMeta(meta);
		} catch (Exception e) {
			state = block.block.getDefaultState();
		}
		return state;
	}

	public static IBlockState getBlockStateFrom(ItemStack blockStack) {
		if (!blockStack.isEmpty() && blockStack.getItem() instanceof ItemBlock) { return getBlockStateFrom((ItemBlock) blockStack.getItem(), blockStack.getMetadata()); }
		return Blocks.STONE.getDefaultState();
	}

	@SideOnly(Side.CLIENT)
	public static IBakedModel getBakedModelFromBlockState(IBlockState state, IBakedModel defaultModel) {
		if (state != null) {
			Minecraft mc = Minecraft.getMinecraft();
			BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
			BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
			IBakedModel blockModel = blockModelShapes.getModelForState(state);
			if (blockModel != null && blockModel != blockModelShapes.getModelManager().getMissingModel()) { return blockModel; }
		}
		return defaultModel;
	}

	@SideOnly(Side.CLIENT)
	public static IBakedModel getBakedModelFromItemBlock(ItemBlock blockItem, int meta, IBakedModel defaultModel) {
		return getBakedModelFromBlockState(getBlockStateFrom(blockItem, meta), defaultModel);
	}

	@SideOnly(Side.CLIENT)
	public static IBakedModel getBakedModelFromItemBlockStack(ItemStack blockStack, IBakedModel defaultModel) {
		return getBakedModelFromBlockState(getBlockStateFrom(blockStack), defaultModel);
	}

	public static <T extends Comparable<T>> void setBlockStateProperty(World world, BlockPos pos, IProperty<T> property, T value) {
		setBlockState(world, pos, world.getBlockState(pos).withProperty(property, value));
	}

	/*public static <A,B extends A> A[] fillArray(B[] putTo, B value){
		for(int i = 0;i<putTo.length;i++)putTo[i] = value;
		return putTo;
	}*/
	public static void sendAccessDeniedMessageTo(EntityPlayer player, String tag) {
		if (tag != null)
			sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.accessDeniedP", new TextComponentTranslation(tag));
		else
			sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.accessDenied");
	}

	public static void sendAccessDeniedMessageToWithTag(EntityPlayer player, String tag) {
		if (tag != null)
			sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.accessDeniedP", new TextComponentTranslation("tomsMod.tag", new TextComponentTranslation(tag)));
		else
			sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.accessDenied");
	}

	@SideOnly(Side.CLIENT)
	public static class GuiTextFieldLabel extends GuiLabel {
		GuiTextField field;

		public GuiTextFieldLabel(GuiTextField field) {
			super(null, 0, 0, 0, 0, 0, 0);
			this.field = field;
		}

		@Override
		public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
			field.drawTextBox();
		}
	}

	@SideOnly(Side.CLIENT)
	public static List<GuiLabel> addTextFieldToLabelList(GuiTextField field, List<GuiLabel> labelList) {
		if (field != null)
			labelList.add(new GuiTextFieldLabel(field));
		return labelList;
	}

	@SideOnly(Side.CLIENT)
	public static class GuiNumberValueBoxLabel extends GuiLabel {
		GuiNumberValueBox field;

		public GuiNumberValueBoxLabel(GuiNumberValueBox field) {
			super(null, 0, 0, 0, 0, 0, 0);
			this.field = field;
		}

		@Override
		public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
			field.drawText(mc.fontRenderer, field.color);
		}
	}

	@SideOnly(Side.CLIENT)
	public static List<GuiLabel> addNumberValueBoxToLabelList(GuiNumberValueBox field, List<GuiLabel> labelList) {
		if (field != null)
			labelList.add(new GuiNumberValueBoxLabel(field));
		return labelList;
	}

	@SideOnly(Side.CLIENT)
	public static IModel getModelOBJ(ResourceLocation loc) {
		if (!EventHandlerClient.models.containsKey(loc)) {
			IModel model = null;
			try {
				model = OBJLoader.INSTANCE.loadModel(loc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (model == null)
				model = ModelLoaderRegistry.getMissingModel();
			EventHandlerClient.models.put(loc, model);
		}
		return EventHandlerClient.models.get(loc);
	}

	@SideOnly(Side.CLIENT)
	public static IModel getModelJSON(ResourceLocation loc) {
		if (!EventHandlerClient.models.containsKey(loc)) {
			IModel model = null;
			try {
				model = ModelLoaderRegistry.getModel(loc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (model == null)
				model = ModelLoaderRegistry.getMissingModel();
			EventHandlerClient.models.put(loc, model);
		}
		return EventHandlerClient.models.get(loc);
	}

	public static int getFirstTrue(boolean... bs) {
		if (bs == null || bs.length < 1)
			return -1;
		for (int i = 0;i < bs.length;i++)
			if (bs[i])
				return i;
		return -1;
	}

	public static void breakBlockWithDrops(World world, BlockPos pos) {
		world.destroyBlock(pos, true);
	}

	public static Iterable<BlockPos> getAllBlockPosInBounds(AxisAlignedBB bounds) {
		BlockPos start = new BlockPos(bounds.minX, bounds.minY, bounds.minZ);
		BlockPos end = new BlockPos(bounds.maxX, bounds.maxY, bounds.maxZ);
		return BlockPos.getAllInBox(start, end);
	}

	public static void sendAccessGrantedMessageTo(EntityPlayer player, String tag) {
		if (tag != null)
			sendNoSpamTranslate(player, new Style().setColor(TextFormatting.GREEN), "tomsMod.accessGrantedP", new TextComponentTranslation(tag));
		else
			sendNoSpamTranslate(player, new Style().setColor(TextFormatting.GREEN), "tomsMod.accessGranted");
	}

	public static void sendAccessGrantedMessageToWithTag(EntityPlayer player, String tag) {
		if (tag != null)
			sendNoSpamTranslate(player, new Style().setColor(TextFormatting.GREEN), "tomsMod.accessGrantedP", new TextComponentTranslation("tomsMod.tag", new TextComponentTranslation(tag)));
		else
			sendNoSpamTranslate(player, new Style().setColor(TextFormatting.GREEN), "tomsMod.accessGranted");
	}

	public static void sendAccessGrantedMessageToWithExtraInformation(EntityPlayer player, String tag, ITextComponent extraInfo) {
		if (tag != null)
			sendNoSpam(player, new TextComponentTranslation("tomsMod.accessGrantedP", new TextComponentTranslation("tomsMod.tag", new TextComponentTranslation(tag))).setStyle(new Style().setColor(TextFormatting.GREEN)), extraInfo);
		else
			sendNoSpam(player, new TextComponentTranslation("tomsMod.accessGranted").setStyle(new Style().setColor(TextFormatting.GREEN)), extraInfo);
	}

	public static void sendAccessGrantedMessageToWithExtraInformation(EntityPlayer player, String tag, String extraInfo) {
		sendAccessGrantedMessageToWithExtraInformation(player, tag, new TextComponentTranslation(extraInfo, new TextComponentTranslation("tomsMod.tag", new TextComponentTranslation(tag).setStyle(new Style().setColor(TextFormatting.GREEN))).setStyle(new Style().setColor(TextFormatting.GREEN))).setStyle(new Style().setColor(TextFormatting.GREEN)));
	}

	public static void sendNoSpamTranslateWithTag(EntityPlayer player, Style style, String tag, String extraInfo) {
		if (!player.world.isRemote)
			sendNoSpam(player, new TextComponentTranslation(extraInfo, new TextComponentTranslation("tomsMod.tag", new TextComponentTranslation(tag).setStyle(style)).setStyle(style)).setStyle(style));
	}

	public static ItemStack pushStackToNeighbours(ItemStack stack, World world, BlockPos pos, EnumFacing[] sides) {
		if (sides == null)
			sides = EnumFacing.VALUES;
		for (EnumFacing f : sides) {
			BlockPos p = pos.offset(f);
			IItemHandler inv = getItemHandler(world, p, f.getOpposite(), true);
			if (inv != null) {
				stack = putStackInInventoryAllSlots(inv, stack);
			}
			if (stack.isEmpty())
				break;
		}
		return stack;
	}

	public static void sendNoSpamTranslate(EntityPlayer player, TextFormatting color, String key, Object... args) {
		sendNoSpamTranslate(player, new Style().setColor(color), key, args);
	}

	public static String formatNumber(long number) {
		int width = 4;
		assert number >= 0;
		String numberString = Long.toString(number);
		int numberSize = numberString.length();
		if (numberSize <= width) { return numberString; }

		long base = number;
		double last = base * 1000;
		int exponent = -1;
		String postFix = "";

		while (numberSize > width) {
			last = base;
			base /= DIVISION_BASE;

			exponent++;

			numberSize = Long.toString(base).length() + 1;
			postFix = String.valueOf(ENCODED_POSTFIXES[exponent]);
		}

		String withPrecision = format.format(last / DIVISION_BASE) + postFix;
		String withoutPrecision = Long.toString(base) + postFix;

		String slimResult = (withPrecision.length() <= width) ? withPrecision : withoutPrecision;
		assert slimResult.length() <= width;
		return slimResult;
	}

	@SideOnly(Side.CLIENT)
	public static class GuiRunnableLabel extends GuiLabel {
		GuiRenderRunnable field;

		public GuiRunnableLabel(GuiRenderRunnable field) {
			super(null, 0, 0, 0, 0, 0, 0);
			this.field = field;
		}

		@Override
		public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
			field.run(mouseX, mouseY);
		}
	}

	@SideOnly(Side.CLIENT)
	public static interface GuiRenderRunnable {
		void run(int mouseX, int mouseY);
	}

	@SideOnly(Side.CLIENT)
	public static List<GuiLabel> addRunnableToLabelList(GuiRenderRunnable field, List<GuiLabel> labelList) {
		if (field != null)
			labelList.add(new GuiRunnableLabel(field));
		return labelList;
	}

	public static boolean isClient() {
		return FMLCommonHandler.instance().getEffectiveSide().isClient();
	}

	public static boolean areItemStacksEqualOreDict(ItemStack stack, ItemStack matchTo, boolean checkMeta, boolean checkNBT, boolean checkMod, boolean checkOreDict) {
		if (stack.isEmpty() && matchTo.isEmpty())
			return true;
		if (!stack.isEmpty() && !matchTo.isEmpty()) {
			if (areItemStacksEqual(stack, matchTo, checkMeta, checkNBT, checkMod)) {
				return true;
			} else if (checkOreDict) {
				int[] matchIds = OreDictionary.getOreIDs(matchTo);
				int[] ids = OreDictionary.getOreIDs(stack);
				if (matchIds.length < 1 && ids.length < 1) { return areItemStacksEqual(stack, matchTo, checkMeta, checkNBT, checkMod); }
				boolean equals = false;
				for (int i = 0;i < matchIds.length;i++) {
					for (int j = 0;j < ids.length;j++) {
						if (matchIds[i] == ids[j]) {
							equals = true;
							break;
						}
					}
				}
				if (checkNBT) {
					equals = equals && ItemStack.areItemStackTagsEqual(stack, matchTo);
				}
				return equals;
			}
		}
		return false;
	}

	public static void sendChatMessages(EntityPlayer player, ITextComponent... lines) {
		for (ITextComponent c : lines) {
			player.sendMessage(c);
		}
	}

	public static void sendChatTranslate(EntityPlayer player, String key, Object... args) {
		player.sendMessage(new TextComponentTranslation(key, args));
	}

	public static void sendChatTranslate(EntityPlayer player, Style style, String key, Object... args) {
		player.sendMessage(new TextComponentTranslation(key, args).setStyle(style));
	}

	public static MinecraftServer getServer() {
		return server;
	}

	public static void setServer(MinecraftServer server) {
		TomsModUtils.server = server;
	}

	@SideOnly(Side.CLIENT)
	public static String getTranslatedName(ItemStack stack) {
		String name = I18n.format(stack.getUnlocalizedName() + ".name");
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("display", 10)) {
			NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("display");

			if (nbttagcompound.hasKey("Name", 8)) {
				name = nbttagcompound.getString("Name");
			}
		}
		return name;
	}

	/*@SuppressWarnings("unchecked")
	public static <A,B extends A> List<A> getListFromArrayNullCheck(B... in){
		List<A> list = new ArrayList<A>();
		if(in != null){
			for(A a : in){
				if(a != null)
					list.add(a);
			}
		}
		return list;
	}*/
	public static FakePlayer getFakePlayer(World world) {
		if (world instanceof WorldServer) {
			return FakePlayerFactory.get((WorldServer) world, profile);
		} else
			return null;
	}

	public static List<ItemStack> craft(ItemStack[] input, EntityPlayer player, World world) {
		if (player == null)
			player = getFakePlayer(world);
		for (int i = 0;i < input.length && i < 9;i++) {
			craftingInv.setInventorySlotContents(i, input[i] != null && !input[i].isEmpty() ? input[i].copy() : ItemStack.EMPTY);
		}
		NonNullList<ItemStack> ret = NonNullList.<ItemStack>create();
		ItemStack result = CraftingManager.getInstance().findMatchingRecipe(craftingInv, player.world);
		if (result != null) {
			FMLCommonHandler.instance().firePlayerCraftingEvent(player, result, craftingInv);
			ForgeHooks.setCraftingPlayer(player);
			NonNullList<ItemStack> aitemstack = CraftingManager.getInstance().getRemainingItems(craftingInv, player.world);
			ForgeHooks.setCraftingPlayer(null);
			ret.add(result);
			for (int i = 0;i < aitemstack.size();++i) {
				ItemStack itemstack = craftingInv.getStackInSlot(i);
				ItemStack itemstack1 = aitemstack.get(i);

				if (!itemstack.isEmpty()) {
					craftingInv.decrStackSize(i, 1);
					itemstack = craftingInv.getStackInSlot(i);
				}

				if (!itemstack1.isEmpty()) {
					if (itemstack.isEmpty()) {
						craftingInv.setInventorySlotContents(i, itemstack1);
					} else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
						itemstack1.grow(itemstack.getCount());
						craftingInv.setInventorySlotContents(i, itemstack1);
					} else
						ret.add(itemstack1);
				}
			}
		}
		for (int i = 0;i < 9;i++) {
			ItemStack stack = craftingInv.removeStackFromSlot(i);
			if (!stack.isEmpty()) {
				ret.add(stack);
			}
		}
		return !result.isEmpty() ? ret : null;
	}

	public static ItemStack[] getStackArrayFromInventory(IInventory inv) {
		ItemStack[] ret = new ItemStack[inv.getSizeInventory()];
		for (int i = 0;i < inv.getSizeInventory();i++) {
			ret[i] = inv.getStackInSlot(i);
		}
		return ret;
	}

	public static ItemStack getMathchingRecipe(ItemStack[] input, World world) {
		for (int i = 0;i < input.length && i < 9;i++) {
			craftingInv.setInventorySlotContents(i, input[i]);
		}
		ItemStack result = CraftingManager.getInstance().findMatchingRecipe(craftingInv, world);
		for (int i = 0;i < 9;i++) {
			craftingInv.setInventorySlotContents(i, ItemStack.EMPTY);
		}
		return result;
	}

	public static ItemStack getMathchingRecipe(IInventory input, World world) {
		return getMathchingRecipe(getStackArrayFromInventory(input), world);
	}

	/*@SuppressWarnings("unchecked")
	public static <A,B extends A> List<A> getListFromArrayNullCheckWithCap(int cap, B... in){
		List<A> list = new ArrayList<A>(cap);
		if(in != null){
			for(A a : in){
				if(a != null)
					if(cap != list.size())
						list.add(a);
					else
						break;
			}
		}
		return list;
	}*/
	@SideOnly(Side.CLIENT)
	public static void addActiveTag(List<String> list, boolean active) {
		list.add(I18n.format("tomsMod" + (active ? "." : ".in") + "active"));
	}

	@SideOnly(Side.CLIENT)
	public static IBakedModel getBakedModelFromItemBlockStack(ItemStack blockStack, IBlockState defaultBlock) {
		IBlockState state = blockStack != null ? getBlockStateFrom(blockStack) : defaultBlock;
		return getBakedModelFromBlockState(state, getBakedModelFromBlockState(defaultBlock, null));
	}

	public static List<ItemStack> copyItemStackList(List<ItemStack> in, boolean nullCheck) {
		return copyItemStackList(in, nullCheck, -1);
	}

	public static List<ItemStack> copyItemStackList(List<ItemStack> in, int stacksize) {
		return copyItemStackList(in, false, stacksize);
	}

	public static List<ItemStack> copyItemStackList(List<ItemStack> in, boolean nullCheck, int stacksize) {
		List<ItemStack> list = new ArrayList<>();
		for (int i = 0;i < in.size();i++) {
			ItemStack stack = in.get(i);
			if (!stack.isEmpty()) {
				ItemStack s = stack.copy();
				if (stacksize > 0)
					s.setCount(stacksize);
				list.add(s);
			} else if (!nullCheck) {
				list.add(ItemStack.EMPTY);
			}
		}
		return list;
	}

	public static List<ItemStack> copyItemStackList(List<ItemStack> in) {
		return copyItemStackList(in, false);
	}

	public static List<String> getStringList(String... in) {
		List<String> list = new ArrayList<>();
		if (in != null) {
			for (String a : in) {
				list.add(a);
			}
		}
		return list;
	}

	public static List<ItemStack> getItemStackList(ItemStack... in) {
		List<ItemStack> list = new ArrayList<>();
		if (in != null) {
			for (ItemStack a : in) {
				list.add(a);
			}
		}
		return list;
	}

	public static boolean occlusionTest(IMultipartContainer cont, MultipartTomsMod part, AxisAlignedBB box) {
		if (cont == null)
			return true;
		/*Map<IPartSlot, ? extends IPartInfo> parts = cont.getParts();
		List<AxisAlignedBB> boxes = new ArrayList<>();
		for(Entry<IPartSlot, ? extends IPartInfo> e : parts.entrySet()){
			if(e.getValue() != null){
				if(e.getValue().getTile() != part){
					boxes.addAll(e.getValue().getPart().getOcclusionBoxes(part.getWorld(), part.getPos(), e.getValue()));
				}
			}
		}
		return OcclusionHelper.testIntersection(boxes, Collections.singleton(box));*/
		return !MultipartOcclusionHelper.testContainerBoxIntersection(cont, Collections.singleton(box), s -> s == EnumCenterSlot.CENTER);
	}

	public static boolean areFluidStacksEqual(FluidStack stackA, FluidStack stackB) {
		return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isFluidStackIdentical(stackB) : false);
	}

	public static int getAllTrues(boolean... bs) {
		if (bs == null || bs.length < 1)
			return -1;
		int i2 = 0;
		for (int i = 0;i < bs.length;i++)
			if (bs[i])
				i2++;
		return i2;
	}

	public static void sendChatTranslate(EntityPlayer player, TextFormatting color, String key, Object... args) {
		sendChatTranslate(player, new Style().setColor(color), key, args);
	}

	public static int average_int(int[] values) {
		int i = 0;

		for (int j : values) {
			i += j;
		}

		return MathHelper.ceil((double) i / (double) values.length);
	}

	public static int[] array_intAddLimit(int[] in, int value, int limit) {
		int[] ret = new int[Math.min(in.length + 1, limit)];
		System.arraycopy(in, Math.max(0, in.length - limit), ret, 0, in.length);
		ret[Math.min(in.length, limit - 1)] = value;
		return ret;
	}

	public static boolean areFluidStacksEqual2(FluidStack stackA, FluidStack stackB) {
		return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isFluidEqual(stackB) && stackA.amount <= stackB.amount : false);
	}

	public static boolean checkAll(RunnableStorage killList, final Checker... checkers) {
		boolean state = true;
		killList.runnable = () -> {
			for (Checker c : checkers)
				c.apply(2);
		};
		for (Checker c : checkers) {
			int cS = c.apply(0);
			if (cS == 0)
				return false;
			if (cS == 1)
				state = false;
		}
		if (state) {
			for (Checker c : checkers) {
				c.apply(1);
			}
		}
		return true;
	}

	public static boolean checkAll(List<? extends Checker> checkers, RunnableStorage killList) {
		return checkAll(killList, checkers.toArray(new Checker[0]));
	}

	@SuppressWarnings("unchecked")
	public static boolean matches(List<Object> input, IInventory inv) {
		ArrayList<Object> required = new ArrayList<>(input);
		for (int x = 0;x < inv.getSizeInventory();x++) {
			ItemStack slot = inv.getStackInSlot(x);
			if (!slot.isEmpty()) {
				boolean inRecipe = false;
				Iterator<Object> req = required.iterator();
				while (req.hasNext()) {
					boolean match = false;
					Object next = req.next();
					if (next instanceof ItemStack) {
						match = itemMatches((ItemStack) next, slot);
					} else if (next instanceof List) {
						Iterator<ItemStack> itr = ((List<ItemStack>) next).iterator();
						while (itr.hasNext() && !match) {
							match = itemMatches(itr.next(), slot);
						}
					}
					if (match) {
						inRecipe = true;
						required.remove(next);
						break;
					}
				}
				if (!inRecipe) { return false; }
			}
		}
		return required.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public static Runnable matchesAndConsume(List<Object> input, final IInventory inv) {
		ArrayList<Object> required = new ArrayList<>(input);
		final List<Runnable> applyChanges = new ArrayList<>();
		for (int x = 0;x < inv.getSizeInventory();x++) {
			ItemStack slot = inv.getStackInSlot(x);
			if (!slot.isEmpty()) {
				boolean inRecipe = false;
				Iterator<Object> req = required.iterator();
				while (req.hasNext()) {
					boolean match = false;
					Object next = req.next();
					int amount = -1;
					if (next instanceof ItemStack) {
						match = itemMatches((ItemStack) next, slot);
						amount = ((ItemStack) next).getCount();
					} else if (next instanceof List) {
						Iterator<ItemStack> itr = ((List<ItemStack>) next).iterator();
						while (itr.hasNext() && !match) {
							ItemStack s = itr.next();
							match = itemMatches(s, slot);
							amount = s.getCount();
						}
					}
					if (match) {
						inRecipe = true;
						required.remove(next);
						final int fx = x;
						final int famount = amount;
						applyChanges.add(() -> inv.decrStackSize(fx, famount));
						break;
					}
				}
				if (!inRecipe) { return null; }
			}
		}
		return required.isEmpty() ? () -> runAll(applyChanges) : null;
	}

	public static boolean itemMatches(ItemStack target, ItemStack input) {
		if (input.isEmpty() && !target.isEmpty() || !input.isEmpty() && target.isEmpty()) { return false; }
		return target.getItem() == input.getItem() && target.getItemDamage() == input.getItemDamage() && target.getCount() <= input.getCount();
	}

	public static List<Object> createRecipe(Object... recipe) {
		List<Object> input = new ArrayList<>();
		for (Object in : recipe) {
			if (in instanceof ItemStack) {
				input.add(((ItemStack) in).copy());
			} else if (in instanceof Item) {
				input.add(new ItemStack((Item) in));
			} else if (in instanceof Block) {
				input.add(new ItemStack((Block) in));
			} else if (in instanceof String) {
				input.add(OreDictionary.getOres((String) in));
			} else if (in instanceof Object[]) {
				List<ItemStack> ores = OreDictionary.getOres((String) ((Object[]) in)[0]);
				input.add(copyItemStackList(ores, (Integer) ((Object[]) in)[1]));
			} else {
				String ret = "Invalid shapeless ore recipe: ";
				for (Object tmp : recipe) {
					ret += tmp + ", ";
				}
				throw new RuntimeException(ret);
			}
		}
		return input;
	}

	public static Map<Character, MultiblockBlockChecker> createMaterialMap(Object[][] config, final ItemStack master) {
		Map<Character, MultiblockBlockChecker> materialMap = new HashMap<>();
		for (int i = 0;i < config[0].length;i += 2) {
			char c = (Character) config[0][i];
			Object stateO = config[0][i + 1];
			final BlockData data = new BlockData(stateO);
			/*if(stateO instanceof Object[]){
				Object[] o = (Object[]) stateO;
				stateO = o[0];
				for(int j = 1;j<o.length;j++){
					if(o[j] instanceof BlockProperties){
						prop = (BlockProperties) o[j];
					}else if(o[j] instanceof Boolean){
						h = (boolean) o[j];
					}
				}
			}
			if(stateO instanceof IBlockState){
				IBlockState state = (IBlockState) stateO;
				b = state.getBlock();
				m = b.getMetaFromState(state);
			}else{
				m = -1;
				b = (Block) stateO;
			}
			final int meta = m;
			final Block block = b;
			final BlockProperties properties = prop;
			final boolean hatch = h;*/
			materialMap.put(c, new MultiblockBlockChecker(worldPos -> {
				IBlockState input = worldPos.world.getBlockState(worldPos.pos);
				if (input.getBlock() instanceof BlockHidden) {
					TileEntityHidden te = (TileEntityHidden) worldPos.world.getTileEntity(worldPos.pos);
					if (worldPos.num1 == 2) {
						te.kill();
						return 0;
					}
					return te.blockEquals(data) ? 1 : 0;
				} else {
					if (worldPos.num1 == 1) {
						TileEntityHidden.place(worldPos.world, worldPos.pos, worldPos.pos2, master, worldPos.num2, data);
						return 0;
					} else {
						int m = input.getBlock().getMetaFromState(input);
						return data.matches(input.getBlock(), m) ? 2 : 0;
					}
				}
			}, data));
		}
		return materialMap;
	}

	public static boolean getLayers(Object[][] config, final Map<Character, MultiblockBlockChecker> materialMap, final World world, final EnumFacing facing, final BlockPos pos, RunnableStorage killList) {
		return checkAll(getLayers(config, materialMap, world, facing, pos), killList);
	}

	public static List<BlockChecker> getLayers(Object[][] config, final Map<Character, MultiblockBlockChecker> materialMap, final World world, final EnumFacing facing, final BlockPos pos) {
		List<BlockChecker> list = new ArrayList<>();
		final MutableBlockPos corner = new MutableBlockPos(pos);
		for (int l = 1;l < config.length;l++) {
			final int m = l - 1;
			Object[] objA = config[l];
			for (int k = 0;k < objA.length;k++) {
				Object o = objA[k];
				final int n = k;
				char[] cA = o.toString().toCharArray();
				for (int i = 0;i < cA.length;i++) {
					final int j = i;
					final char c = cA[i];
					if (c == '@') {
						corner.setPos(pos.offset(facing.rotateY(), -i).offset(facing, -k).offset(EnumFacing.DOWN, l - 1));
					} else if (c == ' ') {
						list.add(new BlockChecker(doRun -> AIR.apply(new WorldPos(world, corner.offset(facing.rotateY(), j).offset(facing, n).offset(EnumFacing.UP, m), pos, 0, 0)), 1, c, () -> corner.offset(facing.rotateY(), j).offset(facing, n).offset(EnumFacing.UP, m), m));
					} else if (c == '*') {
						list.add(new BlockChecker(a -> 2, 2, c, () -> corner.offset(facing.rotateY(), j).offset(facing, n).offset(EnumFacing.UP, m), m));
					} else {
						list.add(new BlockChecker(doRun -> {
							MultiblockBlockChecker predicate = materialMap.get(c);
							if (predicate == null)
								predicate = AIR;
							return predicate.apply(new WorldPos(world, corner.offset(facing.rotateY(), j).offset(facing, n).offset(EnumFacing.UP, m), pos, doRun, m));
						}, 0, c, () -> corner.offset(facing.rotateY(), j).offset(facing, n).offset(EnumFacing.UP, m), m));
					}
				}
			}
		}
		return list;
	}

	public static class BlockChecker implements Checker {
		private Checker checker;
		private int type, layer;
		private char c;
		private Supplier<BlockPos> loc;

		public BlockChecker(Checker checker, int type, char c, Supplier<BlockPos> loc, int layer) {
			this.checker = checker;
			this.type = type;
			this.c = c;
			this.loc = loc;
			this.layer = layer;
		}

		@Override
		public int apply(int function) {
			return checker.apply(function);
		}

		public int getType() {
			return type;
		}

		public char getChar() {
			return c;
		}

		public BlockPos getLocation() {
			return loc.get();
		}

		public boolean notAir() {
			return type == 0;
		}

		public int getLayer() {
			return layer;
		}

		public boolean isInLayer(int layer) {
			return layer == -1 || this.layer == layer;
		}
	}

	@SuppressWarnings("unchecked")
	public static Object[] checkAndConsumeMatch(Object[][] recipeA, final IInventory inv, Object[] parts) {
		for (int i = 0;i < recipeA.length;i++) {
			Object[] recipe = recipeA[i];
			List<Object> input = (List<Object>) recipe[0];
			Object[][] extra = (Object[][]) recipe[1];
			Runnable removeItems = matchesAndConsume(input, inv);
			if (removeItems != null) {
				List<Runnable> applyChanges = new ArrayList<>();
				List<ItemStackChecker> output = new ArrayList<>();
				applyChanges.add(removeItems);
				for (int j = 0;j < extra.length;j++) {
					Object[] e = extra[j];
					Object m = e[0];
					if (m instanceof ItemStack) {
						final int slot = (Integer) e[1];
						final ItemStack s = (ItemStack) m;
						final ItemStack ss = inv.getStackInSlot(slot);
						if (ss.isEmpty() || (s.isItemEqual(ss) && ItemStack.areItemStackTagsEqual(s, ss) && ss.getCount() + s.getCount() <= Math.min(s.getMaxStackSize(), inv.getInventoryStackLimit()))) {
							applyChanges.add(new FillRunnable() {

								@Override
								public void run() {
									if (ss.isEmpty()) {
										inv.setInventorySlotContents(slot, s.copy());
									} else {
										ss.grow(s.getCount());
									}
								}
							});
							output.add(new ItemStackChecker(s));
						}
					} else if (m instanceof FluidStack) {
						// Drain
						boolean mode = (Boolean) e[1];
						final FluidStack s = (FluidStack) m;
						final FluidTank h = (FluidTank) parts[(Integer) e[2]];
						if (mode) {
							final FluidStack r = h.drainInternal(s, false);
							if (s.isFluidStackIdentical(r)) {
								applyChanges.add(new UseRunnable() {

									@Override
									public void run() {
										h.drainInternal(r, true);
									}
								});
							} else
								return new Object[]{-1};
						} else {
							int fill = h.fillInternal(s, false);
							if (fill == s.amount) {
								applyChanges.add(new FillRunnable() {

									@Override
									public void run() {
										h.fillInternal(s, true);
									}
								});
								output.add(new ItemStackChecker(ItemStack.EMPTY).setExtraF(s));
							} else
								return new Object[]{-1};
						}
					}
				}
				return new Object[]{i, applyChanges, output};
			}
		}
		return new Object[]{-1};
	}

	@FunctionalInterface
	public static interface FillRunnable extends Runnable {
	}

	@FunctionalInterface
	public static interface UseRunnable extends Runnable {
	}

	public static void runAll(List<Runnable> list) {
		for (int i = 0;i < list.size();i++)
			if (list.get(i) != null)
				list.get(i).run();
	}

	public static List<FluidStack> getFluidStack(Object[][] in, boolean mode) {
		List<FluidStack> list = new ArrayList<>();
		for (int i = 0;i < in.length;i++) {
			Object[] e = in[i];
			Object m = e[0];
			if (m instanceof FluidStack) {
				if ((Boolean) e[1] == mode) {
					list.add((FluidStack) m);
				}
			}
		}
		return list;
	}

	public static List<ResourceLocation> getResourceLocationList(String... strings) {
		List<ResourceLocation> ret = new ArrayList<>();
		for (int i = 0;i < strings.length;i++) {
			ret.add(new ResourceLocation(strings[i]));
		}
		return ret;
	}

	public static ItemStack loadItemStackFromNBT(NBTTagCompound compoundTag) {
		return new ItemStack(compoundTag);
	}

	public static ItemStack copyItemStack(ItemStack stack) {
		return stack == null ? null : stack.copy();
	}

	public static boolean isNull(ItemStack s) {
		return s.isEmpty();
	}

	public static NBTTagList saveAllItems(List<ItemStack> list) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0;i < list.size();++i) {
			ItemStack itemstack = list.get(i);

			if (!itemstack.isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				itemstack.writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}
		return nbttaglist;
	}

	public static NBTTagList saveAllItems(IInventory inv) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0;i < inv.getSizeInventory();++i) {
			ItemStack itemstack = inv.getStackInSlot(i);

			if (!itemstack.isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				itemstack.writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}
		return nbttaglist;
	}

	public static void loadAllItems(NBTTagList nbttaglist, List<ItemStack> list) {
		for (int i = 0;i < nbttaglist.tagCount();++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < list.size()) {
				list.set(j, new ItemStack(nbttagcompound));
			}
		}
	}

	public static void loadAllItems(NBTTagList nbttaglist, IInventory inv) {
		inv.clear();
		int invSize = inv.getSizeInventory();
		for (int i = 0;i < nbttaglist.tagCount();++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < invSize) {
				inv.setInventorySlotContents(j, new ItemStack(nbttagcompound));
			}
		}
	}

	public static void loadAllItems(NBTTagCompound tag, String name, IInventory inv) {
		loadAllItems(tag.getTagList(name, 10), inv);
	}

	public static int getSpeedUpgradeCount(IInventory inv, int slot, int max) {
		return Math.min(slot < 0 ? 0 : !inv.getStackInSlot(slot).isEmpty() && inv.getStackInSlot(slot).getItem() == FactoryInit.speedUpgrade ? inv.getStackInSlot(slot).getCount() : 0, 4);
	}

	@SuppressWarnings("deprecation")
	public static boolean interactWithFluidHandler(IFluidHandler tankOnSide, EntityPlayer playerIn, EnumHand hand) {
		if (tankOnSide != null) {
			FluidActionResult r = FluidUtil.interactWithFluidHandler(playerIn.getHeldItem(hand), tankOnSide, playerIn);
			if (r.success)
				playerIn.setHeldItem(hand, r.result);
			return r.success;
		} else
			return false;
	}

	public static IPartInfo getPartInfo(IMultipartContainer c, IMultipartTile part) {
		Map<IPartSlot, ? extends IPartInfo> parts = c.getParts();
		for (Entry<IPartSlot, ? extends IPartInfo> e : parts.entrySet()) {
			if (e.getValue() != null && e.getValue().getTile() == part) { return e.getValue(); }
		}
		return null;
	}

	public static IPartInfo getPartInfo(IMultipartContainer c, IBlockState state) {
		Map<IPartSlot, ? extends IPartInfo> parts = c.getParts();
		for (Entry<IPartSlot, ? extends IPartInfo> e : parts.entrySet()) {
			if (e.getValue() != null && e.getValue().getState() == state) { return e.getValue(); }
		}
		return null;
	}

	public static boolean getBit(int in, int slot) {
		return (in & (1 << slot)) > 0;
	}

	public static int setBit(int in, int slot, boolean value) {
		if (value) {
			in |= 1 << slot;
		} else {
			in &= ~(1 << slot);
		}
		return in;
	}

	public static <T> NBTTagList writeCollection(Collection<T> listIn, Function<T, NBTTagCompound> mapper) {
		NBTTagList nbttaglist = new NBTTagList();
		listIn.stream().filter(o -> o != null).map(mapper).forEach(nbttaglist::appendTag);
		return nbttaglist;
	}

	public static <T> void readCollection(Collection<T> listIn, NBTTagList list, Function<NBTTagCompound, T> mapper) {
		for (int i = 0;i < list.tagCount();i++) {
			listIn.add(mapper.apply(list.getCompoundTagAt(i)));
		}
	}

	@SideOnly(Side.CLIENT)
	public static String formatYesNoMessage(String key, boolean value, Object... parameters) {
		String msg = (value ? TextFormatting.GREEN : TextFormatting.RED) + I18n.format("tomsMod.chat." + (value ? "yes" : "no"));
		return I18n.format(key, parameters == null || parameters.length < 1 ? msg : Stream.of(parameters).map(v -> "@val".equalsIgnoreCase(v.toString()) ? msg : v).collect(Collectors.toList()).toArray());
	}

	@SideOnly(Side.CLIENT)
	public static String formatOrDefault(String key, String def, Object... parameters) {
		String f = I18n.format(key, parameters);
		if (f.equalsIgnoreCase(key) || f.startsWith("Format error:"))
			return String.format(def, parameters);
		else
			return f;
	}

	public static String join(int[] in) {
		return comma.join(Arrays.stream(in).iterator());
	}

	public static IItemHandler getItemHandler(World world, BlockPos pos, EnumFacing side, boolean includeEntities) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IDrawer) {
			return new DrawerWrapper((IDrawer) te);
		} else if (te instanceof IDrawerGroup) {
			return new DrawerGroupWrapper((IDrawerGroup) te);
		} else if (te == null || !te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			IInventory inv = includeEntities ? TileEntityHopper.getInventoryAtPosition(world, pos.getX(), pos.getY(), pos.getZ()) : (te instanceof IInventory ? (IInventory) te : null);
			if (inv != null) {
				if (inv instanceof ISidedInventory) {
					return new SidedInvWrapper((ISidedInventory) inv, side);
				} else {
					return new InvWrapper(inv);
				}
			} else
				return null;
		} else {
			return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
		}
	}

	public static <T> List<T> copyOf(List<T> profilingData) {
		List<T> ret = new ArrayList<>();
		ret.addAll(profilingData);
		return ret;
	}

	public static <T> void addIfNotNull(List<T> list, T in) {
		if (in != null)
			list.add(in);
	}

	public static <T> T[] createArray(int states, Supplier<T> s, T[] t) {
		List<T> ret = new ArrayList<>();
		for (int i = 0;i < states;i++)
			ret.add(s.get());
		return ret.toArray(t);
	}

	public static int[] createIntsFromBlockPos(BlockPos pos) {
		long h = pos.toLong();
		int a = (int) (h >> 32);
		int b = (int) h;
		return new int[]{a, b};
	}

	public static BlockPos createBlockPos(int a, int b) {
		return BlockPos.fromLong((long) a << 32 | b & 0xFFFFFFFFL);
	}

	public static ItemStack putStackInInventoryAllSlots(IItemHandler inventory, ItemStack sIn) {
		for (int i = 0;i < inventory.getSlots() && !sIn.isEmpty();i++) {
			sIn = inventory.insertItem(i, sIn, false);
		}
		return sIn;
	}

	public static int getColorFrom(TextFormatting f) {
		switch (f) {
		case AQUA:
			return 0x55FFFF;
		case BLACK:
			return 0;
		case BLUE:
			return 0x5555FF;
		case BOLD:
			return 0;
		case DARK_AQUA:
			return 0x00AAAA;
		case DARK_BLUE:
			return 0x0000AA;
		case DARK_GRAY:
			return 0x555555;
		case DARK_GREEN:
			return 0x00AA00;
		case DARK_PURPLE:
			return 0xAA00AA;
		case DARK_RED:
			return 0xAA0000;
		case GOLD:
			return 0xFFAA00;
		case GRAY:
			return 0xAAAAAA;
		case GREEN:
			return 0x55FF55;
		case ITALIC:
			return 0;
		case LIGHT_PURPLE:
			return 0xFF55FF;
		case OBFUSCATED:
			return 0;
		case RED:
			return 0xFF5555;
		case RESET:
			return 0;
		case STRIKETHROUGH:
			return 0;
		case UNDERLINE:
			return 0;
		case WHITE:
			return 0xFFFFFF;
		case YELLOW:
			return 0xFFFF55;
		default:
			return 0;
		}
	}

	public static void setFinalField(Field field, Object ins, Object value) throws Throwable {
		field.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(ins, value);
	}

	public static void trySetFinalField(Field field, Object ins, Object value, Logger log, String errorMsg) {
		try {
			setFinalField(field, ins, value);
		} catch (Throwable e) {
			log.error(errorMsg, e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void trySetFinalField(Class clazz, Predicate<Object> insChecker, Object ins, Object value, Logger log, String errorMsg) {
		try {
			for (Field f : clazz.getDeclaredFields()) {
				f.setAccessible(true);
				if (insChecker.test(f.get(ins))) {
					setFinalField(f, ins, value);
				}
			}
		} catch (Throwable e) {
			log.error(errorMsg, e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void setFinalField(Class clazz, Predicate<Object> insChecker, Object ins, Object value) throws Throwable {
		for (Field f : clazz.getDeclaredFields()) {
			f.setAccessible(true);
			if (insChecker.test(f.get(ins))) {
				setFinalField(f, ins, value);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void trySetFinalField(Class clazz, Class type, Object ins, Object value, Logger log, String errorMsg) {
		trySetFinalField(clazz, type::isInstance, ins, value, log, errorMsg);
	}

	@SuppressWarnings("rawtypes")
	public static void setFinalField(Class clazz, Class type, Object ins, Object value) throws Throwable {
		setFinalField(clazz, type::isInstance, ins, value);
	}

	public static void writeInventory(String name, NBTTagCompound compound, IInventory inv) {
		compound.setTag(name, saveAllItems(inv));
	}

	public static boolean isItemListEmpty(NonNullList<ItemStack> list) {
		return list.stream().allMatch(ItemStack::isEmpty);
	}

	public static NBTTagCompound writeBlock(Block block) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("b", block.delegate.name().toString());
		return tag;
	}

	public static Block readBlock(NBTTagCompound tag) {
		return Block.REGISTRY.getObject(new ResourceLocation(tag.getString("b")));
	}
}
