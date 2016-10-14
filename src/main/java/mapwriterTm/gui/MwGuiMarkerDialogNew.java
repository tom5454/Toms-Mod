package mapwriterTm.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import mapwriterTm.api.MwAPI;
import mapwriterTm.map.Marker;
import mapwriterTm.map.Marker.RenderType;
import mapwriterTm.map.MarkerManager;
import mapwriterTm.util.Utils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import net.minecraftforge.fml.relauncher.Side;

@net.minecraftforge.fml.relauncher.SideOnly(Side.CLIENT)
public class MwGuiMarkerDialogNew extends GuiScreen implements Runnable
{
	private final GuiScreen parentScreen;
	String title = "";
	String titleNew = "mw.gui.mwguimarkerdialognew.title.new";
	String titleEdit = "mw.gui.mwguimarkerdialognew.title.edit";
	private String editMarkerName = "mw.gui.mwguimarkerdialognew.editMarkerName";
	private String editMarkerIcon = "tomsmod.map.markerIcon";
	private String editMarkerGroup = "mw.gui.mwguimarkerdialognew.editMarkerGroup";
	private String editMarkerX = "mw.gui.mwguimarkerdialognew.editMarkerX";
	private String editMarkerY = "mw.gui.mwguimarkerdialognew.editMarkerY";
	private String editMarkerZ = "mw.gui.mwguimarkerdialognew.editMarkerZ";
	private String editMarkerColor = "mw.gui.mwguimarkerdialognew.editMarkerColor";
	private String editMarkerLabelType = "tomsmod.map.labelType";
	private String editMarkerBeamType = "tomsmod.map.beamType";
	ScrollableTextBox scrollableTextBoxName = null;
	ScrollableTextBox scrollableTextBoxGroup = null;
	ScrollableTextBox scrollableTextBoxIcon = null;
	ScrollableNumericTextBox scrollableNumericTextBoxX = null;
	ScrollableNumericTextBox scrollableNumericTextBoxY = null;
	ScrollableNumericTextBox scrollableNumericTextBoxZ = null;
	ScrollableColorSelector ScrollableColorSelectorColor = null;
	ScrollableRenderTypeSelector scrollableRenderTypeSelectorLabel = null;
	ScrollableRenderTypeSelector scrollableRenderTypeSelectorBeam = null;
	boolean backToGameOnSubmit = false;
	static final int dialogWidthPercent = 50;
	static final int elementVSpacing = 20;
	static final int normalNumberOfElements = 11;
	static final int extendedNumberOfElements = 12;
	private int numberOfElements = normalNumberOfElements;
	private final MarkerManager markerManager;
	private Marker editingMarker;
	private String markerName = "";
	private String markerGroup = "", markerIcon, beamIcon = "normal";
	private int markerX = 0;
	private int markerY = 80;
	private int markerZ = 0;
	private int dimension = 0;
	private int colour = 0;
	private RenderType labelType, beamType;

	public MwGuiMarkerDialogNew(GuiScreen parentScreen, MarkerManager markerManager, String markerName, String markerGroup, int x, int y, int z, int dimension)
	{
		this.markerManager = markerManager;
		this.markerName = markerName;
		this.markerGroup = markerGroup;
		this.markerX = x;
		this.markerY = y;
		this.markerZ = z;
		this.dimension = dimension;
		this.colour = Utils.getCurrentColour();
		this.editingMarker = null;
		this.parentScreen = parentScreen;
		this.title = this.titleNew;
		this.markerIcon = "normal";
		this.beamType = RenderType.NORMAL;
		this.labelType = RenderType.NORMAL;
	}

	public MwGuiMarkerDialogNew(GuiScreen parentScreen, MarkerManager markerManager, Marker editingMarker)
	{
		this.markerManager = markerManager;
		this.editingMarker = editingMarker;
		this.markerName = editingMarker.name;
		this.markerGroup = editingMarker.groupName;
		this.markerX = editingMarker.x;
		this.markerY = editingMarker.y;
		this.markerZ = editingMarker.z;
		this.dimension = editingMarker.dimension;
		this.colour = editingMarker.color;
		this.parentScreen = parentScreen;
		this.title = this.titleEdit;
		this.beamType = editingMarker.beamType;
		this.labelType = editingMarker.labelType;
		this.markerIcon = editingMarker.iconLocation;
		this.beamIcon = editingMarker.beamIconLocation;
	}

