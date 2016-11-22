package com.tom.core.transformers;

import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.world.storage.WorldSummary;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.apis.TMLogger;
import com.tom.lib.Keys;

@SideOnly(Side.CLIENT)
public class GuiWorldSelectionTM extends GuiWorldSelection
{
	private GuiListWorldSelectionEntry entry;
	public GuiWorldSelectionTM(GuiScreen screenIn) {
		super(screenIn);
	}
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(entry != null && Keys.CONFIG.isActiveAndMatches(keyCode)){
			GuiListWorldProperties p = getSelected();
			if(p != null){
				System.out.println("Pressed configure on " + p.summary.getDisplayName());
			}
		}else
			super.keyTyped(typedChar, keyCode);
	}
	@Override
	public void selectWorld(GuiListWorldSelectionEntry entry) {
		super.selectWorld(entry);
		this.entry = entry;
	}
	private GuiListWorldProperties getSelected(){
		if(entry != null){
			try{
				Field[] f = entry.getClass().getDeclaredFields();
				WorldSummary summary = null;
				DynamicTexture icon = null;
				for(int i = 0;i<f.length;i++){
					if(f[i] != null){
						f[i].setAccessible(true);
						Object obj = f[i].get(entry);
						if(obj != null){
							if(obj instanceof WorldSummary){
								summary = (WorldSummary) obj;
							}else if(obj instanceof DynamicTexture){
								icon = (DynamicTexture) obj;
							}
						}
					}
				}
				if(summary == null){
					throw new NoSuchFieldException("missing world summary");
				}
				return new GuiListWorldProperties(summary, icon);
			}catch(Exception e){
				TMLogger.error("Failed to reflect world summary.", e);
			}
		}
		return null;
	}
	public static class GuiListWorldProperties{
		public WorldSummary summary;
		public DynamicTexture icon;
		public GuiListWorldProperties(WorldSummary summary, DynamicTexture icon) {
			this.summary = summary;
			this.icon = icon;
		}
	}
}