package com.tom.api.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IMethod {
	void exec();
	public interface IClientMethod extends IMethod{
		@Override
		@SideOnly(Side.CLIENT)
		void exec();
	}
	public interface IServerMethod extends IMethod{
		@Override
		@SideOnly(Side.SERVER)
		void exec();
	}
}