	public boolean submit()
	{
		boolean inputCorrect = true;

		if (this.scrollableTextBoxName.validateTextFieldData())
		{
			this.markerName = this.scrollableTextBoxName.getText();
		}
		else
		{
			inputCorrect = false;
		}
		if (this.scrollableTextBoxIcon.validateTextFieldData())
		{
			this.markerIcon = this.scrollableTextBoxIcon.getText();
		}
		else
		{
			inputCorrect = false;
		}

		if (this.scrollableTextBoxGroup.validateTextFieldData())
		{
			this.markerGroup = this.scrollableTextBoxGroup.getText();
		}
		else
		{
			inputCorrect = false;
		}

		if (this.scrollableNumericTextBoxX.validateTextFieldData())
		{
			this.markerX = this.scrollableNumericTextBoxX.getTextFieldIntValue();
		}
		else
		{
			inputCorrect = false;
		}

		if (this.scrollableNumericTextBoxY.validateTextFieldData())
		{
			this.markerY = this.scrollableNumericTextBoxY.getTextFieldIntValue();
		}
		else
		{
			inputCorrect = false;
		}

		if (this.scrollableNumericTextBoxZ.validateTextFieldData())
		{
			this.markerZ = this.scrollableNumericTextBoxZ.getTextFieldIntValue();
		}
		else
		{
			inputCorrect = false;
		}

		if (this.ScrollableColorSelectorColor.validateColorData())
		{
			this.colour = this.ScrollableColorSelectorColor.getColor();
		}
		else
		{
			inputCorrect = false;
		}

		if (this.scrollableRenderTypeSelectorBeam.validateTextFieldData())
		{
			this.beamType = this.scrollableRenderTypeSelectorBeam.type;
			this.beamIcon = this.scrollableRenderTypeSelectorBeam.getExtraText();
		}
		else
		{
			inputCorrect = false;
		}
		labelType = scrollableRenderTypeSelectorLabel.type;
		if (inputCorrect)
		{
			if (this.editingMarker != null)
			{
				this.markerManager.delMarker(this.editingMarker);
				this.editingMarker = null;
			}
			this.markerManager.addMarker(this.markerName, this.markerGroup, this.markerX, this.markerY, this.markerZ, this.dimension, markerIcon, this.colour, beamType, labelType, true, this.beamIcon);
			this.markerManager.setVisibleGroupName(this.markerGroup);
			this.markerManager.update();
		}
		return inputCorrect;
	}

