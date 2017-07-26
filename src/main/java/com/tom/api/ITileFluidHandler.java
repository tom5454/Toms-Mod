package com.tom.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerFluidMap;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import com.tom.apis.PredicatedLinkedHashMap;
import com.tom.core.CoreInit.FluidSupplier;

public interface ITileFluidHandler {
	IFluidHandler getTankOnSide(EnumFacing f);

	public static class Helper {
		private static final Predicate<Fluid> ALWAYS_TRUE = new Predicate<Fluid>() {

			@Override
			public boolean apply(Fluid input) {
				return true;
			}
		};

		public static IFluidHandler getFluidHandlerFromTank(FluidTank tank, Fluid fluid, boolean canFill, boolean canDrain) {
			tank.setCanFill(canFill);
			tank.setCanDrain(canDrain);
			FluidHandlerFluidMap m = new FluidHandlerFluidMap();
			m.addHandler(fluid, tank);
			return m;
		}

		public static IFluidHandler getFluidHandlerFromTank(FluidTank tank, FluidSupplier fluid, boolean canFill, boolean canDrain) {
			return getFluidHandlerFromTank(tank, fluid.get(), canFill, canDrain);
		}

		public static IFluidHandler getFluidHandlerFromTanks(FluidTank[] tanks, Fluid[] fluid, boolean[] canFill, boolean[] canDrain) {
			FluidHandlerFluidMap m = new FluidHandlerFluidMap();
			for (int i = 0;i < tanks.length && i < fluid.length && i < canFill.length && i < canDrain.length;i++) {
				tanks[i].setCanDrain(canDrain[i]);
				tanks[i].setCanFill(canFill[i]);
				m.addHandler(fluid[i], tanks[i]);
			}
			return m;
		}

		public static IFluidHandler getFluidHandlerFromTank(FluidTank tank, boolean canFill, boolean canDrain, Fluid... fluids) {
			tank.setCanFill(canFill);
			tank.setCanDrain(canDrain);
			FluidHandlerFluidMap m = new FluidHandlerFluidMap();
			for (Fluid fluid : fluids)
				m.addHandler(fluid, tank);
			return m;
		}

		public static IFluidHandler getFluidHandlerFromTank(FluidTank tank, boolean canFill, boolean canDrain, FluidSupplier... fluids) {
			return getFluidHandlerFromTank(tank, canFill, canDrain, Arrays.stream(fluids).map(FluidSupplier::get).collect(Collectors.toList()).toArray(new Fluid[0]));
		}

		public static IFluidHandler getFluidHandlerFromTank(FluidTank tank, Predicate<Fluid> fluid, boolean canFill, boolean canDrain) {
			tank.setCanFill(canFill);
			tank.setCanDrain(canDrain);
			FluidHandlerPredicateMap m = new FluidHandlerPredicateMap();
			m.addHandler(fluid, tank);
			return m;
		}

		@SuppressWarnings("unchecked")
		public static IFluidHandler getFluidHandlerFromTanksWithPredicate(FluidTank[] tanks, Object[] fluid, boolean[] canFill, boolean[] canDrain) {
			FluidHandlerPredicateMap m = new FluidHandlerPredicateMap();
			for (int i = 0;i < tanks.length && i < fluid.length && i < canFill.length && i < canDrain.length;i++) {
				tanks[i].setCanDrain(canDrain[i]);
				tanks[i].setCanFill(canFill[i]);
				final Object obj = fluid[i];
				if (obj == null) {
					m.addHandler(ALWAYS_TRUE, tanks[i]);
				} else if (obj instanceof Object[]) {
					Object[] objA = (Object[]) obj;
					for (int j = 0;j < objA.length;j++) {
						final Object obj2 = objA[j];
						if (obj2 instanceof Predicate) {
							m.addHandler((Predicate<Fluid>) obj2, tanks[i]);
						} else {
							m.addHandler(new Predicate<Fluid>() {

								@Override
								public boolean apply(Fluid input) {
									return obj2.equals(input);
								}
							}, tanks[i]);
						}
					}
				} else if (obj instanceof Predicate) {
					m.addHandler((Predicate<Fluid>) obj, tanks[i]);
				} else if (obj instanceof FluidSupplier) {
					final FluidSupplier s = (FluidSupplier) obj;
					m.addHandler(new Predicate<Fluid>() {

						@Override
						public boolean apply(Fluid input) {
							return s.get().equals(input);
						}
					}, tanks[i]);
				} else {
					m.addHandler(new Predicate<Fluid>() {

						@Override
						public boolean apply(Fluid input) {
							return obj.equals(input);
						}
					}, tanks[i]);
				}
			}
			return m;
		}
	}

	public static class FluidHandlerPredicateMap implements IFluidHandler {
		protected final Map<Predicate<Fluid>, IFluidHandler> handlers;

		public FluidHandlerPredicateMap() {
			// LinkedHashMap to ensure iteration order is consistent.
			this(new PredicatedLinkedHashMap<Fluid, IFluidHandler>(null));
		}

		public FluidHandlerPredicateMap(Map<Predicate<Fluid>, IFluidHandler> handlers) {
			this.handlers = handlers;
		}

		public FluidHandlerPredicateMap addHandler(Predicate<Fluid> fluid, IFluidHandler handler) {
			handlers.put(fluid, handler);
			return this;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {
			List<IFluidTankProperties> tanks = Lists.newArrayList();
			for (IFluidHandler iFluidHandler : handlers.values()) {
				Collections.addAll(tanks, iFluidHandler.getTankProperties());
			}
			return tanks.toArray(new IFluidTankProperties[tanks.size()]);
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			if (resource == null)
				return 0;
			IFluidHandler handler = handlers.get(resource.getFluid());
			if (handler == null)
				return 0;
			return handler.fill(resource, doFill);
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			if (resource == null)
				return null;
			IFluidHandler handler = handlers.get(resource.getFluid());
			if (handler == null)
				return null;
			return handler.drain(resource, doDrain);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			for (IFluidHandler handler : handlers.values()) {
				FluidStack drain = handler.drain(maxDrain, doDrain);
				if (drain != null)
					return drain;
			}
			return null;
		}
	}
}
