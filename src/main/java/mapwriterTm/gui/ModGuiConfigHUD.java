package mapwriterTm.gui;

import java.awt.Point;
import java.io.IOException;
import java.util.List;

import mapwriterTm.Mw;
import mapwriterTm.config.MapModeConfig;
import mapwriterTm.config.largeMapModeConfig;
import mapwriterTm.config.smallMapModeConfig;
import mapwriterTm.gui.ModGuiConfig.ModNumberSliderEntry;
import mapwriterTm.map.MapRenderer;
import mapwriterTm.map.mapmode.MapMode;
import mapwriterTm.util.Reference;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ModGuiConfigHUD extends GuiConfig
{
	private Mw mw;
	public MapMode mapMode;
	private MapRenderer map;

	private Boolean DraggingMap = false;

	protected GuiButtonExt btnTopLeft;
	protected GuiButtonExt btnTopRight;
	protected GuiButtonExt btnBottomLeft;
	protected GuiButtonExt btnBottomRight;
	protected GuiButtonExt btnCenterTop;
	protected GuiButtonExt btnCenterBottom;
	protected GuiButtonExt btnCenterLeft;
	protected GuiButtonExt btnCenterRight;
	protected GuiButtonExt btnCenter;

	private MapModeConfig dummyMapConfig;

	public ModGuiConfigHUD(GuiScreen parentScreen, List<IConfigElement> configElements,
			String modID, String configID, boolean allRequireWorldRestart,
			boolean allRequireMcRestart, String title, String Config)
	{
		super(
				parentScreen,
				configElements,
				modID,
				configID,
				allRequireWorldRestart,
				allRequireMcRestart,
				title,
				"Use right click and hold to move the map");

		if (Config.equals(Reference.catFullMapConfig))
		{
			dummyMapConfig = new MapModeConfig(Reference.catFullMapConfig);
		}
		else if (Config.equals(Reference.catLargeMapConfig))
		{
			dummyMapConfig = new largeMapModeConfig(Reference.catLargeMapConfig);
		}
		else if (Config.equals(Reference.catSmallMapConfig))
		{
			dummyMapConfig = new smallMapModeConfig(Reference.catSmallMapConfig);
		}
		dummyMapConfig.setDefaults();
		dummyMapConfig.loadConfig();

		this.mw = Mw.getInstance();
		this.mapMode = new MapMode(dummyMapConfig);
		this.map = new MapRenderer(this.mw, this.mapMode, null);
	}

	@Override
	public void initGui()
	{
		super.initGui();
		int topLeftWidth = Math.max(
				mc.fontRendererObj.getStringWidth(
						I18n.format("mw.config.map.ctgy.position.topleft")) + 20,
				100);

		int topRightWidth = Math.max(
				mc.fontRendererObj.getStringWidth(
						I18n.format("mw.config.map.ctgy.position.topright")) + 20,
				100);

		int bottomLeftWidth = Math.max(
				mc.fontRendererObj.getStringWidth(
						I18n.format("mw.config.map.ctgy.position.botleft")) + 20,
				100);

		int bottomRightWidth = Math.max(
				mc.fontRendererObj.getStringWidth(
						I18n.format("mw.config.map.ctgy.position.botright")) + 20,
				100);

		int CenterTopWidth = Math.max(
				mc.fontRendererObj.getStringWidth(
						I18n.format("mw.config.map.ctgy.position.centertop")) + 20,
				100);

		int CenterBottomWidth = Math.max(
				mc.fontRendererObj.getStringWidth(
						I18n.format("mw.config.map.ctgy.position.centerbottom")) + 20,
				100);

		int CenterWidth = Math.max(
				mc.fontRendererObj.getStringWidth(I18n.format("mw.config.map.ctgy.position.center"))
						+ 20,
				100);

		int CenterLeft = Math.max(
				mc.fontRendererObj.getStringWidth(
						I18n.format("mw.config.map.ctgy.position.centerleft")) + 20,
				100);

		int CenterRight = Math.max(
				mc.fontRendererObj.getStringWidth(
						I18n.format("mw.config.map.ctgy.position.centerright")) + 20,
				100);

		int buttonWidthHalf1 = (bottomLeftWidth + 5 + bottomRightWidth + CenterTopWidth + 5) / 2;
		int buttonWidthHalf2 = (CenterLeft + 5 + CenterWidth + CenterRight + 5) / 2;
		int buttonWidthHalf3 = (topLeftWidth + 5 + topRightWidth + CenterBottomWidth + 5) / 2;

		int buttonHeigth1 = this.height - 29 - 29 - 29 - 29;
		int buttonHeigth2 = this.height - 29 - 29 - 29;
		int buttonHeigth3 = this.height - 29 - 29;

		this.buttonList.add(
				new GuiButtonExt(
						3000,
						this.width / 2 - buttonWidthHalf1,
						buttonHeigth1,
						topLeftWidth,
						20,
						I18n.format("mw.config.map.ctgy.position.topleft")));

		this.buttonList.add(
				new GuiButtonExt(
						3001,
						this.width / 2 - buttonWidthHalf1 + topLeftWidth + 5,
						buttonHeigth1,
						CenterTopWidth,
						20,
						I18n.format("mw.config.map.ctgy.position.centertop")));

		this.buttonList.add(
				new GuiButtonExt(
						3002,
						this.width / 2 - buttonWidthHalf1 + topLeftWidth + 5 + CenterTopWidth + 5,
						buttonHeigth1,
						topRightWidth,
						20,
						I18n.format("mw.config.map.ctgy.position.topright")));

		this.buttonList.add(
				new GuiButtonExt(
						3010,
						this.width / 2 - buttonWidthHalf2,
						buttonHeigth2,
						CenterLeft,
						20,
						I18n.format("mw.config.map.ctgy.position.centerleft")));

		this.buttonList.add(
				new GuiButtonExt(
						3011,
						this.width / 2 - buttonWidthHalf2 + CenterLeft + 5,
						buttonHeigth2,
						CenterWidth,
						20,
						I18n.format("mw.config.map.ctgy.position.center")));

		this.buttonList.add(
				new GuiButtonExt(
						3012,
						this.width / 2 - buttonWidthHalf2 + CenterLeft + 5 + CenterWidth + 5,
						buttonHeigth2,
						CenterRight,
						20,
						I18n.format("mw.config.map.ctgy.position.centerright")));

		this.buttonList.add(
				new GuiButtonExt(
						3020,
						this.width / 2 - buttonWidthHalf3,
						buttonHeigth3,
						bottomLeftWidth,
						20,
						I18n.format("mw.config.map.ctgy.position.botleft")));

		this.buttonList.add(
				new GuiButtonExt(
						3021,
						this.width / 2 - buttonWidthHalf3 + bottomLeftWidth + 5,
						buttonHeigth3,
						CenterBottomWidth,
						20,
						I18n.format("mw.config.map.ctgy.position.centerbottom")));

		this.buttonList.add(
				new GuiButtonExt(
						3022,
						this.width / 2 - buttonWidthHalf3 + bottomLeftWidth + 5 + CenterBottomWidth
								+ 5,
						buttonHeigth3,
						bottomRightWidth,
						20,
						I18n.format("mw.config.map.ctgy.position.botright")));

		UpdateParrentSettings();
	}

	protected void actionPerformed(GuiButton button)
	{
		double bottomOffset = 0;
		if (!this.mapMode.config.biomeMode.equals(MapModeConfig.coordsModeStringArray[0]))
		{
			bottomOffset = bottomOffset + mc.fontRendererObj.FONT_HEIGHT + 3;
		}
		if (!this.mapMode.config.biomeMode.equals(MapModeConfig.coordsModeStringArray[0]))
		{
			bottomOffset = bottomOffset + mc.fontRendererObj.FONT_HEIGHT + 3;
		}
		bottomOffset = (bottomOffset / this.height) * 100;

		double SmallMarginY = (10.00 / (double) (this.height - this.mapMode.h)) * 100.0;
		double SmallMarginX = (10.00 / (double) (this.width - this.mapMode.w)) * 100.0;
		double LargeMarginBottom = (40.00 / (double) (this.height - this.mapMode.h)) * 100.0;

		bottomOffset = bottomOffset < SmallMarginY ? SmallMarginY : bottomOffset;
		// top left
		if (button.id == 3000)
		{
			updateMap(new Point.Double(SmallMarginX, SmallMarginY));
		}
		// top center
		else if (button.id == 3001)
		{
			updateMap(new Point.Double(50, SmallMarginY));
		}
		// top right
		else if (button.id == 3002)
		{
			updateMap(new Point.Double(100 - SmallMarginX, SmallMarginY));
		}
		// center left
		else if (button.id == 3010)
		{
			updateMap(new Point.Double(SmallMarginX, 50));
		}
		// center
		else if (button.id == 3011)
		{
			updateMap(new Point.Double(50, 50));
		}
		// center right
		else if (button.id == 3012)
		{
			updateMap(new Point.Double(100 - SmallMarginX, 50));
		}
		// bottom left
		else if (button.id == 3020)
		{
			updateMap(new Point.Double(SmallMarginX, 100 - bottomOffset));
		}
		// bottom center
		else if (button.id == 3021)
		{
			updateMap(new Point.Double(50, 100 - LargeMarginBottom));
		}
		// bottom right
		else if (button.id == 3022)
		{
			updateMap(new Point.Double(100 - SmallMarginX, 100 - bottomOffset));
		}
		else
		{
			super.actionPerformed(button);
		}
	}

	// mouse button clicked. 0 = LMB, 1 = RMB, 2 = MMB
	@Override
	public void mouseClicked(int x, int y, int mouseEvent) throws IOException
	{
		if (mouseEvent != 1 || !this.mapMode.posWithin(x, y))
		{
			// this.entryList.mouseClickedPassThru(x, y, mouseEvent);
			super.mouseClicked(x, y, mouseEvent);
		}
		else
		{
			DraggingMap = true;
		}
	}

	@Override
	protected void mouseClickMove(int x, int y, int mouseEvent, long timeSinceLastClick)
	{
		if (DraggingMap)
		{
			updateMap(this.mapMode.getNewPosPoint(x, y));
		}
		else

		{
			super.mouseClickMove(x, y, mouseEvent, timeSinceLastClick);
		}

	}

	@Override
	public void mouseReleased(int x, int y, int mouseEvent)
	{
		if (DraggingMap)
		{
			DraggingMap = false;
		}
		else
		{
			super.mouseReleased(x, y, mouseEvent);
		}
	}

	// also called every frame
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		UpdateButtonValues();

		// draw the map
		this.map.drawDummy();
	}

	private void UpdateButtonValues()
	{
		for (IConfigEntry entry : this.entryList.listEntries)
		{
			if (entry.getName().equals("xPos"))
			{
				dummyMapConfig.xPos = (Double) entry.getCurrentValue();
			}
			else if (entry.getName().equals("yPos"))
			{
				dummyMapConfig.yPos = (Double) entry.getCurrentValue();
			}
			else if (entry.getName().equals("heightPercent"))
			{
				dummyMapConfig.heightPercent = (Double) entry.getCurrentValue();
			}
			else if (entry.getName().equals("widthPercent"))
			{
				dummyMapConfig.widthPercent = (Double) entry.getCurrentValue();
				if (this.mapMode.config.circular)
				{
					((ModNumberSliderEntry) entry).setEnabled(false);
				}
				else
				{
					((ModNumberSliderEntry) entry).setEnabled(true);
				}
			}
		}
	}

	private void UpdateParrentSettings()
	{
		if (this.parentScreen != null && this.parentScreen instanceof GuiConfig)
		{
			GuiConfig parrent = (GuiConfig) this.parentScreen;

			if (parrent.entryList != null && parrent.entryList.listEntries != null)
			{
				for (IConfigEntry entry : parrent.entryList.listEntries)
				{
					if (entry.getName().equals("circular"))
					{
						dummyMapConfig.circular = (Boolean) entry.getCurrentValue();
					}
					else if (entry.getName().equals("coordsMode"))
					{
						dummyMapConfig.coordsMode = (String) entry.getCurrentValue();
					}
					else if (entry.getName().equals("borderMode"))
					{
						dummyMapConfig.borderMode = (Boolean) entry.getCurrentValue();
					}
					else if (entry.getName().equals("playerArrowSize"))
					{
						dummyMapConfig.playerArrowSize = Integer.valueOf(
								(String) entry.getCurrentValue());
					}
					else if (entry.getName().equals("biomeMode"))
					{
						dummyMapConfig.biomeMode = (String) entry.getCurrentValue();
					}
				}
			}
		}
	}

	private void updateMap(Point.Double point)
	{
		for (IConfigEntry entry : this.entryList.listEntries)
		{
			if (entry instanceof ModNumberSliderEntry)
			{
				if (entry.getName().equals("xPos"))
				{
					((ModNumberSliderEntry) entry).setValue(point.getX());
				}
				else if (entry.getName().equals("yPos"))
				{
					((ModNumberSliderEntry) entry).setValue(point.getY());
				}
			}
		}
	}

	public static class MapPosConfigEntry extends CategoryEntry
	{
		public MapPosConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList,
				IConfigElement prop)
		{
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen()
		{
			String QualifiedName = this.configElement.getQualifiedName();
			String config = QualifiedName
					.substring(0, QualifiedName.indexOf(Configuration.CATEGORY_SPLITTER))
					.replace(Configuration.CATEGORY_SPLITTER, "");

			return new ModGuiConfigHUD(
					this.owningScreen,
					this.getConfigElement().getChildElements(),
					this.owningScreen.modID,
					null,
					this.configElement.requiresWorldRestart()
							|| this.owningScreen.allRequireWorldRestart,
					this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
					this.owningScreen.title,
					config);
		}
	}
}