	@Override
	public void initGui()
	{
		if(beamType == RenderType.ICON){
			numberOfElements = extendedNumberOfElements;
		}else{
			numberOfElements = normalNumberOfElements;
		}
		int labelsWidth = this.fontRendererObj.getStringWidth(I18n.format(this.editMarkerIcon));
		int width = ((this.width * dialogWidthPercent) / 100) - labelsWidth - 20;
		int x = ((this.width - width) + labelsWidth) / 2;
		int y = (this.height - (elementVSpacing * numberOfElements)) / 2;

		this.scrollableTextBoxName = new ScrollableTextBox(x, y, width, I18n.format(this.editMarkerName), this.fontRendererObj);
		this.scrollableTextBoxName.setFocused(true);
		this.scrollableTextBoxName.setText(this.markerName);

		this.scrollableTextBoxGroup = new ScrollableTextBox(x, y + MwGuiMarkerDialogNew.elementVSpacing, width, I18n.format(this.editMarkerGroup), this.markerManager.groupList, this.fontRendererObj);
		this.scrollableTextBoxGroup.setText(this.markerGroup);
		this.scrollableTextBoxGroup.setDrawArrows(true);

		this.scrollableNumericTextBoxX = new ScrollableNumericTextBox(x, y + (MwGuiMarkerDialogNew.elementVSpacing * 2), width, I18n.format(this.editMarkerX), this.fontRendererObj);
		this.scrollableNumericTextBoxX.setText("" + this.markerX);
		this.scrollableNumericTextBoxX.setDrawArrows(true);

		this.scrollableNumericTextBoxY = new ScrollableNumericTextBox(x, y + (MwGuiMarkerDialogNew.elementVSpacing * 3), width, I18n.format(this.editMarkerY), this.fontRendererObj);
		this.scrollableNumericTextBoxY.setText("" + this.markerY);
		this.scrollableNumericTextBoxY.setDrawArrows(true);

		this.scrollableNumericTextBoxZ = new ScrollableNumericTextBox(x, y + (MwGuiMarkerDialogNew.elementVSpacing * 4), width, I18n.format(this.editMarkerZ), this.fontRendererObj);
		this.scrollableNumericTextBoxZ.setText("" + this.markerZ);
		this.scrollableNumericTextBoxZ.setDrawArrows(true);

		this.ScrollableColorSelectorColor = new ScrollableColorSelector(x, y + (MwGuiMarkerDialogNew.elementVSpacing * 5), width, I18n.format(this.editMarkerColor), this.fontRendererObj);
		this.ScrollableColorSelectorColor.setColor(this.colour);
		this.ScrollableColorSelectorColor.setDrawArrows(true);

		this.scrollableTextBoxIcon = new ScrollableTextBox(x, y + (MwGuiMarkerDialogNew.elementVSpacing * 8), width, I18n.format(this.editMarkerIcon), this.fontRendererObj);
		this.scrollableTextBoxIcon.setText(markerIcon);

		this.scrollableRenderTypeSelectorLabel = new ScrollableRenderTypeSelector(x, y + (MwGuiMarkerDialogNew.elementVSpacing * 9), width, I18n.format(this.editMarkerLabelType), this.fontRendererObj, false, null);
		this.scrollableRenderTypeSelectorLabel.type = labelType;

		this.scrollableRenderTypeSelectorBeam = new ScrollableRenderTypeSelector(x, y + (MwGuiMarkerDialogNew.elementVSpacing * 10), width, I18n.format(this.editMarkerBeamType), this.fontRendererObj, true, this);
		this.scrollableRenderTypeSelectorBeam.type = beamType;
		this.scrollableRenderTypeSelectorBeam.setExtraText(beamIcon);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f)
	{
		if (this.parentScreen != null)
		{
			this.parentScreen.drawScreen(mouseX, mouseY, f);
		}
		else
		{
			this.drawDefaultBackground();
		}

		int w = (this.width * MwGuiMarkerDialogNew.dialogWidthPercent) / 100;
		drawRect((this.width - w) / 2, ((this.height - (MwGuiMarkerDialogNew.elementVSpacing * (numberOfElements + 2))) / 2) - 4, ((this.width - w) / 2) + w, ((this.height - (MwGuiMarkerDialogNew.elementVSpacing * (numberOfElements + 2))) / 2)
				+ (MwGuiMarkerDialogNew.elementVSpacing * (numberOfElements + 1)), 0x80000000);
		this.drawCenteredString(this.fontRendererObj, I18n.format(this.title), (this.width) / 2, ((this.height - (MwGuiMarkerDialogNew.elementVSpacing * (numberOfElements + 1))) / 2) - (MwGuiMarkerDialogNew.elementVSpacing / 4), 0xffffff);
		this.scrollableTextBoxName.draw();
		this.scrollableTextBoxGroup.draw();
		this.scrollableNumericTextBoxX.draw();
		this.scrollableNumericTextBoxY.draw();
		this.scrollableNumericTextBoxZ.draw();
		this.ScrollableColorSelectorColor.draw();
		this.scrollableTextBoxIcon.draw();
		this.scrollableRenderTypeSelectorLabel.draw();
		this.scrollableRenderTypeSelectorBeam.draw();
		super.drawScreen(mouseX, mouseY, f);
	}

	// override GuiScreen's handleMouseInput to process
	// the scroll wheel.
	@Override
	public void handleMouseInput() throws IOException
	{
		if (MwAPI.getCurrentDataProvider() != null)
		{
			return;
		}
		int x = (Mouse.getEventX() * this.width) / this.mc.displayWidth;
		int y = this.height - ((Mouse.getEventY() * this.height) / this.mc.displayHeight) - 1;
		int direction = Mouse.getEventDWheel();
		if (direction != 0)
		{
			this.mouseDWheelScrolled(x, y, direction);
		}
		super.handleMouseInput();
	}

