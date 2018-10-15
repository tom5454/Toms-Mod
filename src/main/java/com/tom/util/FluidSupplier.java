package com.tom.util;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.SoundEvent;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidSupplier implements Supplier<Fluid> {
	private Fluid f;

	public FluidSupplier(Fluid f) {
		this.f = f;
	}

	@Override
	public Fluid get() {
		return f;
	}

	public void update() {
		f = FluidRegistry.getFluid(f.getName());
	}

	public FluidSupplier setLuminosity(int luminosity) {
		f.setLuminosity(luminosity);
		return this;
	}

	public FluidSupplier setDensity(int density) {
		f.setDensity(density);
		return this;
	}

	public FluidSupplier setTemperature(int temperature) {
		f.setTemperature(temperature);
		return this;
	}

	public FluidSupplier setViscosity(int viscosity) {
		f.setViscosity(viscosity);
		return this;
	}

	public FluidSupplier setGaseous(boolean isGaseous) {
		f.setGaseous(isGaseous);
		return this;
	}

	public FluidSupplier setRarity(EnumRarity rarity) {
		f.setRarity(rarity);
		return this;
	}

	public FluidSupplier setFillSound(SoundEvent fillSound) {
		f.setFillSound(fillSound);
		return this;
	}

	public FluidSupplier setEmptySound(SoundEvent emptySound) {
		f.setEmptySound(emptySound);
		return this;
	}

	public Block getBlock() {
		return f.getBlock();
	}
}