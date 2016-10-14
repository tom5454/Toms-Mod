package mapwriterTm.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mapwriterTm.Mw;
import mapwriterTm.util.Reference;
import mapwriterTm.util.Utils;

import net.minecraftforge.common.config.Configuration;

public class WorldConfig
{
	private static WorldConfig instance = null;

	public Configuration worldConfiguration = null;
	//public File worldFile = null;

	// list of available dimensions
	public List<Integer> dimensionList = new ArrayList<Integer>();

	private WorldConfig()
	{
		// load world specific config file
		File worldConfigFile = new File(Mw.getInstance().worldDir, Reference.worldDirConfigName);
		this.worldConfiguration = new Configuration(worldConfigFile);
		//this.worldFile = new File(worldConfigFile.getAbsolutePath()+"d");

		this.InitDimensionList();
	}

	public static WorldConfig getInstance()
	{
		if (instance == null)
		{
			synchronized (WorldConfig.class)
			{
				if (instance == null)
				{
					instance = new WorldConfig();
				}
			}
		}

		return instance;
	}
	public static void reloadWorldConfig(){
		synchronized (WorldConfig.class)
		{
			instance = new WorldConfig();
		}
	}

	public void saveWorldConfig()
	{
		this.worldConfiguration.save();
	}

	// Dimension List
	public void InitDimensionList()
	{
		this.dimensionList.clear();
		this.worldConfiguration.get(Reference.catWorld, "dimensionList", Utils.integerListToIntArray(this.dimensionList));
		this.addDimension(0);
		this.cleanDimensionList();
	}

	public void addDimension(int dimension)
	{
		int i = this.dimensionList.indexOf(dimension);
		if (i < 0)
		{
			this.dimensionList.add(dimension);
		}
	}

	public void cleanDimensionList()
	{
		List<Integer> dimensionListCopy = new ArrayList<Integer>(this.dimensionList);
		this.dimensionList.clear();
		for (int dimension : dimensionListCopy)
		{
			this.addDimension(dimension);
		}
	}

}
