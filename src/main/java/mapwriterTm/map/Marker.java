package mapwriterTm.map;

import java.awt.Point;

import org.lwjgl.opengl.GL11;

import mapwriterTm.Mw;
import mapwriterTm.map.mapmode.MapMode;
import mapwriterTm.util.Logging;
import mapwriterTm.util.Render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.config.Config;

public class Marker
{
	public final String name;
	public final String groupName;
	public String iconLocation, beamIconLocation;
	public int x;
	public int y;
	public int z;
	public int dimension;
	public final boolean reloadable;
	public int color;
	public final RenderType beamType;
	public final RenderType labelType;
	public boolean isServerSided = false;

	public Point.Double screenPos = new Point.Double(0, 0);

	public Marker(String name, String groupName, int x, int y, int z, int dimension, String icon, int color, RenderType beamType, RenderType labelType, String beamLoc, boolean reloadable) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimension = dimension;
		this.iconLocation = icon != null ? icon : "normal";
		this.groupName = groupName;
		this.color = color;
		this.reloadable = reloadable;
		this.beamType = beamType;
		this.labelType = labelType;
		this.beamIconLocation = beamLoc != null ? beamLoc : "normal";
	}
	@SideOnly(Side.CLIENT)
	public void draw(MapMode mapMode, MapView mapView, boolean selected)
	{
		boolean isMinimap = mapView.isMinimap();
		EntityPlayer thePlayer = Minecraft.getMinecraft().thePlayer;
		double playerDist = thePlayer.getDistance(x, thePlayer.posY, z);
		boolean shown = isMinimap ? playerDist <= Config.markerUnloadDist : true;
		if(shown || selected){
			double scale = mapView.getDimensionScaling(this.dimension);
			Point.Double p = mapMode.getClampedScreenXY(mapView, this.x * scale, this.z * scale);
			this.screenPos.setLocation(p.x + mapMode.xTranslation, p.y + mapMode.yTranslation);

			// draw a coloured rectangle centered on the calculated (x, y)
			double mSize = mapMode.config.markerSize;
			double halfMSize = mapMode.config.markerSize / 2.0;
			GL11.glPushMatrix();
			//Render.setColour(this.colour);
			//Render.drawRect(p.x - halfMSize + 0.5, p.y - halfMSize + 0.5, mSize - 1.0, mSize - 1.0);
			if(iconLocation != null && !iconLocation.isEmpty() && !iconLocation.equals("") && !iconLocation.equals("normal")){
				ResourceLocation t = new ResourceLocation(iconLocation + ".png");
				Mw.getInstance().mc.renderEngine.bindTexture(t);
				Render.drawTexturedRect(p.x - halfMSize + 0.5, p.y - halfMSize + 0.5, mSize - 1.0, mSize - 1.0);
			}else{
				drawMarkerTexture(p.x - halfMSize + 0.5, p.y - halfMSize + 0.5, mSize - 1.0, mSize - 1.0, color, Render.zDepth);
			}
			if(selected){
				ResourceLocation t = new ResourceLocation("tm:minimap/selection2.png");
				Mw.getInstance().mc.renderEngine.bindTexture(t);
				Render.drawTexturedRect(p.x - halfMSize, p.y - halfMSize, mSize, mSize);
				if(!isMinimap) Render.drawCentredString(MathHelper.floor_double(p.x), MathHelper.floor_double(p.y), 0xFFFFFFFF, ""+MathHelper.floor_double(playerDist));
				Mw.getInstance().selectedDist = playerDist;
				Mw.getInstance().selectedY = y;
			}
			GL11.glPopMatrix();
			/*if(bordered && !selected){
			Render.setColour(this.borderColor);
			Render.drawRect(p.x - halfMSize, p.y - halfMSize, mSize, mSize);
		}else if(borderedA && selected){
			Render.setColour(this.borderColorActive);
			Render.drawRect(p.x - halfMSize, p.y - halfMSize, mSize, mSize);
		}*/
		}
	}

	// arraylist.contains was producing unexpected results in some situations
	// rather than figure out why i'll just control how two markers are compared
	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o instanceof Marker)
		{
			Marker m = (Marker) o;
			return (this.name == m.name) && (this.groupName == m.groupName) && (this.x == m.x) && (this.y == m.y) && (this.z == m.z) && (this.dimension == m.dimension);
		}
		return false;
	}

	public double getDistanceToMarker(Entity entityIn)
	{
		double d0 = this.x - entityIn.posX;
		double d1 = this.y - entityIn.posY;
		double d2 = this.z - entityIn.posZ;
		return MathHelper.sqrt_double((d0 * d0) + (d1 * d1) + (d2 * d2));
	}

	public float getRed()
	{
		return (((color >> 16) & 0xff) / 255.0f);
	}
	public float getGreen()
	{
		return (((color >> 8) & 0xff) / 255.0f);
	}
	public float getBlue()
	{
		return (((color) & 0xff) / 255.0f);
	}
	public static enum RenderType{
		NORMAL("normal"),
		NONE("none"),
		ICON("icon");
		public static final RenderType[] VALUES = values();
		private final String name;
		public static RenderType fromString(String in){
			if(in != null){
				if(in.equalsIgnoreCase("normal")){
					return NORMAL;
				}else if(in.equalsIgnoreCase("none")){
					return NONE;
				}else if(in.equalsIgnoreCase("icon")){
					return ICON;
				}
			}
			return NORMAL;
		}
		private RenderType(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
		public static RenderType get(int index){
			return VALUES[MathHelper.abs_int(index % VALUES.length)];
		}
		public static String[] getStringList(){
			String[] list = new String[VALUES.length];
			for(int i = 0;i<VALUES.length;i++){
				list[i] = VALUES[i].toString();
			}
			return list;
		}
	}
	public static Marker fromNBT(NBTTagCompound tag){
		return new Marker(tag.getString("name"), tag.getString("group"), tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"), tag.getInteger("dim"), tag.getString("icon"), tag.getInteger("color"), RenderType.VALUES[tag.getInteger("beam")], RenderType.VALUES[tag.getInteger("label")], tag.getString("beamLoc"), true);
	}
	public void writeToNBT(NBTTagCompound tag){
		if(reloadable){
			tag.setString("name", name);
			tag.setString("group", groupName);
			tag.setString("icon", iconLocation);
			tag.setInteger("x", x);
			tag.setInteger("y", y);
			tag.setInteger("z", z);
			tag.setInteger("dim", dimension);
			tag.setInteger("color", color);
			tag.setInteger("beam", beamType.ordinal());
			tag.setInteger("label", labelType.ordinal());
			tag.setString("beamLoc", beamIconLocation);
		}else{
			tag.setBoolean("null", true);
		}
	}
	@SideOnly(Side.CLIENT)
	public static void drawMarkerTexture(double x, double y, double w, double h, int color, double zDepth){
		ResourceLocation t = new ResourceLocation("tm:minimap/markerE.png");
		Mw.getInstance().mc.renderEngine.bindTexture(t);
		Render.drawTexturedRect(x,y,w,h);
		t = new ResourceLocation("tm:minimap/markerIn.png");
		Mw.getInstance().mc.renderEngine.bindTexture(t);
		double u1 = 0.0D, v1 = 0.0D, u2 = 1.0D, v2 = 1.0D;
		float fRed = (((color >> 16) & 0xff) / 255.0f);
		float fGreen = (((color >> 8) & 0xff) / 255.0f);
		float fBlue = (((color) & 0xff) / 255.0f);
		float fAlpha = 1.0f;
		try
		{
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX_COLOR );
			vertexbuffer.pos(x + w, y, zDepth).tex(u2, v1).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			vertexbuffer.pos(x, y, zDepth).tex(u1, v1).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			vertexbuffer.pos(x, y + h, zDepth).tex(u1, v2).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			vertexbuffer.pos(x + w, y + h, zDepth).tex(u2, v2).color(fRed, fGreen, fBlue, fAlpha).endVertex();
			tessellator.draw();
			GlStateManager.disableBlend();
		}
		catch (NullPointerException e)
		{
			Logging.log("Marker.drawMarkerTexture: null pointer exception");
		}
	}
	@SideOnly(Side.CLIENT)
	public String[] getMessage() {
		if(isServerSided)
			return new String[]
					{
							name,
							String.format("(%d, %d, %d)", x, y, z),
							I18n.format("tomsmod.map.serverSided")
					};
		else return new String[]
				{
						name,
						String.format("(%d, %d, %d)", x, y, z)
				};
	}
}