	public void mouseDWheelScrolled(int x, int y, int direction)
	{
		this.scrollableTextBoxName.mouseDWheelScrolled(x, y, direction);
		this.scrollableTextBoxGroup.mouseDWheelScrolled(x, y, direction);
		this.scrollableNumericTextBoxX.mouseDWheelScrolled(x, y, direction);
		this.scrollableNumericTextBoxY.mouseDWheelScrolled(x, y, direction);
		this.scrollableNumericTextBoxZ.mouseDWheelScrolled(x, y, direction);
		this.ScrollableColorSelectorColor.mouseDWheelScrolled(x, y, direction);
		this.scrollableTextBoxIcon.mouseDWheelScrolled(x, y, direction);
		this.scrollableRenderTypeSelectorBeam.mouseDWheelScrolled(x, y, direction);
		this.scrollableRenderTypeSelectorLabel.mouseDWheelScrolled(x, y, direction);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException
	{
		super.mouseClicked(x, y, button);

		this.scrollableTextBoxName.mouseClicked(x, y, button);
		this.scrollableTextBoxGroup.mouseClicked(x, y, button);
		this.scrollableNumericTextBoxX.mouseClicked(x, y, button);
		this.scrollableNumericTextBoxY.mouseClicked(x, y, button);
		this.scrollableNumericTextBoxZ.mouseClicked(x, y, button);
		this.ScrollableColorSelectorColor.mouseClicked(x, y, button);
		this.scrollableTextBoxIcon.mouseClicked(x, y, button);
		this.scrollableRenderTypeSelectorLabel.mouseClicked(x, y, button);
		this.scrollableRenderTypeSelectorBeam.mouseClicked(x, y, button);
	}

	@Override
	protected void keyTyped(char c, int key)
	{
		switch (key)
		{
		case Keyboard.KEY_ESCAPE:
			this.mc.displayGuiScreen(this.parentScreen);
			break;
		case Keyboard.KEY_RETURN:
			// when enter pressed, submit current input
			if (this.submit())
			{
				if (!this.backToGameOnSubmit)
				{
					this.mc.displayGuiScreen(this.parentScreen);
				}
				else
				{
					this.mc.displayGuiScreen(null);
				}
			}
			break;
		case Keyboard.KEY_TAB:
			ScrollableField thisField = null;
			ScrollableField prevField = null;
			ScrollableField nextField = null;

			if (this.scrollableTextBoxName.isFocused())
			{
				thisField = this.scrollableTextBoxName;
				prevField = this.ScrollableColorSelectorColor;
				nextField = this.scrollableTextBoxGroup;
			}
			else if (this.scrollableTextBoxGroup.isFocused())
			{
				thisField = this.scrollableTextBoxGroup;
				prevField = this.scrollableTextBoxName;
				nextField = this.scrollableNumericTextBoxX;
			}
			else if (this.scrollableNumericTextBoxX.isFocused())
			{
				thisField = this.scrollableNumericTextBoxX;
				prevField = this.scrollableTextBoxGroup;
				nextField = this.scrollableNumericTextBoxY;
			}
			else if (this.scrollableNumericTextBoxY.isFocused())
			{
				thisField = this.scrollableNumericTextBoxY;
				prevField = this.scrollableNumericTextBoxX;
				nextField = this.scrollableNumericTextBoxZ;
			}
			else if (this.scrollableNumericTextBoxZ.isFocused())
			{
				thisField = this.scrollableNumericTextBoxZ;
				prevField = this.scrollableNumericTextBoxY;
				nextField = this.ScrollableColorSelectorColor;
			}
			else if (this.ScrollableColorSelectorColor.isFocused())
			{
				thisField = this.ScrollableColorSelectorColor.thisField();
				nextField = this.ScrollableColorSelectorColor.nextField(this.scrollableTextBoxIcon);
				prevField = this.ScrollableColorSelectorColor.prevField(this.scrollableNumericTextBoxZ);
			}
			else if (this.scrollableTextBoxIcon.isFocused())
			{
				thisField = this.scrollableTextBoxIcon;
				prevField = this.ScrollableColorSelectorColor;
				nextField = this.scrollableRenderTypeSelectorLabel;
			}
			else if (this.scrollableRenderTypeSelectorLabel.isFocused())
			{
				thisField = this.scrollableRenderTypeSelectorLabel;
				prevField = this.scrollableTextBoxIcon;
				nextField = this.scrollableRenderTypeSelectorBeam;
			}
			else if (this.scrollableRenderTypeSelectorBeam.isFocused())
			{
				thisField = this.scrollableRenderTypeSelectorBeam.thisField();
				prevField = this.scrollableRenderTypeSelectorBeam.prev(scrollableRenderTypeSelectorLabel);
				nextField = this.scrollableRenderTypeSelectorBeam.next(scrollableTextBoxName);
			}

			thisField.setFocused(false);

			if (thisField instanceof ScrollableTextBox)
			{
				((ScrollableTextBox) thisField).setCursorPositionEnd();
			}
			if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))
			{
				prevField.setFocused(true);
			}
			else
			{
				nextField.setFocused(true);
			}

			break;
		default:
			this.scrollableTextBoxName.KeyTyped(c, key);
			this.scrollableTextBoxGroup.KeyTyped(c, key);
			this.scrollableNumericTextBoxX.KeyTyped(c, key);
			this.scrollableNumericTextBoxY.KeyTyped(c, key);
			this.scrollableNumericTextBoxZ.KeyTyped(c, key);
			this.ScrollableColorSelectorColor.KeyTyped(c, key);
			this.scrollableTextBoxIcon.KeyTyped(c, key);
			this.scrollableRenderTypeSelectorBeam.KeyTyped(c, key);
			break;
		}
	}

	@Override
	public void run() {
		if(scrollableRenderTypeSelectorBeam.type == RenderType.ICON){
			beamType = RenderType.ICON;
			if (this.scrollableTextBoxName.validateTextFieldData())
			{
				this.markerName = this.scrollableTextBoxName.getText();
			}
			if (this.scrollableTextBoxIcon.validateTextFieldData())
			{
				this.markerIcon = this.scrollableTextBoxIcon.getText();
			}

			if (this.scrollableTextBoxGroup.validateTextFieldData())
			{
				this.markerGroup = this.scrollableTextBoxGroup.getText();
			}

			if (this.scrollableNumericTextBoxX.validateTextFieldData())
			{
				this.markerX = this.scrollableNumericTextBoxX.getTextFieldIntValue();
			}

			if (this.scrollableNumericTextBoxY.validateTextFieldData())
			{
				this.markerY = this.scrollableNumericTextBoxY.getTextFieldIntValue();
			}

			if (this.scrollableNumericTextBoxZ.validateTextFieldData())
			{
				this.markerZ = this.scrollableNumericTextBoxZ.getTextFieldIntValue();
			}

			if (this.ScrollableColorSelectorColor.validateColorData())
			{
				this.colour = this.ScrollableColorSelectorColor.getColor();
			}

			if (this.scrollableRenderTypeSelectorBeam.validateTextFieldData())
			{
				this.beamType = this.scrollableRenderTypeSelectorBeam.type;
				this.beamIcon = this.scrollableRenderTypeSelectorBeam.getExtraText();
			}
			labelType = scrollableRenderTypeSelectorLabel.type;
			initGui();
		}else{
			if(beamType == RenderType.ICON){
				beamType = scrollableRenderTypeSelectorBeam.type;

				if (this.scrollableTextBoxName.validateTextFieldData())
				{
					this.markerName = this.scrollableTextBoxName.getText();
				}
				if (this.scrollableTextBoxIcon.validateTextFieldData())
				{
					this.markerIcon = this.scrollableTextBoxIcon.getText();
				}

				if (this.scrollableTextBoxGroup.validateTextFieldData())
				{
					this.markerGroup = this.scrollableTextBoxGroup.getText();
				}

				if (this.scrollableNumericTextBoxX.validateTextFieldData())
				{
					this.markerX = this.scrollableNumericTextBoxX.getTextFieldIntValue();
				}

				if (this.scrollableNumericTextBoxY.validateTextFieldData())
				{
					this.markerY = this.scrollableNumericTextBoxY.getTextFieldIntValue();
				}

				if (this.scrollableNumericTextBoxZ.validateTextFieldData())
				{
					this.markerZ = this.scrollableNumericTextBoxZ.getTextFieldIntValue();
				}

				if (this.ScrollableColorSelectorColor.validateColorData())
				{
					this.colour = this.ScrollableColorSelectorColor.getColor();
				}

				if (this.scrollableRenderTypeSelectorBeam.validateTextFieldData())
				{
					this.beamType = this.scrollableRenderTypeSelectorBeam.type;
					this.beamIcon = this.scrollableRenderTypeSelectorBeam.getExtraText();
				}
				labelType = scrollableRenderTypeSelectorLabel.type;
				initGui();
			}
		}
	}
}