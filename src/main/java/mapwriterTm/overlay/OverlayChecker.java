package mapwriterTm.overlay;

import java.awt.Point;
import java.util.ArrayList;

import mapwriterTm.api.IMwChunkOverlay;
import mapwriterTm.api.IMwDataProvider;
import mapwriterTm.map.MapView;
import mapwriterTm.map.mapmode.MapMode;
import net.minecraft.util.math.MathHelper;

public class OverlayChecker implements IMwDataProvider
{

	public class ChunkOverlay implements IMwChunkOverlay
	{

		Point coord;

		public ChunkOverlay(int x, int z)
		{
			this.coord = new Point(x, z);
		}

		@Override
		public Point getCoordinates()
		{
			return this.coord;
		}

		@Override
		public int getColor()
		{
			return 0x90ffffff;
		}

		@Override
		public float getFilling()
		{
			return 1.0f;
		}

		@Override
		public boolean hasBorder()
		{
			return true;
		}

		@Override
		public float getBorderWidth()
		{
			return 0.5f;
		}

		@Override
		public int getBorderColor()
		{
			return 0xff000000;
		}

	}

	@Override
	public ArrayList<IMwChunkOverlay> getChunksOverlay(int dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ)
	{

		// We should pass the center of the map too to reduce the display like
		// in this case
		// and the zoom lvl, to provide higher level informations

		int minChunkX = (MathHelper.ceiling_double_int(minX) >> 4) - 1;
		int minChunkZ = (MathHelper.ceiling_double_int(minZ) >> 4) - 1;
		int maxChunkX = (MathHelper.ceiling_double_int(maxX) >> 4) + 1;
		int maxChunkZ = (MathHelper.ceiling_double_int(maxZ) >> 4) + 1;
		int cX = (MathHelper.ceiling_double_int(centerX) >> 4) + 1;
		int cZ = (MathHelper.ceiling_double_int(centerZ) >> 4) + 1;

		int limitMinX = Math.max(minChunkX, cX - 100);
		int limitMaxX = Math.min(maxChunkX, cX + 100);
		int limitMinZ = Math.max(minChunkZ, cZ - 100);
		int limitMaxZ = Math.min(maxChunkZ, cZ + 100);

		ArrayList<IMwChunkOverlay> chunks = new ArrayList<IMwChunkOverlay>();
		for (int x = limitMinX; x <= limitMaxX; x++)
		{
			for (int z = limitMinZ; z <= limitMaxZ; z++)
			{
				if (((x + z) % 2) == 0)
				{
					chunks.add(new ChunkOverlay(x, z));
				}
			}
		}
		;

		return chunks;
	}

	@Override
	public String getStatusString(int dim, int bX, int bY, int bZ)
	{
		return "";
	}

	@Override
	public void onMiddleClick(int dim, int bX, int bZ, MapView mapview)
	{
	}

	@Override
	public void onDimensionChanged(int dimension, MapView mapview)
	{
	}

	@Override
	public void onMapCenterChanged(double vX, double vZ, MapView mapview)
	{

	}

	@Override
	public void onZoomChanged(int level, MapView mapview)
	{

	}

	@Override
	public void onOverlayActivated(MapView mapview)
	{

	}

	@Override
	public void onOverlayDeactivated(MapView mapview)
	{

	}

	@Override
	public void onDraw(MapView mapview, MapMode mapmode)
	{

	}

	@Override
	public boolean onMouseInput(MapView mapview, MapMode mapmode)
	{

		return false;
	}

}
