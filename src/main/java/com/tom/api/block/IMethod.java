package com.tom.api.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@FunctionalInterface
public interface IMethod {
	void exec();

	@FunctionalInterface
	public interface IClientMethod extends IMethod {
		@Override
		@SideOnly(Side.CLIENT)
		void exec();
	}

	@FunctionalInterface
	public interface IServerMethod extends IMethod {
		@Override
		@SideOnly(Side.SERVER)
		void exec();
	}
}
