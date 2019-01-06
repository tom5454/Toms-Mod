package com.tom.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.JsonUtils;
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

import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.tom.api.gui.GuiNumberValueBox;
import com.tom.api.multipart.IModule;
import com.tom.api.multipart.MultipartTomsMod;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.factory.FactoryInit;
import com.tom.lib.utils.TomsUtils;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;
import com.tom.util.Checker.RunnableStorage;

import com.tom.core.block.BlockHidden;

import com.tom.core.tileentity.TileEntityHidden;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.multipart.MultipartOcclusionHelper;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.api.slot.EnumFaceSlot;
import mcmultipart.api.slot.IPartSlot;

public final class TomsModUtils extends TomsUtils {
	public static final MultiblockBlockChecker AIR = new MultiblockBlockChecker(worldPos -> worldPos.world.getBlockState(worldPos.pos).getMaterial() == Material.AIR ? 2 : 0, null);

	public static int invertInt(int num, int max) {
		return num < max ? max - num : 0;
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

	public static boolean isEqual(BlockPos pos1, BlockPos pos2) {
		return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ();
	}

	public static RayTraceResult rayTrace(World world, Vec3d pos1, Vec3d pos2) {
		return world.rayTraceBlocks(pos1, pos2, true);
	}

	public static ITextComponent getYesNoMessage(boolean value) {
		return new TextComponentTranslation("tomsMod.chat." + (value ? "yes" : "no")).setStyle(new Style().setColor(value ? TextFormatting.GREEN : TextFormatting.RED));
	}

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

	@SuppressWarnings("deprecation")
	public static IBlockState getBlockStateFrom(ItemBlock block, int meta) {
		IBlockState state;
		try {
			state = block.getBlock().getStateFromMeta(meta);
		} catch (Exception e) {
			state = block.getBlock().getDefaultState();
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

	public static int getSpeedUpgradeCount(IInventory inv, int slot, int max) {
		return Math.min(slot < 0 ? 0 : !inv.getStackInSlot(slot).isEmpty() && inv.getStackInSlot(slot).getItem() == FactoryInit.speedUpgrade ? inv.getStackInSlot(slot).getCount() : 0, 4);
	}

	public static boolean interactWithFluidHandler(IFluidHandler tankOnSide, EntityPlayer playerIn, EnumHand hand) {
		if (tankOnSide != null) {
			return FluidUtil.interactWithFluidHandler(playerIn, hand, tankOnSide);
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

	public static long randomLong() {
		return rng.nextLong();
	}
	/**
	 * 0: in main but not in check
	 * 1: in check but not in main
	 * */
	@SuppressWarnings("unchecked")
	public static <T> List<T>[] findDifference(Collection<T> main, Collection<T> check, boolean modify) {
		if(check.isEmpty()){
			List<T>[] ret = new List[2];
			ret[0] = new ArrayList<>(main);
			ret[1] = Collections.emptyList();
			if(modify)main.clear();
			return ret;
		}else if(main.isEmpty()){
			List<T>[] ret = new List[2];
			ret[0] = Collections.emptyList();
			ret[1] = new ArrayList<>(check);
			if(modify)main.addAll(check);
			return ret;
		}else{
			List<T>[] ret = new List[2];
			List<T> list = new ArrayList<>(main);
			list.removeAll(check);
			ret[0] = list;
			list = new ArrayList<>(check);
			list.removeAll(main);
			ret[1] = list;
			if(modify){
				main.clear();
				main.addAll(check);
			}
			return ret;
		}
	}

	public static void walkResources(String root, Gson gson, JsonContext ctx, BiConsumer<JsonObject, String> parser){
		List<String> names = new ArrayList<>();
		getResourcePath(root, path -> {
			try{
				Iterator<Path> iterator = Files.walk(path).iterator();
				iterateFiles(root, iterator, ctx, path, gson, names, parser);
				File cfgroot = new File(CoreInit.configFolder, root);
				cfgroot.mkdirs();
				iterator = Files.walk(cfgroot.toPath()).iterator();
				if(iterator.hasNext()){
					log.info("Loading overrides");
					iterateFiles(root, iterator, ctx, path, gson, names, parser);
				}
			} catch(IOException e){
				Config.error("Couldn't load json files", e);
			}
		});
		return;
	}
	public static void getResourcePath(String root, Consumer<Path> c){
		FileSystem filesystem = null;
		root = "/assets/tomsmodcore/" + root;
		try{
			URL url = TomsModUtils.class.getResource("/assets/tomsmod");

			if (url != null)
			{
				URI uri = url.toURI();
				Path path;

				if ("file".equals(uri.getScheme()))
				{
					path = Paths.get(TomsModUtils.class.getResource(root).toURI());
				}
				else
				{
					if (!"jar".equals(uri.getScheme()))
					{
						Config.logErr("Unsupported scheme " + uri + " trying to list all recipes");
						return;
					}

					filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
					path = filesystem.getPath(root);
				}
				c.accept(path);
				return;
			}
			Config.logErr("Couldn't find .mcassetsroot");
		} catch (IOException | URISyntaxException urisyntaxexception) {
			Config.error("Couldn't get file path", urisyntaxexception);
			return;
		} finally {
			IOUtils.closeQuietly(filesystem);
		}
	}
	public static void iterateFiles(String root, Iterator<Path> iterator, JsonContext ctx, Path path, Gson gson, List<String> names, BiConsumer<JsonObject, String> parser){
		while (iterator.hasNext())
		{
			Path path1 = iterator.next();

			if ("json".equals(FilenameUtils.getExtension(path1.toString())))
			{
				Path path2 = path.relativize(path1);
				String s = FilenameUtils.removeExtension(path2.toString()).replaceAll("\\\\", "/");
				if(!names.contains(s)){
					ResourceLocation resourcelocation = new ResourceLocation(s);
					BufferedReader bufferedreader = null;
					names.add(s);

					try
					{
						try
						{
							bufferedreader = Files.newBufferedReader(checkCfg(root, path1));
							parser.accept(JsonUtils.fromJson(gson, bufferedreader, JsonObject.class), s);
						}
						catch (JsonParseException | IllegalStateException jsonparseexception)
						{
							Config.error("Parsing error loading json " + resourcelocation, jsonparseexception);
						}
						catch (IOException ioexception)
						{
							Config.error("Couldn't read json " + resourcelocation + " from " + path1, ioexception);
						}
					}
					finally
					{
						IOUtils.closeQuietly(bufferedreader);
					}
				}
			}
		}
	}
	public static File checkCfg(String root, File path1) {
		String name = path1.getName();
		if(!root.isEmpty())name = root + "/" + name;
		File f = new File(CoreInit.configFolder, name);
		return f.exists() ? f : path1;
	}
	public static Path checkCfg(String root, Path path) {
		String name = path.toString().replace("\\", "/");
		name = name.substring(name.lastIndexOf("/")+1);
		if(!root.isEmpty())name = root + "/" + name;
		File f = new File(CoreInit.configFolder, name);
		return f.exists() ? f.toPath() : path;
	}
	public static void parseJson(String root, String path, Gson gson, Consumer<JsonObject> parser){
		getResourcePath(root + "/" + path + ".json", p -> {
			try {
				Storage<Boolean> success = new Storage<>(false);
				loadJson(root + "/" + path, getConfig(root, p), gson, m -> {
					if(m.get("dummy") != null)loadJson(root + "/" + path, p, gson, parser, false);
					else parser.accept(m);
					success.accept(true);
				}, true);
				if(!success.get()){
					Map<String, Object> map = new HashMap<>();
					map.put("dummy", true);
					map.put("_comment", "This is a placeholder file. You can find the original in the mod jar at assets/tomsmodcore/" + path + ".json");
					File out = new File(CoreInit.configFolder, root + "/" + path + ".json");
					out.getParentFile().mkdirs();
					PrintWriter w = null;
					try {
						w = new PrintWriter(out);
						gson.toJson(map, w);
					} catch (JsonIOException | FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						IOUtils.closeQuietly(w);
					}
					loadJson(root + "/" + path, p, gson, parser, false);
				}
			} catch(IllegalStateException | JsonParseException e){
				Config.error("Error parsing json", e);
			}
		});
	}
	public static Path getConfig(String root, Path path) {
		String name = path.toString().replace("\\", "/");
		name = name.substring(name.lastIndexOf("/")+1);
		if(!root.isEmpty())name = root + "/" + name;
		File f = new File(CoreInit.configFolder, name);
		return f.toPath();
	}

	public static void loadJson(String path, Path p, Gson gson, Consumer<JsonObject> parser, boolean cfg){
		BufferedReader bufferedreader = null;
		try
		{
			try
			{
				if(cfg){
					File f = p.toFile();
					if(f.isDirectory()){
						Config.logErr("Path " + FilenameUtils.removeExtension(path) + " is a directory");
					}else if(f.exists()){
						bufferedreader = Files.newBufferedReader(p);
						parser.accept(JsonUtils.fromJson(gson, bufferedreader, JsonObject.class));
					}
				}else{
					bufferedreader = Files.newBufferedReader(p);
					parser.accept(JsonUtils.fromJson(gson, bufferedreader, JsonObject.class));
				}
			}
			catch (JsonParseException | IllegalStateException | ClassCastException jsonparseexception)
			{
				Config.error("Parsing error loading json " + FilenameUtils.removeExtension(path), jsonparseexception);
			}
			catch (IOException ioexception)
			{
				Config.error("Couldn't read json " + FilenameUtils.removeExtension(path) + " from " + p, ioexception);
			}
		}
		finally
		{
			IOUtils.closeQuietly(bufferedreader);
		}
	}
	public static void breakBlock(World world, BlockPos pos){
		IBlockState state = world.getBlockState(pos);
		NonNullList<ItemStack> drops = NonNullList.create();
		state.getBlock().getDrops(drops, world, pos, state, 0);
		drops.forEach(d -> Block.spawnAsEntity(world, pos, d));
		world.setBlockToAir(pos);
	}
}
