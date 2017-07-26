package mapwriterTm.region;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import mapwriterTm.util.Logging;
import mapwriterTm.util.Reference;
import mapwriterTm.util.Render;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;

public class BlockColours {
	public static final int MAX_META = 16;

	public static final String biomeSectionString = "[biomes]";
	public static final String blockSectionString = "[blocks]";

	private LinkedHashMap<String, BiomeData> biomeMap = new LinkedHashMap<>();

	private LinkedHashMap<String, BlockData> bcMap = new LinkedHashMap<>();

	public enum BlockType {
		NORMAL, GRASS, LEAVES, FOLIAGE, WATER, OPAQUE
	}

	public BlockColours() {
	}

	public String CombineBlockMeta(String BlockName, int meta) {
		return BlockName + " " + meta;
	}

	public String CombineBlockMeta(String BlockName, String meta) {
		return BlockName + " " + meta;
	}

	public int getColour(String BlockName, int meta) {
		String BlockAndMeta = this.CombineBlockMeta(BlockName, meta);
		String BlockAndWildcard = this.CombineBlockMeta(BlockName, "*");

		BlockData data = new BlockData();

		if (this.bcMap.containsKey(BlockAndMeta)) {
			data = this.bcMap.get(BlockAndMeta);
		} else if (this.bcMap.containsKey(BlockAndWildcard)) {
			data = this.bcMap.get(BlockAndWildcard);
		}
		return data.color;
	}

	public int getColour(IBlockState BlockState) {
		Block block = BlockState.getBlock();
		int meta = block.getMetaFromState(BlockState);

		if (block.delegate == null) {
			Logging.logError("Delegate was Null when getting colour, Block in: %s", block.toString());
			return 0;
		} else if (block.delegate.name() == null) {
			// Logging.logError("Block Name was Null when getting colour, Block
			// in: %s, Delegate: %s",
			// block.toString(),block.delegate.toString());
			return 0;
		}
		return this.getColour(block.delegate.name().toString(), meta);
	}

	public void setColour(String BlockName, String meta, int colour) {
		String BlockAndMeta = this.CombineBlockMeta(BlockName, meta);

		if (meta.equals("*")) {
			for (int i = 0;i < 16;i++) {
				this.setColour(BlockName, String.valueOf(i), colour);
			}
		}

		if (this.bcMap.containsKey(BlockAndMeta)) {
			BlockData data = this.bcMap.get(BlockAndMeta);
			data.color = colour;
		} else {
			BlockData data = new BlockData();
			data.color = colour;
			this.bcMap.put(BlockAndMeta, data);
		}
	}

	private int getGrassColourMultiplier(String biomeName) {
		BiomeData data = this.biomeMap.get(biomeName);
		return (data != null) ? data.grassMultiplier : 0xffffff;
	}

	private int getWaterColourMultiplier(String biomeName) {
		BiomeData data = this.biomeMap.get(biomeName);
		return (data != null) ? data.waterMultiplier : 0xffffff;
	}

	private int getFoliageColourMultiplier(String biomeName) {
		BiomeData data = this.biomeMap.get(biomeName);
		return (data != null) ? data.foliageMultiplier : 0xffffff;
	}

	public int getBiomeColour(String BlockName, int meta, String biomeName) {
		int colourMultiplier = 0xffffff;

		if (this.bcMap.containsKey(this.CombineBlockMeta(BlockName, meta))) {
			switch (this.bcMap.get(this.CombineBlockMeta(BlockName, meta)).type) {
			case GRASS:
				colourMultiplier = this.getGrassColourMultiplier(biomeName);
				break;
			case LEAVES:
			case FOLIAGE:
				colourMultiplier = this.getFoliageColourMultiplier(biomeName);
				break;
			case WATER:
				colourMultiplier = this.getWaterColourMultiplier(biomeName);
				break;
			default:
				colourMultiplier = 0xffffff;
				break;
			}
		}
		return colourMultiplier;
	}

