package com.tom.apis;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

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
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import com.mojang.authlib.GameProfile;

import com.tom.api.gui.GuiNumberValueBox;
import com.tom.api.multipart.IModule;
import com.tom.client.EventHandlerClient;
import com.tom.config.Config;
import com.tom.lib.Configs;
import com.tom.network.NetworkHandler;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.OcclusionHelper;
import mcmultipart.multipart.PartSlot;

public final class TomsModUtils {
	/*private static final Map<EnumFacing, Matrix4f> rotationMap;
	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;
	private static final int SHADE = 3;*/
	private static final int DIVISION_BASE = 1000;
	private static final char[] ENCODED_POSTFIXES = "KMGTPE".toCharArray();
	private static MinecraftServer server;
	private static Format format;
	private static GameProfile profile;
	private static WeakReference<FakePlayer> TOMSMOD_PLAYER = null;
	private static InventoryCrafting craftingInv = new InventoryCrafting(new ContainerTomsMod(){
		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return true;
		}
		@Override
		public void onCraftMatrixChanged(IInventory inventoryIn) {};
	}, 3, 3);
	public static void constructFakePlayer(){
		log.info("Tom's Mod Fake Player: " + Configs.fakePlayerName + ", UUID: " + Config.tomsmodFakePlayerUUID.toString());
		profile = new GameProfile(Config.tomsmodFakePlayerUUID, Configs.fakePlayerName);
	}
	static {
		/*ImmutableMap.Builder<EnumFacing, Matrix4f> builder = ImmutableMap.builder();
		for (EnumFacing face : EnumFacing.values()) {
			Matrix4f mat = new Matrix4f();
			mat.setIdentity();

			if (face == EnumFacing.WEST) {
				builder.put(face, mat);
				continue;
			}
			mat.setTranslation(new Vector3f(0.5f, 0.5f, 0.5f));
			Matrix4f m2 = new Matrix4f();
			m2.setIdentity();

			if (face.getAxis() == Axis.Y) {
				AxisAngle4f axisAngle = new AxisAngle4f(0, 0, 1, (float) Math.PI * 0.5f * -face.getFrontOffsetY());
				m2.setRotation(axisAngle);
				mat.mul(m2);

				m2.setIdentity();
				m2.setRotation(new AxisAngle4f(1, 0, 0, (float) Math.PI * (1 + face.getFrontOffsetY() * 0.5f)));
				mat.mul(m2);
			} else {
				int ang;
				if (face == EnumFacing.EAST) ang = 2;
				else if (face == EnumFacing.NORTH) ang = 3;
				else ang = 1;
				AxisAngle4f axisAngle = new AxisAngle4f(0, 1, 0, (float) Math.PI * 0.5f * ang);
				m2.setRotation(axisAngle);
				mat.mul(m2);
			}

			m2.setIdentity();
			m2.setTranslation(new Vector3f(-0.5f, -0.5f, -0.5f));
			mat.mul(m2);
			builder.put(face, mat);
		}
		rotationMap = builder.build();*/
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat(".#;0.#");
		format.setDecimalFormatSymbols(symbols);
		format.setRoundingMode(RoundingMode.DOWN);
		TomsModUtils.format = format;
	}
	@SideOnly(Side.CLIENT)
	private static final int DELETION_ID = 2525277;
	@SideOnly(Side.CLIENT)
	private static int lastAdded;

	public static Logger log = LogManager.getLogger("TomsModUtils");

	/*public static long getLong(String string, boolean negative){
		long current = 0;
		for(int i=0;i<string.length();i++){
			int iU = i + 1;
			int pos = 10 ^ i;
			int num = 0;
			String currentString = string.substring(iU,iU);
			String cS = currentString;
			num = cS == "0" ? 0 : (cS == "1" ? 1 : (cS == "2" ? 2 : (cS == "3" ? 3 : (cS == "4" ? 4 : (cS == "5" ? 5 : (cS == "6" ? 6 : (cS == "7" ? 7 : (cS == "8" ? 8 : (cS == "9" ? 9 : 0)))))))));
			current = current + (pos * num);
		}
		return negative ? -current : current;
	}
	public static long getLong(String string){
		return getLong(string, false);
	}*/
	public static int invertInt(int num,int max){
		return num < max ? max - num : 0;
	}

	/*public static long getLongBoolean(boolean[] table){
		long current = 0;
		for(int i = 0;i<16;i++){
			int iU = invertInt(i,16);
			int pos = 10 ^ iU;
			int num = table[iU] ? 1 : 0;
			current = current + (pos * num);
		}
		return current + 10 ^ 16;
	}*/
	//Table {{{{<x,y,z>},{<color>}}, {{<screenData>}} ,{{<x,y,z,color>}, {<screenData>}}, {{<x,y,z,color>}, {<screenData>}}}
	//return {<(int) boolean>, <slot>}
	public static int[] find(int[][][][] table, int x, int y, int z, int color){
		int[] returnData = {0,0};
		for(int i = 0;i<table.length;i++){
			//current {{<x,y,z>},{<color>}}
			int[][] current = table[i][0];
			int[] coords = current[0];
			int xC = coords[0];
			int yC = coords[1];
			int zC = coords[2];
			int colorC = current[1][0];
			if(xC == x && yC == y && zC == z && colorC == color){
				returnData[0] = 1;
				returnData[1] = i;
				break;
			}
		}
		return returnData;
	}

	public static int[] toInt(boolean[] table){
		int[] current = {};
		for(int i=0;i<table.length;i++) current[i] = table[i] ? 1 : 0;
		return current;
	}

	/*public static int[][] compressInt(int[][] table){
		int[][] ret = {};
		for(int i = 0;i<table.length;i++){
			int[] current = table[i];
			for(int k = 0;k<2;k++){
				String current2 = "1";
				for(int j = 0;j<8;j++){
					current2 = current2 + (current[doubleLoop(j,k,8)] == 1 ? "1" : "0");
				}
				ret[i][k] = Integer.getInteger(current2);
			}
		}
		return ret;
	}

	public static int doubleLoop(int j, int k, int mid){
		return j == 0  && k != 0 ? mid : j * (k + 1);
	}

	public static int[][] decompressInt(int[][] table){
		int[][] ret = {};
		for(int i = 0;i<table.length;i++){
			int[] current = table[i];
			int loop;
			//1<numbers>
			String[] cString = {Integer.toString(current[0]), Integer.toString(current[1])};
			cString[2] = cString[0].substring(1);
			cString[3] = cString[0].substring(1);
			//<numbers>
			int[] current2 = {};
			for(loop = 0;loop<2;loop++)
				for (int j = 0;j<8;j++){
					current2[doubleLoop(j, loop, 8)] = cString[loop + 2].substring(j,j) == "1" ? 1 : 0;
				}
			ret[i] = current2;
		}
		return ret;
	}*/

	/*public static boolean[][] getBoolean(int[][] table){
		boolean[][] ret = {};
		for(int i=0;i<table.length;i++){
			boolean[] current = {};
			for(int j = 0;j<table[i].length;j++) current[j] = table[i][j] == 1;
			ret[i] = current;
		}
		return ret;
	}*/

	public static Object[][][] fillObject(int amount){
		Object[][][] ret = new Object[amount][][];
		Object[][] empty = new Object[][]{{new Object()},new Object[65536]};
		for(int i = 0;i<amount;i++){
			ret[i] = empty;
		}
		return ret;
	}

	public static Object[][] fillObjectSimple(int amount){
		Object[][] ret = new Object[amount][2];
		/*Object[] empty = new Object[2];
		for(int i = 0;i<amount;i++){
			ret[i] = empty;
		}*/
		return ret;
	}
	public static int[] getCoordTable(int x, int y, int z){
		return new int[]{x,y,z};
	}
	public static int[] getCoordTable(BlockPos pos){
		return getCoordTable(pos.getX(),pos.getY(),pos.getZ());
	}
	public static int[] getRelativeCoordTable(int[] current, int x, int y, int z, int d){
		int[] ret = {};
		int xCoord = current[0], yCoord = current[1], zCoord = current[2];
		if(d == 0){
			ret = TomsModUtils.getCoordTable(xCoord + x, yCoord + y, zCoord + z);
		}else if(d == 1){
			ret = TomsModUtils.getCoordTable(xCoord + (-x), yCoord + y, zCoord + z);
		}else if(d == 2){
			ret = TomsModUtils.getCoordTable(xCoord + z, yCoord + y, zCoord + x);
		}else if(d == 3){
			ret = TomsModUtils.getCoordTable(xCoord + z, yCoord + y, zCoord + (-x));
		}else{
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord);
		}
		return ret;
	}
	public static int[] energyCalculator(int maxEnergy,int energy,int maxEnergyInput,int maxReceive, boolean canConnectEnergy){
		int energyReceive = 0;
		if(maxEnergy > energy && canConnectEnergy){
			int canReceive = (energy + maxEnergyInput) < maxEnergy ? maxEnergyInput : maxEnergy - energy;
			energyReceive = maxReceive >= canReceive ? canReceive : maxReceive;
		}
		energy = energy + energyReceive;
		return new int[]{energyReceive, energy, energyReceive};
	}
	public static int[] energyCalculator(int maxEnergy,int energy,int maxEnergyInput,int maxReceive){
		return energyCalculator(maxEnergy, energy, maxReceive, maxEnergyInput, true);
	}
	public static int[] energyCalculator(int energy, int maxReceive){
		return energyCalculator(120000, energy, 1000, maxReceive);
	}
	public static boolean isUseable(int xCoord, int yCoord, int zCoord, EntityPlayer player, World worldObj, TileEntity thisT){
		return isUseable(new BlockPos(xCoord, yCoord, zCoord), player, worldObj, thisT);
	}
	public static boolean isUseable(BlockPos pos, EntityPlayer player, World worldObj, TileEntity thisT){
		return worldObj.getTileEntity(pos) != thisT ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}
	/*public static int find(String[] table, String string){
		int ret = -1;
		for(int i = 0;i<table.length;i++){
			if(table[i] == string){
				ret = i;
				break;
			}
		}
		return ret;
	}
	public static boolean findString(String[] table, String string){
		boolean ret = false;
		for(int i = 0;i<table.length;i++){
			if(table[i] == string){
				ret = true;
				break;
			}
		}
		return ret;
	}
	public static boolean findString(List<String> table, String string){
		boolean ret = false;
		for(int i = 0;i<table.size();i++){
			if(table.get(i) == string){
				ret = true;
				break;
			}
		}
		return ret;
	}
	public static int find(List<String> table, String string){
		int ret = -1;
		for(int i = 0;i<table.size();i++){
			if(table.get(i) == string){
				ret = i;
				break;
			}
		}
		return ret;
	}*/
	/*public static double getNum(int num, int max, int max2){
		double max3 = max / max2;
		return num / max3;
	}*/
	public static EnumFacing getDirectionFacing(EntityLivingBase entity, boolean includeUpAndDown){
		double yaw = entity.rotationYaw;
		while(yaw < 0)
			yaw += 360;
		yaw = yaw % 360;
		if(includeUpAndDown) {
			if(entity.rotationPitch > 45) return EnumFacing.DOWN;
			else if(entity.rotationPitch < -45) return EnumFacing.UP;
		}
		if(yaw < 45) return EnumFacing.SOUTH;
		else if(yaw < 135) return EnumFacing.WEST;
		else if(yaw < 225) return EnumFacing.NORTH;
		else if(yaw < 315) return EnumFacing.EAST;

		else return EnumFacing.SOUTH;
	}
	public static int getIntFromEnumFacing(EnumFacing d){
		if(d == EnumFacing.UP){
			return 1;
		}else if (d == EnumFacing.DOWN)return 0;
		else if(d == EnumFacing.NORTH)return 1;
		else if(d == EnumFacing.SOUTH)return 2;
		else if(d == EnumFacing.EAST)return 3;
		else if(d == EnumFacing.WEST)return 4;
		else return 0;
	}
	/*public static int[] energyCalculatorExtract(int maxEnergy,int energy,int maxEnergyOutput,int maxExtract, boolean canConnectEnergy){
		int energyExtract = 0;
		if(energy > 0 && canConnectEnergy){
			int extract = maxExtract >= maxEnergyOutput ? maxEnergyOutput : maxExtract;
			energyExtract = extract <= energy ? extract : energy;
			energy = energy - extract;
		}
		return new int[]{energyExtract, energy, energyExtract};
	}
	public static int[] energyCalculatorExtract(int maxEnergy,int energy,int maxEnergyOutput,int maxExtract){
		return energyCalculatorExtract(maxEnergy, energy, maxExtract, maxEnergyOutput, true);
	}
	public static int[] energyCalculatorExtract(int energy, int maxExtract){
		return energyCalculatorExtract(120000, energy, 1000, maxExtract);
	}*/
	public static int[] getCoordTableUD(int[] current, int d){
		int[] ret = {};
		int xCoord = current[0], yCoord = current[1], zCoord = current[2];
		//System.out.println(yCoord);
		if(d == 0){
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord + 1);
		}else if(d == 1){
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord - 1);
		}else if(d == 2){
			ret = TomsModUtils.getCoordTable(xCoord + 1, yCoord, zCoord);
		}else if(d == 3){
			ret = TomsModUtils.getCoordTable(xCoord - 1, yCoord, zCoord);
		}else if(d == 4){
			ret = TomsModUtils.getCoordTable(xCoord, yCoord + 1, zCoord);
		}else if(d == 5){
			ret = TomsModUtils.getCoordTable(xCoord, yCoord - 1, zCoord);
		}else{
			ret = TomsModUtils.getCoordTable(xCoord, yCoord, zCoord);
		}
		return ret;
	}
	public static EnumFacing getFD(int d){
		if(d == 0){
			return EnumFacing.NORTH;
			//ret = TomsMathHelper.getCoordTable(xCoord + 1, yCoord, zCoord);
		}else if(d == 1){
			return EnumFacing.SOUTH;
			//ret = TomsMathHelper.getCoordTable(xCoord - 1, yCoord, zCoord);
		}else if(d == 2){
			return EnumFacing.WEST;
			//ret = TomsMathHelper.getCoordTable(xCoord, yCoord, zCoord + 1);
		}else if(d == 3){
			return EnumFacing.EAST;
			//ret = TomsMathHelper.getCoordTable(xCoord, yCoord, zCoord - 1);
		}else if(d == 4){
			return EnumFacing.UP;
			//ret = TomsMathHelper.getCoordTable(xCoord, yCoord + 1, zCoord);
		}else if(d == 5){
			return EnumFacing.DOWN;
			//ret = TomsMathHelper.getCoordTable(xCoord, yCoord - 1, zCoord);
		}else{
			return null;
			//ret = TomsMathHelper.getCoordTable(xCoord, yCoord, zCoord);
		}
		//return ret;
	}
	/*public static Object[][][] fillObject2(int amount){
		Object[][][] ret = new Object[amount][][];
		Object[][] empty = new Object[][]{new Object[2],new Object[65536]};
		for(int i = 0;i<amount;i++){
			ret[i] = empty;
		}
		return ret;
	}*/
	@SideOnly(Side.CLIENT)
	public static double rotateMatrixByMetadata(int metadata){
		if(metadata == 0) metadata = 1;
		else if(metadata == 1) metadata = 0;
		EnumFacing facing = EnumFacing.VALUES[metadata];
		//System.out.println(facing);
		double metaRotation;
		switch(facing){
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
			//GL11.glTranslatef(1F, 2F + 5/32F, 0.5F);
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
	/*public static float[] getMatrixByMetadata(int metadata){
		EnumFacing facing = EnumFacing.VALUES[metadata];
        //System.out.println(facing);
		float[] ret;
        switch(facing){
            case UP:
            	ret = new float[]{0, 0.991F + 16/32F, -(0.1F + 1/16F)};
                break;
            case DOWN:
            	ret = new float[]{0, 1.509F, 1F + 5/32F};
                break;
            case NORTH:
            	ret = new float[]{0, 4F + 10/32F, 0.999F};
                break;
            case EAST:
            	ret = new float[]{0.5F, 2F + 5/32F, 1F};
                break;
            case SOUTH:
            	ret = new float[]{0, 2F + 5/32F, 0.491F};
                break;
            default://west
            	ret = new float[]{0.5099F, 2F + 5/32F, 0};
                break;
        }
        return ret;
	}*/
	public static TileEntity getTileEntity(World world, int[] coords){
		return world.getTileEntity(new BlockPos(coords[0], coords[1], coords[2]));
	}
	public static int[][] separateIntArray(int[][] in, int index1, int index2, int size1, int size2){
		int[][] ret = new int[size1][size2];
		try{
			int indexStart1 = index1 * size1;
			int indexStart2 = index2 * size2;
			int indexEnd1 = ((index1 + 1) * size1);
			int indexEnd2 = ((index2 + 1) * size2);
			int i2 = 0;
			//System.out.println("i1:"+indexStart1+":"+indexEnd1+" i2:"+indexStart2+":"+indexEnd2);
			for(int x = indexStart1;x < indexEnd1;x++){
				int i1 = 0;
				for(int y = indexStart2;y < indexEnd2;y++){
					//if(i1 == 16 || i2 == 16){System.out.println("i:"+i1+":"+i2); break;}
					ret[i2][i1] = in[x][y];
					i1++;
				}
				i2++;
			}
		}catch(Exception e){log.error("ERROR: Calculation error");/*e.printStackTrace();*/}
		return ret;
	}
	public static int getBurnTime(ItemStack is){
		return TileEntityFurnace.getItemBurnTime(is);
	}
	public static void setBlockState(World worldIn, BlockPos pos, IBlockState state, int flags){
		TileEntity tileentity = worldIn.getTileEntity(pos);
		//System.out.println(state);
		worldIn.setBlockState(pos, state, flags);
		if (tileentity != null)
		{
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
	}
	public static void setBlockState(World worldIn, BlockPos pos, IBlockState state){
		setBlockState(worldIn, pos, state, 2);
	}
	public static void setBlockState(World worldIn, int x, int y, int z, IBlockState state){
		setBlockState(worldIn, getBlockPos(x,y,z), state);
	}
	public static void setBlockState(World worldIn, int x, int y, int z, IBlockState state, int flags){
		setBlockState(worldIn, getBlockPos(x,y,z), state, flags);
	}
	public static BlockPos getBlockPos(int x, int y, int z){
		return new BlockPos(x,y,z);
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
	private static void sendNoSpamMessages(ITextComponent[] messages)
	{
		GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
		for (int i = DELETION_ID + messages.length - 1; i <= lastAdded; i++)
		{
			chat.deleteChatLine(i);
		}
		for (int i = 0; i < messages.length; i++)
		{
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
	 *        The player to send the chat message to
	 * @param lines
	 *        The chat lines to send.
	 */
	public static void sendNoSpam(EntityPlayer player, ITextComponent... lines)
	{
		if (lines.length > 0)
			NetworkHandler.sendTo(new PacketNoSpamChat(lines), (EntityPlayerMP) player);
	}

	/**
	 * @author tterrag1098
	 *
	 *         Ripped from EnderCore (and slightly altered)
	 */
	public static class PacketNoSpamChat implements IMessage
	{

		private ITextComponent[] chatLines;

		public PacketNoSpamChat()
		{
			chatLines = new ITextComponent[0];
		}

		private PacketNoSpamChat(ITextComponent... lines)
		{
			// this is guaranteed to be >1 length by accessing methods
			this.chatLines = lines;
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(chatLines.length);
			for (ITextComponent c : chatLines)
			{
				ByteBufUtils.writeUTF8String(buf, ITextComponent.Serializer.componentToJson(c));
			}
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			chatLines = new ITextComponent[buf.readInt()];
			for (int i = 0; i < chatLines.length; i++)
			{
				chatLines[i] = ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(buf));
			}
		}

		public static class Handler implements IMessageHandler<PacketNoSpamChat, IMessage>
		{

			@Override
			public IMessage onMessage(final PacketNoSpamChat message, MessageContext ctx)
			{
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
	public static ITextComponent getChatMessageFromString(String in, Object... args){
		return new TextComponentTranslation(in, args);

	}
	public static boolean isEqual(BlockPos pos1, BlockPos pos2){
		return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ();
	}
	public static RayTraceResult rayTrace(World world, Vec3d pos1, Vec3d pos2){
		return world.rayTraceBlocks(pos1, pos2, true);
	}
	public static void writeBlockPosToPacket(ByteBuf buf, BlockPos pos){
		boolean hasPos = pos != null;
		buf.writeBoolean(hasPos);
		if (hasPos) {
			buf.writeInt(pos.getX());
			buf.writeInt(pos.getY());
			buf.writeInt(pos.getZ());
		}
	}
	public static BlockPos readBlockPosFromPacket(ByteBuf buf){
		if(buf.readBoolean()){
			return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		}
		return null;
	}
	public static ITextComponent getYesNoMessage(boolean value){
		return new TextComponentTranslation("tomsMod.chat."+(value ? "yes" : "no")).setStyle(new Style().setColor(value ? TextFormatting.GREEN : TextFormatting.RED));
	}
	public static void sendNoSpamTranslate(EntityPlayer player, String key, Object... args){
		if(!player.worldObj.isRemote)sendNoSpam(player, new TextComponentTranslation(key, args));
	}
	public static void sendNoSpamTranslate(EntityPlayer player, Style style, String key, Object... args){
		if(!player.worldObj.isRemote)sendNoSpam(player, new TextComponentTranslation(key, args).setStyle(style));
	}
	public static <T extends Comparable<T>> void setBlockStateWithCondition(World worldIn, BlockPos pos, IBlockState state, IProperty<T> p, T valueE){
		try{
			if(state.getValue(p) != valueE) setBlockState(worldIn, pos, state.withProperty(p, valueE), 2);
		}catch(Exception e){
			log.catching(e);
		}
	}
	public static TileEntity getTileEntity(World worldIn, BlockPos pos, int dim){
		if(worldIn.isRemote){
			log.error("world is remote");
			return null;
		}
		World world = worldIn;
		if(world.provider.getDimension() != dim){
			if(server == null){
				log.error("MinecraftServer.getServer() == null");
				FMLLog.bigWarning("MinecraftServer.getServer() == null");
				return null;
			}
			world = server.worldServerForDimension(dim);
		}
		if(world == null){
			log.error("world == null");
			return null;
		}
		return world.getTileEntity(pos);
	}
	public static IBlockState getBlockState(World worldIn, BlockPos pos, int dim){
		if(worldIn.isRemote){
			log.error("world is remote");
			return null;
		}
		World world = worldIn;
		if(world.provider.getDimension() != dim){
			if(server == null){
				log.error("MinecraftServer.getServer() == null");
				FMLLog.bigWarning("MinecraftServer.getServer() == null");
				return null;
			}
			world = server.worldServerForDimension(dim);
		}
		if(world == null){
			log.error("world == null");
			return null;
		}
		return world.getBlockState(pos);
	}
	public static World getWorld(int dim){
		if(server == null){
			log.error("MinecraftServer.getServer() == null");
			FMLLog.bigWarning("MinecraftServer.getServer() == null");
			return null;
		}
		return server.worldServerForDimension(dim);
	}
	public static <T extends Comparable<T>> void setBlockStateWithCondition(World worldIn, BlockPos pos, IProperty<T> p, T valueE){
		setBlockStateWithCondition(worldIn, pos, worldIn.getBlockState(pos), p, valueE);
	}
	/*public static List<ItemStack> getItemStackList(ItemStack... stacks){
    	List<ItemStack> list = new ArrayList<ItemStack>();
    	for(ItemStack is : stacks){
    		list.add(is);
    	}
    	return list;
    }*/
	public static boolean or(boolean... bs){
		boolean r = false;
		for(boolean b : bs){
			r = b || r;
		}
		return r;
	}
	public static boolean and(boolean... bs){
		boolean r = true;
		for(boolean b : bs){
			r = b && r;
		}
		return r;
	}
	public static IBlockState getBlockStateFromMeta(int meta, PropertyInteger propState, PropertyDirection propDir, IBlockState defState){
		EnumFacing facing = EnumFacing.NORTH;
		int state = 0;
		switch(meta){
		case 0: break;
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
		return defState.withProperty(propState, state).withProperty(propDir, facing);
	}

	public static int getMetaFromState(EnumFacing facing, int state){
		if (facing.getAxis() == EnumFacing.Axis.Y)
		{
			facing = EnumFacing.NORTH;
		}
		switch(facing){
		case EAST:
			switch(state){
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
			switch(state){
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
			switch(state){
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
			switch(state){
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
			switch(state){
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
	public static File getSavedFile(){
		File file = null;
		if(server != null){
			if(server.isSinglePlayer()){
				String s1 = server.getFile("saves").getAbsolutePath() + File.separator + server.getFolderName() + File.separator + "tm";
				file = new File(s1);
			}else{
				String f = server.getFile("a").getAbsolutePath();
				String s1 = f.substring(0, f.length()-1) + File.separator + server.getFolderName() + File.separator + "tm";
				file = new File(s1);
			}
		}
		return file;
	}
	public static <A,B extends A> A[] toArray(B... in){
		return in;
	}
	public static IModule getModule(World world, BlockPos blockPos, EnumFacing pos){
		if(pos == null)return null;
		IMultipartContainer container = MultipartHelper.getPartContainer(world, blockPos);
		if (container == null) {
			return null;
		}

		ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(pos));
		if(part instanceof IModule){
			return (IModule) part;
		}
		return null;
	}
	public static void writeBlockPosToNBT(NBTTagCompound tag, BlockPos pos){
		if(pos != null){
			tag.setInteger("posX", pos.getX());
			tag.setInteger("posY", pos.getY());
			tag.setInteger("posZ", pos.getZ());
		}else{
			tag.setBoolean("null", true);
		}
	}
	public static BlockPos readBlockPosFromNBT(NBTTagCompound tag){
		if(tag.hasKey("null")) return null;
		return new BlockPos(tag.getInteger("posX"),tag.getInteger("posY"),tag.getInteger("posZ"));
	}
	/*public static ItemStack[] mergeItemStacks(ItemStack stack, ItemStack mergeTo, ItemStack matchTo, boolean checkMeta, boolean checkNBT, boolean checkMod, int maxA){
    	if(stack == null && matchTo == null)return new ItemStack[]{stack,mergeTo};
    	if(stack != null && matchTo != null){
    		if(mergeTo == null){
    			if(stack.stackSize <= maxA){
    				if(areItemStacksEqual(stack, matchTo, checkMeta, checkNBT, checkMod))
    					return new ItemStack[]{};

    			}else{

    			}
    		}else{
    			if(stack.stackSize+mergeTo.stackSize <= maxA){

    			}else{
    				int size = maxA - mergeTo.stackSize;
    				if(size > 0){

    				}
    			}
    		}
    	}
		return new ItemStack[]{stack,mergeTo};
	}*/
	public static boolean areItemStacksEqual(ItemStack stack, ItemStack matchTo, boolean checkMeta, boolean checkNBT, boolean checkMod){
		if(stack == null && matchTo == null)return false;
		if(stack != null && matchTo != null){
			if(checkMod){
				String modname = stack.getItem().delegate.name().getResourceDomain();
				return modname != null && modname.equals(matchTo.getItem().delegate.name().getResourceDomain());
			}else{
				if(stack.getItem() == matchTo.getItem()){
					boolean equals = true;
					if(checkMeta){
						equals = equals && (stack.getItemDamage() == matchTo.getItemDamage() || stack.getMetadata() == OreDictionary.WILDCARD_VALUE || matchTo.getMetadata() == OreDictionary.WILDCARD_VALUE);
					}
					if(checkNBT){
						equals = equals && ItemStack.areItemStackTagsEqual(stack, matchTo);
					}
					return equals;
				}
			}
		}
		return false;
	}
	/*@SideOnly(Side.CLIENT)
	public static List<BakedQuad> bakeBlockModelToShape(ItemStack blockStack, IModel bakeTo, EnumFacing face){
		if(blockStack != null && blockStack.getItem() instanceof ItemBlock){
			return bakeBlockModelToShape((ItemBlock) blockStack.getItem(), blockStack.getMetadata(), bakeTo, face);
		}
		return bakeBlockModelToShape(Blocks.stone.getDefaultState(), bakeTo, face);
	}
	@SideOnly(Side.CLIENT)
	public static List<BakedQuad> bakeBlockModelToShape(ItemBlock block, int meta, IModel bakeTo, EnumFacing face){
		IBlockState state;
		try{
			state = block.getBlock().getStateFromMeta(meta);
		}catch(Exception e){
			state = block.block.getDefaultState();
		}
		return bakeBlockModelToShape(state, bakeTo, face);
	}
	@SideOnly(Side.CLIENT)
	public static List<BakedQuad> bakeBlockModelToShape(IBlockState block, IModel bakeTo, EnumFacing face){
		List<BakedQuad> quads = Lists.newArrayList();
		final TextureAtlasSprite sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(block);
		if (bakeTo != null) {
			IFlexibleBakedModel baked = bakeTo.bake(ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, new Function<ResourceLocation, TextureAtlasSprite>() {
				@Override
				public TextureAtlasSprite apply(ResourceLocation input) {
					return sprite;
				}
			});
			if(face == null){
				face = EnumFacing.NORTH;
				//log.warn("face == null");
			}
			Matrix4f matrix = new Matrix4f(rotationMap.get(face));
			for (BakedQuad quad : baked.getGeneralQuads()) {
				quad = transform(quad, matrix);
				quad = replaceShade(quad, 0xFFFFFFFF);
				quad = applyDiffuse(quad);
				quads.add(quad);
			}
		}
		return quads;
	}
	@SideOnly(Side.CLIENT)
	public static BakedQuad transform(BakedQuad quad, Matrix4f matrix4f) {
		int[] data = quad.getVertexData();
		data = Arrays.copyOf(data, data.length);
		boolean colour = quad instanceof IColoredBakedQuad;
		int step = data.length / 4;
		for (int i = 0; i < 4; i++) {
			Point3f vec = new Point3f();
			vec.x = Float.intBitsToFloat(data[i * step + X]);
			vec.y = Float.intBitsToFloat(data[i * step + Y]);
			vec.z = Float.intBitsToFloat(data[i * step + Z]);

			matrix4f.transform(vec);

			data[i * step + X] = Float.floatToRawIntBits(vec.x);
			data[i * step + Y] = Float.floatToRawIntBits(vec.y);
			data[i * step + Z] = Float.floatToRawIntBits(vec.z);
		}
		return colour ? new ColoredBakedQuad(data, quad.getTintIndex(), quad.getFace()) : new BakedQuad(data, quad.getTintIndex(), quad.getFace());
	}
	@SideOnly(Side.CLIENT)
	public static BakedQuad replaceShade(BakedQuad quad, int shade) {
		int[] data = quad.getVertexData();
		int step = data.length / 4;
		data = Arrays.copyOf(data, data.length);
		boolean colour = quad instanceof IColoredBakedQuad;
		for (int i = 0; i < 4; i++) {
			data[i * step + SHADE] = shade;
		}
		return colour ? new ColoredBakedQuad(data, quad.getTintIndex(), quad.getFace()) : new BakedQuad(data, quad.getTintIndex(), quad.getFace());
	}
	@SideOnly(Side.CLIENT)
	public static BakedQuad applyDiffuse(BakedQuad quad) {
		Vector3f normal = normal(quad);
		float diffuse = diffuseLight(normal);
		int diffuseI = (int) (diffuse * 0xFF);
		int shade = 0xFF000000 + diffuseI * 0x010101;
		return replaceShade(quad, shade);
	}
	@SideOnly(Side.CLIENT)
	public static Vector3f normal(BakedQuad quad) {
		int[] data = quad.getVertexData();
		int step = data.length / 4;
		data = Arrays.copyOf(data, data.length);
		Point3f[] positions = new Point3f[3];
		for (int i = 0; i < 3; i++) {
			Point3f vec = new Point3f();
			vec.x = Float.intBitsToFloat(data[i * step + X]);
			vec.y = Float.intBitsToFloat(data[i * step + Y]);
			vec.z = Float.intBitsToFloat(data[i * step + Z]);
			positions[i] = vec;
		}

		Vector3f a = new Vector3f(positions[1]);
		a.sub(positions[0]);

		Vector3f b = new Vector3f(positions[2]);
		b.sub(positions[0]);

		Vector3f c = new Vector3f();
		c.cross(a, b);
		return c;
	}
	@SideOnly(Side.CLIENT)
	public static float diffuseLight(Vector3f normal) {
		return diffuseLight(normal.x, normal.y, normal.z);
	}
	@SideOnly(Side.CLIENT)
	public static float diffuseLight(float x, float y, float z) {
		boolean up = y >= 0;

		float xx = x * x;
		float yy = y * y;
		float zz = z * z;

		float t = xx + yy + zz;
		float light = (xx * 0.6f + zz * 0.8f) / t;

		float yyt = yy / t;
		if (!up) yyt *= 0.5;
		light += yyt;

		return light;
	}*/
	@SuppressWarnings("deprecation")
	public static IBlockState getBlockStateFrom(ItemBlock block, int meta){
		IBlockState state;
		try{
			state = block.getBlock().getStateFromMeta(meta);
		}catch(Exception e){
			state = block.block.getDefaultState();
		}
		return state;
	}
	public static IBlockState getBlockStateFrom(ItemStack blockStack){
		if(blockStack != null && blockStack.getItem() instanceof ItemBlock){
			return getBlockStateFrom((ItemBlock) blockStack.getItem(), blockStack.getMetadata());
		}
		return Blocks.STONE.getDefaultState();
	}
	@SideOnly(Side.CLIENT)
	public static IBakedModel getBakedModelFromBlockState(IBlockState state, IBakedModel defaultModel){
		if(state != null){
			Minecraft mc = Minecraft.getMinecraft();
			BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
			BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
			IBakedModel blockModel = blockModelShapes.getModelForState(state);
			if(blockModel != null && blockModel != blockModelShapes.getModelManager().getMissingModel()){
				return blockModel;
			}
		}
		return defaultModel;
	}
	@SideOnly(Side.CLIENT)
	public static IBakedModel getBakedModelFromItemBlock(ItemBlock blockItem, int meta, IBakedModel defaultModel){
		return getBakedModelFromBlockState(getBlockStateFrom(blockItem, meta), defaultModel);
	}
	@SideOnly(Side.CLIENT)
	public static IBakedModel getBakedModelFromItemBlockStack(ItemStack blockStack, IBakedModel defaultModel){
		return getBakedModelFromBlockState(getBlockStateFrom(blockStack), defaultModel);
	}
	public static <T extends Comparable<T>> void setBlockStateProperty(World world, BlockPos pos, IProperty<T> property, T value){
		setBlockState(world, pos, world.getBlockState(pos).withProperty(property, value));
	}
	/*public static <A,B extends A> A[] fillArray(B[] putTo, B value){
		for(int i = 0;i<putTo.length;i++)putTo[i] = value;
		return putTo;
	}*/
	public static void sendAccessDeniedMessageTo(EntityPlayer player, String tag){
		if(tag != null)sendNoSpamTranslate(player,new Style().setColor(TextFormatting.RED), "tomsMod.accessDeniedP", new TextComponentTranslation(tag));
		else sendNoSpamTranslate(player,new Style().setColor(TextFormatting.RED), "tomsMod.accessDenied");
	}
	public static void sendAccessDeniedMessageToWithTag(EntityPlayer player, String tag){
		if(tag != null)sendNoSpamTranslate(player,new Style().setColor(TextFormatting.RED), "tomsMod.accessDeniedP", new TextComponentTranslation("tomsMod.tag",new TextComponentTranslation(tag)));
		else sendNoSpamTranslate(player,new Style().setColor(TextFormatting.RED), "tomsMod.accessDenied");
	}
	@SideOnly(Side.CLIENT)
	public static class GuiTextFieldLabel extends GuiLabel{
		GuiTextField field;
		public GuiTextFieldLabel(GuiTextField field) {
			super(null,0,0,0,0,0,0);
			this.field = field;
		}
		@Override
		public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
			field.drawTextBox();
		}
	}
	@SideOnly(Side.CLIENT)
	public static List<GuiLabel> addTextFieldToLabelList(GuiTextField field, List<GuiLabel> labelList){
		if(field != null)labelList.add(new GuiTextFieldLabel(field));
		return labelList;
	}
	@SideOnly(Side.CLIENT)
	public static class GuiNumberValueBoxLabel extends GuiLabel{
		GuiNumberValueBox field;
		public GuiNumberValueBoxLabel(GuiNumberValueBox field) {
			super(null,0,0,0,0,0,0);
			this.field = field;
		}
		@Override
		public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
			field.drawText(mc.fontRendererObj, field.color);
		}
	}
	@SideOnly(Side.CLIENT)
	public static List<GuiLabel> addNumberValueBoxToLabelList(GuiNumberValueBox field, List<GuiLabel> labelList){
		if(field != null)labelList.add(new GuiNumberValueBoxLabel(field));
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
			if (model == null) model = ModelLoaderRegistry.getMissingModel();
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
			if (model == null) model = ModelLoaderRegistry.getMissingModel();
			EventHandlerClient.models.put(loc, model);
		}
		return EventHandlerClient.models.get(loc);
	}
	public static int getFirstTrue(boolean... bs){
		if(bs == null || bs.length < 1)return -1;
		for(int i = 0;i<bs.length;i++)
			if(bs[i])return i;
		return -1;
	}
	public static void breakBlockWithDrops(World world, BlockPos pos){
		/*IBlockState state = world.getBlockState(pos);
		if(state != null && state.getBlock() != null && (state.getBlock().getBlockHardness(state, world, pos) > 0)){
			state.getBlock().dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}*/
		world.destroyBlock(pos, true);
	}
	public static Iterable<BlockPos> getAllBlockPosInBounds(AxisAlignedBB bounds){
		BlockPos start = new BlockPos(bounds.minX, bounds.minY, bounds.minZ);
		BlockPos end = new BlockPos(bounds.maxX, bounds.maxY,  bounds.maxZ);
		return BlockPos.getAllInBox(start, end);
	}
	public static void sendAccessGrantedMessageTo(EntityPlayer player, String tag){
		if(tag != null)sendNoSpamTranslate(player,new Style().setColor(TextFormatting.GREEN), "tomsMod.accessGrantedP", new TextComponentTranslation(tag));
		else sendNoSpamTranslate(player,new Style().setColor(TextFormatting.GREEN), "tomsMod.accessGranted");
	}
	public static void sendAccessGrantedMessageToWithTag(EntityPlayer player, String tag){
		if(tag != null)sendNoSpamTranslate(player,new Style().setColor(TextFormatting.GREEN), "tomsMod.accessGrantedP", new TextComponentTranslation("tomsMod.tag",new TextComponentTranslation(tag)));
		else sendNoSpamTranslate(player,new Style().setColor(TextFormatting.GREEN), "tomsMod.accessGranted");
	}
	public static void sendAccessGrantedMessageToWithExtraInformation(EntityPlayer player, String tag, ITextComponent extraInfo){
		if(tag != null)sendNoSpam(player,new TextComponentTranslation("tomsMod.accessGrantedP", new TextComponentTranslation("tomsMod.tag",new TextComponentTranslation(tag))).setStyle(new Style().setColor(TextFormatting.GREEN)), extraInfo);
		else sendNoSpam(player, new TextComponentTranslation("tomsMod.accessGranted").setStyle(new Style().setColor(TextFormatting.GREEN)), extraInfo);
	}
	public static void sendAccessGrantedMessageToWithExtraInformation(EntityPlayer player, String tag, String extraInfo){
		sendAccessGrantedMessageToWithExtraInformation(player, tag, new TextComponentTranslation(extraInfo,new TextComponentTranslation("tomsMod.tag",new TextComponentTranslation(tag).setStyle(new Style().setColor(TextFormatting.GREEN))).setStyle(new Style().setColor(TextFormatting.GREEN))).setStyle(new Style().setColor(TextFormatting.GREEN)));
	}
	public static void sendNoSpamTranslateWithTag(EntityPlayer player, Style style, String tag, String extraInfo){
		if(!player.worldObj.isRemote)sendNoSpam(player, new TextComponentTranslation(extraInfo, new TextComponentTranslation("tomsMod.tag", new TextComponentTranslation(tag).setStyle(style)).setStyle(style)).setStyle(style));
	}
	public static ItemStack pushStackToNeighbours(ItemStack stack, World world, BlockPos pos, EnumFacing[] sides){
		if(sides == null)sides = EnumFacing.VALUES;
		for(EnumFacing f : sides){
			BlockPos p = pos.offset(f);
			IInventory inv = TileEntityHopper.getInventoryAtPosition(world, p.getX(), p.getY(), p.getZ());
			if(inv != null){
				stack = TileEntityHopper.putStackInInventoryAllSlots(inv, stack, f.getOpposite());
			}
			if(stack == null)break;
		}
		return stack;
	}
	public static void sendNoSpamTranslate(EntityPlayer player, TextFormatting color, String key, Object... args){
		sendNoSpamTranslate(player, new Style().setColor(color), key, args);
	}
	public static String formatNumber(int number)
	{
		int width = 4;
		assert number >= 0;
		String numberString = Integer.toString( number );
		int numberSize = numberString.length();
		if( numberSize <= width )
		{
			return numberString;
		}

		int base = number;
		double last = base * 1000;
		int exponent = -1;
		String postFix = "";

		while( numberSize > width )
		{
			last = base;
			base /= DIVISION_BASE;

			exponent++;

			numberSize = Integer.toString( base ).length() + 1;
			postFix = String.valueOf( ENCODED_POSTFIXES[exponent] );
		}

		String withPrecision = format.format( last / DIVISION_BASE ) + postFix;
		String withoutPrecision = Integer.toString( base ) + postFix;

		String slimResult = ( withPrecision.length() <= width ) ? withPrecision : withoutPrecision;
		assert slimResult.length() <= width;
		return slimResult;
	}
	@SideOnly(Side.CLIENT)
	public static class GuiRunnableLabel extends GuiLabel{
		GuiRenderRunnable field;
		public GuiRunnableLabel(GuiRenderRunnable field) {
			super(null,0,0,0,0,0,0);
			this.field = field;
		}
		@Override
		public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
			field.run(mouseX, mouseY);
		}
	}
	@SideOnly(Side.CLIENT)
	public static interface GuiRenderRunnable{
		void run(int mouseX, int mouseY);
	}
	@SideOnly(Side.CLIENT)
	public static List<GuiLabel> addRunnableToLabelList(GuiRenderRunnable field, List<GuiLabel> labelList){
		if(field != null)labelList.add(new GuiRunnableLabel(field));
		return labelList;
	}
	public static boolean isClient()
	{
		return FMLCommonHandler.instance().getEffectiveSide().isClient();
	}
	public static boolean areItemStacksEqualOreDict(ItemStack stack, ItemStack matchTo, boolean checkMeta, boolean checkNBT, boolean checkMod, boolean checkOreDict){
		if(stack == null && matchTo == null)return true;
		if(stack != null && matchTo != null){
			if(areItemStacksEqual(stack, matchTo, checkMeta, checkNBT, checkMod)){
				return true;
			}else if(checkOreDict){
				int[] matchIds = OreDictionary.getOreIDs(matchTo);
				int[] ids = OreDictionary.getOreIDs(stack);
				if(matchIds.length < 1 && ids.length < 1){
					return areItemStacksEqual(stack, matchTo, checkMeta, checkNBT, checkMod);
				}
				boolean equals = false;
				for(int i = 0;i<matchIds.length;i++){
					for(int j = 0;j<ids.length;j++){
						if(matchIds[i] == ids[j]){
							equals = true;
							break;
						}
					}
				}
				if(checkNBT){
					equals = equals && ItemStack.areItemStackTagsEqual(stack, matchTo);
				}
				return equals;
			}
		}
		return false;
	}
	public static void sendChatMessages(EntityPlayer player, ITextComponent... lines){
		for(ITextComponent c : lines){
			player.addChatMessage(c);
		}
	}
	public static void sendChatTranslate(EntityPlayer player, String key, Object... args){
		player.addChatMessage(new TextComponentTranslation(key,args));
	}
	public static void sendChatTranslate(EntityPlayer player,
			Style style, String key, Object... args) {
		player.addChatMessage(new TextComponentTranslation(key,args).setStyle(style));
	}
	public static MinecraftServer getServer(){
		return server;
	}
	public static void setServer(MinecraftServer server){
		TomsModUtils.server = server;
	}
	@SideOnly(Side.CLIENT)
	public static String getTranslatedName(ItemStack stack){
		String name = I18n.format(stack.getUnlocalizedName() + ".name");
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("display", 10))
		{
			NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("display");

			if (nbttagcompound.hasKey("Name", 8))
			{
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
	public static FakePlayer getFakePlayer(World world){
		if(world instanceof WorldServer){
			FakePlayer ret = TOMSMOD_PLAYER != null ? TOMSMOD_PLAYER.get() : null;
			if (ret == null)
			{
				ret = FakePlayerFactory.get((WorldServer) world,  profile);
				TOMSMOD_PLAYER = new WeakReference<FakePlayer>(ret);
			}
			return ret;
		}else return null;
	}
	public static List<ItemStack> craft(ItemStack[] input, EntityPlayer player, World world){
		if(player == null)player = getFakePlayer(world);
		for(int i = 0;i<input.length && i<9;i++){
			craftingInv.setInventorySlotContents(i, ItemStack.copyItemStack(input[i]));
		}
		List<ItemStack> ret = new ArrayList<ItemStack>();
		ItemStack result = CraftingManager.getInstance().findMatchingRecipe(craftingInv, player.worldObj);
		if(result != null){
			FMLCommonHandler.instance().firePlayerCraftingEvent(player, result, craftingInv);
			ForgeHooks.setCraftingPlayer(player);
			ItemStack[] aitemstack = CraftingManager.getInstance().getRemainingItems(craftingInv, player.worldObj);
			ForgeHooks.setCraftingPlayer(null);
			ret.add(result);
			for (int i = 0; i < aitemstack.length; ++i)
			{
				ItemStack itemstack = craftingInv.getStackInSlot(i);
				ItemStack itemstack1 = aitemstack[i];

				if (itemstack != null)
				{
					craftingInv.decrStackSize(i, 1);
					itemstack = craftingInv.getStackInSlot(i);
				}

				if (itemstack1 != null)
				{
					if (itemstack == null)
					{
						craftingInv.setInventorySlotContents(i, itemstack1);
					}
					else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1))
					{
						itemstack1.stackSize += itemstack.stackSize;
						craftingInv.setInventorySlotContents(i, itemstack1);
					}
					else ret.add(itemstack1);
				}
			}
		}
		for(int i = 0;i<9;i++){
			ItemStack stack = craftingInv.removeStackFromSlot(i);
			if(stack != null){
				ret.add(stack);
			}
		}
		return result != null ? ret : null;
	}
	public static ItemStack[] getStackArrayFromInventory(IInventory inv){
		ItemStack[] ret = new ItemStack[inv.getSizeInventory()];
		for(int i = 0;i<inv.getSizeInventory();i++){
			ret[i] = inv.getStackInSlot(i);
		}
		return ret;
	}
	public static ItemStack getMathchingRecipe(ItemStack[] input, World world){
		for(int i = 0;i<input.length && i<9;i++){
			craftingInv.setInventorySlotContents(i, input[i]);
		}
		ItemStack result = CraftingManager.getInstance().findMatchingRecipe(craftingInv, world);
		for(int i = 0;i<9;i++){
			craftingInv.setInventorySlotContents(i, null);
		}
		return result;
	}
	public static ItemStack getMathchingRecipe(IInventory input, World world){
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
	public static void addActiveTag(List<String> list, boolean active){
		list.add(I18n.format("tomsMod" + (active ? "." : ".in") + "active"));
	}
	@SideOnly(Side.CLIENT)
	public static IBakedModel getBakedModelFromItemBlockStack(ItemStack blockStack, IBlockState defaultBlock){
		IBlockState state = blockStack != null ? getBlockStateFrom(blockStack) : defaultBlock;
		return getBakedModelFromBlockState(state, getBakedModelFromBlockState(defaultBlock, null));
	}
	public static List<ItemStack> copyItemStackList(List<ItemStack> in, boolean nullCheck){
		List<ItemStack> list = new ArrayList<ItemStack>();
		for(int i = 0;i<in.size();i++){
			ItemStack stack = in.get(i);
			if(stack != null){
				list.add(stack.copy());
			}else if(!nullCheck){
				list.add(null);
			}
		}
		return list;
	}
	public static List<ItemStack> copyItemStackList(List<ItemStack> in){
		return copyItemStackList(in, false);
	}
	public static List<String> getStringList(String... in){
		List<String> list = new ArrayList<String>();
		if(in != null){
			for(String a : in){
				list.add(a);
			}
		}
		return list;
	}
	public static List<ItemStack> getItemStackList(ItemStack... in) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		if(in != null){
			for(ItemStack a : in){
				list.add(a);
			}
		}
		return list;
	}
	public static boolean occlusionTest(IMultipart part, AxisAlignedBB box){
		return OcclusionHelper.occlusionTest(OcclusionHelper.boxes(box), part.getContainer().getParts());
	}
	public static boolean areFluidStacksEqual(FluidStack stackA, FluidStack stackB){
		return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isFluidStackIdentical(stackB) : false);
	}
	public static int getAllTrues(boolean... bs){
		if(bs == null || bs.length < 1)return -1;
		int i2 = 0;
		for(int i = 0;i<bs.length;i++)
			if(bs[i])i2++;
		return i2;
	}
	public static void sendChatTranslate(EntityPlayer player, TextFormatting color, String key, Object... args) {
		sendChatTranslate(player, new Style().setColor(color), key, args);
	}
	public static int average_int(int[] values){
		int i = 0;

		for (int j : values)
		{
			i += j;
		}

		return MathHelper.ceiling_double_int((double)i / (double)values.length);
	}
	public static int[] array_intAddLimit(int[] in, int value, int limit){
		int[] ret = new int[Math.min(in.length + 1, limit)];
		System.arraycopy(in, Math.max(0, in.length - limit), ret, 0, in.length);
		ret[Math.min(in.length, limit - 1)] = value;
		return ret;
	}
	public static boolean areFluidStacksEqual2(FluidStack stackA, FluidStack stackB){
		return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isFluidEqual(stackB) && stackA.amount <= stackB.amount : false);
	}
}
