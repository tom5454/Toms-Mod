package mapwriterTm.region;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mapwriterTm.util.Logging;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class MwChunk implements IChunk
{
	public static final int SIZE = 16;

	public final int x;
	public final int z;
	public final int dimension;

	public ExtendedBlockStorage[] dataArray = new ExtendedBlockStorage[16];
	public final Map<BlockPos, TileEntity> tileentityMap;
	public final byte[] biomeArray;
	public final int maxY;

	public MwChunk(int x, int z, int dimension, ExtendedBlockStorage[] data, byte[] biomeArray,
			Map<BlockPos, TileEntity> TileEntityMap)
	{
		this.x = x;
		this.z = z;
		this.dimension = dimension;
		this.biomeArray = biomeArray;
		this.tileentityMap = TileEntityMap;
		this.dataArray = data;
		int maxY = 0;
		for (int y = 0; y < 16; y++)
		{
			if (data[y] != null)
			{
				maxY = (y << 4) + 15;
			}
		}
		this.maxY = maxY;
	}

	@Override
	public String toString()
	{
		return String.format("(%d, %d) dim%d", this.x, this.z, this.dimension);
	}

	// load from anvil file
	public static MwChunk read(int x, int z, int dimension, RegionFileCache regionFileCache)
	{
		//
		Boolean flag = true;
		byte[] biomeArray = null;
		ExtendedBlockStorage[] data = new ExtendedBlockStorage[16];
		Map<BlockPos, TileEntity> TileEntityMap = new HashMap<BlockPos, TileEntity>();

		DataInputStream dis = null;
		RegionFile regionFile = regionFileCache.getRegionFile(x << 4, z << 4, dimension);
		if (!regionFile.isOpen())
		{
			if (regionFile.exists())
			{
				regionFile.open();
			}
		}

		if (regionFile.isOpen())
		{
			dis = regionFile.getChunkDataInputStream(x & 31, z & 31);
		}

		if (dis != null)
		{
			try
			{

				// chunk NBT structure:
				//
				// COMPOUND ""
				// COMPOUND "level"
				// INT "xPos"
				// INT "zPos"
				// LONG "LastUpdate"
				// BYTE "TerrainPopulated"
				// BYTE_ARRAY "Biomes"
				// INT_ARRAY "HeightMap"
				// LIST(COMPOUND) "Sections"
				// BYTE "Y"
				// BYTE_ARRAY "Blocks"
				// BYTE_ARRAY "Add"
				// BYTE_ARRAY "Data"
				// BYTE_ARRAY "BlockLight"
				// BYTE_ARRAY "SkyLight"
				// END
				// LIST(COMPOUND) "Entities"
				// LIST(COMPOUND) "TileEntities"
				// LIST(COMPOUND) "TileTicks"
				// END
				// END
				NBTTagCompound nbttagcompound = CompressedStreamTools.read(dis);
				NBTTagCompound level = nbttagcompound.getCompoundTag("Level");

				int xNbt = level.getInteger("xPos");
				int zNbt = level.getInteger("zPos");

				if ((xNbt != x) || (zNbt != z))
				{
					Logging.logWarning("chunk (%d, %d) has NBT coords (%d, %d)", x, z, xNbt, zNbt);
				}

				NBTTagList sections = level.getTagList("Sections", 10);

				for (int k = 0; k < sections.tagCount(); ++k)
				{
					NBTTagCompound section = sections.getCompoundTagAt(k);
					int y = section.getByte("Y");
					ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(
							y << 4,
							flag);
					byte[] abyte = nbttagcompound.getByteArray("Blocks");
					NibbleArray nibblearray = new NibbleArray(nbttagcompound.getByteArray("Data"));
					NibbleArray nibblearray1 = nbttagcompound.hasKey("Add", 7) ? new NibbleArray(
							nbttagcompound.getByteArray("Add")) : null;
							extendedblockstorage.getData().setDataFromNBT(abyte, nibblearray, nibblearray1);
							extendedblockstorage.setBlocklightArray(
									new NibbleArray(nbttagcompound.getByteArray("BlockLight")));

							if (flag)
							{
								extendedblockstorage.setSkylightArray(
										new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
							}

							extendedblockstorage.removeInvalidBlocks();
							data[y] = extendedblockstorage;
				}

				biomeArray = level.getByteArray("Biomes");

				NBTTagList nbttaglist2 = level.getTagList("TileEntities", 10);

				if (nbttaglist2 != null)
				{
					for (int i1 = 0; i1 < nbttaglist2.tagCount(); ++i1)
					{
						NBTTagCompound nbttagcompound4 = nbttaglist2.getCompoundTagAt(i1);
						TileEntity tileentity = TileEntity.create(null, nbttagcompound4);
						if (tileentity != null)
						{
							TileEntityMap.put(tileentity.getPos(), tileentity);
						}
					}
				}

			}
			catch (IOException e)
			{
				Logging.logError("%s: could not read chunk (%d, %d) from region file\n", e, x, z);
			}
			finally
			{
				try
				{
					dis.close();
				}
				catch (IOException e)
				{
					Logging.logError("MwChunk.read: %s while closing input stream", e);
				}
			}
			// this.log("MwChunk.read: chunk (%d, %d) empty=%b", this.x, this.z,
			// empty);
		}
		else
		{
			// this.log("MwChunk.read: chunk (%d, %d) input stream is null",
			// this.x, this.z);
		}

		return new MwChunk(x, z, dimension, data, biomeArray, TileEntityMap);
	}

	public boolean isEmpty()
	{
		return (this.maxY <= 0);
	}

	@Override
	public byte getBiome(int x, int z)
	{
		return (this.biomeArray != null) ? (this.biomeArray[((z & 0xf) << 4) | (x & 0xf)]) : 0;
	}

	@Override
	public int getLightValue(int x, int y, int z)
	{
		// int yi = (y >> 4) & 0xf;
		// int offset = ((y & 0xf) << 8) | ((z & 0xf) << 4) | (x & 0xf);

		// int light = ((this.lightingArray != null) && (this.lightingArray[yi]
		// != null)) ? this.lightingArray[yi][offset >> 1] : 15;

		// return ((offset & 1) == 1) ? ((light >> 4) & 0xf) : (light & 0xf);
		return 15;
	}

	@Override
	public int getMaxY()
	{
		return this.maxY;
	}

	@SuppressWarnings("unused")
	private static java.lang.reflect.Method CarpenterMethod = null;

	public static void carpenterdata()
	{
		try
		{
			Class<?> act = Class.forName("com.carpentersblocks.tileentity.TEBase");
			CarpenterMethod = act.getMethod("getAttribute", byte.class);
		}
		catch (SecurityException e)
		{
			// ...
		}
		catch (NoSuchMethodException e)
		{
			// ...
		}
		catch (ClassNotFoundException e)
		{
			//
		}
	}

	@SuppressWarnings("unused")
	private static java.lang.reflect.Method FMPMethodParts = null;
	@SuppressWarnings("unused")
	private static java.lang.reflect.Method FMPMethodMaterial = null;

	private static java.lang.reflect.Field FMPFieldBlock = null;
	private static java.lang.reflect.Field FMPFieldMeta = null;

	public static void FMPdata()
	{
		try
		{
			Class<?> act = Class.forName("codechicken.multipart.TileMultipart");
			FMPMethodParts = act.getMethod("jPartList");
			act = Class.forName("codechicken.microblock.Microblock");
			FMPMethodMaterial = act.getMethod("getIMaterial");

			act = Class.forName("codechicken.microblock.BlockMicroMaterial");
			FMPFieldBlock = act.getDeclaredField("block");
			FMPFieldBlock.setAccessible(true);

			FMPFieldMeta = act.getDeclaredField("meta");
			FMPFieldMeta.setAccessible(true);

		}
		catch (SecurityException e)
		{
			// ...
		}
		catch (NoSuchMethodException e)
		{
			// ...
		}
		catch (ClassNotFoundException e)
		{
			//
		}
		catch (NoSuchFieldException e)
		{
			//
		}
	}

	@Override
	public IBlockState getBlockState(int x, int y, int z)
	{
		int yi = (y >> 4) & 0xf;

		return ((this.dataArray != null) && (this.dataArray[yi] != null)) ? this.dataArray[yi]
				.getData()
				.get(x & 15, y & 15, z & 15) : Blocks.AIR.getDefaultState();
	}

	// changed to use the NBTTagCompound that minecraft uses. this makes the
	// local way of saving anvill data the same as Minecraft world data
	private NBTTagCompound writeChunkToNBT()
	{
		NBTTagCompound level = new NBTTagCompound();
		NBTTagCompound compound = new NBTTagCompound();
		level.setTag("Level", compound);

		compound.setInteger("xPos", this.x);
		compound.setInteger("zPos", this.z);
		ExtendedBlockStorage[] aextendedblockstorage = this.dataArray;
		NBTTagList nbttaglist = new NBTTagList();
		//boolean flag = true;

		for (ExtendedBlockStorage extendedblockstorage : aextendedblockstorage)
		{
			if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte(
						"Y",
						(byte) (extendedblockstorage.getYLocation() >> 4 & 255));
				byte[] abyte = new byte[4096];
				NibbleArray nibblearray = new NibbleArray();
				NibbleArray nibblearray1 = extendedblockstorage.getData().getDataForNBT(
						abyte,
						nibblearray);
				nbttagcompound.setByteArray("Blocks", abyte);
				nbttagcompound.setByteArray("Data", nibblearray.getData());

				if (nibblearray1 != null)
				{
					nbttagcompound.setByteArray("Add", nibblearray1.getData());
				}

				nbttagcompound.setByteArray(
						"BlockLight",
						extendedblockstorage.getBlocklightArray().getData());

				if (extendedblockstorage.getSkylightArray() != null && extendedblockstorage
						.getSkylightArray()
						.getData() != null)
				{
					nbttagcompound.setByteArray(
							"SkyLight",
							extendedblockstorage.getSkylightArray().getData());
				}
				else
				{
					nbttagcompound.setByteArray(
							"SkyLight",
							new byte[extendedblockstorage.getBlocklightArray().getData().length]);
				}

				nbttaglist.appendTag(nbttagcompound);
			}
		}

		compound.setTag("Sections", nbttaglist);
		compound.setByteArray("Biomes", biomeArray);

		NBTTagList nbttaglist2 = new NBTTagList();

		for (TileEntity tileentity : tileentityMap.values())
		{
			try
			{
				NBTTagCompound nbttagcompound3 = new NBTTagCompound();
				tileentity.writeToNBT(nbttagcompound3);
				nbttaglist2.appendTag(nbttagcompound3);
			}
			catch (Exception e)
			{
				// we eat this exception becous we are doing something we
				// shouldnt do on client side.
			}
		}

		compound.setTag("TileEntities", nbttaglist2);

		return level;
	}

	public synchronized boolean write(RegionFileCache regionFileCache)
	{
		boolean error = false;
		RegionFile regionFile = regionFileCache.getRegionFile(
				this.x << 4,
				this.z << 4,
				this.dimension);
		if (!regionFile.isOpen())
		{
			error = regionFile.open();
		}
		if (!error)
		{
			DataOutputStream dos = regionFile.getChunkDataOutputStream(this.x & 31, this.z & 31);
			if (dos != null)
			{
				// Nbt chunkNbt = this.getNbt();
				try
				{
					// RegionManager.logInfo("writing chunk (%d, %d) to region
					// file",
					// this.x, this.z);
					// chunkNbt.writeElement(dos);
					// use minecraft build in save tool for saving the Anvil
					// Data
					CompressedStreamTools.write(this.writeChunkToNBT(), dos);
				}
				catch (IOException e)
				{
					Logging.logError(
							"%s: could not write chunk (%d, %d) to region file",
							e,
							this.x,
							this.z);
					error = true;
				}
				finally
				{
					try
					{
						dos.close();
					}
					catch (IOException e)
					{
						Logging.logError("%s while closing chunk data output stream", e);
					}
				}
			}
			else
			{
				Logging.logError(
						"error: could not get output stream for chunk (%d, %d)",
						this.x,
						this.z);
			}
		}
		else
		{
			Logging.logError(
					"error: could not open region file for chunk (%d, %d)",
					this.x,
					this.z);
		}

		return error;
	}

	public Long getCoordIntPair()
	{
		return ChunkPos.asLong(this.x, this.z);
	}
}