	public int getBiomeColour(IBlockState BlockState, byte biomeId) {
		String biomeName = "";
		Biome biome = Biome.getBiomeForId(biomeId);
		if (biome != null) {
			biomeName = Biome.getBiomeForId(biomeId).getBiomeName();
		}

		Block block = BlockState.getBlock();
		int meta = block.getMetaFromState(BlockState);

		return this.getBiomeColour(block.delegate.name().toString(), meta, biomeName);
	}

	public void setBiomeData(String biomeName, int waterShading, int grassShading, int foliageShading) {
		BiomeData data = new BiomeData();
		data.foliageMultiplier = foliageShading;
		data.grassMultiplier = grassShading;
		data.waterMultiplier = waterShading;
		biomeMap.put(biomeName, data);
	}

	private static BlockType getBlockTypeFromString(String typeString) {
		BlockType blockType = BlockType.NORMAL;
		if (typeString.equalsIgnoreCase("normal")) {
			blockType = BlockType.NORMAL;
		} else if (typeString.equalsIgnoreCase("grass")) {
			blockType = BlockType.GRASS;
		} else if (typeString.equalsIgnoreCase("leaves")) {
			blockType = BlockType.LEAVES;
		} else if (typeString.equalsIgnoreCase("foliage")) {
			blockType = BlockType.FOLIAGE;
		} else if (typeString.equalsIgnoreCase("water")) {
			blockType = BlockType.WATER;
		} else if (typeString.equalsIgnoreCase("opaque")) {
			blockType = BlockType.OPAQUE;
		} else {
			Logging.logWarning("unknown block type '%s'", typeString);
		}
		return blockType;
	}

	private static String getBlockTypeAsString(BlockType blockType) {
		String s = "normal";
		switch (blockType) {
		case NORMAL:
			s = "normal";
			break;
		case GRASS:
			s = "grass";
			break;
		case LEAVES:
			s = "leaves";
			break;
		case FOLIAGE:
			s = "foliage";
			break;
		case WATER:
			s = "water";
			break;
		case OPAQUE:
			s = "opaque";
			break;
		}
		return s;
	}

	public BlockType getBlockType(String BlockName, int meta) {
		String BlockAndMeta = this.CombineBlockMeta(BlockName, meta);
		String BlockAndWildcard = this.CombineBlockMeta(BlockName, "*");

		BlockData data = new BlockData();

		if (this.bcMap.containsKey(BlockAndMeta)) {
			data = this.bcMap.get(BlockAndMeta);
		} else if (this.bcMap.containsKey(BlockAndWildcard)) {
			data = this.bcMap.get(BlockAndWildcard);
		}
		return data.type;
	}

	public BlockType getBlockType(int BlockAndMeta) {
		Block block = Block.getBlockById(BlockAndMeta >> 4);
		int meta = BlockAndMeta & 0xf;
		return this.getBlockType(block.delegate.name().toString(), meta);
	}

	public void setBlockType(String BlockName, String meta, BlockType type) {
		String BlockAndMeta = this.CombineBlockMeta(BlockName, meta);

		if (meta.equals("*")) {
			for (int i = 0;i < 16;i++) {
				this.setBlockType(BlockName, String.valueOf(i), type);
			}
			return;
		}

		if (this.bcMap.containsKey(BlockAndMeta)) {
			BlockData data = this.bcMap.get(BlockAndMeta);
			data.type = type;
			data.color = adjustBlockColourFromType(BlockName, meta, type, data.color);
		} else {
			BlockData data = new BlockData();
			data.type = type;
			this.bcMap.put(BlockAndMeta, data);
		}
	}

	private static int adjustBlockColourFromType(String BlockName, String meta, BlockType type, int blockColour) {
		// for normal blocks multiply the block colour by the render colour.
		// for other blocks the block colour will be multiplied by the biome
		// colour.
		Block block = Block.getBlockFromName(BlockName);

		switch (type) {
		case OPAQUE:
			blockColour |= 0xff000000;
		case NORMAL:
			// fix crash when mods don't implement getRenderColor for all
			// block meta values.
			try {
				@SuppressWarnings("deprecation")
				int renderColour = block.getMapColor(block.getStateFromMeta(Integer.parseInt(meta) & 0xf)).colorValue;
				if (renderColour != 0xffffff) {
					blockColour = Render.multiplyColours(blockColour, 0xff000000 | renderColour);
				}
			} catch (RuntimeException e) {
				// do nothing
			}
			break;
		case LEAVES:
			// leaves look weird on the map if they are not opaque.
			// they also look too dark if the render colour is applied.
			blockColour |= 0xff000000;
			break;
		case GRASS:
			// the icon returns the dirt texture so hardcode it to the grey
			// undertexture.
			blockColour = 0xff9b9b9b;
		default:
			break;
		}
		return blockColour;
	}

