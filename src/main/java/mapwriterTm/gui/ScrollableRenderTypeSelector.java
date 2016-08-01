package mapwriterTm.gui;

import mapwriterTm.map.Marker.RenderType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

public class ScrollableRenderTypeSelector extends ScrollableField{
	public RenderType type = RenderType.NORMAL;
	public int textFieldX;
	public int textFieldY;
	public int textFieldWidth;
	private static final int textFieldHeight = 12;
	private boolean focused = false;
	private final ScrollableTextBox boxExtra;
	private final Runnable detector;
	public ScrollableRenderTypeSelector(int x, int y, int width, String label, FontRenderer fontrendererObj, boolean extraBox, Runnable detector) {
		super(x, y, width, label, fontrendererObj);
		setDrawArrows(true);
		this.textFieldX = this.x + ScrollableField.arrowsWidth + 3;
		this.textFieldY = this.y;
		this.textFieldWidth = this.width - 5 - (ScrollableField.arrowsWidth * 2);
		if(extraBox)boxExtra = new ScrollableTextBox(x + 5, y + MwGuiMarkerDialogNew.elementVSpacing, width - 5, I18n.format("tomsmod.map.icon")+":", fontrendererObj);
		else boxExtra = null;
		this.detector = detector;
	}
	public boolean posWithinTextField(int x, int y)
	{
		return (x >= this.textFieldX) && (y >= this.textFieldY) && (x <= (this.textFieldWidth + this.textFieldX)) && (y <= (textFieldHeight + this.textFieldY));
	}
	@Override
	public void nextElement() {
		fieldScroll(1);
	}

	@Override
	public void previousElement() {
		fieldScroll(-1);
	}

	@Override
	public void setFocused(Boolean focus) {
		focused = focus;
	}

	@Override
	public Boolean isFocused() {
		return focused;
	}
	public String getExtraText(){
		return boxExtra != null ? boxExtra.getText() : "normal";
	}
	public void setExtraText(String text){
		if(boxExtra != null)boxExtra.setText(text);
	}
	@Override
	public void draw() {
		super.draw();
		drawRect(this.textFieldX - 1, this.textFieldY - 1, this.textFieldX + this.textFieldWidth + 1, this.textFieldY + textFieldHeight + 1, -6250336);
		drawRect(this.textFieldX, this.textFieldY, this.textFieldX + this.textFieldWidth, this.textFieldY + textFieldHeight, -16777216);
		String text = I18n.format("tomsmod.map."+type.toString());
		fontrendererObj.drawString(text, (this.textFieldX + (this.textFieldWidth / 2)) - (fontrendererObj.getStringWidth(text) / 2), textFieldY + 2, 14737632);
		if(type == RenderType.ICON && boxExtra != null){
			boxExtra.draw();
		}
	}
	@Override
	public void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		if(type == RenderType.ICON && boxExtra != null)this.boxExtra.mouseClicked(x, y, button);
	}

	public void mouseDWheelScrolled(int x, int y, int direction)
	{
		if (this.posWithinTextField(x, y))
		{
			int i = direction > 0 ? 1 : direction < 0 ? -1 : 0;
			this.fieldScroll(i);
		}
	}
	private void fieldScroll(int i) {
		i += type.ordinal();
		type = RenderType.get(i < 0 ? 2 : i);
		if(detector != null)detector.run();
	}
	public ScrollableField next(ScrollableField field){
		if(type == RenderType.ICON && boxExtra != null){
			return boxExtra.isFocused() ? field : boxExtra;
		}
		return field;
	}
	public ScrollableField thisField(){
		if(type == RenderType.ICON && boxExtra != null){
			return boxExtra.isFocused() ? this : boxExtra;
		}
		return this;
	}
	public ScrollableField prev(ScrollableField field){
		if(type == RenderType.ICON && boxExtra != null){
			return boxExtra.isFocused() ? this : field;
		}
		return field;
	}
	public void KeyTyped(char c, int key) {
		if(type == RenderType.ICON && boxExtra != null)boxExtra.KeyTyped(c, key);
	}
	public boolean validateTextFieldData()
	{
		return this.getExtraText().length() > 0;
	}
}
