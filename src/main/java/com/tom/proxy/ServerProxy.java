package com.tom.proxy;

import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;

import com.tom.api.block.IMethod;
import com.tom.api.block.IMethod.IServerMethod;

public class ServerProxy extends CommonProxy {

	@Override
	public void preInit() {

	}

	@Override
	public void init() {

	}

	@Override
	public void postInit() {

	}

	@Override
	public EntityPlayer getClientPlayer() {
		return null;
	}

	@Override
	public void serverStart() {

	}

	@Override
	public void construction() {

	}

	@Override
	public void runMethod(IMethod m) {
		if (m instanceof IServerMethod) {
			m.exec();
		}
	}

	@Override
	public <I, R> R runClientFunction(I i, Function<I, R> m) {
		return null;
	}
}