	public static int getColourFromString(String s) {
		return (int) (Long.parseLong(s, 16) & 0xffffffffL);
	}

	//
	// Methods for loading block colours from file:
	//

	// read biome colour multiplier values.
	// line format is:
	// biome <biomeId> <waterMultiplier> <grassMultiplier> <foliageMultiplier>
	// accepts "*" wildcard for biome id (meaning for all biomes).
	private void loadBiomeLine(String[] split) {
		try {
			int waterMultiplier = getColourFromString(split[2]) & 0xffffff;
			int grassMultiplier = getColourFromString(split[3]) & 0xffffff;
			int foliageMultiplier = getColourFromString(split[4]) & 0xffffff;
			this.setBiomeData(split[1], waterMultiplier, grassMultiplier, foliageMultiplier);
		}

		catch (NumberFormatException e) {
			Logging.logWarning("invalid biome colour line '%s %s %s %s %s'", split[0], split[1], split[2], split[3], split[4]);
		}
	}

	// read block colour values.
	// line format is:
	// block <blockName> <blockMeta> <colour>
	// the biome id, meta value, and colour code are in hex.
	// accepts "*" wildcard for biome id and meta (meaning for all blocks and/or
	// meta values).
	private void loadBlockLine(String[] split) {
		try {
			// block colour line
			int colour = getColourFromString(split[3]);
			this.setColour(split[1], split[2], colour);

		} catch (NumberFormatException e) {
			Logging.logWarning("invalid block colour line '%s %s %s %s'", split[0], split[1], split[2], split[3]);
		}
	}

	private void loadBlockTypeLine(String[] split) {
		try {
			// block type line
			BlockType type = getBlockTypeFromString(split[3]);
			this.setBlockType(split[1], split[2], type);
		} catch (NumberFormatException e) {
			Logging.logWarning("invalid block colour line '%s %s %s %s'", split[0], split[1], split[2], split[3]);
		}
	}

	public void loadFromFile(File f) {
		Scanner fin = null;
		try {
			fin = new Scanner(new FileReader(f));

			while (fin.hasNextLine()) {
				// get next line and remove comments (part of line after #)
				String line = fin.nextLine().split("#")[0].trim();
				if (line.length() > 0) {
					String[] lineSplit = line.split(" ");
					if (lineSplit[0].equals("biome") && (lineSplit.length == 5)) {
						this.loadBiomeLine(lineSplit);
					} else if (lineSplit[0].equals("block") && (lineSplit.length == 4)) {
						this.loadBlockLine(lineSplit);
					} else if (lineSplit[0].equals("blocktype") && (lineSplit.length == 4)) {
						this.loadBlockTypeLine(lineSplit);
					} else if (lineSplit[0].equals("version:")) {

					} else {
						Logging.logWarning("invalid map colour line '%s'", line);
					}
				}
			}
		} catch (IOException e) {
			Logging.logError("loading block colours: no such file '%s'", f);

		} finally {
			if (fin != null) {
				fin.close();
			}
		}
	}

	//
	// Methods for saving block colours to file.
	//

	// save biome colour multipliers to a file.
	public void saveBiomes(Writer fout) throws IOException {
		fout.write("biome * ffffff ffffff ffffff\n");

		for (Map.Entry<String, BiomeData> entry : this.biomeMap.entrySet()) {
			String biomeName = entry.getKey();
			BiomeData data = entry.getValue();

			// don't add lines that are covered by the default.
			if ((data.waterMultiplier != 0xffffff) || (data.grassMultiplier != 0xffffff) || (data.foliageMultiplier != 0xffffff)) {
				fout.write(String.format("biome %s %06x %06x %06x\n", biomeName, data.waterMultiplier, data.grassMultiplier, data.foliageMultiplier));
			}
		}
	}

