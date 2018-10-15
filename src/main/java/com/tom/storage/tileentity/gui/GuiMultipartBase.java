package com.tom.storage.tileentity.gui;

import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import com.tom.api.gui.GuiTomsLib;
import com.tom.api.multipart.IGuiMultipart;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.IPartSlot;

public class GuiMultipartBase extends GuiTomsLib {
	protected IGuiMultipart te;

	public GuiMultipartBase(Container inv, String guiTexture, IGuiMultipart te) {
		super(inv, guiTexture);
		this.te = te;
	}

	@Override
	public void updateScreen() {
		if (te instanceof IGuiMultipart) {
			IPartSlot p = te.getPosition();
			IMultipartContainer c = MultipartHelper.getContainer(te.getWorld2(), te.getPos2()).orElse(null);
			if (c == null) {
				TileEntity t = te.getWorld2().getTileEntity(te.getPos2());
				if (t instanceof IGuiMultipart)
					te = (IGuiMultipart) t;
				else {
					mc.player.closeScreen();
					return;
				}
			}
			IPartInfo i = c.get(p).orElse(null);
			if (i != null && i.getTile() != null && i.getTile() instanceof IGuiMultipart)
				;/*te = (IGuiMultipart) i.getTile();*/
			else {
				mc.player.closeScreen();
				return;
			}
		}
		super.updateScreen();
	}
}