	private static String getMostOccurringKey(Map<String, Integer> map, String defaultItem) {
		// find the most commonly occurring key in a hash map.
		// only return a key if there is more than 1.
		int maxCount = 1;
		String mostOccurringKey = defaultItem;
		for (Entry<String, Integer> entry : map.entrySet()) {
			String key = entry.getKey();
			int count = entry.getValue();

			if (count > maxCount) {
				maxCount = count;
				mostOccurringKey = key;
			}
		}

		return mostOccurringKey;
	}

	// to use the least number of lines possible find the most commonly
	// occurring
	// item for the different meta values of a block.
	// an 'item' is either a block colour or a block type.
	// the most commonly occurring item is then used as the wildcard entry for
	// the block, and all non matching items added afterwards.
	private static void writeMinimalBlockLines(Writer fout, String lineStart, List<String> items, String defaultItem) throws IOException {

		Map<String, Integer> frequencyMap = new HashMap<>();

		// first count the number of occurrences of each item.
		for (String item : items) {
			int count = 0;
			if (frequencyMap.containsKey(item)) {
				count = frequencyMap.get(item);
			}
			frequencyMap.put(item, count + 1);
		}

		// then find the most commonly occurring item.
		String mostOccurringItem = getMostOccurringKey(frequencyMap, defaultItem);

		// only add a wildcard line if it actually saves lines.
		if (!mostOccurringItem.equals(defaultItem)) {
			fout.write(String.format("%s * %s\n", lineStart, mostOccurringItem));
		}

		// add lines for items that don't match the wildcard line.

		int meta = 0;
		for (String s : items) {
			if (!s.equals(mostOccurringItem) && !s.equals(defaultItem)) {
				fout.write(String.format("%s %d %s\n", lineStart, meta, s));
			}
			meta++;
		}
	}

	public void saveBlocks(Writer fout) throws IOException {
		fout.write("block * * 00000000\n");

		String LastBlock = "";
		List<String> colours = new ArrayList<>();

		for (Map.Entry<String, BlockData> entry : this.bcMap.entrySet()) {
			String[] BlockAndMeta = entry.getKey().split(" ");
			String block = BlockAndMeta[0];

			String color = String.format("%08x", entry.getValue().color);

			if (!LastBlock.equals(block) && !LastBlock.isEmpty()) {
				String lineStart = String.format("block %s", LastBlock);
				writeMinimalBlockLines(fout, lineStart, colours, "00000000");

				colours.clear();
			}

			colours.add(color);
			LastBlock = block;
		}
	}

	public void saveBlockTypes(Writer fout) throws IOException {
		fout.write("blocktype * * normal\n");

		String LastBlock = "";
		List<String> blockTypes = new ArrayList<>();

		for (Map.Entry<String, BlockData> entry : this.bcMap.entrySet()) {
			String[] BlockAndMeta = entry.getKey().split(" ");
			String block = BlockAndMeta[0];

			String Type = getBlockTypeAsString(entry.getValue().type);

			if (!LastBlock.equals(block) && !LastBlock.isEmpty()) {
				String lineStart = String.format("blocktype %s", LastBlock);
				writeMinimalBlockLines(fout, lineStart, blockTypes, getBlockTypeAsString(BlockType.NORMAL));

				blockTypes.clear();
			}

			blockTypes.add(Type);
			LastBlock = block;
		}
	}

	// save block colours and biome colour multipliers to a file.
	public void saveToFile(File f) {
		Writer fout = null;
		try {
			fout = new OutputStreamWriter(new FileOutputStream(f));
			fout.write(String.format("version: %s\n", Reference.VERSION));
			this.saveBiomes(fout);
			this.saveBlockTypes(fout);
			this.saveBlocks(fout);

		} catch (IOException e) {
			Logging.logError("saving block colours: could not write to '%s'", f);

		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void writeOverridesFile(File f) {
		Writer fout = null;
		try {
			fout = new OutputStreamWriter(new FileOutputStream(f));
			fout.write(String.format("version: %s\n", Reference.VERSION));

			fout.write("block minecraft:yellow_flower * 60ffff00	# make dandelions more yellow\n" + "block minecraft:red_flower 0 60ff0000		# make poppy more red\n" + "block minecraft:red_flower 1 601c92d6		# make Blue Orchid more red\n" + "block minecraft:red_flower 2 60b865fb		# make Allium more red\n" + "block minecraft:red_flower 3 60e4eaf2		# make Azure Bluet more red\n" + "block minecraft:red_flower 4 60d33a17		# make Red Tulip more red\n" + "block minecraft:red_flower 5 60e17124		# make Orange Tulip more red\n" + "block minecraft:red_flower 6 60ffffff		# make White Tulip more red\n" + "block minecraft:red_flower 7 60eabeea		# make Pink Tulip more red\n" + "block minecraft:red_flower 8 60eae6ad		# make Oxeye Daisy more red\n" + "block minecraft:double_plant 0 60ffff00		# make Sunflower more Yellow-orrange\n" + "block minecraft:double_plant 1 d09f78a4		# make Lilac more pink\n" + "block minecraft:double_plant 4 60ff0000		# make Rose Bush more red\n" + "block minecraft:double_plant 5 d0e3b8f7		# make Peony more red\n" + "blocktype minecraft:grass * grass			# grass block\n" + "blocktype minecraft:flowing_water * water	# flowing water block\n" + "blocktype minecraft:water * water			# still water block\n" + "blocktype minecraft:leaves * leaves    		# leaves block\n" + "blocktype minecraft:leaves2 * leaves    		# leaves block\n" + "blocktype minecraft:leaves 1 opaque    		# pine leaves (not biome colorized)\n" + "blocktype minecraft:leaves 2 opaque    		# birch leaves (not biome colorized)\n" + "blocktype minecraft:tallgrass * grass     	# tall grass block\n" + "blocktype minecraft:vine * foliage  			# vines block\n" + "blocktype BiomesOPlenty:grass * grass		# BOP grass block\n" + "blocktype BiomesOPlenty:plant_0 * grass		# BOP plant block\n" + "blocktype BiomesOPlenty:plant_1 * grass		# BOP plant block\n" + "blocktype BiomesOPlenty:leaves_0 * leaves	# BOP Leave block\n" + "blocktype BiomesOPlenty:leaves_1 * leaves	# BOP Leave block\n" + "blocktype BiomesOPlenty:leaves_2 * leaves	# BOP Leave block\n" + "blocktype BiomesOPlenty:leaves_3 * leaves	# BOP Leave block\n" + "blocktype BiomesOPlenty:leaves_4 * leaves	# BOP Leave block\n" + "blocktype BiomesOPlenty:leaves_5 * leaves	# BOP Leave block\n" + "blocktype BiomesOPlenty:tree_moss * foliage	# biomes o plenty tree moss\n");
			// TODO: Find out the names and readd these overwrites
			// + "blocktype 2164 * leaves # twilight forest leaves\n"
			// +
			// "blocktype 2177 * leaves # twilight forest magic leaves\n"

			// + "blocktype 2204 * leaves # extrabiomesXL green leaves\n"
			// +
			// "blocktype 2200 * opaque # extrabiomesXL autumn leaves\n"

			// + "blocktype 3257 * opaque # natura berry bush\n"
			// + "blocktype 3272 * opaque # natura darkwood leaves\n"
			// + "blocktype 3259 * leaves # natura flora leaves\n"
			// + "blocktype 3278 * opaque # natura rare leaves\n"
			// + "blocktype 3258 * opaque # natura sakura leaves\n"
		} catch (IOException e) {
			Logging.logError("saving block overrides: could not write to '%s'", f);

		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public boolean CheckFileVersion(File fn) {
		String lineData = "";
		try {
			RandomAccessFile inFile = new RandomAccessFile(fn, "rw");
			lineData = inFile.readLine();
			inFile.close();
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}

		if (lineData.equals(String.format("version: %s", Reference.VERSION))) { return true; }

		return false;
	}

	public class BlockData {
		public int color = 0;
		public BlockType type = BlockType.NORMAL;
	}

	public class BiomeData {
		private int waterMultiplier = 0;
		private int grassMultiplier = 0;
		private int foliageMultiplier = 0;
	}
}
